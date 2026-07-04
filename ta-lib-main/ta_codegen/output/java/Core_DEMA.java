/* List of contributors:
 *
 *  Initial  Name/description
 *  -------------------------------------------------------------------
 *  MF       Mario Fortier
 *
 *
 * Change history:
 *
 *  MMDDYY BY   Description
 *  -------------------------------------------------------------------
 *  010102 MF   Template creation.
 *  052603 MF   Adapt code to compile with .NET Managed C++
 */

   public int demaLookback( int optInTimePeriod )
   {
      if( optInTimePeriod == Integer.MIN_VALUE ) {
         optInTimePeriod = 30;
      } else if( optInTimePeriod < 1 || optInTimePeriod > 100000 ) {
         return -1;
      }
      /* Get lookback for one EMA.
       * Multiply by two (because double smoothing).
       */
      return emaLookback(optInTimePeriod) * 2 ;

   }
   public RetCode dema( int startIdx,
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
      int tempInt = 0;
      int outIdx = 0;
      int firstEMAIdx = 0;
      int lookbackTotal = 0;
      int lookbackEMA = 0;
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
      /* For an explanation of this function, please read
       *
       * Stocks & Commodities V. 12:1 (11-19):
       *   Smoothing Data With Faster Moving Averages
       * Stocks & Commodities V. 12:2 (72-80):
       *   Smoothing Data With Less Lag
       *
       * Both magazine articles written by Patrick G. Mulloy
       *
       * Essentially, a DEMA of time serie 't' is:
       *   EMA2 = EMA(EMA(t,period),period)
       *   DEMA = 2*EMA(t,period)- EMA2
       *
       * DEMA offers a moving average with less lags then the
       * traditional EMA.
       *
       * Do not confuse a DEMA with the EMA2. Both are called
       * "Double EMA" in the litterature, but EMA2 is a simple
       * EMA of an EMA, while DEMA is a compostie of a single
       * EMA with EMA2.
       *
       * TEMA is very similar (and from the same author).
       */
      /* Will change only on success. */
      outNBElement.value = 0;
      outBegIdx.value = 0;
      /* Adjust startIdx to account for the lookback period. */
      lookbackEMA = emaLookback(optInTimePeriod);
      lookbackTotal = lookbackEMA * 2;
      if( startIdx < lookbackTotal ) {
         startIdx = lookbackTotal;
      }
      /* Make sure there is still something to evaluate. */
      if( startIdx > endIdx ) {
         return RetCode.Success ;
      }
      /* Allocate a temporary buffer for the firstEMA.
       *
       * When possible, re-use the outputBuffer for temp
       * calculation.
       */
      if( inReal == outReal ) {
         firstEMA = outReal;
      } else {
         tempInt = lookbackTotal + (endIdx - startIdx) + 1;
         firstEMA = new double[(int)(tempInt * 1)];
      }
      /* Calculate the first EMA */
      retCode = emaUnguarded(startIdx - lookbackEMA, endIdx, inReal, optInTimePeriod, firstEMABegIdx, firstEMANbElement, firstEMA);
      /* Verify for failure or if not enough data after
       * calculating the first EMA.
       */
      if( retCode != RetCode.Success || firstEMANbElement.value == 0 ) {
         if( firstEMA != outReal ) {
         }
         return retCode ;
      }
      /* Allocate a temporary buffer for storing the EMA of the EMA. */
      secondEMA = new double[(int)(firstEMANbElement.value * 1)];
      retCode = emaUnguarded(0, firstEMANbElement.value - 1, firstEMA, optInTimePeriod, secondEMABegIdx, secondEMANbElement, secondEMA);
      /* Return empty output on failure or if not enough data after
       * calculating the second EMA.
       */
      if( retCode != RetCode.Success || secondEMANbElement.value == 0 ) {
         if( firstEMA != outReal ) {
         }
         return retCode ;
      }
      /* Iterate through the second EMA and write the DEMA into
       * the output.
       */
      firstEMAIdx = secondEMABegIdx.value;
      outIdx = 0;
      while( outIdx < secondEMANbElement.value ) {
         outReal[outIdx] = 2.0 * firstEMA[firstEMAIdx++] - secondEMA[outIdx];
         outIdx += 1;
      }
      if( firstEMA != outReal ) {
      }
      /* Succeed. Indicate where the output starts relative to
       * the caller input.
       */
      outBegIdx.value = firstEMABegIdx.value + secondEMABegIdx.value;
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode demaUnguarded( int startIdx,
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
      int tempInt = 0;
      int outIdx = 0;
      int firstEMAIdx = 0;
      int lookbackTotal = 0;
      int lookbackEMA = 0;
      RetCode retCode;
      outNBElement.value = 0;
      outBegIdx.value = 0;
      lookbackEMA = emaLookback(optInTimePeriod);
      lookbackTotal = lookbackEMA * 2;
      if( startIdx < lookbackTotal ) {
         startIdx = lookbackTotal;
      }
      if( startIdx > endIdx ) {
         return RetCode.Success ;
      }
      if( inReal == outReal ) {
         firstEMA = outReal;
      } else {
         tempInt = lookbackTotal + (endIdx - startIdx) + 1;
         firstEMA = new double[(int)(tempInt * 1)];
      }
      retCode = emaUnguarded(startIdx - lookbackEMA, endIdx, inReal, optInTimePeriod, firstEMABegIdx, firstEMANbElement, firstEMA);
      if( retCode != RetCode.Success || firstEMANbElement.value == 0 ) {
         if( firstEMA != outReal ) {
         }
         return retCode ;
      }
      secondEMA = new double[(int)(firstEMANbElement.value * 1)];
      retCode = emaUnguarded(0, firstEMANbElement.value - 1, firstEMA, optInTimePeriod, secondEMABegIdx, secondEMANbElement, secondEMA);
      if( retCode != RetCode.Success || secondEMANbElement.value == 0 ) {
         if( firstEMA != outReal ) {
         }
         return retCode ;
      }
      firstEMAIdx = secondEMABegIdx.value;
      outIdx = 0;
      while( outIdx < secondEMANbElement.value ) {
         outReal[outIdx] = 2.0 * firstEMA[firstEMAIdx++] - secondEMA[outIdx];
         outIdx += 1;
      }
      if( firstEMA != outReal ) {
      }
      outBegIdx.value = firstEMABegIdx.value + secondEMABegIdx.value;
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode dema( int startIdx,
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
      int tempInt = 0;
      int outIdx = 0;
      int firstEMAIdx = 0;
      int lookbackTotal = 0;
      int lookbackEMA = 0;
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
      lookbackTotal = lookbackEMA * 2;
      if( startIdx < lookbackTotal ) {
         startIdx = lookbackTotal;
      }
      if( startIdx > endIdx ) {
         return RetCode.Success ;
      }
      if( false ) {
         firstEMA = outReal;
      } else {
         tempInt = lookbackTotal + (endIdx - startIdx) + 1;
         firstEMA = new double[(int)(tempInt * 1)];
      }
      retCode = emaUnguarded(startIdx - lookbackEMA, endIdx, inReal, optInTimePeriod, firstEMABegIdx, firstEMANbElement, firstEMA);
      if( retCode != RetCode.Success || firstEMANbElement.value == 0 ) {
         if( firstEMA != outReal ) {
         }
         return retCode ;
      }
      secondEMA = new double[(int)(firstEMANbElement.value * 1)];
      retCode = emaUnguarded(0, firstEMANbElement.value - 1, firstEMA, optInTimePeriod, secondEMABegIdx, secondEMANbElement, secondEMA);
      if( retCode != RetCode.Success || secondEMANbElement.value == 0 ) {
         if( firstEMA != outReal ) {
         }
         return retCode ;
      }
      firstEMAIdx = secondEMABegIdx.value;
      outIdx = 0;
      while( outIdx < secondEMANbElement.value ) {
         outReal[outIdx] = 2.0 * firstEMA[firstEMAIdx++] - secondEMA[outIdx];
         outIdx += 1;
      }
      if( firstEMA != outReal ) {
      }
      outBegIdx.value = firstEMABegIdx.value + secondEMABegIdx.value;
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode demaUnguarded( int startIdx,
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
      int tempInt = 0;
      int outIdx = 0;
      int firstEMAIdx = 0;
      int lookbackTotal = 0;
      int lookbackEMA = 0;
      RetCode retCode;
      outNBElement.value = 0;
      outBegIdx.value = 0;
      lookbackEMA = emaLookback(optInTimePeriod);
      lookbackTotal = lookbackEMA * 2;
      if( startIdx < lookbackTotal ) {
         startIdx = lookbackTotal;
      }
      if( startIdx > endIdx ) {
         return RetCode.Success ;
      }
      if( false ) {
         firstEMA = outReal;
      } else {
         tempInt = lookbackTotal + (endIdx - startIdx) + 1;
         firstEMA = new double[(int)(tempInt * 1)];
      }
      retCode = emaUnguarded(startIdx - lookbackEMA, endIdx, inReal, optInTimePeriod, firstEMABegIdx, firstEMANbElement, firstEMA);
      if( retCode != RetCode.Success || firstEMANbElement.value == 0 ) {
         if( firstEMA != outReal ) {
         }
         return retCode ;
      }
      secondEMA = new double[(int)(firstEMANbElement.value * 1)];
      retCode = emaUnguarded(0, firstEMANbElement.value - 1, firstEMA, optInTimePeriod, secondEMABegIdx, secondEMANbElement, secondEMA);
      if( retCode != RetCode.Success || secondEMANbElement.value == 0 ) {
         if( firstEMA != outReal ) {
         }
         return retCode ;
      }
      firstEMAIdx = secondEMABegIdx.value;
      outIdx = 0;
      while( outIdx < secondEMANbElement.value ) {
         outReal[outIdx] = 2.0 * firstEMA[firstEMAIdx++] - secondEMA[outIdx];
         outIdx += 1;
      }
      if( firstEMA != outReal ) {
      }
      outBegIdx.value = firstEMABegIdx.value + secondEMABegIdx.value;
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
