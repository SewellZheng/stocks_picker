# ta_codegen Alpha Parity — Design Document

**Date:** 2026-03-08
**Goal:** Full parity between ta_codegen-generated code and the original TA-Lib C implementation for all 164 indicators across 5 languages.

## Alpha Definition

Alpha is complete when:
- All 164 indicators are extracted to `ta_func_defs/` (YAML metadata + prefix-free C logic)
- All 164 indicators generate correct output for all 5 languages (C, Rust, Java, .NET, SWIG/Python)
- ta_regtest passes 100% for all indicators, all languages (via `--codegen` path)
- Generated C output matches (or near-matches) original `src/ta_func/ta_*.c`
- Performance benchmarks show no significant regressions
- Parser guards catch all unsupported syntax in both extraction and backends
- `enums.yaml` covers all enum types used across indicators
- IDL documentation is complete and accurate

---

## Phase 0: Clean Slate + Naming Convention

### Cleanup

- Delete `rust/src/ta_func/{mult,rsi,sma}.rs` (old gen_code output, superseded by ta_codegen)
- Delete all `docs/plans/*.md` except this file
- Delete `ta_codegen_output/` build artifacts from git tracking if present

### Naming Convention

Source files in `ta_func_defs/<name>/<name>.c` are prefix-free. Function names must match the filename.

For `ta_func_defs/ema/ema.c`:
- `ema_lookback()` — lookback calculation
- `ema_logic()` — core computation (the "logic function", single source of truth)

Cross-indicator calls are also prefix-free: `ema_logic()` calls `sma_lookback()`, not `TA_SMA_Lookback()`.

### Generated Variants Per Language

**C:**
| Generated function | Description |
|---|---|
| `TA_EMA_Lookback()` | Lookback |
| `TA_EMA()` | Guarded (range checks, default substitution) |
| `TA_EMA_Logic()` | Unguarded core logic |
| `TA_INT_EMA()` | Alias for Logic (backwards compat) |
| `TA_S_EMA()` | Single-precision guarded |
| `TA_S_EMA_Lookback()` | Single-precision lookback |
| `TA_S_EMA_Logic()` | Single-precision unguarded |

**Rust:**
| Generated function | Description |
|---|---|
| `ema_lookback()` | Lookback |
| `ema()` | Safe, guarded |
| `ema_logic()` | No range checking |
| `ema_unsafe()` | No ownership checks |
| `ema_s()` | Single-precision guarded |
| `ema_lookback_s()` | Single-precision lookback |
| `ema_logic_s()` | Single-precision unguarded |
| `ema_unsafe_s()` | Single-precision no ownership checks |

**Java:**
| Generated function | Description |
|---|---|
| `emaLookback()` | Lookback |
| `ema()` | Guarded |
| `emaLogic()` | Unguarded |

**.NET:**
| Generated function | Description |
|---|---|
| `EmaLookback()` | Lookback |
| `Ema()` | Guarded |
| `EmaLogic()` | Unguarded |

**SWIG/Python:** Mirrors C naming, exposed via SWIG interface.

### Indicator Registry

Auto-discovered from `ta_func_defs/` directories. Used by all backends to resolve cross-indicator calls: `sma_lookback()` in source → `TA_SMA_Lookback()` in C output, `sma_lookback()` in Rust output, etc.

### Refactor Existing 6 Indicators

Update the existing converted indicators (MULT, SMA, RSI, EMA, WMA, MA) to use the new prefix-free convention, update the C parser and all 5 backends to generate the full variant set, and verify ta_regtest still passes.

---

## Phase 1: Extraction Tool

A Rust tool in `tools/ta_codegen/` that bulk-extracts all 164 indicators from the existing codebase.

### Input
- `src/ta_abstract/tables/table_*.c` — function metadata (group, flags, input/output definitions, ranges, defaults)
- `src/ta_func/ta_*.c` — function implementations

### Output Per Indicator
- `ta_func_defs/<name>/<name>.yaml` — metadata from abstract tables, following the IDL schema defined in `docs/ta_func_defs_idl.md`
- `ta_func_defs/<name>/<name>.c` — logic extracted from source, stripped of generated boilerplate (signatures, guards, lookback wrapper), prefix-free

### Extraction Rules
- Strip `TA_` prefix from function names
- Convert `TA_INT_<NAME>` → `<name>_logic`
- Convert `TA_<NAME>_Lookback` → `<name>_lookback`
- Convert cross-indicator calls: `TA_SMA(...)` → `sma(...)`, `TA_EMA_Lookback(...)` → `ema_lookback(...)`
- Enforce filename = function name rule

### Parser Guards
- Error on unrecognized syntax: unknown macros, goto, unexpected preprocessor directives, pointer patterns
- Error on function names that don't match the filename

### Verification
After extraction, run ta_codegen on each indicator and diff the generated C output against the original `src/ta_func/ta_*.c`. Flag any indicator where output doesn't match.

---

## Phase 2: Scaling Out — Bulk Conversion

### Process
1. Run extraction tool on all 164 indicators
2. Triage failures — categorize as missing parser features vs genuinely unusual indicators
3. Iterate parser — add support for new patterns, re-run
4. Batch verification (~20 indicators at a time):
   - Generate C output via ta_codegen
   - Diff against original source
   - AI review diffs to confirm expected vs bugs
5. ta_regtest validation — full suite with `--codegen` for all converted indicators, all 5 languages

### Backend Parser Guards
Each backend validates it can handle every IR node it receives. If a backend encounters an unsupported statement type or pattern, it errors rather than producing garbage. This catches cases where extraction succeeds but a specific backend can't render the result.

### Expected Difficulty Tiers
- **Easy (~100):** Math operators, simple moving averages, math transforms — single input, single output, straightforward logic
- **Medium (~40):** MACD, BBANDS, STOCH, ADX, etc. — multiple outputs, price inputs, cross-indicator calls, enum parameters
- **Hard (~60):** Candlestick patterns — structurally similar but numerous, may need pattern-specific handling. Price input type support required.

---

## Phase 3: Performance Benchmarking

All benchmarking is behind the `--codegen` flag in ta_regtest. Regular ta_regtest flow is unchanged.

### Server-Side Timing
Each language's JSON-RPC server wraps indicator calls with high-resolution timing and includes it in the response (e.g., `"elapsed_ns": 12345`). This measures actual indicator computation, not IPC overhead.

### ta_regtest Reporting

**Summary (stdout):**
```
C:            164/164 pass | avg +2.3% vs baseline (σ 4.1%)
Rust:         164/164 pass | avg +1.1% vs baseline (σ 3.2%)
Java:         164/164 pass | avg +5.7% vs baseline (σ 6.0%)
.NET:         164/164 pass | avg +3.4% vs baseline (σ 4.8%)
SWIG/Python:  164/164 pass | avg +8.2% vs baseline (σ 7.1%)

Full report: ./ta_regtest_report_2026-03-08.csv
```

**Full report (CSV):**
Per-indicator, per-language, per-test timing data with columns like:
`indicator,language,test,baseline_ns,codegen_ns,diff_pct,pass`

### Regression Threshold
Flag any indicator where generated code is >10% slower than baseline. Investigate and fix before declaring alpha.

---

## Future Work (Beta)

Not in scope for alpha, noted for later:

- Drop `TA_` prefix as the public API — expose `ema()` directly to users
- Price input type in YAML (`type: price` with `components: [high, low, close]`)
- Rust `_unsafe` variants that skip ownership checks (design the safety model)
- Publish public beta release from generated code
- Additional documentation for generator architecture
- API stability guarantees
