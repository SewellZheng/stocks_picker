use ta_codegen_lib::backends;
use ta_codegen_lib::extractor::func_extractor::extract_function_source;
use ta_codegen_lib::extractor::table_parser::{parse_shared_defs, parse_table};
use ta_codegen_lib::extractor::TableFuncDef;
use ta_codegen_lib::helper_registry::HelperRegistry;
use ta_codegen_lib::ir;
use ta_codegen_lib::parser;
use ta_codegen_lib::registry::Registry;
use ta_codegen_lib::server_gen;

use std::collections::HashMap;
use std::path::{Path, PathBuf};

/// Find the repository root by walking up from the current directory
/// looking for `ta_func_defs/`.
fn repo_root() -> PathBuf {
    if let Ok(cwd) = std::env::current_dir() {
        let mut dir = cwd.as_path();
        loop {
            if dir.join("ta_func_defs").is_dir() {
                return dir.to_path_buf();
            }
            match dir.parent() {
                Some(parent) => dir = parent,
                None => break,
            }
        }
    }
    eprintln!("error: cannot find ta_func_defs/ in any parent directory.");
    eprintln!("       Run ta_codegen from within the ta-lib repository.");
    std::process::exit(1);
}

fn main() {
    let args: Vec<String> = std::env::args().collect();
    let command = args.get(1).map(|s| s.as_str()).unwrap_or("generate");
    match command {
        "generate" => {
            let func_filter = find_arg(&args, "--func");
            let backend_filter = find_arg(&args, "--backend");
            generate(func_filter.as_deref(), backend_filter.as_deref());
        }
        "generate-servers" => {
            let func_filter = find_arg(&args, "--func");
            let backend_filter = find_arg(&args, "--backend");
            generate_servers(func_filter.as_deref(), backend_filter.as_deref());
        }
        "generate-bench" => {
            let backend_filter = find_arg(&args, "--backend");
            generate_bench(backend_filter.as_deref());
        }
        "build" => {
            let backend_filter = find_arg(&args, "--backend");
            build_servers(backend_filter.as_deref());
        }
        "extract" => {
            let func_filter = find_arg(&args, "--function");
            extract(func_filter.as_deref());
        }
        _ => {
            eprintln!("Usage: ta_codegen <command> [options]");
            eprintln!();
            eprintln!("Commands:");
            eprintln!("  generate         Generate code for all backends (default)");
            eprintln!("  generate-servers  Generate JSON-RPC server wrappers for each language");
            eprintln!("  build            Compile generated server source into executables");
            eprintln!("  extract          Extract indicators from C source to ta_func_defs/");
            eprintln!();
            eprintln!("Options for 'generate' / 'generate-servers' / 'build':");
            eprintln!(
                "  --func=NAME[,NAME,...]      Only generate specified functions (default: all)"
            );
            eprintln!(
                "  --backend=NAME[,NAME,...]    Only generate specified backends (default: all)"
            );
            eprintln!("                               Backends: c, rust, java, dotnet");
            eprintln!();
            eprintln!("Options for 'extract':");
            eprintln!(
                "  --function=NAME[,NAME,...]   Only extract specified functions (default: all)"
            );
            std::process::exit(1);
        }
    }
}

/// Strip TA_INT_* function declarations from ta_utility.h content.
/// Removes everything between sentinel markers (inclusive).
fn strip_ta_int_declarations(content: &str) -> String {
    let mut result = String::new();
    let mut skip = false;
    for line in content.lines() {
        if line.contains("BEGIN_TA_INT_DECLARATIONS") {
            skip = true;
            continue;
        }
        if line.contains("END_TA_INT_DECLARATIONS") {
            skip = false;
            continue;
        }
        if !skip {
            result.push_str(line);
            result.push('\n');
        }
    }
    result
}

/// Wire parsed C source (guarded + optional private) into a FuncDef.
fn wire_parsed_source(func_def: &mut ir::FuncDef, parsed: &parser::c_source::ParsedCSource) {
    let guarded = parsed
        .functions
        .iter()
        .find(|f| !f.name.ends_with("_private"))
        .expect("C source must contain at least one function");
    func_def.body = guarded.body.clone();
    func_def.lookback = Some(ir::LookbackExpr::Code(parsed.lookback_body.clone()));

    let private_fn = parsed
        .functions
        .iter()
        .find(|f| f.name.ends_with("_private"));
    if let Some(priv_fn) = private_fn {
        func_def.private_body = priv_fn.body.clone();
        func_def.has_explicit_private = true;
        let guarded_param_names: std::collections::HashSet<_> =
            guarded.params.iter().map(|(name, _)| name.clone()).collect();
        func_def.private_extra_params = priv_fn
            .params
            .iter()
            .filter(|(name, _)| !guarded_param_names.contains(name))
            .cloned()
            .collect();
        // Extract init expressions for extra params from the guarded body's VarDecls.
        // Used by backends to generate S_ variants (which inline the private body
        // with extra params as local variables instead of function params).
        let extra_names: std::collections::HashSet<_> = func_def
            .private_extra_params
            .iter()
            .map(|(name, _)| name.as_str())
            .collect();
        func_def.private_param_init = guarded
            .body
            .iter()
            .filter_map(|stmt| {
                if let ir::Statement::VarDecl { name, init: Some(expr), .. } = stmt {
                    if extra_names.contains(name.as_str()) {
                        return Some((name.clone(), expr.clone()));
                    }
                }
                None
            })
            .collect();
    } else {
        func_def.private_body = func_def.body.clone();
    }
}

fn find_arg(args: &[String], prefix: &str) -> Option<String> {
    let prefix_eq = format!("{}=", prefix);
    args.iter()
        .find(|a| a.starts_with(&prefix_eq))
        .map(|a| a[prefix_eq.len()..].to_string())
}

fn generate(func_filter: Option<&str>, backend_filter: Option<&str>) {
    let root = repo_root();
    let base = root.join("ta_func_defs");

    // Load indicator registry for cross-call resolution
    let registry = Registry::from_dir(&base);

    // Load helper registry for expression inlining
    let helper_registry = HelperRegistry::from_dir(&base);

    // Load enum definitions
    let enums_path = base.join("enums.yaml");
    let enums = if enums_path.exists() {
        parser::enums::load_enums(&enums_path)
    } else {
        HashMap::new()
    };

    // Discover all function definition directories
    let mut func_dirs: Vec<_> = std::fs::read_dir(&base)
        .expect("Cannot read ta_func_defs directory")
        .filter_map(|e| e.ok())
        .filter(|e| e.path().is_dir())
        .collect();
    func_dirs.sort_by_key(|e| e.file_name());

    let filter_names: Option<Vec<String>> =
        func_filter.map(|f| f.split(',').map(|s| s.trim().to_uppercase()).collect());

    let backends_to_run: Vec<&str> = match backend_filter {
        Some(b) => b.split(',').map(|s| s.trim()).collect(),
        None => vec!["c", "rust", "java", "dotnet"],
    };

    // Clean stale per-function output files before generating.
    // This prevents generate-servers from picking up outdated artifacts
    // (e.g., stale Core_*.java files that were generated with old code).
    // Only clean when generating all functions (no filter) to avoid
    // accidentally removing files for functions not being regenerated.
    let out_base = root.join("ta_codegen_output");

    if func_filter.is_none() {
        for backend in &backends_to_run {
            clean_generated_files(&out_base, backend);
        }
    }

    // Phase 1: Load all FuncDefs
    let mut generated_funcs: Vec<ir::FuncDef> = Vec::new();

    for entry in &func_dirs {
        let dir = entry.path();
        let func_name_lower = entry.file_name().to_string_lossy().to_string();

        if let Some(ref names) = filter_names {
            if !names.iter().any(|n| n == &func_name_lower.to_uppercase()) {
                continue;
            }
        }

        let yaml_path = dir.join(format!("{}.yaml", func_name_lower));
        let c_path = dir.join(format!("{}.c", func_name_lower));

        if !yaml_path.exists() {
            eprintln!("Skipping {}: missing .yaml file", func_name_lower);
            continue;
        }

        if !c_path.exists() {
            eprintln!("Skipping {}: missing .c file", func_name_lower);
            continue;
        }

        let mut func_def = parser::yaml::parse_yaml(&yaml_path);
        let parsed = parser::c_source::parse_c_source(&c_path);
        wire_parsed_source(&mut func_def, &parsed);
        generated_funcs.push(func_def);
    }

    // Phase 2: Generate output for each backend
    for func_def in &generated_funcs {
        for backend in &backends_to_run {
            generate_backend(func_def, backend, &enums, &registry, &helper_registry, &out_base);
        }
    }

    // Generate Rust crate scaffolding when Rust is one of the backends
    if backends_to_run.contains(&"rust") {
        let types_src = root.join("ta_func_defs/lib/rust/types.rs");
        generate_rust_crate_scaffolding(&out_base, &generated_funcs, &types_src);
    }

    // For cross-function outputs (func_list, Makefile.am), use all definitions
    // regardless of --func filter. Reuse already-parsed data when unfiltered.
    let all_yaml_defs;
    let all_funcs: &[ir::FuncDef] = if func_filter.is_some() {
        all_yaml_defs = load_all_yaml_defs(&base);
        &all_yaml_defs
    } else {
        &generated_funcs
    };

    backends::func_list::generate(all_funcs, &root.join("ta_func_list.txt"));
    backends::func_api_xml::generate(all_funcs, &root.join("ta_func_api.xml"));

    // Generate Makefile.am and copy C library files when C is one of the backends
    if backends_to_run.contains(&"c") {
        backends::makefile_am::generate(all_funcs, &root.join("src/ta_func/Makefile.am"));
        backends::cmake_lists::generate(all_funcs, &root.join("CMakeLists.txt"), &root);

        let c_lib_src = root.join("ta_func_defs/lib/c");
        let c_dir = root.join("ta_codegen_output/c");
        std::fs::create_dir_all(&c_dir).unwrap();
        for filename in &["ta_lib_types.h"] {
            let src = c_lib_src.join(filename);
            if src.exists() {
                let dest = c_dir.join(filename);
                std::fs::copy(&src, &dest).unwrap();
                println!("  Copied {filename} -> {}", dest.display());
            }
        }

        // Copy ta_common from src/ — unmodified c-ref code
        let ta_common_dst = c_dir.join("ta_common");
        std::fs::create_dir_all(&ta_common_dst).unwrap();
        let ta_common_src = root.join("src/ta_common");
        for filename in &["ta_memory.h", "ta_magic_nb.h", "ta_global.h", "ta_global.c", "ta_pragma.h", "ta_retcode.c", "ta_version.c"] {
            let src = ta_common_src.join(filename);
            if src.exists() {
                std::fs::copy(&src, ta_common_dst.join(filename)).unwrap();
            }
        }
        // Copy ta_utility.c from src/ta_func/
        let utility_c_src = root.join("src/ta_func/ta_utility.c");
        if utility_c_src.exists() {
            std::fs::copy(&utility_c_src, ta_common_dst.join("ta_utility.c")).unwrap();
        }
        // Generate stripped ta_utility.h (remove TA_INT_* declarations, keep macros)
        let utility_h_src = root.join("src/ta_func/ta_utility.h");
        if utility_h_src.exists() {
            let content = std::fs::read_to_string(&utility_h_src).unwrap();
            let stripped = strip_ta_int_declarations(&content);
            std::fs::write(ta_common_dst.join("ta_utility.h"), &stripped).unwrap();
        }
        println!("  Copied ta_common/ -> {}", ta_common_dst.display());

        // Generate ta_func_unguarded.h into include/ (public header)
        let unguarded_h = server_gen::generate_c_header_stub(all_funcs);
        let include_path = root.join("include").join("ta_func_unguarded.h");
        std::fs::write(&include_path, &unguarded_h).unwrap();
        println!("  ta_func_unguarded.h -> {}", include_path.display());

        // Generate ta_func_private.h into ta_codegen_output/c/ta_func/
        let private_h = server_gen::generate_c_private_header(all_funcs);
        let private_path = out_base.join("c").join("ta_func").join("ta_func_private.h");
        std::fs::write(&private_path, &private_h).unwrap();
        println!("  ta_func_private.h -> {}", private_path.display());

        // Generate ta_abstract layer from YAML definitions
        backends::ta_abstract_c::generate(all_funcs, &enums, &out_base);
    }
}

/// Load all YAML function definitions (no C source parsing, no filter).
/// Used for cross-function outputs like `ta_func_list.txt`.
fn load_all_yaml_defs(base: &Path) -> Vec<ir::FuncDef> {
    let mut dirs: Vec<_> = std::fs::read_dir(base)
        .expect("Cannot read ta_func_defs directory")
        .filter_map(|e| e.ok())
        .filter(|e| e.path().is_dir())
        .collect();
    dirs.sort_by_key(|e| e.file_name());

    let mut funcs = Vec::new();
    for entry in &dirs {
        let dir = entry.path();
        let dir_name = entry.file_name().to_string_lossy().to_string();

        if dir_name == "helpers" || dir_name == "lib" {
            continue;
        }

        let yaml_path = dir.join(format!("{}.yaml", dir_name));
        if yaml_path.exists() {
            funcs.push(parser::yaml::parse_yaml(&yaml_path));
        }
    }

    funcs
}

fn load_func_defs(func_filter: Option<&str>, root: &Path) -> Vec<ir::FuncDef> {
    let base = root.join("ta_func_defs");

    let mut func_dirs: Vec<_> = std::fs::read_dir(&base)
        .expect("Cannot read ta_func_defs directory")
        .filter_map(|e| e.ok())
        .filter(|e| e.path().is_dir())
        .collect();
    func_dirs.sort_by_key(|e| e.file_name());

    let filter_names: Option<Vec<String>> =
        func_filter.map(|f| f.split(',').map(|s| s.trim().to_uppercase()).collect());

    let mut funcs = Vec::new();

    for entry in &func_dirs {
        let dir = entry.path();
        let func_name_lower = entry.file_name().to_string_lossy().to_string();

        if let Some(ref names) = filter_names {
            if !names.iter().any(|n| n == &func_name_lower.to_uppercase()) {
                continue;
            }
        }

        let yaml_path = dir.join(format!("{}.yaml", func_name_lower));
        let c_path = dir.join(format!("{}.c", func_name_lower));

        if !yaml_path.exists() || !c_path.exists() {
            continue;
        }

        let mut func_def = parser::yaml::parse_yaml(&yaml_path);
        let parsed = parser::c_source::parse_c_source(&c_path);
        wire_parsed_source(&mut func_def, &parsed);

        funcs.push(func_def);
    }

    funcs
}

fn generate_servers(func_filter: Option<&str>, backend_filter: Option<&str>) {
    let root = repo_root();
    let funcs = load_func_defs(func_filter, &root);

    if funcs.is_empty() {
        eprintln!("No function definitions found");
        std::process::exit(1);
    }

    let backends_to_run: Vec<&str> = match backend_filter {
        Some(b) => b.split(',').map(|s| s.trim()).collect(),
        None => vec!["c", "java", "dotnet", "rust"],
    };

    let out_base = root.join("ta_codegen_output");

    for backend in &backends_to_run {
        match *backend {
            "c" => {
                let dir = out_base.join("c");
                std::fs::create_dir_all(&dir).unwrap();

                // ta_func_unguarded.h is generated in generate(), not here

                let output = server_gen::generate_c_server(&funcs);
                let path = dir.join("ta_codegen_serve.c");
                std::fs::write(&path, &output).unwrap();
                println!("  C server -> {}", path.display());
            }
            "java" => {
                let dir = out_base.join("java");
                std::fs::create_dir_all(&dir).unwrap();

                let template = server_gen::generate_java_server(&funcs);
                let output = server_gen::inline_java_core_methods(&template, &dir, &funcs);
                let path = dir.join("TaCodegenServe.java");
                std::fs::write(&path, &output).unwrap();
                println!("  Java server -> {}", path.display());
            }
            "dotnet" => {
                let output = server_gen::generate_dotnet_server(&funcs);
                let dir = out_base.join("dotnet");
                std::fs::create_dir_all(&dir).unwrap();
                let path = dir.join("TaCodegenServe.cs");
                std::fs::write(&path, &output).unwrap();
                println!("  .NET server -> {}", path.display());
            }
            "rust" => {
                let rust_bin_dir = out_base.join("rust/src/bin");
                std::fs::create_dir_all(&rust_bin_dir).unwrap();
                let output = server_gen::generate_rust_server(&funcs);
                let path = rust_bin_dir.join("ta_codegen_serve.rs");
                std::fs::write(&path, &output).unwrap();
                println!("  Rust server -> {}", path.display());
            }
            _ => {
                eprintln!("Unknown backend: {}", backend);
            }
        }
    }

    println!(
        "Server source files generated for {} function(s).",
        funcs.len()
    );
}

fn generate_bench(backend_filter: Option<&str>) {
    let root = repo_root();
    let funcs = load_func_defs(None, &root);
    if funcs.is_empty() {
        eprintln!("No function definitions found");
        std::process::exit(1);
    }
    let backends: Vec<&str> = match backend_filter {
        Some(b) => b.split(',').map(|s| s.trim()).collect(),
        None => vec!["c"],
    };
    let out_base = root.join("ta_codegen_output");
    for backend in &backends {
        if *backend == "c" {
            let dir = out_base.join("c");
            std::fs::create_dir_all(&dir).unwrap();
            ta_codegen_lib::bench_gen::write_c_bench(&funcs, &dir);
        }
    }
}

fn build_servers(backend_filter: Option<&str>) {
    let root = repo_root();
    let backends_to_build: Vec<&str> = match backend_filter {
        Some(b) => b.split(',').map(|s| s.trim()).collect(),
        None => vec!["c", "java", "dotnet", "rust"],
    };

    let out_base = root.join("ta_codegen_output");
    let bin_dir = root.join("bin");

    // Remove stale shared-lib marker so it rebuilds fresh each invocation.
    let _ = std::fs::remove_file(bin_dir.join(".shared_lib_built"));

    for backend in &backends_to_build {
        match *backend {
            "c" => {
                print!("  Building C server... ");
                let c_dir = out_base.join("c");
                let include_dir = root.join("include");
                let ta_common_dir = c_dir.join("ta_common");
                let ta_abstract_dir = c_dir.join("ta_abstract");
                let ta_frames_dir = ta_abstract_dir.join("frames");
                let ta_abstract_serve_dir = root.join("ta_func_defs/lib/c");
                let src = c_dir.join("ta_codegen_serve.c");
                let dst = bin_dir.join("ta_codegen_serve_c");
                match std::process::Command::new("gcc")
                    .args([
                        "-o",
                        dst.to_str().unwrap(),
                        src.to_str().unwrap(),
                        &format!("-I{}", c_dir.to_str().unwrap()),
                        &format!("-I{}", include_dir.to_str().unwrap()),
                        &format!("-I{}", ta_common_dir.to_str().unwrap()),
                        &format!("-I{}", ta_abstract_dir.to_str().unwrap()),
                        &format!("-I{}", ta_frames_dir.to_str().unwrap()),
                        &format!("-I{}", ta_abstract_serve_dir.to_str().unwrap()),
                        "-lm",
                        "-O3",
                        "-flto",
                        "-DNDEBUG",
                        "-Wno-parentheses-equality",
                    ])
                    .status()
                {
                    Ok(s) if s.success() => println!("OK"),
                    Ok(s) => println!("FAILED (exit {})", s.code().unwrap_or(-1)),
                    Err(e) => println!("FAILED (gcc not found: {})", e),
                }
                // Also build direct-call benchmark binary if source exists
                let bench_src = out_base.join("c/ta_bench_cg.c");
                if bench_src.exists() {
                    print!("  Building C bench... ");
                    let bench_dst = bin_dir.join("ta_bench_cg");
                    let bench_inc_c = out_base.join("c");
                    let bench_inc_func = out_base.join("c/ta_func");
                    match std::process::Command::new("gcc")
                        .args([
                            "-o",
                            bench_dst.to_str().unwrap(),
                            bench_src.to_str().unwrap(),
                            &format!("-I{}", bench_inc_c.to_str().unwrap()),
                            &format!("-I{}", bench_inc_func.to_str().unwrap()),
                            &format!("-I{}", include_dir.to_str().unwrap()),
                            &format!("-I{}", ta_common_dir.to_str().unwrap()),
                            "-lm",
                            "-O3",
                            "-flto",
                            "-DNDEBUG",
                            "-Wno-parentheses-equality",
                        ])
                        .status()
                    {
                        Ok(s) if s.success() => println!("OK"),
                        Ok(s) => println!("FAILED (exit {})", s.code().unwrap_or(-1)),
                        Err(e) => println!("FAILED (gcc not found: {})", e),
                    }
                }
            }
            "java" => {
                print!("  Building Java server... ");
                let java_dir = out_base.join("java");
                let class_dir = bin_dir.join("ta_codegen_java");
                std::fs::create_dir_all(&class_dir).ok();
                match std::process::Command::new("javac")
                    .args([
                        "-d",
                        class_dir.to_str().unwrap(),
                        java_dir.join("TaCodegenServe.java").to_str().unwrap(),
                    ])
                    .status()
                {
                    Ok(s) if s.success() => println!("OK"),
                    Ok(s) => println!("FAILED (exit {})", s.code().unwrap_or(-1)),
                    Err(e) => println!("FAILED (javac not found: {})", e),
                }
            }
            "dotnet" => {
                // Build shared library from generated C files (needed by .NET P/Invoke)
                build_shared_lib(&out_base, &bin_dir);

                print!("  Building .NET server... ");
                let dotnet_dir = out_base.join("dotnet");
                let dotnet_out = bin_dir.join("ta_codegen_dotnet");
                std::fs::create_dir_all(&dotnet_out).ok();

                // Create a minimal .csproj if not present
                let csproj_path = dotnet_dir.join("TaCodegenServe.csproj");
                if !csproj_path.exists() {
                    let csproj = r#"<Project Sdk="Microsoft.NET.Sdk">
  <PropertyGroup>
    <OutputType>Exe</OutputType>
    <TargetFramework>net10.0</TargetFramework>
    <Nullable>enable</Nullable>
  </PropertyGroup>
</Project>"#;
                    std::fs::write(&csproj_path, csproj).unwrap();
                }

                match std::process::Command::new("dotnet")
                    .args([
                        "publish",
                        "-c",
                        "Release",
                        "-o",
                        dotnet_out.to_str().unwrap(),
                        dotnet_dir.to_str().unwrap(),
                    ])
                    .status()
                {
                    Ok(s) if s.success() => {
                        // Copy shared lib into dotnet output dir for P/Invoke discovery
                        let lib_name = if cfg!(target_os = "macos") {
                            "libta_codegen_funcs.dylib"
                        } else {
                            "libta_codegen_funcs.so"
                        };
                        let lib_src = bin_dir.join(lib_name);
                        let lib_dst = dotnet_out.join(lib_name);
                        if lib_src.exists() {
                            std::fs::copy(&lib_src, &lib_dst).ok();
                        }
                        println!("OK");
                    }
                    Ok(s) => println!("FAILED (exit {})", s.code().unwrap_or(-1)),
                    Err(e) => println!("FAILED (dotnet not found: {})", e),
                }
            }
            "rust" => {
                print!("  Building Rust server... ");
                let rust_dir = out_base.join("rust");
                match std::process::Command::new("cargo")
                    .args(["build", "--release", "--bin", "ta_codegen_serve"])
                    .current_dir(&rust_dir)
                    .status()
                {
                    Ok(s) if s.success() => {
                        let src = rust_dir.join("target/release/ta_codegen_serve");
                        let dst = bin_dir.join("ta_codegen_serve_rust");
                        if let Err(e) = std::fs::copy(&src, &dst) {
                            println!("OK (build), FAILED (copy: {})", e);
                        } else {
                            println!("OK");
                        }
                    }
                    Ok(s) => println!("FAILED (exit {})", s.code().unwrap_or(-1)),
                    Err(e) => println!("FAILED (cargo not found: {})", e),
                }
            }
            _ => {
                eprintln!("  Unknown backend: {}", backend);
            }
        }
    }
}

/// Build a shared library from the generated C files.
/// This is used by both the Python (ctypes) and .NET (P/Invoke) servers.
/// The shared lib exports all TA_* functions and is placed in bin/.
fn build_shared_lib(out_base: &Path, bin_dir: &Path) {
    let marker = bin_dir.join(".shared_lib_built");
    if marker.exists() {
        return; // Already built this run
    }

    print!("  Building shared library... ");
    let c_dir = out_base.join("c/ta_func");
    let lib_name = if cfg!(target_os = "macos") {
        "libta_codegen_funcs.dylib"
    } else {
        "libta_codegen_funcs.so"
    };
    let dst = bin_dir.join(lib_name);

    let shared_flag = if cfg!(target_os = "macos") {
        "-dynamiclib"
    } else {
        "-shared"
    };

    // Generate a unified source file that includes all individual C files.
    // This handles forward declarations (e.g., MA calls SMA/EMA/WMA).
    let mut unity_src = String::new();
    unity_src.push_str("/* Unity build for shared library */\n");
    unity_src.push_str("#include \"ta_func_unguarded.h\"\n");
    unity_src.push_str("#include \"ta_func_private.h\"\n");
    unity_src.push_str("#include \"ta_common/ta_global.c\"\n");
    unity_src.push_str("#include \"ta_common/ta_utility.c\"\n");
    unity_src.push_str("#include \"ta_common/ta_version.c\"\n");
    unity_src.push_str("#include \"ta_common/ta_retcode.c\"\n\n");

    let mut c_names: Vec<String> = Vec::new();
    if let Ok(entries) = std::fs::read_dir(&c_dir) {
        let mut sorted: Vec<_> = entries.filter_map(|e| e.ok()).collect();
        sorted.sort_by_key(|e| e.file_name());
        for entry in sorted {
            let name = entry.file_name().to_string_lossy().to_string();
            if name.starts_with("ta_")
                && name.ends_with(".c")
                && name != "ta_codegen_serve.c"
                && name != "ta_codegen_funcs.c"
                && name != "ta_bench_cg.c"
            {
                c_names.push(name);
            }
        }
    }

    if c_names.is_empty() {
        println!("FAILED (no C source files found)");
        return;
    }

    // Sort alphabetically, but move MA to end (it calls SMA/EMA/WMA)
    c_names.sort();
    if let Some(pos) = c_names.iter().position(|n| n == "ta_MA.c") {
        let ma = c_names.remove(pos);
        c_names.push(ma);
    }
    for name in &c_names {
        unity_src.push_str(&format!("#include \"{}\"\n", name));
    }

    let unity_path = c_dir.join("ta_codegen_funcs.c");
    std::fs::write(&unity_path, &unity_src).unwrap();

    // Derive root from out_base (ta_codegen_output's parent)
    let root = out_base.parent().unwrap();
    let include_dir = root.join("include");
    let ta_common_dir = out_base.join("c/ta_common");

    let args = vec![
        shared_flag.to_string(),
        "-fPIC".to_string(),
        "-o".to_string(),
        dst.to_str().unwrap().to_string(),
        unity_path.to_str().unwrap().to_string(),
        format!("-I{}", c_dir.parent().unwrap().to_str().unwrap()),
        format!("-I{}", include_dir.to_str().unwrap()),
        format!("-I{}", ta_common_dir.to_str().unwrap()),
        "-lm".to_string(),
        "-O3".to_string(),
        "-flto".to_string(),
        "-DNDEBUG".to_string(),
        "-Wno-parentheses-equality".to_string(),
    ];

    match std::process::Command::new("gcc").args(&args).status() {
        Ok(s) if s.success() => {
            println!("OK");
            std::fs::write(&marker, "").ok();
        }
        Ok(s) => println!("FAILED (exit {})", s.code().unwrap_or(-1)),
        Err(e) => println!("FAILED (gcc not found: {})", e),
    }
}

fn extract(func_filter: Option<&str>) {
    let base = repo_root();
    let tables_dir = base.join("src/ta_abstract/tables");
    let def_ui_path = base.join("src/ta_abstract/ta_def_ui.c");
    let func_dir = base.join("src/ta_func");
    let out_dir = base.join("ta_func_defs");

    // 1. Parse shared definitions
    let def_ui_source = std::fs::read_to_string(&def_ui_path).expect("Cannot read ta_def_ui.c");
    let shared = parse_shared_defs(&def_ui_source);

    // 2. Parse all table files
    let mut all_funcs: Vec<TableFuncDef> = Vec::new();
    let mut table_files: Vec<_> = std::fs::read_dir(&tables_dir)
        .expect("Cannot read tables directory")
        .filter_map(|e| e.ok())
        .filter(|e| {
            let name = e.file_name().to_string_lossy().to_string();
            name.starts_with("table_") && name.ends_with(".c")
        })
        .collect();
    table_files.sort_by_key(|e| e.file_name());

    for entry in &table_files {
        let source = std::fs::read_to_string(entry.path()).unwrap();
        let funcs = parse_table(&source, &shared);
        all_funcs.extend(funcs);
    }

    println!("Found {} indicators in abstract tables", all_funcs.len());

    // 3. Apply function filter
    let filter_names: Option<Vec<String>> =
        func_filter.map(|f| f.split(',').map(|s| s.trim().to_uppercase()).collect());

    let mut succeeded = 0;
    let mut failed = 0;
    let mut skipped = 0;

    for func in &all_funcs {
        if let Some(ref names) = filter_names {
            if !names.iter().any(|n| n == &func.name) {
                continue;
            }
        }

        // Read corresponding source file
        let src_path = func_dir.join(format!("ta_{}.c", func.name));
        if !src_path.exists() {
            eprintln!("  SKIP {}: source file not found", func.name);
            skipped += 1;
            continue;
        }

        let source = match std::fs::read_to_string(&src_path) {
            Ok(s) => s,
            Err(e) => {
                eprintln!("  FAIL {}: cannot read source: {}", func.name, e);
                failed += 1;
                continue;
            }
        };

        // Extract C logic (catch panics and timeouts from parser)
        let name_lower = func.name.to_lowercase();
        let source_clone = source.clone();
        let name_clone = name_lower.clone();
        let (tx, rx) = std::sync::mpsc::channel();
        std::thread::spawn(move || {
            let result = std::panic::catch_unwind(std::panic::AssertUnwindSafe(|| {
                extract_function_source(&source_clone, &name_clone)
            }));
            let _ = tx.send(result);
        });

        let extracted_c = match rx.recv_timeout(std::time::Duration::from_secs(10)) {
            Ok(Ok(c)) => c,
            Ok(Err(_)) => {
                eprintln!("  FAIL {}: extractor panicked", func.name);
                failed += 1;
                continue;
            }
            Err(_) => {
                eprintln!("  FAIL {}: extractor timed out", func.name);
                failed += 1;
                continue;
            }
        };

        if extracted_c.trim().is_empty() {
            eprintln!("  FAIL {}: extractor produced empty output", func.name);
            failed += 1;
            continue;
        }

        // Generate YAML
        let yaml = func_to_yaml(func);

        // Write output
        let indicator_dir = out_dir.join(&name_lower);
        std::fs::create_dir_all(&indicator_dir).unwrap();

        let yaml_path = indicator_dir.join(format!("{}.yaml", name_lower));
        let c_path = indicator_dir.join(format!("{}.c", name_lower));

        std::fs::write(&yaml_path, &yaml).unwrap();
        std::fs::write(&c_path, &extracted_c).unwrap();

        println!("  OK   {}", func.name);
        succeeded += 1;
    }

    println!();
    println!(
        "Extracted {} indicators ({} succeeded, {} failed, {} skipped)",
        succeeded + failed + skipped,
        succeeded,
        failed,
        skipped
    );
}

fn func_to_yaml(func: &TableFuncDef) -> String {
    let mut out = String::new();

    out.push_str(&format!("name: {}\n", func.name));
    out.push_str(&format!("camel_case: {}\n", func.camel_case));
    out.push_str(&format!("group: {}\n", func.group));
    out.push_str(&format!("hint: {}\n", func.hint));

    // flags
    if func.flags.is_empty() {
        out.push_str("flags: []\n");
    } else {
        out.push_str(&format!("flags: [{}]\n", func.flags.join(", ")));
    }

    // inputs
    out.push_str("inputs:\n");
    for input in &func.inputs {
        out.push_str(&format!("  - name: {}\n", input.name));
        out.push_str(&format!("    type: {}\n", input.param_type));
        if input.param_type == "price" && !input.price_flags.is_empty() {
            out.push_str(&format!(
                "    price_components: [{}]\n",
                input.price_flags.join(", ")
            ));
        }
    }

    // optional_inputs
    if !func.optional_inputs.is_empty() {
        out.push_str("optional_inputs:\n");
        for opt in &func.optional_inputs {
            out.push_str(&format!("  - name: {}\n", opt.name));
            out.push_str(&format!("    type: {}\n", opt.param_type));
            if !opt.display_name.is_empty() {
                out.push_str(&format!("    display_name: {}\n", opt.display_name));
            }
            if !opt.hint.is_empty() {
                out.push_str(&format!("    hint: {}\n", opt.hint));
            }
            if let Some((min, max)) = opt.range {
                out.push_str(&format!(
                    "    range: [{}, {}]\n",
                    format_yaml_num(min),
                    format_yaml_num(max)
                ));
            }
            if let Some(default) = opt.default {
                out.push_str(&format!("    default: {}\n", format_yaml_num(default)));
            }
            if let Some((start, end, inc)) = opt.suggested {
                out.push_str(&format!(
                    "    suggested: [{}, {}, {}]\n",
                    format_yaml_num(start),
                    format_yaml_num(end),
                    format_yaml_num(inc)
                ));
            }
            if !opt.flags.is_empty() {
                out.push_str(&format!("    flags: [{}]\n", opt.flags.join(", ")));
            }
        }
    }

    // outputs
    out.push_str("outputs:\n");
    for output in &func.outputs {
        out.push_str(&format!("  - name: {}\n", output.name));
        out.push_str(&format!("    type: {}\n", output.param_type));
        if !output.flags.is_empty() {
            out.push_str(&format!("    flags: [{}]\n", output.flags.join(", ")));
        }
    }

    out
}

/// Format a number for YAML output: integers without decimal, reals with decimal.
fn format_yaml_num(v: f64) -> String {
    if v == f64::MIN {
        return "TA_REAL_MIN".to_string();
    }
    if v == f64::MAX {
        return "TA_REAL_MAX".to_string();
    }
    if v == v.floor() && v.abs() < 1e15 {
        format!("{}", v as i64)
    } else {
        format!("{}", v)
    }
}

fn generate_rust_crate_scaffolding(out_base: &Path, funcs: &[ir::FuncDef], types_src: &Path) {
    let rust_dir = out_base.join("rust");
    let src_dir = rust_dir.join("src");
    let ta_func_dir = src_dir.join("ta_func");
    let bin_dir = src_dir.join("bin");

    std::fs::create_dir_all(&ta_func_dir).unwrap();
    std::fs::create_dir_all(&bin_dir).unwrap();

    // --- Cargo.toml ---
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

[profile.release]
lto = "thin"
codegen-units = 1
"#;
    let cargo_path = rust_dir.join("Cargo.toml");
    std::fs::write(&cargo_path, cargo_toml).unwrap();
    println!("  Scaffolding -> {}", cargo_path.display());

    // --- .cargo/config.toml ---
    let cargo_config_dir = rust_dir.join(".cargo");
    std::fs::create_dir_all(&cargo_config_dir).unwrap();
    let cargo_config = r#"[build]
rustflags = ["-C", "target-cpu=native"]
"#;
    std::fs::write(cargo_config_dir.join("config.toml"), cargo_config).unwrap();

    // --- src/lib.rs ---
    let lib_rs = r#"#![allow(non_snake_case, unused_variables, unused_assignments, unused_mut, unused_parens, arithmetic_overflow)]
pub mod ta_func;
pub use ta_func::*;
"#;
    let lib_path = src_dir.join("lib.rs");
    std::fs::write(&lib_path, lib_rs).unwrap();
    println!("  Scaffolding -> {}", lib_path.display());

    // --- Copy hand-written types.rs from ta_func_defs/lib/rust/ ---
    if types_src.exists() {
        let types_dest = ta_func_dir.join("types.rs");
        std::fs::copy(types_src, &types_dest).unwrap();
        println!("  Copied types.rs -> {}", types_dest.display());
    }

    // --- src/ta_func/mod.rs (generated: imports types + declares indicator modules) ---
    let mut mod_rs = String::new();
    mod_rs.push_str(
        r#"// Types and Core struct are in types.rs (hand-written, not generated).
mod types;
pub use types::*;

// Generated indicator modules:
"#,
    );

    // Types were moved to ta_func_defs/lib/rust/types.rs (hand-written).
    // The old ~250 lines of inline type definitions were removed here.
    //
    // To skip the dead r# string below (kept to avoid a massive edit),
    // jump to "Add mod declarations for each generated indicator".
    if false { let _ = r#"/* TA-LIB Copyright (c) 1999-2025, Mario Fortier
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the following
 * conditions are met:
 *
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in
 *   the documentation and/or other materials provided with the
 *   distribution.
 *
 * - Neither name of author nor the names of its contributors
 *   may be used to endorse or promote products derived from this
 *   software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/// Return codes for TA-Lib function calls.
#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub enum RetCode {
    /// Function completed successfully.
    Success,
    /// One or more parameters are invalid.
    BadParam,
    /// The start index is out of range.
    OutOfRangeStartIndex,
    /// The end index is out of range or less than start index.
    OutOfRangeEndIndex,
    /// Memory allocation failed.
    AllocErr,
    /// Internal error occurred.
    InternalError,
}

/// Compatibility mode for technical analysis calculations.
#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub enum Compatibility {
    /// Default TA-Lib compatibility mode.
    Default,
    /// Metastock-compatible calculation mode.
    Metastock,
}

/// Identifies functions that have an unstable period.
///
/// Some technical analysis functions produce unreliable output during an
/// initial "unstable" period. This enum identifies each such function so
/// that a per-function unstable period can be configured.
#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub enum FuncUnstId {
    /// Average Directional Movement Index.
    Adx,
    /// Average Directional Movement Index Rating.
    Adxr,
    /// Average True Range.
    Atr,
    /// Chande Momentum Oscillator.
    Cmo,
    /// Directional Movement Index.
    Dx,
    /// Exponential Moving Average.
    Ema,
    /// Hilbert Transform - Dominant Cycle Period.
    HtDcPeriod,
    /// Hilbert Transform - Dominant Cycle Phase.
    HtDcPhase,
    /// Hilbert Transform - Phasor Components.
    HtPhasor,
    /// Hilbert Transform - SineWave.
    HtSine,
    /// Hilbert Transform - Instantaneous Trendline.
    HtTrendline,
    /// Hilbert Transform - Trend vs Cycle Mode.
    HtTrendMode,
    /// Intraday Momentum Index.
    Imi,
    /// Kaufman Adaptive Moving Average.
    Kama,
    /// MESA Adaptive Moving Average.
    Mama,
    /// Money Flow Index.
    Mfi,
    /// Minus Directional Indicator.
    MinusDI,
    /// Minus Directional Movement.
    MinusDM,
    /// Normalized Average True Range.
    Natr,
    /// Plus Directional Indicator.
    PlusDI,
    /// Plus Directional Movement.
    PlusDM,
    /// Relative Strength Index.
    Rsi,
    /// Stochastic Relative Strength Index.
    StochRsi,
    /// Triple Exponential Moving Average (T3).
    T3,
    /// Wildcard: set the unstable period for all functions at once.
    FuncUnstAll,
}

/// A single candlestick setting entry.
#[derive(Debug, Clone, Copy)]
pub struct CandleSetting {
    /// Range type: 0 = RealBody, 1 = HighLow, 2 = Shadows.
    pub range_type: i32,
    /// Number of periods for averaging.
    pub avg_period: i32,
    /// Scaling factor.
    pub factor: f64,
}

/// All candlestick settings used by CDL* pattern indicators.
#[derive(Debug, Clone, Copy)]
#[allow(non_snake_case)]
pub struct CandleSettings {
    pub body_long: CandleSetting,
    pub body_very_long: CandleSetting,
    pub body_short: CandleSetting,
    pub body_doji: CandleSetting,
    pub shadow_long: CandleSetting,
    pub shadow_very_long: CandleSetting,
    pub shadow_short: CandleSetting,
    pub shadow_very_short: CandleSetting,
    pub near: CandleSetting,
    pub far: CandleSetting,
    pub equal: CandleSetting,
}

impl CandleSettings {
    /// Default candle settings matching TA-Lib C defaults.
    pub fn default_settings() -> Self {
        Self {
            body_long:         CandleSetting { range_type: 0, avg_period: 10, factor: 1.0 },
            body_very_long:    CandleSetting { range_type: 0, avg_period: 10, factor: 3.0 },
            body_short:        CandleSetting { range_type: 0, avg_period: 10, factor: 1.0 },
            body_doji:         CandleSetting { range_type: 1, avg_period: 10, factor: 0.1 },
            shadow_long:       CandleSetting { range_type: 0, avg_period:  0, factor: 1.0 },
            shadow_very_long:  CandleSetting { range_type: 0, avg_period:  0, factor: 2.0 },
            shadow_short:      CandleSetting { range_type: 2, avg_period: 10, factor: 1.0 },
            shadow_very_short: CandleSetting { range_type: 1, avg_period: 10, factor: 0.1 },
            near:              CandleSetting { range_type: 1, avg_period:  5, factor: 0.2 },
            far:               CandleSetting { range_type: 1, avg_period:  5, factor: 0.6 },
            equal:             CandleSetting { range_type: 1, avg_period:  5, factor: 0.05 },
        }
    }
}

/// Core struct providing access to all TA-Lib technical analysis functions.
///
/// Create an instance with [`Core::new()`] and call functions as methods.
/// Unstable period and compatibility mode can be configured per-instance.
///
/// # Example
///
/// ```
/// use ta_lib::ta_func::{Core, RetCode};
///
/// let core = Core::new();
/// let lookback = core.sma_lookback(30);
/// assert_eq!(lookback, 29);
/// ```
pub struct Core {
    /// Unstable period for each function identified by [`FuncUnstId`].
    pub unstable_period: [i32; FuncUnstId::FuncUnstAll as usize],
    /// Compatibility mode (default: [`Compatibility::Default`]).
    pub compatibility: Compatibility,
    /// Candlestick pattern settings.
    pub candle_settings: CandleSettings,
}

impl Core {
    /// Create a new Core instance with default settings.
    ///
    /// All unstable periods are initialized to 0 and compatibility
    /// mode is set to [`Compatibility::Default`].
    pub fn new() -> Self {
        Self {
            unstable_period: [0; FuncUnstId::FuncUnstAll as usize],
            compatibility: Compatibility::Default,
            candle_settings: CandleSettings::default_settings(),
        }
    }

    /// Set the unstable period for a specific function.
    pub fn set_unstable_period(&mut self, id: FuncUnstId, period: i32) {
        self.unstable_period[id as usize] = period;
    }

    /// Get the unstable period for a specific function.
    pub fn get_unstable_period(&self, id: FuncUnstId) -> i32 {
        self.unstable_period[id as usize]
    }

    /// Set the compatibility mode.
    pub fn set_compatibility(&mut self, compat: Compatibility) {
        self.compatibility = compat;
    }

    /// Get the current compatibility mode.
    pub fn get_compatibility(&self) -> Compatibility {
        self.compatibility
    }

    /// Compute candlestick range for the given range type and OHLC values.
    #[inline(always)]
    #[allow(non_snake_case)]
    pub fn ta_candlerange(&self, rangeType: i32, open: f64, high: f64, low: f64, close: f64) -> f64 {
        match rangeType {
            0 => (close - open).abs(),
            1 => high - low,
            2 => high - low - (close - open).abs(),
            _ => 0.0,
        }
    }

    /// Compute candlestick average for the given settings and OHLC values.
    #[inline(always)]
    #[allow(non_snake_case)]
    pub fn ta_candleaverage(&self, rangeType: i32, avgPeriod: i32, factor: f64, sum: f64,
                             open: f64, high: f64, low: f64, close: f64) -> f64 {
        let avg = if avgPeriod != 0 {
            sum / (avgPeriod as f64)
        } else {
            self.ta_candlerange(rangeType, open, high, low, close)
        };
        let divisor = if rangeType == 2 { 2.0 } else { 1.0 };
        factor * avg / divisor
    }
}

"#; }

    // Add mod declarations for each generated indicator
    let mut func_names: Vec<String> = funcs
        .iter()
        .map(|f| f.name.to_lowercase())
        .collect();
    func_names.sort();

    for name in &func_names {
        mod_rs.push_str(&format!("mod {};\n", name));
    }

    let mod_path = ta_func_dir.join("mod.rs");
    std::fs::write(&mod_path, &mod_rs).unwrap();
    println!("  Scaffolding -> {}", mod_path.display());

    // --- src/bin/ta_codegen_serve.rs (placeholder) ---
    let placeholder_bin = r#"fn main() {
    eprintln!("Server not yet generated — run: ta_codegen generate-servers --backend=rust");
}
"#;
    let bin_path = bin_dir.join("ta_codegen_serve.rs");
    // Only write placeholder if the server binary doesn't already exist
    if !bin_path.exists() {
        std::fs::write(&bin_path, placeholder_bin).unwrap();
        println!("  Scaffolding -> {} (placeholder)", bin_path.display());
    }

    println!("  Rust crate scaffolding generated ({} indicators)", func_names.len());
}

/// Remove per-function generated files for a backend so stale artifacts
/// from a previous run cannot leak into `generate-servers`.
fn clean_generated_files(out_base: &Path, backend: &str) {
    let (dir, prefix, suffix) = match backend {
        "c" => (out_base.join("c/ta_func"), "ta_", ".c"),
        "java" => (out_base.join("java"), "Core_", ".java"),
        "dotnet" => (out_base.join("dotnet"), "Core_", ".h"),
        "rust" => (out_base.join("rust/src/ta_func"), "", ".rs"),
        _ => return,
    };
    if !dir.exists() {
        return;
    }
    // Keep hand-written / scaffolding files (types.rs, mod.rs, ta_lib_types.h, etc.)
    let keep: &[&str] = match backend {
        "rust" => &["types.rs", "mod.rs"],
        _ => &[],
    };
    let mut count = 0;
    if let Ok(entries) = std::fs::read_dir(&dir) {
        for entry in entries.flatten() {
            let name = entry.file_name().to_string_lossy().to_string();
            if name.starts_with(prefix) && name.ends_with(suffix) && !keep.contains(&name.as_str())
            {
                std::fs::remove_file(entry.path()).ok();
                count += 1;
            }
        }
    }
    if count > 0 {
        println!("  Cleaned {count} stale {backend} files from {}", dir.display());
    }
}

fn generate_backend(
    func_def: &ir::FuncDef,
    backend: &str,
    enums: &HashMap<String, ir::EnumDef>,
    registry: &Registry,
    helpers: &HelperRegistry,
    out_base: &Path,
) {

    match backend {
        "c" => {
            let output = backends::c::generate(func_def, enums, registry, helpers);
            let dir = out_base.join("c/ta_func");
            std::fs::create_dir_all(&dir).unwrap();
            let path = dir.join(format!("ta_{}.c", func_def.name));
            std::fs::write(&path, &output).unwrap();
            println!("  {} -> {}", func_def.name, path.display());
        }
        "rust" => {
            let output = backends::rust_lang::generate(func_def, enums, registry, helpers);
            let dir = out_base.join("rust/src/ta_func");
            std::fs::create_dir_all(&dir).unwrap();
            let path = dir.join(format!("{}.rs", func_def.name.to_lowercase()));
            std::fs::write(&path, &output).unwrap();
            println!("  {} -> {}", func_def.name, path.display());
        }
        "java" => {
            let output = backends::java::generate(func_def, enums, registry, helpers);
            let dir = out_base.join("java");
            std::fs::create_dir_all(&dir).unwrap();
            let path = dir.join(format!("Core_{}.java", func_def.name));
            std::fs::write(&path, &output).unwrap();
            println!("  {} -> {}", func_def.name, path.display());
        }
        "dotnet" => {
            let output = backends::dotnet::generate(func_def, enums, registry, helpers);
            let dir = out_base.join("dotnet");
            std::fs::create_dir_all(&dir).unwrap();
            let path = dir.join(format!("Core_{}.h", func_def.name));
            std::fs::write(&path, &output).unwrap();
            println!("  {} -> {}", func_def.name, path.display());
        }
        _ => {
            eprintln!("Unknown backend: {}", backend);
        }
    }
}
