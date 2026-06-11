# Instructions for TA-Lib maintainers
**If you only want to install and use TA-Lib, there is nothing here for you... check instead https://ta-lib.org/install **

You must have python installed.

## How to push changes to Github?
Modifications (or PR) must be made on the 'dev' branch.

Before committing, run ```scripts/sync.py``` to:
 - Ensure your local dev branch is up-to-date with both remote dev and main branches.
 - Do various check and fixes on your code (e.g. update "x.y.z" versioning in various files).

Merge to main branch are done with ```scripts/merge.py``` by TA-Lib maintainers with the proper permissions.

## How to update the "./configure" script
This will do all the needed autotools steps:
```$ autoreconf -fi```

Repeat whenever you need to refresh the makefiles.

## How to build and test with scripts/build.py

Prerequisites: CMake 3.18+, a C compiler (clang or gcc), the Rust toolchain (`rustup`), and `mcpp`.

For cross-language server testing (`servers`, `regtest`, `regtest-only` targets), also: JDK (`javac` + `java`) and .NET SDK (`dotnet`).

```
scripts/build.py                # Build library + all tools
scripts/build.py ta_regtest     # Build just the test runner
scripts/build.py gen_code       # Build the legacy C code generator
scripts/build.py ta_codegen     # Build the Rust codegen tool
scripts/build.py servers        # Generate + compile JSON-RPC language servers
```

Built binaries go to `bin/`. CMake is configured automatically on first run.

To run tests:
```
scripts/build.py test           # C reference tests only (quick)
scripts/build.py regtest        # Full pipeline: servers + C tests + cross-language verification
scripts/build.py regtest-only   # Codegen verification only (skip C reference tests)
```

For more control, run `ta_regtest` directly from `bin/`:
```
./ta_regtest                                               # C reference tests only
./ta_regtest --codegen                                     # C tests + all-language codegen
./ta_regtest --codegen-only                                # Codegen only (all languages)
./ta_regtest --codegen --language=c,rust                   # Filter to specific languages
./ta_regtest --codegen --function=RSI,SMA                  # Filter to specific functions
```

## How to run ta_codegen

In addition to gen_code (see below), a Rust tool `ta_codegen` generates the Rust indicator implementations and the JSON-RPC test servers:

```
cd tools/ta_codegen
cargo run -- generate                            # Generate indicator code for all backends
cargo run -- generate --func=SMA --backend=rust  # Specific function + backend
cargo run -- generate-servers                    # Generate JSON-RPC servers
cargo run -- build                               # Compile servers
cargo run -- extract                             # Extract indicators from C source → YAML
```

Generated output goes to `ta_codegen_output/` organized by language.

## How to build with CMakeLists.txt
```
$ cd ta-lib
$ mkdir build
$ cd build
$ cmake ..
$ make
```
Libraries will be in ```ta-lib/build``` and executable in ```ta-lib/bin```

## How to run gen_code
After ```make```, call ```gen_code``` located in ta-lib/bin

Do this to refresh many files and code variant (Rust, Java etc...)

You should call ```make`` again after gen_code to verify if the
potentially updated C code is still compiling.


## How to run ta_regtest
After ```make```, call ```ta_regtest``` located in ta-lib/src/tools

Exit code is 0 on success


## How to do a new release?

Any dev with permission to merge to main branch can do a release.

(1) On the dev branch, edit the VERSION file in the root of the repos.

(2) Run "./scripts/sync.py". This ensures your dev branch is up-to-date (among other things).

(3) Push to the dev branch.

(4) Manually trig the "nightly dev" Github action. This will regenerate and test for **all** platforms. If you do not trig it, it will get run anyway once per day.

(5) Merge dev into main with "./scripts/merge.py". At this point, the main branch is the release candidate with all the assets under "dist" folder.

(6) Manually trig the "nightly main" Github action. This will perform a last round of check prior to alloweing for the release. If you do not trig it, it will get run anyway once per day.

(7) Manually trig the "Release (step-1)" Github action on main branch. This will tag, generate a draft release and attach all assets from the dist/ directory.

(8) Optionally edit the draft "Release notes" on the Github website. A good time to add thank you to contributors. You can still edit after the official release.

(9) Manually trig "Release (step 2)" Github action. This will make the release official/public and update the website.

(10) Verify that https://ta-lib.org/install reflects the new version.

(11) Run "./scripts/post-release-vcpkg.py" and follow the instructions to submit a PR to microsoft/vcpkg. Monitor the PR is eventually merged by vcpkg maintainers. This may take a few days.

(12) Monitor homebrew-core. The following formula will eventually be updated (may take an hour):
https://github.com/Homebrew/homebrew-core/blob/30106807361198c58a395de65547694427adf229/Formula/t/ta-lib.rb


## I want to modify the code... should I care to rebuild the packages?
No.

Commit your source code changes in devs and... just let the Github action do all the repackaging for you.

It may take up to one day for the CI to regenerate and test for **all** platforms.

The rest of this section is only if you want to re-package locally.

You can call the ```scripts/package.py``` to generate the packages for your hosting platform.

You can test your packages with ```scripts/test-dist.py```. This verifies from a TA-Lib user perspective. Notably, this simulates a ta-lib-python user.

Try to avoid pushing your generated packages to the TA-Lib repo, but do not worry if you do. As needed, they will get overwritten by the "nightly dev" CI.
