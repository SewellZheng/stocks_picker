/* Generated */
   public int cdlhighwaveLookback( )
   {
      int BodyShort_rangeType = this.candleSettings.bodyShort.rangeType;
      int BodyShort_avgPeriod = this.candleSettings.bodyShort.avgPeriod;
      double BodyShort_factor = this.candleSettings.bodyShort.factor;
      int ShadowVeryLong_rangeType = this.candleSettings.shadowVeryLong.rangeType;
      int ShadowVeryLong_avgPeriod = this.candleSettings.shadowVeryLong.avgPeriod;
      double ShadowVeryLong_factor = this.candleSettings.shadowVeryLong.factor;
      return Math.max(BodyShort_avgPeriod, ShadowVeryLong_avgPeriod) ;

   }
   public RetCode cdlhighwave( int startIdx,
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
      double ShadowPeriodTotal = 0;
      int i = 0;
      int outIdx = 0;
      int BodyTrailingIdx = 0;
      int ShadowTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyShort_rangeType = this.candleSettings.bodyShort.rangeType;
      int BodyShort_avgPeriod = this.candleSettings.bodyShort.avgPeriod;
      double BodyShort_factor = this.candleSettings.bodyShort.factor;
      int ShadowVeryLong_rangeType = this.candleSettings.shadowVeryLong.rangeType;
      int ShadowVeryLong_avgPeriod = this.candleSettings.shadowVeryLong.avgPeriod;
      double ShadowVeryLong_factor = this.candleSettings.shadowVeryLong.factor;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackTotal = cdlhighwaveLookback();
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
      ShadowPeriodTotal = 0;
      ShadowTrailingIdx = (startIdx-ShadowVeryLong_avgPeriod);
      i = BodyTrailingIdx;
      while( (i<startIdx) ) {
         BodyPeriodTotal += ((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = ShadowTrailingIdx;
      while( (i<startIdx) ) {
         ShadowPeriodTotal += ((ShadowVeryLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      outIdx = 0;
      do {
         if( (((Math.abs((inClose[i]-inOpen[i]))<((BodyShort_factor * (((BodyShort_avgPeriod != 0) ? (BodyPeriodTotal / BodyShort_avgPeriod) : ((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((BodyShort_rangeType == 2) ? 2.0 : 1.0)))))&&((inHigh[i]-(((inClose[i]>=inOpen[i])) ? (inClose[i]) : (inOpen[i])))>((ShadowVeryLong_factor * (((ShadowVeryLong_avgPeriod != 0) ? (ShadowPeriodTotal / ShadowVeryLong_avgPeriod) : ((ShadowVeryLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowVeryLong_rangeType == 2) ? 2.0 : 1.0))))))&&(((((inClose[i]>=inOpen[i])) ? (inOpen[i]) : (inClose[i]))-inLow[i])>((ShadowVeryLong_factor * (((ShadowVeryLong_avgPeriod != 0) ? (ShadowPeriodTotal / ShadowVeryLong_avgPeriod) : ((ShadowVeryLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowVeryLong_rangeType == 2) ? 2.0 : 1.0)))))) ) {
            outInteger[outIdx++] = ((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))*100);
         } else {
            outInteger[outIdx++] = 0;
         }
         BodyPeriodTotal += (((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((BodyShort_rangeType == 0) ? (Math.abs(inClose[BodyTrailingIdx] - inOpen[BodyTrailingIdx])) : ((BodyShort_rangeType == 1) ? (inHigh[BodyTrailingIdx] - inLow[BodyTrailingIdx]) : ((BodyShort_rangeType == 2) ? ((inHigh[BodyTrailingIdx] - inLow[BodyTrailingIdx]) - Math.abs(inClose[BodyTrailingIdx] - inOpen[BodyTrailingIdx])) : 0.0))));
         ShadowPeriodTotal += (((ShadowVeryLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((ShadowVeryLong_rangeType == 0) ? (Math.abs(inClose[ShadowTrailingIdx] - inOpen[ShadowTrailingIdx])) : ((ShadowVeryLong_rangeType == 1) ? (inHigh[ShadowTrailingIdx] - inLow[ShadowTrailingIdx]) : ((ShadowVeryLong_rangeType == 2) ? ((inHigh[ShadowTrailingIdx] - inLow[ShadowTrailingIdx]) - Math.abs(inClose[ShadowTrailingIdx] - inOpen[ShadowTrailingIdx])) : 0.0))));
         i += 1;
         BodyTrailingIdx += 1;
         ShadowTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdlhighwaveLogic( int startIdx,
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
      double ShadowPeriodTotal = 0;
      int i = 0;
      int outIdx = 0;
      int BodyTrailingIdx = 0;
      int ShadowTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyShort_rangeType = this.candleSettings.bodyShort.rangeType;
      int BodyShort_avgPeriod = this.candleSettings.bodyShort.avgPeriod;
      double BodyShort_factor = this.candleSettings.bodyShort.factor;
      int ShadowVeryLong_rangeType = this.candleSettings.shadowVeryLong.rangeType;
      int ShadowVeryLong_avgPeriod = this.candleSettings.shadowVeryLong.avgPeriod;
      double ShadowVeryLong_factor = this.candleSettings.shadowVeryLong.factor;
      lookbackTotal = cdlhighwaveLookback();
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
      ShadowPeriodTotal = 0;
      ShadowTrailingIdx = (startIdx-ShadowVeryLong_avgPeriod);
      i = BodyTrailingIdx;
      while( (i<startIdx) ) {
         BodyPeriodTotal += ((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = ShadowTrailingIdx;
      while( (i<startIdx) ) {
         ShadowPeriodTotal += ((ShadowVeryLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      outIdx = 0;
      do {
         if( (((Math.abs((inClose[i]-inOpen[i]))<((BodyShort_factor * (((BodyShort_avgPeriod != 0) ? (BodyPeriodTotal / BodyShort_avgPeriod) : ((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((BodyShort_rangeType == 2) ? 2.0 : 1.0)))))&&((inHigh[i]-(((inClose[i]>=inOpen[i])) ? (inClose[i]) : (inOpen[i])))>((ShadowVeryLong_factor * (((ShadowVeryLong_avgPeriod != 0) ? (ShadowPeriodTotal / ShadowVeryLong_avgPeriod) : ((ShadowVeryLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowVeryLong_rangeType == 2) ? 2.0 : 1.0))))))&&(((((inClose[i]>=inOpen[i])) ? (inOpen[i]) : (inClose[i]))-inLow[i])>((ShadowVeryLong_factor * (((ShadowVeryLong_avgPeriod != 0) ? (ShadowPeriodTotal / ShadowVeryLong_avgPeriod) : ((ShadowVeryLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowVeryLong_rangeType == 2) ? 2.0 : 1.0)))))) ) {
            outInteger[outIdx++] = ((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))*100);
         } else {
            outInteger[outIdx++] = 0;
         }
         BodyPeriodTotal += (((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((BodyShort_rangeType == 0) ? (Math.abs(inClose[BodyTrailingIdx] - inOpen[BodyTrailingIdx])) : ((BodyShort_rangeType == 1) ? (inHigh[BodyTrailingIdx] - inLow[BodyTrailingIdx]) : ((BodyShort_rangeType == 2) ? ((inHigh[BodyTrailingIdx] - inLow[BodyTrailingIdx]) - Math.abs(inClose[BodyTrailingIdx] - inOpen[BodyTrailingIdx])) : 0.0))));
         ShadowPeriodTotal += (((ShadowVeryLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((ShadowVeryLong_rangeType == 0) ? (Math.abs(inClose[ShadowTrailingIdx] - inOpen[ShadowTrailingIdx])) : ((ShadowVeryLong_rangeType == 1) ? (inHigh[ShadowTrailingIdx] - inLow[ShadowTrailingIdx]) : ((ShadowVeryLong_rangeType == 2) ? ((inHigh[ShadowTrailingIdx] - inLow[ShadowTrailingIdx]) - Math.abs(inClose[ShadowTrailingIdx] - inOpen[ShadowTrailingIdx])) : 0.0))));
         i += 1;
         BodyTrailingIdx += 1;
         ShadowTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdlhighwave( int startIdx,
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
      double ShadowPeriodTotal = 0;
      int i = 0;
      int outIdx = 0;
      int BodyTrailingIdx = 0;
      int ShadowTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyShort_rangeType = this.candleSettings.bodyShort.rangeType;
      int BodyShort_avgPeriod = this.candleSettings.bodyShort.avgPeriod;
      double BodyShort_factor = this.candleSettings.bodyShort.factor;
      int ShadowVeryLong_rangeType = this.candleSettings.shadowVeryLong.rangeType;
      int ShadowVeryLong_avgPeriod = this.candleSettings.shadowVeryLong.avgPeriod;
      double ShadowVeryLong_factor = this.candleSettings.shadowVeryLong.factor;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackTotal = cdlhighwaveLookback();
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
      ShadowPeriodTotal = 0;
      ShadowTrailingIdx = (startIdx-ShadowVeryLong_avgPeriod);
      i = BodyTrailingIdx;
      while( (i<startIdx) ) {
         BodyPeriodTotal += ((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = ShadowTrailingIdx;
      while( (i<startIdx) ) {
         ShadowPeriodTotal += ((ShadowVeryLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      outIdx = 0;
      do {
         if( (((Math.abs((inClose[i]-inOpen[i]))<((BodyShort_factor * (((BodyShort_avgPeriod != 0) ? (BodyPeriodTotal / BodyShort_avgPeriod) : ((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((BodyShort_rangeType == 2) ? 2.0 : 1.0)))))&&((inHigh[i]-(((inClose[i]>=inOpen[i])) ? (inClose[i]) : (inOpen[i])))>((ShadowVeryLong_factor * (((ShadowVeryLong_avgPeriod != 0) ? (ShadowPeriodTotal / ShadowVeryLong_avgPeriod) : ((ShadowVeryLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowVeryLong_rangeType == 2) ? 2.0 : 1.0))))))&&(((((inClose[i]>=inOpen[i])) ? (inOpen[i]) : (inClose[i]))-inLow[i])>((ShadowVeryLong_factor * (((ShadowVeryLong_avgPeriod != 0) ? (ShadowPeriodTotal / ShadowVeryLong_avgPeriod) : ((ShadowVeryLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowVeryLong_rangeType == 2) ? 2.0 : 1.0)))))) ) {
            outInteger[outIdx++] = ((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))*100);
         } else {
            outInteger[outIdx++] = 0;
         }
         BodyPeriodTotal += (((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((BodyShort_rangeType == 0) ? (Math.abs(inClose[BodyTrailingIdx] - inOpen[BodyTrailingIdx])) : ((BodyShort_rangeType == 1) ? (inHigh[BodyTrailingIdx] - inLow[BodyTrailingIdx]) : ((BodyShort_rangeType == 2) ? ((inHigh[BodyTrailingIdx] - inLow[BodyTrailingIdx]) - Math.abs(inClose[BodyTrailingIdx] - inOpen[BodyTrailingIdx])) : 0.0))));
         ShadowPeriodTotal += (((ShadowVeryLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((ShadowVeryLong_rangeType == 0) ? (Math.abs(inClose[ShadowTrailingIdx] - inOpen[ShadowTrailingIdx])) : ((ShadowVeryLong_rangeType == 1) ? (inHigh[ShadowTrailingIdx] - inLow[ShadowTrailingIdx]) : ((ShadowVeryLong_rangeType == 2) ? ((inHigh[ShadowTrailingIdx] - inLow[ShadowTrailingIdx]) - Math.abs(inClose[ShadowTrailingIdx] - inOpen[ShadowTrailingIdx])) : 0.0))));
         i += 1;
         BodyTrailingIdx += 1;
         ShadowTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdlhighwaveLogic( int startIdx,
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
      double ShadowPeriodTotal = 0;
      int i = 0;
      int outIdx = 0;
      int BodyTrailingIdx = 0;
      int ShadowTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyShort_rangeType = this.candleSettings.bodyShort.rangeType;
      int BodyShort_avgPeriod = this.candleSettings.bodyShort.avgPeriod;
      double BodyShort_factor = this.candleSettings.bodyShort.factor;
      int ShadowVeryLong_rangeType = this.candleSettings.shadowVeryLong.rangeType;
      int ShadowVeryLong_avgPeriod = this.candleSettings.shadowVeryLong.avgPeriod;
      double ShadowVeryLong_factor = this.candleSettings.shadowVeryLong.factor;
      lookbackTotal = cdlhighwaveLookback();
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
      ShadowPeriodTotal = 0;
      ShadowTrailingIdx = (startIdx-ShadowVeryLong_avgPeriod);
      i = BodyTrailingIdx;
      while( (i<startIdx) ) {
         BodyPeriodTotal += ((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = ShadowTrailingIdx;
      while( (i<startIdx) ) {
         ShadowPeriodTotal += ((ShadowVeryLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      outIdx = 0;
      do {
         if( (((Math.abs((inClose[i]-inOpen[i]))<((BodyShort_factor * (((BodyShort_avgPeriod != 0) ? (BodyPeriodTotal / BodyShort_avgPeriod) : ((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((BodyShort_rangeType == 2) ? 2.0 : 1.0)))))&&((inHigh[i]-(((inClose[i]>=inOpen[i])) ? (inClose[i]) : (inOpen[i])))>((ShadowVeryLong_factor * (((ShadowVeryLong_avgPeriod != 0) ? (ShadowPeriodTotal / ShadowVeryLong_avgPeriod) : ((ShadowVeryLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowVeryLong_rangeType == 2) ? 2.0 : 1.0))))))&&(((((inClose[i]>=inOpen[i])) ? (inOpen[i]) : (inClose[i]))-inLow[i])>((ShadowVeryLong_factor * (((ShadowVeryLong_avgPeriod != 0) ? (ShadowPeriodTotal / ShadowVeryLong_avgPeriod) : ((ShadowVeryLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowVeryLong_rangeType == 2) ? 2.0 : 1.0)))))) ) {
            outInteger[outIdx++] = ((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))*100);
         } else {
            outInteger[outIdx++] = 0;
         }
         BodyPeriodTotal += (((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((BodyShort_rangeType == 0) ? (Math.abs(inClose[BodyTrailingIdx] - inOpen[BodyTrailingIdx])) : ((BodyShort_rangeType == 1) ? (inHigh[BodyTrailingIdx] - inLow[BodyTrailingIdx]) : ((BodyShort_rangeType == 2) ? ((inHigh[BodyTrailingIdx] - inLow[BodyTrailingIdx]) - Math.abs(inClose[BodyTrailingIdx] - inOpen[BodyTrailingIdx])) : 0.0))));
         ShadowPeriodTotal += (((ShadowVeryLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowVeryLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowVeryLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((ShadowVeryLong_rangeType == 0) ? (Math.abs(inClose[ShadowTrailingIdx] - inOpen[ShadowTrailingIdx])) : ((ShadowVeryLong_rangeType == 1) ? (inHigh[ShadowTrailingIdx] - inLow[ShadowTrailingIdx]) : ((ShadowVeryLong_rangeType == 2) ? ((inHigh[ShadowTrailingIdx] - inLow[ShadowTrailingIdx]) - Math.abs(inClose[ShadowTrailingIdx] - inOpen[ShadowTrailingIdx])) : 0.0))));
         i += 1;
         BodyTrailingIdx += 1;
         ShadowTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
