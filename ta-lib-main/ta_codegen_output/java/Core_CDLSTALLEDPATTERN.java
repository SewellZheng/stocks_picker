/* Generated */
   public int cdlstalledpatternLookback( )
   {
      int BodyLong_rangeType = this.candleSettings.bodyLong.rangeType;
      int BodyLong_avgPeriod = this.candleSettings.bodyLong.avgPeriod;
      double BodyLong_factor = this.candleSettings.bodyLong.factor;
      int BodyShort_rangeType = this.candleSettings.bodyShort.rangeType;
      int BodyShort_avgPeriod = this.candleSettings.bodyShort.avgPeriod;
      double BodyShort_factor = this.candleSettings.bodyShort.factor;
      int Near_rangeType = this.candleSettings.near.rangeType;
      int Near_avgPeriod = this.candleSettings.near.avgPeriod;
      double Near_factor = this.candleSettings.near.factor;
      int ShadowVeryShort_rangeType = this.candleSettings.shadowVeryShort.rangeType;
      int ShadowVeryShort_avgPeriod = this.candleSettings.shadowVeryShort.avgPeriod;
      double ShadowVeryShort_factor = this.candleSettings.shadowVeryShort.factor;
      return (Math.max(Math.max(BodyLong_avgPeriod, BodyShort_avgPeriod), Math.max(ShadowVeryShort_avgPeriod, Near_avgPeriod))+2) ;

   }
   public RetCode cdlstalledpattern( int startIdx,
                                     int endIdx,
                                     double inOpen[],
                                     double inHigh[],
                                     double inLow[],
                                     double inClose[],
                                     MInteger outBegIdx,
                                     MInteger outNBElement,
                                     int outInteger[] )
   {
      double[] BodyLongPeriodTotal = new double[3];
      double[] NearPeriodTotal = new double[3];
      double BodyShortPeriodTotal = 0;
      double ShadowVeryShortPeriodTotal = 0;
      int i = 0;
      int outIdx = 0;
      int totIdx = 0;
      int BodyLongTrailingIdx = 0;
      int BodyShortTrailingIdx = 0;
      int ShadowVeryShortTrailingIdx = 0;
      int NearTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyLong_rangeType = this.candleSettings.bodyLong.rangeType;
      int BodyLong_avgPeriod = this.candleSettings.bodyLong.avgPeriod;
      double BodyLong_factor = this.candleSettings.bodyLong.factor;
      int BodyShort_rangeType = this.candleSettings.bodyShort.rangeType;
      int BodyShort_avgPeriod = this.candleSettings.bodyShort.avgPeriod;
      double BodyShort_factor = this.candleSettings.bodyShort.factor;
      int Near_rangeType = this.candleSettings.near.rangeType;
      int Near_avgPeriod = this.candleSettings.near.avgPeriod;
      double Near_factor = this.candleSettings.near.factor;
      int ShadowVeryShort_rangeType = this.candleSettings.shadowVeryShort.rangeType;
      int ShadowVeryShort_avgPeriod = this.candleSettings.shadowVeryShort.avgPeriod;
      double ShadowVeryShort_factor = this.candleSettings.shadowVeryShort.factor;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackTotal = cdlstalledpatternLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      BodyLongPeriodTotal[2] = 0;
      BodyLongPeriodTotal[1] = 0;
      BodyLongPeriodTotal[0] = 0;
      BodyLongTrailingIdx = (startIdx-BodyLong_avgPeriod);
      BodyShortPeriodTotal = 0;
      BodyShortTrailingIdx = (startIdx-BodyShort_avgPeriod);
      ShadowVeryShortPeriodTotal = 0;
      ShadowVeryShortTrailingIdx = (startIdx-ShadowVeryShort_avgPeriod);
      NearPeriodTotal[2] = 0;
      NearPeriodTotal[1] = 0;
      NearPeriodTotal[0] = 0;
      NearTrailingIdx = (startIdx-Near_avgPeriod);
      i = BodyLongTrailingIdx;
      while( (i<startIdx) ) {
         BodyLongPeriodTotal[2] = (BodyLongPeriodTotal[2]+((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0))));
         BodyLongPeriodTotal[1] = (BodyLongPeriodTotal[1]+((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0))));
         i += 1;
      }
      i = BodyShortTrailingIdx;
      while( (i<startIdx) ) {
         BodyShortPeriodTotal += ((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = ShadowVeryShortTrailingIdx;
      while( (i<startIdx) ) {
         ShadowVeryShortPeriodTotal += ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)));
         i += 1;
      }
      i = NearTrailingIdx;
      while( (i<startIdx) ) {
         NearPeriodTotal[2] = (NearPeriodTotal[2]+((Near_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((Near_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((Near_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0))));
         NearPeriodTotal[1] = (NearPeriodTotal[1]+((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0))));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( (((((((((((((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==1)&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==1))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==1))&&(inClose[i]>inClose[(i-1)]))&&(inClose[(i-1)]>inClose[(i-2)]))&&(Math.abs((inClose[(i-2)]-inOpen[(i-2)]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyLongPeriodTotal[2] / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyLongPeriodTotal[1] / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))))&&((inHigh[(i-1)]-(((inClose[(i-1)]>=inOpen[(i-1)])) ? (inClose[(i-1)]) : (inOpen[(i-1)])))<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))&&(inOpen[(i-1)]>inOpen[(i-2)]))&&(inOpen[(i-1)]<=(inClose[(i-2)]+((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal[2] / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((Near_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((Near_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0)))))))&&(Math.abs((inClose[i]-inOpen[i]))<((BodyShort_factor * (((BodyShort_avgPeriod != 0) ? (BodyShortPeriodTotal / BodyShort_avgPeriod) : ((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((BodyShort_rangeType == 2) ? 2.0 : 1.0))))))&&(inOpen[i]>=((inClose[(i-1)]-Math.abs((inClose[i]-inOpen[i])))-((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal[1] / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0))))))) ) {
            outInteger[outIdx++] = (0-100);
         } else {
            outInteger[outIdx++] = 0;
         }
         for( totIdx = 2; (totIdx>=1); totIdx -= 1 ) {
            BodyLongPeriodTotal[totIdx] = (BodyLongPeriodTotal[totIdx]+(((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((BodyLong_rangeType == 0) ? (Math.abs(inClose[(BodyLongTrailingIdx-totIdx)] - inOpen[(BodyLongTrailingIdx-totIdx)])) : ((BodyLong_rangeType == 1) ? (inHigh[(BodyLongTrailingIdx-totIdx)] - inLow[(BodyLongTrailingIdx-totIdx)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(BodyLongTrailingIdx-totIdx)] - inLow[(BodyLongTrailingIdx-totIdx)]) - Math.abs(inClose[(BodyLongTrailingIdx-totIdx)] - inOpen[(BodyLongTrailingIdx-totIdx)])) : 0.0)))));
            NearPeriodTotal[totIdx] = (NearPeriodTotal[totIdx]+(((Near_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((Near_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((Near_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((Near_rangeType == 0) ? (Math.abs(inClose[(NearTrailingIdx-totIdx)] - inOpen[(NearTrailingIdx-totIdx)])) : ((Near_rangeType == 1) ? (inHigh[(NearTrailingIdx-totIdx)] - inLow[(NearTrailingIdx-totIdx)]) : ((Near_rangeType == 2) ? ((inHigh[(NearTrailingIdx-totIdx)] - inLow[(NearTrailingIdx-totIdx)]) - Math.abs(inClose[(NearTrailingIdx-totIdx)] - inOpen[(NearTrailingIdx-totIdx)])) : 0.0)))));
         }
         BodyShortPeriodTotal += (((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((BodyShort_rangeType == 0) ? (Math.abs(inClose[BodyShortTrailingIdx] - inOpen[BodyShortTrailingIdx])) : ((BodyShort_rangeType == 1) ? (inHigh[BodyShortTrailingIdx] - inLow[BodyShortTrailingIdx]) : ((BodyShort_rangeType == 2) ? ((inHigh[BodyShortTrailingIdx] - inLow[BodyShortTrailingIdx]) - Math.abs(inClose[BodyShortTrailingIdx] - inOpen[BodyShortTrailingIdx])) : 0.0))));
         ShadowVeryShortPeriodTotal += (((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))-((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(ShadowVeryShortTrailingIdx-1)] - inOpen[(ShadowVeryShortTrailingIdx-1)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(ShadowVeryShortTrailingIdx-1)] - inLow[(ShadowVeryShortTrailingIdx-1)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(ShadowVeryShortTrailingIdx-1)] - inLow[(ShadowVeryShortTrailingIdx-1)]) - Math.abs(inClose[(ShadowVeryShortTrailingIdx-1)] - inOpen[(ShadowVeryShortTrailingIdx-1)])) : 0.0))));
         i += 1;
         BodyLongTrailingIdx += 1;
         BodyShortTrailingIdx += 1;
         ShadowVeryShortTrailingIdx += 1;
         NearTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdlstalledpatternLogic( int startIdx,
                                          int endIdx,
                                          double inOpen[],
                                          double inHigh[],
                                          double inLow[],
                                          double inClose[],
                                          MInteger outBegIdx,
                                          MInteger outNBElement,
                                          int outInteger[] )
   {
      double[] BodyLongPeriodTotal = new double[3];
      double[] NearPeriodTotal = new double[3];
      double BodyShortPeriodTotal = 0;
      double ShadowVeryShortPeriodTotal = 0;
      int i = 0;
      int outIdx = 0;
      int totIdx = 0;
      int BodyLongTrailingIdx = 0;
      int BodyShortTrailingIdx = 0;
      int ShadowVeryShortTrailingIdx = 0;
      int NearTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyLong_rangeType = this.candleSettings.bodyLong.rangeType;
      int BodyLong_avgPeriod = this.candleSettings.bodyLong.avgPeriod;
      double BodyLong_factor = this.candleSettings.bodyLong.factor;
      int BodyShort_rangeType = this.candleSettings.bodyShort.rangeType;
      int BodyShort_avgPeriod = this.candleSettings.bodyShort.avgPeriod;
      double BodyShort_factor = this.candleSettings.bodyShort.factor;
      int Near_rangeType = this.candleSettings.near.rangeType;
      int Near_avgPeriod = this.candleSettings.near.avgPeriod;
      double Near_factor = this.candleSettings.near.factor;
      int ShadowVeryShort_rangeType = this.candleSettings.shadowVeryShort.rangeType;
      int ShadowVeryShort_avgPeriod = this.candleSettings.shadowVeryShort.avgPeriod;
      double ShadowVeryShort_factor = this.candleSettings.shadowVeryShort.factor;
      lookbackTotal = cdlstalledpatternLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      BodyLongPeriodTotal[2] = 0;
      BodyLongPeriodTotal[1] = 0;
      BodyLongPeriodTotal[0] = 0;
      BodyLongTrailingIdx = (startIdx-BodyLong_avgPeriod);
      BodyShortPeriodTotal = 0;
      BodyShortTrailingIdx = (startIdx-BodyShort_avgPeriod);
      ShadowVeryShortPeriodTotal = 0;
      ShadowVeryShortTrailingIdx = (startIdx-ShadowVeryShort_avgPeriod);
      NearPeriodTotal[2] = 0;
      NearPeriodTotal[1] = 0;
      NearPeriodTotal[0] = 0;
      NearTrailingIdx = (startIdx-Near_avgPeriod);
      i = BodyLongTrailingIdx;
      while( (i<startIdx) ) {
         BodyLongPeriodTotal[2] = (BodyLongPeriodTotal[2]+((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0))));
         BodyLongPeriodTotal[1] = (BodyLongPeriodTotal[1]+((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0))));
         i += 1;
      }
      i = BodyShortTrailingIdx;
      while( (i<startIdx) ) {
         BodyShortPeriodTotal += ((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = ShadowVeryShortTrailingIdx;
      while( (i<startIdx) ) {
         ShadowVeryShortPeriodTotal += ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)));
         i += 1;
      }
      i = NearTrailingIdx;
      while( (i<startIdx) ) {
         NearPeriodTotal[2] = (NearPeriodTotal[2]+((Near_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((Near_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((Near_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0))));
         NearPeriodTotal[1] = (NearPeriodTotal[1]+((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0))));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( (((((((((((((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==1)&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==1))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==1))&&(inClose[i]>inClose[(i-1)]))&&(inClose[(i-1)]>inClose[(i-2)]))&&(Math.abs((inClose[(i-2)]-inOpen[(i-2)]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyLongPeriodTotal[2] / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyLongPeriodTotal[1] / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))))&&((inHigh[(i-1)]-(((inClose[(i-1)]>=inOpen[(i-1)])) ? (inClose[(i-1)]) : (inOpen[(i-1)])))<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))&&(inOpen[(i-1)]>inOpen[(i-2)]))&&(inOpen[(i-1)]<=(inClose[(i-2)]+((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal[2] / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((Near_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((Near_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0)))))))&&(Math.abs((inClose[i]-inOpen[i]))<((BodyShort_factor * (((BodyShort_avgPeriod != 0) ? (BodyShortPeriodTotal / BodyShort_avgPeriod) : ((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((BodyShort_rangeType == 2) ? 2.0 : 1.0))))))&&(inOpen[i]>=((inClose[(i-1)]-Math.abs((inClose[i]-inOpen[i])))-((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal[1] / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0))))))) ) {
            outInteger[outIdx++] = (0-100);
         } else {
            outInteger[outIdx++] = 0;
         }
         for( totIdx = 2; (totIdx>=1); totIdx -= 1 ) {
            BodyLongPeriodTotal[totIdx] = (BodyLongPeriodTotal[totIdx]+(((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((BodyLong_rangeType == 0) ? (Math.abs(inClose[(BodyLongTrailingIdx-totIdx)] - inOpen[(BodyLongTrailingIdx-totIdx)])) : ((BodyLong_rangeType == 1) ? (inHigh[(BodyLongTrailingIdx-totIdx)] - inLow[(BodyLongTrailingIdx-totIdx)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(BodyLongTrailingIdx-totIdx)] - inLow[(BodyLongTrailingIdx-totIdx)]) - Math.abs(inClose[(BodyLongTrailingIdx-totIdx)] - inOpen[(BodyLongTrailingIdx-totIdx)])) : 0.0)))));
            NearPeriodTotal[totIdx] = (NearPeriodTotal[totIdx]+(((Near_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((Near_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((Near_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((Near_rangeType == 0) ? (Math.abs(inClose[(NearTrailingIdx-totIdx)] - inOpen[(NearTrailingIdx-totIdx)])) : ((Near_rangeType == 1) ? (inHigh[(NearTrailingIdx-totIdx)] - inLow[(NearTrailingIdx-totIdx)]) : ((Near_rangeType == 2) ? ((inHigh[(NearTrailingIdx-totIdx)] - inLow[(NearTrailingIdx-totIdx)]) - Math.abs(inClose[(NearTrailingIdx-totIdx)] - inOpen[(NearTrailingIdx-totIdx)])) : 0.0)))));
         }
         BodyShortPeriodTotal += (((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((BodyShort_rangeType == 0) ? (Math.abs(inClose[BodyShortTrailingIdx] - inOpen[BodyShortTrailingIdx])) : ((BodyShort_rangeType == 1) ? (inHigh[BodyShortTrailingIdx] - inLow[BodyShortTrailingIdx]) : ((BodyShort_rangeType == 2) ? ((inHigh[BodyShortTrailingIdx] - inLow[BodyShortTrailingIdx]) - Math.abs(inClose[BodyShortTrailingIdx] - inOpen[BodyShortTrailingIdx])) : 0.0))));
         ShadowVeryShortPeriodTotal += (((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))-((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(ShadowVeryShortTrailingIdx-1)] - inOpen[(ShadowVeryShortTrailingIdx-1)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(ShadowVeryShortTrailingIdx-1)] - inLow[(ShadowVeryShortTrailingIdx-1)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(ShadowVeryShortTrailingIdx-1)] - inLow[(ShadowVeryShortTrailingIdx-1)]) - Math.abs(inClose[(ShadowVeryShortTrailingIdx-1)] - inOpen[(ShadowVeryShortTrailingIdx-1)])) : 0.0))));
         i += 1;
         BodyLongTrailingIdx += 1;
         BodyShortTrailingIdx += 1;
         ShadowVeryShortTrailingIdx += 1;
         NearTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdlstalledpattern( int startIdx,
                                     int endIdx,
                                     float inOpen[],
                                     float inHigh[],
                                     float inLow[],
                                     float inClose[],
                                     MInteger outBegIdx,
                                     MInteger outNBElement,
                                     int outInteger[] )
   {
      double[] BodyLongPeriodTotal = new double[3];
      double[] NearPeriodTotal = new double[3];
      double BodyShortPeriodTotal = 0;
      double ShadowVeryShortPeriodTotal = 0;
      int i = 0;
      int outIdx = 0;
      int totIdx = 0;
      int BodyLongTrailingIdx = 0;
      int BodyShortTrailingIdx = 0;
      int ShadowVeryShortTrailingIdx = 0;
      int NearTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyLong_rangeType = this.candleSettings.bodyLong.rangeType;
      int BodyLong_avgPeriod = this.candleSettings.bodyLong.avgPeriod;
      double BodyLong_factor = this.candleSettings.bodyLong.factor;
      int BodyShort_rangeType = this.candleSettings.bodyShort.rangeType;
      int BodyShort_avgPeriod = this.candleSettings.bodyShort.avgPeriod;
      double BodyShort_factor = this.candleSettings.bodyShort.factor;
      int Near_rangeType = this.candleSettings.near.rangeType;
      int Near_avgPeriod = this.candleSettings.near.avgPeriod;
      double Near_factor = this.candleSettings.near.factor;
      int ShadowVeryShort_rangeType = this.candleSettings.shadowVeryShort.rangeType;
      int ShadowVeryShort_avgPeriod = this.candleSettings.shadowVeryShort.avgPeriod;
      double ShadowVeryShort_factor = this.candleSettings.shadowVeryShort.factor;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackTotal = cdlstalledpatternLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      BodyLongPeriodTotal[2] = 0;
      BodyLongPeriodTotal[1] = 0;
      BodyLongPeriodTotal[0] = 0;
      BodyLongTrailingIdx = (startIdx-BodyLong_avgPeriod);
      BodyShortPeriodTotal = 0;
      BodyShortTrailingIdx = (startIdx-BodyShort_avgPeriod);
      ShadowVeryShortPeriodTotal = 0;
      ShadowVeryShortTrailingIdx = (startIdx-ShadowVeryShort_avgPeriod);
      NearPeriodTotal[2] = 0;
      NearPeriodTotal[1] = 0;
      NearPeriodTotal[0] = 0;
      NearTrailingIdx = (startIdx-Near_avgPeriod);
      i = BodyLongTrailingIdx;
      while( (i<startIdx) ) {
         BodyLongPeriodTotal[2] = (BodyLongPeriodTotal[2]+((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0))));
         BodyLongPeriodTotal[1] = (BodyLongPeriodTotal[1]+((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0))));
         i += 1;
      }
      i = BodyShortTrailingIdx;
      while( (i<startIdx) ) {
         BodyShortPeriodTotal += ((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = ShadowVeryShortTrailingIdx;
      while( (i<startIdx) ) {
         ShadowVeryShortPeriodTotal += ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)));
         i += 1;
      }
      i = NearTrailingIdx;
      while( (i<startIdx) ) {
         NearPeriodTotal[2] = (NearPeriodTotal[2]+((Near_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((Near_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((Near_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0))));
         NearPeriodTotal[1] = (NearPeriodTotal[1]+((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0))));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( (((((((((((((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==1)&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==1))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==1))&&(inClose[i]>inClose[(i-1)]))&&(inClose[(i-1)]>inClose[(i-2)]))&&(Math.abs((inClose[(i-2)]-inOpen[(i-2)]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyLongPeriodTotal[2] / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyLongPeriodTotal[1] / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))))&&((inHigh[(i-1)]-(((inClose[(i-1)]>=inOpen[(i-1)])) ? (inClose[(i-1)]) : (inOpen[(i-1)])))<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))&&(inOpen[(i-1)]>inOpen[(i-2)]))&&(inOpen[(i-1)]<=(inClose[(i-2)]+((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal[2] / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((Near_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((Near_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0)))))))&&(Math.abs((inClose[i]-inOpen[i]))<((BodyShort_factor * (((BodyShort_avgPeriod != 0) ? (BodyShortPeriodTotal / BodyShort_avgPeriod) : ((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((BodyShort_rangeType == 2) ? 2.0 : 1.0))))))&&(inOpen[i]>=((inClose[(i-1)]-Math.abs((inClose[i]-inOpen[i])))-((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal[1] / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0))))))) ) {
            outInteger[outIdx++] = (0-100);
         } else {
            outInteger[outIdx++] = 0;
         }
         for( totIdx = 2; (totIdx>=1); totIdx -= 1 ) {
            BodyLongPeriodTotal[totIdx] = (BodyLongPeriodTotal[totIdx]+(((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((BodyLong_rangeType == 0) ? (Math.abs(inClose[(BodyLongTrailingIdx-totIdx)] - inOpen[(BodyLongTrailingIdx-totIdx)])) : ((BodyLong_rangeType == 1) ? (inHigh[(BodyLongTrailingIdx-totIdx)] - inLow[(BodyLongTrailingIdx-totIdx)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(BodyLongTrailingIdx-totIdx)] - inLow[(BodyLongTrailingIdx-totIdx)]) - Math.abs(inClose[(BodyLongTrailingIdx-totIdx)] - inOpen[(BodyLongTrailingIdx-totIdx)])) : 0.0)))));
            NearPeriodTotal[totIdx] = (NearPeriodTotal[totIdx]+(((Near_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((Near_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((Near_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((Near_rangeType == 0) ? (Math.abs(inClose[(NearTrailingIdx-totIdx)] - inOpen[(NearTrailingIdx-totIdx)])) : ((Near_rangeType == 1) ? (inHigh[(NearTrailingIdx-totIdx)] - inLow[(NearTrailingIdx-totIdx)]) : ((Near_rangeType == 2) ? ((inHigh[(NearTrailingIdx-totIdx)] - inLow[(NearTrailingIdx-totIdx)]) - Math.abs(inClose[(NearTrailingIdx-totIdx)] - inOpen[(NearTrailingIdx-totIdx)])) : 0.0)))));
         }
         BodyShortPeriodTotal += (((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((BodyShort_rangeType == 0) ? (Math.abs(inClose[BodyShortTrailingIdx] - inOpen[BodyShortTrailingIdx])) : ((BodyShort_rangeType == 1) ? (inHigh[BodyShortTrailingIdx] - inLow[BodyShortTrailingIdx]) : ((BodyShort_rangeType == 2) ? ((inHigh[BodyShortTrailingIdx] - inLow[BodyShortTrailingIdx]) - Math.abs(inClose[BodyShortTrailingIdx] - inOpen[BodyShortTrailingIdx])) : 0.0))));
         ShadowVeryShortPeriodTotal += (((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))-((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(ShadowVeryShortTrailingIdx-1)] - inOpen[(ShadowVeryShortTrailingIdx-1)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(ShadowVeryShortTrailingIdx-1)] - inLow[(ShadowVeryShortTrailingIdx-1)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(ShadowVeryShortTrailingIdx-1)] - inLow[(ShadowVeryShortTrailingIdx-1)]) - Math.abs(inClose[(ShadowVeryShortTrailingIdx-1)] - inOpen[(ShadowVeryShortTrailingIdx-1)])) : 0.0))));
         i += 1;
         BodyLongTrailingIdx += 1;
         BodyShortTrailingIdx += 1;
         ShadowVeryShortTrailingIdx += 1;
         NearTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdlstalledpatternLogic( int startIdx,
                                          int endIdx,
                                          float inOpen[],
                                          float inHigh[],
                                          float inLow[],
                                          float inClose[],
                                          MInteger outBegIdx,
                                          MInteger outNBElement,
                                          int outInteger[] )
   {
      double[] BodyLongPeriodTotal = new double[3];
      double[] NearPeriodTotal = new double[3];
      double BodyShortPeriodTotal = 0;
      double ShadowVeryShortPeriodTotal = 0;
      int i = 0;
      int outIdx = 0;
      int totIdx = 0;
      int BodyLongTrailingIdx = 0;
      int BodyShortTrailingIdx = 0;
      int ShadowVeryShortTrailingIdx = 0;
      int NearTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyLong_rangeType = this.candleSettings.bodyLong.rangeType;
      int BodyLong_avgPeriod = this.candleSettings.bodyLong.avgPeriod;
      double BodyLong_factor = this.candleSettings.bodyLong.factor;
      int BodyShort_rangeType = this.candleSettings.bodyShort.rangeType;
      int BodyShort_avgPeriod = this.candleSettings.bodyShort.avgPeriod;
      double BodyShort_factor = this.candleSettings.bodyShort.factor;
      int Near_rangeType = this.candleSettings.near.rangeType;
      int Near_avgPeriod = this.candleSettings.near.avgPeriod;
      double Near_factor = this.candleSettings.near.factor;
      int ShadowVeryShort_rangeType = this.candleSettings.shadowVeryShort.rangeType;
      int ShadowVeryShort_avgPeriod = this.candleSettings.shadowVeryShort.avgPeriod;
      double ShadowVeryShort_factor = this.candleSettings.shadowVeryShort.factor;
      lookbackTotal = cdlstalledpatternLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      BodyLongPeriodTotal[2] = 0;
      BodyLongPeriodTotal[1] = 0;
      BodyLongPeriodTotal[0] = 0;
      BodyLongTrailingIdx = (startIdx-BodyLong_avgPeriod);
      BodyShortPeriodTotal = 0;
      BodyShortTrailingIdx = (startIdx-BodyShort_avgPeriod);
      ShadowVeryShortPeriodTotal = 0;
      ShadowVeryShortTrailingIdx = (startIdx-ShadowVeryShort_avgPeriod);
      NearPeriodTotal[2] = 0;
      NearPeriodTotal[1] = 0;
      NearPeriodTotal[0] = 0;
      NearTrailingIdx = (startIdx-Near_avgPeriod);
      i = BodyLongTrailingIdx;
      while( (i<startIdx) ) {
         BodyLongPeriodTotal[2] = (BodyLongPeriodTotal[2]+((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0))));
         BodyLongPeriodTotal[1] = (BodyLongPeriodTotal[1]+((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0))));
         i += 1;
      }
      i = BodyShortTrailingIdx;
      while( (i<startIdx) ) {
         BodyShortPeriodTotal += ((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = ShadowVeryShortTrailingIdx;
      while( (i<startIdx) ) {
         ShadowVeryShortPeriodTotal += ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)));
         i += 1;
      }
      i = NearTrailingIdx;
      while( (i<startIdx) ) {
         NearPeriodTotal[2] = (NearPeriodTotal[2]+((Near_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((Near_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((Near_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0))));
         NearPeriodTotal[1] = (NearPeriodTotal[1]+((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0))));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( (((((((((((((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==1)&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==1))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==1))&&(inClose[i]>inClose[(i-1)]))&&(inClose[(i-1)]>inClose[(i-2)]))&&(Math.abs((inClose[(i-2)]-inOpen[(i-2)]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyLongPeriodTotal[2] / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyLongPeriodTotal[1] / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))))&&((inHigh[(i-1)]-(((inClose[(i-1)]>=inOpen[(i-1)])) ? (inClose[(i-1)]) : (inOpen[(i-1)])))<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))&&(inOpen[(i-1)]>inOpen[(i-2)]))&&(inOpen[(i-1)]<=(inClose[(i-2)]+((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal[2] / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((Near_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((Near_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0)))))))&&(Math.abs((inClose[i]-inOpen[i]))<((BodyShort_factor * (((BodyShort_avgPeriod != 0) ? (BodyShortPeriodTotal / BodyShort_avgPeriod) : ((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((BodyShort_rangeType == 2) ? 2.0 : 1.0))))))&&(inOpen[i]>=((inClose[(i-1)]-Math.abs((inClose[i]-inOpen[i])))-((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal[1] / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0))))))) ) {
            outInteger[outIdx++] = (0-100);
         } else {
            outInteger[outIdx++] = 0;
         }
         for( totIdx = 2; (totIdx>=1); totIdx -= 1 ) {
            BodyLongPeriodTotal[totIdx] = (BodyLongPeriodTotal[totIdx]+(((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((BodyLong_rangeType == 0) ? (Math.abs(inClose[(BodyLongTrailingIdx-totIdx)] - inOpen[(BodyLongTrailingIdx-totIdx)])) : ((BodyLong_rangeType == 1) ? (inHigh[(BodyLongTrailingIdx-totIdx)] - inLow[(BodyLongTrailingIdx-totIdx)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(BodyLongTrailingIdx-totIdx)] - inLow[(BodyLongTrailingIdx-totIdx)]) - Math.abs(inClose[(BodyLongTrailingIdx-totIdx)] - inOpen[(BodyLongTrailingIdx-totIdx)])) : 0.0)))));
            NearPeriodTotal[totIdx] = (NearPeriodTotal[totIdx]+(((Near_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((Near_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((Near_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((Near_rangeType == 0) ? (Math.abs(inClose[(NearTrailingIdx-totIdx)] - inOpen[(NearTrailingIdx-totIdx)])) : ((Near_rangeType == 1) ? (inHigh[(NearTrailingIdx-totIdx)] - inLow[(NearTrailingIdx-totIdx)]) : ((Near_rangeType == 2) ? ((inHigh[(NearTrailingIdx-totIdx)] - inLow[(NearTrailingIdx-totIdx)]) - Math.abs(inClose[(NearTrailingIdx-totIdx)] - inOpen[(NearTrailingIdx-totIdx)])) : 0.0)))));
         }
         BodyShortPeriodTotal += (((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((BodyShort_rangeType == 0) ? (Math.abs(inClose[BodyShortTrailingIdx] - inOpen[BodyShortTrailingIdx])) : ((BodyShort_rangeType == 1) ? (inHigh[BodyShortTrailingIdx] - inLow[BodyShortTrailingIdx]) : ((BodyShort_rangeType == 2) ? ((inHigh[BodyShortTrailingIdx] - inLow[BodyShortTrailingIdx]) - Math.abs(inClose[BodyShortTrailingIdx] - inOpen[BodyShortTrailingIdx])) : 0.0))));
         ShadowVeryShortPeriodTotal += (((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))-((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[(ShadowVeryShortTrailingIdx-1)] - inOpen[(ShadowVeryShortTrailingIdx-1)])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[(ShadowVeryShortTrailingIdx-1)] - inLow[(ShadowVeryShortTrailingIdx-1)]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[(ShadowVeryShortTrailingIdx-1)] - inLow[(ShadowVeryShortTrailingIdx-1)]) - Math.abs(inClose[(ShadowVeryShortTrailingIdx-1)] - inOpen[(ShadowVeryShortTrailingIdx-1)])) : 0.0))));
         i += 1;
         BodyLongTrailingIdx += 1;
         BodyShortTrailingIdx += 1;
         ShadowVeryShortTrailingIdx += 1;
         NearTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
