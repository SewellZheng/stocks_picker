#!/usr/bin/env python3
"""Replace candlestick macros in ta_codegen/input extracted .c files with helper function calls."""

import re
from pathlib import Path


def replace_simple_candle_macros(content: str) -> str:
    # TA_REALBODY(IDX) -> ta_realbody(inClose[IDX], inOpen[IDX])
    content = re.sub(
        r"\bTA_REALBODY\(\s*([^)]+?)\s*\)",
        lambda m: f"ta_realbody(inClose[{m.group(1)}], inOpen[{m.group(1)}])",
        content,
    )

    # TA_CANDLECOLOR(IDX) -> ta_candlecolor(inClose[IDX], inOpen[IDX])
    content = re.sub(
        r"\bTA_CANDLECOLOR\(\s*([^)]+?)\s*\)",
        lambda m: f"ta_candlecolor(inClose[{m.group(1)}], inOpen[{m.group(1)}])",
        content,
    )

    # TA_UPPERSHADOW(IDX) -> ta_uppershadow(inHigh[IDX], inClose[IDX], inOpen[IDX])
    content = re.sub(
        r"\bTA_UPPERSHADOW\(\s*([^)]+?)\s*\)",
        lambda m: f"ta_uppershadow(inHigh[{m.group(1)}], inClose[{m.group(1)}], inOpen[{m.group(1)}])",
        content,
    )

    # TA_LOWERSHADOW(IDX) -> ta_lowershadow(inLow[IDX], inClose[IDX], inOpen[IDX])
    content = re.sub(
        r"\bTA_LOWERSHADOW\(\s*([^)]+?)\s*\)",
        lambda m: f"ta_lowershadow(inLow[{m.group(1)}], inClose[{m.group(1)}], inOpen[{m.group(1)}])",
        content,
    )

    # TA_HIGHLOWRANGE(IDX) -> ta_highlowrange(inHigh[IDX], inLow[IDX])
    content = re.sub(
        r"\bTA_HIGHLOWRANGE\(\s*([^)]+?)\s*\)",
        lambda m: f"ta_highlowrange(inHigh[{m.group(1)}], inLow[{m.group(1)}])",
        content,
    )

    return content


def replace_gap_macros(content: str) -> str:
    # TA_REALBODYGAPUP(IDX1, IDX2) -> ta_realbodygapup(inOpen[IDX1], inClose[IDX1], inOpen[IDX2], inClose[IDX2])
    content = re.sub(
        r"\bTA_REALBODYGAPUP\(\s*([^,]+?)\s*,\s*([^)]+?)\s*\)",
        lambda m: (
            f"ta_realbodygapup(inOpen[{m.group(1)}], inClose[{m.group(1)}],"
            f" inOpen[{m.group(2)}], inClose[{m.group(2)}])"
        ),
        content,
    )

    # TA_REALBODYGAPDOWN(IDX1, IDX2) -> ta_realbodygapdown(inOpen[IDX1], inClose[IDX1], inOpen[IDX2], inClose[IDX2])
    content = re.sub(
        r"\bTA_REALBODYGAPDOWN\(\s*([^,]+?)\s*,\s*([^)]+?)\s*\)",
        lambda m: (
            f"ta_realbodygapdown(inOpen[{m.group(1)}], inClose[{m.group(1)}],"
            f" inOpen[{m.group(2)}], inClose[{m.group(2)}])"
        ),
        content,
    )

    # TA_CANDLEGAPUP(IDX1, IDX2) -> ta_candlegapup(inLow[IDX1], inHigh[IDX2])
    content = re.sub(
        r"\bTA_CANDLEGAPUP\(\s*([^,]+?)\s*,\s*([^)]+?)\s*\)",
        lambda m: f"ta_candlegapup(inLow[{m.group(1)}], inHigh[{m.group(2)}])",
        content,
    )

    # TA_CANDLEGAPDOWN(IDX1, IDX2) -> ta_candlegapdown(inHigh[IDX1], inLow[IDX2])
    content = re.sub(
        r"\bTA_CANDLEGAPDOWN\(\s*([^,]+?)\s*,\s*([^)]+?)\s*\)",
        lambda m: f"ta_candlegapdown(inHigh[{m.group(1)}], inLow[{m.group(2)}])",
        content,
    )

    return content


def replace_candle_settings_macros(content: str) -> str:
    # TA_CANDLEAVGPERIOD(Set) -> Set_avgPeriod
    content = re.sub(
        r"\bTA_CANDLEAVGPERIOD\(\s*([A-Za-z]+)\s*\)",
        r"\1_avgPeriod",
        content,
    )

    # TA_CANDLERANGE(Set, IDX) -> ta_candlerange(Set_rangeType, inOpen[IDX], inHigh[IDX], inLow[IDX], inClose[IDX])
    content = re.sub(
        r"\bTA_CANDLERANGE\(\s*([A-Za-z]+)\s*,\s*([^)]+?)\s*\)",
        lambda m: (
            f"ta_candlerange({m.group(1)}_rangeType,"
            f" inOpen[{m.group(2)}], inHigh[{m.group(2)}],"
            f" inLow[{m.group(2)}], inClose[{m.group(2)}])"
        ),
        content,
    )

    # TA_CANDLEAVERAGE(Set, sum, IDX) -> ta_candleaverage(Set_rangeType, Set_avgPeriod, Set_factor, sum, inOpen[IDX], inHigh[IDX], inLow[IDX], inClose[IDX])
    content = re.sub(
        r"\bTA_CANDLEAVERAGE\(\s*([A-Za-z]+)\s*,\s*([^,]+?)\s*,\s*([^)]+?)\s*\)",
        lambda m: (
            f"ta_candleaverage({m.group(1)}_rangeType, {m.group(1)}_avgPeriod,"
            f" {m.group(1)}_factor, {m.group(2)},"
            f" inOpen[{m.group(3)}], inHigh[{m.group(3)}],"
            f" inLow[{m.group(3)}], inClose[{m.group(3)}])"
        ),
        content,
    )

    return content


def replace_local_define_macros(content: str) -> str:
    # TRUE_RANGE(th, tl, yc, out) -> out = ta_true_range(th, tl, yc)
    # Handles both "TRUE_RANGE(a,b,c,d);" and "TRUE_RANGE(a,b,c,d)" (with or without semicolon)
    content = re.sub(
        r"\bTRUE_RANGE\(\s*([^,]+?)\s*,\s*([^,]+?)\s*,\s*([^,]+?)\s*,\s*([^)]+?)\s*\)",
        lambda m: f"{m.group(4).strip()} = ta_true_range({m.group(1).strip()}, {m.group(2).strip()}, {m.group(3).strip()})",
        content,
    )

    # round_pos(x) -> ta_round_pos(x)
    # Must come after replacing the #define lines, so only call sites are affected
    content = re.sub(r"\bround_pos\(", "ta_round_pos(", content)

    # SAR_ROUNDING(x) -> ta_sar_rounding(x)
    content = re.sub(
        r"\bSAR_ROUNDING\(\s*([^)]+?)\s*\)\s*;",
        r"ta_sar_rounding(\1);",
        content,
    )

    return content


def remove_local_defines(content: str) -> str:
    # Remove multi-line #define TRUE_RANGE(...) { ... } blocks
    content = re.sub(
        r"[ \t]*#define\s+TRUE_RANGE\([^)]*\)\s*\{[^}]*\}\s*\n",
        "",
        content,
    )

    # Remove #undef round_pos lines
    content = re.sub(r"[ \t]*#undef\s+round_pos\s*\n", "", content)

    # Remove #define round_pos(x) (x) lines
    content = re.sub(r"[ \t]*#define\s+round_pos\([^)]*\)\s*\([^)]*\)\s*\n", "", content)

    return content


def process_file(filepath: Path, dry_run: bool = False) -> list[str]:
    """Process a single .c file, return list of changes made."""
    original = filepath.read_text()
    content = original
    content = remove_local_defines(content)
    content = replace_simple_candle_macros(content)
    content = replace_gap_macros(content)
    content = replace_candle_settings_macros(content)
    content = replace_local_define_macros(content)
    if content == original:
        return []
    changes = []
    orig_lines = original.splitlines()
    new_lines = content.splitlines()
    for i, (orig_line, new_line) in enumerate(zip(orig_lines, new_lines), 1):
        if orig_line != new_line:
            changes.append(f"  L{i}: {orig_line.strip()} -> {new_line.strip()}")
    # Report added/removed lines when counts differ
    if len(new_lines) != len(orig_lines):
        changes.append(
            f"  (line count: {len(orig_lines)} -> {len(new_lines)})"
        )
    if not dry_run:
        filepath.write_text(content)
    return changes


def main():
    import argparse

    parser = argparse.ArgumentParser(
        description="Replace candlestick macros in ta_codegen/input .c files"
    )
    parser.add_argument("--dry-run", action="store_true")
    parser.add_argument("--file", type=str)
    args = parser.parse_args()

    base = Path(__file__).parent.parent / "ta_codegen/input"
    if args.file:
        files = [Path(args.file)]
    else:
        all_files = sorted(base.glob("*/*.c"))
        helpers = base / "helpers"
        files = [f for f in all_files if not f.is_relative_to(helpers)]

    total_changes = 0
    for filepath in files:
        changes = process_file(filepath, dry_run=args.dry_run)
        if changes:
            try:
                label = filepath.resolve().relative_to(base.resolve())
            except ValueError:
                label = filepath
            print(f"\n{label}:")
            for c in changes[:10]:
                print(c)
            if len(changes) > 10:
                print(f"  ... and {len(changes) - 10} more")
            total_changes += len(changes)

    mode = "Would change" if args.dry_run else "Changed"
    print(f"\n{mode} {total_changes} lines across {len(files)} files")


if __name__ == "__main__":
    main()
