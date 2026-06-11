/* Generated */
   public int midpriceLookback( int optInTimePeriod )
   {
      return (optInTimePeriod-1) ;

   }
   public RetCode midprice( int startIdx,
                            int endIdx,
                            double inHigh[],
                            double inLow[],
                            int optInTimePeriod,
                            MInteger outBegIdx,
                            MInteger outNBElement,
                            double outReal[] )
   {
      double lowest = 0;
      double highest = 0;
      double tmp = 0;
      int outIdx = 0;
      int nbInitialElementNeeded = 0;
      int trailingIdx = 0;
      int today = 0;
      int i = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      nbInitialElementNeeded = (optInTimePeriod-1);
      if( (startIdx<nbInitialElementNeeded) ) {
         startIdx = nbInitialElementNeeded;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outIdx = 0;
      today = startIdx;
      trailingIdx = (startIdx-nbInitialElementNeeded);
      while( (today<=endIdx) ) {
         lowest = inLow[trailingIdx];
         highest = inHigh[trailingIdx];
         trailingIdx += 1;
         for( i = trailingIdx; (i<=today); i += 1 ) {
            tmp = inLow[i];
            if( (tmp<lowest) ) {
               lowest = tmp;
            }
            tmp = inHigh[i];
            if( (tmp>highest) ) {
               highest = tmp;
            }
         }
         outReal[outIdx++] = ((highest+lowest)/2.0);
         today += 1;
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode midpriceLogic( int startIdx,
                                 int endIdx,
                                 double inHigh[],
                                 double inLow[],
                                 int optInTimePeriod,
                                 MInteger outBegIdx,
                                 MInteger outNBElement,
                                 double outReal[] )
   {
      double lowest = 0;
      double highest = 0;
      double tmp = 0;
      int outIdx = 0;
      int nbInitialElementNeeded = 0;
      int trailingIdx = 0;
      int today = 0;
      int i = 0;
      nbInitialElementNeeded = (optInTimePeriod-1);
      if( (startIdx<nbInitialElementNeeded) ) {
         startIdx = nbInitialElementNeeded;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outIdx = 0;
      today = startIdx;
      trailingIdx = (startIdx-nbInitialElementNeeded);
      while( (today<=endIdx) ) {
         lowest = inLow[trailingIdx];
         highest = inHigh[trailingIdx];
         trailingIdx += 1;
         for( i = trailingIdx; (i<=today); i += 1 ) {
            tmp = inLow[i];
            if( (tmp<lowest) ) {
               lowest = tmp;
            }
            tmp = inHigh[i];
            if( (tmp>highest) ) {
               highest = tmp;
            }
         }
         outReal[outIdx++] = ((highest+lowest)/2.0);
         today += 1;
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode midprice( int startIdx,
                            int endIdx,
                            float inHigh[],
                            float inLow[],
                            int optInTimePeriod,
                            MInteger outBegIdx,
                            MInteger outNBElement,
                            double outReal[] )
   {
      double lowest = 0;
      double highest = 0;
      double tmp = 0;
      int outIdx = 0;
      int nbInitialElementNeeded = 0;
      int trailingIdx = 0;
      int today = 0;
      int i = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      nbInitialElementNeeded = (optInTimePeriod-1);
      if( (startIdx<nbInitialElementNeeded) ) {
         startIdx = nbInitialElementNeeded;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outIdx = 0;
      today = startIdx;
      trailingIdx = (startIdx-nbInitialElementNeeded);
      while( (today<=endIdx) ) {
         lowest = inLow[trailingIdx];
         highest = inHigh[trailingIdx];
         trailingIdx += 1;
         for( i = trailingIdx; (i<=today); i += 1 ) {
            tmp = inLow[i];
            if( (tmp<lowest) ) {
               lowest = tmp;
            }
            tmp = inHigh[i];
            if( (tmp>highest) ) {
               highest = tmp;
            }
         }
         outReal[outIdx++] = ((highest+lowest)/2.0);
         today += 1;
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode midpriceLogic( int startIdx,
                                 int endIdx,
                                 float inHigh[],
                                 float inLow[],
                                 int optInTimePeriod,
                                 MInteger outBegIdx,
                                 MInteger outNBElement,
                                 double outReal[] )
   {
      double lowest = 0;
      double highest = 0;
      double tmp = 0;
      int outIdx = 0;
      int nbInitialElementNeeded = 0;
      int trailingIdx = 0;
      int today = 0;
      int i = 0;
      nbInitialElementNeeded = (optInTimePeriod-1);
      if( (startIdx<nbInitialElementNeeded) ) {
         startIdx = nbInitialElementNeeded;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outIdx = 0;
      today = startIdx;
      trailingIdx = (startIdx-nbInitialElementNeeded);
      while( (today<=endIdx) ) {
         lowest = inLow[trailingIdx];
         highest = inHigh[trailingIdx];
         trailingIdx += 1;
         for( i = trailingIdx; (i<=today); i += 1 ) {
            tmp = inLow[i];
            if( (tmp<lowest) ) {
               lowest = tmp;
            }
            tmp = inHigh[i];
            if( (tmp>highest) ) {
               highest = tmp;
            }
         }
         outReal[outIdx++] = ((highest+lowest)/2.0);
         today += 1;
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
