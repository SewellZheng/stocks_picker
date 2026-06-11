# Separate Compilation for C Codegen Server — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Compile each C indicator as its own translation unit so the compiler can optimize each function independently, eliminating the ~1.2x performance gap for MINMAX/STOCHF.

**Architecture:** Move `static` global definitions from `ta_lib_types.h` to `ta_lib_globals.c`. Server stops `#include`-ing indicator `.c` files, uses `ta_func.h` extern declarations instead. Build step compiles each `.c` separately then links.

**Tech Stack:** Rust (codegen tool), C (generated output), gcc

**Spec:** `docs/superpowers/specs/2026-03-19-separate-compilation-design.md`

---

### Task 1: Move globals from header to `.c` file

**Files:**
- Modify: `ta_func_defs/lib/c/ta_lib_types.h:94-109`
- Modify: `ta_func_defs/lib/c/ta_lib_globals.c`

- [ ] **Step 1: Replace static definitions with extern declarations in header**

In `ta_func_defs/lib/c/ta_lib_types.h`, replace lines 94-109:
```c
static TA_GlobalsType ta_globals_data = {
    .candleSettings = {
        ...
    }
};
static TA_GlobalsType *TA_Globals = &ta_globals_data;
```
with:
```c
extern TA_GlobalsType ta_globals_data;
extern TA_GlobalsType *TA_Globals;
```

- [ ] **Step 2: Add actual definitions to ta_lib_globals.c**

In `ta_func_defs/lib/c/ta_lib_globals.c`, add after the existing `TA_SetUnstablePeriod` function:
```c
/* Candle settings — one definition, declared extern in ta_lib_types.h */
TA_GlobalsType ta_globals_data = {
    .candleSettings = {
        { TA_BodyLong,        TA_RangeType_RealBody, 10, 1.0 },
        { TA_BodyVeryLong,    TA_RangeType_RealBody, 10, 3.0 },
        { TA_BodyShort,       TA_RangeType_RealBody, 10, 1.0 },
        { TA_BodyDoji,        TA_RangeType_HighLow,  10, 0.1 },
        { TA_ShadowLong,      TA_RangeType_RealBody,  0, 1.0 },
        { TA_ShadowVeryLong,  TA_RangeType_RealBody,  0, 2.0 },
        { TA_ShadowShort,     TA_RangeType_Shadows,  10, 1.0 },
        { TA_ShadowVeryShort, TA_RangeType_HighLow,  10, 0.1 },
        { TA_Near,            TA_RangeType_HighLow,   5, 0.2 },
        { TA_Far,             TA_RangeType_HighLow,   5, 0.6 },
        { TA_Equal,           TA_RangeType_HighLow,   5, 0.05 },
    }
};
TA_GlobalsType *TA_Globals = &ta_globals_data;
```

- [ ] **Step 3: Verify templates compile standalone**

```bash
cd ta_codegen_output/c
# After regenerating (next task), compile one indicator + globals to verify linkage:
gcc -c -O3 -DNDEBUG ta_lib_globals.c -o /tmp/globals.o
gcc -c -O3 -DNDEBUG ta_CCI.c -o /tmp/cci.o
# Should succeed with no errors
```

- [ ] **Step 4: Commit**

```bash
git add ta_func_defs/lib/c/ta_lib_types.h ta_func_defs/lib/c/ta_lib_globals.c
git commit -m "refactor: move candle settings globals from header to .c file

Extern declarations in ta_lib_types.h, definitions in ta_lib_globals.c.
Enables separate compilation of each indicator."
```

---

### Task 2: Update server generation to use extern declarations

**Files:**
- Modify: `tools/ta_codegen/src/server_gen.rs:333-363`

- [ ] **Step 1: Replace includes with ta_func.h**

In `server_gen.rs`, replace lines 333-363 (the header comment, `#include "ta_lib_globals.c"`, and all `#include "ta_*.c"` lines) with:
```rust
    // Header
    s.push_str("/* Auto-generated JSON-RPC server for ta_codegen C output.\n");
    s.push_str(" * Reads JSON-RPC requests from stdin, writes responses to stdout.\n");
    s.push_str(" * Build: compile each ta_*.c separately, then link with this file.\n");
    s.push_str(" */\n");
    s.push_str("#include <stdio.h>\n");
    s.push_str("#include <stdlib.h>\n");
    s.push_str("#include <string.h>\n");
    s.push_str("#include <math.h>\n");
    s.push_str("#include <time.h>\n");
    s.push_str("#ifdef __APPLE__\n");
    s.push_str("#include <mach/mach_time.h>\n");
    s.push_str("#endif\n\n");

    // Use extern declarations — each indicator is compiled separately
    s.push_str("#include \"ta_func.h\"\n\n");
```

This removes:
- `#include "ta_lib_globals.c"` (now compiled separately)
- All `#include "ta_*.c"` lines (now compiled separately)
- The sorted_names/MA-ordering logic (no longer needed — linker handles ordering)

- [ ] **Step 2: Run codegen tests**

```bash
cd tools/ta_codegen && cargo test
```

- [ ] **Step 3: Regenerate server source**

```bash
cd tools/ta_codegen && cargo run --release -- generate-servers --backend=c
```

- [ ] **Step 4: Verify server source has no #include "ta_*.c" lines**

```bash
grep '#include "ta_' ta_codegen_output/c/ta_codegen_serve.c
# Should show ONLY: #include "ta_func.h"
```

- [ ] **Step 5: Commit**

```bash
git add tools/ta_codegen/src/server_gen.rs
git commit -m "refactor(server_gen): use extern declarations instead of #include .c files"
```

---

### Task 3: Update build step to compile separately

**Files:**
- Modify: `tools/ta_codegen/src/main.rs:330-349`

- [ ] **Step 1: Replace single-file gcc call with separate compilation**

Replace the C build block (lines 330-349 in `main.rs`) with:
```rust
            "c" => {
                print!("  Building C server... ");
                let c_dir = out_base.join("c");

                // Collect all .c files to compile
                let mut c_files: Vec<std::path::PathBuf> = Vec::new();
                c_files.push(c_dir.join("ta_lib_globals.c"));
                c_files.push(c_dir.join("ta_codegen_serve.c"));
                for entry in std::fs::read_dir(&c_dir).unwrap() {
                    let path = entry.unwrap().path();
                    if let Some(name) = path.file_name().and_then(|n| n.to_str()) {
                        if name.starts_with("ta_") && name.ends_with(".c")
                            && name != "ta_lib_globals.c"
                            && name != "ta_codegen_serve.c"
                            && name != "ta_codegen_funcs.c"
                        {
                            c_files.push(path);
                        }
                    }
                }

                // Compile each .c to .o
                // Clean stale .o files from previous builds
                let obj_dir = c_dir.join("obj");
                let _ = std::fs::remove_dir_all(&obj_dir);
                std::fs::create_dir_all(&obj_dir).ok();
                let mut obj_files: Vec<String> = Vec::new();
                let mut compile_ok = true;

                for c_file in &c_files {
                    let stem = c_file.file_stem().unwrap().to_str().unwrap();
                    let obj_file = obj_dir.join(format!("{stem}.o"));
                    let status = std::process::Command::new("gcc")
                        .args([
                            "-c",
                            "-O3",
                            "-DNDEBUG",
                            "-Wno-parentheses-equality",
                            &format!("-I{}", c_dir.to_str().unwrap()),
                            "-o",
                            obj_file.to_str().unwrap(),
                            c_file.to_str().unwrap(),
                        ])
                        .status();
                    match status {
                        Ok(s) if s.success() => {
                            obj_files.push(obj_file.to_str().unwrap().to_string());
                        }
                        Ok(s) => {
                            println!("FAILED compiling {} (exit {})",
                                stem, s.code().unwrap_or(-1));
                            compile_ok = false;
                            break;
                        }
                        Err(e) => {
                            println!("FAILED (gcc not found: {})", e);
                            compile_ok = false;
                            break;
                        }
                    }
                }

                if compile_ok {
                    // Link all .o files
                    let dst = bin_dir.join("ta_codegen_serve_c");
                    let mut link_args: Vec<String> = vec![
                        "-o".to_string(),
                        dst.to_str().unwrap().to_string(),
                    ];
                    link_args.extend(obj_files);
                    link_args.push("-lm".to_string());

                    match std::process::Command::new("gcc")
                        .args(&link_args)
                        .status()
                    {
                        Ok(s) if s.success() => println!("OK ({} files)", c_files.len()),
                        Ok(s) => println!("LINK FAILED (exit {})", s.code().unwrap_or(-1)),
                        Err(e) => println!("LINK FAILED (gcc not found: {})", e),
                    }
                }
            }
```

- [ ] **Step 2: Run the build**

```bash
cd tools/ta_codegen && cargo run --release -- build --backend=c
```
Expected: `Building C server... OK (165 files)` (163 indicators + globals + server)

- [ ] **Step 3: Commit**

```bash
git add tools/ta_codegen/src/main.rs
git commit -m "refactor(build): compile C indicators separately then link

Each indicator gets its own translation unit so the compiler can
optimize each function independently."
```

---

### Task 4: Regenerate, test, and benchmark

**Files:**
- Regenerated: `ta_codegen_output/c/*`

- [ ] **Step 1: Full regenerate and build**

```bash
cd tools/ta_codegen
cargo run --release -- generate --backend=c
cargo run --release -- generate-servers --backend=c
cargo run --release -- build --backend=c
```

- [ ] **Step 2: Run regtest for correctness**

```bash
cd bin && ./ta_regtest --codegen --language=c
```
Expected: All 163 indicators pass.

- [ ] **Step 3: Benchmark MINMAX and STOCHF**

```bash
cd bin
for run in 1 2 3; do
  echo "=== Run $run ==="
  ./ta_bench --language=cref,c --function=MINMAX,STOCHF --points=100000 --iters=500
done
```
Expected: MINMAX and STOCHF within ~5% of C-ref (parity).

- [ ] **Step 4: Full benchmark**

```bash
cd bin && ./ta_bench --language=cref,c --points=100000 --iters=200
```
Verify no regressions — all indicators at parity or better.

- [ ] **Step 5: Commit regenerated output**

```bash
git add ta_codegen_output/c/
git commit -m "chore: regenerated C output with separate compilation support"
```

- [ ] **Step 6: Final commit — all changes together**

If preferred, squash into one commit or leave as separate commits.
