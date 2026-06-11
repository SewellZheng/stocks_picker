# Rust Crate Restructure & Server Generation Implementation Plan

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Restructure Rust codegen output into a self-contained Cargo crate with a JSON-RPC server binary, then pass all 163 indicators in `ta_regtest --codegen --language=rust`.

**Architecture:** Change `generate --backend=rust` to emit a complete Cargo crate at `ta_codegen_output/rust/` with indicator code, scaffolding, and a serde_json-based server binary. The old `rust/` top-level directory is deleted.

**Tech Stack:** Rust, serde_json, ta_codegen (code generator)

**Spec:** `docs/superpowers/specs/2026-03-14-rust-server-generation-design.md`

---

## Chunk 1: Crate Restructure

### Task 1: Change Rust Backend Output Path and Generate Crate Scaffolding

Change `generate --backend=rust` to write indicator files into a proper crate structure and emit all scaffolding files.

**Files:**
- Modify: `tools/ta_codegen/src/main.rs` (~line 852-858, Rust backend case in generate)
- Modify: `tools/ta_codegen/src/backends/rust_lang.rs` (add embedded float.rs content)
- Create: `ta_codegen_output/rust/Cargo.toml` (generated)
- Create: `ta_codegen_output/rust/src/lib.rs` (generated)
- Create: `ta_codegen_output/rust/src/ta_func/mod.rs` (generated)
- Create: `ta_codegen_output/rust/src/ta_func/float.rs` (generated)
- Move: `ta_codegen_output/rust/*.rs` → `ta_codegen_output/rust/src/ta_func/*.rs`

- [ ] **Step 1: Read the current Rust generate case in main.rs**

Read `tools/ta_codegen/src/main.rs` lines 852-858. Currently:
```rust
"rust" => {
    let output = backends::rust_lang::generate(func_def, enums, registry, helpers);
    let dir = out_base.join("rust");
    std::fs::create_dir_all(&dir).unwrap();
    let path = dir.join(format!("{}.rs", func_def.name.to_lowercase()));
    std::fs::write(&path, &output).unwrap();
    println!("  {} -> {}", func_def.name, path.display());
}
```

- [ ] **Step 2: Change output path to crate structure**

Change the Rust case in the per-function generate loop (~line 852) to write into `src/ta_func/`:

```rust
"rust" => {
    let output = backends::rust_lang::generate(func_def, enums, registry, helpers);
    let dir = out_base.join("rust/src/ta_func");
    std::fs::create_dir_all(&dir).unwrap();
    let path = dir.join(format!("{}.rs", func_def.name.to_lowercase()));
    std::fs::write(&path, &output).unwrap();
    println!("  {} -> {}", func_def.name, path.display());
}
```

- [ ] **Step 3: Add crate scaffolding generation**

In `main.rs`, find where the per-function generate loop runs for Rust. AFTER all functions are generated, add scaffolding generation. Find the appropriate place — likely after the `for func_def in &funcs` loop, or add a post-generation step.

Look for where other post-generation work happens (e.g., the `generate_servers` function is separate). Add a new function or a post-loop block that generates:

**Cargo.toml** at `ta_codegen_output/rust/Cargo.toml`:
```toml
[package]
name = "ta-lib"
version = "0.6.4"
edition = "2021"

[lib]
name = "ta_lib"
path = "src/lib.rs"

[[bin]]
name = "ta_codegen_serve"
path = "src/bin/ta_codegen_serve.rs"

[dependencies]
serde_json = "1"
```

**src/lib.rs** at `ta_codegen_output/rust/src/lib.rs`:
```rust
#![allow(non_snake_case, unused_variables, unused_assignments, unused_mut, unused_parens)]
pub mod ta_func;
pub use ta_func::*;
```

**src/ta_func/mod.rs** — generated dynamically with:
1. `RetCode` enum (copy from `rust/src/ta_func/mod.rs` lines 40-54)
2. `Compatibility` enum (lines 57-63)
3. `FuncUnstId` enum (lines 70-122)
4. `mod float; pub use float::TaFloat;`
5. `Core` struct with `unstable_period`, `compatibility` fields
6. `Core` impl with `new()`, `set_unstable_period()`, `set_compatibility()`, `get_unstable_period()`, `get_compatibility()`
7. `mod <name>;` for each of the 163 indicators (from the `funcs` list)

Generate this in `main.rs` after the per-function loop, using the `funcs` list to build the mod declarations.

**src/ta_func/float.rs** — embed the content of `rust/src/ta_func/float.rs` as a const string in `rust_lang.rs` (or `main.rs`) and write it out. The file is 133 lines. Read it and embed it.

- [ ] **Step 4: Implement the scaffolding generation**

Add a function in `main.rs` (or a helper in `rust_lang.rs`):

```rust
fn generate_rust_crate_scaffolding(out_base: &Path, funcs: &[ir::FuncDef]) {
    let rust_dir = out_base.join("rust");

    // Cargo.toml
    let cargo_toml = r#"[package]
name = "ta-lib"
version = "0.6.4"
edition = "2021"

[lib]
name = "ta_lib"
path = "src/lib.rs"

[[bin]]
name = "ta_codegen_serve"
path = "src/bin/ta_codegen_serve.rs"

[dependencies]
serde_json = "1"
"#;
    std::fs::write(rust_dir.join("Cargo.toml"), cargo_toml).unwrap();

    // src/lib.rs
    let lib_rs = r#"#![allow(non_snake_case, unused_variables, unused_assignments, unused_mut, unused_parens)]
pub mod ta_func;
pub use ta_func::*;
"#;
    std::fs::create_dir_all(rust_dir.join("src")).unwrap();
    std::fs::write(rust_dir.join("src/lib.rs"), lib_rs).unwrap();

    // src/ta_func/mod.rs
    let mut mod_rs = String::new();
    // ... build mod.rs content with RetCode, Compatibility, FuncUnstId, Core, mod declarations
    // (see Step 3 for the content)

    std::fs::write(rust_dir.join("src/ta_func/mod.rs"), &mod_rs).unwrap();

    // src/ta_func/float.rs
    std::fs::write(rust_dir.join("src/ta_func/float.rs"), FLOAT_RS_CONTENT).unwrap();

    // src/bin/ directory (for server binary)
    std::fs::create_dir_all(rust_dir.join("src/bin")).unwrap();
}
```

For the `mod.rs` content, build it dynamically:
```rust
mod_rs.push_str("/* Generated by ta_codegen */\n\n");
// Add RetCode, Compatibility, FuncUnstId enums (embed as string)
mod_rs.push_str(RUST_MOD_HEADER);  // const with enums + Core struct
// Add mod declarations for each indicator
for func in funcs {
    mod_rs.push_str(&format!("mod {};\n", func.name.to_lowercase()));
}
```

The `RUST_MOD_HEADER` const should contain everything from the current `rust/src/ta_func/mod.rs` (RetCode through the end of the Core impl block).

- [ ] **Step 5: Call scaffolding generation from the generate command**

In `main.rs`, find the `generate` function. After the per-function loop completes for Rust, call the scaffolding function:

```rust
// After all functions are generated, emit crate scaffolding for Rust
if backends_to_run.contains(&"rust") {
    generate_rust_crate_scaffolding(&out_base, &funcs);
}
```

- [ ] **Step 6: Build and verify**

```bash
cd tools/ta_codegen && cargo build 2>&1 | tail -5
cargo run -- generate --backend=rust 2>&1 | tail -5
```

Expected: Files generated in `ta_codegen_output/rust/src/ta_func/`.

```bash
ls ta_codegen_output/rust/Cargo.toml ta_codegen_output/rust/src/lib.rs ta_codegen_output/rust/src/ta_func/mod.rs ta_codegen_output/rust/src/ta_func/float.rs
```

Expected: All 4 files exist.

```bash
ls ta_codegen_output/rust/src/ta_func/*.rs | wc -l
```

Expected: 165 (163 indicators + mod.rs + float.rs).

- [ ] **Step 7: Try to compile the Rust crate**

```bash
cd ../../ta_codegen_output/rust && cargo check --lib 2>&1 | tail -20
```

This will likely show compilation errors in the generated indicator code. That's expected — the Rust backend has known issues. But the scaffolding itself (Cargo.toml, lib.rs, mod.rs, float.rs) should be structurally correct. If the errors are ONLY in the indicator `.rs` files and not in the scaffolding, we're good.

- [ ] **Step 8: Commit**

```bash
cd ../../tools/ta_codegen && git add src/main.rs src/backends/rust_lang.rs
git commit -m "feat(rust): restructure codegen output into self-contained Cargo crate

Indicator files now output to ta_codegen_output/rust/src/ta_func/.
Generates Cargo.toml, lib.rs, mod.rs, float.rs scaffolding.

Co-Authored-By: Claude Opus 4.6 (1M context) <noreply@anthropic.com>"
```

---

### Task 2: Update Server Generation Paths

The `generate_rust_server()` (already implemented) writes to `rust/src/bin/`. Change it to write to `ta_codegen_output/rust/src/bin/`.

**Files:**
- Modify: `tools/ta_codegen/src/main.rs` (~line 256-260, Rust server gen path)
- Modify: `tools/ta_codegen/src/main.rs` (~line 508-520, Rust build path)

- [ ] **Step 1: Fix server generation output path**

In `main.rs`, find the `generate-servers` Rust case (~line 256). Change:
```rust
"rust" => {
    let rust_bin_dir = Path::new("../../rust/src/bin");
```
To:
```rust
"rust" => {
    let rust_bin_dir = out_base.join("rust/src/bin");
```

This uses `out_base` (`../../ta_codegen_output`) instead of hardcoding `../../rust`.

- [ ] **Step 2: Fix build path**

In `main.rs`, find the `build_servers` Rust case (~line 508). Change:
```rust
let rust_dir = Path::new("../../rust");
```
To:
```rust
let rust_dir = out_base.join("rust");
```

- [ ] **Step 3: Build and verify**

```bash
cd tools/ta_codegen && cargo build && cargo run -- generate-servers --backend=rust 2>&1
```

Expected: `Rust server -> ../../ta_codegen_output/rust/src/bin/ta_codegen_serve.rs`

- [ ] **Step 4: Commit**

```bash
git add src/main.rs && git commit -m "fix(rust): update server gen and build paths to ta_codegen_output/rust

Co-Authored-By: Claude Opus 4.6 (1M context) <noreply@anthropic.com>"
```

---

### Task 3: Delete Old `rust/` Directory

**Files:**
- Delete: `rust/` (entire directory)
- Modify: `tools/ta_codegen/src/main.rs` (remove old Cargo.toml/bin setup from Task RS-1)

- [ ] **Step 1: Remove old rust/ directory**

```bash
cd /Users/chadfurman/projects/ta-lib && rm -rf rust/
```

- [ ] **Step 2: Clean up any references**

Check `main.rs` for any remaining references to `../../rust`:

```bash
grep -n "../../rust" tools/ta_codegen/src/main.rs
```

If any remain from earlier tasks (RS-1 added Cargo.toml changes to `rust/`), they should already be fixed by Task 2. If not, fix them.

- [ ] **Step 3: Verify ta_codegen still works**

```bash
cd tools/ta_codegen && cargo test --lib --test backend_suite 2>&1 | tail -5
```

- [ ] **Step 4: Commit**

```bash
git add -u && git commit -m "chore: delete legacy rust/ crate — replaced by ta_codegen_output/rust

Co-Authored-By: Claude Opus 4.6 (1M context) <noreply@anthropic.com>"
```

---

## Chunk 2: Compilation & Regression Testing

### Task 4: Fix Rust Indicator Compilation Errors

The generated Rust indicator code likely has compilation errors. Fix them iteratively.

**Files:**
- Modify: `tools/ta_codegen/src/backends/rust_lang.rs` (fix generation patterns)
- Reference: `ta_codegen_output/rust/src/ta_func/*.rs` (generated output to check)

- [ ] **Step 1: Attempt to compile the crate**

```bash
cd ta_codegen_output/rust && cargo check --lib 2>&1 | head -50
```

Categorize the errors. Common issues in the Rust backend:
- C-style for loops not converted
- Type mismatches (`i32` where `T: TaFloat` expected)
- Missing variable bindings
- Incorrect use of `&mut` references

- [ ] **Step 2: Fix errors in rust_lang.rs**

For each error category, fix the generation pattern in `rust_lang.rs`, then regenerate:

```bash
cd tools/ta_codegen && cargo run -- generate --backend=rust
cd ../../ta_codegen_output/rust && cargo check --lib 2>&1 | head -50
```

Iterate until `cargo check --lib` passes.

- [ ] **Step 3: Build the server binary**

```bash
cd tools/ta_codegen && cargo run -- generate-servers --backend=rust
cd ../../ta_codegen_output/rust && cargo check --bin ta_codegen_serve 2>&1 | tail -10
```

If the server doesn't compile, fix `generate_rust_server()` in `server_gen.rs`.

- [ ] **Step 4: Build release binary**

```bash
cd tools/ta_codegen && cargo run -- build --backend=rust 2>&1
```

Expected: `Building Rust server... OK`

- [ ] **Step 5: Test manually**

```bash
echo '{"method":"list_functions","params":{}}' | ../../bin/ta_codegen_serve_rust | head -c 200
echo '{"method":"TA_SMA","params":{"startIdx":0,"endIdx":4,"inReal":[1.0,2.0,3.0,4.0,5.0],"optInTimePeriod":3}}' | ../../bin/ta_codegen_serve_rust
```

- [ ] **Step 6: Commit**

```bash
cd tools/ta_codegen && git add src/backends/rust_lang.rs src/server_gen.rs
git commit -m "fix(rust): fix indicator compilation errors in Rust backend

Co-Authored-By: Claude Opus 4.6 (1M context) <noreply@anthropic.com>"
```

---

### Task 5: Run Regression Tests

**Files:**
- May modify: `tools/ta_codegen/src/backends/rust_lang.rs` or `server_gen.rs`

- [ ] **Step 1: Rebuild ta_regtest**

```bash
cd cmake-build && cmake .. -DCMAKE_BUILD_TYPE=Release && make ta_regtest -j4 2>&1 | tail -5
```

- [ ] **Step 2: Run Rust regtest**

```bash
cd ../bin && ../cmake-build/bin/ta_regtest --codegen-only --language=rust 2>&1 | tail -30
```

- [ ] **Step 3: Fix any failures**

For each failing function, test manually and compare with C server output. Fix in `rust_lang.rs` or `server_gen.rs` as needed.

- [ ] **Step 4: Run full multi-language regtest**

```bash
../cmake-build/bin/ta_regtest --codegen --language=c,java,dotnet,rust 2>&1 | tail -20
```

Expected: All 4 languages pass all 163 indicators.

- [ ] **Step 5: Push**

```bash
git push
```
