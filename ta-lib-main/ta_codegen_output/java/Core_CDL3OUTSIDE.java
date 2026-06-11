/* Generated */
   public int cdl3outsideLookback( )
   {
      return 3 ;

   }
   public RetCode cdl3outside( int startIdx,
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
      lookbackTotal = cdl3outsideLookback();
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
         if( (((((((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==1)&&((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==(0-1)))&&(inClose[(i-1)]>inOpen[(i-2)]))&&(inOpen[(i-1)]<inClose[(i-2)]))&&(inClose[i]>inClose[(i-1)]))||((((((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-1))&&((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==1))&&(inOpen[(i-1)]>inClose[(i-2)]))&&(inClose[(i-1)]<inOpen[(i-2)]))&&(inClose[i]<inClose[(i-1)]))) ) {
            outInteger[outIdx++] = ((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))*100);
         } else {
            outInteger[outIdx++] = 0;
         }
         i += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdl3outsideLogic( int startIdx,
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
      lookbackTotal = cdl3outsideLookback();
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
         if( (((((((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==1)&&((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==(0-1)))&&(inClose[(i-1)]>inOpen[(i-2)]))&&(inOpen[(i-1)]<inClose[(i-2)]))&&(inClose[i]>inClose[(i-1)]))||((((((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-1))&&((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==1))&&(inOpen[(i-1)]>inClose[(i-2)]))&&(inClose[(i-1)]<inOpen[(i-2)]))&&(inClose[i]<inClose[(i-1)]))) ) {
            outInteger[outIdx++] = ((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))*100);
         } else {
            outInteger[outIdx++] = 0;
         }
         i += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdl3outside( int startIdx,
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
      lookbackTotal = cdl3outsideLookback();
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
         if( (((((((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==1)&&((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==(0-1)))&&(inClose[(i-1)]>inOpen[(i-2)]))&&(inOpen[(i-1)]<inClose[(i-2)]))&&(inClose[i]>inClose[(i-1)]))||((((((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-1))&&((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==1))&&(inOpen[(i-1)]>inClose[(i-2)]))&&(inClose[(i-1)]<inOpen[(i-2)]))&&(inClose[i]<inClose[(i-1)]))) ) {
            outInteger[outIdx++] = ((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))*100);
         } else {
            outInteger[outIdx++] = 0;
         }
         i += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdl3outsideLogic( int startIdx,
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
      lookbackTotal = cdl3outsideLookback();
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
         if( (((((((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==1)&&((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==(0-1)))&&(inClose[(i-1)]>inOpen[(i-2)]))&&(inOpen[(i-1)]<inClose[(i-2)]))&&(inClose[i]>inClose[(i-1)]))||((((((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-1))&&((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==1))&&(inOpen[(i-1)]>inClose[(i-2)]))&&(inClose[(i-1)]<inOpen[(i-2)]))&&(inClose[i]<inClose[(i-1)]))) ) {
            outInteger[outIdx++] = ((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))*100);
         } else {
            outInteger[outIdx++] = 0;
         }
         i += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
