# Unguarded Comparison Columns — Next Steps

## What we want
regtest output with columns:
```
Function    C-ref    C    C-unguarded    Rust    Rust-unguarded    Java    .NET
```

Plus flags:
- `--language=rust` shows C-ref + Rust + Rust-unguarded
- `--no-guarded` hides guarded columns
- `--no-unguarded` hides unguarded columns

## The EMA extra-param problem
Some functions have extra params in their unguarded (Logic) signatures:
- EMA: `optInK_1` (double, pre-computed from period)
- MACD: `slowK, fastK, signalK` (pre-computed from periods)

The server can't call `TA_INT_EMA(...)` without knowing the k value.
Options:
1. Skip unguarded for functions with extra params
2. Compute the extra params in the server before calling unguarded
3. Have the guarded function call the unguarded internally (already does this)

Option 2 is cleanest — the server computes k the same way the guarded function does,
then calls the unguarded variant directly.

## Implementation
1. server_gen.rs: for C server, emit a second timing pass that calls TA_INT_*
   - For functions with extra params, compute them first
   - Report `timing_ns_unguarded` in the JSON response alongside `timing_ns`
2. server_gen.rs: for Rust server, emit both `core.sma()` and `core.sma_unguarded()` passes
3. ta_regtest: parse both timing fields, display in separate columns
4. regtest.py: pass through --no-guarded/--no-unguarded flags

## Deferred to next session
This is a cross-cutting change across server_gen.rs, all server outputs,
and ta_regtest. Better done fresh with full context.
