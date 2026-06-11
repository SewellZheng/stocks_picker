# Java Server Compilation Fixes — Round 2 Implementation Plan

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Eliminate all ~3739 remaining Java server compilation errors.

**Architecture:** 11 targeted fixes across `server_gen.rs` (template), `java.rs` (backend), and `helper_registry.rs` (inlining). Each fix addresses one root cause independently. Fixes are ordered by error count — biggest wins first.

**Tech Stack:** Rust (ta_codegen), Java (generated server)

**Spec:** `docs/superpowers/specs/2026-03-14-java-server-compilation-fixes-round2-design.md`

---

## Chunk 1: Template & Hoisted Block Fixes (~3476 errors)

### Task 1: Add candleSettings Field to Java Server Template (~1800 errors)

The `Core` class is missing a `candleSettings` field. The `candle_settings.rs` module already generates unpacking code like `this.candleSettings.bodyLong.rangeType`, but the field, its type, and the settings enum are never declared.

**Files:**
- Modify: `tools/ta_codegen/src/server_gen.rs` (~line 788-791, inside `generate_java_server()`)
- Reference: `tools/ta_codegen/src/candle_settings.rs` (lines 12-27 for setting names and properties)
- Reference: `include/ta_defs.h` (search for `TA_CandleSettingType` for default values)
- Reference: `src/tools/gen_code/gen_code.c` (search for `candleSettings` for legacy Java initialization)

- [ ] **Step 1: Research legacy candle settings initialization**

Read the reference files to understand:
1. The 11 setting names from `candle_settings.rs` lines 12-24: `BodyLong`, `BodyVeryLong`, `BodyShort`, `BodyDoji`, `ShadowLong`, `ShadowVeryLong`, `ShadowShort`, `ShadowVeryShort`, `Near`, `Far`, `Equal`
2. The 3 properties per setting from line 27: `rangeType`, `avgPeriod`, `factor`
3. Default values from `ta_defs.h` (search for `TA_CandleDefaultRangeType`, `TA_CandleDefaultAvgPeriod`, `TA_CandleDefaultFactor`)
4. How `gen_code.c` generates Java candle settings initialization

- [ ] **Step 2: Add CandleSetting class and CandleSettings container class**

The generated Java code uses named-field access: `this.candleSettings.bodyLong.rangeType` (confirmed in `candle_settings.rs` lines 231-247, `emit_java_unpacking()`). This means `candleSettings` must be an object with named fields, NOT an array.

In `server_gen.rs`, in `generate_java_server()`, after the `MAType` enum (~line 786), add:

```rust
s.push_str("class CandleSetting {\n");
s.push_str("    int rangeType;\n");
s.push_str("    int avgPeriod;\n");
s.push_str("    double factor;\n");
s.push_str("    CandleSetting(int rt, int ap, double f) { rangeType = rt; avgPeriod = ap; factor = f; }\n");
s.push_str("}\n\n");

s.push_str("class CandleSettings {\n");
```

Then for each of the 11 settings, emit a field with default values from `ta_defs.h`. Example:

```rust
s.push_str("    CandleSetting bodyLong = new CandleSetting(0, 10, 1.0);\n");
s.push_str("    CandleSetting bodyVeryLong = new CandleSetting(0, 10, 3.0);\n");
// ... etc for all 11 settings with their defaults from ta_defs.h
```

Close the class and add the field to Core:

```rust
s.push_str("}\n\n");
```

The defaults must match `ta_defs.h` — search for `TA_CandleDefaultRangeType`, `TA_CandleDefaultAvgPeriod`, `TA_CandleDefaultFactor` arrays. The camelCase field names must match what `pascal_to_camel_case()` in `candle_settings.rs` produces:
- `BodyLong` → `bodyLong`
- `BodyVeryLong` → `bodyVeryLong`
- `ShadowVeryShort` → `shadowVeryShort`
- etc.

- [ ] **Step 3: Add the candleSettings field to Core class**

In `server_gen.rs`, after the `unstablePeriod` and `compatibility` fields (~line 790-791), add the candleSettings declaration with proper default initialization.

- [ ] **Step 4: Build and verify**

```bash
cd tools/ta_codegen && cargo build 2>&1 | tail -5
cargo run -- generate-servers --backend=java
cd ../../ta_codegen_output/java && javac -Xmaxerrs 5 TaCodegenServe.java 2>&1 | grep "candleSettings" | wc -l
```

Expected: 0 errors mentioning `candleSettings`.

```bash
javac -Xmaxerrs 10000 TaCodegenServe.java 2>&1 | tail -3
```

Expected: Error count drops from ~3739 to ~1939.

- [ ] **Step 5: Run cargo tests**

```bash
cd tools/ta_codegen && cargo test --lib --test backend_suite 2>&1 | tail -5
```

Expected: All 63 tests pass.

- [ ] **Step 6: Commit**

```bash
cd tools/ta_codegen && git add src/server_gen.rs
git commit -m "fix(java): add CandleSetting class, enum, and field to server template"
```

---

### Task 2: Declare Hoisted Block Local Variables (~920 errors)

`render_hoisted_blocks()` declares the output temp variable but not the helper's internal locals (which are `VarDecl` statements in the hoisted body). `render_statement` skips all `VarDecl`, so these locals vanish.

**Files:**
- Modify: `tools/ta_codegen/src/backends/java.rs` (~lines 404-441, `render_hoisted_blocks()`)

- [ ] **Step 1: Add VarDecl scanning to `render_hoisted_blocks`**

In `render_hoisted_blocks()`, after the output temp declaration (line 426), add a loop that scans the body for `VarDecl` entries and emits their declarations:

```rust
out.push_str(&format!("{pad}{java_decl};\n"));
// Declare local variables from the hoisted helper body
// (render_statement skips VarDecl, so these need explicit declaration here)
for stmt in body {
    if let Statement::VarDecl { var_type: vt, name, .. } = stmt {
        let local_decl = match vt {
            VarType::Real => format!("double {name}"),
            VarType::Integer | VarType::Index => format!("int {name}"),
            VarType::RetCodeType => format!("RetCode {name}"),
            VarType::RealPointer => format!("double[] {name}"),
            VarType::IntPointer => format!("int[] {name}"),
            VarType::RealArray(size) => format!("double[] {name} = new double[{size}]"),
            VarType::IntArray(size) => format!("int[] {name} = new int[{size}]"),
        };
        out.push_str(&format!("{pad}{local_decl};\n"));
    }
}
for stmt in body {
```

- [ ] **Step 2: Build and verify**

```bash
cd tools/ta_codegen && cargo build 2>&1 | tail -5
cargo run -- generate-servers --backend=java
cd ../../ta_codegen_output/java && javac -Xmaxerrs 10000 TaCodegenServe.java 2>&1 | tail -3
```

Expected: Error count drops by ~920. Specifically, check that `tmp_0`, `range_0`, `i` (in hoisted blocks), `close`, `open`, `Plus_dm` are no longer in the error list:

```bash
javac -Xmaxerrs 10000 TaCodegenServe.java 2>&1 | grep "variable tmp_0\|variable range_0\|variable _tempReal" | wc -l
```

Expected: 0.

- [ ] **Step 3: Run cargo tests**

```bash
cd tools/ta_codegen && cargo test --lib --test backend_suite 2>&1 | tail -5
```

- [ ] **Step 4: Commit**

```bash
cd tools/ta_codegen && git add src/backends/java.rs
git commit -m "fix(java): declare hoisted block local variables in render_hoisted_blocks"
```

---

### Task 3: Extend hoist_block_helpers to If/While/For Conditions (~756 errors)

`hoist_block_helpers()` is only called for VarDecl inits and Assign values. Multi-statement helpers like `ta_candleaverage` in `If` conditions pass through as raw function calls. Need to hoist from all expression contexts.

**Files:**
- Modify: `tools/ta_codegen/src/backends/java.rs` (render_statement — If, While, DoWhile, ForC, Switch arms)
- Reference: `tools/ta_codegen/src/backends/java.rs` (~lines 503-513, existing Assign hoist pattern)

- [ ] **Step 1: Study the existing hoist pattern in Statement::Assign**

Read `java.rs` lines 503-513. The pattern is:
1. Create a `hoisted` vec and get the counter
2. Call `hoist_block_helpers(value, helpers, &mut hoisted, &mut cnt)`
3. Set the counter back
4. Call `render_hoisted_blocks` to emit declarations + body
5. Use the simplified `new_value` for the rest of the statement

- [ ] **Step 2: Add hoisting to Statement::If condition**

In the `Statement::If` arm (after the alloc-err check, before the `format!("if(...")` call), add:

```rust
// Hoist multi-statement helpers from the condition expression
let mut hoisted = Vec::new();
let mut cnt = inline_counter.get();
let new_condition = hoist_block_helpers(
    condition, helpers, &mut hoisted, &mut cnt,
);
inline_counter.set(cnt);
let mut out = render_hoisted_blocks(
    &hoisted, indent, single_precision, enums, registry,
    helpers, inline_counter, address_of_vars,
);
out.push_str(&format!(
    "{}if( {} ) {{\n",
    pad,
    render_expr(&new_condition, single_precision, registry, helpers,
        address_of_vars)
));
```

Replace the existing `let mut out = format!("{}if( {} ) {{\n", ...)` with this.

- [ ] **Step 3: Add hoisting to Statement::While condition**

Find the `Statement::While` arm. Apply the same pattern — hoist from condition, emit hoisted blocks before the `while`.

- [ ] **Step 4: Add hoisting to Statement::DoWhile condition**

Find the `Statement::DoWhile` arm. The condition is at the END of the loop. Hoist from condition and emit the hoisted blocks INSIDE the loop body, just before the `} while(condition)` line.

**Note:** This depends on Task 2's fix — hoisted blocks emitted inside the loop body will contain VarDecl statements that need `render_hoisted_blocks` to declare their locals. Ensure Task 2 is completed first.

- [ ] **Step 5: Add hoisting to Statement::ForC condition**

Find the `Statement::ForC` arm. Hoist from the condition expression. Emit hoisted blocks before the `for` statement.

- [ ] **Step 6: Add hoisting to Statement::Switch expression**

Find the `Statement::Switch` arm. Hoist from the switch expression. Emit hoisted blocks before the `switch`.

- [ ] **Step 7: Build and verify**

```bash
cd tools/ta_codegen && cargo build 2>&1 | tail -5
cargo run -- generate-servers --backend=java
cd ../../ta_codegen_output/java && javac -Xmaxerrs 10000 TaCodegenServe.java 2>&1 | tail -3
```

Expected: Error count drops by ~756. Check:

```bash
javac -Xmaxerrs 10000 TaCodegenServe.java 2>&1 | grep "ta_candleaverage" | wc -l
```

Expected: 0.

- [ ] **Step 8: Run cargo tests**

```bash
cd tools/ta_codegen && cargo test --lib --test backend_suite 2>&1 | tail -5
cargo clippy 2>&1 | grep "java.rs" | wc -l
```

- [ ] **Step 9: Commit**

```bash
cd tools/ta_codegen && git add src/backends/java.rs
git commit -m "fix(java): extend hoist_block_helpers to If/While/DoWhile/ForC/Switch conditions"
```

---

## Chunk 2: Enum/Constant & Type Coercion Fixes (~262 errors)

### Task 4: Map TA_MAType_* Constants in Expr::Var (~4 errors)

`TA_MAType_SMA` etc. appear in expression context (not just switch labels). Need mapping in `render_expr`.

**Files:**
- Modify: `tools/ta_codegen/src/backends/java.rs` (~lines 904-913, `Expr::Var` arm in `render_expr`)

- [ ] **Step 1: Add TA_MAType_* mappings**

In `render_expr`'s `Expr::Var` match arm, add all 9 MAType constant mappings:

```rust
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
```

- [ ] **Step 2: Build, verify, commit**

```bash
cd tools/ta_codegen && cargo build && cargo run -- generate-servers --backend=java
cd ../../ta_codegen_output/java && javac -Xmaxerrs 10000 TaCodegenServe.java 2>&1 | grep "TA_MAType" | wc -l
```

Expected: 0.

```bash
cd tools/ta_codegen && cargo test --lib --test backend_suite 2>&1 | tail -5
git add src/backends/java.rs && git commit -m "fix(java): map TA_MAType_* constants to MAType enum in expressions"
```

---

### Task 5: Fix FuncUnstId PascalCase Conversion (~30 errors)

`to_pascal_case("HT_DCPERIOD")` produces `"Ht_dcperiod"` instead of `"HtDcPeriod"`. The function only capitalizes the first letter — it doesn't handle underscores.

**Files:**
- Modify: `tools/ta_codegen/src/backends/java.rs` (~line 1024, `to_pascal_case()`)

- [ ] **Step 1: Fix `to_pascal_case` to handle underscores and compound words**

The current `to_pascal_case` only capitalizes the first letter. It needs to handle underscore-separated segments. However, compound words like `DCPERIOD` (which should become `DcPeriod`) cannot be split algorithmically.

**Two-part fix:**

1. First, improve `to_pascal_case` to handle underscores (split on `_`, capitalize each segment):

```rust
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
```

This handles `ADX` → `Adx`, `STOCH_RSI` → `StochRsi`, `MINUS_DI` → `MinusDi`, `PLUS_DM` → `PlusDm` correctly.

2. Then add a lookup table for the Hilbert transform compound words, because `HT_DCPERIOD` → `to_pascal_case` → `HtDcperiod` but the enum has `HtDcPeriod`. Add the lookup BEFORE the algorithmic conversion in the `UNSTABLE_PERIOD` handler (~line 1057):

```rust
if fname == "UNSTABLE_PERIOD" {
    if let Some(Expr::Var(func_name)) = args.first() {
        let base = func_name
            .strip_prefix("FUNC_UNST_")
            .unwrap_or(func_name);
        // Lookup table for compound-word FuncUnstId names
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
}
```

Verify the lookup matches `server_gen.rs` line 773-777 enum variants exactly: `Adx, Adxr, Atr, Cmo, Dx, Ema, HtDcPeriod, HtDcPhase, HtPhasor, HtSine, HtTrendline, HtTrendMode, Imi, Kama, Mama, Mfi, MinusDI, MinusDM, Natr, PlusDI, PlusDM, Rsi, StochRsi, T3, None`.

- [ ] **Step 2: Build and verify**

```bash
cd tools/ta_codegen && cargo build && cargo run -- generate-servers --backend=java
cd ../../ta_codegen_output/java && javac -Xmaxerrs 10000 TaCodegenServe.java 2>&1 | grep "FuncUnstId" | wc -l
```

Expected: 0.

- [ ] **Step 3: Run tests and commit**

```bash
cd tools/ta_codegen && cargo test --lib --test backend_suite 2>&1 | tail -5
git add src/backends/java.rs && git commit -m "fix(java): fix FuncUnstId PascalCase conversion for underscored names"
```

---

### Task 6: Add Int-to-Boolean Wrapping in Conditions (~20 errors)

C allows `if(intVar)`, Java requires `if(intVar != 0)`.

**Files:**
- Modify: `tools/ta_codegen/src/backends/java.rs` (Statement::If, Statement::While, Statement::DoWhile arms)
- Reference: `src/tools/gen_code/gen_code.c` (search for int-to-boolean pattern)
- Reference: existing `Core.java` for comparison

- [ ] **Step 1: Add a helper to detect boolean expressions**

Add near the top of `java.rs`:

```rust
/// Check if an expression already produces a boolean result in Java.
fn is_boolean_expr(expr: &Expr) -> bool {
    match expr {
        Expr::BinOp(_, op, _) => matches!(
            op,
            BinOp::Eq | BinOp::NotEq | BinOp::Less | BinOp::LessEq
                | BinOp::Greater | BinOp::GreaterEq | BinOp::And | BinOp::Or
        ),
        Expr::Not(_) => true,
        _ => false,
    }
}
```

- [ ] **Step 2: Wrap non-boolean conditions in If/While/DoWhile**

In the `Statement::If` arm, after hoisting, when rendering the condition:

```rust
let cond_str = render_expr(&new_condition, single_precision, registry, helpers, address_of_vars);
let cond_java = if is_boolean_expr(&new_condition) {
    cond_str
} else {
    format!("({cond_str}) != 0")
};
out.push_str(&format!("{}if( {cond_java} ) {{\n", pad));
```

Apply the same pattern to `While` and `DoWhile` condition rendering.

- [ ] **Step 3: Build, verify, commit**

```bash
cd tools/ta_codegen && cargo build && cargo run -- generate-servers --backend=java
cd ../../ta_codegen_output/java && javac -Xmaxerrs 10000 TaCodegenServe.java 2>&1 | grep "cannot be converted to boolean" | wc -l
```

Expected: 0.

```bash
cd tools/ta_codegen && cargo test --lib --test backend_suite 2>&1 | tail -5
git add src/backends/java.rs && git commit -m "fix(java): wrap int conditions with != 0 for Java boolean requirement"
```

---

### Task 7: Simplify Boolean Ternaries in Candlestick Patterns (~72 errors)

Candlestick macros produce `(comparison) ? (1) : (0)` → int, which then fails in `&&` chains.

**Files:**
- Modify: `tools/ta_codegen/src/backends/java.rs` (Expr::Ternary arm in `render_expr`)
- Reference: `src/tools/gen_code/gen_code.c` (search for `TA_CANDLEGAPUP` Java handling)

- [ ] **Step 1: Detect and simplify boolean ternaries**

Find the `Expr::Ternary` arm in `render_expr`. Add detection for the `(cond) ? (1) : (0)` pattern:

```rust
Expr::Ternary(cond, then_expr, else_expr) => {
    // Detect boolean ternary: (cond) ? (1) : (0) → just render the condition
    if is_int_literal(then_expr, 1) && is_int_literal(else_expr, 0) {
        return render_expr(cond, single_precision, registry, helpers, address_of_vars);
    }
    // Detect direction ternary: (cond) ? (1) : (-1) → keep as ternary
    // (these produce int values, not booleans, so they're correct as-is)

    // Default: render normally
    format!(
        "(({}) ? ({}) : ({}))",
        render_expr(cond, single_precision, registry, helpers, address_of_vars),
        render_expr(then_expr, single_precision, registry, helpers, address_of_vars),
        render_expr(else_expr, single_precision, registry, helpers, address_of_vars),
    )
}
```

Add the `is_int_literal` helper:

```rust
fn is_int_literal(expr: &Expr, value: i64) -> bool {
    matches!(expr, Expr::IntLiteral(v) if *v == value)
}
```

**Important:** Also detect `(cond) ? (0) : (1)` which is `!cond`, and `(cond) ? (-1) : (1)` / `(cond) ? (1) : (-1)` which are directional and should remain as-is. The `(0-1)` pattern may appear as `BinOp(IntLiteral(0), Sub, IntLiteral(1))` — check what the IR actually produces.

- [ ] **Step 2: Build, verify, commit**

```bash
cd tools/ta_codegen && cargo build && cargo run -- generate-servers --backend=java
cd ../../ta_codegen_output/java && javac -Xmaxerrs 10000 TaCodegenServe.java 2>&1 | grep "bad operand" | wc -l
```

Expected: 0.

```bash
cd tools/ta_codegen && cargo test --lib --test backend_suite 2>&1 | tail -5
git add src/backends/java.rs && git commit -m "fix(java): simplify boolean ternaries — strip (cond)?1:0 wrappers from candlestick patterns"
```

---

### Task 8: Replace Float[]/Double[] Aliasing Checks with false (~36 errors)

Float-precision overloads compare `float[]` to `double[]` for aliasing. This is always false in Java.

**Files:**
- Modify: `tools/ta_codegen/src/backends/java.rs` (BinOp Eq/NotEq in `render_expr`, or at the `gen_func` level)
- Reference: existing `Core.java` float overloads

- [ ] **Step 1: Investigate the pattern**

Check the generated output to see the exact aliasing check pattern. Look at a BBANDS float variant:

```bash
grep -n "inReal==out\|inReal == out" ta_codegen_output/java/TaCodegenServe.java | head -5
```

Determine whether this is best fixed by:
(a) Detecting the comparison in `render_expr` based on parameter types
(b) Skipping the entire if-block in the float variant at the `gen_func` level
(c) Hard-coding `false` when comparing arrays of different base types

- [ ] **Step 2: Implement the fix**

The simplest approach: in `gen_func` for float-precision variants, when the IR has `BinOp(Var(a), Eq, Var(b))` where one is a float input and the other is a double output, render `false`. This requires threading the `single_precision` flag into the comparison logic.

Alternatively, in `render_expr`'s `BinOp(Eq)` arm, when `single_precision` is true and both operands are array-typed Var references, emit `false`.

- [ ] **Step 3: Build, verify, commit**

```bash
cd tools/ta_codegen && cargo build && cargo run -- generate-servers --backend=java
cd ../../ta_codegen_output/java && javac -Xmaxerrs 10000 TaCodegenServe.java 2>&1 | grep "incomparable types" | wc -l
```

Expected: 0.

```bash
cd tools/ta_codegen && cargo test --lib --test backend_suite 2>&1 | tail -5
git add src/backends/java.rs && git commit -m "fix(java): replace float/double array aliasing checks with false in float variants"
```

---

## Chunk 3: Variable Type Inference & Final Fixes (~93 errors)

### Task 9: Fix MAType Variable Type Inference (~8 errors)

`tempMAType` is declared as `int` but assigned from `MAType` params.

**Files:**
- Modify: `tools/ta_codegen/src/backends/java.rs` (gen_func VarDecl loop, or add a pre-scan)
- Reference: `src/tools/gen_code/gen_code.c` and existing `Core.java` for `macdext`

- [ ] **Step 1: Research the pattern**

Check `Core_MACDEXT.java` to see how `tempMAType` is used. Determine if a pre-scan approach (like `collect_address_of_vars`) is needed, or if a simpler name-based heuristic works (e.g., any variable whose name contains `MAType` or `maType`).

- [ ] **Step 2: Implement the fix**

Add a pre-scan in `gen_func` that collects variables assigned from enum-typed params. In the VarDecl loop, check if the variable is in the set and override its type to the enum type.

Or simpler: add a heuristic in the VarDecl rendering — if the variable name contains `MAType` (case-insensitive), declare as `MAType` instead of `int`.

- [ ] **Step 3: Build, verify, commit**

```bash
cd tools/ta_codegen && cargo build && cargo run -- generate-servers --backend=java
cd ../../ta_codegen_output/java && javac -Xmaxerrs 10000 TaCodegenServe.java 2>&1 | grep "MAType cannot be converted" | wc -l
```

Expected: 0.

```bash
cd tools/ta_codegen && cargo test --lib --test backend_suite 2>&1 | tail -5
git add src/backends/java.rs && git commit -m "fix(java): declare MAType-assigned variables with enum type instead of int"
```

---

### Task 10: Fix Scalar-as-Array Pointer Pattern (~73 errors)

Functions like ATR pass `double prevATR` via `&prevATR` to `smaLogic` which expects `double[]`. Also causes "double cannot be dereferenced" from `.value` wrapping.

**Files:**
- Modify: `tools/ta_codegen/src/backends/java.rs` (multiple areas)
- Reference: `src/tools/gen_code/gen_code.c` (search for `prevATR` in Java context)
- Reference: existing `Core.java` ATR implementation

- [ ] **Step 1: Research the legacy pattern**

This is the most complex fix. Read:
1. `gen_code.c` — how does `extractTALogic()` handle `prevATR` in Java output?
2. `Core.java` ATR/NATR — what type is `prevATR` declared as?
3. The IR for ATR — how is `prevATR` used (AddressOf, PointerDeref, etc.)?

Determine the correct approach:
- Option A: Declare `double[] prevATR = new double[1]` and use `prevATR[0]` for scalar access
- Option B: Use a different wrapper type
- Option C: Match whatever `Core.java` does

- [ ] **Step 2: Implement the fix**

Based on research, modify the VarDecl rendering and the `collect_address_of_vars` logic:
1. Distinguish between `AddressOf` on `int` (→ MInteger) and `AddressOf` on `double` (→ double[1])
2. For `double` variables in the address-of set: declare as `double[] name = new double[1]` instead of `MInteger`
3. For reads: use `name[0]` instead of `name.value`
4. For AddressOf context: pass `name` directly (the array IS the reference)

- [ ] **Step 3: Build, verify, commit**

```bash
cd tools/ta_codegen && cargo build && cargo run -- generate-servers --backend=java
cd ../../ta_codegen_output/java && javac -Xmaxerrs 10000 TaCodegenServe.java 2>&1 | grep "cannot be dereferenced\|no suitable method found for sma" | wc -l
```

Expected: 0.

```bash
cd tools/ta_codegen && cargo test --lib --test backend_suite 2>&1 | tail -5
git add src/backends/java.rs && git commit -m "fix(java): handle scalar-double-as-array pointer pattern for cross-indicator calls"
```

---

### Task 11: Fix Missing linearreg*Lookback Methods (~12 errors)

Three lookback functions are called but never defined.

**Files:**
- Modify: `tools/ta_codegen/src/server_gen.rs` or `tools/ta_codegen/src/backends/java.rs`
- Reference: `src/tools/gen_code/gen_code.c` (search for `linearreg` lookback)

- [ ] **Step 1: Research the issue**

```bash
grep -n "linearregAngleLookback\|linearregInterceptLookback\|linearregSlopeLookback" ../../ta_codegen_output/java/TaCodegenServe.java | head -10
```

Determine:
1. Where these are called from (which functions?)
2. Are the actual lookback methods generated with different names? (e.g., `linearRegAngleLookback` vs `linearregAngleLookback`)
3. In `Core.java`, are these aliased to `linearregLookback`?

- [ ] **Step 2: Implement the fix**

Based on research — either fix the naming to match generated methods, or add aliases/wrappers.

- [ ] **Step 3: Build, verify, commit**

```bash
cd tools/ta_codegen && cargo build && cargo run -- generate-servers --backend=java
cd ../../ta_codegen_output/java && javac -Xmaxerrs 10000 TaCodegenServe.java 2>&1 | grep "linearreg.*Lookback" | wc -l
```

Expected: 0.

```bash
cd tools/ta_codegen && cargo test --lib --test backend_suite 2>&1 | tail -5
git add src/backends/java.rs src/server_gen.rs && git commit -m "fix(java): resolve linearreg*Lookback method name mismatches"
```

---

### Task 12: Full Compilation Verification

No code changes — verify everything compiles.

**Files:**
- No changes

- [ ] **Step 1: Regenerate and compile**

```bash
cd tools/ta_codegen && cargo run -- generate-servers --backend=java
cd ../../ta_codegen_output/java && javac -Xmaxerrs 10000 TaCodegenServe.java 2>&1 | tail -5
```

Expected: `0 errors` or a small number of residual errors.

- [ ] **Step 2: If errors remain, categorize them**

```bash
javac -Xmaxerrs 10000 TaCodegenServe.java 2>&1 | grep "error:" | sed 's/.*error: //' | sort | uniq -c | sort -rn
```

If any errors remain, they are downstream issues from Fixes 5b or other edge cases. Document them and fix case-by-case.

- [ ] **Step 3: Run all tests**

```bash
cd tools/ta_codegen && cargo test --lib --test backend_suite 2>&1 | tail -5
cargo clippy 2>&1 | grep "java.rs" | wc -l
```

Expected: 63 tests pass, 0 clippy warnings for java.rs.

- [ ] **Step 4: Final commit if any cleanup was needed**

```bash
git add src/backends/java.rs src/server_gen.rs && git commit -m "fix(java): final compilation cleanup — Java server compiles with 0 errors"
```
