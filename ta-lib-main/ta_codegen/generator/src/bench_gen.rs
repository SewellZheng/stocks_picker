//! Generates a standalone direct-call benchmark binary for the C backend.
//!
//! Unlike the JSON-RPC server, this binary generates its own price data
//! (deterministic, seed=42) and calls indicator functions directly.
//! Output: one `FUNCNAME timing_ns` line per function on stdout.

use crate::ir::{FuncDef, ParamType};
use crate::server_gen::expand_input_names;
use std::path::Path;

pub fn generate_c_bench(funcs: &[FuncDef]) -> String {
    let mut s = String::new();

    s.push_str("/* Auto-generated direct-call benchmark for ta_codegen C output.\n");
    s.push_str(" * No JSON-RPC -- generates its own data, calls functions directly.\n");
    s.push_str(" * Output: FUNCNAME timing_ns (one per line)\n");
    s.push_str(" */\n");
    s.push_str("#include <stdio.h>\n#include <stdlib.h>\n#include <string.h>\n");
    s.push_str("#include <math.h>\n#include <time.h>\n");
    s.push_str("#ifdef __APPLE__\n#include <mach/mach_time.h>\n#endif\n\n");

    // Include headers for unguarded and private function declarations
    s.push_str("#include \"ta_func_unguarded.h\"\n");
    s.push_str("#include \"ta_func/ta_func_private.h\"\n\n");
    // Globals + indicator includes (same single-TU pattern as server)
    s.push_str("#include \"ta_common/ta_global.c\"\n");
    s.push_str("#include \"ta_func/ta_utility.c\"\n");
    s.push_str("#include \"ta_common/ta_version.c\"\n");
    s.push_str("#include \"ta_common/ta_retcode.c\"\n\n");
    let mut sorted: Vec<&str> = funcs.iter().map(|f| f.name.as_str()).collect();
    sorted.sort_unstable();
    if let Some(pos) = sorted.iter().position(|n| *n == "MA") {
        let ma = sorted.remove(pos);
        sorted.push(ma);
    }
    for name in &sorted {
        s.push_str(&format!("#include \"ta_{name}.c\"\n"));
    }
    s.push('\n');

    s.push_str(TIMING_HELPER);
    s.push_str(PRICE_DATA_GEN);

    s.push_str("#define MAX_POINTS 200000\n");
    s.push_str("static double g_outBuf0[MAX_POINTS];\n");
    s.push_str("static double g_outBuf1[MAX_POINTS];\n");
    s.push_str("static double g_outBuf2[MAX_POINTS];\n");
    s.push_str("static int g_outIntBuf0[MAX_POINTS];\n");
    s.push_str("static int g_outIntBuf1[MAX_POINTS];\n\n");

    s.push_str(FUNC_MATCHES);
    generate_bench_func(&mut s, funcs);
    s.push_str(MAIN_FUNC);

    s
}

/// Map an expanded input name to the global benchmark array variable.
fn input_name_to_global(name: &str, real_idx: usize) -> String {
    match name {
        "inOpen" => "g_open".to_string(),
        "inHigh" => "g_high".to_string(),
        "inLow" => "g_low".to_string(),
        "inClose" => "g_close".to_string(),
        "inVolume" => "g_volume".to_string(),
        "inOpenInterest" => "g_oi".to_string(),
        _ => {
            // Generic real inputs: first uses g_close, second uses g_high
            if real_idx == 0 { "g_close".to_string() } else { "g_high".to_string() }
        }
    }
}

fn generate_bench_func(s: &mut String, funcs: &[FuncDef]) {
    // Volatile sink prevents LTO from eliminating function calls
    s.push_str("static volatile int g_sink = 0;\n\n");
    s.push_str("static void bench_all(const char *filter, int iters) {\n");

    for func in funcs {
        let name = &func.name;
        let ta_name = format!("TA_{name}");
        let input_names = expand_input_names(&func.inputs);

        s.push_str(&format!("    if( func_matches(filter, \"{name}\") ) {{\n"));
        s.push_str("        long long best = 0;\n");
        s.push_str("        for( int pass = 0; pass < 3; pass++ ) {\n");
        s.push_str("            int outBegIdx, outNBElement;\n");
        s.push_str("            long long t0 = get_nanotime();\n");
        s.push_str("            for( int it = 0; it < iters; it++ ) {\n");

        // Build the function call arguments
        let mut args = Vec::new();
        args.push("0".to_string());
        args.push("g_nPoints - 1".to_string());

        // Input arrays
        let mut real_idx = 0;
        for inp_name in &input_names {
            args.push(input_name_to_global(inp_name, real_idx));
            if !matches!(
                inp_name.as_str(),
                "inOpen" | "inHigh" | "inLow" | "inClose" | "inVolume" | "inOpenInterest"
            ) {
                real_idx += 1;
            }
        }

        // Optional inputs — use defaults
        for opt in &func.optional_inputs {
            if opt.param_type == ParamType::Real {
                let default = opt.default.unwrap_or(0.0);
                args.push(format!("{default:.15}"));
            } else {
                #[allow(clippy::cast_possible_truncation)]
                let default = opt.default.unwrap_or(0.0) as i32;
                args.push(format!("{default}"));
            }
        }

        args.push("&outBegIdx".to_string());
        args.push("&outNBElement".to_string());

        // Output arrays
        let mut out_real_idx = 0;
        let mut out_int_idx = 0;
        for out in &func.outputs {
            if out.param_type == ParamType::Integer {
                args.push(format!("g_outIntBuf{out_int_idx}"));
                out_int_idx += 1;
            } else {
                args.push(format!("g_outBuf{out_real_idx}"));
                out_real_idx += 1;
            }
        }

        let args_str = args.join(", ");
        s.push_str(&format!("                {ta_name}({args_str});\n"));

        s.push_str("            }\n");
        s.push_str("            long long elapsed = get_nanotime() - t0;\n");
        s.push_str("            if( !best || elapsed < best ) best = elapsed;\n");
        // Observe outputs after timing — prevents LTO from eliminating function bodies.
        // Must read from ALL output buffers, not just outNBElement.
        s.push_str("            g_sink += outNBElement;\n");
        // Sink first element of each output array so LTO can't DCE the writes
        {
            let mut sink_real = 0;
            let mut sink_int = 0;
            for out in &func.outputs {
                if out.param_type == ParamType::Integer {
                    s.push_str(&format!("            g_sink += g_outIntBuf{sink_int}[0];\n"));
                    sink_int += 1;
                } else {
                    s.push_str(&format!("            g_sink += (int)g_outBuf{sink_real}[0];\n"));
                    sink_real += 1;
                }
            }
        }
        s.push_str("        }\n");
        s.push_str(&format!("        printf(\"{name} %lld\\n\", best / iters);\n"));
        s.push_str("        fflush(stdout);\n");
        s.push_str("    }\n");
    }

    s.push_str("}\n\n");
}

pub fn write_c_bench(funcs: &[FuncDef], output_dir: &Path) {
    let content = generate_c_bench(funcs);
    let path = output_dir.join("ta_bench_cg.c");
    std::fs::write(&path, &content).unwrap_or_else(|e| {
        panic!("Failed to write {}: {e}", path.display());
    });
    eprintln!("  C bench -> {}", path.display());
}

const TIMING_HELPER: &str = r"
static long long get_nanotime(void) {
#ifdef __APPLE__
    static mach_timebase_info_data_t info = {0, 0};
    if( info.denom == 0 ) mach_timebase_info(&info);
    uint64_t t = mach_absolute_time();
    return (long long)(t * info.numer / info.denom);
#else
    struct timespec ts;
    if( clock_gettime(CLOCK_MONOTONIC, &ts) == 0 )
        return (long long)ts.tv_sec * 1000000000LL + (long long)ts.tv_nsec;
    return 0;
#endif
}

";

const PRICE_DATA_GEN: &str = r"
static double *g_open, *g_high, *g_low, *g_close, *g_volume, *g_oi;
static int g_nPoints;

static void generate_price_data(int n) {
    g_nPoints = n;
    g_open   = calloc(n, sizeof(double));
    g_high   = calloc(n, sizeof(double));
    g_low    = calloc(n, sizeof(double));
    g_close  = calloc(n, sizeof(double));
    g_volume = calloc(n, sizeof(double));
    g_oi     = calloc(n, sizeof(double));
    unsigned int seed = 42;
    double price = 100.0;
    for( int i = 0; i < n; i++ ) {
        seed = seed * 1103515245 + 12345;
        double r = ((double)(seed >> 16) / 32768.0) - 1.0;
        double o = price, c = price + r * 2.0;
        double h = fmax(o, c) + fabs(r) * 0.5;
        double l = fmin(o, c) - fabs(r) * 0.5;
        if( l < 1.0 ) l = 1.0;
        g_open[i] = o; g_high[i] = h; g_low[i] = l; g_close[i] = c;
        g_volume[i] = 1000000.0 + r * 500000.0;
        price = c; if( price < 1.0 ) price = 1.0;
    }
}

";

const FUNC_MATCHES: &str = r#"
static int func_matches(const char *filter, const char *name) {
    if( !filter || !*filter ) return 1;
    char buf[512]; strncpy(buf, filter, sizeof(buf)-1); buf[sizeof(buf)-1]='\0';
    char *saveptr = NULL;
    for( char *tok = strtok_r(buf, ",", &saveptr); tok; tok = strtok_r(NULL, ",", &saveptr) )
        if( strcasestr(name, tok) ) return 1;
    return 0;
}

"#;

const MAIN_FUNC: &str = r#"
int main(int argc, char *argv[]) {
    TA_Initialize();
    int n_points = 100000;
    int n_iters = 200;
    const char *func_filter = NULL;
    for( int i = 1; i < argc; i++ ) {
        if( strncmp(argv[i], "--points=", 9) == 0 )    n_points = atoi(argv[i]+9);
        else if( strncmp(argv[i], "--iters=", 8) == 0 ) n_iters = atoi(argv[i]+8);
        else if( strncmp(argv[i], "--function=", 11) == 0 ) func_filter = argv[i]+11;
    }
    if( n_points > MAX_POINTS ) n_points = MAX_POINTS;
    generate_price_data(n_points);
    bench_all(func_filter, n_iters);
    free(g_open); free(g_high); free(g_low); free(g_close); free(g_volume); free(g_oi);
    return 0;
}
"#;
