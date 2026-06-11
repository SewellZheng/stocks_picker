/* Generated */
   public int cdleveningstarLookback( double optInPenetration )
   {
      int BodyLong_rangeType = this.candleSettings.bodyLong.rangeType;
      int BodyLong_avgPeriod = this.candleSettings.bodyLong.avgPeriod;
      double BodyLong_factor = this.candleSettings.bodyLong.factor;
      int BodyShort_rangeType = this.candleSettings.bodyShort.rangeType;
      int BodyShort_avgPeriod = this.candleSettings.bodyShort.avgPeriod;
      double BodyShort_factor = this.candleSettings.bodyShort.factor;
      return (Math.max(BodyShort_avgPeriod, BodyLong_avgPeriod)+2) ;

   }
   public RetCode cdleveningstar( int startIdx,
                                  int endIdx,
                                  double inOpen[],
                                  double inHigh[],
                                  double inLow[],
                                  double inClose[],
                                  double optInPenetration,
                                  MInteger outBegIdx,
                                  MInteger outNBElement,
                                  int outInteger[] )
   {
      double BodyShortPeriodTotal = 0;
      double BodyLongPeriodTotal = 0;
      double BodyShortPeriodTotal2 = 0;
      int i = 0;
      int outIdx = 0;
      int BodyShortTrailingIdx = 0;
      int BodyLongTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyLong_rangeType = this.candleSettings.bodyLong.rangeType;
      int BodyLong_avgPeriod = this.candleSettings.bodyLong.avgPeriod;
      double BodyLong_factor = this.candleSettings.bodyLong.factor;
      int BodyShort_rangeType = this.candleSettings.bodyShort.rangeType;
      int BodyShort_avgPeriod = this.candleSettings.bodyShort.avgPeriod;
      double BodyShort_factor = this.candleSettings.bodyShort.factor;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackTotal = cdleveningstarLookback(optInPenetration);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      BodyLongPeriodTotal = 0;
      BodyShortPeriodTotal = 0;
      BodyShortPeriodTotal2 = 0;
      BodyLongTrailingIdx = ((startIdx-2)-BodyLong_avgPeriod);
      BodyShortTrailingIdx = ((startIdx-1)-BodyShort_avgPeriod);
      i = BodyLongTrailingIdx;
      while( (i<(startIdx-2)) ) {
         BodyLongPeriodTotal += ((BodyLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = BodyShortTrailingIdx;
      while( (i<(startIdx-1)) ) {
         BodyShortPeriodTotal += ((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         BodyShortPeriodTotal2 += ((BodyShort_rangeType == 0) ? (Math.abs(inClose[(i+1)] - inOpen[(i+1)])) : ((BodyShort_rangeType == 1) ? (inHigh[(i+1)] - inLow[(i+1)]) : ((BodyShort_rangeType == 2) ? ((inHigh[(i+1)] - inLow[(i+1)]) - Math.abs(inClose[(i+1)] - inOpen[(i+1)])) : 0.0)));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( ((((((((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==1)&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==(0-1)))&&(Math.min(inOpen[(i-1)], inClose[(i-1)])>Math.max(inOpen[(i-2)], inClose[(i-2)])))&&(inClose[i]<(inClose[(i-2)]-(Math.abs((inClose[(i-2)]-inOpen[(i-2)]))*optInPenetration))))&&(Math.abs((inClose[(i-2)]-inOpen[(i-2)]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyLongPeriodTotal / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))<=((BodyShort_factor * (((BodyShort_avgPeriod != 0) ? (BodyShortPeriodTotal / BodyShort_avgPeriod) : ((BodyShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((BodyShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((BodyShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((BodyShort_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.abs((inClose[i]-inOpen[i]))>((BodyShort_factor * (((BodyShort_avgPeriod != 0) ? (BodyShortPeriodTotal2 / BodyShort_avgPeriod) : ((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((BodyShort_rangeType == 2) ? 2.0 : 1.0)))))) ) {
            outInteger[outIdx++] = (0-100);
         } else {
            outInteger[outIdx++] = 0;
         }
         BodyLongPeriodTotal += (((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))-((BodyLong_rangeType == 0) ? (Math.abs(inClose[BodyLongTrailingIdx] - inOpen[BodyLongTrailingIdx])) : ((BodyLong_rangeType == 1) ? (inHigh[BodyLongTrailingIdx] - inLow[BodyLongTrailingIdx]) : ((BodyLong_rangeType == 2) ? ((inHigh[BodyLongTrailingIdx] - inLow[BodyLongTrailingIdx]) - Math.abs(inClose[BodyLongTrailingIdx] - inOpen[BodyLongTrailingIdx])) : 0.0))));
         BodyShortPeriodTotal += (((BodyShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((BodyShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((BodyShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))-((BodyShort_rangeType == 0) ? (Math.abs(inClose[BodyShortTrailingIdx] - inOpen[BodyShortTrailingIdx])) : ((BodyShort_rangeType == 1) ? (inHigh[BodyShortTrailingIdx] - inLow[BodyShortTrailingIdx]) : ((BodyShort_rangeType == 2) ? ((inHigh[BodyShortTrailingIdx] - inLow[BodyShortTrailingIdx]) - Math.abs(inClose[BodyShortTrailingIdx] - inOpen[BodyShortTrailingIdx])) : 0.0))));
         BodyShortPeriodTotal2 += (((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((BodyShort_rangeType == 0) ? (Math.abs(inClose[(BodyShortTrailingIdx+1)] - inOpen[(BodyShortTrailingIdx+1)])) : ((BodyShort_rangeType == 1) ? (inHigh[(BodyShortTrailingIdx+1)] - inLow[(BodyShortTrailingIdx+1)]) : ((BodyShort_rangeType == 2) ? ((inHigh[(BodyShortTrailingIdx+1)] - inLow[(BodyShortTrailingIdx+1)]) - Math.abs(inClose[(BodyShortTrailingIdx+1)] - inOpen[(BodyShortTrailingIdx+1)])) : 0.0))));
         i += 1;
         BodyLongTrailingIdx += 1;
         BodyShortTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdleveningstarLogic( int startIdx,
                                       int endIdx,
                                       double inOpen[],
                                       double inHigh[],
                                       double inLow[],
                                       double inClose[],
                                       double optInPenetration,
                                       MInteger outBegIdx,
                                       MInteger outNBElement,
                                       int outInteger[] )
   {
      double BodyShortPeriodTotal = 0;
      double BodyLongPeriodTotal = 0;
      double BodyShortPeriodTotal2 = 0;
      int i = 0;
      int outIdx = 0;
      int BodyShortTrailingIdx = 0;
      int BodyLongTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyLong_rangeType = this.candleSettings.bodyLong.rangeType;
      int BodyLong_avgPeriod = this.candleSettings.bodyLong.avgPeriod;
      double BodyLong_factor = this.candleSettings.bodyLong.factor;
      int BodyShort_rangeType = this.candleSettings.bodyShort.rangeType;
      int BodyShort_avgPeriod = this.candleSettings.bodyShort.avgPeriod;
      double BodyShort_factor = this.candleSettings.bodyShort.factor;
      lookbackTotal = cdleveningstarLookback(optInPenetration);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      BodyLongPeriodTotal = 0;
      BodyShortPeriodTotal = 0;
      BodyShortPeriodTotal2 = 0;
      BodyLongTrailingIdx = ((startIdx-2)-BodyLong_avgPeriod);
      BodyShortTrailingIdx = ((startIdx-1)-BodyShort_avgPeriod);
      i = BodyLongTrailingIdx;
      while( (i<(startIdx-2)) ) {
         BodyLongPeriodTotal += ((BodyLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = BodyShortTrailingIdx;
      while( (i<(startIdx-1)) ) {
         BodyShortPeriodTotal += ((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         BodyShortPeriodTotal2 += ((BodyShort_rangeType == 0) ? (Math.abs(inClose[(i+1)] - inOpen[(i+1)])) : ((BodyShort_rangeType == 1) ? (inHigh[(i+1)] - inLow[(i+1)]) : ((BodyShort_rangeType == 2) ? ((inHigh[(i+1)] - inLow[(i+1)]) - Math.abs(inClose[(i+1)] - inOpen[(i+1)])) : 0.0)));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( ((((((((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==1)&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==(0-1)))&&(Math.min(inOpen[(i-1)], inClose[(i-1)])>Math.max(inOpen[(i-2)], inClose[(i-2)])))&&(inClose[i]<(inClose[(i-2)]-(Math.abs((inClose[(i-2)]-inOpen[(i-2)]))*optInPenetration))))&&(Math.abs((inClose[(i-2)]-inOpen[(i-2)]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyLongPeriodTotal / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))<=((BodyShort_factor * (((BodyShort_avgPeriod != 0) ? (BodyShortPeriodTotal / BodyShort_avgPeriod) : ((BodyShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((BodyShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((BodyShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((BodyShort_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.abs((inClose[i]-inOpen[i]))>((BodyShort_factor * (((BodyShort_avgPeriod != 0) ? (BodyShortPeriodTotal2 / BodyShort_avgPeriod) : ((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((BodyShort_rangeType == 2) ? 2.0 : 1.0)))))) ) {
            outInteger[outIdx++] = (0-100);
         } else {
            outInteger[outIdx++] = 0;
         }
         BodyLongPeriodTotal += (((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))-((BodyLong_rangeType == 0) ? (Math.abs(inClose[BodyLongTrailingIdx] - inOpen[BodyLongTrailingIdx])) : ((BodyLong_rangeType == 1) ? (inHigh[BodyLongTrailingIdx] - inLow[BodyLongTrailingIdx]) : ((BodyLong_rangeType == 2) ? ((inHigh[BodyLongTrailingIdx] - inLow[BodyLongTrailingIdx]) - Math.abs(inClose[BodyLongTrailingIdx] - inOpen[BodyLongTrailingIdx])) : 0.0))));
         BodyShortPeriodTotal += (((BodyShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((BodyShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((BodyShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))-((BodyShort_rangeType == 0) ? (Math.abs(inClose[BodyShortTrailingIdx] - inOpen[BodyShortTrailingIdx])) : ((BodyShort_rangeType == 1) ? (inHigh[BodyShortTrailingIdx] - inLow[BodyShortTrailingIdx]) : ((BodyShort_rangeType == 2) ? ((inHigh[BodyShortTrailingIdx] - inLow[BodyShortTrailingIdx]) - Math.abs(inClose[BodyShortTrailingIdx] - inOpen[BodyShortTrailingIdx])) : 0.0))));
         BodyShortPeriodTotal2 += (((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((BodyShort_rangeType == 0) ? (Math.abs(inClose[(BodyShortTrailingIdx+1)] - inOpen[(BodyShortTrailingIdx+1)])) : ((BodyShort_rangeType == 1) ? (inHigh[(BodyShortTrailingIdx+1)] - inLow[(BodyShortTrailingIdx+1)]) : ((BodyShort_rangeType == 2) ? ((inHigh[(BodyShortTrailingIdx+1)] - inLow[(BodyShortTrailingIdx+1)]) - Math.abs(inClose[(BodyShortTrailingIdx+1)] - inOpen[(BodyShortTrailingIdx+1)])) : 0.0))));
         i += 1;
         BodyLongTrailingIdx += 1;
         BodyShortTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdleveningstar( int startIdx,
                                  int endIdx,
                                  float inOpen[],
                                  float inHigh[],
                                  float inLow[],
                                  float inClose[],
                                  double optInPenetration,
                                  MInteger outBegIdx,
                                  MInteger outNBElement,
                                  int outInteger[] )
   {
      double BodyShortPeriodTotal = 0;
      double BodyLongPeriodTotal = 0;
      double BodyShortPeriodTotal2 = 0;
      int i = 0;
      int outIdx = 0;
      int BodyShortTrailingIdx = 0;
      int BodyLongTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyLong_rangeType = this.candleSettings.bodyLong.rangeType;
      int BodyLong_avgPeriod = this.candleSettings.bodyLong.avgPeriod;
      double BodyLong_factor = this.candleSettings.bodyLong.factor;
      int BodyShort_rangeType = this.candleSettings.bodyShort.rangeType;
      int BodyShort_avgPeriod = this.candleSettings.bodyShort.avgPeriod;
      double BodyShort_factor = this.candleSettings.bodyShort.factor;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackTotal = cdleveningstarLookback(optInPenetration);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      BodyLongPeriodTotal = 0;
      BodyShortPeriodTotal = 0;
      BodyShortPeriodTotal2 = 0;
      BodyLongTrailingIdx = ((startIdx-2)-BodyLong_avgPeriod);
      BodyShortTrailingIdx = ((startIdx-1)-BodyShort_avgPeriod);
      i = BodyLongTrailingIdx;
      while( (i<(startIdx-2)) ) {
         BodyLongPeriodTotal += ((BodyLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = BodyShortTrailingIdx;
      while( (i<(startIdx-1)) ) {
         BodyShortPeriodTotal += ((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         BodyShortPeriodTotal2 += ((BodyShort_rangeType == 0) ? (Math.abs(inClose[(i+1)] - inOpen[(i+1)])) : ((BodyShort_rangeType == 1) ? (inHigh[(i+1)] - inLow[(i+1)]) : ((BodyShort_rangeType == 2) ? ((inHigh[(i+1)] - inLow[(i+1)]) - Math.abs(inClose[(i+1)] - inOpen[(i+1)])) : 0.0)));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( ((((((((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==1)&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==(0-1)))&&(Math.min(inOpen[(i-1)], inClose[(i-1)])>Math.max(inOpen[(i-2)], inClose[(i-2)])))&&(inClose[i]<(inClose[(i-2)]-(Math.abs((inClose[(i-2)]-inOpen[(i-2)]))*optInPenetration))))&&(Math.abs((inClose[(i-2)]-inOpen[(i-2)]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyLongPeriodTotal / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))<=((BodyShort_factor * (((BodyShort_avgPeriod != 0) ? (BodyShortPeriodTotal / BodyShort_avgPeriod) : ((BodyShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((BodyShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((BodyShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((BodyShort_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.abs((inClose[i]-inOpen[i]))>((BodyShort_factor * (((BodyShort_avgPeriod != 0) ? (BodyShortPeriodTotal2 / BodyShort_avgPeriod) : ((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((BodyShort_rangeType == 2) ? 2.0 : 1.0)))))) ) {
            outInteger[outIdx++] = (0-100);
         } else {
            outInteger[outIdx++] = 0;
         }
         BodyLongPeriodTotal += (((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))-((BodyLong_rangeType == 0) ? (Math.abs(inClose[BodyLongTrailingIdx] - inOpen[BodyLongTrailingIdx])) : ((BodyLong_rangeType == 1) ? (inHigh[BodyLongTrailingIdx] - inLow[BodyLongTrailingIdx]) : ((BodyLong_rangeType == 2) ? ((inHigh[BodyLongTrailingIdx] - inLow[BodyLongTrailingIdx]) - Math.abs(inClose[BodyLongTrailingIdx] - inOpen[BodyLongTrailingIdx])) : 0.0))));
         BodyShortPeriodTotal += (((BodyShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((BodyShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((BodyShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))-((BodyShort_rangeType == 0) ? (Math.abs(inClose[BodyShortTrailingIdx] - inOpen[BodyShortTrailingIdx])) : ((BodyShort_rangeType == 1) ? (inHigh[BodyShortTrailingIdx] - inLow[BodyShortTrailingIdx]) : ((BodyShort_rangeType == 2) ? ((inHigh[BodyShortTrailingIdx] - inLow[BodyShortTrailingIdx]) - Math.abs(inClose[BodyShortTrailingIdx] - inOpen[BodyShortTrailingIdx])) : 0.0))));
         BodyShortPeriodTotal2 += (((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((BodyShort_rangeType == 0) ? (Math.abs(inClose[(BodyShortTrailingIdx+1)] - inOpen[(BodyShortTrailingIdx+1)])) : ((BodyShort_rangeType == 1) ? (inHigh[(BodyShortTrailingIdx+1)] - inLow[(BodyShortTrailingIdx+1)]) : ((BodyShort_rangeType == 2) ? ((inHigh[(BodyShortTrailingIdx+1)] - inLow[(BodyShortTrailingIdx+1)]) - Math.abs(inClose[(BodyShortTrailingIdx+1)] - inOpen[(BodyShortTrailingIdx+1)])) : 0.0))));
         i += 1;
         BodyLongTrailingIdx += 1;
         BodyShortTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdleveningstarLogic( int startIdx,
                                       int endIdx,
                                       float inOpen[],
                                       float inHigh[],
                                       float inLow[],
                                       float inClose[],
                                       double optInPenetration,
                                       MInteger outBegIdx,
                                       MInteger outNBElement,
                                       int outInteger[] )
   {
      double BodyShortPeriodTotal = 0;
      double BodyLongPeriodTotal = 0;
      double BodyShortPeriodTotal2 = 0;
      int i = 0;
      int outIdx = 0;
      int BodyShortTrailingIdx = 0;
      int BodyLongTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyLong_rangeType = this.candleSettings.bodyLong.rangeType;
      int BodyLong_avgPeriod = this.candleSettings.bodyLong.avgPeriod;
      double BodyLong_factor = this.candleSettings.bodyLong.factor;
      int BodyShort_rangeType = this.candleSettings.bodyShort.rangeType;
      int BodyShort_avgPeriod = this.candleSettings.bodyShort.avgPeriod;
      double BodyShort_factor = this.candleSettings.bodyShort.factor;
      lookbackTotal = cdleveningstarLookback(optInPenetration);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      BodyLongPeriodTotal = 0;
      BodyShortPeriodTotal = 0;
      BodyShortPeriodTotal2 = 0;
      BodyLongTrailingIdx = ((startIdx-2)-BodyLong_avgPeriod);
      BodyShortTrailingIdx = ((startIdx-1)-BodyShort_avgPeriod);
      i = BodyLongTrailingIdx;
      while( (i<(startIdx-2)) ) {
         BodyLongPeriodTotal += ((BodyLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         i += 1;
      }
      i = BodyShortTrailingIdx;
      while( (i<(startIdx-1)) ) {
         BodyShortPeriodTotal += ((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)));
         BodyShortPeriodTotal2 += ((BodyShort_rangeType == 0) ? (Math.abs(inClose[(i+1)] - inOpen[(i+1)])) : ((BodyShort_rangeType == 1) ? (inHigh[(i+1)] - inLow[(i+1)]) : ((BodyShort_rangeType == 2) ? ((inHigh[(i+1)] - inLow[(i+1)]) - Math.abs(inClose[(i+1)] - inOpen[(i+1)])) : 0.0)));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( ((((((((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==1)&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==(0-1)))&&(Math.min(inOpen[(i-1)], inClose[(i-1)])>Math.max(inOpen[(i-2)], inClose[(i-2)])))&&(inClose[i]<(inClose[(i-2)]-(Math.abs((inClose[(i-2)]-inOpen[(i-2)]))*optInPenetration))))&&(Math.abs((inClose[(i-2)]-inOpen[(i-2)]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyLongPeriodTotal / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))<=((BodyShort_factor * (((BodyShort_avgPeriod != 0) ? (BodyShortPeriodTotal / BodyShort_avgPeriod) : ((BodyShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((BodyShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((BodyShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((BodyShort_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.abs((inClose[i]-inOpen[i]))>((BodyShort_factor * (((BodyShort_avgPeriod != 0) ? (BodyShortPeriodTotal2 / BodyShort_avgPeriod) : ((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((BodyShort_rangeType == 2) ? 2.0 : 1.0)))))) ) {
            outInteger[outIdx++] = (0-100);
         } else {
            outInteger[outIdx++] = 0;
         }
         BodyLongPeriodTotal += (((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))-((BodyLong_rangeType == 0) ? (Math.abs(inClose[BodyLongTrailingIdx] - inOpen[BodyLongTrailingIdx])) : ((BodyLong_rangeType == 1) ? (inHigh[BodyLongTrailingIdx] - inLow[BodyLongTrailingIdx]) : ((BodyLong_rangeType == 2) ? ((inHigh[BodyLongTrailingIdx] - inLow[BodyLongTrailingIdx]) - Math.abs(inClose[BodyLongTrailingIdx] - inOpen[BodyLongTrailingIdx])) : 0.0))));
         BodyShortPeriodTotal += (((BodyShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((BodyShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((BodyShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))-((BodyShort_rangeType == 0) ? (Math.abs(inClose[BodyShortTrailingIdx] - inOpen[BodyShortTrailingIdx])) : ((BodyShort_rangeType == 1) ? (inHigh[BodyShortTrailingIdx] - inLow[BodyShortTrailingIdx]) : ((BodyShort_rangeType == 2) ? ((inHigh[BodyShortTrailingIdx] - inLow[BodyShortTrailingIdx]) - Math.abs(inClose[BodyShortTrailingIdx] - inOpen[BodyShortTrailingIdx])) : 0.0))));
         BodyShortPeriodTotal2 += (((BodyShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((BodyShort_rangeType == 0) ? (Math.abs(inClose[(BodyShortTrailingIdx+1)] - inOpen[(BodyShortTrailingIdx+1)])) : ((BodyShort_rangeType == 1) ? (inHigh[(BodyShortTrailingIdx+1)] - inLow[(BodyShortTrailingIdx+1)]) : ((BodyShort_rangeType == 2) ? ((inHigh[(BodyShortTrailingIdx+1)] - inLow[(BodyShortTrailingIdx+1)]) - Math.abs(inClose[(BodyShortTrailingIdx+1)] - inOpen[(BodyShortTrailingIdx+1)])) : 0.0))));
         i += 1;
         BodyLongTrailingIdx += 1;
         BodyShortTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
