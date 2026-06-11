/* Generated */
   public int cdl3blackcrowsLookback( )
   {
      int ShadowVeryShort_rangeType = this.candleSettings.shadowVeryShort.rangeType;
      int ShadowVeryShort_avgPeriod = this.candleSettings.shadowVeryShort.avgPeriod;
      double ShadowVeryShort_factor = this.candleSettings.shadowVeryShort.factor;
      return (ShadowVeryShort_avgPeriod+3) ;

   }
   public RetCode cdl3blackcrows( int startIdx,
                                  int endIdx,
                                  double inOpen[],
                                  double inHigh[],
                                  double inLow[],
                                  double inClose[],
                                  MInteger outBegIdx,
                                  MInteger outNBElement,
                                  int outInteger[] )
   {
      double[] ShadowVeryShortPeriodTotal = new double[3];
      int i = 0;
      int outIdx = 0;
      int totIdx = 0;
      int ShadowVeryShortTrailingIdx = 0;
      int lookbackTotal = 0;
      int ShadowVeryShort_rangeType = this.candleSettings.shadowVeryShort.rangeType;
      int ShadowVeryShort_avgPeriod = this.candleSettings.shadowVeryShort.avgPeriod;
      double ShadowVeryShort_factor = this.candleSettings.shadowVeryShort.factor;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackTotal = cdl3blackcrowsLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      ShadowVeryShortPeriodTotal[2] = 0;
      ShadowVeryShortPeriodTotal[1] = 0;
      ShadowVeryShortPeriodTotal[0] = 0;
      ShadowVeryShortTrailingIdx = (startIdx-ShadowVeryShort_avgPeriod);
      i = ShadowVeryShortTrailingIdx;
      while( (i<startIdx) ) {
         ShadowVeryShortPeriodTotal[2] = (ShadowVeryShortPeriodTotal[2]+((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0))));
         ShadowVeryShortPeriodTotal[1] = (ShadowVeryShortPeriodTotal[1]+((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0))));
         ShadowVeryShortPeriodTotal[0] = (ShadowVeryShortPeriodTotal[0]+((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0))));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( (((((((((((((((((inClose[(i-3)]>=inOpen[(i-3)])) ? (1) : ((0-1)))==1)&&((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==(0-1)))&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-1)))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==(0-1)))&&(inOpen[(i-1)]<inOpen[(i-2)]))&&(inOpen[(i-1)]>inClose[(i-2)]))&&(inOpen[i]<inOpen[(i-1)]))&&(inOpen[i]>inClose[(i-1)]))&&(inHigh[(i-3)]>inClose[(i-2)]))&&(inClose[(i-2)]>inClose[(i-1)]))&&(inClose[(i-1)]>inClose[i]))&&(((((inClose[(i-2)]>=inOpen[(i-2)])) ? (inOpen[(i-2)]) : (inClose[(i-2)]))-inLow[(i-2)])<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal[2] / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))&&(((((inClose[(i-1)]>=inOpen[(i-1)])) ? (inOpen[(i-1)]) : (inClose[(i-1)]))-inLow[(i-1)])<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal[1] / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))&&(((((inClose[i]>=inOpen[i])) ? (inOpen[i]) : (inClose[i]))-inLow[i])<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal[0] / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0)))))) ) {
            outInteger[outIdx++] = (0-100);
         } else {
            outInteger[outIdx++] = 0;
         }
         for( totIdx = 2; (totIdx>=0); totIdx -= 1 ) {
            ShadowVeryShortPeriodTotal[totIdx] = (ShadowVeryShortPeriodTotal[totIdx]+(((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(ShadowVeryShortTrailingIdx-totIdx)] - inOpen[(ShadowVeryShortTrailingIdx-totIdx)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(ShadowVeryShortTrailingIdx-totIdx)] - inLow[(ShadowVeryShortTrailingIdx-totIdx)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(ShadowVeryShortTrailingIdx-totIdx)] - inLow[(ShadowVeryShortTrailingIdx-totIdx)]) - Math.abs(inClose[(ShadowVeryShortTrailingIdx-totIdx)] - inOpen[(ShadowVeryShortTrailingIdx-totIdx)])) : 0.0)))));
         }
         i += 1;
         ShadowVeryShortTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdl3blackcrowsLogic( int startIdx,
                                       int endIdx,
                                       double inOpen[],
                                       double inHigh[],
                                       double inLow[],
                                       double inClose[],
                                       MInteger outBegIdx,
                                       MInteger outNBElement,
                                       int outInteger[] )
   {
      double[] ShadowVeryShortPeriodTotal = new double[3];
      int i = 0;
      int outIdx = 0;
      int totIdx = 0;
      int ShadowVeryShortTrailingIdx = 0;
      int lookbackTotal = 0;
      int ShadowVeryShort_rangeType = this.candleSettings.shadowVeryShort.rangeType;
      int ShadowVeryShort_avgPeriod = this.candleSettings.shadowVeryShort.avgPeriod;
      double ShadowVeryShort_factor = this.candleSettings.shadowVeryShort.factor;
      lookbackTotal = cdl3blackcrowsLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      ShadowVeryShortPeriodTotal[2] = 0;
      ShadowVeryShortPeriodTotal[1] = 0;
      ShadowVeryShortPeriodTotal[0] = 0;
      ShadowVeryShortTrailingIdx = (startIdx-ShadowVeryShort_avgPeriod);
      i = ShadowVeryShortTrailingIdx;
      while( (i<startIdx) ) {
         ShadowVeryShortPeriodTotal[2] = (ShadowVeryShortPeriodTotal[2]+((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0))));
         ShadowVeryShortPeriodTotal[1] = (ShadowVeryShortPeriodTotal[1]+((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0))));
         ShadowVeryShortPeriodTotal[0] = (ShadowVeryShortPeriodTotal[0]+((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0))));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( (((((((((((((((((inClose[(i-3)]>=inOpen[(i-3)])) ? (1) : ((0-1)))==1)&&((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==(0-1)))&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-1)))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==(0-1)))&&(inOpen[(i-1)]<inOpen[(i-2)]))&&(inOpen[(i-1)]>inClose[(i-2)]))&&(inOpen[i]<inOpen[(i-1)]))&&(inOpen[i]>inClose[(i-1)]))&&(inHigh[(i-3)]>inClose[(i-2)]))&&(inClose[(i-2)]>inClose[(i-1)]))&&(inClose[(i-1)]>inClose[i]))&&(((((inClose[(i-2)]>=inOpen[(i-2)])) ? (inOpen[(i-2)]) : (inClose[(i-2)]))-inLow[(i-2)])<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal[2] / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))&&(((((inClose[(i-1)]>=inOpen[(i-1)])) ? (inOpen[(i-1)]) : (inClose[(i-1)]))-inLow[(i-1)])<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal[1] / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))&&(((((inClose[i]>=inOpen[i])) ? (inOpen[i]) : (inClose[i]))-inLow[i])<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal[0] / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0)))))) ) {
            outInteger[outIdx++] = (0-100);
         } else {
            outInteger[outIdx++] = 0;
         }
         for( totIdx = 2; (totIdx>=0); totIdx -= 1 ) {
            ShadowVeryShortPeriodTotal[totIdx] = (ShadowVeryShortPeriodTotal[totIdx]+(((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(ShadowVeryShortTrailingIdx-totIdx)] - inOpen[(ShadowVeryShortTrailingIdx-totIdx)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(ShadowVeryShortTrailingIdx-totIdx)] - inLow[(ShadowVeryShortTrailingIdx-totIdx)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(ShadowVeryShortTrailingIdx-totIdx)] - inLow[(ShadowVeryShortTrailingIdx-totIdx)]) - Math.abs(inClose[(ShadowVeryShortTrailingIdx-totIdx)] - inOpen[(ShadowVeryShortTrailingIdx-totIdx)])) : 0.0)))));
         }
         i += 1;
         ShadowVeryShortTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdl3blackcrows( int startIdx,
                                  int endIdx,
                                  float inOpen[],
                                  float inHigh[],
                                  float inLow[],
                                  float inClose[],
                                  MInteger outBegIdx,
                                  MInteger outNBElement,
                                  int outInteger[] )
   {
      double[] ShadowVeryShortPeriodTotal = new double[3];
      int i = 0;
      int outIdx = 0;
      int totIdx = 0;
      int ShadowVeryShortTrailingIdx = 0;
      int lookbackTotal = 0;
      int ShadowVeryShort_rangeType = this.candleSettings.shadowVeryShort.rangeType;
      int ShadowVeryShort_avgPeriod = this.candleSettings.shadowVeryShort.avgPeriod;
      double ShadowVeryShort_factor = this.candleSettings.shadowVeryShort.factor;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackTotal = cdl3blackcrowsLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      ShadowVeryShortPeriodTotal[2] = 0;
      ShadowVeryShortPeriodTotal[1] = 0;
      ShadowVeryShortPeriodTotal[0] = 0;
      ShadowVeryShortTrailingIdx = (startIdx-ShadowVeryShort_avgPeriod);
      i = ShadowVeryShortTrailingIdx;
      while( (i<startIdx) ) {
         ShadowVeryShortPeriodTotal[2] = (ShadowVeryShortPeriodTotal[2]+((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0))));
         ShadowVeryShortPeriodTotal[1] = (ShadowVeryShortPeriodTotal[1]+((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0))));
         ShadowVeryShortPeriodTotal[0] = (ShadowVeryShortPeriodTotal[0]+((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0))));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( (((((((((((((((((inClose[(i-3)]>=inOpen[(i-3)])) ? (1) : ((0-1)))==1)&&((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==(0-1)))&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-1)))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==(0-1)))&&(inOpen[(i-1)]<inOpen[(i-2)]))&&(inOpen[(i-1)]>inClose[(i-2)]))&&(inOpen[i]<inOpen[(i-1)]))&&(inOpen[i]>inClose[(i-1)]))&&(inHigh[(i-3)]>inClose[(i-2)]))&&(inClose[(i-2)]>inClose[(i-1)]))&&(inClose[(i-1)]>inClose[i]))&&(((((inClose[(i-2)]>=inOpen[(i-2)])) ? (inOpen[(i-2)]) : (inClose[(i-2)]))-inLow[(i-2)])<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal[2] / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))&&(((((inClose[(i-1)]>=inOpen[(i-1)])) ? (inOpen[(i-1)]) : (inClose[(i-1)]))-inLow[(i-1)])<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal[1] / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))&&(((((inClose[i]>=inOpen[i])) ? (inOpen[i]) : (inClose[i]))-inLow[i])<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal[0] / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0)))))) ) {
            outInteger[outIdx++] = (0-100);
         } else {
            outInteger[outIdx++] = 0;
         }
         for( totIdx = 2; (totIdx>=0); totIdx -= 1 ) {
            ShadowVeryShortPeriodTotal[totIdx] = (ShadowVeryShortPeriodTotal[totIdx]+(((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(ShadowVeryShortTrailingIdx-totIdx)] - inOpen[(ShadowVeryShortTrailingIdx-totIdx)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(ShadowVeryShortTrailingIdx-totIdx)] - inLow[(ShadowVeryShortTrailingIdx-totIdx)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(ShadowVeryShortTrailingIdx-totIdx)] - inLow[(ShadowVeryShortTrailingIdx-totIdx)]) - Math.abs(inClose[(ShadowVeryShortTrailingIdx-totIdx)] - inOpen[(ShadowVeryShortTrailingIdx-totIdx)])) : 0.0)))));
         }
         i += 1;
         ShadowVeryShortTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdl3blackcrowsLogic( int startIdx,
                                       int endIdx,
                                       float inOpen[],
                                       float inHigh[],
                                       float inLow[],
                                       float inClose[],
                                       MInteger outBegIdx,
                                       MInteger outNBElement,
                                       int outInteger[] )
   {
      double[] ShadowVeryShortPeriodTotal = new double[3];
      int i = 0;
      int outIdx = 0;
      int totIdx = 0;
      int ShadowVeryShortTrailingIdx = 0;
      int lookbackTotal = 0;
      int ShadowVeryShort_rangeType = this.candleSettings.shadowVeryShort.rangeType;
      int ShadowVeryShort_avgPeriod = this.candleSettings.shadowVeryShort.avgPeriod;
      double ShadowVeryShort_factor = this.candleSettings.shadowVeryShort.factor;
      lookbackTotal = cdl3blackcrowsLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      ShadowVeryShortPeriodTotal[2] = 0;
      ShadowVeryShortPeriodTotal[1] = 0;
      ShadowVeryShortPeriodTotal[0] = 0;
      ShadowVeryShortTrailingIdx = (startIdx-ShadowVeryShort_avgPeriod);
      i = ShadowVeryShortTrailingIdx;
      while( (i<startIdx) ) {
         ShadowVeryShortPeriodTotal[2] = (ShadowVeryShortPeriodTotal[2]+((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0))));
         ShadowVeryShortPeriodTotal[1] = (ShadowVeryShortPeriodTotal[1]+((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0))));
         ShadowVeryShortPeriodTotal[0] = (ShadowVeryShortPeriodTotal[0]+((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0))));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( (((((((((((((((((inClose[(i-3)]>=inOpen[(i-3)])) ? (1) : ((0-1)))==1)&&((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==(0-1)))&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-1)))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==(0-1)))&&(inOpen[(i-1)]<inOpen[(i-2)]))&&(inOpen[(i-1)]>inClose[(i-2)]))&&(inOpen[i]<inOpen[(i-1)]))&&(inOpen[i]>inClose[(i-1)]))&&(inHigh[(i-3)]>inClose[(i-2)]))&&(inClose[(i-2)]>inClose[(i-1)]))&&(inClose[(i-1)]>inClose[i]))&&(((((inClose[(i-2)]>=inOpen[(i-2)])) ? (inOpen[(i-2)]) : (inClose[(i-2)]))-inLow[(i-2)])<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal[2] / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))&&(((((inClose[(i-1)]>=inOpen[(i-1)])) ? (inOpen[(i-1)]) : (inClose[(i-1)]))-inLow[(i-1)])<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal[1] / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))&&(((((inClose[i]>=inOpen[i])) ? (inOpen[i]) : (inClose[i]))-inLow[i])<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal[0] / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0)))))) ) {
            outInteger[outIdx++] = (0-100);
         } else {
            outInteger[outIdx++] = 0;
         }
         for( totIdx = 2; (totIdx>=0); totIdx -= 1 ) {
            ShadowVeryShortPeriodTotal[totIdx] = (ShadowVeryShortPeriodTotal[totIdx]+(((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(ShadowVeryShortTrailingIdx-totIdx)] - inOpen[(ShadowVeryShortTrailingIdx-totIdx)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(ShadowVeryShortTrailingIdx-totIdx)] - inLow[(ShadowVeryShortTrailingIdx-totIdx)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(ShadowVeryShortTrailingIdx-totIdx)] - inLow[(ShadowVeryShortTrailingIdx-totIdx)]) - Math.abs(inClose[(ShadowVeryShortTrailingIdx-totIdx)] - inOpen[(ShadowVeryShortTrailingIdx-totIdx)])) : 0.0)))));
         }
         i += 1;
         ShadowVeryShortTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
