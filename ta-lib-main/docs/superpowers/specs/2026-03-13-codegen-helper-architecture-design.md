# Codegen Helper Architecture Design

## Context

The first round of codegen backend fixes (spec: `2026-03-13-codegen-backend-fixes-design.md`) addressed ForC rendering, SWIG naming, macro replacement, parser updates, math functions, and forward declarations. Task 14 (build all servers) revealed deeper gaps not covered by that spec:

- **Candlestick macros** (~1200 calls across 60 files) — 11 macros referencing global candle settings
- **Local-define helpers** (TRUE_RANGE, round_pos, CALC_TERMS, etc.) — small utility macros/functions used by specific indicator families
- **Remaining cross-language macros** missed by the replacement script (ARRAY_LOCAL, ENUM_CASE, etc.)
- **Global mutable state** (candle settings, unstable period) — not thread-safe

This spec covers the architecture for resolving all of these.

**Out of scope (deferred):**
- Pre-compute pattern for internal function variants (EMA's `optInK_1`, PO's `tempBuffer`/`doPercentageOutput`). Will be designed after helper inlining is working — the inlining machinery built here is the foundation that pattern needs.
- Hilbert transform macros (`HILBERT_VARIABLES`, `INIT_HILBERT_VARIABLES`, `DO_HILBERT_ODD`, `DO_HILBERT_EVEN`, `DO_PRICE_WMA`) — ~140 calls across 7 files (ht_dcperiod, ht_dcphase, ht_phasor, ht_sine, ht_trendline, ht_trendmode, mama). These are complex stateful macro blocks with heavy internal state. They need their own helper files in `ta_func_defs/helpers/` using the same architecture described here, but the extraction and parameterization is a separate task.

---

## 1. Helper File Structure

Helper functions live in `ta_func_defs/helpers/`, grouped by domain:

```
ta_func_defs/helpers/
  candlestick.c      — 11 candle helpers
  range.c             — TRUE_RANGE
  rounding.c          — round_pos, SAR_ROUNDING
```

**Statement-block macros that are NOT helper functions** — these mutate multiple local variables and cannot be represented as pure functions. The replacement script should **inline-expand** them directly at their call sites:

- **`CALCULATE_AD`**: mutates 5 locals (`high`, `low`, `tmp`, `close`, `ad`) + increments `today`. Inline-expand at its 3 call sites in `adosc.c`.
- **`CALC_TERMS(day)`**: mutates 6 locals (`tempLT`, `tempHT`, `tempCY`, `trueLow`, `closeMinusTrueLow`, `trueRange`). Inline-expand at its 6 call sites in `ultosc.c`.
- **`PRIME_TOTALS(a, b, period)`**: loops internally using `CALC_TERMS`, accumulates into output params. Inline-expand at its 4 call sites in `ultosc.c`.

No YAML files for helpers. The parser extracts function signatures and bodies directly from the C source.

Each helper is a **real C function with explicit parameters** — no implicit array references, no global state access:

```c
// candlestick.c

double ta_realbody(double close, double open) {
    return fabs(close - open);
}

int ta_candlecolor(double close, double open) {
    return (close >= open) ? 1 : -1;
}

double ta_uppershadow(double high, double close, double open) {
    return high - (close >= open ? close : open);
}

double ta_lowershadow(double low, double close, double open) {
    return (close >= open ? open : close) - low;
}

double ta_highlowrange(double high, double low) {
    return high - low;
}

int ta_realbodygapup(double open1, double close1, double open2, double close2) {
    return (fmin(open1, close1) > fmax(open2, close2)) ? 1 : 0;
}

int ta_realbodygapdown(double open1, double close1, double open2, double close2) {
    return (fmax(open1, close1) < fmin(open2, close2)) ? 1 : 0;
}

int ta_candlegapup(double low1, double high2) {
    return (low1 > high2) ? 1 : 0;
}

int ta_candlegapdown(double high1, double low2) {
    return (high1 < low2) ? 1 : 0;
}

double ta_candlerange(int rangeType, double open, double high, double low, double close) {
    switch (rangeType) {
        case 0: return fabs(close - open);       /* RealBody */
        case 1: return high - low;                /* HighLow */
        case 2: return high - low - fabs(close - open); /* Shadows */
        default: return 0.0;
    }
}

double ta_candleaverage(int rangeType, int avgPeriod, double factor, double sum,
                        double open, double high, double low, double close) {
    double avg = (avgPeriod != 0)
        ? sum / avgPeriod
        : ta_candlerange(rangeType, open, high, low, close);
    double divisor = (rangeType == 2) ? 2.0 : 1.0; /* Shadows / 2 */
    return factor * avg / divisor;
}
```

```c
// range.c

double ta_true_range(double th, double tl, double yc) {
    double range = th - tl;
    double tmp = fabs(th - yc);
    if (tmp > range) range = tmp;
    tmp = fabs(tl - yc);
    if (tmp > range) range = tmp;
    return range;
}
```

```c
// rounding.c

double ta_round_pos(double x) {
    return floor(x + 0.5);
}

double ta_sar_rounding(double x) {
    return x; /* no-op in C; other backends may implement rounding */
}
```

Each helper file gets corresponding tests in the Rust codegen test suite (`tools/ta_codegen/tests/`). Tests verify:
- The parser correctly extracts helper function signatures and bodies from the C source
- Each backend renders the inlined helper correctly (C, Rust, Java)
- Known input/output pairs produce expected results

---

## 2. Call Site Transformation

A Python script (extending `scripts/replace_macros.py` or a new script) rewrites macro calls in indicator source files to explicit helper function calls.

### Candlestick simple helpers

| Before | After |
|--------|-------|
| `TA_REALBODY(i)` | `ta_realbody(inClose[i], inOpen[i])` |
| `TA_CANDLECOLOR(i)` | `ta_candlecolor(inClose[i], inOpen[i])` |
| `TA_UPPERSHADOW(i)` | `ta_uppershadow(inHigh[i], inClose[i], inOpen[i])` |
| `TA_LOWERSHADOW(i)` | `ta_lowershadow(inLow[i], inClose[i], inOpen[i])` |
| `TA_HIGHLOWRANGE(i)` | `ta_highlowrange(inHigh[i], inLow[i])` |
| `TA_REALBODYGAPUP(i, j)` | `ta_realbodygapup(inOpen[i], inClose[i], inOpen[j], inClose[j])` |
| `TA_REALBODYGAPDOWN(i, j)` | `ta_realbodygapdown(inOpen[i], inClose[i], inOpen[j], inClose[j])` |
| `TA_CANDLEGAPUP(i, j)` | `ta_candlegapup(inLow[i], inHigh[j])` |
| `TA_CANDLEGAPDOWN(i, j)` | `ta_candlegapdown(inHigh[i], inLow[j])` |

### Candlestick settings helpers

| Before | After |
|--------|-------|
| `TA_CANDLERANGE(Set, i)` | `ta_candlerange(Set_rangeType, inOpen[i], inHigh[i], inLow[i], inClose[i])` |
| `TA_CANDLEAVGPERIOD(Set)` | `Set_avgPeriod` |
| `TA_CANDLEAVERAGE(Set, sum, i)` | `ta_candleaverage(Set_rangeType, Set_avgPeriod, Set_factor, sum, inOpen[i], inHigh[i], inLow[i], inClose[i])` |

Where `Set` is a candle setting name like `BodyLong`, `BodyShort`, `ShadowLong`, etc. The variables `Set_rangeType` and `Set_avgPeriod` are unpacked from candle settings at function entry (see section 4).

### Local-define helpers

| Before | After |
|--------|-------|
| `TRUE_RANGE(th, tl, yc, out)` | `out = ta_true_range(th, tl, yc)` |
| `round_pos(x)` | `ta_round_pos(x)` |
| `CALC_TERMS(day)` | (inline-expand at call sites — see section 1) |
| `PRIME_TOTALS(a, b, period)` | (inline-expand at call sites — see section 1) |
| `CALCULATE_AD` | (inline-expand at call sites — see section 1) |
| `SAR_ROUNDING(x)` | `ta_sar_rounding(x)` |

The script handles regex matching with arbitrary index expressions (not just single variables — expressions like `i-1`, `lookbackTotal`, etc.).

---

## 3. Inlining Mechanism

The codegen inlines helper function bodies at call sites during generation, avoiding function call overhead.

### Load phase

At startup, the codegen:
1. Scans `ta_func_defs/helpers/*.c`
2. Parses each file in **helper mode** — a new parser mode that expects standalone utility functions rather than indicator structure (no lookback/main split, no YAML-defined params). Each function in the file produces a `HelperDef`.
3. Builds a **helper registry**: `HashMap<String, HelperDef>` where `HelperDef` contains the function name, parameter list (name + type), return type, and IR body (Vec<Statement>)

**Parser changes required:** The existing `c_source.rs` parser expects TA-Lib indicator files (lookback + main function with specific structure). Helper files are simpler — just standalone C functions. Add a `parse_helper_file()` entry point that:
- Tokenizes the file normally
- Parses each top-level function definition (return type, name, params, body)
- Returns `Vec<HelperDef>` instead of `ParsedCSource`
- Reuses all existing expression/statement parsing — only the top-level structure differs

### Generate phase

When a backend renders an indicator and encounters a function call IR node:
1. Check the helper registry for the function name
2. If **not found**: render as a normal function call (math functions, other indicators, etc.)
3. If **found**: perform argument substitution and inline the body

### Argument substitution

For a helper like:
```
ta_realbody(close, open) → fabs(close - open)
```

Called as:
```
ta_realbody(inClose[i], inOpen[i])
```

The inliner replaces every reference to `close` with `inClose[i]` and `open` with `inOpen[i]` in the helper's IR, then emits the resulting expression.

### Expression vs. block inlining

- **Single-expression helpers** (ta_realbody, ta_candlecolor, etc.): inline as an expression directly in place of the function call
- **Multi-statement helpers** (ta_candlerange with switch, ta_true_range with conditionals): emit as a block with a temporary variable holding the result

Example of multi-statement inlining:
```c
// Original call: x = ta_true_range(high, low, prevClose);
// Inlined (note unique suffix _0 to avoid name collisions):
double _tr_tmp_0;
{
    double _range_0 = high - low;
    double _tmp_0 = fabs(high - prevClose);
    if (_tmp_0 > _range_0) _range_0 = _tmp_0;
    _tmp_0 = fabs(low - prevClose);
    if (_tmp_0 > _range_0) _range_0 = _tmp_0;
    _tr_tmp_0 = _range_0;
}
x = _tr_tmp_0;
```

**Name collision avoidance:** The inliner maintains a monotonic counter per function being generated. Each inlining of a multi-statement helper appends `_N` (where N is the counter value) to all local variable names in the inlined body. This prevents collisions when the same helper is inlined multiple times in one function (common for candlestick indicators calling `ta_candlerange` many times).

**Switch statements in helpers:** `ta_candlerange` contains a `switch` on `rangeType`. The inliner emits the full switch at every call site — no constant folding. The rangeType is a runtime value from candle settings, so the switch is necessary.

### Per-backend rendering

Each backend renders the inlined IR in its own idiom. The inlining happens at the IR level (before backend rendering), so:
- C backend: emits C math (`fabs()`, ternary, etc.)
- Rust backend: emits Rust idioms (`.abs()`, `f64::max()`, etc.)
- Java backend: emits Java idioms (`Math.abs()`, `Math.max()`, etc.)

---

## 4. Candle Settings and Unstable Period

### Problem

`TA_Globals->candleSettings[SET]` and `TA_Globals->unstablePeriod[FUNC]` are global mutable state. Not thread-safe.

### Solution

**Rust, Java:** Move candle settings and unstable period to fields on the Core instance. Each thread creates its own Core with its own configuration.

**C:** Keep `TA_Globals->` for backwards compatibility. The C backend continues emitting global state references. Thread-safe C can be added later as an additional API if needed.

**.NET:** Excluded from this work. The .NET backend currently only generates declarations/wrappers, not function bodies. It does not render indicator logic, so helper inlining and Core-instance patterns do not apply.

### Per-indicator unpacking

For candlestick indicators, the codegen detects which candle settings the indicator uses (from its helper calls) and emits unpacking lines at the top of the function body. Each candle setting has three properties: `rangeType`, `avgPeriod`, and `factor`.

**Lookback functions:** Lookback functions also reference candle avg periods (`TA_CANDLEAVGPERIOD(BodyLong)` → `Set_avgPeriod`). These need candle settings too:
- **C:** Lookback functions access `TA_Globals->` directly (unchanged)
- **Rust:** Lookback functions are methods on Core (`self.cdl2crows_lookback()`), so they access `self.candle_settings` — same as the main function
- **Java:** Lookback functions are instance methods, access `this.candleSettings`

Rust example:
```rust
let body_long_range_type = self.candle_settings.body_long.range_type;
let body_long_avg_period = self.candle_settings.body_long.avg_period;
let body_short_range_type = self.candle_settings.body_short.range_type;
// ... only the settings this indicator actually uses
```

Java example:
```java
int bodyLongRangeType = this.candleSettings.bodyLong.rangeType;
int bodyLongAvgPeriod = this.candleSettings.bodyLong.avgPeriod;
```

C example (unchanged — uses globals):
```c
int BodyLong_rangeType = TA_Globals->candleSettings[TA_BodyLong].rangeType;
int BodyLong_avgPeriod = TA_Globals->candleSettings[TA_BodyLong].avgPeriod;
```

### Defaults

Core instances initialize candle settings and unstable periods with the same defaults the globals have today. Behavior is identical out of the box.

### Unstable period

Same pattern as candle settings. Indicators that reference `TA_Globals->unstablePeriod[TA_FUNC_UNST_X]` get it from Core in Rust/Java, from globals in C.

---

## 5. Remaining Macro Cleanup

### Missed cross-language macros

Update `scripts/replace_macros.py` to handle:

| Macro | Expansion | Calls |
|-------|-----------|-------|
| `ARRAY_LOCAL(name, size)` | `double name[size];` | 27 |
| `ARRAY_ALLOC(name, size)` | remaining instances | 6 |
| `ENUM_CASE(type, c_val, pascal_val)` | `case c_val:` | 18 |
| `ENUM_DECLARATION(RetCode)` | `enum RetCode` | 1 |
| `TA_ARRAY_COPY(dst, dstOff, src, srcOff, n)` | `memcpy(&dst[dstOff], &src[srcOff], n * sizeof(double));` | 4 |
| `TA_INTERNAL_ERROR(code)` | `return TA_INTERNAL_ERROR;` | 2 |
| `CIRCBUF_INIT(name, size, ...)` | manual expansion | 1 |

### Math function mappings

Add to each backend's MATH_FUNCTIONS constant:

| Call | C | Rust | Java |
|------|---|------|------|
| `max(a, b)` | `fmax(a, b)` | `a.max(b)` | `Math.max(a, b)` |
| `min(a, b)` | `fmin(a, b)` | `a.min(b)` | `Math.min(a, b)` |
| `fmax(a, b)` | `fmax(a, b)` | `a.max(b)` | `Math.max(a, b)` |
| `fmin(a, b)` | `fmin(a, b)` | `a.min(b)` | `Math.min(a, b)` |
| `ABS(x)` | `fabs(x)` | `x.abs()` | `Math.abs(x)` |

---

## Deferred: Pre-Compute Pattern

Internal function variants (EMA's `optInK_1`, PO's `tempBuffer`/`doPercentageOutput`, MACD's renamed params, VAR's missing `optInNbDev`) require a separate design.

**Direction agreed upon:** Define a pre-compute step per indicator. The guarded (public) version inlines the pre-compute after validation. The unguarded version accepts pre-computed values directly as params. This eliminates runtime branching — the codegen resolves which version to emit.

**Dependency:** The inlining machinery built in this spec is the foundation the pre-compute pattern will use.

**Affected indicators:** ~4 (EMA, PO, MACD, VAR).

---

## Testing Strategy

- **Helper unit tests:** Each helper file gets a test verifying its logic with known inputs/outputs
- **Inlining tests:** Backend suite tests verifying that helper calls produce correct inlined output per backend
- **Call site transformation:** Verify the replacement script produces parseable C that generates correct output
- **Candle settings:** Test that Rust/Java backends emit Core-instance access, C backend emits globals
- **Regression:** Full build of all ~158 indicators across C, Rust, Java backends after all changes
