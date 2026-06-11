/* Generated */
   public int sinLookback( )
   {
      return 0 ;

   }
   public RetCode sin( int startIdx,
                       int endIdx,
                       double inReal[],
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
      for( i = startIdx, outIdx = 0; (i<=endIdx); i += 1, outIdx += 1 ) {
         outReal[outIdx] = Math.sin(inReal[i]);
      }
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode sinLogic( int startIdx,
                            int endIdx,
                            double inReal[],
                            MInteger outBegIdx,
                            MInteger outNBElement,
                            double outReal[] )
   {
      int outIdx = 0;
      int i = 0;
      for( i = startIdx, outIdx = 0; (i<=endIdx); i += 1, outIdx += 1 ) {
         outReal[outIdx] = Math.sin(inReal[i]);
      }
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode sin( int startIdx,
                       int endIdx,
                       float inReal[],
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
      for( i = startIdx, outIdx = 0; (i<=endIdx); i += 1, outIdx += 1 ) {
         outReal[outIdx] = Math.sin(inReal[i]);
      }
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode sinLogic( int startIdx,
                            int endIdx,
                            float inReal[],
                            MInteger outBegIdx,
                            MInteger outNBElement,
                            double outReal[] )
   {
      int outIdx = 0;
      int i = 0;
      for( i = startIdx, outIdx = 0; (i<=endIdx); i += 1, outIdx += 1 ) {
         outReal[outIdx] = Math.sin(inReal[i]);
      }
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
