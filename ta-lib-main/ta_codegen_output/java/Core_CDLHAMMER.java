/* Generated */
   public int cdlhammerLookback( )
   {
      int BodyShort_rangeType = this.candleSettings.bodyShort.rangeType;
      int BodyShort_avgPeriod = this.candleSettings.bodyShort.avgPeriod;
      double BodyShort_factor = this.candleSettings.bodyShort.factor;
      int Near_rangeType = this.candleSettings.near.rangeType;
      int Near_avgPeriod = this.candleSettings.near.avgPeriod;
      double Near_factor = this.candleSettings.near.factor;
      int ShadowLong_rangeType = this.candleSettings.shadowLong.rangeType;
      int ShadowLong_avgPeriod = this.candleSettings.shadowLong.avgPeriod;
      double ShadowLong_factor = this.candleSettings.shadowLong.factor;
      int ShadowVeryShort_rangeType = this.candleSettings.shadowVeryShort.rangeType;
      int ShadowVeryShort_avgPeriod = this.candleSettings.shadowVeryShort.avgPeriod;
      double ShadowVeryShort_factor = this.candleSettings.shadowVeryShort.factor;
      return (Math.max(Math.max(Math.max(BodyShort_avgPeriod, ShadowLong_avgPeriod), ShadowVeryShort_avgPeriod), Near_avgPeriod)+1) ;

   }
   public RetCode cdlhammer( int startIdx,
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
      double ShadowLongPeriodTotal = 0;
      double ShadowVeryShortPeriodTotal = 0;
      double NearPeriodTotal = 0;
      int i = 0;
      int outIdx = 0;
      int BodyTrailingIdx = 0;
      int ShadowLongTrailingIdx = 0;
      int ShadowVeryShortTrailingIdx = 0;
      int NearTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyShort_rangeType = this.candleSettings.bodyShort.rangeType;
      int BodyShort_avgPeriod = this.candleSettings.bodyShort.avgPeriod;
      double BodyShort_factor = this.candleSettings.bodyShort.factor;
      int Near_rangeType = this.candleSettings.near.rangeType;
      int Near_avgPeriod = this.candleSettings.near.avgPeriod;
      double Near_factor = this.candleSettings.near.factor;
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
      lookbackTotal = cdlhammerLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      BodyPeriodTotal = 0;
      BodyTrailingIdx = (startIdx-BodyShort_avgPeriod);
      ShadowLongPeriodTotal = 0;
      ShadowLongTrailingIdx = (startIdx-ShadowLong_avgPeriod);
      ShadowVeryShortPeriodTotal = 0;
      ShadowVeryShortTrailingIdx = (startIdx-ShadowVeryShort_avgPeriod);
      NearPeriodTotal = 0;
      NearTrailingIdx = ((startIdx-1)-Near_avgPeriod);
      i = BodyTrailingIdx;
      while( (i<startIdx) ) {
         BodyPeriodTotal += ((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = ShadowLongTrailingIdx;
      while( (i<startIdx) ) {
         ShadowLongPeriodTotal += ((ShadowLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = ShadowVeryShortTrailingIdx;
      while( (i<startIdx) ) {
         ShadowVeryShortPeriodTotal += ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = NearTrailingIdx;
      while( (i<(startIdx-1)) ) {
         NearPeriodTotal += ((Near_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((Near_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((Near_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( ((((Math.abs((inClose[i]-inOpen[i]))<((BodyShort_factor * (((BodyShort_avgPeriod != 0) ? (BodyPeriodTotal / BodyShort_avgPeriod) : ((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((BodyShort_rangeType == 2) ? 2.0 : 1.0)))))&&(((((inClose[i]>=inOpen[i])) ? (inOpen[i]) : (inClose[i]))-inLow[i])>((ShadowLong_factor * (((ShadowLong_avgPeriod != 0) ? (ShadowLongPeriodTotal / ShadowLong_avgPeriod) : ((ShadowLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowLong_rangeType == 2) ? 2.0 : 1.0))))))&&((inHigh[i]-(((inClose[i]>=inOpen[i])) ? (inClose[i]) : (inOpen[i])))<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.min(inClose[i], inOpen[i])<=(inLow[(i-1)]+((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0))))))) ) {
            outInteger[outIdx++] = 100;
         } else {
            outInteger[outIdx++] = 0;
         }
         BodyPeriodTotal += (((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((BodyShort_rangeType == 0) ? (Math.abs(inClose[BodyTrailingIdx] - inOpen[BodyTrailingIdx])) : ((BodyShort_rangeType == 1) ? (inHigh[BodyTrailingIdx] - inLow[BodyTrailingIdx]) : ((BodyShort_rangeType == 2) ? ((inHigh[BodyTrailingIdx] - inLow[BodyTrailingIdx]) - Math.abs(inClose[BodyTrailingIdx] - inOpen[BodyTrailingIdx])) : 0.0))));
         ShadowLongPeriodTotal += (((ShadowLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((ShadowLong_rangeType == 0) ? (Math.abs(inClose[ShadowLongTrailingIdx] - inOpen[ShadowLongTrailingIdx])) : ((ShadowLong_rangeType == 1) ? (inHigh[ShadowLongTrailingIdx] - inLow[ShadowLongTrailingIdx]) : ((ShadowLong_rangeType == 2) ? ((inHigh[ShadowLongTrailingIdx] - inLow[ShadowLongTrailingIdx]) - Math.abs(inClose[ShadowLongTrailingIdx] - inOpen[ShadowLongTrailingIdx])) : 0.0))));
         ShadowVeryShortPeriodTotal += (((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[ShadowVeryShortTrailingIdx] - inOpen[ShadowVeryShortTrailingIdx])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[ShadowVeryShortTrailingIdx] - inLow[ShadowVeryShortTrailingIdx]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[ShadowVeryShortTrailingIdx] - inLow[ShadowVeryShortTrailingIdx]) - Math.abs(inClose[ShadowVeryShortTrailingIdx] - inOpen[ShadowVeryShortTrailingIdx])) : 0.0))));
         NearPeriodTotal += (((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))-((Near_rangeType == 0) ? (Math.abs(inClose[NearTrailingIdx] - inOpen[NearTrailingIdx])) : ((Near_rangeType == 1) ? (inHigh[NearTrailingIdx] - inLow[NearTrailingIdx]) : ((Near_rangeType == 2) ? ((inHigh[NearTrailingIdx] - inLow[NearTrailingIdx]) - Math.abs(inClose[NearTrailingIdx] - inOpen[NearTrailingIdx])) : 0.0))));
         i += 1;
         BodyTrailingIdx += 1;
         ShadowLongTrailingIdx += 1;
         ShadowVeryShortTrailingIdx += 1;
         NearTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdlhammerLogic( int startIdx,
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
      double ShadowLongPeriodTotal = 0;
      double ShadowVeryShortPeriodTotal = 0;
      double NearPeriodTotal = 0;
      int i = 0;
      int outIdx = 0;
      int BodyTrailingIdx = 0;
      int ShadowLongTrailingIdx = 0;
      int ShadowVeryShortTrailingIdx = 0;
      int NearTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyShort_rangeType = this.candleSettings.bodyShort.rangeType;
      int BodyShort_avgPeriod = this.candleSettings.bodyShort.avgPeriod;
      double BodyShort_factor = this.candleSettings.bodyShort.factor;
      int Near_rangeType = this.candleSettings.near.rangeType;
      int Near_avgPeriod = this.candleSettings.near.avgPeriod;
      double Near_factor = this.candleSettings.near.factor;
      int ShadowLong_rangeType = this.candleSettings.shadowLong.rangeType;
      int ShadowLong_avgPeriod = this.candleSettings.shadowLong.avgPeriod;
      double ShadowLong_factor = this.candleSettings.shadowLong.factor;
      int ShadowVeryShort_rangeType = this.candleSettings.shadowVeryShort.rangeType;
      int ShadowVeryShort_avgPeriod = this.candleSettings.shadowVeryShort.avgPeriod;
      double ShadowVeryShort_factor = this.candleSettings.shadowVeryShort.factor;
      lookbackTotal = cdlhammerLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      BodyPeriodTotal = 0;
      BodyTrailingIdx = (startIdx-BodyShort_avgPeriod);
      ShadowLongPeriodTotal = 0;
      ShadowLongTrailingIdx = (startIdx-ShadowLong_avgPeriod);
      ShadowVeryShortPeriodTotal = 0;
      ShadowVeryShortTrailingIdx = (startIdx-ShadowVeryShort_avgPeriod);
      NearPeriodTotal = 0;
      NearTrailingIdx = ((startIdx-1)-Near_avgPeriod);
      i = BodyTrailingIdx;
      while( (i<startIdx) ) {
         BodyPeriodTotal += ((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = ShadowLongTrailingIdx;
      while( (i<startIdx) ) {
         ShadowLongPeriodTotal += ((ShadowLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = ShadowVeryShortTrailingIdx;
      while( (i<startIdx) ) {
         ShadowVeryShortPeriodTotal += ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = NearTrailingIdx;
      while( (i<(startIdx-1)) ) {
         NearPeriodTotal += ((Near_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((Near_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((Near_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( ((((Math.abs((inClose[i]-inOpen[i]))<((BodyShort_factor * (((BodyShort_avgPeriod != 0) ? (BodyPeriodTotal / BodyShort_avgPeriod) : ((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((BodyShort_rangeType == 2) ? 2.0 : 1.0)))))&&(((((inClose[i]>=inOpen[i])) ? (inOpen[i]) : (inClose[i]))-inLow[i])>((ShadowLong_factor * (((ShadowLong_avgPeriod != 0) ? (ShadowLongPeriodTotal / ShadowLong_avgPeriod) : ((ShadowLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowLong_rangeType == 2) ? 2.0 : 1.0))))))&&((inHigh[i]-(((inClose[i]>=inOpen[i])) ? (inClose[i]) : (inOpen[i])))<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.min(inClose[i], inOpen[i])<=(inLow[(i-1)]+((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0))))))) ) {
            outInteger[outIdx++] = 100;
         } else {
            outInteger[outIdx++] = 0;
         }
         BodyPeriodTotal += (((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((BodyShort_rangeType == 0) ? (Math.abs(inClose[BodyTrailingIdx] - inOpen[BodyTrailingIdx])) : ((BodyShort_rangeType == 1) ? (inHigh[BodyTrailingIdx] - inLow[BodyTrailingIdx]) : ((BodyShort_rangeType == 2) ? ((inHigh[BodyTrailingIdx] - inLow[BodyTrailingIdx]) - Math.abs(inClose[BodyTrailingIdx] - inOpen[BodyTrailingIdx])) : 0.0))));
         ShadowLongPeriodTotal += (((ShadowLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((ShadowLong_rangeType == 0) ? (Math.abs(inClose[ShadowLongTrailingIdx] - inOpen[ShadowLongTrailingIdx])) : ((ShadowLong_rangeType == 1) ? (inHigh[ShadowLongTrailingIdx] - inLow[ShadowLongTrailingIdx]) : ((ShadowLong_rangeType == 2) ? ((inHigh[ShadowLongTrailingIdx] - inLow[ShadowLongTrailingIdx]) - Math.abs(inClose[ShadowLongTrailingIdx] - inOpen[ShadowLongTrailingIdx])) : 0.0))));
         ShadowVeryShortPeriodTotal += (((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[ShadowVeryShortTrailingIdx] - inOpen[ShadowVeryShortTrailingIdx])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[ShadowVeryShortTrailingIdx] - inLow[ShadowVeryShortTrailingIdx]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[ShadowVeryShortTrailingIdx] - inLow[ShadowVeryShortTrailingIdx]) - Math.abs(inClose[ShadowVeryShortTrailingIdx] - inOpen[ShadowVeryShortTrailingIdx])) : 0.0))));
         NearPeriodTotal += (((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))-((Near_rangeType == 0) ? (Math.abs(inClose[NearTrailingIdx] - inOpen[NearTrailingIdx])) : ((Near_rangeType == 1) ? (inHigh[NearTrailingIdx] - inLow[NearTrailingIdx]) : ((Near_rangeType == 2) ? ((inHigh[NearTrailingIdx] - inLow[NearTrailingIdx]) - Math.abs(inClose[NearTrailingIdx] - inOpen[NearTrailingIdx])) : 0.0))));
         i += 1;
         BodyTrailingIdx += 1;
         ShadowLongTrailingIdx += 1;
         ShadowVeryShortTrailingIdx += 1;
         NearTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdlhammer( int startIdx,
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
      double ShadowLongPeriodTotal = 0;
      double ShadowVeryShortPeriodTotal = 0;
      double NearPeriodTotal = 0;
      int i = 0;
      int outIdx = 0;
      int BodyTrailingIdx = 0;
      int ShadowLongTrailingIdx = 0;
      int ShadowVeryShortTrailingIdx = 0;
      int NearTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyShort_rangeType = this.candleSettings.bodyShort.rangeType;
      int BodyShort_avgPeriod = this.candleSettings.bodyShort.avgPeriod;
      double BodyShort_factor = this.candleSettings.bodyShort.factor;
      int Near_rangeType = this.candleSettings.near.rangeType;
      int Near_avgPeriod = this.candleSettings.near.avgPeriod;
      double Near_factor = this.candleSettings.near.factor;
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
      lookbackTotal = cdlhammerLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      BodyPeriodTotal = 0;
      BodyTrailingIdx = (startIdx-BodyShort_avgPeriod);
      ShadowLongPeriodTotal = 0;
      ShadowLongTrailingIdx = (startIdx-ShadowLong_avgPeriod);
      ShadowVeryShortPeriodTotal = 0;
      ShadowVeryShortTrailingIdx = (startIdx-ShadowVeryShort_avgPeriod);
      NearPeriodTotal = 0;
      NearTrailingIdx = ((startIdx-1)-Near_avgPeriod);
      i = BodyTrailingIdx;
      while( (i<startIdx) ) {
         BodyPeriodTotal += ((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = ShadowLongTrailingIdx;
      while( (i<startIdx) ) {
         ShadowLongPeriodTotal += ((ShadowLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = ShadowVeryShortTrailingIdx;
      while( (i<startIdx) ) {
         ShadowVeryShortPeriodTotal += ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = NearTrailingIdx;
      while( (i<(startIdx-1)) ) {
         NearPeriodTotal += ((Near_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((Near_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((Near_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( ((((Math.abs((inClose[i]-inOpen[i]))<((BodyShort_factor * (((BodyShort_avgPeriod != 0) ? (BodyPeriodTotal / BodyShort_avgPeriod) : ((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((BodyShort_rangeType == 2) ? 2.0 : 1.0)))))&&(((((inClose[i]>=inOpen[i])) ? (inOpen[i]) : (inClose[i]))-inLow[i])>((ShadowLong_factor * (((ShadowLong_avgPeriod != 0) ? (ShadowLongPeriodTotal / ShadowLong_avgPeriod) : ((ShadowLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowLong_rangeType == 2) ? 2.0 : 1.0))))))&&((inHigh[i]-(((inClose[i]>=inOpen[i])) ? (inClose[i]) : (inOpen[i])))<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.min(inClose[i], inOpen[i])<=(inLow[(i-1)]+((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0))))))) ) {
            outInteger[outIdx++] = 100;
         } else {
            outInteger[outIdx++] = 0;
         }
         BodyPeriodTotal += (((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((BodyShort_rangeType == 0) ? (Math.abs(inClose[BodyTrailingIdx] - inOpen[BodyTrailingIdx])) : ((BodyShort_rangeType == 1) ? (inHigh[BodyTrailingIdx] - inLow[BodyTrailingIdx]) : ((BodyShort_rangeType == 2) ? ((inHigh[BodyTrailingIdx] - inLow[BodyTrailingIdx]) - Math.abs(inClose[BodyTrailingIdx] - inOpen[BodyTrailingIdx])) : 0.0))));
         ShadowLongPeriodTotal += (((ShadowLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((ShadowLong_rangeType == 0) ? (Math.abs(inClose[ShadowLongTrailingIdx] - inOpen[ShadowLongTrailingIdx])) : ((ShadowLong_rangeType == 1) ? (inHigh[ShadowLongTrailingIdx] - inLow[ShadowLongTrailingIdx]) : ((ShadowLong_rangeType == 2) ? ((inHigh[ShadowLongTrailingIdx] - inLow[ShadowLongTrailingIdx]) - Math.abs(inClose[ShadowLongTrailingIdx] - inOpen[ShadowLongTrailingIdx])) : 0.0))));
         ShadowVeryShortPeriodTotal += (((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[ShadowVeryShortTrailingIdx] - inOpen[ShadowVeryShortTrailingIdx])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[ShadowVeryShortTrailingIdx] - inLow[ShadowVeryShortTrailingIdx]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[ShadowVeryShortTrailingIdx] - inLow[ShadowVeryShortTrailingIdx]) - Math.abs(inClose[ShadowVeryShortTrailingIdx] - inOpen[ShadowVeryShortTrailingIdx])) : 0.0))));
         NearPeriodTotal += (((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))-((Near_rangeType == 0) ? (Math.abs(inClose[NearTrailingIdx] - inOpen[NearTrailingIdx])) : ((Near_rangeType == 1) ? (inHigh[NearTrailingIdx] - inLow[NearTrailingIdx]) : ((Near_rangeType == 2) ? ((inHigh[NearTrailingIdx] - inLow[NearTrailingIdx]) - Math.abs(inClose[NearTrailingIdx] - inOpen[NearTrailingIdx])) : 0.0))));
         i += 1;
         BodyTrailingIdx += 1;
         ShadowLongTrailingIdx += 1;
         ShadowVeryShortTrailingIdx += 1;
         NearTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdlhammerLogic( int startIdx,
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
      double ShadowLongPeriodTotal = 0;
      double ShadowVeryShortPeriodTotal = 0;
      double NearPeriodTotal = 0;
      int i = 0;
      int outIdx = 0;
      int BodyTrailingIdx = 0;
      int ShadowLongTrailingIdx = 0;
      int ShadowVeryShortTrailingIdx = 0;
      int NearTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyShort_rangeType = this.candleSettings.bodyShort.rangeType;
      int BodyShort_avgPeriod = this.candleSettings.bodyShort.avgPeriod;
      double BodyShort_factor = this.candleSettings.bodyShort.factor;
      int Near_rangeType = this.candleSettings.near.rangeType;
      int Near_avgPeriod = this.candleSettings.near.avgPeriod;
      double Near_factor = this.candleSettings.near.factor;
      int ShadowLong_rangeType = this.candleSettings.shadowLong.rangeType;
      int ShadowLong_avgPeriod = this.candleSettings.shadowLong.avgPeriod;
      double ShadowLong_factor = this.candleSettings.shadowLong.factor;
      int ShadowVeryShort_rangeType = this.candleSettings.shadowVeryShort.rangeType;
      int ShadowVeryShort_avgPeriod = this.candleSettings.shadowVeryShort.avgPeriod;
      double ShadowVeryShort_factor = this.candleSettings.shadowVeryShort.factor;
      lookbackTotal = cdlhammerLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      BodyPeriodTotal = 0;
      BodyTrailingIdx = (startIdx-BodyShort_avgPeriod);
      ShadowLongPeriodTotal = 0;
      ShadowLongTrailingIdx = (startIdx-ShadowLong_avgPeriod);
      ShadowVeryShortPeriodTotal = 0;
      ShadowVeryShortTrailingIdx = (startIdx-ShadowVeryShort_avgPeriod);
      NearPeriodTotal = 0;
      NearTrailingIdx = ((startIdx-1)-Near_avgPeriod);
      i = BodyTrailingIdx;
      while( (i<startIdx) ) {
         BodyPeriodTotal += ((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = ShadowLongTrailingIdx;
      while( (i<startIdx) ) {
         ShadowLongPeriodTotal += ((ShadowLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = ShadowVeryShortTrailingIdx;
      while( (i<startIdx) ) {
         ShadowVeryShortPeriodTotal += ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = NearTrailingIdx;
      while( (i<(startIdx-1)) ) {
         NearPeriodTotal += ((Near_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((Near_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((Near_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( ((((Math.abs((inClose[i]-inOpen[i]))<((BodyShort_factor * (((BodyShort_avgPeriod != 0) ? (BodyPeriodTotal / BodyShort_avgPeriod) : ((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((BodyShort_rangeType == 2) ? 2.0 : 1.0)))))&&(((((inClose[i]>=inOpen[i])) ? (inOpen[i]) : (inClose[i]))-inLow[i])>((ShadowLong_factor * (((ShadowLong_avgPeriod != 0) ? (ShadowLongPeriodTotal / ShadowLong_avgPeriod) : ((ShadowLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowLong_rangeType == 2) ? 2.0 : 1.0))))))&&((inHigh[i]-(((inClose[i]>=inOpen[i])) ? (inClose[i]) : (inOpen[i])))<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.min(inClose[i], inOpen[i])<=(inLow[(i-1)]+((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0))))))) ) {
            outInteger[outIdx++] = 100;
         } else {
            outInteger[outIdx++] = 0;
         }
         BodyPeriodTotal += (((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((BodyShort_rangeType == 0) ? (Math.abs(inClose[BodyTrailingIdx] - inOpen[BodyTrailingIdx])) : ((BodyShort_rangeType == 1) ? (inHigh[BodyTrailingIdx] - inLow[BodyTrailingIdx]) : ((BodyShort_rangeType == 2) ? ((inHigh[BodyTrailingIdx] - inLow[BodyTrailingIdx]) - Math.abs(inClose[BodyTrailingIdx] - inOpen[BodyTrailingIdx])) : 0.0))));
         ShadowLongPeriodTotal += (((ShadowLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((ShadowLong_rangeType == 0) ? (Math.abs(inClose[ShadowLongTrailingIdx] - inOpen[ShadowLongTrailingIdx])) : ((ShadowLong_rangeType == 1) ? (inHigh[ShadowLongTrailingIdx] - inLow[ShadowLongTrailingIdx]) : ((ShadowLong_rangeType == 2) ? ((inHigh[ShadowLongTrailingIdx] - inLow[ShadowLongTrailingIdx]) - Math.abs(inClose[ShadowLongTrailingIdx] - inOpen[ShadowLongTrailingIdx])) : 0.0))));
         ShadowVeryShortPeriodTotal += (((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[ShadowVeryShortTrailingIdx] - inOpen[ShadowVeryShortTrailingIdx])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[ShadowVeryShortTrailingIdx] - inLow[ShadowVeryShortTrailingIdx]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[ShadowVeryShortTrailingIdx] - inLow[ShadowVeryShortTrailingIdx]) - Math.abs(inClose[ShadowVeryShortTrailingIdx] - inOpen[ShadowVeryShortTrailingIdx])) : 0.0))));
         NearPeriodTotal += (((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))-((Near_rangeType == 0) ? (Math.abs(inClose[NearTrailingIdx] - inOpen[NearTrailingIdx])) : ((Near_rangeType == 1) ? (inHigh[NearTrailingIdx] - inLow[NearTrailingIdx]) : ((Near_rangeType == 2) ? ((inHigh[NearTrailingIdx] - inLow[NearTrailingIdx]) - Math.abs(inClose[NearTrailingIdx] - inOpen[NearTrailingIdx])) : 0.0))));
         i += 1;
         BodyTrailingIdx += 1;
         ShadowLongTrailingIdx += 1;
         ShadowVeryShortTrailingIdx += 1;
         NearTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
