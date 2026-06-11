# Perftest Redesign — Pre-loaded Data + Server-side Iteration

## Problem

The current perftest (ta_bench) sends large JSON payloads per call, causing:
- Cache pollution from JSON parsing between iterations (STOCH appeared 2x slower)
- Server buffer overflow for multi-input indicators at 10k+ points
- Java JIT cold-start penalty (no warmup opportunity)
- Regtest uses 252 points with single-call timing (too noisy for perf measurement)

## Design

### New JSON-RPC Methods (additive — existing protocol unchanged)

#### `load_data`

Sent once at start of session. Server stores data in immutable reference buffers.

```json
{"method":"load_data","params":{
  "open":[...], "high":[...], "low":[...],
  "close":[...], "volume":[...], "openInterest":[...]
}}
```

Response: `{"status":"ok","n":10000}`

Server behavior on `load_data`:
- Copy arrays into persistent reference buffers (never mutated)
- Before each subsequent indicator call, copy reference → working buffers
- Perform language-specific warmup:
  - Java: run all indicators with default params to trigger JIT compilation
  - C/Rust/.NET: no-op or minimal warmup at server's discretion
- Harness waits for response before proceeding (warmup is synchronous)

#### Updated `call` (existing method, new optional fields)

```json
{"method":"TA_SMA","params":{
  "startIdx":0, "endIdx":9999,
  "optInTimePeriod":20,
  "iters":100,
  "use_preloaded":true
}}
```

- `use_preloaded:true` — use pre-loaded data instead of inline arrays
- `iters:N` — loop indicator N times internally, return average timing_ns
- Input arrays from `params` (inline) still work for backward compat (regtest)
- Output arrays always returned (for correctness spot-check)
- Copy reference → working buffers before EACH iteration to protect against mutation

Response (unchanged format):
```json
{"retCode":0,"outBegIdx":19,"outNBElement":9981,"timing_ns":15234,"outReal":[...]}
```

### Harness Flow (ta_bench.c)

1. **Generate data** — deterministic OHLCV (LCG seed 42, N points)
2. **Start servers** — spawn all language server processes
3. **Load data** — send `load_data` to each server, wait for response
   (Java JIT warmup happens here, transparent to harness)
4. **Probe ranges** — call C-ref for each indicator to get valid outBegIdx/outNBElement
5. **Generate range plan** per indicator:
   - Deterministic: full range, first half, second half, first quarter, last quarter
   - Random: M additional ranges from LCG seed 137, within valid bounds
6. **Benchmark loop** — for each indicator, for each range in plan:
   - C-ref: direct call (linked library), timed with get_nanotime()
   - Each server: send `call` with `use_preloaded:true` + `iters:N`
   - Collect timing_ns from response
7. **Correctness spot-check** — compare server output arrays against C-ref with epsilon
8. **Report** — per-indicator per-language timing table, flag >10% deviations

### C-ref Timing

C-ref calls are made directly (linked against ta-lib-static). To match the server's
mutation protection, C-ref also copies data from reference buffers before each call.
Timing includes the indicator call only, not the copy.

### What Stays Unchanged

- `ta_regtest` — completely untouched, keeps 252-point inline JSON protocol
- Existing server `call` with inline arrays — backward compatible for regtest
- `doRangeTest` in regtest — still does full range sweep for correctness
- `load_data` is additive — servers that don't implement it still work for regtest

### Server Implementation Changes

Each server (C, Rust, Java, .NET) needs:
1. `load_data` handler: parse arrays, store in reference buffers, run warmup
2. `use_preloaded` check: when true, use stored buffers instead of parsing inline arrays
3. Pre-call copy: memcpy reference → working buffers before each iteration

### regtest.py Integration

```
scripts/regtest.py                          # build + regtest + perftest
scripts/regtest.py --no-regtest             # build + perftest only
scripts/regtest.py --no-perftest            # build + regtest only
scripts/regtest.py --test-only --no-regtest # just run perftest (no build)
```

Perftest output is clearly separated from regtest output in the report.

## Implementation Order

1. Update server_gen.rs — add `load_data` handler + `use_preloaded` + pre-call copy
2. Update ta_bench.c — new harness flow (load_data, range plan, timing loop)
3. Regenerate + rebuild all servers
4. Test with C server first, then Rust, Java, .NET
5. Add Java warmup in Java server template
6. Update regtest.py to run both regtest and perftest
7. Verify regtest still passes unchanged
