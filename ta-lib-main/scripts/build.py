#!/usr/bin/env python3

# Developer build helper for TA-Lib.
#
# Wraps CMake so you can build and test from any directory within the repo.
#
# Usage:
#   scripts/build.py                Build library + all tools
#   scripts/build.py ta_regtest     Build the regression test runner
#   scripts/build.py gen_code       Build the legacy C code generator
#   scripts/build.py ta_codegen     Build the Rust codegen tool
#   scripts/build.py generate       Generate per-function source for all backends
#   scripts/build.py servers        Generate + compile JSON-RPC language servers
#   scripts/build.py test           C reference regression tests
#   scripts/build.py regtest        Full cross-language regression tests
#   scripts/build.py regtest-only   Codegen verification only (skip C tests)
#   scripts/build.py clean          Remove build directory
#   scripts/build.py help           Show all targets

import argparse
import os
import shlex
import shutil
import subprocess
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from utilities.common import (
    check_prerequisites,
    PREREQS_BUILD_BASIC, PREREQS_BUILD_CODEGEN, PREREQS_BUILD_SERVERS,
)

BUILD_DIR_NAME = "cmake-build"
DEFAULT_BUILD_TYPE = "Release"
DEFAULT_JOBS = os.cpu_count() or 4

def find_repo_root() -> str:
    """Find the git repository root, regardless of where the script is called from."""
    # Use the script's own location to find the repo, so it works even
    # when cwd is outside the repository.
    script_dir = os.path.dirname(os.path.abspath(__file__))
    try:
        result = subprocess.run(
            ['git', 'rev-parse', '--show-toplevel'],
            check=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE,
            cwd=script_dir
        )
        root = result.stdout.strip().decode('utf-8')
    except (subprocess.CalledProcessError, FileNotFoundError):
        print("Error: Must be run from within a TA-Lib Git repository.")
        sys.exit(1)

    # Sanity check
    if not os.path.isdir(os.path.join(root, 'src', 'ta_func')):
        print("Error: Not a complete TA-Lib repository (src/ta_func missing).")
        sys.exit(1)

    return root

def ensure_configured(root_dir: str, build_dir: str, build_type: str, cmake_args: str):
    """Ensure cmake-build exists and is configured. Reconfigure if CMakeLists.txt is newer."""
    cmake_cache = os.path.join(build_dir, "CMakeCache.txt")
    cmakelists = os.path.join(root_dir, "CMakeLists.txt")
    needs_configure = False

    if not os.path.exists(cmake_cache):
        needs_configure = True
    elif os.path.getmtime(cmakelists) > os.path.getmtime(cmake_cache):
        needs_configure = True

    if needs_configure:
        os.makedirs(build_dir, exist_ok=True)
        cmd = ['cmake', root_dir, f'-DCMAKE_BUILD_TYPE={build_type}']
        if cmake_args:
            cmd.extend(shlex.split(cmake_args))
        subprocess.run(cmd, check=True, cwd=build_dir)

def cmake_build(build_dir: str, target: str = None, jobs: int = DEFAULT_JOBS):
    """Run cmake --build, optionally for a specific target."""
    cmd = ['cmake', '--build', '.', '-j', str(jobs)]
    if target:
        cmd.extend(['--target', target])
    subprocess.run(cmd, check=True, cwd=build_dir)

def show_help():
    print("""TA-Lib Build Targets

  Building:
    (default)           Build library + all tools
    ta_regtest          Build the regression test runner
    gen_code            Build the legacy C code generator
    ta_codegen          Build the Rust codegen tool
    generate            Generate per-function source for all backends
    servers             Generate all source + compile JSON-RPC language servers

  Testing:
    test                C reference regression tests
    regtest             Full pipeline: servers + C tests + codegen verification
    regtest-only        Codegen verification only (skip C tests)

  Other:
    clean               Remove cmake-build/
    help                Show this message

  Options:
    --build-type=Debug  Set cmake build type (default: Release)
    --jobs=8            Parallel jobs (default: number of CPUs)
    --cmake-args="..."  Extra arguments passed to cmake configure
""")

# Each target maps to cmake target(s). Use only the final dependency target
# when earlier targets are already wired as CMake dependencies.
SIMPLE_TARGETS = {
    'ta_regtest':  'ensure_ta_regtest_in_bin',
    'gen_code':    'ensure_gen_code_in_bin',
    'ta_codegen':  'ta_codegen_bin',
    'generate':    'ta_codegen_generate',
    'servers':     'ta_codegen_servers',
    'test':        'test',
    'regtest':     'regtest',
    'regtest-only':'regtest-only',
}

# Map each target to the prerequisite set it requires.
TARGET_PREREQS = {
    'all':          PREREQS_BUILD_BASIC,
    'ta_regtest':   PREREQS_BUILD_BASIC,
    'gen_code':     PREREQS_BUILD_BASIC,
    'ta_codegen':   PREREQS_BUILD_CODEGEN,
    'generate':     PREREQS_BUILD_CODEGEN,
    'servers':      PREREQS_BUILD_SERVERS,
    'test':         PREREQS_BUILD_BASIC,
    'regtest':      PREREQS_BUILD_SERVERS,
    'regtest-only': PREREQS_BUILD_SERVERS,
}

def main():
    # Refuse to run as root/sudo (Unix only).
    if hasattr(os, 'getuid') and os.getuid() == 0:
        print("Error: Do not run this script as root or with sudo.")
        sys.exit(1)

    parser = argparse.ArgumentParser(
        description="TA-Lib developer build helper",
        add_help=False,
    )
    parser.add_argument('target', nargs='?', default='all')
    parser.add_argument('--build-type', default=DEFAULT_BUILD_TYPE)
    parser.add_argument('--jobs', '-j', type=int, default=DEFAULT_JOBS)
    parser.add_argument('--cmake-args', default='')
    parser.add_argument('--help', '-h', action='store_true')
    args = parser.parse_args()

    if args.help or args.target == 'help':
        show_help()
        return

    root_dir = find_repo_root()
    build_dir = os.path.join(root_dir, BUILD_DIR_NAME)

    if args.target == 'clean':
        try:
            shutil.rmtree(build_dir)
            print(f"Removed {build_dir}")
        except FileNotFoundError:
            print("Nothing to clean.")
        return

    check_prerequisites(TARGET_PREREQS.get(args.target, PREREQS_BUILD_BASIC))

    ensure_configured(root_dir, build_dir, args.build_type, args.cmake_args)

    if args.target == 'all':
        cmake_build(build_dir, jobs=args.jobs)
    elif args.target in SIMPLE_TARGETS:
        cmake_build(build_dir, target=SIMPLE_TARGETS[args.target], jobs=args.jobs)
    else:
        print(f"Error: Unknown target '{args.target}'. Run with 'help' to see available targets.")
        sys.exit(1)

if __name__ == '__main__':
    main()
