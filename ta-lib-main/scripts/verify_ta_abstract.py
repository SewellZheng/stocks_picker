#!/usr/bin/env python3
"""Compare ta_abstract JSON dumps between c-ref and generated implementations.

Usage:
    python scripts/verify_ta_abstract.py [ref.json] [new.json]

If no arguments given, builds and runs both dump tools automatically.
"""

import json
import sys
import subprocess
import os

def load_json(path):
    with open(path) as f:
        return json.load(f)

def compare_values(path, ref_val, new_val):
    """Compare two values, return list of (path, ref, new) differences."""
    diffs = []
    if isinstance(ref_val, dict) and isinstance(new_val, dict):
        all_keys = set(ref_val.keys()) | set(new_val.keys())
        for k in sorted(all_keys):
            if k not in ref_val:
                diffs.append((f"{path}.{k}", "<missing>", new_val[k]))
            elif k not in new_val:
                diffs.append((f"{path}.{k}", ref_val[k], "<missing>"))
            else:
                diffs.extend(compare_values(f"{path}.{k}", ref_val[k], new_val[k]))
    elif isinstance(ref_val, list) and isinstance(new_val, list):
        if len(ref_val) != len(new_val):
            diffs.append((f"{path}[len]", len(ref_val), len(new_val)))
        for i in range(min(len(ref_val), len(new_val))):
            diffs.extend(compare_values(f"{path}[{i}]", ref_val[i], new_val[i]))
    elif isinstance(ref_val, float) and isinstance(new_val, float):
        if abs(ref_val - new_val) > 1e-10:
            diffs.append((path, ref_val, new_val))
    elif ref_val != new_val:
        diffs.append((path, ref_val, new_val))
    return diffs

def compare_functions(ref_funcs, new_funcs):
    """Compare function arrays, matching by name."""
    ref_by_name = {f["name"]: f for f in ref_funcs}
    new_by_name = {f["name"]: f for f in new_funcs}

    only_ref = sorted(set(ref_by_name) - set(new_by_name))
    only_new = sorted(set(new_by_name) - set(ref_by_name))
    common = sorted(set(ref_by_name) & set(new_by_name))

    per_func_diffs = {}
    for name in common:
        diffs = compare_values(name, ref_by_name[name], new_by_name[name])
        if diffs:
            per_func_diffs[name] = diffs

    return only_ref, only_new, per_func_diffs

def main():
    if len(sys.argv) >= 3:
        ref_path = sys.argv[1]
        new_path = sys.argv[2]
    else:
        # Auto mode: expect pre-built JSON files
        ref_path = "/tmp/ta_abstract_ref.json"
        new_path = "/tmp/ta_abstract_new.json"
        if not os.path.exists(ref_path):
            print(f"ERROR: {ref_path} not found. Run:")
            print("  bin/ta_abstract_dump_ref > /tmp/ta_abstract_ref.json")
            sys.exit(1)
        if not os.path.exists(new_path):
            print(f"ERROR: {new_path} not found. Run:")
            print("  bin/ta_abstract_dump_new > /tmp/ta_abstract_new.json")
            sys.exit(1)

    ref = load_json(ref_path)
    new = load_json(new_path)

    print(f"Reference: {ref['totalFunctions']} functions")
    print(f"Generated: {new['totalFunctions']} functions")
    print()

    # Compare groups
    if ref["groups"] != new["groups"]:
        print("GROUPS DIFFER:")
        print(f"  ref: {ref['groups']}")
        print(f"  new: {new['groups']}")
    else:
        print(f"Groups: MATCH ({len(ref['groups'])} groups)")

    # Compare functions_by_group
    group_diffs = compare_values("functions_by_group", ref.get("functions_by_group", {}), new.get("functions_by_group", {}))
    if group_diffs:
        print(f"\nGroup function lists: {len(group_diffs)} differences")
        for path, rv, nv in group_diffs:
            print(f"  {path}: ref={rv}, new={nv}")
    else:
        print("Group function lists: MATCH")

    # Compare individual functions
    only_ref, only_new, per_func_diffs = compare_functions(
        ref["functions"], new["functions"]
    )

    if only_ref:
        print(f"\nFunctions only in REFERENCE ({len(only_ref)}):")
        for name in only_ref:
            print(f"  {name}")

    if only_new:
        print(f"\nFunctions only in GENERATED ({len(only_new)}):")
        for name in only_new:
            print(f"  {name}")

    matched = ref["totalFunctions"] - len(only_ref)
    differing = len(per_func_diffs)
    perfect = matched - differing

    print(f"\nFunction comparison: {perfect}/{matched} perfect match, {differing} with differences")

    if per_func_diffs:
        print(f"\n{'='*70}")
        print(f"DIFFERENCES ({differing} functions):")
        print(f"{'='*70}")
        for name, diffs in sorted(per_func_diffs.items()):
            print(f"\n  {name} ({len(diffs)} diffs):")
            for path, rv, nv in diffs[:20]:  # cap at 20 per function
                print(f"    {path}")
                print(f"      ref: {rv}")
                print(f"      new: {nv}")
            if len(diffs) > 20:
                print(f"    ... and {len(diffs) - 20} more")

    total_diffs = len(only_ref) + len(only_new) + sum(len(d) for d in per_func_diffs.values())
    if total_diffs == 0:
        print("\n*** ALL MATCH ***")
        return 0
    else:
        print(f"\n*** {total_diffs} total differences ***")
        return 1

if __name__ == "__main__":
    sys.exit(main())
