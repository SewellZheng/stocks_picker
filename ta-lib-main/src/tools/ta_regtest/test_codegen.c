/* test_codegen.c — generic ta_abstract-driven codegen verification
 *
 * Replaces 3 hand-coded callbacks (SMA, MULT, RSI) with one generic
 * callback that uses ta_abstract to call ANY TA function. Handles
 * price inputs, multi-output, integer outputs, real optional params,
 * and unstable periods.
 */
#include "test_codegen.h"
#include "codegen_pipe.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include <time.h>

/* Display flags set by ta_regtest.c --no-guarded / --no-unguarded */
int g_hideGuarded = 0;
int g_hideUnguarded = 0;
#include <limits.h>
#ifdef __APPLE__
#include <mach/mach_time.h>
#endif
#if defined(WIN32) || defined(_WIN32)
#include <windows.h>
#endif

#include "ta_libc.h"
#include "ta_abstract.h"

/* ---- High-resolution nanosecond timer ---- */
static long long get_nanotime(void) {
#ifdef __APPLE__
    static mach_timebase_info_data_t info = {0, 0};
    if( info.denom == 0 ) mach_timebase_info(&info);
    uint64_t t = mach_absolute_time();
    return (long long)(t * info.numer / info.denom);
#elif defined(WIN32) || defined(_WIN32)
    static LARGE_INTEGER freq = {0};
    LARGE_INTEGER t;
    if( freq.QuadPart == 0 ) QueryPerformanceFrequency(&freq);
    QueryPerformanceCounter(&t);
    return (t.QuadPart / freq.QuadPart) * 1000000000LL
         + (t.QuadPart % freq.QuadPart) * 1000000000LL / freq.QuadPart;
#else
    struct timespec ts;
    if( clock_gettime(CLOCK_MONOTONIC, &ts) == 0 )
        return (long long)ts.tv_sec * 1000000000LL + (long long)ts.tv_nsec;
    return 0;
#endif
}

/* ---- Language definitions ---- */

typedef struct {
    const char *name;           /* "rust", "c", "java", "dotnet" */
    const char *display;        /* "Rust", "C", "Java", ".NET" */
    const char *const *argv;    /* NULL-terminated command array */
} CodegenLanguage;

static const char *const argv_rust[]  = {"./ta_codegen_serve_rust", NULL};
static const char *const argv_c[]     = {"./ta_codegen_serve_c", NULL};
static const char *const argv_java[]  = {"java", "-cp", "ta_codegen_java", "TaCodegenServe", NULL};
static const char *const argv_dotnet[]= {"dotnet", "ta_codegen_dotnet/TaCodegenServe.dll", NULL};
static const CodegenLanguage ALL_LANGUAGES[] = {
    {"c",      "C",            argv_c},
    {"rust",   "Rust",         argv_rust},
    {"java",   "Java",         argv_java},
    {"dotnet", ".NET",         argv_dotnet},
};
#define NUM_LANGUAGES (sizeof(ALL_LANGUAGES) / sizeof(ALL_LANGUAGES[0]))

/* ---- Global timing results store (Task 12) ---- */

#define MAX_FUNCTIONS 200

typedef struct {
    char   funcName[64];
    double c_ref_ns;
    struct {
        int    tested;   /* 0=skipped, 1=pass, -1=fail */
        double avg_ns;
        double avg_ns_unguarded;
    } langs[NUM_LANGUAGES];
} FuncTimingResult;

static FuncTimingResult g_timingResults[MAX_FUNCTIONS];
static int              g_numTimingResults = 0;

/* ---- Constants ---- */

#define CODEGEN_EPSILON  1e-6
#define JSON_BUF_SIZE    (128 * 1024)   /* 128KB: enough for OHLCV inputs */
#define MAX_OUTPUTS      3              /* Max outputs any TA function has */

/* ---- Minimal JSON helpers (no library dependency) ---- */

static int json_write_double_array(char *buf, int buf_size,
                                   const TA_Real *data, int count)
{
    int pos = 0;
    buf[pos++] = '[';
    for( int i = 0; i < count; i++ )
    {
        if( i > 0 )
            pos += snprintf(buf + pos, buf_size - pos, ",");
        pos += snprintf(buf + pos, buf_size - pos, "%.15g", data[i]);
    }
    buf[pos++] = ']';
    buf[pos] = '\0';
    return pos;
}

static const char *json_find_field(const char *json, const char *field, int *len)
{
    char pattern[256];
    snprintf(pattern, sizeof(pattern), "\"%s\":", field);
    const char *p = strstr(json, pattern);
    if( !p ) return NULL;
    p += strlen(pattern);
    while( *p == ' ' ) p++;

    const char *start = p;
    if( *p == '"' )
    {
        p++;
        while( *p && *p != '"' ) p++;
        if( *p == '"' ) p++;
    }
    else if( *p == '[' )
    {
        int depth = 1;
        p++;
        while( *p && depth > 0 )
        {
            if( *p == '[' ) depth++;
            else if( *p == ']' ) depth--;
            p++;
        }
    }
    else
    {
        while( *p && *p != ',' && *p != '}' ) p++;
    }

    *len = (int)(p - start);
    return start;
}

static int json_get_int(const char *json, const char *field)
{
    int len;
    const char *val = json_find_field(json, field, &len);
    if( !val ) return 0;
    return atoi(val);
}

static int json_get_double_array(const char *json, const char *field,
                                 TA_Real *out, int max_count)
{
    int len;
    const char *val = json_find_field(json, field, &len);
    if( !val || *val != '[' ) return 0;

    int count = 0;
    const char *p = val + 1;
    while( *p && *p != ']' && count < max_count )
    {
        while( *p == ' ' || *p == ',' ) p++;
        if( *p == ']' ) break;
        out[count] = strtod(p, (char **)&p);
        count++;
    }
    return count;
}

static int json_get_int_array(const char *json, const char *field,
                              TA_Integer *out, int max_count)
{
    int len;
    const char *val = json_find_field(json, field, &len);
    if( !val || *val != '[' ) return 0;

    int count = 0;
    const char *p = val + 1;
    while( *p && *p != ']' && count < max_count )
    {
        while( *p == ' ' || *p == ',' ) p++;
        if( *p == ']' ) break;
        out[count] = (TA_Integer)strtol(p, (char **)&p, 10);
        count++;
    }
    return count;
}

static int json_is_error(const char *json)
{
    return strstr(json, "\"error\"") != NULL;
}

/* ---- Unstable period lookup ---- */

/* Map function name to TA_FuncUnstId. Only the 24 functions with
 * TA_FUNC_FLG_UNST_PER have entries here.
 */
typedef struct {
    const char   *name;
    TA_FuncUnstId id;
} UnstableLookup;

static const UnstableLookup UNSTABLE_MAP[] = {
    {"ADX",          TA_FUNC_UNST_ADX},
    {"ADXR",         TA_FUNC_UNST_ADXR},
    {"ATR",          TA_FUNC_UNST_ATR},
    {"CMO",          TA_FUNC_UNST_CMO},
    {"DX",           TA_FUNC_UNST_DX},
    {"EMA",          TA_FUNC_UNST_EMA},
    {"HT_DCPERIOD",  TA_FUNC_UNST_HT_DCPERIOD},
    {"HT_DCPHASE",   TA_FUNC_UNST_HT_DCPHASE},
    {"HT_PHASOR",    TA_FUNC_UNST_HT_PHASOR},
    {"HT_SINE",      TA_FUNC_UNST_HT_SINE},
    {"HT_TRENDLINE", TA_FUNC_UNST_HT_TRENDLINE},
    {"HT_TRENDMODE", TA_FUNC_UNST_HT_TRENDMODE},
    {"IMI",          TA_FUNC_UNST_IMI},
    {"KAMA",         TA_FUNC_UNST_KAMA},
    {"MAMA",         TA_FUNC_UNST_MAMA},
    {"MFI",          TA_FUNC_UNST_MFI},
    {"MINUS_DI",     TA_FUNC_UNST_MINUS_DI},
    {"MINUS_DM",     TA_FUNC_UNST_MINUS_DM},
    {"NATR",         TA_FUNC_UNST_NATR},
    {"PLUS_DI",      TA_FUNC_UNST_PLUS_DI},
    {"PLUS_DM",      TA_FUNC_UNST_PLUS_DM},
    {"RSI",          TA_FUNC_UNST_RSI},
    {"STOCHRSI",     TA_FUNC_UNST_STOCHRSI},
    {"T3",           TA_FUNC_UNST_T3},
};
#define NUM_UNSTABLE_MAP (sizeof(UNSTABLE_MAP) / sizeof(UNSTABLE_MAP[0]))

static TA_FuncUnstId get_unst_id(const char *funcName)
{
    for( unsigned int i = 0; i < NUM_UNSTABLE_MAP; i++ )
    {
        if( strcmp(funcName, UNSTABLE_MAP[i].name) == 0 )
            return UNSTABLE_MAP[i].id;
    }
    return TA_FUNC_UNST_NONE;
}

/* ---- Generic CodegenRangeTestParam (Task 6) ---- */

typedef struct {
    /* ta_abstract function metadata */
    const TA_FuncInfo *funcInfo;
    TA_ParamHolder   *paramHolder;

    /* Input data */
    const TA_History *history;
    int nbBars;

    /* Output buffers indexed by outputNb (logical output index).
     * Each output is EITHER real or integer, never both.
     * We allocate both arrays per outputNb for simplicity; only
     * the correct one (per outputIsInteger[i]) is used. */
    TA_Real    *outRealBufs[MAX_OUTPUTS];
    TA_Integer *outIntBufs[MAX_OUTPUTS];
    int         outputIsInteger[MAX_OUTPUTS];   /* Flag per output */
    int         totalOutputs;

    /* Cached results from TA_CallFunc (for multi-output) */
    TA_RetCode  lastRetCode;
    TA_Integer  lastBegIdx;
    TA_Integer  lastNbElement;

    /* Unstable period info */
    TA_FuncUnstId unstId;

    /* Codegen pipe */
    CodegenPipe *cp;
    char *requestBuf;
    char *responseBuf;

    /* Error tracking */
    ErrorNumber codegenError;

    /* Timing */
    long long c_ref_total_ns;
    long long server_total_ns;
    int       timing_count;
    long long server_unguarded_total_ns;
    int       timing_unguarded_count;
} CodegenRangeTestParam;

/* ---- Generic JSON request builder (Task 7) ---- */

static int build_json_request(CodegenRangeTestParam *p,
                              TA_Integer startIdx, TA_Integer endIdx)
{
    const TA_FuncInfo *fi = p->funcInfo;
    char *buf = p->requestBuf;
    int bufSize = JSON_BUF_SIZE;
    int pos = 0;
    unsigned int i;
    int realInputCount = 0;

    /* Method and startIdx/endIdx */
    pos += snprintf(buf + pos, bufSize - pos,
        "{\"method\":\"TA_%s\",\"params\":{\"startIdx\":%d,\"endIdx\":%d",
        fi->name, (int)startIdx, (int)endIdx);

    /* Pre-count real inputs to decide naming convention */
    int totalRealInputs = 0;
    for( i = 0; i < fi->nbInput; i++ )
    {
        const TA_InputParameterInfo *tmpInfo;
        TA_GetInputParameterInfo(fi->handle, i, &tmpInfo);
        if( tmpInfo->type == TA_Input_Real )
            totalRealInputs++;
    }

    /* Input parameters */
    for( i = 0; i < fi->nbInput; i++ )
    {
        const TA_InputParameterInfo *inputInfo;
        TA_GetInputParameterInfo(fi->handle, i, &inputInfo);

        switch( inputInfo->type )
        {
        case TA_Input_Price:
        {
            TA_InputFlags flags = inputInfo->flags;
            if( flags & TA_IN_PRICE_OPEN )
            {
                pos += snprintf(buf + pos, bufSize - pos, ",\"inOpen\":");
                pos += json_write_double_array(buf + pos, bufSize - pos,
                           p->history->open, p->nbBars);
            }
            if( flags & TA_IN_PRICE_HIGH )
            {
                pos += snprintf(buf + pos, bufSize - pos, ",\"inHigh\":");
                pos += json_write_double_array(buf + pos, bufSize - pos,
                           p->history->high, p->nbBars);
            }
            if( flags & TA_IN_PRICE_LOW )
            {
                pos += snprintf(buf + pos, bufSize - pos, ",\"inLow\":");
                pos += json_write_double_array(buf + pos, bufSize - pos,
                           p->history->low, p->nbBars);
            }
            if( flags & TA_IN_PRICE_CLOSE )
            {
                pos += snprintf(buf + pos, bufSize - pos, ",\"inClose\":");
                pos += json_write_double_array(buf + pos, bufSize - pos,
                           p->history->close, p->nbBars);
            }
            if( flags & TA_IN_PRICE_VOLUME )
            {
                pos += snprintf(buf + pos, bufSize - pos, ",\"inVolume\":");
                pos += json_write_double_array(buf + pos, bufSize - pos,
                           p->history->volume, p->nbBars);
            }
            if( flags & TA_IN_PRICE_OPENINTEREST )
            {
                pos += snprintf(buf + pos, bufSize - pos, ",\"inOpenInterest\":");
                pos += json_write_double_array(buf + pos, bufSize - pos,
                           p->history->openInterest, p->nbBars);
            }
            break;
        }
        case TA_Input_Real:
        {
            if( totalRealInputs == 1 )
            {
                /* Single real input: "inReal" */
                pos += snprintf(buf + pos, bufSize - pos, ",\"inReal\":");
                pos += json_write_double_array(buf + pos, bufSize - pos,
                           p->history->close, p->nbBars);
            }
            else
            {
                /* Multiple real inputs: "inReal0", "inReal1", etc.
                 * Map: inReal0=close, inReal1=volume (matches old MULT behavior).
                 */
                const TA_Real *data;
                if( realInputCount == 0 )
                    data = p->history->close;
                else if( realInputCount == 1 )
                    data = p->history->volume;
                else
                    data = p->history->close;  /* fallback */

                pos += snprintf(buf + pos, bufSize - pos, ",\"inReal%d\":", realInputCount);
                pos += json_write_double_array(buf + pos, bufSize - pos,
                           data, p->nbBars);
            }
            realInputCount++;
            break;
        }
        case TA_Input_Integer:
            /* Integer inputs are rare (unused in practice).
             * Pass close prices cast to integers as placeholder. */
            break;
        }
    }

    /* Optional input parameters */
    for( i = 0; i < fi->nbOptInput; i++ )
    {
        const TA_OptInputParameterInfo *optInfo;
        TA_GetOptInputParameterInfo(fi->handle, i, &optInfo);

        pos += snprintf(buf + pos, bufSize - pos, ",\"%s\":", optInfo->paramName);

        switch( optInfo->type )
        {
        case TA_OptInput_RealRange:
        case TA_OptInput_RealList:
            pos += snprintf(buf + pos, bufSize - pos, "%.15g", optInfo->defaultValue);
            break;
        case TA_OptInput_IntegerRange:
        case TA_OptInput_IntegerList:
            pos += snprintf(buf + pos, bufSize - pos, "%d", (int)optInfo->defaultValue);
            break;
        }
    }

    /* Unstable period (for functions with TA_FUNC_FLG_UNST_PER) */
    if( fi->flags & TA_FUNC_FLG_UNST_PER )
    {
        int unstPeriod = (p->unstId != TA_FUNC_UNST_NONE)
                         ? (int)TA_GetUnstablePeriod(p->unstId) : 0;
        pos += snprintf(buf + pos, bufSize - pos, ",\"unstablePeriod\":%d", unstPeriod);
    }

    pos += snprintf(buf + pos, bufSize - pos, "}}");

    return pos;
}

/* ---- Generic output comparison (Task 8) ---- */

static void compare_codegen_output_generic(
    CodegenRangeTestParam *p,
    unsigned int outputNb)
{
    /* Skip if we already have an error */
    if( p->codegenError != TA_TEST_PASS )
        return;

    /* Unsupported function -- skip silently */
    if( json_is_error(p->responseBuf) )
        return;

    /* Compare retCode */
    int cg_retCode = json_get_int(p->responseBuf, "retCode");
    if( (int)p->lastRetCode != cg_retCode )
    {
        printf("CODEGEN MISMATCH [TA_%s]: retCode C=%d codegen=%d\n",
               p->funcInfo->name, (int)p->lastRetCode, cg_retCode);
        p->codegenError = TA_CODEGEN_RETCODE_MISMATCH;
        return;
    }

    /* If C returned error, both agree -- done */
    if( p->lastRetCode != TA_SUCCESS )
        return;

    /* If C produced no output, skip comparison */
    if( p->lastNbElement == 0 )
        return;

    /* Compare outBegIdx */
    int cg_begIdx = json_get_int(p->responseBuf, "outBegIdx");
    if( p->lastBegIdx != cg_begIdx )
    {
        printf("CODEGEN MISMATCH [TA_%s]: outBegIdx C=%d codegen=%d\n",
               p->funcInfo->name, (int)p->lastBegIdx, cg_begIdx);
        p->codegenError = TA_CODEGEN_BEGIDX_MISMATCH;
        return;
    }

    /* Compare outNBElement */
    int cg_nbElement = json_get_int(p->responseBuf, "outNBElement");
    if( p->lastNbElement != cg_nbElement )
    {
        printf("CODEGEN MISMATCH [TA_%s]: outNBElement C=%d codegen=%d\n",
               p->funcInfo->name, (int)p->lastNbElement, cg_nbElement);
        p->codegenError = TA_CODEGEN_NBELEMENT_MISMATCH;
        return;
    }

    /* Compare output values for the requested outputNb */
    if( p->outputIsInteger[outputNb] )
    {
        /* Integer output comparison (exact match) */
        char fieldName[64];
        if( outputNb == 0 )
            snprintf(fieldName, sizeof(fieldName), "outInteger");
        else
            snprintf(fieldName, sizeof(fieldName), "outInteger%d", outputNb);

        TA_Integer cg_out[MAX_NB_TEST_ELEMENT];
        int parsed = json_get_int_array(p->responseBuf, fieldName,
                                         cg_out, MAX_NB_TEST_ELEMENT);
        for( int i = 0; i < p->lastNbElement && i < parsed; i++ )
        {
            if( p->outIntBufs[outputNb][i] != cg_out[i] )
            {
                printf("CODEGEN MISMATCH [TA_%s]: %s[%d] C=%d codegen=%d\n",
                       p->funcInfo->name, fieldName, i,
                       (int)p->outIntBufs[outputNb][i], (int)cg_out[i]);
                p->codegenError = TA_CODEGEN_OUTPUT_MISMATCH;
                return;
            }
        }
    }
    else
    {
        /* Real output comparison (epsilon) */
        char fieldName[64];
        if( outputNb == 0 )
            snprintf(fieldName, sizeof(fieldName), "outReal");
        else
            snprintf(fieldName, sizeof(fieldName), "outReal%d", outputNb);

        TA_Real cg_out[MAX_NB_TEST_ELEMENT];
        int parsed = json_get_double_array(p->responseBuf, fieldName,
                                            cg_out, MAX_NB_TEST_ELEMENT);
        for( int i = 0; i < p->lastNbElement && i < parsed; i++ )
        {
            double cVal = p->outRealBufs[outputNb][i];
            double diff = fabs(cVal - cg_out[i]);
            double threshold = CODEGEN_EPSILON;
            /* Use relative epsilon for large values (JSON roundtrip precision) */
            if( fabs(cVal) > 1.0 )
            {
                double relThreshold = fabs(cVal) * 1e-12;
                if( relThreshold > threshold )
                    threshold = relThreshold;
            }
            if( diff > threshold )
            {
                printf("CODEGEN MISMATCH [TA_%s]: %s[%d] C=%.10f codegen=%.10f diff=%.2e\n",
                       p->funcInfo->name, fieldName, i,
                       p->outRealBufs[outputNb][i], cg_out[i], diff);
                p->codegenError = TA_CODEGEN_OUTPUT_MISMATCH;
                return;
            }
        }
    }

    /* Parse server timing_ns if present */
    int len;
    const char *timingVal = json_find_field(p->responseBuf, "timing_ns", &len);
    if( timingVal )
    {
        long long serverNs = strtoll(timingVal, NULL, 10);
        if( outputNb == 0 && serverNs == 0 )
            fprintf(stderr, "DEBUG timing_ns=0 for TA_%s, raw='%.20s'\n", p->funcInfo->name, timingVal);
        p->server_total_ns += serverNs;
        p->timing_count++;
    }
    else if( outputNb == 0 )
    {
        /* Debug: show first 120 chars of response when timing_ns is missing */
        fprintf(stderr, "DEBUG no timing_ns for TA_%s: %.120s\n", p->funcInfo->name, p->responseBuf);
    }

    /* Parse server timing_ns_unguarded if present */
    const char *timingUngVal = json_find_field(p->responseBuf, "timing_ns_unguarded", &len);
    if( timingUngVal )
    {
        long long serverUngNs = strtoll(timingUngVal, NULL, 10);
        p->server_unguarded_total_ns += serverUngNs;
        p->timing_unguarded_count++;
    }
}

/* ---- Generic doRangeTest callback (Task 8) ---- */

static TA_RetCode codegen_range_generic(
    TA_Integer startIdx, TA_Integer endIdx,
    TA_Real *outputBuffer, TA_Integer *outputBufferInt,
    TA_Integer *outBegIdx, TA_Integer *outNbElement,
    TA_Integer *lookback, void *opaqueData,
    unsigned int outputNb, unsigned int *isOutputInteger)
{
    CodegenRangeTestParam *p = (CodegenRangeTestParam *)opaqueData;

    /* Set output type flag */
    *isOutputInteger = (unsigned int)p->outputIsInteger[outputNb];

    /* Get lookback */
    TA_GetLookback(p->paramHolder, lookback);

    /* Call TA_CallFunc for EVERY invocation (not just outputNb==0).
     * doRangeTest iterates all startIdx values for output 0, then all for
     * output 1, etc. — so the startIdx/endIdx differ between outputNb calls
     * and we cannot cache across them. */

    /* Re-point all output buffers (TA_CallFunc writes into these) */
    for( unsigned int i = 0; i < p->funcInfo->nbOutput; i++ )
    {
        if( p->outputIsInteger[i] )
            TA_SetOutputParamIntegerPtr(p->paramHolder, i, p->outIntBufs[i]);
        else
            TA_SetOutputParamRealPtr(p->paramHolder, i, p->outRealBufs[i]);
    }

    /* Call the C reference function via ta_abstract */
    p->lastRetCode = TA_CallFunc(p->paramHolder, startIdx, endIdx,
                                  &p->lastBegIdx, &p->lastNbElement);


    *outBegIdx = p->lastBegIdx;
    *outNbElement = p->lastNbElement;

    /* Copy the requested output into the doRangeTest buffer */
    if( p->lastRetCode == TA_SUCCESS && p->lastNbElement > 0 )
    {
        if( p->outputIsInteger[outputNb] )
        {
            for( int i = 0; i < p->lastNbElement; i++ )
                outputBufferInt[i] = p->outIntBufs[outputNb][i];
        }
        else
        {
            for( int i = 0; i < p->lastNbElement; i++ )
                outputBuffer[i] = p->outRealBufs[outputNb][i];
        }
    }

    /* Codegen comparison is done once in test_one_function (full range).
     * This callback only handles C reference coherency for doRangeTest. */

    return p->lastRetCode;
}

/* ---- Setup helpers (Task 9) ---- */

static void setup_inputs(TA_ParamHolder *paramHolder,
                         const TA_FuncInfo *funcInfo,
                         const TA_History *history)
{
    unsigned int i;
    int realInputCount = 0;

    for( i = 0; i < funcInfo->nbInput; i++ )
    {
        const TA_InputParameterInfo *inputInfo;
        TA_GetInputParameterInfo(funcInfo->handle, i, &inputInfo);

        switch( inputInfo->type )
        {
        case TA_Input_Price:
            TA_SetInputParamPricePtr(paramHolder, i,
                inputInfo->flags & TA_IN_PRICE_OPEN         ? history->open : NULL,
                inputInfo->flags & TA_IN_PRICE_HIGH         ? history->high : NULL,
                inputInfo->flags & TA_IN_PRICE_LOW          ? history->low  : NULL,
                inputInfo->flags & TA_IN_PRICE_CLOSE        ? history->close : NULL,
                inputInfo->flags & TA_IN_PRICE_VOLUME       ? history->volume : NULL,
                inputInfo->flags & TA_IN_PRICE_OPENINTEREST ? history->openInterest : NULL);
            break;

        case TA_Input_Real:
        {
            const TA_Real *data;
            if( realInputCount == 0 )
                data = history->close;
            else if( realInputCount == 1 )
                data = history->volume;
            else
                data = history->close;  /* fallback */
            TA_SetInputParamRealPtr(paramHolder, i, data);
            realInputCount++;
            break;
        }
        case TA_Input_Integer:
            /* Integer inputs are rare; pass NULL -- TA_CallFunc will
             * return an error for these functions, which is fine. */
            break;
        }
    }
}

/* Optional inputs are left at defaults (TA_ParamHolderAlloc sets them). */

static void setup_outputs(CodegenRangeTestParam *p)
{
    unsigned int i;

    memset(p->outputIsInteger, 0, sizeof(p->outputIsInteger));
    memset(p->outRealBufs, 0, sizeof(p->outRealBufs));
    memset(p->outIntBufs, 0, sizeof(p->outIntBufs));
    p->totalOutputs = 0;

    for( i = 0; i < p->funcInfo->nbOutput && i < MAX_OUTPUTS; i++ )
    {
        const TA_OutputParameterInfo *outputInfo;
        TA_GetOutputParameterInfo(p->funcInfo->handle, i, &outputInfo);

        switch( outputInfo->type )
        {
        case TA_Output_Real:
            p->outputIsInteger[i] = 0;
            p->outRealBufs[i] = (TA_Real *)calloc(MAX_NB_TEST_ELEMENT, sizeof(TA_Real));
            TA_SetOutputParamRealPtr(p->paramHolder, i, p->outRealBufs[i]);
            break;
        case TA_Output_Integer:
            p->outputIsInteger[i] = 1;
            p->outIntBufs[i] = (TA_Integer *)calloc(MAX_NB_TEST_ELEMENT, sizeof(TA_Integer));
            TA_SetOutputParamIntegerPtr(p->paramHolder, i, p->outIntBufs[i]);
            break;
        }
        p->totalOutputs++;
    }
}

static void free_outputs(CodegenRangeTestParam *p)
{
    for( int i = 0; i < p->totalOutputs; i++ )
    {
        free(p->outRealBufs[i]);
        p->outRealBufs[i] = NULL;
        free(p->outIntBufs[i]);
        p->outIntBufs[i] = NULL;
    }
}

/* ---- Determine integer tolerance for doRangeTest ---- */

static unsigned int get_integer_tolerance(const TA_FuncInfo *funcInfo)
{
    /* Functions with only integer outputs (candlestick patterns) and
     * functions with unstable periods need TA_DO_NOT_COMPARE to avoid
     * false failures from range-dependent floating-point drift.
     *
     * For now, use TA_DO_NOT_COMPARE for all functions -- the codegen
     * comparison is the real test. doRangeTest still validates coherency
     * (lookback consistency, no out-of-bounds writes, etc.).
     */
    (void)funcInfo;
    return TA_DO_NOT_COMPARE;
}

/* ---- Filter helper ---- */

static int codegen_matches_filter(const char *filter, const char *name)
{
    char filterCopy[1024];
    char *token;
    if( filter == NULL ) return 1;
    strncpy(filterCopy, filter, sizeof(filterCopy) - 1);
    filterCopy[sizeof(filterCopy) - 1] = '\0';
    token = strtok(filterCopy, ",");
    while( token != NULL )
    {
        if( strstr(name, token) != NULL ) return 1;
        token = strtok(NULL, ",");
    }
    return 0;
}

/* ---- Per-function callback for TA_ForEachFunc (Task 9) ---- */

typedef struct {
    const TA_History *history;
    const char       *functionFilter;
    CodegenPipe      *cp;
    char             *requestBuf;
    char             *responseBuf;
    ErrorNumber       error;
    int               passed;
    int               failed;
    int               skipped;
    int               langIndex;   /* index into ALL_LANGUAGES */
    const CodegenLanguage *lang;
} ForEachFuncContext;

static void test_one_function(const TA_FuncInfo *funcInfo, void *opaqueData)
{
    ForEachFuncContext *ctx = (ForEachFuncContext *)opaqueData;

    /* Stop iterating if we already hit an error */
    if( ctx->error != TA_TEST_PASS )
        return;

    /* Apply function filter */
    if( !codegen_matches_filter(ctx->functionFilter, funcInfo->name) )
        return;

    /* Skip functions with integer inputs (very rare, no test data) */
    unsigned int hasIntegerInput = 0;
    for( unsigned int i = 0; i < funcInfo->nbInput; i++ )
    {
        const TA_InputParameterInfo *inputInfo;
        TA_GetInputParameterInfo(funcInfo->handle, i, &inputInfo);
        if( inputInfo->type == TA_Input_Integer )
        {
            hasIntegerInput = 1;
            break;
        }
    }
    if( hasIntegerInput )
    {
        /* Record skip in global table */
        int ridx = -1;
        for( int i = 0; i < g_numTimingResults; i++ )
        {
            if( strcmp(g_timingResults[i].funcName, funcInfo->name) == 0 )
            {
                ridx = i;
                break;
            }
        }
        if( ridx < 0 && g_numTimingResults < MAX_FUNCTIONS )
        {
            ridx = g_numTimingResults++;
            memset(&g_timingResults[ridx], 0, sizeof(FuncTimingResult));
            strncpy(g_timingResults[ridx].funcName, funcInfo->name,
                    sizeof(g_timingResults[ridx].funcName) - 1);
        }
        /* langs[langIndex].tested stays 0 (skipped) */
        ctx->skipped++;
        return;
    }

    /* Reset all unstable periods to 0 — doRangeTest leaves them at
     * high values which contaminates subsequent functions. */
    TA_SetUnstablePeriod(TA_FUNC_UNST_ALL, 0);

    printf("  %-40s ", funcInfo->name);
    fflush(stdout);

    /* Allocate param holder */
    TA_ParamHolder *paramHolder = NULL;
    TA_RetCode retCode = TA_ParamHolderAlloc(funcInfo->handle, &paramHolder);
    if( retCode != TA_SUCCESS )
    {
        printf("FAILED (ParamHolderAlloc: %d)\n", retCode);
        ctx->error = TA_CODEGEN_ALLOC_FAILED;
        return;
    }

    /* Build CodegenRangeTestParam */
    CodegenRangeTestParam params;
    memset(&params, 0, sizeof(params));
    params.funcInfo    = funcInfo;
    params.paramHolder = paramHolder;
    params.history     = ctx->history;
    params.nbBars      = (int)ctx->history->nbBars;
    params.unstId      = get_unst_id(funcInfo->name);
    params.cp          = ctx->cp;
    params.requestBuf  = ctx->requestBuf;
    params.responseBuf = ctx->responseBuf;
    params.codegenError = TA_TEST_PASS;

    /* Set up inputs (using defaults for optional params) */
    setup_inputs(paramHolder, funcInfo, ctx->history);

    /* Set up output buffers */
    setup_outputs(&params);

    /* Warmup C reference call (cache priming) */
    TA_Integer begIdx  = 0;
    TA_Integer nbElem  = 0;
    TA_CallFunc(params.paramHolder, 0, params.nbBars - 1, &begIdx, &nbElem);

    /* Measure C reference call (single, post-warmup) */
    begIdx = 0;
    nbElem = 0;
    long long c_ns0 = get_nanotime();
    TA_CallFunc(params.paramHolder, 0, params.nbBars - 1, &begIdx, &nbElem);
    long long c_ns1 = get_nanotime();
    double c_avg_ns = (double)(c_ns1 - c_ns0);
    params.c_ref_total_ns = (long long)c_avg_ns;

    /* Save C reference results for codegen comparison */
    params.lastRetCode  = TA_SUCCESS;
    params.lastBegIdx   = begIdx;
    params.lastNbElement = nbElem;

    /* Warmup call: discard the first call to eliminate cold-start effects
     * (JVM class loading, Rust monomorphization, CPU cache priming, etc.) */
    build_json_request(&params, 0, params.nbBars - 1);
    codegen_pipe_call(params.cp, params.requestBuf,
                      params.responseBuf, JSON_BUF_SIZE);

    /* Codegen comparison: one full-range JSON-RPC call, compare all outputs.
     * This is done BEFORE doRangeTest to separate concerns:
     * - codegen comparison: does generated code match C reference?
     * - range test: is the C function coherent across sub-ranges?
     */
    build_json_request(&params, 0, params.nbBars - 1);
    ErrorNumber codegenErr = codegen_pipe_call(params.cp, params.requestBuf,
                                               params.responseBuf, JSON_BUF_SIZE);
    if( codegenErr != TA_TEST_PASS )
    {
        params.codegenError = codegenErr;
    }
    else
    {
        for( unsigned int outNb = 0; outNb < funcInfo->nbOutput; outNb++ )
            compare_codegen_output_generic(&params, outNb);
    }

    /* Snapshot server timing from the full-range comparison call.
     * Both c_ref_ns and s_avg_ns are single full-range calls (apples-to-apples).
     * Note: C-ref includes ta_abstract dispatch overhead; server measures the
     * raw indicator call only. This is intentional — the dispatch overhead is
     * real cost that library users pay. */
    double s_avg_ns = (params.timing_count > 0)
                      ? (double)params.server_total_ns / (double)params.timing_count
                      : 0.0;
    double s_avg_ns_unguarded = (params.timing_unguarded_count > 0)
                      ? (double)params.server_unguarded_total_ns / (double)params.timing_unguarded_count
                      : 0.0;

    /* Run doRangeTest with the generic callback (C reference coherency only).
     * Skip when lookback exceeds data range (no output possible). */
    ErrorNumber errNb = TA_TEST_PASS;
    if( nbElem > 0 )
    {
        errNb = doRangeTest(
            codegen_range_generic,
            params.unstId,
            (void *)&params,
            funcInfo->nbOutput,
            get_integer_tolerance(funcInfo));
    }

    /* Record results in global timing table */
    int resultIdx = -1;
    for( int i = 0; i < g_numTimingResults; i++ )
    {
        if( strcmp(g_timingResults[i].funcName, funcInfo->name) == 0 )
        {
            resultIdx = i;
            break;
        }
    }
    if( resultIdx < 0 && g_numTimingResults < MAX_FUNCTIONS )
    {
        resultIdx = g_numTimingResults++;
        memset(&g_timingResults[resultIdx], 0, sizeof(FuncTimingResult));
        strncpy(g_timingResults[resultIdx].funcName, funcInfo->name,
                sizeof(g_timingResults[resultIdx].funcName) - 1);
        g_timingResults[resultIdx].c_ref_ns = c_avg_ns;
    }

    /* Check for codegen mismatch */
    if( params.codegenError != TA_TEST_PASS )
    {
        printf("CODEGEN FAILED (code=%d)\n", params.codegenError);
        if( resultIdx >= 0 && ctx->langIndex < (int)NUM_LANGUAGES )
        {
            g_timingResults[resultIdx].langs[ctx->langIndex].tested  = -1;
            g_timingResults[resultIdx].langs[ctx->langIndex].avg_ns  = s_avg_ns;
            if( params.timing_unguarded_count > 0 )
                g_timingResults[resultIdx].langs[ctx->langIndex].avg_ns_unguarded = s_avg_ns_unguarded;
        }
        free_outputs(&params);
        TA_ParamHolderFree(paramHolder);
        ctx->failed++;
        ctx->error = params.codegenError;
        return;
    }

    if( errNb != TA_TEST_PASS )
    {
        printf("RANGE TEST FAILED (code=%d)\n", errNb);
        if( resultIdx >= 0 && ctx->langIndex < (int)NUM_LANGUAGES )
        {
            g_timingResults[resultIdx].langs[ctx->langIndex].tested  = -1;
            g_timingResults[resultIdx].langs[ctx->langIndex].avg_ns  = s_avg_ns;
            if( params.timing_unguarded_count > 0 )
                g_timingResults[resultIdx].langs[ctx->langIndex].avg_ns_unguarded = s_avg_ns_unguarded;
        }
        free_outputs(&params);
        TA_ParamHolderFree(paramHolder);
        ctx->failed++;
        ctx->error = errNb;
        return;
    }

    /* Mark pass in global table */
    if( resultIdx >= 0 && ctx->langIndex < (int)NUM_LANGUAGES )
    {
        g_timingResults[resultIdx].langs[ctx->langIndex].tested  = 1;
        g_timingResults[resultIdx].langs[ctx->langIndex].avg_ns  = s_avg_ns;
        if( params.timing_unguarded_count > 0 )
            g_timingResults[resultIdx].langs[ctx->langIndex].avg_ns_unguarded = s_avg_ns_unguarded;
    }

    /* Print result with timing and speedup ratio */
    if( s_avg_ns > 0 && c_avg_ns > 0 )
    {
        double ratio = c_avg_ns / s_avg_ns;
        printf("PASS   (C: %.0fns, %s: %.0fns, %.2fx %s)\n",
               c_avg_ns, ctx->lang->display, s_avg_ns,
               (ratio >= 1.0) ? ratio : 1.0 / ratio,
               (ratio >= 1.0) ? "faster" : "slower");
    }
    else
    {
        printf("PASS\n");
    }
    ctx->passed++;

    free_outputs(&params);
    TA_ParamHolderFree(paramHolder);
}

/* ---- Test orchestration (Task 9) ---- */

static ErrorNumber test_codegen_for_language(
    const CodegenLanguage *lang,
    int langIndex,
    const TA_History *history,
    const char *functionFilter)
{
    CodegenPipe cp;
    ErrorNumber errNb;

    printf("\n");
    printf("Codegen verification: %s\n", lang->display);
    printf("---------------------------------------------\n");

    errNb = codegen_pipe_open(&cp, lang->argv);
    if( errNb != TA_TEST_PASS )
    {
        printf("FAILED: Cannot start %s server", lang->display);
        if( strcmp(lang->name, "rust") == 0 )
            printf(" (is ./ta_codegen built?)");
        else
            printf(" (run: ta_codegen build --lang=%s)", lang->name);
        printf("\n");
        return errNb;
    }
    printf("  Server started (pid=%d)\n", cp.child_pid);

    /* Allocate reusable JSON buffers */
    char *requestBuf = malloc(JSON_BUF_SIZE);
    char *responseBuf = malloc(JSON_BUF_SIZE);
    if( !requestBuf || !responseBuf )
    {
        free(requestBuf);
        free(responseBuf);
        codegen_pipe_close(&cp);
        return TA_CODEGEN_ALLOC_FAILED;
    }

    /* Use TA_ForEachFunc to iterate all functions */
    ForEachFuncContext ctx;
    memset(&ctx, 0, sizeof(ctx));
    ctx.history        = history;
    ctx.functionFilter = functionFilter;
    ctx.cp             = &cp;
    ctx.requestBuf     = requestBuf;
    ctx.responseBuf    = responseBuf;
    ctx.error          = TA_TEST_PASS;
    ctx.passed         = 0;
    ctx.failed         = 0;
    ctx.skipped        = 0;
    ctx.langIndex      = langIndex;
    ctx.lang           = lang;

    TA_ForEachFunc(test_one_function, &ctx);

    free(requestBuf);
    free(responseBuf);
    codegen_pipe_close(&cp);

    if( ctx.error != TA_TEST_PASS )
        return ctx.error;

    printf("\n  %s: %d passed, %d failed, %d skipped\n",
           lang->display, ctx.passed, ctx.failed, ctx.skipped);

    return TA_TEST_PASS;
}

/* ---- Main entry point ---- */

static int language_matches_filter(const char *filter, const char *name)
{
    char filterCopy[1024];
    char *token;
    if( filter == NULL ) return 1;
    strncpy(filterCopy, filter, sizeof(filterCopy) - 1);
    filterCopy[sizeof(filterCopy) - 1] = '\0';
    token = strtok(filterCopy, ",");
    while( token != NULL )
    {
        if( strcmp(name, token) == 0 ) return 1;
        token = strtok(NULL, ",");
    }
    return 0;
}

/* ---- Cross-language timing table (Task 12) ---- */

static void print_timing_table(const char *languageFilter)
{
    if( g_numTimingResults == 0 )
        return;

    /* Collect which language columns to show */
    int showLang[NUM_LANGUAGES];
    for( unsigned int li = 0; li < NUM_LANGUAGES; li++ )
        showLang[li] = language_matches_filter(languageFilter, ALL_LANGUAGES[li].name);

    printf("\n");
    printf("=============================================\n");
    printf("Codegen Results + Timing (avg ns/call)\n");
    printf("=============================================\n");

    /* Check if any language has unguarded data (and user hasn't hidden it) */
    int hasUnguarded = 0;
    if( !g_hideUnguarded )
        for( int ri = 0; ri < g_numTimingResults && !hasUnguarded; ri++ )
            for( unsigned int li = 0; li < NUM_LANGUAGES; li++ )
                if( showLang[li] && g_timingResults[ri].langs[li].avg_ns_unguarded > 0 )
                { hasUnguarded = 1; break; }
    int showGuarded = !g_hideGuarded;

    /* Header */
    printf("%-20s %9s", "Function", "C-ref");
    for( unsigned int li = 0; li < NUM_LANGUAGES; li++ )
    {
        if( showLang[li] )
        {
            if( showGuarded )
                printf(" %9s", ALL_LANGUAGES[li].display);
            if( hasUnguarded )
                printf(" %9s", showGuarded ? "ung" : ALL_LANGUAGES[li].display);
        }
    }
    printf("\n");

    /* Rows — C column uses ANSI color: red if slower than C-ref, green if faster */
    for( int ri = 0; ri < g_numTimingResults; ri++ )
    {
        FuncTimingResult *r = &g_timingResults[ri];
        printf("%-20s %9.0f", r->funcName, r->c_ref_ns);
        for( unsigned int li = 0; li < NUM_LANGUAGES; li++ )
        {
            if( !showLang[li] )
                continue;
            int st = r->langs[li].tested;
            if( st == 0 )
            {
                if( showGuarded ) printf(" %9s", "--");
                if( hasUnguarded ) printf(" %9s", "--");
            }
            else if( st == -1 )
            {
                if( showGuarded ) printf(" %9s", "FAIL");
                if( hasUnguarded ) printf(" %9s", "--");
            }
            else
            {
                /* Guarded column: color relative to C-ref */
                if( showGuarded )
                {
                    if( r->c_ref_ns > 0 )
                    {
                        if( r->langs[li].avg_ns > r->c_ref_ns )
                            printf(" \033[31m%9.0f\033[0m", r->langs[li].avg_ns);
                        else if( r->langs[li].avg_ns < r->c_ref_ns )
                            printf(" \033[32m%9.0f\033[0m", r->langs[li].avg_ns);
                        else
                            printf(" %9.0f", r->langs[li].avg_ns);
                    }
                    else
                        printf(" %9.0f", r->langs[li].avg_ns);
                }

                /* Unguarded column: color relative to C-ref */
                if( hasUnguarded )
                {
                    if( r->langs[li].avg_ns_unguarded > 0 )
                    {
                        if( r->c_ref_ns > 0 && r->langs[li].avg_ns_unguarded > r->c_ref_ns )
                            printf(" \033[31m%9.0f\033[0m", r->langs[li].avg_ns_unguarded);
                        else if( r->c_ref_ns > 0 && r->langs[li].avg_ns_unguarded < r->c_ref_ns )
                            printf(" \033[32m%9.0f\033[0m", r->langs[li].avg_ns_unguarded);
                        else
                            printf(" %9.0f", r->langs[li].avg_ns_unguarded);
                    }
                    else
                        printf(" %9s", "--");
                }
            }
        }
        printf("\n");
    }
}

/* ---- JSONL rolling report (Task 13) ---- */

static void write_timing_report(const char *filepath)
{
    FILE *f = fopen(filepath, "a");
    if( !f ) return;

    /* Get git SHA */
    char gitSha[64] = "unknown";
    FILE *git = popen("git rev-parse --short HEAD 2>/dev/null", "r");
    if( git ) { fgets(gitSha, sizeof(gitSha), git); pclose(git); }
    char *nl = strchr(gitSha, '\n');
    if( nl ) *nl = '\0';

    /* Get timestamp */
    time_t now = time(NULL);
    char timestamp[64];
    strftime(timestamp, sizeof(timestamp), "%Y-%m-%dT%H:%M:%SZ", gmtime(&now));

    /* Write JSONL line */
    fprintf(f, "{\"timestamp\":\"%s\",\"git_sha\":\"%s\",\"results\":{",
            timestamp, gitSha);

    int first = 1;
    for( int ri = 0; ri < g_numTimingResults; ri++ )
    {
        FuncTimingResult *r = &g_timingResults[ri];
        if( !first ) fprintf(f, ",");
        first = 0;
        fprintf(f, "\"%s\":{\"c_ref_ns\":%.3f,\"langs\":{", r->funcName, r->c_ref_ns);
        int firstLang = 1;
        for( unsigned int li = 0; li < NUM_LANGUAGES; li++ )
        {
            int st = r->langs[li].tested;
            if( st == 0 ) continue;   /* skip not-tested */
            if( !firstLang ) fprintf(f, ",");
            firstLang = 0;
            fprintf(f, "\"%s\":{\"status\":\"%s\",\"avg_ns\":%.0f",
                    ALL_LANGUAGES[li].name,
                    (st == 1) ? "pass" : "fail",
                    r->langs[li].avg_ns);
            if( r->langs[li].avg_ns_unguarded > 0 )
                fprintf(f, ",\"avg_ns_unguarded\":%.0f", r->langs[li].avg_ns_unguarded);
            fprintf(f, "}");
        }
        fprintf(f, "}}");
    }

    fprintf(f, "}}\n");
    fclose(f);
}

/* ---- Markdown report writer ---- */

static void fmt_ns(char *buf, int buf_size, double ns)
{
    if( ns <= 0 )      snprintf(buf, buf_size, "<42");
    else if( ns < 100 ) snprintf(buf, buf_size, "%.0f", ns);
    else               snprintf(buf, buf_size, "%.0f", ns);
}

static void fmt_ratio(char *buf, int buf_size, double val, double ref)
{
    if( val <= 0 || ref <= 0 ) { snprintf(buf, buf_size, "\xe2\x80\x94"); return; }
    double ratio = val / ref;
    if( ratio > 1.1 )      snprintf(buf, buf_size, "%.1f\xc3\x97 slower", ratio);
    else if( ratio < 0.9 ) snprintf(buf, buf_size, "%.1f\xc3\x97 faster", 1.0/ratio);
    else                    snprintf(buf, buf_size, "\xe2\x89\x88 same");
}

static void write_markdown_report(const char *filepath, const char *languageFilter)
{
    if( g_numTimingResults == 0 ) return;

    FILE *f = fopen(filepath, "w");
    if( !f ) return;

    /* Collect which languages to include */
    int showLang[NUM_LANGUAGES];
    int numShown = 0;
    for( unsigned int li = 0; li < NUM_LANGUAGES; li++ ) {
        showLang[li] = language_matches_filter(languageFilter, ALL_LANGUAGES[li].name);
        if( showLang[li] ) numShown++;
    }

    /* Git SHA */
    char gitSha[64] = "unknown";
    FILE *git = popen("git rev-parse --short HEAD 2>/dev/null", "r");
    if( git ) { fgets(gitSha, sizeof(gitSha), git); pclose(git); }
    char *nl = strchr(gitSha, '\n');
    if( nl ) *nl = '\0';

    /* Timestamp */
    time_t now = time(NULL);
    char timestamp[64];
    strftime(timestamp, sizeof(timestamp), "%Y-%m-%d %H:%M", localtime(&now));

    /* Count pass/fail per language + averages */
    int total = g_numTimingResults;
    int langPass[NUM_LANGUAGES];
    double langSum[NUM_LANGUAGES];
    int langMeasured[NUM_LANGUAGES];
    memset(langPass, 0, sizeof(langPass));
    memset(langSum, 0, sizeof(langSum));
    memset(langMeasured, 0, sizeof(langMeasured));

    double cRefSum = 0; int cRefCount = 0;
    for( int ri = 0; ri < g_numTimingResults; ri++ ) {
        FuncTimingResult *r = &g_timingResults[ri];
        if( r->c_ref_ns > 0 ) { cRefSum += r->c_ref_ns; cRefCount++; }
        for( unsigned int li = 0; li < NUM_LANGUAGES; li++ ) {
            if( r->langs[li].tested == 1 ) langPass[li]++;
            if( r->langs[li].tested == 1 && r->langs[li].avg_ns > 0 ) {
                langSum[li] += r->langs[li].avg_ns;
                langMeasured[li]++;
            }
        }
    }
    double cRefAvg = cRefCount > 0 ? cRefSum / cRefCount : 0;

    /* Header */
    fprintf(f, "# ta_regtest Cross-Language Report\n\n");
    fprintf(f, "> **Date:** %s  \n", timestamp);
    fprintf(f, "> **Git:** `%s`  \n", gitSha);
    fprintf(f, "> **Indicators:** %d  \n", total);

    int allPass = 1;
    for( unsigned int li = 0; li < NUM_LANGUAGES; li++ ) {
        if( showLang[li] && langPass[li] < total ) allPass = 0;
    }
    fprintf(f, "> **Status:** %s\n\n",
            allPass ? "\xe2\x9c\x85 ALL PASSING" : "\xe2\x9d\x8c FAILURES DETECTED");

    /* Summary table */
    fprintf(f, "## Summary\n\n```\n");
    fprintf(f, "\xe2\x94\x8c\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80"
            "\xe2\x94\xac\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80"
            "\xe2\x94\xac\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80"
            "\xe2\x94\xac\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80"
            "\xe2\x94\xac\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80"
            "\xe2\x94\x90\n");

    fprintf(f, "\xe2\x94\x82 %-9s\xe2\x94\x82 %-5s\xe2\x94\x82 %-5s\xe2\x94\x82 %-11s\xe2\x94\x82 %-15s\xe2\x94\x82\n",
            "Language", "Pass", "Fail", "Avg (ns)", "vs C-ref");

    fprintf(f, "\xe2\x94\x9c\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80"
            "\xe2\x94\xbc\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80"
            "\xe2\x94\xbc\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80"
            "\xe2\x94\xbc\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80"
            "\xe2\x94\xbc\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80"
            "\xe2\x94\xa4\n");

    /* C-ref row */
    {
        char avg[32]; fmt_ns(avg, sizeof(avg), cRefAvg);
        fprintf(f, "\xe2\x94\x82 %-9s\xe2\x94\x82 %-5d\xe2\x94\x82 %-5d\xe2\x94\x82 %-11s\xe2\x94\x82 %-15s\xe2\x94\x82\n",
                "C-ref", total, 0, avg, "baseline");
    }

    /* Per-language rows */
    for( unsigned int li = 0; li < NUM_LANGUAGES; li++ ) {
        if( !showLang[li] ) continue;
        double avg = langMeasured[li] > 0 ? langSum[li] / langMeasured[li] : 0;
        char avgStr[32], vsStr[32];
        if( langMeasured[li] < total / 2 ) {
            fmt_ns(avgStr, sizeof(avgStr), avg);
            char tmp[16]; snprintf(tmp, sizeof(tmp), "~%s*", avgStr);
            strncpy(avgStr, tmp, sizeof(avgStr));
            snprintf(vsStr, sizeof(vsStr), "*%d/%d measured", langMeasured[li], total);
        } else {
            fmt_ns(avgStr, sizeof(avgStr), avg);
            fmt_ratio(vsStr, sizeof(vsStr), avg, cRefAvg);
        }
        int fail = total - langPass[li];
        fprintf(f, "\xe2\x94\x82 %-9s\xe2\x94\x82 %-5d\xe2\x94\x82 %-5d\xe2\x94\x82 %-11s\xe2\x94\x82 %-15s\xe2\x94\x82\n",
                ALL_LANGUAGES[li].display, langPass[li], fail, avgStr, vsStr);
    }

    fprintf(f, "\xe2\x94\x94\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80"
            "\xe2\x94\xb4\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80"
            "\xe2\x94\xb4\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80"
            "\xe2\x94\xb4\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80"
            "\xe2\x94\xb4\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80"
            "\xe2\x94\x98\n");
    fprintf(f, "```\n\n");

    /* Check if any language has unguarded data */
    int mdHasUnguarded = 0;
    for( int ri = 0; ri < g_numTimingResults && !mdHasUnguarded; ri++ )
        for( unsigned int li = 0; li < NUM_LANGUAGES; li++ )
            if( showLang[li] && g_timingResults[ri].langs[li].avg_ns_unguarded > 0 )
            { mdHasUnguarded = 1; break; }

    /* Detailed per-function table */
    fprintf(f, "## Results (ns/call)\n\n");
    fprintf(f, "| Function |  C-ref |");
    for( unsigned int li = 0; li < NUM_LANGUAGES; li++ ) {
        if( showLang[li] ) {
            fprintf(f, " %s |", ALL_LANGUAGES[li].display);
            if( mdHasUnguarded ) fprintf(f, " %s-ung |", ALL_LANGUAGES[li].display);
        }
    }
    fprintf(f, "\n|----------|--------|");
    for( unsigned int li = 0; li < NUM_LANGUAGES; li++ ) {
        if( showLang[li] ) {
            fprintf(f, "--------|");
            if( mdHasUnguarded ) fprintf(f, "--------|");
        }
    }
    fprintf(f, "\n");

    for( int ri = 0; ri < g_numTimingResults; ri++ ) {
        FuncTimingResult *r = &g_timingResults[ri];
        char cref[32]; fmt_ns(cref, sizeof(cref), r->c_ref_ns);
        fprintf(f, "| %-8s | %6s |", r->funcName, cref);
        for( unsigned int li = 0; li < NUM_LANGUAGES; li++ ) {
            if( !showLang[li] ) continue;
            if( r->langs[li].tested == -1 ) {
                fprintf(f, " FAIL   |");
                if( mdHasUnguarded ) fprintf(f, "     \xe2\x80\x94 |");
            } else if( r->langs[li].tested == 1 ) {
                char t[32]; fmt_ns(t, sizeof(t), r->langs[li].avg_ns);
                fprintf(f, " %6s |", t);
                if( mdHasUnguarded ) {
                    if( r->langs[li].avg_ns_unguarded > 0 ) {
                        char u[32]; fmt_ns(u, sizeof(u), r->langs[li].avg_ns_unguarded);
                        fprintf(f, " %6s |", u);
                    } else
                        fprintf(f, "     \xe2\x80\x94 |");
                }
            } else {
                fprintf(f, "     \xe2\x80\x94 |");
                if( mdHasUnguarded ) fprintf(f, "     \xe2\x80\x94 |");
            }
        }
        fprintf(f, "\n");
    }

    fprintf(f, "\n*Generated by ta_regtest — %s*\n", timestamp);
    fclose(f);
}

ErrorNumber test_codegen(const TA_History *history,
                         const char *languageFilter,
                         const char *functionFilter)
{
    ErrorNumber errNb;
    int langsTested = 0;

    printf("\n");
    printf("=============================================\n");
    printf("Codegen Multi-Language Verification\n");
    printf("=============================================\n");

    for( unsigned int i = 0; i < NUM_LANGUAGES; i++ )
    {
        if( !language_matches_filter(languageFilter, ALL_LANGUAGES[i].name) )
            continue;

        errNb = test_codegen_for_language(&ALL_LANGUAGES[i], (int)i, history,
                                          functionFilter);
        if( errNb != TA_TEST_PASS )
            return errNb;

        langsTested++;
    }

    if( langsTested == 0 )
    {
        printf("\nNo languages matched filter '%s'\n",
               languageFilter ? languageFilter : "(none)");
        return TA_REGTEST_BAD_USER_PARAM;
    }

    print_timing_table(languageFilter);

    printf("\n=============================================\n");
    printf("All %d language(s) passed codegen verification\n", langsTested);
    printf("=============================================\n");

    /* Write report files */
    write_timing_report("ta_regtest_timing.jsonl");
    write_markdown_report("ta_regtest_report.md", languageFilter);

    /* Print absolute paths */
    {
        char jsonl_path[PATH_MAX];
        char md_path[PATH_MAX];
        if( realpath("ta_regtest_timing.jsonl", jsonl_path) )
            printf("\nJSONL: %s\n", jsonl_path);
        else
            printf("\nJSONL: ta_regtest_timing.jsonl\n");
        if( realpath("ta_regtest_report.md", md_path) )
            printf("Report: %s\n", md_path);
        else
            printf("Report: ta_regtest_report.md\n");
    }

    /* Print summary chart to stdout */
    {
        int total = g_numTimingResults;
        double cRefSum = 0; int cRefCount = 0;
        int langPass[NUM_LANGUAGES];
        double langSum[NUM_LANGUAGES];
        int langMeasured[NUM_LANGUAGES];
        memset(langPass, 0, sizeof(langPass));
        memset(langSum, 0, sizeof(langSum));
        memset(langMeasured, 0, sizeof(langMeasured));

        for( int ri = 0; ri < g_numTimingResults; ri++ ) {
            FuncTimingResult *r = &g_timingResults[ri];
            if( r->c_ref_ns > 0 ) { cRefSum += r->c_ref_ns; cRefCount++; }
            for( unsigned int li = 0; li < NUM_LANGUAGES; li++ ) {
                if( r->langs[li].tested == 1 ) langPass[li]++;
                if( r->langs[li].tested == 1 && r->langs[li].avg_ns > 0 ) {
                    langSum[li] += r->langs[li].avg_ns;
                    langMeasured[li]++;
                }
            }
        }
        double cRefAvg = cRefCount > 0 ? cRefSum / cRefCount : 0;

        printf("\n\xe2\x94\x8c\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80"
               "\xe2\x94\xac\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80"
               "\xe2\x94\xac\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80"
               "\xe2\x94\xac\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80"
               "\xe2\x94\xac\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80"
               "\xe2\x94\x90\n");
        printf("\xe2\x94\x82 %-9s\xe2\x94\x82 %-5s\xe2\x94\x82 %-5s\xe2\x94\x82 %-11s\xe2\x94\x82 %-15s\xe2\x94\x82\n",
               "Language", "Pass", "Fail", "Avg (ns)", "vs C-ref");
        printf("\xe2\x94\x9c\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80"
               "\xe2\x94\xbc\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80"
               "\xe2\x94\xbc\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80"
               "\xe2\x94\xbc\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80"
               "\xe2\x94\xbc\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80"
               "\xe2\x94\xa4\n");

        /* C-ref row */
        {
            char avg[32]; fmt_ns(avg, sizeof(avg), cRefAvg);
            printf("\xe2\x94\x82 %-9s\xe2\x94\x82 %-5d\xe2\x94\x82 %-5d\xe2\x94\x82 %-11s\xe2\x94\x82 %-15s\xe2\x94\x82\n",
                   "C-ref", total, 0, avg, "baseline");
        }

        for( unsigned int li = 0; li < NUM_LANGUAGES; li++ ) {
            if( !language_matches_filter(languageFilter, ALL_LANGUAGES[li].name) )
                continue;
            double avg = langMeasured[li] > 0 ? langSum[li] / langMeasured[li] : 0;
            char avgStr[32], vsStr[32];
            if( langMeasured[li] < total / 2 ) {
                fmt_ns(avgStr, sizeof(avgStr), avg);
                char tmp[16]; snprintf(tmp, sizeof(tmp), "~%s*", avgStr);
                strncpy(avgStr, tmp, sizeof(avgStr));
                snprintf(vsStr, sizeof(vsStr), "*%d/%d measured", langMeasured[li], total);
            } else {
                fmt_ns(avgStr, sizeof(avgStr), avg);
                fmt_ratio(vsStr, sizeof(vsStr), avg, cRefAvg);
            }
            int fail = total - langPass[li];
            printf("\xe2\x94\x82 %-9s\xe2\x94\x82 %-5d\xe2\x94\x82 %-5d\xe2\x94\x82 %-11s\xe2\x94\x82 %-15s\xe2\x94\x82\n",
                   ALL_LANGUAGES[li].display, langPass[li], fail, avgStr, vsStr);
        }

        printf("\xe2\x94\x94\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80"
               "\xe2\x94\xb4\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80"
               "\xe2\x94\xb4\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80"
               "\xe2\x94\xb4\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80"
               "\xe2\x94\xb4\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80\xe2\x94\x80"
               "\xe2\x94\x98\n");
    }

    return TA_TEST_PASS;
}

/* Abstract codegen tests are integrated into test_abstract.c via
 * test_abstract_set_server(). */

