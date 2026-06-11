# Two-Variant System — Design Spec

## Goal

Replace the 4-variant codegen system (guarded/unguarded x checked/unchecked) with 2 variants per indicator. Enable unguarded variants to accept extra optimization parameters (e.g., EMA's k factor). Unblock MACDFIX.

## Architecture

The codegen parses C source files from `ta_func_defs/`, builds IR, and renders per-backend. This refactor changes how variants are parsed, represented in IR, and rendered — but doesn't change the overall pipeline.

**Hand-written per language (not generated):** RetCode, FuncUnstId, MAType, Compatibility, CandleSetting, Core struct scaffolding. These are stable and stay as-is in server templates and module scaffolding.

## Variant System

Each indicator produces exactly 2 variants:

| Variant | Validation | Rust Safety | Extra Params |
|---------|-----------|-------------|-------------|
| `foo` | Range checks | Bounds-checked | Public API only |
| `foo_unguarded` | None | `get_unchecked` | Can accept extra params |

- Cross-indicator calls always target `_unguarded`
- Guarded validates, pre-computes optimization values, delegates to unguarded
- In Rust, unguarded uses `unsafe` blocks internally with `get_unchecked`/`get_unchecked_mut` for all array access (the function signature is `pub fn`, not `pub unsafe fn` — the unsafety is contained)

## C Source Convention

Each `.c` file in `ta_func_defs/<name>/` can define up to 3 functions:

```c
// Lookback (unchanged)
int ema_lookback(int optInTimePeriod) { ... }

// Guarded — validates, pre-computes, delegates to unguarded
TA_RetCode ema(int startIdx, int endIdx, const double *inReal,
               int optInTimePeriod,
               int *outBegIdx, int *outNBElement, double *outReal)
{
    // range checks injected by codegen (not written here)
    double k = 2.0 / ((double)(optInTimePeriod + 1));
    return ema_unguarded(startIdx, endIdx, inReal, optInTimePeriod, k,
                         outBegIdx, outNBElement, outReal);
}

// Unguarded — no validation, extra params, unchecked in Rust
TA_RetCode ema_unguarded(int startIdx, int endIdx, const double *inReal,
                         int optInTimePeriod, double optInK_1,
                         int *outBegIdx, int *outNBElement, double *outReal)
{
    // actual computation using optInK_1
}
```

**This is a rewrite of `ema.c`** — the current monolithic function gets split into guarded (thin wrapper) + unguarded (the algorithm). Only indicators that need extra internal params require this manual split.

**Auto-generation:** If only `foo()` is defined (no explicit `foo_unguarded()`), the codegen auto-generates the unguarded variant by:
1. Copying the guarded body
2. Stripping range-check blocks (the `if(startIdx < 0) return OUT_OF_RANGE` pattern and `optIn*` default/validation blocks)
3. Rendering with `get_unchecked` in Rust

The vast majority of indicators need zero changes — only indicators with extra internal params get explicit unguarded functions.

## Files Requiring Manual Changes

These indicators need explicit guarded + unguarded splits because they use extra internal params or call other indicators with extra params:

| File | Change |
|------|--------|
| `ta_func_defs/ema/ema.c` | Split: guarded pre-computes k, unguarded accepts k param |
| `ta_func_defs/macd/macd.c` | Call `ema_unguarded` with explicit k (0.075/0.15 for fix case) |
| `ta_func_defs/macdfix/macdfix.c` | Calls `macd` which calls `ema_unguarded` — no direct changes needed |

Other indicators that call `ema()` internally (DEMA, TEMA, TRIX, T3, MA, MACDEXT, etc.) can continue calling the guarded `ema()` unless they need to override k. The guarded wrapper handles the pre-computation transparently.

All remaining ~155 indicators use auto-generated unguarded variants with no source changes.

## Parser Changes

The current parser (`c_source.rs`) detects function definitions by return type + name + parameter list + opening brace. This detection logic is unchanged. New behavior:

- Scan `.c` file for all function definitions
- `foo_lookback(...)` → lookback function (unchanged)
- `foo(...)` → guarded variant
- `foo_unguarded(...)` → unguarded variant with its own parameter list
- If no `_unguarded` found → flag for auto-generation

## IR Changes

`FuncDef` currently has `body: Vec<Statement>` and parameters spread across `inputs`, `optional_inputs`, `outputs`. Changes:

- Rename `body` → `guarded_body` (from `foo()`)
- Add `unguarded_body: Vec<Statement>` (from `foo_unguarded()` or auto-derived)
- Add `unguarded_params: Vec<Param>` — full parameter list of the unguarded variant
- Add `auto_unguarded: bool` — true when unguarded was auto-generated
- `extra_params` (the params in unguarded but not in guarded) are computed on-the-fly by diffing `unguarded_params` against the guarded param list. Not stored separately.

## Backend Rendering

Each backend emits 2 functions per indicator instead of 4:

**Guarded (`foo`):**
- Renders `guarded_body` with bounds-checked array access
- C: `TA_FOO(...)` with range validation
- Rust: `pub fn foo(&self, ...)` with bounds-checked indexing
- Java: `public RetCode foo(...)` with range validation
- .NET and SWIG follow the same pattern as their current guarded variants

**Unguarded (`foo_unguarded`):**
- Renders `unguarded_body` with `unguarded_params`
- C: `TA_INT_FOO(...)` — no range checks (matches existing `TA_INT_*` convention)
- Rust: `pub fn foo_unguarded(&self, ...)` — contains `unsafe {}` blocks for `get_unchecked` array access
- Java: `public RetCode fooLogic(...)` — no range checks (matches existing `*Logic` convention)
- .NET and SWIG follow the same pattern as their current unguarded/Logic variants

**Cross-indicator call resolution:**
- When `macd.c` calls `ema_unguarded(period, k)`, backends resolve to the target's unguarded variant
- `render_func_call` checks for `_unguarded` suffix on the callee name
- Looks up the target function's `unguarded_params` to map arguments correctly (including extra params)
- This extends the existing cross-indicator resolution — currently all cross-indicator calls already map to the Logic/unguarded variant, this just adds support for extra params in the target signature

## What Gets Dropped

- `_unchecked` variant (folded into `_unguarded`)
- `_unguarded_unchecked` variant (redundant)
- `unchecked` context tracking in `RustRenderCtx`
- ~50% of per-function generated Rust code (other backends already had ~2 variants)

## MACDFIX Fix (Validation)

**Current failure:** MACDFIX calls MACD with period=0 as a sentinel. MACD defaults 0→26/12 and calls `ema(period)`, which computes `k = 2/(period+1)`. This gives k=0.074074 for period 26, but the original algorithm uses a hardcoded k=0.075. The 0.001 difference accumulates over 219 data points → 2.19e-02 mismatch vs C reference.

**Fix:** Once the 2-variant system is in place:

**`ema.c`** gets split into guarded + unguarded with k param (as shown in C Source Convention).

**`macd.c`** calls `ema_unguarded` with explicit k:
```c
if( optInSlowPeriod == 0 ) { optInSlowPeriod = 26; slowK = 0.075; }
else { slowK = 2.0 / ((double)(optInSlowPeriod + 1)); }

retCode = ema_unguarded(tempInteger, endIdx, inReal,
                        optInSlowPeriod, slowK,
                        &outBegIdx1, &outNbElement1, slowEMABuffer);
```

## Generate vs Generate-Servers

- `generate` produces indicator functions as standalone library code (all backends)
- `generate-servers` wraps those with JSON-RPC server layer
- Servers import the generated library — they don't embed indicator code

## Success Criteria

- `make regtest` passes all 4 languages — C, Java, .NET, Rust — including MACDFIX (163/163 per language, 652 total)
- Generated Rust code per indicator is ~50% smaller (2 variants, not 4). Other backends see smaller reduction since they already had ~2 variants.
- EMA's k parameter flows through all backends correctly
- At most ~5 indicators need manual source changes (EMA, MACD, and any others needing extra params). All others use auto-generated unguarded.
- Cross-indicator calls with extra params work end-to-end

## Risks

- **Auto-generation range-check stripping**: Needs reliable identification of validation vs logic. Current patterns are consistent (`if(startIdx < 0) return OUT_OF_RANGE`, `optIn* == TA_*_DEFAULT` blocks). If edge cases appear, developer writes explicit unguarded. Mitigation: validate auto-generation on a representative sample (SMA, RSI, BBANDS, CDL patterns) before committing to the approach.
- **Cross-indicator param resolution**: When `macd.c` calls `ema_unguarded(period, k)`, the parser must resolve that to a different function's unguarded variant with a different parameter list. This extends the existing cross-indicator call handling but adds parameter mapping across function boundaries. Mitigation: test with the EMA→MACD chain first.
- **Backend complexity**: Each backend's `render_func_call` needs to handle extra params on unguarded calls. This is a targeted extension of existing logic, not a rewrite.
- **Breaking change**: `_unchecked` and `_unguarded_unchecked` public API variants are removed. This is acceptable since the generated code is not yet in a stable release and the Rust crate is internal to `ta_codegen_output/`.
