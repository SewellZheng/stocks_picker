use std::collections::HashMap;

use std::cell::Cell;

use crate::candle_settings::{detect_candle_settings, emit_c_unpacking};
use crate::helper_registry::{hoist_block_helpers, try_inline_expr, HelperRegistry};
use crate::ir::{BinOp, EnumDef, Expr, FuncDef, LookbackExpr, ParamType, Statement, VarType};
use crate::parser::enums::lookup_variant;
use crate::registry::{Lang, Registry};

/// Candle helper functions emitted as C preprocessor macros instead of expanded code.
/// This enables compiler loop-unswitching, matching the reference library's macro pattern.
const C_CANDLE_MACRO_FNS: &[&str] = &["ta_candlerange", "ta_candleaverage"];

#[allow(clippy::implicit_hasher)]
pub fn generate(
    func: &FuncDef,
    enums: &HashMap<String, EnumDef>,
    registry: &Registry,
    helpers: &HelperRegistry,
) -> String {
    let mut out = String::new();
    out.push_str(&gen_header(func));
    out.push_str(&gen_lookback(func, enums, registry, helpers));

    // For functions with explicit _private, emit Private and Logic BEFORE guarded
    // so the C compiler knows the signatures when the guarded body calls them.
    if func.has_explicit_private {
        out.push_str(&gen_private(func, enums, registry, helpers)); // double-only private
        out.push_str(&gen_func(func, false, true, enums, registry, helpers)); // double-precision logic
        out.push_str(&gen_func(func, false, false, enums, registry, helpers)); // double-precision guarded
        out.push_str(&gen_func(func, true, true, enums, registry, helpers)); // single-precision logic
        out.push_str(&gen_func(func, true, false, enums, registry, helpers)); // single-precision guarded
    } else {
        out.push_str(&gen_func(func, false, false, enums, registry, helpers)); // double-precision guarded
        out.push_str(&gen_func(func, false, true, enums, registry, helpers)); // double-precision logic
        out.push_str(&gen_func(func, true, false, enums, registry, helpers)); // single-precision guarded
        out.push_str(&gen_func(func, true, true, enums, registry, helpers)); // single-precision logic
    }
    out
}

fn gen_header(_func: &FuncDef) -> String {
    let mut out = String::new();
    out.push_str(
        "/* TA-LIB Copyright (c) 1999-2025, Mario Fortier\n\
         * All rights reserved.\n\
         *\n\
         * Redistribution and use in source and binary forms, with or\n\
         * without modification, are permitted provided that the following\n\
         * conditions are met:\n\
         *\n\
         * - Redistributions of source code must retain the above copyright\n\
         *   notice, this list of conditions and the following disclaimer.\n\
         *\n\
         * - Redistributions in binary form must reproduce the above copyright\n\
         *   notice, this list of conditions and the following disclaimer in\n\
         *   the documentation and/or other materials provided with the\n\
         *   distribution.\n\
         *\n\
         * - Neither name of author nor the names of its contributors\n\
         *   may be used to endorse or promote products derived from this\n\
         *   software without specific prior written permission.\n\
         *\n\
         * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS\n\
         * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT\n\
         * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS\n\
         * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE\n\
         * REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,\n\
         * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES\n\
         * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS\n\
         * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS\n\
         * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,\n\
         * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE\n\
         * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,\n\
         * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.\n\
         */\n\n",
    );

    // Match the exact includes from the c-ref (gen_code output).
    out.push_str(
        "#include <string.h>\n\
         #include <math.h>\n\
         #include \"ta_func.h\"\n\
         #include \"ta_utility.h\"\n\
         #include \"ta_memory.h\"\n\n",
    );

    out
}

fn gen_lookback(
    func: &FuncDef,
    enums: &HashMap<String, EnumDef>,
    registry: &Registry,
    helpers: &HelperRegistry,
) -> String {
    let name = &func.name;

    let has_opt_params = !func.optional_inputs.is_empty();

    // Build parameter list for signature
    let param_str = if has_opt_params {
        let params: Vec<String> = func
            .optional_inputs
            .iter()
            .map(|opt| {
                let c_type = match &opt.param_type {
                    ParamType::Real => "double".to_string(),
                    ParamType::Integer => "int".to_string(),
                    ParamType::Enum(name) => format!("TA_{name}"),
                    ParamType::Price(_) => unreachable!("Price expanded during parsing"),
                };
                format!("{} {}", c_type, opt.name)
            })
            .collect();
        format!(" {} ", params.join(", "))
    } else {
        " void ".to_string()
    };

    let body = match &func.lookback {
        Some(LookbackExpr::Literal(n)) => format!("   return {n};\n"),
        Some(LookbackExpr::ParamMinus(param, offset)) => {
            format!("   return {param} - {offset};\n")
        }
        Some(LookbackExpr::Code(stmts)) => render_lookback_code(stmts, enums, registry, helpers),
        None => "   return 0;\n".to_string(),
    };

    format!(
        "TA_LIB_API int TA_{name}_Lookback({param_str})\n\
         {{\n\
         {body}\
         }}\n\n"
    )
}

/// Generate the double-only `TA_{NAME}_Private` function variant.
/// Same body and params as `gen_func(logic=true)` but with `_Private` naming.
fn gen_private(
    func: &FuncDef,
    enums: &HashMap<String, EnumDef>,
    registry: &Registry,
    helpers: &HelperRegistry,
) -> String {
    let name_override = format!("TA_{}_Private", func.name);
    gen_func_inner(func, false, true, Some(&name_override), enums, registry, helpers)
}

#[allow(clippy::too_many_lines)]
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

#[allow(clippy::too_many_lines)]
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

    let prefix = if let Some(name) = name_override {
        name.to_string()
    } else {
        match (single_precision, logic) {
            (false, false) => format!("TA_{}", func.name),
            (false, true) => format!("TA_{}_Unguarded", func.name),
            (true, false) => format!("TA_S_{}", func.name),
            (true, true) => format!("TA_S_{}_Unguarded", func.name),
        }
    };

    let ret_type = if single_precision {
        "TA_RetCode"
    } else {
        "TA_LIB_API TA_RetCode"
    };

    // Build parameter list
    let mut params: Vec<String> = Vec::new();
    params.push("int    startIdx".to_string());
    params.push("int    endIdx".to_string());

    for input in &func.inputs {
        let c_type = if single_precision {
            match input.param_type {
                ParamType::Real => "const float",
                ParamType::Integer | ParamType::Enum(_) | ParamType::Price(_) => "const int",
            }
        } else {
            match input.param_type {
                ParamType::Real => "const double",
                ParamType::Integer | ParamType::Enum(_) | ParamType::Price(_) => "const int",
            }
        };
        params.push(format!("{} {}[]", c_type, input.name));
    }

    for opt in &func.optional_inputs {
        let c_type = match &opt.param_type {
            ParamType::Real => "double".to_string(),
            ParamType::Integer => "int".to_string(),
            ParamType::Enum(name) => format!("TA_{name}"),
            ParamType::Price(_) => unreachable!("Price expanded during parsing"),
        };
        params.push(format!("{} {}", c_type, opt.name));
    }

    // Extra params only on _Private variant (via name_override)
    if name_override.is_some() {
        for (param_name, c_type) in &func.private_extra_params {
            params.push(format!("{c_type} {param_name}"));
        }
    }

    // Output scalars (always present)
    params.push("int          *outBegIdx".to_string());
    params.push("int          *outNBElement".to_string());

    for output in &func.outputs {
        let c_type = match output.param_type {
            ParamType::Real => "double",
            ParamType::Integer | ParamType::Enum(_) | ParamType::Price(_) => "int",
        };
        params.push(format!("{}        {}[]", c_type, output.name));
    }

    // Format the function signature
    let indent = " ".repeat(ret_type.len() + 1 + prefix.len() + 2);
    out.push_str(&format!("{ret_type} {prefix}( "));
    for (i, param) in params.iter().enumerate() {
        if i > 0 {
            out.push_str(&format!(",\n{indent}"));
        }
        out.push_str(param);
    }
    out.push_str(" )\n");

    // Function body
    out.push_str("{\n");

    // Body selection:
    // - Private variant (name_override set): always private_body
    // - S_ variants with explicit _private: inline private_body (can't call double-only Private)
    // - Double variants with explicit _private (guarded OR logic): body delegates to TA_*_Private
    // - Logic without _private: private_body (auto-copied from body, same content)
    // - Guarded without _private: body
    let body = if name_override.is_some() {
        &func.private_body  // Private variant: actual computation body
    } else if single_precision && func.has_explicit_private {
        &func.private_body  // S_ variants: inline private body
    } else if func.has_explicit_private {
        &func.body          // double guarded/logic: body delegates to _Private
    } else if logic {
        &func.private_body  // logic variant without _private
    } else {
        &func.body          // guarded without _private
    };

    // Declare local variables from body (deduplicated)
    let mut declared_vars: Vec<String> = Vec::new();
    for stmt in body {
        emit_c_var_decls(stmt, &mut out, &mut declared_vars);
    }

    // For S_ variants with explicit _private: emit private_param_init as local VarDecls.
    // These provide the extra params (e.g., k factor) that the inlined private body needs.
    // Both guarded and logic S_ variants need this (both use private_body).
    if single_precision && func.has_explicit_private && name_override.is_none() {
        for (param_name, init_expr) in &func.private_param_init {
            let c_type = func
                .private_extra_params
                .iter()
                .find(|(n, _)| n == param_name)
                .map_or("double", |(_, t)| t.as_str());
            let init_c = render_expr(init_expr, single_precision, registry, helpers);
            out.push_str(&format!("   {c_type} {param_name} = {init_c};\n"));
            declared_vars.push(param_name.clone());
        }
    }

    // Emit candle settings unpacking (only for referenced settings)
    let candle_used = detect_candle_settings(body);
    if !candle_used.is_empty() {
        out.push_str(&emit_c_unpacking(&candle_used, 3));
    }

    out.push('\n');

    // Validation (omitted for Logic/unguarded variant)
    if !logic {
        out.push_str("   if( startIdx < 0 )\n");
        out.push_str("      return TA_OUT_OF_RANGE_START_INDEX;\n");
        out.push_str("   if( (endIdx < 0) || (endIdx < startIdx) )\n");
        out.push_str("      return TA_OUT_OF_RANGE_END_INDEX;\n");
        out.push('\n');

        // Input array NULL checks
        for input in &func.inputs {
            out.push_str(&format!("   if( !{} )\n", input.name));
            out.push_str("      return TA_BAD_PARAM;\n");
        }

        // Optional parameter validation (default + range)
        for opt in &func.optional_inputs {
            match &opt.param_type {
                ParamType::Integer | ParamType::Enum(_) => {
                    if let Some(default_val) = opt.default {
                        out.push_str(&format!(
                            "   if( (int){name} == (int)0x80000000 )\n      {name} = {val};\n",
                            name = opt.name,
                            val = default_val as i32
                        ));
                        if let Some((min, max)) = opt.range {
                            let min_i = min as i32;
                            let max_i = max as i32;
                            out.push_str(&format!(
                                "   else if( (int){name} < {min_i} || (int){name} > {max_i} )\n      return TA_BAD_PARAM;\n",
                                name = opt.name
                            ));
                        }
                    }
                }
                ParamType::Real => {
                    if let Some(default_val) = opt.default {
                        out.push_str(&format!(
                            "   if( {name} == -4e37 )\n      {name} = {val};\n",
                            name = opt.name,
                            val = default_val
                        ));
                        if let Some((min, max)) = opt.range {
                            // Skip unbounded ranges (f64::MIN/MAX = no real constraint)
                            if min > f64::MIN / 2.0 || max < f64::MAX / 2.0 {
                                out.push_str(&format!(
                                    "   else if( {name} < {min:e} || {name} > {max:e} )\n      return TA_BAD_PARAM;\n",
                                    name = opt.name
                                ));
                            }
                        }
                    }
                }
                _ => {}
            }
        }

        // Output array NULL checks
        for output in &func.outputs {
            out.push_str(&format!("   if( !{} )\n", output.name));
            out.push_str("      return TA_BAD_PARAM;\n");
        }
        out.push('\n');
    }

    let inline_counter = Cell::new(0);

    // Determine which VarDecl names are first-occurrence (eligible for hoisted init).
    // A VarDecl with init is only hoisted if it's the first occurrence of that name.
    // Duplicate VarDecls (same name, second or later occurrence) are rendered inline.
    let mut first_seen_names: Vec<String> = Vec::new();
    collect_first_seen_var_names(body, &mut first_seen_names);

    // Emit hoisted VarDecl initializations (first-occurrence only)
    for stmt in body {
        if let Statement::VarDecl {
            name,
            init: Some(init),
            ..
        } = stmt
        {
            if first_seen_names.contains(name) {
                // This is the first occurrence with init — hoist it
                first_seen_names.retain(|n| n != name); // remove so duplicates aren't hoisted
                let mut hoisted = Vec::new();
                let mut cnt = inline_counter.get();
                let new_init = hoist_block_helpers(init, helpers, &mut hoisted, &mut cnt, C_CANDLE_MACRO_FNS);
                inline_counter.set(cnt);
                out.push_str(&render_hoisted_blocks(
                    &hoisted,
                    3,
                    single_precision,
                    enums,
                    registry,
                    helpers,
                    &inline_counter,
                ));
                out.push_str(&format!(
                    "   {} = {};\n",
                    name,
                    render_expr(&new_init, single_precision, registry, helpers)
                ));
            }
        }
    }

    // Render remaining body statements.
    // First-occurrence VarDecls (with or without init) are skipped.
    // Duplicate VarDecls with init are rendered as plain assignments in their natural position.
    let mut body_seen: Vec<String> = Vec::new();
    for stmt in body {
        match stmt {
            Statement::VarDecl { name, init, .. } => {
                if body_seen.contains(name) {
                    // Duplicate VarDecl: render as assignment if it has an init
                    if let Some(init_expr) = init {
                        let mut hoisted = Vec::new();
                        let mut cnt = inline_counter.get();
                        let new_init =
                            hoist_block_helpers(init_expr, helpers, &mut hoisted, &mut cnt, C_CANDLE_MACRO_FNS);
                        inline_counter.set(cnt);
                        out.push_str(&render_hoisted_blocks(
                            &hoisted,
                            3,
                            single_precision,
                            enums,
                            registry,
                            helpers,
                            &inline_counter,
                        ));
                        out.push_str(&format!(
                            "   {} = {};\n",
                            name,
                            render_expr(&new_init, single_precision, registry, helpers)
                        ));
                    }
                } else {
                    body_seen.push(name.clone());
                }
            }
            Statement::Block { body } => {
                // Track VarDecl names inside Blocks (multi-var declarations)
                // and render non-VarDecl statements (e.g., chained assignments
                // like `*outBegIdx = today = startIdx;` which parse into a Block
                // containing [Assign(today=startIdx), Assign(*outBegIdx=today)]).
                for s in body {
                    if let Statement::VarDecl { name, .. } = s {
                        if !body_seen.contains(name) {
                            body_seen.push(name.clone());
                        }
                    } else {
                        out.push_str(&render_statement(
                            s,
                            3,
                            single_precision,
                            enums,
                            registry,
                            helpers,
                            &inline_counter,
                        ));
                    }
                }
            }
            _ => {
                out.push_str(&render_statement(
                    stmt,
                    3,
                    single_precision,
                    enums,
                    registry,
                    helpers,
                    &inline_counter,
                ));
            }
        }
    }

    out.push_str("\n   return TA_SUCCESS;\n");
    out.push_str("}\n\n");

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
) -> String {
    match stmt {
        Statement::Block { body } => body
            .iter()
            .map(|s| {
                render_statement(s, 0, single_precision, enums, registry, helpers, inline_counter)
                    .trim()
                    .trim_end_matches(';')
                    .to_string()
            })
            .collect::<Vec<_>>()
            .join(", "),
        _ => render_statement(stmt, 0, single_precision, enums, registry, helpers, inline_counter)
            .trim()
            .trim_end_matches(';')
            .to_string(),
    }
}

/// Render hoisted block-inline helpers as C code (temp var decl + body).
fn render_hoisted_blocks(
    hoisted: &[(String, VarType, Vec<Statement>)],
    indent: usize,
    single_precision: bool,
    enums: &HashMap<String, EnumDef>,
    registry: &Registry,
    helpers: &HelperRegistry,
    inline_counter: &Cell<usize>,
) -> String {
    let pad = " ".repeat(indent);
    let mut out = String::new();
    for (temp_name, var_type, body) in hoisted {
        let c_decl = match var_type {
            VarType::Real => format!("double {temp_name}"),
            VarType::Integer | VarType::Index => format!("int {temp_name}"),
            VarType::RetCodeType => format!("TA_RetCode {temp_name}"),
            VarType::RealPointer => format!("double *{temp_name}"),
            VarType::IntPointer => format!("int *{temp_name}"),
            VarType::RealArray(size) => format!("double {temp_name}[{size}]"),
            VarType::IntArray(size) => format!("int {temp_name}[{size}]"),
        };
        out.push_str(&format!("{pad}{c_decl};\n"));
        for stmt in body {
            out.push_str(&render_statement(
                stmt,
                indent,
                single_precision,
                enums,
                registry,
                helpers,
                inline_counter,
            ));
        }
    }
    out
}

#[allow(clippy::too_many_lines, clippy::cognitive_complexity, clippy::implicit_hasher)]
pub fn render_statement(
    stmt: &Statement,
    indent: usize,
    single_precision: bool,
    enums: &HashMap<String, EnumDef>,
    registry: &Registry,
    helpers: &HelperRegistry,
    inline_counter: &Cell<usize>,
) -> String {
    let pad = " ".repeat(indent);
    match stmt {
        Statement::VarDecl {
            var_type,
            name,
            init,
        } => {
            let c_decl = match var_type {
                VarType::Real => format!("double {name}"),
                VarType::Integer | VarType::Index => format!("int {name}"),
                VarType::RetCodeType => format!("TA_RetCode {name}"),
                VarType::RealPointer => format!("double *{name}"),
                VarType::IntPointer => format!("int *{name}"),
                VarType::RealArray(size) => format!("double {name}[{size}]"),
                VarType::IntArray(size) => format!("int {name}[{size}]"),
            };
            match init {
                Some(init_expr) => {
                    // Hoist multi-statement helpers from the init expression
                    let mut hoisted = Vec::new();
                    let mut cnt = inline_counter.get();
                    let new_init = hoist_block_helpers(
                        init_expr, helpers, &mut hoisted, &mut cnt, C_CANDLE_MACRO_FNS,
                    );
                    inline_counter.set(cnt);
                    let mut out = render_hoisted_blocks(
                        &hoisted, indent, single_precision, enums, registry,
                        helpers, inline_counter,
                    );
                    out.push_str(&format!(
                        "{pad}{c_decl} = {};\n",
                        render_expr(&new_init, single_precision, registry, helpers)
                    ));
                    out
                }
                None => format!("{pad}{c_decl};\n"),
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
                        return format!(
                            "{}{};\n",
                            pad,
                            render_func_call(fname, args, single_precision, registry, helpers)
                        );
                    }
                }
            }

            // Hoist multi-statement helpers from the value expression
            let mut hoisted = Vec::new();
            let mut cnt = inline_counter.get();
            let new_value = hoist_block_helpers(
                value, helpers, &mut hoisted, &mut cnt, C_CANDLE_MACRO_FNS,
            );
            inline_counter.set(cnt);
            let mut out = render_hoisted_blocks(
                &hoisted, indent, single_precision, enums, registry,
                helpers, inline_counter,
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
                                let target_str =
                                    render_assign_target(target, single_precision, registry, helpers);
                                out.push_str(&format!(
                                    "{}{}{} {};\n",
                                    pad,
                                    target_str,
                                    op_str,
                                    render_expr(right, single_precision, registry, helpers)
                                ));
                                return out;
                            }
                        }
                    }
                }
            }
            let target_str = render_assign_target(target, single_precision, registry, helpers);
            let value_str = render_expr(&new_value, single_precision, registry, helpers);
            out.push_str(&format!("{pad}{target_str}= {value_str};\n"));
            out
        }
        Statement::While { condition, body } => {
            let mut hoisted = Vec::new();
            let mut cnt = inline_counter.get();
            let new_cond = hoist_block_helpers(condition, helpers, &mut hoisted, &mut cnt, C_CANDLE_MACRO_FNS);
            inline_counter.set(cnt);
            let mut out = String::new();
            out.push_str(&render_hoisted_blocks(
                &hoisted, indent, single_precision, enums, registry, helpers, inline_counter,
            ));
            out.push_str(&format!(
                "{}while( {} )\n{}{{\n",
                pad,
                render_expr(&new_cond, single_precision, registry, helpers),
                pad
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
                ));
            }
            out.push_str(&format!("{pad}}}\n"));
            out
        }
        Statement::DoWhile { condition, body } => {
            let mut out = format!("{pad}do\n{pad}{{\n");
            for s in body {
                out.push_str(&render_statement(
                    s,
                    indent + 3,
                    single_precision,
                    enums,
                    registry,
                    helpers,
                    inline_counter,
                ));
            }
            let mut hoisted = Vec::new();
            let mut cnt = inline_counter.get();
            let new_cond = hoist_block_helpers(condition, helpers, &mut hoisted, &mut cnt, C_CANDLE_MACRO_FNS);
            inline_counter.set(cnt);
            out.push_str(&render_hoisted_blocks(
                &hoisted, indent, single_precision, enums, registry, helpers, inline_counter,
            ));
            out.push_str(&format!(
                "{}}} while( {} );\n",
                pad,
                render_expr(&new_cond, single_precision, registry, helpers)
            ));
            out
        }
        Statement::If {
            condition,
            then_body,
            else_body,
        } => {
            // Split `if(A && B)` into nested `if(A) { if(B)` when either side
            // contains a candle macro call (ta_candlerange/ta_candleaverage).
            // This prevents the compiler from speculatively computing both sides
            // of the &&, which wastes expensive fdiv cycles on the common path
            // where the first condition fails.
            if let Expr::BinOp(left, BinOp::And, right) = condition {
                if expr_directly_contains_candle_call(left)
                    && expr_directly_contains_candle_call(right)
                {
                    // Render as: if(left) { if(right) { then } else { els } } else { els }
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
                        helpers, inline_counter,
                    );
                }
            }

            let mut hoisted = Vec::new();
            let mut cnt = inline_counter.get();
            let new_cond = hoist_block_helpers(condition, helpers, &mut hoisted, &mut cnt, C_CANDLE_MACRO_FNS);
            inline_counter.set(cnt);
            let mut out = String::new();
            out.push_str(&render_hoisted_blocks(
                &hoisted, indent, single_precision, enums, registry, helpers, inline_counter,
            ));
            out.push_str(&format!(
                "{}if( {} )\n{}{{\n",
                pad,
                render_expr(&new_cond, single_precision, registry, helpers),
                pad
            ));
            for s in then_body {
                out.push_str(&render_statement(
                    s,
                    indent + 3,
                    single_precision,
                    enums,
                    registry,
                    helpers,
                    inline_counter,
                ));
            }
            if else_body.is_empty() {
                out.push_str(&format!("{pad}}}\n"));
            } else {
                out.push_str(&format!("{pad}}} else "));
                // Check if the else body is a single if statement (else-if chain)
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
                        );
                        out.push_str(if_str.trim_start());
                        return out;
                    }
                }
                out.push_str(&format!("\n{pad}{{\n"));
                for s in else_body {
                    out.push_str(&render_statement(
                        s,
                        indent + 3,
                        single_precision,
                        enums,
                        registry,
                        helpers,
                        inline_counter,
                    ));
                }
                out.push_str(&format!("{pad}}}\n"));
            }
            out
        }
        Statement::Return { value } => match value {
            Some(expr) => {
                let rendered = render_return_expr(expr, single_precision, registry, helpers);
                format!("{pad}return {rendered};\n")
            }
            None => format!("{pad}return;\n"),
        },
        Statement::For { var, count, body } => {
            let mut out = format!(
                "{}for( {} = {}; {} > 0; {}-- )\n{}{{\n",
                pad,
                var,
                render_expr(count, single_precision, registry, helpers),
                var,
                var,
                pad
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
            );
            let update_str = render_forc_part(
                update, single_precision, enums, registry, helpers, inline_counter,
            );
            let mut out = format!(
                "{}for( {}; {}; {} )\n{}{{\n",
                pad,
                init_str.trim(),
                render_expr(condition, single_precision, registry, helpers),
                update_str.trim(),
                pad
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
                ));
            }
            out.push_str(&format!("{pad}}}\n"));
            out
        }
        Statement::Block { body } => {
            let mut out = String::new();
            for s in body {
                out.push_str(&render_statement(
                    s,
                    indent,
                    single_precision,
                    enums,
                    registry,
                    helpers,
                    inline_counter,
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
            // Detect candle rangeType switches: each case assigns to the same
            // variable. Emit as a ternary chain instead of a switch — the compiler
            // loop-unswitches ternary chains much more aggressively, producing
            // specialized tight loops like the reference library's macro expansion.
            if let Some(ternary) = try_render_switch_as_ternary(
                expr, cases, default, &pad, single_precision, registry, helpers,
            ) {
                return ternary;
            }

            let mut out = format!(
                "{}switch( {} )\n{}{{\n",
                pad,
                render_expr(expr, single_precision, registry, helpers),
                pad
            );
            for (label, case_body) in cases {
                let c_label = render_c_switch_label(label, enums);
                out.push_str(&format!("{pad}case {c_label}:\n"));
                for s in case_body {
                    out.push_str(&render_statement(
                        s,
                        indent + 3,
                        single_precision,
                        enums,
                        registry,
                        helpers,
                        inline_counter,
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
                    ));
                }
                out.push_str(&format!("{pad}   break;\n"));
            }
            out.push_str(&format!("{pad}}}\n"));
            out
        }
    }
}

/// Try to render a switch as a ternary chain.
///
/// Detects the pattern where every case (and default) assigns to the SAME target variable.
/// This is the candle rangeType switch pattern. Ternary chains get loop-unswitched by
/// the compiler much more aggressively than switch statements, matching the reference
/// library's `TA_CandleRange()` macro behavior.
///
/// Returns `Some(rendered)` if the switch can be converted, `None` otherwise.
#[allow(clippy::too_many_arguments)]
fn try_render_switch_as_ternary(
    expr: &Expr,
    cases: &[(String, Vec<Statement>)],
    default: &[Statement],
    pad: &str,
    single_precision: bool,
    registry: &Registry,
    helpers: &HelperRegistry,
) -> Option<String> {
    // Only convert switches with numeric case labels (candle rangeType pattern).
    // Enum switches (like MA's MAType dispatch) must stay as switch statements.
    for (label, _) in cases {
        if label.parse::<i32>().is_err() {
            return None;
        }
    }

    // Each case must be exactly one Assign statement to the same target.
    let mut target_name: Option<String> = None;
    let mut case_exprs: Vec<(&str, String)> = Vec::new();

    for (label, body) in cases {
        if body.len() != 1 {
            return None;
        }
        if let Statement::Assign {
            target,
            value,
            compound: false,
        } = &body[0]
        {
            let tgt = render_expr(target, single_precision, registry, helpers);
            if let Some(ref prev) = target_name {
                if *prev != tgt {
                    return None; // Different targets — not a simple switch
                }
            } else {
                target_name = Some(tgt.clone());
            }
            let val = render_expr(value, single_precision, registry, helpers);
            case_exprs.push((label.as_str(), val));
        } else {
            return None; // Not a simple assignment
        }
    }

    // Default must also be a single assignment to the same target (or empty)
    let default_expr = if default.len() == 1 {
        if let Statement::Assign {
            target,
            value,
            compound: false,
        } = &default[0]
        {
            let tgt = render_expr(target, single_precision, registry, helpers);
            if target_name.as_ref().is_some_and(|t| *t != tgt) {
                return None;
            }
            render_expr(value, single_precision, registry, helpers)
        } else {
            return None;
        }
    } else if default.is_empty() {
        "0.0".to_string()
    } else {
        return None;
    };

    let target = target_name?;
    let switch_expr = render_expr(expr, single_precision, registry, helpers);

    // Build nested ternary: (expr==0 ? val0 : (expr==1 ? val1 : (expr==2 ? val2 : default)))
    let mut ternary = default_expr;
    for (label, val) in case_exprs.iter().rev() {
        ternary = format!("(({switch_expr}=={label}) ? ({val}) : ({ternary}))");
    }

    Some(format!("{pad}{target} = {ternary};\n"))
}

/// Render a switch case label for C output.
/// Looks up the label in the enum registry to generate `ENUM_CASE(Type, C_Name, Pascal)`.
/// Falls back to the raw label if not an enum variant.
fn render_c_switch_label(label: &str, enums: &HashMap<String, EnumDef>) -> String {
    if let Some((enum_name, variant)) = lookup_variant(label, enums) {
        format!(
            "ENUM_CASE({}, {}, {})",
            enum_name, variant.c_name, variant.pascal_name
        )
    } else {
        label.to_string()
    }
}

fn render_assign_target(
    expr: &Expr,
    single_precision: bool,
    registry: &Registry,
    helpers: &HelperRegistry,
) -> String {
    match expr {
        Expr::Var(name) if name == "outBegIdx" || name == "outNBElement" => {
            format!("*{name} ")
        }
        Expr::Var(name) => format!("{name} "),
        Expr::ArrayAccess(name, idx) => {
            format!(
                "{}[{}] ",
                name,
                render_expr(idx, single_precision, registry, helpers)
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
        | Expr::Ternary(_, _, _) => render_expr(expr, single_precision, registry, helpers),
    }
}

/// Render a return expression, mapping known enum values to C constants.
fn render_return_expr(
    expr: &Expr,
    single_precision: bool,
    registry: &Registry,
    helpers: &HelperRegistry,
) -> String {
    // Handle known RetCode enum values when expressed as Var
    if let Expr::Var(name) = expr {
        return match name.as_str() {
            "SUCCESS" => "TA_SUCCESS".to_string(),
            "BadParam" => "TA_BAD_PARAM".to_string(),
            "OutOfRangeEndIndex" => "TA_OUT_OF_RANGE_END_INDEX".to_string(),
            "OutOfRangeStartIndex" => "TA_OUT_OF_RANGE_START_INDEX".to_string(),
            "ALLOC_ERR" => "TA_ALLOC_ERR".to_string(),
            "INTERNAL_ERROR" => "TA_INTERNAL_ERROR".to_string(),
            _ => render_expr(expr, single_precision, registry, helpers),
        };
    }
    render_expr(expr, single_precision, registry, helpers)
}

#[allow(clippy::too_many_lines)]
fn render_expr(
    expr: &Expr,
    single_precision: bool,
    registry: &Registry,
    helpers: &HelperRegistry,
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
        Expr::Var(name) => match name.as_str() {
            "COMPATIBILITY" => "TA_GLOBALS_COMPATIBILITY".to_string(),
            "METASTOCK" => {
                "ENUM_VALUE(Compatibility,TA_COMPATIBILITY_METASTOCK,Metastock)".to_string()
            }
            "DEFAULT" => "ENUM_VALUE(Compatibility,TA_COMPATIBILITY_DEFAULT,Default)".to_string(),
            "BAD_PARAM" => "TA_BAD_PARAM".to_string(),
            "SUCCESS" => "TA_SUCCESS".to_string(),
            "ALLOC_ERR" => "TA_ALLOC_ERR".to_string(),
            "INTERNAL_ERROR" => "TA_INTERNAL_ERROR".to_string(),
            _ => name.clone(),
        },
        Expr::ArrayAccess(name, idx) => {
            format!(
                "{}[{}]",
                name,
                render_expr(idx, single_precision, registry, helpers)
            )
        }
        Expr::BinOp(left, op, right) => {
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
            // In single-precision variants, pointer aliasing checks compare
            // float* inputs against double* outputs/buffers. Cast both to
            // void* to avoid -Wcompare-distinct-pointer-types warnings.
            let needs_void_cast = single_precision
                && matches!(op, BinOp::Eq | BinOp::NotEq)
                && is_pointer_var(left)
                && is_pointer_var(right);
            let l = render_expr(left, single_precision, registry, helpers);
            let r = render_expr(right, single_precision, registry, helpers);
            if needs_void_cast {
                format!("((void *){}{}(void *){})", l, op_str, r)
            } else {
                format!("({}{}{})", l, op_str, r)
            }
        }
        Expr::Cast(var_type, inner) => {
            let c_type = match var_type {
                VarType::Real => "double",
                VarType::Integer | VarType::Index => "int",
                VarType::RetCodeType => "TA_RetCode",
                VarType::RealPointer => "double *",
                VarType::IntPointer => "int *",
                VarType::RealArray(_) | VarType::IntArray(_) => "/* array cast */",
            };
            format!(
                "(({}){})",
                c_type,
                render_expr(inner, single_precision, registry, helpers)
            )
        }
        Expr::Not(inner) => {
            format!(
                "!({})",
                render_expr(inner, single_precision, registry, helpers)
            )
        }
        Expr::FuncCall(name, args) => {
            render_func_call(name, args, single_precision, registry, helpers)
        }
        Expr::PointerDeref(name) => format!("*{name}"),
        Expr::AddressOf(inner) => {
            format!(
                "&{}",
                render_expr(inner, single_precision, registry, helpers)
            )
        }
        Expr::PostIncrement(inner) => {
            format!(
                "{}++",
                render_expr(inner, single_precision, registry, helpers)
            )
        }
        Expr::PostDecrement(inner) => {
            format!(
                "{}--",
                render_expr(inner, single_precision, registry, helpers)
            )
        }
        Expr::PreIncrement(inner) => {
            format!(
                "++{}",
                render_expr(inner, single_precision, registry, helpers)
            )
        }
        Expr::PreDecrement(inner) => {
            format!(
                "--{}",
                render_expr(inner, single_precision, registry, helpers)
            )
        }
        Expr::Ternary(cond, then_expr, else_expr) => {
            format!(
                "(({}) ? ({}) : ({}))",
                render_expr(cond, single_precision, registry, helpers),
                render_expr(then_expr, single_precision, registry, helpers),
                render_expr(else_expr, single_precision, registry, helpers)
            )
        }
    }
}

/// Convert a function identifier to `PascalCase`.
/// e.g., "RSI" -> "Rsi", "SMA" -> "Sma"
fn to_pascal_case(s: &str) -> String {
    let lower = s.to_lowercase();
    let mut chars = lower.chars();
    match chars.next() {
        None => String::new(),
        Some(c) => c.to_uppercase().collect::<String>() + chars.as_str(),
    }
}

/// Check if an expression is a variable that likely holds a pointer (array param
/// or buffer). Used to detect pointer aliasing comparisons that need void* casts.
/// Must NOT match integer variables like outBegIdx, outNBElement, tempInteger.
fn is_pointer_var(expr: &Expr) -> bool {
    match expr {
        Expr::Var(name) => {
            // Input array params: inReal, inHigh, inLow, inClose, inVolume, inOpenInterest
            let is_input = name.starts_with("in")
                && name.len() > 2
                && name.as_bytes()[2].is_ascii_uppercase();
            // Output array params: outReal*, outSlowK, outFastK, outMACDSignal, etc.
            // Exclude integer outputs: outBegIdx, outNBElement, outIdx, outNbElement
            let is_output = name.starts_with("out")
                && !name.contains("BegIdx")
                && !name.contains("NbElement")
                && !name.contains("NBElement")
                && !name.contains("Idx")
                && name != "outIdx";
            // Temp buffers: tempBuffer*, firstEMA, secondEMA, fastEMABuffer, etc.
            // Exclude: tempInteger, tempReal (scalars)
            let is_buffer = name.starts_with("tempBuffer")
                || name.starts_with("firstEMA")
                || name.starts_with("secondEMA")
                || name.starts_with("thirdEMA")
                || name.starts_with("fastEMA")
                || name.starts_with("fastMA")
                || name.starts_with("slowEMA")
                || name.starts_with("slowMA")
                || name.starts_with("tempRSI");
            is_input || is_output || is_buffer
        }
        _ => false,
    }
}

/// Check if any argument in a cross-indicator call references a function input
/// parameter (inReal, inHigh, inClose, etc.). Input params are float* in single-
/// precision context, while intermediate buffers (tempBuffer, firstEMA, etc.) are
/// always double*. This determines whether to use TA_S_ or TA_ in SP variants.
fn args_have_input_param(args: &[Expr]) -> bool {
    args.iter().any(|arg| match arg {
        Expr::Var(name) => {
            // Input parameters follow TA-Lib naming: in + uppercase letter
            // e.g., inReal, inHigh, inLow, inClose, inVolume, inOpenInterest, inReal0
            name.starts_with("in")
                && name.len() > 2
                && name.as_bytes()[2].is_ascii_uppercase()
        }
        _ => false,
    })
}

/// Check if an expression directly contains a candle macro function call,
/// WITHOUT recursing through `&&`/`||` operators. This ensures we only split
/// `if(A && B)` when both A and B themselves contain expensive candle calls,
/// not when they're part of a longer `&&` chain where only some parts are expensive.
fn expr_directly_contains_candle_call(expr: &Expr) -> bool {
    match expr {
        Expr::FuncCall(name, args) => {
            C_CANDLE_MACRO_FNS.contains(&name.as_str())
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

/// Try to render a candle helper function call as a C preprocessor macro.
///
/// Converts `ta_candlerange(SET_rangeType, inOpen[idx], ...)` → `TA_CANDLERANGE(SET, idx)`
/// and `ta_candleaverage(SET_rangeType, ..., sum, inOpen[idx], ...)` → `TA_CANDLEAVERAGE(SET, sum, idx)`.
///
/// Returns `None` if the function isn't a candle helper or args don't match the expected pattern.
fn try_render_candle_macro(
    fname: &str,
    args: &[Expr],
    single_precision: bool,
    registry: &Registry,
    helpers: &HelperRegistry,
) -> Option<String> {
    match fname {
        "ta_candlerange" if args.len() == 5 => {
            // ta_candlerange(SET_rangeType, inOpen[idx], inHigh[idx], inLow[idx], inClose[idx])
            let setting = extract_candle_setting_name(&args[0])?;
            let idx = extract_array_index(&args[4])?;
            let idx_str = render_expr(&idx, single_precision, registry, helpers);
            Some(format!("TA_CANDLERANGE({setting},{idx_str})"))
        }
        "ta_candleaverage" if args.len() == 8 => {
            // ta_candleaverage(SET_rangeType, SET_avgPeriod, SET_factor, sum,
            //                  inOpen[idx], inHigh[idx], inLow[idx], inClose[idx])
            let setting = extract_candle_setting_name(&args[0])?;
            let sum_str = render_expr(&args[3], single_precision, registry, helpers);
            let idx = extract_array_index(&args[7])?;
            let idx_str = render_expr(&idx, single_precision, registry, helpers);
            Some(format!("TA_CANDLEAVERAGE({setting},{sum_str},{idx_str})"))
        }
        _ => None,
    }
}

/// Extract the candle setting name from a `_rangeType` variable reference.
/// `Expr::Var("ShadowVeryShort_rangeType")` → `Some("ShadowVeryShort")`
fn extract_candle_setting_name(expr: &Expr) -> Option<String> {
    if let Expr::Var(name) = expr {
        name.strip_suffix("_rangeType").map(String::from)
    } else {
        None
    }
}

/// Extract the index expression from an array access node.
/// `Expr::ArrayAccess("inClose", idx_expr)` → `Some(idx_expr)`
fn extract_array_index(expr: &Expr) -> Option<Expr> {
    if let Expr::ArrayAccess(_, idx) = expr {
        Some(*idx.clone())
    } else {
        None
    }
}

/// Render a `FuncCall` expression to C code.
fn render_func_call(
    fname: &str,
    args: &[Expr],
    single_precision: bool,
    registry: &Registry,
    helpers: &HelperRegistry,
) -> String {
    // C candle macros: emit preprocessor macro calls instead of expanded code.
    // This enables compiler loop-unswitching, matching the reference library.
    if let Some(macro_call) = try_render_candle_macro(fname, args, single_precision, registry, helpers) {
        return macro_call;
    }

    // Check if this is a call to a helper function that can be inlined
    if let Some(helper) = helpers.get(fname) {
        if let Some(inlined_expr) = try_inline_expr(helper, args) {
            return render_expr(&inlined_expr, single_precision, registry, helpers);
        }
        // Multi-statement helpers: Task 10 will handle
    }

    if fname == "UNSTABLE_PERIOD" {
        // UNSTABLE_PERIOD(RSI) -> TA_GLOBALS_UNSTABLE_PERIOD(TA_FUNC_UNST_RSI,Rsi)
        // UNSTABLE_PERIOD(FUNC_UNST_ATR) -> strip FUNC_UNST_ prefix first
        if let Some(Expr::Var(func_name)) = args.first() {
            let base = func_name
                .strip_prefix("FUNC_UNST_")
                .unwrap_or(func_name);
            let upper = base.to_uppercase();
            let pascal = to_pascal_case(base);
            return format!("TA_GLOBALS_UNSTABLE_PERIOD(TA_FUNC_UNST_{upper},{pascal})");
        }
        "TA_GLOBALS_UNSTABLE_PERIOD(0,0)".to_string()
    } else if fname == "COMPATIBILITY" {
        // COMPATIBILITY() -> TA_GLOBALS_COMPATIBILITY
        "TA_GLOBALS_COMPATIBILITY".to_string()
    } else if fname == "IS_ZERO" {
        // IS_ZERO(x) -> TA_IS_ZERO(x)
        if let Some(arg) = args.first() {
            let x = render_expr(arg, single_precision, registry, helpers);
            return format!("TA_IS_ZERO({x})");
        }
        "TA_IS_ZERO(0)".to_string()
    } else if fname == "IS_ZERO_OR_NEG" {
        // IS_ZERO_OR_NEG(x) -> TA_IS_ZERO_OR_NEG(x)
        if let Some(arg) = args.first() {
            let x = render_expr(arg, single_precision, registry, helpers);
            return format!("TA_IS_ZERO_OR_NEG({x})");
        }
        "TA_IS_ZERO_OR_NEG(0)".to_string()
    } else if fname == "ARRAY_COPY" {
        // ARRAY_COPY(dst, dstOff, src, srcOff, count)
        if args.len() == 5 {
            let rendered: Vec<String> = args
                .iter()
                .map(|a| render_expr(a, single_precision, registry, helpers))
                .collect();
            let macro_name = if single_precision {
                "ARRAY_MEMMOVEMIX"
            } else {
                "ARRAY_MEMMOVE"
            };
            return format!(
                "{}({},{},{},{},{})",
                macro_name, rendered[0], rendered[1], rendered[2], rendered[3], rendered[4]
            );
        }
        "/* ARRAY_COPY: bad args */".to_string()
    } else if fname == "PER_TO_K" {
        // PER_TO_K(period) -> (2.0 / ((double)(period) + 1.0))
        if let Some(arg) = args.first() {
            let x = render_expr(arg, single_precision, registry, helpers);
            return format!("(2.0 / ((double)({x}) + 1.0))");
        }
        "0.0".to_string()
    } else if MATH_FUNCTIONS.contains(&fname) {
        // Plain C math functions — remap names where needed, then emit as C function calls.
        // max → fmax, min → fmin, ABS → fabs (all from <math.h>)
        let c_name = match fname {
            "max" => "fmax",
            "min" => "fmin",
            "ABS" => "fabs",
            other => other,
        };
        let rendered: Vec<String> = args
            .iter()
            .map(|a| render_expr(a, single_precision, registry, helpers))
            .collect();
        format!("{}({})", c_name, rendered.join(","))
    } else if STDLIB_FUNCTIONS.contains(&fname) {
        // C stdlib functions — pass through as-is without TA_ prefix
        let rendered: Vec<String> = args
            .iter()
            .map(|a| render_expr(a, single_precision, registry, helpers))
            .collect();
        format!("{}({})", fname, rendered.join(","))
    } else {
        // Try cross-call resolution through the registry
        let resolved = registry.resolve_call(fname, Lang::C);
        let rendered: Vec<String> = args
            .iter()
            .map(|a| render_expr(a, single_precision, registry, helpers))
            .collect();
        if resolved != fname {
            // Registry resolved it (e.g. sma_lookback -> TA_SMA_Lookback, sma -> TA_SMA)
            // For single-precision variants, use TA_S_ ONLY when passing user input
            // arrays (float* in SP context). Intermediate buffers are always double*,
            // so calls with those must use the double-precision TA_ variant.
            // Matches the reference code's FUNCTION_CALL vs FUNCTION_CALL_DOUBLE distinction.
            let use_sp = single_precision
                && resolved.starts_with("TA_")
                && !resolved.starts_with("TA_S_")
                && !resolved.contains("_Lookback")
                && args_have_input_param(args);
            let final_name = if use_sp {
                format!("TA_S_{}", &resolved[3..])
            } else {
                resolved
            };
            format!("{}({})", final_name, rendered.join(","))
        } else if fname.ends_with("_Lookback") {
            // Legacy: RSI_Lookback(args...) -> TA_RSI_Lookback(args...)
            format!("TA_{}({})", fname, rendered.join(","))
        } else {
            // General TA function call: SMA(...) -> TA_SMA(...) or TA_S_SMA(...) for single precision
            // Same rule: only use TA_S_ when passing user input arrays.
            let use_sp = single_precision && args_have_input_param(args);
            let prefix = if use_sp { "TA_S_" } else { "TA_" };
            format!("{}{}({})", prefix, fname, rendered.join(","))
        }
    }
}

/// Render a complex lookback body (`LookbackExpr::Code`) into C code.
fn render_lookback_code(
    stmts: &[Statement],
    enums: &HashMap<String, EnumDef>,
    registry: &Registry,
    helpers: &HelperRegistry,
) -> String {
    let mut out = String::new();
    let inline_counter = Cell::new(0);

    // Declare local variables (deduplicated)
    let mut declared_vars: Vec<String> = Vec::new();
    for stmt in stmts {
        emit_c_var_decls(stmt, &mut out, &mut declared_vars);
    }

    // Emit candle settings unpacking for lookback body
    let candle_used = detect_candle_settings(stmts);
    if !candle_used.is_empty() {
        out.push_str(&emit_c_unpacking(&candle_used, 3));
    }

    // Emit VarDecl initializations (including those inside Block multi-var declarations)
    for stmt in stmts {
        let var_decls: Vec<&Statement> = match stmt {
            Statement::VarDecl { .. } => vec![stmt],
            Statement::Block { body } => body.iter().collect(),
            _ => vec![],
        };
        for s in var_decls {
            if let Statement::VarDecl {
                name,
                init: Some(init),
                ..
            } = s
            {
                out.push_str(&format!(
                    "   {} = {};\n",
                    name,
                    render_expr(init, false, registry, helpers)
                ));
            }
        }
    }

    // Render non-VarDecl statements (skip Blocks that only contain VarDecls)
    for stmt in stmts {
        if matches!(stmt, Statement::VarDecl { .. }) {
            continue;
        }
        if let Statement::Block { body } = stmt {
            if body.iter().all(|s| matches!(s, Statement::VarDecl { .. })) {
                continue;
            }
        }
        out.push_str(&render_statement(
            stmt, 3, false, enums, registry, helpers, &inline_counter,
        ));
    }

    out
}

/// Math functions from `<math.h>` supported in C.
/// `max`/`min` map to `fmax`/`fmin`; `ABS` maps to `fabs`.
const MATH_FUNCTIONS: &[&str] = &[
    "atan", "sqrt", "fabs", "floor", "ceil", "log", "cos", "sin", "tan", "acos", "asin", "exp",
    "cosh", "sinh", "tanh", "log10", "max", "min", "fmax", "fmin", "ABS",
];

/// C standard library functions and macros that should pass through as-is (no `TA_` prefix).
const STDLIB_FUNCTIONS: &[&str] = &[
    "free", "malloc", "memcpy", "memmove", "memset", "sizeof", "ARRAY_ALLOC",
];

/// Collect variable names whose first VarDecl occurrence has an initializer.
/// These are eligible for hoisted initialization. Names that first appear without init
/// are NOT included (their init comes from a later duplicate and should be emitted inline).
fn collect_first_seen_var_names(stmts: &[Statement], first_seen: &mut Vec<String>) {
    let mut all_seen: Vec<String> = Vec::new();
    for stmt in stmts {
        match stmt {
            Statement::VarDecl { name, init, .. } => {
                if !all_seen.contains(name) {
                    all_seen.push(name.clone());
                    if init.is_some() {
                        first_seen.push(name.clone());
                    }
                }
            }
            Statement::Block { body } => {
                for s in body {
                    if let Statement::VarDecl { name, init, .. } = s {
                        if !all_seen.contains(name) {
                            all_seen.push(name.clone());
                            if init.is_some() {
                                first_seen.push(name.clone());
                            }
                        }
                    }
                }
            }
            _ => {}
        }
    }
}

/// Recursively emit C variable declarations from a statement, deduplicating by name.
/// Walks into `Block` statements to handle multi-var declarations like `int i, j, k;`.
fn emit_c_var_decls(stmt: &Statement, out: &mut String, declared: &mut Vec<String>) {
    match stmt {
        Statement::VarDecl { var_type, name, .. } => {
            if declared.contains(name) {
                return;
            }
            declared.push(name.clone());
            let c_decl = match var_type {
                VarType::Real => format!("double {name}"),
                VarType::Integer | VarType::Index => format!("int {name}"),
                VarType::RetCodeType => format!("TA_RetCode {name}"),
                VarType::RealPointer => format!("double *{name}"),
                VarType::IntPointer => format!("int *{name}"),
                VarType::RealArray(size) => format!("double {name}[{size}]"),
                VarType::IntArray(size) => format!("int {name}[{size}]"),
            };
            out.push_str(&format!("   {c_decl};\n"));
        }
        Statement::Block { body } => {
            for s in body {
                emit_c_var_decls(s, out, declared);
            }
        }
        _ => {}
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use crate::ir;
    use crate::parser;
    use std::path::Path;

    /// Helper to load a FuncDef from the ta_func_defs directory.
    fn load_func(name: &str) -> (FuncDef, HashMap<String, EnumDef>) {
        let base = Path::new(env!("CARGO_MANIFEST_DIR")).join("../../ta_func_defs");
        let dir = base.join(name);
        let yaml_path = dir.join(format!("{name}.yaml"));
        let c_path = dir.join(format!("{name}.c"));

        let enums_path = base.join("enums.yaml");
        let enums = if enums_path.exists() {
            parser::enums::load_enums(&enums_path)
        } else {
            HashMap::new()
        };

        let mut func_def = parser::yaml::parse_yaml(&yaml_path);
        let parsed = parser::c_source::parse_c_source(&c_path);
        func_def.body = parsed.functions.first().unwrap().body.clone();
        func_def.lookback = Some(ir::LookbackExpr::Code(parsed.lookback_body));

        (func_def, enums)
    }

    fn make_registry() -> Registry {
        let base = Path::new(env!("CARGO_MANIFEST_DIR")).join("../../ta_func_defs");
        Registry::from_dir(&base)
    }

    #[test]
    fn test_c_generates_all_variants() {
        let (func, enums) = load_func("sma");
        let registry = make_registry();
        let output = generate(&func, &enums, &registry, &HelperRegistry::empty());

        assert!(output.contains("TA_SMA_Lookback"), "Missing lookback");
        assert!(output.contains("TA_SMA("), "Missing guarded function");
        assert!(output.contains("TA_SMA_Unguarded("), "Missing logic function");
        // TA_INT_* macros are no longer generated
        assert!(!output.contains("TA_INT_SMA"), "Should not have TA_INT_ alias");
        assert!(output.contains("TA_S_SMA("), "Missing single-precision");
        assert!(
            output.contains("TA_S_SMA_Unguarded("),
            "Missing single-precision logic"
        );
    }

    #[test]
    fn test_c_logic_omits_range_checks() {
        let (func, enums) = load_func("sma");
        let registry = make_registry();
        let output = generate(&func, &enums, &registry, &HelperRegistry::empty());

        // Find the Logic function and verify it doesn't have range checks
        let logic_start = output
            .find("TA_SMA_Unguarded(")
            .expect("Missing logic function");
        let guarded_start = output
            .find("TA_LIB_API TA_RetCode TA_SMA(")
            .expect("Missing guarded function");

        // Extract each function body (up to next function or end)
        let logic_body = &output[logic_start..];
        let guarded_body = &output[guarded_start..logic_start];

        // Guarded function should have range checks
        assert!(
            guarded_body.contains("TA_OUT_OF_RANGE_START_INDEX"),
            "Guarded should have start index check"
        );
        assert!(
            guarded_body.contains("TA_OUT_OF_RANGE_END_INDEX"),
            "Guarded should have end index check"
        );

        // Logic function should NOT have range checks
        // Find end of the logic function body (before the next function)
        let logic_end = logic_body
            .find("TA_LIB_API")
            .or_else(|| logic_body.find("TA_RetCode TA_S_"))
            .unwrap_or(logic_body.len());
        let logic_section = &logic_body[..logic_end];
        assert!(
            !logic_section.contains("TA_OUT_OF_RANGE_START_INDEX"),
            "Logic should not have start index check"
        );
        assert!(
            !logic_section.contains("TA_OUT_OF_RANGE_END_INDEX"),
            "Logic should not have end index check"
        );
    }

    #[test]
    fn test_c_no_int_alias() {
        // Verify TA_INT_* macros are no longer generated
        let (func, enums) = load_func("sma");
        let registry = make_registry();
        let output = generate(&func, &enums, &registry, &HelperRegistry::empty());

        assert!(
            !output.contains("#define TA_INT_"),
            "Should not generate TA_INT_ macros"
        );
        assert!(
            !output.contains("#define TA_S_INT_"),
            "Should not generate TA_S_INT_ macros"
        );
    }

    #[test]
    fn test_c_resolves_cross_calls() {
        let (func, enums) = load_func("ma");
        let registry = make_registry();
        let output = generate(&func, &enums, &registry, &HelperRegistry::empty());

        // The MA body has calls like sma_lookback() and ema_lookback()
        // In generated C, these should become TA_SMA_Lookback() and TA_EMA_Lookback()
        assert!(
            output.contains("TA_SMA_Lookback("),
            "sma_lookback should resolve to TA_SMA_Lookback"
        );
        assert!(
            output.contains("TA_EMA_Lookback("),
            "ema_lookback should resolve to TA_EMA_Lookback"
        );

        // bare sma and ema calls resolve to Unguarded (cross-indicator = skip validation)
        assert!(
            output.contains("TA_SMA_Unguarded("),
            "sma should resolve to TA_SMA_Unguarded"
        );
        assert!(
            output.contains("TA_EMA_Unguarded("),
            "ema should resolve to TA_EMA_Unguarded"
        );
    }
}
