use regex::Regex;

/// Extract prefix-free C source from an original TA-Lib source file.
/// Returns the extracted C source with lookback and logic functions.
pub fn extract_function_source(source: &str, name: &str) -> String {
    let upper = name.to_uppercase();
    let lower = name.to_lowercase();

    let lines: Vec<&str> = source.lines().collect();

    let lookback = extract_lookback(&lines, &upper, &lower);
    let logic = extract_logic(&lines, &upper, &lower);

    let mut result = lookback;
    result.push('\n');
    result.push_str(&logic);
    result
}

/// Find the index of a line matching a pattern in the lines slice.
fn find_line(lines: &[&str], pattern: &str) -> Option<usize> {
    lines.iter().position(|l| l.contains(pattern))
}

/// Extract the lookback function body from between END SECTION 1 and START SECTION 3.
fn extract_lookback(lines: &[&str], upper: &str, lower: &str) -> String {
    let end_sec1 = find_line(lines, "END GENCODE SECTION 1").expect("missing END SECTION 1");
    let start_sec3 = find_line(lines, "START GENCODE SECTION 3").expect("missing START SECTION 3");

    // Parse lookback signature from section 1 to get parameter list.
    // Look for the C variant: `TA_LIB_API int TA_<NAME>_Lookback(...)`
    let start_sec1 = find_line(lines, "START GENCODE SECTION 1").expect("missing START SECTION 1");
    let lookback_params = extract_lookback_params(lines, start_sec1, end_sec1, upper);

    // Body is between `{` after END SECTION 1 and `}` before START SECTION 3.
    // Find the opening `{` after end_sec1.
    let mut body_start = end_sec1 + 1;
    while body_start < start_sec3 {
        if lines[body_start].trim() == "{" {
            body_start += 1;
            break;
        }
        body_start += 1;
    }

    // Find the closing `}` before start_sec3. Scan backwards from start_sec3.
    let mut body_end = start_sec3 - 1;
    while body_end > body_start {
        if lines[body_end].trim() == "}" {
            break;
        }
        body_end -= 1;
    }

    // Collect body lines, skipping GENCODE section 2 and /* Generated */ lines.
    let mut body_lines: Vec<String> = Vec::new();
    let mut in_gencode_2 = false;
    for line in &lines[body_start..body_end] {
        if line.contains("START GENCODE SECTION 2") {
            in_gencode_2 = true;
            continue;
        }
        if line.contains("END GENCODE SECTION 2") {
            in_gencode_2 = false;
            continue;
        }
        if in_gencode_2 {
            continue;
        }
        if line.trim_start().starts_with("/* Generated */") {
            continue;
        }
        // Skip comment-only lines like "/* insert local variable here */" and
        // "/* insert lookback code here. */"
        let trimmed = line.trim();
        if trimmed.starts_with("/* insert ") && trimmed.ends_with("*/") {
            continue;
        }
        body_lines.push(line.to_string());
    }

    // Resolve preprocessor conditionals for C double-precision target
    body_lines = strip_preprocessor_conditionals(&body_lines);

    // Trim leading/trailing blank lines from body
    while body_lines.first().is_some_and(|l| l.trim().is_empty()) {
        body_lines.remove(0);
    }
    while body_lines.last().is_some_and(|l| l.trim().is_empty()) {
        body_lines.pop();
    }

    // Apply macro expansions to body lines
    let body_lines: Vec<String> = body_lines
        .iter()
        .map(|l| expand_macros(l, upper, lower))
        .collect();

    // Build the lookback function
    let mut result = format!("int {lower}_lookback({lookback_params})\n{{\n");
    for line in &body_lines {
        result.push_str(&format!("    {}\n", line.trim()));
    }
    result.push_str("}\n");
    result
}

/// Extract lookback function parameters from Section 1.
/// Looks for `TA_LIB_API int TA_<NAME>_Lookback( ... )` in the `#else` C path.
fn extract_lookback_params(lines: &[&str], start: usize, end: usize, upper: &str) -> String {
    let target = format!("TA_{upper}_Lookback");
    for i in start..end {
        let line = strip_generated_prefix(lines[i]);
        if line.contains(&target) {
            // Extract the parameter list from parentheses
            let combined = collect_until_paren_close(lines, i, end);
            if let Some(params) = extract_parens(&combined) {
                let params = params.trim();
                // Strip inline comments like /* From 2 to 100000 */
                let params = strip_inline_comments(params);
                let params = params.trim();
                if params == "void" || params.is_empty() {
                    return "void".to_string();
                }
                return params.to_string();
            }
        }
    }
    "void".to_string()
}

/// Collect lines starting from `start` until we see a balanced closing `)`, joining them.
fn collect_until_paren_close(lines: &[&str], start: usize, end: usize) -> String {
    let mut result = String::new();
    let mut depth = 0i32;
    let mut found_open = false;
    for line in &lines[start..end] {
        let stripped = strip_generated_prefix(line);
        for ch in stripped.chars() {
            if ch == '(' {
                found_open = true;
                depth += 1;
            }
            if ch == ')' {
                depth -= 1;
            }
        }
        result.push_str(&stripped);
        result.push(' ');
        if found_open && depth <= 0 {
            break;
        }
    }
    result
}

/// Extract content between first `(` and last `)`.
fn extract_parens(s: &str) -> Option<String> {
    let open = s.find('(')?;
    let close = s.rfind(')')?;
    if close > open {
        Some(s[open + 1..close].to_string())
    } else {
        None
    }
}

/// Strip `/* Generated */` prefix from a line.
fn strip_generated_prefix(line: &str) -> String {
    let trimmed = line.trim_start();
    if let Some(rest) = trimmed.strip_prefix("/* Generated */") {
        rest.to_string()
    } else {
        line.to_string()
    }
}

/// Strip inline C comments like /* From 2 to 100000 */
fn strip_inline_comments(s: &str) -> String {
    let re = Regex::new(r"/\*.*?\*/").unwrap();
    re.replace_all(s, "").to_string().trim().to_string()
}

/// Extract the logic function (main body / `TA_INT` function).
fn extract_logic(lines: &[&str], upper: &str, lower: &str) -> String {
    let end_sec3 = find_line(lines, "END GENCODE SECTION 3").expect("missing END SECTION 3");
    let start_sec5 = find_line(lines, "START GENCODE SECTION 5").expect("missing START SECTION 5");

    // Parse the main function signature from section 3 to get parameters.
    let start_sec3 = find_line(lines, "START GENCODE SECTION 3").expect("missing START SECTION 3");
    let main_sig_params = extract_main_func_params(lines, start_sec3, end_sec3, upper);

    // Find the opening `{` after END SECTION 3 (start of main function body).
    let mut main_body_start = end_sec3 + 1;
    while main_body_start < start_sec5 {
        if lines[main_body_start].trim() == "{" {
            main_body_start += 1;
            break;
        }
        main_body_start += 1;
    }

    // Find the closing `}` of the main function body.
    // We need to count braces to find the matching `}`.
    let main_body_end = find_matching_brace(lines, main_body_start - 1, start_sec5);

    // Check if the main function delegates to TA_INT.
    let delegates_to_int = check_delegates_to_int(lines, main_body_start, main_body_end, upper);

    if delegates_to_int {
        // The real logic is in the TA_INT function. Extract it.
        let int_func = extract_int_function(lines, main_body_end, start_sec5, upper, lower);
        if let Some((int_params, int_body)) = int_func {
            // Use the TA_INT function's params (may have extra like optInK_1).
            let mut result = format!("TA_RetCode {lower}({int_params})\n{{\n");
            result.push_str(&int_body);
            result.push_str("}\n");
            return result;
        }
    }

    // Inline logic: extract from main function body (after GENCODE SECTION 4).
    let body = extract_main_body(lines, main_body_start, main_body_end, upper, lower);
    let mut result = format!("TA_RetCode {lower}({main_sig_params})\n{{\n");
    result.push_str(&body);
    result.push_str("}\n");
    result
}

/// Extract main function parameters from Section 3 (C variant).
fn extract_main_func_params(lines: &[&str], start: usize, end: usize, upper: &str) -> String {
    let target = format!("TA_LIB_API TA_RetCode TA_{upper}");
    // Also check for just `TA_RetCode TA_{upper}` without TA_LIB_API for the TA_INT variant
    for i in start..end {
        let line = strip_generated_prefix(lines[i]);
        if line.contains(&target)
            || line.contains(&format!("TA_RetCode TA_{upper}("))
            || line.contains(&format!("TA_RetCode TA_{upper} ("))
        {
            let combined = collect_until_paren_close(lines, i, end);
            if let Some(params) = extract_parens(&combined) {
                return clean_params(&params, upper);
            }
        }
    }
    String::new()
}

/// Clean parameter list: strip comments, normalize whitespace.
fn clean_params(params: &str, _upper: &str) -> String {
    let params = strip_inline_comments(params);
    // Normalize whitespace
    let re = Regex::new(r"\s+").unwrap();
    let params = re.replace_all(&params, " ").trim().to_string();
    // Replace INPUT_TYPE with double
    params.replace("INPUT_TYPE", "double")
}

/// Find the matching closing `}` for an opening `{` at `open_brace_line`.
fn find_matching_brace(lines: &[&str], open_brace_line: usize, limit: usize) -> usize {
    let mut depth = 0i32;
    for (i, line) in lines.iter().enumerate().take(limit).skip(open_brace_line) {
        for ch in line.chars() {
            if ch == '{' {
                depth += 1;
            }
            if ch == '}' {
                depth -= 1;
                if depth == 0 {
                    return i;
                }
            }
        }
    }
    limit
}

/// Check if the main function body just delegates to `TA_INT`_<NAME> via `FUNCTION_CALL`.
fn check_delegates_to_int(lines: &[&str], start: usize, end: usize, upper: &str) -> bool {
    let pattern1 = format!("FUNCTION_CALL(INT_{upper})");
    let pattern2 = format!("TA_INT_{upper}");
    for line in &lines[start..end] {
        // Skip GENCODE sections
        if line.contains("GENCODE SECTION") {
            continue;
        }
        if line.trim_start().starts_with("/* Generated */") {
            continue;
        }
        if line.contains(&pattern1) || line.contains(&pattern2) {
            return true;
        }
    }
    false
}

/// Extract the `TA_INT`_<NAME> function (signature + body).
fn extract_int_function(
    lines: &[&str],
    after_main_end: usize,
    before_sec5: usize,
    upper: &str,
    lower: &str,
) -> Option<(String, String)> {
    // Look for the C variant of TA_INT_<NAME> signature.
    // It's between `#else` and `#endif` in a block after the main function.
    // The C signature looks like:
    //   TA_RetCode TA_PREFIX(INT_<NAME>)( ... )
    // or possibly:
    //   TA_RetCode TA_INT_<NAME>( ... )

    let prefix_pattern = format!("TA_PREFIX(INT_{upper})");
    let direct_pattern = format!("TA_INT_{upper}");

    // Find the C path of the TA_INT function signature.
    // The structure has nested #if/#else blocks:
    //   #if defined(_MANAGED) && defined(USE_SUBARRAY) && defined(USE_SINGLE_PRECISION_INPUT)
    //     // No INT function
    //   #else
    //     #if defined(_MANAGED) && defined(USE_SUBARRAY)
    //       managed subarray signature with TA_INT_<NAME>
    //     #elif defined(_MANAGED)
    //       managed signature with TA_INT_<NAME>
    //     #elif defined(_JAVA)
    //       java signature
    //     #elif defined(_RUST)
    //       rust signature
    //     #else                    <-- this is the C path
    //       TA_PREFIX(INT_<NAME>)  <-- we want THIS one
    //     #endif
    //   #endif
    //
    // Strategy: collect ALL lines that match the signature pattern,
    // and take the LAST one (which will be the C #else path).
    let mut sig_line = None;

    for (i, line) in lines
        .iter()
        .enumerate()
        .take(before_sec5)
        .skip(after_main_end + 1)
    {
        let stripped = strip_generated_prefix(line);
        let trimmed = stripped.trim();

        if trimmed.contains(&prefix_pattern) || trimmed.contains(&direct_pattern) {
            sig_line = Some(i); // Keep updating — last match wins (C path)
        }
    }

    let sig_line = sig_line?;

    // Collect the full signature (may span multiple lines until `)`).
    let combined = collect_until_paren_close(lines, sig_line, before_sec5);
    // Replace TA_PREFIX(INT_<NAME>) with a placeholder to avoid confusing extract_parens
    // (it would match the macro parens instead of the parameter list parens).
    let combined = combined
        .replace(
            &format!("TA_PREFIX(INT_{upper})"),
            &format!("TA_INT_{upper}"),
        )
        .replace(
            &format!("TA_PREFIX( INT_{upper} )"),
            &format!("TA_INT_{upper}"),
        );
    let params_raw = extract_parens(&combined)?;
    let params = clean_params(&params_raw, upper);

    // Find the opening `{` after the signature (and after #endif).
    let mut body_open = sig_line;
    while body_open < before_sec5 {
        if lines[body_open].trim() == "{" || strip_generated_prefix(lines[body_open]).trim() == "{"
        {
            break;
        }
        body_open += 1;
    }

    // Find the matching closing `}`.
    let body_close = find_matching_brace(lines, body_open, before_sec5);

    // Extract body lines.
    let mut body_lines: Vec<String> = Vec::new();
    for line in &lines[(body_open + 1)..body_close] {
        let stripped = strip_generated_prefix(line);
        body_lines.push(stripped);
    }

    // Resolve all preprocessor conditionals for C double-precision target
    body_lines = strip_preprocessor_conditionals(&body_lines);

    // Apply macro expansions
    let body_lines: Vec<String> = body_lines
        .iter()
        .map(|l| expand_macros(l, upper, lower))
        .collect();

    // Trim leading/trailing blank lines
    let mut body_lines = body_lines;
    while body_lines.first().is_some_and(|l| l.trim().is_empty()) {
        body_lines.remove(0);
    }
    while body_lines.last().is_some_and(|l| l.trim().is_empty()) {
        body_lines.pop();
    }

    let mut body = String::new();
    for line in &body_lines {
        if line.trim().is_empty() {
            body.push('\n');
        } else {
            body.push_str(&format!("    {}\n", line.trim()));
        }
    }

    Some((params, body))
}

/// Strip all `#if`/`#else`/`#endif` preprocessor blocks, resolving for C double-precision target.
/// Symbols _JAVA, _MANAGED, `USE_SUBARRAY`, `USE_SINGLE_PRECISION_INPUT` are all undefined for C.
/// `#define` and `#undef` lines are preserved (they're local macros the backends handle).
#[allow(clippy::similar_names)]
fn strip_preprocessor_conditionals(lines: &[String]) -> Vec<String> {
    let mut result = Vec::new();
    // Stack tracks: (keeping_content, depth)
    // keeping_content = true means we're in a branch whose code should be emitted
    let mut stack: Vec<bool> = Vec::new(); // true = keeping, false = skipping

    for line in lines {
        let trimmed = line.trim();

        let is_if = trimmed.starts_with("#if ") || trimmed.starts_with("#if\t");
        let is_ifdef = trimmed.starts_with("#ifdef ");
        let is_ifndef = trimmed.starts_with("#ifndef ");
        let is_else = trimmed == "#else";
        let is_endif = trimmed == "#endif"
            || trimmed.starts_with("#endif ")
            || trimmed.starts_with("#endif/")
            || trimmed.starts_with("#endif\t");

        if is_if || is_ifdef || is_ifndef {
            let condition_true = eval_condition_for_c(trimmed);
            stack.push(condition_true);
            continue;
        }

        if is_else {
            if let Some(top) = stack.last_mut() {
                *top = !*top; // flip: if we were keeping, now skip; vice versa
            }
            continue;
        }

        if is_endif {
            stack.pop();
            continue;
        }

        // Emit line only if all enclosing conditions are true (or no conditions)
        if stack.iter().all(|&keeping| keeping) {
            result.push(line.clone());
        }
    }
    result
}

/// Evaluate a `#if` condition for C double-precision target.
/// All of _JAVA, _MANAGED, `USE_SUBARRAY`, `USE_SINGLE_PRECISION_INPUT`, _RUST are undefined.
fn eval_condition_for_c(directive: &str) -> bool {
    let trimmed = directive.trim();

    // #ifndef X → true if X is undefined (it is for our target symbols)
    if trimmed.starts_with("#ifndef ") {
        return true;
    }
    // #ifdef X → false for our target symbols
    if trimmed.starts_with("#ifdef ") {
        return false;
    }

    // #if ... — extract the condition after "#if "
    let Some(cond) = trimmed.strip_prefix("#if ") else {
        return true; // unknown directive, keep content
    };

    // Check for positive defined() terms — if any target symbol is positively tested, condition is false
    let positive_defines = [
        "_JAVA",
        "_MANAGED",
        "USE_SUBARRAY",
        "USE_SINGLE_PRECISION_INPUT",
        "_RUST",
    ];
    let negative_defines = [
        "_JAVA",
        "_MANAGED",
        "USE_SUBARRAY",
        "USE_SINGLE_PRECISION_INPUT",
        "_RUST",
    ];

    // Simple heuristic: if the condition has `defined(X)` (without `!`) for any target symbol → false
    // If the condition has `!defined(X)` for target symbols → true
    // For compound conditions with &&: all terms must be true
    // For compound conditions with ||: any term must be true

    // Check if it's purely negated conditions (all !defined)
    let has_positive = positive_defines.iter().any(|sym| {
        // Match `defined(SYM)` or `defined( SYM )` NOT preceded by `!`
        let patterns = [
            format!("defined({sym})"),
            format!("defined( {sym} )"),
            format!("defined({sym})"),
        ];
        for pat in &patterns {
            if let Some(pos) = cond.find(pat.as_str()) {
                // Check if preceded by `!`
                let before = &cond[..pos];
                let before_trimmed = before.trim_end();
                if !before_trimmed.ends_with('!') {
                    return true; // positive defined() → this symbol being tested as present
                }
            }
        }
        false
    });

    let has_negative = negative_defines.iter().any(|sym| {
        let patterns = [
            format!("!defined({sym})"),
            format!("!defined( {sym} )"),
            format!("! defined({sym})"),
            format!("! defined( {sym} )"),
        ];
        patterns.iter().any(|pat| cond.contains(pat.as_str()))
    });

    if has_positive && !has_negative {
        // Pure positive: `defined(USE_SINGLE_PRECISION_INPUT)` → false
        false
    } else if has_negative && !has_positive {
        // Pure negative: `!defined(_JAVA)` → true
        true
    } else if has_positive && has_negative {
        // Mixed: e.g. `defined(USE_SINGLE_PRECISION_INPUT) || !defined(_JAVA)`
        // For &&: positive term is false → whole thing false
        // For ||: negative term is true → whole thing true
        if cond.contains("||") {
            true // any !defined term makes it true
        } else {
            false // && with a positive term makes it false
        }
    } else {
        // No recognized symbols — keep content as safe default
        true
    }
}

/// Extract the main function body (for inline logic, not delegating).
fn extract_main_body(
    lines: &[&str],
    body_start: usize,
    body_end: usize,
    upper: &str,
    lower: &str,
) -> String {
    let mut body_lines: Vec<String> = Vec::new();
    let mut in_gencode_4 = false;

    for line in &lines[body_start..body_end] {
        if line.contains("START GENCODE SECTION 4") {
            in_gencode_4 = true;
            continue;
        }
        if line.contains("END GENCODE SECTION 4") {
            in_gencode_4 = false;
            continue;
        }
        if in_gencode_4 {
            continue;
        }
        if line.trim_start().starts_with("/* Generated */") {
            continue;
        }
        let trimmed = line.trim();
        if trimmed.starts_with("/* Insert ") && trimmed.ends_with("*/") {
            continue;
        }
        if trimmed.starts_with("/* insert ") && trimmed.ends_with("*/") {
            continue;
        }
        body_lines.push((*line).to_string());
    }

    // Resolve preprocessor conditionals for C double-precision target
    body_lines = strip_preprocessor_conditionals(&body_lines);

    // Trim leading/trailing blank lines
    while body_lines.first().is_some_and(|l| l.trim().is_empty()) {
        body_lines.remove(0);
    }
    while body_lines.last().is_some_and(|l| l.trim().is_empty()) {
        body_lines.pop();
    }

    let body_lines: Vec<String> = body_lines
        .iter()
        .map(|l| expand_macros(l, upper, lower))
        .collect();

    let mut body = String::new();
    for line in &body_lines {
        if line.trim().is_empty() {
            body.push('\n');
        } else {
            body.push_str(&format!("    {}\n", line.trim()));
        }
    }
    body
}

/// Apply all macro expansions and renames to a line.
fn expand_macros(line: &str, upper: &str, lower: &str) -> String {
    let mut s = line.to_string();

    // Remove `/* Generated */` prefix
    let trimmed = s.trim_start();
    if let Some(rest) = trimmed.strip_prefix("/* Generated */") {
        s = rest.to_string();
    }

    // DECLARE macros
    s = expand_declare_macros(&s);

    // CAST macros
    s = expand_cast_macros(&s);

    // VALUE_HANDLE macros (order matters: longer patterns first)
    s = expand_value_handle_macros(&s);

    // ENUM_VALUE macro
    s = expand_enum_value(&s);

    // FUNCTION_CALL / LOOKBACK_CALL macros (cross-indicator)
    s = expand_function_calls(&s, upper, lower);

    // TA_GLOBALS macros
    s = expand_globals_macros(&s);

    // FOR_EACH_OUTPUT / FOR_COUNTDOWN macros
    s = expand_loop_macros(&s);

    // INPUT_TYPE -> double
    s = s.replace("INPUT_TYPE", "double");

    // TA_PREFIX(INT_<NAME>) -> <name>
    let prefix_int = format!("TA_PREFIX(INT_{upper})");
    s = s.replace(&prefix_int, lower);

    // Clean up PER_TO_K -> TA_PER_TO_K (keep the macro, it's used in prefix-free too)
    // Actually looking at ema.c target, it uses TA_PER_TO_K
    s = s.replace("PER_TO_K(", "TA_PER_TO_K(");
    // But avoid double-prefixing
    s = s.replace("TA_TA_PER_TO_K(", "TA_PER_TO_K(");

    // ARRAY_MEMMOVE macros - keep as-is (rename to TA_ARRAY_COPY for the non-mix variant)
    // Actually looking at the target RSI, it uses TA_ARRAY_COPY
    s = s.replace("ARRAY_MEMMOVEMIX(", "TA_ARRAY_MEMMOVEMIX(");
    s = s.replace("ARRAY_MEMMOVE(", "TA_ARRAY_COPY(");
    // Avoid double-prefixing
    s = s.replace("TA_TA_ARRAY_MEMMOVEMIX(", "TA_ARRAY_MEMMOVEMIX(");
    s = s.replace("TA_TA_ARRAY_COPY(", "TA_ARRAY_COPY(");
    // Also clean up ARRAY_MEMMOVEMIX_VAR
    if s.contains("ARRAY_MEMMOVEMIX_VAR") {
        s = String::new(); // Remove this line entirely
    }

    s
}

/// Expand DECLARE_* macros.
fn expand_declare_macros(line: &str) -> String {
    let mut s = line.to_string();
    let re_double = Regex::new(r"DECLARE_DOUBLE_VAR\((\w+)\)").unwrap();
    let re_int = Regex::new(r"DECLARE_INT_VAR\((\w+)\)").unwrap();
    let re_index = Regex::new(r"DECLARE_INDEX_VAR\((\w+)\)").unwrap();
    let re_loop = Regex::new(r"DECLARE_LOOP_VAR\((\w+)\)").unwrap();

    s = re_double.replace_all(&s, "double $1;").to_string();
    s = re_int.replace_all(&s, "int $1;").to_string();
    s = re_index.replace_all(&s, "size_t $1;").to_string();
    // DECLARE_LOOP_VAR - remove the line
    if re_loop.is_match(&s) {
        return String::new();
    }

    s
}

/// Expand CAST_* macros.
#[allow(clippy::similar_names)]
fn expand_cast_macros(line: &str) -> String {
    let mut s = line.to_string();
    // These can be nested, so we need to handle them carefully.
    // Use a simple approach: expand innermost first, iterate.
    // Compile regexes outside the loop to avoid repeated compilation.
    let re_cast_f64 = Regex::new(r"CAST_TO_F64\(([^()]*)\)").unwrap();
    let re_index = Regex::new(r"CAST_TO_INDEX\(([^()]*)\)").unwrap();
    let re_cast_i32 = Regex::new(r"CAST_TO_I32\(([^()]*)\)").unwrap();
    for _ in 0..5 {
        let prev = s.clone();
        s = re_cast_f64.replace_all(&s, "(double)($1)").to_string();
        s = re_index.replace_all(&s, "(size_t)($1)").to_string();
        s = re_cast_i32.replace_all(&s, "(int)($1)").to_string();
        if s == prev {
            break;
        }
    }
    // Simplify (double)(var) to (double)var when var is simple
    let re_simple = Regex::new(r"\(double\)\((\w+)\)").unwrap();
    s = re_simple.replace_all(&s, "(double)$1").to_string();
    let re_simple = Regex::new(r"\(size_t\)\((\w+)\)").unwrap();
    s = re_simple.replace_all(&s, "(size_t)$1").to_string();
    let re_simple = Regex::new(r"\(int\)\((\w+)\)").unwrap();
    s = re_simple.replace_all(&s, "(int)$1").to_string();
    s
}

/// Expand `VALUE_HANDLE_DEREF`* macros.
fn expand_value_handle_macros(line: &str) -> String {
    let mut s = line.to_string();

    // VALUE_HANDLE_DEREF_TO_ZERO(x) -> (*x) = 0
    let re = Regex::new(r"VALUE_HANDLE_DEREF_TO_ZERO\((\w+)\)").unwrap();
    s = re.replace_all(&s, "*$1 = 0").to_string();

    // VALUE_HANDLE_DEREF_INDEX(n, v) -> (*n) = (v)
    let re = Regex::new(r"VALUE_HANDLE_DEREF_INDEX\((\w+),\s*(\w+)\)").unwrap();
    s = re.replace_all(&s, "*$1 = $2").to_string();

    // VALUE_HANDLE_DEREF(x) -> (*x)
    let re = Regex::new(r"VALUE_HANDLE_DEREF\((\w+)\)").unwrap();
    s = re.replace_all(&s, "*$1").to_string();

    s
}

/// Expand `ENUM_VALUE(Type,TA_VALUE,CamelValue)` -> `TA_VALUE`.
fn expand_enum_value(line: &str) -> String {
    let mut s = line.to_string();
    let re = Regex::new(r"ENUM_VALUE\(\w+,\s*(\w+),\s*\w+\)").unwrap();
    s = re.replace_all(&s, "$1").to_string();
    s
}

/// Expand `FUNCTION_CALL`, `LOOKBACK_CALL`, and direct TA_<NAME> references.
fn expand_function_calls(line: &str, upper: &str, lower: &str) -> String {
    let mut s = line.to_string();

    // FUNCTION_CALL(INT_<NAME>) -> <name>
    // This pattern handles any indicator name, not just the current one.
    let re_func_int = Regex::new(r"FUNCTION_CALL\(INT_(\w+)\)").unwrap();
    s = re_func_int
        .replace_all(&s, |caps: &regex::Captures| caps[1].to_lowercase())
        .to_string();

    // FUNCTION_CALL(<NAME>) -> <name>
    let re_func = Regex::new(r"FUNCTION_CALL\((\w+)\)").unwrap();
    s = re_func
        .replace_all(&s, |caps: &regex::Captures| caps[1].to_lowercase())
        .to_string();

    // FUNCTION_CALL_DOUBLE(INT_<NAME>) -> <name>
    let re_func_dbl = Regex::new(r"FUNCTION_CALL_DOUBLE\(INT_(\w+)\)").unwrap();
    s = re_func_dbl
        .replace_all(&s, |caps: &regex::Captures| caps[1].to_lowercase())
        .to_string();

    // FUNCTION_CALL_DOUBLE(<NAME>) -> <name>
    let re_func_dbl2 = Regex::new(r"FUNCTION_CALL_DOUBLE\((\w+)\)").unwrap();
    s = re_func_dbl2
        .replace_all(&s, |caps: &regex::Captures| caps[1].to_lowercase())
        .to_string();

    // LOOKBACK_CALL(<NAME>) -> <name>_lookback
    let re_lb = Regex::new(r"LOOKBACK_CALL\((\w+)\)").unwrap();
    s = re_lb
        .replace_all(&s, |caps: &regex::Captures| {
            format!("{}_lookback", caps[1].to_lowercase())
        })
        .to_string();

    // Direct references: TA_INT_<NAME>(...) -> <name>(...)
    // But be careful not to match TA_INT_<NAME> inside macro definitions.
    let re_ta_int = Regex::new(r"\bTA_INT_(\w+)\b").unwrap();
    s = re_ta_int
        .replace_all(&s, |caps: &regex::Captures| caps[1].to_lowercase())
        .to_string();

    // TA_<NAME>_Lookback -> <name>_lookback
    let re_ta_lb = Regex::new(r"\bTA_(\w+)_Lookback\b").unwrap();
    s = re_ta_lb
        .replace_all(&s, |caps: &regex::Captures| {
            format!("{}_lookback", caps[1].to_lowercase())
        })
        .to_string();

    let _ = (upper, lower); // suppress unused warnings
    s
}

/// Expand `TA_GLOBALS`_* macros.
fn expand_globals_macros(line: &str) -> String {
    let mut s = line.to_string();

    // TA_GLOBALS_UNSTABLE_PERIOD(TA_FUNC_UNST_<NAME>,CamelName) -> TA_GetUnstablePeriod(<NAME>)
    let re = Regex::new(r"TA_GLOBALS_UNSTABLE_PERIOD\(TA_FUNC_UNST_(\w+),\s*\w+\)").unwrap();
    s = re.replace_all(&s, "TA_GetUnstablePeriod($1)").to_string();

    // TA_GLOBALS_COMPATIBILITY -> TA_GetCompatibility()
    s = s.replace("TA_GLOBALS_COMPATIBILITY", "TA_GetCompatibility()");

    s
}

/// Expand loop macros.
fn expand_loop_macros(line: &str) -> String {
    let mut s = line.to_string();

    // FOR_EACH_OUTPUT(startIdx, endIdx, i, outIdx) -> expanded loop header
    // In the prefix-free C, this becomes a simple while loop.
    // Looking at mult.c target: the macro is expanded inline.
    // Keep as-is for now - the target uses explicit while loops.
    // Actually let's check: the target mult.c doesn't use FOR_EACH_OUTPUT.
    // It's manually expanded. So we need to expand it.
    let re_feo = Regex::new(r"FOR_EACH_OUTPUT\((\w+),\s*(\w+),\s*(\w+),\s*(\w+)\)").unwrap();
    if let Some(caps) = re_feo.captures(&s) {
        let start = &caps[1];
        let end = &caps[2];
        let i = &caps[3];
        let out_idx = &caps[4];
        s = format!(
            "{out_idx} = 0;\n    {i} = (size_t){start};\n    while( {i} <= (size_t){end} ) {{"
        );
    }

    // FOR_EACH_OUTPUT_END(outIdx) -> close brace + increment
    let re_feo_end = Regex::new(r"FOR_EACH_OUTPUT_END\((\w+)\)").unwrap();
    if re_feo_end.is_match(&s) {
        s = re_feo_end
            .replace_all(&s, |caps: &regex::Captures| {
                format!("{} += 1;\n        i += 1;\n    }}", &caps[1])
            })
            .to_string();
    }

    // FOR_COUNTDOWN(n, i) -> for( i = n; i > 0; i-- ) {
    let re_fcd = Regex::new(r"FOR_COUNTDOWN\((\w+),\s*(\w+)\)").unwrap();
    if let Some(caps) = re_fcd.captures(&s) {
        let n = &caps[1];
        let i = &caps[2];
        s = format!("for( {i} = {n}; {i} > 0; {i}-- ) {{");
    }

    // FOR_COUNTDOWN_END -> }
    s = s.replace("FOR_COUNTDOWN_END", "}");

    s
}

#[cfg(test)]
mod tests {
    use super::*;
    use std::fs;
    use std::path::Path;

    fn read_source_file(name: &str) -> String {
        let path = Path::new(env!("CARGO_MANIFEST_DIR"))
            .join("../../src/ta_func")
            .join(format!("ta_{name}.c"));
        fs::read_to_string(&path)
            .unwrap_or_else(|e| panic!("Failed to read {}: {}", path.display(), e))
    }

    #[test]
    fn test_extract_sma_source() {
        let source = read_source_file("SMA");
        let result = extract_function_source(&source, "sma");

        // Contains expected function names
        assert!(
            result.contains("sma_lookback("),
            "should contain sma_lookback("
        );
        assert!(result.contains("sma("), "should contain sma(");

        // Does NOT contain original prefixed names
        assert!(
            !result.contains("TA_SMA_Lookback"),
            "should not contain TA_SMA_Lookback"
        );
        assert!(
            !result.contains("TA_INT_SMA"),
            "should not contain TA_INT_SMA"
        );

        // Does NOT contain GENCODE markers
        assert!(!result.contains("GENCODE"), "should not contain GENCODE");

        // Does NOT contain /* Generated */ prefix
        assert!(
            !result.contains("/* Generated */"),
            "should not contain /* Generated */"
        );

        // Does NOT contain managed code guards
        assert!(
            !result.contains("#if defined( _MANAGED )"),
            "should not contain _MANAGED guards"
        );

        // Contains the lookback body
        assert!(
            result.contains("return optInTimePeriod - 1;"),
            "should contain lookback return"
        );

        // Contains TA_SUCCESS
        assert!(
            result.contains("return TA_SUCCESS;"),
            "should contain return TA_SUCCESS"
        );
    }

    #[test]
    fn test_extract_rsi_source() {
        let source = read_source_file("RSI");
        let result = extract_function_source(&source, "rsi");

        // Contains expected function names
        assert!(
            result.contains("rsi_lookback("),
            "should contain rsi_lookback("
        );
        assert!(result.contains("TA_RetCode rsi("), "should contain rsi(");

        // Contains expanded globals macros
        assert!(
            result.contains("TA_GetUnstablePeriod(RSI)"),
            "should contain TA_GetUnstablePeriod(RSI), got:\n{}",
            &result[..result.len().min(2000)]
        );
        assert!(
            result.contains("TA_GetCompatibility()"),
            "should contain TA_GetCompatibility()"
        );

        // Does NOT contain GENCODE markers
        assert!(!result.contains("GENCODE"), "should not contain GENCODE");

        // Does NOT contain unexpanded macros
        assert!(
            !result.contains("TA_GLOBALS_UNSTABLE_PERIOD"),
            "should not contain TA_GLOBALS_UNSTABLE_PERIOD"
        );
        assert!(
            !result.contains("TA_GLOBALS_COMPATIBILITY"),
            "should not contain TA_GLOBALS_COMPATIBILITY"
        );
    }

    #[test]
    fn test_extract_mult_source() {
        let source = read_source_file("MULT");
        let result = extract_function_source(&source, "mult");

        // Contains expected function names
        assert!(
            result.contains("mult_lookback("),
            "should contain mult_lookback("
        );
        assert!(result.contains("mult("), "should contain mult(");

        // Does NOT contain TA_MULT (except in lookback/logic names)
        assert!(
            !result.contains("TA_MULT"),
            "should not contain TA_MULT, got:\n{result}"
        );

        // Contains return 0 for lookback
        assert!(
            result.contains("return 0;"),
            "should contain return 0 for lookback"
        );
    }

    #[test]
    fn test_extract_ema_source() {
        let source = read_source_file("EMA");
        let result = extract_function_source(&source, "ema");

        // Contains expected function names
        assert!(
            result.contains("ema_lookback("),
            "should contain ema_lookback("
        );
        assert!(result.contains("TA_RetCode ema("), "should contain ema(");

        // EMA internally calls SMA via LOOKBACK_CALL and FUNCTION_CALL.
        // The TA_INT_EMA body calls LOOKBACK_CALL(EMA) -> ema_lookback.
        assert!(
            result.contains("ema_lookback("),
            "should contain ema_lookback reference"
        );

        // Does NOT contain TA_INT_SMA or TA_INT_EMA (these are generated by backends, not source defs)
        assert!(
            !result.contains("TA_INT_SMA"),
            "should not contain TA_INT_SMA"
        );
        assert!(
            !result.contains("TA_INT_EMA"),
            "should not contain TA_INT_EMA"
        );
        // Does NOT contain _logic suffix (plain names now)
        assert!(
            !result.contains("ema_logic"),
            "should not contain ema_logic"
        );

        // Contains TA_SUCCESS
        assert!(
            result.contains("return TA_SUCCESS;"),
            "should contain return TA_SUCCESS"
        );
    }
}
