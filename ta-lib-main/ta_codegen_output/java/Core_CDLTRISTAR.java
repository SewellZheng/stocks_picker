/* Generated */
   public int cdltristarLookback( )
   {
      int BodyDoji_rangeType = this.candleSettings.bodyDoji.rangeType;
      int BodyDoji_avgPeriod = this.candleSettings.bodyDoji.avgPeriod;
      double BodyDoji_factor = this.candleSettings.bodyDoji.factor;
      return (BodyDoji_avgPeriod+2) ;

   }
   public RetCode cdltristar( int startIdx,
                              int endIdx,
                              double inOpen[],
                              double inHigh[],
                              double inLow[],
                              double inClose[],
                              MInteger outBegIdx,
                              MInteger outNBElement,
                              int outInteger[] )
   {
      double BodyPeriodTotal = 0;
      int i = 0;
      int outIdx = 0;
      int BodyTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyDoji_rangeType = this.candleSettings.bodyDoji.rangeType;
      int BodyDoji_avgPeriod = this.candleSettings.bodyDoji.avgPeriod;
      double BodyDoji_factor = this.candleSettings.bodyDoji.factor;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackTotal = cdltristarLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      BodyPeriodTotal = 0;
      BodyTrailingIdx = ((startIdx-2)-BodyDoji_avgPeriod);
      i = BodyTrailingIdx;
      while( (i<(startIdx-2)) ) {
         BodyPeriodTotal += ((BodyDoji_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyDoji_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyDoji_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( (((Math.abs((inClose[(i-2)]-inOpen[(i-2)]))<=((BodyDoji_factor * (((BodyDoji_avgPeriod != 0) ? (BodyPeriodTotal / BodyDoji_avgPeriod) : ((BodyDoji_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyDoji_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyDoji_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((BodyDoji_rangeType == 2) ? 2.0 : 1.0)))))&&(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))<=((BodyDoji_factor * (((BodyDoji_avgPeriod != 0) ? (BodyPeriodTotal / BodyDoji_avgPeriod) : ((BodyDoji_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyDoji_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyDoji_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((BodyDoji_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.abs((inClose[i]-inOpen[i]))<=((BodyDoji_factor * (((BodyDoji_avgPeriod != 0) ? (BodyPeriodTotal / BodyDoji_avgPeriod) : ((BodyDoji_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyDoji_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyDoji_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((BodyDoji_rangeType == 2) ? 2.0 : 1.0)))))) ) {
            outInteger[outIdx] = 0;
            if( ((Math.min(inOpen[(i-1)], inClose[(i-1)])>Math.max(inOpen[(i-2)], inClose[(i-2)]))&&(Math.max(inOpen[i], inClose[i])<Math.max(inOpen[(i-1)], inClose[(i-1)]))) ) {
               outInteger[outIdx] = (0-100);
            }
            if( ((Math.max(inOpen[(i-1)], inClose[(i-1)])<Math.min(inOpen[(i-2)], inClose[(i-2)]))&&(Math.min(inOpen[i], inClose[i])>Math.min(inOpen[(i-1)], inClose[(i-1)]))) ) {
               outInteger[outIdx] = 100;
            }
            outIdx += 1;
         } else {
            outInteger[outIdx++] = 0;
         }
         BodyPeriodTotal += (((BodyDoji_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyDoji_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyDoji_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))-((BodyDoji_rangeType == 0) ? (Math.abs(inClose[BodyTrailingIdx] - inOpen[BodyTrailingIdx])) : ((BodyDoji_rangeType == 1) ? (inHigh[BodyTrailingIdx] - inLow[BodyTrailingIdx]) : ((BodyDoji_rangeType == 2) ? ((inHigh[BodyTrailingIdx] - inLow[BodyTrailingIdx]) - Math.abs(inClose[BodyTrailingIdx] - inOpen[BodyTrailingIdx])) : 0.0))));
         i += 1;
         BodyTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdltristarLogic( int startIdx,
                                   int endIdx,
                                   double inOpen[],
                                   double inHigh[],
                                   double inLow[],
                                   double inClose[],
                                   MInteger outBegIdx,
                                   MInteger outNBElement,
                                   int outInteger[] )
   {
      double BodyPeriodTotal = 0;
      int i = 0;
      int outIdx = 0;
      int BodyTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyDoji_rangeType = this.candleSettings.bodyDoji.rangeType;
      int BodyDoji_avgPeriod = this.candleSettings.bodyDoji.avgPeriod;
      double BodyDoji_factor = this.candleSettings.bodyDoji.factor;
      lookbackTotal = cdltristarLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      BodyPeriodTotal = 0;
      BodyTrailingIdx = ((startIdx-2)-BodyDoji_avgPeriod);
      i = BodyTrailingIdx;
      while( (i<(startIdx-2)) ) {
         BodyPeriodTotal += ((BodyDoji_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyDoji_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyDoji_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( (((Math.abs((inClose[(i-2)]-inOpen[(i-2)]))<=((BodyDoji_factor * (((BodyDoji_avgPeriod != 0) ? (BodyPeriodTotal / BodyDoji_avgPeriod) : ((BodyDoji_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyDoji_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyDoji_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((BodyDoji_rangeType == 2) ? 2.0 : 1.0)))))&&(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))<=((BodyDoji_factor * (((BodyDoji_avgPeriod != 0) ? (BodyPeriodTotal / BodyDoji_avgPeriod) : ((BodyDoji_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyDoji_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyDoji_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((BodyDoji_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.abs((inClose[i]-inOpen[i]))<=((BodyDoji_factor * (((BodyDoji_avgPeriod != 0) ? (BodyPeriodTotal / BodyDoji_avgPeriod) : ((BodyDoji_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyDoji_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyDoji_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((BodyDoji_rangeType == 2) ? 2.0 : 1.0)))))) ) {
            outInteger[outIdx] = 0;
            if( ((Math.min(inOpen[(i-1)], inClose[(i-1)])>Math.max(inOpen[(i-2)], inClose[(i-2)]))&&(Math.max(inOpen[i], inClose[i])<Math.max(inOpen[(i-1)], inClose[(i-1)]))) ) {
               outInteger[outIdx] = (0-100);
            }
            if( ((Math.max(inOpen[(i-1)], inClose[(i-1)])<Math.min(inOpen[(i-2)], inClose[(i-2)]))&&(Math.min(inOpen[i], inClose[i])>Math.min(inOpen[(i-1)], inClose[(i-1)]))) ) {
               outInteger[outIdx] = 100;
            }
            outIdx += 1;
         } else {
            outInteger[outIdx++] = 0;
         }
         BodyPeriodTotal += (((BodyDoji_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyDoji_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyDoji_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))-((BodyDoji_rangeType == 0) ? (Math.abs(inClose[BodyTrailingIdx] - inOpen[BodyTrailingIdx])) : ((BodyDoji_rangeType == 1) ? (inHigh[BodyTrailingIdx] - inLow[BodyTrailingIdx]) : ((BodyDoji_rangeType == 2) ? ((inHigh[BodyTrailingIdx] - inLow[BodyTrailingIdx]) - Math.abs(inClose[BodyTrailingIdx] - inOpen[BodyTrailingIdx])) : 0.0))));
         i += 1;
         BodyTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdltristar( int startIdx,
                              int endIdx,
                              float inOpen[],
                              float inHigh[],
                              float inLow[],
                              float inClose[],
                              MInteger outBegIdx,
                              MInteger outNBElement,
                              int outInteger[] )
   {
      double BodyPeriodTotal = 0;
      int i = 0;
      int outIdx = 0;
      int BodyTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyDoji_rangeType = this.candleSettings.bodyDoji.rangeType;
      int BodyDoji_avgPeriod = this.candleSettings.bodyDoji.avgPeriod;
      double BodyDoji_factor = this.candleSettings.bodyDoji.factor;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackTotal = cdltristarLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      BodyPeriodTotal = 0;
      BodyTrailingIdx = ((startIdx-2)-BodyDoji_avgPeriod);
      i = BodyTrailingIdx;
      while( (i<(startIdx-2)) ) {
         BodyPeriodTotal += ((BodyDoji_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyDoji_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyDoji_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( (((Math.abs((inClose[(i-2)]-inOpen[(i-2)]))<=((BodyDoji_factor * (((BodyDoji_avgPeriod != 0) ? (BodyPeriodTotal / BodyDoji_avgPeriod) : ((BodyDoji_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyDoji_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyDoji_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((BodyDoji_rangeType == 2) ? 2.0 : 1.0)))))&&(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))<=((BodyDoji_factor * (((BodyDoji_avgPeriod != 0) ? (BodyPeriodTotal / BodyDoji_avgPeriod) : ((BodyDoji_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyDoji_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyDoji_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((BodyDoji_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.abs((inClose[i]-inOpen[i]))<=((BodyDoji_factor * (((BodyDoji_avgPeriod != 0) ? (BodyPeriodTotal / BodyDoji_avgPeriod) : ((BodyDoji_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyDoji_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyDoji_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((BodyDoji_rangeType == 2) ? 2.0 : 1.0)))))) ) {
            outInteger[outIdx] = 0;
            if( ((Math.min(inOpen[(i-1)], inClose[(i-1)])>Math.max(inOpen[(i-2)], inClose[(i-2)]))&&(Math.max(inOpen[i], inClose[i])<Math.max(inOpen[(i-1)], inClose[(i-1)]))) ) {
               outInteger[outIdx] = (0-100);
            }
            if( ((Math.max(inOpen[(i-1)], inClose[(i-1)])<Math.min(inOpen[(i-2)], inClose[(i-2)]))&&(Math.min(inOpen[i], inClose[i])>Math.min(inOpen[(i-1)], inClose[(i-1)]))) ) {
               outInteger[outIdx] = 100;
            }
            outIdx += 1;
         } else {
            outInteger[outIdx++] = 0;
         }
         BodyPeriodTotal += (((BodyDoji_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyDoji_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyDoji_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))-((BodyDoji_rangeType == 0) ? (Math.abs(inClose[BodyTrailingIdx] - inOpen[BodyTrailingIdx])) : ((BodyDoji_rangeType == 1) ? (inHigh[BodyTrailingIdx] - inLow[BodyTrailingIdx]) : ((BodyDoji_rangeType == 2) ? ((inHigh[BodyTrailingIdx] - inLow[BodyTrailingIdx]) - Math.abs(inClose[BodyTrailingIdx] - inOpen[BodyTrailingIdx])) : 0.0))));
         i += 1;
         BodyTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdltristarLogic( int startIdx,
                                   int endIdx,
                                   float inOpen[],
                                   float inHigh[],
                                   float inLow[],
                                   float inClose[],
                                   MInteger outBegIdx,
                                   MInteger outNBElement,
                                   int outInteger[] )
   {
      double BodyPeriodTotal = 0;
      int i = 0;
      int outIdx = 0;
      int BodyTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyDoji_rangeType = this.candleSettings.bodyDoji.rangeType;
      int BodyDoji_avgPeriod = this.candleSettings.bodyDoji.avgPeriod;
      double BodyDoji_factor = this.candleSettings.bodyDoji.factor;
      lookbackTotal = cdltristarLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      BodyPeriodTotal = 0;
      BodyTrailingIdx = ((startIdx-2)-BodyDoji_avgPeriod);
      i = BodyTrailingIdx;
      while( (i<(startIdx-2)) ) {
         BodyPeriodTotal += ((BodyDoji_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyDoji_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyDoji_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( (((Math.abs((inClose[(i-2)]-inOpen[(i-2)]))<=((BodyDoji_factor * (((BodyDoji_avgPeriod != 0) ? (BodyPeriodTotal / BodyDoji_avgPeriod) : ((BodyDoji_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyDoji_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyDoji_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((BodyDoji_rangeType == 2) ? 2.0 : 1.0)))))&&(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))<=((BodyDoji_factor * (((BodyDoji_avgPeriod != 0) ? (BodyPeriodTotal / BodyDoji_avgPeriod) : ((BodyDoji_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyDoji_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyDoji_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((BodyDoji_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.abs((inClose[i]-inOpen[i]))<=((BodyDoji_factor * (((BodyDoji_avgPeriod != 0) ? (BodyPeriodTotal / BodyDoji_avgPeriod) : ((BodyDoji_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyDoji_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyDoji_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((BodyDoji_rangeType == 2) ? 2.0 : 1.0)))))) ) {
            outInteger[outIdx] = 0;
            if( ((Math.min(inOpen[(i-1)], inClose[(i-1)])>Math.max(inOpen[(i-2)], inClose[(i-2)]))&&(Math.max(inOpen[i], inClose[i])<Math.max(inOpen[(i-1)], inClose[(i-1)]))) ) {
               outInteger[outIdx] = (0-100);
            }
            if( ((Math.max(inOpen[(i-1)], inClose[(i-1)])<Math.min(inOpen[(i-2)], inClose[(i-2)]))&&(Math.min(inOpen[i], inClose[i])>Math.min(inOpen[(i-1)], inClose[(i-1)]))) ) {
               outInteger[outIdx] = 100;
            }
            outIdx += 1;
         } else {
            outInteger[outIdx++] = 0;
         }
         BodyPeriodTotal += (((BodyDoji_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyDoji_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyDoji_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))-((BodyDoji_rangeType == 0) ? (Math.abs(inClose[BodyTrailingIdx] - inOpen[BodyTrailingIdx])) : ((BodyDoji_rangeType == 1) ? (inHigh[BodyTrailingIdx] - inLow[BodyTrailingIdx]) : ((BodyDoji_rangeType == 2) ? ((inHigh[BodyTrailingIdx] - inLow[BodyTrailingIdx]) - Math.abs(inClose[BodyTrailingIdx] - inOpen[BodyTrailingIdx])) : 0.0))));
         i += 1;
         BodyTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
