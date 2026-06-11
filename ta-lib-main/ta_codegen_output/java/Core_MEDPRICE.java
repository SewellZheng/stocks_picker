/* Generated */
   public int medpriceLookback( )
   {
      return 0 ;

   }
   public RetCode medprice( int startIdx,
                            int endIdx,
                            double inHigh[],
                            double inLow[],
                            MInteger outBegIdx,
                            MInteger outNBElement,
                            double outReal[] )
   {
      int outIdx = 0;
      int i = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      outIdx = 0;
      for( i = startIdx; (i<=endIdx); i += 1 ) {
         outReal[outIdx++] = ((inHigh[i]+inLow[i])/2.0);
      }
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode medpriceLogic( int startIdx,
                                 int endIdx,
                                 double inHigh[],
                                 double inLow[],
                                 MInteger outBegIdx,
                                 MInteger outNBElement,
                                 double outReal[] )
   {
      int outIdx = 0;
      int i = 0;
      outIdx = 0;
      for( i = startIdx; (i<=endIdx); i += 1 ) {
         outReal[outIdx++] = ((inHigh[i]+inLow[i])/2.0);
      }
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode medprice( int startIdx,
                            int endIdx,
                            float inHigh[],
                            float inLow[],
                            MInteger outBegIdx,
                            MInteger outNBElement,
                            double outReal[] )
   {
      int outIdx = 0;
      int i = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      outIdx = 0;
      for( i = startIdx; (i<=endIdx); i += 1 ) {
         outReal[outIdx++] = ((inHigh[i]+inLow[i])/2.0);
      }
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode medpriceLogic( int startIdx,
                                 int endIdx,
                                 float inHigh[],
                                 float inLow[],
                                 MInteger outBegIdx,
                                 MInteger outNBElement,
                                 double outReal[] )
   {
      int outIdx = 0;
      int i = 0;
      outIdx = 0;
      for( i = startIdx; (i<=endIdx); i += 1 ) {
         outReal[outIdx++] = ((inHigh[i]+inLow[i])/2.0);
      }
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
