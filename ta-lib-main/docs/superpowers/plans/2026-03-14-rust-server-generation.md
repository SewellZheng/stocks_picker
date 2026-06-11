# Rust JSON-RPC Server Generation Implementation Plan

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Generate a Rust JSON-RPC server binary that passes all 163 indicators in `ta_regtest --codegen --language=rust`.

**Architecture:** Add `generate_rust_server()` to `server_gen.rs` that emits `rust/src/bin/ta_codegen_serve.rs` — a serde_json-based stdin/stdout dispatcher calling existing `Core` methods with `f64` turbofish. Built via `cargo build --release --bin ta_codegen_serve`, binary copied to `bin/ta_codegen_serve_rust`.

**Tech Stack:** Rust, serde_json, ta_codegen (code generator)

**Spec:** `docs/superpowers/specs/2026-03-14-rust-server-generation-design.md`

---

## Chunk 1: Setup & Server Generation

### Task 1: Add serde_json Dependency and Binary Target

Add `serde_json` to the Rust crate and declare the binary target.

**Files:**
- Modify: `rust/Cargo.toml`

- [ ] **Step 1: Add serde_json dependency and bin target**

Add to `rust/Cargo.toml`:

```toml
[[bin]]
name = "ta_codegen_serve"
path = "src/bin/ta_codegen_serve.rs"

[dependencies]
serde_json = "1"
```

The `[dependencies]` section currently says "No external dependencies". Replace that comment with the `serde_json` line. Keep the `[[bin]]` section after `[lib]`.

- [ ] **Step 2: Create placeholder binary**

Create `rust/src/bin/ta_codegen_serve.rs` with a minimal placeholder so `cargo check` passes:

```rust
fn main() {
    eprintln!("Rust server not yet implemented");
}
```

- [ ] **Step 3: Verify it builds**

```bash
cd rust && cargo check --bin ta_codegen_serve 2>&1 | tail -5
```

Expected: compiles without errors.

- [ ] **Step 4: Commit**

```bash
git add rust/Cargo.toml rust/src/bin/ta_codegen_serve.rs
git commit -m "chore: add serde_json dep and ta_codegen_serve binary target to rust crate"
```

---

### Task 2: Implement `generate_rust_server()` in server_gen.rs

This is the main task — generate the full Rust server source code from the FuncDef list.

**Files:**
- Modify: `tools/ta_codegen/src/server_gen.rs` (add `generate_rust_server` function)
- Modify: `tools/ta_codegen/src/main.rs` (lines 200, 256-258, 274 — wire up Rust backend)
- Overwrite: `rust/src/bin/ta_codegen_serve.rs` (generated output)

- [ ] **Step 1: Study the C dispatch pattern**

Read `tools/ta_codegen/src/server_gen.rs` — specifically `generate_c_server()` (line 407) and `generate_c_dispatch()` (line 555). Understand:
1. How `expand_input_names()` maps inputs to JSON field names
2. How `output_json_key()` maps outputs to JSON field names
3. How `func_unst_id()` maps function names to unstable period IDs
4. How the dispatch loop generates per-function handlers

Also read the Rust `Core` API in `rust/src/ta_func/mod.rs`:
- `Core::new()`, `set_unstable_period(FuncUnstId, i32)`, `set_compatibility(Compatibility)`
- Function signatures: `core.sma::<f64>(startIdx, endIdx, &inReal, optInTimePeriod, &mut outBegIdx, &mut outNBElement, &mut outReal) -> RetCode`
- Lookback: `core.sma_lookback(optInTimePeriod) -> i32`

- [ ] **Step 2: Add `generate_rust_server()` function**

Add `pub fn generate_rust_server(funcs: &[FuncDef]) -> String` to `server_gen.rs`. It should emit a complete Rust source file.

The generated file structure:

```rust
#![allow(non_snake_case, unused_variables, clippy::all)]

use serde_json::{json, Value};
use std::io::{self, BufRead, Write};
use std::time::Instant;
use ta_lib::{Core, RetCode, FuncUnstId, Compatibility, TaFloat};

fn main() {
    let mut core = Core::new();
    let stdin = io::stdin();
    let mut stdout = io::stdout();
    for line in stdin.lock().lines() {
        let line = line.expect("Failed to read line");
        if line.trim().is_empty() { continue; }
        let response = handle_request(&mut core, &line);
        writeln!(stdout, "{}", response).expect("Failed to write");
        stdout.flush().expect("Failed to flush");
    }
}

fn retcode_to_int(rc: RetCode) -> i32 {
    match rc {
        RetCode::Success => 0,
        RetCode::BadParam => 2,
        RetCode::AllocErr => 3,
        RetCode::OutOfRangeStartIndex => 12,
        RetCode::OutOfRangeEndIndex => 13,
        RetCode::InternalError => 5000,
    }
}

fn parse_f64_array(v: &Value) -> Vec<f64> {
    v.as_array()
        .map(|arr| arr.iter().filter_map(|x| x.as_f64()).collect())
        .unwrap_or_default()
}

fn handle_request(core: &mut Core, json_str: &str) -> String {
    let v: Value = match serde_json::from_str(json_str) {
        Ok(v) => v,
        Err(e) => return json!({"error": format!("{}", e)}).to_string(),
    };
    let method = v["method"].as_str().unwrap_or("");
    let params = &v["params"];
    match method {
        "list_functions" => { /* generated list */ }
        "set_unstable_period" => {
            let id = params["id"].as_i64().unwrap_or(0) as usize;
            let period = params["period"].as_i64().unwrap_or(0) as i32;
            if id < FuncUnstId::FuncUnstAll as usize {
                core.unstable_period[id] = period;
            }
            return json!({"retCode": 0}).to_string();
        }
        "set_compatibility" => {
            let mode = params["mode"].as_i64().unwrap_or(0);
            core.set_compatibility(if mode == 1 { Compatibility::Metastock } else { Compatibility::Default });
            return json!({"retCode": 0}).to_string();
        }
        // Per-indicator dispatch arms generated below
        "TA_SMA" => return dispatch_sma(core, params),
        // ... one arm per indicator
        _ => {}
    }
    json!({"error": "unknown method"}).to_string()
}
```

Then for each function, generate a `dispatch_<name>` function:

```rust
fn dispatch_sma(core: &mut Core, params: &Value) -> String {
    let start_idx = params["startIdx"].as_i64().unwrap_or(0) as usize;
    let end_idx = params["endIdx"].as_i64().unwrap_or(0) as usize;
    let in_real = parse_f64_array(&params["inReal"]);
    let opt_in_time_period = params["optInTimePeriod"].as_i64().unwrap_or(0) as i32;
    let buf_size = if end_idx >= start_idx { end_idx - start_idx + 1 } else { 0 };
    let mut out_beg_idx: usize = 0;
    let mut out_nb_element: usize = 0;
    let mut out_real: Vec<f64> = vec![0.0; buf_size];
    let start = Instant::now();
    let rc = core.sma::<f64>(
        start_idx, end_idx, &in_real, opt_in_time_period,
        &mut out_beg_idx, &mut out_nb_element, &mut out_real,
    );
    let elapsed_ns = start.elapsed().as_nanos() as i64;
    let out_slice = &out_real[..out_nb_element];
    json!({
        "retCode": retcode_to_int(rc),
        "outBegIdx": out_beg_idx,
        "outNBElement": out_nb_element,
        "outReal": out_slice,
        "timing_ns": elapsed_ns,
    }).to_string()
}
```

**Key patterns for each function (parallel to C dispatch):**

For **inputs**, use `expand_input_names()` to get JSON field names:
- Single real: `parse_f64_array(&params["inReal"])`
- Multi real: `parse_f64_array(&params["inReal0"])`, `parse_f64_array(&params["inReal1"])`
- Price-expanded: `parse_f64_array(&params["inHigh"])`, etc.

For **optional inputs**:
- `ParamType::Real`: `params["name"].as_f64().unwrap_or(0.0) as f64`
- `ParamType::Integer` / `ParamType::Enum`: `params["name"].as_i64().unwrap_or(0) as i32`

For **unstable period** (if `func_unst_id` returns Some):
- Before the function call: `if let Some(p) = params["unstablePeriod"].as_i64() { core.unstable_period[{id}] = p as i32; }`

For **outputs**, use `output_json_key()` for JSON field names:
- Real output: `"outReal"`, `"outReal1"`, `"outReal2"` → `Vec<f64>`
- Integer output: `"outInteger"`, `"outInteger1"` → `Vec<i32>`

For the **function call**, the Rust method name is `func.name.to_lowercase()` (e.g., `SMA` → `sma`). Call with turbofish `::<f64>`.

For **lookback dispatch**, generate separate arms for `TA_SMA_Lookback` etc.

For **`list_functions`**, generate the JSON array of all `TA_<NAME>` strings.

- [ ] **Step 3: Wire up in main.rs**

In `main.rs`:

1. Line 200 — add `"rust"` to default backends:
```rust
None => vec!["c", "java", "dotnet", "swig", "rust"],
```

2. Lines 256-258 — replace the skip with generation:
```rust
"rust" => {
    let rust_bin_dir = Path::new("../../rust/src/bin");
    std::fs::create_dir_all(rust_bin_dir).unwrap();
    let output = server_gen::generate_rust_server(&funcs);
    let path = rust_bin_dir.join("ta_codegen_serve.rs");
    std::fs::write(&path, &output).unwrap();
    println!("  Rust server -> {}", path.display());
}
```

3. Line 274 — add `"rust"` to build defaults:
```rust
None => vec!["c", "java", "dotnet", "swig", "rust"],
```

4. In `build_servers`, add the Rust build case (around line 380, after the swig case):
```rust
"rust" => {
    print!("  Building Rust server... ");
    let rust_dir = Path::new("../../rust");
    match std::process::Command::new("cargo")
        .args(["build", "--release", "--bin", "ta_codegen_serve"])
        .current_dir(rust_dir)
        .status()
    {
        Ok(s) if s.success() => {
            // Copy binary to bin/
            let src = rust_dir.join("target/release/ta_codegen_serve");
            let dst = bin_dir.join("ta_codegen_serve_rust");
            if let Err(e) = std::fs::copy(&src, &dst) {
                println!("OK (build), FAILED (copy: {})", e);
            } else {
                println!("OK");
            }
        }
        Ok(s) => println!("FAILED (exit {})", s.code().unwrap_or(-1)),
        Err(e) => println!("FAILED (cargo not found: {})", e),
    }
}
```

- [ ] **Step 4: Build and verify generation**

```bash
cd tools/ta_codegen && cargo build 2>&1 | tail -5
cargo run -- generate-servers --backend=rust 2>&1
```

Expected: `Rust server -> ../../rust/src/bin/ta_codegen_serve.rs`

- [ ] **Step 5: Verify generated Rust code compiles**

```bash
cd ../../rust && cargo check --bin ta_codegen_serve 2>&1 | tail -10
```

Expected: compiles. If there are errors, fix `generate_rust_server()`.

- [ ] **Step 6: Build the binary**

```bash
cd ../tools/ta_codegen && cargo run -- build --backend=rust 2>&1
```

Expected: `Building Rust server... OK`

- [ ] **Step 7: Test manually**

```bash
echo '{"method":"list_functions","params":{}}' | ../../bin/ta_codegen_serve_rust | head -1
echo '{"method":"TA_SMA","params":{"startIdx":0,"endIdx":4,"inReal":[1.0,2.0,3.0,4.0,5.0],"optInTimePeriod":3}}' | ../../bin/ta_codegen_serve_rust
```

Expected: function list, then SMA result with `retCode: 0`.

- [ ] **Step 8: Commit**

```bash
cd tools/ta_codegen && git add src/server_gen.rs src/main.rs
git commit -m "feat(rust): generate JSON-RPC server for Rust backend — 163 indicator dispatch"
```

---

### Task 3: Update ta_regtest argv for Rust

The ta_regtest binary invocation for Rust currently expects `{"./ta_codegen", "serve", NULL}` but our server is a standalone binary.

**Files:**
- Modify: `src/tools/ta_regtest/test_codegen.c` (line 28)

- [ ] **Step 1: Update argv_rust**

Change line 28 from:
```c
static const char *const argv_rust[]  = {"./ta_codegen", "serve", NULL};
```
To:
```c
static const char *const argv_rust[]  = {"./ta_codegen_serve_rust", NULL};
```

- [ ] **Step 2: Rebuild ta_regtest**

```bash
cd cmake-build && cmake .. -DCMAKE_BUILD_TYPE=Release && make ta_regtest -j4 2>&1 | tail -5
```

- [ ] **Step 3: Commit**

```bash
git add src/tools/ta_regtest/test_codegen.c
git commit -m "fix(regtest): update Rust server argv to standalone binary name"
```

---

## Chunk 2: Regression Testing & Fixes

### Task 4: Run Regression Tests and Fix Failures

**Files:**
- May modify: `tools/ta_codegen/src/server_gen.rs` (if generation bugs found)
- May modify: `rust/src/ta_func/mod.rs` (if Core API gaps found)

- [ ] **Step 1: Run Rust regtest**

```bash
cd bin && ../cmake-build/bin/ta_regtest --codegen-only --language=rust 2>&1 | tail -30
```

- [ ] **Step 2: If failures, investigate**

For each failing function:
1. Test manually: `echo '{"method":"TA_<NAME>","params":{...}}' | ./ta_codegen_serve_rust`
2. Compare with C server output
3. Check if the issue is in server dispatch (JSON parsing, parameter mapping) or in the indicator code itself

Common issues to watch for:
- **MAType enum dispatch**: The Rust `Core` may not have an MAType enum. Check if `ma` function takes `i32` or an enum for the MA type parameter.
- **Candle settings**: Rust `Core` may not have candle settings infrastructure. If CDL functions fail, check what the Rust backend generates for candle settings unpacking.
- **Integer outputs**: CDL* functions return `i32` outputs. Make sure the dispatch allocates `Vec<i32>` and serializes with `"outInteger"` key.
- **Multi-output functions**: BBANDS (3 outputs), MACD (3), STOCH (2), AROON (2). Make sure all output buffers are allocated and all output keys are in the response.

- [ ] **Step 3: Fix any issues found**

Fix in `generate_rust_server()` and regenerate. Iterate until all 163 pass.

- [ ] **Step 4: Run full multi-language regtest**

```bash
cd bin && ../cmake-build/bin/ta_regtest --codegen --language=c,java,rust 2>&1 | tail -20
```

Expected: All 3 languages pass all 163 indicators.

- [ ] **Step 5: Commit fixes**

```bash
cd tools/ta_codegen && git add src/server_gen.rs
git commit -m "fix(rust): address regtest failures in Rust server dispatch"
```

---

### Task 5: Final Verification

**Files:**
- No changes expected

- [ ] **Step 1: Full 4-language regtest**

```bash
cd bin && ../cmake-build/bin/ta_regtest --codegen --language=c,java,dotnet,rust 2>&1 | tail -20
```

Expected: All 4 languages pass all 163 indicators.

- [ ] **Step 2: Run codegen tests**

```bash
cd tools/ta_codegen && cargo test --lib --test backend_suite 2>&1 | tail -5
cargo clippy 2>&1 | tail -5
```

Expected: All tests pass, no new clippy warnings.

- [ ] **Step 3: Verify Rust server binary exists in bin/**

```bash
ls -la ../../bin/ta_codegen_serve_rust
```

- [ ] **Step 4: Push**

```bash
git push
```
