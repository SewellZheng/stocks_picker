# Rust Generic Type System Implementation Plan

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace the `_s` suffix convention with Rust generics (`<T: TaFloat>`) and add unguarded + unchecked function variants to the ta_codegen Rust backend.

**Architecture:** Hand-write a sealed `TaFloat` trait in `rust/src/ta_func/float.rs`. Refactor `rust_lang.rs` to emit generic functions instead of duplicating code for f64/f32. Add `_unguarded` and `_unchecked` variants. Update all backend tests and downstream consumers (existing Rust tests, FFI wrappers).

**Tech Stack:** Rust, ta_codegen (Rust crate at `tools/ta_codegen/`)

**Spec:** `docs/superpowers/specs/2026-03-12-rust-generics-design.md`

**Scope note:** This plan covers the new Rust-based `ta_codegen` pipeline only. The legacy C-based `gen_code.c`/`gen_rust.c` pipeline is a separate system and not modified here.

---

## File Map

| File | Action | Purpose |
|------|--------|---------|
| `rust/src/ta_func/float.rs` | Create | TaFloat trait + f32/f64 impls (hand-written) |
| `rust/src/ta_func/mod.rs` | Modify | Wire in float module, re-export TaFloat |
| `rust/tests/float_test.rs` | Create | TaFloat trait unit tests |
| `tools/ta_codegen/src/backends/rust_lang.rs` | Modify | Core refactoring: generic rendering |
| `tools/ta_codegen/tests/backend_suite.rs` | Modify | Update variant checks for generic API |
| `rust/Cargo.toml` | Modify | Remove obsolete `single-precision` feature |
| `rust/tests/mult_test.rs` | Modify | Update `_s` calls to generic API |
| `rust/tests/sma_test.rs` | Modify | Update `_s` calls to generic API |
| `rust/tests/rsi_test.rs` | Modify | Update `_s` calls to generic API |
| `rust/ffi/src/generated.rs` | Modify | Update FFI wrappers (via gen_code, not ta_codegen) |
| `CLAUDE.md` | Modify | Document generics, remove stale `_s` references |

---

## Chunk 1: TaFloat Trait

### Task 1: Write TaFloat trait with unit tests

**Files:**
- Create: `rust/src/ta_func/float.rs`
- Create: `rust/tests/float_test.rs`
- Modify: `rust/src/ta_func/mod.rs`

- [ ] **Step 1: Write the TaFloat trait test file**

Create `rust/tests/float_test.rs`:

```rust
use ta_lib::ta_func::TaFloat;

#[test]
fn test_f64_constants() {
    assert_eq!(f64::ta_zero(), 0.0);
    assert_eq!(f64::ta_one(), 1.0);
    assert!(f64::ta_epsilon() > 0.0);
    assert!(f64::ta_epsilon() < 1e-10);
}

#[test]
fn test_f32_constants() {
    assert_eq!(f32::ta_zero(), 0.0f32);
    assert_eq!(f32::ta_one(), 1.0f32);
    assert!(f32::ta_epsilon() > 0.0f32);
}

#[test]
fn test_f64_conversions() {
    assert_eq!(f64::ta_from_f64(3.14), 3.14_f64);
    assert_eq!(f64::ta_from_i32(42), 42.0_f64);
    assert_eq!(3.14_f64.ta_to_f64(), 3.14);
}

#[test]
fn test_f32_conversions() {
    let v: f32 = f32::ta_from_f64(3.14);
    assert!((v - 3.14_f32).abs() < 1e-6);
    assert_eq!(f32::ta_from_i32(42), 42.0_f32);
    assert!((42.0_f32.ta_to_f64() - 42.0).abs() < 1e-10);
}

#[test]
fn test_f64_math_ops() {
    assert_eq!(4.0_f64.ta_sqrt(), 2.0);
    assert_eq!((-3.5_f64).ta_abs(), 3.5);
    assert_eq!(2.3_f64.ta_ceil(), 3.0);
    assert_eq!(2.7_f64.ta_floor(), 2.0);
    assert_eq!(2.5_f64.ta_round(), 3.0);
}

#[test]
fn test_f32_math_ops() {
    assert_eq!(4.0_f32.ta_sqrt(), 2.0_f32);
    assert_eq!((-3.5_f32).ta_abs(), 3.5_f32);
    assert_eq!(2.3_f32.ta_ceil(), 3.0_f32);
    assert_eq!(2.7_f32.ta_floor(), 2.0_f32);
    assert_eq!(2.5_f32.ta_round(), 3.0_f32);
}

#[test]
fn test_f64_trig() {
    let pi = std::f64::consts::PI;
    assert!((pi.ta_sin()).abs() < 1e-15);
    assert!((0.0_f64.ta_cos() - 1.0).abs() < 1e-15);
    assert!((1.0_f64.ta_atan() - std::f64::consts::FRAC_PI_4).abs() < 1e-15);
}

#[test]
fn test_f32_trig() {
    let pi = std::f32::consts::PI;
    assert!((pi.ta_sin()).abs() < 1e-6);
    assert!((0.0_f32.ta_cos() - 1.0_f32).abs() < 1e-6);
    assert!((1.0_f32.ta_atan() - std::f32::consts::FRAC_PI_4).abs() < 1e-6);
}

#[test]
fn test_f64_log_exp() {
    assert!((1.0_f64.ta_exp() - std::f64::consts::E).abs() < 1e-14);
    assert!((std::f64::consts::E.ta_ln() - 1.0).abs() < 1e-15);
    assert!((100.0_f64.ta_log10() - 2.0).abs() < 1e-15);
}

#[test]
fn test_f32_log_exp() {
    assert!((1.0_f32.ta_exp() - std::f32::consts::E).abs() < 1e-5);
    assert!((std::f32::consts::E.ta_ln() - 1.0_f32).abs() < 1e-6);
    assert!((100.0_f32.ta_log10() - 2.0_f32).abs() < 1e-6);
}

#[test]
fn test_f64_hyperbolic() {
    assert!((0.0_f64.ta_sinh()).abs() < 1e-15);
    assert!((0.0_f64.ta_cosh() - 1.0).abs() < 1e-15);
    assert!((0.0_f64.ta_tanh()).abs() < 1e-15);
}

#[test]
fn test_f32_hyperbolic() {
    assert!((0.0_f32.ta_sinh()).abs() < 1e-6);
    assert!((0.0_f32.ta_cosh() - 1.0_f32).abs() < 1e-6);
    assert!((0.0_f32.ta_tanh()).abs() < 1e-6);
}

#[test]
fn test_f64_inverse_trig() {
    assert!((0.0_f64.ta_asin()).abs() < 1e-15);
    assert!((1.0_f64.ta_acos()).abs() < 1e-15);
    assert!((0.0_f64.ta_tan()).abs() < 1e-15);
}

#[test]
fn test_f32_inverse_trig() {
    assert!((0.0_f32.ta_asin()).abs() < 1e-6);
    assert!((1.0_f32.ta_acos()).abs() < 1e-6);
    assert!((0.0_f32.ta_tan()).abs() < 1e-6);
}

#[test]
fn test_operator_traits() {
    fn add_em<T: TaFloat>(a: T, b: T) -> T { a + b }
    fn sub_em<T: TaFloat>(a: T, b: T) -> T { a - b }
    fn mul_em<T: TaFloat>(a: T, b: T) -> T { a * b }
    fn div_em<T: TaFloat>(a: T, b: T) -> T { a / b }
    fn rem_em<T: TaFloat>(a: T, b: T) -> T { a % b }
    fn neg_em<T: TaFloat>(a: T) -> T { -a }

    assert_eq!(add_em(1.0_f64, 2.0), 3.0);
    assert_eq!(sub_em(5.0_f32, 3.0_f32), 2.0_f32);
    assert_eq!(mul_em(2.0_f64, 3.0), 6.0);
    assert_eq!(div_em(6.0_f32, 2.0_f32), 3.0_f32);
    assert_eq!(rem_em(7.0_f64, 3.0), 1.0);
    assert_eq!(neg_em(5.0_f64), -5.0);
}
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `cd /Users/chadfurman/projects/ta-lib/rust && cargo test --test float_test 2>&1 | head -20`
Expected: Compilation error — `TaFloat` doesn't exist yet.

- [ ] **Step 3: Write the TaFloat trait and implementations**

Create `rust/src/ta_func/float.rs`:

```rust
//! Sealed floating-point trait for TA-Lib generic indicator functions.
//!
//! [`TaFloat`] is implemented for [`f32`] and [`f64`]. It cannot be implemented
//! outside this crate (sealed via `private::Sealed`).
//!
//! All trait methods use a `ta_` prefix to avoid name collisions with inherent
//! `f32`/`f64` methods in the standard library.

/// Sealed floating-point trait for generic TA-Lib indicator functions.
///
/// Provides constants, conversions, and math operations needed by
/// generated indicator code. All methods delegate to built-in intrinsics
/// and compile to single CPU instructions after monomorphization.
///
/// # Sealed
///
/// This trait cannot be implemented outside this crate. This allows
/// adding methods in future versions without breaking changes.
pub trait TaFloat:
    private::Sealed
    + Copy
    + PartialOrd
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
    /// The additive identity (0.0).
    fn ta_zero() -> Self;
    /// The multiplicative identity (1.0).
    fn ta_one() -> Self;
    /// Machine epsilon for near-zero comparison.
    fn ta_epsilon() -> Self;

    /// Convert from `f64`. For `f64` this is identity; for `f32` it narrows.
    fn ta_from_f64(v: f64) -> Self;
    /// Convert from `i32`.
    fn ta_from_i32(v: i32) -> Self;
    /// Convert to `f64`. For `f64` this is identity; for `f32` it widens.
    fn ta_to_f64(self) -> f64;

    /// Absolute value.
    fn ta_abs(self) -> Self;
    /// Square root.
    fn ta_sqrt(self) -> Self;
    /// Ceiling (round up).
    fn ta_ceil(self) -> Self;
    /// Floor (round down).
    fn ta_floor(self) -> Self;
    /// Round to nearest integer.
    fn ta_round(self) -> Self;

    /// Sine.
    fn ta_sin(self) -> Self;
    /// Cosine.
    fn ta_cos(self) -> Self;
    /// Tangent.
    fn ta_tan(self) -> Self;
    /// Arcsine.
    fn ta_asin(self) -> Self;
    /// Arccosine.
    fn ta_acos(self) -> Self;
    /// Arctangent.
    fn ta_atan(self) -> Self;

    /// Hyperbolic sine.
    fn ta_sinh(self) -> Self;
    /// Hyperbolic cosine.
    fn ta_cosh(self) -> Self;
    /// Hyperbolic tangent.
    fn ta_tanh(self) -> Self;

    /// Natural logarithm.
    fn ta_ln(self) -> Self;
    /// Base-10 logarithm.
    fn ta_log10(self) -> Self;
    /// Exponential (e^self).
    fn ta_exp(self) -> Self;
}

macro_rules! impl_ta_float {
    ($t:ty, $epsilon:expr) => {
        impl TaFloat for $t {
            #[inline(always)] fn ta_zero() -> Self { 0.0 }
            #[inline(always)] fn ta_one() -> Self { 1.0 }
            #[inline(always)] fn ta_epsilon() -> Self { $epsilon }

            #[inline(always)] fn ta_from_f64(v: f64) -> Self { v as Self }
            #[inline(always)] fn ta_from_i32(v: i32) -> Self { v as Self }
            #[inline(always)] fn ta_to_f64(self) -> f64 { self as f64 }

            #[inline(always)] fn ta_abs(self) -> Self { <$t>::abs(self) }
            #[inline(always)] fn ta_sqrt(self) -> Self { <$t>::sqrt(self) }
            #[inline(always)] fn ta_ceil(self) -> Self { <$t>::ceil(self) }
            #[inline(always)] fn ta_floor(self) -> Self { <$t>::floor(self) }
            #[inline(always)] fn ta_round(self) -> Self { <$t>::round(self) }

            #[inline(always)] fn ta_sin(self) -> Self { <$t>::sin(self) }
            #[inline(always)] fn ta_cos(self) -> Self { <$t>::cos(self) }
            #[inline(always)] fn ta_tan(self) -> Self { <$t>::tan(self) }
            #[inline(always)] fn ta_asin(self) -> Self { <$t>::asin(self) }
            #[inline(always)] fn ta_acos(self) -> Self { <$t>::acos(self) }
            #[inline(always)] fn ta_atan(self) -> Self { <$t>::atan(self) }

            #[inline(always)] fn ta_sinh(self) -> Self { <$t>::sinh(self) }
            #[inline(always)] fn ta_cosh(self) -> Self { <$t>::cosh(self) }
            #[inline(always)] fn ta_tanh(self) -> Self { <$t>::tanh(self) }

            #[inline(always)] fn ta_ln(self) -> Self { <$t>::ln(self) }
            #[inline(always)] fn ta_log10(self) -> Self { <$t>::log10(self) }
            #[inline(always)] fn ta_exp(self) -> Self { <$t>::exp(self) }
        }
    };
}

impl_ta_float!(f64, 1e-14);
impl_ta_float!(f32, 1e-6);

mod private {
    /// Sealed trait — prevents external implementations of [`super::TaFloat`].
    pub trait Sealed {}
    impl Sealed for f32 {}
    impl Sealed for f64 {}
}
```

- [ ] **Step 4: Wire float.rs into mod.rs**

Add to `rust/src/ta_func/mod.rs`, just before `impl Core {`:

```rust
mod float;
pub use float::TaFloat;
```

- [ ] **Step 5: Run tests to verify they pass**

Run: `cd /Users/chadfurman/projects/ta-lib/rust && cargo test --test float_test`
Expected: All 16 tests pass.

- [ ] **Step 6: Commit**

```bash
git add rust/src/ta_func/float.rs rust/src/ta_func/mod.rs rust/tests/float_test.rs
git commit -m "feat(rust): add sealed TaFloat trait for generic indicator functions"
```

---

## Chunk 2: Refactor Rust Backend — Generic Rendering

### Task 2: Replace `single_precision: bool` with `RustRenderCtx` in all render functions

This is a **pure signature refactoring** — all callers pass `RustRenderCtx { generic: false, unchecked: false }`, so the output is identical. The new generic/unchecked code paths exist as dead code until Task 3 activates them.

**Files:**
- Modify: `tools/ta_codegen/src/backends/rust_lang.rs`

Functions to refactor (all take `single_precision: bool`, change to `ctx: &RustRenderCtx`):

| Function | Line | Notes |
|----------|------|-------|
| `render_expr` | 1188 | Core expression renderer |
| `render_func_call` | 1330 | Built-in function dispatch |
| `render_statement` | 845 | Statement renderer |
| `render_binop_operand` | 1137 | Binary operator helper |
| `render_assign_target` | 1114 | Array write targets — **critical for unchecked writes** |
| `render_return_expr` | 1175 | Return expression helper |
| `render_lookback_code` | 1272 | Lookback body — always `generic: false` |
| `gen_func` | 461 | Flat function generator |
| `gen_internal_func` | 337 | Logic function generator |
| `gen_public_func` | 188 | Public wrapper generator |

- [ ] **Step 1: Add `RustRenderCtx` struct**

At the top of `rust_lang.rs` (after the imports), add:

```rust
/// Controls how the Rust renderer emits code.
struct RustRenderCtx {
    /// If true, emit `T` instead of `f64`/`f32` for Real types, wrap literals in `T::ta_from_f64()`, etc.
    generic: bool,
    /// If true, emit `get_unchecked()` / `get_unchecked_mut()` instead of `[]` for array access.
    unchecked: bool,
}

impl RustRenderCtx {
    fn concrete() -> Self { RustRenderCtx { generic: false, unchecked: false } }
}
```

- [ ] **Step 2: Refactor `render_expr` signature and add generic branches**

Change `render_expr(expr, single_precision, registry)` → `render_expr(expr, ctx, registry)`.

Update match arms (generic branches are dead code for now, activated in Task 3):

- `Expr::Literal(f)`: if `ctx.generic`, emit `T::ta_from_f64({formatted_literal})`. Otherwise emit as before.
- `Expr::Cast(VarType::Real, inner)`: if `ctx.generic`:
  - If inner is `IntLiteral` or `Cast(Integer, _)`: emit `T::ta_from_i32({inner})`
  - Otherwise: emit `T::ta_from_f64(({inner}).ta_to_f64())`
  Otherwise emit `({inner}) as f64` as before.
- `Expr::ArrayAccess`: if `ctx.unchecked`, emit `unsafe { *{name}.get_unchecked({idx}) }`. Otherwise emit `{name}[{idx}]` as before.
- All recursive calls: pass `ctx` instead of `single_precision`.

- [ ] **Step 3: Refactor `render_func_call` signature and add generic branches**

Change to `render_func_call(fname, args, ctx, registry)`. Update built-in cases for `ctx.generic`:

- `IS_ZERO(x)` → `{x}.ta_abs() < T::ta_epsilon()`
- `PER_TO_K(period)` → `T::ta_from_f64(2.0) / (T::ta_from_i32({period}) + T::ta_one())`
- `ARRAY_COPY` → always `copy_from_slice` (same types when generic)
- Cross-indicator calls (`registry.contains(fname)`) → `self.{fname}_unguarded({args})`
- TA function calls (`is_ta_function`) → `self.{fname_lower}({args})` (no `_s`)
- Math functions (else fallback) → method call on first arg: `{args[0]}.ta_{fname}()` for known names (sqrt, sin, cos, atan, abs, ceil, floor, ln, log10, exp, sinh, cosh, tanh, tan, asin, acos, round). If `fname` not recognized or `args.len() != 1`, keep as free function call.

- [ ] **Step 4: Refactor `render_statement` and `render_assign_target`**

Change `render_statement(stmt, indent, single_precision, ...)` → `render_statement(stmt, indent, ctx, ...)`.

For `render_assign_target`: when `ctx.unchecked` and target is `ArrayAccess(name, idx)`, return a special marker (e.g., tuple with `is_unchecked_array: true`) so the `Assign` arm in `render_statement` can emit:
```rust
unsafe { *{name}.get_unchecked_mut({idx}) = {value}; }
```
instead of the normal `{target} = {value};` pattern. This is the only place where the assignment *shape* changes for unchecked mode.

Also update `expr_has_uncast_array_access`: when `ctx.generic` is true, this function should return `false` (output arrays are `&mut [T]`, no `as f64` cast needed).

- [ ] **Step 5: Refactor remaining helper functions**

Update `render_binop_operand`, `render_return_expr` to take `ctx: &RustRenderCtx` and pass through.

Update `render_lookback_code` to create `RustRenderCtx::concrete()` internally (lookback is never generic).

- [ ] **Step 6: Update all generator function callers**

Every generator that constructs a `RustRenderCtx` and passes it to render functions:

- `gen_func()` → `RustRenderCtx::concrete()`
- `gen_internal_func()` → `RustRenderCtx::concrete()`
- `gen_public_func()` → `RustRenderCtx::concrete()`
- `gen_unsafe_func()` → `RustRenderCtx::concrete()`

No behavioral change — all callers use `concrete()` which matches the old `single_precision: false` path.

- [ ] **Step 7: Verify existing tests still pass**

Run: `cd /Users/chadfurman/projects/ta-lib/tools/ta_codegen && cargo test`
Expected: All existing tests pass — output is byte-for-byte identical to before.

- [ ] **Step 8: Commit**

```bash
git add tools/ta_codegen/src/backends/rust_lang.rs
git commit -m "refactor(codegen): replace single_precision bool with RustRenderCtx"
```

### Task 3: Rewrite generator functions to emit generic variants

**Files:**
- Modify: `tools/ta_codegen/src/backends/rust_lang.rs` (gen_impl_block, gen_public_func → gen_guarded_func, gen_internal_func/gen_func → gen_unguarded_func, gen_unsafe_func → gen_unchecked_func)
- Modify: `tools/ta_codegen/tests/backend_suite.rs`

- [ ] **Step 1: Write failing test for generic function structure**

Add `check_rust_generic_variants` to `backend_suite.rs` and replace the call to `check_rust_variants` in `test_all_indicators_all_backends`:

```rust
fn check_rust_generic_variants(r: &str, snake: &str, name: &str) {
    assert!(
        r.contains(&format!("{}_lookback", snake)),
        "{}: Rust missing {}_lookback", name, snake
    );
    assert!(
        r.contains(&format!("pub fn {}<T: TaFloat>", snake)),
        "{}: Rust missing pub fn {}<T: TaFloat>", name, snake
    );
    assert!(
        r.contains(&format!("pub fn {}_unguarded<T: TaFloat>", snake)),
        "{}: Rust missing pub fn {}_unguarded<T: TaFloat>", name, snake
    );
    assert!(
        r.contains(&format!("pub unsafe fn {}_unchecked<T: TaFloat>", snake)),
        "{}: Rust missing pub unsafe fn {}_unchecked<T: TaFloat>", name, snake
    );
    assert!(
        r.contains(&format!("pub unsafe fn {}_unguarded_unchecked<T: TaFloat>", snake)),
        "{}: Rust missing pub unsafe fn {}_unguarded_unchecked<T: TaFloat>", name, snake
    );
    assert!(
        !r.contains(&format!("fn {}_s(", snake)) && !r.contains(&format!("fn {}_s<", snake)),
        "{}: Rust should NOT have old {}_s variant", name, snake
    );
    assert!(
        !r.contains(&format!("fn {}_logic(", snake)),
        "{}: Rust should NOT have old {}_logic variant", name, snake
    );
}
```

Delete the old `check_rust_variants` function. Update the call site in `test_all_indicators_all_backends` to pass only `(r, snake, name)` (remove `has_opt_inputs`).

- [ ] **Step 2: Run tests to verify they fail**

Run: `cd /Users/chadfurman/projects/ta-lib/tools/ta_codegen && cargo test test_all_indicators_all_backends -- --nocapture 2>&1 | tail -20`
Expected: Failures — generated Rust doesn't have `<T: TaFloat>` yet.

- [ ] **Step 3: Rewrite `gen_impl_block`**

Replace the body to emit 4 generic functions. The flat vs two-layer distinction (whether indicator has optional inputs) is absorbed: `gen_guarded_func` always validates whatever opt params exist (may be none) and always delegates to `_unguarded`.

```rust
fn gen_impl_block(func: &FuncDef, enums: &HashMap<String, EnumDef>, registry: &Registry) -> String {
    let mut out = String::new();
    let snake = func.name.to_lowercase();

    out.push_str(
        "// Allow non-snake-case names to maintain TA-Lib API compatibility\n\
         #[allow(non_snake_case)]\n\
         #[allow(unused_variables)]\n\
         #[allow(dead_code)]\n\
         #[allow(unused_mut)]\n\
         #[allow(unused_assignments)]\n\
         impl Core {\n",
    );

    out.push_str(&gen_lookback(func, &snake, enums, registry));

    // Guarded: validates params, delegates to unguarded
    out.push_str(&gen_guarded_func(func, &snake, enums, registry));

    // Unguarded: real algorithm, bounds-checked array access
    let safe_ctx = RustRenderCtx { generic: true, unchecked: false };
    out.push_str(&gen_unguarded_func(func, &snake, &safe_ctx, enums, registry));

    // Unchecked: validates params, delegates to unguarded_unchecked
    out.push_str(&gen_unchecked_func(func, &snake, enums, registry));

    // Unguarded unchecked: real algorithm, unchecked array access
    let unsafe_ctx = RustRenderCtx { generic: true, unchecked: true };
    out.push_str(&gen_unguarded_func(func, &snake, &unsafe_ctx, enums, registry));

    out.push_str("}\n");
    out
}
```

Note: `#[allow(unused_mut)]` and `#[allow(unused_assignments)]` are added deliberately — generic code may produce these warnings due to `let mut x: T` patterns where mut-ness can't always be statically determined by the codegen.

- [ ] **Step 4: Write `gen_guarded_func`**

Generates `pub fn ema<T: TaFloat>(...)`:
- Signature: `pub fn {snake}<T: TaFloat>(&self, startIdx: usize, endIdx: usize, ...)`
- Input arrays: `&[T]` for `ParamType::Real`
- Output arrays: `&mut [T]` for `ParamType::Real`, `&mut [i32]` for `ParamType::Integer`
- OptInput `ParamType::Real`: stays `f64` (configuration, not price data)
- OptInput `ParamType::Integer`/`Enum`: stays `i32`
- Validates `endIdx < startIdx`
- Validates optional params (default substitution + range check)
- Delegates to `self.{snake}_unguarded(...)` passing all params through

For indicators without optional inputs (like MULT), the validation section is just the `endIdx < startIdx` check, then immediate delegation.

- [ ] **Step 5: Write `gen_unguarded_func`**

Generates `pub fn ema_unguarded<T: TaFloat>(...)` (when `ctx.unchecked == false`) or `pub unsafe fn ema_unguarded_unchecked<T: TaFloat>(...)` (when `ctx.unchecked == true`).

Same generic signature. Contains the real algorithm body:
- `VarType::Real` in `VarDecl` → `let mut tempReal: T;`
- All expressions rendered with `ctx.generic = true` (literals wrapped, casts use trait methods)
- No param validation, no `endIdx < startIdx` check
- The function name suffix changes based on `ctx.unchecked`: `_unguarded` vs `_unguarded_unchecked`
- If `ctx.unchecked`, the function is `pub unsafe fn`

OptInput `ParamType::Real` values (like `optInK_1` in EMA) are `f64` parameters. When used in arithmetic with generic `T` variables, the generated code wraps them: `T::ta_from_f64(optInK_1)` at the point of use. This conversion is emitted by `render_expr` when it encounters a `Var` reference to an OptInput Real parameter in a generic context.

- [ ] **Step 6: Write `gen_unchecked_func`**

Generates `pub unsafe fn ema_unchecked<T: TaFloat>(...)`:
- Structurally identical to `gen_guarded_func` but:
  - `pub unsafe fn` instead of `pub fn`
  - Delegates to `self.{snake}_unguarded_unchecked(...)` instead of `_unguarded`

- [ ] **Step 7: Update Rust-specific tests in `backend_suite.rs`**

Update these tests to use new function names:

```rust
// test_rust_sma_guarded_has_validation
let guarded = extract_section(&out.rust, "pub fn sma<T: TaFloat>", "pub fn sma_unguarded<T: TaFloat>");
assert!(guarded.contains("endIdx < startIdx"), ...);

// test_rust_sma_logic_omits_validation
let unguarded_start = out.rust.find("pub fn sma_unguarded<T: TaFloat>").expect("...");
let unguarded_section = &out.rust[unguarded_start..];
let end = unguarded_section.find("pub unsafe fn sma_unchecked").unwrap_or(unguarded_section.len());
let unguarded = &unguarded_section[..end];
assert!(!unguarded.contains("OutOfRangeStartIndex"), ...);

// test_ma_rust_cross_calls
assert!(r.contains("self.sma_unguarded("), ...);
assert!(r.contains("self.ema_unguarded("), ...);
// Remove assertions for self.sma_logic( and self.ema_logic(
```

- [ ] **Step 8: Run all codegen tests**

Run: `cd /Users/chadfurman/projects/ta-lib/tools/ta_codegen && cargo test -- --nocapture 2>&1 | tail -30`
Expected: All tests pass including new generic variant checks for all 163 indicators.

- [ ] **Step 9: Verify C/Java/.NET/SWIG output is unchanged**

Run: `cd /Users/chadfurman/projects/ta-lib/tools/ta_codegen && cargo test test_all_indicators_all_backends -- --nocapture 2>&1 | grep -E "tested|failed"`
Expected: Same number tested, 0 failed. C/Java/.NET/SWIG variant checks are unchanged and must still pass.

- [ ] **Step 10: Commit**

```bash
git add tools/ta_codegen/src/backends/rust_lang.rs tools/ta_codegen/tests/backend_suite.rs
git commit -m "feat(codegen): Rust backend emits generic <T: TaFloat> function variants"
```

---

## Chunk 3: Downstream Updates and Cleanup

### Task 4: Update existing Rust test files for generic API

The existing test files call `_s` methods (e.g., `core.mult_s()`, `core.sma_s()`, `core.rsi_s()`) which no longer exist. Update them to use the generic API with `f32` input slices (type inference resolves `T = f32`).

**Files:**
- Modify: `rust/tests/mult_test.rs`
- Modify: `rust/tests/sma_test.rs`
- Modify: `rust/tests/rsi_test.rs`

- [ ] **Step 1: Update mult_test.rs**

Replace calls like `core.mult_s(&f32_in0, &f32_in1, ...)` → `core.mult(&f32_in0, &f32_in1, ...)`. Also update output buffers: old `_s` variants used `&mut [f64]` output with `&[f32]` input; new generic API uses `&mut [f32]` output (matching T).

- [ ] **Step 2: Update sma_test.rs**

Same pattern: `core.sma_s(...)` → `core.sma(...)` with f32 slices for both input and output.

- [ ] **Step 3: Update rsi_test.rs**

Same pattern: `core.rsi_s(...)` → `core.rsi(...)` with f32 slices.

- [ ] **Step 4: Verify Rust tests pass**

Run: `cd /Users/chadfurman/projects/ta-lib/rust && cargo test`
Expected: All tests pass.

Note: These tests may not compile until the generated indicator files are regenerated with the new ta_codegen. If the generated files haven't been regenerated yet, this task should be deferred until after a full codegen run.

- [ ] **Step 5: Commit**

```bash
git add rust/tests/mult_test.rs rust/tests/sma_test.rs rust/tests/rsi_test.rs
git commit -m "fix(rust): update test files for generic TaFloat API"
```

### Task 5: Update FFI wrapper generation

The FFI wrappers in `rust/ffi/src/generated.rs` are generated by the legacy `gen_code.c`/`gen_rust.c` pipeline (not ta_codegen). They call `core.mult_s()` etc. which no longer exist. The FFI wrappers need to call the generic functions with explicit monomorphization.

**Files:**
- Modify: `src/tools/gen_code/gen_rust.c` (FFI wrapper generation in `writeRustFfiGenerated()`)

- [ ] **Step 1: Update FFI call sites**

In `gen_rust.c`'s `writeRustFfiGenerated()`, change the generated calls from:
- `core.{func}(...)` → `core.{func}::<f64>(...)` (double-precision FFI)
- `core.{func}_s(...)` → `core.{func}::<f32>(...)` (if single-precision FFI is needed)

The turbofish `::<f64>` is needed here because the FFI wrapper converts raw pointers to slices, and the compiler may not infer `T` from `unsafe { std::slice::from_raw_parts(...) }`.

- [ ] **Step 2: Regenerate FFI wrappers**

Run: `cd /Users/chadfurman/projects/ta-lib/cmake-build && cmake .. -DCMAKE_BUILD_TYPE=Release && make gen_code -j4 && cd ../bin && ../cmake-build/bin/gen_code`

- [ ] **Step 3: Verify FFI crate compiles**

Run: `cd /Users/chadfurman/projects/ta-lib/rust/ffi && cargo build --release`
Expected: Compiles successfully.

- [ ] **Step 4: Commit**

```bash
git add src/tools/gen_code/gen_rust.c rust/ffi/src/generated.rs
git commit -m "fix(ffi): update FFI wrappers for generic TaFloat API"
```

### Task 6: Remove obsolete feature flag and add smoke test

**Files:**
- Modify: `rust/Cargo.toml`
- Modify: `tools/ta_codegen/tests/backend_suite.rs`

- [ ] **Step 1: Remove the `single-precision` feature flag**

In `rust/Cargo.toml`, remove lines 29-30:
```toml
# Feature flag for single precision variants
single-precision = []
```

Also grep for `#[cfg(feature = "single-precision")]` across the codebase — if any references exist, remove them.

- [ ] **Step 2: Add smoke test for generic output patterns**

Add to `backend_suite.rs`:

```rust
#[test]
fn test_rust_generic_output_smoke() {
    for name in &["sma", "ema", "mult", "rsi", "ma"] {
        let (func, enums) = load_indicator(name);
        let registry = make_registry();
        let rust = backends::rust_lang::generate(&func, &enums, &registry);
        let snake = name.to_lowercase();

        // Generic signatures present
        assert!(rust.contains("<T: TaFloat>"), "{}: missing <T: TaFloat>", name);
        assert!(rust.contains(": &[T]"), "{}: missing generic input slice &[T]", name);

        // No old _s variants after lookback section
        let after_lookback = rust.split(&format!("{}_lookback", snake)).nth(1).unwrap_or("");
        assert!(
            !after_lookback.contains(&format!("fn {}_s(", snake)),
            "{}: should not have _s variant", name
        );

        // T::ta_from_f64 used for float literals in generic functions
        assert!(
            rust.contains("T::ta_from_f64"),
            "{}: generic functions should use T::ta_from_f64()", name
        );

        // All 4 variants present
        assert!(rust.contains(&format!("pub fn {}<T: TaFloat>", snake)), "{}: missing guarded", name);
        assert!(rust.contains(&format!("pub fn {}_unguarded<T: TaFloat>", snake)), "{}: missing unguarded", name);
        assert!(rust.contains(&format!("pub unsafe fn {}_unchecked<T: TaFloat>", snake)), "{}: missing unchecked", name);
        assert!(rust.contains(&format!("pub unsafe fn {}_unguarded_unchecked<T: TaFloat>", snake)), "{}: missing unguarded_unchecked", name);
    }
}
```

- [ ] **Step 3: Run tests**

Run: `cd /Users/chadfurman/projects/ta-lib/tools/ta_codegen && cargo test test_rust_generic_output_smoke -- --nocapture`
Expected: Pass.

- [ ] **Step 4: Commit**

```bash
git add rust/Cargo.toml tools/ta_codegen/tests/backend_suite.rs
git commit -m "chore: remove single-precision feature flag, add generic output smoke test"
```

### Task 7: Update CLAUDE.md

**Files:**
- Modify: `CLAUDE.md`

- [ ] **Step 1: Add Rust generics section after "## Type Mappings"**

```markdown
## Rust Generics (ta_codegen)

The ta_codegen Rust backend generates generic functions using `<T: TaFloat>` instead of
separate `_s` (single-precision) variants. For each indicator, 4 public functions + 1 lookback:

| Function | Signature | Description |
|----------|-----------|-------------|
| `ema_lookback` | `pub fn ema_lookback(&self, ...) -> i32` | Non-generic lookback |
| `ema` | `pub fn ema<T: TaFloat>(&self, ...) -> RetCode` | Safe, validated |
| `ema_unguarded` | `pub fn ema_unguarded<T: TaFloat>(&self, ...) -> RetCode` | Safe, no validation |
| `ema_unchecked` | `pub unsafe fn ema_unchecked<T: TaFloat>(&self, ...) -> RetCode` | Unchecked array access, validated |
| `ema_unguarded_unchecked` | `pub unsafe fn ema_unguarded_unchecked<T: TaFloat>(&self, ...) -> RetCode` | Unchecked, no validation |

- `TaFloat` is a sealed trait in `rust/src/ta_func/float.rs`, implemented for `f32` and `f64`
- All trait methods use `ta_` prefix to avoid inherent method shadowing (e.g., `ta_sqrt`, `ta_from_f64`)
- Type parameter `T` is inferred from input slices — no turbofish needed
- Cross-indicator calls use `_unguarded` variant to avoid double-validation
- Input/output Real arrays are `&[T]` / `&mut [T]`; Integer arrays stay `&[i32]` / `&mut [i32]`
- OptInput Real params stay `f64` (configuration values, not price data)
```

- [ ] **Step 2: Remove stale references**

Search and update/remove these specific patterns in CLAUDE.md:
- `printRustSinglePrecisionFunctionSignature` — remove or note as legacy gen_code only
- References to `_s` suffix for Rust (e.g., `ema_s`, `sma_s`)
- References to `_logic` / `_logic_s` for Rust cross-calls
- The `single-precision = []` feature flag mention
- `gen_public_func`, `gen_internal_func`, `gen_unsafe_func` if referenced in ta_codegen context

- [ ] **Step 3: Commit**

```bash
git add CLAUDE.md
git commit -m "docs: update CLAUDE.md with Rust generics documentation"
```
