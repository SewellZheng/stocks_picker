# Codegen Helper Architecture Implementation Plan

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Get all ~158 TA-Lib indicators compiling across C, Java, and Rust backends by implementing helper function inlining, candlestick macro replacement, candle settings unpacking, and remaining macro cleanup.

**Architecture:** Helper C files in `ta_func_defs/helpers/` are parsed into IR at load time, registered in a `HelperRegistry`, and inlined at call sites during backend rendering. A Python script transforms ~1200 candlestick macro calls to explicit helper function calls. Candle settings move from globals to Core instance in Rust/Java.

**Tech Stack:** Rust (ta_codegen), Python (replacement scripts), C (helper source files, generated output)

**Spec:** `docs/superpowers/specs/2026-03-13-codegen-helper-architecture-design.md`

**Out of scope:** .NET backend (only generates declarations, no function bodies — per spec). Unstable period is already handled by the existing backends. CandleSettings struct definition on Core (Rust/Java) — the unpacking code emitted here assumes the struct exists; it will be defined when the Core struct is built out.

**Note on helper C files:** These files are parse-only — they are consumed by the codegen parser, not compiled directly. No `#include` headers needed.

---

## File Structure

### New files to create:
| File | Responsibility |
|------|----------------|
| `ta_func_defs/helpers/candlestick.c` | 11 candle helper functions (realbody, candlerange, etc.) |
| `ta_func_defs/helpers/range.c` | ta_true_range |
| `ta_func_defs/helpers/rounding.c` | ta_round_pos, ta_sar_rounding |
| `tools/ta_codegen/src/helper_registry.rs` | HelperDef struct, HelperRegistry, helper file parsing + loading |
| `scripts/replace_candle_macros.py` | Candlestick + local-define macro replacement script |

### Existing files to modify:
| File | Changes |
|------|---------|
| `tools/ta_codegen/src/ir.rs` | Add HelperDef, HelperParam structs |
| `tools/ta_codegen/src/lib.rs` | Export `helper_registry` module |
| `tools/ta_codegen/src/parser/c_source.rs` | Add `parse_helper_file()` entry point |
| `tools/ta_codegen/src/backends/c.rs` | Hook inlining into `render_func_call`, add math mappings |
| `tools/ta_codegen/src/backends/rust_lang.rs` | Hook inlining into render, add math mappings |
| `tools/ta_codegen/src/backends/java.rs` | Hook inlining into render, add math mappings |
| `tools/ta_codegen/src/main.rs` | Load helper registry at startup, pass to backends |
| `tools/ta_codegen/tests/backend_suite.rs` | Add helper parsing + inlining tests |
| `scripts/replace_macros.py` | Add ARRAY_LOCAL, ENUM_CASE, and other missed macros |
| `ta_func_defs/**/*.c` (~60 candlestick files) | Transformed by replacement script |

---

## Chunk 1: Foundation — Helper Files, IR, Parser, Registry

### Task 1: Create helper C source files

**Files:**
- Create: `ta_func_defs/helpers/candlestick.c`
- Create: `ta_func_defs/helpers/range.c`
- Create: `ta_func_defs/helpers/rounding.c`

- [ ] **Step 1: Create `ta_func_defs/helpers/` directory**

```bash
mkdir -p ta_func_defs/helpers
```

- [ ] **Step 2: Write `candlestick.c`**

Create `ta_func_defs/helpers/candlestick.c` with all 11 candle helper functions. Each takes explicit params — no globals, no implicit array names. Reference the spec (section 1) for exact signatures. Functions:

```c
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
        case 0: return fabs(close - open);
        case 1: return high - low;
        case 2: return high - low - fabs(close - open);
        default: return 0.0;
    }
}

double ta_candleaverage(int rangeType, int avgPeriod, double factor, double sum,
                        double open, double high, double low, double close) {
    double avg = (avgPeriod != 0)
        ? sum / avgPeriod
        : ta_candlerange(rangeType, open, high, low, close);
    double divisor = (rangeType == 2) ? 2.0 : 1.0;
    return factor * avg / divisor;
}
```

- [ ] **Step 3: Write `range.c`**

```c
double ta_true_range(double th, double tl, double yc) {
    double range = th - tl;
    double tmp = fabs(th - yc);
    if (tmp > range) range = tmp;
    tmp = fabs(tl - yc);
    if (tmp > range) range = tmp;
    return range;
}
```

- [ ] **Step 4: Write `rounding.c`**

```c
double ta_round_pos(double x) {
    return floor(x + 0.5);
}

double ta_sar_rounding(double x) {
    return x;
}
```

- [ ] **Step 5: Commit**

```bash
git add ta_func_defs/helpers/
git commit -m "feat(codegen): add helper C source files for candlestick, range, rounding"
```

---

### Task 2: Add HelperDef and HelperParam to IR

**Files:**
- Modify: `tools/ta_codegen/src/ir.rs`

- [ ] **Step 1: Write failing test**

Add to `tools/ta_codegen/tests/backend_suite.rs`:

```rust
#[test]
fn helper_def_stores_params_and_body() {
    use ta_codegen::ir::{HelperDef, HelperParam, Statement, Expr, VarType};

    let helper = HelperDef {
        name: "ta_realbody".to_string(),
        return_type: VarType::Real,
        params: vec![
            HelperParam { name: "close".to_string(), var_type: VarType::Real },
            HelperParam { name: "open".to_string(), var_type: VarType::Real },
        ],
        body: vec![
            Statement::Return {
                value: Some(Expr::FuncCall("fabs".to_string(), vec![
                    Expr::BinOp(
                        Box::new(Expr::Var("close".to_string())),
                        ta_codegen::ir::BinOp::Sub,
                        Box::new(Expr::Var("open".to_string())),
                    ),
                ])),
            },
        ],
    };
    assert_eq!(helper.name, "ta_realbody");
    assert_eq!(helper.params.len(), 2);
    assert_eq!(helper.params[0].name, "close");
}
```

- [ ] **Step 2: Run test to verify it fails**

```bash
cd tools/ta_codegen && cargo test helper_def_stores_params_and_body
```
Expected: FAIL — `HelperDef` and `HelperParam` don't exist.

- [ ] **Step 3: Add HelperDef and HelperParam structs to ir.rs**

Add at the end of `tools/ta_codegen/src/ir.rs`:

```rust
/// A helper function definition (parsed from ta_func_defs/helpers/*.c).
/// Helpers are inlined at call sites during code generation.
#[derive(Debug, Clone)]
pub struct HelperDef {
    pub name: String,
    pub return_type: VarType,
    pub params: Vec<HelperParam>,
    pub body: Vec<Statement>,
}

/// A parameter of a helper function.
#[derive(Debug, Clone)]
pub struct HelperParam {
    pub name: String,
    pub var_type: VarType,
}
```

- [ ] **Step 4: Run test to verify it passes**

```bash
cd tools/ta_codegen && cargo test helper_def_stores_params_and_body
```
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add tools/ta_codegen/src/ir.rs tools/ta_codegen/tests/backend_suite.rs
git commit -m "feat(codegen): add HelperDef and HelperParam to IR"
```

---

### Task 3: Add parse_helper_file() to the parser

**Files:**
- Modify: `tools/ta_codegen/src/parser/c_source.rs`

**Context:** The existing `parse_c_source()` expects TA-Lib indicator files (lookback + main function). Helper files are simpler — just standalone C functions. We need a new `parse_helper_file()` entry point that reuses the existing tokenizer and expression/statement parsing but has a different top-level structure.

- [ ] **Step 1: Write failing test**

Add to `tools/ta_codegen/tests/backend_suite.rs`:

```rust
#[test]
fn parse_helper_file_extracts_functions() {
    use ta_codegen::parser::c_source::parse_helper_file_str;

    let source = r#"
double ta_realbody(double close, double open) {
    return fabs(close - open);
}

int ta_candlecolor(double close, double open) {
    return (close >= open) ? 1 : -1;
}
"#;

    let helpers = parse_helper_file_str(source);
    assert_eq!(helpers.len(), 2);
    assert_eq!(helpers[0].name, "ta_realbody");
    assert_eq!(helpers[0].params.len(), 2);
    assert_eq!(helpers[0].params[0].name, "close");
    assert_eq!(helpers[1].name, "ta_candlecolor");
    assert_eq!(helpers[1].params.len(), 2);
}
```

- [ ] **Step 2: Run test to verify it fails**

```bash
cd tools/ta_codegen && cargo test parse_helper_file_extracts_functions
```
Expected: FAIL — `parse_helper_file_str` doesn't exist.

- [ ] **Step 3: Implement parse_helper_file()**

Add to `tools/ta_codegen/src/parser/c_source.rs`:

```rust
use crate::ir::{HelperDef, HelperParam};

/// Parse a helper C file containing standalone utility functions.
/// Returns a Vec of HelperDef (one per function in the file).
pub fn parse_helper_file(path: &Path) -> Vec<HelperDef> {
    let input = std::fs::read_to_string(path)
        .unwrap_or_else(|e| panic!("Failed to read {}: {}", path.display(), e));
    parse_helper_file_str(&input)
}

/// Parse helper functions from a string.
pub fn parse_helper_file_str(source: &str) -> Vec<HelperDef> {
    let stripped = strip_local_macros(source);
    let tokens = tokenize(&stripped);
    extract_helper_functions(&tokens)
}
```

Then implement `extract_helper_functions()` — a new top-level extractor that:
1. Scans for a return type token (`double`, `int`, `void`)
2. Reads the function name (next `Ident`)
3. Reads the param list inside `(` ... `)` — pairs of `type name` separated by `,`
4. Reads the body inside `{` ... `}` using the existing `parse_body()` / `parse_statements()`
5. Produces a `HelperDef` for each function
6. Maps C types to VarType: `double` → `VarType::Real`, `int` → `VarType::Integer`

The key reuse: `parse_statements()` (or whatever the internal statement parser is called) already handles all C constructs. We just need a different top-level entry that looks for function definitions instead of the indicator-specific structure.

**Important edge cases:**
- Helper functions may call other helper functions (e.g., `ta_candleaverage` calls `ta_candlerange`). The parser just sees this as a `FuncCall` node — the inliner resolves it later.
- Some params are `int` (rangeType, avgPeriod), not all `double`.

- [ ] **Step 4: Run test to verify it passes**

```bash
cd tools/ta_codegen && cargo test parse_helper_file_extracts_functions
```
Expected: PASS

- [ ] **Step 5: Write additional parser tests**

Test more complex helpers — switch statements, multi-statement bodies, helper-calls-helper:

```rust
#[test]
fn parse_helper_with_switch() {
    use ta_codegen::parser::c_source::parse_helper_file_str;
    use ta_codegen::ir::Statement;

    let source = r#"
double ta_candlerange(int rangeType, double open, double high, double low, double close) {
    switch (rangeType) {
        case 0: return fabs(close - open);
        case 1: return high - low;
        case 2: return high - low - fabs(close - open);
        default: return 0.0;
    }
}
"#;

    let helpers = parse_helper_file_str(source);
    assert_eq!(helpers.len(), 1);
    assert_eq!(helpers[0].name, "ta_candlerange");
    assert_eq!(helpers[0].params.len(), 5);
    // Body should contain a Switch statement
    assert!(matches!(helpers[0].body[0], Statement::Switch { .. }));
}

#[test]
fn parse_helper_file_reads_from_disk() {
    use ta_codegen::parser::c_source::parse_helper_file;
    let path = std::path::Path::new(env!("CARGO_MANIFEST_DIR"))
        .join("../../ta_func_defs/helpers/candlestick.c");
    let helpers = parse_helper_file(&path);
    assert_eq!(helpers.len(), 11);
    assert_eq!(helpers.iter().find(|h| h.name == "ta_realbody").unwrap().params.len(), 2);
    assert_eq!(helpers.iter().find(|h| h.name == "ta_candleaverage").unwrap().params.len(), 8);
}
```

- [ ] **Step 6: Run all tests**

```bash
cd tools/ta_codegen && cargo test
```
Expected: All pass, including new helper parser tests.

- [ ] **Step 7: Commit**

```bash
git add tools/ta_codegen/src/parser/c_source.rs tools/ta_codegen/tests/backend_suite.rs
git commit -m "feat(codegen): add parse_helper_file() for standalone utility functions"
```

---

### Task 4: Create HelperRegistry module

**Files:**
- Create: `tools/ta_codegen/src/helper_registry.rs`
- Modify: `tools/ta_codegen/src/lib.rs`

**Context:** The HelperRegistry scans `ta_func_defs/helpers/*.c`, parses each, and stores all HelperDefs in a HashMap keyed by function name. Backends will query this registry when they encounter a FuncCall to decide whether to inline or emit a normal call.

- [ ] **Step 1: Write failing test**

Add to `tools/ta_codegen/tests/backend_suite.rs`:

```rust
#[test]
fn helper_registry_loads_from_disk() {
    use ta_codegen::helper_registry::HelperRegistry;

    let base = std::path::Path::new(env!("CARGO_MANIFEST_DIR")).join("../../ta_func_defs");
    let registry = HelperRegistry::from_dir(&base);

    // Should find all helpers from candlestick.c, range.c, rounding.c
    assert!(registry.get("ta_realbody").is_some());
    assert!(registry.get("ta_candlerange").is_some());
    assert!(registry.get("ta_true_range").is_some());
    assert!(registry.get("ta_round_pos").is_some());
    assert!(registry.get("ta_sar_rounding").is_some());

    // Should NOT contain indicator functions
    assert!(registry.get("sma").is_none());
    assert!(registry.get("ema").is_none());
}
```

- [ ] **Step 2: Run test to verify it fails**

```bash
cd tools/ta_codegen && cargo test helper_registry_loads_from_disk
```
Expected: FAIL — module doesn't exist.

- [ ] **Step 3: Implement HelperRegistry**

Create `tools/ta_codegen/src/helper_registry.rs`:

```rust
use std::collections::HashMap;
use std::path::Path;

use crate::ir::HelperDef;
use crate::parser::c_source::parse_helper_file;

/// Registry of helper functions available for inlining.
pub struct HelperRegistry {
    helpers: HashMap<String, HelperDef>,
}

impl HelperRegistry {
    /// Build the registry by scanning `base_dir/helpers/*.c`.
    pub fn from_dir(base_dir: &Path) -> Self {
        let mut helpers = HashMap::new();
        let helpers_dir = base_dir.join("helpers");

        if let Ok(entries) = std::fs::read_dir(&helpers_dir) {
            for entry in entries.filter_map(Result::ok) {
                let path = entry.path();
                if path.extension().and_then(|e| e.to_str()) == Some("c") {
                    let parsed = parse_helper_file(&path);
                    for helper in parsed {
                        helpers.insert(helper.name.clone(), helper);
                    }
                }
            }
        }

        HelperRegistry { helpers }
    }

    /// Look up a helper by function name.
    pub fn get(&self, name: &str) -> Option<&HelperDef> {
        self.helpers.get(name)
    }
}
```

- [ ] **Step 4: Add module export to lib.rs**

In `tools/ta_codegen/src/lib.rs`, add:

```rust
pub mod helper_registry;
```

- [ ] **Step 5: Run test to verify it passes**

```bash
cd tools/ta_codegen && cargo test helper_registry_loads_from_disk
```
Expected: PASS

- [ ] **Step 6: Run all tests to check for regressions**

```bash
cd tools/ta_codegen && cargo test
```
Expected: All pass.

- [ ] **Step 7: Commit**

```bash
git add tools/ta_codegen/src/helper_registry.rs tools/ta_codegen/src/lib.rs tools/ta_codegen/tests/backend_suite.rs
git commit -m "feat(codegen): add HelperRegistry for loading helper functions"
```

---

## Chunk 2: Call Site Transformation — Replacement Scripts

### Task 5: Write candlestick and local-define macro replacement script

**Files:**
- Create: `scripts/replace_candle_macros.py`

**Context:** This script transforms ~1200 candlestick macro calls across ~60 files into explicit helper function calls. The mappings are in the spec (section 2). The script must handle arbitrary index expressions (not just single variables — expressions like `i-1`, `lookbackTotal`, `startIdx-2`).

- [ ] **Step 1: Write the script with all candlestick replacements**

Create `scripts/replace_candle_macros.py`. Follow the same structure as `scripts/replace_macros.py` (argparse, `--dry-run`, `--file` flags, recursive over `ta_func_defs/`).

Implement these replacement functions:

```python
def replace_simple_candle_macros(content: str) -> str:
    """Replace simple candlestick macros that only reference OHLC arrays."""

    # TA_REALBODY(IDX) -> ta_realbody(inClose[IDX], inOpen[IDX])
    content = re.sub(
        r'\bTA_REALBODY\(\s*([^)]+?)\s*\)',
        r'ta_realbody(inClose[\1], inOpen[\1])',
        content,
    )

    # TA_CANDLECOLOR(IDX) -> ta_candlecolor(inClose[IDX], inOpen[IDX])
    content = re.sub(
        r'\bTA_CANDLECOLOR\(\s*([^)]+?)\s*\)',
        r'ta_candlecolor(inClose[\1], inOpen[\1])',
        content,
    )

    # TA_UPPERSHADOW(IDX) -> ta_uppershadow(inHigh[IDX], inClose[IDX], inOpen[IDX])
    content = re.sub(
        r'\bTA_UPPERSHADOW\(\s*([^)]+?)\s*\)',
        r'ta_uppershadow(inHigh[\1], inClose[\1], inOpen[\1])',
        content,
    )

    # TA_LOWERSHADOW(IDX) -> ta_lowershadow(inLow[IDX], inClose[IDX], inOpen[IDX])
    content = re.sub(
        r'\bTA_LOWERSHADOW\(\s*([^)]+?)\s*\)',
        r'ta_lowershadow(inLow[\1], inClose[\1], inOpen[\1])',
        content,
    )

    # TA_HIGHLOWRANGE(IDX) -> ta_highlowrange(inHigh[IDX], inLow[IDX])
    content = re.sub(
        r'\bTA_HIGHLOWRANGE\(\s*([^)]+?)\s*\)',
        r'ta_highlowrange(inHigh[\1], inLow[\1])',
        content,
    )

    # TA_REALBODYGAPUP(IDX1, IDX2) — 2-arg macro, need nested capture
    content = re.sub(
        r'\bTA_REALBODYGAPUP\(\s*([^,]+?)\s*,\s*([^)]+?)\s*\)',
        r'ta_realbodygapup(inOpen[\1], inClose[\1], inOpen[\2], inClose[\2])',
        content,
    )

    # TA_REALBODYGAPDOWN(IDX1, IDX2)
    content = re.sub(
        r'\bTA_REALBODYGAPDOWN\(\s*([^,]+?)\s*,\s*([^)]+?)\s*\)',
        r'ta_realbodygapdown(inOpen[\1], inClose[\1], inOpen[\2], inClose[\2])',
        content,
    )

    # TA_CANDLEGAPUP(IDX1, IDX2) -> ta_candlegapup(inLow[IDX1], inHigh[IDX2])
    content = re.sub(
        r'\bTA_CANDLEGAPUP\(\s*([^,]+?)\s*,\s*([^)]+?)\s*\)',
        r'ta_candlegapup(inLow[\1], inHigh[\2])',
        content,
    )

    # TA_CANDLEGAPDOWN(IDX1, IDX2) -> ta_candlegapdown(inHigh[IDX1], inLow[IDX2])
    content = re.sub(
        r'\bTA_CANDLEGAPDOWN\(\s*([^,]+?)\s*,\s*([^)]+?)\s*\)',
        r'ta_candlegapdown(inHigh[\1], inLow[\2])',
        content,
    )

    return content


def replace_candle_settings_macros(content: str) -> str:
    """Replace candlestick macros that reference candle settings."""

    # TA_CANDLEAVGPERIOD(Set) -> Set_avgPeriod
    content = re.sub(
        r'\bTA_CANDLEAVGPERIOD\(\s*([A-Za-z]+)\s*\)',
        r'\1_avgPeriod',
        content,
    )

    # TA_CANDLERANGE(Set, IDX) -> ta_candlerange(Set_rangeType, inOpen[IDX], inHigh[IDX], inLow[IDX], inClose[IDX])
    content = re.sub(
        r'\bTA_CANDLERANGE\(\s*([A-Za-z]+)\s*,\s*([^)]+?)\s*\)',
        r'ta_candlerange(\1_rangeType, inOpen[\2], inHigh[\2], inLow[\2], inClose[\2])',
        content,
    )

    # TA_CANDLEAVERAGE(Set, sum, IDX) -> ta_candleaverage(Set_rangeType, Set_avgPeriod, Set_factor, sum, inOpen[IDX], inHigh[IDX], inLow[IDX], inClose[IDX])
    content = re.sub(
        r'\bTA_CANDLEAVERAGE\(\s*([A-Za-z]+)\s*,\s*([^,]+?)\s*,\s*([^)]+?)\s*\)',
        r'ta_candleaverage(\1_rangeType, \1_avgPeriod, \1_factor, \2, inOpen[\3], inHigh[\3], inLow[\3], inClose[\3])',
        content,
    )

    return content


def replace_local_define_helpers(content: str) -> str:
    """Replace local-define helper macros."""

    # TRUE_RANGE(th, tl, yc, out) -> out = ta_true_range(th, tl, yc)
    content = re.sub(
        r'\bTRUE_RANGE\(\s*([^,]+?)\s*,\s*([^,]+?)\s*,\s*([^,]+?)\s*,\s*([^)]+?)\s*\)',
        r'\4 = ta_true_range(\1, \2, \3)',
        content,
    )

    # round_pos(x) -> ta_round_pos(x)
    content = re.sub(r'\bround_pos\(', 'ta_round_pos(', content)

    # SAR_ROUNDING(x) -> ta_sar_rounding(x)
    content = re.sub(r'\bSAR_ROUNDING\(', 'ta_sar_rounding(', content)

    return content
```

Also add the `main()` with argparse matching the structure of `replace_macros.py` — `--dry-run`, `--file` flags, processes all `.c` files under `ta_func_defs/` recursively (excluding `helpers/`).

**Regex edge case:** The `[^)]+?` captures may fail on nested parentheses like `TA_CANDLEAVERAGE(BodyLong, sum, (i-1))`. Verify actual call sites in the codebase first — if any have nested parens, use a balanced-paren matching approach (e.g., `re` with a manual paren counter) for those specific macros.

- [ ] **Step 2: Dry-run on a single file to verify**

```bash
python3 scripts/replace_candle_macros.py --dry-run --file ta_func_defs/cdl2crows/cdl2crows.c
```
Expected: Shows diff with `TA_CANDLEAVGPERIOD(BodyLong)` → `BodyLong_avgPeriod`, etc.

- [ ] **Step 3: Commit the script (before running)**

```bash
git add scripts/replace_candle_macros.py
git commit -m "feat: add candlestick and local-define macro replacement script"
```

---

### Task 6: Run candlestick macro replacement on all files

**Files:**
- Modify: `ta_func_defs/**/*.c` (~60 candlestick files + a few others with TRUE_RANGE/round_pos)

- [ ] **Step 1: Run the replacement script**

```bash
python3 scripts/replace_candle_macros.py
```
Expected: Reports files modified with change counts.

- [ ] **Step 2: Verify no candlestick macros remain**

```bash
cd ta_func_defs && grep -r "TA_REALBODY\|TA_CANDLECOLOR\|TA_UPPERSHADOW\|TA_LOWERSHADOW\|TA_HIGHLOWRANGE\|TA_REALBODYGAP\|TA_CANDLEGAP\|TA_CANDLERANGE\|TA_CANDLEAVGPERIOD\|TA_CANDLEAVERAGE" --include="*.c" | grep -v helpers/ | head -20
```
Expected: No output (all replaced). If any remain, debug the regex.

- [ ] **Step 3: Verify no TRUE_RANGE/round_pos/SAR_ROUNDING macros remain**

```bash
cd ta_func_defs && grep -rn "\bTRUE_RANGE(\|SAR_ROUNDING(" --include="*.c" | grep -v helpers/ | head -10
```
Expected: No output.

- [ ] **Step 4: Spot-check a transformed file**

Read `ta_func_defs/cdl2crows/cdl2crows.c` and verify:
- `TA_CANDLEAVGPERIOD(BodyLong)` → `BodyLong_avgPeriod`
- `TA_CANDLERANGE( BodyLong, i )` → `ta_candlerange(BodyLong_rangeType, inOpen[i], inHigh[i], inLow[i], inClose[i])`
- `TA_REALBODY(i)` → `ta_realbody(inClose[i], inOpen[i])`

- [ ] **Step 5: Quick parse check — run existing tests to catch parse failures early**

```bash
cd tools/ta_codegen && cargo test
```
Expected: Existing auto-discovery tests parse all indicators. If any fail, debug the replacement regex before committing.

- [ ] **Step 6: Commit**

```bash
git add ta_func_defs/
git commit -m "refactor: replace candlestick macros with explicit helper function calls

~1200 macro calls across ~60 files transformed to explicit function calls.
Candlestick settings (rangeType, avgPeriod, factor) now referenced as
local variables (e.g., BodyLong_rangeType) to be unpacked from Core."
```

---

### Task 7: Inline-expand statement-block macros + update replace_macros.py

**Files:**
- Modify: `scripts/replace_macros.py`
- Modify: `ta_func_defs/adosc/adosc.c` (CALCULATE_AD — 3 call sites)
- Modify: `ta_func_defs/ultosc/ultosc.c` (CALC_TERMS — 6 calls, PRIME_TOTALS — 4 calls)

**Context:** These macros mutate multiple locals and can't be helper functions. They must be inline-expanded at each call site. Also add remaining missed macros to `replace_macros.py`.

- [ ] **Step 1: Manually inline-expand CALCULATE_AD in adosc.c**

Read `src/ta_func/ta_ADOSC.c` to find the original `CALCULATE_AD` macro body. Then read `ta_func_defs/adosc/adosc.c` to find the 3 call sites. Replace each `CALCULATE_AD` with the equivalent C statements that reference the existing local variables.

- [ ] **Step 2: Manually inline-expand CALC_TERMS and PRIME_TOTALS in ultosc.c**

Read `src/ta_func/ta_ULTOSC.c` to find the original macro bodies. Then read `ta_func_defs/ultosc/ultosc.c` and replace:
- Each `CALC_TERMS(day)` with the 6-line expansion
- Each `PRIME_TOTALS(a, b, period)` with the loop expansion

- [ ] **Step 3: Add remaining missed macros to replace_macros.py**

Add these replacement rules to `scripts/replace_macros.py`:

```python
# ARRAY_LOCAL(name, size) -> double name[size];
content = re.sub(
    r'\bARRAY_LOCAL\(\s*([^,]+?)\s*,\s*([^)]+?)\s*\)',
    r'double \1[\2];',
    content,
)

# ENUM_CASE(type, c_val, pascal_val) -> case c_val:
content = re.sub(
    r'\bENUM_CASE\(\s*[^,]+?\s*,\s*([^,]+?)\s*,\s*[^)]+?\s*\)',
    r'case \1:',
    content,
)

# TA_INTERNAL_ERROR(code) -> return TA_INTERNAL_ERROR;
content = re.sub(
    r'\bTA_INTERNAL_ERROR\(\s*[^)]*\)\s*;',
    'return TA_INTERNAL_ERROR;',
    content,
)

# ENUM_DECLARATION(RetCode) -> TA_RetCode  (if any remaining)
content = re.sub(
    r'\bENUM_DECLARATION\(\s*RetCode\s*\)',
    'TA_RetCode',
    content,
)

# TA_ARRAY_COPY(dst, dstOff, src, srcOff, n) -> memcpy(...)
content = re.sub(
    r'\bTA_ARRAY_COPY\(\s*([^,]+?)\s*,\s*([^,]+?)\s*,\s*([^,]+?)\s*,\s*([^,]+?)\s*,\s*([^)]+?)\s*\)',
    r'memcpy(&\1[\2], &\3[\4], \5 * sizeof(double))',
    content,
)
```

Also handle any remaining `ARRAY_ALLOC` and `CIRCBUF_INIT` (1 remaining call in cci.c — manually expand).

**Note:** `replace_macros.py` is idempotent — the regex patterns only match the macro form, not the already-expanded C. Re-running after Task 6's candlestick changes is safe.

- [ ] **Step 4: Run updated replace_macros.py**

```bash
python3 scripts/replace_macros.py
```

- [ ] **Step 5: Verify no missed macros remain**

```bash
cd ta_func_defs && grep -rn "ARRAY_LOCAL\|ENUM_CASE\|TA_INTERNAL_ERROR\|CALCULATE_AD\|CALC_TERMS\|PRIME_TOTALS" --include="*.c" | grep -v helpers/ | head -20
```
Expected: No output.

- [ ] **Step 6: Commit**

```bash
git add scripts/replace_macros.py ta_func_defs/
git commit -m "refactor: inline-expand statement-block macros and remaining missed macros

CALCULATE_AD expanded in adosc.c (3 sites), CALC_TERMS/PRIME_TOTALS in
ultosc.c (10 sites). ARRAY_LOCAL, ENUM_CASE, TA_INTERNAL_ERROR added to
replace_macros.py and run on all files."
```

---

## Chunk 3: Inlining Mechanism — Expression and Block Inlining

### Task 8: Add math function mappings (max, min, fmax, fmin, ABS)

**Files:**
- Modify: `tools/ta_codegen/src/backends/c.rs`
- Modify: `tools/ta_codegen/src/backends/rust_lang.rs`
- Modify: `tools/ta_codegen/src/backends/java.rs`

- [ ] **Step 1: Write failing test**

Add to `tools/ta_codegen/tests/backend_suite.rs`:

```rust
#[test]
fn backends_render_max_min_fmax_fmin_abs() {
    // Build a minimal FuncDef whose body contains:
    //   x = max(a, b);
    //   y = fmin(c, d);
    //   z = ABS(e);
    // Use the same test helper pattern as existing backend_suite tests
    // (create FuncDef, generate each backend, check output contains expected strings).
    //
    // Assert C output contains: "fmax(a, b)", "fmin(c, d)", "fabs(e)"
    // Assert Rust output contains: "a.max(b)", "c.min(d)", "e.abs()"
    // Assert Java output contains: "Math.max(a, b)", "Math.min(c, d)", "Math.abs(e)"
    //
    // See existing test `backends_render_math_functions_idiomatically` for pattern.
}
```

- [ ] **Step 2: Add mappings to each backend's MATH_FUNCTIONS and render_func_call**

In `c.rs`: Add `"max"`, `"min"`, `"fmax"`, `"fmin"`, `"ABS"` to the math function handling in `render_func_call`. Map `max`→`fmax`, `min`→`fmin`, `ABS`→`fabs`, pass `fmax`/`fmin` through.

In `rust_lang.rs`: Map `max`/`fmax` → `a.max(b)`, `min`/`fmin` → `a.min(b)`, `ABS` → `e.abs()`.

In `java.rs`: Map `max`/`fmax` → `Math.max(a, b)`, `min`/`fmin` → `Math.min(a, b)`, `ABS` → `Math.abs(e)`.

- [ ] **Step 3: Run tests**

```bash
cd tools/ta_codegen && cargo test
```

- [ ] **Step 4: Commit**

```bash
git add tools/ta_codegen/src/backends/ tools/ta_codegen/tests/backend_suite.rs
git commit -m "feat(codegen): add max/min/fmax/fmin/ABS math function mappings to all backends"
```

---

### Task 9: Implement expression inlining for single-expression helpers

**Files:**
- Modify: `tools/ta_codegen/src/backends/c.rs`
- Modify: `tools/ta_codegen/src/backends/rust_lang.rs`
- Modify: `tools/ta_codegen/src/backends/java.rs`
- Modify: `tools/ta_codegen/src/main.rs` (pass helper_registry to backends)

**Context:** When a backend encounters `FuncCall("ta_realbody", [inClose[i], inOpen[i]])` and `ta_realbody` is in the HelperRegistry, it should substitute the args into the helper's IR body and render the resulting expression inline. For single-expression helpers (body is one `Return { value: Some(expr) }`), this produces a simple expression substitution.

- [ ] **Step 1: Write failing test**

```rust
#[test]
fn c_backend_inlines_single_expr_helper() {
    // Setup: create a HelperRegistry with ta_realbody
    // Create a FuncDef indicator that calls ta_realbody(inClose[i], inOpen[i])
    // Generate C code
    // Assert output contains "fabs(inClose[i] - inOpen[i])" NOT "ta_realbody("
}
```

- [ ] **Step 2: Implement the inlining infrastructure**

The approach: add a shared inlining module or function that all backends can use. The inliner works at the IR level:

1. Given a `FuncCall(name, args)` and the `HelperDef` from registry:
2. Build a substitution map: `param_name → actual_arg_expr`
3. Clone the helper's body, walking the IR tree and replacing every `Expr::Var(param_name)` with the corresponding actual arg
4. If the body is a single `Return { value: Some(expr) }`, return just the substituted expression (expression inlining)
5. Otherwise, it's a block inline (Task 10)

Create a new function (could live in `helper_registry.rs` or a new `inliner.rs`):

```rust
/// Substitute helper parameters with actual arguments in an expression.
/// IMPORTANT: Must recurse into ALL 13 Expr variants. Do not use a `_ =>` fallback
/// that clones without recursion — that would silently skip substitution inside
/// Ternary, ArrayAccess, Not, Cast, PostIncrement, PostDecrement.
pub fn substitute_expr(expr: &Expr, subs: &HashMap<String, Expr>) -> Expr {
    match expr {
        Expr::Var(name) => subs.get(name.as_str()).cloned().unwrap_or_else(|| expr.clone()),
        Expr::Literal(_) | Expr::IntLiteral(_) | Expr::PointerDeref(_) => expr.clone(),
        Expr::BinOp(l, op, r) => Expr::BinOp(
            Box::new(substitute_expr(l, subs)),
            op.clone(),
            Box::new(substitute_expr(r, subs)),
        ),
        Expr::FuncCall(name, args) => Expr::FuncCall(
            name.clone(),
            args.iter().map(|a| substitute_expr(a, subs)).collect(),
        ),
        Expr::ArrayAccess(name, idx) => {
            // Array name could be a param — check subs
            // But typically arrays aren't params in helpers
            Expr::ArrayAccess(name.clone(), Box::new(substitute_expr(idx, subs)))
        }
        Expr::Ternary(cond, then_e, else_e) => Expr::Ternary(
            Box::new(substitute_expr(cond, subs)),
            Box::new(substitute_expr(then_e, subs)),
            Box::new(substitute_expr(else_e, subs)),
        ),
        Expr::Cast(vt, inner) => Expr::Cast(vt.clone(), Box::new(substitute_expr(inner, subs))),
        Expr::Not(inner) => Expr::Not(Box::new(substitute_expr(inner, subs))),
        Expr::AddressOf(inner) => Expr::AddressOf(Box::new(substitute_expr(inner, subs))),
        Expr::PostIncrement(inner) => Expr::PostIncrement(Box::new(substitute_expr(inner, subs))),
        Expr::PostDecrement(inner) => Expr::PostDecrement(Box::new(substitute_expr(inner, subs))),
    }
}

/// Try to inline a helper call. Returns Some(inlined_expr) for single-expression
/// helpers, None for multi-statement helpers (handled separately).
pub fn try_inline_expr(helper: &HelperDef, args: &[Expr]) -> Option<Expr> {
    if helper.body.len() == 1 {
        if let Statement::Return { value: Some(ret_expr) } = &helper.body[0] {
            let subs: HashMap<String, Expr> = helper.params.iter()
                .zip(args.iter())
                .map(|(p, a)| (p.name.clone(), a.clone()))
                .collect();
            return Some(substitute_expr(ret_expr, &subs));
        }
    }
    None
}
```

- [ ] **Step 3: Hook inlining into each backend's render_func_call**

Each backend's `render_func_call` needs access to the `HelperRegistry`. Update the function signature to accept `&HelperRegistry`. Before checking math functions or the indicator registry, check the helper registry:

```rust
fn render_func_call(
    fname: &str,
    args: &[Expr],
    single_precision: bool,
    registry: &Registry,
    helpers: &HelperRegistry,
) -> String {
    // Check helper registry first
    if let Some(helper) = helpers.get(fname) {
        if let Some(inlined_expr) = try_inline_expr(helper, args) {
            return render_expr(&inlined_expr, single_precision, registry, helpers);
        }
        // Multi-statement: handled by Task 10
    }
    // ... existing math function / indicator registry logic
}
```

This requires threading `HelperRegistry` through the render call chain. The cascading signature changes are:
- `generate(func, enums, registry)` → `generate(func, enums, registry, helpers)`
- `render_func_call(fname, args, ..., registry)` → add `helpers: &HelperRegistry`
- `render_expr(expr, ..., registry)` → add `helpers: &HelperRegistry`
- `render_statement(stmt, ..., registry)` → add `helpers: &HelperRegistry`
- And all callers of these functions in gen_func, gen_lookback, etc.

This is the same pattern in all three backends (c.rs, rust_lang.rs, java.rs). The `generate()` function signature in each backend must match.

- [ ] **Step 4: Update main.rs to load and pass HelperRegistry**

In `tools/ta_codegen/src/main.rs`, in the `generate` command handler:

```rust
let helper_registry = HelperRegistry::from_dir(&base_dir);
// Pass to backend generate() calls
```

- [ ] **Step 5: Write test for helper-calls-helper (recursive inlining)**

```rust
#[test]
fn inlining_handles_helper_calling_helper() {
    // ta_candleaverage calls ta_candlerange in its body.
    // When ta_candleaverage is inlined, the nested ta_candlerange call
    // should ALSO be inlined (because render_func_call checks the helper
    // registry for every FuncCall it encounters).
    // Generate C for an indicator calling ta_candleaverage.
    // Assert output contains NO "ta_candleaverage(" or "ta_candlerange(" calls.
    // Assert output contains the actual math (fabs, switch, etc.)
}
```

This works naturally because the backend's `render_expr` calls `render_func_call` for every `FuncCall` node, and `render_func_call` always checks the helper registry. When the inlined body of `ta_candleaverage` contains a `FuncCall("ta_candlerange", ...)`, the renderer will inline that too.

- [ ] **Step 6: Run tests**

```bash
cd tools/ta_codegen && cargo test
```

- [ ] **Step 7: Commit**

```bash
git add tools/ta_codegen/src/
git commit -m "feat(codegen): implement expression inlining for single-expression helpers"
```

---

### Task 10: Implement block inlining for multi-statement helpers

**Files:**
- Modify: `tools/ta_codegen/src/helper_registry.rs` (or `inliner.rs`)
- Modify: `tools/ta_codegen/src/backends/c.rs`
- Modify: `tools/ta_codegen/src/backends/rust_lang.rs`
- Modify: `tools/ta_codegen/src/backends/java.rs`

**Context:** Multi-statement helpers (ta_candlerange with switch, ta_true_range with conditionals) can't be inlined as a simple expression. They need to emit a block with a temporary variable. The inliner maintains a monotonic counter to avoid name collisions when the same helper is inlined multiple times.

- [ ] **Step 1: Write failing test**

```rust
#[test]
fn c_backend_inlines_multi_statement_helper_with_temp_var() {
    // Setup: create HelperRegistry with ta_true_range (multi-statement)
    // Create indicator that calls: x = ta_true_range(high, low, prev);
    // Generate C code
    // Assert output contains a temp variable pattern and block scope
    // Assert no "ta_true_range(" function call in output
}

#[test]
fn inlining_counter_avoids_name_collisions() {
    // Setup: indicator that calls ta_candlerange twice
    // Generate C code
    // Assert temp vars have different suffixes (_0, _1)
}
```

- [ ] **Step 2: Implement block inlining**

Extend the inliner to handle multi-statement helpers:

```rust
/// Inline a multi-statement helper as a block with a temp variable.
/// Returns the rendered block as a string and the temp variable name.
pub fn inline_block(
    helper: &HelperDef,
    args: &[Expr],
    counter: &mut usize,
) -> (Vec<Statement>, String) {
    let suffix = *counter;
    *counter += 1;

    let subs: HashMap<String, Expr> = helper.params.iter()
        .zip(args.iter())
        .map(|(p, a)| (p.name.clone(), a.clone()))
        .collect();

    let temp_name = format!("_{}_{}", helper.name.trim_start_matches("ta_"), suffix);

    // Clone and substitute the body, renaming all local variables with suffix
    let mut body = Vec::new();
    for stmt in &helper.body {
        body.push(substitute_statement(stmt, &subs, suffix));
    }

    // Replace any Return statements with assignments to temp_name
    replace_returns_with_assign(&mut body, &temp_name);

    (body, temp_name)
}

/// Recursively substitute params and rename locals in a statement.
/// Must handle ALL 12 Statement variants (VarDecl, Assign, While, DoWhile,
/// For, If, Return, Break, Continue, Switch, ForC, Block).
/// For VarDecl: rename the variable name by appending `_suffix`, substitute in init expr.
/// For Assign/While/If/etc.: substitute in all contained Expr and recurse into child Vec<Statement>.
fn substitute_statement(
    stmt: &Statement, subs: &HashMap<String, Expr>, suffix: usize
) -> Statement { /* recurse all variants */ }

/// Walk the body and replace `Return { value: Some(expr) }` with
/// `Assign { target: Var(temp_name), value: expr }`.
fn replace_returns_with_assign(body: &mut Vec<Statement>, temp_name: &str) { /* ... */ }
```

- [ ] **Step 3: Hook block inlining into backends**

When `try_inline_expr` returns `None` (multi-statement), the backend should:
1. Declare a temp variable of the helper's return type
2. Emit a `{ ... }` block with the substituted body
3. Use the temp variable in place of the original function call

Each backend handles the block scope and temp var in its own syntax.

- [ ] **Step 4: Run tests**

```bash
cd tools/ta_codegen && cargo test
```

- [ ] **Step 5: Commit**

```bash
git add tools/ta_codegen/src/
git commit -m "feat(codegen): implement block inlining for multi-statement helpers with collision avoidance"
```

---

## Chunk 4: Candle Settings + Integration

### Task 11: Add candle settings unpacking to codegen

**Files:**
- Modify: `tools/ta_codegen/src/backends/c.rs`
- Modify: `tools/ta_codegen/src/backends/rust_lang.rs`
- Modify: `tools/ta_codegen/src/backends/java.rs`

**Context:** After call site transformation, candlestick indicators reference variables like `BodyLong_rangeType`, `BodyLong_avgPeriod`, `BodyLong_factor`. The codegen must emit unpacking lines at the top of each function that uses them. Detection is based on scanning the function body IR for these variable names.

- [ ] **Step 1: Write failing test**

```rust
#[test]
fn c_backend_emits_candle_settings_unpacking() {
    // Create a FuncDef with body referencing BodyLong_rangeType and BodyLong_avgPeriod
    // Generate C code
    // Assert the output contains:
    //   int BodyLong_rangeType = TA_Globals->candleSettings[TA_BodyLong].rangeType;
    //   int BodyLong_avgPeriod = TA_Globals->candleSettings[TA_BodyLong].avgPeriod;
    //   double BodyLong_factor = TA_Globals->candleSettings[TA_BodyLong].factor;
}

#[test]
fn rust_backend_emits_candle_settings_from_core() {
    // Same but for Rust — should reference self.candle_settings.body_long.range_type
}
```

- [ ] **Step 2: Implement candle settings detection**

Write a function that scans a `Vec<Statement>` for variable references matching the pattern `{SetName}_{property}` where SetName is one of the known candle setting names (BodyLong, BodyShort, BodyDoji, ShadowLong, ShadowShort, ShadowVeryShort, Near, Far, Equal) and property is `rangeType`, `avgPeriod`, or `factor`.

```rust
/// Known candle setting names
const CANDLE_SETTINGS: &[&str] = &[
    "BodyLong", "BodyVeryLong", "BodyShort", "BodyDoji",
    "ShadowLong", "ShadowVeryLong", "ShadowShort", "ShadowVeryShort",
    "Near", "Far", "Equal",
];

/// Scan statements for candle setting variable references.
/// Returns set of setting names used (e.g., {"BodyLong", "ShadowShort"}).
fn detect_candle_settings(body: &[Statement]) -> HashSet<String> { ... }
```

- [ ] **Step 3: Emit unpacking per backend**

In each backend's function generation, after the existing variable declarations and before the main logic:

**C backend:**
```c
int BodyLong_rangeType = TA_Globals->candleSettings[TA_BodyLong].rangeType;
int BodyLong_avgPeriod = TA_Globals->candleSettings[TA_BodyLong].avgPeriod;
double BodyLong_factor = TA_Globals->candleSettings[TA_BodyLong].factor;
```

**Rust backend:**
```rust
let body_long_range_type: i32 = self.candle_settings.body_long.range_type;
let body_long_avg_period: i32 = self.candle_settings.body_long.avg_period;
let body_long_factor: f64 = self.candle_settings.body_long.factor;
```

**Rust candle settings mapping mechanism:** The unpacking lines are emitted *before* the indicator body is rendered. They declare local variables matching the C names (`BodyLong_rangeType`, etc.) initialized from the Core struct's snake_case fields. The indicator body then uses these local variables — no special mapping needed in `render_expr` because the variable names match what the parser produces. The conversion happens once in the unpacking, not per-reference.

Concretely, for each detected setting (e.g., "BodyLong"), emit:
```rust
let BodyLong_rangeType: i32 = self.candle_settings.body_long.range_type;
let BodyLong_avgPeriod: i32 = self.candle_settings.body_long.avg_period;
let BodyLong_factor: f64 = self.candle_settings.body_long.factor;
```
The `#[allow(non_snake_case)]` attribute handles the naming. A hardcoded map converts setting names: `"BodyLong"` → `"body_long"`, `"ShadowShort"` → `"shadow_short"`, etc.

**Java backend:**
```java
int BodyLong_rangeType = this.candleSettings.bodyLong.rangeType;
int BodyLong_avgPeriod = this.candleSettings.bodyLong.avgPeriod;
double BodyLong_factor = this.candleSettings.bodyLong.factor;
```

- [ ] **Step 4: Handle lookback functions**

Lookback functions also reference `BodyLong_avgPeriod`. Apply the same detection + unpacking logic to lookback body generation. For C, it accesses globals. For Rust/Java, it accesses the Core instance (lookback is a method on Core).

- [ ] **Step 5: Run tests**

```bash
cd tools/ta_codegen && cargo test
```

- [ ] **Step 6: Commit**

```bash
git add tools/ta_codegen/src/backends/
git commit -m "feat(codegen): emit candle settings unpacking per backend (C globals, Rust/Java Core)"
```

---

### Task 12: Generate and verify all indicators parse

**Files:** None (verification only)

- [ ] **Step 1: Run the parser on all indicators**

```bash
cd tools/ta_codegen && cargo test
```

Check that the auto-discovery tests in `backend_suite.rs` still pass — these parse all `ta_func_defs/` indicators and generate all backends.

- [ ] **Step 2: Check for parser failures**

If any indicators fail to parse after the call site transformation, debug and fix. Common issues:
- New function call patterns the parser doesn't handle
- Multi-line expressions from the replacement

- [ ] **Step 3: Commit any parser fixes**

```bash
git add tools/ta_codegen/src/parser/
git commit -m "fix(codegen): parser fixes for transformed candlestick indicator code"
```

---

## Chunk 5: Full Build + Regression Tests

### Task 13: Generate all backend code

**Files:** Generated output in `ta_codegen_output/`

- [ ] **Step 1: Run generate**

```bash
cd tools/ta_codegen && cargo run -- generate
```
Expected: Generates C, Rust, Java files for all ~158 indicators.

- [ ] **Step 2: Check for generation errors**

Look for panics, missing helpers, unresolved function calls. Fix any issues.

- [ ] **Step 3: Run generate-servers**

```bash
cd tools/ta_codegen && cargo run -- generate-servers
```
Expected: Generates JSON-RPC servers for C, Java.

- [ ] **Step 4: Commit generated output**

```bash
git add ta_codegen_output/
git commit -m "chore: regenerate all backend code with helper inlining"
```

---

### Task 14: Build and compile all servers

**Files:** Compiled output in `bin/`

- [ ] **Step 1: Build**

```bash
cd tools/ta_codegen && cargo run -- build
```
Expected: Compiles C and Java servers.

- [ ] **Step 2: Fix compilation errors**

Likely issues:
- Missing candle setting struct definitions in C server
- Type mismatches from inlining
- Missing forward declarations

Fix each error, regenerate, rebuild until clean.

- [ ] **Step 3: Commit fixes**

```bash
git add tools/ta_codegen/src/ ta_codegen_output/
git commit -m "fix(codegen): compilation fixes for helper inlining across backends"
```

---

### Task 15: Run regression tests

- [ ] **Step 1: Build ta_regtest**

```bash
cd cmake-build && cmake .. -DCMAKE_BUILD_TYPE=Release && make ta_regtest -j4
```

- [ ] **Step 2: Run C reference tests**

```bash
cd bin && ./ta_regtest
```
Expected: All C reference tests pass.

- [ ] **Step 3: Run codegen verification**

```bash
cd bin && ./ta_regtest --codegen
```
Expected: All generated servers match C reference output.

- [ ] **Step 4: Fix any failing tests, iterate until green**

- [ ] **Step 5: Final commit**

```bash
git add -A
git commit -m "feat(codegen): helper architecture complete — all indicators compiling and passing regression tests"
```
