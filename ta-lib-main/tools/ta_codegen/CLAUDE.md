# ta_codegen — TA-Lib Code Generation Tool

## What This Is

`ta_codegen` is the Rust-based code generator that replaces the old `gen_code.c` pipeline for indicator code generation. It reads YAML function definitions extracted from the C source, produces language-specific indicator implementations, and generates JSON-RPC servers for cross-language regression testing.

## Architecture

```
ta_func_defs/*.yaml          (extracted indicator definitions)
       ↓
    parser                   (YAML → raw parsed structs)
       ↓
    ir                       (parsed → FuncDef intermediate representation)
       ↓
  ┌────┴─────────────┐
backends            server_gen / bench_gen
  ↓                      ↓
c.rs, rust_lang.rs,   JSON-RPC servers, bench binary,
java.rs, dotnet.rs    include/ta_func_unguarded.h
ta_abstract_c.rs
  ↓
ta_codegen_output/       (generated code per language)
  c/ta_func/*.c          (C indicator code)
  c/ta_abstract/         (ta_abstract introspection layer)
  rust/src/ta_func/*.rs  (Rust indicator code)
  java/, dotnet/         (Java/.NET code)
include/ta_func.h        (generated public header)
```

### Key Modules

| Module | Purpose |
|--------|---------|
| `parser` | Parses YAML files into raw function definitions |
| `ir` | Intermediate representation (`FuncDef`, `ParamType`, etc.) |
| `extractor` | Extracts indicator definitions from C source files → YAML |
| `backends/c.rs` | Generates C indicator implementations (guarded + unguarded variants) |
| `backends/rust_lang.rs` | Generates Rust indicator implementations with `<T: TaFloat>` generics |
| `backends/java.rs` | Generates Java Core class methods |
| `backends/dotnet.rs` | Generates .NET P/Invoke wrappers |
| `backends/ta_abstract_c.rs` | Generates `ta_abstract` introspection layer (tables, frames, group index, runtime API) |
| `backends/func_api_xml.rs` | Generates `ta_func_api.xml` metadata |
| `server_gen` | Generates JSON-RPC server wrappers + `include/ta_func_unguarded.h` |
| `bench_gen` | Generates direct-call benchmark binary |
| `registry` | Function registry for tracking available indicators |

## Commands

```bash
# Run from tools/ta_codegen/
cargo run -- generate                        # Generate indicator code for all backends
cargo run -- generate --func=SMA,RSI         # Generate specific functions
cargo run -- generate --backend=rust         # Generate for specific backend

cargo run -- generate-servers                # Generate JSON-RPC servers for all languages
cargo run -- generate-servers --backend=c    # Generate server for specific language

cargo run -- build                           # Compile generated servers into executables
cargo run -- build --backend=c,java          # Build specific servers

cargo run -- extract                         # Extract all indicators from C source → YAML
cargo run -- extract --function=EMA          # Extract specific indicator
```

## Testing

```bash
cd tools/ta_codegen && cargo test            # Run all 445+ tests
cd tools/ta_codegen && cargo clippy          # Strict pedantic lints enabled
```

Tests are in `tests/backend_suite.rs` and `tests/integration_test.rs` — they verify IR-to-backend rendering, expression types, generic signatures, and function variants across all backends.

## Cross-Language Testing Architecture

**The big picture**: `ta_regtest` is the universal test runner. It should test ALL languages, not just C.

### Current State

**Fully working:**
- `codegen_pipe.c/h` in ta_regtest — complete subprocess pipe abstraction (fork, exec, stdin/stdout JSON-RPC)
- `test_codegen.c/h` in ta_regtest — full orchestration: multi-language loop, JSON helpers, `doRangeTest` integration, epsilon comparison (`1e-6`), language/function filters
- Server generation for all 4 languages (C, Java, .NET, Rust)
- `ta_codegen build` compiles servers into executables in `bin/`

**What's working end-to-end:**
- Generic callback in `test_codegen.c` auto-generates JSON-RPC requests from ta_abstract metadata for all 161 indicators
- `list_functions` implemented — servers report available indicators with parameter metadata
- `timing_ns` returned with each response — ta_regtest collects and prints a timing summary
- `set_unstable_period` and `set_compatibility` implemented for all 24 unstable-period functions

### How It Works

1. `ta_codegen generate-servers` produces a JSON-RPC server per language
2. `ta_codegen build` compiles them into executables in `bin/`
3. Each server reads JSON-RPC from stdin, dispatches to compiled indicators, writes responses to stdout
4. `ta_regtest` spawns each server as a subprocess via `codegen_pipe`
5. For each indicator, ta_regtest calls the C reference AND sends the same call to the server
6. `compare_codegen_output()` validates retCode, outBegIdx, outNbElement, and output values match

### What This Replaced

- **Rust FFI layer** (`rust/ffi/`) — legacy `extern "C"` wrappers letting C call Rust directly. Deleted in favor of server architecture.
- **Hand-written Rust test files** (`rust/tests/mult_test.rs`, `sma_test.rs`, `rsi_test.rs`) — legacy from manual porting phase. Deleted; all indicator testing goes through ta_regtest.
- **`ta_regtest_rust` CMake target** — linked ta_regtest against Rust staticlib. Deleted; replaced by server-based approach.

### Server Protocol

JSON-RPC over stdin/stdout.

**Request format:**
```json
{"method": "TA_SMA", "params": {"startIdx": 0, "endIdx": 251, "optInTimePeriod": 30, "inReal": [...]}}
```

**Input types vary by function:**
- `inReal` / `inReal0` / `inReal1` — for functions with `TA_Input_Real` params
- `inHigh`, `inLow`, `inClose`, etc. — for functions with `TA_Input_Price` params (STOCH, BBANDS, ADX, etc.)
- The server must handle both styles based on the function's signature

**Response format:**
```json
{"retCode": 0, "outBegIdx": 14, "outNBElement": 237, "outReal": [...], "timing_ns": 1842}
```

**Multi-output functions** (STOCH, BBANDS, MACD, etc.) return multiple arrays:
```json
{"retCode": 0, "outBegIdx": 14, "outNBElement": 50, "outReal": [...], "outReal1": [...], "outReal2": [...]}
```

**Integer output functions** (CDL* candlestick patterns, MINMAXINDEX) return:
```json
{"retCode": 0, "outBegIdx": 14, "outNBElement": 50, "outInteger": [...]}
```

**Server protocol is complete:**
- `list_functions` — servers report available indicators with parameter metadata
- `set_unstable_period` / `set_compatibility` — global state management implemented
- `timing_ns` — execution timing returned with every response
- All 24 unstable-period functions mapped in `func_unst_id()`
- Real-valued optional params use `json_find_double` (e.g., BBANDS `optInNbDevUp`, SAR `optInAcceleration`)
- Price input support (OHLCV arrays) for STOCH, BBANDS, ADX, MACD, etc.
- Multi-output support (BBANDS=3, MACD=3, STOCH=2) with `outReal`, `outReal1`, `outReal2`
- Integer output support (CDL* patterns, MINMAXINDEX) with `outInteger`

## Rust Backend Details

### Generic Type System

All generated Rust indicator functions use `<T: TaFloat>` — a sealed trait implemented for f32 and f64. No `_s` suffix convention.

### 4 Function Variants Per Indicator

| Variant | Safety | Purpose |
|---------|--------|---------|
| `fn sma<T: TaFloat>(...)` | Safe | Public API with parameter validation |
| `fn sma_unguarded<T: TaFloat>(...)` | Safe | Cross-indicator calls (skip validation) |
| `unsafe fn sma_unchecked<T: TaFloat>(...)` | Unsafe | Performance (get_unchecked indexing) |
| `unsafe fn sma_unguarded_unchecked<T: TaFloat>(...)` | Unsafe | Internal hot paths |

### Known Code Quality Issues (non-blocking)

1. **`collect_for_loop_vars`** doesn't recurse into nested structures
2. **`gen_opt_param_validation`** silently skips Real/Enum optional params

## Linting

Strict Clippy pedantic lints are enabled in `src/lib.rs`. Allowed exceptions:
- `module_name_repetitions` — common in codegen
- `must_use_candidate` — codegen builders don't need this
- `format_push_string` — string building is the natural codegen pattern
- `doc_markdown` — generated doc comments come from upstream C

`rustfmt.toml`: edition 2021, max_width 100, use_field_init_shorthand true.

## Performance: C Server Compilation

- Server is single-TU (`#include .c` files) — do NOT switch to separate compilation, it causes CDL binary layout issues
- Candle settings reads use `volatile` cast in `emit_c_unpacking()` to prevent constant propagation that kills loop unswitching
- Ternary chains (not switch statements) for numeric-case switches — matches reference macro pattern for compiler optimization
- CCI uses conditional reset (`idx++; if(idx>=max) idx=0`) not modulo — modulo costs ~10 cycles on ARM
- Full parameter validation (NULL checks, INTEGER_DEFAULT, range) is required — missing validation changes compiler register allocation
- `ta_ref_serve` is statically linked against `libta-lib.a` — MUST rebuild when cmake rebuilds the library, or benchmarks compare against stale code
- `regtest.py` auto-rebuilds ta_ref_serve in the cmake step
- Benchmark noise: full 161-indicator runs have 10-20% variance from icache pressure. Use `ta_bench --function=NAME --iters=500` for ground truth.
- All servers and bench binaries call `TA_Initialize()` at startup — required for candle settings defaults.
- Thermal canary (SMA) runs between each indicator in ta_bench to normalize CPU thermal state
