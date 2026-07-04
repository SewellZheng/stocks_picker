/* List of contributors:
 *
 *  Initial  Name/description
 *  -------------------------------------------------------------------
 *  MF       Mario Fortier
 *  AM       Adrian Michel
 *
 * Change history:
 *
 *  MMDDYY BY   Description
 *  -------------------------------------------------------------------
 *  010802 MF   Template creation.
 *  052603 MF   Adapt code to compile with .NET Managed C++
 *  082303 MF   Fix #792298. Remove rounding. Bug reported by AM.
 */

   public int adxrLookback( int optInTimePeriod )
   {
      if( optInTimePeriod == Integer.MIN_VALUE ) {
         optInTimePeriod = 14;
      } else if( optInTimePeriod < 2 || optInTimePeriod > 100000 ) {
         return -1;
      }
      if( optInTimePeriod > 1 ) {
         return optInTimePeriod + adxLookback(optInTimePeriod) - 1 ;
      } else {
         return 3 ;
      }

   }
   public RetCode adxr( int startIdx,
                        int endIdx,
                        double inHigh[],
                        double inLow[],
                        double inClose[],
                        int optInTimePeriod,
                        MInteger outBegIdx,
                        MInteger outNBElement,
                        double outReal[] )
   {
      double[] adx;
      int adxrLookback = 0;
      int i = 0;
      int j = 0;
      int outIdx = 0;
      int nbElement = 0;
      RetCode retCode;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      if( optInTimePeriod == Integer.MIN_VALUE ) {
         optInTimePeriod = 14;
      } else if( optInTimePeriod < 2 || optInTimePeriod > 100000 ) {
         return RetCode.BadParam;
      }
      /* Original implementation from Wilder's book was doing some integer
       * rounding in its calculations.
       *
       * This was understandable in the context that at the time the book
       * was written, most user were doing the calculation by hand.
       *
       * For a computer, rounding is unnecessary (and even problematic when inputs
       * are close to 1).
       *
       * TA-Lib does not do the rounding. Still, if you want to reproduce Wilder's examples,
       * you can comment out the following #undef/#define and rebuild the library.
       */
      /* Move up the start index if there is not
       * enough initial data.
       * Always one price bar gets consumed.
       */
      adxrLookback = adxrLookback(optInTimePeriod);
      if( startIdx < adxrLookback ) {
         startIdx = adxrLookback;
      }
      /* Make sure there is still something to evaluate. */
      if( startIdx > endIdx ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      adx = new double[(int)((endIdx - startIdx + optInTimePeriod) * 1)];
      retCode = adxUnguarded(startIdx - (optInTimePeriod - 1), endIdx, inHigh, inLow, inClose, optInTimePeriod, outBegIdx, outNBElement, adx);
      if( retCode != RetCode.Success ) {
         return retCode ;
      }
      i = optInTimePeriod - 1;
      j = 0;
      outIdx = 0;
      nbElement = endIdx - startIdx + 2;
      while( --nbElement != 0 ) {
         outReal[outIdx++] = ((adx[i++] + adx[j++]) / 2.0);
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode adxrUnguarded( int startIdx,
                                 int endIdx,
                                 double inHigh[],
                                 double inLow[],
                                 double inClose[],
                                 int optInTimePeriod,
                                 MInteger outBegIdx,
                                 MInteger outNBElement,
                                 double outReal[] )
   {
      double[] adx;
      int adxrLookback = 0;
      int i = 0;
      int j = 0;
      int outIdx = 0;
      int nbElement = 0;
      RetCode retCode;
      adxrLookback = adxrLookback(optInTimePeriod);
      if( startIdx < adxrLookback ) {
         startIdx = adxrLookback;
      }
      if( startIdx > endIdx ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      adx = new double[(int)((endIdx - startIdx + optInTimePeriod) * 1)];
      retCode = adxUnguarded(startIdx - (optInTimePeriod - 1), endIdx, inHigh, inLow, inClose, optInTimePeriod, outBegIdx, outNBElement, adx);
      if( retCode != RetCode.Success ) {
         return retCode ;
      }
      i = optInTimePeriod - 1;
      j = 0;
      outIdx = 0;
      nbElement = endIdx - startIdx + 2;
      while( --nbElement != 0 ) {
         outReal[outIdx++] = ((adx[i++] + adx[j++]) / 2.0);
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode adxr( int startIdx,
                        int endIdx,
                        float inHigh[],
                        float inLow[],
                        float inClose[],
                        int optInTimePeriod,
                        MInteger outBegIdx,
                        MInteger outNBElement,
                        double outReal[] )
   {
      double[] adx;
      int adxrLookback = 0;
      int i = 0;
      int j = 0;
      int outIdx = 0;
      int nbElement = 0;
      RetCode retCode;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      if( optInTimePeriod == Integer.MIN_VALUE ) {
         optInTimePeriod = 14;
      } else if( optInTimePeriod < 2 || optInTimePeriod > 100000 ) {
         return RetCode.BadParam;
      }
      adxrLookback = adxrLookback(optInTimePeriod);
      if( startIdx < adxrLookback ) {
         startIdx = adxrLookback;
      }
      if( startIdx > endIdx ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      adx = new double[(int)((endIdx - startIdx + optInTimePeriod) * 1)];
      retCode = adxUnguarded(startIdx - (optInTimePeriod - 1), endIdx, inHigh, inLow, inClose, optInTimePeriod, outBegIdx, outNBElement, adx);
      if( retCode != RetCode.Success ) {
         return retCode ;
      }
      i = optInTimePeriod - 1;
      j = 0;
      outIdx = 0;
      nbElement = endIdx - startIdx + 2;
      while( --nbElement != 0 ) {
         outReal[outIdx++] = ((adx[i++] + adx[j++]) / 2.0);
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode adxrUnguarded( int startIdx,
                                 int endIdx,
                                 float inHigh[],
                                 float inLow[],
                                 float inClose[],
                                 int optInTimePeriod,
                                 MInteger outBegIdx,
                                 MInteger outNBElement,
                                 double outReal[] )
   {
      double[] adx;
      int adxrLookback = 0;
      int i = 0;
      int j = 0;
      int outIdx = 0;
      int nbElement = 0;
      RetCode retCode;
      adxrLookback = adxrLookback(optInTimePeriod);
      if( startIdx < adxrLookback ) {
         startIdx = adxrLookback;
      }
      if( startIdx > endIdx ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      adx = new double[(int)((endIdx - startIdx + optInTimePeriod) * 1)];
      retCode = adxUnguarded(startIdx - (optInTimePeriod - 1), endIdx, inHigh, inLow, inClose, optInTimePeriod, outBegIdx, outNBElement, adx);
      if( retCode != RetCode.Success ) {
         return retCode ;
      }
      i = optInTimePeriod - 1;
      j = 0;
      outIdx = 0;
      nbElement = endIdx - startIdx + 2;
      while( --nbElement != 0 ) {
         outReal[outIdx++] = ((adx[i++] + adx[j++]) / 2.0);
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
