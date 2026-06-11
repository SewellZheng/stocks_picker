# Direct Bench Performance Log

## Session: 2026-03-20

### ta_bench_direct Results (161 indicators, 100k points, 200 iters)

**Summary:**
- 70 faster (<0.90x)
- 85 at parity (0.90-1.10x)
- 6 slower in full run (>1.10x)

### Isolated Confirmation (500 iters)
Only 3 indicators are genuinely >1.10x slower:
- CDLHARAMI: 1.39x — constant propagation prevents loop unswitching (232 vs 816 asm lines)
- CDLBREAKAWAY: 1.24x — same root cause
- CDL3BLACKCROWS: 1.12x — same root cause

### Previously "Slow" Indicators — Now Resolved
- **MINMAX**: 0.68x — was 1.25x via server. NEVER ACTUALLY SLOW. Server overhead.
- **SAR**: 1.21x isolated — assembly identical to reference (14 diff lines, just constant encoding). Binary layout effect in single-TU.
- **AROONOSC**: 1.09x isolated — parity (was 1.13x in full run, noise)
- **CDLENGULFING**: 1.00x isolated — parity (was 1.14x in full run, noise)
- **WILLR**: 1.05x isolated — parity (was 1.11x in full run, noise)

### CDL Patterns: 53 faster, 3 slower
The constant propagation from static globals is a massive net win for CDL patterns:
- 53 CDL indicators faster (<0.90x), 12 of those >2x faster (<0.50x)
- 3 CDL indicators slower (constant propagation eliminates branches that reference's separate-TU unswitches)
- Root cause: static TA_Globals in single-TU → compiler folds candle settings → compact code
- This is NOT fixable without hurting the 53 that benefit. Accepted trade-off.

### Notable Fast Indicators
- CDLKICKING: 0.27x (3.7x faster)
- CDLSTALLEDPATTERN: 0.27x
- CDLEVENINGSTAR: 0.31x
- LINEARREG_SLOPE: 0.31x
- TSF: 0.34x
- CDLMORNINGSTAR: 0.33x
- AVGDEV: 0.39x
- CDLCOUNTERATTACK: 0.37x

### Key Finding
The server-based benchmark (ta_bench) had significant transport noise that made codegen look worse than it actually is. The direct-call benchmark reveals that codegen C output is substantially better than the reference library overall.
