#!/usr/bin/env python3
"""Replace cross-language macros in ta_func_defs extracted .c files with plain C."""

import re
from pathlib import Path


STD_MATH_FUNCS = [
    "atan", "sqrt", "fabs", "floor", "ceil", "log", "cos", "sin", "tan",
    "acos", "asin", "exp", "cosh", "sinh", "tanh", "log10",
]


def replace_simple_macros(content: str) -> str:
    # ENUM_DECLARATION(RetCode) -> TA_RetCode
    content = re.sub(r"ENUM_DECLARATION\(RetCode\)", "TA_RetCode", content)

    # CONSTANT_DOUBLE(name) -> const double name
    content = re.sub(r"CONSTANT_DOUBLE\(([^)]+)\)", r"const double \1", content)

    # CONSTANT_INTEGER(name) -> const int name
    content = re.sub(r"CONSTANT_INTEGER\(([^)]+)\)", r"const int \1", content)

    # ARRAY_REF(buf) -> double *buf
    content = re.sub(r"ARRAY_REF\(\s*([^)]+?)\s*\)", r"double *\1", content)

    # UNUSED_VARIABLE(x); -> (void)x;
    content = re.sub(r"UNUSED_VARIABLE\(([^)]+)\)\s*;", r"(void)\1;", content)

    # CAST_TO_INDEX(expr) -> (int)(expr)
    content = re.sub(r"CAST_TO_INDEX\(([^)]+)\)", r"(int)(\1)", content)

    # CAST_TO_I32(expr) -> (int)(expr)
    content = re.sub(r"CAST_TO_I32\(([^)]+)\)", r"(int)(\1)", content)

    # CAST_TO_F64(expr) -> (double)(expr)
    content = re.sub(r"CAST_TO_F64\(([^)]+)\)", r"(double)(\1)", content)

    # TA_IS_ZERO(x) — preserved as-is (codegen handles per-language rendering)

    # TA_IS_ZERO_OR_NEG(x) — preserved as-is (codegen handles per-language rendering)

    # TA_PER_TO_K(period)
    content = re.sub(
        r"TA_PER_TO_K\(([^)]+)\)",
        r"(2.0 / ((double)(\1) + 1.0))",
        content,
    )

    # TA_GetUnstablePeriod(FUNC) -> TA_GetUnstablePeriod(TA_FUNC_UNST_FUNC)
    # Skip if argument already starts with TA_FUNC_UNST_ (idempotent)
    content = re.sub(
        r"TA_GetUnstablePeriod\((?!TA_FUNC_UNST_)([^)]+)\)",
        lambda m: f"TA_GetUnstablePeriod(TA_FUNC_UNST_{m.group(1)})",
        content,
    )

    # Local #define NAME VALUE (integer) -> const int NAME = VALUE;
    content = re.sub(
        r"^(\s*)#define\s+([A-Z_][A-Z0-9_]*)\s+(\d+)\s*$",
        r"\1const int \2 = \3;",
        content,
        flags=re.MULTILINE,
    )

    # std_* math functions -> plain C equivalents
    for fn in STD_MATH_FUNCS:
        content = re.sub(rf"\bstd_{fn}\b", fn, content)

    # ARRAY_LOCAL(name, size); -> double name[size];
    content = re.sub(
        r'\bARRAY_LOCAL\(\s*([^,]+?)\s*,\s*([^)]+?)\s*\)\s*;',
        r'double \1[\2];',
        content,
    )

    # case ENUM_CASE(type, c_val, pascal_val) [):] -> case c_val:
    # Trailing `:?` consumes the stray colon from original `case ENUM_CASE(...) :`
    content = re.sub(
        r'\bcase\s+ENUM_CASE\(\s*[^,]+?\s*,\s*([^,]+?)\s*,\s*[^)]+?\s*\)\s*:?',
        r'case \1:',
        content,
    )

    # TA_INTERNAL_ERROR(code) -> TA_INTERNAL_ERROR  (strips the error code argument)
    content = re.sub(
        r'\bTA_INTERNAL_ERROR\(\s*[^)]*\)',
        'TA_INTERNAL_ERROR',
        content,
    )

    return content


def replace_array_macros(content: str) -> str:
    # ARRAY_ALLOC(buf, size); -> double *buf = malloc((size) * sizeof(double));
    content = re.sub(
        r"ARRAY_ALLOC\(\s*([^,]+?)\s*,\s*([^)]+?)\s*\)\s*;",
        r"double *\1 = malloc((\2) * sizeof(double));",
        content,
    )

    # ARRAY_INT_ALLOC(buf, size); -> int *buf = malloc((size) * sizeof(int));
    content = re.sub(
        r"ARRAY_INT_ALLOC\(\s*([^,]+?)\s*,\s*([^)]+?)\s*\)\s*;",
        r"int *\1 = malloc((\2) * sizeof(int));",
        content,
    )

    # ARRAY_INT_FREE(buf); -> free(buf);
    content = re.sub(r"ARRAY_INT_FREE\(\s*([^)]+?)\s*\)\s*;", r"free(\1);", content)

    # ARRAY_INT_REF(buf) -> int *buf
    content = re.sub(r"ARRAY_INT_REF\(\s*([^)]+?)\s*\)", r"int *\1", content)

    # ARRAY_INT_LOCAL(buf, size); -> int buf[size];
    content = re.sub(
        r"ARRAY_INT_LOCAL\(\s*([^,]+?)\s*,\s*([^)]+?)\s*\)\s*;",
        r"int \1[\2];",
        content,
    )

    # ARRAY_FREE(buf); -> free(buf);
    content = re.sub(r"ARRAY_FREE\(\s*([^)]+?)\s*\)\s*;", r"free(\1);", content)

    # ARRAY_FREE_COND(flag, buf); -> if (flag) { free(buf); }
    content = re.sub(
        r"ARRAY_FREE_COND\(\s*([^,]+?)\s*,\s*([^)]+?)\s*\)\s*;",
        r"if (\1) { free(\2); }",
        content,
    )

    # TA_ARRAY_COPY(dst, dstOff, src, srcOff, n);
    content = re.sub(
        r"TA_ARRAY_COPY\(\s*([^,]+?)\s*,\s*([^,]+?)\s*,\s*([^,]+?)\s*,\s*([^,]+?)\s*,\s*([^)]+?)\s*\)\s*;",
        r"memcpy(&\1[\2], &\3[\4], (\5) * sizeof(double));",
        content,
    )

    # ARRAY_COPY(dst, src, count); -> memcpy(dst, src, (count) * sizeof(double));
    content = re.sub(
        r"ARRAY_COPY\(\s*([^,]+?)\s*,\s*([^,]+?)\s*,\s*([^)]+?)\s*\)\s*;",
        r"memcpy(\1, \2, (\3) * sizeof(double));",
        content,
    )

    # ARRAY_MEMMOVE(dst, dOff, src, sOff, n);
    content = re.sub(
        r"ARRAY_MEMMOVE\(\s*([^,]+?)\s*,\s*([^,]+?)\s*,\s*([^,]+?)\s*,\s*([^,]+?)\s*,\s*([^)]+?)\s*\)\s*;",
        r"memmove(&\1[\2], &\3[\4], (\5) * sizeof(double));",
        content,
    )

    return content


def replace_value_handle_macros(content: str) -> str:
    # VALUE_HANDLE_INT(x); -> int x;
    content = re.sub(
        r"VALUE_HANDLE_INT\(\s*([^)]+?)\s*\)\s*;",
        r"int \1;",
        content,
    )

    # VALUE_HANDLE_GET(x) -> x
    content = re.sub(r"VALUE_HANDLE_GET\(\s*([^)]+?)\s*\)", r"\1", content)

    # VALUE_HANDLE_OUT(x) -> &x
    content = re.sub(r"VALUE_HANDLE_OUT\(\s*([^)]+?)\s*\)", r"&\1", content)

    return content


def replace_circbuf_macros(content: str) -> str:
    # CIRCBUF_PROLOG(buf, type, size); -> type buf[size]; int buf_Idx = 0;
    content = re.sub(
        r"CIRCBUF_PROLOG\(\s*([^,]+?)\s*,\s*([^,]+?)\s*,\s*([^)]+?)\s*\)\s*;",
        r"\2 \1[\3]; int \1_Idx = 0;",
        content,
    )

    # CIRCBUF_PROLOG_CLASS(buf, Type, size); -> Type buf[size]; int buf_Idx = 0;
    content = re.sub(
        r"CIRCBUF_PROLOG_CLASS\(\s*([^,]+?)\s*,\s*([^,]+?)\s*,\s*([^)]+?)\s*\)\s*;",
        r"\2 \1[\3]; int \1_Idx = 0;",
        content,
    )

    # CIRCBUF_INIT_LOCAL_ONLY(...); -> /* circular buffer already declared */
    content = re.sub(
        r"CIRCBUF_INIT_LOCAL_ONLY\([^)]*\)\s*;",
        r"/* circular buffer already declared */",
        content,
    )

    # CIRCBUF_INIT_CLASS(buf, Type, size); -> memset(buf, 0, (size) * sizeof(Type)); buf_Idx = 0;
    content = re.sub(
        r"CIRCBUF_INIT_CLASS\(\s*([^,]+?)\s*,\s*([^,]+?)\s*,\s*([^)]+?)\s*\)\s*;",
        r"memset(\1, 0, (\3) * sizeof(\2)); \1_Idx = 0;",
        content,
    )

    # CIRCBUF_DESTROY(buf); -> /* circular buffer cleanup (stack-allocated, no-op) */
    content = re.sub(
        r"CIRCBUF_DESTROY\(\s*([^)]+?)\s*\)\s*;",
        r"/* circular buffer cleanup (stack-allocated, no-op) */",
        content,
    )

    # CIRCBUF_REF(name[idx])field -> name[idx].field
    content = re.sub(
        r"CIRCBUF_REF\((\w+)\[(\w+)\]\)(\w+)",
        r"\1[\2].\3",
        content,
    )

    # CIRCBUF_INIT(buf, type, size); -> memset(buf, 0, (size) * sizeof(type)); buf_Idx = 0;
    content = re.sub(
        r'CIRCBUF_INIT\(\s*([^,]+?)\s*,\s*([^,]+?)\s*,\s*([^)]+?)\s*\)\s*;',
        r'memset(\1, 0, (\3) * sizeof(\2)); \1_Idx = 0;',
        content,
    )

    # NOTE: CIRCBUF_NEXT is intentionally NOT replaced — needs manual fixing per file

    return content


def process_file(filepath: Path, dry_run: bool = False) -> list[str]:
    """Process a single .c file, return list of changes made."""
    original = filepath.read_text()
    content = original
    content = replace_simple_macros(content)
    content = replace_array_macros(content)
    content = replace_value_handle_macros(content)
    content = replace_circbuf_macros(content)
    if content == original:
        return []
    changes = []
    for i, (orig_line, new_line) in enumerate(
        zip(original.splitlines(), content.splitlines()), 1
    ):
        if orig_line != new_line:
            changes.append(f"  L{i}: {orig_line.strip()} -> {new_line.strip()}")
    if not dry_run:
        filepath.write_text(content)
    return changes


def main():
    import argparse

    parser = argparse.ArgumentParser(description="Replace macros in ta_func_defs .c files")
    parser.add_argument("--dry-run", action="store_true")
    parser.add_argument("--file", type=str)
    args = parser.parse_args()

    base = Path(__file__).parent.parent / "ta_func_defs"
    if args.file:
        files = [Path(args.file)]
    else:
        files = sorted(base.glob("*/*.c"))

    total_changes = 0
    for filepath in files:
        changes = process_file(filepath, dry_run=args.dry_run)
        if changes:
            print(f"\n{filepath.relative_to(base)}:")
            for c in changes[:10]:
                print(c)
            if len(changes) > 10:
                print(f"  ... and {len(changes) - 10} more")
            total_changes += len(changes)

    mode = "Would change" if args.dry_run else "Changed"
    print(f"\n{mode} {total_changes} lines across {len(files)} files")


if __name__ == "__main__":
    main()
