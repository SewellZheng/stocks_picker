use crate::ir::FuncDef;
use std::path::Path;

/// Generate `ta_func_list.txt` from the given function definitions.
///
/// Sorts alphabetically by name, formats as `{name:<20}{hint}\n`,
/// and writes only if content has changed (preserving the file timestamp).
pub fn generate(funcs: &[FuncDef], out_path: &Path) {
    let mut entries: Vec<(&str, &str)> = funcs
        .iter()
        .map(|f| (f.name.as_str(), f.hint.as_deref().unwrap_or("")))
        .collect();
    entries.sort_by(|a, b| a.0.cmp(b.0));

    let mut content = String::new();
    for (name, hint) in &entries {
        content.push_str(&format!("{name:<20}{hint}\n"));
    }

    super::write_if_changed(out_path, &content, "ta_func_list.txt", entries.len());
}
