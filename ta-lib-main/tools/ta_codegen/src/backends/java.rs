use std::cell::Cell;
use std::collections::{HashMap, HashSet};

use crate::candle_settings::{detect_candle_settings, emit_java_unpacking};
use crate::helper_registry::{hoist_block_helpers, try_inline_expr, HelperRegistry};
use crate::ir::{BinOp, EnumDef, Expr, FuncDef, LookbackExpr, ParamType, Statement, VarType};
use crate::parser::enums::lookup_variant;
use crate::registry::{Lang, Registry};

/// Candle helper function names that should be rendered inline (as ternary
/// expressions) rather than hoisted into switch-block temporaries.  Keeping
/// them as `FuncCall` nodes lets the `&&`-split optimisation preserve
/// short-circuit evaluation — hoisted switch blocks would be evaluated
/// unconditionally before the `if`.
const JAVA_CANDLE_FNS: &[&str] = &["ta_candlerange", "ta_candleaverage"];

/// Check if an expression directly contains a candle helper function call,
/// WITHOUT recursing through `&&`/`||` operators.  This ensures we only split
/// `if(A && B)` when both A and B themselves contain expensive candle calls,
/// not when they're part of a longer `&&` chain where only some parts are
/// expensive.
#[allow(clippy::match_same_arms)] // And/Or arm intentionally stops recursion
fn expr_directly_contains_candle_call(expr: &Expr) -> bool {
    match expr {
        Expr::FuncCall(name, args) => {
            JAVA_CANDLE_FNS.contains(&name.as_str())
                || args.iter().any(expr_directly_contains_candle_call)
        }
        // Stop at logical operators — those are separate conditions in the chain
        Expr::BinOp(_, BinOp::And | BinOp::Or, _) => false,
        Expr::BinOp(l, _, r) => {
            expr_directly_contains_candle_call(l) || expr_directly_contains_candle_call(r)
        }
        Expr::Ternary(c, t, e) => {
            expr_directly_contains_candle_call(c)
                || expr_directly_contains_candle_call(t)
                || expr_directly_contains_candle_call(e)
        }
        Expr::Cast(_, inner)
        | Expr::Not(inner)
        | Expr::AddressOf(inner)
        | Expr::PostIncrement(inner)
        | Expr::PostDecrement(inner)
        | Expr::PreIncrement(inner)
        | Expr::PreDecrement(inner) => expr_directly_contains_candle_call(inner),
        Expr::ArrayAccess(_, idx) => expr_directly_contains_candle_call(idx),
        Expr::Var(_) | Expr::Literal(_) | Expr::IntLiteral(_) | Expr::PointerDeref(_) => false,
    }
}

/// Check if a statement list contains a return with ALLOC_ERR value.
fn contains_alloc_err_return(stmts: &[Statement]) -> bool {
    stmts.iter().any(|s| matches!(s, Statement::Return { value: Some(Expr::Var(name)) } if name == "ALLOC_ERR"))
}

/// Check if an expression already produces a boolean result in Java.
/// Used to avoid wrapping comparisons with `!= 0` (which would be a type error).
fn is_boolean_expr(expr: &Expr) -> bool {
    match expr {
        Expr::BinOp(_, op, _) => matches!(
            op,
            BinOp::Eq
                | BinOp::NotEq
                | BinOp::Less
                | BinOp::LessEq
                | BinOp::Greater
                | BinOp::GreaterEq
                | BinOp::And
                | BinOp::Or
        ),
        Expr::Not(_) => true,
        Expr::FuncCall(name, _) => matches!(name.as_str(), "IS_ZERO" | "IS_ZERO_OR_NEG"),
        _ => false,
    }
}

/// Check if an expression is an integer literal with a specific value.
fn is_int_literal(expr: &Expr, value: i64) -> bool {
    matches!(expr, Expr::IntLiteral(v) if *v == value)
}

/// Collect all variable names used in `AddressOf(Var(name))` contexts.
/// These variables need to be declared as `MInteger` instead of `int` in Java.
fn collect_address_of_vars(stmts: &[Statement]) -> HashSet<String> {
    let mut vars = HashSet::new();
    collect_address_of_vars_stmts(stmts, &mut vars);
    vars
}

fn collect_address_of_vars_stmts(stmts: &[Statement], vars: &mut HashSet<String>) {
    for stmt in stmts {
        collect_address_of_vars_stmt(stmt, vars);
    }
}

fn collect_address_of_vars_stmt(stmt: &Statement, vars: &mut HashSet<String>) {
    match stmt {
        Statement::Assign { target, value, .. } => {
            scan_expr_for_address_of(target, vars);
            scan_expr_for_address_of(value, vars);
        }
        Statement::If {
            condition,
            then_body,
            else_body,
        } => {
            scan_expr_for_address_of(condition, vars);
            collect_address_of_vars_stmts(then_body, vars);
            collect_address_of_vars_stmts(else_body, vars);
        }
        Statement::While { condition, body } | Statement::DoWhile { condition, body } => {
            scan_expr_for_address_of(condition, vars);
            collect_address_of_vars_stmts(body, vars);
        }
        Statement::ForC {
            init,
            condition,
            update,
            body,
        } => {
            collect_address_of_vars_stmt(init, vars);
            scan_expr_for_address_of(condition, vars);
            collect_address_of_vars_stmt(update, vars);
            collect_address_of_vars_stmts(body, vars);
        }
        Statement::For { count, body, .. } => {
            scan_expr_for_address_of(count, vars);
            collect_address_of_vars_stmts(body, vars);
        }
        Statement::Return { value: Some(expr) } => {
            scan_expr_for_address_of(expr, vars);
        }
        Statement::Block { body } => {
            collect_address_of_vars_stmts(body, vars);
        }
        Statement::Switch {
            expr,
            cases,
            default,
        } => {
            scan_expr_for_address_of(expr, vars);
            for (_, case_body) in cases {
                collect_address_of_vars_stmts(case_body, vars);
            }
            collect_address_of_vars_stmts(default, vars);
        }
        Statement::VarDecl { init: Some(e), .. } => {
            scan_expr_for_address_of(e, vars);
        }
        Statement::VarDecl { init: None, .. }
        | Statement::Return { value: None }
        | Statement::Break
        | Statement::Continue => {}
    }
}

fn scan_expr_for_address_of(expr: &Expr, vars: &mut HashSet<String>) {
    match expr {
        Expr::AddressOf(inner) => {
            if let Expr::Var(name) = inner.as_ref() {
                vars.insert(name.clone());
            }
            scan_expr_for_address_of(inner, vars);
        }
        Expr::FuncCall(_, args) => {
            for arg in args {
                scan_expr_for_address_of(arg, vars);
            }
        }
        Expr::BinOp(l, _, r) => {
            scan_expr_for_address_of(l, vars);
            scan_expr_for_address_of(r, vars);
        }
        Expr::Not(inner)
        | Expr::Cast(_, inner)
        | Expr::PostIncrement(inner)
        | Expr::PostDecrement(inner)
        | Expr::PreIncrement(inner)
        | Expr::PreDecrement(inner) => {
            scan_expr_for_address_of(inner, vars);
        }
        Expr::ArrayAccess(_, idx) => {
            scan_expr_for_address_of(idx, vars);
        }
        Expr::Ternary(cond, then_expr, else_expr) => {
            scan_expr_for_address_of(cond, vars);
            scan_expr_for_address_of(then_expr, vars);
            scan_expr_for_address_of(else_expr, vars);
        }
        Expr::Literal(_)
        | Expr::IntLiteral(_)
        | Expr::Var(_)
        | Expr::PointerDeref(_) => {}
    }
}

/// Collect local int variables that are assigned from MAType enum parameters.
/// These variables must be declared as `MAType` instead of `int` in Java.
///
/// Scans the function body for `Assign { target: Var(local), value: Var(param) }`
/// where `param` is a known MAType parameter name.
fn collect_matype_vars(stmts: &[Statement], matype_params: &HashSet<String>) -> HashSet<String> {
    let mut vars = HashSet::new();
    if matype_params.is_empty() {
        return vars;
    }
    collect_matype_vars_stmts(stmts, matype_params, &mut vars);
    vars
}

fn collect_matype_vars_stmts(
    stmts: &[Statement],
    matype_params: &HashSet<String>,
    vars: &mut HashSet<String>,
) {
    for stmt in stmts {
        match stmt {
            Statement::Assign {
                target: Expr::Var(tname),
                value: Expr::Var(vname),
                ..
            } => {
                // If value is a known MAType param, target must be MAType
                if matype_params.contains(vname) {
                    vars.insert(tname.clone());
                }
                // If value is a known MAType local var, target must be too
                if vars.contains(vname) {
                    vars.insert(tname.clone());
                }
            }
            Statement::If {
                then_body,
                else_body,
                ..
            } => {
                collect_matype_vars_stmts(then_body, matype_params, vars);
                collect_matype_vars_stmts(else_body, matype_params, vars);
            }
            Statement::While { body, .. }
            | Statement::DoWhile { body, .. }
            | Statement::For { body, .. }
            | Statement::Block { body } => {
                collect_matype_vars_stmts(body, matype_params, vars);
            }
            Statement::ForC { init, body, .. } => {
                collect_matype_vars_stmts(&[*init.clone()], matype_params, vars);
                collect_matype_vars_stmts(body, matype_params, vars);
            }
            Statement::Switch {
                cases, default, ..
            } => {
                for (_, case_body) in cases {
                    collect_matype_vars_stmts(case_body, matype_params, vars);
                }
                collect_matype_vars_stmts(default, matype_params, vars);
            }
            _ => {}
        }
    }
}

/// Collect Real-typed variables that appear in AddressOf contexts.
/// These need `double[]` wrapping instead of MInteger wrapping in Java.
fn collect_double_address_of_vars(
    stmts: &[Statement],
    address_of_vars: &HashSet<String>,
) -> HashSet<String> {
    let mut double_vars = HashSet::new();
    for stmt in stmts {
        if let Statement::VarDecl {
            var_type: VarType::Real,
            name,
            ..
        } = stmt
        {
            if address_of_vars.contains(name) {
                double_vars.insert(name.clone());
            }
        }
    }
    double_vars
}

#[allow(clippy::implicit_hasher)]
pub fn generate(
    func: &FuncDef,
    enums: &HashMap<String, EnumDef>,
    registry: &Registry,
    helpers: &HelperRegistry,
) -> String {
    let mut out = String::new();
    out.push_str("/* Generated */\n");
    out.push_str(&gen_lookback(func, enums, registry, helpers));
    if func.has_explicit_private {
        out.push_str(&gen_private(func, enums, registry, helpers)); // Private method (double)
        out.push_str(&gen_private_sp(func, enums, registry, helpers)); // Private method (float overload)
    }
    out.push_str(&gen_func(func, false, false, enums, registry, helpers)); // double-precision guarded
    out.push_str(&gen_func(func, false, true, enums, registry, helpers)); // double-precision logic (unguarded)
    out.push_str(&gen_func(func, true, false, enums, registry, helpers)); // single-precision guarded
    out.push_str(&gen_func(func, true, true, enums, registry, helpers)); // single-precision logic (unguarded)
    out
}

fn gen_lookback(
    func: &FuncDef,
    enums: &HashMap<String, EnumDef>,
    registry: &Registry,
    helpers: &HelperRegistry,
) -> String {
    let name = to_java_method_name(&func.name);

    // Build parameter list for signature
    let param_str = if func.optional_inputs.is_empty() {
        " ".to_string()
    } else {
        let params: Vec<String> = func
            .optional_inputs
            .iter()
            .map(|opt| {
                let java_type = match &opt.param_type {
                    ParamType::Real => "double",
                    ParamType::Integer => "int",
                    ParamType::Enum(ref name) => name.as_str(),
                    ParamType::Price(_) => unreachable!("Price expanded during parsing"),
                };
                format!("{} {}", java_type, opt.name)
            })
            .collect();
        format!(" {} ", params.join(", "))
    };

    let body = match &func.lookback {
        Some(LookbackExpr::Literal(n)) => format!("      return {n};"),
        Some(LookbackExpr::ParamMinus(param, offset)) => {
            format!("      return {param} - {offset};")
        }
        Some(LookbackExpr::Code(stmts)) => render_lookback_code(stmts, enums, registry, helpers),
        None => "      return 0;".to_string(),
    };

    format!(
        "   public int {name}Lookback({param_str})\n\
         \x20  {{\n\
         {body}\n\
         \x20  }}\n"
    )
}

/// Render a simple init expression for private_param_init VarDecls.
/// Only needs to handle arithmetic on optIn params (e.g., 2.0 / (period + 1)).
fn render_init_expr(expr: &Expr) -> String {
    match expr {
        Expr::Literal(f) => {
            let s = format!("{f}");
            if f.fract() == 0.0 && !s.contains('.') { format!("{s}.0") } else { s }
        }
        Expr::IntLiteral(i) => format!("{i}"),
        Expr::Var(name) => name.clone(),
        Expr::BinOp(lhs, op, rhs) => {
            let op_str = match op {
                BinOp::Add => "+",
                BinOp::Sub => "-",
                BinOp::Mul => "*",
                BinOp::Div => "/",
                _ => panic!("Unsupported op in private_param_init"),
            };
            format!("({}{}{})", render_init_expr(lhs), op_str, render_init_expr(rhs))
        }
        Expr::Cast(_ty, inner) => {
            format!("(double)({})", render_init_expr(inner))
        }
        _ => panic!("Unsupported expr in private_param_init: {expr:?}"),
    }
}

/// Generate the Private method (double, extra params).
fn gen_private(
    func: &FuncDef,
    enums: &HashMap<String, EnumDef>,
    registry: &Registry,
    helpers: &HelperRegistry,
) -> String {
    let base_name = to_java_method_name(&func.name);
    let name_override = format!("{base_name}Private");
    gen_func_inner(func, false, true, Some(&name_override), enums, registry, helpers)
}

/// Generate the Private method float overload (for Java method overloading).
/// Java needs this because float[] is not assignable to double[] — S_ callers
/// of emaPrivate(float_input, k) need a float overload.
fn gen_private_sp(
    func: &FuncDef,
    enums: &HashMap<String, EnumDef>,
    registry: &Registry,
    helpers: &HelperRegistry,
) -> String {
    let base_name = to_java_method_name(&func.name);
    let name_override = format!("{base_name}Private");
    gen_func_inner(func, true, true, Some(&name_override), enums, registry, helpers)
}

#[allow(clippy::too_many_lines, clippy::cognitive_complexity)]
fn gen_func(
    func: &FuncDef,
    single_precision: bool,
    logic: bool,
    enums: &HashMap<String, EnumDef>,
    registry: &Registry,
    helpers: &HelperRegistry,
) -> String {
    gen_func_inner(func, single_precision, logic, None, enums, registry, helpers)
}

#[allow(clippy::too_many_lines, clippy::cognitive_complexity)]
fn gen_func_inner(
    func: &FuncDef,
    single_precision: bool,
    logic: bool,
    name_override: Option<&str>,
    enums: &HashMap<String, EnumDef>,
    registry: &Registry,
    helpers: &HelperRegistry,
) -> String {
    let mut out = String::new();
    let base_name = to_java_method_name(&func.name);
    let name = if let Some(n) = name_override {
        n.to_string()
    } else if logic {
        format!("{base_name}Logic")
    } else {
        base_name
    };

    // Build parameter list
    let mut params: Vec<String> = Vec::new();
    params.push("int startIdx".to_string());
    params.push("int endIdx".to_string());

    for input in &func.inputs {
        let java_type = if single_precision {
            match &input.param_type {
                ParamType::Real => "float",
                ParamType::Integer | ParamType::Enum(_) | ParamType::Price(_) => "int",
            }
        } else {
            match &input.param_type {
                ParamType::Real => "double",
                ParamType::Integer | ParamType::Enum(_) | ParamType::Price(_) => "int",
            }
        };
        params.push(format!("{} {}[]", java_type, input.name));
    }

    for opt in &func.optional_inputs {
        let java_type = match &opt.param_type {
            ParamType::Real => "double",
            ParamType::Integer => "int",
            ParamType::Enum(ref name) => name.as_str(),
            ParamType::Price(_) => unreachable!("Price expanded during parsing"),
        };
        params.push(format!("{} {}", java_type, opt.name));
    }

    // Extra params only on Private variant (via name_override)
    if name_override.is_some() {
        for (param_name, c_type) in &func.private_extra_params {
            let java_type = match c_type.as_str() {
                "int" => "int",
                _ => "double",
            };
            params.push(format!("{java_type} {param_name}"));
        }
    }

    params.push("MInteger outBegIdx".to_string());
    params.push("MInteger outNBElement".to_string());

    for output in &func.outputs {
        let java_type = match &output.param_type {
            ParamType::Real => "double",
            ParamType::Integer | ParamType::Enum(_) | ParamType::Price(_) => "int",
        };
        params.push(format!("{} {}[]", java_type, output.name));
    }

    // Format signature
    let sig_prefix = format!("   public RetCode {name}( ");
    let indent = " ".repeat(sig_prefix.len());
    out.push_str(&sig_prefix);
    for (i, param) in params.iter().enumerate() {
        if i > 0 {
            out.push_str(&format!(",\n{indent}"));
        }
        out.push_str(param);
    }
    out.push_str(" )\n");

    // Body
    out.push_str("   {\n");

    // Body selection (same pattern as C backend):
    // - Private variant (name_override): always private_body
    // - S_ variants with _private: inline private_body
    // - Double variants with _private: body (delegates to Private)
    // - Logic without _private: private_body (same content as body)
    // - Guarded without _private: body
    let body = if name_override.is_some() {
        &func.private_body
    } else if single_precision && func.has_explicit_private {
        &func.private_body
    } else if func.has_explicit_private {
        &func.body
    } else if logic {
        &func.private_body
    } else {
        &func.body
    };

    // Pre-scan for variables used in AddressOf contexts (need MInteger wrapping)
    let mut address_of_vars = collect_address_of_vars(body);

    // In single-precision variants, input params are float[] while outputs are double[].
    // Collect input param names so render_expr can replace float[]==double[] with false.
    let float_input_params: HashSet<String> = if single_precision {
        func.inputs.iter().map(|p| p.name.clone()).collect()
    } else {
        HashSet::new()
    };

    // Pre-scan for local int variables that are assigned from MAType enum params.
    // In C, `ENUM_DECLARATION(MAType) tempMAType` is parsed as VarType::Integer,
    // but in Java the variable must be declared as `MAType` to allow enum assignment.
    let matype_params: HashSet<String> = func
        .optional_inputs
        .iter()
        .filter(|o| matches!(&o.param_type, ParamType::Enum(n) if n == "MAType"))
        .map(|o| o.name.clone())
        .collect();
    let matype_vars = collect_matype_vars(body, &matype_params);

    // Collect Real-typed variables used in AddressOf contexts.
    // These need `double[]` wrapping (not MInteger) — e.g. `double prevATR`
    // becomes `double[] prevATR = new double[1]` and uses `[0]` instead of `.value`.
    let double_address_of_vars = collect_double_address_of_vars(body, &address_of_vars);

    // Remove double address-of vars from the integer set so they don't get `.value`
    for name in &double_address_of_vars {
        address_of_vars.remove(name);
    }

    // Declare local variables
    for stmt in body {
        if let Statement::VarDecl { var_type, name, .. } = stmt {
            let java_decl = if matype_vars.contains(name) {
                format!("MAType {name}")
            } else if address_of_vars.contains(name)
                && matches!(var_type, VarType::Integer | VarType::Index)
            {
                format!("MInteger {name} = new MInteger()")
            } else if double_address_of_vars.contains(name) {
                format!("double[] {name} = new double[1]")
            } else {
                match var_type {
                    VarType::Real => format!("double {name} = 0"),
                    VarType::Integer | VarType::Index => format!("int {name} = 0"),
                    VarType::RetCodeType => format!("RetCode {name}"),
                    VarType::RealPointer => format!("double[] {name}"),
                    VarType::IntPointer => format!("int[] {name}"),
                    VarType::RealArray(size) => {
                        format!("double[] {name} = new double[{size}]")
                    }
                    VarType::IntArray(size) => format!("int[] {name} = new int[{size}]"),
                }
            };
            out.push_str(&format!("      {java_decl};\n"));
        }
    }

    // For S_ variants with _private: emit private_param_init as local VarDecls
    // Both guarded and logic S_ variants need this (both use private_body).
    if single_precision && func.has_explicit_private && name_override.is_none() {
        for (param_name, init_expr) in &func.private_param_init {
            let init_java = render_init_expr(init_expr);
            out.push_str(&format!("      double {param_name} = {init_java};\n"));
        }
    }

    // Emit candle settings unpacking (only for referenced settings)
    let candle_used = detect_candle_settings(body);
    if !candle_used.is_empty() {
        out.push_str(&emit_java_unpacking(&candle_used, 6));
    }

    // Validation (omitted for Logic/unguarded variant)
    if !logic {
        out.push_str("      if( startIdx < 0 ) {\n");
        out.push_str("         return RetCode.OutOfRangeStartIndex ;\n");
        out.push_str("      }\n");
        out.push_str("      if( (endIdx < 0) || (endIdx < startIdx)) {\n");
        out.push_str("         return RetCode.OutOfRangeEndIndex ;\n");
        out.push_str("      }\n");
    }

    let inline_counter = Cell::new(0);

    // Emit VarDecl initializations
    for stmt in body {
        if let Statement::VarDecl {
            name,
            init: Some(init),
            ..
        } = stmt
        {
            // Hoist multi-statement helpers from init expressions
            let mut hoisted_vec = Vec::new();
            let mut cnt = inline_counter.get();
            let new_init = hoist_block_helpers(
                init, helpers, &mut hoisted_vec, &mut cnt, JAVA_CANDLE_FNS,
            );
            inline_counter.set(cnt);
            out.push_str(&render_hoisted_blocks(
                &hoisted_vec,
                6,
                single_precision,
                enums,
                registry,
                helpers,
                &inline_counter,
                &address_of_vars,
                &double_address_of_vars,
                &float_input_params,
            ));
            let init_str =
                render_expr(&new_init, single_precision, registry, helpers, &address_of_vars, &double_address_of_vars, &float_input_params);
            if address_of_vars.contains(name) {
                out.push_str(&format!("      {name}.value = {init_str};\n"));
            } else if double_address_of_vars.contains(name) {
                out.push_str(&format!("      {name}[0] = {init_str};\n"));
            } else {
                out.push_str(&format!("      {name} = {init_str};\n"));
            }
        }
    }

    // Render body statements (skip VarDecls)
    for stmt in body {
        if matches!(stmt, Statement::VarDecl { .. }) {
            continue;
        }
        out.push_str(&render_statement(
            stmt,
            6,
            single_precision,
            enums,
            registry,
            helpers,
            &inline_counter,
            &address_of_vars,
            &double_address_of_vars,
            &float_input_params,
        ));
    }

    // Closing brace — return statement comes from IR body
    out.push_str("   }\n");

    out
}

/// Render a ForC init or update clause. If it's a Block with multiple
/// statements, comma-separate them instead of using semicolons.
fn render_forc_part(
    stmt: &Statement,
    single_precision: bool,
    enums: &HashMap<String, EnumDef>,
    registry: &Registry,
    helpers: &HelperRegistry,
    inline_counter: &Cell<usize>,
    address_of_vars: &HashSet<String>,
    double_address_of_vars: &HashSet<String>,
    float_input_params: &HashSet<String>,
) -> String {
    match stmt {
        Statement::Block { body } => body
            .iter()
            .map(|s| {
                render_statement(
                    s, 0, single_precision, enums, registry, helpers, inline_counter,
                    address_of_vars, double_address_of_vars, float_input_params,
                )
                .trim()
                .trim_end_matches(';')
                .to_string()
            })
            .collect::<Vec<_>>()
            .join(", "),
        _ => render_statement(
            stmt, 0, single_precision, enums, registry, helpers, inline_counter,
            address_of_vars, double_address_of_vars, float_input_params,
        )
        .trim()
        .trim_end_matches(';')
        .to_string(),
    }
}

/// Render hoisted block-inline helpers as Java code (temp var decl + body).
fn render_hoisted_blocks(
    hoisted: &[(String, VarType, Vec<Statement>)],
    indent: usize,
    single_precision: bool,
    enums: &HashMap<String, EnumDef>,
    registry: &Registry,
    helpers: &HelperRegistry,
    inline_counter: &Cell<usize>,
    address_of_vars: &HashSet<String>,
    double_address_of_vars: &HashSet<String>,
    float_input_params: &HashSet<String>,
) -> String {
    let pad = " ".repeat(indent);
    let mut out = String::new();
    for (temp_name, var_type, body) in hoisted {
        let java_decl = match var_type {
            VarType::Real => format!("double {temp_name}"),
            VarType::Integer | VarType::Index => format!("int {temp_name}"),
            VarType::RetCodeType => format!("RetCode {temp_name}"),
            VarType::RealPointer => format!("double[] {temp_name}"),
            VarType::IntPointer => format!("int[] {temp_name}"),
            VarType::RealArray(size) => format!("double[] {temp_name} = new double[{size}]"),
            VarType::IntArray(size) => format!("int[] {temp_name} = new int[{size}]"),
        };
        out.push_str(&format!("{pad}{java_decl};\n"));
        // Declare local variables from the hoisted helper body.
        // render_statement skips VarDecl, so we emit them explicitly here.
        // For VarDecls with an initializer, emit `type name = <init>;` directly.
        for stmt in body {
            if let Statement::VarDecl { var_type: vt, name, init } = stmt {
                let type_part = match vt {
                    VarType::Real => "double".to_string(),
                    VarType::Integer | VarType::Index => "int".to_string(),
                    VarType::RetCodeType => "RetCode".to_string(),
                    VarType::RealPointer => "double[]".to_string(),
                    VarType::IntPointer => "int[]".to_string(),
                    VarType::RealArray(size) => {
                        // Arrays with size are initialized inline; emit and continue
                        out.push_str(&format!("{pad}double[] {name} = new double[{size}];\n"));
                        continue;
                    }
                    VarType::IntArray(size) => {
                        out.push_str(&format!("{pad}int[] {name} = new int[{size}];\n"));
                        continue;
                    }
                };
                if let Some(init_expr) = init {
                    // Hoist any multi-statement helpers in the init expression
                    // (e.g. ta_candlerange inside ta_candleaverage's VarDecl init)
                    let mut inner_hoisted = Vec::new();
                    let mut cnt = inline_counter.get();
                    let hoisted_init = hoist_block_helpers(
                        init_expr, helpers, &mut inner_hoisted, &mut cnt, JAVA_CANDLE_FNS,
                    );
                    inline_counter.set(cnt);
                    out.push_str(&render_hoisted_blocks(
                        &inner_hoisted, indent, single_precision, enums, registry,
                        helpers, inline_counter, address_of_vars,
                        double_address_of_vars, float_input_params,
                    ));
                    let init_str = render_expr(
                        &hoisted_init,
                        single_precision,
                        registry,
                        helpers,
                        address_of_vars,
                        double_address_of_vars,
                        float_input_params,
                    );
                    out.push_str(&format!("{pad}{type_part} {name} = {init_str};\n"));
                } else {
                    out.push_str(&format!("{pad}{type_part} {name};\n"));
                }
            }
        }
        for stmt in body {
            // Skip VarDecls — already emitted in the declaration loop above
            if matches!(stmt, Statement::VarDecl { .. }) {
                continue;
            }
            out.push_str(&render_statement(
                stmt,
                indent,
                single_precision,
                enums,
                registry,
                helpers,
                inline_counter,
                address_of_vars,
                double_address_of_vars,
                float_input_params,
            ));
        }
    }
    out
}

#[allow(clippy::too_many_lines, clippy::implicit_hasher, clippy::cognitive_complexity)]
pub fn render_statement(
    stmt: &Statement,
    indent: usize,
    single_precision: bool,
    enums: &HashMap<String, EnumDef>,
    registry: &Registry,
    helpers: &HelperRegistry,
    inline_counter: &Cell<usize>,
    address_of_vars: &HashSet<String>,
    double_address_of_vars: &HashSet<String>,
    float_input_params: &HashSet<String>,
) -> String {
    let pad = " ".repeat(indent);
    match stmt {
        Statement::VarDecl {
            var_type,
            name,
            init,
        } => {
            // Top-level VarDecls are emitted by the function renderer and skipped
            // before calling render_statement. This arm handles block-scoped VarDecls
            // (inside while/for/if bodies) that need local declarations.
            let type_str = match var_type {
                VarType::Real => "double",
                VarType::Integer | VarType::Index => "int",
                VarType::RetCodeType => "RetCode",
                VarType::RealPointer => "double[]",
                VarType::IntPointer => "int[]",
                VarType::RealArray(size) => {
                    return format!(
                        "{pad}double[] {name} = new double[{size}];\n"
                    );
                }
                VarType::IntArray(size) => {
                    return format!("{pad}int[] {name} = new int[{size}];\n");
                }
            };
            if let Some(init_expr) = init {
                let mut hoisted_vec = Vec::new();
                let mut cnt = inline_counter.get();
                let new_init = hoist_block_helpers(
                    init_expr, helpers, &mut hoisted_vec, &mut cnt, JAVA_CANDLE_FNS,
                );
                inline_counter.set(cnt);
                let mut out = render_hoisted_blocks(
                    &hoisted_vec, indent, single_precision, enums, registry,
                    helpers, inline_counter, address_of_vars,
                    double_address_of_vars, float_input_params,
                );
                let init_str = render_expr(
                    &new_init, single_precision, registry, helpers,
                    address_of_vars, double_address_of_vars, float_input_params,
                );
                out.push_str(&format!("{pad}{type_str} {name} = {init_str};\n"));
                out
            } else {
                format!("{pad}{type_str} {name};\n")
            }
        }
        Statement::Assign {
            target,
            value,
            compound,
        } => {
            // Statement-level expression: when target is Var("_"), render as standalone
            if let Expr::Var(tname) = target {
                if tname == "_" {
                    // Skip bare variable statements (no side effects — e.g. inlined identity helpers)
                    if matches!(value, Expr::Var(_)) {
                        return String::new();
                    }
                    if let Expr::FuncCall(fname, args) = value {
                        // Check if helper inlines to a bare variable (identity helper)
                        if let Some(helper) = helpers.get(fname) {
                            if let Some(inlined) = try_inline_expr(helper, args) {
                                if matches!(inlined, Expr::Var(_)) {
                                    return String::new();
                                }
                            }
                        }
                        let rendered = render_func_call(
                            fname, args, single_precision, registry, helpers,
                            address_of_vars, double_address_of_vars, float_input_params,
                        );
                        // Skip empty renders (e.g. free() returns "")
                        if rendered.is_empty() {
                            return String::new();
                        }
                        return format!("{pad}{rendered};\n");
                    }
                }
            }
            // Handle output scalar assignments via .value
            if let Expr::Var(name) = target {
                if name == "outBegIdx" || name == "outNBElement" {
                    return format!(
                        "{}{}.value = {};\n",
                        pad,
                        name,
                        render_expr(value, single_precision, registry, helpers,
                            address_of_vars, double_address_of_vars, float_input_params)
                    );
                }
            }

            // Hoist multi-statement helpers from the value expression
            let mut hoisted = Vec::new();
            let mut cnt = inline_counter.get();
            let new_value = hoist_block_helpers(
                value, helpers, &mut hoisted, &mut cnt, JAVA_CANDLE_FNS,
            );
            inline_counter.set(cnt);
            let mut out = render_hoisted_blocks(
                &hoisted, indent, single_precision, enums, registry,
                helpers, inline_counter, address_of_vars, double_address_of_vars, float_input_params,
            );

            // Only fold compound assignments if the original source used +=/-=/etc.
            if *compound {
                if let (Expr::Var(tname), Expr::BinOp(left, op, right)) = (target, &new_value) {
                    if let Expr::Var(lname) = left.as_ref() {
                        if lname == tname {
                            let op_str = match op {
                                BinOp::Add => "+=",
                                BinOp::Sub => "-=",
                                BinOp::Mul => "*=",
                                BinOp::Div => "/=",
                                BinOp::Mod
                                | BinOp::LessEq
                                | BinOp::Less
                                | BinOp::Greater
                                | BinOp::GreaterEq
                                | BinOp::Eq
                                | BinOp::NotEq
                                | BinOp::And
                                | BinOp::Or
                                | BinOp::BitwiseOr
                                | BinOp::Shr
                                | BinOp::Shl => "",
                            };
                            if !op_str.is_empty() {
                                let target_str = render_assign_target(
                                    target, single_precision, registry, helpers,
                                    address_of_vars, double_address_of_vars, float_input_params,
                                );
                                out.push_str(&format!(
                                    "{}{} {} {};\n",
                                    pad,
                                    target_str,
                                    op_str,
                                    render_expr(right, single_precision, registry, helpers,
                                        address_of_vars, double_address_of_vars, float_input_params)
                                ));
                                return out;
                            }
                        }
                    }
                }
            }

            let target_str = render_assign_target(
                target, single_precision, registry, helpers, address_of_vars, double_address_of_vars, float_input_params,
            );
            let value_str = render_expr(
                &new_value, single_precision, registry, helpers, address_of_vars, double_address_of_vars, float_input_params,
            );
            out.push_str(&format!("{pad}{target_str} = {value_str};\n"));
            out
        }
        Statement::While { condition, body } => {
            // Hoist multi-statement helpers from the condition expression
            let mut hoisted = Vec::new();
            let mut cnt = inline_counter.get();
            let new_condition = hoist_block_helpers(
                condition, helpers, &mut hoisted, &mut cnt, JAVA_CANDLE_FNS,
            );
            inline_counter.set(cnt);
            let mut out = render_hoisted_blocks(
                &hoisted, indent, single_precision, enums, registry,
                helpers, inline_counter, address_of_vars, double_address_of_vars, float_input_params,
            );
            let cond_str =
                render_expr(&new_condition, single_precision, registry, helpers, address_of_vars, double_address_of_vars, float_input_params);
            let cond_java = if is_boolean_expr(&new_condition) {
                cond_str
            } else {
                format!("({cond_str}) != 0")
            };
            out.push_str(&format!("{pad}while( {cond_java} ) {{\n"));
            for s in body {
                out.push_str(&render_statement(
                    s,
                    indent + 3,
                    single_precision,
                    enums,
                    registry,
                    helpers,
                    inline_counter,
                    address_of_vars,
                    double_address_of_vars,
                    float_input_params,
                ));
            }
            out.push_str(&format!("{pad}}}\n"));
            out
        }
        Statement::DoWhile { condition, body } => {
            // Hoist multi-statement helpers from the condition expression.
            // For do-while, hoisted blocks go INSIDE the loop body (before the
            // closing `} while(cond)`) so they execute each iteration.
            let mut hoisted = Vec::new();
            let mut cnt = inline_counter.get();
            let new_condition = hoist_block_helpers(
                condition, helpers, &mut hoisted, &mut cnt, JAVA_CANDLE_FNS,
            );
            inline_counter.set(cnt);
            let mut out = format!("{pad}do {{\n");
            for s in body {
                out.push_str(&render_statement(
                    s,
                    indent + 3,
                    single_precision,
                    enums,
                    registry,
                    helpers,
                    inline_counter,
                    address_of_vars,
                    double_address_of_vars,
                    float_input_params,
                ));
            }
            out.push_str(&render_hoisted_blocks(
                &hoisted, indent + 3, single_precision, enums, registry,
                helpers, inline_counter, address_of_vars, double_address_of_vars, float_input_params,
            ));
            let cond_str =
                render_expr(&new_condition, single_precision, registry, helpers, address_of_vars, double_address_of_vars, float_input_params);
            let cond_java = if is_boolean_expr(&new_condition) {
                cond_str
            } else {
                format!("({cond_str}) != 0")
            };
            out.push_str(&format!("{pad}}} while( {cond_java} );\n"));
            out
        }
        Statement::If {
            condition,
            then_body,
            else_body,
        } => {
            // Skip post-allocation null-check blocks (dead code in Java — `new` never returns null)
            if contains_alloc_err_return(then_body) {
                return String::new();
            }
            // Split `if(A && B)` into nested `if(A) { if(B)` when both sides
            // contain a candle helper call (ta_candlerange/ta_candleaverage).
            // This preserves short-circuit evaluation so the expensive ternary
            // on the right side is only computed when the left side is true.
            if let Expr::BinOp(left, BinOp::And, right) = condition {
                if expr_directly_contains_candle_call(left)
                    && expr_directly_contains_candle_call(right)
                {
                    let inner_if = Statement::If {
                        condition: *right.clone(),
                        then_body: then_body.clone(),
                        else_body: else_body.clone(),
                    };
                    let outer_if = Statement::If {
                        condition: *left.clone(),
                        then_body: vec![inner_if],
                        else_body: else_body.clone(),
                    };
                    return render_statement(
                        &outer_if, indent, single_precision, enums, registry,
                        helpers, inline_counter, address_of_vars,
                        double_address_of_vars, float_input_params,
                    );
                }
            }
            // Hoist multi-statement helpers from the condition expression
            let mut hoisted = Vec::new();
            let mut cnt = inline_counter.get();
            let new_condition = hoist_block_helpers(
                condition, helpers, &mut hoisted, &mut cnt, JAVA_CANDLE_FNS,
            );
            inline_counter.set(cnt);
            let mut out = render_hoisted_blocks(
                &hoisted, indent, single_precision, enums, registry,
                helpers, inline_counter, address_of_vars, double_address_of_vars, float_input_params,
            );
            let cond_str =
                render_expr(&new_condition, single_precision, registry, helpers, address_of_vars, double_address_of_vars, float_input_params);
            let cond_java = if is_boolean_expr(&new_condition) {
                cond_str
            } else {
                format!("({cond_str}) != 0")
            };
            out.push_str(&format!("{pad}if( {cond_java} ) {{\n"));
            for s in then_body {
                out.push_str(&render_statement(
                    s,
                    indent + 3,
                    single_precision,
                    enums,
                    registry,
                    helpers,
                    inline_counter,
                    address_of_vars,
                    double_address_of_vars,
                    float_input_params,
                ));
            }
            if else_body.is_empty() {
                out.push_str(&format!("{pad}}}\n"));
            } else {
                out.push_str(&format!("{pad}}} else "));
                if else_body.len() == 1 {
                    if let Statement::If { .. } = &else_body[0] {
                        let if_str = render_statement(
                            &else_body[0],
                            indent,
                            single_precision,
                            enums,
                            registry,
                            helpers,
                            inline_counter,
                            address_of_vars,
                            double_address_of_vars,
                            float_input_params,
                        );
                        out.push_str(if_str.trim_start());
                        return out;
                    }
                }
                out.push_str("{\n");
                for s in else_body {
                    out.push_str(&render_statement(
                        s,
                        indent + 3,
                        single_precision,
                        enums,
                        registry,
                        helpers,
                        inline_counter,
                        address_of_vars,
                        double_address_of_vars,
                        float_input_params,
                    ));
                }
                out.push_str(&format!("{pad}}}\n"));
            }
            out
        }
        Statement::Return { value } => match value {
            Some(expr) => {
                let rendered = render_return_expr(
                    expr, single_precision, registry, helpers, address_of_vars, double_address_of_vars, float_input_params,
                );
                format!("{pad}return {rendered} ;\n")
            }
            None => format!("{pad}return ;\n"),
        },
        Statement::For { var, count, body } => {
            let mut out = format!(
                "{}for( {} = {}; {} > 0; {}-- ) {{\n",
                pad,
                var,
                render_expr(count, single_precision, registry, helpers,
                    address_of_vars, double_address_of_vars, float_input_params),
                var,
                var,
            );
            for s in body {
                out.push_str(&render_statement(
                    s,
                    indent + 3,
                    single_precision,
                    enums,
                    registry,
                    helpers,
                    inline_counter,
                    address_of_vars,
                    double_address_of_vars,
                    float_input_params,
                ));
            }
            out.push_str(&format!("{pad}}}\n"));
            out
        }
        Statement::ForC {
            init,
            condition,
            update,
            body,
        } => {
            let init_str = render_forc_part(
                init, single_precision, enums, registry, helpers, inline_counter,
                address_of_vars, double_address_of_vars, float_input_params,
            );
            let update_str = render_forc_part(
                update, single_precision, enums, registry, helpers, inline_counter,
                address_of_vars, double_address_of_vars, float_input_params,
            );
            // Hoist multi-statement helpers from the condition expression
            let mut hoisted = Vec::new();
            let mut cnt = inline_counter.get();
            let new_condition = hoist_block_helpers(
                condition, helpers, &mut hoisted, &mut cnt, JAVA_CANDLE_FNS,
            );
            inline_counter.set(cnt);
            let mut out = render_hoisted_blocks(
                &hoisted, indent, single_precision, enums, registry,
                helpers, inline_counter, address_of_vars, double_address_of_vars, float_input_params,
            );
            out.push_str(&format!(
                "{}for( {}; {}; {} ) {{\n",
                pad,
                init_str.trim(),
                render_expr(&new_condition, single_precision, registry, helpers,
                    address_of_vars, double_address_of_vars, float_input_params),
                update_str.trim()
            ));
            for s in body {
                out.push_str(&render_statement(
                    s,
                    indent + 3,
                    single_precision,
                    enums,
                    registry,
                    helpers,
                    inline_counter,
                    address_of_vars,
                    double_address_of_vars,
                    float_input_params,
                ));
            }
            out.push_str(&format!("{pad}}}\n"));
            out
        }
        Statement::Block { body } => {
            let mut out = String::new();
            // Emit VarDecl declarations first (render_statement skips VarDecl)
            for s in body {
                if let Statement::VarDecl { var_type: vt, name, init } = s {
                    let type_part = match vt {
                        VarType::Real => "double".to_string(),
                        VarType::Integer | VarType::Index => "int".to_string(),
                        VarType::RetCodeType => "RetCode".to_string(),
                        VarType::RealPointer => "double[]".to_string(),
                        VarType::IntPointer => "int[]".to_string(),
                        VarType::RealArray(size) => {
                            out.push_str(&format!("{pad}double[] {name} = new double[{size}];\n"));
                            continue;
                        }
                        VarType::IntArray(size) => {
                            out.push_str(&format!("{pad}int[] {name} = new int[{size}];\n"));
                            continue;
                        }
                    };
                    if let Some(init_expr) = init {
                        let init_str = render_expr(
                            init_expr,
                            single_precision,
                            registry,
                            helpers,
                            address_of_vars,
                            double_address_of_vars,
                            float_input_params,
                        );
                        out.push_str(&format!("{pad}{type_part} {name} = {init_str};\n"));
                    } else {
                        out.push_str(&format!("{pad}{type_part} {name};\n"));
                    }
                }
            }
            for s in body {
                // Skip VarDecls — already emitted in the declaration loop above
                if matches!(s, Statement::VarDecl { .. }) {
                    continue;
                }
                out.push_str(&render_statement(
                    s,
                    indent,
                    single_precision,
                    enums,
                    registry,
                    helpers,
                    inline_counter,
                    address_of_vars,
                    double_address_of_vars,
                    float_input_params,
                ));
            }
            out
        }
        Statement::Break => format!("{pad}break;\n"),
        Statement::Continue => format!("{pad}continue;\n"),
        Statement::Switch {
            expr,
            cases,
            default,
        } => {
            // Hoist multi-statement helpers from the switch expression
            let mut hoisted = Vec::new();
            let mut cnt = inline_counter.get();
            let new_expr = hoist_block_helpers(
                expr, helpers, &mut hoisted, &mut cnt, JAVA_CANDLE_FNS,
            );
            inline_counter.set(cnt);
            let mut out = render_hoisted_blocks(
                &hoisted, indent, single_precision, enums, registry,
                helpers, inline_counter, address_of_vars, double_address_of_vars, float_input_params,
            );
            out.push_str(&format!(
                "{}switch( {} )\n{}{{\n",
                pad,
                render_expr(&new_expr, single_precision, registry, helpers,
                    address_of_vars, double_address_of_vars, float_input_params),
                pad
            ));
            for (label, case_body) in cases {
                let java_label = render_java_switch_label(label, enums);
                out.push_str(&format!("{pad}case {java_label}:\n"));
                for s in case_body {
                    out.push_str(&render_statement(
                        s,
                        indent + 3,
                        single_precision,
                        enums,
                        registry,
                        helpers,
                        inline_counter,
                        address_of_vars,
                        double_address_of_vars,
                        float_input_params,
                    ));
                }
                out.push_str(&format!("{pad}   break;\n"));
            }
            if !default.is_empty() {
                out.push_str(&format!("{pad}default:\n"));
                for s in default {
                    out.push_str(&render_statement(
                        s,
                        indent + 3,
                        single_precision,
                        enums,
                        registry,
                        helpers,
                        inline_counter,
                        address_of_vars,
                        double_address_of_vars,
                        float_input_params,
                    ));
                }
                out.push_str(&format!("{pad}   break;\n"));
            }
            out.push_str(&format!("{pad}}}\n"));
            out
        }
    }
}

fn render_java_switch_label(label: &str, enums: &HashMap<String, EnumDef>) -> String {
    if let Some((enum_name, variant)) = lookup_variant(label, enums) {
        format!("{}.{}", enum_name, variant.pascal_name)
    } else {
        label.to_string()
    }
}

fn render_assign_target(
    expr: &Expr,
    single_precision: bool,
    registry: &Registry,
    helpers: &HelperRegistry,
    address_of_vars: &HashSet<String>,
    double_address_of_vars: &HashSet<String>,
    float_input_params: &HashSet<String>,
) -> String {
    match expr {
        Expr::Var(name) => {
            if address_of_vars.contains(name) {
                format!("{name}.value")
            } else if double_address_of_vars.contains(name) {
                format!("{name}[0]")
            } else {
                name.clone()
            }
        }
        Expr::ArrayAccess(name, idx) => {
            format!(
                "{}[{}]",
                name,
                render_expr(idx, single_precision, registry, helpers,
                    address_of_vars, double_address_of_vars, float_input_params)
            )
        }
        Expr::Literal(_)
        | Expr::IntLiteral(_)
        | Expr::BinOp(_, _, _)
        | Expr::Cast(_, _)
        | Expr::Not(_)
        | Expr::FuncCall(_, _)
        | Expr::PointerDeref(_)
        | Expr::AddressOf(_)
        | Expr::PostIncrement(_)
        | Expr::PostDecrement(_)
        | Expr::PreIncrement(_)
        | Expr::PreDecrement(_)
        | Expr::Ternary(_, _, _) => {
            render_expr(expr, single_precision, registry, helpers, address_of_vars, double_address_of_vars, float_input_params)
        }
    }
}

/// Render a return expression, mapping known enum values to Java constants.
fn render_return_expr(
    expr: &Expr,
    single_precision: bool,
    registry: &Registry,
    helpers: &HelperRegistry,
    address_of_vars: &HashSet<String>,
    double_address_of_vars: &HashSet<String>,
    float_input_params: &HashSet<String>,
) -> String {
    if let Expr::Var(name) = expr {
        return match name.as_str() {
            "SUCCESS" => "RetCode.Success".to_string(),
            "BadParam" => "RetCode.BadParam".to_string(),
            "OutOfRangeEndIndex" => "RetCode.OutOfRangeEndIndex".to_string(),
            "OutOfRangeStartIndex" => "RetCode.OutOfRangeStartIndex".to_string(),
            _ => render_expr(expr, single_precision, registry, helpers, address_of_vars, double_address_of_vars, float_input_params),
        };
    }
    render_expr(expr, single_precision, registry, helpers, address_of_vars, double_address_of_vars, float_input_params)
}

#[allow(clippy::too_many_lines)]
fn render_expr(
    expr: &Expr,
    single_precision: bool,
    registry: &Registry,
    helpers: &HelperRegistry,
    address_of_vars: &HashSet<String>,
    double_address_of_vars: &HashSet<String>,
    float_input_params: &HashSet<String>,
) -> String {
    match expr {
        Expr::Literal(f) => {
            #[allow(clippy::float_cmp)]
            let is_whole = *f == f.floor() && f.abs() < 1e15;
            if is_whole {
                #[allow(clippy::cast_possible_truncation)]
                let i = *f as i64;
                format!("{i}.0")
            } else {
                format!("{f}")
            }
        }
        Expr::IntLiteral(i) => format!("{i}"),
        Expr::Var(name) => {
            let mapped = match name.as_str() {
                "COMPATIBILITY" => "this.compatibility".to_string(),
                "METASTOCK" => "Compatibility.Metastock".to_string(),
                "DEFAULT" => "Compatibility.Default".to_string(),
                "BAD_PARAM" => "RetCode.BadParam".to_string(),
                "SUCCESS" => "RetCode.Success".to_string(),
                "ALLOC_ERR" => "RetCode.AllocErr".to_string(),
                "INTERNAL_ERROR" => "RetCode.InternalError".to_string(),
                "TA_MAType_SMA" => "MAType.Sma".to_string(),
                "TA_MAType_EMA" => "MAType.Ema".to_string(),
                "TA_MAType_WMA" => "MAType.Wma".to_string(),
                "TA_MAType_DEMA" => "MAType.Dema".to_string(),
                "TA_MAType_TEMA" => "MAType.Tema".to_string(),
                "TA_MAType_TRIMA" => "MAType.Trima".to_string(),
                "TA_MAType_KAMA" => "MAType.Kama".to_string(),
                "TA_MAType_MAMA" => "MAType.Mama".to_string(),
                "TA_MAType_T3" => "MAType.T3".to_string(),
                _ => name.clone(),
            };
            if address_of_vars.contains(name) {
                format!("{mapped}.value")
            } else if double_address_of_vars.contains(name) {
                format!("{mapped}[0]")
            } else {
                mapped
            }
        }
        Expr::ArrayAccess(name, idx) => {
            format!(
                "{}[{}]",
                name,
                render_expr(idx, single_precision, registry, helpers, address_of_vars, double_address_of_vars, float_input_params)
            )
        }
        Expr::BinOp(left, op, right) => {
            // In single-precision variants, input params are float[] and output params are
            // double[]. Java forbids == / != comparisons between incompatible array types.
            // When exactly one operand is a known float input param, the comparison can
            // never be true (they are different types and can never alias), so emit false/true.
            if single_precision && matches!(op, BinOp::Eq | BinOp::NotEq) {
                if let (Expr::Var(lname), Expr::Var(rname)) = (left.as_ref(), right.as_ref()) {
                    let left_is_input = float_input_params.contains(lname.as_str());
                    let right_is_input = float_input_params.contains(rname.as_str());
                    if left_is_input != right_is_input {
                        return if matches!(op, BinOp::Eq) {
                            "false".to_string()
                        } else {
                            "true".to_string()
                        };
                    }
                }
            }
            let op_str = match op {
                BinOp::Add => "+",
                BinOp::Sub => "-",
                BinOp::Mul => "*",
                BinOp::Div => "/",
                BinOp::Mod => "%",
                BinOp::LessEq => "<=",
                BinOp::Less => "<",
                BinOp::Greater => ">",
                BinOp::GreaterEq => ">=",
                BinOp::Eq => "==",
                BinOp::NotEq => "!=",
                BinOp::And => "&&",
                BinOp::Or => "||",
                BinOp::BitwiseOr => "|",
                BinOp::Shr => ">>",
                BinOp::Shl => "<<",
            };
            format!(
                "({}{}{})",
                render_expr(left, single_precision, registry, helpers, address_of_vars, double_address_of_vars, float_input_params),
                op_str,
                render_expr(right, single_precision, registry, helpers, address_of_vars, double_address_of_vars, float_input_params)
            )
        }
        Expr::Cast(var_type, inner) => {
            let java_type = match var_type {
                VarType::Real => "double",
                VarType::Integer | VarType::Index => "int",
                VarType::RetCodeType => "RetCode",
                VarType::RealPointer => "double[]",
                VarType::IntPointer => "int[]",
                VarType::RealArray(_) | VarType::IntArray(_) => "/* array cast */",
            };
            format!(
                "(({}){})",
                java_type,
                render_expr(inner, single_precision, registry, helpers, address_of_vars, double_address_of_vars, float_input_params)
            )
        }
        Expr::Not(inner) => {
            format!(
                "!({})",
                render_expr(inner, single_precision, registry, helpers, address_of_vars, double_address_of_vars, float_input_params)
            )
        }
        Expr::FuncCall(name, args) => {
            render_func_call(name, args, single_precision, registry, helpers, address_of_vars, double_address_of_vars, float_input_params)
        }
        Expr::PointerDeref(name) => {
            // Java has no pointer dereference; output params are MInteger .value
            // For double address-of vars, use [0] instead
            if double_address_of_vars.contains(name) {
                format!("{name}[0]")
            } else {
                format!("{name}.value")
            }
        }
        Expr::AddressOf(inner) => {
            // Java has no address-of; render the inner expression directly.
            // Pass empty sets so MInteger vars render as object refs (no .value)
            // and double[] vars render as array refs (no [0]).
            let empty = HashSet::new();
            let empty2 = HashSet::new();
            render_expr(inner, single_precision, registry, helpers, &empty, &empty2, float_input_params)
        }
        Expr::PostIncrement(inner) => {
            format!(
                "{}++",
                render_expr(inner, single_precision, registry, helpers, address_of_vars, double_address_of_vars, float_input_params)
            )
        }
        Expr::PostDecrement(inner) => {
            format!(
                "{}--",
                render_expr(inner, single_precision, registry, helpers, address_of_vars, double_address_of_vars, float_input_params)
            )
        }
        Expr::PreIncrement(inner) => {
            format!(
                "++{}",
                render_expr(inner, single_precision, registry, helpers, address_of_vars, double_address_of_vars, float_input_params)
            )
        }
        Expr::PreDecrement(inner) => {
            format!(
                "--{}",
                render_expr(inner, single_precision, registry, helpers, address_of_vars, double_address_of_vars, float_input_params)
            )
        }
        Expr::Ternary(cond, then_expr, else_expr) => {
            // (cond) ? (1) : (0) → just the condition (boolean in Java)
            if is_int_literal(then_expr, 1) && is_int_literal(else_expr, 0) {
                return render_expr(cond, single_precision, registry, helpers, address_of_vars, double_address_of_vars, float_input_params);
            }
            // (cond) ? (0) : (1) → !condition
            if is_int_literal(then_expr, 0) && is_int_literal(else_expr, 1) {
                return format!(
                    "!({})",
                    render_expr(cond, single_precision, registry, helpers, address_of_vars, double_address_of_vars, float_input_params)
                );
            }
            // Default: render as Java ternary
            format!(
                "(({}) ? ({}) : ({}))",
                render_expr(cond, single_precision, registry, helpers, address_of_vars, double_address_of_vars, float_input_params),
                render_expr(then_expr, single_precision, registry, helpers, address_of_vars, double_address_of_vars, float_input_params),
                render_expr(else_expr, single_precision, registry, helpers, address_of_vars, double_address_of_vars, float_input_params)
            )
        }
    }
}

/// Convert a function identifier to `PascalCase`.
/// e.g., "RSI" -> "Rsi", "ADX" -> "Adx", "HT_DCPERIOD" -> "HtDcperiod"
fn to_pascal_case(s: &str) -> String {
    s.to_lowercase()
        .split('_')
        .map(|word| {
            let mut chars = word.chars();
            match chars.next() {
                None => String::new(),
                Some(c) => c.to_uppercase().collect::<String>() + chars.as_str(),
            }
        })
        .collect()
}

/// Convert a function name to Java `camelCase`.
/// Keeps the first segment lowercase, capitalizes subsequent segments.
/// e.g., "linearreg_angle" -> "linearregAngle", "ht_dcperiod" -> "htDcperiod"
/// Names without underscores pass through unchanged: "sma" -> "sma"
fn to_java_method_name(s: &str) -> String {
    let lower = s.to_lowercase();
    let parts: Vec<&str> = lower.split('_').collect();
    let mut result = String::new();
    for (i, part) in parts.iter().enumerate() {
        if i == 0 {
            result.push_str(part);
        } else {
            let mut chars = part.chars();
            if let Some(c) = chars.next() {
                result.extend(c.to_uppercase());
                result.push_str(chars.as_str());
            }
        }
    }
    result
}

/// Try to render a candle helper function call as an inline Java ternary chain.
///
/// Converts `ta_candlerange(rangeType, open, high, low, close)` into a nested
/// ternary that mirrors the original switch:
/// ```text
/// ((rt==0) ? Math.abs(close-open) : ((rt==1) ? (high-low) : ((rt==2) ? …)))
/// ```
///
/// `ta_candleaverage(rangeType, avgPeriod, factor, sum, open, high, low, close)`
/// becomes:
/// ```text
/// (factor * (((avgPeriod!=0) ? sum/avgPeriod : <candlerange>) / ((rt==2)?2.0:1.0)))
/// ```
///
/// Returns `None` if the function isn't a candle helper or the arg count is wrong.
#[allow(clippy::too_many_arguments)]
fn try_render_candle_ternary(
    fname: &str,
    args: &[Expr],
    single_precision: bool,
    registry: &Registry,
    helpers: &HelperRegistry,
    address_of_vars: &HashSet<String>,
    double_address_of_vars: &HashSet<String>,
    float_input_params: &HashSet<String>,
) -> Option<String> {
    let r = |e: &Expr| {
        render_expr(
            e, single_precision, registry, helpers,
            address_of_vars, double_address_of_vars, float_input_params,
        )
    };
    match fname {
        "ta_candlerange" if args.len() == 5 => {
            let rt = r(&args[0]);
            let open = r(&args[1]);
            let high = r(&args[2]);
            let low = r(&args[3]);
            let close = r(&args[4]);
            Some(format!(
                "(({rt} == 0) ? (Math.abs({close} - {open})) \
                 : (({rt} == 1) ? ({high} - {low}) \
                 : (({rt} == 2) ? (({high} - {low}) - Math.abs({close} - {open})) \
                 : 0.0)))"
            ))
        }
        "ta_candleaverage" if args.len() == 8 => {
            let rt = r(&args[0]);
            let avg_period = r(&args[1]);
            let factor = r(&args[2]);
            let sum = r(&args[3]);
            // Build the 5-element arg list for the nested ta_candlerange call:
            // [rangeType, open, high, low, close]
            let cr_args: Vec<Expr> = std::iter::once(args[0].clone())
                .chain(args[4..8].iter().cloned())
                .collect();
            let candlerange = try_render_candle_ternary(
                "ta_candlerange", &cr_args,
                single_precision, registry, helpers,
                address_of_vars, double_address_of_vars, float_input_params,
            )?;
            Some(format!(
                "(({factor} * ((({avg_period} != 0) \
                 ? ({sum} / {avg_period}) : {candlerange}) \
                 / (({rt} == 2) ? 2.0 : 1.0))))"
            ))
        }
        _ => None,
    }
}

/// Render a `FuncCall` expression to Java code.
#[allow(clippy::too_many_lines)]
fn render_func_call(
    fname: &str,
    args: &[Expr],
    single_precision: bool,
    registry: &Registry,
    helpers: &HelperRegistry,
    address_of_vars: &HashSet<String>,
    double_address_of_vars: &HashSet<String>,
    float_input_params: &HashSet<String>,
) -> String {
    // Check if this is a call to a helper function that can be inlined
    if let Some(helper) = helpers.get(fname) {
        if let Some(inlined_expr) = try_inline_expr(helper, args) {
            return render_expr(
                &inlined_expr, single_precision, registry, helpers, address_of_vars, double_address_of_vars, float_input_params,
            );
        }
        // Multi-statement helpers: Task 10 will handle
    }

    // Candle helpers: render inline as Java ternary chains instead of
    // hoisted switch blocks.  This keeps them inside the expression so
    // the && split can preserve short-circuit evaluation.
    if let Some(ternary) = try_render_candle_ternary(
        fname, args, single_precision, registry, helpers,
        address_of_vars, double_address_of_vars, float_input_params,
    ) {
        return ternary;
    }

    if fname == "UNSTABLE_PERIOD" {
        // UNSTABLE_PERIOD(RSI) -> this.unstablePeriod[FuncUnstId.Rsi.ordinal()]
        // UNSTABLE_PERIOD(FUNC_UNST_ATR) -> strip FUNC_UNST_ prefix first
        if let Some(Expr::Var(func_name)) = args.first() {
            let base = func_name
                .strip_prefix("FUNC_UNST_")
                .unwrap_or(func_name);
            let pascal = match base {
                "HT_DCPERIOD" => "HtDcPeriod".to_string(),
                "HT_DCPHASE" => "HtDcPhase".to_string(),
                "HT_PHASOR" => "HtPhasor".to_string(),
                "HT_SINE" => "HtSine".to_string(),
                "HT_TRENDLINE" => "HtTrendline".to_string(),
                "HT_TRENDMODE" => "HtTrendMode".to_string(),
                "MINUS_DI" => "MinusDI".to_string(),
                "MINUS_DM" => "MinusDM".to_string(),
                "PLUS_DI" => "PlusDI".to_string(),
                "PLUS_DM" => "PlusDM".to_string(),
                "STOCH_RSI" => "StochRsi".to_string(),
                _ => to_pascal_case(base),
            };
            return format!("this.unstablePeriod[FuncUnstId.{pascal}.ordinal()]");
        }
        "this.unstablePeriod[0]".to_string()
    } else if fname == "COMPATIBILITY" {
        // COMPATIBILITY() -> this.compatibility
        "this.compatibility".to_string()
    } else if fname == "IS_ZERO" {
        // IS_ZERO(x) -> inline epsilon check
        if let Some(arg) = args.first() {
            let x = render_expr(arg, single_precision, registry, helpers, address_of_vars, double_address_of_vars, float_input_params);
            return format!("((-0.00000000000001 < {x}) && ({x} < 0.00000000000001))");
        }
        "false".to_string()
    } else if fname == "IS_ZERO_OR_NEG" {
        // IS_ZERO_OR_NEG(x) -> (x < epsilon)
        if let Some(arg) = args.first() {
            let x = render_expr(arg, single_precision, registry, helpers, address_of_vars, double_address_of_vars, float_input_params);
            return format!("({x} < 0.00000000000001)");
        }
        "false".to_string()
    } else if fname == "ARRAY_COPY" {
        // ARRAY_COPY(dst, dstOff, src, srcOff, count)
        // -> System.arraycopy(src, srcOff, dst, dstOff, count) (note arg reordering)
        if args.len() == 5 {
            let dst = render_expr(&args[0], single_precision, registry, helpers, address_of_vars, double_address_of_vars, float_input_params);
            let dst_off =
                render_expr(&args[1], single_precision, registry, helpers, address_of_vars, double_address_of_vars, float_input_params);
            let src = render_expr(&args[2], single_precision, registry, helpers, address_of_vars, double_address_of_vars, float_input_params);
            let src_off =
                render_expr(&args[3], single_precision, registry, helpers, address_of_vars, double_address_of_vars, float_input_params);
            let count =
                render_expr(&args[4], single_precision, registry, helpers, address_of_vars, double_address_of_vars, float_input_params);
            return format!("System.arraycopy({src},{src_off},{dst},{dst_off},{count})");
        }
        "/* ARRAY_COPY: bad args */".to_string()
    } else if fname == "PER_TO_K" {
        // PER_TO_K(period) -> (2.0 / ((double)(period) + 1.0))
        if let Some(arg) = args.first() {
            let x = render_expr(arg, single_precision, registry, helpers, address_of_vars, double_address_of_vars, float_input_params);
            return format!("(2.0 / ((double)({x}) + 1.0))");
        }
        "0.0".to_string()
    } else if MATH_FUNCTIONS.contains(&fname) {
        // Java uses Math.func() for standard math functions.
        // fabs/ABS → Math.abs; max/fmax → Math.max; min/fmin → Math.min
        let java_name = match fname {
            "fabs" | "ABS" => "abs",
            "fmax" => "max",
            "fmin" => "min",
            other => other,
        };
        let rendered: Vec<String> = args
            .iter()
            .map(|a| render_expr(a, single_precision, registry, helpers, address_of_vars, double_address_of_vars, float_input_params))
            .collect();
        format!("Math.{}({})", java_name, rendered.join(", "))
    } else if fname == "sizeof" {
        // sizeof(TYPE) → 1: normalizes byte counts to element counts for Java array operations
        "1".to_string()
    } else if fname == "malloc" {
        // malloc(N * sizeof(TYPE)) → new TYPE_JAVA[(int)(N)]
        // sizeof renders as 1, so the arg is already the element count
        if let Some(arg) = args.first() {
            let java_type = match find_sizeof_type(arg).as_deref() {
                Some("int") => "int",
                Some("float") => "float",
                _ => "double",
            };
            let size = render_expr(arg, single_precision, registry, helpers, address_of_vars, double_address_of_vars, float_input_params);
            format!("new {java_type}[(int)({size})]")
        } else {
            "new double[0]".to_string()
        }
    } else if fname == "free" {
        // No-op in Java (garbage collector handles deallocation)
        String::new()
    } else if fname == "memcpy" || fname == "memmove" {
        // memcpy/memmove(dst, src, count) → System.arraycopy(src, srcOff, dst, dstOff, count)
        if args.len() >= 3 {
            let (dst_arr, dst_off) =
                decompose_java_array_ref(
                    &args[0], single_precision, registry, helpers, address_of_vars, double_address_of_vars, float_input_params,
                );
            let (src_arr, src_off) =
                decompose_java_array_ref(
                    &args[1], single_precision, registry, helpers, address_of_vars, double_address_of_vars, float_input_params,
                );
            let count =
                render_expr(&args[2], single_precision, registry, helpers, address_of_vars, double_address_of_vars, float_input_params);
            format!("System.arraycopy({src_arr}, {src_off}, {dst_arr}, {dst_off}, {count})")
        } else {
            format!("/* {fname}: bad args */")
        }
    } else if fname == "memset" {
        // memset(buf, 0, count) → java.util.Arrays.fill(buf, off, off+count, fillVal)
        if args.len() >= 3 {
            let (arr, off) =
                decompose_java_array_ref(
                    &args[0], single_precision, registry, helpers, address_of_vars, double_address_of_vars, float_input_params,
                );
            let count =
                render_expr(&args[2], single_precision, registry, helpers, address_of_vars, double_address_of_vars, float_input_params);
            let fill_val = match find_sizeof_type(&args[2]).as_deref() {
                Some("int") => "0",
                _ => "0.0",
            };
            if off == "0" {
                format!("java.util.Arrays.fill({arr}, 0, (int)({count}), {fill_val})")
            } else {
                format!(
                    "java.util.Arrays.fill({arr}, {off}, ({off}) + (int)({count}), {fill_val})"
                )
            }
        } else {
            "/* memset: bad args */".to_string()
        }
    } else {
        // Use registry for cross-call resolution
        let java_name = registry.resolve_call(fname, Lang::Java);
        let rendered: Vec<String> = args
            .iter()
            .map(|a| render_expr(a, single_precision, registry, helpers, address_of_vars, double_address_of_vars, float_input_params))
            .collect();
        format!("{}({})", java_name, rendered.join(", "))
    }
}

/// Math functions that map to `java.lang.Math` methods.
/// `fabs`/`ABS` → `Math.abs`; `max`/`fmax` → `Math.max`; `min`/`fmin` → `Math.min`.
const MATH_FUNCTIONS: &[&str] = &[
    "atan", "sqrt", "fabs", "floor", "ceil", "log", "cos", "sin", "tan", "acos", "asin", "exp",
    "cosh", "sinh", "tanh", "log10", "ABS", "max", "min", "fmax", "fmin",
];

/// Scan an expression tree for `sizeof(TYPE)` and return the type name.
/// Used by `malloc` to determine the Java array element type.
fn find_sizeof_type(expr: &Expr) -> Option<String> {
    match expr {
        Expr::FuncCall(name, args) if name == "sizeof" => args
            .first()
            .and_then(|a| match a {
                Expr::Var(type_name) => Some(type_name.clone()),
                _ => None,
            }),
        Expr::BinOp(left, _, right) => {
            find_sizeof_type(left).or_else(|| find_sizeof_type(right))
        }
        Expr::Cast(_, inner) => find_sizeof_type(inner),
        _ => None,
    }
}

/// Decompose an expression into (array_name, offset) for array copy operations.
/// `Var("arr")` → `("arr", "0")`; `AddressOf(ArrayAccess("arr", idx))` → `("arr", rendered_idx)`
fn decompose_java_array_ref(
    expr: &Expr,
    single_precision: bool,
    registry: &Registry,
    helpers: &HelperRegistry,
    address_of_vars: &HashSet<String>,
    double_address_of_vars: &HashSet<String>,
    float_input_params: &HashSet<String>,
) -> (String, String) {
    match expr {
        Expr::AddressOf(inner) => {
            if let Expr::ArrayAccess(name, offset) = inner.as_ref() {
                let off = render_expr(
                    offset, single_precision, registry, helpers, address_of_vars, double_address_of_vars, float_input_params,
                );
                (name.clone(), off)
            } else {
                let s = render_expr(
                    expr, single_precision, registry, helpers, address_of_vars, double_address_of_vars, float_input_params,
                );
                (s, "0".to_string())
            }
        }
        Expr::Var(name) => (name.clone(), "0".to_string()),
        _ => {
            let s = render_expr(
                expr, single_precision, registry, helpers, address_of_vars, double_address_of_vars, float_input_params,
            );
            (s, "0".to_string())
        }
    }
}

/// Render a complex lookback body (`LookbackExpr::Code`) into Java code.
fn render_lookback_code(
    stmts: &[Statement],
    enums: &HashMap<String, EnumDef>,
    registry: &Registry,
    helpers: &HelperRegistry,
) -> String {
    let mut out = String::new();
    let inline_counter = Cell::new(0);
    // Lookback bodies don't have cross-indicator calls, so no address-of vars
    let address_of_vars = HashSet::new();
    let double_address_of_vars = HashSet::new();
    // Lookback bodies are always double-precision; no float input params needed
    let float_input_params: HashSet<String> = HashSet::new();

    // Declare local variables
    for stmt in stmts {
        if let Statement::VarDecl { var_type, name, .. } = stmt {
            let java_decl = match var_type {
                VarType::Real => format!("double {name}"),
                VarType::Integer | VarType::Index => format!("int {name}"),
                VarType::RetCodeType => format!("RetCode {name}"),
                VarType::RealPointer => format!("double[] {name}"),
                VarType::IntPointer => format!("int[] {name}"),
                VarType::RealArray(size) => format!("double[] {name} = new double[{size}]"),
                VarType::IntArray(size) => format!("int[] {name} = new int[{size}]"),
            };
            out.push_str(&format!("      {java_decl};\n"));
        }
    }

    // Emit candle settings unpacking for lookback body
    let candle_used = detect_candle_settings(stmts);
    if !candle_used.is_empty() {
        out.push_str(&emit_java_unpacking(&candle_used, 6));
    }

    // Emit VarDecl initializations
    for stmt in stmts {
        if let Statement::VarDecl {
            name,
            init: Some(init),
            ..
        } = stmt
        {
            out.push_str(&format!(
                "      {} = {};\n",
                name,
                render_expr(init, false, registry, helpers, &address_of_vars, &double_address_of_vars, &float_input_params)
            ));
        }
    }

    // Render non-VarDecl statements
    for stmt in stmts {
        if matches!(stmt, Statement::VarDecl { .. }) {
            continue;
        }
        out.push_str(&render_statement(
            stmt, 6, false, enums, registry, helpers, &inline_counter,
            &address_of_vars, &double_address_of_vars, &float_input_params,
        ));
    }

    out
}

#[cfg(test)]
mod tests {
    use super::*;
    use crate::parser;
    use crate::registry::Registry;
    use std::path::Path;

    fn make_registry() -> Registry {
        let base = Path::new(env!("CARGO_MANIFEST_DIR")).join("../../ta_func_defs");
        Registry::from_dir(&base)
    }

    fn load_sma() -> FuncDef {
        let base = Path::new(env!("CARGO_MANIFEST_DIR"));
        let yaml_path = base.join("../../ta_func_defs/sma/sma.yaml");
        let c_path = base.join("../../ta_func_defs/sma/sma.c");
        let mut func_def = parser::yaml::parse_yaml(&yaml_path);
        let parsed = parser::c_source::parse_c_source(&c_path);
        func_def.body = parsed.functions[0].body.clone();
        func_def.lookback = Some(LookbackExpr::Code(parsed.lookback_body));
        func_def
    }

    #[test]
    fn test_java_generates_logic_variant() {
        let func = load_sma();
        let enums = HashMap::new();
        let registry = make_registry();
        let output = generate(&func, &enums, &registry, &HelperRegistry::empty());

        // Should contain the logic variant
        assert!(output.contains("smaLogic("), "Missing smaLogic function");

        // Logic variant should NOT have validation
        // Find the smaLogic section and verify no validation
        let logic_pos = output.find("smaLogic( ").unwrap();
        let logic_section = &output[logic_pos..];
        let next_fn_pos = logic_section
            .find("   public RetCode")
            .unwrap_or(logic_section.len());
        let logic_body = &logic_section[..next_fn_pos];
        assert!(
            !logic_body.contains("OutOfRangeStartIndex"),
            "Logic variant should not contain validation"
        );

        // The guarded variant should have validation
        let guarded_pos = output.find("public RetCode sma( ").unwrap();
        let guarded_section = &output[guarded_pos..];
        let guarded_end = guarded_section
            .find("public RetCode smaLogic(")
            .unwrap_or(guarded_section.len());
        let guarded_body = &guarded_section[..guarded_end];
        assert!(
            guarded_body.contains("OutOfRangeStartIndex"),
            "Guarded variant should contain validation"
        );
    }
}
