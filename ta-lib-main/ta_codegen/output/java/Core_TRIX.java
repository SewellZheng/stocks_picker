/* List of contributors:
 *
 *  Initial  Name/description
 *  -------------------------------------------------------------------
 *  MF       Mario Fortier
 *  AA       Andrew Atkinson
 *
 * Change history:
 *
 *  MMDDYY BY   Description
 *  -------------------------------------------------------------------
 *  112400 MF   Template creation.
 *  052603 MF   Adapt code to compile with .NET Managed C++
 *  020605 AA   Fix #1117656. NULL pointer assignement.
 */

   public int trixLookback( int optInTimePeriod )
   {
      if( optInTimePeriod == Integer.MIN_VALUE ) {
         optInTimePeriod = 30;
      } else if( optInTimePeriod < 1 || optInTimePeriod > 100000 ) {
         return -1;
      }
      int emaLookback;
      emaLookback = emaLookback(optInTimePeriod);
      return emaLookback * 3 + rocRLookback(1) ;

   }
   public RetCode trix( int startIdx,
                        int endIdx,
                        double inReal[],
                        int optInTimePeriod,
                        MInteger outBegIdx,
                        MInteger outNBElement,
                        double outReal[] )
   {
      double[] tempBuffer;
      MInteger nbElement = new MInteger();
      MInteger begIdx = new MInteger();
      int totalLookback = 0;
      int emaLookback = 0;
      int rocLookback = 0;
      RetCode retCode;
      int nbElementToOutput = 0;
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
      /* Adjust the startIdx to account for the lookback. */
      emaLookback = emaLookback(optInTimePeriod);
      rocLookback = rocRLookback(1);
      totalLookback = emaLookback * 3 + rocLookback;
      if( startIdx < totalLookback ) {
         startIdx = totalLookback;
      }
      /* Make sure there is still something to evaluate. */
      if( startIdx > endIdx ) {
         outNBElement.value = 0;
         outBegIdx.value = 0;
         return RetCode.Success ;
      }
      outBegIdx.value = startIdx;
      nbElementToOutput = endIdx - startIdx + 1 + totalLookback;
      /* Allocate a temporary buffer for performing
       * the calculation.
       */
      tempBuffer = new double[(int)(nbElementToOutput * 1)];
      /* Calculate the first EMA */
      retCode = emaUnguarded(startIdx - totalLookback, endIdx, inReal, optInTimePeriod, begIdx, nbElement, tempBuffer);
      /* Verify for failure or if not enough data after
       * calculating the EMA.
       */
      if( retCode != RetCode.Success || nbElement.value == 0 ) {
         outNBElement.value = 0;
         outBegIdx.value = 0;
         return retCode ;
      }
      nbElementToOutput -= 1;
      /* Make this variable zero base from now on. */
      /* Calculate the second EMA */
      nbElementToOutput -= emaLookback;
      retCode = emaUnguarded(0, nbElementToOutput, tempBuffer, optInTimePeriod, begIdx, nbElement, tempBuffer);
      /* Verify for failure or if not enough data after
       * calculating the EMA.
       */
      if( retCode != RetCode.Success || nbElement.value == 0 ) {
         outNBElement.value = 0;
         outBegIdx.value = 0;
         return retCode ;
      }
      /* Calculate the third EMA */
      nbElementToOutput -= emaLookback;
      retCode = emaUnguarded(0, nbElementToOutput, tempBuffer, optInTimePeriod, begIdx, nbElement, tempBuffer);
      /* Verify for failure or if not enough data after
       * calculating the EMA.
       */
      if( retCode != RetCode.Success || nbElement.value == 0 ) {
         outNBElement.value = 0;
         outBegIdx.value = 0;
         return retCode ;
      }
      /* Calculate the 1-day Rate-Of-Change */
      nbElementToOutput -= emaLookback;
      retCode = rocUnguarded(0, nbElementToOutput, tempBuffer, 1, begIdx, outNBElement, outReal);
      /* Verify for failure or if not enough data after
       * calculating the rate-of-change.
       */
      if( retCode != RetCode.Success || (int)outNBElement.value == 0 ) {
         outNBElement.value = 0;
         outBegIdx.value = 0;
         return retCode ;
      }
      return RetCode.Success ;
   }
   public RetCode trixUnguarded( int startIdx,
                                 int endIdx,
                                 double inReal[],
                                 int optInTimePeriod,
                                 MInteger outBegIdx,
                                 MInteger outNBElement,
                                 double outReal[] )
   {
      double[] tempBuffer;
      MInteger nbElement = new MInteger();
      MInteger begIdx = new MInteger();
      int totalLookback = 0;
      int emaLookback = 0;
      int rocLookback = 0;
      RetCode retCode;
      int nbElementToOutput = 0;
      emaLookback = emaLookback(optInTimePeriod);
      rocLookback = rocRLookback(1);
      totalLookback = emaLookback * 3 + rocLookback;
      if( startIdx < totalLookback ) {
         startIdx = totalLookback;
      }
      if( startIdx > endIdx ) {
         outNBElement.value = 0;
         outBegIdx.value = 0;
         return RetCode.Success ;
      }
      outBegIdx.value = startIdx;
      nbElementToOutput = endIdx - startIdx + 1 + totalLookback;
      tempBuffer = new double[(int)(nbElementToOutput * 1)];
      retCode = emaUnguarded(startIdx - totalLookback, endIdx, inReal, optInTimePeriod, begIdx, nbElement, tempBuffer);
      if( retCode != RetCode.Success || nbElement.value == 0 ) {
         outNBElement.value = 0;
         outBegIdx.value = 0;
         return retCode ;
      }
      nbElementToOutput -= 1;
      nbElementToOutput -= emaLookback;
      retCode = emaUnguarded(0, nbElementToOutput, tempBuffer, optInTimePeriod, begIdx, nbElement, tempBuffer);
      if( retCode != RetCode.Success || nbElement.value == 0 ) {
         outNBElement.value = 0;
         outBegIdx.value = 0;
         return retCode ;
      }
      nbElementToOutput -= emaLookback;
      retCode = emaUnguarded(0, nbElementToOutput, tempBuffer, optInTimePeriod, begIdx, nbElement, tempBuffer);
      if( retCode != RetCode.Success || nbElement.value == 0 ) {
         outNBElement.value = 0;
         outBegIdx.value = 0;
         return retCode ;
      }
      nbElementToOutput -= emaLookback;
      retCode = rocUnguarded(0, nbElementToOutput, tempBuffer, 1, begIdx, outNBElement, outReal);
      if( retCode != RetCode.Success || (int)outNBElement.value == 0 ) {
         outNBElement.value = 0;
         outBegIdx.value = 0;
         return retCode ;
      }
      return RetCode.Success ;
   }
   public RetCode trix( int startIdx,
                        int endIdx,
                        float inReal[],
                        int optInTimePeriod,
                        MInteger outBegIdx,
                        MInteger outNBElement,
                        double outReal[] )
   {
      double[] tempBuffer;
      MInteger nbElement = new MInteger();
      MInteger begIdx = new MInteger();
      int totalLookback = 0;
      int emaLookback = 0;
      int rocLookback = 0;
      RetCode retCode;
      int nbElementToOutput = 0;
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
      emaLookback = emaLookback(optInTimePeriod);
      rocLookback = rocRLookback(1);
      totalLookback = emaLookback * 3 + rocLookback;
      if( startIdx < totalLookback ) {
         startIdx = totalLookback;
      }
      if( startIdx > endIdx ) {
         outNBElement.value = 0;
         outBegIdx.value = 0;
         return RetCode.Success ;
      }
      outBegIdx.value = startIdx;
      nbElementToOutput = endIdx - startIdx + 1 + totalLookback;
      tempBuffer = new double[(int)(nbElementToOutput * 1)];
      retCode = emaUnguarded(startIdx - totalLookback, endIdx, inReal, optInTimePeriod, begIdx, nbElement, tempBuffer);
      if( retCode != RetCode.Success || nbElement.value == 0 ) {
         outNBElement.value = 0;
         outBegIdx.value = 0;
         return retCode ;
      }
      nbElementToOutput -= 1;
      nbElementToOutput -= emaLookback;
      retCode = emaUnguarded(0, nbElementToOutput, tempBuffer, optInTimePeriod, begIdx, nbElement, tempBuffer);
      if( retCode != RetCode.Success || nbElement.value == 0 ) {
         outNBElement.value = 0;
         outBegIdx.value = 0;
         return retCode ;
      }
      nbElementToOutput -= emaLookback;
      retCode = emaUnguarded(0, nbElementToOutput, tempBuffer, optInTimePeriod, begIdx, nbElement, tempBuffer);
      if( retCode != RetCode.Success || nbElement.value == 0 ) {
         outNBElement.value = 0;
         outBegIdx.value = 0;
         return retCode ;
      }
      nbElementToOutput -= emaLookback;
      retCode = rocUnguarded(0, nbElementToOutput, tempBuffer, 1, begIdx, outNBElement, outReal);
      if( retCode != RetCode.Success || (int)outNBElement.value == 0 ) {
         outNBElement.value = 0;
         outBegIdx.value = 0;
         return retCode ;
      }
      return RetCode.Success ;
   }
   public RetCode trixUnguarded( int startIdx,
                                 int endIdx,
                                 float inReal[],
                                 int optInTimePeriod,
                                 MInteger outBegIdx,
                                 MInteger outNBElement,
                                 double outReal[] )
   {
      double[] tempBuffer;
      MInteger nbElement = new MInteger();
      MInteger begIdx = new MInteger();
      int totalLookback = 0;
      int emaLookback = 0;
      int rocLookback = 0;
      RetCode retCode;
      int nbElementToOutput = 0;
      emaLookback = emaLookback(optInTimePeriod);
      rocLookback = rocRLookback(1);
      totalLookback = emaLookback * 3 + rocLookback;
      if( startIdx < totalLookback ) {
         startIdx = totalLookback;
      }
      if( startIdx > endIdx ) {
         outNBElement.value = 0;
         outBegIdx.value = 0;
         return RetCode.Success ;
      }
      outBegIdx.value = startIdx;
      nbElementToOutput = endIdx - startIdx + 1 + totalLookback;
      tempBuffer = new double[(int)(nbElementToOutput * 1)];
      retCode = emaUnguarded(startIdx - totalLookback, endIdx, inReal, optInTimePeriod, begIdx, nbElement, tempBuffer);
      if( retCode != RetCode.Success || nbElement.value == 0 ) {
         outNBElement.value = 0;
         outBegIdx.value = 0;
         return retCode ;
      }
      nbElementToOutput -= 1;
      nbElementToOutput -= emaLookback;
      retCode = emaUnguarded(0, nbElementToOutput, tempBuffer, optInTimePeriod, begIdx, nbElement, tempBuffer);
      if( retCode != RetCode.Success || nbElement.value == 0 ) {
         outNBElement.value = 0;
         outBegIdx.value = 0;
         return retCode ;
      }
      nbElementToOutput -= emaLookback;
      retCode = emaUnguarded(0, nbElementToOutput, tempBuffer, optInTimePeriod, begIdx, nbElement, tempBuffer);
      if( retCode != RetCode.Success || nbElement.value == 0 ) {
         outNBElement.value = 0;
         outBegIdx.value = 0;
         return retCode ;
      }
      nbElementToOutput -= emaLookback;
      retCode = rocUnguarded(0, nbElementToOutput, tempBuffer, 1, begIdx, outNBElement, outReal);
      if( retCode != RetCode.Success || (int)outNBElement.value == 0 ) {
         outNBElement.value = 0;
         outBegIdx.value = 0;
         return retCode ;
      }
      return RetCode.Success ;
   }
