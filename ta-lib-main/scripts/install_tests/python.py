import glob
import os
import shutil
import subprocess
import sys
import tarfile
import zipfile

# End-user simulation: install the release-candidate C package, then follow
# the ta-lib-python installation procedure — build the wrapper FROM SOURCE
# against it (pip --no-binary) in a fresh venv, import talib, and verify the
# reported TA-Lib version and a set of computed values.
#
# Any problem => sys.exit(1). Intermediate files stay in temp_dir for
# debugging.

# Runs inside the venv's python. Verifies the C library the wrapper linked:
# version string, classic values, multi-output alignment, and the 0.6.5
# period=1 semantics (MACD signalPeriod=1 == MACD line).
VERIFY_SCRIPT = r'''
import sys
import numpy as np
import talib

expected_version = sys.argv[1]

ta_ver = talib.__ta_version__
if isinstance(ta_ver, bytes):
    ta_ver = ta_ver.decode()
print(f"talib.__ta_version__ = {ta_ver}")
if not ta_ver.startswith(expected_version):
    print(f"FAIL: wrapper linked TA-Lib {ta_ver!r}, expected {expected_version}*")
    sys.exit(1)

c = np.array([91.50, 94.81, 94.38, 95.09, 93.78, 94.62, 92.53, 92.75,
              90.31, 92.47, 96.12, 97.25, 98.50, 89.88, 91.00, 92.81,
              89.16, 89.34, 91.62, 89.88], dtype=np.float64)

# SMA(3): straightforward hand-checkable pins.
sma = talib.SMA(c, timeperiod=3)
expected = (91.50 + 94.81 + 94.38) / 3.0
if abs(sma[2] - expected) > 1e-9:
    print(f"FAIL: SMA[2]={sma[2]!r}, expected {expected!r}")
    sys.exit(1)
if not np.isnan(sma[1]) or np.isnan(sma[-1]):
    print("FAIL: SMA nan padding wrong")
    sys.exit(1)

# RSI(14): output range sanity + lookback position.
rsi = talib.RSI(c, timeperiod=14)
if np.isnan(rsi[-1]) or not (0.0 <= rsi[-1] <= 100.0):
    print(f"FAIL: RSI[-1]={rsi[-1]!r}")
    sys.exit(1)

# Multi-output alignment.
macd, sig, hist = talib.MACD(c, fastperiod=3, slowperiod=4, signalperiod=2)
if np.isnan(macd[-1]) or np.isnan(sig[-1]) or np.isnan(hist[-1]):
    print("FAIL: MACD(3,4,2) tail is nan")
    sys.exit(1)
if abs((macd[-1] - sig[-1]) - hist[-1]) > 1e-9:
    print("FAIL: MACD hist != macd - signal")
    sys.exit(1)

# 0.6.5 period=1 semantics: signalPeriod=1 means no smoothing -- the signal
# IS the MACD line and the histogram is exactly 0 (issues #48/#59).
macd1, sig1, hist1 = talib.MACD(c, fastperiod=3, slowperiod=4, signalperiod=1)
valid = ~np.isnan(macd1)
if not valid.any():
    print("FAIL: MACD(3,4,1) produced no values")
    sys.exit(1)
if np.nanmax(np.abs(macd1[valid] - sig1[valid])) != 0.0 or np.nanmax(np.abs(hist1[valid])) != 0.0:
    print("FAIL: MACD signalPeriod=1 is not the identity signal (issues #48/#59)")
    sys.exit(1)
if np.isnan(macd1[-1]):
    print("FAIL: MACD(3,4,1) last bar missing (pre-0.6.5 truncation bug)")
    sys.exit(1)

# EMA period=1 = copy of input (min period is 1 since 0.6.5).
ema1 = talib.EMA(c, timeperiod=1)
if np.nanmax(np.abs(ema1 - c)) != 0.0:
    print("FAIL: EMA(period=1) is not an exact copy of the input")
    sys.exit(1)

print(f"OK: ta-lib-python built and verified against TA-Lib {ta_ver}")
'''


def log_params(host: str, package_file_path: str, temp_dir: str, version: str, sudo_pwd: str):
    print(f"Testing ta-lib-python installation on {host}")
    # Never display or log sudo_pwd, but want to know if it was specified.
    if sudo_pwd:
        hidden_sudo_pwd = "(hidden)"
    else:
        hidden_sudo_pwd = "\"\""

    print(f"  package_file_path={package_file_path}")
    print(f"  temp_dir={temp_dir}")
    print(f"  version={version}")
    print(f"  sudo_pwd={hidden_sudo_pwd}")

    # Create a dummy file "PARAMS" into temp_dir to help debugging.
    with open(os.path.join(temp_dir, "PARAMS"), "w") as f:
        f.write(f"package_file_path={package_file_path}\n")
        f.write(f"temp_dir={temp_dir}\n")
        f.write(f"version={version}\n")
        f.write(f"sudo_pwd={hidden_sudo_pwd}\n")

    return


def run_or_die(cmd, step: str, cwd: str = None, env: dict = None):
    print(f"  [{step}] {' '.join(str(a) for a in cmd)}")
    result = subprocess.run(cmd, cwd=cwd, env=env)
    if result.returncode != 0:
        print(f"FAIL: {step} exited with {result.returncode}")
        sys.exit(1)


def venv_python(venv_dir: str) -> str:
    if sys.platform == "win32":
        return os.path.join(venv_dir, "Scripts", "python.exe")
    return os.path.join(venv_dir, "bin", "python")


def build_wrapper_and_verify(temp_dir: str, version: str,
                             include_path: str = None, library_path: str = None):
    """Create a venv, build ta-lib-python from source against the installed
    C library (env overrides when given, system default paths otherwise),
    then run the verification script inside the venv."""
    venv_dir = os.path.join(temp_dir, "venv")
    run_or_die([sys.executable, "-m", "venv", venv_dir], "create venv")
    vpy = venv_python(venv_dir)

    env = os.environ.copy()
    if include_path:
        env["TA_INCLUDE_PATH"] = include_path
    if library_path:
        env["TA_LIBRARY_PATH"] = library_path

    run_or_die([vpy, "-m", "pip", "install", "--upgrade", "pip", "numpy"],
               "install numpy", env=env)
    # --no-binary TA-Lib: force the SOURCE build — the whole point is that
    # the wrapper compiles and links against THIS release candidate.
    run_or_die([vpy, "-m", "pip", "install", "-v", "--no-binary", "TA-Lib", "TA-Lib"],
               "build ta-lib-python from source", env=env)

    verify_file = os.path.join(temp_dir, "verify_talib.py")
    with open(verify_file, "w", encoding="utf-8") as f:
        f.write(VERIFY_SCRIPT)
    run_or_die([vpy, verify_file, version], "verify wrapper", env=env)


def test_python_windows(package_file_path: str, temp_dir: str, version: str, sudo_pwd: str):
    # Test installation procedure for ta-lib-python and validate
    # that this ta-lib package release candidate is OK.
    #
    # package_file_path is the ta-lib-{version}-windows-x86_64.zip.
    # The wrapper statically links lib/ta-lib-static.lib and resolves
    # headers as "ta-lib/xxx.h", so TA_INCLUDE_PATH must be the PARENT of
    # an include/ta-lib directory.
    log_params("Windows", package_file_path, temp_dir, version, sudo_pwd)

    pkg_dir = os.path.join(temp_dir, "pkg")
    with zipfile.ZipFile(package_file_path) as zf:
        zf.extractall(pkg_dir)

    include_dir = os.path.join(pkg_dir, "include")
    # Tolerate both layouts: include/ta-lib/*.h (0.6.5+) and the legacy flat
    # include/*.h (synthesize the ta-lib/ subdirectory the wrapper needs).
    if not os.path.isdir(os.path.join(include_dir, "ta-lib")):
        talib_inc = os.path.join(include_dir, "ta-lib")
        os.makedirs(talib_inc, exist_ok=True)
        for h in glob.glob(os.path.join(include_dir, "*.h")):
            shutil.move(h, talib_inc)
        print("  NOTE: package had flat headers; synthesized include/ta-lib/ for the wrapper")

    build_wrapper_and_verify(
        temp_dir, version,
        include_path=include_dir,
        library_path=os.path.join(pkg_dir, "lib"),
    )
    return


def test_python_linux(package_file_path: str, temp_dir: str, version: str, sudo_pwd: str):
    # Same as test_python_windows except package_file_path is the
    # ta-lib-{version}-src.tar.gz, installed the end-user way:
    # ./configure && make && sudo make install (then ldconfig).
    log_params("Linux", package_file_path, temp_dir, version, sudo_pwd)

    src_dir = os.path.join(temp_dir, "src")
    os.makedirs(src_dir, exist_ok=True)
    with tarfile.open(package_file_path) as tf:
        tf.extractall(src_dir)

    # The tarball extracts into a single ta-lib-* directory.
    entries = [d for d in os.listdir(src_dir) if os.path.isdir(os.path.join(src_dir, d))]
    if len(entries) != 1:
        print(f"FAIL: unexpected tarball layout: {entries}")
        sys.exit(1)
    configure_dir = os.path.join(src_dir, entries[0])

    run_or_die(["./configure"], "configure", cwd=configure_dir)
    run_or_die(["make", f"-j{os.cpu_count() or 2}"], "make", cwd=configure_dir)

    def sudo(cmd_tail, step):
        if sudo_pwd:
            full = ["sudo", "-S"] + cmd_tail
            print(f"  [{step}] sudo -S {' '.join(cmd_tail)}")
            result = subprocess.run(full, cwd=configure_dir, input=sudo_pwd + "\n", text=True)
        else:
            full = ["sudo"] + cmd_tail
            print(f"  [{step}] {' '.join(full)}")
            result = subprocess.run(full, cwd=configure_dir)
        if result.returncode != 0:
            print(f"FAIL: {step} exited with {result.returncode}")
            sys.exit(1)

    sudo(["make", "install"], "sudo make install")
    sudo(["ldconfig"], "ldconfig")

    # No env overrides: the wrapper must find /usr/local/{include/ta-lib,lib}
    # by itself, exactly like an end user.
    build_wrapper_and_verify(temp_dir, version)
    return
