/* Generated */
   public int cdltakuriLookback( )
   {
      int BodyDoji_rangeType = this.candleSettings.bodyDoji.rangeType;
      int BodyDoji_avgPeriod = this.candleSettings.bodyDoji.avgPeriod;
      double BodyDoji_factor = this.candleSettings.bodyDoji.factor;
      int ShadowVeryLong_rangeType = this.candleSettings.shadowVeryLong.rangeType;
      int ShadowVeryLong_avgPeriod = this.candleSettings.shadowVeryLong.avgPeriod;
      double ShadowVeryLong_factor = this.candleSettings.shadowVeryLong.factor;
      int ShadowVeryShort_rangeType = this.candleSettings.shadowVeryShort.rangeType;
      int ShadowVeryShort_avgPeriod = this.candleSettings.shadowVeryShort.avgPeriod;
      double ShadowVeryShort_factor = this.candleSettings.shadowVeryShort.factor;
      return Math.max(Math.max(BodyDoji_avgPeriod, ShadowVeryShort_avgPeriod), ShadowVeryLong_avgPeriod) ;

   }
   public RetCode cdltakuri( int startIdx,
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
      double ShadowVeryShortPeriodTotal = 0;
      double ShadowVeryLongPeriodTotal = 0;
      int i = 0;
      int outIdx = 0;
      int BodyDojiTrailingIdx = 0;
      int ShadowVeryShortTrailingIdx = 0;
      int ShadowVeryLongTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyDoji_rangeType = this.candleSettings.bodyDoji.rangeType;
      int BodyDoji_avgPeriod = this.candleSettings.bodyDoji.avgPeriod;
      double BodyDoji_factor = this.candleSettings.bodyDoji.factor;
      int ShadowVeryLong_rangeType = this.candleSettings.shadowVeryLong.rangeType;
      int ShadowVeryLong_avgPeriod = this.candleSettings.shadowVeryLong.avgPeriod;
      double ShadowVeryLong_factor = this.candleSettings.shadowVeryLong.factor;
      int ShadowVeryShort_rangeType = this.candleSettings.shadowVeryShort.rangeType;
      int ShadowVeryShort_avgPeriod = this.candleSettings.shadowVeryShort.avgPeriod;
      double ShadowVeryShort_factor = this.candleSettings.shadowVeryShort.factor;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackTotal = cdltakuriLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      BodyDojiPeriodTotal = 0;
      BodyDojiTrailingIdx = (startIdx-BodyDoji_avgPeriod);
      ShadowVeryShortPeriodTotal = 0;
      ShadowVeryShortTrailingIdx = (startIdx-ShadowVeryShort_avgPeriod);
      ShadowVeryLongPeriodTotal = 0;
      ShadowVeryLongTrailingIdx = (startIdx-ShadowVeryLong_avgPeriod);
      i = BodyDojiTrailingIdx;
      while( (i<startIdx) ) {
         BodyDojiPeriodTotal += ((BodyDoji_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyDoji_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyDoji_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = ShadowVeryShortTrailingIdx;
      while( (i<startIdx) ) {
         ShadowVeryShortPeriodTotal += ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = ShadowVeryLongTrailingIdx;
      while( (i<startIdx) ) {
         ShadowVeryLongPeriodTotal += ((ShadowVeryLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      outIdx = 0;
      do {
         if( (((Math.abs((inClose[i]-inOpen[i]))<=((BodyDoji_factor * (((BodyDoji_avgPeriod != 0) ? (BodyDojiPeriodTotal / BodyDoji_avgPeriod) : ((BodyDoji_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyDoji_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyDoji_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((BodyDoji_rangeType == 2) ? 2.0 : 1.0)))))&&((inHigh[i]-(((inClose[i]>=inOpen[i])) ? (inClose[i]) : (inOpen[i])))<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))&&(((((inClose[i]>=inOpen[i])) ? (inOpen[i]) : (inClose[i]))-inLow[i])>((ShadowVeryLong_factor * (((ShadowVeryLong_avgPeriod != 0) ? (ShadowVeryLongPeriodTotal / ShadowVeryLong_avgPeriod) : ((ShadowVeryLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowVeryLong_rangeType == 2) ? 2.0 : 1.0)))))) ) {
            outInteger[outIdx++] = 100;
         } else {
            outInteger[outIdx++] = 0;
         }
         BodyDojiPeriodTotal += (((BodyDoji_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyDoji_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyDoji_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((BodyDoji_rangeType == 0) ? (Math.abs(inClose[BodyDojiTrailingIdx] - inOpen[BodyDojiTrailingIdx])) : ((BodyDoji_rangeType == 1) ? (inHigh[BodyDojiTrailingIdx] - inLow[BodyDojiTrailingIdx]) : ((BodyDoji_rangeType == 2) ? ((inHigh[BodyDojiTrailingIdx] - inLow[BodyDojiTrailingIdx]) - Math.abs(inClose[BodyDojiTrailingIdx] - inOpen[BodyDojiTrailingIdx])) : 0.0))));
         ShadowVeryShortPeriodTotal += (((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[ShadowVeryShortTrailingIdx] - inOpen[ShadowVeryShortTrailingIdx])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[ShadowVeryShortTrailingIdx] - inLow[ShadowVeryShortTrailingIdx]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[ShadowVeryShortTrailingIdx] - inLow[ShadowVeryShortTrailingIdx]) - Math.abs(inClose[ShadowVeryShortTrailingIdx] - inOpen[ShadowVeryShortTrailingIdx])) : 0.0))));
         ShadowVeryLongPeriodTotal += (((ShadowVeryLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((ShadowVeryLong_rangeType == 0) ? (Math.abs(inClose[ShadowVeryLongTrailingIdx] - inOpen[ShadowVeryLongTrailingIdx])) : ((ShadowVeryLong_rangeType == 1) ? (inHigh[ShadowVeryLongTrailingIdx] - inLow[ShadowVeryLongTrailingIdx]) : ((ShadowVeryLong_rangeType == 2) ? ((inHigh[ShadowVeryLongTrailingIdx] - inLow[ShadowVeryLongTrailingIdx]) - Math.abs(inClose[ShadowVeryLongTrailingIdx] - inOpen[ShadowVeryLongTrailingIdx])) : 0.0))));
         i += 1;
         BodyDojiTrailingIdx += 1;
         ShadowVeryShortTrailingIdx += 1;
         ShadowVeryLongTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdltakuriLogic( int startIdx,
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
      double ShadowVeryShortPeriodTotal = 0;
      double ShadowVeryLongPeriodTotal = 0;
      int i = 0;
      int outIdx = 0;
      int BodyDojiTrailingIdx = 0;
      int ShadowVeryShortTrailingIdx = 0;
      int ShadowVeryLongTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyDoji_rangeType = this.candleSettings.bodyDoji.rangeType;
      int BodyDoji_avgPeriod = this.candleSettings.bodyDoji.avgPeriod;
      double BodyDoji_factor = this.candleSettings.bodyDoji.factor;
      int ShadowVeryLong_rangeType = this.candleSettings.shadowVeryLong.rangeType;
      int ShadowVeryLong_avgPeriod = this.candleSettings.shadowVeryLong.avgPeriod;
      double ShadowVeryLong_factor = this.candleSettings.shadowVeryLong.factor;
      int ShadowVeryShort_rangeType = this.candleSettings.shadowVeryShort.rangeType;
      int ShadowVeryShort_avgPeriod = this.candleSettings.shadowVeryShort.avgPeriod;
      double ShadowVeryShort_factor = this.candleSettings.shadowVeryShort.factor;
      lookbackTotal = cdltakuriLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      BodyDojiPeriodTotal = 0;
      BodyDojiTrailingIdx = (startIdx-BodyDoji_avgPeriod);
      ShadowVeryShortPeriodTotal = 0;
      ShadowVeryShortTrailingIdx = (startIdx-ShadowVeryShort_avgPeriod);
      ShadowVeryLongPeriodTotal = 0;
      ShadowVeryLongTrailingIdx = (startIdx-ShadowVeryLong_avgPeriod);
      i = BodyDojiTrailingIdx;
      while( (i<startIdx) ) {
         BodyDojiPeriodTotal += ((BodyDoji_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyDoji_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyDoji_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = ShadowVeryShortTrailingIdx;
      while( (i<startIdx) ) {
         ShadowVeryShortPeriodTotal += ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = ShadowVeryLongTrailingIdx;
      while( (i<startIdx) ) {
         ShadowVeryLongPeriodTotal += ((ShadowVeryLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      outIdx = 0;
      do {
         if( (((Math.abs((inClose[i]-inOpen[i]))<=((BodyDoji_factor * (((BodyDoji_avgPeriod != 0) ? (BodyDojiPeriodTotal / BodyDoji_avgPeriod) : ((BodyDoji_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyDoji_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyDoji_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((BodyDoji_rangeType == 2) ? 2.0 : 1.0)))))&&((inHigh[i]-(((inClose[i]>=inOpen[i])) ? (inClose[i]) : (inOpen[i])))<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))&&(((((inClose[i]>=inOpen[i])) ? (inOpen[i]) : (inClose[i]))-inLow[i])>((ShadowVeryLong_factor * (((ShadowVeryLong_avgPeriod != 0) ? (ShadowVeryLongPeriodTotal / ShadowVeryLong_avgPeriod) : ((ShadowVeryLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowVeryLong_rangeType == 2) ? 2.0 : 1.0)))))) ) {
            outInteger[outIdx++] = 100;
         } else {
            outInteger[outIdx++] = 0;
         }
         BodyDojiPeriodTotal += (((BodyDoji_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyDoji_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyDoji_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((BodyDoji_rangeType == 0) ? (Math.abs(inClose[BodyDojiTrailingIdx] - inOpen[BodyDojiTrailingIdx])) : ((BodyDoji_rangeType == 1) ? (inHigh[BodyDojiTrailingIdx] - inLow[BodyDojiTrailingIdx]) : ((BodyDoji_rangeType == 2) ? ((inHigh[BodyDojiTrailingIdx] - inLow[BodyDojiTrailingIdx]) - Math.abs(inClose[BodyDojiTrailingIdx] - inOpen[BodyDojiTrailingIdx])) : 0.0))));
         ShadowVeryShortPeriodTotal += (((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[ShadowVeryShortTrailingIdx] - inOpen[ShadowVeryShortTrailingIdx])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[ShadowVeryShortTrailingIdx] - inLow[ShadowVeryShortTrailingIdx]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[ShadowVeryShortTrailingIdx] - inLow[ShadowVeryShortTrailingIdx]) - Math.abs(inClose[ShadowVeryShortTrailingIdx] - inOpen[ShadowVeryShortTrailingIdx])) : 0.0))));
         ShadowVeryLongPeriodTotal += (((ShadowVeryLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((ShadowVeryLong_rangeType == 0) ? (Math.abs(inClose[ShadowVeryLongTrailingIdx] - inOpen[ShadowVeryLongTrailingIdx])) : ((ShadowVeryLong_rangeType == 1) ? (inHigh[ShadowVeryLongTrailingIdx] - inLow[ShadowVeryLongTrailingIdx]) : ((ShadowVeryLong_rangeType == 2) ? ((inHigh[ShadowVeryLongTrailingIdx] - inLow[ShadowVeryLongTrailingIdx]) - Math.abs(inClose[ShadowVeryLongTrailingIdx] - inOpen[ShadowVeryLongTrailingIdx])) : 0.0))));
         i += 1;
         BodyDojiTrailingIdx += 1;
         ShadowVeryShortTrailingIdx += 1;
         ShadowVeryLongTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdltakuri( int startIdx,
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
      double ShadowVeryShortPeriodTotal = 0;
      double ShadowVeryLongPeriodTotal = 0;
      int i = 0;
      int outIdx = 0;
      int BodyDojiTrailingIdx = 0;
      int ShadowVeryShortTrailingIdx = 0;
      int ShadowVeryLongTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyDoji_rangeType = this.candleSettings.bodyDoji.rangeType;
      int BodyDoji_avgPeriod = this.candleSettings.bodyDoji.avgPeriod;
      double BodyDoji_factor = this.candleSettings.bodyDoji.factor;
      int ShadowVeryLong_rangeType = this.candleSettings.shadowVeryLong.rangeType;
      int ShadowVeryLong_avgPeriod = this.candleSettings.shadowVeryLong.avgPeriod;
      double ShadowVeryLong_factor = this.candleSettings.shadowVeryLong.factor;
      int ShadowVeryShort_rangeType = this.candleSettings.shadowVeryShort.rangeType;
      int ShadowVeryShort_avgPeriod = this.candleSettings.shadowVeryShort.avgPeriod;
      double ShadowVeryShort_factor = this.candleSettings.shadowVeryShort.factor;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackTotal = cdltakuriLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      BodyDojiPeriodTotal = 0;
      BodyDojiTrailingIdx = (startIdx-BodyDoji_avgPeriod);
      ShadowVeryShortPeriodTotal = 0;
      ShadowVeryShortTrailingIdx = (startIdx-ShadowVeryShort_avgPeriod);
      ShadowVeryLongPeriodTotal = 0;
      ShadowVeryLongTrailingIdx = (startIdx-ShadowVeryLong_avgPeriod);
      i = BodyDojiTrailingIdx;
      while( (i<startIdx) ) {
         BodyDojiPeriodTotal += ((BodyDoji_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyDoji_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyDoji_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = ShadowVeryShortTrailingIdx;
      while( (i<startIdx) ) {
         ShadowVeryShortPeriodTotal += ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = ShadowVeryLongTrailingIdx;
      while( (i<startIdx) ) {
         ShadowVeryLongPeriodTotal += ((ShadowVeryLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      outIdx = 0;
      do {
         if( (((Math.abs((inClose[i]-inOpen[i]))<=((BodyDoji_factor * (((BodyDoji_avgPeriod != 0) ? (BodyDojiPeriodTotal / BodyDoji_avgPeriod) : ((BodyDoji_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyDoji_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyDoji_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((BodyDoji_rangeType == 2) ? 2.0 : 1.0)))))&&((inHigh[i]-(((inClose[i]>=inOpen[i])) ? (inClose[i]) : (inOpen[i])))<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))&&(((((inClose[i]>=inOpen[i])) ? (inOpen[i]) : (inClose[i]))-inLow[i])>((ShadowVeryLong_factor * (((ShadowVeryLong_avgPeriod != 0) ? (ShadowVeryLongPeriodTotal / ShadowVeryLong_avgPeriod) : ((ShadowVeryLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowVeryLong_rangeType == 2) ? 2.0 : 1.0)))))) ) {
            outInteger[outIdx++] = 100;
         } else {
            outInteger[outIdx++] = 0;
         }
         BodyDojiPeriodTotal += (((BodyDoji_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyDoji_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyDoji_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((BodyDoji_rangeType == 0) ? (Math.abs(inClose[BodyDojiTrailingIdx] - inOpen[BodyDojiTrailingIdx])) : ((BodyDoji_rangeType == 1) ? (inHigh[BodyDojiTrailingIdx] - inLow[BodyDojiTrailingIdx]) : ((BodyDoji_rangeType == 2) ? ((inHigh[BodyDojiTrailingIdx] - inLow[BodyDojiTrailingIdx]) - Math.abs(inClose[BodyDojiTrailingIdx] - inOpen[BodyDojiTrailingIdx])) : 0.0))));
         ShadowVeryShortPeriodTotal += (((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[ShadowVeryShortTrailingIdx] - inOpen[ShadowVeryShortTrailingIdx])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[ShadowVeryShortTrailingIdx] - inLow[ShadowVeryShortTrailingIdx]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[ShadowVeryShortTrailingIdx] - inLow[ShadowVeryShortTrailingIdx]) - Math.abs(inClose[ShadowVeryShortTrailingIdx] - inOpen[ShadowVeryShortTrailingIdx])) : 0.0))));
         ShadowVeryLongPeriodTotal += (((ShadowVeryLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((ShadowVeryLong_rangeType == 0) ? (Math.abs(inClose[ShadowVeryLongTrailingIdx] - inOpen[ShadowVeryLongTrailingIdx])) : ((ShadowVeryLong_rangeType == 1) ? (inHigh[ShadowVeryLongTrailingIdx] - inLow[ShadowVeryLongTrailingIdx]) : ((ShadowVeryLong_rangeType == 2) ? ((inHigh[ShadowVeryLongTrailingIdx] - inLow[ShadowVeryLongTrailingIdx]) - Math.abs(inClose[ShadowVeryLongTrailingIdx] - inOpen[ShadowVeryLongTrailingIdx])) : 0.0))));
         i += 1;
         BodyDojiTrailingIdx += 1;
         ShadowVeryShortTrailingIdx += 1;
         ShadowVeryLongTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdltakuriLogic( int startIdx,
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
      double ShadowVeryShortPeriodTotal = 0;
      double ShadowVeryLongPeriodTotal = 0;
      int i = 0;
      int outIdx = 0;
      int BodyDojiTrailingIdx = 0;
      int ShadowVeryShortTrailingIdx = 0;
      int ShadowVeryLongTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyDoji_rangeType = this.candleSettings.bodyDoji.rangeType;
      int BodyDoji_avgPeriod = this.candleSettings.bodyDoji.avgPeriod;
      double BodyDoji_factor = this.candleSettings.bodyDoji.factor;
      int ShadowVeryLong_rangeType = this.candleSettings.shadowVeryLong.rangeType;
      int ShadowVeryLong_avgPeriod = this.candleSettings.shadowVeryLong.avgPeriod;
      double ShadowVeryLong_factor = this.candleSettings.shadowVeryLong.factor;
      int ShadowVeryShort_rangeType = this.candleSettings.shadowVeryShort.rangeType;
      int ShadowVeryShort_avgPeriod = this.candleSettings.shadowVeryShort.avgPeriod;
      double ShadowVeryShort_factor = this.candleSettings.shadowVeryShort.factor;
      lookbackTotal = cdltakuriLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      BodyDojiPeriodTotal = 0;
      BodyDojiTrailingIdx = (startIdx-BodyDoji_avgPeriod);
      ShadowVeryShortPeriodTotal = 0;
      ShadowVeryShortTrailingIdx = (startIdx-ShadowVeryShort_avgPeriod);
      ShadowVeryLongPeriodTotal = 0;
      ShadowVeryLongTrailingIdx = (startIdx-ShadowVeryLong_avgPeriod);
      i = BodyDojiTrailingIdx;
      while( (i<startIdx) ) {
         BodyDojiPeriodTotal += ((BodyDoji_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyDoji_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyDoji_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = ShadowVeryShortTrailingIdx;
      while( (i<startIdx) ) {
         ShadowVeryShortPeriodTotal += ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = ShadowVeryLongTrailingIdx;
      while( (i<startIdx) ) {
         ShadowVeryLongPeriodTotal += ((ShadowVeryLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      outIdx = 0;
      do {
         if( (((Math.abs((inClose[i]-inOpen[i]))<=((BodyDoji_factor * (((BodyDoji_avgPeriod != 0) ? (BodyDojiPeriodTotal / BodyDoji_avgPeriod) : ((BodyDoji_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyDoji_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyDoji_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((BodyDoji_rangeType == 2) ? 2.0 : 1.0)))))&&((inHigh[i]-(((inClose[i]>=inOpen[i])) ? (inClose[i]) : (inOpen[i])))<((ShadowVeryShort_factor * (((ShadowVeryShort_avgPeriod != 0) ? (ShadowVeryShortPeriodTotal / ShadowVeryShort_avgPeriod) : ((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowVeryShort_rangeType == 2) ? 2.0 : 1.0))))))&&(((((inClose[i]>=inOpen[i])) ? (inOpen[i]) : (inClose[i]))-inLow[i])>((ShadowVeryLong_factor * (((ShadowVeryLong_avgPeriod != 0) ? (ShadowVeryLongPeriodTotal / ShadowVeryLong_avgPeriod) : ((ShadowVeryLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowVeryLong_rangeType == 2) ? 2.0 : 1.0)))))) ) {
            outInteger[outIdx++] = 100;
         } else {
            outInteger[outIdx++] = 0;
         }
         BodyDojiPeriodTotal += (((BodyDoji_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyDoji_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyDoji_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((BodyDoji_rangeType == 0) ? (Math.abs(inClose[BodyDojiTrailingIdx] - inOpen[BodyDojiTrailingIdx])) : ((BodyDoji_rangeType == 1) ? (inHigh[BodyDojiTrailingIdx] - inLow[BodyDojiTrailingIdx]) : ((BodyDoji_rangeType == 2) ? ((inHigh[BodyDojiTrailingIdx] - inLow[BodyDojiTrailingIdx]) - Math.abs(inClose[BodyDojiTrailingIdx] - inOpen[BodyDojiTrailingIdx])) : 0.0))));
         ShadowVeryShortPeriodTotal += (((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((ShadowVeryShort_rangeType == 0) ? (Math.abs(inClose[ShadowVeryShortTrailingIdx] - inOpen[ShadowVeryShortTrailingIdx])) : ((ShadowVeryShort_rangeType == 1) ? (inHigh[ShadowVeryShortTrailingIdx] - inLow[ShadowVeryShortTrailingIdx]) : ((ShadowVeryShort_rangeType == 2) ? ((inHigh[ShadowVeryShortTrailingIdx] - inLow[ShadowVeryShortTrailingIdx]) - Math.abs(inClose[ShadowVeryShortTrailingIdx] - inOpen[ShadowVeryShortTrailingIdx])) : 0.0))));
         ShadowVeryLongPeriodTotal += (((ShadowVeryLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((ShadowVeryLong_rangeType == 0) ? (Math.abs(inClose[ShadowVeryLongTrailingIdx] - inOpen[ShadowVeryLongTrailingIdx])) : ((ShadowVeryLong_rangeType == 1) ? (inHigh[ShadowVeryLongTrailingIdx] - inLow[ShadowVeryLongTrailingIdx]) : ((ShadowVeryLong_rangeType == 2) ? ((inHigh[ShadowVeryLongTrailingIdx] - inLow[ShadowVeryLongTrailingIdx]) - Math.abs(inClose[ShadowVeryLongTrailingIdx] - inOpen[ShadowVeryLongTrailingIdx])) : 0.0))));
         i += 1;
         BodyDojiTrailingIdx += 1;
         ShadowVeryShortTrailingIdx += 1;
         ShadowVeryLongTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
