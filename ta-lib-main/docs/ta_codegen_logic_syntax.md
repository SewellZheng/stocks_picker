# ta_codegen Logic File Syntax Reference

The `.logic` file contains the algorithm for a TA-Lib function, written in a restricted C-like syntax. The generator reads this file along with the companion `.yaml` metadata and produces native code for C, Rust, Java, .NET, and SWIG.

**No macros. No preprocessor. No GENCODE markers.** Just the algorithm.

## Quick Example (MULT)

```c
size_t outIdx = 0;
size_t i = startIdx;
while( i <= endIdx ) {
    outReal[outIdx] = inReal0[i] * inReal1[i];
    outIdx += 1;
    i += 1;
}
outNBElement = outIdx;
outBegIdx = startIdx;
```

## Types

| Logic type | C | Rust | Java | Purpose |
|---|---|---|---|---|
| `double` | `double` | `f64` | `double` | Floating-point values |
| `int` | `int` | `i32` | `int` | Integer parameters, counters |
| `size_t` | `int` | `usize` | `int` | Array indices, sizes |

Use `size_t` for anything that indexes into an array or represents a count/size. Use `int` for optional parameters and integer arithmetic. Use `double` for price data and calculations.

## Variable Declarations

```c
double periodTotal = 0.0;
double tempReal;
size_t i = startIdx;
size_t outIdx = 0;
int lookbackTotal;
```

Variables can be declared with or without an initializer. All variables are mutable.

## Statements

Every statement ends with a semicolon.

### Assignment

```c
periodTotal = 0.0;
outReal[outIdx] = tempReal / (double)optInTimePeriod;
outBegIdx = startIdx;
outNBElement = outIdx;
```

### Compound Assignment

```c
periodTotal += (double)inReal[i];
periodTotal -= (double)inReal[trailingIdx];
trailingIdx += 1;
prevLoss *= (double)(optInTimePeriod - 1);
prevGain /= (double)optInTimePeriod;
```

Supported: `+=`, `-=`, `*=`, `/=`

## Control Flow

### if / else if / else

```c
if( startIdx < lookbackTotal ) {
    startIdx = lookbackTotal;
}

if( startIdx > endIdx ) {
    outBegIdx = 0;
    outNBElement = 0;
    return SUCCESS;
} else if( optInTimePeriod == 1 ) {
    // degenerate case
} else {
    // normal path
}
```

Curly braces are always required (no single-statement bodies).

### while

```c
while( i < startIdx ) {
    periodTotal += (double)inReal[i];
    i += 1;
}
```

### for (countdown)

Used for iterating a fixed number of times in reverse (common in RSI and similar):

```c
for( i = optInTimePeriod; i > 0; i-- ) {
    tempValue1 = (double)inReal[today];
    today += 1;
}
```

The generator maps this to idiomatic constructs per language (e.g., `for i in (1..=n).rev()` in Rust).

### break / continue

```c
while( i <= endIdx ) {
    if( someCondition ) {
        break;
    }
    if( otherCondition ) {
        continue;
    }
    // ...
}
```

### return (early exit)

```c
return SUCCESS;
```

The final `return SUCCESS` at the end of the function is implicit — the generator adds it. Use explicit `return` only for early exits (empty output, degenerate cases).

Return values: `SUCCESS`, `BAD_PARAM`, `ALLOC_ERR`, `INTERNAL_ERROR`. The generator maps these to each language's enum (`TA_SUCCESS` in C, `RetCode::Success` in Rust, `RetCode.Success` in Java).

### switch / case

For functions that dispatch to other TA functions (MA, SAR):

```c
switch( optInMAType ) {
    case MAType_SMA:
        retCode = SMA(startIdx, endIdx, inReal, optInTimePeriod, outBegIdx, outNBElement, outReal);
        break;
    case MAType_EMA:
        retCode = EMA(startIdx, endIdx, inReal, optInTimePeriod, outBegIdx, outNBElement, outReal);
        break;
}
```

## Expressions

### Arithmetic

```c
periodTotal + inReal[i]
tempReal / (double)optInTimePeriod
100.0 * (prevGain / tempValue1)
optInTimePeriod - 1
```

Operators: `+`, `-`, `*`, `/`

### Comparison

```c
startIdx < lookbackTotal
i <= endIdx
optInTimePeriod > 1
unstablePeriod == 0
```

Operators: `<`, `<=`, `>`, `>=`, `==`, `!=`

### Boolean Logic

```c
unstablePeriod == 0 && COMPATIBILITY == METASTOCK
optInTimePeriod < 2 || optInTimePeriod > 100000
!IS_ZERO(tempValue1)
```

Operators: `&&`, `||`, `!`

### Type Casts

C-style cast syntax:

```c
(double)inReal[i]          // input value to double (important for f32 inputs)
(double)optInTimePeriod    // int param to double for division
(size_t)(optInTimePeriod - 1)  // int expression to index
(int)someValue             // to integer
```

### Array Access

```c
inReal[i]                  // read input
outReal[outIdx]            // write output
inReal[trailingIdx]        // trailing window
```

### Math Functions

Standard math functions are available. The generator maps them to each language's math library:

```c
sqrt(x)
floor(x)
ceil(x)
fabs(x)
sin(x)
cos(x)
tan(x)
atan(x)
atan2(y, x)
log(x)
exp(x)
pow(base, exp)
fmod(x, y)
```

## Function Calls

Call other TA-Lib functions without prefix — the generator adds the appropriate prefix/method syntax per language:

```c
retCode = SMA(startIdx, endIdx, inReal, optInTimePeriod, outBegIdx, outNBElement, outReal);
lookback = SMA_Lookback(optInTimePeriod);
```

The generator maps these: `TA_SMA(...)` in C, `self.sma(...)` in Rust, `sma(...)` in Java, etc.

## Temp Buffers

For functions that need temporary working arrays (STOCH, MACD, etc.), use VLA-style declaration:

```c
double tempBuffer[endIdx - today + 1];
```

The generator handles allocation per language (stack/heap as appropriate) and automatic cleanup. No explicit `free` needed.

## Built-in Identifiers

These reserved identifiers are recognized by the generator and mapped to language-specific constructs:

### Global State

```c
int unstablePeriod = UNSTABLE_PERIOD(RSI);    // read unstable period for a function
if( COMPATIBILITY == METASTOCK ) { ... }       // check compatibility mode
```

`UNSTABLE_PERIOD(name)` and `COMPATIBILITY` are the only global state accessors. The generator maps them to each language's state mechanism (`self.unstable_period[FuncUnstId::Rsi as usize]` in Rust, `TA_GetUnstablePeriod(...)` in C via FFI, etc.).

Compatibility values: `DEFAULT`, `METASTOCK`

### Zero Check

```c
if( !IS_ZERO(tempValue1) ) {
    outReal[outIdx] = 100.0 * (prevGain / tempValue1);
} else {
    outReal[outIdx] = 0.0;
}
```

`IS_ZERO(x)` tests whether `x` is within epsilon of zero (±1e-14). The generator emits the appropriate epsilon comparison per language.

### Bulk Array Copy

```c
ARRAY_COPY(outReal, 0, inReal, startIdx, count);
```

Copies `count` elements from `inReal[startIdx..]` to `outReal[0..]`. The generator maps to `copy_from_slice` in Rust, `memcpy` in C, `System.arraycopy` in Java, etc. For single-precision variants, the generator automatically inserts element-wise f32→f64 conversion.

## Implicit Behavior

The generator handles these automatically from the YAML metadata — they do NOT appear in the logic file:

- **Parameter validation** — default substitution (`INT_MIN` sentinel), range checking
- **endIdx < startIdx check** — always generated
- **Public/private function split** — public validates, private holds the algorithm
- **Lookback calculation** — derived from YAML `lookback:` field
- **startIdx adjustment** — `if( startIdx < lookbackTotal ) startIdx = lookbackTotal`
- **Single-precision variant** — generated automatically with `(double)` casts on inputs
- **Function signatures** — generated from YAML inputs/outputs
- **Doc comments** — generated from YAML description
- **File headers** — copyright, imports, module structure

## What the Logic File Is NOT

- Not C source code — it doesn't `#include` anything or use the preprocessor
- Not a macro template — no `#if defined(_RUST)`, no `DECLARE_DOUBLE_VAR`
- Not a complete program — no `main()`, no function signature, no return type
- Not language-specific — the same logic file produces all 5 target languages

The logic file is **just the algorithm**, expressed in syntax that any C programmer can read and modify.
