# Java Server Compilation Fixes — Round 2

## Problem

After Round 1 fixes (parser VarDecl flattening/dedup, null-check elimination, ALLOC_ERR mapping, MInteger wrapping, duplicate return removal, MAType enum, class field cleanup), the Java server still has ~3739 compilation errors across 8 root causes.

## Reference

For every questionable pattern, consult:
- Legacy `gen_code.c` (`src/ta_abstract/gen_code/gen_code.c`) — the original code generator
- Existing `Core.java` in the Java SDK (`src/ta_func/java/Core.java` or similar) — known-working output
- C source in `ta_func_defs/` — the IR source of truth

---

## Fix 1: candleSettings Field in Server Template (~1800 errors)

**Current:** The `Core` class declares `int[] unstablePeriod` and `Compatibility compatibility` as class fields. The `candle_settings.rs` module generates unpacking code like `this.candleSettings.bodyLong.rangeType`, but the field itself is never declared.

**Change:** In `server_gen.rs`, inside `generate_java_server()`:

1. Add a `CandleSetting` class definition with fields matching the C struct: `int rangeType`, `int avgPeriod`, `double factor`.
2. Add a `CandleSettingType` enum matching `TA_CandleSettingType` from `ta_defs.h` — the variant names that `candle_settings.rs` already references (e.g., `bodyLong`, `bodyVeryLong`, `bodyShort`, `bodyDoji`, `shadowLong`, `shadowVeryLong`, `shadowShort`, `shadowVeryShort`, `near`, `far`, `equal`).
3. Add `CandleSetting[] candleSettings = new CandleSetting[CandleSettingType.values().length];` as a `Core` class field.
4. Add initialization in a constructor or instance initializer block that populates default values matching `ta_utility.h` defaults.

**Reference:** Check `gen_code.c` for how it generates the candle settings initialization, and `candle_settings.rs` for the exact field/enum names already in use.

**Why server template:** This is shared infrastructure for all candlestick indicators — not per-function.

**Risk:** Low. Additive change — only adds missing declarations.

---

## Fix 2: Hoisted Block Local Declarations (~920 errors)

**Current:** When multi-statement helpers (like `ta_true_range`) get inlined via `inline_block()` in `helper_registry.rs`, the helper's local variables are renamed (e.g., `tmp` → `tmp_0`, `range` → `range_0`) and included as `VarDecl` statements in the hoisted body. But `render_statement()` in `java.rs` returns `String::new()` for all `VarDecl` — by design, since `gen_func()` hoists main-body declarations to the top. The hoisted block bodies also go through `render_statement`, so their local declarations silently vanish.

**Change:** In `render_hoisted_blocks()` in `java.rs`, before rendering the body statements, scan the body for `VarDecl` entries and emit their Java declarations inline. Only scan the top level of the body (not recursively) since `VarDecl` entries from `inline_block` are always top-level statements.

```
render_hoisted_blocks:
  for each (temp_name, var_type, body):
    emit output temp declaration       ← already works
    scan body for VarDecl statements   ← NEW
    emit local declarations            ← NEW
    render body statements             ← VarDecl still skipped by render_statement, fine
```

**Covers:** `tmp_N`, `range_N`, `i` (ForC loop vars in hoisted blocks), candlestick locals (`close`, `open`, `upsum`, `downsum`, `todaySum`, `todayDev`), ADX vars (`Plus_dm`, `Plus_di`, `Minus_dm`, `Minus_di`), and hoisted output temps (`_tempReal`, `_outIdx`, `_periodTotal2`, `_startSum`, `_endSum`, `_meanValue2`).

**Why backend:** The hoisting mechanism is backend-specific. The IR and helper registry are correct — the body includes VarDecls. The Java backend just doesn't render them in this context.

**Risk:** Low. Only affects hoisted block rendering — main body declaration logic is untouched.

---

## Fix 2b: Extend `hoist_block_helpers` to All Expression Contexts (~756 errors)

**Current:** `hoist_block_helpers()` is only called in two places in `java.rs`:
1. VarDecl init expressions (line ~321)
2. Assign value expressions (line ~506)

It is NOT called for `Statement::If` conditions, `Statement::While` conditions, `Statement::ForC` conditions, or `Statement::Switch` expressions. When multi-statement helpers like `ta_candleaverage` appear in these contexts, they pass through as raw function calls instead of being inlined. This produces "no suitable method found for ta_candleaverage" errors — 756 of them (378 double + 378 float variants).

**Change:** In `render_statement()`, add `hoist_block_helpers()` calls before rendering expressions in:
- `Statement::If` — hoist from condition, emit hoisted blocks before the `if`
- `Statement::While` — hoist from condition, emit before the `while`
- `Statement::DoWhile` — hoist from condition, emit before the condition check (inside the loop body, before the condition evaluation)
- `Statement::ForC` — hoist from condition, emit before the `for`
- `Statement::Switch` — hoist from the switch expression

The pattern is the same as what's already done for Assign values: call `hoist_block_helpers` on the expression, emit the hoisted blocks, then render the statement with the simplified expression.

**Reference:** Check how `render_statement`'s `Statement::Assign` arm already uses `hoist_block_helpers` — follow the same pattern.

**Risk:** Medium. The `DoWhile` case needs care — the condition is evaluated at the end of each iteration, so hoisted blocks may need to be emitted inside the loop body rather than before the loop.

---

## Fix 3: Enum/Constant Name Mapping (~34 errors)

### Fix 3a: `TA_MAType_*` in Expr::Var Context (~4 errors)

**Current:** Task 7 fixed `TA_MAType_SMA` in switch labels via `render_java_switch_label`. But `TA_MAType_SMA` also appears in plain expressions (assignments, comparisons like `optInMAType == TA_MAType_SMA`). The `Expr::Var` mapping in `render_expr` doesn't handle these.

**Change:** In `render_expr`'s `Expr::Var` arm, add mappings for all `TA_MAType_*` constants:
- `"TA_MAType_SMA"` → `"MAType.Sma"`
- `"TA_MAType_EMA"` → `"MAType.Ema"`
- `"TA_MAType_WMA"` → `"MAType.Wma"`
- etc. for all 9 variants

**Reference:** Check `ta_defs.h` for the full list of `TA_MAType_*` constants.

### Fix 3b: Hilbert Transform `FuncUnstId` Naming (~30 errors)

**Current:** Generated code emits `FuncUnstId.Ht_dcperiod` but the enum defines `FuncUnstId.HtDcPeriod`. The rendering of `UNSTABLE_PERIOD()` macro calls produces the wrong variant name.

**Change:** Find where `FuncUnstId` variant names are rendered (likely in `render_func_call` or `render_expr` when handling the unstable period macro). Ensure the function ID maps to the correct PascalCase variant name. This may be a simple string-transform issue (strip `TA_`, convert `Ht_dcperiod` to `HtDcPeriod`).

**Reference:** Check the `FuncUnstId` enum definition in `server_gen.rs` for the canonical PascalCase names.

**Risk:** Low. String mapping only.

---

## Fix 4: Type Coercion Issues (~128 errors)

### Fix 4a: Int-to-Boolean in Conditions (20 errors)

**Current:** C allows `if(bufferIsAllocated)` where `bufferIsAllocated` is `int`. Java requires `boolean`. The backend emits the C pattern verbatim.

**Change:** In `render_statement`'s `Statement::If` and `Statement::While` arms, detect when the condition is a plain `Expr::Var` or other non-boolean expression (not already a comparison `BinOp(_, Eq|Ne|Lt|Gt|Le|Ge, _)` or logical `BinOp(_, And|Or, _)` or `Not(_)`). Wrap with `!= 0`.

**Reference:** Check `gen_code.c` for how the legacy generator handled this. The existing `Core.java` should show the correct pattern.

### Fix 4b: Ternary Int/Boolean Mixing in Candlestick Patterns (72 errors)

**Current:** Candlestick gap macros produce `(comparison) ? (1) : (0)` which yields `int`, then the result is used in `&&` chains. Java won't `&&` an `int`.

**Change:** In `render_expr`'s `Expr::Ternary` arm, detect the pattern where both branches are integer literals `1` and `0` (or `-1` via `0-1`). When detected, render just the condition directly (the ternary is a no-op boolean-to-int-to-boolean round-trip). If the ternary branches are `1` and `-1` (directional indicator), keep the ternary but ensure the result gets `!= 0` when used in boolean context.

**Reference:** Check how `gen_code.c` handles `TA_CANDLEGAPUP`/`TA_CANDLEGAPDOWN` macros in Java output.

### Fix 4c: Float[]/Double[] Array Comparisons (36 errors)

**Current:** C source compares pointers for aliasing checks (`inReal == outRealUpperBand`). In float-precision overloads, `inReal` is `float[]` and `outRealUpperBand` is `double[]` — Java forbids comparing these types with `==`.

**Change:** In the float-precision function variants, these aliasing checks are always false (different types can never alias). Replace the comparison with `false` when the operand types differ. This can be detected in `render_expr` when rendering `BinOp(Eq, ...)` — if one side is a float array parameter and the other is a double array, emit `false`.

**Reference:** Check the existing `Core.java` float overloads to see how aliasing checks are handled.

**Risk:** Medium. The ternary simplification (4b) needs care to distinguish boolean ternaries from value ternaries.

---

## Fix 5: Variable Type Inference (~72 errors)

### Fix 5a: `tempMAType` Declared as `int` Instead of `MAType` (8 errors)

**Current:** In `macdext` and similar, a temporary variable `tempMAType` is declared as `int` in C and used to swap `MAType` enum values. The parser infers `int` from the C type. Java needs it as `MAType`.

**Change:** Pre-scan the function body for assignments where an `int`-typed variable receives a value from a `MAType`-typed parameter. If found, change the variable's declaration to `MAType` instead of `int`. This can be done with a scan similar to `collect_address_of_vars` — check assignment RHS against known enum-typed parameters.

**Reference:** Check `gen_code.c` and `Core.java` for how `tempInteger`/`tempMAType` variables are handled in `macdext`.

### Fix 5b: Scalar `double` Used as Array/Pointer (64 + 9 errors)

**Current:** Functions like ATR call `smaLogic(..., prevATR)` where `prevATR` is a `double` scalar, but the method expects `double[]` (output array). In C, a pointer to a single double works as a one-element array.

**Symptoms — two distinct error types from the same root cause:**
1. **"no suitable method found" (9 errors):** `smaLogic(int, int, double[], int, MInteger, MInteger, double)` — last arg is `double` but method expects `double[]`
2. **"double cannot be dereferenced" (64 errors):** `prevATR.value *= ...` — the `collect_address_of_vars` pre-scan detects `&prevATR` in AddressOf context and wraps accesses with `.value`, but `prevATR` is declared as `double` (not MInteger), so `.value` is invalid

**Change:** This requires understanding how the legacy `gen_code.c` handled these cases. Options:
1. Declare as `double[] prevATR = new double[1]` and use `prevATR[0]` for scalar access — this resolves BOTH symptom types (array parameter match + indexable instead of `.value`)
2. Create a method overload that accepts scalar
3. Use a different calling pattern based on what `Core.java` does

The fix must also ensure `collect_address_of_vars` does NOT flag these variables for MInteger wrapping — they're pointer-to-double, not pointer-to-int.

**Reference:** This is the most complex fix. Check `gen_code.c`'s `extractTALogic()` and the existing `Core.java` ATR/NATR implementations to see the known-working pattern. The fix must match whatever the reference uses.

**Risk:** High for 5b. Needs careful tracing of the legacy approach before implementing.

---

## Fix 6: Missing `linearreg*Lookback` Methods (12 errors)

**Current:** Three lookback functions are called but never defined: `linearregAngleLookback`, `linearregInterceptLookback`, `linearregSlopeLookback` (4 errors each = 12). These are cross-indicator lookback calls from the `linearregAngle`, `linearregIntercept`, and `linearregSlope` functions.

**Change:** Investigate how these lookback functions are generated. The issue may be:
1. The functions aren't in the `FuncDef` list that drives forward declaration generation
2. The naming convention doesn't match (e.g., `linearRegAngleLookback` vs `linearregAngleLookback`)
3. The lookback functions are aliased to `linearregLookback` in C but not in Java

**Reference:** Check `gen_code.c` and `Core.java` for how these lookback aliases are handled.

**Risk:** Low. Likely a naming or aliasing issue.

---

## Fix 7: Cross-Indicator Method Signatures (9 errors)

**Current:** Functions like `bbands`, `stoch`, `ma`, etc. fail with "no suitable method found" when called cross-indicator. The callers pass arguments with wrong types — `MAType` vs `int`, scalar vs array.

**Expected:** These errors are downstream of Fixes 2b, 4c, 5a, and 5b. Once helper inlining is extended, variable types are correct, and MAType handling works, the method calls should resolve.

**Change:** No separate fix. Verify after other fixes land. If any remain, trace the specific signature mismatch and fix case-by-case.

**Risk:** Low — downstream of other fixes.

---

## Implementation Order

1. Fix 1 (candleSettings) — eliminates ~1800 errors, no dependencies
2. Fix 2 (hoisted block locals) — eliminates ~920 errors, no dependencies
3. Fix 2b (hoist in conditions) — eliminates ~756 errors, no dependencies
4. Fix 3 (enum/constant names) — eliminates ~34 errors, no dependencies
5. Fix 4a (int-to-boolean) — eliminates ~20 errors, no dependencies
6. Fix 4b (ternary boolean) — eliminates ~72 errors, no dependencies
7. Fix 4c (array comparison) — eliminates ~36 errors, no dependencies
8. Fix 5a (MAType inference) — eliminates ~8 errors, no dependencies
9. Fix 5b (scalar-as-array) — eliminates ~73 errors, depends on reference investigation
10. Fix 6 (linearreg lookbacks) — eliminates ~12 errors, no dependencies
11. Fix 7 (method signatures) — verification only, depends on all above

## Verification

After all fixes: `javac TaCodegenServe.java` should produce 0 errors.
