# Rust Generic Type System for ta_codegen

**Date:** 2026-03-12
**Status:** Approved (design phase)
**Scope:** Rust backend of `tools/ta_codegen` — how indicator functions are generated

## Problem

The current Rust backend generates separate functions for f64 and f32 (`ema()` + `ema_s()`). This doubles the API surface per indicator. Rust's monomorphization makes this unnecessary — a single generic function `ema<T>()` compiles to identical machine code as the concrete variants.

Additionally, the current backend lacks:
- Unguarded variants (skip parameter validation) — needed for cross-indicator calls
- Unchecked variants (skip bounds checks on array access) — needed for HFT use cases

## Design

### Function Variant Matrix

Every indicator generates **4 generic functions** plus a concrete lookback:

| Function | Generic | Safe | Validates Params | Bounds-Checked | Purpose |
|----------|---------|------|-----------------|----------------|---------|
| `ema_lookback` | No | Yes | Yes | N/A | Lookback period calculation |
| `ema<T>` | Yes | Yes | Yes | Yes | Default safe API |
| `ema_unguarded<T>` | Yes | Yes | No | Yes | Cross-indicator calls, hot paths |
| `ema_unchecked<T>` | Yes | No (`unsafe fn`) | Yes | No | HFT with user-validated inputs |
| `ema_unguarded_unchecked<T>` | Yes | No (`unsafe fn`) | No | No | Maximum throughput, caller guarantees everything |

**Call chain:**
- `ema<T>` validates params, delegates to `ema_unguarded<T>`
- `ema_unchecked<T>` validates params, delegates to `ema_unguarded_unchecked<T>`
- `ema_unguarded<T>` contains the real algorithm with `[]` indexing
- `ema_unguarded_unchecked<T>` contains the real algorithm with `get_unchecked()` / `get_unchecked_mut()`

**Lookback stays concrete** — it only deals with `i32` periods, no float math involved.

**Cross-indicator calls** (e.g., MACD calling EMA) pass through `T`: `self.ema_unguarded::<T>(...)`.

### Other Language Backends

For **C, Java, SWIG, .NET**, the pattern is 4 concrete functions per indicator (no generics):

| Function | Precision | Validates Params |
|----------|-----------|-----------------|
| `ema` | double | Yes |
| `ema_s` | single (float) | Yes |
| `ema_unguarded` | double | No |
| `ema_unguarded_s` | single (float) | No |

C has no generics. Java generics can't handle primitives without boxing (kills performance). .NET could support generics via `INumber<T>` in the future but keeps `_s` for now. SWIG follows C.

### The `TaFloat` Trait

Zero-dependency sealed trait covering all math operations used across 163 indicators.

```rust
/// Sealed floating-point trait for TA-Lib generic indicator functions.
///
/// Implemented for `f32` and `f64`. Cannot be implemented outside this crate.
pub trait TaFloat: private::Sealed + Copy + PartialOrd
    + std::ops::Add<Output = Self>
    + std::ops::Sub<Output = Self>
    + std::ops::Mul<Output = Self>
    + std::ops::Div<Output = Self>
    + std::ops::Rem<Output = Self>
    + std::ops::Neg<Output = Self>
    + std::ops::AddAssign
    + std::ops::SubAssign
    + std::ops::MulAssign
    + std::ops::DivAssign
    + std::ops::RemAssign
{
    // --- Constants ---
    fn ta_zero() -> Self;
    fn ta_one() -> Self;
    fn ta_epsilon() -> Self;

    // --- Conversions ---
    fn ta_from_f64(v: f64) -> Self;
    fn ta_from_i32(v: i32) -> Self;
    fn ta_to_f64(self) -> f64;

    // --- Absolute value (14 indicators) ---
    fn ta_abs(self) -> Self;

    // --- Trigonometric (11 indicators) ---
    fn ta_sin(self) -> Self;
    fn ta_cos(self) -> Self;
    fn ta_tan(self) -> Self;
    fn ta_asin(self) -> Self;
    fn ta_acos(self) -> Self;
    fn ta_atan(self) -> Self;

    // --- Hyperbolic (3 indicators) ---
    fn ta_sinh(self) -> Self;
    fn ta_cosh(self) -> Self;
    fn ta_tanh(self) -> Self;

    // --- Logarithmic/exponential (3 indicators) ---
    fn ta_ln(self) -> Self;
    fn ta_log10(self) -> Self;
    fn ta_exp(self) -> Self;

    // --- Root (3 indicators) ---
    fn ta_sqrt(self) -> Self;

    // --- Rounding ---
    fn ta_ceil(self) -> Self;
    fn ta_floor(self) -> Self;
    fn ta_round(self) -> Self;
}

mod private {
    pub trait Sealed {}
    impl Sealed for f32 {}
    impl Sealed for f64 {}
}
```

**22 trait methods** (16 math operations + 3 constants + 3 conversions). `Rem`/`RemAssign` are provided via operator supertraits for `BinOp::Mod`.

All trait methods use a `ta_` prefix (e.g., `ta_sqrt`, `ta_from_f64`) to avoid name collisions with inherent `f32`/`f64` methods in the standard library. This ensures unambiguous method resolution in generic contexts.

Each `impl` method is `#[inline(always)]` and delegates to the built-in intrinsic (e.g., `fn ta_sqrt(self) -> Self { f64::sqrt(self) }`). After monomorphization, the compiler generates identical machine code to hand-written concrete implementations.

**Sealed via `private::Sealed`** — users can use `TaFloat` as a trait bound but cannot implement it for new types. This allows adding methods without breaking changes.

### Crate Structure

```
rust/src/
  lib.rs                    pub mod ta_func;
  ta_func/
    mod.rs                  Core struct, RetCode, pub use float::TaFloat
    float.rs                TaFloat trait + f32/f64 impls (hand-written, not generated)
    ema.rs                  generated: impl Core { ema<T>, ema_unguarded<T>, ... }
    sma.rs                  generated
    ...                     one file per indicator
```

`float.rs` is hand-written and part of the crate's stable public API. Generated indicator files `use super::*` which pulls in `TaFloat`.

### Backend Code Generation Changes

**Current `rust_lang.rs` structure** (being replaced):
```
gen_impl_block()
  gen_lookback()
  gen_public_func(single_precision=false)       // ema()
  gen_internal_func(single_precision=false)      // ema_logic()
  gen_public_func(single_precision=true)         // ema_s()
  gen_internal_func(single_precision=true)       // ema_logic_s()
  gen_unsafe_func(single_precision=false)        // TODO stub
  gen_unsafe_func(single_precision=true)         // TODO stub
```

**New structure:**
```
gen_impl_block()
  gen_lookback()                                 // unchanged
  gen_guarded_func()                             // ema<T: TaFloat>()
  gen_unguarded_func(unchecked=false)            // ema_unguarded<T>()
  gen_unchecked_func()                           // unsafe ema_unchecked<T>()
  gen_unguarded_func(unchecked=true)             // unsafe ema_unguarded_unchecked<T>()
```

The `single_precision: bool` parameter is removed from all render functions. Instead:

| Current output | New output |
|---------------|------------|
| `f64` / `f32` (for Real types) | `T` |
| `&[f64]` / `&[f32]` | `&[T]` |
| `0.0` (float literal) | `T::ta_from_f64(0.0)` |
| `period as f64` (int-to-float cast) | `T::ta_from_i32(period)` |
| `sqrt(x)` | `x.ta_sqrt()` |
| `TA_IS_ZERO(x)` | `x.ta_abs() < T::ta_epsilon()` |
| `inReal[today]` | `inReal[today]` (safe) / `*inReal.get_unchecked(today)` (unchecked) |
| `outReal[outIdx] = v` | `outReal[outIdx] = v` (safe) / `*outReal.get_unchecked_mut(outIdx) = v` (unchecked) |

The `unchecked: bool` flag is passed into `render_expr` and `render_statement` to control array access rendering. This avoids duplicating the full render pipeline.

### Expression Rendering Rules (Generic Context)

These rules apply inside the 4 generic indicator functions. `gen_lookback()` remains non-generic and continues using concrete `f64`/`i32`.

**`VarType::Real` in declarations:**
- Generic functions: `let mut tempReal: T;`
- Lookback: `let mut tempReal: f64;` (unchanged)

**`Expr::Literal(f64)` — all float literals:**
- Wrapped in `T::ta_from_f64()`: `0.0` → `T::ta_from_f64(0.0)`, `0.5` → `T::ta_from_f64(0.5)`
- This applies universally — in assignments, comparisons, arithmetic

**`Expr::Cast(VarType::Real, inner)` — float casts:**
- Cannot use `as T` (not valid Rust for generics)
- If inner is an integer expression: `T::ta_from_i32(inner)`
- If inner is a float expression (e.g., f32→f64 in old code): `T::ta_from_f64(inner.ta_to_f64())`
- Safe default for ambiguous cases: `T::ta_from_f64((inner).ta_to_f64())`

**`Expr::Cast(VarType::Integer, inner)` — integer casts:**
- Remains `(inner).ta_to_f64() as i32` or `(inner) as i32` — unchanged from current

**Built-in function call transformations (complete list):**

| Current | Generic |
|---------|---------|
| `UNSTABLE_PERIOD(id)` | `self.unstable_period[FuncUnstId::id as usize]` (unchanged) |
| `COMPATIBILITY` | `self.compatibility` (unchanged) |
| `IS_ZERO(x)` | `x.ta_abs() < T::ta_epsilon()` |
| `PER_TO_K(period)` | `T::ta_from_f64(2.0) / (T::ta_from_i32(period) + T::ta_one())` |
| `ARRAY_COPY(src, dst, count)` | `dst[..count].copy_from_slice(&src[..count])` (same types now, works for any `T: Copy`) |
| `xxx_lookback(params)` | `self.xxx_lookback(params)` (lookback returns `i32`, non-generic, unchanged) |
| `xxx(args)` (cross-indicator) | `self.xxx_unguarded(args)` — T inferred from argument types, no turbofish needed |

**Cross-indicator calls:** When indicator A calls indicator B internally, it calls the `_unguarded` variant (to avoid double-validation). The type parameter `T` is inferred from the slice arguments — no explicit turbofish required. The registry resolves bare names (e.g., `ema`) to `self.ema_unguarded` in the Rust backend.

**`PointerDeref` expressions:** `*outBegIdx`, `*outNBElement` — these dereference `&mut usize` parameters, not float data. No change needed under generics.

**Output parameter types:**
- `ParamType::Real` outputs → `&mut [T]` (generic)
- `ParamType::Integer` outputs → `&mut [i32]` (stays concrete)

**Optional input parameter types:**
- `ParamType::Integer` / `ParamType::Enum` → `i32` (stays concrete)
- `ParamType::Real` → `f64` (stays concrete — these are configuration values like `optInNbDevUp`, not price data)

### Cleanup

- Remove `single-precision = []` feature flag from `rust/Cargo.toml` — no longer needed since generics replace the `_s` suffix convention

### Performance Characteristics

- **Zero overhead**: monomorphization produces identical assembly to hand-written f64/f32 versions
- **Trait methods**: `#[inline(always)]` + intrinsic delegation = single CPU instructions after inlining
- **`T::ta_from_f64(0.0)`**: identity for f64, single `cvtsd2ss` for f32 (or compiler emits f32 constant directly)
- **`get_unchecked()`**: eliminates bounds checks. Note that in release builds, LLVM often elides bounds checks in tight loops anyway, so the `_unchecked` variants offer marginal-to-moderate improvement depending on loop structure
- **No allocations**: all functions operate on caller-provided slices

### Public API Surface Per Indicator

```rust
impl Core {
    // Lookback
    pub fn ema_lookback(&self, optInTimePeriod: i32) -> i32;

    // Safe, validated
    pub fn ema<T: TaFloat>(&self, startIdx: usize, endIdx: usize,
        inReal: &[T], optInTimePeriod: i32,
        outBegIdx: &mut usize, outNBElement: &mut usize, outReal: &mut [T]) -> RetCode;

    // Safe, unvalidated
    pub fn ema_unguarded<T: TaFloat>(&self, startIdx: usize, endIdx: usize,
        inReal: &[T], optInTimePeriod: i32,
        outBegIdx: &mut usize, outNBElement: &mut usize, outReal: &mut [T]) -> RetCode;

    // Unsafe, validated
    pub unsafe fn ema_unchecked<T: TaFloat>(&self, startIdx: usize, endIdx: usize,
        inReal: &[T], optInTimePeriod: i32,
        outBegIdx: &mut usize, outNBElement: &mut usize, outReal: &mut [T]) -> RetCode;

    // Unsafe, unvalidated
    pub unsafe fn ema_unguarded_unchecked<T: TaFloat>(&self, startIdx: usize, endIdx: usize,
        inReal: &[T], optInTimePeriod: i32,
        outBegIdx: &mut usize, outNBElement: &mut usize, outReal: &mut [T]) -> RetCode;
}
```

Usage:
```rust
let core = Core::new();

// Type inferred from input slice — no turbofish needed
let prices: Vec<f64> = vec![1.0, 2.0, 3.0, 4.0, 5.0];
let mut out = vec![0.0f64; 5];
let mut beg = 0usize;
let mut nb = 0usize;

core.ema(0, 4, &prices, 3, &mut beg, &mut nb, &mut out);

// Explicit f32
let prices_f32: Vec<f32> = vec![1.0, 2.0, 3.0, 4.0, 5.0];
let mut out_f32 = vec![0.0f32; 5];
core.ema(0, 4, &prices_f32, 3, &mut beg, &mut nb, &mut out_f32);

// Generic user code
fn analyze<T: TaFloat>(core: &Core, prices: &[T], out: &mut [T]) {
    let mut beg = 0;
    let mut nb = 0;
    core.ema(0, prices.len() - 1, prices, 14, &mut beg, &mut nb, out);
}
```

### Testing Strategy

1. **Compilation gate:** Every generated indicator file must compile with `cargo check`. This is the first validation — if the generic code has type errors, it fails here.

2. **Existing backend test suite:** The `backend_suite.rs` tests verify that generated code matches expected output. These tests will be updated to expect generic signatures (`<T: TaFloat>`) instead of concrete `f64`/`f32`.

3. **f64/f32 equivalence:** For each indicator, run the same input data through `ema::<f64>()` and `ema::<f32>()`, verify both produce valid results (f32 results will differ slightly due to precision, but must be within expected tolerance).

4. **Cross-language regression (ta_regtest):** The existing FFI test harness (`ta_regtest_rust`) links C tests against Rust implementations. The FFI wrappers in `rust/ffi/` monomorphize the generic functions to `f64` and expose them as `extern "C"`. If ta_regtest passes, the generic Rust code produces identical results to the C reference.

5. **TaFloat trait unit tests:** Verify that `f32` and `f64` implementations produce correct results for all 22 trait methods (compare against direct intrinsic calls).
