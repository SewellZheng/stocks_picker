use crate::ir::FuncDef;
use std::collections::HashSet;
use std::path::Path;

const BEGIN_MARKER: &str = "# [ta_codegen begin LIB_SOURCES]";
const END_MARKER: &str = "# [ta_codegen end LIB_SOURCES]";

/// Generate the `set(LIB_SOURCES ...)` section of `CMakeLists.txt`.
///
/// Uses begin/end markers to identify and replace only the generated section,
/// leaving the rest of CMakeLists.txt unchanged.
///
/// Sources included (sorted alphabetically):
/// - `src/ta_func/ta_utility.c` (hand-written helper, always first)
/// - `src/ta_func/ta_NAME.c` for each function in `ta_func_defs/` (from `funcs`)
/// - Any extra `ta_*.c` files found in `src/ta_func/` not yet in `ta_func_defs/`
/// - `src/ta_abstract/frames/ta_frame.c`
/// - `src/ta_abstract/ta_func_api.c`
/// - `src/ta_abstract/ta_group_idx.c`
pub fn generate(funcs: &[FuncDef], cmake_path: &Path, root: &Path) {
    let mut names: Vec<String> = funcs.iter().map(|f| f.name.to_uppercase()).collect();

    // Pick up any ta_*.c files in src/ta_func/ not yet extracted to ta_func_defs/
    // (e.g. NVI, PVI). This makes the generator self-correcting as functions are ported.
    let known: HashSet<String> = names.iter().cloned().collect();
    let src_ta_func = root.join("src/ta_func");
    if let Ok(dir) = std::fs::read_dir(&src_ta_func) {
        let extras: Vec<String> = dir
            .filter_map(Result::ok)
            .filter_map(|e| {
                let fname = e.file_name().to_string_lossy().to_string();
                if let Some(func_lower) = fname
                    .strip_prefix("ta_")
                    .and_then(|s| s.strip_suffix(".c"))
                    .filter(|s| *s != "utility")
                {
                    let func = func_lower.to_uppercase();
                    // Include only files whose function is not already in ta_func_defs/
                    Some(func).filter(|f| !known.contains(f))
                } else {
                    None
                }
            })
            .collect();
        if !extras.is_empty() {
            println!(
                "  CMakeLists.txt: including {} extra src/ta_func file(s) not in ta_func_defs: {}",
                extras.len(),
                extras.join(", ")
            );
        }
        names.extend(extras);
    }

    names.sort();

    // Build the replacement block
    let mut block = String::new();
    block.push_str(BEGIN_MARKER);
    block.push('\n');
    block.push_str("set(LIB_SOURCES\n");

    // ta_utility.c is a hand-written helper — always first
    block.push_str("\t\"${CMAKE_CURRENT_SOURCE_DIR}/src/ta_func/ta_utility.c\"\n");

    for name in &names {
        block.push_str(&format!(
            "\t\"${{CMAKE_CURRENT_SOURCE_DIR}}/src/ta_func/ta_{name}.c\"\n"
        ));
    }

    // Fixed ta_abstract sources that CMake also needs
    block.push_str("\t\"${CMAKE_CURRENT_SOURCE_DIR}/src/ta_abstract/frames/ta_frame.c\"\n");
    block.push_str("\t\"${CMAKE_CURRENT_SOURCE_DIR}/src/ta_abstract/ta_func_api.c\"\n");
    block.push_str("\t\"${CMAKE_CURRENT_SOURCE_DIR}/src/ta_abstract/ta_group_idx.c\"\n");
    block.push(')');
    block.push('\n');
    block.push_str(END_MARKER);

    // Read existing CMakeLists.txt
    let existing = std::fs::read_to_string(cmake_path)
        .unwrap_or_else(|_| panic!("Cannot read {}", cmake_path.display()));

    let begin_pos = existing.find(BEGIN_MARKER).unwrap_or_else(|| {
        panic!(
            "Cannot find '{}' in {}",
            BEGIN_MARKER,
            cmake_path.display()
        )
    });
    let end_pos = existing.find(END_MARKER).unwrap_or_else(|| {
        panic!(
            "Cannot find '{}' in {}",
            END_MARKER,
            cmake_path.display()
        )
    });
    let end_pos = end_pos + END_MARKER.len();

    let new_content = format!(
        "{}{}{}",
        &existing[..begin_pos],
        block,
        &existing[end_pos..]
    );

    if existing == new_content {
        println!(
            "  CMakeLists.txt LIB_SOURCES is up to date ({} functions)",
            names.len()
        );
    } else {
        std::fs::write(cmake_path, &new_content).unwrap();
        println!(
            "  CMakeLists.txt LIB_SOURCES updated ({} functions)",
            names.len()
        );
    }
}
