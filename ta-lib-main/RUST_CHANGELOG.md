# Rust Integration Changelog

Tracking progress of TA-Lib's Rust code generation pipeline.

> **Note:** Diff links point to commits on the `dev` branch. Push `dev` to GitHub for links to resolve.

---

## 2026-03-08 -- Generic enum type support in ta_codegen

`git diff d5c67b85^..d5c67b85` | [view on GitHub](https://github.com/TA-Lib/ta-lib/compare/92a3d581...d5c67b85)

* [d5c67b85](https://github.com/TA-Lib/ta-lib/commit/d5c67b85) Generic enum type support — `enums.yaml` defines enum variants, `ParamType::Enum(String)` in IR, `lookup_variant()` replaces hardcoded switch label tables in all 5 backends
* [d5c67b85](https://github.com/TA-Lib/ta-lib/commit/d5c67b85) MA source uses named case labels (`MAType_SMA`), YAML uses `type: enum:MAType`
* [d5c67b85](https://github.com/TA-Lib/ta-lib/commit/d5c67b85) Generated output: C → `TA_MAType` + `ENUM_CASE()`, Java → `MAType` enum, Rust → `i32`
* Adding new enum types requires only `enums.yaml` entries — zero backend code changes

## 2026-03-08 -- Multi-language codegen regression testing (5 languages × 3 functions)

`git diff f5df7080^..92a3d581` | [view on GitHub](https://github.com/TA-Lib/ta-lib/compare/4404a8e4...92a3d581)

* [f5df7080](https://github.com/TA-Lib/ta-lib/commit/f5df7080) Added error codes for codegen verification (TA_CODEGEN_*)
* [bc58114c](https://github.com/TA-Lib/ta-lib/commit/bc58114c) Implemented codegen subprocess pipe management (fork/exec/pipe in codegen_pipe.c)
* [28b453e7](https://github.com/TA-Lib/ta-lib/commit/28b453e7) Added codegen verification test loop for MULT/SMA/RSI
* [e01e5ab5](https://github.com/TA-Lib/ta-lib/commit/e01e5ab5) Added `--codegen` flag to ta_regtest to enable codegen verification
* [b4006d96](https://github.com/TA-Lib/ta-lib/commit/b4006d96) CMake `ta_codegen_bin` target to build and install ta_codegen binary
* [fdb138fb](https://github.com/TA-Lib/ta-lib/commit/fdb138fb) Expanded codegen test coverage with multiple parameter combinations
* [d96f58ab](https://github.com/TA-Lib/ta-lib/commit/d96f58ab) Design doc for v2 multi-language codegen regression testing
* [8183ed36](https://github.com/TA-Lib/ta-lib/commit/8183ed36) Implementation plan for v2 multi-language codegen regression testing
* [e2dfdfab](https://github.com/TA-Lib/ta-lib/commit/e2dfdfab) Refactored codegen_pipe_open to accept argv array for flexible command execution
* [6ef4129b](https://github.com/TA-Lib/ta-lib/commit/6ef4129b) Rewrote test_codegen with doRangeTest callbacks and multi-language support (Rust/C/Java/.NET/SWIG)
* [361ff93a](https://github.com/TA-Lib/ta-lib/commit/361ff93a) Added `--codegen=lang` syntax for per-language testing
* [cd09a10c](https://github.com/TA-Lib/ta-lib/commit/cd09a10c) Fixed unstable period handling and MULT tolerance in codegen doRangeTest
* [6ba802ca](https://github.com/TA-Lib/ta-lib/commit/6ba802ca) Added `generate-servers` command with C, Java, .NET, SWIG server generators
* [2991e32c](https://github.com/TA-Lib/ta-lib/commit/2991e32c) Java server generation + fixes to C/Java servers
* [4ceb89e1](https://github.com/TA-Lib/ta-lib/commit/4ceb89e1) Added `build` command to compile all generated language servers
* [133eb320](https://github.com/TA-Lib/ta-lib/commit/133eb320) CMake `ta_codegen_servers` target for multi-language server builds
* [7f8098b3](https://github.com/TA-Lib/ta-lib/commit/7f8098b3) Initial .NET (P/Invoke) and Python (ctypes) codegen servers with shared C library
* [c74b61c2](https://github.com/TA-Lib/ta-lib/commit/c74b61c2) Proper SWIG compilation pipeline — Python server imports SWIG-generated module instead of ctypes
* [65b3e7e0](https://github.com/TA-Lib/ta-lib/commit/65b3e7e0) RUST_CHANGELOG entries for C parser migration and multi-language regtest
* [61deee36](https://github.com/TA-Lib/ta-lib/commit/61deee36) Fixed RUST_CHANGELOG commit hashes after rebase
* [8ed91f59](https://github.com/TA-Lib/ta-lib/commit/8ed91f59) Unstable period support in all 5 JSON-RPC servers — RSI now tested with full doRangeTest (0-240+ unstable periods)
* [92a3d581](https://github.com/TA-Lib/ta-lib/commit/92a3d581) Updated RUST_CHANGELOG with unstable period support entry
* All 5 languages (Rust, C, Java, .NET, SWIG/Python) pass all 3 functions (MULT, SMA, RSI) through doRangeTest including unstable period verification

## 2026-03-08 -- ta_codegen: migrate from .logic files to C source parser

`git diff 0fc87df0^..4404a8e4` | [view on GitHub](https://github.com/TA-Lib/ta-lib/compare/9a24133...4404a8e4)

* [0fc87df0](https://github.com/TA-Lib/ta-lib/commit/0fc87df0) Fixed changelog hash + rewrote convert-indicator skill for ta_codegen pipeline
* [ecbc9aa3](https://github.com/TA-Lib/ta-lib/commit/ecbc9aa3) Fixed missing commit URL in RUST_CHANGELOG MA bullet
* [b22df2bf](https://github.com/TA-Lib/ta-lib/commit/b22df2bf) Enriched IR and YAML parser with IDL metadata fields (flags, hints, display name, etc.)
* [f8cec46f](https://github.com/TA-Lib/ta-lib/commit/f8cec46f) Enriched mult.yaml with IDL metadata fields
* [be99eb76](https://github.com/TA-Lib/ta-lib/commit/be99eb76) Added plain C source for MULT as parser input
* [7855b58e](https://github.com/TA-Lib/ta-lib/commit/7855b58e) Extended IR with PointerDeref, ForC, optional lookback, BinOp::Mod
* [f515e0ab](https://github.com/TA-Lib/ta-lib/commit/f515e0ab) C source parser: tokenizer + function extraction + statement/expression parsing
* [48222f69](https://github.com/TA-Lib/ta-lib/commit/48222f69) Wired up C source parser, validated MULT end-to-end
* [ea83d502](https://github.com/TA-Lib/ta-lib/commit/ea83d502) Migrated WMA to C source with enriched YAML
* [85a3b3f2](https://github.com/TA-Lib/ta-lib/commit/85a3b3f2) Migrated RSI to C source with enriched YAML
* [1ee8907a](https://github.com/TA-Lib/ta-lib/commit/1ee8907a) Migrated EMA to C source with enriched YAML
* [cb5acf38](https://github.com/TA-Lib/ta-lib/commit/cb5acf38) Migrated MA to C source with enriched YAML
* [4404a8e4](https://github.com/TA-Lib/ta-lib/commit/4404a8e4) Deleted .logic parser and files — C source is now the only input format
* All 5 functions (MULT, SMA, RSI, EMA, MA) generating across 5 backends from C source

## 2026-03-07 -- ta_codegen: SMA, RSI, EMA, MA support with switch/case dispatch

`git diff 4af160c3^..9a24133` | [view on GitHub](https://github.com/TA-Lib/ta-lib/compare/9f295c53...9a24133)

* [4af160c](https://github.com/TA-Lib/ta-lib/commit/4af160c3) Logic syntax reference doc and updated ta_codegen design for C-like syntax
* [48aa690](https://github.com/TA-Lib/ta-lib/commit/48aa690d) SMA implementation plan for ta_codegen
* [5d6114f](https://github.com/TA-Lib/ta-lib/commit/5d6114ff) Extended IR and parser for C-like logic syntax (semicolons, parens, C types), migrated MULT
* [09d3278](https://github.com/TA-Lib/ta-lib/commit/09d3278c) SMA function definition: sma.yaml + sma.logic
* [f3b85bd](https://github.com/TA-Lib/ta-lib/commit/f3b85bd3) If/Return in all backends, SMA generation matching reference
* [75f7019](https://github.com/TA-Lib/ta-lib/commit/75f17019) SMA JSON-RPC validation server support
* [8f0424c](https://github.com/TA-Lib/ta-lib/commit/8f0424c6) RSI support: for loops, builtins (UNSTABLE_PERIOD, IS_ZERO, ARRAY_COPY), complex lookback
* [c68af8f](https://github.com/TA-Lib/ta-lib/commit/c68af8f9) EMA support: PER_TO_K, DEFAULT/METASTOCK compat builtins, smart operator precedence
* [9a24133](https://github.com/TA-Lib/ta-lib/commit/9a241336) MA support: switch/case/break/continue, RetCodeType, function call dispatch, BAD_PARAM/SUCCESS mapping
* 33 validations passing (5 functions × 5 backends + byte-identical MULT/SMA + 6 JSON-RPC tests + 13 integration tests)

## 2026-03-07 -- ta_codegen prototype: YAML+logic multi-language generator

`git diff f3bf0542^..9f295c53` | [view on GitHub](https://github.com/TA-Lib/ta-lib/compare/1cb68ba0...9f295c53)

* [f3bf054](https://github.com/TA-Lib/ta-lib/commit/f3bf0542) Scaffolded `tools/ta_codegen/` Rust crate + `ta_func_defs/mult/` with YAML metadata and logic pseudocode
* [87477a3](https://github.com/TA-Lib/ta-lib/commit/87477a31) IR types (`FuncDef`, `Statement`, `Expr`, `BinOp`) + serde-based YAML parser
* [13b3072](https://github.com/TA-Lib/ta-lib/commit/13b3072d) Recursive descent logic file parser (tokenizer + while/assign/vardecl/compound-assign)
* [64f5d60](https://github.com/TA-Lib/ta-lib/commit/64f5d601) C backend — generates `TA_MULT` + `TA_S_MULT` + lookback from IR
* [19e49f2](https://github.com/TA-Lib/ta-lib/commit/19e49f2a) Rust backend — **byte-identical** output to gen_code reference (`mult.rs`)
* [b26fd0d](https://github.com/TA-Lib/ta-lib/commit/b26fd0dd) Java backend — MInteger output params, lookback method
* [618b185](https://github.com/TA-Lib/ta-lib/commit/618b1857) .NET backend — SubArray + cli::array overloads, `#define` aliases
* [b7bac62](https://github.com/TA-Lib/ta-lib/commit/b7bac620) SWIG backend — typemap markers (START_IDX, IN_ARRAY, OUT_ARRAY, etc.)
* [d0e431e](https://github.com/TA-Lib/ta-lib/commit/d0e431ee) JSON-RPC validation server over stdin/stdout using ta-lib Rust crate
* [9f295c5](https://github.com/TA-Lib/ta-lib/commit/9f295c53) Integration tests (9 tests), validation harness (8 checks), CLI with `--func`/`--backend` filters
* All 10 ta_codegen tests passing, Rust output byte-identical to gen_code reference, JSON-RPC server verified

## 2026-03-07 -- Cross-language ta_regtest via FFI link-time swap

`git diff a8526c1b^..ce037cb9` | [view on GitHub](https://github.com/TA-Lib/ta-lib/compare/fe84577b...ce037cb9)

* [a8526c1](https://github.com/TA-Lib/ta-lib/commit/a8526c1b) Design doc for cross-language ta_regtest via FFI link-time swap
* [c96a058](https://github.com/TA-Lib/ta-lib/commit/c96a058b) Implementation plan for Rust regtest FFI
* [133451c](https://github.com/TA-Lib/ta-lib/commit/133451c5) Added `--function=CSV` filter to ta_regtest for selective test runs (substring match against test group descriptions)
* [8eaf0cd](https://github.com/TA-Lib/ta-lib/commit/8eaf0cde) Created `rust/ffi/` staticlib crate with `extern "C"` wrappers for SMA, RSI, MULT matching C signatures
* [da924a3](https://github.com/TA-Lib/ta-lib/commit/da924a36) Added `ta_regtest_rust` CMake target (`ENABLE_RUST_REGTEST` flag) with macOS `-force_load` handling for duplicate symbols
* [d2ddd9d](https://github.com/TA-Lib/ta-lib/commit/d2ddd9d8) Fixed FFI: read global state from C via `TA_GetUnstablePeriod()`/`TA_GetCompatibility()` instead of creating default Core (required for RSI range tests)
* [cfc1de5](https://github.com/TA-Lib/ta-lib/commit/cfc1de5c) Extended gen_code to auto-generate FFI wrappers (`writeRustFfiGenerated()` in gen_rust.c), replaced hand-written wrappers
* [ce037cb](https://github.com/TA-Lib/ta-lib/commit/ce037cb9) Documented cross-language regression testing workflow in CLAUDE.md
* All 30 Rust tests passing, ta_regtest_rust passes SMA/RSI/MULT via Rust FFI through full range testing suite

## 2026-03-01 -- RSI complete — first unstable indicator

`git diff f81456aa^..1cc09c88` | [view on GitHub](https://github.com/TA-Lib/ta-lib/compare/b2def6df...1cc09c88)

* [f81456a](https://github.com/TA-Lib/ta-lib/commit/f81456aa) Converted ta_RSI.c logic section to cross-language macros (DECLARE vars, FOR_COUNTDOWN, CAST macros, braces)
* [e61b232](https://github.com/TA-Lib/ta-lib/commit/e61b232f) Regenerated Java RSI output after logic section macro conversion (cosmetic: braces, split declarations, `today++` → `today = today + 1`)
* [51a6b82](https://github.com/TA-Lib/ta-lib/commit/51a6b82e) Added RSI to `RUST_SUPPORTED_FUNCS`, template imports via `use super::*`, `mut startIdx` in signatures
* [51a6b82](https://github.com/TA-Lib/ta-lib/commit/51a6b82e) Fixed `< 0` → `< 0.0` for f64 comparisons, added `CAST_TO_F64(inReal[today])` for single-precision compat
* [5d9833d](https://github.com/TA-Lib/ta-lib/commit/5d9833de) RSI test suite: 13 tests covering basic, single precision, lookback, error conditions, unstable period, Metastock compat
* [0cdd319](https://github.com/TA-Lib/ta-lib/commit/0cdd3190) Added rich hand-written doctest example for RSI
* [f8d5677](https://github.com/TA-Lib/ta-lib/commit/f8d5677c) Updated CLAUDE.md status and RUST_CHANGELOG with RSI completion
* [1cc09c8](https://github.com/TA-Lib/ta-lib/commit/1cc09c88) Fixed formatting in RSI tests and regenerated doctest in ta_RSI.c
* All 30 Rust tests passing (6 MULT + 7 SMA + 13 RSI + 4 doctests), zero warnings

## 2026-03-01 -- Core becomes stateful — infrastructure for unstable indicators

`git diff 59b61e9d^..b2def6df` | [view on GitHub](https://github.com/TA-Lib/ta-lib/compare/561d3b58...b2def6df)

* [59b61e9](https://github.com/TA-Lib/ta-lib/commit/59b61e9d) Added `Compatibility` and `FuncUnstId` enums to Rust template (25 variants)
* [a5ef144](https://github.com/TA-Lib/ta-lib/commit/a5ef1448) Made Core stateful with `unstable_period` array and `compatibility` field
* [0ba23d1](https://github.com/TA-Lib/ta-lib/commit/0ba23d1c) Added `&self` to all Rust function signatures — static functions become instance methods
* [b2def6d](https://github.com/TA-Lib/ta-lib/commit/b2def6df) Updated MULT/SMA tests for `&self` method API (`Core::func()` → `core.func()`)
* [cca127b](https://github.com/TA-Lib/ta-lib/commit/cca127b2) Separated Rust from Java in `TA_GLOBALS_*` and `ARRAY_MEMMOVE/MEMMOVEMIX` macros (ta_memory.h)
* [c78b0a8](https://github.com/TA-Lib/ta-lib/commit/c78b0a87) Added `LOOKBACK_CALL` for Rust — two-level expansion: `LOOKBACK_CALL(RSI)` → `TA_RSI_Lookback` → `self.rsi_lookback`
* All 16 existing tests passing (6 MULT + 7 SMA + 3 doctests), zero regressions

---

## 2026-03-01 -- SMA complete with full validation and tests

`git diff 509d6af2^..24ad5e21` | [view on GitHub](https://github.com/TA-Lib/ta-lib/compare/121b4852...24ad5e21)

* [509d6af](https://github.com/TA-Lib/ta-lib/commit/509d6af2) Removed `TA_FUNC_NO_RANGE_CHECK` which was disabling ALL validation for Rust (caused MULT test regressions)
* [509d6af](https://github.com/TA-Lib/ta-lib/commit/509d6af2) Added `CAST_TO_I32` macro to `printOptInputValidation` so `(int)` casts become cross-language
* [509d6af](https://github.com/TA-Lib/ta-lib/commit/509d6af2) Renamed `CAST_TO_USIZE` to `CAST_TO_INDEX` (concept-based naming -- each language maps to its own index type)
* [509d6af](https://github.com/TA-Lib/ta-lib/commit/509d6af2) Removed redundant `OUTPUT_F64` macro, consolidated to `CAST_TO_F64`
* [509d6af](https://github.com/TA-Lib/ta-lib/commit/509d6af2) Added `FUNCTION_CALL` / `FUNCTION_CALL_DOUBLE` macros for Rust (needed for SMA internal dispatch)
* [509d6af](https://github.com/TA-Lib/ta-lib/commit/509d6af2) Split Rust `INT_MIN`/`INT_MAX` from Java (`i32::MIN` vs `Integer.MIN_VALUE`)
* [509d6af](https://github.com/TA-Lib/ta-lib/commit/509d6af2) Added `mut` to lookback function params in gen_rust.c (validation assigns default values)
* [509d6af](https://github.com/TA-Lib/ta-lib/commit/509d6af2) Refactored `RUST_SINGLE_FUNC` to `RUST_SUPPORTED_FUNCS` for multi-function support
* [95f1413](https://github.com/TA-Lib/ta-lib/commit/95f1413d) SMA tests: basic, single precision, lookback, partial range
* [1132d92](https://github.com/TA-Lib/ta-lib/commit/1132d920) Updated CLAUDE.md with `CAST_TO_INDEX` macro docs and SMA status
* [66fd2f8](https://github.com/TA-Lib/ta-lib/commit/66fd2f88) Removed empty `#if defined(_RUST)` wrapper in gen_code.c Section 4 — Rust now goes through `printFunc(validationCode=1)` like all other languages
* [66fd2f8](https://github.com/TA-Lib/ta-lib/commit/66fd2f88) Added `!defined(_RUST)` to null-pointer check guards (alongside `!defined(_JAVA)`) — Rust doesn't need null checks
* [66fd2f8](https://github.com/TA-Lib/ta-lib/commit/66fd2f88) Added `mut` to optional input params in both double and single precision Rust signatures
* [66fd2f8](https://github.com/TA-Lib/ta-lib/commit/66fd2f88) SMA error condition tests: BadParam for period 0/1/100001, OutOfRangeEndIndex, default period via i32::MIN (double + single precision)
* [66fd2f8](https://github.com/TA-Lib/ta-lib/commit/66fd2f88) Replaced `test_sma_period_1` with `test_sma_minimum_period` (period=2, the actual minimum)
* [7889054](https://github.com/TA-Lib/ta-lib/commit/78890540) Added doc comments to `RetCode` enum, variants, and `Core` struct in mod.rs template
* [ef27bf6](https://github.com/TA-Lib/ta-lib/commit/ef27bf6a) Added `printRustFuncDoc` to gen_rust.c — generates `///` doc comments from ta_abstract metadata for all Rust functions
* [ef27bf6](https://github.com/TA-Lib/ta-lib/commit/ef27bf6a) Uses `@RUSTDOC@` marker encoding to survive mcpp preprocessing (which strips `//` comments)
* [ef27bf6](https://github.com/TA-Lib/ta-lib/commit/ef27bf6a) Lookback functions get cross-reference + param docs; main functions get full docs with working doctest examples; single-precision gets cross-reference one-liner
* [f9e9d7a](https://github.com/TA-Lib/ta-lib/commit/f9e9d7ae) Added doc comments to `pub mod` declarations and `lib.rs` module — zero `missing_docs` warnings
* [24ad5e2](https://github.com/TA-Lib/ta-lib/commit/24ad5e21) Added rich hand-written doctest example for SMA (overrides mechanical generated example via `rust_examples/sma.txt`)
* All 16 Rust tests passing (6 MULT + 7 SMA + 3 doctests), zero `missing_docs` warnings

## 2026-01-07 -- Document generated code philosophy and macro-first approach

`git diff 121b4852^..121b4852` | [view on GitHub](https://github.com/TA-Lib/ta-lib/compare/db9c2e71...121b4852)

* [121b485](https://github.com/TA-Lib/ta-lib/commit/121b4852) Major CLAUDE.md update documenting the code generation pipeline
* [121b485](https://github.com/TA-Lib/ta-lib/commit/121b4852) Macro system reference, type mappings, build commands
* [121b485](https://github.com/TA-Lib/ta-lib/commit/121b4852) Cross-language development workflow guidelines

## 2026-01-06 -- Remove naked conditionals for Rust compatibility

`git diff 02a2d4ec^..02a2d4ec` | [view on GitHub](https://github.com/TA-Lib/ta-lib/compare/1bdf5438...02a2d4ec)

* [02a2d4e](https://github.com/TA-Lib/ta-lib/commit/02a2d4ec) Added curly braces to all bare if/else in generated validation code
* [02a2d4e](https://github.com/TA-Lib/ta-lib/commit/02a2d4ec) Rust requires braces on all conditionals; change is syntactically identical in C/Java/.NET
* [02a2d4e](https://github.com/TA-Lib/ta-lib/commit/02a2d4ec) Affects all generated `ta_*.c` and `Core.java` files

## 2025-06-22 -- MULT passes all tests

`git diff 35a19bbc^..9ba6d22e` | [view on GitHub](https://github.com/TA-Lib/ta-lib/compare/fc92f9d0...9ba6d22e)

* [35a19bb...9ba6d22](https://github.com/TA-Lib/ta-lib/compare/35a19bbc...9ba6d22e) Added MULT test suite (double precision, single precision, error conditions, lookback, partial range, usize validation)
* [35a19bb...9ba6d22](https://github.com/TA-Lib/ta-lib/compare/35a19bbc...9ba6d22e) Fixed Rust compilation warnings for unused variables and index validation
* [35a19bb...9ba6d22](https://github.com/TA-Lib/ta-lib/compare/35a19bbc...9ba6d22e) Updated ta_MULT.c with new loop variable macros

## 2025-06-21 -- Rust functions made public, cargo fix/fmt automated

`git diff be62763a^..1562ce87` | [view on GitHub](https://github.com/TA-Lib/ta-lib/compare/dfe47891...1562ce87)

* [be62763...1562ce8](https://github.com/TA-Lib/ta-lib/compare/be62763a...1562ce87) Made generated Rust functions `pub` on `Core` struct
* [be62763...1562ce8](https://github.com/TA-Lib/ta-lib/compare/be62763a...1562ce87) Added `cargo fix` and `cargo fmt` as post-generation steps in gen_code
* [be62763...1562ce8](https://github.com/TA-Lib/ta-lib/compare/be62763a...1562ce87) Fixed usize type issues in generated code

## 2025-06-09 -- MULT compiles in Rust

`git diff 2c95c858^..97e7908d` | [view on GitHub](https://github.com/TA-Lib/ta-lib/compare/bd4667a9...97e7908d)

* [2c95c85...97e7908](https://github.com/TA-Lib/ta-lib/compare/2c95c858...97e7908d) Capital C `Core` struct
* [2c95c85...97e7908](https://github.com/TA-Lib/ta-lib/compare/2c95c858...97e7908d) Added FOR_EACH_OUTPUT, VALUE_HANDLE, DECLARE_*_VAR macros
* [2c95c85...97e7908](https://github.com/TA-Lib/ta-lib/compare/2c95c858...97e7908d) MULT source updated to use cross-language macros
* [2c95c85...97e7908](https://github.com/TA-Lib/ta-lib/compare/2c95c858...97e7908d) First successful `cargo check` on generated Rust code

## 2025-06-08 -- Macro system and Cargo setup

`git diff a6702544^..469f8ed7` | [view on GitHub](https://github.com/TA-Lib/ta-lib/compare/314de99c...469f8ed7)

* [a670254...469f8ed](https://github.com/TA-Lib/ta-lib/compare/a6702544...469f8ed7) Added curly braces to all generated if/else (Rust requirement, valid in all languages)
* [a670254...469f8ed](https://github.com/TA-Lib/ta-lib/compare/a6702544...469f8ed7) Created loop and variable declaration macros in ta_defs.h
* [a670254...469f8ed](https://github.com/TA-Lib/ta-lib/compare/a6702544...469f8ed7) Added enum and value handle macros for Rust
* [a670254...469f8ed](https://github.com/TA-Lib/ta-lib/compare/a6702544...469f8ed7) Created `rust/Cargo.toml` and `rust/src/lib.rs`
* [a670254...469f8ed](https://github.com/TA-Lib/ta-lib/compare/a6702544...469f8ed7) Reformatted and re-indented gen_rust.c

## 2025-06-04 -- Claude-assisted development begins

`git diff 6d68e4a1^..6d68e4a1` | [view on GitHub](https://github.com/TA-Lib/ta-lib/compare/c042ea3a...6d68e4a1)

* [6d68e4a](https://github.com/TA-Lib/ta-lib/commit/6d68e4a1) Added CLAUDE.md project instructions
* [6d68e4a](https://github.com/TA-Lib/ta-lib/commit/6d68e4a1) Addressed TODOs in gen_rust.c

## 2025-02-15 -- gen_rust.c split and input validation

`git diff 722a704c^..64ed4fd9` | [view on GitHub](https://github.com/TA-Lib/ta-lib/compare/7412be6f...64ed4fd9)

* [722a704...64ed4fd](https://github.com/TA-Lib/ta-lib/compare/722a704c...64ed4fd9) Moved Rust code generation to dedicated `gen_rust.c` file
* [722a704...64ed4fd](https://github.com/TA-Lib/ta-lib/compare/722a704c...64ed4fd9) Added input array validation for Rust
* [722a704...64ed4fd](https://github.com/TA-Lib/ta-lib/compare/722a704c...64ed4fd9) Start/end index parameters appearing in generated signatures

## 2025-01-22 -- Function name generation

`git diff a7299da7^..66e65076` | [view on GitHub](https://github.com/TA-Lib/ta-lib/compare/864e49d6...66e65076)

* [a7299da...66e6507](https://github.com/TA-Lib/ta-lib/compare/a7299da7...66e65076) WIP Rust function name generation (snake_case conversion)
* [a7299da...66e6507](https://github.com/TA-Lib/ta-lib/compare/a7299da7...66e65076) Removed extra memcopy naming for Rust

## 2025-01-05 -- Rust function templating

`git diff 470ac693^..eabc13b8` | [view on GitHub](https://github.com/TA-Lib/ta-lib/compare/caec5738...eabc13b8)

* [470ac69...eabc13b](https://github.com/TA-Lib/ta-lib/compare/470ac693...eabc13b8) First pass at templating Rust function output from gen_code
* [470ac69...eabc13b](https://github.com/TA-Lib/ta-lib/compare/470ac693...eabc13b8) Added `outputForRust` argument plumbing

## 2024-12-31 -- Initial Rust preprocessor support

`git diff 6ec3d779^..42bbc3d3` | [view on GitHub](https://github.com/TA-Lib/ta-lib/compare/b6f8a4de...42bbc3d3)

* [6ec3d77...42bbc3d](https://github.com/TA-Lib/ta-lib/compare/6ec3d779...42bbc3d3) Added `_RUST` define to mcpp preprocessor calls
* [6ec3d77...42bbc3d](https://github.com/TA-Lib/ta-lib/compare/6ec3d779...42bbc3d3) Regenerated ta_func files with Rust preprocessing support
* [6ec3d77...42bbc3d](https://github.com/TA-Lib/ta-lib/compare/6ec3d779...42bbc3d3) Foundation for the entire Rust code generation pipeline
