#!/usr/bin/env python3
"""Expand Hilbert transform macros and DO_PRICE_WMA in ta_func_defs source files.

Handles:
- HILBERT_VARIABLES(name) -> variable declarations
- INIT_HILBERT_VARIABLES(name) -> initialization block
- DO_HILBERT_EVEN(name, input) -> Hilbert transform (Even)
- DO_HILBERT_ODD(name, input) -> Hilbert transform (Odd)
- DO_PRICE_WMA(newPrice, storeVar) -> weighted moving average step
- #define DO_PRICE_WMA(...) { ... } -> removed (local macro definition)
"""

import re
import sys
import glob
import os


def expand_hilbert_variables(name):
    """HILBERT_VARIABLES(name) -> variable declarations."""
    return (
        f"double {name}_Odd[3];\n"
        f"    double {name}_Even[3];\n"
        f"    double {name};\n"
        f"    double prev_{name}_Odd;\n"
        f"    double prev_{name}_Even;\n"
        f"    double prev_{name}_input_Odd;\n"
        f"    double prev_{name}_input_Even;"
    )


def expand_init_hilbert_variables(name):
    """INIT_HILBERT_VARIABLES(name) -> initialization."""
    return (
        f"{name}_Odd[0] = 0.0;\n"
        f"    {name}_Odd[1] = 0.0;\n"
        f"    {name}_Odd[2] = 0.0;\n"
        f"    {name}_Even[0] = 0.0;\n"
        f"    {name}_Even[1] = 0.0;\n"
        f"    {name}_Even[2] = 0.0;\n"
        f"    {name} = 0.0;\n"
        f"    prev_{name}_Odd = 0.0;\n"
        f"    prev_{name}_Even = 0.0;\n"
        f"    prev_{name}_input_Odd = 0.0;\n"
        f"    prev_{name}_input_Even = 0.0;"
    )


def expand_do_hilbert(name, input_var, odd_or_even):
    """DO_HILBERT_ODD/EVEN(name, input) -> transform block."""
    return (
        f"hilbertTempReal = a * {input_var};\n"
        f"    {name} = -{name}_{odd_or_even}[hilbertIdx];\n"
        f"    {name}_{odd_or_even}[hilbertIdx] = hilbertTempReal;\n"
        f"    {name} += hilbertTempReal;\n"
        f"    {name} -= prev_{name}_{odd_or_even};\n"
        f"    prev_{name}_{odd_or_even} = b * prev_{name}_input_{odd_or_even};\n"
        f"    {name} += prev_{name}_{odd_or_even};\n"
        f"    prev_{name}_input_{odd_or_even} = {input_var};\n"
        f"    {name} *= adjustedPrevPeriod;"
    )


def expand_do_price_wma(new_price, store_var):
    """DO_PRICE_WMA(newPrice, storeVar) -> WMA step."""
    return (
        f"periodWMASub += {new_price};\n"
        f"    periodWMASub -= trailingWMAValue;\n"
        f"    periodWMASum += {new_price}*4.0;\n"
        f"    trailingWMAValue = inReal[trailingWMAIdx++];\n"
        f"    {store_var} = periodWMASum*0.1;\n"
        f"    periodWMASum -= periodWMASub;"
    )


def remove_define_block(text, macro_name):
    """Remove a #define macro_name(...) { ... } block."""
    # Match #define macro_name(args) { ... } with line continuations
    pattern = rf'[ \t]*#define\s+{re.escape(macro_name)}\s*\([^)]*\)\s*\{{[^}}]*\}}\s*\n?'
    # Handle multi-line defines with backslash continuations
    lines = text.split('\n')
    result = []
    in_define = False
    for line in lines:
        stripped = line.strip()
        if stripped.startswith(f'#define {macro_name}'):
            in_define = True
            continue
        if in_define:
            if stripped.endswith('\\'):
                continue
            elif '}' in stripped:
                in_define = False
                continue
            else:
                in_define = False
                continue
        result.append(line)
    return '\n'.join(result)


def process_file(filepath):
    with open(filepath, 'r') as f:
        text = f.read()

    original = text

    # Remove local #define DO_PRICE_WMA blocks
    text = remove_define_block(text, 'DO_PRICE_WMA')

    # Expand HILBERT_VARIABLES(name)
    text = re.sub(
        r'HILBERT_VARIABLES\(\s*(\w+)\s*\)\s*;',
        lambda m: expand_hilbert_variables(m.group(1)),
        text
    )

    # Expand INIT_HILBERT_VARIABLES(name);
    text = re.sub(
        r'INIT_HILBERT_VARIABLES\(\s*(\w+)\s*\)\s*;',
        lambda m: expand_init_hilbert_variables(m.group(1)),
        text
    )

    # Expand DO_HILBERT_EVEN(name, input);
    text = re.sub(
        r'DO_HILBERT_EVEN\(\s*(\w+)\s*,\s*(\w+)\s*\)\s*;',
        lambda m: expand_do_hilbert(m.group(1), m.group(2), 'Even'),
        text
    )

    # Expand DO_HILBERT_ODD(name, input);
    text = re.sub(
        r'DO_HILBERT_ODD\(\s*(\w+)\s*,\s*(\w+)\s*\)\s*;',
        lambda m: expand_do_hilbert(m.group(1), m.group(2), 'Odd'),
        text
    )

    # Expand DO_PRICE_WMA(arg1, arg2);
    text = re.sub(
        r'DO_PRICE_WMA\(\s*(\w+)\s*,\s*(\w+)\s*\)\s*;',
        lambda m: expand_do_price_wma(m.group(1), m.group(2)),
        text
    )

    if text != original:
        with open(filepath, 'w') as f:
            f.write(text)
        print(f"  Expanded macros in {filepath}")
    else:
        print(f"  No changes in {filepath}")


def main():
    base = os.path.join(os.path.dirname(__file__), '..', 'ta_func_defs')
    files = glob.glob(os.path.join(base, '**', '*.c'), recursive=True)

    count = 0
    for f in sorted(files):
        with open(f, 'r') as fh:
            content = fh.read()
        if any(m in content for m in ['HILBERT_VARIABLES', 'DO_HILBERT_', 'INIT_HILBERT', 'DO_PRICE_WMA']):
            process_file(f)
            count += 1

    print(f"\nProcessed {count} files.")


if __name__ == '__main__':
    main()
