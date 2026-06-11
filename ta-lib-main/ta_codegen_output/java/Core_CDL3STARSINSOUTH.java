/* Generated */
   public int cdl3starsinsouthLookback( )
   {
      int BodyLong_rangeType = this.candleSettings.bodyLong.rangeType;
      int BodyLong_avgPeriod = this.candleSettings.bodyLong.avgPeriod;
      double BodyLong_factor = this.candleSettings.bodyLong.factor;
      int BodyShort_rangeType = this.candleSettings.bodyShort.rangeType;
      int BodyShort_avgPeriod = this.candleSettings.bodyShort.avgPeriod;
      double BodyShort_factor = this.candleSettings.bodyShort.factor;
      int ShadowLong_rangeType = this.candleSettings.shadowLong.rangeType;
      int ShadowLong_avgPeriod = this.candleSettings.shadowLong.avgPeriod;
      double ShadowLong_factor = this.candleSettings.shadowLong.factor;
      int ShadowVeryShort_rangeType = this.candleSettings.shadowVeryShort.rangeType;
      int ShadowVeryShort_avgPeriod = this.candleSettings.shadowVeryShort.avgPeriod;
      double ShadowVeryShort_factor = this.candleSettings.shadowVeryShort.factor;
      return (Math.max(Math.max(ShadowVeryShort_avgPeriod, ShadowLong_avgPeriod), Math.max(BodyLong_avgPeriod, BodyShort_avgPeriod))+2) ;

   }
   public RetCode cdl3starsinsouth( int startIdx,
                                    int endIdx,
                                    double inOpen[],
                                    double inHigh[],
                                    double inLow[],
                                    double inClose[],
                                    MInteger outBegIdx,
                                    MInteger outNBElement,
                                    int outInteger[] )
   {
      double BodyLongPeriodTotal = 0;
      double BodyShortPeriodTotal = 0;
      double ShadowLongPeriodTotal = 0;
      double[] ShadowVeryShortPeriodTotal = new double[2];
      int i = 0;
      int outIdx = 0;
      int totIdx = 0;
      int BodyLongTrailingIdx = 0;
      int BodyShortTrailingIdx = 0;
      int ShadowLongTrailingIdx = 0;
      int ShadowVeryShortTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyLong_rangeType = this.candleSettings.bodyLong.rangeType;
      int BodyLong_avgPeriod = this.candleSettings.bodyLong.avgPeriod;
      double BodyLong_factor = this.candleSettings.bodyLong.factor;
      int BodyShort_rangeType = this.candleSettings.bodyShort.rangeType;
      int BodyShort_avgPeriod = this.candleSettings.bodyShort.avgPeriod;
      double BodyShort_factor = this.candleSettings.bodyShort.factor;
      int ShadowLong_rangeType = this.candleSettings.shadowLong.rangeType;
      int ShadowLong_avgPeriod = this.candleSettings.shadowLong.avgPeriod;
      double ShadowLong_factor = this.candleSettings.shadowLong.factor;
      int ShadowVeryShort_rangeType = this.candleSettings.shadowVeryShort.rangeType;
      int ShadowVeryShort_avgPeriod = this.candleSettings.shadowVeryShort.avgPeriod;
      double ShadowVeryShort_factor = this.candleSettings.shadowVeryShort.factor;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackTotal = cdl3starsinsouthLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      BodyLongPeriodTotal = 0;
      BodyLongTrailingIdx = (startIdx-BodyLong_avgPeriod);
      ShadowLongPeriodTotal = 0;
      ShadowLongTrailingIdx = (startIdx-ShadowLong_avgPeriod);
      ShadowVeryShortPeriodTotal[1] = 0;
      ShadowVeryShortPeriodTotal[0] = 0;
      ShadowVeryShortTrailingIdx = (startIdx-ShadowVeryShort_avgPeriod);
      BodyShortPeriodTotal = 0;
      BodyShortTrailingIdx = (startIdx-BodyShort_avgPeriod);
      i = BodyLongTrailingIdx;
      while( (i<startIdx) ) {
         BodyLongPeriodTotal += ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)));
         i += 1;
      }
      i = ShadowLongTrailingIdx;
      while( (i<startIdx) ) {
         ShadowLongPeriodTotal += ((ShadowLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((ShadowLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((ShadowLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)));
         i += 1;
      }
      i = ShadowVeryShortTrailingIdx;
      while( (i<startIdx) ) {
         ShadowVeryShortPeriodTotal[1] = (ShadowVeryShortPeriodTotal[1]+((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0))));
         ShadowVeryShortPeriodTotal[0] = (ShadowVeryShortPeriodTotal[0]+((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0))));
         i += 1;
      }
      i = BodyShortTrailingIdx;
      while( (i<startIdx) ) {
         BodyShortPeriodTotal += ((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( (((((((((((((((((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==(0-1))&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-1)))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==(0-1)))&&(Math.abs((inClose[(i-2)]-inOpen[(i-2)]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyLongPeriodTotal / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))))&&(((((inClose[(i-2)]>=inOpen[(i-2)])) ? (inOpen[(i-2)]) : (inClose[(i-2)]))-inLow[(i-2)])>((ShadowLong_factor * (((ShadowLong_avgPeriod != 0) ? (ShadowLongPeriodTotal / ShadowLong_avgPeriod) : ((ShadowLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((ShadowLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((ShadowLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((ShadowLong_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))<Math.abs((inClose[(i-2)]-inOpen[(i-2)]))))&&(inOpen[(i-1)]>inClose[(i-2)]))&&(inOpen[(i-1)]<=inHigh[(i-2)]))&&(inLow[(i-1)]<inClose[(i-2)]))&&(inLow[(i-1)]>=inLow[(i-2)]))&&(((((inClose[(i-1)]>=inOpen[(i-1)])) ? (inOpen[(i-1)]) : (inClose[(i-1)]))-inLow[(i-1)])>((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal[1] / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.abs((inClose[i]-inOpen[i]))<((BodyShort_factor * (((BodyShort_avgPeriod != 0) ? (BodyShortPeriodTotal / BodyShort_avgPeriod) : ((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((BodyShort_rangeType == 2) ? 2.0 : 1.0))))))&&(((((inClose[i]>=inOpen[i])) ? (inOpen[i]) : (inClose[i]))-inLow[i])<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal[0] / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))&&((inHigh[i]-(((inClose[i]>=inOpen[i])) ? (inClose[i]) : (inOpen[i])))<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal[0] / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))&&(inLow[i]>inLow[(i-1)]))&&(inHigh[i]<inHigh[(i-1)])) ) {
            outInteger[outIdx++] = 100;
         } else {
            outInteger[outIdx++] = 0;
         }
         BodyLongPeriodTotal += (((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))-((BodyLong_rangeType == 0) ? (Math.abs(inClose[(BodyLongTrailingIdx-2)] - inOpen[(BodyLongTrailingIdx-2)])) : ((BodyLong_rangeType == 1) ? (inHigh[(BodyLongTrailingIdx-2)] - inLow[(BodyLongTrailingIdx-2)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(BodyLongTrailingIdx-2)] - inLow[(BodyLongTrailingIdx-2)]) - Math.abs(inClose[(BodyLongTrailingIdx-2)] - inOpen[(BodyLongTrailingIdx-2)])) : 0.0))));
         ShadowLongPeriodTotal += (((ShadowLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((ShadowLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((ShadowLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))-((ShadowLong_rangeType == 0) ? (Math.abs(inClose[(ShadowLongTrailingIdx-2)] - inOpen[(ShadowLongTrailingIdx-2)])) : ((ShadowLong_rangeType == 1) ? (inHigh[(ShadowLongTrailingIdx-2)] - inLow[(ShadowLongTrailingIdx-2)]) : ((ShadowLong_rangeType == 2) ? ((inHigh[(ShadowLongTrailingIdx-2)] - inLow[(ShadowLongTrailingIdx-2)]) - Math.abs(inClose[(ShadowLongTrailingIdx-2)] - inOpen[(ShadowLongTrailingIdx-2)])) : 0.0))));
         for( totIdx = 1; (totIdx>=0); totIdx -= 1 ) {
            ShadowVeryShortPeriodTotal[totIdx] = (ShadowVeryShortPeriodTotal[totIdx]+(((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(ShadowVeryShortTrailingIdx-totIdx)] - inOpen[(ShadowVeryShortTrailingIdx-totIdx)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(ShadowVeryShortTrailingIdx-totIdx)] - inLow[(ShadowVeryShortTrailingIdx-totIdx)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(ShadowVeryShortTrailingIdx-totIdx)] - inLow[(ShadowVeryShortTrailingIdx-totIdx)]) - Math.abs(inClose[(ShadowVeryShortTrailingIdx-totIdx)] - inOpen[(ShadowVeryShortTrailingIdx-totIdx)])) : 0.0)))));
         }
         BodyShortPeriodTotal += (((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((BodyShort_rangeType == 0) ? (Math.abs(inClose[BodyShortTrailingIdx] - inOpen[BodyShortTrailingIdx])) : ((BodyShort_rangeType == 1) ? (inHigh[BodyShortTrailingIdx] - inLow[BodyShortTrailingIdx]) : ((BodyShort_rangeType == 2) ? ((inHigh[BodyShortTrailingIdx] - inLow[BodyShortTrailingIdx]) - Math.abs(inClose[BodyShortTrailingIdx] - inOpen[BodyShortTrailingIdx])) : 0.0))));
         i += 1;
         BodyLongTrailingIdx += 1;
         ShadowLongTrailingIdx += 1;
         ShadowVeryShortTrailingIdx += 1;
         BodyShortTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdl3starsinsouthLogic( int startIdx,
                                         int endIdx,
                                         double inOpen[],
                                         double inHigh[],
                                         double inLow[],
                                         double inClose[],
                                         MInteger outBegIdx,
                                         MInteger outNBElement,
                                         int outInteger[] )
   {
      double BodyLongPeriodTotal = 0;
      double BodyShortPeriodTotal = 0;
      double ShadowLongPeriodTotal = 0;
      double[] ShadowVeryShortPeriodTotal = new double[2];
      int i = 0;
      int outIdx = 0;
      int totIdx = 0;
      int BodyLongTrailingIdx = 0;
      int BodyShortTrailingIdx = 0;
      int ShadowLongTrailingIdx = 0;
      int ShadowVeryShortTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyLong_rangeType = this.candleSettings.bodyLong.rangeType;
      int BodyLong_avgPeriod = this.candleSettings.bodyLong.avgPeriod;
      double BodyLong_factor = this.candleSettings.bodyLong.factor;
      int BodyShort_rangeType = this.candleSettings.bodyShort.rangeType;
      int BodyShort_avgPeriod = this.candleSettings.bodyShort.avgPeriod;
      double BodyShort_factor = this.candleSettings.bodyShort.factor;
      int ShadowLong_rangeType = this.candleSettings.shadowLong.rangeType;
      int ShadowLong_avgPeriod = this.candleSettings.shadowLong.avgPeriod;
      double ShadowLong_factor = this.candleSettings.shadowLong.factor;
      int ShadowVeryShort_rangeType = this.candleSettings.shadowVeryShort.rangeType;
      int ShadowVeryShort_avgPeriod = this.candleSettings.shadowVeryShort.avgPeriod;
      double ShadowVeryShort_factor = this.candleSettings.shadowVeryShort.factor;
      lookbackTotal = cdl3starsinsouthLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      BodyLongPeriodTotal = 0;
      BodyLongTrailingIdx = (startIdx-BodyLong_avgPeriod);
      ShadowLongPeriodTotal = 0;
      ShadowLongTrailingIdx = (startIdx-ShadowLong_avgPeriod);
      ShadowVeryShortPeriodTotal[1] = 0;
      ShadowVeryShortPeriodTotal[0] = 0;
      ShadowVeryShortTrailingIdx = (startIdx-ShadowVeryShort_avgPeriod);
      BodyShortPeriodTotal = 0;
      BodyShortTrailingIdx = (startIdx-BodyShort_avgPeriod);
      i = BodyLongTrailingIdx;
      while( (i<startIdx) ) {
         BodyLongPeriodTotal += ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)));
         i += 1;
      }
      i = ShadowLongTrailingIdx;
      while( (i<startIdx) ) {
         ShadowLongPeriodTotal += ((ShadowLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((ShadowLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((ShadowLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)));
         i += 1;
      }
      i = ShadowVeryShortTrailingIdx;
      while( (i<startIdx) ) {
         ShadowVeryShortPeriodTotal[1] = (ShadowVeryShortPeriodTotal[1]+((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0))));
         ShadowVeryShortPeriodTotal[0] = (ShadowVeryShortPeriodTotal[0]+((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0))));
         i += 1;
      }
      i = BodyShortTrailingIdx;
      while( (i<startIdx) ) {
         BodyShortPeriodTotal += ((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( (((((((((((((((((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==(0-1))&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-1)))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==(0-1)))&&(Math.abs((inClose[(i-2)]-inOpen[(i-2)]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyLongPeriodTotal / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))))&&(((((inClose[(i-2)]>=inOpen[(i-2)])) ? (inOpen[(i-2)]) : (inClose[(i-2)]))-inLow[(i-2)])>((ShadowLong_factor * (((ShadowLong_avgPeriod != 0) ? (ShadowLongPeriodTotal / ShadowLong_avgPeriod) : ((ShadowLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((ShadowLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((ShadowLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((ShadowLong_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))<Math.abs((inClose[(i-2)]-inOpen[(i-2)]))))&&(inOpen[(i-1)]>inClose[(i-2)]))&&(inOpen[(i-1)]<=inHigh[(i-2)]))&&(inLow[(i-1)]<inClose[(i-2)]))&&(inLow[(i-1)]>=inLow[(i-2)]))&&(((((inClose[(i-1)]>=inOpen[(i-1)])) ? (inOpen[(i-1)]) : (inClose[(i-1)]))-inLow[(i-1)])>((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal[1] / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.abs((inClose[i]-inOpen[i]))<((BodyShort_factor * (((BodyShort_avgPeriod != 0) ? (BodyShortPeriodTotal / BodyShort_avgPeriod) : ((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((BodyShort_rangeType == 2) ? 2.0 : 1.0))))))&&(((((inClose[i]>=inOpen[i])) ? (inOpen[i]) : (inClose[i]))-inLow[i])<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal[0] / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))&&((inHigh[i]-(((inClose[i]>=inOpen[i])) ? (inClose[i]) : (inOpen[i])))<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal[0] / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))&&(inLow[i]>inLow[(i-1)]))&&(inHigh[i]<inHigh[(i-1)])) ) {
            outInteger[outIdx++] = 100;
         } else {
            outInteger[outIdx++] = 0;
         }
         BodyLongPeriodTotal += (((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))-((BodyLong_rangeType == 0) ? (Math.abs(inClose[(BodyLongTrailingIdx-2)] - inOpen[(BodyLongTrailingIdx-2)])) : ((BodyLong_rangeType == 1) ? (inHigh[(BodyLongTrailingIdx-2)] - inLow[(BodyLongTrailingIdx-2)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(BodyLongTrailingIdx-2)] - inLow[(BodyLongTrailingIdx-2)]) - Math.abs(inClose[(BodyLongTrailingIdx-2)] - inOpen[(BodyLongTrailingIdx-2)])) : 0.0))));
         ShadowLongPeriodTotal += (((ShadowLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((ShadowLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((ShadowLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))-((ShadowLong_rangeType == 0) ? (Math.abs(inClose[(ShadowLongTrailingIdx-2)] - inOpen[(ShadowLongTrailingIdx-2)])) : ((ShadowLong_rangeType == 1) ? (inHigh[(ShadowLongTrailingIdx-2)] - inLow[(ShadowLongTrailingIdx-2)]) : ((ShadowLong_rangeType == 2) ? ((inHigh[(ShadowLongTrailingIdx-2)] - inLow[(ShadowLongTrailingIdx-2)]) - Math.abs(inClose[(ShadowLongTrailingIdx-2)] - inOpen[(ShadowLongTrailingIdx-2)])) : 0.0))));
         for( totIdx = 1; (totIdx>=0); totIdx -= 1 ) {
            ShadowVeryShortPeriodTotal[totIdx] = (ShadowVeryShortPeriodTotal[totIdx]+(((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(ShadowVeryShortTrailingIdx-totIdx)] - inOpen[(ShadowVeryShortTrailingIdx-totIdx)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(ShadowVeryShortTrailingIdx-totIdx)] - inLow[(ShadowVeryShortTrailingIdx-totIdx)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(ShadowVeryShortTrailingIdx-totIdx)] - inLow[(ShadowVeryShortTrailingIdx-totIdx)]) - Math.abs(inClose[(ShadowVeryShortTrailingIdx-totIdx)] - inOpen[(ShadowVeryShortTrailingIdx-totIdx)])) : 0.0)))));
         }
         BodyShortPeriodTotal += (((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((BodyShort_rangeType == 0) ? (Math.abs(inClose[BodyShortTrailingIdx] - inOpen[BodyShortTrailingIdx])) : ((BodyShort_rangeType == 1) ? (inHigh[BodyShortTrailingIdx] - inLow[BodyShortTrailingIdx]) : ((BodyShort_rangeType == 2) ? ((inHigh[BodyShortTrailingIdx] - inLow[BodyShortTrailingIdx]) - Math.abs(inClose[BodyShortTrailingIdx] - inOpen[BodyShortTrailingIdx])) : 0.0))));
         i += 1;
         BodyLongTrailingIdx += 1;
         ShadowLongTrailingIdx += 1;
         ShadowVeryShortTrailingIdx += 1;
         BodyShortTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdl3starsinsouth( int startIdx,
                                    int endIdx,
                                    float inOpen[],
                                    float inHigh[],
                                    float inLow[],
                                    float inClose[],
                                    MInteger outBegIdx,
                                    MInteger outNBElement,
                                    int outInteger[] )
   {
      double BodyLongPeriodTotal = 0;
      double BodyShortPeriodTotal = 0;
      double ShadowLongPeriodTotal = 0;
      double[] ShadowVeryShortPeriodTotal = new double[2];
      int i = 0;
      int outIdx = 0;
      int totIdx = 0;
      int BodyLongTrailingIdx = 0;
      int BodyShortTrailingIdx = 0;
      int ShadowLongTrailingIdx = 0;
      int ShadowVeryShortTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyLong_rangeType = this.candleSettings.bodyLong.rangeType;
      int BodyLong_avgPeriod = this.candleSettings.bodyLong.avgPeriod;
      double BodyLong_factor = this.candleSettings.bodyLong.factor;
      int BodyShort_rangeType = this.candleSettings.bodyShort.rangeType;
      int BodyShort_avgPeriod = this.candleSettings.bodyShort.avgPeriod;
      double BodyShort_factor = this.candleSettings.bodyShort.factor;
      int ShadowLong_rangeType = this.candleSettings.shadowLong.rangeType;
      int ShadowLong_avgPeriod = this.candleSettings.shadowLong.avgPeriod;
      double ShadowLong_factor = this.candleSettings.shadowLong.factor;
      int ShadowVeryShort_rangeType = this.candleSettings.shadowVeryShort.rangeType;
      int ShadowVeryShort_avgPeriod = this.candleSettings.shadowVeryShort.avgPeriod;
      double ShadowVeryShort_factor = this.candleSettings.shadowVeryShort.factor;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackTotal = cdl3starsinsouthLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      BodyLongPeriodTotal = 0;
      BodyLongTrailingIdx = (startIdx-BodyLong_avgPeriod);
      ShadowLongPeriodTotal = 0;
      ShadowLongTrailingIdx = (startIdx-ShadowLong_avgPeriod);
      ShadowVeryShortPeriodTotal[1] = 0;
      ShadowVeryShortPeriodTotal[0] = 0;
      ShadowVeryShortTrailingIdx = (startIdx-ShadowVeryShort_avgPeriod);
      BodyShortPeriodTotal = 0;
      BodyShortTrailingIdx = (startIdx-BodyShort_avgPeriod);
      i = BodyLongTrailingIdx;
      while( (i<startIdx) ) {
         BodyLongPeriodTotal += ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)));
         i += 1;
      }
      i = ShadowLongTrailingIdx;
      while( (i<startIdx) ) {
         ShadowLongPeriodTotal += ((ShadowLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((ShadowLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((ShadowLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)));
         i += 1;
      }
      i = ShadowVeryShortTrailingIdx;
      while( (i<startIdx) ) {
         ShadowVeryShortPeriodTotal[1] = (ShadowVeryShortPeriodTotal[1]+((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0))));
         ShadowVeryShortPeriodTotal[0] = (ShadowVeryShortPeriodTotal[0]+((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0))));
         i += 1;
      }
      i = BodyShortTrailingIdx;
      while( (i<startIdx) ) {
         BodyShortPeriodTotal += ((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( (((((((((((((((((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==(0-1))&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-1)))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==(0-1)))&&(Math.abs((inClose[(i-2)]-inOpen[(i-2)]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyLongPeriodTotal / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))))&&(((((inClose[(i-2)]>=inOpen[(i-2)])) ? (inOpen[(i-2)]) : (inClose[(i-2)]))-inLow[(i-2)])>((ShadowLong_factor * (((ShadowLong_avgPeriod != 0) ? (ShadowLongPeriodTotal / ShadowLong_avgPeriod) : ((ShadowLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((ShadowLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((ShadowLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((ShadowLong_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))<Math.abs((inClose[(i-2)]-inOpen[(i-2)]))))&&(inOpen[(i-1)]>inClose[(i-2)]))&&(inOpen[(i-1)]<=inHigh[(i-2)]))&&(inLow[(i-1)]<inClose[(i-2)]))&&(inLow[(i-1)]>=inLow[(i-2)]))&&(((((inClose[(i-1)]>=inOpen[(i-1)])) ? (inOpen[(i-1)]) : (inClose[(i-1)]))-inLow[(i-1)])>((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal[1] / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.abs((inClose[i]-inOpen[i]))<((BodyShort_factor * (((BodyShort_avgPeriod != 0) ? (BodyShortPeriodTotal / BodyShort_avgPeriod) : ((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((BodyShort_rangeType == 2) ? 2.0 : 1.0))))))&&(((((inClose[i]>=inOpen[i])) ? (inOpen[i]) : (inClose[i]))-inLow[i])<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal[0] / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))&&((inHigh[i]-(((inClose[i]>=inOpen[i])) ? (inClose[i]) : (inOpen[i])))<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal[0] / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))&&(inLow[i]>inLow[(i-1)]))&&(inHigh[i]<inHigh[(i-1)])) ) {
            outInteger[outIdx++] = 100;
         } else {
            outInteger[outIdx++] = 0;
         }
         BodyLongPeriodTotal += (((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))-((BodyLong_rangeType == 0) ? (Math.abs(inClose[(BodyLongTrailingIdx-2)] - inOpen[(BodyLongTrailingIdx-2)])) : ((BodyLong_rangeType == 1) ? (inHigh[(BodyLongTrailingIdx-2)] - inLow[(BodyLongTrailingIdx-2)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(BodyLongTrailingIdx-2)] - inLow[(BodyLongTrailingIdx-2)]) - Math.abs(inClose[(BodyLongTrailingIdx-2)] - inOpen[(BodyLongTrailingIdx-2)])) : 0.0))));
         ShadowLongPeriodTotal += (((ShadowLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((ShadowLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((ShadowLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))-((ShadowLong_rangeType == 0) ? (Math.abs(inClose[(ShadowLongTrailingIdx-2)] - inOpen[(ShadowLongTrailingIdx-2)])) : ((ShadowLong_rangeType == 1) ? (inHigh[(ShadowLongTrailingIdx-2)] - inLow[(ShadowLongTrailingIdx-2)]) : ((ShadowLong_rangeType == 2) ? ((inHigh[(ShadowLongTrailingIdx-2)] - inLow[(ShadowLongTrailingIdx-2)]) - Math.abs(inClose[(ShadowLongTrailingIdx-2)] - inOpen[(ShadowLongTrailingIdx-2)])) : 0.0))));
         for( totIdx = 1; (totIdx>=0); totIdx -= 1 ) {
            ShadowVeryShortPeriodTotal[totIdx] = (ShadowVeryShortPeriodTotal[totIdx]+(((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(ShadowVeryShortTrailingIdx-totIdx)] - inOpen[(ShadowVeryShortTrailingIdx-totIdx)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(ShadowVeryShortTrailingIdx-totIdx)] - inLow[(ShadowVeryShortTrailingIdx-totIdx)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(ShadowVeryShortTrailingIdx-totIdx)] - inLow[(ShadowVeryShortTrailingIdx-totIdx)]) - Math.abs(inClose[(ShadowVeryShortTrailingIdx-totIdx)] - inOpen[(ShadowVeryShortTrailingIdx-totIdx)])) : 0.0)))));
         }
         BodyShortPeriodTotal += (((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((BodyShort_rangeType == 0) ? (Math.abs(inClose[BodyShortTrailingIdx] - inOpen[BodyShortTrailingIdx])) : ((BodyShort_rangeType == 1) ? (inHigh[BodyShortTrailingIdx] - inLow[BodyShortTrailingIdx]) : ((BodyShort_rangeType == 2) ? ((inHigh[BodyShortTrailingIdx] - inLow[BodyShortTrailingIdx]) - Math.abs(inClose[BodyShortTrailingIdx] - inOpen[BodyShortTrailingIdx])) : 0.0))));
         i += 1;
         BodyLongTrailingIdx += 1;
         ShadowLongTrailingIdx += 1;
         ShadowVeryShortTrailingIdx += 1;
         BodyShortTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdl3starsinsouthLogic( int startIdx,
                                         int endIdx,
                                         float inOpen[],
                                         float inHigh[],
                                         float inLow[],
                                         float inClose[],
                                         MInteger outBegIdx,
                                         MInteger outNBElement,
                                         int outInteger[] )
   {
      double BodyLongPeriodTotal = 0;
      double BodyShortPeriodTotal = 0;
      double ShadowLongPeriodTotal = 0;
      double[] ShadowVeryShortPeriodTotal = new double[2];
      int i = 0;
      int outIdx = 0;
      int totIdx = 0;
      int BodyLongTrailingIdx = 0;
      int BodyShortTrailingIdx = 0;
      int ShadowLongTrailingIdx = 0;
      int ShadowVeryShortTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyLong_rangeType = this.candleSettings.bodyLong.rangeType;
      int BodyLong_avgPeriod = this.candleSettings.bodyLong.avgPeriod;
      double BodyLong_factor = this.candleSettings.bodyLong.factor;
      int BodyShort_rangeType = this.candleSettings.bodyShort.rangeType;
      int BodyShort_avgPeriod = this.candleSettings.bodyShort.avgPeriod;
      double BodyShort_factor = this.candleSettings.bodyShort.factor;
      int ShadowLong_rangeType = this.candleSettings.shadowLong.rangeType;
      int ShadowLong_avgPeriod = this.candleSettings.shadowLong.avgPeriod;
      double ShadowLong_factor = this.candleSettings.shadowLong.factor;
      int ShadowVeryShort_rangeType = this.candleSettings.shadowVeryShort.rangeType;
      int ShadowVeryShort_avgPeriod = this.candleSettings.shadowVeryShort.avgPeriod;
      double ShadowVeryShort_factor = this.candleSettings.shadowVeryShort.factor;
      lookbackTotal = cdl3starsinsouthLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      BodyLongPeriodTotal = 0;
      BodyLongTrailingIdx = (startIdx-BodyLong_avgPeriod);
      ShadowLongPeriodTotal = 0;
      ShadowLongTrailingIdx = (startIdx-ShadowLong_avgPeriod);
      ShadowVeryShortPeriodTotal[1] = 0;
      ShadowVeryShortPeriodTotal[0] = 0;
      ShadowVeryShortTrailingIdx = (startIdx-ShadowVeryShort_avgPeriod);
      BodyShortPeriodTotal = 0;
      BodyShortTrailingIdx = (startIdx-BodyShort_avgPeriod);
      i = BodyLongTrailingIdx;
      while( (i<startIdx) ) {
         BodyLongPeriodTotal += ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)));
         i += 1;
      }
      i = ShadowLongTrailingIdx;
      while( (i<startIdx) ) {
         ShadowLongPeriodTotal += ((ShadowLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((ShadowLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((ShadowLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)));
         i += 1;
      }
      i = ShadowVeryShortTrailingIdx;
      while( (i<startIdx) ) {
         ShadowVeryShortPeriodTotal[1] = (ShadowVeryShortPeriodTotal[1]+((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0))));
         ShadowVeryShortPeriodTotal[0] = (ShadowVeryShortPeriodTotal[0]+((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0))));
         i += 1;
      }
      i = BodyShortTrailingIdx;
      while( (i<startIdx) ) {
         BodyShortPeriodTotal += ((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( (((((((((((((((((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==(0-1))&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-1)))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==(0-1)))&&(Math.abs((inClose[(i-2)]-inOpen[(i-2)]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyLongPeriodTotal / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))))&&(((((inClose[(i-2)]>=inOpen[(i-2)])) ? (inOpen[(i-2)]) : (inClose[(i-2)]))-inLow[(i-2)])>((ShadowLong_factor * (((ShadowLong_avgPeriod != 0) ? (ShadowLongPeriodTotal / ShadowLong_avgPeriod) : ((ShadowLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((ShadowLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((ShadowLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((ShadowLong_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))<Math.abs((inClose[(i-2)]-inOpen[(i-2)]))))&&(inOpen[(i-1)]>inClose[(i-2)]))&&(inOpen[(i-1)]<=inHigh[(i-2)]))&&(inLow[(i-1)]<inClose[(i-2)]))&&(inLow[(i-1)]>=inLow[(i-2)]))&&(((((inClose[(i-1)]>=inOpen[(i-1)])) ? (inOpen[(i-1)]) : (inClose[(i-1)]))-inLow[(i-1)])>((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal[1] / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.abs((inClose[i]-inOpen[i]))<((BodyShort_factor * (((BodyShort_avgPeriod != 0) ? (BodyShortPeriodTotal / BodyShort_avgPeriod) : ((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((BodyShort_rangeType == 2) ? 2.0 : 1.0))))))&&(((((inClose[i]>=inOpen[i])) ? (inOpen[i]) : (inClose[i]))-inLow[i])<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal[0] / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))&&((inHigh[i]-(((inClose[i]>=inOpen[i])) ? (inClose[i]) : (inOpen[i])))<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal[0] / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))&&(inLow[i]>inLow[(i-1)]))&&(inHigh[i]<inHigh[(i-1)])) ) {
            outInteger[outIdx++] = 100;
         } else {
            outInteger[outIdx++] = 0;
         }
         BodyLongPeriodTotal += (((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))-((BodyLong_rangeType == 0) ? (Math.abs(inClose[(BodyLongTrailingIdx-2)] - inOpen[(BodyLongTrailingIdx-2)])) : ((BodyLong_rangeType == 1) ? (inHigh[(BodyLongTrailingIdx-2)] - inLow[(BodyLongTrailingIdx-2)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(BodyLongTrailingIdx-2)] - inLow[(BodyLongTrailingIdx-2)]) - Math.abs(inClose[(BodyLongTrailingIdx-2)] - inOpen[(BodyLongTrailingIdx-2)])) : 0.0))));
         ShadowLongPeriodTotal += (((ShadowLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((ShadowLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((ShadowLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))-((ShadowLong_rangeType == 0) ? (Math.abs(inClose[(ShadowLongTrailingIdx-2)] - inOpen[(ShadowLongTrailingIdx-2)])) : ((ShadowLong_rangeType == 1) ? (inHigh[(ShadowLongTrailingIdx-2)] - inLow[(ShadowLongTrailingIdx-2)]) : ((ShadowLong_rangeType == 2) ? ((inHigh[(ShadowLongTrailingIdx-2)] - inLow[(ShadowLongTrailingIdx-2)]) - Math.abs(inClose[(ShadowLongTrailingIdx-2)] - inOpen[(ShadowLongTrailingIdx-2)])) : 0.0))));
         for( totIdx = 1; (totIdx>=0); totIdx -= 1 ) {
            ShadowVeryShortPeriodTotal[totIdx] = (ShadowVeryShortPeriodTotal[totIdx]+(((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(ShadowVeryShortTrailingIdx-totIdx)] - inOpen[(ShadowVeryShortTrailingIdx-totIdx)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(ShadowVeryShortTrailingIdx-totIdx)] - inLow[(ShadowVeryShortTrailingIdx-totIdx)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(ShadowVeryShortTrailingIdx-totIdx)] - inLow[(ShadowVeryShortTrailingIdx-totIdx)]) - Math.abs(inClose[(ShadowVeryShortTrailingIdx-totIdx)] - inOpen[(ShadowVeryShortTrailingIdx-totIdx)])) : 0.0)))));
         }
         BodyShortPeriodTotal += (((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((BodyShort_rangeType == 0) ? (Math.abs(inClose[BodyShortTrailingIdx] - inOpen[BodyShortTrailingIdx])) : ((BodyShort_rangeType == 1) ? (inHigh[BodyShortTrailingIdx] - inLow[BodyShortTrailingIdx]) : ((BodyShort_rangeType == 2) ? ((inHigh[BodyShortTrailingIdx] - inLow[BodyShortTrailingIdx]) - Math.abs(inClose[BodyShortTrailingIdx] - inOpen[BodyShortTrailingIdx])) : 0.0))));
         i += 1;
         BodyLongTrailingIdx += 1;
         ShadowLongTrailingIdx += 1;
         ShadowVeryShortTrailingIdx += 1;
         BodyShortTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
