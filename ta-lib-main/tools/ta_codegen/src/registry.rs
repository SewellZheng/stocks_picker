use std::path::Path;

/// Target language for cross-call resolution.
#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub enum Lang {
    C,
    Rust,
    Java,
    DotNet,
}

/// Registry of discovered indicators, used for cross-function call resolution.
///
/// Scans `ta_func_defs/` subdirectories to discover indicator names,
/// then translates prefix-free calls (e.g. `sma_lookback`) to
/// language-specific names (e.g. `TA_SMA_Lookback` for C).
pub struct Registry {
    /// Sorted by descending length so longest-match wins in `parse_func_name`.
    indicators: Vec<String>,
}

impl Registry {
    /// Build a registry by scanning `base_dir` for subdirectories containing `.yaml` files.
    pub fn from_dir(base_dir: &Path) -> Self {
        let mut indicators = Vec::new();

        if let Ok(entries) = std::fs::read_dir(base_dir) {
            for entry in entries.filter_map(std::result::Result::ok) {
                let path = entry.path();
                if !path.is_dir() {
                    continue;
                }
                let dir_name = entry.file_name().to_string_lossy().to_string();
                let yaml_path = path.join(format!("{dir_name}.yaml"));
                if yaml_path.exists() {
                    indicators.push(dir_name);
                }
            }
        }

        // Sort by descending length so longest-match wins (e.g. "stochrsi" before "stoch")
        indicators.sort_by(|a, b| b.len().cmp(&a.len()).then(a.cmp(b)));

        Registry { indicators }
    }

    /// Check if an indicator exists in the registry.
    pub fn contains(&self, name: &str) -> bool {
        self.indicators.iter().any(|n| n == name)
    }

    /// Resolve a prefix-free function call to a language-specific name.
    ///
    /// Given `sma_lookback` or bare `sma`, parses out the indicator name
    /// and function type, then maps to the target language's naming convention.
    ///
    /// Bare indicator names (e.g. `sma`) are cross-indicator logic calls
    /// and resolve to the unguarded/internal variant in each language.
    ///
    /// Returns the original name unchanged if the indicator is not found.
    pub fn resolve_call(&self, func_name: &str, lang: Lang) -> String {
        // Handle _private suffix: maps to the Private variant (double-only in C,
        // generic in Rust). No double/float routing — always the same function.
        if let Some(base) = func_name.strip_suffix("_private") {
            if self.contains(base) {
                return match lang {
                    Lang::Rust => format!("{base}_private"),
                    Lang::C => format!("TA_{}_Private", base.to_uppercase()),
                    Lang::Java => {
                        let camel = self.to_camel_case(base);
                        format!("{camel}Private")
                    }
                    Lang::DotNet => {
                        let pascal = capitalize(base);
                        format!("{pascal}Private")
                    }
                };
            }
        }

        // Bare indicator names resolve to the Unguarded variant.
        // All code in ta_func_defs is internal — cross-indicator calls skip validation.
        if self.contains(func_name) {
            return match lang {
                Lang::Rust => format!("{func_name}_unguarded"),
                Lang::C => format!("TA_{}_Unguarded", func_name.to_uppercase()),
                Lang::Java => {
                    let camel = self.to_camel_case(func_name);
                    format!("{camel}Logic")
                }
                Lang::DotNet => {
                    let pascal = capitalize(func_name);
                    format!("{pascal}Logic")
                }
            };
        }

        let Some((indicator, suffix)) = self.parse_func_name(func_name) else {
            return func_name.to_string();
        };

        match lang {
            Lang::Rust => func_name.to_string(),
            Lang::C => self.to_c_name(&indicator, &suffix),
            Lang::Java => self.to_camel_case(func_name),
            Lang::DotNet => self.to_pascal_case(func_name),
        }
    }

    /// Resolve a prefix-free type reference to a language-specific name.
    ///
    /// Same logic as `resolve_call` — provided as a separate method
    /// so backends can distinguish calls from type references if needed.
    pub fn resolve_type(&self, func_name: &str, lang: Lang) -> String {
        self.resolve_call(func_name, lang)
    }

    /// Parse a prefix-free function name into (indicator, suffix).
    /// e.g. "`sma_lookback`" -> ("sma", "lookback")
    fn parse_func_name(&self, func_name: &str) -> Option<(String, String)> {
        // Try matching known indicators by checking if func_name starts with
        // an indicator name followed by underscore
        for indicator in &self.indicators {
            let prefix = format!("{indicator}_");
            if func_name.starts_with(&prefix) {
                let suffix = func_name[prefix.len()..].to_string();
                return Some((indicator.clone(), suffix));
            }
        }
        None
    }

    /// Convert to C naming: `sma_lookback` -> `TA_SMA_Lookback`
    #[allow(clippy::unused_self)]
    fn to_c_name(&self, indicator: &str, suffix: &str) -> String {
        let upper = indicator.to_uppercase();
        // Capitalize first letter of suffix
        let cap_suffix = capitalize(suffix);
        format!("TA_{upper}_{cap_suffix}")
    }

    /// Convert `snake_case` to camelCase: `sma_lookback` -> `smaLookback`
    #[allow(clippy::unused_self)]
    fn to_camel_case(&self, name: &str) -> String {
        let parts: Vec<&str> = name.split('_').collect();
        let mut result = String::new();
        for (i, part) in parts.iter().enumerate() {
            if i == 0 {
                result.push_str(part);
            } else {
                result.push_str(&capitalize(part));
            }
        }
        result
    }

    /// Convert `snake_case` to `PascalCase`: `sma_lookback` -> `SmaLookback`
    #[allow(clippy::unused_self)]
    fn to_pascal_case(&self, name: &str) -> String {
        name.split('_').map(capitalize).collect()
    }
}

/// Capitalize the first letter of a string.
fn capitalize(s: &str) -> String {
    let mut chars = s.chars();
    match chars.next() {
        None => String::new(),
        Some(c) => c.to_uppercase().collect::<String>() + chars.as_str(),
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_registry_discovers_indicators() {
        let base = Path::new(env!("CARGO_MANIFEST_DIR")).join("../../ta_func_defs");
        let registry = Registry::from_dir(&base);
        assert!(registry.contains("sma"));
        assert!(registry.contains("rsi"));
        assert!(registry.contains("ema"));
    }

    #[test]
    fn test_registry_resolves_cross_calls() {
        let base = Path::new(env!("CARGO_MANIFEST_DIR")).join("../../ta_func_defs");
        let registry = Registry::from_dir(&base);

        // C backend: lookback suffixed calls
        assert_eq!(
            registry.resolve_call("sma_lookback", Lang::C),
            "TA_SMA_Lookback"
        );

        // C backend: bare indicator names resolve to Unguarded (cross-indicator = skip validation)
        assert_eq!(registry.resolve_call("sma", Lang::C), "TA_SMA_Unguarded");
        assert_eq!(registry.resolve_call("ema", Lang::C), "TA_EMA_Unguarded");

        // C backend: _private suffix resolves to Private (double-only)
        assert_eq!(registry.resolve_call("ema_private", Lang::C), "TA_EMA_Private");

        // Rust backend (bare names resolve to _unguarded)
        assert_eq!(registry.resolve_call("ema", Lang::Rust), "ema_unguarded");
        assert_eq!(
            registry.resolve_call("ema_lookback", Lang::Rust),
            "ema_lookback"
        );
        // Rust: _private stays as _private (generic, handles both f32/f64)
        assert_eq!(registry.resolve_call("ema_private", Lang::Rust), "ema_private");

        // Java backend (bare names resolve to Logic)
        assert_eq!(
            registry.resolve_call("ema_lookback", Lang::Java),
            "emaLookback"
        );
        assert_eq!(registry.resolve_call("ema", Lang::Java), "emaLogic");
        assert_eq!(registry.resolve_call("ema_private", Lang::Java), "emaPrivate");

        // .NET backend (bare names resolve to Logic)
        assert_eq!(
            registry.resolve_call("sma_lookback", Lang::DotNet),
            "SmaLookback"
        );
        assert_eq!(registry.resolve_call("sma", Lang::DotNet), "SmaLogic");
        assert_eq!(registry.resolve_call("ema_private", Lang::DotNet), "EmaPrivate");
    }

    #[test]
    fn test_registry_unknown_func_returns_unchanged() {
        let base = Path::new(env!("CARGO_MANIFEST_DIR")).join("../../ta_func_defs");
        let registry = Registry::from_dir(&base);
        assert_eq!(
            registry.resolve_call("unknown_func", Lang::C),
            "unknown_func"
        );
    }

    #[test]
    fn test_registry_resolve_type_mirrors_resolve_call() {
        let base = Path::new(env!("CARGO_MANIFEST_DIR")).join("../../ta_func_defs");
        let registry = Registry::from_dir(&base);
        assert_eq!(
            registry.resolve_type("sma_lookback", Lang::C),
            registry.resolve_call("sma_lookback", Lang::C)
        );
    }

    #[test]
    fn test_registry_bare_name_resolves_to_unguarded() {
        let base = Path::new(env!("CARGO_MANIFEST_DIR")).join("../../ta_func_defs");
        let registry = Registry::from_dir(&base);
        // Bare indicator names resolve to unguarded (cross-indicator calls skip validation)
        assert_eq!(registry.resolve_call("sma", Lang::C), "TA_SMA_Unguarded");
        // _private resolves to Private
        assert_eq!(registry.resolve_call("sma_private", Lang::C), "TA_SMA_Private");
        // For Rust, bare names resolve to _unguarded
        assert_eq!(registry.resolve_call("sma", Lang::Rust), "sma_unguarded");
    }

    #[test]
    fn test_registry_does_not_include_non_dirs() {
        let base = Path::new(env!("CARGO_MANIFEST_DIR")).join("../../ta_func_defs");
        let registry = Registry::from_dir(&base);
        // enums.yaml is a file at the top level, not a directory
        assert!(!registry.contains("enums"));
    }
}
