# Java Server Compilation Fixes — Implementation Plan

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Fix ~30K Java server compilation errors by cleaning up the parser IR and Java backend rendering.

**Architecture:** Parser emits cleaner IR (flattened VarDecl, deduped declarations) → Java backend handles language-specific rendering (null-check elimination, MInteger wrapping, ALLOC_ERR mapping) → server template adds missing type definitions (MAType enum, field cleanup).

**Tech Stack:** Rust (ta_codegen), Java (generated server)

**Spec:** `docs/superpowers/specs/2026-03-14-java-server-compilation-fixes-design.md`

---

## File Structure

| File | Changes | Purpose |
|------|---------|---------|
| `tools/ta_codegen/src/parser/c_source.rs` | Modify | Fix 1-2: VarDecl flattening + dedup |
| `tools/ta_codegen/src/backends/java.rs` | Modify | Fix 3-6: null-check elimination, ALLOC_ERR, MInteger, duplicate return |
| `tools/ta_codegen/src/backends/rust_lang.rs` | Modify | Fix 3, 4, 6 parity (same VarDecl loop bug too) |
| `tools/ta_codegen/src/server_gen.rs` | Modify | Fix 7-8: MAType enum, class field cleanup |

---

## Chunk 1: Parser Fixes

### Task 1: Flatten Multi-Var VarDecl in Parser

The parser currently wraps multi-variable declarations like `int i, j, k;` into `Statement::Block { body: [VarDecl, VarDecl, ...] }`. The java.rs and rust_lang.rs backends only iterate direct `VarDecl` entries from `func.body`, so nested ones are missed. Fix the parser to emit individual `VarDecl` entries directly.

**Files:**
- Modify: `tools/ta_codegen/src/parser/c_source.rs:619-654` (parse_statements, parse_statement)
- Modify: `tools/ta_codegen/src/parser/c_source.rs:919-953` (parse_var_decl)

- [ ] **Step 1: Change `parse_var_decl()` to return `Vec<Statement>`**

In `c_source.rs`, change the method signature and body:

```rust
fn parse_var_decl(&mut self) -> Vec<Statement> {
    let type_tok = self.advance();
    let var_type = match type_tok {
        Token::Ident(ref s) => Self::type_from_keyword(s),
        _ => panic!("Expected type keyword"),
    };

    // Consume optional pointer declarator: `double *buf` or `int *ptr`
    let var_type = if self.peek() == Some(&Token::Star) {
        self.advance();
        match var_type {
            VarType::Real => VarType::RealPointer,
            VarType::Integer => VarType::IntPointer,
            other => other,
        }
    } else {
        var_type
    };

    let first = self.parse_single_var_decl(var_type.clone());

    // Check for multi-variable declarations: int x, y, z;
    if self.peek() == Some(&Token::Comma) {
        let mut stmts = vec![first];
        while self.peek() == Some(&Token::Comma) {
            self.advance(); // consume ,
            stmts.push(self.parse_single_var_decl(var_type.clone()));
        }
        self.consume_semicolon();
        stmts
    } else {
        self.consume_semicolon();
        vec![first]
    }
}
```

- [ ] **Step 2: Update `parse_statements()` to handle `Vec<Statement>` from var decls**

In `parse_statements()` (~line 616), handle type keywords and `const` before the general `parse_statement()` call, using `extend()` instead of `push()`:

```rust
fn parse_statements(&mut self) -> Vec<Statement> {
    let mut stmts = Vec::new();
    while self.pos < self.tokens.len() {
        if self.peek() == Some(&Token::RBrace) {
            break;
        }
        // Skip bare semicolons
        if self.peek() == Some(&Token::Semicolon) {
            self.advance();
            continue;
        }
        // Handle var decls specially — they can return multiple statements
        match self.peek().cloned() {
            Some(Token::Ident(ref s)) if Self::is_type_keyword(s) => {
                stmts.extend(self.parse_var_decl());
            }
            Some(Token::Ident(ref s)) if s == "const" => {
                self.advance(); // consume `const`
                stmts.extend(self.parse_var_decl());
            }
            _ => {
                stmts.push(self.parse_statement());
            }
        }
    }
    stmts
}
```

- [ ] **Step 3: Remove type keyword and const branches from `parse_statement()`**

In `parse_statement()` (~line 632), remove these two match arms since they're now handled in `parse_statements()`:

```rust
// REMOVE these lines (~650-655):
// Some(Token::Ident(ref s)) if Self::is_type_keyword(s) => self.parse_var_decl(),
// Some(Token::Ident(ref s)) if s == "const" => {
//     self.advance();
//     self.parse_var_decl()
// }
```

**Important:** Check if `parse_var_decl()` is called from any OTHER location (e.g., inside for-loop init parsing or other contexts). If so, those call sites need updating too. Grep for `parse_var_decl` to find all call sites.

- [ ] **Step 4: Build and test**

```bash
cd tools/ta_codegen && cargo build 2>&1 | head -50
cd tools/ta_codegen && cargo test 2>&1 | tail -20
```

Expected: All existing tests pass. If any test relies on `Statement::Block` wrapping multi-var decls, update it.

- [ ] **Step 5: Verify flattening works — regenerate and check ACCBANDS**

```bash
cd tools/ta_codegen && cargo run -- generate-servers --backend=java
grep -n "int i\|int j\|int outputSize\|int bufferSize\|int lookbackTotal" ../ta_codegen_output/java/Core_ACCBANDS.java | head -20
```

Expected: Each variable should now have its own `int varname;` declaration line. No more missing variables.

- [ ] **Step 6: Verify C output unchanged**

```bash
cd tools/ta_codegen && cargo run -- generate-servers --backend=c
cd .. && git diff ta_codegen_output/c/
```

Expected: No changes to C output (parser fix should be transparent to C backend).

- [ ] **Step 7: Commit**

```bash
cd tools/ta_codegen && git add src/parser/c_source.rs
git commit -m "fix(parser): flatten multi-var VarDecl — emit individual declarations instead of Block wrapper"
```

---

### Task 2: Deduplicate VarDecl in Parser

The C source has patterns like `double *tempBuffer1;` (declaration) then `double *tempBuffer1 = malloc(...)` (re-declaration with init). The parser captures both as VarDecl, producing illegal duplicate declarations in Java. Fix: track declared names per-function, convert re-declarations to assignments.

**Files:**
- Modify: `tools/ta_codegen/src/parser/c_source.rs` (parse_statements, add declared_vars tracking)

- [ ] **Step 1: Add `HashSet<String>` tracking to `parse_statements()`**

Add a `declared_vars` set and check each VarDecl against it. If already declared, convert to `Statement::Assign`:

```rust
fn parse_statements(&mut self) -> Vec<Statement> {
    let mut stmts = Vec::new();
    let mut declared_vars: std::collections::HashSet<String> = std::collections::HashSet::new();
    while self.pos < self.tokens.len() {
        if self.peek() == Some(&Token::RBrace) {
            break;
        }
        if self.peek() == Some(&Token::Semicolon) {
            self.advance();
            continue;
        }
        match self.peek().cloned() {
            Some(Token::Ident(ref s)) if Self::is_type_keyword(s) => {
                let decls = self.parse_var_decl();
                for decl in decls {
                    if let Statement::VarDecl { ref name, ref init, .. } = decl {
                        if declared_vars.contains(name) {
                            // Re-declaration: convert to assignment if has init, else skip
                            if let Some(init_expr) = init {
                                stmts.push(Statement::Assign {
                                    target: Expr::Var(name.clone()),
                                    value: init_expr.clone(),
                                    compound: false,
                                });
                            }
                            // If no init, skip entirely (already declared)
                        } else {
                            declared_vars.insert(name.clone());
                            stmts.push(decl);
                        }
                    } else {
                        stmts.push(decl);
                    }
                }
            }
            Some(Token::Ident(ref s)) if s == "const" => {
                self.advance();
                let decls = self.parse_var_decl();
                for decl in decls {
                    if let Statement::VarDecl { ref name, ref init, .. } = decl {
                        if declared_vars.contains(name) {
                            if let Some(init_expr) = init {
                                stmts.push(Statement::Assign {
                                    target: Expr::Var(name.clone()),
                                    value: init_expr.clone(),
                                    compound: false,
                                });
                            }
                        } else {
                            declared_vars.insert(name.clone());
                            stmts.push(decl);
                        }
                    } else {
                        stmts.push(decl);
                    }
                }
            }
            _ => {
                stmts.push(self.parse_statement());
            }
        }
    }
    stmts
}
```

**Note:** The dedup logic for type keywords and const is identical — extract a helper function `dedup_var_decls(decls, declared_vars, stmts)` to avoid repetition.

- [ ] **Step 2: Build and test**

```bash
cd tools/ta_codegen && cargo build 2>&1 | head -50
cd tools/ta_codegen && cargo test 2>&1 | tail -20
```

- [ ] **Step 3: Verify dedup — check ACCBANDS for duplicate tempBuffer declarations**

```bash
cd tools/ta_codegen && cargo run -- generate-servers --backend=java
grep -c "double\[\] tempBuffer1" ../ta_codegen_output/java/Core_ACCBANDS.java
```

Expected: Count should be `4` (one per function variant: accbands, accbandsLogic, accbands float, accbandsLogic float). Previously was `8` (doubled by the duplicate declaration).

- [ ] **Step 4: Verify the re-declaration becomes an assignment**

```bash
grep -A2 "tempBuffer1" ../ta_codegen_output/java/Core_ACCBANDS.java | head -20
```

Expected: `double[] tempBuffer1;` (declaration), then later `tempBuffer1 = new double[...]` (assignment, no type prefix).

- [ ] **Step 5: Commit**

```bash
cd tools/ta_codegen && git add src/parser/c_source.rs
git commit -m "fix(parser): deduplicate VarDecl — convert re-declarations to assignments"
```

---

## Chunk 2: Java Backend Fixes

### Task 3: Eliminate Post-Allocation Null-Check Blocks

After stdlib mapping, `malloc` → `new double[...]` in Java. The subsequent `if(!ptr) { return ALLOC_ERR; }` blocks are dead code and invalid Java syntax (`!` on array). Skip entire if-blocks whose body returns ALLOC_ERR.

**Files:**
- Modify: `tools/ta_codegen/src/backends/java.rs:457-511` (Statement::If rendering)

- [ ] **Step 1: Add `contains_alloc_err_return` helper function**

Add this function near the top of `java.rs` (after the imports):

```rust
/// Check if a statement list contains a return with ALLOC_ERR value.
fn contains_alloc_err_return(stmts: &[Statement]) -> bool {
    stmts.iter().any(|s| matches!(s, Statement::Return { value: Some(Expr::Var(name)) } if name == "ALLOC_ERR"))
}
```

- [ ] **Step 2: Add early return in `Statement::If` rendering**

At the top of the `Statement::If` arm in `render_statement` (~line 457), before the existing format! call, add:

```rust
Statement::If {
    condition,
    then_body,
    else_body,
} => {
    // Skip post-allocation null-check blocks (dead code in Java — `new` never returns null)
    if contains_alloc_err_return(then_body) {
        return String::new();
    }
    // ... existing rendering code
```

- [ ] **Step 3: Build and verify**

```bash
cd tools/ta_codegen && cargo build
cargo run -- generate-servers --backend=java
grep -c "ALLOC_ERR" ../ta_codegen_output/java/Core_ACCBANDS.java
```

Expected: `0` — all ALLOC_ERR blocks eliminated.

- [ ] **Step 4: Verify null-check `if(!tempBuffer1)` blocks are gone**

```bash
grep "!(tempBuffer" ../ta_codegen_output/java/Core_ACCBANDS.java
```

Expected: No matches.

- [ ] **Step 5: Commit**

```bash
cd tools/ta_codegen && git add src/backends/java.rs
git commit -m "fix(java): eliminate post-allocation null-check blocks — dead code after stdlib mapping"
```

---

### Task 4: Add ALLOC_ERR Constant Mapping

Safety net: map the `ALLOC_ERR` IR constant to `RetCode.AllocErr` in java.rs for any references that survive null-check elimination (e.g., in else branches or other contexts).

**Files:**
- Modify: `tools/ta_codegen/src/backends/java.rs:714-720` (Expr::Var constant mapping)

- [ ] **Step 1: Add ALLOC_ERR and INTERNAL_ERROR to the Var mapping**

In `render_expr`, in the `Expr::Var(name)` match arm (~line 714), add the missing constants:

```rust
Expr::Var(name) => match name.as_str() {
    "COMPATIBILITY" => "this.compatibility".to_string(),
    "METASTOCK" => "Compatibility.Metastock".to_string(),
    "DEFAULT" => "Compatibility.Default".to_string(),
    "BAD_PARAM" => "RetCode.BadParam".to_string(),
    "SUCCESS" => "RetCode.Success".to_string(),
    "ALLOC_ERR" => "RetCode.AllocErr".to_string(),
    "INTERNAL_ERROR" => "RetCode.InternalError".to_string(),
    "OutOfRangeStartIndex" => "RetCode.OutOfRangeStartIndex".to_string(),
    "OutOfRangeEndIndex" => "RetCode.OutOfRangeEndIndex".to_string(),
    _ => name.clone(),
},
```

- [ ] **Step 2: Build and verify**

```bash
cd tools/ta_codegen && cargo build
cargo run -- generate-servers --backend=java
grep -rn "ALLOC_ERR\|INTERNAL_ERROR" ../ta_codegen_output/java/ | head -10
```

Expected: No raw `ALLOC_ERR` or `INTERNAL_ERROR` references — they should all be mapped to `RetCode.*`.

- [ ] **Step 3: Commit**

```bash
cd tools/ta_codegen && git add src/backends/java.rs
git commit -m "fix(java): map ALLOC_ERR and other missing constants to RetCode enum"
```

---

### Task 5: MInteger Wrapping for Cross-Indicator Output Params

When DEMA calls `ema(...)`, it passes `&firstEMABegIdx` as an output param. The IR has `AddressOf(Var("firstEMABegIdx"))`. Java needs `MInteger` wrappers for these variables.

**Files:**
- Modify: `tools/ta_codegen/src/backends/java.rs:11-16` (add imports)
- Modify: `tools/ta_codegen/src/backends/java.rs:145-159` (VarDecl declaration loop)
- Modify: `tools/ta_codegen/src/backends/java.rs:714-720` (Expr::Var rendering)
- Modify: `tools/ta_codegen/src/backends/java.rs:773-776` (Expr::AddressOf rendering)

- [ ] **Step 1: Add `collect_address_of_vars` helper function**

Add this function near the top of java.rs:

```rust
use std::collections::HashSet;

/// Collect variable names that appear in AddressOf(Var(name)) contexts.
/// These variables need to be declared as MInteger instead of int in Java.
fn collect_address_of_vars(stmts: &[Statement]) -> HashSet<String> {
    let mut vars = HashSet::new();
    collect_address_of_vars_recursive(stmts, &mut vars);
    vars
}

fn collect_address_of_vars_recursive(stmts: &[Statement], vars: &mut HashSet<String>) {
    for stmt in stmts {
        collect_address_of_exprs(stmt, vars);
    }
}

fn collect_address_of_exprs(stmt: &Statement, vars: &mut HashSet<String>) {
    match stmt {
        Statement::Assign { target, value, .. } => {
            scan_expr_for_address_of(target, vars);
            scan_expr_for_address_of(value, vars);
        }
        Statement::If { condition, then_body, else_body } => {
            scan_expr_for_address_of(condition, vars);
            collect_address_of_vars_recursive(then_body, vars);
            collect_address_of_vars_recursive(else_body, vars);
        }
        Statement::While { condition, body } | Statement::DoWhile { condition, body } => {
            scan_expr_for_address_of(condition, vars);
            collect_address_of_vars_recursive(body, vars);
        }
        Statement::ForC { init, condition, update, body } => {
            collect_address_of_exprs(init, vars);
            scan_expr_for_address_of(condition, vars);
            collect_address_of_exprs(update, vars);
            collect_address_of_vars_recursive(body, vars);
        }
        Statement::Return { value: Some(expr) } => {
            scan_expr_for_address_of(expr, vars);
        }
        Statement::Block { body } => {
            collect_address_of_vars_recursive(body, vars);
        }
        _ => {}
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
        Expr::Not(inner) | Expr::Cast(_, inner) | Expr::PostIncrement(inner) => {
            scan_expr_for_address_of(inner, vars);
        }
        Expr::ArrayAccess(_, idx) => {
            scan_expr_for_address_of(idx, vars);
        }
        _ => {}
    }
}
```

- [ ] **Step 2: Pass `address_of_vars` into `gen_func` and modify VarDecl loop**

In `gen_func()` (~line 70), compute the set before the VarDecl loop:

```rust
let address_of_vars = collect_address_of_vars(&func.body);
```

Then modify the VarDecl declaration loop (~line 146):

```rust
for stmt in &func.body {
    if let Statement::VarDecl { var_type, name, .. } = stmt {
        let java_decl = if address_of_vars.contains(name) && matches!(var_type, VarType::Integer | VarType::Index) {
            format!("MInteger {name} = new MInteger()")
        } else {
            match var_type {
                VarType::Real => format!("double {name}"),
                VarType::Integer | VarType::Index => format!("int {name}"),
                VarType::RetCodeType => format!("RetCode {name}"),
                VarType::RealPointer => format!("double[] {name}"),
                VarType::IntPointer => format!("int[] {name}"),
                VarType::RealArray(size) => format!("double[] {name} = new double[{size}]"),
                VarType::IntArray(size) => format!("int[] {name} = new int[{size}]"),
            }
        };
        out.push_str(&format!("      {java_decl};\n"));
    }
}
```

- [ ] **Step 3: Thread `address_of_vars` through render functions and add `.value` access**

Add `address_of_vars: &HashSet<String>` as a parameter to these functions (update all call sites accordingly):
- `render_expr()` — 4 args → 5 args
- `render_statement()` — 7 args → 8 args
- `render_func_call()` — 5 args → 6 args
- `render_return_expr()` — 4 args → 5 args

**Call sites to update** (grep for each function name to find all):
- `render_expr` is called from: `render_statement` (multiple places), `render_func_call`, `render_return_expr`, `gen_func` (VarDecl init rendering ~line 206), `render_assign_target`
- `render_statement` is called from: `gen_func` body loop (~line 216), and recursively from itself (If/While/For arms)
- `render_func_call` is called from: `render_expr` Expr::FuncCall arm
- `render_return_expr` is called from: `render_statement` Statement::Return arm

In `render_expr`, update the `Expr::Var` arm:

```rust
Expr::Var(name) => {
    let mapped = match name.as_str() {
        "COMPATIBILITY" => "this.compatibility".to_string(),
        // ... other constants ...
        _ => name.clone(),
    };
    // If this is an address-of variable (MInteger), access .value
    if address_of_vars.contains(name) {
        format!("{mapped}.value")
    } else {
        mapped
    }
},
```

**Critical: Fix the `AddressOf` handler to bypass `.value` logic.** The existing handler at ~line 773 renders `AddressOf(inner)` by calling `render_expr(inner, ...)`, which would now append `.value` to the inner Var. Change it to pass an empty set:

```rust
Expr::AddressOf(inner) => {
    // Java passes MInteger objects by reference — render bare name, NOT .value
    let empty = HashSet::new();
    render_expr(inner, single_precision, registry, helpers, &empty)
}
```

This ensures `AddressOf(Var("x"))` renders as `x` (the MInteger object) rather than `x.value`.

- [ ] **Step 4: Build and verify**

```bash
cd tools/ta_codegen && cargo build 2>&1 | head -50
cargo run -- generate-servers --backend=java
grep -n "MInteger\|firstEMABegIdx\|outBegIdxDummy" ../ta_codegen_output/java/Core_DEMA.java | head -20
```

Expected:
- `MInteger firstEMABegIdx = new MInteger();` (not `int firstEMABegIdx;`)
- `firstEMABegIdx.value` when used in arithmetic
- Just `firstEMABegIdx` when passed to `emaLogic(...)` as an argument

- [ ] **Step 5: Commit**

```bash
cd tools/ta_codegen && git add src/backends/java.rs
git commit -m "fix(java): wrap cross-indicator output params in MInteger — pre-scan AddressOf patterns"
```

---

### Task 6: Eliminate Duplicate Return Statements

The duplicate return comes from TWO sources: the C source body ends with `return SUCCESS;` (rendered from IR), AND `gen_func()` has a hardcoded `return RetCode.Success;` at line 228. Remove the hardcoded return — the body's own return is sufficient.

**Files:**
- Modify: `tools/ta_codegen/src/backends/java.rs:227-229` (gen_func trailing return)

- [ ] **Step 1: Remove the hardcoded trailing return**

In `gen_func()` at ~line 227-229, change:

```rust
// BEFORE:
    // Assign output scalars and return
    out.push_str("      return RetCode.Success ;\n");
    out.push_str("   }\n");

// AFTER:
    out.push_str("   }\n");
```

The body already contains `return RetCode.Success;` from the IR — the hardcoded one creates the duplicate.

- [ ] **Step 2: Build and verify**

```bash
cd tools/ta_codegen && cargo build
cargo run -- generate-servers --backend=java
grep -c "return RetCode.Success" ../ta_codegen_output/java/Core_ACCBANDS.java
```

Expected: Count should be halved compared to before (one per function variant, not two).

- [ ] **Step 3: Commit**

```bash
cd tools/ta_codegen && git add src/backends/java.rs
git commit -m "fix(java): remove hardcoded duplicate return — body IR already has return statement"
```

---

## Chunk 3: Server Template Fixes

### Task 7: Add MAType Enum to Server Template

Core_MA.java references `TA_MAType_SMA` etc. in switch statements. The MAType enum needs to be defined in the server template, and the switch labels need to resolve via `lookup_variant()`.

**Files:**
- Modify: `tools/ta_codegen/src/server_gen.rs:782-783` (after Compatibility enum)
- Modify: `tools/ta_codegen/src/backends/java.rs:641-646` (switch label rendering — verify `lookup_variant` handles MAType)

- [ ] **Step 1: Add MAType enum to server template**

In `generate_java_server()` in `server_gen.rs`, after the Compatibility enum (~line 782), add:

```rust
s.push_str("enum MAType {\n");
s.push_str("    Sma, Ema, Wma, Dema, Tema, Trima, Kama, Mama, T3;\n");
s.push_str("}\n\n");
```

- [ ] **Step 2: Verify `lookup_variant` handles MAType**

Check if the enum parsing/registry already knows about MAType. If `render_java_switch_label` at java.rs:641 can resolve `TA_MAType_SMA` → `MAType.Sma` via `lookup_variant()`, no further changes needed. If not, add a hardcoded fallback mapping in `render_java_switch_label`:

```rust
fn render_java_switch_label(label: &str, enums: &HashMap<String, EnumDef>) -> String {
    if let Some((enum_name, variant)) = lookup_variant(label, enums) {
        format!("{}.{}", enum_name, variant.pascal_name)
    } else if label.starts_with("TA_MAType_") {
        // Fallback for MAType enum variants
        let variant = label.strip_prefix("TA_MAType_").unwrap();
        let pascal = variant.chars().next().unwrap().to_uppercase().to_string()
            + &variant[1..].to_lowercase();
        format!("MAType.{pascal}")
    } else {
        label.to_string()
    }
}
```

- [ ] **Step 3: Build and verify**

```bash
cd tools/ta_codegen && cargo build
cargo run -- generate-servers --backend=java
grep -n "MAType\|TA_MAType" ../ta_codegen_output/java/Core_MA.java | head -20
```

Expected: Switch cases use `MAType.Sma`, `MAType.Ema`, etc. — not `TA_MAType_SMA`.

- [ ] **Step 4: Commit**

```bash
cd tools/ta_codegen && git add src/server_gen.rs src/backends/java.rs
git commit -m "fix(java): add MAType enum to server template and resolve switch labels"
```

---

### Task 8: Clean Up Class-Level Fields in Server Template

After Task 1 (VarDecl flattening), all variables are declared locally in each method. Remove the class-level field declarations that are now redundant.

**Files:**
- Modify: `tools/ta_codegen/src/server_gen.rs:786-789` (Core class fields)

- [ ] **Step 1: Verify local declarations exist before removing class fields**

```bash
cd tools/ta_codegen && cargo run -- generate-servers --backend=java
grep -n "int lookbackTotal\|int i;" ../ta_codegen_output/java/Core_ACCBANDS.java | head -10
grep -n "int outIdx" ../ta_codegen_output/java/Core_DEMA.java | head -5
```

Expected: Each variable appears as a local declaration in each method. If any are MISSING, do NOT proceed — fall back to keeping those as class fields.

- [ ] **Step 2: Remove redundant class fields**

In `server_gen.rs` (~line 786), change:

```rust
// BEFORE:
s.push_str("    int lookbackTotal, i, outIdx, trailingIdx;\n");
s.push_str("    int[] unstablePeriod = new int[FuncUnstId.values().length];\n");
s.push_str("    Compatibility compatibility = Compatibility.Default;\n");
s.push_str("    int nbElement;\n\n");

// AFTER:
s.push_str("    int[] unstablePeriod = new int[FuncUnstId.values().length];\n");
s.push_str("    Compatibility compatibility = Compatibility.Default;\n\n");
```

Remove `lookbackTotal, i, outIdx, trailingIdx` and `nbElement` — these are all declared locally.

- [ ] **Step 3: Build and verify**

```bash
cd tools/ta_codegen && cargo build
cargo run -- generate-servers --backend=java
```

Check that no variable shadowing warnings appear and the generated file looks clean.

- [ ] **Step 4: Commit**

```bash
cd tools/ta_codegen && git add src/server_gen.rs
git commit -m "fix(java): remove redundant class-level field declarations — variables are now method-local"
```

---

## Chunk 4: Rust Backend Parity + Final Verification

### Task 9: Apply Matching Fixes to Rust Backend

The Rust backend has the same VarDecl loop bug (only iterates direct entries, misses Block-wrapped ones). The parser fix (Task 1) resolves this. But null-check elimination, ALLOC_ERR mapping, and duplicate return fixes should also be applied.

**Files:**
- Modify: `tools/ta_codegen/src/backends/rust_lang.rs`

- [ ] **Step 1: Add null-check block elimination (same as Task 3)**

Find the `Statement::If` rendering in rust_lang.rs and add the same `contains_alloc_err_return` check. Import or duplicate the helper function (consider moving it to a shared `backends/common.rs` if both backends need it, or just inline it).

- [ ] **Step 2: Add ALLOC_ERR constant mapping**

Find the `Expr::Var` constant mapping in rust_lang.rs and add `"ALLOC_ERR"` mapping (Rust equivalent — likely `RetCode::AllocErr` or whatever the Rust enum uses).

- [ ] **Step 3: Add duplicate return elimination**

Same pattern as Task 6 — skip consecutive identical return statements during body rendering.

- [ ] **Step 4: Build and test**

```bash
cd tools/ta_codegen && cargo build
cd tools/ta_codegen && cargo test 2>&1 | tail -20
```

- [ ] **Step 5: Commit**

```bash
cd tools/ta_codegen && git add src/backends/rust_lang.rs
git commit -m "fix(rust): apply null-check elimination, ALLOC_ERR mapping, and duplicate return fixes"
```

---

### Task 10: Full Compilation Test

Regenerate the Java server, compile it, and verify zero compilation errors.

**Files:**
- No code changes — verification only

- [ ] **Step 1: Verify supporting type definitions (Spec Fix 9)**

Confirm that `server_gen.rs` already defines all required types. Check the generated `TaCodegenServe.java` for:

```bash
cd tools/ta_codegen && cargo run -- generate-servers --backend=java
grep -n "^enum\|^class MInteger" ../ta_codegen_output/java/TaCodegenServe.java
```

Expected: `RetCode`, `MInteger`, `FuncUnstId`, `Compatibility`, `MAType` (added by Task 7) all present. If any are missing, add them to `server_gen.rs` before proceeding.

- [ ] **Step 2: Regenerate Java server**

```bash
cd tools/ta_codegen && cargo run -- generate-servers --backend=java
```

- [ ] **Step 3: Build the Java server**

```bash
cd tools/ta_codegen && cargo run -- build --backend=java 2>&1 | tail -30
```

Or manually:

```bash
cd ta_codegen_output/java && javac TaCodegenServe.java 2>&1 | head -100
```

Expected: Zero compilation errors.

- [ ] **Step 4: If errors remain, triage and fix**

Count remaining errors:

```bash
cd ta_codegen_output/java && javac TaCodegenServe.java 2>&1 | grep -c "error:"
```

Common remaining issues to check:
- Any unmapped constants (grep for all-caps identifiers)
- Any remaining `!(arrayVar)` patterns
- Any `free()` calls that weren't eliminated (should be empty string from stdlib mapping)
- Any enum parameters that need type definitions

Fix each category in the appropriate layer (parser/backend/template) and iterate.

- [ ] **Step 5: Run C regression to verify no regressions**

```bash
cd bin && ./ta_regtest --codegen --language=c 2>&1 | tail -20
```

Expected: All tests pass, same as before these changes.

- [ ] **Step 6: Run Java regression (once compiling)**

```bash
cd bin && ./ta_regtest --codegen --language=java 2>&1 | tail -30
```

Expected: Server starts, indicators are called, results compared against C reference.

- [ ] **Step 7: Commit any remaining fixes**

```bash
git add -A
git commit -m "fix(java): resolve remaining compilation errors in Java codegen server"
```
