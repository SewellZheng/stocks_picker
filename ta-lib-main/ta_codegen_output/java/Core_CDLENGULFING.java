/* Generated */
   public int cdlengulfingLookback( )
   {
      return 2 ;

   }
   public RetCode cdlengulfing( int startIdx,
                                int endIdx,
                                double inOpen[],
                                double inHigh[],
                                double inLow[],
                                double inClose[],
                                MInteger outBegIdx,
                                MInteger outNBElement,
                                int outInteger[] )
   {
      int i = 0;
      int outIdx = 0;
      int lookbackTotal = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackTotal = cdlengulfingLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( (((((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==1)&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-1)))&&(((inClose[i]>=inOpen[(i-1)])&&(inOpen[i]<inClose[(i-1)]))||((inClose[i]>inOpen[(i-1)])&&(inOpen[i]<=inClose[(i-1)]))))||((((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==(0-1))&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==1))&&(((inOpen[i]>=inClose[(i-1)])&&(inClose[i]<inOpen[(i-1)]))||((inOpen[i]>inClose[(i-1)])&&(inClose[i]<=inOpen[(i-1)]))))) ) {
            if( ((inOpen[i]!=inClose[(i-1)])&&(inClose[i]!=inOpen[(i-1)])) ) {
               outInteger[outIdx++] = ((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))*100);
            } else {
               outInteger[outIdx++] = ((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))*80);
            }
         } else {
            outInteger[outIdx++] = 0;
         }
         i += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdlengulfingLogic( int startIdx,
                                     int endIdx,
                                     double inOpen[],
                                     double inHigh[],
                                     double inLow[],
                                     double inClose[],
                                     MInteger outBegIdx,
                                     MInteger outNBElement,
                                     int outInteger[] )
   {
      int i = 0;
      int outIdx = 0;
      int lookbackTotal = 0;
      lookbackTotal = cdlengulfingLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( (((((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==1)&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-1)))&&(((inClose[i]>=inOpen[(i-1)])&&(inOpen[i]<inClose[(i-1)]))||((inClose[i]>inOpen[(i-1)])&&(inOpen[i]<=inClose[(i-1)]))))||((((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==(0-1))&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==1))&&(((inOpen[i]>=inClose[(i-1)])&&(inClose[i]<inOpen[(i-1)]))||((inOpen[i]>inClose[(i-1)])&&(inClose[i]<=inOpen[(i-1)]))))) ) {
            if( ((inOpen[i]!=inClose[(i-1)])&&(inClose[i]!=inOpen[(i-1)])) ) {
               outInteger[outIdx++] = ((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))*100);
            } else {
               outInteger[outIdx++] = ((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))*80);
            }
         } else {
            outInteger[outIdx++] = 0;
         }
         i += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdlengulfing( int startIdx,
                                int endIdx,
                                float inOpen[],
                                float inHigh[],
                                float inLow[],
                                float inClose[],
                                MInteger outBegIdx,
                                MInteger outNBElement,
                                int outInteger[] )
   {
      int i = 0;
      int outIdx = 0;
      int lookbackTotal = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackTotal = cdlengulfingLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( (((((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==1)&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-1)))&&(((inClose[i]>=inOpen[(i-1)])&&(inOpen[i]<inClose[(i-1)]))||((inClose[i]>inOpen[(i-1)])&&(inOpen[i]<=inClose[(i-1)]))))||((((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==(0-1))&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==1))&&(((inOpen[i]>=inClose[(i-1)])&&(inClose[i]<inOpen[(i-1)]))||((inOpen[i]>inClose[(i-1)])&&(inClose[i]<=inOpen[(i-1)]))))) ) {
            if( ((inOpen[i]!=inClose[(i-1)])&&(inClose[i]!=inOpen[(i-1)])) ) {
               outInteger[outIdx++] = ((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))*100);
            } else {
               outInteger[outIdx++] = ((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))*80);
            }
         } else {
            outInteger[outIdx++] = 0;
         }
         i += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdlengulfingLogic( int startIdx,
                                     int endIdx,
                                     float inOpen[],
                                     float inHigh[],
                                     float inLow[],
                                     float inClose[],
                                     MInteger outBegIdx,
                                     MInteger outNBElement,
                                     int outInteger[] )
   {
      int i = 0;
      int outIdx = 0;
      int lookbackTotal = 0;
      lookbackTotal = cdlengulfingLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( (((((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==1)&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-1)))&&(((inClose[i]>=inOpen[(i-1)])&&(inOpen[i]<inClose[(i-1)]))||((inClose[i]>inOpen[(i-1)])&&(inOpen[i]<=inClose[(i-1)]))))||((((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==(0-1))&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==1))&&(((inOpen[i]>=inClose[(i-1)])&&(inClose[i]<inOpen[(i-1)]))||((inOpen[i]>inClose[(i-1)])&&(inClose[i]<=inOpen[(i-1)]))))) ) {
            if( ((inOpen[i]!=inClose[(i-1)])&&(inClose[i]!=inOpen[(i-1)])) ) {
               outInteger[outIdx++] = ((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))*100);
            } else {
               outInteger[outIdx++] = ((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))*80);
            }
         } else {
            outInteger[outIdx++] = 0;
         }
         i += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
