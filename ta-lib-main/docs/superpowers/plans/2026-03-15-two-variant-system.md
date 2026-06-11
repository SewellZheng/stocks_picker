# Two-Variant System Implementation Plan

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace the 4-variant codegen (guarded/unguarded × checked/unchecked) with 2 variants (guarded + unguarded), where unguarded accepts extra optimization params and uses `get_unchecked` in Rust.

**Architecture:** The C source parser learns to recognize `foo_unguarded()` alongside `foo()` in the same file. The IR stores both bodies and parameter lists. Each backend renders 2 variants instead of 4. Cross-indicator calls resolve extra params via the target's unguarded parameter list.

**Tech Stack:** Rust (ta_codegen), C (ta_func_defs sources), CMake (build system)

**Spec:** `docs/superpowers/specs/2026-03-15-two-variant-system-design.md`

---

## Chunk 1: IR + Parser Foundation

### Task 1: Extend FuncDef IR to hold two variants

**Files:**
- Modify: `tools/ta_codegen/src/ir.rs:1-15`

The current `FuncDef` has a single `body: Vec<Statement>`. We need it to hold both guarded and unguarded bodies, plus the unguarded parameter list.

- [ ] **Step 1: Add new fields to FuncDef**

In `tools/ta_codegen/src/ir.rs`, add these fields to the `FuncDef` struct:

```rust
pub struct FuncDef {
    pub name: String,
    pub group: String,
    pub description: String,
    pub camel_case: String,
    pub hint: String,
    pub flags: u32,
    pub inputs: Vec<Input>,
    pub optional_inputs: Vec<OptInput>,
    pub outputs: Vec<Output>,
    pub lookback: Vec<Statement>,
    pub body: Vec<Statement>,             // guarded body (existing, renamed conceptually)
    pub unguarded_body: Vec<Statement>,    // NEW: unguarded body (explicit or auto-derived)
    pub unguarded_extra_params: Vec<(String, String)>,  // NEW: (name, type) pairs for extra params
    pub has_explicit_unguarded: bool,      // NEW: true when source defines foo_unguarded()
}
```

The `unguarded_extra_params` holds the params that appear in the unguarded signature but NOT in the guarded signature. E.g., for EMA: `[("optInK_1", "double")]`.

- [ ] **Step 2: Update FuncDef construction sites**

Search for all places that construct `FuncDef` (in `main.rs` around line 131-137 and anywhere else). Add default values:

```rust
unguarded_body: Vec::new(),
unguarded_extra_params: Vec::new(),
has_explicit_unguarded: false,
```

- [ ] **Step 3: Verify it compiles**

Run: `cd tools/ta_codegen && cargo check`
Expected: Compiles with warnings about unused fields (that's fine)

- [ ] **Step 4: Commit**

```bash
git add tools/ta_codegen/src/ir.rs tools/ta_codegen/src/main.rs
git commit -m "feat(ir): add unguarded body and extra params to FuncDef"
```

---

### Task 2: Parser recognizes foo_unguarded() in C source

**Files:**
- Modify: `tools/ta_codegen/src/parser/c_source.rs`

The parser currently expects one main function per file (plus lookback). It needs to detect `_unguarded` suffix and store it separately.

- [ ] **Step 1: Update ParsedCSource to hold unguarded data**

The parser returns a `ParsedCSource` struct. Find its definition and add:

```rust
pub unguarded_body: Vec<Statement>,
pub unguarded_extra_params: Vec<(String, String)>,  // (name, c_type)
pub has_explicit_unguarded: bool,
```

- [ ] **Step 2: Update parse logic to detect _unguarded functions**

In the main parse loop where function definitions are detected, after finding `foo()` and `foo_lookback()`, also check for `foo_unguarded()`:

```rust
// After parsing all function definitions from the file:
// - foo_lookback → lookback_body
// - foo → body (guarded)
// - foo_unguarded → unguarded_body + extract extra params
```

The extra params are computed by comparing the parameter lists of `foo()` and `foo_unguarded()` — any params in unguarded that aren't in guarded are the extra params.

- [ ] **Step 3: Wire parsed unguarded data into FuncDef construction in main.rs**

In `main.rs` where `FuncDef` is constructed from `ParsedCSource` (~line 131-137), populate the new fields:

```rust
unguarded_body: parsed.unguarded_body,
unguarded_extra_params: parsed.unguarded_extra_params,
has_explicit_unguarded: parsed.has_explicit_unguarded,
```

- [ ] **Step 4: Auto-generate unguarded when not explicit**

After constructing `FuncDef`, if `has_explicit_unguarded` is false, auto-generate:

```rust
if !func_def.has_explicit_unguarded {
    func_def.unguarded_body = strip_range_checks(&func_def.body);
    func_def.unguarded_extra_params = vec![];
}
```

The `strip_range_checks()` function clones the body and removes statements matching the range-check pattern:
- `if(startIdx < 0) return OUT_OF_RANGE_START_INDEX`
- `if(endIdx < 0 || endIdx < startIdx) return OUT_OF_RANGE_END_INDEX`
- `if(optIn* == TA_*_DEFAULT)` default-value assignment blocks
- `if(optIn* < min || optIn* > max) return BAD_PARAM` validation blocks

For the initial implementation, identify these by pattern-matching on the IR `Statement` variants (If statements whose condition references `startIdx < 0` or `endIdx < 0`, and whose body contains a Return with an error code).

- [ ] **Step 5: Verify parsing works with existing indicators**

Run: `cd tools/ta_codegen && cargo run -- generate --backend=c 2>&1 | head -20`
Expected: All indicators generate successfully (none have `_unguarded` yet, so all use auto-generation)

- [ ] **Step 6: Commit**

```bash
git add tools/ta_codegen/src/parser/c_source.rs tools/ta_codegen/src/main.rs
git commit -m "feat(parser): recognize foo_unguarded() with extra params in C source"
```

---

### Task 3: Write EMA with explicit unguarded variant

**Files:**
- Modify: `ta_func_defs/ema/ema.c`

- [ ] **Step 1: Rewrite ema.c with guarded + unguarded split**

```c
int ema_lookback(int optInTimePeriod)
{
    return optInTimePeriod - 1 + TA_GetUnstablePeriod(TA_FUNC_UNST_EMA);
}

TA_RetCode ema(int startIdx, int endIdx, const double *inReal,
               int optInTimePeriod,
               int *outBegIdx, int *outNBElement, double *outReal)
{
    double optInK_1 = 2.0 / ((double)(optInTimePeriod + 1));
    return ema_unguarded(startIdx, endIdx, inReal, optInTimePeriod, optInK_1,
                         outBegIdx, outNBElement, outReal);
}

TA_RetCode ema_unguarded(int startIdx, int endIdx, const double *inReal,
                         int optInTimePeriod, double optInK_1,
                         int *outBegIdx, int *outNBElement, double *outReal)
{
    double tempReal, prevMA;
    int i, today, outIdx, lookbackTotal;

    lookbackTotal = ema_lookback( optInTimePeriod );

    if( startIdx < lookbackTotal )
    startIdx = lookbackTotal;

    if( startIdx > endIdx )
    {
    *outBegIdx = 0;
    *outNBElement = 0;
    return TA_SUCCESS;
    }
    *outBegIdx = startIdx;

    if( TA_GetCompatibility() == TA_COMPATIBILITY_DEFAULT )
    {
    today = startIdx-lookbackTotal;
    i = optInTimePeriod;
    tempReal = 0.0;
    while( i-- > 0 )
    tempReal += inReal[today++];
    prevMA = tempReal / optInTimePeriod;
    }
    else
    {
    prevMA = inReal[0];
    today = 1;
    }

    while( today <= startIdx )
    prevMA = ((inReal[today++]-prevMA)*optInK_1) + prevMA;

    outReal[0] = prevMA;
    outIdx = 1;

    while( today <= endIdx )
    {
    prevMA = ((inReal[today++]-prevMA)*optInK_1) + prevMA;
    outReal[outIdx++] = prevMA;
    }

    *outNBElement = outIdx;

    return TA_SUCCESS;
}
```

- [ ] **Step 2: Verify parser picks up both variants**

Run: `cd tools/ta_codegen && cargo run -- generate --backend=c 2>&1 | grep EMA`
Expected: EMA generates without errors. Check the output file has both `TA_EMA` and `TA_INT_EMA` (or equivalent unguarded name).

- [ ] **Step 3: Commit**

```bash
git add ta_func_defs/ema/ema.c
git commit -m "feat(ema): split into guarded + unguarded with explicit k param"
```

---

### Task 4: Update MACD to call ema_unguarded with explicit k

**Files:**
- Modify: `ta_func_defs/macd/macd.c`

- [ ] **Step 1: Add slowK/fastK variables and compute based on fix case**

Replace the current period-defaulting logic:

```c
// In the variable declarations, add:
double slowK, fastK;

// Replace the period-defaulting block with:
if( optInSlowPeriod == 0 )
{
    optInSlowPeriod = 26;
    slowK = 0.075;
}
else
{
    slowK = 2.0 / ((double)(optInSlowPeriod + 1));
}

if( optInFastPeriod == 0 )
{
    optInFastPeriod = 12;
    fastK = 0.15;
}
else
{
    fastK = 2.0 / ((double)(optInFastPeriod + 1));
}
```

- [ ] **Step 2: Change ema() calls to ema_unguarded() with k**

Replace:
```c
retCode = ema( tempInteger, endIdx,
    inReal, optInSlowPeriod,
    &outBegIdx1, &outNbElement1, slowEMABuffer );
```

With:
```c
retCode = ema_unguarded( tempInteger, endIdx,
    inReal, optInSlowPeriod, slowK,
    &outBegIdx1, &outNbElement1, slowEMABuffer );
```

Same for the fast EMA call (use `fastK`). The signal EMA call can stay as `ema()` since it uses standard k.

- [ ] **Step 3: Commit**

```bash
git add ta_func_defs/macd/macd.c
git commit -m "feat(macd): call ema_unguarded with explicit k for fix case"
```

---

## Chunk 2: Rust Backend — 4→2 Variants

### Task 5: Simplify Rust backend to 2 variants

**Files:**
- Modify: `tools/ta_codegen/src/backends/rust_lang.rs`

This is the largest change. The Rust backend currently generates 4 variants via 4 separate functions. We collapse to 2.

- [ ] **Step 1: Remove gen_unchecked_func()**

Find `gen_unchecked_func()` in `rust_lang.rs` and delete it entirely. This was the guarded+unchecked variant.

- [ ] **Step 2: Update gen_unguarded_func() to always use get_unchecked**

Currently `gen_unguarded_func()` takes a `RustRenderCtx` with `unchecked: bool`. Change it to always use `unchecked: true` internally. Remove the `unchecked` parameter/flag from `RustRenderCtx` — it's always true for unguarded, always false for guarded.

- [ ] **Step 3: Add extra params to unguarded signature**

In `gen_unguarded_func()`, when building the function signature, append the extra params from `func.unguarded_extra_params`:

```rust
for (name, c_type) in &func.unguarded_extra_params {
    let rust_type = match c_type.as_str() {
        "double" => "f64",
        "int" => "i32",
        _ => "f64",
    };
    signature.push_str(&format!(", {}: {}", name, rust_type));
}
```

- [ ] **Step 4: Update generate() to emit only 2 variants**

In the main `generate()` function (lines 72-84), replace the 4 variant calls with 2:

```rust
// 1. Lookback (unchanged)
output.push_str(&gen_lookback(func, ...));

// 2. Guarded variant — bounds-checked, public API params only
output.push_str(&gen_guarded_func(func, ...));

// 3. Unguarded variant — get_unchecked, includes extra params
output.push_str(&gen_unguarded_func(func, ...));
```

- [ ] **Step 5: Render unguarded_body instead of body for unguarded variant**

In `gen_unguarded_func()`, use `func.unguarded_body` instead of `func.body` when rendering statements.

- [ ] **Step 6: Update cross-indicator call rendering for extra params**

In `render_func_call()` (around line 3160+), when resolving a call to `foo_unguarded()`:
- Look up the target function's `unguarded_extra_params`
- Map the extra arguments from the call site to the extra params
- Currently cross-indicator calls use `render_cross_indicator_args()` — extend this to include extra args when the callee has `_unguarded` suffix

- [ ] **Step 7: Update server_gen.rs Rust dispatch**

In `generate_rust_server()` (around line 1979), update the dispatch call from `core.{fn}_unguarded_unchecked(...)` to `core.{fn}_unguarded(...)`.

- [ ] **Step 8: Verify Rust backend compiles and generates**

Run: `cd tools/ta_codegen && cargo check && cargo run -- generate --backend=rust 2>&1 | tail -5`
Expected: All 163 indicators generate. Check a sample output (e.g., `ta_codegen_output/rust/src/ta_func/sma.rs`) has only 2 variants.

- [ ] **Step 9: Build Rust crate**

Run: `cd ta_codegen_output/rust && cargo check 2>&1 | tail -20`
Expected: Compiles (may have warnings, should have 0 errors)

- [ ] **Step 10: Commit**

```bash
git add tools/ta_codegen/src/backends/rust_lang.rs tools/ta_codegen/src/server_gen.rs
git commit -m "feat(rust): collapse 4 variants to 2 (guarded + unguarded with get_unchecked)"
```

---

### Task 6: Update C and Java backends for extra params

**Files:**
- Modify: `tools/ta_codegen/src/backends/c.rs`
- Modify: `tools/ta_codegen/src/backends/java.rs`

C and Java already have guarded + Logic/INT variants. The change is adding extra param support to the unguarded/Logic variant signature and cross-indicator call resolution.

- [ ] **Step 1: C backend — add extra params to Logic variant signature**

In `c.rs` `gen_func()` (around line 30+), when `logic == true`, append extra params from `func.unguarded_extra_params` to the function signature.

- [ ] **Step 2: C backend — render unguarded_body for Logic variant**

When `logic == true`, render `func.unguarded_body` instead of `func.body`.

- [ ] **Step 3: C backend — update render_func_call for _unguarded calls**

When the callee name ends in `_unguarded`, look up the target's extra params and include them in the rendered call.

- [ ] **Step 4: Java backend — same changes as C**

Mirror the C backend changes: extra params on Logic signature, render unguarded_body, handle _unguarded cross-indicator calls.

- [ ] **Step 5: Update server_gen.rs C and Java dispatch**

In `generate_c_server()` and `generate_java_server()`, ensure dispatch calls to Logic/INT variants don't pass extra params (servers always use default k values through the guarded API).

- [ ] **Step 6: Verify C and Java generate correctly**

Run:
```bash
cd tools/ta_codegen
cargo run -- generate --backend=c 2>&1 | tail -5
cargo run -- generate --backend=java 2>&1 | tail -5
```
Expected: All 163 indicators generate for both backends.

- [ ] **Step 7: Commit**

```bash
git add tools/ta_codegen/src/backends/c.rs tools/ta_codegen/src/backends/java.rs tools/ta_codegen/src/server_gen.rs
git commit -m "feat(c,java): support extra params on unguarded/Logic variants"
```

---

## Chunk 3: Build, Test, Validate

### Task 7: Build all servers and run regtest

**Files:**
- Modify: `tools/ta_codegen/src/server_gen.rs` (if needed for dispatch fixes)
- Modify: `tools/ta_codegen/src/backends/dotnet.rs` (if needed)
- Modify: `tools/ta_codegen/src/backends/swig.rs` (if needed)

- [ ] **Step 1: Generate all backends**

Run: `cd tools/ta_codegen && cargo run -- generate`
Expected: All 5 backends generate without errors.

- [ ] **Step 2: Generate servers**

Run: `cd tools/ta_codegen && cargo run -- generate-servers`
Expected: All server files generated.

- [ ] **Step 3: Build servers**

Run: `cd tools/ta_codegen && cargo run -- build`
Expected: C server, Java server, .NET server, Rust server all compile.

- [ ] **Step 4: Run make regtest**

Run: `cd cmake-build && cmake .. -DCMAKE_BUILD_TYPE=Release && make regtest`
Expected: All 4 languages pass (163/163 each), including MACDFIX. Check the summary chart shows 163 pass, 0 fail for each language.

- [ ] **Step 5: Verify MACDFIX specifically**

Run: `cd bin && ./ta_regtest --codegen-only --language=c --function=MACD`
Expected: MACD, MACDEXT, and MACDFIX all show PASS with real timing.

- [ ] **Step 6: Verify EMA unguarded has extra k param in generated output**

Check: `grep 'optInK_1\|k_1' ta_codegen_output/c/ta_EMA.c | head -5`
Expected: The Logic/INT variant signature includes the k parameter.

Check: `grep 'optInK_1\|k_1' ta_codegen_output/rust/src/ta_func/ema.rs | head -5`
Expected: The `ema_unguarded` function signature includes k as f64.

- [ ] **Step 7: Verify variant count reduced in Rust**

Check a simple indicator (SMA) to confirm only 2 variants:
```bash
grep 'pub fn sma' ta_codegen_output/rust/src/ta_func/sma.rs
```
Expected: Only `pub fn sma(` and `pub fn sma_unguarded(` — no `_unchecked` variants.

- [ ] **Step 8: Fix any remaining issues**

If regtest fails on specific indicators, debug and fix. Common issues:
- Auto-generated unguarded stripped too much or too little
- Cross-indicator param mapping incorrect
- Server dispatch using wrong variant name

- [ ] **Step 9: Final commit**

```bash
git add -A
git commit -m "feat: complete two-variant system — 163/163 all languages, MACDFIX fixed"
```

- [ ] **Step 10: Push**

```bash
git push
```
