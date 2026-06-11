/* Generated */
   public int bopLookback( )
   {
      return 0 ;

   }
   public RetCode bop( int startIdx,
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
      double tempReal = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      outIdx = 0;
      for( i = startIdx; (i<=endIdx); i += 1 ) {
         tempReal = (inHigh[i]-inLow[i]);
         if( (tempReal < 0.00000000000001) ) {
            outReal[outIdx++] = 0.0;
         } else {
            outReal[outIdx++] = ((inClose[i]-inOpen[i])/tempReal);
         }
      }
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode bopLogic( int startIdx,
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
      double tempReal = 0;
      outIdx = 0;
      for( i = startIdx; (i<=endIdx); i += 1 ) {
         tempReal = (inHigh[i]-inLow[i]);
         if( (tempReal < 0.00000000000001) ) {
            outReal[outIdx++] = 0.0;
         } else {
            outReal[outIdx++] = ((inClose[i]-inOpen[i])/tempReal);
         }
      }
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode bop( int startIdx,
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
      double tempReal = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      outIdx = 0;
      for( i = startIdx; (i<=endIdx); i += 1 ) {
         tempReal = (inHigh[i]-inLow[i]);
         if( (tempReal < 0.00000000000001) ) {
            outReal[outIdx++] = 0.0;
         } else {
            outReal[outIdx++] = ((inClose[i]-inOpen[i])/tempReal);
         }
      }
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode bopLogic( int startIdx,
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
      double tempReal = 0;
      outIdx = 0;
      for( i = startIdx; (i<=endIdx); i += 1 ) {
         tempReal = (inHigh[i]-inLow[i]);
         if( (tempReal < 0.00000000000001) ) {
            outReal[outIdx++] = 0.0;
         } else {
            outReal[outIdx++] = ((inClose[i]-inOpen[i])/tempReal);
         }
      }
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
