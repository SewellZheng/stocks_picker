use std::collections::HashMap;
use std::path::Path;

use serde::Deserialize;

use crate::ir::{EnumDef, EnumVariant};

#[derive(Deserialize)]
struct YamlVariant {
    c_name: String,
    pascal_name: String,
    value: i32,
}

/// Load enum definitions from `enums.yaml`.
///
/// Returns a map from enum name (e.g. `"MAType"`) to its definition.
pub fn load_enums(path: &Path) -> HashMap<String, EnumDef> {
    let content = std::fs::read_to_string(path)
        .unwrap_or_else(|e| panic!("Failed to read {}: {}", path.display(), e));

    let raw: HashMap<String, Vec<YamlVariant>> = serde_yaml::from_str(&content)
        .unwrap_or_else(|e| panic!("Failed to parse {}: {}", path.display(), e));

    raw.into_iter()
        .map(|(name, variants)| {
            let prefix = format!("TA_{name}_");
            let variants = variants
                .into_iter()
                .map(|v| {
                    let short_name = v
                        .c_name
                        .strip_prefix(&prefix)
                        .unwrap_or(&v.c_name)
                        .to_string();
                    EnumVariant {
                        c_name: v.c_name,
                        pascal_name: v.pascal_name,
                        short_name,
                        value: v.value,
                    }
                })
                .collect();
            (name.clone(), EnumDef { name, variants })
        })
        .collect()
}

/// Look up an enum variant by its case label (e.g. `"MAType_SMA"`).
///
/// Returns `(enum_name, &EnumVariant)` if found, or `None` if the label
/// doesn't match any known enum variant.
#[allow(clippy::implicit_hasher)]
pub fn lookup_variant<'a>(
    label: &str,
    enums: &'a HashMap<String, EnumDef>,
) -> Option<(&'a str, &'a EnumVariant)> {
    // Labels have the format `EnumName_VARIANT`, e.g. `MAType_SMA`
    for (enum_name, enum_def) in enums {
        let prefix = format!("{enum_name}_");
        if label.starts_with(&prefix) {
            let short = &label[prefix.len()..];
            if let Some(variant) = enum_def.variants.iter().find(|v| v.short_name == short) {
                return Some((enum_name, variant));
            }
        }
    }
    None
}
