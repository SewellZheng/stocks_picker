# Function Variant Refactor — Design

## Problem

The current system has too many variant names (`_Logic`, `_unchecked`, `_unguarded_unchecked`, `TA_INT_`) and unclear ownership of validation vs pre-compute. The source files reference internal symbols (`TA_INT_EMA`, `ema_unguarded`) that are output concerns, not algorithm concerns.

## Design

### Source files are always unguarded

A source `.c` file in `ta_func_defs/` contains ONLY the algorithm. No validation, no `TA_INT_` prefixes, no `_unguarded` suffixes. Cross-indicator calls use plain names (`TA_EMA`) which the codegen resolves to the unguarded variant in output.

### Two cases

**Case 1 — Simple (most indicators):** Source has ONE function.

```c
// ta_func_defs/sma/sma.c
TA_RetCode sma(startIdx, endIdx, inReal, optInTimePeriod, outBegIdx, outNBElement, outReal) {
    // pure algorithm
}
```

Codegen generates 2 functions per language:
- `TA_SMA` / `core.sma()` — validation prepended, algorithm inlined
- `TA_SMA_Unguarded` / `core.sma_unguarded()` — algorithm as-is, unsafe/unchecked

**Case 2 — Pre-compute (EMA, MACD):** Source has TWO functions. The first is a pre-compute wrapper, the second is the core algorithm with an extra parameter.

```c
// ta_func_defs/ema/ema.c
TA_RetCode ema(startIdx, endIdx, inReal, optInTimePeriod, outBegIdx, outNBElement, outReal) {
    double k = 2.0 / (optInTimePeriod + 1.0);
    // calls the core below with k
}

TA_RetCode ema_core(startIdx, endIdx, inReal, optInTimePeriod, k, outBegIdx, outNBElement, outReal) {
    // pure algorithm using pre-computed k
}
```

Codegen generates 2 functions:
- `TA_EMA` / `core.ema()` — validation + pre-compute wrapper with core inlined
- `TA_EMA_Unguarded` / `core.ema_unguarded()` — the core algorithm (extra k param)

### Cross-indicator calls

In source files, `TA_EMA(...)` always means the unguarded variant. The caller provides all params including pre-computed ones:

```c
// ta_func_defs/macd/macd.c
double slowK = 2.0 / (optInSlowPeriod + 1.0);
retCode = TA_EMA(startIdx, endIdx, inReal, optInSlowPeriod, slowK, outBegIdx, outNBElement, slowEMABuffer);
```

The pre-compute moves to the caller. The codegen maps `TA_EMA` to the unguarded symbol in output.

### Inlining, not calling

The guarded variant INLINES the algorithm — it doesn't call the unguarded variant. This means:
- No function call overhead between guarded and unguarded
- The compiler sees one function body, can optimize freely
- No symbol dependency between the two output functions

### Single-precision (C only)

C gets 2 additional functions: `TA_S_SMA` and `TA_S_SMA_Unguarded` with `float` inputs. Same inlining pattern.

### Rust specifics

- `sma_unguarded` uses `unsafe { get_unchecked() }` — no bounds checks
- `sma` uses safe indexing with bounds checks
- No `_unchecked` or `_unguarded_unchecked` variants — `_unguarded` IS unchecked

### Naming

| Context | Current | New |
|---------|---------|-----|
| C guarded | `TA_SMA` | `TA_SMA` (unchanged) |
| C unguarded | `TA_SMA_Logic` / `TA_INT_SMA` | `TA_SMA_Unguarded` |
| Rust guarded | `core.sma()` | `core.sma()` (unchanged) |
| Rust unguarded | `core.sma_unguarded()` | `core.sma_unguarded()` (unchanged) |
| Java guarded | `core.sma()` | `core.sma()` (unchanged) |
| Java unguarded | N/A | `core.smaUnguarded()` |
| Source cross-call | `TA_INT_EMA` | `TA_EMA` (plain name = unguarded) |

## Files affected

### Source files (ta_func_defs/)
- Remove `TA_INT_` prefixes from cross-indicator calls
- Rename explicit `_unguarded` functions to `_core` (or similar)
- Move pre-compute logic to callers where needed (MACD calling EMA with k)

### Parser (tools/ta_codegen/src/parser/)
- Detect case 1 vs case 2 (single function vs wrapper + core)
- Parse extra params from the core function signature

### IR (tools/ta_codegen/src/ir.rs)
- `FuncDef.unguarded_extra_params` already exists — verify it captures case 2
- `FuncDef.has_explicit_unguarded` — rename/clarify

### C backend (backends/c.rs)
- Rename `_Logic` to `_Unguarded` in output
- Change `#define TA_INT_X TA_X_Logic` to `#define TA_INT_X TA_X_Unguarded` (or remove TA_INT_ entirely)
- Inline the algorithm body instead of calling the unguarded function from guarded

### Rust backend (backends/rust_lang.rs)
- Already generates 2 variants with correct naming
- Verify `_unguarded` uses `get_unchecked` everywhere (it does)

### Java backend (backends/java.rs)
- Add `_unguarded` / `Unguarded` variant generation
- Port `&&` split optimization from C backend
- Port candle function skip-hoisting from C backend

### Server generation (server_gen.rs)
- All perf test servers call unguarded variants
- Split Java handleRequest into per-function methods (JIT fix)
- Fix .NET timing overflow (already done)

## Migration strategy

1. Start with naming: rename `_Logic` → `_Unguarded` in C output, update `TA_INT_` macro
2. Add unguarded variants to Java server
3. Port `&&` split to Java and Rust backends
4. Refactor source files to remove `TA_INT_` prefixes
5. Add guarded/unguarded columns to benchmark output
