# Codegen Backend Fixes — Design Spec

## Problem

`ta_codegen generate` produces per-function source files for all TA-Lib indicators, but only 6 compile (SMA, MULT, RSI, EMA, WMA, MA). The remaining fail across backends (C, Java, SWIG) due to unresolved macros, broken for-loop rendering, missing cross-function linkage, and SWIG naming mismatches.

There are 164 directories in `ta_func_defs/`. The 6 working functions already contain some macros (e.g., `TA_IS_ZERO`, `CAST_TO_INDEX`, `TA_GetUnstablePeriod`) — these work because the parser resolves them to proper IR nodes. The remaining ~158 files contain macros the parser either discards or doesn't recognize.

## Root Causes

### 1. Unresolved macros in extracted source

The `extract` command pulls logic from `src/ta_func/ta_*.c` into `ta_func_defs/<name>/<name>.c`. These extracted files preserve macro invocations. The parser handles some macros correctly (converting them to useful IR), but others are either parsed with args discarded or not recognized at all.

**Cross-language macros the parser currently resolves (still need removal):**

These macros exist solely to enable cross-language compilation in the legacy `gen_code` pipeline. Even though the parser handles them today, they should be replaced with plain C to simplify the source files and eliminate parser complexity.

| Macro | Plain C replacement |
|-------|-------------------|
| `ENUM_DECLARATION(RetCode) retCode;` | `TA_RetCode retCode;` |
| `ARRAY_REF(buf)` | `double *buf;` |
| `CONSTANT_DOUBLE(name) = value;` | `const double name = value;` |
| `CONSTANT_INTEGER(name) = value;` | `const int name = value;` |
| `TA_IS_ZERO(x)` | `((-0.00000001 < x) && (x < 0.00000001))` |
| `CAST_TO_INDEX(v)` | `(int)(v)` |
| `CAST_TO_I32(v)` | `(int)(v)` |
| `CAST_TO_F64(v)` | `(double)(v)` |
| `UNUSED_VARIABLE(x)` | `(void)x;` |
| `TA_GetUnstablePeriod(X)` | `TA_GetUnstablePeriod(TA_FUNC_UNST_##X)` (plain function call) |
| `TA_GetCompatibility()` | `TA_GetCompatibility()` (already plain C — just ensure parser handles it) |

**Reusability macros — consider helper files:**

These macros exist for code reuse across multiple indicators, not cross-language switching. They should be pulled into a shared helper file that the parser processes separately, or inline-expanded in each extracted `.c` file.

| Macro | Used in | Strategy |
|-------|---------|----------|
| `HILBERT_VARIABLES(prefix)` | 6 HT_* files | Shared helper (declares ~20 vars per prefix) |
| `INIT_HILBERT_VARIABLES(...)` | 6 HT_* files | Shared helper |
| `DO_HILBERT_ODD(...)` / `DO_HILBERT_EVEN(...)` | 6 HT_* files | Shared helper |
| `DO_PRICE_WMA(...)` | 6 HT_* files | Shared helper |
| `TRUE_RANGE(TH,TL,YC,OUT)` | adx, adxr, dx, minus_di, plus_di, trange | Inline-expand (simple block) |
| `SAR_ROUNDING(v)` | sar, sarext | Inline-expand |
| `CIRCBUF_REF(arr[idx])field` | mfi, cci | Inline-expand to `arr[idx].field` |

For the Hilbert transform family: these 4 macros are used identically across 6 files (`ht_trendmode`, `ht_dcperiod`, `ht_dcphase`, `ht_phasor`, `ht_sine`, `ht_trendline`). Best approach: extract into a `ta_func_defs/_helpers/hilbert.c` shared helper that gets parsed once and included in each HT function's IR.

**Macros the parser recognizes but discards args (19 files, ~113 occurrences):**

| Macro | Occurrences | Parser behavior |
|-------|-------------|----------------|
| `ARRAY_ALLOC(buf, size)` | 21 across 16 files | Args discarded, emits `FuncCall("ARRAY_ALLOC", [])` |
| `ARRAY_FREE(buf)` | 54 across 19 files | Args discarded |
| `CIRCBUF_PROLOG(buf, type, size)` | 5 across 4 HT files | Args discarded |
| `CIRCBUF_PROLOG_CLASS(buf, type, size)` | 1 (mfi.c) | Args discarded |
| `CIRCBUF_INIT_CLASS(buf, type, size)` | 1 (mfi.c) | Args discarded |
| `CIRCBUF_DESTROY(buf)` | 3 across 2 files (mfi, cci) | Args discarded |
| `CIRCBUF_NEXT(buf)` | 9 across 6 files | Args discarded |

**Macros the parser does NOT recognize (will crash or produce bad IR):**

| Macro | Occurrences | Files |
|-------|-------------|-------|
| `ARRAY_FREE_COND(flag, buf)` | 8 | stoch, stochf, dema |
| `TA_ARRAY_COPY(dst, dstOff, src, srcOff, n)` | 6 | rsi, stoch, macd, macdext, stochf, cmo |
| `CIRCBUF_INIT_LOCAL_ONLY(buf, type)` | 4 | ht_trendmode, ht_sine, ht_dcphase, ht_trendline |
| `ALLOC_ERR` (bare identifier) | 15 across 10 files | Used in `return ALLOC_ERR;` |
| `VALUE_HANDLE_INT(x)` | varies | Output handle declaration |
| `VALUE_HANDLE_GET(x)` | varies | Output handle dereference |
| `VALUE_HANDLE_OUT(x)` | varies | Output handle address-of |
| `FOR_EACH_OUTPUT(s, e, i, o)` / `FOR_EACH_OUTPUT_END(o)` | 0 | Not present in extracted files |

Note: `VALUE_HANDLE_*` macros appear in ~12 files with ~101 total occurrences. `FOR_EACH_OUTPUT` does NOT appear in extracted files (already expanded during extraction).

**Local `#define` macros inside function bodies:**

Several extracted files contain local `#define` macros:
- `ht_trendmode.c:54`: `#define SMOOTH_PRICE_SIZE 50`
- `adx.c`: `#define TRUE_RANGE(TH,TL,YC,OUT) { ... }`
- `ultosc.c`: `#define CALC_TERMS(day) ...`
- Various: `#define round_pos(x) (x)`

The parser's `strip_local_macros()` removes these `#define` lines, but function bodies still reference the macro names. For simple constants (`SMOOTH_PRICE_SIZE`), these become `Expr::Var` nodes. For function-like macros (`TRUE_RANGE`, `CALC_TERMS`), the ALL_CAPS handler catches them as `FuncCall` nodes with args preserved — the backends then need to handle them.

**Standard library function wrappers:**

Functions like `std_atan`, `std_sqrt`, `std_fabs` appear in ~30 files (109 occurrences). These need to map to the standard math library for each language.

### 2. Broken for-loop rendering

C-style `for(j=0, i=start; cond; i++, j++)` parses into `ForC { init: Block([j=0, i=start]), ... }`. The C and Java backends render blocks with newlines and semicolons, producing:

```c
for( j = 0;
i = (startIdx-lookbackTotal); (i<=endIdx); i += 1;
j += 1 )
```

Init and update blocks need comma-separated expressions, not semicolon-terminated statements.

Affected backends: `c.rs`, `java.rs`. The `.NET` backend only generates declarations (signatures, macro wrappers) — it does not render function bodies, so ForC rendering does not apply.

### 3. Missing cross-function forward declarations

Generated files are standalone. When ACCBANDS calls `TA_INT_SMA`, there's no declaration in scope. The C server uses a unity build (`#include` all files), but alphabetical ordering means ACCBANDS is included before SMA.

**Internal helper functions**: Some indicators call helper functions that aren't registered indicators. For example, `bbands.c:75` calls `stddev_using_precalc_ma(...)`, which is defined inside `stddev.c` as an internal helper with no Registry entry. The forward declaration strategy must also cover these unregistered helpers.

### 4. SWIG naming mismatch

The SWIG backend generates both `TA_SMA(...)` and `TA_SMA_Logic(...)` declarations, and the C backend defines `#define TA_INT_SMA TA_SMA_Logic`. The SWIG `.i` interface file must link against the correct exported symbol. The naming needs to be consistent between the SWIG interface and the C unity build exports.

## Approach

### Replace macros with plain C in extracted source files

The macros exist for the legacy `gen_code.c` pipeline which needed them to switch behavior per language. The new ta_codegen pipeline parses source into an IR and renders per-language — macros are vestigial.

**Fix the extracted `.c` files in `ta_func_defs/`** by replacing macros with their plain C equivalents. The parser and backends already handle plain C correctly — the 6 base functions prove this.

Important: we modify ONLY the `ta_func_defs/<name>/<name>.c` files. The old `src/ta_func/ta_*.c` files stay untouched for the legacy `gen_code` pipeline.

#### Which macros need replacement

ALL macros that exist for cross-language compilation get replaced with plain C — even ones the parser currently handles. This simplifies the source files and reduces parser complexity. Reusability macros (Hilbert helpers, TRUE_RANGE, etc.) get either inline-expanded or pulled into shared helper files.

#### Macro → Plain C mapping

**Array allocation/deallocation (~16 files):**

| Macro | Plain C equivalent |
|-------|-------------------|
| `ARRAY_ALLOC(buf, size)` | `double *buf = malloc(size * sizeof(double)); if (!buf) return TA_ALLOC_ERR;` |
| `ARRAY_FREE(buf)` | `free(buf);` |
| `ARRAY_FREE_COND(flag, buf)` | `if (flag) { free(buf); }` |
| `TA_ARRAY_COPY(dst, dstOff, src, srcOff, n)` | `memcpy(&dst[dstOff], &src[srcOff], n * sizeof(double));` |
| `ALLOC_ERR` | `TA_ALLOC_ERR` |

**Value handle macros (~12 files, ~101 occurrences):**

| Macro | Plain C equivalent |
|-------|-------------------|
| `VALUE_HANDLE_INT(x)` | `int x;` |
| `VALUE_HANDLE_GET(x)` | `x` (plain variable read) |
| `VALUE_HANDLE_OUT(x)` | `&x` (address-of for pointer params) |

**Circular buffer macros (~6 files):**

The CIRCBUF family has multiple variants. MFI uses class-based circular buffers with a custom `MoneyFlow` struct type. The HT_* (Hilbert Transform) functions use local-only double arrays.

| Macro | Plain C equivalent |
|-------|-------------------|
| `CIRCBUF_PROLOG(buf, type, staticSize)` | `type buf[staticSize]; int buf_Idx = 0;` |
| `CIRCBUF_PROLOG_CLASS(buf, Type, staticSize)` | `Type buf[staticSize]; int buf_Idx = 0;` |
| `CIRCBUF_INIT_LOCAL_ONLY(buf, type)` | *(no-op — declaration already handled by PROLOG)* |
| `CIRCBUF_INIT_CLASS(buf, Type, size)` | `memset(buf, 0, size * sizeof(Type)); buf_Idx = 0;` |
| `CIRCBUF_NEXT(buf)` | `buf_Idx = (buf_Idx + 1) % bufSize;` (bufSize from context) |
| `CIRCBUF_DESTROY(buf)` | *(no-op for stack-allocated buffers)* |

Note: `CIRCBUF_REF(arr[idx])field` should be inline-expanded to `arr[idx].field` in the source (see reusability macros table above).

**Local `#define` macros:**

For simple constants (e.g., `#define SMOOTH_PRICE_SIZE 50`), replace with `const int SMOOTH_PRICE_SIZE = 50;`.

For function-like local macros (`TRUE_RANGE`, `CALC_TERMS`, etc.), inline-expand them in the extracted `.c` files — replace the `#define` and all call sites with the equivalent plain C block. For complex shared macros (Hilbert transform family), extract into shared helper files (see reusability macros table in Root Causes §1).

**Standard library wrappers:**

| Macro/Function | Plain C equivalent |
|-------|-------------------|
| `std_atan(x)` | `atan(x)` |
| `std_sqrt(x)` | `sqrt(x)` |
| `std_fabs(x)` | `fabs(x)` |
| `std_floor(x)` | `floor(x)` |
| `std_ceil(x)` | `ceil(x)` |
| `std_log(x)` | `log(x)` |

Each backend then renders these plain C patterns idiomatically:
- C: `malloc`/`free`, `memcpy`, `atan`
- Java: `new double[size]` (GC handles free), `System.arraycopy`, `Math.atan`
- Rust: `vec![T::zero(); size]`, `.copy_from_slice()`, `T::atan()`
- SWIG/Python: same as C (compiled to native)

### Fix for-loop rendering in backends

When rendering a `ForC` init or update that is a `Block` with multiple statements, join them with `, ` (comma) instead of `;\n` (semicolon-newline).

Affected backends: `c.rs`, `java.rs`.

**Rust backend improvement**: When a `ForC` has a single counter with `<=` condition, emit `for i in start..=end` range iteration instead of the current `init; while cond { body; update; }` lowering. The `While` → range pattern at `rust_lang.rs:911` already does this — extend it to `ForC`.

### Cross-function forward declarations

Generate a forward declaration block in the `ta_func.h` stub from the Registry. Every registered function gets a prototype:

```c
extern int TA_SMA_Lookback(int optInTimePeriod);
extern TA_RetCode TA_SMA(int startIdx, int endIdx, const double inReal[], int optInTimePeriod, int *outBegIdx, int *outNBElement, double outReal[]);
```

**Internal helper functions** (e.g., `stddev_using_precalc_ma` called by BBANDS) are not in the Registry. These need either:
- Manual forward declarations in a supplementary header, or
- Extraction into the Registry as internal-only entries, or
- Inlining at the call site in the extracted `.c` file

The simplest approach: scan all extracted `.c` files for cross-function calls to unregistered helpers, and add manual forward declarations to the generated header.

The unity build includes this header first, so all symbols are declared before any function body references them.

### Cross-function call naming per language

When one indicator calls another internally (e.g., ACCBANDS calls SMA), each backend emits the correct name:

| Language | Internal call | Lookback call |
|----------|--------------|---------------|
| C | `TA_INT_SMA(...)` | `TA_SMA_Lookback(...)` |
| Java | `sma(...)` | `smaLookback(...)` |
| Rust | `self.sma_unguarded(...)` | `self.sma_lookback(...)` |
| SWIG | `TA_SMA(...)` | `TA_SMA_Lookback(...)` |

Rust has 4 variants per function:

| Variant | Purpose | Non-Rust equivalent |
|---------|---------|-------------------|
| `sma<T: TaFloat>` | Public API, validates params | `sma()` |
| `sma_unguarded<T>` | Cross-indicator calls, skips validation | `sma()` (same) |
| `sma_unchecked<T>` | Unsafe, `get_unchecked` indexing | `sma()` (same) |
| `sma_unguarded_unchecked<T>` | Internal hot paths | `sma()` (same) |

The `_s()` suffix (old single-precision convention) maps to `sma::<f32>()` in Rust and doesn't exist in other languages.

The Registry + IR already track function names. Each backend's `render_func_call` uses this mapping.

### SWIG naming alignment

SWIG backend exports the same function names as the C backend. Ensure the SWIG `.i` file and the C unity build use matching symbol names. Drop any `_Logic` suffix mismatch.

## Scope

### In scope
- Replace macros with plain C in extracted `.c` files in `ta_func_defs/` (~158 files need changes)
- Fix `ForC` rendering in C, Java backends (comma-separate init/update)
- Improve Rust `ForC` to emit range iteration where possible
- Generate forward declarations in `ta_func.h` stub from Registry
- Handle internal helper functions (forward declarations for unregistered cross-function calls)
- Fix SWIG naming to match C exports
- Verify all functions compile for all backends
- Verify `make regtest` passes for all functions

### Out of scope
- Modifying `src/ta_func/ta_*.c` (legacy `gen_code` pipeline)
- Modifying the legacy `gen_code.c` pipeline
- Adding new indicators
- Performance optimization of generated code (beyond idiomatic patterns)

## Verification

Build and generation must complete before testing:
```bash
make servers          # Generates source + compiles JSON-RPC servers
make regtest          # Full pipeline: servers + C tests + codegen verification
```

Per-backend spot checks (from `bin/` after servers are built):
- C server: `./ta_regtest --codegen-only --language=c`
- Java server: `./ta_regtest --codegen-only --language=java`
- SWIG/Python server: `./ta_regtest --codegen-only --language=swig`

## Key files

| File | Change |
|------|--------|
| `ta_func_defs/*/*.c` | Replace macros with plain C (~158 files) |
| `tools/ta_codegen/src/backends/c.rs` | Fix ForC rendering, forward decls |
| `tools/ta_codegen/src/backends/java.rs` | Fix ForC rendering |
| `tools/ta_codegen/src/backends/rust_lang.rs` | ForC → range iteration |
| `tools/ta_codegen/src/backends/swig.rs` | Fix naming alignment |
| `tools/ta_codegen/src/server_gen.rs` | Forward declarations in `ta_func.h` stub |
| `tools/ta_codegen/src/parser/c_source.rs` | May need updates if new plain C patterns aren't parsed correctly |
