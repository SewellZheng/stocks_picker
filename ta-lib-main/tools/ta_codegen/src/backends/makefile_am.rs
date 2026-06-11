use crate::ir::FuncDef;
use std::path::Path;

/// Generate `src/ta_func/Makefile.am` from function definitions.
///
/// Sorts alphabetically by name, writes only if content has changed.
pub fn generate(funcs: &[FuncDef], out_path: &Path) {
    let mut names: Vec<&str> = funcs.iter().map(|f| f.name.as_str()).collect();
    names.sort_unstable();

    let mut content = String::new();

    // Header (matches the old Makefile.am.template)
    content.push_str(
        "\nnoinst_LTLIBRARIES = libta_func.la\n\
         AM_CPPFLAGS = -I../ta_common/\n\
         \n\
         libta_func_la_SOURCES = ta_utility.c \\\n",
    );

    // Function source entries
    for (i, name) in names.iter().enumerate() {
        if i + 1 < names.len() {
            content.push_str(&format!("\tta_{name}.c \\\n"));
        } else {
            content.push_str(&format!("\tta_{name}.c\n"));
        }
    }

    // Footer
    content.push_str(
        "\nlibta_funcdir=$(includedir)/ta-lib/\n\
         libta_func_HEADERS = ../../include/ta_defs.h \\\n\
         \t../../include/ta_libc.h \\\n\
         \t../../include/ta_func.h\n",
    );

    super::write_if_changed(out_path, &content, "Makefile.am", names.len());
}
