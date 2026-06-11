/* Generated */
   public int cdlhikkakeLookback( )
   {
      return 5 ;

   }
   public RetCode cdlhikkake( int startIdx,
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
      int patternIdx = 0;
      int patternResult = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackTotal = cdlhikkakeLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      patternIdx = 0;
      patternResult = 0;
      i = (startIdx-3);
      while( (i<startIdx) ) {
         if( (((inHigh[(i-1)]<inHigh[(i-2)])&&(inLow[(i-1)]>inLow[(i-2)]))&&(((inHigh[i]<inHigh[(i-1)])&&(inLow[i]<inLow[(i-1)]))||((inHigh[i]>inHigh[(i-1)])&&(inLow[i]>inLow[(i-1)])))) ) {
            patternResult = (100*(((inHigh[i]<inHigh[(i-1)])) ? (1) : ((0-1))));
            patternIdx = i;
         } else if( ((i<=(patternIdx+3))&&(((patternResult>0)&&(inClose[i]>inHigh[(patternIdx-1)]))||((patternResult<0)&&(inClose[i]<inLow[(patternIdx-1)])))) ) {
            patternIdx = 0;
         }
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( (((inHigh[(i-1)]<inHigh[(i-2)])&&(inLow[(i-1)]>inLow[(i-2)]))&&(((inHigh[i]<inHigh[(i-1)])&&(inLow[i]<inLow[(i-1)]))||((inHigh[i]>inHigh[(i-1)])&&(inLow[i]>inLow[(i-1)])))) ) {
            patternResult = (100*(((inHigh[i]<inHigh[(i-1)])) ? (1) : ((0-1))));
            patternIdx = i;
            outInteger[outIdx++] = patternResult;
         } else if( ((i<=(patternIdx+3))&&(((patternResult>0)&&(inClose[i]>inHigh[(patternIdx-1)]))||((patternResult<0)&&(inClose[i]<inLow[(patternIdx-1)])))) ) {
            outInteger[outIdx++] = (patternResult+(100*(((patternResult>0)) ? (1) : ((0-1)))));
            patternIdx = 0;
         } else {
            outInteger[outIdx++] = 0;
         }
         i += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdlhikkakeLogic( int startIdx,
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
      int patternIdx = 0;
      int patternResult = 0;
      lookbackTotal = cdlhikkakeLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      patternIdx = 0;
      patternResult = 0;
      i = (startIdx-3);
      while( (i<startIdx) ) {
         if( (((inHigh[(i-1)]<inHigh[(i-2)])&&(inLow[(i-1)]>inLow[(i-2)]))&&(((inHigh[i]<inHigh[(i-1)])&&(inLow[i]<inLow[(i-1)]))||((inHigh[i]>inHigh[(i-1)])&&(inLow[i]>inLow[(i-1)])))) ) {
            patternResult = (100*(((inHigh[i]<inHigh[(i-1)])) ? (1) : ((0-1))));
            patternIdx = i;
         } else if( ((i<=(patternIdx+3))&&(((patternResult>0)&&(inClose[i]>inHigh[(patternIdx-1)]))||((patternResult<0)&&(inClose[i]<inLow[(patternIdx-1)])))) ) {
            patternIdx = 0;
         }
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( (((inHigh[(i-1)]<inHigh[(i-2)])&&(inLow[(i-1)]>inLow[(i-2)]))&&(((inHigh[i]<inHigh[(i-1)])&&(inLow[i]<inLow[(i-1)]))||((inHigh[i]>inHigh[(i-1)])&&(inLow[i]>inLow[(i-1)])))) ) {
            patternResult = (100*(((inHigh[i]<inHigh[(i-1)])) ? (1) : ((0-1))));
            patternIdx = i;
            outInteger[outIdx++] = patternResult;
         } else if( ((i<=(patternIdx+3))&&(((patternResult>0)&&(inClose[i]>inHigh[(patternIdx-1)]))||((patternResult<0)&&(inClose[i]<inLow[(patternIdx-1)])))) ) {
            outInteger[outIdx++] = (patternResult+(100*(((patternResult>0)) ? (1) : ((0-1)))));
            patternIdx = 0;
         } else {
            outInteger[outIdx++] = 0;
         }
         i += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdlhikkake( int startIdx,
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
      int patternIdx = 0;
      int patternResult = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackTotal = cdlhikkakeLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      patternIdx = 0;
      patternResult = 0;
      i = (startIdx-3);
      while( (i<startIdx) ) {
         if( (((inHigh[(i-1)]<inHigh[(i-2)])&&(inLow[(i-1)]>inLow[(i-2)]))&&(((inHigh[i]<inHigh[(i-1)])&&(inLow[i]<inLow[(i-1)]))||((inHigh[i]>inHigh[(i-1)])&&(inLow[i]>inLow[(i-1)])))) ) {
            patternResult = (100*(((inHigh[i]<inHigh[(i-1)])) ? (1) : ((0-1))));
            patternIdx = i;
         } else if( ((i<=(patternIdx+3))&&(((patternResult>0)&&(inClose[i]>inHigh[(patternIdx-1)]))||((patternResult<0)&&(inClose[i]<inLow[(patternIdx-1)])))) ) {
            patternIdx = 0;
         }
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( (((inHigh[(i-1)]<inHigh[(i-2)])&&(inLow[(i-1)]>inLow[(i-2)]))&&(((inHigh[i]<inHigh[(i-1)])&&(inLow[i]<inLow[(i-1)]))||((inHigh[i]>inHigh[(i-1)])&&(inLow[i]>inLow[(i-1)])))) ) {
            patternResult = (100*(((inHigh[i]<inHigh[(i-1)])) ? (1) : ((0-1))));
            patternIdx = i;
            outInteger[outIdx++] = patternResult;
         } else if( ((i<=(patternIdx+3))&&(((patternResult>0)&&(inClose[i]>inHigh[(patternIdx-1)]))||((patternResult<0)&&(inClose[i]<inLow[(patternIdx-1)])))) ) {
            outInteger[outIdx++] = (patternResult+(100*(((patternResult>0)) ? (1) : ((0-1)))));
            patternIdx = 0;
         } else {
            outInteger[outIdx++] = 0;
         }
         i += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdlhikkakeLogic( int startIdx,
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
      int patternIdx = 0;
      int patternResult = 0;
      lookbackTotal = cdlhikkakeLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      patternIdx = 0;
      patternResult = 0;
      i = (startIdx-3);
      while( (i<startIdx) ) {
         if( (((inHigh[(i-1)]<inHigh[(i-2)])&&(inLow[(i-1)]>inLow[(i-2)]))&&(((inHigh[i]<inHigh[(i-1)])&&(inLow[i]<inLow[(i-1)]))||((inHigh[i]>inHigh[(i-1)])&&(inLow[i]>inLow[(i-1)])))) ) {
            patternResult = (100*(((inHigh[i]<inHigh[(i-1)])) ? (1) : ((0-1))));
            patternIdx = i;
         } else if( ((i<=(patternIdx+3))&&(((patternResult>0)&&(inClose[i]>inHigh[(patternIdx-1)]))||((patternResult<0)&&(inClose[i]<inLow[(patternIdx-1)])))) ) {
            patternIdx = 0;
         }
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( (((inHigh[(i-1)]<inHigh[(i-2)])&&(inLow[(i-1)]>inLow[(i-2)]))&&(((inHigh[i]<inHigh[(i-1)])&&(inLow[i]<inLow[(i-1)]))||((inHigh[i]>inHigh[(i-1)])&&(inLow[i]>inLow[(i-1)])))) ) {
            patternResult = (100*(((inHigh[i]<inHigh[(i-1)])) ? (1) : ((0-1))));
            patternIdx = i;
            outInteger[outIdx++] = patternResult;
         } else if( ((i<=(patternIdx+3))&&(((patternResult>0)&&(inClose[i]>inHigh[(patternIdx-1)]))||((patternResult<0)&&(inClose[i]<inLow[(patternIdx-1)])))) ) {
            outInteger[outIdx++] = (patternResult+(100*(((patternResult>0)) ? (1) : ((0-1)))));
            patternIdx = 0;
         } else {
            outInteger[outIdx++] = 0;
         }
         i += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
