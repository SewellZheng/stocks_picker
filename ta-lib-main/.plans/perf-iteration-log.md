# Performance Iteration Log

## Session: 2026-03-19 22:10 - 23:30 EDT

### Final Comprehensive Results (33 indicators tested in isolation, 300 iters)

**10 indicators FASTER than reference (codegen wins):**
- CDLMORNINGDOJISTAR: 0.50x (2x faster)
- CDLRICKSHAWMAN: 0.62x
- CDLHARAMICROSS: 0.62x
- CDLSHORTLINE: 0.64x
- CDL3WHITESOLDIERS: 0.69x
- CDLHARAMI: 0.78x
- CDL2CROWS: 0.79x
- AROON: 0.86x
- CDL3STARSINSOUTH: 0.87x
- STOCH: 0.88x

**19 indicators at parity (0.90-1.10x):**
RSI 1.00x, SMA 1.00x, EMA 0.99x, MACD 1.00x, BBANDS 1.01x, WMA 1.02x, DEMA 0.97x, ADX 0.92x, KAMA 1.05x, STOCHRSI 1.05x, STOCHF 1.05x, and more

**4 indicators slightly slower (1.10-1.35x):**
- MINMAX: 1.35x — assembly identical to ref, binary layout effect, NOT fixable in source
- CDL3BLACKCROWS: 1.21x — constant propagation vs loop unswitching trade-off
- CCI: 1.11x — borderline, was at parity in earlier runs
- AROONOSC: 1.11x — borderline

### Convergence
No more actionable fixes. The remaining 4 slowdowns are either:
1. Binary layout effects (assembly-identical to reference)
2. Compiler optimization trade-offs (constant propagation vs loop unswitching)
3. Borderline noise (1.11x)

### Session Summary of Changes
1. CCI circular buffer: modulo → conditional reset
2. Parameter validation: NULL checks, INTEGER_DEFAULT, range bounds
3. Ternary chains for numeric switches (cosmetic — compiler handles identically)
4. ta_ref_serve auto-rebuild in regtest.py
5. Thermal canary in ta_bench
6. Single-TU server compilation (reverted separate compilation)
7. Static globals (reverted extern/volatile — constant propagation is faster)
8. f64 range field in IR (fixes MAMA validation)
9. load_data for Java/.NET/Rust servers
10. --language filter always includes cref in perftest
11. --indicators/--functions aliases in regtest.py
