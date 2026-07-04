/* List of contributors:
 *
 *  Initial  Name/description
 *  -------------------------------------------------------------------
 *  MF       Mario Fortier
 *  JPP      JP Pienaar (j.pienaar@mci.co.za)
 *
 * Change history:
 *
 *  MMDDYY BY   Description
 *  -------------------------------------------------------------------
 *  112400 MF   Template creation.
 *  052603 MF   Adapt code to compile with .NET Managed C++
 *  080403 JPP  Fix #767653 for logic when swapping periods.
 */

   public int macdLookback( int optInFastPeriod, int optInSlowPeriod, int optInSignalPeriod )
   {
      if( optInFastPeriod == Integer.MIN_VALUE ) {
         optInFastPeriod = 12;
      } else if( optInFastPeriod < 2 || optInFastPeriod > 100000 ) {
         return -1;
      }
      if( optInSlowPeriod == Integer.MIN_VALUE ) {
         optInSlowPeriod = 26;
      } else if( optInSlowPeriod < 2 || optInSlowPeriod > 100000 ) {
         return -1;
      }
      if( optInSignalPeriod == Integer.MIN_VALUE ) {
         optInSignalPeriod = 9;
      } else if( optInSignalPeriod < 1 || optInSignalPeriod > 100000 ) {
         return -1;
      }
      int tempInteger;
      /* The lookback is driven by the signal line output.
       *
       * (must also account for the initial data consume
       *  by the slow period).
       */
      /* Make sure slow is really slower than
       * the fast period! if not, swap...
       */
      if( optInSlowPeriod < optInFastPeriod ) {
         /* swap */
         tempInteger = optInSlowPeriod;
         optInSlowPeriod = optInFastPeriod;
         optInFastPeriod = tempInteger;
      }
      return emaLookback(optInSlowPeriod) + emaLookback(optInSignalPeriod) ;

   }
   public RetCode macd( int startIdx,
                        int endIdx,
                        double inReal[],
                        int optInFastPeriod,
                        int optInSlowPeriod,
                        int optInSignalPeriod,
                        MInteger outBegIdx,
                        MInteger outNBElement,
                        double outMACD[],
                        double outMACDSignal[],
                        double outMACDHist[] )
   {
      double[] slowEMABuffer;
      double[] fastEMABuffer;
      RetCode retCode;
      int tempInteger = 0;
      MInteger outBegIdx1 = new MInteger();
      MInteger outNbElement1 = new MInteger();
      MInteger outBegIdx2 = new MInteger();
      MInteger outNbElement2 = new MInteger();
      double slowK = 0;
      double fastK = 0;
      double signalK = 0;
      int lookbackTotal = 0;
      int lookbackSignal = 0;
      int useFixedK = 0;
      int i = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      if( optInFastPeriod == Integer.MIN_VALUE ) {
         optInFastPeriod = 12;
      } else if( optInFastPeriod < 2 || optInFastPeriod > 100000 ) {
         return RetCode.BadParam;
      }
      if( optInSlowPeriod == Integer.MIN_VALUE ) {
         optInSlowPeriod = 26;
      } else if( optInSlowPeriod < 2 || optInSlowPeriod > 100000 ) {
         return RetCode.BadParam;
      }
      if( optInSignalPeriod == Integer.MIN_VALUE ) {
         optInSignalPeriod = 9;
      } else if( optInSignalPeriod < 1 || optInSignalPeriod > 100000 ) {
         return RetCode.BadParam;
      }
      /* !!! A lot of speed optimization could be done
       * !!! with this function.
       * !!!
       * !!! A better approach would be to use ema
       * !!! just to get the seeding values for the
       * !!! fast and slow EMA. Then process the difference
       * !!! in an allocated buffer until enough data is
       * !!! available for the first signal value.
       * !!! From that point all the processing can
       * !!! be done in a tight loop.
       * !!!
       * !!! That approach will have the following
       * !!! advantage:
       * !!!   1) One mem allocation needed instead of two.
       * !!!   2) The mem allocation size will be only the
       * !!!      signal lookback period instead of the
       * !!!      whole range of data.
       * !!!   3) Processing will be done in a tight loop.
       * !!!      allowing to avoid a lot of memory store-load
       * !!!      operation.
       * !!!   4) The memcpy at the end will be eliminated!
       * !!!
       * !!! If only I had time....
       */
      /* Make sure slow is really slower than
       * the fast period! if not, swap...
       */
      if( optInSlowPeriod < optInFastPeriod ) {
         /* swap */
         tempInteger = optInSlowPeriod;
         optInSlowPeriod = optInFastPeriod;
         optInFastPeriod = tempInteger;
      }
      /* Catch special case for fix 26/12 MACD.
       * Use hardcoded k values matching the original algorithm.
       */
      useFixedK = 0;
      if( optInSlowPeriod == 0 ) {
         optInSlowPeriod = 26;
         /* Fix 26 */
         slowK = 0.075;
         useFixedK = 1;
      } else {
         slowK = 2.0 / (double)(optInSlowPeriod + 1);
      }
      if( optInFastPeriod == 0 ) {
         optInFastPeriod = 12;
         /* Fix 12 */
         fastK = 0.15;
         useFixedK = 1;
      } else {
         fastK = 2.0 / (double)(optInFastPeriod + 1);
      }
      signalK = 2.0 / (double)(optInSignalPeriod + 1);
      lookbackSignal = emaLookback(optInSignalPeriod);
      /* Move up the start index if there is not
       * enough initial data.
       */
      lookbackTotal = lookbackSignal;
      lookbackTotal += emaLookback(optInSlowPeriod);
      if( startIdx < lookbackTotal ) {
         startIdx = lookbackTotal;
      }
      /* Make sure there is still something to evaluate. */
      if( startIdx > endIdx ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      /* Allocate intermediate buffer for fast/slow EMA. */
      tempInteger = endIdx - startIdx + 1 + lookbackSignal;
      fastEMABuffer = new double[(int)(tempInteger * 1)];
      slowEMABuffer = new double[(int)(tempInteger * 1)];
      /* Calculate the slow EMA.
       *
       * Move back the startIdx to get enough data
       * for the signal period. That way, once the
       * signal calculation is done, all the output
       * will start at the requested 'startIdx'.
       */
      tempInteger = startIdx - lookbackSignal;
      /* Use ema_private when hardcoded k is needed (MACDFIX path).
       * Use ema() for the normal path — codegen handles double/float routing.
       */
      if( (useFixedK) != 0 ) {
         retCode = emaPrivate(tempInteger, endIdx, inReal, optInSlowPeriod, slowK, outBegIdx1, outNbElement1, slowEMABuffer);
      } else {
         retCode = emaUnguarded(tempInteger, endIdx, inReal, optInSlowPeriod, outBegIdx1, outNbElement1, slowEMABuffer);
      }
      if( retCode != RetCode.Success ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      /* Calculate the fast EMA. */
      if( (useFixedK) != 0 ) {
         retCode = emaPrivate(tempInteger, endIdx, inReal, optInFastPeriod, fastK, outBegIdx2, outNbElement2, fastEMABuffer);
      } else {
         retCode = emaUnguarded(tempInteger, endIdx, inReal, optInFastPeriod, outBegIdx2, outNbElement2, fastEMABuffer);
      }
      if( retCode != RetCode.Success ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      /* Parano tests. Will be removed eventually. */
      if( outBegIdx1.value != tempInteger || outBegIdx2.value != tempInteger || outNbElement1.value != outNbElement2.value || outNbElement1.value != endIdx - startIdx + 1 + lookbackSignal ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.BadParam ;
      }
      /* Calculate (fast EMA) - (slow EMA). */
      for( i = 0; i < outNbElement1.value; i += 1 ) {
         fastEMABuffer[i] = fastEMABuffer[i] - slowEMABuffer[i];
      }
      /* Copy the result into the output for the caller. */
      System.arraycopy(fastEMABuffer, lookbackSignal, outMACD, 0, (endIdx - startIdx + 1) * 1);
      /* Calculate the signal/trigger line (on double buffer, use ema_private). */
      retCode = emaPrivate(0, outNbElement1.value - 1, fastEMABuffer, optInSignalPeriod, signalK, outBegIdx2, outNbElement2, outMACDSignal);
      if( retCode != RetCode.Success ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      /* Calculate the histogram. */
      for( i = 0; i < outNbElement2.value; i += 1 ) {
         outMACDHist[i] = outMACD[i] - outMACDSignal[i];
      }
      /* All done! Indicate the output limits and return success. */
      outBegIdx.value = startIdx;
      outNBElement.value = outNbElement2.value;
      return RetCode.Success ;
   }
   public RetCode macdUnguarded( int startIdx,
                                 int endIdx,
                                 double inReal[],
                                 int optInFastPeriod,
                                 int optInSlowPeriod,
                                 int optInSignalPeriod,
                                 MInteger outBegIdx,
                                 MInteger outNBElement,
                                 double outMACD[],
                                 double outMACDSignal[],
                                 double outMACDHist[] )
   {
      double[] slowEMABuffer;
      double[] fastEMABuffer;
      RetCode retCode;
      int tempInteger = 0;
      MInteger outBegIdx1 = new MInteger();
      MInteger outNbElement1 = new MInteger();
      MInteger outBegIdx2 = new MInteger();
      MInteger outNbElement2 = new MInteger();
      double slowK = 0;
      double fastK = 0;
      double signalK = 0;
      int lookbackTotal = 0;
      int lookbackSignal = 0;
      int useFixedK = 0;
      int i = 0;
      if( optInSlowPeriod < optInFastPeriod ) {
         tempInteger = optInSlowPeriod;
         optInSlowPeriod = optInFastPeriod;
         optInFastPeriod = tempInteger;
      }
      useFixedK = 0;
      if( optInSlowPeriod == 0 ) {
         optInSlowPeriod = 26;
         slowK = 0.075;
         useFixedK = 1;
      } else {
         slowK = 2.0 / (double)(optInSlowPeriod + 1);
      }
      if( optInFastPeriod == 0 ) {
         optInFastPeriod = 12;
         fastK = 0.15;
         useFixedK = 1;
      } else {
         fastK = 2.0 / (double)(optInFastPeriod + 1);
      }
      signalK = 2.0 / (double)(optInSignalPeriod + 1);
      lookbackSignal = emaLookback(optInSignalPeriod);
      lookbackTotal = lookbackSignal;
      lookbackTotal += emaLookback(optInSlowPeriod);
      if( startIdx < lookbackTotal ) {
         startIdx = lookbackTotal;
      }
      if( startIdx > endIdx ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      tempInteger = endIdx - startIdx + 1 + lookbackSignal;
      fastEMABuffer = new double[(int)(tempInteger * 1)];
      slowEMABuffer = new double[(int)(tempInteger * 1)];
      tempInteger = startIdx - lookbackSignal;
      if( (useFixedK) != 0 ) {
         retCode = emaPrivate(tempInteger, endIdx, inReal, optInSlowPeriod, slowK, outBegIdx1, outNbElement1, slowEMABuffer);
      } else {
         retCode = emaUnguarded(tempInteger, endIdx, inReal, optInSlowPeriod, outBegIdx1, outNbElement1, slowEMABuffer);
      }
      if( retCode != RetCode.Success ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      if( (useFixedK) != 0 ) {
         retCode = emaPrivate(tempInteger, endIdx, inReal, optInFastPeriod, fastK, outBegIdx2, outNbElement2, fastEMABuffer);
      } else {
         retCode = emaUnguarded(tempInteger, endIdx, inReal, optInFastPeriod, outBegIdx2, outNbElement2, fastEMABuffer);
      }
      if( retCode != RetCode.Success ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      if( outBegIdx1.value != tempInteger || outBegIdx2.value != tempInteger || outNbElement1.value != outNbElement2.value || outNbElement1.value != endIdx - startIdx + 1 + lookbackSignal ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.BadParam ;
      }
      for( i = 0; i < outNbElement1.value; i += 1 ) {
         fastEMABuffer[i] = fastEMABuffer[i] - slowEMABuffer[i];
      }
      System.arraycopy(fastEMABuffer, lookbackSignal, outMACD, 0, (endIdx - startIdx + 1) * 1);
      retCode = emaPrivate(0, outNbElement1.value - 1, fastEMABuffer, optInSignalPeriod, signalK, outBegIdx2, outNbElement2, outMACDSignal);
      if( retCode != RetCode.Success ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      for( i = 0; i < outNbElement2.value; i += 1 ) {
         outMACDHist[i] = outMACD[i] - outMACDSignal[i];
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outNbElement2.value;
      return RetCode.Success ;
   }
   public RetCode macd( int startIdx,
                        int endIdx,
                        float inReal[],
                        int optInFastPeriod,
                        int optInSlowPeriod,
                        int optInSignalPeriod,
                        MInteger outBegIdx,
                        MInteger outNBElement,
                        double outMACD[],
                        double outMACDSignal[],
                        double outMACDHist[] )
   {
      double[] slowEMABuffer;
      double[] fastEMABuffer;
      RetCode retCode;
      int tempInteger = 0;
      MInteger outBegIdx1 = new MInteger();
      MInteger outNbElement1 = new MInteger();
      MInteger outBegIdx2 = new MInteger();
      MInteger outNbElement2 = new MInteger();
      double slowK = 0;
      double fastK = 0;
      double signalK = 0;
      int lookbackTotal = 0;
      int lookbackSignal = 0;
      int useFixedK = 0;
      int i = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      if( optInFastPeriod == Integer.MIN_VALUE ) {
         optInFastPeriod = 12;
      } else if( optInFastPeriod < 2 || optInFastPeriod > 100000 ) {
         return RetCode.BadParam;
      }
      if( optInSlowPeriod == Integer.MIN_VALUE ) {
         optInSlowPeriod = 26;
      } else if( optInSlowPeriod < 2 || optInSlowPeriod > 100000 ) {
         return RetCode.BadParam;
      }
      if( optInSignalPeriod == Integer.MIN_VALUE ) {
         optInSignalPeriod = 9;
      } else if( optInSignalPeriod < 1 || optInSignalPeriod > 100000 ) {
         return RetCode.BadParam;
      }
      if( optInSlowPeriod < optInFastPeriod ) {
         tempInteger = optInSlowPeriod;
         optInSlowPeriod = optInFastPeriod;
         optInFastPeriod = tempInteger;
      }
      useFixedK = 0;
      if( optInSlowPeriod == 0 ) {
         optInSlowPeriod = 26;
         slowK = 0.075;
         useFixedK = 1;
      } else {
         slowK = 2.0 / (double)(optInSlowPeriod + 1);
      }
      if( optInFastPeriod == 0 ) {
         optInFastPeriod = 12;
         fastK = 0.15;
         useFixedK = 1;
      } else {
         fastK = 2.0 / (double)(optInFastPeriod + 1);
      }
      signalK = 2.0 / (double)(optInSignalPeriod + 1);
      lookbackSignal = emaLookback(optInSignalPeriod);
      lookbackTotal = lookbackSignal;
      lookbackTotal += emaLookback(optInSlowPeriod);
      if( startIdx < lookbackTotal ) {
         startIdx = lookbackTotal;
      }
      if( startIdx > endIdx ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      tempInteger = endIdx - startIdx + 1 + lookbackSignal;
      fastEMABuffer = new double[(int)(tempInteger * 1)];
      slowEMABuffer = new double[(int)(tempInteger * 1)];
      tempInteger = startIdx - lookbackSignal;
      if( (useFixedK) != 0 ) {
         retCode = emaPrivate(tempInteger, endIdx, inReal, optInSlowPeriod, slowK, outBegIdx1, outNbElement1, slowEMABuffer);
      } else {
         retCode = emaUnguarded(tempInteger, endIdx, inReal, optInSlowPeriod, outBegIdx1, outNbElement1, slowEMABuffer);
      }
      if( retCode != RetCode.Success ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      if( (useFixedK) != 0 ) {
         retCode = emaPrivate(tempInteger, endIdx, inReal, optInFastPeriod, fastK, outBegIdx2, outNbElement2, fastEMABuffer);
      } else {
         retCode = emaUnguarded(tempInteger, endIdx, inReal, optInFastPeriod, outBegIdx2, outNbElement2, fastEMABuffer);
      }
      if( retCode != RetCode.Success ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      if( outBegIdx1.value != tempInteger || outBegIdx2.value != tempInteger || outNbElement1.value != outNbElement2.value || outNbElement1.value != endIdx - startIdx + 1 + lookbackSignal ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.BadParam ;
      }
      for( i = 0; i < outNbElement1.value; i += 1 ) {
         fastEMABuffer[i] = fastEMABuffer[i] - slowEMABuffer[i];
      }
      System.arraycopy(fastEMABuffer, lookbackSignal, outMACD, 0, (endIdx - startIdx + 1) * 1);
      retCode = emaPrivate(0, outNbElement1.value - 1, fastEMABuffer, optInSignalPeriod, signalK, outBegIdx2, outNbElement2, outMACDSignal);
      if( retCode != RetCode.Success ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      for( i = 0; i < outNbElement2.value; i += 1 ) {
         outMACDHist[i] = outMACD[i] - outMACDSignal[i];
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outNbElement2.value;
      return RetCode.Success ;
   }
   public RetCode macdUnguarded( int startIdx,
                                 int endIdx,
                                 float inReal[],
                                 int optInFastPeriod,
                                 int optInSlowPeriod,
                                 int optInSignalPeriod,
                                 MInteger outBegIdx,
                                 MInteger outNBElement,
                                 double outMACD[],
                                 double outMACDSignal[],
                                 double outMACDHist[] )
   {
      double[] slowEMABuffer;
      double[] fastEMABuffer;
      RetCode retCode;
      int tempInteger = 0;
      MInteger outBegIdx1 = new MInteger();
      MInteger outNbElement1 = new MInteger();
      MInteger outBegIdx2 = new MInteger();
      MInteger outNbElement2 = new MInteger();
      double slowK = 0;
      double fastK = 0;
      double signalK = 0;
      int lookbackTotal = 0;
      int lookbackSignal = 0;
      int useFixedK = 0;
      int i = 0;
      if( optInSlowPeriod < optInFastPeriod ) {
         tempInteger = optInSlowPeriod;
         optInSlowPeriod = optInFastPeriod;
         optInFastPeriod = tempInteger;
      }
      useFixedK = 0;
      if( optInSlowPeriod == 0 ) {
         optInSlowPeriod = 26;
         slowK = 0.075;
         useFixedK = 1;
      } else {
         slowK = 2.0 / (double)(optInSlowPeriod + 1);
      }
      if( optInFastPeriod == 0 ) {
         optInFastPeriod = 12;
         fastK = 0.15;
         useFixedK = 1;
      } else {
         fastK = 2.0 / (double)(optInFastPeriod + 1);
      }
      signalK = 2.0 / (double)(optInSignalPeriod + 1);
      lookbackSignal = emaLookback(optInSignalPeriod);
      lookbackTotal = lookbackSignal;
      lookbackTotal += emaLookback(optInSlowPeriod);
      if( startIdx < lookbackTotal ) {
         startIdx = lookbackTotal;
      }
      if( startIdx > endIdx ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      tempInteger = endIdx - startIdx + 1 + lookbackSignal;
      fastEMABuffer = new double[(int)(tempInteger * 1)];
      slowEMABuffer = new double[(int)(tempInteger * 1)];
      tempInteger = startIdx - lookbackSignal;
      if( (useFixedK) != 0 ) {
         retCode = emaPrivate(tempInteger, endIdx, inReal, optInSlowPeriod, slowK, outBegIdx1, outNbElement1, slowEMABuffer);
      } else {
         retCode = emaUnguarded(tempInteger, endIdx, inReal, optInSlowPeriod, outBegIdx1, outNbElement1, slowEMABuffer);
      }
      if( retCode != RetCode.Success ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      if( (useFixedK) != 0 ) {
         retCode = emaPrivate(tempInteger, endIdx, inReal, optInFastPeriod, fastK, outBegIdx2, outNbElement2, fastEMABuffer);
      } else {
         retCode = emaUnguarded(tempInteger, endIdx, inReal, optInFastPeriod, outBegIdx2, outNbElement2, fastEMABuffer);
      }
      if( retCode != RetCode.Success ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      if( outBegIdx1.value != tempInteger || outBegIdx2.value != tempInteger || outNbElement1.value != outNbElement2.value || outNbElement1.value != endIdx - startIdx + 1 + lookbackSignal ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.BadParam ;
      }
      for( i = 0; i < outNbElement1.value; i += 1 ) {
         fastEMABuffer[i] = fastEMABuffer[i] - slowEMABuffer[i];
      }
      System.arraycopy(fastEMABuffer, lookbackSignal, outMACD, 0, (endIdx - startIdx + 1) * 1);
      retCode = emaPrivate(0, outNbElement1.value - 1, fastEMABuffer, optInSignalPeriod, signalK, outBegIdx2, outNbElement2, outMACDSignal);
      if( retCode != RetCode.Success ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      for( i = 0; i < outNbElement2.value; i += 1 ) {
         outMACDHist[i] = outMACD[i] - outMACDSignal[i];
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outNbElement2.value;
      return RetCode.Success ;
   }
