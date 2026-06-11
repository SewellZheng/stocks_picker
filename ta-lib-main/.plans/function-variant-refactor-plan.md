# Function Variant Refactor — Execution Plan

## Phase 1: Quick server fixes (parallel, no source file changes)

### Task 1A: Split Java handleRequest into per-function methods
- Fix: `server_gen.rs` Java generation — emit `static String handle_SMA(...)` per function
- The dispatch method becomes a thin router
- Unblocks JIT compilation of timing loops
- Test: `ta_regtest --codegen --language=java`

### Task 1B: Port && split to Java backend
- Copy `expr_directly_contains_candle_call` logic to `java.rs`
- Split `if(A && B)` into nested ifs when both sides have candle calls
- Skip candle function hoisting (pass skip list like C does)
- Test: `ta_regtest --codegen --language=java`

### Task 1C: Port && split to Rust backend
- Same logic in `rust_lang.rs`
- Test: `ta_regtest --codegen --language=rust`

### Task 1D: Fix .NET timing overflow
- Already done in server_gen.rs
- Regenerate + test: `ta_regtest --codegen --language=dotnet`

## Phase 2: Rename _Logic to _Unguarded (C backend)

### Task 2A: Rename in C output
- `c.rs`: Change `_Logic` suffix to `_Unguarded`
- Update `#define TA_INT_X` to point to `TA_X_Unguarded`
- Update all tests in `backend_suite.rs`
- Test: `cargo test && ta_regtest --codegen --language=c`

## Phase 3: Add unguarded variants to Java and .NET

### Task 3A: Java unguarded generation
- `java.rs`: Generate `smaUnguarded()` methods (skip validation)
- `server_gen.rs`: Java server dispatches to unguarded variants
- Test: `ta_regtest --codegen --language=java`

### Task 3B: .NET unguarded
- When .NET gets native C# implementations (not P/Invoke), add unguarded variants
- Deferred until .NET architecture decision

## Phase 4: Source file cleanup

### Task 4A: Remove TA_INT_ from source files
- Replace `TA_INT_EMA(...)` with `TA_EMA(...)` in source .c files
- Parser maps plain `TA_EMA` to unguarded variant in cross-indicator context
- Move pre-compute logic to callers where needed
- Test: all backends, full regtest

## Phase 5: Benchmark columns

### Task 5A: Add guarded/unguarded columns
- ta_regtest reports both guarded and unguarded timings per language
- Add --no-guarded / --no-unguarded flags
- --language=rust shows c_ref + rust + rust_unguarded
