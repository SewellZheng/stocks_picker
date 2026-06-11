/* Generated */
   public int cdlseparatinglinesLookback( )
   {
      int BodyLong_rangeType = this.candleSettings.bodyLong.rangeType;
      int BodyLong_avgPeriod = this.candleSettings.bodyLong.avgPeriod;
      double BodyLong_factor = this.candleSettings.bodyLong.factor;
      int Equal_rangeType = this.candleSettings.equal.rangeType;
      int Equal_avgPeriod = this.candleSettings.equal.avgPeriod;
      double Equal_factor = this.candleSettings.equal.factor;
      int ShadowVeryShort_rangeType = this.candleSettings.shadowVeryShort.rangeType;
      int ShadowVeryShort_avgPeriod = this.candleSettings.shadowVeryShort.avgPeriod;
      double ShadowVeryShort_factor = this.candleSettings.shadowVeryShort.factor;
      return (Math.max(Math.max(ShadowVeryShort_avgPeriod, BodyLong_avgPeriod), Equal_avgPeriod)+1) ;

   }
   public RetCode cdlseparatinglines( int startIdx,
                                      int endIdx,
                                      double inOpen[],
                                      double inHigh[],
                                      double inLow[],
                                      double inClose[],
                                      MInteger outBegIdx,
                                      MInteger outNBElement,
                                      int outInteger[] )
   {
      double ShadowVeryShortPeriodTotal = 0;
      double BodyLongPeriodTotal = 0;
      double EqualPeriodTotal = 0;
      int i = 0;
      int outIdx = 0;
      int ShadowVeryShortTrailingIdx = 0;
      int BodyLongTrailingIdx = 0;
      int EqualTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyLong_rangeType = this.candleSettings.bodyLong.rangeType;
      int BodyLong_avgPeriod = this.candleSettings.bodyLong.avgPeriod;
      double BodyLong_factor = this.candleSettings.bodyLong.factor;
      int Equal_rangeType = this.candleSettings.equal.rangeType;
      int Equal_avgPeriod = this.candleSettings.equal.avgPeriod;
      double Equal_factor = this.candleSettings.equal.factor;
      int ShadowVeryShort_rangeType = this.candleSettings.shadowVeryShort.rangeType;
      int ShadowVeryShort_avgPeriod = this.candleSettings.shadowVeryShort.avgPeriod;
      double ShadowVeryShort_factor = this.candleSettings.shadowVeryShort.factor;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackTotal = cdlseparatinglinesLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      ShadowVeryShortPeriodTotal = 0;
      ShadowVeryShortTrailingIdx = (startIdx-ShadowVeryShort_avgPeriod);
      BodyLongPeriodTotal = 0;
      BodyLongTrailingIdx = (startIdx-BodyLong_avgPeriod);
      EqualPeriodTotal = 0;
      EqualTrailingIdx = (startIdx-Equal_avgPeriod);
      i = ShadowVeryShortTrailingIdx;
      while( (i<startIdx) ) {
         ShadowVeryShortPeriodTotal += ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = BodyLongTrailingIdx;
      while( (i<startIdx) ) {
         BodyLongPeriodTotal += ((BodyLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = EqualTrailingIdx;
      while( (i<startIdx) ) {
         EqualPeriodTotal += ((Equal_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Equal_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( ((((((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-(((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))))&&(inOpen[i]<=(inOpen[(i-1)]+((Equal_factor * (((Equal_avgPeriod != 0) ? (EqualPeriodTotal / Equal_avgPeriod) : ((Equal_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Equal_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Equal_rangeType == 2) ? 2.0 : 1.0)))))))&&(inOpen[i]>=(inOpen[(i-1)]-((Equal_factor * (((Equal_avgPeriod != 0) ? (EqualPeriodTotal / Equal_avgPeriod) : ((Equal_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Equal_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Equal_rangeType == 2) ? 2.0 : 1.0)))))))&&(Math.abs((inClose[i]-inOpen[i]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyLongPeriodTotal / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))))&&((((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==1)&&(((((inClose[i]>=inOpen[i])) ? (inOpen[i]) : (inClose[i]))-inLow[i])<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))||(((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==(0-1))&&((inHigh[i]-(((inClose[i]>=inOpen[i])) ? (inClose[i]) : (inOpen[i])))<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0)))))))) ) {
            outInteger[outIdx++] = ((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))*100);
         } else {
            outInteger[outIdx++] = 0;
         }
         ShadowVeryShortPeriodTotal += (((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[ShadowVeryShortTrailingIdx] - inOpen[ShadowVeryShortTrailingIdx])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[ShadowVeryShortTrailingIdx] - inLow[ShadowVeryShortTrailingIdx]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[ShadowVeryShortTrailingIdx] - inLow[ShadowVeryShortTrailingIdx]) - Math.abs(inClose[ShadowVeryShortTrailingIdx] - inOpen[ShadowVeryShortTrailingIdx])) : 0.0))));
         BodyLongPeriodTotal += (((BodyLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((BodyLong_rangeType == 0) ? (Math.abs(inClose[BodyLongTrailingIdx] - inOpen[BodyLongTrailingIdx])) : ((BodyLong_rangeType == 1) ? (inHigh[BodyLongTrailingIdx] - inLow[BodyLongTrailingIdx]) : ((BodyLong_rangeType == 2) ? ((inHigh[BodyLongTrailingIdx] - inLow[BodyLongTrailingIdx]) - Math.abs(inClose[BodyLongTrailingIdx] - inOpen[BodyLongTrailingIdx])) : 0.0))));
         EqualPeriodTotal += (((Equal_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Equal_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))-((Equal_rangeType == 0) ? (Math.abs(inClose[(EqualTrailingIdx-1)] - inOpen[(EqualTrailingIdx-1)])) : ((Equal_rangeType == 1) ? (inHigh[(EqualTrailingIdx-1)] - inLow[(EqualTrailingIdx-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(EqualTrailingIdx-1)] - inLow[(EqualTrailingIdx-1)]) - Math.abs(inClose[(EqualTrailingIdx-1)] - inOpen[(EqualTrailingIdx-1)])) : 0.0))));
         i += 1;
         ShadowVeryShortTrailingIdx += 1;
         BodyLongTrailingIdx += 1;
         EqualTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdlseparatinglinesLogic( int startIdx,
                                           int endIdx,
                                           double inOpen[],
                                           double inHigh[],
                                           double inLow[],
                                           double inClose[],
                                           MInteger outBegIdx,
                                           MInteger outNBElement,
                                           int outInteger[] )
   {
      double ShadowVeryShortPeriodTotal = 0;
      double BodyLongPeriodTotal = 0;
      double EqualPeriodTotal = 0;
      int i = 0;
      int outIdx = 0;
      int ShadowVeryShortTrailingIdx = 0;
      int BodyLongTrailingIdx = 0;
      int EqualTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyLong_rangeType = this.candleSettings.bodyLong.rangeType;
      int BodyLong_avgPeriod = this.candleSettings.bodyLong.avgPeriod;
      double BodyLong_factor = this.candleSettings.bodyLong.factor;
      int Equal_rangeType = this.candleSettings.equal.rangeType;
      int Equal_avgPeriod = this.candleSettings.equal.avgPeriod;
      double Equal_factor = this.candleSettings.equal.factor;
      int ShadowVeryShort_rangeType = this.candleSettings.shadowVeryShort.rangeType;
      int ShadowVeryShort_avgPeriod = this.candleSettings.shadowVeryShort.avgPeriod;
      double ShadowVeryShort_factor = this.candleSettings.shadowVeryShort.factor;
      lookbackTotal = cdlseparatinglinesLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      ShadowVeryShortPeriodTotal = 0;
      ShadowVeryShortTrailingIdx = (startIdx-ShadowVeryShort_avgPeriod);
      BodyLongPeriodTotal = 0;
      BodyLongTrailingIdx = (startIdx-BodyLong_avgPeriod);
      EqualPeriodTotal = 0;
      EqualTrailingIdx = (startIdx-Equal_avgPeriod);
      i = ShadowVeryShortTrailingIdx;
      while( (i<startIdx) ) {
         ShadowVeryShortPeriodTotal += ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = BodyLongTrailingIdx;
      while( (i<startIdx) ) {
         BodyLongPeriodTotal += ((BodyLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = EqualTrailingIdx;
      while( (i<startIdx) ) {
         EqualPeriodTotal += ((Equal_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Equal_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( ((((((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-(((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))))&&(inOpen[i]<=(inOpen[(i-1)]+((Equal_factor * (((Equal_avgPeriod != 0) ? (EqualPeriodTotal / Equal_avgPeriod) : ((Equal_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Equal_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Equal_rangeType == 2) ? 2.0 : 1.0)))))))&&(inOpen[i]>=(inOpen[(i-1)]-((Equal_factor * (((Equal_avgPeriod != 0) ? (EqualPeriodTotal / Equal_avgPeriod) : ((Equal_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Equal_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Equal_rangeType == 2) ? 2.0 : 1.0)))))))&&(Math.abs((inClose[i]-inOpen[i]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyLongPeriodTotal / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))))&&((((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==1)&&(((((inClose[i]>=inOpen[i])) ? (inOpen[i]) : (inClose[i]))-inLow[i])<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))||(((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==(0-1))&&((inHigh[i]-(((inClose[i]>=inOpen[i])) ? (inClose[i]) : (inOpen[i])))<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0)))))))) ) {
            outInteger[outIdx++] = ((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))*100);
         } else {
            outInteger[outIdx++] = 0;
         }
         ShadowVeryShortPeriodTotal += (((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[ShadowVeryShortTrailingIdx] - inOpen[ShadowVeryShortTrailingIdx])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[ShadowVeryShortTrailingIdx] - inLow[ShadowVeryShortTrailingIdx]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[ShadowVeryShortTrailingIdx] - inLow[ShadowVeryShortTrailingIdx]) - Math.abs(inClose[ShadowVeryShortTrailingIdx] - inOpen[ShadowVeryShortTrailingIdx])) : 0.0))));
         BodyLongPeriodTotal += (((BodyLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((BodyLong_rangeType == 0) ? (Math.abs(inClose[BodyLongTrailingIdx] - inOpen[BodyLongTrailingIdx])) : ((BodyLong_rangeType == 1) ? (inHigh[BodyLongTrailingIdx] - inLow[BodyLongTrailingIdx]) : ((BodyLong_rangeType == 2) ? ((inHigh[BodyLongTrailingIdx] - inLow[BodyLongTrailingIdx]) - Math.abs(inClose[BodyLongTrailingIdx] - inOpen[BodyLongTrailingIdx])) : 0.0))));
         EqualPeriodTotal += (((Equal_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Equal_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))-((Equal_rangeType == 0) ? (Math.abs(inClose[(EqualTrailingIdx-1)] - inOpen[(EqualTrailingIdx-1)])) : ((Equal_rangeType == 1) ? (inHigh[(EqualTrailingIdx-1)] - inLow[(EqualTrailingIdx-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(EqualTrailingIdx-1)] - inLow[(EqualTrailingIdx-1)]) - Math.abs(inClose[(EqualTrailingIdx-1)] - inOpen[(EqualTrailingIdx-1)])) : 0.0))));
         i += 1;
         ShadowVeryShortTrailingIdx += 1;
         BodyLongTrailingIdx += 1;
         EqualTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdlseparatinglines( int startIdx,
                                      int endIdx,
                                      float inOpen[],
                                      float inHigh[],
                                      float inLow[],
                                      float inClose[],
                                      MInteger outBegIdx,
                                      MInteger outNBElement,
                                      int outInteger[] )
   {
      double ShadowVeryShortPeriodTotal = 0;
      double BodyLongPeriodTotal = 0;
      double EqualPeriodTotal = 0;
      int i = 0;
      int outIdx = 0;
      int ShadowVeryShortTrailingIdx = 0;
      int BodyLongTrailingIdx = 0;
      int EqualTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyLong_rangeType = this.candleSettings.bodyLong.rangeType;
      int BodyLong_avgPeriod = this.candleSettings.bodyLong.avgPeriod;
      double BodyLong_factor = this.candleSettings.bodyLong.factor;
      int Equal_rangeType = this.candleSettings.equal.rangeType;
      int Equal_avgPeriod = this.candleSettings.equal.avgPeriod;
      double Equal_factor = this.candleSettings.equal.factor;
      int ShadowVeryShort_rangeType = this.candleSettings.shadowVeryShort.rangeType;
      int ShadowVeryShort_avgPeriod = this.candleSettings.shadowVeryShort.avgPeriod;
      double ShadowVeryShort_factor = this.candleSettings.shadowVeryShort.factor;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackTotal = cdlseparatinglinesLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      ShadowVeryShortPeriodTotal = 0;
      ShadowVeryShortTrailingIdx = (startIdx-ShadowVeryShort_avgPeriod);
      BodyLongPeriodTotal = 0;
      BodyLongTrailingIdx = (startIdx-BodyLong_avgPeriod);
      EqualPeriodTotal = 0;
      EqualTrailingIdx = (startIdx-Equal_avgPeriod);
      i = ShadowVeryShortTrailingIdx;
      while( (i<startIdx) ) {
         ShadowVeryShortPeriodTotal += ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = BodyLongTrailingIdx;
      while( (i<startIdx) ) {
         BodyLongPeriodTotal += ((BodyLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = EqualTrailingIdx;
      while( (i<startIdx) ) {
         EqualPeriodTotal += ((Equal_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Equal_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( ((((((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-(((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))))&&(inOpen[i]<=(inOpen[(i-1)]+((Equal_factor * (((Equal_avgPeriod != 0) ? (EqualPeriodTotal / Equal_avgPeriod) : ((Equal_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Equal_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Equal_rangeType == 2) ? 2.0 : 1.0)))))))&&(inOpen[i]>=(inOpen[(i-1)]-((Equal_factor * (((Equal_avgPeriod != 0) ? (EqualPeriodTotal / Equal_avgPeriod) : ((Equal_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Equal_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Equal_rangeType == 2) ? 2.0 : 1.0)))))))&&(Math.abs((inClose[i]-inOpen[i]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyLongPeriodTotal / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))))&&((((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==1)&&(((((inClose[i]>=inOpen[i])) ? (inOpen[i]) : (inClose[i]))-inLow[i])<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))||(((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==(0-1))&&((inHigh[i]-(((inClose[i]>=inOpen[i])) ? (inClose[i]) : (inOpen[i])))<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0)))))))) ) {
            outInteger[outIdx++] = ((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))*100);
         } else {
            outInteger[outIdx++] = 0;
         }
         ShadowVeryShortPeriodTotal += (((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[ShadowVeryShortTrailingIdx] - inOpen[ShadowVeryShortTrailingIdx])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[ShadowVeryShortTrailingIdx] - inLow[ShadowVeryShortTrailingIdx]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[ShadowVeryShortTrailingIdx] - inLow[ShadowVeryShortTrailingIdx]) - Math.abs(inClose[ShadowVeryShortTrailingIdx] - inOpen[ShadowVeryShortTrailingIdx])) : 0.0))));
         BodyLongPeriodTotal += (((BodyLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((BodyLong_rangeType == 0) ? (Math.abs(inClose[BodyLongTrailingIdx] - inOpen[BodyLongTrailingIdx])) : ((BodyLong_rangeType == 1) ? (inHigh[BodyLongTrailingIdx] - inLow[BodyLongTrailingIdx]) : ((BodyLong_rangeType == 2) ? ((inHigh[BodyLongTrailingIdx] - inLow[BodyLongTrailingIdx]) - Math.abs(inClose[BodyLongTrailingIdx] - inOpen[BodyLongTrailingIdx])) : 0.0))));
         EqualPeriodTotal += (((Equal_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Equal_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))-((Equal_rangeType == 0) ? (Math.abs(inClose[(EqualTrailingIdx-1)] - inOpen[(EqualTrailingIdx-1)])) : ((Equal_rangeType == 1) ? (inHigh[(EqualTrailingIdx-1)] - inLow[(EqualTrailingIdx-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(EqualTrailingIdx-1)] - inLow[(EqualTrailingIdx-1)]) - Math.abs(inClose[(EqualTrailingIdx-1)] - inOpen[(EqualTrailingIdx-1)])) : 0.0))));
         i += 1;
         ShadowVeryShortTrailingIdx += 1;
         BodyLongTrailingIdx += 1;
         EqualTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdlseparatinglinesLogic( int startIdx,
                                           int endIdx,
                                           float inOpen[],
                                           float inHigh[],
                                           float inLow[],
                                           float inClose[],
                                           MInteger outBegIdx,
                                           MInteger outNBElement,
                                           int outInteger[] )
   {
      double ShadowVeryShortPeriodTotal = 0;
      double BodyLongPeriodTotal = 0;
      double EqualPeriodTotal = 0;
      int i = 0;
      int outIdx = 0;
      int ShadowVeryShortTrailingIdx = 0;
      int BodyLongTrailingIdx = 0;
      int EqualTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyLong_rangeType = this.candleSettings.bodyLong.rangeType;
      int BodyLong_avgPeriod = this.candleSettings.bodyLong.avgPeriod;
      double BodyLong_factor = this.candleSettings.bodyLong.factor;
      int Equal_rangeType = this.candleSettings.equal.rangeType;
      int Equal_avgPeriod = this.candleSettings.equal.avgPeriod;
      double Equal_factor = this.candleSettings.equal.factor;
      int ShadowVeryShort_rangeType = this.candleSettings.shadowVeryShort.rangeType;
      int ShadowVeryShort_avgPeriod = this.candleSettings.shadowVeryShort.avgPeriod;
      double ShadowVeryShort_factor = this.candleSettings.shadowVeryShort.factor;
      lookbackTotal = cdlseparatinglinesLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      ShadowVeryShortPeriodTotal = 0;
      ShadowVeryShortTrailingIdx = (startIdx-ShadowVeryShort_avgPeriod);
      BodyLongPeriodTotal = 0;
      BodyLongTrailingIdx = (startIdx-BodyLong_avgPeriod);
      EqualPeriodTotal = 0;
      EqualTrailingIdx = (startIdx-Equal_avgPeriod);
      i = ShadowVeryShortTrailingIdx;
      while( (i<startIdx) ) {
         ShadowVeryShortPeriodTotal += ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = BodyLongTrailingIdx;
      while( (i<startIdx) ) {
         BodyLongPeriodTotal += ((BodyLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = EqualTrailingIdx;
      while( (i<startIdx) ) {
         EqualPeriodTotal += ((Equal_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Equal_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( ((((((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-(((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))))&&(inOpen[i]<=(inOpen[(i-1)]+((Equal_factor * (((Equal_avgPeriod != 0) ? (EqualPeriodTotal / Equal_avgPeriod) : ((Equal_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Equal_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Equal_rangeType == 2) ? 2.0 : 1.0)))))))&&(inOpen[i]>=(inOpen[(i-1)]-((Equal_factor * (((Equal_avgPeriod != 0) ? (EqualPeriodTotal / Equal_avgPeriod) : ((Equal_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Equal_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Equal_rangeType == 2) ? 2.0 : 1.0)))))))&&(Math.abs((inClose[i]-inOpen[i]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyLongPeriodTotal / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))))&&((((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==1)&&(((((inClose[i]>=inOpen[i])) ? (inOpen[i]) : (inClose[i]))-inLow[i])<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))||(((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==(0-1))&&((inHigh[i]-(((inClose[i]>=inOpen[i])) ? (inClose[i]) : (inOpen[i])))<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0)))))))) ) {
            outInteger[outIdx++] = ((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))*100);
         } else {
            outInteger[outIdx++] = 0;
         }
         ShadowVeryShortPeriodTotal += (((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[ShadowVeryShortTrailingIdx] - inOpen[ShadowVeryShortTrailingIdx])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[ShadowVeryShortTrailingIdx] - inLow[ShadowVeryShortTrailingIdx]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[ShadowVeryShortTrailingIdx] - inLow[ShadowVeryShortTrailingIdx]) - Math.abs(inClose[ShadowVeryShortTrailingIdx] - inOpen[ShadowVeryShortTrailingIdx])) : 0.0))));
         BodyLongPeriodTotal += (((BodyLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((BodyLong_rangeType == 0) ? (Math.abs(inClose[BodyLongTrailingIdx] - inOpen[BodyLongTrailingIdx])) : ((BodyLong_rangeType == 1) ? (inHigh[BodyLongTrailingIdx] - inLow[BodyLongTrailingIdx]) : ((BodyLong_rangeType == 2) ? ((inHigh[BodyLongTrailingIdx] - inLow[BodyLongTrailingIdx]) - Math.abs(inClose[BodyLongTrailingIdx] - inOpen[BodyLongTrailingIdx])) : 0.0))));
         EqualPeriodTotal += (((Equal_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Equal_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))-((Equal_rangeType == 0) ? (Math.abs(inClose[(EqualTrailingIdx-1)] - inOpen[(EqualTrailingIdx-1)])) : ((Equal_rangeType == 1) ? (inHigh[(EqualTrailingIdx-1)] - inLow[(EqualTrailingIdx-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(EqualTrailingIdx-1)] - inLow[(EqualTrailingIdx-1)]) - Math.abs(inClose[(EqualTrailingIdx-1)] - inOpen[(EqualTrailingIdx-1)])) : 0.0))));
         i += 1;
         ShadowVeryShortTrailingIdx += 1;
         BodyLongTrailingIdx += 1;
         EqualTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
