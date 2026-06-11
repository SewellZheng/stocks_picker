#!/usr/bin/env python3
"""
Manual helper to prepare/update a TA-Lib vcpkg port PR.

Why this exists:
- Homebrew already has manual post-release support via post-release-brew.py.
- vcpkg maintainers welcomed a manual PR workflow for TA-Lib updates.

What this script does:
1) Reads TA-Lib version from the repository VERSION file.
2) Downloads/caches the latest release source tarball and computes SHA512 (required by vcpkg).
3) Prints ready-to-copy update instructions for microsoft/vcpkg.
4) Optionally updates a local vcpkg checkout (versions + baseline) when provided.

Notes:
- This script is intentionally conservative and defaults to "plan/output" mode.
- It does not push branches by default.
"""

from __future__ import annotations

import argparse
import hashlib
import json
import re
import shutil
import sys
from pathlib import Path
from urllib.request import Request, urlopen

from utilities.common import run_command
from utilities.files import path_join
from utilities.versions import get_version_string

API_RELEASE_LATEST = "https://api.github.com/repos/TA-Lib/ta-lib/releases/latest"
ASSET_PATTERN = re.compile(r"ta-lib-(\d+\.\d+\.\d+)-src\.tar\.gz$")
VCPKG_PORT_NAME = "talib"


def sha512_file(path: Path) -> str:
    h = hashlib.sha512()
    with path.open("rb") as f:
        for chunk in iter(lambda: f.read(1024 * 1024), b""):
            h.update(chunk)
    return h.hexdigest()


def download(url: str, out: Path) -> None:
    out.parent.mkdir(parents=True, exist_ok=True)
    req = Request(url, headers={"User-Agent": "ta-lib-post-release-script"})
    with urlopen(req) as r, out.open("wb") as f:
        while True:
            chunk = r.read(1024 * 1024)
            if not chunk:
                break
            f.write(chunk)


def fetch_json(url: str) -> dict:
    req = Request(url, headers={"Accept": "application/vnd.github+json", "User-Agent": "ta-lib-post-release-script"})
    with urlopen(req) as r:
        return json.loads(r.read().decode("utf-8"))


def version_key(v: str) -> tuple[int, int, int]:
    m = re.fullmatch(r"(\d+)\.(\d+)\.(\d+)", v)
    if not m:
        raise ValueError(f"Invalid version: {v}")
    return int(m.group(1)), int(m.group(2)), int(m.group(3))


def read_latest_release_src_tarball() -> tuple[str, str, str]:
    data = fetch_json(API_RELEASE_LATEST)
    assets = data.get("assets", [])
    for a in assets:
        name = a.get("name", "")
        m = ASSET_PATTERN.search(name)
        if m:
            version = m.group(1)
            return version, name, a.get("browser_download_url", "")
    raise RuntimeError("Could not find ta-lib-<version>-src.tar.gz asset in latest release")


def read_cached_latest_release_src_tarball(out_dir: Path) -> tuple[str, str, Path]:
    candidates: list[tuple[tuple[int, int, int], str, Path]] = []
    for p in out_dir.glob("ta-lib-*-src.tar.gz"):
        m = ASSET_PATTERN.search(p.name)
        if not m:
            continue
        version = m.group(1)
        candidates.append((version_key(version), version, p))
    if not candidates:
        raise RuntimeError("No cached ta-lib-<version>-src.tar.gz found")
    _, version, path = sorted(candidates)[-1]
    return version, path.name, path


def read_repo_version() -> str:
    root_dir = Path(path_join(Path(__file__).resolve().parent, "..")).resolve()
    return get_version_string(str(root_dir))


def update_vcpkg_files(vcpkg_root: Path, version: str, sha512: str) -> None:
    """
    Best-effort local file updates for a standard vcpkg checkout.
    If expected files are missing, print guidance and return.
    """
    portfile = vcpkg_root / "ports" / VCPKG_PORT_NAME / "portfile.cmake"
    vcpkg_json = vcpkg_root / "ports" / VCPKG_PORT_NAME / "vcpkg.json"

    if not portfile.exists() or not vcpkg_json.exists():
        print(f"[warn] Could not find ports/{VCPKG_PORT_NAME} files in provided vcpkg checkout.")
        print("       Please update the port manually in microsoft/vcpkg.")
        return

    # Update version in vcpkg.json while preserving the current versioning scheme.
    # vcpkg manifests must contain exactly one of:
    # version, version-date, version-semver, version-string.
    data = json.loads(vcpkg_json.read_text())
    version_keys = ["version", "version-date", "version-semver", "version-string"]
    if "version-semver" in data:
        target_version_key = "version-semver"
    elif "version-string" in data:
        target_version_key = "version-string"
    elif "version-date" in data:
        target_version_key = "version-date"
    elif "version" in data:
        target_version_key = "version"
    else:
        target_version_key = "version"

    for key in version_keys:
        data.pop(key, None)
    data[target_version_key] = version
    vcpkg_json.write_text(json.dumps(data, indent=2) + "\n")

    # Replace SHA512 in portfile.cmake (first SHA512 occurrence)
    text = portfile.read_text()
    text_new = re.sub(r"(SHA512\s+)[0-9a-fA-F]{64,128}", rf"\1{sha512}", text, count=1)
    if text_new == text:
        print("[warn] Could not auto-replace SHA512 in portfile.cmake. Update manually.")
    else:
        portfile.write_text(text_new)

    print(f"[ok] Updated local vcpkg {VCPKG_PORT_NAME} port files.")


def ensure_vcpkg_checkout(vcpkg_root: Path) -> Path:
    vcpkg_root = vcpkg_root.resolve()
    if (vcpkg_root / ".git").exists():
        print(f"[ok] Using existing vcpkg checkout: {vcpkg_root}")
        try:
            run_command(["git", "fetch", "--all", "--prune"], cwd=str(vcpkg_root))
        except Exception as e:
            print(f"[warn] Could not fetch vcpkg remote: {e}")
        return vcpkg_root

    # Recover from older path bug where clone target was created nested under out_dir.
    nested_candidates = [
        p for p in vcpkg_root.parent.rglob("vcpkg")
        if p != vcpkg_root and (p / ".git").exists()
    ]
    if nested_candidates:
        nested = sorted(nested_candidates, key=lambda p: len(str(p)))[0]
        print(f"[warn] Found nested vcpkg checkout at: {nested}")
        print("[warn] Reusing it for this run.")
        return nested

    vcpkg_root.parent.mkdir(parents=True, exist_ok=True)
    try:
        run_command(
            ["git", "clone", "https://github.com/microsoft/vcpkg.git", str(vcpkg_root)],
            cwd=str(vcpkg_root.parent),
        )
        print(f"[ok] Cloned vcpkg into: {vcpkg_root}")
        return vcpkg_root
    except Exception as e:
        raise RuntimeError(
            f"Could not clone microsoft/vcpkg into {vcpkg_root}: {e}. "
            "Pass --vcpkg-root to an existing local checkout."
        )


def run_x_add_version(vcpkg_root: Path) -> None:
    vcpkg_exe = vcpkg_root / "vcpkg"
    if not vcpkg_exe.exists():
        bootstrap = vcpkg_root / "bootstrap-vcpkg.sh"
        if bootstrap.exists():
            try:
                run_command([str(bootstrap)], cwd=str(vcpkg_root))
            except Exception as e:
                print(f"[warn] bootstrap-vcpkg.sh failed: {e}")
                print("[warn] Skipping x-add-version. Run it manually when network/toolchain is available.")
                return
        if not vcpkg_exe.exists():
            print(f"[warn] Could not find {vcpkg_exe}; skipping x-add-version")
            return
    manifest_path = vcpkg_root / "ports" / VCPKG_PORT_NAME / "vcpkg.json"
    if manifest_path.exists():
        try:
            run_command([str(vcpkg_exe), "format-manifest", str(manifest_path)], cwd=str(vcpkg_root))
        except Exception as e:
            print(f"[warn] format-manifest failed: {e}")
            print(f"[warn] Run manually later: ./vcpkg format-manifest {manifest_path}")
            return
    try:
        run_command([str(vcpkg_exe), "x-add-version", VCPKG_PORT_NAME], cwd=str(vcpkg_root))
    except Exception as e:
        print(f"[warn] x-add-version failed: {e}")
        print(f"[warn] Run manually later: ./vcpkg x-add-version {VCPKG_PORT_NAME}")
        return
    print(f"[ok] Ran ./vcpkg x-add-version {VCPKG_PORT_NAME}")


def commit_and_open_pr(vcpkg_root: Path, version: str) -> None:
    run_command(["git", "checkout", "-B", f"ta-lib-{version}"], cwd=str(vcpkg_root))
    run_command(["git", "add", "-A"], cwd=str(vcpkg_root))

    status = run_command(["git", "status", "--porcelain"], cwd=str(vcpkg_root))
    if not status.strip():
        print("[warn] No changes to commit.")
        return

    run_command(["git", "commit", "-m", f"[ta-lib] update to {version}"], cwd=str(vcpkg_root))
    run_command(["git", "push", "-u", "origin", f"ta-lib-{version}"], cwd=str(vcpkg_root))

    gh_path = shutil.which("gh")
    if gh_path is None:
        print("[warn] GitHub CLI not found; skipping PR creation.")
        print("       Run manually: gh pr create --repo microsoft/vcpkg --fill")
        return

    run_command([gh_path, "pr", "create", "--repo", "microsoft/vcpkg", "--fill"], cwd=str(vcpkg_root))
    print("[ok] Opened PR in microsoft/vcpkg")


def main() -> int:
    p = argparse.ArgumentParser(description="Prepare TA-Lib vcpkg update info")
    p.add_argument("--vcpkg-root", help="Optional path to local microsoft/vcpkg checkout")
    p.add_argument("--out-dir", default="temp/post-release-vcpkg", help="Download/cache directory")
    p.add_argument(
        "--perform-local-changes",
        action="store_true",
        help=f"Perform: clone/fetch vcpkg, update {VCPKG_PORT_NAME} port files, and run ./vcpkg x-add-version {VCPKG_PORT_NAME}",
    )
    p.add_argument(
        "--create-pr",
        action="store_true",
        help="Perform: commit changes, push branch, and open a PR with gh",
    )
    args = p.parse_args()

    out_dir = Path(args.out_dir).resolve()
    version = read_repo_version()
    try:
        release_version, tar_name, asset_url = read_latest_release_src_tarball()
        tar_path = out_dir / tar_name
        if not tar_path.exists():
            print(f"Downloading {asset_url}")
            download(asset_url, tar_path)
        else:
            print(f"Using cached tarball: {tar_path}")
    except Exception as e:
        print(f"[warn] Failed to read/download latest release tarball: {e}")
        release_version, tar_name, tar_path = read_cached_latest_release_src_tarball(out_dir)
        asset_url = "(cached/offline)"
        print(f"[ok] Falling back to cached latest tarball: {tar_path}")

    if release_version != version:
        raise RuntimeError(
            f"VERSION file has {version}, but latest release tarball is {release_version}. "
            "Update VERSION to the released version (or publish the matching release) and rerun."
        )

    sha512 = sha512_file(tar_path)

    print("\n=== TA-Lib vcpkg update plan ===")
    print(f"Version : {version}")
    print(f"Asset   : {asset_url}")
    print(f"SHA512  : {sha512}")
    print("\nSuggested next steps:")
    print("1) Clone/fetch microsoft/vcpkg")
    print(f"2) Update ports/{VCPKG_PORT_NAME}/{{portfile.cmake,vcpkg.json}}")
    print(f"3) Run: ./vcpkg x-add-version {VCPKG_PORT_NAME}")
    print("4) Commit + open PR to microsoft/vcpkg")

    vcpkg_root: Path | None = None
    if args.perform_local_changes or args.create_pr:
        vcpkg_root = Path(args.vcpkg_root) if args.vcpkg_root else Path(path_join(out_dir, "vcpkg"))
        vcpkg_root = ensure_vcpkg_checkout(vcpkg_root)

    if args.perform_local_changes:
        assert vcpkg_root is not None
        update_vcpkg_files(vcpkg_root, version, sha512)
        run_x_add_version(vcpkg_root)
    elif args.vcpkg_root:
        update_vcpkg_files(Path(args.vcpkg_root), version, sha512)

    if args.create_pr:
        assert vcpkg_root is not None
        commit_and_open_pr(vcpkg_root, version)

    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except Exception as e:
        print(f"Error: {e}")
        raise SystemExit(1)
