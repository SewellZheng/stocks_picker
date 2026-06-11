# Universal Cross-Language Regression Testing

## Goal

Make ta_regtest the universal test runner for all TA-Lib language implementations. Every indicator, every language, every variation — tested against the old C reference with timing data and correctness reporting.

## Problem

ta_regtest currently only tests C indicators directly. Codegen servers exist for C, Java, .NET, Python, and Rust, and the pipe/JSON-RPC infrastructure is already built (`codegen_pipe.c/h`, `test_codegen.c`), but only 3 functions (SMA, MULT, RSI) have hand-coded test callbacks. We need to generalize to all 158 indicators automatically.

## Architecture

```
ta_regtest (C binary)
  │
  ├── Existing test suite (test_ma.c, test_rsi.c, etc.)
  │     └── Validates old C reference implementations — unchanged
  │
  └── Codegen verification (test_codegen.c)
        │
        ├── ta_abstract API
        │     ├── Enumerates all functions and their signatures
        │     └── Calls C reference via TA_CallFunc()
        │
        └── Codegen servers (via codegen_pipe)
              ├── ta_codegen_serve_c      (C server)
              ├── ta_codegen serve         (Rust server)
              ├── TaCodegenServe.class     (Java server)
              ├── TaCodegenServe           (.NET server)
              └── ta_codegen_serve.py      (Python/SWIG server)
```

### Flow per indicator per language

1. ta_abstract provides function metadata (inputs, outputs, optional params)
2. Generic callback calls C reference via `TA_CallFunc()` — captures output + timing
3. Same parameters are serialized to JSON-RPC and sent to the language server
4. Server executes the codegen-generated indicator, returns output + `timing_ns`
5. `compare_codegen_output()` validates: retCode, outBegIdx, outNbElement, output values (epsilon 1e-6)
6. `doRangeTest` runs this callback hundreds of times with every startIdx/endIdx combination
7. Results (pass/fail/skip + timing) collected per function per language

## Design

### 1. Generic Codegen Test Callback

Replace the 3 hand-coded callbacks (`codegen_range_sma`, `codegen_range_mult`, `codegen_range_rsi`) with one generic callback that handles any function.

**Generalized `CodegenRangeTestParam`:**

Instead of per-function fields (`optInTimePeriod`, `inReal`/`inReal1`), the struct holds:
- `TA_ParamHolder *params` — from ta_abstract, represents any function's inputs/outputs
- `const TA_FuncInfo *funcInfo` — function metadata (input count, output count, types)
- `TA_Real *allOutputBuffers[3]` — pre-allocated for up to 3 outputs
- `TA_Integer *allOutputIntBuffers[3]` — for integer outputs (candlestick patterns)
- Pipe and JSON buffer fields (unchanged from current)

**Callback flow (one invocation of `doRangeTest`):**

1. `TA_CallFunc(params)` — calls C reference, fills ALL output buffers at once
2. Copy the output for the requested `outputNb` into `outputBuffer` or `outputBufferInt`
3. Set `*lookback` via `TA_GetLookback(params)` (NOT from `TA_CallFunc`)
4. Set `*isOutputInteger` from `TA_OutputParameterInfo.type == TA_Output_Integer`
5. Build JSON-RPC request from `TA_ParamHolder` metadata (see input type handling below)
6. Send to server, `compare_codegen_output()` validates match

**Input type handling for JSON-RPC requests:**

| Input Type | How to detect | JSON serialization |
|-----------|---------------|-------------------|
| `TA_Input_Real` (single) | `TA_InputParameterInfo.type == TA_Input_Real` | `"inReal": [...]` |
| `TA_Input_Real` (multiple) | Count of `TA_Input_Real` params > 1 | `"inReal0": [...], "inReal1": [...]` |
| `TA_Input_Price` | `TA_InputParameterInfo.type == TA_Input_Price` | Named per flag: `"inOpen": [...], "inHigh": [...], "inLow": [...], "inClose": [...], "inVolume": [...]` |

Price input flag mapping (from `TA_InputParameterInfo.flags`):
- `TA_IN_PRICE_OPEN` → `history->open` → `"inOpen"`
- `TA_IN_PRICE_HIGH` → `history->high` → `"inHigh"`
- `TA_IN_PRICE_LOW` → `history->low` → `"inLow"`
- `TA_IN_PRICE_CLOSE` → `history->close` → `"inClose"`
- `TA_IN_PRICE_VOLUME` → `history->volume` → `"inVolume"`
- `TA_IN_PRICE_OPENINTEREST` → `history->openInterest` → `"inOpenInterest"`

Reference: `test_abstract.c` lines 415-421 shows the working flag-to-history mapping.

**Optional parameter handling:**

| Param Type | JSON field | Parsing |
|-----------|-----------|---------|
| `TA_OptInput_IntegerRange` | `"optInTimePeriod": 30` | `json_find_int` |
| `TA_OptInput_RealRange` | `"optInNbDevUp": 2.0` | `json_find_double` (new — servers currently only parse ints) |
| `TA_OptInput_IntegerList` | `"optInMAType": 0` | `json_find_int` |

**Output comparison:**

| Output Type | Comparison | JSON field |
|------------|-----------|------------|
| `TA_Output_Real` (single) | Epsilon 1e-6 | `"outReal": [...]` |
| `TA_Output_Real` (multi) | Epsilon 1e-6, per-output | `"outReal": [...], "outReal1": [...], "outReal2": [...]` |
| `TA_Output_Integer` | Exact match (or tolerance via `TA_DO_NOT_COMPARE`) | `"outInteger": [...]` or `"outInteger0": [...], "outInteger1": [...]` |

**Unstable period propagation:**

For functions with `TA_FUNC_FLG_UNST_PER` flag, include `"unstablePeriod": N` in the JSON request. The generic callback reads the current value via `TA_GetUnstablePeriod(funcInfo->unstId)`. All 24 unstable-period function IDs must be mapped in `server_gen.rs`.

**Runtime enumeration replaces `CODEGEN_TESTS[]`:**

Use `TA_ForEachFunc` (from ta_abstract) to iterate all functions. For each:
- Get `TA_FuncInfo` → input/output counts and types
- Allocate `TA_ParamHolder`, populate inputs from `TA_History`
- Set optional params to defaults (from `TA_OptInputParameterInfo.defaultValue`)
- Run `doRangeTest` with `nbOutput` from `funcInfo->nbOutput`
- If server doesn't support the function, skip cleanly (reported in output)

### 2. Server Protocol Extensions

All changes go into `server_gen.rs` so every language server gets them automatically.

**Timing data** — each response includes execution time of just the indicator call:

```json
{"retCode": 0, "outBegIdx": 14, "outNBElement": 237, "outReal": [...], "timing_ns": 1842}
```

**Multi-output responses** (BBANDS, MACD, STOCH, etc.):

```json
{"retCode": 0, "outBegIdx": 14, "outNBElement": 50, "outReal": [...], "outReal1": [...], "outReal2": [...], "timing_ns": 3210}
```

**Integer output responses** (CDL* patterns, MINMAXINDEX):

```json
{"retCode": 0, "outBegIdx": 14, "outNBElement": 50, "outInteger": [...], "timing_ns": 890}
```

**`list_functions`** — server reports supported functions:

```json
{"method": "list_functions"}
{"functions": ["TA_SMA", "TA_RSI", "TA_MULT", ...]}
```

Called once at server startup. ta_regtest checks this list before sending calls, giving clean skip reporting.

**Price input handling** — servers must accept named OHLCV arrays:

```json
{"method": "TA_STOCH", "params": {"startIdx": 0, "endIdx": 251, "inHigh": [...], "inLow": [...], "inClose": [...], "optInFastK_Period": 5}}
```

**Server-side fixes needed in `server_gen.rs`:**
- Add `json_find_double` for `TA_OptInput_RealRange` params (currently all parsed as int)
- Add price input parsing (`inOpen`, `inHigh`, `inLow`, `inClose`, `inVolume`, `inOpenInterest`)
- Add multi-output buffer allocation and serialization
- Add integer output support
- Expand `func_unst_id()` to all 24 unstable-period functions (ADX, ADXR, ATR, CMO, DX, EMA, HT_DCPERIOD, HT_DCPHASE, HT_PHASOR, HT_SINE, HT_TRENDLINE, HT_TRENDMODE, IMI, KAMA, MAMA, MFI, MINUS_DI, MINUS_DM, NATR, PLUS_DI, PLUS_DM, RSI, STOCHRSI, T3)
- Add `list_functions` method
- Add `timing_ns` to all responses
- Add `set_unstable_period` and `set_compatibility` methods

### 3. Timing Collection and Reporting

Three layers of output, all showing correctness alongside performance:

**CLI summary** — per-language, per-function results printed as tests run:

```
Codegen verification: Rust
---------------------------------------------
  SMA                  PASS   (C: 2.3us, Rust: 1.8us, 1.28x faster)
  RSI                  PASS   (C: 4.1us, Rust: 3.2us, 1.28x faster)
  BBANDS               FAIL   outReal[3] C=24.8719 Rust=24.8722 diff=3.0e-04
  MACD                 SKIP   (not supported by server)

  Rust: 156 passed, 1 failed, 1 skipped
```

**Cross-language table** — printed at the end of the full run:

```
=============================================
Codegen Results + Timing (avg us/call)
=============================================
Function     C-ref    C-cg    Rust    Java    .NET    Python
SMA          2.3      2.4ok   1.8ok   4.1ok   3.9ok   12.3ok
RSI          4.1      4.0ok   3.2ok   7.8ok   6.1ok   18.9ok
BBANDS       3.7      3.8ok   FAIL    7.2ok   --      15.1ok
MACD         5.2      --      --      --      --      --
```

`ok` = output matches C reference. `FAIL` = mismatch. `--` = not supported/skipped.

**Rolling report** — appended to `ta_regtest_timing.jsonl`:

```json
{"timestamp":"2026-03-13T14:30:00Z","git_sha":"f086249f","results":{"SMA":{"c_ref_ns":2300,"rust":{"status":"pass","ns":1800},"java":{"status":"pass","ns":4100}},"BBANDS":{"c_ref_ns":3700,"rust":{"status":"fail","error":"outReal[3] diff=3.0e-04"}}}}
```

JSONL format (one JSON object per line) — trivially appendable and parseable. Git SHA ties each run to a codebase state for regression tracking.

C reference timing is measured locally in the callback. Server timing comes from the `timing_ns` response field. Reported values are averages across all doRangeTest invocations (hundreds of calls per function).

### 4. Cleanup of Legacy Artifacts

Once the generic callback is validated:

**Remove:**
- `rust/ffi/` — entire FFI crate. Rust indicators tested through Rust server.
- `rust/tests/mult_test.rs`, `sma_test.rs`, `rsi_test.rs` — hand-written test files from manual porting phase.
- `ta_regtest_rust` CMake target and `ENABLE_RUST_REGTEST` flag.
- FFI generation functions in `gen_rust.c` (`printRustFfiSingleWrapper`, `writeRustFfiGenerated`, etc.)

**Keep:**
- `rust/tests/float_test.rs` — TaFloat trait unit tests are independent.
- All `test_*.c` files in ta_regtest — C reference tests stay forever.

**Phasing:**

- **Phase 1:** Generic callback produces identical pass/fail results to the 3 existing hand-coded callbacks for all languages. Hand-coded callbacks still present for comparison.
- **Phase 2:** Expand to all 158 indicators. Handle price inputs, multi-output, integer outputs, real optional params. Delete hand-coded callbacks.
- **Phase 3:** Add timing collection, CLI summary, cross-language table, JSONL report.
- **Phase 4:** Clean up legacy artifacts (FFI crate, hand-written tests, CMake targets).

### 5. Future: YAML-Driven Testing

Currently ta_abstract (old IDL) provides function metadata. Eventually, the YAML files become the source of truth:

- Servers already know what they support (generated from YAML)
- `list_functions` can return parameter metadata, not just function names
- ta_regtest uses server-reported metadata to build requests
- ta_abstract remains only for calling the C reference, not for function discovery

This transition happens naturally — new indicators added only through YAML get tested as soon as their server supports them, without backfilling the old IDL.

## Files Modified

| File | Change |
|------|--------|
| `src/tools/ta_regtest/test_codegen.c` | Replace hand-coded callbacks with generic ta_abstract-driven callback. Add timing collection. Add CLI summary + cross-language table + JSONL report. |
| `src/tools/ta_regtest/test_codegen.h` | Update API if needed |
| `src/tools/ta_regtest/codegen_pipe.c/h` | Possibly add timing field parsing (minor) |
| `tools/ta_codegen/src/server_gen.rs` | Add `timing_ns`, `list_functions`, multi-output, integer output, price inputs, real optional params, all 24 unstable-period IDs. |
| `src/tools/ta_regtest/ta_regtest.c` | Wire up `--codegen` flag (see CLI section below) |

## CLI Integration

`--codegen` flag runs codegen verification after (or instead of) the normal C test suite:

```bash
# Run codegen tests against all languages, all functions
./ta_regtest --codegen

# Run codegen tests for specific languages and functions
./ta_regtest --codegen --language=rust,c --function=SMA,RSI

# Run both normal C tests AND codegen verification
./ta_regtest --codegen

# Run ONLY codegen verification (skip normal C test suite)
./ta_regtest --codegen-only --language=rust
```

The `--codegen` flag adds codegen verification to the existing test run. `--codegen-only` skips the normal C test suite entirely. `--language` and `--function` filters apply only to the codegen portion.

## Files Removed (after validation)

| File | Reason |
|------|--------|
| `rust/ffi/` (entire directory) | Replaced by server architecture |
| `rust/tests/mult_test.rs` | Replaced by ta_regtest |
| `rust/tests/sma_test.rs` | Replaced by ta_regtest |
| `rust/tests/rsi_test.rs` | Replaced by ta_regtest |

## Success Criteria

1. `./ta_regtest --codegen` runs all 158 indicators against every available language server
2. Output matches C reference within epsilon (1e-6) for all supported functions
3. Timing data printed per-function per-language in CLI and cross-language table
4. Rolling JSONL report appended with git SHA for regression tracking
5. Failures clearly reported with function name, output index, values, and diff
6. Unsupported functions cleanly skipped with count in summary
