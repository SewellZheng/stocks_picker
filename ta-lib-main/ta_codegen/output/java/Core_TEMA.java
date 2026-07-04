/* List of contributors:
 *
 *  Initial  Name/description
 *  -------------------------------------------------------------------
 *  MF       Mario Fortier
 *
 *
 * Change history:
 *
 *  MMDDYY BY     Description
 *  -------------------------------------------------------------------
 *  112400 MF     Template creation.
 *  052603 MF     Adapt code to compile with .NET Managed C++
 *  070226 MF,CC  Allow period of 1: output is an exact copy of the
 *                input, consistent with TA_MA (issues #48, #59). The
 *                natural math (3*e1 - 3*e2 + e3 with e1=e2=e3=x) is
 *                exact on x86 but not under FMA contraction (ARM64
 *                clang leaves ~1e-14 residue), so the copy is explicit.
 */

   public int temaLookback( int optInTimePeriod )
   {
      if( optInTimePeriod == Integer.MIN_VALUE ) {
         optInTimePeriod = 30;
      } else if( optInTimePeriod < 1 || optInTimePeriod > 100000 ) {
         return -1;
      }
      int retValue;
      /* Get lookack for one EMA. */
      retValue = emaLookback(optInTimePeriod);
      return retValue * 3 ;

   }
   public RetCode tema( int startIdx,
                        int endIdx,
                        double inReal[],
                        int optInTimePeriod,
                        MInteger outBegIdx,
                        MInteger outNBElement,
                        double outReal[] )
   {
      double[] firstEMA;
      double[] secondEMA;
      MInteger firstEMABegIdx = new MInteger();
      MInteger firstEMANbElement = new MInteger();
      MInteger secondEMABegIdx = new MInteger();
      MInteger secondEMANbElement = new MInteger();
      MInteger thirdEMABegIdx = new MInteger();
      MInteger thirdEMANbElement = new MInteger();
      int tempInt = 0;
      int outIdx = 0;
      int lookbackTotal = 0;
      int lookbackEMA = 0;
      int firstEMAIdx = 0;
      int secondEMAIdx = 0;
      RetCode retCode;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      if( optInTimePeriod == Integer.MIN_VALUE ) {
         optInTimePeriod = 30;
      } else if( optInTimePeriod < 1 || optInTimePeriod > 100000 ) {
         return RetCode.BadParam;
      }
      /* For an explanation of this function, please read:
       *
       * Stocks & Commodities V. 12:1 (11-19):
       *   Smoothing Data With Faster Moving Averages
       * Stocks & Commodities V. 12:2 (72-80):
       *   Smoothing Data With Less Lag
       *
       * Both magazine articles written by Patrick G. Mulloy
       *
       * Essentially, a TEMA of time serie 't' is:
       *   EMA1 = EMA(t,period)
       *   EMA2 = EMA(EMA(t,period),period)
       *   EMA3 = EMA(EMA(EMA(t,period),period))
       *   TEMA = 3*EMA1 - 3*EMA2 + EMA3
       *
       * TEMA offers a moving average with less lags then the
       * traditional EMA.
       *
       * Do not confuse a TEMA with EMA3. Both are called "Triple EMA"
       * in the litterature.
       *
       * DEMA is very similar (and from the same author).
       */
      /* Will change only on success. */
      outNBElement.value = 0;
      outBegIdx.value = 0;
      /* Adjust startIdx to account for the lookback period. */
      lookbackEMA = emaLookback(optInTimePeriod);
      lookbackTotal = lookbackEMA * 3;
      if( startIdx < lookbackTotal ) {
         startIdx = lookbackTotal;
      }
      /* Make sure there is still something to evaluate. */
      if( startIdx > endIdx ) {
         return RetCode.Success ;
      }
      /* No smoothing at period of 1: the output is a copy of the input
       * (same convention as TA_MA for every MAType). Explicit because the
       * 3*e1 - 3*e2 + e3 composition cancels exactly only without FMA
       * contraction; ARM64 fused multiply-add leaves ~1e-14 residue.
       */
      if( optInTimePeriod == 1 ) {
         outBegIdx.value = startIdx;
         outIdx = 0;
         while( startIdx <= endIdx ) {
            outReal[outIdx++] = inReal[startIdx++];
         }
         outNBElement.value = outIdx;
         return RetCode.Success ;
      }
      /* Allocate a temporary buffer for the firstEMA. */
      tempInt = lookbackTotal + (endIdx - startIdx) + 1;
      firstEMA = new double[(int)(tempInt * 1)];
      /* Calculate the first EMA */
      retCode = emaUnguarded(startIdx - lookbackEMA * 2, endIdx, inReal, optInTimePeriod, firstEMABegIdx, firstEMANbElement, firstEMA);
      /* Verify for failure or if not enough data after
       * calculating the first EMA.
       */
      if( retCode != RetCode.Success || firstEMANbElement.value == 0 ) {
         return retCode ;
      }
      /* Allocate a temporary buffer for storing the EMA2 */
      secondEMA = new double[(int)(firstEMANbElement.value * 1)];
      retCode = emaUnguarded(0, firstEMANbElement.value - 1, firstEMA, optInTimePeriod, secondEMABegIdx, secondEMANbElement, secondEMA);
      /* Return empty output on failure or if not enough data after
       * calculating the second EMA.
       */
      if( retCode != RetCode.Success || secondEMANbElement.value == 0 ) {
         return retCode ;
      }
      /* Calculate the EMA3 into the caller provided output. */
      retCode = emaUnguarded(0, secondEMANbElement.value - 1, secondEMA, optInTimePeriod, thirdEMABegIdx, thirdEMANbElement, outReal);
      /* Return empty output on failure or if not enough data after
       * calculating the third EMA.
       */
      if( retCode != RetCode.Success || thirdEMANbElement.value == 0 ) {
         return retCode ;
      }
      /* Indicate where the output starts relative to
       * the caller input.
       */
      firstEMAIdx = thirdEMABegIdx.value + secondEMABegIdx.value;
      secondEMAIdx = thirdEMABegIdx.value;
      outBegIdx.value = firstEMAIdx + firstEMABegIdx.value;
      /* Do the TEMA:
       *  Iterate through the EMA3 (output buffer) and adjust
       *  the value by using the EMA2 and EMA1.
       */
      outIdx = 0;
      while( outIdx < thirdEMANbElement.value ) {
         outReal[outIdx] = outReal[outIdx] + (3.0 * firstEMA[firstEMAIdx++] - 3.0 * secondEMA[secondEMAIdx++]);
         outIdx += 1;
      }
      /* Indicates to the caller the number of output
       * successfully calculated.
       */
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode temaUnguarded( int startIdx,
                                 int endIdx,
                                 double inReal[],
                                 int optInTimePeriod,
                                 MInteger outBegIdx,
                                 MInteger outNBElement,
                                 double outReal[] )
   {
      double[] firstEMA;
      double[] secondEMA;
      MInteger firstEMABegIdx = new MInteger();
      MInteger firstEMANbElement = new MInteger();
      MInteger secondEMABegIdx = new MInteger();
      MInteger secondEMANbElement = new MInteger();
      MInteger thirdEMABegIdx = new MInteger();
      MInteger thirdEMANbElement = new MInteger();
      int tempInt = 0;
      int outIdx = 0;
      int lookbackTotal = 0;
      int lookbackEMA = 0;
      int firstEMAIdx = 0;
      int secondEMAIdx = 0;
      RetCode retCode;
      outNBElement.value = 0;
      outBegIdx.value = 0;
      lookbackEMA = emaLookback(optInTimePeriod);
      lookbackTotal = lookbackEMA * 3;
      if( startIdx < lookbackTotal ) {
         startIdx = lookbackTotal;
      }
      if( startIdx > endIdx ) {
         return RetCode.Success ;
      }
      if( optInTimePeriod == 1 ) {
         outBegIdx.value = startIdx;
         outIdx = 0;
         while( startIdx <= endIdx ) {
            outReal[outIdx++] = inReal[startIdx++];
         }
         outNBElement.value = outIdx;
         return RetCode.Success ;
      }
      tempInt = lookbackTotal + (endIdx - startIdx) + 1;
      firstEMA = new double[(int)(tempInt * 1)];
      retCode = emaUnguarded(startIdx - lookbackEMA * 2, endIdx, inReal, optInTimePeriod, firstEMABegIdx, firstEMANbElement, firstEMA);
      if( retCode != RetCode.Success || firstEMANbElement.value == 0 ) {
         return retCode ;
      }
      secondEMA = new double[(int)(firstEMANbElement.value * 1)];
      retCode = emaUnguarded(0, firstEMANbElement.value - 1, firstEMA, optInTimePeriod, secondEMABegIdx, secondEMANbElement, secondEMA);
      if( retCode != RetCode.Success || secondEMANbElement.value == 0 ) {
         return retCode ;
      }
      retCode = emaUnguarded(0, secondEMANbElement.value - 1, secondEMA, optInTimePeriod, thirdEMABegIdx, thirdEMANbElement, outReal);
      if( retCode != RetCode.Success || thirdEMANbElement.value == 0 ) {
         return retCode ;
      }
      firstEMAIdx = thirdEMABegIdx.value + secondEMABegIdx.value;
      secondEMAIdx = thirdEMABegIdx.value;
      outBegIdx.value = firstEMAIdx + firstEMABegIdx.value;
      outIdx = 0;
      while( outIdx < thirdEMANbElement.value ) {
         outReal[outIdx] = outReal[outIdx] + (3.0 * firstEMA[firstEMAIdx++] - 3.0 * secondEMA[secondEMAIdx++]);
         outIdx += 1;
      }
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode tema( int startIdx,
                        int endIdx,
                        float inReal[],
                        int optInTimePeriod,
                        MInteger outBegIdx,
                        MInteger outNBElement,
                        double outReal[] )
   {
      double[] firstEMA;
      double[] secondEMA;
      MInteger firstEMABegIdx = new MInteger();
      MInteger firstEMANbElement = new MInteger();
      MInteger secondEMABegIdx = new MInteger();
      MInteger secondEMANbElement = new MInteger();
      MInteger thirdEMABegIdx = new MInteger();
      MInteger thirdEMANbElement = new MInteger();
      int tempInt = 0;
      int outIdx = 0;
      int lookbackTotal = 0;
      int lookbackEMA = 0;
      int firstEMAIdx = 0;
      int secondEMAIdx = 0;
      RetCode retCode;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      if( optInTimePeriod == Integer.MIN_VALUE ) {
         optInTimePeriod = 30;
      } else if( optInTimePeriod < 1 || optInTimePeriod > 100000 ) {
         return RetCode.BadParam;
      }
      outNBElement.value = 0;
      outBegIdx.value = 0;
      lookbackEMA = emaLookback(optInTimePeriod);
      lookbackTotal = lookbackEMA * 3;
      if( startIdx < lookbackTotal ) {
         startIdx = lookbackTotal;
      }
      if( startIdx > endIdx ) {
         return RetCode.Success ;
      }
      if( optInTimePeriod == 1 ) {
         outBegIdx.value = startIdx;
         outIdx = 0;
         while( startIdx <= endIdx ) {
            outReal[outIdx++] = inReal[startIdx++];
         }
         outNBElement.value = outIdx;
         return RetCode.Success ;
      }
      tempInt = lookbackTotal + (endIdx - startIdx) + 1;
      firstEMA = new double[(int)(tempInt * 1)];
      retCode = emaUnguarded(startIdx - lookbackEMA * 2, endIdx, inReal, optInTimePeriod, firstEMABegIdx, firstEMANbElement, firstEMA);
      if( retCode != RetCode.Success || firstEMANbElement.value == 0 ) {
         return retCode ;
      }
      secondEMA = new double[(int)(firstEMANbElement.value * 1)];
      retCode = emaUnguarded(0, firstEMANbElement.value - 1, firstEMA, optInTimePeriod, secondEMABegIdx, secondEMANbElement, secondEMA);
      if( retCode != RetCode.Success || secondEMANbElement.value == 0 ) {
         return retCode ;
      }
      retCode = emaUnguarded(0, secondEMANbElement.value - 1, secondEMA, optInTimePeriod, thirdEMABegIdx, thirdEMANbElement, outReal);
      if( retCode != RetCode.Success || thirdEMANbElement.value == 0 ) {
         return retCode ;
      }
      firstEMAIdx = thirdEMABegIdx.value + secondEMABegIdx.value;
      secondEMAIdx = thirdEMABegIdx.value;
      outBegIdx.value = firstEMAIdx + firstEMABegIdx.value;
      outIdx = 0;
      while( outIdx < thirdEMANbElement.value ) {
         outReal[outIdx] = outReal[outIdx] + (3.0 * firstEMA[firstEMAIdx++] - 3.0 * secondEMA[secondEMAIdx++]);
         outIdx += 1;
      }
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode temaUnguarded( int startIdx,
                                 int endIdx,
                                 float inReal[],
                                 int optInTimePeriod,
                                 MInteger outBegIdx,
                                 MInteger outNBElement,
                                 double outReal[] )
   {
      double[] firstEMA;
      double[] secondEMA;
      MInteger firstEMABegIdx = new MInteger();
      MInteger firstEMANbElement = new MInteger();
      MInteger secondEMABegIdx = new MInteger();
      MInteger secondEMANbElement = new MInteger();
      MInteger thirdEMABegIdx = new MInteger();
      MInteger thirdEMANbElement = new MInteger();
      int tempInt = 0;
      int outIdx = 0;
      int lookbackTotal = 0;
      int lookbackEMA = 0;
      int firstEMAIdx = 0;
      int secondEMAIdx = 0;
      RetCode retCode;
      outNBElement.value = 0;
      outBegIdx.value = 0;
      lookbackEMA = emaLookback(optInTimePeriod);
      lookbackTotal = lookbackEMA * 3;
      if( startIdx < lookbackTotal ) {
         startIdx = lookbackTotal;
      }
      if( startIdx > endIdx ) {
         return RetCode.Success ;
      }
      if( optInTimePeriod == 1 ) {
         outBegIdx.value = startIdx;
         outIdx = 0;
         while( startIdx <= endIdx ) {
            outReal[outIdx++] = inReal[startIdx++];
         }
         outNBElement.value = outIdx;
         return RetCode.Success ;
      }
      tempInt = lookbackTotal + (endIdx - startIdx) + 1;
      firstEMA = new double[(int)(tempInt * 1)];
      retCode = emaUnguarded(startIdx - lookbackEMA * 2, endIdx, inReal, optInTimePeriod, firstEMABegIdx, firstEMANbElement, firstEMA);
      if( retCode != RetCode.Success || firstEMANbElement.value == 0 ) {
         return retCode ;
      }
      secondEMA = new double[(int)(firstEMANbElement.value * 1)];
      retCode = emaUnguarded(0, firstEMANbElement.value - 1, firstEMA, optInTimePeriod, secondEMABegIdx, secondEMANbElement, secondEMA);
      if( retCode != RetCode.Success || secondEMANbElement.value == 0 ) {
         return retCode ;
      }
      retCode = emaUnguarded(0, secondEMANbElement.value - 1, secondEMA, optInTimePeriod, thirdEMABegIdx, thirdEMANbElement, outReal);
      if( retCode != RetCode.Success || thirdEMANbElement.value == 0 ) {
         return retCode ;
      }
      firstEMAIdx = thirdEMABegIdx.value + secondEMABegIdx.value;
      secondEMAIdx = thirdEMABegIdx.value;
      outBegIdx.value = firstEMAIdx + firstEMABegIdx.value;
      outIdx = 0;
      while( outIdx < thirdEMANbElement.value ) {
         outReal[outIdx] = outReal[outIdx] + (3.0 * firstEMA[firstEMAIdx++] - 3.0 * secondEMA[secondEMAIdx++]);
         outIdx += 1;
      }
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
