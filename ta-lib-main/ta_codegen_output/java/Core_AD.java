/* Generated */
   public int adLookback( )
   {
      return 0 ;

   }
   public RetCode ad( int startIdx,
                      int endIdx,
                      double inHigh[],
                      double inLow[],
                      double inClose[],
                      double inVolume[],
                      MInteger outBegIdx,
                      MInteger outNBElement,
                      double outReal[] )
   {
      int nbBar = 0;
      int currentBar = 0;
      int outIdx = 0;
      double high = 0;
      double low = 0;
      double close = 0;
      double tmp = 0;
      double ad = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      nbBar = ((endIdx-startIdx)+1);
      outNBElement.value = nbBar;
      outBegIdx.value = startIdx;
      currentBar = startIdx;
      outIdx = 0;
      ad = 0.0;
      while( (nbBar!=0) ) {
         high = inHigh[currentBar];
         low = inLow[currentBar];
         tmp = (high-low);
         close = inClose[currentBar];
         if( (tmp>0.0) ) {
            ad += ((((close-low)-(high-close))/tmp)*((double)inVolume[currentBar]));
         }
         outReal[outIdx++] = ad;
         currentBar += 1;
         nbBar -= 1;
      }
      return RetCode.Success ;
   }
   public RetCode adLogic( int startIdx,
                           int endIdx,
                           double inHigh[],
                           double inLow[],
                           double inClose[],
                           double inVolume[],
                           MInteger outBegIdx,
                           MInteger outNBElement,
                           double outReal[] )
   {
      int nbBar = 0;
      int currentBar = 0;
      int outIdx = 0;
      double high = 0;
      double low = 0;
      double close = 0;
      double tmp = 0;
      double ad = 0;
      nbBar = ((endIdx-startIdx)+1);
      outNBElement.value = nbBar;
      outBegIdx.value = startIdx;
      currentBar = startIdx;
      outIdx = 0;
      ad = 0.0;
      while( (nbBar!=0) ) {
         high = inHigh[currentBar];
         low = inLow[currentBar];
         tmp = (high-low);
         close = inClose[currentBar];
         if( (tmp>0.0) ) {
            ad += ((((close-low)-(high-close))/tmp)*((double)inVolume[currentBar]));
         }
         outReal[outIdx++] = ad;
         currentBar += 1;
         nbBar -= 1;
      }
      return RetCode.Success ;
   }
   public RetCode ad( int startIdx,
                      int endIdx,
                      float inHigh[],
                      float inLow[],
                      float inClose[],
                      float inVolume[],
                      MInteger outBegIdx,
                      MInteger outNBElement,
                      double outReal[] )
   {
      int nbBar = 0;
      int currentBar = 0;
      int outIdx = 0;
      double high = 0;
      double low = 0;
      double close = 0;
      double tmp = 0;
      double ad = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      nbBar = ((endIdx-startIdx)+1);
      outNBElement.value = nbBar;
      outBegIdx.value = startIdx;
      currentBar = startIdx;
      outIdx = 0;
      ad = 0.0;
      while( (nbBar!=0) ) {
         high = inHigh[currentBar];
         low = inLow[currentBar];
         tmp = (high-low);
         close = inClose[currentBar];
         if( (tmp>0.0) ) {
            ad += ((((close-low)-(high-close))/tmp)*((double)inVolume[currentBar]));
         }
         outReal[outIdx++] = ad;
         currentBar += 1;
         nbBar -= 1;
      }
      return RetCode.Success ;
   }
   public RetCode adLogic( int startIdx,
                           int endIdx,
                           float inHigh[],
                           float inLow[],
                           float inClose[],
                           float inVolume[],
                           MInteger outBegIdx,
                           MInteger outNBElement,
                           double outReal[] )
   {
      int nbBar = 0;
      int currentBar = 0;
      int outIdx = 0;
      double high = 0;
      double low = 0;
      double close = 0;
      double tmp = 0;
      double ad = 0;
      nbBar = ((endIdx-startIdx)+1);
      outNBElement.value = nbBar;
      outBegIdx.value = startIdx;
      currentBar = startIdx;
      outIdx = 0;
      ad = 0.0;
      while( (nbBar!=0) ) {
         high = inHigh[currentBar];
         low = inLow[currentBar];
         tmp = (high-low);
         close = inClose[currentBar];
         if( (tmp>0.0) ) {
            ad += ((((close-low)-(high-close))/tmp)*((double)inVolume[currentBar]));
         }
         outReal[outIdx++] = ad;
         currentBar += 1;
         nbBar -= 1;
      }
      return RetCode.Success ;
   }
