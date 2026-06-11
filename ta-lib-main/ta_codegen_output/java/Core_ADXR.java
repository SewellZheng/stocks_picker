/* Generated */
   public int adxrLookback( int optInTimePeriod )
   {
      if( (optInTimePeriod>1) ) {
         return ((optInTimePeriod+adxLookback(optInTimePeriod))-1) ;
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
      adxrLookback = adxrLookback(optInTimePeriod);
      if( (startIdx<adxrLookback) ) {
         startIdx = adxrLookback;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      adx = new double[(int)((((endIdx-startIdx)+optInTimePeriod)*1))];
      retCode = adxLogic((startIdx-(optInTimePeriod-1)), endIdx, inHigh, inLow, inClose, optInTimePeriod, outBegIdx, outNBElement, adx);
      if( (retCode!=RetCode.Success) ) {
         return retCode ;
      }
      i = (optInTimePeriod-1);
      j = 0;
      outIdx = 0;
      nbElement = ((endIdx-startIdx)+2);
      while( (--nbElement!=0) ) {
         outReal[outIdx++] = ((adx[i++]+adx[j++])/2.0);
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode adxrLogic( int startIdx,
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
      if( (startIdx<adxrLookback) ) {
         startIdx = adxrLookback;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      adx = new double[(int)((((endIdx-startIdx)+optInTimePeriod)*1))];
      retCode = adxLogic((startIdx-(optInTimePeriod-1)), endIdx, inHigh, inLow, inClose, optInTimePeriod, outBegIdx, outNBElement, adx);
      if( (retCode!=RetCode.Success) ) {
         return retCode ;
      }
      i = (optInTimePeriod-1);
      j = 0;
      outIdx = 0;
      nbElement = ((endIdx-startIdx)+2);
      while( (--nbElement!=0) ) {
         outReal[outIdx++] = ((adx[i++]+adx[j++])/2.0);
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
      adxrLookback = adxrLookback(optInTimePeriod);
      if( (startIdx<adxrLookback) ) {
         startIdx = adxrLookback;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      adx = new double[(int)((((endIdx-startIdx)+optInTimePeriod)*1))];
      retCode = adxLogic((startIdx-(optInTimePeriod-1)), endIdx, inHigh, inLow, inClose, optInTimePeriod, outBegIdx, outNBElement, adx);
      if( (retCode!=RetCode.Success) ) {
         return retCode ;
      }
      i = (optInTimePeriod-1);
      j = 0;
      outIdx = 0;
      nbElement = ((endIdx-startIdx)+2);
      while( (--nbElement!=0) ) {
         outReal[outIdx++] = ((adx[i++]+adx[j++])/2.0);
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode adxrLogic( int startIdx,
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
      if( (startIdx<adxrLookback) ) {
         startIdx = adxrLookback;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      adx = new double[(int)((((endIdx-startIdx)+optInTimePeriod)*1))];
      retCode = adxLogic((startIdx-(optInTimePeriod-1)), endIdx, inHigh, inLow, inClose, optInTimePeriod, outBegIdx, outNBElement, adx);
      if( (retCode!=RetCode.Success) ) {
         return retCode ;
      }
      i = (optInTimePeriod-1);
      j = 0;
      outIdx = 0;
      nbElement = ((endIdx-startIdx)+2);
      while( (--nbElement!=0) ) {
         outReal[outIdx++] = ((adx[i++]+adx[j++])/2.0);
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
