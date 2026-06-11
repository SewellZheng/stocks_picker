/* Generated */
   public int cdlkickingLookback( )
   {
      int BodyLong_rangeType = this.candleSettings.bodyLong.rangeType;
      int BodyLong_avgPeriod = this.candleSettings.bodyLong.avgPeriod;
      double BodyLong_factor = this.candleSettings.bodyLong.factor;
      int ShadowVeryShort_rangeType = this.candleSettings.shadowVeryShort.rangeType;
      int ShadowVeryShort_avgPeriod = this.candleSettings.shadowVeryShort.avgPeriod;
      double ShadowVeryShort_factor = this.candleSettings.shadowVeryShort.factor;
      return (Math.max(ShadowVeryShort_avgPeriod, BodyLong_avgPeriod)+1) ;

   }
   public RetCode cdlkicking( int startIdx,
                              int endIdx,
                              double inOpen[],
                              double inHigh[],
                              double inLow[],
                              double inClose[],
                              MInteger outBegIdx,
                              MInteger outNBElement,
                              int outInteger[] )
   {
      double[] ShadowVeryShortPeriodTotal = new double[2];
      double[] BodyLongPeriodTotal = new double[2];
      int i = 0;
      int outIdx = 0;
      int totIdx = 0;
      int ShadowVeryShortTrailingIdx = 0;
      int BodyLongTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyLong_rangeType = this.candleSettings.bodyLong.rangeType;
      int BodyLong_avgPeriod = this.candleSettings.bodyLong.avgPeriod;
      double BodyLong_factor = this.candleSettings.bodyLong.factor;
      int ShadowVeryShort_rangeType = this.candleSettings.shadowVeryShort.rangeType;
      int ShadowVeryShort_avgPeriod = this.candleSettings.shadowVeryShort.avgPeriod;
      double ShadowVeryShort_factor = this.candleSettings.shadowVeryShort.factor;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackTotal = cdlkickingLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      ShadowVeryShortPeriodTotal[1] = 0;
      ShadowVeryShortPeriodTotal[0] = 0;
      ShadowVeryShortTrailingIdx = (startIdx-ShadowVeryShort_avgPeriod);
      BodyLongPeriodTotal[1] = 0;
      BodyLongPeriodTotal[0] = 0;
      BodyLongTrailingIdx = (startIdx-BodyLong_avgPeriod);
      i = ShadowVeryShortTrailingIdx;
      while( (i<startIdx) ) {
         ShadowVeryShortPeriodTotal[1] = (ShadowVeryShortPeriodTotal[1]+((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0))));
         ShadowVeryShortPeriodTotal[0] = (ShadowVeryShortPeriodTotal[0]+((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0))));
         i += 1;
      }
      i = BodyLongTrailingIdx;
      while( (i<startIdx) ) {
         BodyLongPeriodTotal[1] = (BodyLongPeriodTotal[1]+((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0))));
         BodyLongPeriodTotal[0] = (BodyLongPeriodTotal[0]+((BodyLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0))));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( (((((((((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-(((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))))&&(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyLongPeriodTotal[1] / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))))&&((inHigh[(i-1)]-(((inClose[(i-1)]>=inOpen[(i-1)])) ? (inClose[(i-1)]) : (inOpen[(i-1)])))<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal[1] / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))&&(((((inClose[(i-1)]>=inOpen[(i-1)])) ? (inOpen[(i-1)]) : (inClose[(i-1)]))-inLow[(i-1)])<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal[1] / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.abs((inClose[i]-inOpen[i]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyLongPeriodTotal[0] / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))))&&((inHigh[i]-(((inClose[i]>=inOpen[i])) ? (inClose[i]) : (inOpen[i])))<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal[0] / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))&&(((((inClose[i]>=inOpen[i])) ? (inOpen[i]) : (inClose[i]))-inLow[i])<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal[0] / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))&&((((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-1))&&(inLow[i]>inHigh[(i-1)]))||(((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==1)&&(inHigh[i]<inLow[(i-1)])))) ) {
            outInteger[outIdx++] = ((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))*100);
         } else {
            outInteger[outIdx++] = 0;
         }
         for( totIdx = 1; (totIdx>=0); totIdx -= 1 ) {
            BodyLongPeriodTotal[totIdx] = (BodyLongPeriodTotal[totIdx]+(((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((BodyLong_rangeType == 0) ? (Math.abs(inClose[(BodyLongTrailingIdx-totIdx)] - inOpen[(BodyLongTrailingIdx-totIdx)])) : ((BodyLong_rangeType == 1) ? (inHigh[(BodyLongTrailingIdx-totIdx)] - inLow[(BodyLongTrailingIdx-totIdx)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(BodyLongTrailingIdx-totIdx)] - inLow[(BodyLongTrailingIdx-totIdx)]) - Math.abs(inClose[(BodyLongTrailingIdx-totIdx)] - inOpen[(BodyLongTrailingIdx-totIdx)])) : 0.0)))));
            ShadowVeryShortPeriodTotal[totIdx] = (ShadowVeryShortPeriodTotal[totIdx]+(((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(ShadowVeryShortTrailingIdx-totIdx)] - inOpen[(ShadowVeryShortTrailingIdx-totIdx)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(ShadowVeryShortTrailingIdx-totIdx)] - inLow[(ShadowVeryShortTrailingIdx-totIdx)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(ShadowVeryShortTrailingIdx-totIdx)] - inLow[(ShadowVeryShortTrailingIdx-totIdx)]) - Math.abs(inClose[(ShadowVeryShortTrailingIdx-totIdx)] - inOpen[(ShadowVeryShortTrailingIdx-totIdx)])) : 0.0)))));
         }
         i += 1;
         ShadowVeryShortTrailingIdx += 1;
         BodyLongTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdlkickingLogic( int startIdx,
                                   int endIdx,
                                   double inOpen[],
                                   double inHigh[],
                                   double inLow[],
                                   double inClose[],
                                   MInteger outBegIdx,
                                   MInteger outNBElement,
                                   int outInteger[] )
   {
      double[] ShadowVeryShortPeriodTotal = new double[2];
      double[] BodyLongPeriodTotal = new double[2];
      int i = 0;
      int outIdx = 0;
      int totIdx = 0;
      int ShadowVeryShortTrailingIdx = 0;
      int BodyLongTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyLong_rangeType = this.candleSettings.bodyLong.rangeType;
      int BodyLong_avgPeriod = this.candleSettings.bodyLong.avgPeriod;
      double BodyLong_factor = this.candleSettings.bodyLong.factor;
      int ShadowVeryShort_rangeType = this.candleSettings.shadowVeryShort.rangeType;
      int ShadowVeryShort_avgPeriod = this.candleSettings.shadowVeryShort.avgPeriod;
      double ShadowVeryShort_factor = this.candleSettings.shadowVeryShort.factor;
      lookbackTotal = cdlkickingLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      ShadowVeryShortPeriodTotal[1] = 0;
      ShadowVeryShortPeriodTotal[0] = 0;
      ShadowVeryShortTrailingIdx = (startIdx-ShadowVeryShort_avgPeriod);
      BodyLongPeriodTotal[1] = 0;
      BodyLongPeriodTotal[0] = 0;
      BodyLongTrailingIdx = (startIdx-BodyLong_avgPeriod);
      i = ShadowVeryShortTrailingIdx;
      while( (i<startIdx) ) {
         ShadowVeryShortPeriodTotal[1] = (ShadowVeryShortPeriodTotal[1]+((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0))));
         ShadowVeryShortPeriodTotal[0] = (ShadowVeryShortPeriodTotal[0]+((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0))));
         i += 1;
      }
      i = BodyLongTrailingIdx;
      while( (i<startIdx) ) {
         BodyLongPeriodTotal[1] = (BodyLongPeriodTotal[1]+((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0))));
         BodyLongPeriodTotal[0] = (BodyLongPeriodTotal[0]+((BodyLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0))));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( (((((((((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-(((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))))&&(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyLongPeriodTotal[1] / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))))&&((inHigh[(i-1)]-(((inClose[(i-1)]>=inOpen[(i-1)])) ? (inClose[(i-1)]) : (inOpen[(i-1)])))<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal[1] / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))&&(((((inClose[(i-1)]>=inOpen[(i-1)])) ? (inOpen[(i-1)]) : (inClose[(i-1)]))-inLow[(i-1)])<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal[1] / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.abs((inClose[i]-inOpen[i]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyLongPeriodTotal[0] / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))))&&((inHigh[i]-(((inClose[i]>=inOpen[i])) ? (inClose[i]) : (inOpen[i])))<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal[0] / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))&&(((((inClose[i]>=inOpen[i])) ? (inOpen[i]) : (inClose[i]))-inLow[i])<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal[0] / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))&&((((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-1))&&(inLow[i]>inHigh[(i-1)]))||(((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==1)&&(inHigh[i]<inLow[(i-1)])))) ) {
            outInteger[outIdx++] = ((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))*100);
         } else {
            outInteger[outIdx++] = 0;
         }
         for( totIdx = 1; (totIdx>=0); totIdx -= 1 ) {
            BodyLongPeriodTotal[totIdx] = (BodyLongPeriodTotal[totIdx]+(((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((BodyLong_rangeType == 0) ? (Math.abs(inClose[(BodyLongTrailingIdx-totIdx)] - inOpen[(BodyLongTrailingIdx-totIdx)])) : ((BodyLong_rangeType == 1) ? (inHigh[(BodyLongTrailingIdx-totIdx)] - inLow[(BodyLongTrailingIdx-totIdx)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(BodyLongTrailingIdx-totIdx)] - inLow[(BodyLongTrailingIdx-totIdx)]) - Math.abs(inClose[(BodyLongTrailingIdx-totIdx)] - inOpen[(BodyLongTrailingIdx-totIdx)])) : 0.0)))));
            ShadowVeryShortPeriodTotal[totIdx] = (ShadowVeryShortPeriodTotal[totIdx]+(((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(ShadowVeryShortTrailingIdx-totIdx)] - inOpen[(ShadowVeryShortTrailingIdx-totIdx)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(ShadowVeryShortTrailingIdx-totIdx)] - inLow[(ShadowVeryShortTrailingIdx-totIdx)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(ShadowVeryShortTrailingIdx-totIdx)] - inLow[(ShadowVeryShortTrailingIdx-totIdx)]) - Math.abs(inClose[(ShadowVeryShortTrailingIdx-totIdx)] - inOpen[(ShadowVeryShortTrailingIdx-totIdx)])) : 0.0)))));
         }
         i += 1;
         ShadowVeryShortTrailingIdx += 1;
         BodyLongTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdlkicking( int startIdx,
                              int endIdx,
                              float inOpen[],
                              float inHigh[],
                              float inLow[],
                              float inClose[],
                              MInteger outBegIdx,
                              MInteger outNBElement,
                              int outInteger[] )
   {
      double[] ShadowVeryShortPeriodTotal = new double[2];
      double[] BodyLongPeriodTotal = new double[2];
      int i = 0;
      int outIdx = 0;
      int totIdx = 0;
      int ShadowVeryShortTrailingIdx = 0;
      int BodyLongTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyLong_rangeType = this.candleSettings.bodyLong.rangeType;
      int BodyLong_avgPeriod = this.candleSettings.bodyLong.avgPeriod;
      double BodyLong_factor = this.candleSettings.bodyLong.factor;
      int ShadowVeryShort_rangeType = this.candleSettings.shadowVeryShort.rangeType;
      int ShadowVeryShort_avgPeriod = this.candleSettings.shadowVeryShort.avgPeriod;
      double ShadowVeryShort_factor = this.candleSettings.shadowVeryShort.factor;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackTotal = cdlkickingLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      ShadowVeryShortPeriodTotal[1] = 0;
      ShadowVeryShortPeriodTotal[0] = 0;
      ShadowVeryShortTrailingIdx = (startIdx-ShadowVeryShort_avgPeriod);
      BodyLongPeriodTotal[1] = 0;
      BodyLongPeriodTotal[0] = 0;
      BodyLongTrailingIdx = (startIdx-BodyLong_avgPeriod);
      i = ShadowVeryShortTrailingIdx;
      while( (i<startIdx) ) {
         ShadowVeryShortPeriodTotal[1] = (ShadowVeryShortPeriodTotal[1]+((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0))));
         ShadowVeryShortPeriodTotal[0] = (ShadowVeryShortPeriodTotal[0]+((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0))));
         i += 1;
      }
      i = BodyLongTrailingIdx;
      while( (i<startIdx) ) {
         BodyLongPeriodTotal[1] = (BodyLongPeriodTotal[1]+((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0))));
         BodyLongPeriodTotal[0] = (BodyLongPeriodTotal[0]+((BodyLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0))));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( (((((((((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-(((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))))&&(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyLongPeriodTotal[1] / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))))&&((inHigh[(i-1)]-(((inClose[(i-1)]>=inOpen[(i-1)])) ? (inClose[(i-1)]) : (inOpen[(i-1)])))<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal[1] / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))&&(((((inClose[(i-1)]>=inOpen[(i-1)])) ? (inOpen[(i-1)]) : (inClose[(i-1)]))-inLow[(i-1)])<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal[1] / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.abs((inClose[i]-inOpen[i]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyLongPeriodTotal[0] / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))))&&((inHigh[i]-(((inClose[i]>=inOpen[i])) ? (inClose[i]) : (inOpen[i])))<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal[0] / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))&&(((((inClose[i]>=inOpen[i])) ? (inOpen[i]) : (inClose[i]))-inLow[i])<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal[0] / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))&&((((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-1))&&(inLow[i]>inHigh[(i-1)]))||(((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==1)&&(inHigh[i]<inLow[(i-1)])))) ) {
            outInteger[outIdx++] = ((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))*100);
         } else {
            outInteger[outIdx++] = 0;
         }
         for( totIdx = 1; (totIdx>=0); totIdx -= 1 ) {
            BodyLongPeriodTotal[totIdx] = (BodyLongPeriodTotal[totIdx]+(((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((BodyLong_rangeType == 0) ? (Math.abs(inClose[(BodyLongTrailingIdx-totIdx)] - inOpen[(BodyLongTrailingIdx-totIdx)])) : ((BodyLong_rangeType == 1) ? (inHigh[(BodyLongTrailingIdx-totIdx)] - inLow[(BodyLongTrailingIdx-totIdx)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(BodyLongTrailingIdx-totIdx)] - inLow[(BodyLongTrailingIdx-totIdx)]) - Math.abs(inClose[(BodyLongTrailingIdx-totIdx)] - inOpen[(BodyLongTrailingIdx-totIdx)])) : 0.0)))));
            ShadowVeryShortPeriodTotal[totIdx] = (ShadowVeryShortPeriodTotal[totIdx]+(((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(ShadowVeryShortTrailingIdx-totIdx)] - inOpen[(ShadowVeryShortTrailingIdx-totIdx)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(ShadowVeryShortTrailingIdx-totIdx)] - inLow[(ShadowVeryShortTrailingIdx-totIdx)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(ShadowVeryShortTrailingIdx-totIdx)] - inLow[(ShadowVeryShortTrailingIdx-totIdx)]) - Math.abs(inClose[(ShadowVeryShortTrailingIdx-totIdx)] - inOpen[(ShadowVeryShortTrailingIdx-totIdx)])) : 0.0)))));
         }
         i += 1;
         ShadowVeryShortTrailingIdx += 1;
         BodyLongTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdlkickingLogic( int startIdx,
                                   int endIdx,
                                   float inOpen[],
                                   float inHigh[],
                                   float inLow[],
                                   float inClose[],
                                   MInteger outBegIdx,
                                   MInteger outNBElement,
                                   int outInteger[] )
   {
      double[] ShadowVeryShortPeriodTotal = new double[2];
      double[] BodyLongPeriodTotal = new double[2];
      int i = 0;
      int outIdx = 0;
      int totIdx = 0;
      int ShadowVeryShortTrailingIdx = 0;
      int BodyLongTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyLong_rangeType = this.candleSettings.bodyLong.rangeType;
      int BodyLong_avgPeriod = this.candleSettings.bodyLong.avgPeriod;
      double BodyLong_factor = this.candleSettings.bodyLong.factor;
      int ShadowVeryShort_rangeType = this.candleSettings.shadowVeryShort.rangeType;
      int ShadowVeryShort_avgPeriod = this.candleSettings.shadowVeryShort.avgPeriod;
      double ShadowVeryShort_factor = this.candleSettings.shadowVeryShort.factor;
      lookbackTotal = cdlkickingLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      ShadowVeryShortPeriodTotal[1] = 0;
      ShadowVeryShortPeriodTotal[0] = 0;
      ShadowVeryShortTrailingIdx = (startIdx-ShadowVeryShort_avgPeriod);
      BodyLongPeriodTotal[1] = 0;
      BodyLongPeriodTotal[0] = 0;
      BodyLongTrailingIdx = (startIdx-BodyLong_avgPeriod);
      i = ShadowVeryShortTrailingIdx;
      while( (i<startIdx) ) {
         ShadowVeryShortPeriodTotal[1] = (ShadowVeryShortPeriodTotal[1]+((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0))));
         ShadowVeryShortPeriodTotal[0] = (ShadowVeryShortPeriodTotal[0]+((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0))));
         i += 1;
      }
      i = BodyLongTrailingIdx;
      while( (i<startIdx) ) {
         BodyLongPeriodTotal[1] = (BodyLongPeriodTotal[1]+((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0))));
         BodyLongPeriodTotal[0] = (BodyLongPeriodTotal[0]+((BodyLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0))));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( (((((((((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-(((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))))&&(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyLongPeriodTotal[1] / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))))&&((inHigh[(i-1)]-(((inClose[(i-1)]>=inOpen[(i-1)])) ? (inClose[(i-1)]) : (inOpen[(i-1)])))<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal[1] / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))&&(((((inClose[(i-1)]>=inOpen[(i-1)])) ? (inOpen[(i-1)]) : (inClose[(i-1)]))-inLow[(i-1)])<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal[1] / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.abs((inClose[i]-inOpen[i]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyLongPeriodTotal[0] / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))))&&((inHigh[i]-(((inClose[i]>=inOpen[i])) ? (inClose[i]) : (inOpen[i])))<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal[0] / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))&&(((((inClose[i]>=inOpen[i])) ? (inOpen[i]) : (inClose[i]))-inLow[i])<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal[0] / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))&&((((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-1))&&(inLow[i]>inHigh[(i-1)]))||(((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==1)&&(inHigh[i]<inLow[(i-1)])))) ) {
            outInteger[outIdx++] = ((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))*100);
         } else {
            outInteger[outIdx++] = 0;
         }
         for( totIdx = 1; (totIdx>=0); totIdx -= 1 ) {
            BodyLongPeriodTotal[totIdx] = (BodyLongPeriodTotal[totIdx]+(((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((BodyLong_rangeType == 0) ? (Math.abs(inClose[(BodyLongTrailingIdx-totIdx)] - inOpen[(BodyLongTrailingIdx-totIdx)])) : ((BodyLong_rangeType == 1) ? (inHigh[(BodyLongTrailingIdx-totIdx)] - inLow[(BodyLongTrailingIdx-totIdx)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(BodyLongTrailingIdx-totIdx)] - inLow[(BodyLongTrailingIdx-totIdx)]) - Math.abs(inClose[(BodyLongTrailingIdx-totIdx)] - inOpen[(BodyLongTrailingIdx-totIdx)])) : 0.0)))));
            ShadowVeryShortPeriodTotal[totIdx] = (ShadowVeryShortPeriodTotal[totIdx]+(((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(ShadowVeryShortTrailingIdx-totIdx)] - inOpen[(ShadowVeryShortTrailingIdx-totIdx)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(ShadowVeryShortTrailingIdx-totIdx)] - inLow[(ShadowVeryShortTrailingIdx-totIdx)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(ShadowVeryShortTrailingIdx-totIdx)] - inLow[(ShadowVeryShortTrailingIdx-totIdx)]) - Math.abs(inClose[(ShadowVeryShortTrailingIdx-totIdx)] - inOpen[(ShadowVeryShortTrailingIdx-totIdx)])) : 0.0)))));
         }
         i += 1;
         ShadowVeryShortTrailingIdx += 1;
         BodyLongTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
