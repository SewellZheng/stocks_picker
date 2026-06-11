# Universal Cross-Language Regression Testing Implementation Plan

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make ta_regtest drive codegen language servers to test all 158 indicators across all languages, comparing against the C reference with timing data and correctness reporting.

**Architecture:** One generic `doRangeTest` callback uses `ta_abstract` to call any C function and serialize requests to JSON-RPC servers. Servers return output + timing. Results compared with epsilon tolerance and reported as CLI summary + cross-language table + JSONL rolling report.

**Tech Stack:** C (ta_regtest, ta_abstract), Rust (server_gen.rs in ta_codegen), JSON-RPC over stdin/stdout pipes.

**Spec:** `docs/superpowers/specs/2026-03-13-universal-regtest-design.md`

---

## File Structure

| File | Responsibility |
|------|---------------|
| `tools/ta_codegen/src/server_gen.rs` | Generate JSON-RPC servers for all languages. Add multi-output, price inputs, real params, timing, list_functions, all 24 unstable IDs. |
| `src/tools/ta_regtest/test_codegen.c` | Generic ta_abstract-driven callback, JSON request builder, response parser, timing collection, reporting. |
| `src/tools/ta_regtest/test_codegen.h` | Public API (unchanged signature, possibly add flags). |
| `src/tools/ta_regtest/ta_regtest.c` | `--codegen` / `--codegen-only` CLI flags, wire up `test_codegen()`. |
| `src/tools/ta_regtest/Makefile.am` | Already includes `codegen_pipe.c` and `test_codegen.c`. No changes expected. |

---

## Chunk 1: Server Protocol Extensions

### Task 1: Expand `func_unst_id` to all 24 unstable-period functions

**Files:**
- Modify: `tools/ta_codegen/src/server_gen.rs` — `func_unst_id()` function (~line 12-18)

- [ ] **Step 1: Read `server_gen.rs` and locate `func_unst_id`**

Currently only maps EMA and RSI. Read the function.

- [ ] **Step 2: Update `func_unst_id` to map all 24 functions**

```rust
fn func_unst_id(name: &str) -> Option<i32> {
    match name {
        "ADX" => Some(0),
        "ADXR" => Some(1),
        "ATR" => Some(2),
        "CMO" => Some(3),
        "DX" => Some(4),
        "EMA" => Some(5),
        "HT_DCPERIOD" => Some(6),
        "HT_DCPHASE" => Some(7),
        "HT_PHASOR" => Some(8),
        "HT_SINE" => Some(9),
        "HT_TRENDLINE" => Some(10),
        "HT_TRENDMODE" => Some(11),
        "IMI" => Some(12),
        "KAMA" => Some(13),
        "MAMA" => Some(14),
        "MFI" => Some(15),
        "MINUS_DI" => Some(16),
        "MINUS_DM" => Some(17),
        "NATR" => Some(18),
        "PLUS_DI" => Some(19),
        "PLUS_DM" => Some(20),
        "RSI" => Some(21),
        "STOCHRSI" => Some(22),
        "T3" => Some(23),
        _ => None,
    }
}
```

**IMPORTANT:** Verify these numeric IDs against `TA_FuncUnstId` enum in `include/ta_defs.h`. The enum order is the source of truth — the values above are estimates. Read `ta_defs.h` and use the actual enum values.

- [ ] **Step 3: Run ta_codegen tests**

```bash
cd tools/ta_codegen && cargo test
```

Expected: All tests pass (func_unst_id is not directly tested but shouldn't break anything).

- [ ] **Step 4: Commit**

```bash
git add tools/ta_codegen/src/server_gen.rs
git commit -m "feat(server_gen): expand func_unst_id to all 24 unstable-period functions"
```

---

### Task 2: Add `json_find_double` helper to C server generation

**Files:**
- Modify: `tools/ta_codegen/src/server_gen.rs` — `generate_c_json_helpers()` function

- [ ] **Step 1: Read `generate_c_json_helpers()` in `server_gen.rs`**

Currently generates `json_find_int` and `json_find_double_array`. Need to add `json_find_double` for parsing single double values from JSON (e.g., `"optInNbDevUp": 2.0`).

- [ ] **Step 2: Add `json_find_double` to the C JSON helpers string**

Add this to the output of `generate_c_json_helpers()`:

```c
static double json_find_double(const char *json, const char *field) {
    char pattern[256];
    snprintf(pattern, sizeof(pattern), "\"%s\":", field);
    const char *p = strstr(json, pattern);
    if( !p ) return 0.0;
    p += strlen(pattern);
    while( *p == ' ' ) p++;
    return strtod(p, NULL);
}
```

- [ ] **Step 3: Update optional param parsing in dispatch generation**

In `generate_c_dispatch()` (or wherever optional params are parsed from JSON), update so that:
- `TA_OptInput_IntegerRange` and `TA_OptInput_IntegerList` params use `json_find_int`
- `TA_OptInput_RealRange` params use `json_find_double`

This requires checking `FuncDef.opt_inputs[i].param_type` in the IR. Read how optional params are currently rendered in the dispatch function to understand the pattern.

- [ ] **Step 4: Apply same change to Java, .NET, and Python server generators**

Each server generator has its own JSON parsing. Add equivalent `double` parsing where needed:
- Java: `optJSONObject.getDouble(name)` instead of `getInt`
- .NET: `Convert.ToDouble` instead of `Convert.ToInt32`
- Python: `float(params[name])` instead of `int(params[name])`

Check `generate_java_server`, `generate_dotnet_server`, `generate_swig_server` for their optional param parsing patterns.

- [ ] **Step 5: Run ta_codegen tests**

```bash
cd tools/ta_codegen && cargo test
```

- [ ] **Step 6: Commit**

```bash
git add tools/ta_codegen/src/server_gen.rs
git commit -m "feat(server_gen): add json_find_double for real-valued optional params"
```

---

### Task 3: Add price input support to server dispatch

**Files:**
- Modify: `tools/ta_codegen/src/server_gen.rs` — dispatch generation functions for all languages

- [ ] **Step 1: Read how `generate_c_dispatch` currently handles inputs**

Currently, inputs are parsed as `inReal` (single array). For price inputs (STOCH, BBANDS, ADX), the server needs to parse named arrays: `inOpen`, `inHigh`, `inLow`, `inClose`, `inVolume`.

Check `ir::ParamType` — there should be a `Price` variant or similar that distinguishes price inputs from real inputs.

- [ ] **Step 2: Update C dispatch generation for price inputs**

For each function, check if any input is `ParamType::Price`. If so, generate:

```c
/* For price inputs: parse named OHLCV arrays */
int n_high = json_find_double_array(json, "inHigh", g_inHigh, MAX_ARRAY_SIZE);
int n_low = json_find_double_array(json, "inLow", g_inLow, MAX_ARRAY_SIZE);
int n_close = json_find_double_array(json, "inClose", g_inClose, MAX_ARRAY_SIZE);
/* ... only for flags that the function needs */
```

The IR's `FuncDef` should have input param info that says which OHLCV components are needed. Check `ir.rs` for the input parameter model.

- [ ] **Step 3: Add global buffers for OHLCV arrays**

The C server currently has a single `g_inBuf` global. Add additional globals:

```c
static double g_inOpen[MAX_ARRAY_SIZE];
static double g_inHigh[MAX_ARRAY_SIZE];
static double g_inLow[MAX_ARRAY_SIZE];
static double g_inClose[MAX_ARRAY_SIZE];
static double g_inVolume[MAX_ARRAY_SIZE];
```

These need to be generated before the dispatch function.

- [ ] **Step 4: Apply same changes to Java, .NET, Python servers**

Each language server needs the same pattern: parse named OHLCV arrays from the JSON params.

- [ ] **Step 5: Run ta_codegen tests**

```bash
cd tools/ta_codegen && cargo test
```

- [ ] **Step 6: Commit**

```bash
git add tools/ta_codegen/src/server_gen.rs
git commit -m "feat(server_gen): add price input (OHLCV) support to all server dispatchers"
```

---

### Task 4: Add multi-output and integer output support to servers

**Files:**
- Modify: `tools/ta_codegen/src/server_gen.rs` — dispatch and response generation

- [ ] **Step 1: Read how outputs are currently handled in C dispatch**

Currently, there's one `g_outBuf` and the response writes `"outReal": [...]`. Need to support:
- Multiple real outputs: `"outReal": [...], "outReal1": [...], "outReal2": [...]`
- Integer outputs: `"outInteger": [...]`

- [ ] **Step 2: Add additional output buffers to C server generation**

```c
static double g_outBuf[MAX_ARRAY_SIZE];
static double g_outBuf1[MAX_ARRAY_SIZE];
static double g_outBuf2[MAX_ARRAY_SIZE];
static int g_outIntBuf[MAX_ARRAY_SIZE];
static int g_outIntBuf1[MAX_ARRAY_SIZE];
```

- [ ] **Step 3: Update C dispatch to pass correct output buffers per function**

For each function's outputs (from `FuncDef.outputs`), generate the correct buffer assignment:
- First `Real` output → `g_outBuf`
- Second `Real` output → `g_outBuf1`
- Third `Real` output → `g_outBuf2`
- First `Integer` output → `g_outIntBuf`

- [ ] **Step 4: Update C response serialization for multi-output**

After calling the function, serialize all outputs:

```c
/* Write response with all outputs */
pos += snprintf(buf+pos, ...,"\"outBegIdx\":%d,\"outNBElement\":%d", outBegIdx, outNbElement);

/* Real outputs */
if( nbRealOutputs >= 1 ) {
    pos += snprintf(buf+pos, ..., ",\"outReal\":");
    pos += json_write_double_array(buf+pos, ..., g_outBuf, outNbElement);
}
if( nbRealOutputs >= 2 ) {
    pos += snprintf(buf+pos, ..., ",\"outReal1\":");
    pos += json_write_double_array(buf+pos, ..., g_outBuf1, outNbElement);
}
/* ... same pattern for outReal2, outInteger, etc. */
```

- [ ] **Step 5: Add `json_write_int_array` helper for integer outputs**

```c
static int json_write_int_array(char *buf, int buf_size, const int *data, int count) {
    int pos = 0;
    buf[pos++] = '[';
    for( int i = 0; i < count; i++ ) {
        if( i > 0 ) pos += snprintf(buf + pos, buf_size - pos, ",");
        pos += snprintf(buf + pos, buf_size - pos, "%d", data[i]);
    }
    buf[pos++] = ']';
    buf[pos] = '\0';
    return pos;
}
```

- [ ] **Step 6: Apply same changes to Java, .NET, Python servers**

- [ ] **Step 7: Run ta_codegen tests**

```bash
cd tools/ta_codegen && cargo test
```

- [ ] **Step 8: Commit**

```bash
git add tools/ta_codegen/src/server_gen.rs
git commit -m "feat(server_gen): add multi-output and integer output support to all servers"
```

---

### Task 5: Add timing and `list_functions` to servers

**Files:**
- Modify: `tools/ta_codegen/src/server_gen.rs`

- [ ] **Step 1: Add timing measurement to C server dispatch**

Wrap each function call with clock measurement. In the generated C code:

```c
#include <time.h>

/* In dispatch function, around the function call: */
struct timespec ts_start, ts_end;
clock_gettime(CLOCK_MONOTONIC, &ts_start);
retCode = TA_SMA(startIdx, endIdx, inReal, optInTimePeriod, &outBegIdx, &outNbElement, g_outBuf);
clock_gettime(CLOCK_MONOTONIC, &ts_end);
long timing_ns = (ts_end.tv_sec - ts_start.tv_sec) * 1000000000L + (ts_end.tv_nsec - ts_start.tv_nsec);
```

Add `"timing_ns":%ld` to the JSON response.

- [ ] **Step 2: Add timing to Java, .NET, Python servers**

- Java: `System.nanoTime()` before/after
- .NET: `Stopwatch.GetTimestamp()` before/after
- Python: `time.perf_counter_ns()` before/after

- [ ] **Step 3: Add `list_functions` method to C server main loop**

In the generated `main()` function, before the dispatch, check for the `list_functions` method:

```c
if( strstr(line, "\"list_functions\"") ) {
    /* Build list of all supported function names */
    snprintf(response, sizeof(response),
        "{\"functions\":[\"TA_SMA\",\"TA_RSI\",...]}");
    printf("%s\n", response);
    fflush(stdout);
    continue;
}
```

Actually, this should be generated from the `funcs` slice — iterate `funcs` and build the JSON array of `"TA_{name}"` strings.

- [ ] **Step 4: Add `list_functions` to Java, .NET, Python servers**

Same pattern: check for `list_functions` method before dispatch, return JSON array of supported function names.

- [ ] **Step 5: Add `set_unstable_period` method to all servers**

```json
{"method": "set_unstable_period", "params": {"funcId": 21, "period": 10}}
```

Server sets the global unstable period for the given function ID. In C: calls generated equivalent. In other languages: sets on the Core/state object.

- [ ] **Step 6: Run ta_codegen tests, then generate and build servers**

```bash
cd tools/ta_codegen && cargo test
cd tools/ta_codegen && cargo run -- generate-servers
cd tools/ta_codegen && cargo run -- build
```

Verify at least the C server compiles and starts.

- [ ] **Step 7: Commit**

```bash
git add tools/ta_codegen/src/server_gen.rs
git commit -m "feat(server_gen): add timing_ns, list_functions, set_unstable_period to all servers"
```

---

## Chunk 2: Generic Codegen Test Callback (Phase 1)

### Task 6: Generalize `CodegenRangeTestParam` struct

**Files:**
- Modify: `src/tools/ta_regtest/test_codegen.c` (~line 126-145)

- [ ] **Step 1: Read the current `CodegenRangeTestParam` and understand all fields**

Current fields: `inReal`, `inReal1`, `nbBars`, `optInTimePeriod`, `unstId`, `cp`, `requestBuf`, `responseBuf`, `codegenError`, `methodName`.

- [ ] **Step 2: Rewrite the struct to be generic**

```c
typedef struct {
    /* ta_abstract function metadata */
    const TA_FuncInfo *funcInfo;
    TA_ParamHolder *paramHolder;

    /* Input data (from TA_History) */
    const TA_History *history;
    int nbBars;

    /* Output buffers (pre-allocated, for ALL outputs at once) */
    TA_Real    *outRealBufs[3];     /* Up to 3 real outputs */
    TA_Integer *outIntBufs[3];      /* Up to 3 integer outputs */
    int         outputIsInteger[3]; /* Flag per output */
    int         nbRealOutputs;
    int         nbIntOutputs;

    /* Codegen pipe */
    CodegenPipe *cp;
    char *requestBuf;
    char *responseBuf;

    /* Error tracking */
    ErrorNumber codegenError;

    /* Timing accumulators */
    long long c_ref_total_ns;
    long long server_total_ns;
    int       timing_count;
} CodegenRangeTestParam;
```

- [ ] **Step 3: Verify compilation**

```bash
cd cmake-build && cmake .. -DCMAKE_BUILD_TYPE=Release && make ta_regtest -j4
```

This will fail because the old callbacks reference the old fields. That's expected — we'll fix in subsequent tasks.

- [ ] **Step 4: Commit (WIP, compilation may be broken temporarily)**

```bash
git add src/tools/ta_regtest/test_codegen.c
git commit -m "refactor(test_codegen): generalize CodegenRangeTestParam for ta_abstract"
```

---

### Task 7: Write generic JSON request builder

**Files:**
- Modify: `src/tools/ta_regtest/test_codegen.c`

- [ ] **Step 1: Write `build_json_request()` function**

This function uses `ta_abstract` metadata to build a JSON-RPC request for any function:

```c
static int build_json_request(
    CodegenRangeTestParam *p,
    TA_Integer startIdx,
    TA_Integer endIdx)
{
    char *buf = p->requestBuf;
    int pos = 0;

    /* Method name: "TA_SMA", "TA_RSI", etc. */
    pos += snprintf(buf + pos, JSON_BUF_SIZE - pos,
        "{\"method\":\"TA_%s\",\"params\":{\"startIdx\":%d,\"endIdx\":%d",
        p->funcInfo->name, (int)startIdx, (int)endIdx);

    /* Inputs */
    const TA_FuncHandle *handle = p->funcInfo->handle;
    for( unsigned int i = 0; i < p->funcInfo->nbInput; i++ )
    {
        const TA_InputParameterInfo *inputInfo;
        TA_GetInputParameterInfo(handle, i, &inputInfo);

        if( inputInfo->type == TA_Input_Price )
        {
            /* Named OHLCV arrays based on flags */
            if( inputInfo->flags & TA_IN_PRICE_OPEN )
            {
                pos += snprintf(buf + pos, JSON_BUF_SIZE - pos, ",\"inOpen\":");
                pos += json_write_double_array(buf + pos, JSON_BUF_SIZE - pos,
                    p->history->open, p->nbBars);
            }
            if( inputInfo->flags & TA_IN_PRICE_HIGH )
            {
                pos += snprintf(buf + pos, JSON_BUF_SIZE - pos, ",\"inHigh\":");
                pos += json_write_double_array(buf + pos, JSON_BUF_SIZE - pos,
                    p->history->high, p->nbBars);
            }
            if( inputInfo->flags & TA_IN_PRICE_LOW )
            {
                pos += snprintf(buf + pos, JSON_BUF_SIZE - pos, ",\"inLow\":");
                pos += json_write_double_array(buf + pos, JSON_BUF_SIZE - pos,
                    p->history->low, p->nbBars);
            }
            if( inputInfo->flags & TA_IN_PRICE_CLOSE )
            {
                pos += snprintf(buf + pos, JSON_BUF_SIZE - pos, ",\"inClose\":");
                pos += json_write_double_array(buf + pos, JSON_BUF_SIZE - pos,
                    p->history->close, p->nbBars);
            }
            if( inputInfo->flags & TA_IN_PRICE_VOLUME )
            {
                pos += snprintf(buf + pos, JSON_BUF_SIZE - pos, ",\"inVolume\":");
                pos += json_write_double_array(buf + pos, JSON_BUF_SIZE - pos,
                    p->history->volume, p->nbBars);
            }
        }
        else if( inputInfo->type == TA_Input_Real )
        {
            /* For single real input: "inReal", for multiple: "inReal0", "inReal1" */
            if( p->funcInfo->nbInput == 1 || (i == 0 && p->funcInfo->nbInput > 1) )
            {
                const char *name = (p->funcInfo->nbInput == 1) ? "inReal" : "inReal0";
                pos += snprintf(buf + pos, JSON_BUF_SIZE - pos, ",\"%s\":", name);
                pos += json_write_double_array(buf + pos, JSON_BUF_SIZE - pos,
                    p->history->close, p->nbBars);
            }
            else
            {
                pos += snprintf(buf + pos, JSON_BUF_SIZE - pos, ",\"inReal%d\":", i);
                pos += json_write_double_array(buf + pos, JSON_BUF_SIZE - pos,
                    p->history->volume, p->nbBars);
            }
        }
    }

    /* Optional params */
    for( unsigned int i = 0; i < p->funcInfo->nbOptInput; i++ )
    {
        const TA_OptInputParameterInfo *optInfo;
        TA_GetOptInputParameterInfo(handle, i, &optInfo);

        pos += snprintf(buf + pos, JSON_BUF_SIZE - pos, ",\"%s\":", optInfo->paramName);

        if( optInfo->type == TA_OptInput_RealRange || optInfo->type == TA_OptInput_RealList )
        {
            pos += snprintf(buf + pos, JSON_BUF_SIZE - pos, "%.15g",
                optInfo->defaultValue);
        }
        else
        {
            pos += snprintf(buf + pos, JSON_BUF_SIZE - pos, "%d",
                (int)optInfo->defaultValue);
        }
    }

    /* Unstable period (if applicable) */
    if( p->funcInfo->flags & TA_FUNC_FLG_UNST_PER )
    {
        TA_Integer unstPeriod;
        TA_GetUnstablePeriod(/* funcInfo unstable ID */, &unstPeriod);
        pos += snprintf(buf + pos, JSON_BUF_SIZE - pos,
            ",\"unstablePeriod\":%d", (int)unstPeriod);
    }

    pos += snprintf(buf + pos, JSON_BUF_SIZE - pos, "}}");
    return pos;
}
```

**NOTE:** The exact field names for `TA_OptInputParameterInfo` (`.paramName`, `.defaultValue`, `.type`) must be verified against `include/ta_abstract.h`. The names above are estimates — read the actual struct definition.

**NOTE:** The unstable period function ID lookup needs to map from `funcInfo->name` to `TA_FuncUnstId` enum. Check if `TA_FuncInfo` has an unstable ID field, or if you need a name→ID mapping function.

- [ ] **Step 2: Verify it compiles**

```bash
cd cmake-build && make ta_regtest -j4
```

- [ ] **Step 3: Commit**

```bash
git add src/tools/ta_regtest/test_codegen.c
git commit -m "feat(test_codegen): add generic JSON request builder using ta_abstract"
```

---

### Task 8: Write generic doRangeTest callback

**Files:**
- Modify: `src/tools/ta_regtest/test_codegen.c`

- [ ] **Step 1: Write `codegen_range_generic()` callback**

This replaces all 3 hand-coded callbacks:

```c
static TA_RetCode codegen_range_generic(
    TA_Integer startIdx, TA_Integer endIdx,
    TA_Real *outputBuffer, TA_Integer *outputBufferInt,
    TA_Integer *outBegIdx, TA_Integer *outNbElement,
    TA_Integer *lookback, void *opaqueData,
    unsigned int outputNb, unsigned int *isOutputInteger)
{
    CodegenRangeTestParam *p = (CodegenRangeTestParam *)opaqueData;

    /* 1. Call C reference via ta_abstract */
    struct timespec ts_start, ts_end;
    clock_gettime(CLOCK_MONOTONIC, &ts_start);

    TA_RetCode retCode = TA_CallFunc(p->paramHolder,
                                      startIdx, endIdx,
                                      outBegIdx, outNbElement);

    clock_gettime(CLOCK_MONOTONIC, &ts_end);
    p->c_ref_total_ns += (ts_end.tv_sec - ts_start.tv_sec) * 1000000000LL
                       + (ts_end.tv_nsec - ts_start.tv_nsec);
    p->timing_count++;

    /* 2. Get lookback */
    TA_GetLookback(p->paramHolder, lookback);

    /* 3. Determine if this output is integer */
    *isOutputInteger = p->outputIsInteger[outputNb];

    /* 4. Copy the requested output into the provided buffer */
    if( *isOutputInteger )
    {
        for( int i = 0; i < *outNbElement; i++ )
            outputBufferInt[i] = p->outIntBufs[outputNb][i];
    }
    else
    {
        for( int i = 0; i < *outNbElement; i++ )
            outputBuffer[i] = p->outRealBufs[outputNb][i];
    }

    /* 5. Build JSON-RPC request */
    build_json_request(p, startIdx, endIdx);

    /* 6. Send to server and compare */
    if( p->codegenError == TA_TEST_PASS )
    {
        ErrorNumber errNb = codegen_pipe_call(p->cp, p->requestBuf,
                                              p->responseBuf, JSON_BUF_SIZE);
        if( errNb != TA_TEST_PASS )
        {
            p->codegenError = errNb;
        }
        else if( !json_is_error(p->responseBuf) )
        {
            /* Parse server timing */
            int server_ns = json_get_int(p->responseBuf, "timing_ns");
            p->server_total_ns += server_ns;

            /* Compare outputs */
            compare_codegen_output_generic(p, retCode, *outBegIdx, *outNbElement);
        }
        /* else: server returned error = unsupported function, skip silently */
    }

    return retCode;
}
```

**CRITICAL:** `TA_CallFunc` fills ALL output buffers that were set on the `paramHolder` via `TA_SetOutputParamRealPtr` / `TA_SetOutputParamIntegerPtr`. The callback is called once per `outputNb` by `doRangeTest`. On the FIRST call (outputNb==0), `TA_CallFunc` fills everything. On subsequent calls (outputNb==1, 2), we just copy from the pre-filled buffers. So the `TA_CallFunc` must only be called when `outputNb == 0`, and subsequent calls just copy.

Update the callback to handle this:

```c
    /* Only call C reference on first output pass */
    if( outputNb == 0 )
    {
        TA_RetCode retCode = TA_CallFunc(p->paramHolder, startIdx, endIdx,
                                          outBegIdx, outNbElement);
        p->lastRetCode = retCode;
        p->lastBegIdx = *outBegIdx;
        p->lastNbElement = *outNbElement;
        /* ... timing ... */
    }
    else
    {
        *outBegIdx = p->lastBegIdx;
        *outNbElement = p->lastNbElement;
    }
```

Actually — verify this assumption by reading `doRangeTest` in `ta_test_priv.h` / the implementation. It may call the callback once per range and expect ALL outputs, or it may call per-output. This is critical to get right.

- [ ] **Step 2: Write `compare_codegen_output_generic()` function**

Update the existing `compare_codegen_output` to handle multiple output types:

```c
static void compare_codegen_output_generic(
    CodegenRangeTestParam *p,
    TA_RetCode c_retCode,
    TA_Integer c_begIdx,
    TA_Integer c_nbElement)
{
    if( p->codegenError != TA_TEST_PASS ) return;

    int cg_retCode = json_get_int(p->responseBuf, "retCode");
    if( (int)c_retCode != cg_retCode )
    {
        printf("CODEGEN MISMATCH [TA_%s]: retCode C=%d codegen=%d\n",
               p->funcInfo->name, (int)c_retCode, cg_retCode);
        p->codegenError = TA_CODEGEN_RETCODE_MISMATCH;
        return;
    }
    if( c_retCode != TA_SUCCESS ) return;
    if( c_nbElement == 0 ) return;

    int cg_begIdx = json_get_int(p->responseBuf, "outBegIdx");
    if( c_begIdx != cg_begIdx )
    {
        printf("CODEGEN MISMATCH [TA_%s]: outBegIdx C=%d codegen=%d\n",
               p->funcInfo->name, (int)c_begIdx, cg_begIdx);
        p->codegenError = TA_CODEGEN_BEGIDX_MISMATCH;
        return;
    }

    int cg_nbElement = json_get_int(p->responseBuf, "outNBElement");
    if( c_nbElement != cg_nbElement )
    {
        printf("CODEGEN MISMATCH [TA_%s]: outNBElement C=%d codegen=%d\n",
               p->funcInfo->name, (int)c_nbElement, cg_nbElement);
        p->codegenError = TA_CODEGEN_NBELEMENT_MISMATCH;
        return;
    }

    /* Compare each real output */
    TA_Real cg_out[MAX_NB_TEST_ELEMENT];
    for( int outIdx = 0; outIdx < p->nbRealOutputs; outIdx++ )
    {
        const char *fieldName = (outIdx == 0) ? "outReal" :
                                (outIdx == 1) ? "outReal1" : "outReal2";
        int parsed = json_get_double_array(p->responseBuf, fieldName,
                                            cg_out, MAX_NB_TEST_ELEMENT);
        for( int i = 0; i < c_nbElement && i < parsed; i++ )
        {
            double diff = fabs(p->outRealBufs[outIdx][i] - cg_out[i]);
            if( diff > CODEGEN_EPSILON )
            {
                printf("CODEGEN MISMATCH [TA_%s]: %s[%d] C=%.10f codegen=%.10f diff=%.2e\n",
                       p->funcInfo->name, fieldName, i,
                       p->outRealBufs[outIdx][i], cg_out[i], diff);
                p->codegenError = TA_CODEGEN_OUTPUT_MISMATCH;
                return;
            }
        }
    }

    /* Compare each integer output */
    TA_Integer cg_int_out[MAX_NB_TEST_ELEMENT];
    for( int outIdx = 0; outIdx < p->nbIntOutputs; outIdx++ )
    {
        const char *fieldName = (outIdx == 0) ? "outInteger" : "outInteger1";
        /* Parse integer array from JSON */
        /* ... similar to json_get_double_array but for ints ... */
    }
}
```

- [ ] **Step 3: Verify compilation**

```bash
cd cmake-build && make ta_regtest -j4
```

- [ ] **Step 4: Commit**

```bash
git add src/tools/ta_regtest/test_codegen.c
git commit -m "feat(test_codegen): add generic doRangeTest callback and output comparator"
```

---

### Task 9: Wire up ta_abstract enumeration and test orchestration

**Files:**
- Modify: `src/tools/ta_regtest/test_codegen.c` — `test_codegen_for_language()`

- [ ] **Step 1: Replace static `CODEGEN_TESTS[]` with `TA_ForEachFunc` enumeration**

Rewrite `test_codegen_for_language()` to:
1. Call `list_functions` on the server to get supported function list
2. Use `TA_ForEachFunc` to iterate all ta_abstract functions
3. For each function, check if server supports it (skip if not)
4. Allocate `TA_ParamHolder`, populate inputs from `TA_History`, set defaults
5. Run `doRangeTest` with `codegen_range_generic` callback
6. Collect pass/fail/skip results

```c
/* Callback context for TA_ForEachFunc */
typedef struct {
    const CodegenLanguage *lang;
    const TA_History *history;
    CodegenPipe *cp;
    char *requestBuf;
    char *responseBuf;
    const char **supportedFuncs;
    int numSupported;
    int passed;
    int failed;
    int skipped;
    ErrorNumber firstError;
} CodegenForEachCtx;

static void test_one_function(const TA_FuncInfo *funcInfo, void *opaqueData)
{
    CodegenForEachCtx *ctx = (CodegenForEachCtx *)opaqueData;
    if( ctx->firstError != TA_TEST_PASS ) return;

    /* Check if server supports this function */
    char methodName[128];
    snprintf(methodName, sizeof(methodName), "TA_%s", funcInfo->name);
    if( !is_function_supported(methodName, ctx->supportedFuncs, ctx->numSupported) )
    {
        printf("  %-40s SKIP\n", funcInfo->name);
        ctx->skipped++;
        return;
    }

    printf("  %-40s ", funcInfo->name);
    fflush(stdout);

    /* Allocate param holder */
    TA_ParamHolder *paramHolder;
    TA_ParamHolderAlloc(funcInfo->handle, &paramHolder);

    /* Set inputs from history (see Task 7 patterns) */
    setup_inputs(paramHolder, funcInfo, ctx->history);

    /* Set optional params to defaults */
    setup_opt_defaults(paramHolder, funcInfo);

    /* Allocate and set output buffers */
    CodegenRangeTestParam params;
    memset(&params, 0, sizeof(params));
    params.funcInfo = funcInfo;
    params.paramHolder = paramHolder;
    params.history = ctx->history;
    params.nbBars = (int)ctx->history->nbBars;
    params.cp = ctx->cp;
    params.requestBuf = ctx->requestBuf;
    params.responseBuf = ctx->responseBuf;
    params.codegenError = TA_TEST_PASS;

    setup_outputs(&params, funcInfo, paramHolder);

    /* Run doRangeTest */
    ErrorNumber errNb = doRangeTest(
        codegen_range_generic,
        TA_FUNC_UNST_NONE, /* unstable ID handled inside callback */
        (void *)&params,
        funcInfo->nbOutput,
        0 /* integer tolerance */);

    /* Report result */
    if( params.codegenError != TA_TEST_PASS )
    {
        printf("CODEGEN FAILED\n");
        ctx->failed++;
    }
    else if( errNb != TA_TEST_PASS )
    {
        printf("RANGE FAILED\n");
        ctx->failed++;
    }
    else
    {
        double c_avg_us = (params.timing_count > 0)
            ? (double)params.c_ref_total_ns / params.timing_count / 1000.0 : 0;
        double s_avg_us = (params.timing_count > 0)
            ? (double)params.server_total_ns / params.timing_count / 1000.0 : 0;
        printf("PASS   (C: %.1fus, %s: %.1fus)\n",
               c_avg_us, ctx->lang->display, s_avg_us);
        ctx->passed++;
    }

    /* Cleanup */
    free_outputs(&params);
    TA_ParamHolderFree(paramHolder);
}
```

- [ ] **Step 2: Write `setup_inputs()` helper**

```c
static void setup_inputs(TA_ParamHolder *params, const TA_FuncInfo *funcInfo,
                          const TA_History *history)
{
    for( unsigned int i = 0; i < funcInfo->nbInput; i++ )
    {
        const TA_InputParameterInfo *inputInfo;
        TA_GetInputParameterInfo(funcInfo->handle, i, &inputInfo);

        if( inputInfo->type == TA_Input_Price )
        {
            TA_SetInputParamPricePtr(params, i,
                (inputInfo->flags & TA_IN_PRICE_OPEN)   ? history->open   : NULL,
                (inputInfo->flags & TA_IN_PRICE_HIGH)   ? history->high   : NULL,
                (inputInfo->flags & TA_IN_PRICE_LOW)    ? history->low    : NULL,
                (inputInfo->flags & TA_IN_PRICE_CLOSE)  ? history->close  : NULL,
                (inputInfo->flags & TA_IN_PRICE_VOLUME) ? history->volume : NULL,
                NULL /* openInterest */);
        }
        else if( inputInfo->type == TA_Input_Real )
        {
            /* Use close for first, volume for second */
            const TA_Real *data = (i == 0) ? history->close : history->volume;
            TA_SetInputParamRealPtr(params, i, data);
        }
        else if( inputInfo->type == TA_Input_Integer )
        {
            /* Integer inputs are rare — allocate a temp buffer of zeros */
            /* or skip these functions for now */
        }
    }
}
```

- [ ] **Step 3: Write `setup_opt_defaults()` helper**

```c
static void setup_opt_defaults(TA_ParamHolder *params, const TA_FuncInfo *funcInfo)
{
    for( unsigned int i = 0; i < funcInfo->nbOptInput; i++ )
    {
        const TA_OptInputParameterInfo *optInfo;
        TA_GetOptInputParameterInfo(funcInfo->handle, i, &optInfo);

        if( optInfo->type == TA_OptInput_RealRange || optInfo->type == TA_OptInput_RealList )
        {
            TA_SetOptInputParamReal(params, i, optInfo->defaultValue);
        }
        else
        {
            TA_SetOptInputParamInteger(params, i, (TA_Integer)optInfo->defaultValue);
        }
    }
}
```

- [ ] **Step 4: Write `setup_outputs()` and `free_outputs()` helpers**

```c
static void setup_outputs(CodegenRangeTestParam *p, const TA_FuncInfo *funcInfo,
                           TA_ParamHolder *paramHolder)
{
    int realIdx = 0, intIdx = 0;
    for( unsigned int i = 0; i < funcInfo->nbOutput; i++ )
    {
        const TA_OutputParameterInfo *outInfo;
        TA_GetOutputParameterInfo(funcInfo->handle, i, &outInfo);

        if( outInfo->type == TA_Output_Real )
        {
            p->outRealBufs[realIdx] = calloc(MAX_NB_TEST_ELEMENT, sizeof(TA_Real));
            TA_SetOutputParamRealPtr(paramHolder, i, p->outRealBufs[realIdx]);
            p->outputIsInteger[i] = 0;
            realIdx++;
        }
        else
        {
            p->outIntBufs[intIdx] = calloc(MAX_NB_TEST_ELEMENT, sizeof(TA_Integer));
            TA_SetOutputParamIntegerPtr(paramHolder, i, p->outIntBufs[intIdx]);
            p->outputIsInteger[i] = 1;
            intIdx++;
        }
    }
    p->nbRealOutputs = realIdx;
    p->nbIntOutputs = intIdx;
}

static void free_outputs(CodegenRangeTestParam *p)
{
    for( int i = 0; i < 3; i++ )
    {
        free(p->outRealBufs[i]);
        free(p->outIntBufs[i]);
    }
}
```

- [ ] **Step 5: Write `parse_list_functions()` to query server**

```c
static int parse_list_functions(CodegenPipe *cp, char *reqBuf, char *resBuf,
                                 const char ***outFuncs)
{
    snprintf(reqBuf, JSON_BUF_SIZE, "{\"method\":\"list_functions\"}");
    ErrorNumber err = codegen_pipe_call(cp, reqBuf, resBuf, JSON_BUF_SIZE);
    if( err != TA_TEST_PASS ) return 0;

    /* Parse "functions":["TA_SMA","TA_RSI",...] */
    /* Return array of function name strings and count */
    /* ... JSON array parsing ... */
}
```

- [ ] **Step 6: Build and run against existing 3 functions only (C server)**

```bash
cd cmake-build && cmake .. -DCMAKE_BUILD_TYPE=Release && make ta_regtest -j4
cd tools/ta_codegen && cargo run -- generate-servers --backend=c && cargo run -- build --backend=c
cd ../../bin && ./ta_regtest --codegen --language=c --function=SMA,MULT,RSI
```

Verify: SMA, MULT, RSI all PASS. Other functions SKIP (not supported by server).

- [ ] **Step 7: Delete the 3 old hand-coded callbacks and `CODEGEN_TESTS[]`**

Remove: `codegen_range_sma`, `codegen_range_mult`, `codegen_range_rsi`, `CODEGEN_TESTS[]` array.

- [ ] **Step 8: Commit**

```bash
git add src/tools/ta_regtest/test_codegen.c
git commit -m "feat(test_codegen): generic ta_abstract callback replaces hand-coded SMA/MULT/RSI"
```

---

## Chunk 3: Reporting, CLI Integration, and Cleanup

### Task 10: Add CLI flags to `ta_regtest.c`

**Files:**
- Modify: `src/tools/ta_regtest/ta_regtest.c` (~line 105-200, the main arg parsing)

- [ ] **Step 1: Read `ta_regtest.c` main and understand current flag parsing**

Current flags: `--function=CSV`, `--codegen` (may or may not exist), `-p`.

- [ ] **Step 2: Add `--codegen`, `--codegen-only`, `--language=` flags**

```c
/* In arg parsing loop: */
if( strcmp(argv[i], "--codegen") == 0 )
    runCodegen = 1;
else if( strcmp(argv[i], "--codegen-only") == 0 )
{
    runCodegen = 1;
    skipNormalTests = 1;
}
else if( strncmp(argv[i], "--language=", 11) == 0 )
    languageFilter = argv[i] + 11;
```

- [ ] **Step 3: Wire up `test_codegen()` call**

```c
/* After normal test suite (or instead of, if --codegen-only): */
if( runCodegen )
{
    errNb = test_codegen(&history, languageFilter, functionFilter);
    if( errNb != TA_TEST_PASS )
        return errNb;
}
```

- [ ] **Step 4: Build and test**

```bash
cd cmake-build && make ta_regtest -j4
cd ../bin && ./ta_regtest --codegen --language=c --function=SMA
```

- [ ] **Step 5: Commit**

```bash
git add src/tools/ta_regtest/ta_regtest.c
git commit -m "feat(ta_regtest): add --codegen, --codegen-only, --language CLI flags"
```

---

### Task 11: Add CLI summary with timing

**Files:**
- Modify: `src/tools/ta_regtest/test_codegen.c` — reporting in `test_one_function` and `test_codegen_for_language`

- [ ] **Step 1: The per-function line is already in Task 9**

The `printf("PASS   (C: %.1fus, %s: %.1fus)\n", ...)` is already there from Task 9. Verify the format matches the spec:

```
  SMA                  PASS   (C: 2.3us, Rust: 1.8us, 1.28x faster)
```

Add the speedup ratio:

```c
double ratio = (s_avg_us > 0) ? c_avg_us / s_avg_us : 0;
printf("PASS   (C: %.1fus, %s: %.1fus, %.2fx %s)\n",
       c_avg_us, ctx->lang->display, s_avg_us,
       (ratio >= 1.0) ? ratio : 1.0/ratio,
       (ratio >= 1.0) ? "faster" : "slower");
```

- [ ] **Step 2: Add per-language summary line**

At the end of `test_codegen_for_language`:

```c
printf("\n  %s: %d passed, %d failed, %d skipped\n",
       lang->display, ctx.passed, ctx.failed, ctx.skipped);
```

- [ ] **Step 3: Commit**

```bash
git add src/tools/ta_regtest/test_codegen.c
git commit -m "feat(test_codegen): CLI summary with timing and speedup ratio"
```

---

### Task 12: Add cross-language timing table

**Files:**
- Modify: `src/tools/ta_regtest/test_codegen.c` — add after all languages complete in `test_codegen()`

- [ ] **Step 1: Define timing results storage**

```c
#define MAX_FUNCTIONS 200
#define MAX_LANGS 5

typedef struct {
    char funcName[64];
    double c_ref_us;
    struct {
        int tested;      /* 0=skipped, 1=pass, -1=fail */
        double avg_us;
        char error[128]; /* If failed */
    } langs[MAX_LANGS];
} FuncTimingResult;

static FuncTimingResult g_timingResults[MAX_FUNCTIONS];
static int g_numTimingResults = 0;
```

- [ ] **Step 2: Populate timing results during `test_one_function`**

After each function completes, record results into `g_timingResults`.

- [ ] **Step 3: Print cross-language table in `test_codegen()`**

After all languages complete:

```c
printf("\n=============================================\n");
printf("Codegen Results + Timing (avg us/call)\n");
printf("=============================================\n");
printf("%-16s %8s", "Function", "C-ref");
for( int l = 0; l < numLangsTested; l++ )
    printf(" %8s", langNames[l]);
printf("\n");

for( int f = 0; f < g_numTimingResults; f++ )
{
    printf("%-16s %7.1f", g_timingResults[f].funcName, g_timingResults[f].c_ref_us);
    for( int l = 0; l < numLangsTested; l++ )
    {
        if( g_timingResults[f].langs[l].tested == 0 )
            printf(" %8s", "--");
        else if( g_timingResults[f].langs[l].tested < 0 )
            printf(" %8s", "FAIL");
        else
            printf(" %6.1fok", g_timingResults[f].langs[l].avg_us);
    }
    printf("\n");
}
```

- [ ] **Step 4: Build and test**

```bash
cd cmake-build && make ta_regtest -j4
cd ../bin && ./ta_regtest --codegen --language=c
```

- [ ] **Step 5: Commit**

```bash
git add src/tools/ta_regtest/test_codegen.c
git commit -m "feat(test_codegen): cross-language timing comparison table"
```

---

### Task 13: Add JSONL rolling report

**Files:**
- Modify: `src/tools/ta_regtest/test_codegen.c`

- [ ] **Step 1: Write `write_timing_report()` function**

```c
static void write_timing_report(const char *filepath)
{
    FILE *f = fopen(filepath, "a"); /* Append mode */
    if( !f ) return;

    /* Get git SHA */
    char gitSha[64] = "unknown";
    FILE *git = popen("git rev-parse --short HEAD 2>/dev/null", "r");
    if( git ) { fgets(gitSha, sizeof(gitSha), git); pclose(git); }
    /* Trim newline */
    char *nl = strchr(gitSha, '\n');
    if( nl ) *nl = '\0';

    /* Get timestamp */
    time_t now = time(NULL);
    char timestamp[64];
    strftime(timestamp, sizeof(timestamp), "%Y-%m-%dT%H:%M:%SZ", gmtime(&now));

    /* Write JSONL line */
    fprintf(f, "{\"timestamp\":\"%s\",\"git_sha\":\"%s\",\"results\":{", timestamp, gitSha);

    for( int i = 0; i < g_numTimingResults; i++ )
    {
        if( i > 0 ) fprintf(f, ",");
        fprintf(f, "\"%s\":{\"c_ref_ns\":%.0f",
                g_timingResults[i].funcName,
                g_timingResults[i].c_ref_us * 1000.0);

        for( int l = 0; l < MAX_LANGS; l++ )
        {
            if( g_timingResults[i].langs[l].tested == 0 ) continue;
            fprintf(f, ",\"%s\":{\"status\":\"%s\"",
                    /* lang name */"TODO",
                    g_timingResults[i].langs[l].tested > 0 ? "pass" : "fail");
            if( g_timingResults[i].langs[l].tested > 0 )
                fprintf(f, ",\"ns\":%.0f", g_timingResults[i].langs[l].avg_us * 1000.0);
            else
                fprintf(f, ",\"error\":\"%s\"", g_timingResults[i].langs[l].error);
            fprintf(f, "}");
        }
        fprintf(f, "}");
    }

    fprintf(f, "}}\n");
    fclose(f);
}
```

- [ ] **Step 2: Call at end of `test_codegen()`**

```c
write_timing_report("ta_regtest_timing.jsonl");
```

- [ ] **Step 3: Build, run, verify JSONL file is created**

```bash
cd cmake-build && make ta_regtest -j4
cd ../bin && ./ta_regtest --codegen --language=c --function=SMA
cat ta_regtest_timing.jsonl
```

- [ ] **Step 4: Commit**

```bash
git add src/tools/ta_regtest/test_codegen.c
git commit -m "feat(test_codegen): JSONL rolling report with git SHA and timing data"
```

---

### Task 14: Clean up legacy artifacts

**Files:**
- Remove: `rust/ffi/` (entire directory)
- Remove: `rust/tests/mult_test.rs`, `rust/tests/sma_test.rs`, `rust/tests/rsi_test.rs`
- Modify: `CMakeLists.txt` (remove `ta_regtest_rust` target and `ENABLE_RUST_REGTEST` flag)
- Modify: `rust/Cargo.toml` (remove ffi workspace member if listed)

**IMPORTANT:** Only do this AFTER verifying the generic callback passes for all 3 functions (SMA, MULT, RSI) against at least the C server. If the generic callback isn't fully validated yet, defer this task.

- [ ] **Step 1: Verify generic callback passes SMA, MULT, RSI**

```bash
cd bin && ./ta_regtest --codegen --language=c --function=SMA,MULT,RSI
```

All 3 must show PASS.

- [ ] **Step 2: Remove FFI crate**

```bash
rm -rf rust/ffi/
```

- [ ] **Step 3: Remove hand-written test files**

```bash
rm rust/tests/mult_test.rs rust/tests/sma_test.rs rust/tests/rsi_test.rs
```

Keep `rust/tests/float_test.rs`.

- [ ] **Step 4: Remove `ta_regtest_rust` from CMakeLists.txt**

Search for `ENABLE_RUST_REGTEST`, `ta_regtest_rust`, and remove all related blocks.

- [ ] **Step 5: Remove FFI-related functions from `gen_rust.c`**

Remove: `printRustFfiSingleWrapper`, `writeRustFfiGenerated`, `printRustFfiWrapperBody`, and any other FFI-specific generation code. Keep the Rust signature generation functions used by ta_codegen.

- [ ] **Step 6: Verify Rust crate still compiles**

```bash
cd rust && cargo check && cargo test
```

Only `float_test.rs` should remain and pass.

- [ ] **Step 7: Verify ta_regtest still builds and codegen tests pass**

```bash
cd cmake-build && cmake .. -DCMAKE_BUILD_TYPE=Release && make ta_regtest -j4
cd ../bin && ./ta_regtest --codegen --language=c --function=SMA,MULT,RSI
```

- [ ] **Step 8: Commit**

```bash
git add -A
git commit -m "chore: remove legacy FFI layer and hand-written Rust tests

Replaced by server-based cross-language testing architecture.
Kept: rust/tests/float_test.rs (TaFloat trait tests)"
```

---

### Task 15: Update CLAUDE.md files

**Files:**
- Modify: `CLAUDE.md` (root)
- Modify: `tools/ta_codegen/CLAUDE.md`
- Modify: `src/tools/ta_regtest/CLAUDE.md`

- [ ] **Step 1: Update root CLAUDE.md**

- Remove "Legacy Approaches" section (they're gone now)
- Update Quick Reference Commands to show `--codegen` usage
- Update Current Status section

- [ ] **Step 2: Update ta_codegen CLAUDE.md**

- Update "Current State" to reflect completed work
- Remove items from "Server Gen Gaps" that were fixed

- [ ] **Step 3: Update ta_regtest CLAUDE.md**

- Update to reflect generic callback is working
- Add `--codegen` / `--codegen-only` / `--language` flag docs

- [ ] **Step 4: Commit**

```bash
git add CLAUDE.md tools/ta_codegen/CLAUDE.md src/tools/ta_regtest/CLAUDE.md
git commit -m "docs: update CLAUDE.md files for universal regtest architecture"
```
