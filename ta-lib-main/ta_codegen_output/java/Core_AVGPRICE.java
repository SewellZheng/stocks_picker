/* Generated */
   public int avgpriceLookback( )
   {
      return 0 ;

   }
   public RetCode avgprice( int startIdx,
                            int endIdx,
                            double inOpen[],
                            double inHigh[],
                            double inLow[],
                            double inClose[],
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
         outReal[outIdx++] = ((((inHigh[i]+inLow[i])+inClose[i])+inOpen[i])/4);
      }
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode avgpriceLogic( int startIdx,
                                 int endIdx,
                                 double inOpen[],
                                 double inHigh[],
                                 double inLow[],
                                 double inClose[],
                                 MInteger outBegIdx,
                                 MInteger outNBElement,
                                 double outReal[] )
   {
      int outIdx = 0;
      int i = 0;
      outIdx = 0;
      for( i = startIdx; (i<=endIdx); i += 1 ) {
         outReal[outIdx++] = ((((inHigh[i]+inLow[i])+inClose[i])+inOpen[i])/4);
      }
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode avgprice( int startIdx,
                            int endIdx,
                            float inOpen[],
                            float inHigh[],
                            float inLow[],
                            float inClose[],
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
         outReal[outIdx++] = ((((inHigh[i]+inLow[i])+inClose[i])+inOpen[i])/4);
      }
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode avgpriceLogic( int startIdx,
                                 int endIdx,
                                 float inOpen[],
                                 float inHigh[],
                                 float inLow[],
                                 float inClose[],
                                 MInteger outBegIdx,
                                 MInteger outNBElement,
                                 double outReal[] )
   {
      int outIdx = 0;
      int i = 0;
      outIdx = 0;
      for( i = startIdx; (i<=endIdx); i += 1 ) {
         outReal[outIdx++] = ((((inHigh[i]+inLow[i])+inClose[i])+inOpen[i])/4);
      }
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
