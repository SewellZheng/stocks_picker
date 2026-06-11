# Codegen Backend Fixes Implementation Plan

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make all ~158 TA-Lib indicators compile and pass regression tests across C, Java, SWIG, and Rust backends.

**Architecture:** Replace all cross-language macros in extracted `ta_func_defs/<name>/<name>.c` files with plain C. Fix backend rendering bugs (ForC loops, SWIG naming). Add forward declarations for cross-function calls. The parser and backends already handle plain C correctly — the 6 working base functions prove this.

**Tech Stack:** Rust (ta_codegen backends + parser), Python (macro replacement script), C (extracted source files), CMake (build system)

**Spec:** `docs/superpowers/specs/2026-03-13-codegen-backend-fixes-design.md`

---

## File Structure

### Files to create
- `scripts/replace_macros.py` — One-shot Python script that replaces all macros across ~158 extracted `.c` files

### Files to modify
- `tools/ta_codegen/src/backends/c.rs` — Fix ForC init/update Block rendering (comma-separate)
- `tools/ta_codegen/src/backends/java.rs` — Fix ForC init/update Block rendering (comma-separate)
- `tools/ta_codegen/src/backends/rust_lang.rs` — ForC → range iteration for single-counter `<=` patterns
- `tools/ta_codegen/src/backends/swig.rs` — Verify naming alignment (may already be correct)
- `tools/ta_codegen/src/server_gen.rs` — Add forward declarations to generated `ta_func.h` header
- `tools/ta_codegen/src/parser/c_source.rs` — Simplify: remove macro-specific parse_macro_decl handlers after source cleanup; add math function recognition
- `ta_func_defs/*/*.c` — ~158 files: replace macros with plain C

### Files to read (reference only)
- `tools/ta_codegen/tests/backend_suite.rs` — Test infrastructure for adding new tests
- `tools/ta_codegen/tests/integration_test.rs` — Integration test helpers
- `ta_func_defs/sma/sma.c` — Clean baseline (what a macro-free file looks like)
- `ta_func_defs/accbands/accbands.c` — Example with ARRAY_ALLOC/FREE, VALUE_HANDLE
- `ta_func_defs/ht_trendmode/ht_trendmode.c` — Example with CIRCBUF, HILBERT, local #define
- `ta_func_defs/bbands/bbands.c` — Example with cross-function call to unregistered helper
- `src/ta_func/ta_utility.h:191-224` — `std_*` math function macro definitions

---

## Chunk 1: Backend ForC Rendering Fixes

### Task 1: Fix C backend ForC init/update Block rendering

The C backend at `c.rs:457-489` calls `render_statement(init, 0, ...)` which, for a `Block([j=0, i=start])`, renders as `j = 0;\ni = start;\n`. After trimming the trailing semicolon, the intermediate `;\n` remains, breaking the `for()` syntax.

**Files:**
- Modify: `tools/ta_codegen/src/backends/c.rs:457-489`
- Test: `tools/ta_codegen/tests/backend_suite.rs` (new test)

- [ ] **Step 1: Write failing test using synthetic IR (not file-based)**

Because indicators with multi-init ForC loops (like accbands) can't parse until macros are replaced (Chunk 3), use a synthetic IR to test the rendering fix:

Add to `tools/ta_codegen/tests/backend_suite.rs`:

```rust
#[test]
fn c_for_loop_multi_init_comma_separated() {
    use ta_codegen_lib::ir::*;
    use std::collections::HashMap;

    // Build synthetic ForC: for(j=0, i=start; i<=end; i++, j++)
    let init = Box::new(Statement::Block {
        body: vec![
            Statement::Assign {
                target: Expr::Var("j".into()),
                value: Expr::Lit(0.0),
                compound: false,
            },
            Statement::Assign {
                target: Expr::Var("i".into()),
                value: Expr::Var("startIdx".into()),
                compound: false,
            },
        ],
    });
    let condition = Expr::BinOp(
        Box::new(Expr::Var("i".into())),
        BinOp::LessEq,
        Box::new(Expr::Var("endIdx".into())),
    );
    let update = Box::new(Statement::Block {
        body: vec![
            Statement::Assign {
                target: Expr::Var("i".into()),
                value: Expr::BinOp(
                    Box::new(Expr::Var("i".into())),
                    BinOp::Add,
                    Box::new(Expr::Lit(1.0)),
                ),
                compound: false,
            },
            Statement::Assign {
                target: Expr::Var("j".into()),
                value: Expr::BinOp(
                    Box::new(Expr::Var("j".into())),
                    BinOp::Add,
                    Box::new(Expr::Lit(1.0)),
                ),
                compound: false,
            },
        ],
    });
    let stmt = Statement::ForC {
        init,
        condition,
        update,
        body: vec![],
    };

    let enums = HashMap::new();
    let registry = make_registry();
    let rendered = backends::c::render_statement(&stmt, 0, false, &enums, &registry);

    // Should produce: for( j = 0, i = startIdx; ... ; i += 1, j += 1 )
    // NOT: for( j = 0;\ni = startIdx; ... )
    assert!(
        !rendered.contains(";\n"),
        "ForC init/update should use commas, not semicolons: {rendered}"
    );
    assert!(
        rendered.contains(", "),
        "ForC init/update should be comma-separated: {rendered}"
    );
}
```

Note: You may need to make `render_statement` pub in `c.rs` for direct testing, or test through the full `generate()` path with a minimal FuncDef.

- [ ] **Step 2: Run test to verify it fails**

Run: `cd tools/ta_codegen && cargo test c_for_loop_multi_init_comma_separated -- --nocapture`

Expected: FAIL — the current ForC rendering produces semicolons in Block init/update.

- [ ] **Step 3: Implement the fix — add `render_forc_part` helper**

In `tools/ta_codegen/src/backends/c.rs`, add a helper function that renders a ForC init or update part. When the statement is a `Block`, join sub-statements with `, ` instead of rendering them as separate lines:

```rust
/// Render a ForC init or update clause. If it's a Block with multiple
/// statements, comma-separate them instead of using semicolons.
fn render_forc_part(
    stmt: &Statement,
    single_precision: bool,
    enums: &HashMap<String, EnumDef>,
    registry: &Registry,
) -> String {
    match stmt {
        Statement::Block { body } => {
            body.iter()
                .map(|s| {
                    render_statement(s, 0, single_precision, enums, registry)
                        .trim()
                        .trim_end_matches(';')
                        .to_string()
                })
                .collect::<Vec<_>>()
                .join(", ")
        }
        _ => render_statement(stmt, 0, single_precision, enums, registry)
            .trim()
            .trim_end_matches(';')
            .to_string(),
    }
}
```

Then update the `ForC` arm at line 457 to use it:

```rust
Statement::ForC {
    init,
    condition,
    update,
    body,
} => {
    let init_str = render_forc_part(init, single_precision, enums, registry);
    let update_str = render_forc_part(update, single_precision, enums, registry);
    let mut out = format!(
        "{}for( {}; {}; {} )\n{}{{\n",
        pad,
        init_str,
        render_expr(condition, single_precision, registry),
        update_str,
        pad
    );
    // ... body rendering unchanged ...
```

- [ ] **Step 4: Run all existing tests**

Run: `cd tools/ta_codegen && cargo test`

Expected: All tests pass. The change only affects how Block-type init/update renders — single-statement ForC is unchanged.

- [ ] **Step 5: Commit**

```bash
git add tools/ta_codegen/src/backends/c.rs tools/ta_codegen/tests/backend_suite.rs
git commit -m "fix(codegen): comma-separate ForC init/update blocks in C backend"
```

---

### Task 2: Fix Java backend ForC init/update Block rendering

Same bug as Task 1 but in the Java backend at `java.rs:385-417`.

**Files:**
- Modify: `tools/ta_codegen/src/backends/java.rs:385-417`
- Test: `tools/ta_codegen/tests/backend_suite.rs` (new test)

- [ ] **Step 1: Write failing test using synthetic IR (same pattern as Task 1)**

Use the same synthetic `ForC` with a `Block` init/update, but render through the Java backend:

```rust
#[test]
fn java_for_loop_multi_init_comma_separated() {
    // Same synthetic ForC as c_for_loop_multi_init_comma_separated
    // but rendered through Java backend
    // ... (construct same IR) ...
    let rendered = backends::java::render_statement(&stmt, 0, false, &enums, &registry);
    assert!(
        !rendered.contains(";\n"),
        "Java ForC init/update should use commas, not semicolons: {rendered}"
    );
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd tools/ta_codegen && cargo test java_for_loop_multi_init_comma_separated -- --nocapture`

- [ ] **Step 3: Implement the same `render_forc_part` fix for Java**

Add an equivalent `render_forc_part` helper in `java.rs` and update the ForC arm at line 385.

- [ ] **Step 4: Run all tests**

Run: `cd tools/ta_codegen && cargo test`

- [ ] **Step 5: Commit**

```bash
git add tools/ta_codegen/src/backends/java.rs tools/ta_codegen/tests/backend_suite.rs
git commit -m "fix(codegen): comma-separate ForC init/update blocks in Java backend"
```

---

### Task 3: Improve Rust ForC to emit range iteration

The Rust backend at `rust_lang.rs:770-829` lowers all ForC to `init; while cond { body; update; }`. The While handler at `rust_lang.rs:901-930` already detects `i <= end` patterns and emits `for i in start..=end`. Extend this to ForC directly.

**Files:**
- Modify: `tools/ta_codegen/src/backends/rust_lang.rs:770-829`
- Test: `tools/ta_codegen/tests/backend_suite.rs` (new test)

- [ ] **Step 1: Write test using synthetic ForC IR**

Build a synthetic `ForC` with `for(i=start; i<=end; i++)` and verify Rust emits `for i in start..=end`:

```rust
#[test]
fn rust_forc_emits_range_iteration_when_possible() {
    // Build synthetic ForC: for(i=startIdx; i<=endIdx; i++)
    // ... (construct IR with single-counter <= pattern) ...
    let rendered = backends::rust_lang::render_statement(
        &stmt, 0, &ctx, &for_loop_vars, &var_inits,
        &output_names, &opt_real_params, &enums, &registry,
    );
    // Should emit range iteration
    assert!(
        rendered.contains("..="),
        "Simple ForC should emit range iteration: {rendered}"
    );
    assert!(
        !rendered.contains("while "),
        "Simple ForC should not fall through to while: {rendered}"
    );
}
```

- [ ] **Step 2: Implement ForC → range optimization**

In the `ForC` arm at `rust_lang.rs:770`, before the generic `init; while { body; update; }` lowering, check if this ForC matches the single-counter `<=` pattern:

```rust
Statement::ForC {
    init,
    condition,
    update,
    body: for_body,
} => {
    // Try range iteration: for(i=start; i<=end; i++) -> for i in start..=end
    if let Expr::BinOp(left, BinOp::LessEq, right) = condition {
        if let Expr::Var(iter_name) = left.as_ref() {
            // Check if init assigns to this var and update increments it
            if let Some(start_expr) = extract_init_value(init, iter_name) {
                if is_simple_increment(update, iter_name) {
                    let start = render_expr(&start_expr, ctx, opt_real_params, registry);
                    let end = render_expr(right, ctx, opt_real_params, registry);
                    let mut out = format!(
                        "{pad}for {iter_name} in ({start} as usize)..=({end} as usize) {{\n"
                    );
                    for s in for_body {
                        out.push_str(&render_statement(
                            s, indent + 4, ctx, for_loop_vars, var_inits,
                            output_names, opt_real_params, enums, registry,
                        ));
                    }
                    out.push_str(&format!("{pad}}}\n"));
                    return out;
                }
            }
        }
    }
    // Fall through to generic while-based lowering
    // ... existing code ...
```

You'll need two small helpers:
- `extract_init_value(stmt, var_name) -> Option<Expr>` — returns the init expression if stmt assigns to var_name
- `is_simple_increment(stmt, var_name) -> bool` — returns true if stmt is `var_name += 1` or `var_name++`

- [ ] **Step 3: Run all tests**

Run: `cd tools/ta_codegen && cargo test`

- [ ] **Step 4: Commit**

```bash
git add tools/ta_codegen/src/backends/rust_lang.rs tools/ta_codegen/tests/backend_suite.rs
git commit -m "feat(codegen): Rust ForC emits range iteration for simple counter patterns"
```

---

### Task 4: Verify SWIG naming alignment

Check that SWIG declarations align with C unity build exports. The SWIG backend at `swig.rs:80-87` generates `TA_{NAME}_Logic` variants. The C backend at `c.rs` generates `#define TA_INT_{NAME} TA_{NAME}_Logic`. These should match.

**Files:**
- Read: `tools/ta_codegen/src/backends/swig.rs`
- Read: `tools/ta_codegen/src/backends/c.rs` (search for `TA_INT_`)
- Test: `tools/ta_codegen/tests/backend_suite.rs` (the existing `check_c_variants` and SWIG checks)

- [ ] **Step 1: Write explicit naming alignment test**

```rust
#[test]
fn swig_names_match_c_exports() {
    let indicators = discover_indicators();
    let registry = make_registry();
    for name in &indicators {
        let name_owned = name.clone();
        if let Ok((func, enums)) = std::panic::catch_unwind(std::panic::AssertUnwindSafe(|| load_indicator(&name_owned))) {
            let c_out = backends::c::generate(&func, &enums, &registry);
            let swig_out = backends::swig::generate(&func, &enums, &registry);
            let upper = name.to_uppercase();
            // SWIG declares TA_{NAME} — C must export it
            if swig_out.contains(&format!("TA_{}(", upper)) {
                assert!(
                    c_out.contains(&format!("TA_{}(", upper)),
                    "{}: SWIG declares TA_{} but C doesn't export it",
                    name, upper
                );
            }
        }
    }
}
```

- [ ] **Step 2: Run the test**

Run: `cd tools/ta_codegen && cargo test swig_names_match_c_exports -- --nocapture`

Expected: Should pass. If any names mismatch, fix in the SWIG backend.

- [ ] **Step 3: Fix any mismatches found (if needed)**

- [ ] **Step 4: Commit**

```bash
git add tools/ta_codegen/src/backends/swig.rs tools/ta_codegen/tests/backend_suite.rs
git commit -m "test(codegen): verify SWIG naming aligns with C exports"
```

---

## Chunk 2: Macro Replacement Script

### Task 5: Write the macro replacement Python script — simple replacements

Create a Python script that handles the straightforward regex-based macro replacements. These are macros with predictable textual patterns that can be safely replaced with regex.

**Files:**
- Create: `scripts/replace_macros.py`

- [ ] **Step 1: Create the script with simple replacements**

The script should:
1. Find all `.c` files in `ta_func_defs/*/`
2. Apply regex replacements for each macro family
3. Report what was changed per file
4. Support `--dry-run` mode

```python
#!/usr/bin/env python3
"""Replace cross-language macros in ta_func_defs extracted .c files with plain C."""

import re
import sys
from pathlib import Path

def replace_simple_macros(content: str) -> str:
    """Replace macros that have simple, context-free expansions."""

    # ENUM_DECLARATION(RetCode) varName; -> TA_RetCode varName;
    content = re.sub(
        r'ENUM_DECLARATION\s*\(\s*RetCode\s*\)',
        'TA_RetCode',
        content
    )

    # CONSTANT_DOUBLE(name) = value; -> const double name = value;
    content = re.sub(
        r'CONSTANT_DOUBLE\s*\(\s*(\w+)\s*\)',
        r'const double \1',
        content
    )

    # CONSTANT_INTEGER(name) = value; -> const int name = value;
    content = re.sub(
        r'CONSTANT_INTEGER\s*\(\s*(\w+)\s*\)',
        r'const int \1',
        content
    )

    # ARRAY_REF(buf) -> double *buf
    # Note: sometimes followed by semicolon, sometimes used in declarations
    content = re.sub(
        r'ARRAY_REF\s*\(\s*(\w+)\s*\)',
        r'double *\1',
        content
    )

    # UNUSED_VARIABLE(x); -> (void)x;
    content = re.sub(
        r'UNUSED_VARIABLE\s*\(\s*(\w+)\s*\)\s*;',
        r'(void)\1;',
        content
    )

    # Note: bare ALLOC_ERR doesn't appear in extracted files (already TA_ALLOC_ERR).
    # No replacement needed.

    # CAST_TO_INDEX(expr) -> (int)(expr)
    content = re.sub(
        r'CAST_TO_INDEX\s*\(([^)]+)\)',
        r'(int)(\1)',
        content
    )

    # CAST_TO_I32(expr) -> (int)(expr)
    content = re.sub(
        r'CAST_TO_I32\s*\(([^)]+)\)',
        r'(int)(\1)',
        content
    )

    # CAST_TO_F64(expr) -> (double)(expr)
    content = re.sub(
        r'CAST_TO_F64\s*\(([^)]+)\)',
        r'(double)(\1)',
        content
    )

    # TA_IS_ZERO(x) -> ((-0.00000001 < (x)) && ((x) < 0.00000001))
    content = re.sub(
        r'TA_IS_ZERO\s*\(([^)]+)\)',
        r'((-0.00000001 < (\1)) && ((\1) < 0.00000001))',
        content
    )

    # TA_IS_ZERO_OR_NEG(x) -> ((x) < 0.00000001)
    content = re.sub(
        r'TA_IS_ZERO_OR_NEG\s*\(([^)]+)\)',
        r'((\1) < 0.00000001)',
        content
    )

    # TA_PER_TO_K(period) -> (2.0 / ((double)(period) + 1.0))
    content = re.sub(
        r'TA_PER_TO_K\s*\(([^)]+)\)',
        r'(2.0 / ((double)(\1) + 1.0))',
        content
    )

    # TA_GetUnstablePeriod(FUNC) -> TA_GetUnstablePeriod(TA_FUNC_UNST_FUNC)
    content = re.sub(
        r'TA_GetUnstablePeriod\s*\(\s*(\w+)\s*\)',
        r'TA_GetUnstablePeriod(TA_FUNC_UNST_\1)',
        content
    )

    # Local #define constants -> const declarations
    # Handles: #define NAME VALUE (single-line, no args)
    content = re.sub(
        r'^(\s*)#define\s+(\w+)\s+(\d+)\s*$',
        r'\1const int \2 = \3;',
        content,
        flags=re.MULTILINE
    )

    # std_* math function wrappers -> plain C math
    for func in ['atan', 'sqrt', 'fabs', 'floor', 'ceil', 'log', 'cos', 'sin',
                  'tan', 'acos', 'asin', 'exp', 'cosh', 'sinh', 'tanh', 'log10']:
        content = re.sub(
            rf'\bstd_{func}\b',
            func,
            content
        )

    return content
```

- [ ] **Step 2: Add array allocation/free replacements**

```python
def replace_array_macros(content: str) -> str:
    """Replace ARRAY_ALLOC, ARRAY_FREE, ARRAY_FREE_COND, TA_ARRAY_COPY, ARRAY_COPY, ARRAY_MEMMOVE."""

    # ARRAY_ALLOC(buf, size); -> double *buf = malloc((size) * sizeof(double));
    # Note: source files already have explicit null checks after ARRAY_ALLOC,
    # so we only emit the malloc line — don't duplicate the null check.
    content = re.sub(
        r'ARRAY_ALLOC\s*\(\s*(\w+)\s*,\s*([^)]+)\)\s*;',
        r'double *\1 = malloc((\2) * sizeof(double));',
        content
    )

    # ARRAY_INT_ALLOC(buf, size); -> int *buf = malloc((size) * sizeof(int));
    content = re.sub(
        r'ARRAY_INT_ALLOC\s*\(\s*(\w+)\s*,\s*([^)]+)\)\s*;',
        r'int *\1 = malloc((\2) * sizeof(int));',
        content
    )

    # ARRAY_INT_FREE(buf); -> free(buf);
    content = re.sub(
        r'ARRAY_INT_FREE\s*\(\s*(\w+)\s*\)\s*;',
        r'free(\1);',
        content
    )

    # ARRAY_INT_REF(buf) -> int *buf
    content = re.sub(
        r'ARRAY_INT_REF\s*\(\s*(\w+)\s*\)',
        r'int *\1',
        content
    )

    # ARRAY_INT_LOCAL(buf, size); -> int buf[size];
    content = re.sub(
        r'ARRAY_INT_LOCAL\s*\(\s*(\w+)\s*,\s*([^)]+)\)\s*;',
        r'int \1[\2];',
        content
    )

    # ARRAY_FREE(buf); -> free(buf);
    content = re.sub(
        r'ARRAY_FREE\s*\(\s*(\w+)\s*\)\s*;',
        r'free(\1);',
        content
    )

    # ARRAY_FREE_COND(flag, buf); -> if (flag) { free(buf); }
    content = re.sub(
        r'ARRAY_FREE_COND\s*\(\s*([^,]+)\s*,\s*(\w+)\s*\)\s*;',
        r'if (\1) { free(\2); }',
        content
    )

    # TA_ARRAY_COPY(dst, dstOff, src, srcOff, n); -> memcpy(&dst[dstOff], &src[srcOff], (n) * sizeof(double));
    content = re.sub(
        r'TA_ARRAY_COPY\s*\(\s*(\w+)\s*,\s*([^,]+)\s*,\s*(\w+)\s*,\s*([^,]+)\s*,\s*([^)]+)\)\s*;',
        r'memcpy(&\1[\2], &\3[\4], (\5) * sizeof(double));',
        content
    )

    # ARRAY_COPY(dst, src, count); -> memcpy(dst, src, (count) * sizeof(double));
    # 3-arg variant (no offsets), used in bbands.c
    content = re.sub(
        r'ARRAY_COPY\s*\(\s*(\w+)\s*,\s*(\w+)\s*,\s*([^)]+)\)\s*;',
        r'memcpy(\1, \2, (\3) * sizeof(double));',
        content
    )

    # ARRAY_MEMMOVE(dst, dstOff, src, srcOff, count); -> memmove(&dst[dstOff], &src[srcOff], (count) * sizeof(double));
    content = re.sub(
        r'ARRAY_MEMMOVE\s*\(\s*(\w+)\s*,\s*([^,]+)\s*,\s*(\w+)\s*,\s*([^,]+)\s*,\s*([^)]+)\)\s*;',
        r'memmove(&\1[\2], &\3[\4], (\5) * sizeof(double));',
        content
    )

    return content
```

- [ ] **Step 3: Add VALUE_HANDLE replacements**

```python
def replace_value_handle_macros(content: str) -> str:
    """Replace VALUE_HANDLE_INT, VALUE_HANDLE_GET, VALUE_HANDLE_OUT."""

    # VALUE_HANDLE_INT(x); -> int x;
    content = re.sub(
        r'VALUE_HANDLE_INT\s*\(\s*(\w+)\s*\)\s*;',
        r'int \1;',
        content
    )

    # VALUE_HANDLE_GET(x) -> x  (just the variable name)
    content = re.sub(
        r'VALUE_HANDLE_GET\s*\(\s*(\w+)\s*\)',
        r'\1',
        content
    )

    # VALUE_HANDLE_OUT(x) -> &x
    content = re.sub(
        r'VALUE_HANDLE_OUT\s*\(\s*(\w+)\s*\)',
        r'&\1',
        content
    )

    return content
```

- [ ] **Step 4: Add CIRCBUF replacements**

```python
def replace_circbuf_macros(content: str) -> str:
    """Replace CIRCBUF_PROLOG, CIRCBUF_INIT_*, CIRCBUF_NEXT, CIRCBUF_DESTROY, CIRCBUF_REF."""

    # CIRCBUF_PROLOG(buf, type, staticSize); -> type buf[staticSize]; int buf_Idx = 0;
    content = re.sub(
        r'CIRCBUF_PROLOG\s*\(\s*(\w+)\s*,\s*(\w+)\s*,\s*([^)]+)\)\s*;',
        r'\2 \1[\3]; int \1_Idx = 0;',
        content
    )

    # CIRCBUF_PROLOG_CLASS(buf, Type, staticSize); -> Type buf[staticSize]; int buf_Idx = 0;
    content = re.sub(
        r'CIRCBUF_PROLOG_CLASS\s*\(\s*(\w+)\s*,\s*(\w+)\s*,\s*(\w+)\s*\)\s*;',
        r'\2 \1[\3]; int \1_Idx = 0;',
        content
    )

    # CIRCBUF_INIT_LOCAL_ONLY(buf, type); -> /* no-op: declared by PROLOG */
    content = re.sub(
        r'CIRCBUF_INIT_LOCAL_ONLY\s*\([^)]*\)\s*;',
        '/* circular buffer already declared */',
        content
    )

    # CIRCBUF_INIT_CLASS(buf, Type, size); -> memset(buf, 0, (size) * sizeof(Type)); buf_Idx = 0;
    content = re.sub(
        r'CIRCBUF_INIT_CLASS\s*\(\s*(\w+)\s*,\s*(\w+)\s*,\s*([^)]+)\)\s*;',
        r'memset(\1, 0, (\3) * sizeof(\2)); \1_Idx = 0;',
        content
    )

    # CIRCBUF_NEXT(buf); -> buf_Idx++; if (buf_Idx >= bufSize) buf_Idx = 0;
    # Note: The buffer size varies. For optInTimePeriod-sized buffers, it's optInTimePeriod.
    # For SMOOTH_PRICE_SIZE buffers, it's SMOOTH_PRICE_SIZE.
    # We'll use a modulo pattern that works generically:
    # Actually, the original macro just does buf_Idx = (buf_Idx + 1) % bufSize
    # But bufSize isn't always available as a named variable.
    # Keep as CIRCBUF_NEXT for now — the parser handles it as FuncCall.
    # MANUAL FIX NEEDED: Each file using CIRCBUF_NEXT needs manual inspection
    # to determine the correct buffer size variable.

    # CIRCBUF_DESTROY(buf); -> /* no-op for stack-allocated */
    content = re.sub(
        r'CIRCBUF_DESTROY\s*\([^)]*\)\s*;',
        '/* circular buffer cleanup (stack-allocated, no-op) */',
        content
    )

    # CIRCBUF_REF(arr[idx])field -> arr[idx].field
    # Pattern: CIRCBUF_REF(name[name_Idx])fieldname
    content = re.sub(
        r'CIRCBUF_REF\s*\(\s*(\w+)\s*\[\s*(\w+)\s*\]\s*\)\s*(\w+)',
        r'\1[\2].\3',
        content
    )

    return content
```

- [ ] **Step 5: Add main function with dry-run support**

```python
def process_file(filepath: Path, dry_run: bool = False) -> list[str]:
    """Process a single .c file, return list of changes made."""
    original = filepath.read_text()
    content = original

    content = replace_simple_macros(content)
    content = replace_array_macros(content)
    content = replace_value_handle_macros(content)
    content = replace_circbuf_macros(content)

    if content == original:
        return []

    changes = []
    for i, (orig_line, new_line) in enumerate(
        zip(original.splitlines(), content.splitlines()), 1
    ):
        if orig_line != new_line:
            changes.append(f"  L{i}: {orig_line.strip()} -> {new_line.strip()}")

    if not dry_run:
        filepath.write_text(content)

    return changes


def main():
    import argparse
    parser = argparse.ArgumentParser(description="Replace macros in ta_func_defs .c files")
    parser.add_argument("--dry-run", action="store_true", help="Show changes without writing")
    parser.add_argument("--file", type=str, help="Process a single file instead of all")
    args = parser.parse_args()

    base = Path(__file__).parent.parent / "ta_func_defs"

    if args.file:
        files = [Path(args.file)]
    else:
        files = sorted(base.glob("*/*.c"))

    total_changes = 0
    for filepath in files:
        changes = process_file(filepath, dry_run=args.dry_run)
        if changes:
            print(f"\n{filepath.relative_to(base)}:")
            for c in changes[:10]:  # Show first 10 changes per file
                print(c)
            if len(changes) > 10:
                print(f"  ... and {len(changes) - 10} more")
            total_changes += len(changes)

    mode = "Would change" if args.dry_run else "Changed"
    print(f"\n{mode} {total_changes} lines across {len(files)} files")


if __name__ == "__main__":
    main()
```

- [ ] **Step 6: Test the script in dry-run mode on a known file**

Run: `python3 scripts/replace_macros.py --dry-run --file ta_func_defs/accbands/accbands.c`

Expected: Shows list of macro replacements that would be made. Verify each replacement looks correct.

- [ ] **Step 7: Test on a working base function (should be no-op or minimal)**

Run: `python3 scripts/replace_macros.py --dry-run --file ta_func_defs/sma/sma.c`

Expected: Zero or very few changes (SMA is already mostly clean).

- [ ] **Step 8: Commit the script**

```bash
git add scripts/replace_macros.py
git commit -m "feat: add macro replacement script for ta_func_defs cleanup"
```

---

### Task 6: Verify Hilbert transform macros (deferred — parser handles them)

The 6 HT_* files (`ht_trendmode`, `ht_dcperiod`, `ht_dcphase`, `ht_phasor`, `ht_sine`, `ht_trendline`) use `HILBERT_VARIABLES`, `INIT_HILBERT_VARIABLES`, `DO_HILBERT_ODD`, `DO_HILBERT_EVEN`, and `DO_PRICE_WMA`. These are reusability macros — they encapsulate ~20 lines of repeated code.

**Strategy:** The parser already resolves these to proper IR nodes (`FuncCall` with args preserved, `HILBERT_VARIABLES` expands to variable declarations). The backends render them correctly. Extracting a shared helper file is deferred — it's a cleanup that doesn't block compilation. The spec's suggestion of `ta_func_defs/_helpers/hilbert.h` can be revisited in a follow-up.

**Files:**
- Test: `tools/ta_codegen/tests/backend_suite.rs`

- [ ] **Step 1: Verify parser handles HILBERT_VARIABLES correctly**

Run: `cd tools/ta_codegen && cargo test -- ht_trendmode --nocapture`

If no tests exist yet for HT functions, create one:

```rust
#[test]
fn ht_trendmode_parses_and_generates() {
    let (func, enums) = load_indicator("ht_trendmode");
    let _outputs = generate_all(&func, &enums);
    // If we get here without panic, parsing and generation succeeded
}
```

- [ ] **Step 2: Commit test if added**

```bash
git add tools/ta_codegen/tests/backend_suite.rs
git commit -m "test(codegen): verify HT_TRENDMODE parses and generates"
```

---

### Task 7: Handle local `#define` macros (TRUE_RANGE, CALC_TERMS, etc.)

Local `#define` macros inside function bodies get stripped by `strip_local_macros()`. The bodies still reference the macro names. The parser's ALL_CAPS handler catches function-like macro calls as `FuncCall` nodes.

**Strategy:** For the initial pass, leave these as-is — the parser handles them. In a follow-up, inline-expand the most common ones (`TRUE_RANGE` used in 6 files, `round_pos` used in 5 files). Simple constants like `#define SMOOTH_PRICE_SIZE 50` can be left as-is since `strip_local_macros()` removes the `#define` and the symbol becomes a `Var` node.

**Files:**
- None for now — deferred to follow-up

- [ ] **Step 1: Document which local macros exist and where**

Run a grep to inventory all local `#define` macros in ta_func_defs:

```bash
grep -rn '^[[:space:]]*#define ' ta_func_defs/ --include='*.c' | head -30
```

- [ ] **Step 2: Verify parser handles them without crashing**

Run: `cd tools/ta_codegen && cargo test -- adx --nocapture` (adx uses TRUE_RANGE)

---

## Chunk 3: Apply Replacements and Parser Updates

### Task 8: Run the replacement script on all files

**Files:**
- Modify: `ta_func_defs/*/*.c` (~158 files)

- [ ] **Step 1: Run dry-run on all files**

Run: `python3 scripts/replace_macros.py --dry-run 2>&1 | tail -20`

Review the summary output. Check total change count is reasonable.

- [ ] **Step 2: Spot-check a few files**

Run: `python3 scripts/replace_macros.py --dry-run --file ta_func_defs/macd/macd.c`

Verify ARRAY_ALLOC → malloc, ARRAY_FREE → free, VALUE_HANDLE → plain C, etc.

- [ ] **Step 3: Run the script for real**

Run: `python3 scripts/replace_macros.py`

- [ ] **Step 4: Verify the 6 base functions still parse correctly**

Run: `cd tools/ta_codegen && cargo test -- sma --nocapture && cargo test -- mult --nocapture && cargo test -- rsi --nocapture`

Expected: All pass. The replacements for these should be minimal/no-op.

- [ ] **Step 5: Verify a previously-broken function now parses**

Run: `cd tools/ta_codegen && cargo test -- accbands --nocapture`

This will likely still fail because accbands uses cross-function calls that need forward declarations. But it should at least parse without macro-related panics.

- [ ] **Step 6: Commit all changed files**

```bash
git add ta_func_defs/
git commit -m "refactor: replace cross-language macros with plain C in all ta_func_defs"
```

---

### Task 9: Fix CIRCBUF_NEXT manually

`CIRCBUF_NEXT` requires knowing the buffer size, which varies per file. The replacement script left these as comments or partial replacements. Fix them manually.

**Files:**
- Modify: `ta_func_defs/mfi/mfi.c`, `ta_func_defs/cci/cci.c`, `ta_func_defs/ht_trendmode/ht_trendmode.c`, `ta_func_defs/ht_sine/ht_sine.c`, `ta_func_defs/ht_dcphase/ht_dcphase.c`, `ta_func_defs/ht_trendline/ht_trendline.c`

- [ ] **Step 1: Identify all CIRCBUF_NEXT occurrences and their buffer sizes**

For each file, find the CIRCBUF_PROLOG that declares the buffer and determine the size:
- `mfi.c`: `CIRCBUF_PROLOG_CLASS(mflow, MoneyFlow, 50)` with dynamic `optInTimePeriod` → `mflow_Idx = (mflow_Idx + 1) % optInTimePeriod;`
- `cci.c`: Similar pattern
- `ht_*.c` files: `CIRCBUF_PROLOG(smoothPrice, double, SMOOTH_PRICE_SIZE)` → `smoothPrice_Idx = (smoothPrice_Idx + 1) % SMOOTH_PRICE_SIZE;`

- [ ] **Step 2: Replace each CIRCBUF_NEXT with the correct modulo expression**

- [ ] **Step 3: Verify parser handles the replacements**

Run: `cd tools/ta_codegen && cargo test -- mfi --nocapture && cargo test -- cci --nocapture`

- [ ] **Step 4: Commit**

```bash
git add ta_func_defs/mfi/mfi.c ta_func_defs/cci/cci.c ta_func_defs/ht_*/ht_*.c
git commit -m "fix: manually replace CIRCBUF_NEXT with correct buffer-size modulo"
```

---

### Task 10: Update parser to handle new plain C patterns

After macro replacement, the parser may encounter patterns it hasn't seen before. Common issues:
- `malloc(...)` → parser may not handle as an expression
- `free(...)` → needs to be a standalone statement
- `memcpy(...)` → standalone function call
- `memset(...)` → standalone function call
- `(void)x;` → cast expression as statement
- `(int)(expr)` → cast expression
- `(double)(expr)` → cast expression

**Files:**
- Modify: `tools/ta_codegen/src/parser/c_source.rs`
- Test: `tools/ta_codegen/tests/backend_suite.rs`

- [ ] **Step 1: Run the full test suite to find parser failures**

Run: `cd tools/ta_codegen && cargo test 2>&1 | grep -E 'FAILED|panicked' | head -20`

This will reveal which indicators fail to parse after macro replacement.

- [ ] **Step 2: Fix each parser issue as discovered**

Common fixes likely needed:
- `malloc(expr)` — already parsed as `FuncCall("malloc", [expr])` if lowercase. May need to add to known functions.
- `free(expr)` — same, `FuncCall("free", [expr])`. May need to handle as statement.
- `memcpy(a, b, c)` — same pattern.
- Cast expressions `(int)(expr)` — parser's `parse_expr` may need cast handling.

- [ ] **Step 3: Run tests after each fix**

Run: `cd tools/ta_codegen && cargo test`

- [ ] **Step 4: Commit parser fixes**

```bash
git add tools/ta_codegen/src/parser/c_source.rs
git commit -m "fix(parser): handle malloc/free/memcpy/cast patterns from macro replacement"
```

---

### Task 11: Add math function mapping to backends

After replacing `std_atan` → `atan` in source files, backends need to render `atan(x)` idiomatically per language.

**Files:**
- Modify: `tools/ta_codegen/src/backends/c.rs` (C: `atan` stays as-is)
- Modify: `tools/ta_codegen/src/backends/java.rs` (Java: `Math.atan`)
- Modify: `tools/ta_codegen/src/backends/rust_lang.rs` (Rust: `x.atan()` or `T::atan(x)`)
- Test: `tools/ta_codegen/tests/backend_suite.rs`

- [ ] **Step 1: Write test for math function rendering**

```rust
#[test]
fn backends_render_math_functions_idiomatically() {
    // Use an indicator that calls atan (e.g., ht_trendmode)
    let (func, enums) = load_indicator("ht_trendmode");
    let registry = make_registry();

    let c_out = backends::c::generate(&func, &enums, &registry);
    let java_out = backends::java::generate(&func, &enums, &registry);

    // C should use plain atan()
    assert!(c_out.contains("atan("), "C should render atan()");

    // Java should use Math.atan()
    assert!(java_out.contains("Math.atan("), "Java should render Math.atan()");
}
```

- [ ] **Step 2: Add math function mapping to each backend's render_func_call**

In each backend, add a match arm for math function names:

```rust
// In render_func_call:
"atan" | "sqrt" | "fabs" | "floor" | "ceil" | "log" | "cos" | "sin"
| "tan" | "acos" | "asin" | "exp" | "cosh" | "sinh" | "tanh" | "log10" => {
    // C: atan(x) — no change
    // Java: Math.atan(x)
    // Rust: (x).atan() for trig, x.sqrt() etc.
    ...
}
```

- [ ] **Step 3: Run tests**

Run: `cd tools/ta_codegen && cargo test`

- [ ] **Step 4: Commit**

```bash
git add tools/ta_codegen/src/backends/c.rs tools/ta_codegen/src/backends/java.rs tools/ta_codegen/src/backends/rust_lang.rs tools/ta_codegen/tests/backend_suite.rs
git commit -m "feat(codegen): render math functions idiomatically per language"
```

---

## Chunk 4: Forward Declarations and Cross-Function Calls

### Task 12: Generate forward declarations from FuncDef list

The C server unity build `#include`s all generated `.c` files alphabetically. When ACCBANDS (A) calls `TA_INT_SMA` (S), SMA isn't declared yet. Fix: generate a forward declaration header.

**Important:** The `Registry` only stores indicator names (strings), not `FuncDef` objects. It has no `all_function_names()` or `get()` method. Instead, use the `&[FuncDef]` slice already available in `server_gen.rs` — `generate_c_server(funcs: &[FuncDef])` receives the full list.

**Files:**
- Modify: `tools/ta_codegen/src/server_gen.rs:147-229` (`generate_c_header_stub`)
- Test: Run `make servers` and verify compilation

- [ ] **Step 1: Understand current header generation**

Read `server_gen.rs:147-229` — the `generate_c_header_stub()` function currently takes no arguments and generates type definitions only. It needs to accept `&[FuncDef]` to emit prototypes.

- [ ] **Step 2: Change `generate_c_header_stub` signature and add forward declarations**

Change the signature from `pub fn generate_c_header_stub() -> String` to `pub fn generate_c_header_stub(funcs: &[FuncDef]) -> String`. Update all call sites. Then iterate over `funcs` to emit prototypes:

```rust
// After type definitions, before #endif:
s.push_str("\n/* Forward declarations for all indicators */\n");
for func in funcs {
    let upper = func.name.to_uppercase();

    // Build C parameter list from FuncDef
    let lookback_params = build_lookback_param_str(func);
    let full_params = build_full_param_str(func);

    s.push_str(&format!("extern int TA_{upper}_Lookback({lookback_params});\n"));
    s.push_str(&format!("extern TA_RetCode TA_{upper}({full_params});\n"));
    s.push_str(&format!("extern TA_RetCode TA_{upper}_Logic({full_params});\n"));
    s.push_str(&format!("#define TA_INT_{upper} TA_{upper}_Logic\n"));
}
```

Write helper functions `build_lookback_param_str` and `build_full_param_str` that generate C parameter strings from `FuncDef` fields (inputs, optional_inputs, outputs).

- [ ] **Step 3: Test by generating and compiling the C server**

Run: `make servers`

Expected: C server compiles without "undeclared function" errors for cross-function calls.

- [ ] **Step 4: Commit**

```bash
git add tools/ta_codegen/src/server_gen.rs
git commit -m "feat(codegen): generate forward declarations in C server header"
```

---

### Task 13: Handle unregistered internal helper functions

`bbands.c:75` calls `stddev_using_precalc_ma(...)`. This helper is defined in the legacy `src/ta_func/ta_STDDEV.c` (lines ~349-370) but was **never extracted** to `ta_func_defs/stddev/stddev.c`. A forward declaration alone will cause a linker error — the function body is missing from the codegen pipeline.

**Files:**
- Read: `ta_func_defs/bbands/bbands.c:75` (the call site)
- Read: `src/ta_func/ta_STDDEV.c:349-370` (the definition)
- Modify: `ta_func_defs/stddev/stddev.c` (add the helper function body)
- Modify: `tools/ta_codegen/src/server_gen.rs` (add forward declaration)

- [ ] **Step 1: Identify all cross-function calls to unregistered helpers**

```bash
grep -rn 'stddev_using_precalc' ta_func_defs/
grep -rn 'TA_INT_' ta_func_defs/ --include='*.c' | grep -v '#define'
```

Find all internal helpers that are called but have no extracted `.c` files.

- [ ] **Step 2: Extract the helper function body**

Copy `stddev_using_precalc_ma` from `src/ta_func/ta_STDDEV.c` into `ta_func_defs/stddev/stddev.c`, below the existing `stddev` function. Apply the same macro replacements (the function uses plain C already in the legacy source, but verify).

- [ ] **Step 3: Add forward declaration in the generated header**

In `server_gen.rs`, add a hardcoded block for known internal helpers:

```rust
s.push_str("/* Internal helper forward declarations */\n");
s.push_str("extern void stddev_using_precalc_ma(const double inReal[], const double inMovAvg[], int inMovAvgBegIdx, int inMovAvgNbElement, int timePeriod, double output[]);\n");
```

- [ ] **Step 4: Verify BBANDS compiles**

Run: `make servers`

Expected: C server compiles. BBANDS can call stddev_using_precalc_ma.

- [ ] **Step 5: Commit**

```bash
git add ta_func_defs/stddev/stddev.c tools/ta_codegen/src/server_gen.rs
git commit -m "fix(codegen): extract stddev_using_precalc_ma helper and add forward declaration"
```

---

## Chunk 5: Integration Testing and Verification

### Task 14: Generate and compile all backend servers

**Files:**
- None modified — this is a verification task

- [ ] **Step 1: Build everything with make**

Run: `make servers`

This single command generates per-function source (`ta_codegen generate`), generates server wrappers (`ta_codegen generate-servers`), and compiles all servers (`ta_codegen build`). It handles C, Java, .NET, and SWIG.

Expected: All servers compile without errors.

- [ ] **Step 2: If compilation fails, isolate the failing backend**

Run individual cargo commands to isolate:
```bash
cd tools/ta_codegen
cargo run --release -- build --backend=c 2>&1 | head -30
cargo run --release -- build --backend=java 2>&1 | head -30
cargo run --release -- build --backend=swig 2>&1 | head -30
```

- [ ] **Step 3: For each failure, trace and fix**

1. Read the error message (which function, which line)
2. Look at the generated source in `ta_codegen_output/<lang>/`
3. Trace to the IR (add `--verbose` flag or print debug)
4. Fix either the source `.c` file (macro replacement issue) or the backend (rendering issue)
5. Re-run `make servers`

---

### Task 15: Run full regression tests

**Files:**
- None modified — this is a verification task

- [ ] **Step 1: Build ta_regtest**

Run: `make ta_regtest`

- [ ] **Step 2: Run full regtest from top level**

Run: `make regtest`

Expected: All functions pass across all languages. If failures occur:
1. Check which function/language fails
2. Use `./bin/ta_regtest --codegen-only --language=c --function=FAILING_FUNC` to isolate
3. Compare C reference output vs. generated server output
4. Fix and re-run

- [ ] **Step 3: Run per-language spot checks**

```bash
cd bin
./ta_regtest --codegen-only --language=c
./ta_regtest --codegen-only --language=java
./ta_regtest --codegen-only --language=swig
```

- [ ] **Step 4: Commit all final fixes**

```bash
git add -A
git commit -m "fix: resolve remaining compilation and test failures across all backends"
```

---

### Task 16: Final cleanup

- [ ] **Step 1: Commit generated output for diffing**

```bash
git add ta_codegen_output/
git commit -m "chore: track updated generated output after macro cleanup"
```

- [ ] **Step 2: Update CLAUDE.md if any workflow changed**

If the macro replacement affects the "Macro System" sections in CLAUDE.md, update to reflect that extracted source files now use plain C.
