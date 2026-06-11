# Alpha Parity Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Achieve full parity between ta_codegen-generated code and original TA-Lib for all 164 indicators across 5 languages.

**Architecture:** An extraction tool parses the existing C abstract tables and function source files to produce prefix-free YAML+C definitions. The codegen backends are updated to generate all function variants (guarded, logic, lookback) with per-language naming. ta_regtest validates correctness and reports performance via `--codegen`.

**Tech Stack:** Rust (ta_codegen), C (ta_func source), YAML (IDL), JSON-RPC (cross-language testing)

**Design doc:** `docs/plans/2026-03-08-alpha-parity-design.md`

---

## Phase 0: Naming Convention + Backend Variants

### Task 1: Refactor existing C source files to prefix-free naming

**Files:**
- Modify: `ta_func_defs/sma/sma.c`
- Modify: `ta_func_defs/rsi/rsi.c`
- Modify: `ta_func_defs/ema/ema.c`
- Modify: `ta_func_defs/wma/wma.c`
- Modify: `ta_func_defs/ma/ma.c`
- Modify: `ta_func_defs/mult/mult.c`

**Step 1:** For each `.c` file, rename functions:
- `lookback(...)` → `<name>_lookback(...)` (e.g., `sma_lookback`)
- Main function → `<name>_logic(...)` (e.g., `sma_logic`)
- `TA_INT_<NAME>(...)` → `<name>_logic(...)` (e.g., `int_sma` → `sma_logic`)
- Cross-indicator calls: `SMA_Lookback(...)` → `sma_lookback(...)`, `EMA(...)` → `ema_logic(...)`, etc.

**Step 2:** Verify no `TA_` prefixes remain in any `ta_func_defs/**/*.c` file.

**Step 3:** Commit: `refactor: rename ta_func_defs C sources to prefix-free convention`

---

### Task 2: Update C parser to handle prefix-free function names

**Files:**
- Modify: `tools/ta_codegen/src/parser/c_source.rs`

**Step 1: Write failing test**

Add test to `c_source.rs` tests:
```rust
#[test]
fn test_prefix_free_function_names() {
    let src = r#"
int sma_lookback(int optInTimePeriod) {
    return optInTimePeriod - 1;
}

TA_RetCode sma_logic(/* params */) {
    return TA_SUCCESS;
}
"#;
    let parsed = parse_c_source_str(src);
    assert!(!parsed.lookback_body.is_empty());
    assert_eq!(parsed.functions.len(), 1);
    assert_eq!(parsed.functions[0].name, "sma_logic");
}
```

**Step 2:** Run test, confirm it fails (parser expects `TA_` prefix).

**Step 3:** Update `extract_functions()` to recognize `<name>_lookback` and `<name>_logic` patterns alongside existing `TA_` patterns. The function name should be derived from the file path when available.

**Step 4:** Run test, confirm it passes.

**Step 5:** Run `cargo test` — all existing tests must still pass.

**Step 6:** Commit: `feat(parser): support prefix-free function names`

---

### Task 3: Build the indicator registry

**Files:**
- Create: `tools/ta_codegen/src/registry.rs`
- Modify: `tools/ta_codegen/src/lib.rs` (add `pub mod registry`)
- Modify: `tools/ta_codegen/src/main.rs` (load registry)

**Step 1: Write failing test**

```rust
#[test]
fn test_registry_discovers_indicators() {
    let base = Path::new(env!("CARGO_MANIFEST_DIR")).join("../../ta_func_defs");
    let registry = Registry::from_dir(&base);
    assert!(registry.contains("sma"));
    assert!(registry.contains("rsi"));
    assert!(registry.contains("ema"));
}

#[test]
fn test_registry_resolves_cross_calls() {
    let registry = /* ... */;
    // C backend
    assert_eq!(registry.resolve_call("sma_lookback", Lang::C), "TA_SMA_Lookback");
    assert_eq!(registry.resolve_call("sma_logic", Lang::C), "TA_INT_SMA");
    assert_eq!(registry.resolve_call("ema_logic", Lang::Rust), "ema_logic");
    assert_eq!(registry.resolve_call("ema_lookback", Lang::Java), "emaLookback");
}
```

**Step 2:** Run test, confirm fails.

**Step 3:** Implement `Registry` struct:
- `from_dir(path)` — scans `ta_func_defs/` for subdirectories containing `.yaml` files
- `contains(name)` — checks if an indicator exists
- `resolve_call(func_name, lang)` — maps prefix-free call to language-specific name
- `resolve_type(func_name, lang)` — maps prefix-free type references

**Step 4:** Run test, confirm passes. Run `cargo test` for all tests.

**Step 5:** Commit: `feat: indicator registry for cross-function call resolution`

---

### Task 4: Update C backend to generate all function variants

**Files:**
- Modify: `tools/ta_codegen/src/backends/c.rs`

**Step 1: Write failing test**

```rust
#[test]
fn test_c_generates_all_variants() {
    let func = load_sma();
    let enums = no_enums();
    let registry = load_registry();
    let output = backends::c::generate(&func, &enums, &registry);

    assert!(output.contains("TA_SMA_Lookback"), "Missing lookback");
    assert!(output.contains("TA_SMA("), "Missing guarded function");
    assert!(output.contains("TA_SMA_Logic("), "Missing logic function");
    assert!(output.contains("TA_INT_SMA("), "Missing INT alias");
    assert!(output.contains("TA_S_SMA("), "Missing single-precision");
    assert!(output.contains("TA_S_SMA_Logic("), "Missing single-precision logic");
}
```

**Step 2:** Run test, confirm fails.

**Step 3:** Update `generate()` to produce:
- `TA_<NAME>_Lookback()` — lookback function (already exists)
- `TA_<NAME>()` — guarded function with range checks and default substitution (already exists as the main function)
- `TA_<NAME>_Logic()` — unguarded function (the raw body, no validation)
- `TA_INT_<NAME>()` — `#define TA_INT_<NAME> TA_<NAME>_Logic` alias
- `TA_S_<NAME>()` — single-precision guarded (already exists)
- `TA_S_<NAME>_Lookback()` — single-precision lookback
- `TA_S_<NAME>_Logic()` — single-precision unguarded

Use `registry` to resolve cross-indicator calls in the body: `sma_lookback(...)` → `TA_SMA_Lookback(...)`.

**Step 4:** Run test, confirm passes. Run `cargo test`.

**Step 5:** Commit: `feat(c): generate all function variants (guarded, logic, INT alias)`

---

### Task 5: Update Rust backend to generate all function variants

**Files:**
- Modify: `tools/ta_codegen/src/backends/rust_lang.rs`

**Step 1: Write failing test**

```rust
#[test]
fn test_rust_generates_all_variants() {
    let func = load_sma();
    let enums = no_enums();
    let registry = load_registry();
    let output = backends::rust_lang::generate(&func, &enums, &registry);

    assert!(output.contains("fn sma_lookback"), "Missing lookback");
    assert!(output.contains("fn sma("), "Missing guarded");
    assert!(output.contains("fn sma_logic("), "Missing logic");
    assert!(output.contains("fn sma_unsafe("), "Missing unsafe");
    assert!(output.contains("fn sma_s("), "Missing single-precision guarded");
    assert!(output.contains("fn sma_logic_s("), "Missing single-precision logic");
    assert!(output.contains("fn sma_unsafe_s("), "Missing single-precision unsafe");
}
```

**Step 2:** Run test, confirm fails.

**Step 3:** Update `generate()` to produce all 8 variants (4 double + 4 single precision). The `_unsafe` variants use raw pointers instead of slices, skipping ownership checks.

**Step 4:** Run test, confirm passes. Run `cargo test`.

**Step 5:** Commit: `feat(rust): generate all function variants with unsafe and single-precision`

---

### Task 6: Update Java, .NET, SWIG backends for logic variant

**Files:**
- Modify: `tools/ta_codegen/src/backends/java.rs`
- Modify: `tools/ta_codegen/src/backends/dotnet.rs`
- Modify: `tools/ta_codegen/src/backends/swig.rs`

**Step 1: Write failing tests** for each backend checking for `Logic` variant.

**Step 2:** Run tests, confirm they fail.

**Step 3:** Update each backend's `generate()`:
- Java: add `emaLogic()` and `emaLookback()` naming
- .NET: add `EmaLogic()` and `EmaLookback()` naming
- SWIG: mirrors C naming (`TA_EMA_Logic`)

**Step 4:** Run tests, confirm they pass. Run `cargo test`.

**Step 5:** Commit: `feat(java,dotnet,swig): generate logic function variants`

---

### Task 7: Add backend parser guards

**Files:**
- Modify: `tools/ta_codegen/src/backends/c.rs`
- Modify: `tools/ta_codegen/src/backends/rust_lang.rs`
- Modify: `tools/ta_codegen/src/backends/java.rs`
- Modify: `tools/ta_codegen/src/backends/dotnet.rs`
- Modify: `tools/ta_codegen/src/backends/swig.rs`

**Step 1: Write failing test**

```rust
#[test]
#[should_panic(expected = "Unsupported")]
fn test_c_backend_rejects_unsupported_node() {
    // Create a FuncDef with a statement type the backend doesn't handle
    let func = make_func_with_goto();
    let output = backends::c::generate(&func, &no_enums(), &registry);
}
```

**Step 2:** Run test, confirm fails (currently silently ignores unknown nodes).

**Step 3:** Add explicit match arms or catch-all panics in each backend's `render_statement()` that error on unrecognized IR nodes. Each backend should validate it can handle every `Statement` and `Expr` variant it receives.

**Step 4:** Run test, confirm passes. Run `cargo test`.

**Step 5:** Commit: `feat: backend parser guards — error on unsupported IR nodes`

---

### Task 8: Update server_gen and validate.sh for new naming

**Files:**
- Modify: `tools/ta_codegen/src/server_gen.rs`
- Modify: `tools/ta_codegen/tests/validate.sh`

**Step 1:** Update server_gen to dispatch to the guarded `TA_<NAME>()` function (which is what JSON-RPC clients should call).

**Step 2:** Update validate.sh to test both `TA_SMA` (guarded) and `TA_SMA_Logic` (unguarded) endpoints.

**Step 3:** Run `cargo run --release -- generate` from `tools/ta_codegen/`.

**Step 4:** Run `bash tests/validate.sh` — all tests must pass.

**Step 5:** Commit: `feat: update server_gen and validation for new naming convention`

---

### Task 9: Run ta_regtest with refactored indicators

**Step 1:** Build ta_codegen: `cd tools/ta_codegen && cargo build --release`

**Step 2:** Generate code: `cargo run --release -- generate`

**Step 3:** Build and run ta_regtest with `--codegen` for all 6 indicators, all languages.

**Step 4:** Fix any failures.

**Step 5:** Commit: `test: verify ta_regtest passes with prefix-free naming convention`

---

## Phase 1: Extraction Tool

### Task 10: Abstract table parser — extract YAML metadata

**Files:**
- Create: `tools/ta_codegen/src/extractor/mod.rs`
- Create: `tools/ta_codegen/src/extractor/table_parser.rs`
- Modify: `tools/ta_codegen/src/lib.rs` (add `pub mod extractor`)

**Step 1: Write failing test**

```rust
#[test]
fn test_parse_sma_from_table() {
    let table_c = fs::read_to_string("../../src/ta_abstract/tables/table_s.c").unwrap();
    let defs = parse_table(&table_c);
    let sma = defs.iter().find(|d| d.name == "SMA").unwrap();

    assert_eq!(sma.group, "Overlap Studies");
    assert_eq!(sma.camel_case, "Sma");
    assert!(sma.flags.contains(&"overlap".to_string()));
    assert_eq!(sma.inputs.len(), 1);
    assert_eq!(sma.inputs[0].param_type, "real");
    assert_eq!(sma.optional_inputs.len(), 1);
    assert_eq!(sma.optional_inputs[0].name, "optInTimePeriod");
    assert_eq!(sma.optional_inputs[0].range, Some((2, 100000)));
    assert_eq!(sma.optional_inputs[0].default, Some(30.0));
    assert_eq!(sma.outputs.len(), 1);
    assert!(sma.outputs[0].flags.contains(&"line".to_string()));
}
```

**Step 2:** Run test, confirm fails.

**Step 3:** Implement `parse_table()`:
- Parse `DEF_FUNCTION(NAME, groupId, description, flags, CamelCase)` macros
- Parse `TA_<NAME>_Inputs[]`, `TA_<NAME>_Outputs[]`, `TA_<NAME>_OptInputs[]` arrays
- Resolve `TA_DEF_UI_*` references to their actual definitions (from `ta_def_ui.c`)
- Map `TA_GroupId_*` to string names
- Map `TA_FUNC_FLG_*` to flag strings
- Map input/output types and flags

**Step 4:** Run test, confirm passes. Test against `table_m.c` (MACD — multi-output) too.

**Step 5:** Commit: `feat(extractor): parse C abstract tables to YAML metadata`

---

### Task 11: Abstract table parser — resolve shared definitions

**Files:**
- Modify: `tools/ta_codegen/src/extractor/table_parser.rs`

**Step 1: Write failing test**

```rust
#[test]
fn test_resolve_shared_defs() {
    let def_ui = fs::read_to_string("../../src/ta_abstract/ta_def_ui.c").unwrap();
    let shared = parse_shared_defs(&def_ui);

    let tp = shared.get("TA_DEF_UI_TimePeriod_30_MINIMUM2").unwrap();
    assert_eq!(tp.display_name, "Time Period");
    assert_eq!(tp.hint, "Number of period");
    assert_eq!(tp.range, Some((2, 100000)));
    assert_eq!(tp.default, Some(30.0));
    assert_eq!(tp.suggested, Some((4.0, 200.0, 1.0)));

    let ma = shared.get("TA_DEF_UI_MA_Method").unwrap();
    assert_eq!(ma.param_type, "enum:MAType");
}
```

**Step 2:** Run test, confirm fails.

**Step 3:** Parse `ta_def_ui.c` for all `TA_DEF_UI_*` constant definitions:
- `TA_InputParameterInfo` constants → input type + flags
- `TA_OptInputParameterInfo` constants → type, name, flags, displayName, dataSet, default, hint
- `TA_OutputParameterInfo` constants → type + flags
- `TA_IntegerRange` / `TA_RealRange` constants → min, max, suggested_start/end/increment

**Step 4:** Run test, confirm passes.

**Step 5:** Commit: `feat(extractor): resolve shared TA_DEF_UI definitions`

---

### Task 12: Function source extractor — strip boilerplate from ta_func

**Files:**
- Create: `tools/ta_codegen/src/extractor/func_extractor.rs`

**Step 1: Write failing test**

```rust
#[test]
fn test_extract_sma_logic() {
    let source = fs::read_to_string("../../src/ta_func/ta_SMA.c").unwrap();
    let extracted = extract_function_source(&source, "sma");

    // Should contain prefix-free function names
    assert!(extracted.contains("sma_lookback("));
    assert!(extracted.contains("sma_logic(") || extracted.contains("int_sma("));
    // Should NOT contain TA_ prefixes
    assert!(!extracted.contains("TA_SMA_Lookback"));
    assert!(!extracted.contains("TA_INT_SMA"));
    // Should NOT contain GENCODE markers
    assert!(!extracted.contains("GENCODE"));
    assert!(!extracted.contains("/* Generated */"));
}
```

**Step 2:** Run test, confirm fails.

**Step 3:** Implement `extract_function_source()`:
- Find code between GENCODE section boundaries (the hand-written parts):
  - Between END SECTION 1 and START SECTION 2: lookback body
  - Between END SECTION 2 and START SECTION 3: (usually empty)
  - Between END SECTION 4 and START SECTION 5: main function body
  - After END SECTION 5 to end of file: internal functions (TA_INT_*)
- Strip `/* Generated */` prefixed lines
- Rename functions: `TA_SMA_Lookback` → `sma_lookback`, `TA_INT_SMA` → `sma_logic`
- Rename cross-indicator calls: `TA_EMA_Lookback(...)` → `ema_lookback(...)`
- Reconstruct as clean C source with just `sma_lookback()` and `sma_logic()`

**Step 4:** Run test, confirm passes. Test with RSI (has TA_INT function) and MACD (multi-output).

**Step 5:** Commit: `feat(extractor): extract prefix-free C logic from ta_func source`

---

### Task 13: Extraction CLI command — `ta_codegen extract`

**Files:**
- Modify: `tools/ta_codegen/src/main.rs`

**Step 1:** Add `extract` subcommand that:
- Reads all `src/ta_abstract/tables/table_*.c` files
- Reads shared definitions from `src/ta_abstract/ta_def_ui.c`
- For each function found in tables:
  - Reads corresponding `src/ta_func/ta_<NAME>.c`
  - Extracts YAML metadata → writes `ta_func_defs/<name>/<name>.yaml`
  - Extracts C logic → writes `ta_func_defs/<name>/<name>.c`
- Reports: `Extracted 164 indicators (X succeeded, Y failed)`
- On parser guard failure: prints function name + error, continues to next

**Step 2:** Run on a single indicator first: `cargo run -- extract --function=SMA`

**Step 3:** Diff extracted output against existing hand-converted `ta_func_defs/sma/`:
```bash
diff ta_func_defs/sma/sma.yaml ta_func_defs_extracted/sma/sma.yaml
diff ta_func_defs/sma/sma.c ta_func_defs_extracted/sma/sma.c
```

**Step 4:** Fix any differences until extraction matches hand-converted files.

**Step 5:** Commit: `feat: extract CLI command for bulk indicator extraction`

---

### Task 14: Extraction verification — round-trip diff

**Files:**
- Create: `tools/ta_codegen/tests/test_extraction.rs` or extend validate.sh

**Step 1:** For each extracted indicator:
- Run ta_codegen `generate` on the extracted YAML+C
- Diff the generated C output against original `src/ta_func/ta_<NAME>.c`
- Report match/mismatch per indicator

**Step 2:** Build this as a `verify` subcommand: `cargo run -- verify --function=SMA`

**Step 3:** Run on all 6 existing hand-converted indicators to validate.

**Step 4:** Commit: `feat: verify command for round-trip extraction validation`

---

## Phase 2: Bulk Conversion

### Task 15: Run extraction on all 164 indicators

**Step 1:** Run: `cargo run --release -- extract`

**Step 2:** Triage failures — list indicators that errored with their error messages.

**Step 3:** Categorize failures:
- Missing parser features (new C patterns)
- Unusual function structures
- Price input types not yet supported

**Step 4:** Commit extracted indicators that succeeded: `feat: extract N indicators from C source`

---

### Task 16: Iterate parser for unsupported patterns

For each batch of parser failures:

**Step 1:** Identify the new C pattern causing the failure.

**Step 2:** Write a failing test for that pattern.

**Step 3:** Add support in the C parser and/or extractor.

**Step 4:** Run test, confirm passes. Re-run extraction.

**Step 5:** Commit: `feat(parser): support <pattern description>`

Repeat until all 164 indicators extract successfully.

---

### Task 17: Batch verification — 20 indicators at a time

For each batch:

**Step 1:** Run `cargo run -- verify` on the batch.

**Step 2:** Review diffs — confirm differences are expected (naming changes) vs bugs.

**Step 3:** Run ta_regtest with `--codegen` for the batch, all 5 languages.

**Step 4:** Fix any failures.

**Step 5:** Commit: `test: verify batch N (indicators X-Y) pass ta_regtest`

---

## Phase 3: Performance Benchmarking

### Task 18: Add server-side timing to JSON-RPC servers

**Files:**
- Modify: `tools/ta_codegen/src/server_gen.rs`

**Step 1:** For each language's server template, wrap the indicator call with high-resolution timing:
- C: `clock_gettime(CLOCK_MONOTONIC, ...)` before/after
- Java: `System.nanoTime()` before/after
- Rust: `std::time::Instant::now()` before/after
- .NET: `Stopwatch` before/after
- Python/SWIG: `time.perf_counter_ns()` before/after

**Step 2:** Include `"elapsed_ns": <value>` in the JSON-RPC response.

**Step 3:** Regenerate all servers and verify timing appears in responses.

**Step 4:** Commit: `feat: server-side timing in JSON-RPC responses`

---

### Task 19: Add ta_regtest benchmark collection and reporting

**Files:**
- Modify: `src/tools/ta_regtest/` (the `--codegen` path)

**Step 1:** Collect `elapsed_ns` from each JSON-RPC response in the codegen test path.

**Step 2:** After all tests complete, compute per-language summary:
- Total indicators tested
- Pass/fail count
- Average timing difference vs C baseline (%)
- Standard deviation

**Step 3:** Print summary to stdout:
```
C:            164/164 pass | avg +2.3% vs baseline (σ 4.1%)
Rust:         164/164 pass | avg +1.1% vs baseline (σ 3.2%)
...
Full report: ./ta_regtest_report_2026-03-08.csv
```

**Step 4:** Write CSV report with columns: `indicator,language,test,baseline_ns,codegen_ns,diff_pct,pass`

**Step 5:** Commit: `feat: benchmark reporting in ta_regtest --codegen`

---

## Phase 4: Alpha Completion

### Task 20: Final validation — all 164 indicators, all 5 languages

**Step 1:** Run full extraction: `cargo run --release -- extract`

**Step 2:** Generate all code: `cargo run --release -- generate`

**Step 3:** Run ta_regtest with `--codegen` for all indicators, all languages.

**Step 4:** Run verification diff against original source.

**Step 5:** Review benchmark report — flag any >10% regressions.

**Step 6:** Fix any remaining issues.

**Step 7:** Commit: `milestone: alpha parity — 164 indicators, 5 languages, all tests pass`

---

### Task 17b: Dynamic codegen regression via abstract API

**Files:**
- Modify: `src/tools/ta_regtest/test_codegen.c`
- Modify: `src/tools/ta_regtest/test_codegen.h`

**Goal:** Replace the hard-coded per-indicator callbacks (`codegen_range_sma`, `codegen_range_mult`, `codegen_range_rsi`) with a single generic callback that uses the TA-Lib abstract API to call any indicator dynamically.

**Step 1:** Implement a generic `codegen_range_generic` callback that:
- Uses `TA_GetFuncHandle(name)` + `TA_ParamHolderAlloc()` to set up parameters
- Calls the C reference via `TA_CallFunc()`
- Builds the JSON-RPC request from the abstract parameter info
- Compares C vs codegen output

**Step 2:** Replace `CODEGEN_TESTS[]` static array with dynamic discovery:
- Scan `ta_func_defs/` (or query the codegen server for supported functions)
- For each supported function, run the generic callback with doRangeTest
- Auto-detect unstable period function IDs from YAML flags

**Step 3:** Remove the per-indicator callbacks (`codegen_range_sma`, etc.)

**Step 4:** Verify: `ta_regtest --codegen` should now automatically test all indicators that ta_codegen supports, without any code changes needed when new indicators are added.

**Step 5:** Commit: `feat(regtest): dynamic codegen verification via abstract API`

---

### Task 9b: Auto-discover indicators in Rust test suites

**Files:**
- Modify: `tools/ta_codegen/tests/backend_suite.rs`
- Modify: `tools/ta_codegen/tests/integration_test.rs`

**Goal:** Replace hard-coded indicator lists in backend_suite.rs and integration_test.rs with dynamic discovery by scanning `ta_func_defs/` at test time.

**Step 1:** Add a helper function that scans `ta_func_defs/` for directories containing `<name>.yaml` + `<name>.c` files.

**Step 2:** Generate test cases dynamically for each discovered indicator across all backends.

**Step 3:** Remove hard-coded indicator lists from test macros.

**Step 4:** Verify: adding a new indicator to `ta_func_defs/` automatically includes it in the test suite without code changes.

**Step 5:** Commit: `feat(tests): auto-discover indicators in backend and integration tests`

---

### Task 12b: MA dispatcher cleanup

**Files:**
- Modify: `ta_func_defs/ma/ma.c`

**Goal:** The MA dispatcher switch statement currently only handles SMA and EMA. The original `ta_MA.c` dispatches to all 9 MA types (SMA, EMA, WMA, DEMA, TEMA, TRIMA, KAMA, MAMA, T3). As indicators are extracted, the MA switch should grow to include all available MA variants.

**Decision:** The original C code uses `FUNCTION_CALL(SMA)` which maps to the guarded `TA_SMA()`. Our prefix-free `ma.c` calls `sma_logic()` (unguarded). Since MA does its own validation, calling unguarded is cleaner and avoids double-validation. Keep calling `_logic` variants.

**Step 1:** After extracting all MA variant indicators (DEMA, TEMA, TRIMA, KAMA, MAMA, T3), update `ma.c` switch to dispatch to all of them.

**Step 2:** Update `ma_lookback` similarly.

**Step 3:** Verify via ta_regtest --codegen that MA produces identical results for all MA types.

**Step 4:** Commit: `feat: complete MA dispatcher with all 9 MA type variants`

---

## Notes

- **Backend `generate()` signatures** will change to accept `&Registry` as a parameter — this is a breaking change across all backends and tests. Do it in Task 4 and propagate.
- **Price input type** (`type: price` with components) is needed for ~40 indicators. Add support during Phase 2 when candlestick/price indicators start failing extraction.
- **The `--codegen` flag** scopes all benchmark/timing work to the codegen test path. Regular ta_regtest is unchanged.
- **Candlestick patterns** (~60 indicators) are structurally similar. Once one works, batch the rest.
- **Return types** are uniform: lookback always returns `int`, logic/main always returns `TA_RetCode`. Keeping return types in the prefix-free C source makes it valid compilable C.
