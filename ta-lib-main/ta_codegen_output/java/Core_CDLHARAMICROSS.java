/* Generated */
   public int cdlharamicrossLookback( )
   {
      int BodyDoji_rangeType = this.candleSettings.bodyDoji.rangeType;
      int BodyDoji_avgPeriod = this.candleSettings.bodyDoji.avgPeriod;
      double BodyDoji_factor = this.candleSettings.bodyDoji.factor;
      int BodyLong_rangeType = this.candleSettings.bodyLong.rangeType;
      int BodyLong_avgPeriod = this.candleSettings.bodyLong.avgPeriod;
      double BodyLong_factor = this.candleSettings.bodyLong.factor;
      return (Math.max(BodyDoji_avgPeriod, BodyLong_avgPeriod)+1) ;

   }
   public RetCode cdlharamicross( int startIdx,
                                  int endIdx,
                                  double inOpen[],
                                  double inHigh[],
                                  double inLow[],
                                  double inClose[],
                                  MInteger outBegIdx,
                                  MInteger outNBElement,
                                  int outInteger[] )
   {
      double BodyDojiPeriodTotal = 0;
      double BodyLongPeriodTotal = 0;
      int i = 0;
      int outIdx = 0;
      int BodyDojiTrailingIdx = 0;
      int BodyLongTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyDoji_rangeType = this.candleSettings.bodyDoji.rangeType;
      int BodyDoji_avgPeriod = this.candleSettings.bodyDoji.avgPeriod;
      double BodyDoji_factor = this.candleSettings.bodyDoji.factor;
      int BodyLong_rangeType = this.candleSettings.bodyLong.rangeType;
      int BodyLong_avgPeriod = this.candleSettings.bodyLong.avgPeriod;
      double BodyLong_factor = this.candleSettings.bodyLong.factor;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackTotal = cdlharamicrossLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      BodyLongPeriodTotal = 0;
      BodyDojiPeriodTotal = 0;
      BodyLongTrailingIdx = ((startIdx-1)-BodyLong_avgPeriod);
      BodyDojiTrailingIdx = (startIdx-BodyDoji_avgPeriod);
      i = BodyLongTrailingIdx;
      while( (i<(startIdx-1)) ) {
         BodyLongPeriodTotal += ((BodyLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = BodyDojiTrailingIdx;
      while( (i<startIdx) ) {
         BodyDojiPeriodTotal += ((BodyDoji_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyDoji_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyDoji_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( (Math.abs((inClose[(i-1)]-inOpen[(i-1)]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyLongPeriodTotal / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))) ) {
            if( (Math.abs((inClose[i]-inOpen[i]))<=((BodyDoji_factor * (((BodyDoji_avgPeriod != 0) ? (BodyDojiPeriodTotal / BodyDoji_avgPeriod) : ((BodyDoji_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyDoji_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyDoji_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((BodyDoji_rangeType == 2) ? 2.0 : 1.0))))) ) {
               if( ((Math.max(inClose[i], inOpen[i])<Math.max(inClose[(i-1)], inOpen[(i-1)]))&&(Math.min(inClose[i], inOpen[i])>Math.min(inClose[(i-1)], inOpen[(i-1)]))) ) {
                  outInteger[outIdx++] = ((0-(((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1))))*100);
               } else if( ((Math.max(inClose[i], inOpen[i])<=Math.max(inClose[(i-1)], inOpen[(i-1)]))&&(Math.min(inClose[i], inOpen[i])>=Math.min(inClose[(i-1)], inOpen[(i-1)]))) ) {
                  outInteger[outIdx++] = ((0-(((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1))))*80);
               } else {
                  outInteger[outIdx++] = 0;
               }
            } else {
               outInteger[outIdx++] = 0;
            }
         } else {
            outInteger[outIdx++] = 0;
         }
         BodyLongPeriodTotal += (((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))-((BodyLong_rangeType == 0) ? (Math.abs(inClose[BodyLongTrailingIdx] - inOpen[BodyLongTrailingIdx])) : ((BodyLong_rangeType == 1) ? (inHigh[BodyLongTrailingIdx] - inLow[BodyLongTrailingIdx]) : ((BodyLong_rangeType == 2) ? ((inHigh[BodyLongTrailingIdx] - inLow[BodyLongTrailingIdx]) - Math.abs(inClose[BodyLongTrailingIdx] - inOpen[BodyLongTrailingIdx])) : 0.0))));
         BodyDojiPeriodTotal += (((BodyDoji_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyDoji_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyDoji_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((BodyDoji_rangeType == 0) ? (Math.abs(inClose[BodyDojiTrailingIdx] - inOpen[BodyDojiTrailingIdx])) : ((BodyDoji_rangeType == 1) ? (inHigh[BodyDojiTrailingIdx] - inLow[BodyDojiTrailingIdx]) : ((BodyDoji_rangeType == 2) ? ((inHigh[BodyDojiTrailingIdx] - inLow[BodyDojiTrailingIdx]) - Math.abs(inClose[BodyDojiTrailingIdx] - inOpen[BodyDojiTrailingIdx])) : 0.0))));
         i += 1;
         BodyLongTrailingIdx += 1;
         BodyDojiTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdlharamicrossLogic( int startIdx,
                                       int endIdx,
                                       double inOpen[],
                                       double inHigh[],
                                       double inLow[],
                                       double inClose[],
                                       MInteger outBegIdx,
                                       MInteger outNBElement,
                                       int outInteger[] )
   {
      double BodyDojiPeriodTotal = 0;
      double BodyLongPeriodTotal = 0;
      int i = 0;
      int outIdx = 0;
      int BodyDojiTrailingIdx = 0;
      int BodyLongTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyDoji_rangeType = this.candleSettings.bodyDoji.rangeType;
      int BodyDoji_avgPeriod = this.candleSettings.bodyDoji.avgPeriod;
      double BodyDoji_factor = this.candleSettings.bodyDoji.factor;
      int BodyLong_rangeType = this.candleSettings.bodyLong.rangeType;
      int BodyLong_avgPeriod = this.candleSettings.bodyLong.avgPeriod;
      double BodyLong_factor = this.candleSettings.bodyLong.factor;
      lookbackTotal = cdlharamicrossLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      BodyLongPeriodTotal = 0;
      BodyDojiPeriodTotal = 0;
      BodyLongTrailingIdx = ((startIdx-1)-BodyLong_avgPeriod);
      BodyDojiTrailingIdx = (startIdx-BodyDoji_avgPeriod);
      i = BodyLongTrailingIdx;
      while( (i<(startIdx-1)) ) {
         BodyLongPeriodTotal += ((BodyLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = BodyDojiTrailingIdx;
      while( (i<startIdx) ) {
         BodyDojiPeriodTotal += ((BodyDoji_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyDoji_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyDoji_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( (Math.abs((inClose[(i-1)]-inOpen[(i-1)]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyLongPeriodTotal / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))) ) {
            if( (Math.abs((inClose[i]-inOpen[i]))<=((BodyDoji_factor * (((BodyDoji_avgPeriod != 0) ? (BodyDojiPeriodTotal / BodyDoji_avgPeriod) : ((BodyDoji_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyDoji_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyDoji_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((BodyDoji_rangeType == 2) ? 2.0 : 1.0))))) ) {
               if( ((Math.max(inClose[i], inOpen[i])<Math.max(inClose[(i-1)], inOpen[(i-1)]))&&(Math.min(inClose[i], inOpen[i])>Math.min(inClose[(i-1)], inOpen[(i-1)]))) ) {
                  outInteger[outIdx++] = ((0-(((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1))))*100);
               } else if( ((Math.max(inClose[i], inOpen[i])<=Math.max(inClose[(i-1)], inOpen[(i-1)]))&&(Math.min(inClose[i], inOpen[i])>=Math.min(inClose[(i-1)], inOpen[(i-1)]))) ) {
                  outInteger[outIdx++] = ((0-(((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1))))*80);
               } else {
                  outInteger[outIdx++] = 0;
               }
            } else {
               outInteger[outIdx++] = 0;
            }
         } else {
            outInteger[outIdx++] = 0;
         }
         BodyLongPeriodTotal += (((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))-((BodyLong_rangeType == 0) ? (Math.abs(inClose[BodyLongTrailingIdx] - inOpen[BodyLongTrailingIdx])) : ((BodyLong_rangeType == 1) ? (inHigh[BodyLongTrailingIdx] - inLow[BodyLongTrailingIdx]) : ((BodyLong_rangeType == 2) ? ((inHigh[BodyLongTrailingIdx] - inLow[BodyLongTrailingIdx]) - Math.abs(inClose[BodyLongTrailingIdx] - inOpen[BodyLongTrailingIdx])) : 0.0))));
         BodyDojiPeriodTotal += (((BodyDoji_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyDoji_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyDoji_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((BodyDoji_rangeType == 0) ? (Math.abs(inClose[BodyDojiTrailingIdx] - inOpen[BodyDojiTrailingIdx])) : ((BodyDoji_rangeType == 1) ? (inHigh[BodyDojiTrailingIdx] - inLow[BodyDojiTrailingIdx]) : ((BodyDoji_rangeType == 2) ? ((inHigh[BodyDojiTrailingIdx] - inLow[BodyDojiTrailingIdx]) - Math.abs(inClose[BodyDojiTrailingIdx] - inOpen[BodyDojiTrailingIdx])) : 0.0))));
         i += 1;
         BodyLongTrailingIdx += 1;
         BodyDojiTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdlharamicross( int startIdx,
                                  int endIdx,
                                  float inOpen[],
                                  float inHigh[],
                                  float inLow[],
                                  float inClose[],
                                  MInteger outBegIdx,
                                  MInteger outNBElement,
                                  int outInteger[] )
   {
      double BodyDojiPeriodTotal = 0;
      double BodyLongPeriodTotal = 0;
      int i = 0;
      int outIdx = 0;
      int BodyDojiTrailingIdx = 0;
      int BodyLongTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyDoji_rangeType = this.candleSettings.bodyDoji.rangeType;
      int BodyDoji_avgPeriod = this.candleSettings.bodyDoji.avgPeriod;
      double BodyDoji_factor = this.candleSettings.bodyDoji.factor;
      int BodyLong_rangeType = this.candleSettings.bodyLong.rangeType;
      int BodyLong_avgPeriod = this.candleSettings.bodyLong.avgPeriod;
      double BodyLong_factor = this.candleSettings.bodyLong.factor;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackTotal = cdlharamicrossLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      BodyLongPeriodTotal = 0;
      BodyDojiPeriodTotal = 0;
      BodyLongTrailingIdx = ((startIdx-1)-BodyLong_avgPeriod);
      BodyDojiTrailingIdx = (startIdx-BodyDoji_avgPeriod);
      i = BodyLongTrailingIdx;
      while( (i<(startIdx-1)) ) {
         BodyLongPeriodTotal += ((BodyLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = BodyDojiTrailingIdx;
      while( (i<startIdx) ) {
         BodyDojiPeriodTotal += ((BodyDoji_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyDoji_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyDoji_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( (Math.abs((inClose[(i-1)]-inOpen[(i-1)]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyLongPeriodTotal / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))) ) {
            if( (Math.abs((inClose[i]-inOpen[i]))<=((BodyDoji_factor * (((BodyDoji_avgPeriod != 0) ? (BodyDojiPeriodTotal / BodyDoji_avgPeriod) : ((BodyDoji_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyDoji_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyDoji_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((BodyDoji_rangeType == 2) ? 2.0 : 1.0))))) ) {
               if( ((Math.max(inClose[i], inOpen[i])<Math.max(inClose[(i-1)], inOpen[(i-1)]))&&(Math.min(inClose[i], inOpen[i])>Math.min(inClose[(i-1)], inOpen[(i-1)]))) ) {
                  outInteger[outIdx++] = ((0-(((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1))))*100);
               } else if( ((Math.max(inClose[i], inOpen[i])<=Math.max(inClose[(i-1)], inOpen[(i-1)]))&&(Math.min(inClose[i], inOpen[i])>=Math.min(inClose[(i-1)], inOpen[(i-1)]))) ) {
                  outInteger[outIdx++] = ((0-(((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1))))*80);
               } else {
                  outInteger[outIdx++] = 0;
               }
            } else {
               outInteger[outIdx++] = 0;
            }
         } else {
            outInteger[outIdx++] = 0;
         }
         BodyLongPeriodTotal += (((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))-((BodyLong_rangeType == 0) ? (Math.abs(inClose[BodyLongTrailingIdx] - inOpen[BodyLongTrailingIdx])) : ((BodyLong_rangeType == 1) ? (inHigh[BodyLongTrailingIdx] - inLow[BodyLongTrailingIdx]) : ((BodyLong_rangeType == 2) ? ((inHigh[BodyLongTrailingIdx] - inLow[BodyLongTrailingIdx]) - Math.abs(inClose[BodyLongTrailingIdx] - inOpen[BodyLongTrailingIdx])) : 0.0))));
         BodyDojiPeriodTotal += (((BodyDoji_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyDoji_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyDoji_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((BodyDoji_rangeType == 0) ? (Math.abs(inClose[BodyDojiTrailingIdx] - inOpen[BodyDojiTrailingIdx])) : ((BodyDoji_rangeType == 1) ? (inHigh[BodyDojiTrailingIdx] - inLow[BodyDojiTrailingIdx]) : ((BodyDoji_rangeType == 2) ? ((inHigh[BodyDojiTrailingIdx] - inLow[BodyDojiTrailingIdx]) - Math.abs(inClose[BodyDojiTrailingIdx] - inOpen[BodyDojiTrailingIdx])) : 0.0))));
         i += 1;
         BodyLongTrailingIdx += 1;
         BodyDojiTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdlharamicrossLogic( int startIdx,
                                       int endIdx,
                                       float inOpen[],
                                       float inHigh[],
                                       float inLow[],
                                       float inClose[],
                                       MInteger outBegIdx,
                                       MInteger outNBElement,
                                       int outInteger[] )
   {
      double BodyDojiPeriodTotal = 0;
      double BodyLongPeriodTotal = 0;
      int i = 0;
      int outIdx = 0;
      int BodyDojiTrailingIdx = 0;
      int BodyLongTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyDoji_rangeType = this.candleSettings.bodyDoji.rangeType;
      int BodyDoji_avgPeriod = this.candleSettings.bodyDoji.avgPeriod;
      double BodyDoji_factor = this.candleSettings.bodyDoji.factor;
      int BodyLong_rangeType = this.candleSettings.bodyLong.rangeType;
      int BodyLong_avgPeriod = this.candleSettings.bodyLong.avgPeriod;
      double BodyLong_factor = this.candleSettings.bodyLong.factor;
      lookbackTotal = cdlharamicrossLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      BodyLongPeriodTotal = 0;
      BodyDojiPeriodTotal = 0;
      BodyLongTrailingIdx = ((startIdx-1)-BodyLong_avgPeriod);
      BodyDojiTrailingIdx = (startIdx-BodyDoji_avgPeriod);
      i = BodyLongTrailingIdx;
      while( (i<(startIdx-1)) ) {
         BodyLongPeriodTotal += ((BodyLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = BodyDojiTrailingIdx;
      while( (i<startIdx) ) {
         BodyDojiPeriodTotal += ((BodyDoji_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyDoji_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyDoji_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( (Math.abs((inClose[(i-1)]-inOpen[(i-1)]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyLongPeriodTotal / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))) ) {
            if( (Math.abs((inClose[i]-inOpen[i]))<=((BodyDoji_factor * (((BodyDoji_avgPeriod != 0) ? (BodyDojiPeriodTotal / BodyDoji_avgPeriod) : ((BodyDoji_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyDoji_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyDoji_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((BodyDoji_rangeType == 2) ? 2.0 : 1.0))))) ) {
               if( ((Math.max(inClose[i], inOpen[i])<Math.max(inClose[(i-1)], inOpen[(i-1)]))&&(Math.min(inClose[i], inOpen[i])>Math.min(inClose[(i-1)], inOpen[(i-1)]))) ) {
                  outInteger[outIdx++] = ((0-(((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1))))*100);
               } else if( ((Math.max(inClose[i], inOpen[i])<=Math.max(inClose[(i-1)], inOpen[(i-1)]))&&(Math.min(inClose[i], inOpen[i])>=Math.min(inClose[(i-1)], inOpen[(i-1)]))) ) {
                  outInteger[outIdx++] = ((0-(((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1))))*80);
               } else {
                  outInteger[outIdx++] = 0;
               }
            } else {
               outInteger[outIdx++] = 0;
            }
         } else {
            outInteger[outIdx++] = 0;
         }
         BodyLongPeriodTotal += (((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))-((BodyLong_rangeType == 0) ? (Math.abs(inClose[BodyLongTrailingIdx] - inOpen[BodyLongTrailingIdx])) : ((BodyLong_rangeType == 1) ? (inHigh[BodyLongTrailingIdx] - inLow[BodyLongTrailingIdx]) : ((BodyLong_rangeType == 2) ? ((inHigh[BodyLongTrailingIdx] - inLow[BodyLongTrailingIdx]) - Math.abs(inClose[BodyLongTrailingIdx] - inOpen[BodyLongTrailingIdx])) : 0.0))));
         BodyDojiPeriodTotal += (((BodyDoji_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyDoji_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyDoji_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((BodyDoji_rangeType == 0) ? (Math.abs(inClose[BodyDojiTrailingIdx] - inOpen[BodyDojiTrailingIdx])) : ((BodyDoji_rangeType == 1) ? (inHigh[BodyDojiTrailingIdx] - inLow[BodyDojiTrailingIdx]) : ((BodyDoji_rangeType == 2) ? ((inHigh[BodyDojiTrailingIdx] - inLow[BodyDojiTrailingIdx]) - Math.abs(inClose[BodyDojiTrailingIdx] - inOpen[BodyDojiTrailingIdx])) : 0.0))));
         i += 1;
         BodyLongTrailingIdx += 1;
         BodyDojiTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
