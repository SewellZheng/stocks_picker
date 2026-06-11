# Java Server Compilation Fixes — Design Spec

**Date:** 2026-03-14
**Status:** Approved
**Context:** The Java codegen server (TaCodegenServe.java) has ~30K compilation errors after the stdlib mapping (malloc/free/memcpy/sizeof) was completed. These errors fall into 7 categories traced to the parser, Java backend, and server template.

---

## Problem Summary

The ta_codegen pipeline parses C source files into an IR, then renders per-language output. The Java server output does not compile due to:

1. **Undefined variables** (~60% of errors) — multi-var declarations like `int i, j, outputSize, bufferSize, lookbackTotal;` are parsed into `Statement::Block { body: [VarDecl, ...] }`, but java.rs only collects direct `VarDecl` entries from `func.body`, missing those nested in blocks.

2. **Duplicate variable declarations** — C source has `double *tempBuffer1;` (declaration) then `double *tempBuffer1 = malloc(...)` (re-declaration with assignment). Parser captures both as `VarDecl`, producing illegal duplicate declarations in Java.

3. **Post-allocation null-check blocks** — `if(!tempBuffer1) { return TA_ALLOC_ERR; }` after malloc. Meaningless in Java (`new` throws OOM, never returns null). Invalid syntax (`!` on array reference).

4. **Undefined `ALLOC_ERR` constant** — `TA_ALLOC_ERR` not mapped in the Java backend constant table.

5. **Missing `MAType` enum** — Core_MA.java references `TA_MAType_SMA` etc. but the enum is not defined in the server template.

6. **MInteger type mismatch** — Cross-indicator calls (e.g., DEMA calling EMA) pass `int` variables where `MInteger` output params are expected.

7. **Duplicate return statements** — `return RetCode.Success; return RetCode.Success;` at end of functions.

---

## Design

### Philosophy

Fix at the lowest appropriate level:
- **Parser** for issues that produce unclean IR (benefits all backends)
- **Backend** for language-specific rendering concerns
- **Server template** for server-specific type definitions

### Fix 1: Multi-var VarDecl Flattening (Parser — `c_source.rs`)

**Current:** `parse_var_decl()` for `int i, j, k;` returns `Statement::Block { body: [VarDecl("i"), VarDecl("j"), VarDecl("k")] }`.

**Change:** Return `Vec<Statement>` from `parse_var_decl()`. The caller flattens individual `VarDecl` entries directly into the function body instead of wrapping in a Block.

**Call sites:** `parse_var_decl()` is called from two places in `parse_statement()` (lines ~650 and ~654 for `const` declarations). Both currently expect a single `Statement` return. Change them to call a new `parse_var_decl_list()` that returns `Vec<Statement>`, and use `extend()` to push into the body vector instead of `push()`.

**Why parser, not backend:** The Block wrapper is an artifact of the parser's return type constraint. The IR should represent each declaration individually — that's cleaner data for all consumers.

**Risk:** Low. The Block wrapper serves no semantic purpose beyond grouping a parser-internal concern.

### Fix 2: Duplicate VarDecl → Assignment (Parser — `c_source.rs`)

**Current:** Both `double *tempBuffer1;` and `double *tempBuffer1 = malloc(...)` produce separate `VarDecl` entries.

**Change:** Track declared variable names using a `HashSet<String>` initialized at the start of each function body parse (cleared at function boundaries). When a variable name is re-declared:
- If re-declaration has an initializer → emit `Statement::Assign { target: Var(name), value: init_expr }` instead of `VarDecl`
- If no initializer → skip entirely (already declared)

**Why parser, not backend:** The C source uses re-declaration as a scoping idiom. The IR should normalize this to "declare once, assign later" — which is the actual semantic intent and is valid across all target languages.

**Risk:** Medium. Need to handle edge cases:
- Re-declaration with different type (unlikely in these sources, but should warn)
- Re-declaration in nested scope (C block scoping) vs same scope — for now, treat all declarations as function-level scope (matches how the ta_func_defs sources are structured)

### Fix 3: Post-Allocation Null-Check Elimination (Backend — `java.rs`, `rust_lang.rs`)

**Pattern:**
```c
tempBuffer1 = malloc((bufferSize) * sizeof(double));
if( !tempBuffer1 ) {
    *outBegIdx = 0;
    *outNBElement = 0;
    return TA_ALLOC_ERR;
}
```

**Change:** In the backend's `render_statement` for `if` statements, detect the IR pattern: an `If` statement whose `then_body` contains a `Statement::Return` with value `Expr::Var("ALLOC_ERR")`. Skip the entire if-block (including the assignments like `*outBegIdx = 0` that precede the return within the then_body — those are part of the block being skipped).

**Why backend, not parser:** The null-check IS valid C and correct IR. It's only dead code because the backends map `malloc` to language-native allocation (`new` in Java, `vec!` in Rust) which can't fail silently. The parser shouldn't know about stdlib semantics.

**Applies to:** java.rs and rust_lang.rs.

### Fix 4: ALLOC_ERR Constant Mapping (Backend — `java.rs`)

**Change:** Add `"ALLOC_ERR" => "RetCode.AllocErr"` to the constant mapping in java.rs (alongside existing `"BAD_PARAM" => "RetCode.BadParam"` and `"SUCCESS" => "RetCode.Success"`). The IR stores these without the `TA_` prefix — confirmed by the C backend which maps `"ALLOC_ERR"` → `"TA_ALLOC_ERR"` at `c.rs:860`. Safety net for any ALLOC_ERR references that survive the null-check block elimination.

**Applies to:** java.rs. Rust equivalent if needed.

### Fix 5: MInteger Wrapping for Cross-Indicator Output Params (Backend — `java.rs`)

**Problem:** DEMA calls `ema(startIdx, endIdx, inReal, period, &firstEMABegIdx, &firstEMANbElement, firstEMA)`. The IR has `AddressOf(Var("firstEMABegIdx"))`. Java backend strips the `&` but `firstEMABegIdx` is declared as `int`, not `MInteger`.

**Change:** Pre-scan each function body for `AddressOf(Var(name))` patterns. Collect these names into a `HashSet<String>` ("address-of variables"), computed once per function before rendering begins. Then modify the existing rendering:

1. **Declaration (VarDecl loop, java.rs ~line 147):** When the VarDecl loop encounters an `Integer`-typed variable whose name is in the address-of set, emit `MInteger name = new MInteger();` instead of `int name;`. This is a check within the existing loop, not a separate pass.
2. **Value read:** The existing `Expr::PointerDeref(name)` handler (java.rs ~line 770) already renders `name.value` — this covers the C `*outBegIdx` pattern. For cases where the variable is read directly (without deref), render as `name.value` when the name is in the address-of set.
3. **Function call arg:** When variable appears in `AddressOf` context, render as just `name` (Java passes objects by reference). The existing `AddressOf` handler (java.rs ~line 773) already strips the `&` — this behavior is correct for MInteger objects.

**Why backend:** This is fundamentally about Java's value type vs reference type distinction. C uses `int*` for output params; Java uses `MInteger` wrappers. Rust uses `&mut i32`. Each language handles this differently.

### Fix 6: Duplicate Return Elimination (Backend — `java.rs`, `rust_lang.rs`)

**Change:** During statement rendering, track the last rendered statement. If consecutive `return` statements have identical values, skip the duplicate.

**Root cause:** The duplicate returns come from two sources layering a return: the IR body ends with `return TA_SUCCESS;` (from the C source), and the server template or function scaffolding may append another. The fix should detect and skip the second return during rendering, regardless of which layer produced it.

**Why backend:** Simple rendering-time check. Not worth an IR pass for a 3-line fix.

### Fix 7: MAType Enum Definition (Server Template — `server_gen.rs`)

**Change:** Generate the `MAType` enum in the server template, sourced from the registry (same metadata that drives `FuncUnstId`). Define all variants:
```java
enum MAType {
    Sma, Ema, Wma, Dema, Tema, Trima, Kama, Mama, T3
}
```

Also ensure the enum is registered in the backend's type registry so the existing `lookup_variant()` mechanism in `render_java_switch_label` (java.rs ~line 641) can resolve C-style `TA_MAType_SMA` to Java `Sma`. If `lookup_variant` doesn't cover this, add explicit mapping.

### Fix 8: Class-Level Field Cleanup (Server Template — `server_gen.rs`)

**Current:** Core class declares `int lookbackTotal, i, outIdx, trailingIdx;` as shared instance state.

**Change:** Remove these class-level field declarations. After Fix 1, all variables will be properly declared locally in each method via the flattened VarDecl statements. Keep only genuinely shared state:
- `int[] unstablePeriod` — per-function unstable period storage
- `Compatibility compatibility` — global compatibility mode

**Dependency risk:** This fix has a hard dependency on Fix 1 succeeding completely. If any function's multi-var declarations are missed by Fix 1, removing class-level fields would introduce new undefined variable errors. **Mitigation:** After Fix 1, verify by regenerating and checking that every function that previously used `lookbackTotal`, `i`, `outIdx`, `trailingIdx` now has local declarations for them before proceeding with Fix 8.

### Fix 9: Verify Supporting Type Definitions (Server Template — `server_gen.rs`)

Ensure the server template includes all referenced types:
- `RetCode` — enum with all return codes
- `MInteger` — wrapper class with `public int value`
- `Compatibility` — enum (Default, Metastock)
- `FuncUnstId` — enum for unstable period function IDs
- `MAType` — enum (added by Fix 7)

---

## Order of Operations

1. **Parser fixes (Fix 1-2)** — unblocks variable declarations across all backends
2. **Backend fixes (Fix 3-6)** — resolves Java-specific rendering issues
3. **Server template fixes (Fix 7-9)** — adds missing type definitions

Each step should measurably reduce the compilation error count.

## Verification

1. `cargo run -- generate-servers --backend=java` — regenerate Java server
2. `javac TaCodegenServe.java` — compile, target zero errors
3. `cd bin && ./ta_regtest --codegen --language=java` — run regression tests (once compiling)
4. `cd bin && ./ta_regtest --codegen --language=c` — verify C output is unchanged (no regressions)

**Intermediate verification after Fix 1:** Regenerate and grep for `lookbackTotal`, `outputSize`, `bufferSize`, `j` in generated Java to confirm they now have local declarations before proceeding to Fix 8 (class field removal).

## Rust Backend Parity

- Fix 1-2 (parser) benefit all backends automatically
- Fix 3, 4, 6 should also be applied to `rust_lang.rs`
- Fix 5 (MInteger) is Java-only — Rust uses `&mut` refs
- Fix 7-9 are Java server template specific

## Out of Scope

- Rust server generation (not yet implemented)
- SWIG server remaining issues
- .NET server
