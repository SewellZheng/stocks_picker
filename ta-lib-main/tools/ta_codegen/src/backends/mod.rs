pub mod c;
pub mod cmake_lists;
pub mod dotnet;
pub mod func_api_xml;
pub mod func_list;
pub mod java;
pub mod makefile_am;
pub mod rust_lang;
pub mod ta_abstract_c;

/// Write `content` to `path` only if it differs from the current file contents.
/// Prints a one-line status message using `label` and `count`.
pub fn write_if_changed(path: &std::path::Path, content: &str, label: &str, count: usize) {
    let existing = std::fs::read_to_string(path).unwrap_or_default();
    if existing == content {
        println!("  {label} is up to date ({count} functions)");
    } else {
        std::fs::write(path, content).unwrap();
        println!("  {label} updated ({count} functions)");
    }
}
