/* Generated */
   public int rocr100Lookback( int optInTimePeriod )
   {
      return optInTimePeriod ;

   }
   public RetCode rocr100( int startIdx,
                           int endIdx,
                           double inReal[],
                           int optInTimePeriod,
                           MInteger outBegIdx,
                           MInteger outNBElement,
                           double outReal[] )
   {
      int inIdx = 0;
      int outIdx = 0;
      int trailingIdx = 0;
      double tempReal = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      if( (startIdx<optInTimePeriod) ) {
         startIdx = optInTimePeriod;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outIdx = 0;
      inIdx = startIdx;
      trailingIdx = (startIdx-optInTimePeriod);
      while( (inIdx<=endIdx) ) {
         tempReal = inReal[trailingIdx++];
         if( (tempReal!=0.0) ) {
            outReal[outIdx++] = ((inReal[inIdx]/tempReal)*100.0);
         } else {
            outReal[outIdx++] = 0.0;
         }
         inIdx += 1;
      }
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode rocr100Logic( int startIdx,
                                int endIdx,
                                double inReal[],
                                int optInTimePeriod,
                                MInteger outBegIdx,
                                MInteger outNBElement,
                                double outReal[] )
   {
      int inIdx = 0;
      int outIdx = 0;
      int trailingIdx = 0;
      double tempReal = 0;
      if( (startIdx<optInTimePeriod) ) {
         startIdx = optInTimePeriod;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outIdx = 0;
      inIdx = startIdx;
      trailingIdx = (startIdx-optInTimePeriod);
      while( (inIdx<=endIdx) ) {
         tempReal = inReal[trailingIdx++];
         if( (tempReal!=0.0) ) {
            outReal[outIdx++] = ((inReal[inIdx]/tempReal)*100.0);
         } else {
            outReal[outIdx++] = 0.0;
         }
         inIdx += 1;
      }
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode rocr100( int startIdx,
                           int endIdx,
                           float inReal[],
                           int optInTimePeriod,
                           MInteger outBegIdx,
                           MInteger outNBElement,
                           double outReal[] )
   {
      int inIdx = 0;
      int outIdx = 0;
      int trailingIdx = 0;
      double tempReal = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      if( (startIdx<optInTimePeriod) ) {
         startIdx = optInTimePeriod;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outIdx = 0;
      inIdx = startIdx;
      trailingIdx = (startIdx-optInTimePeriod);
      while( (inIdx<=endIdx) ) {
         tempReal = inReal[trailingIdx++];
         if( (tempReal!=0.0) ) {
            outReal[outIdx++] = ((inReal[inIdx]/tempReal)*100.0);
         } else {
            outReal[outIdx++] = 0.0;
         }
         inIdx += 1;
      }
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode rocr100Logic( int startIdx,
                                int endIdx,
                                float inReal[],
                                int optInTimePeriod,
                                MInteger outBegIdx,
                                MInteger outNBElement,
                                double outReal[] )
   {
      int inIdx = 0;
      int outIdx = 0;
      int trailingIdx = 0;
      double tempReal = 0;
      if( (startIdx<optInTimePeriod) ) {
         startIdx = optInTimePeriod;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outIdx = 0;
      inIdx = startIdx;
      trailingIdx = (startIdx-optInTimePeriod);
      while( (inIdx<=endIdx) ) {
         tempReal = inReal[trailingIdx++];
         if( (tempReal!=0.0) ) {
            outReal[outIdx++] = ((inReal[inIdx]/tempReal)*100.0);
         } else {
            outReal[outIdx++] = 0.0;
         }
         inIdx += 1;
      }
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
