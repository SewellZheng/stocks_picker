# Proposal: Generated Streaming (Incremental) API for ta_codegen

Status: **proposal only** — no implementation. Written 2026-07-02.

## Motivation

Live-trading users recompute the full indicator history on every new bar.
Stream-style APIs available in the ecosystem today (e.g. the Python wrapper's
`talib.stream` module) do the same thing under the hood: they run the full
batch computation and return the last element — O(n) work per tick, no
retained state.

A true streaming tier — per-indicator state + O(1) amortized
`update(bar) -> latest value` — eliminates that. A *generated* one (one state
struct + transition function per indicator, emitted for all four backends from
the same canonical input) would be a genuine TA-Lib differentiator, and
ta_codegen's IR makes it feasible without hand-writing 161 stream
implementations.

## Semantic definition (the contract tests enforce)

For every function F and bar stream x[0..t]:

```
stream_F.update(x[t]) == batch_F(startIdx=t, endIdx=t, full history 0..t)
```

i.e. streaming output at bar t equals the batch output when the batch is given
the full history. Two consequences:

- **Unstable-period functions** (EMA, RSI, ADX, …, 24 of them): streaming
  state carries the effect of *all* history, so the stream equals batch with
  effectively infinite warm-up. This matches how live users actually consume
  these indicators. `TA_SetUnstablePeriod` does not apply to the stream tier
  (documented, not silently ignored: the state-init API takes no unstable
  period).
- **Warm-up**: `update()` returns "not ready" (C: out-flag / `TA_NEED_MORE_DATA`;
  Rust: `Option<f64>::None`) for the first `lookback` bars.

Bit-exactness vs batch is testable mechanically (see Verification).

## Streamability tiers (classification of all 161 functions)

| Tier | Shape | State | Examples (not exhaustive) |
|------|-------|-------|---------------------------|
| T1 | pure per-bar map | none | ADD…DIV, all math transforms, price transforms, BOP, TRANGE |
| T2 | scalar recurrence | O(1) scalars | EMA, RSI, CMO, ADX/ADXR/DI/DM, ATR/NATR, KAMA, SAR/SAREXT, OBV, AD, T3 |
| T3 | fixed window | ring buffer O(period) | SMA, WMA, SUM, VAR/STDDEV, MOM/ROC*, TRIMA, CCI, MFI, ULTOSC, LINEARREG family, CDL* body averages |
| T4 | window extrema | monotonic deque O(period) | MIN/MAX/MINMAX(INDEX), MIDPOINT/MIDPRICE, WILLR, AROON*, STOCH/STOCHF |
| T5 | needs restructuring first | — | DEMA/TEMA/TRIX (currently buffered EMA-of-EMA passes), STOCHRSI, MAMA, HT_* (ring buffers already fixed-size → actually T2/T3 after inspection), MAVP (per-bar period ⇒ no fixed window) |

Two observations from the recent batch-optimization work tie in directly:

- The **monotonic deque** was evaluated for the *batch* extrema functions and
  rejected there (the cached-extremum-index idiom the family already uses is
  faster on batch data), but it is the *right* structure for T4 streaming:
  the batch cached-index trick needs arbitrary look-back rescans, which a
  stream cannot do without keeping the whole window anyway; the deque gives
  amortized O(1) per tick with exactly the window's contents.
- The planned **lockstep EMA-cascade rewrites** for DEMA/TEMA/TRIX/MACD
  (bit-exact batch restructurings that eliminate the intermediate EMA
  buffers) are exactly what moves those functions from T5 to T2: once the
  cascade runs in lockstep per-bar instead of buffer-at-a-time, the loop body
  *is* the stream transition function. Landing those batch rewrites first
  makes the streaming tier nearly free for that family.

## API shape per backend

C (caller owns the struct; no hidden allocation for T1/T2; T3/T4 ring/deque
storage via one `TA_Malloc` in init):

```c
TA_SMA_StreamState s;
TA_SMA_StreamInit(&s, optInTimePeriod);          /* validates params */
for (each bar) {
    int outReady;
    double out;
    TA_SMA_StreamUpdate(&s, close, &out, &outReady);
}
TA_SMA_StreamFree(&s);                           /* no-op for T1/T2 */
```

Rust:

```rust
let mut s = SmaStream::new(14)?;
for &x in bars { if let Some(v) = s.update(x) { /* ... */ } }
```

Java/.NET: small state objects with the same `update` shape. Multi-input
functions take `(open, high, low, close, volume)` as their batch signature
demands; multi-output write through out-params (C) or return small structs
(`Option<(f64, f64, f64)>` for MACD).

## How it fits ta_codegen

1. **Metadata, not logic, in YAML**: each function's YAML gains
   `streaming: {tier: T2}` (data only). The tier list is authored once from
   the classification above and validated by the generator (it refuses to
   emit a stream for a function whose IR doesn't match the tier's shape).
2. **IR analysis pass** (`generator/src/streaming.rs`): for the steady-state
   loop of each FuncDef (the `while(today <= endIdx)` body — the parser
   already isolates it), compute:
   - *loop-carried scalars*: locals read before written across iterations →
     state struct fields;
   - *trailing-window reads*: `in[trailingIdx]`-style accesses → ring buffer
     of size `lookback+1` plus the trailing pointer;
   - *rescan loops* (the cached-index `while(++i<=today)` idiom) → tier T4,
     replace with deque ops in the stream emitter.
   The batch IR is untouched; the analysis only *reads* it.
3. **New emitters** (`backends/c_stream.rs`, `rust_stream.rs`, …) render the
   state struct + init/update/free from the analyzed loop body. T1/T2 are a
   direct transcription; T3 swaps `in[trailingIdx]` for `ring[pos]`; T4 swaps
   the cached-index blocks for deque push/pop with the *same comparison
   semantics* (`>=`/`<=` on the incoming side to match batch tie-breaking).
4. **Servers**: `generate-servers` adds a `stream_call` method — same params
   as the batch call plus nothing; the server feeds the input array one bar
   at a time through the stream state and returns the collected outputs.
5. **Verification**: ta_regtest gains a codegen pass that calls both
   `TA_XXX` (batch, startIdx=0) and `stream_call` on the same history and
   requires **bit-identical** outputs from the first ready bar onward, per
   language. The frozen reference oracle stays the batch baseline; the stream
   contract is anchored to batch, so no new reference data is needed.

## Staging

1. T1 + T2 for C and Rust (≈90 functions, mostly mechanical), stream-vs-batch
   regtest pass wired in from day one.
2. T3 ring-buffer machinery (+ LINEARREG family via ring, *not* via running
   sliding sums — those reassociate the floating-point ops, and bit-exactness
   vs batch requires recomputing the window sums the same way batch does,
   O(period) per tick for these; still fine for live use at real-world
   periods).
3. T4 deque extrema.
4. T5 unlocked by the bit-exact batch rewrites (EMA cascades first — they're
   independently worthwhile as batch speedups).
5. Java/.NET emitters once C/Rust stabilize.

## Non-goals / risks

- **No behavioral change to the batch tier.** The stream tier is additive.
- API surface doubles per function — gate emission on the YAML flag so we can
  ship tiers incrementally.
- Candle settings: CDL streams snapshot `TA_Globals->candleSettings` at init
  (documented), avoiding mid-stream global mutation hazards.
- MAVP and functions with per-bar variable periods stay unsupported
  (documented) rather than pretending.
- Memory policy in C: T3/T4 states own one heap block, freed by
  `*_StreamFree`; embedded users who cannot malloc can size the block
  themselves via a `*_StreamStateSize(period)` helper (emitted alongside).
