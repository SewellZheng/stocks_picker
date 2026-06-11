# Separate Compilation for C Codegen Server

**Date**: 2026-03-19
**Status**: Approved

## Problem

The C codegen server compiles all 163 indicator `.c` files into one translation unit via `#include "ta_*.c"`. This forces the compiler to spread its optimization budget across all functions, resulting in ~1.2x slower code for some indicators (MINMAX, STOCHF) compared to the reference library which compiles each indicator separately.

## Design

### File Changes

**`ta_func_defs/lib/c/ta_lib_types.h`** (template, copied to output)
- Replace `static TA_GlobalsType ta_globals_data = { ... };` and `static TA_GlobalsType *TA_Globals = &ta_globals_data;` with `extern` declarations
- The initializer moves to `ta_lib_globals.c`

**`ta_func_defs/lib/c/ta_lib_globals.c`** (template, copied to output)
- Add `TA_GlobalsType ta_globals_data = { .candleSettings = { ... } };`
- Add `TA_GlobalsType *TA_Globals = &ta_globals_data;`
- Already has `ta_unstable_period[]` and `TA_SetUnstablePeriod()`

**`tools/ta_codegen/src/server_gen.rs`**
- Remove `#include "ta_lib_globals.c"` line (globals now compiled separately)
- Remove all `#include "ta_*.c"` indicator lines
- Add `#include "ta_func.h"` (already has all extern function declarations)
- Update build comment in generated header

**`tools/ta_codegen/src/main.rs`**
- C build step: compile each `.c` file separately with `gcc -c -O3`, then link all `.o` files

### Build Flow (after)

```
ta_codegen generate-servers --backend=c
  -> ta_codegen_output/c/ta_codegen_serve.c  (server dispatch, no indicator includes)
  -> ta_codegen_output/c/ta_*.c              (one per indicator, already exists)
  -> ta_codegen_output/c/ta_func.h           (extern declarations, already exists)
  -> ta_codegen_output/c/ta_lib_types.h      (types + extern globals)
  -> ta_codegen_output/c/ta_lib_globals.c    (globals definitions)

ta_codegen build --backend=c
  -> gcc -c -O3 -I. ta_lib_globals.c
  -> gcc -c -O3 -I. ta_ACCBANDS.c
  -> gcc -c -O3 -I. ta_ACOS.c
  -> ... (163 indicators)
  -> gcc -c -O3 -I. ta_codegen_serve.c
  -> gcc -o ta_codegen_serve_c *.o -lm
```

### What Doesn't Change

- Java, .NET, Rust backends (already separate files)
- Generated indicator `.c` files (already self-contained, include `ta_func.h`)
- `ta_func.h` (already has all extern declarations)
- `ta_regtest`, `ta_bench` (unchanged consumers)
- Shared library unity build (`build_shared_lib` / `ta_codegen_funcs.c`) — still uses `#include` for the `.c` files, which works fine with extern globals since `ta_lib_globals.c` is also included and provides the definitions within that TU

### Verification

- `ta_regtest --codegen --language=c` passes all 163 indicators
- `ta_bench` shows MINMAX and STOCHF at parity with C-ref
