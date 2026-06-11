/* Generated */
   public int cdlmatchinglowLookback( )
   {
      int Equal_rangeType = this.candleSettings.equal.rangeType;
      int Equal_avgPeriod = this.candleSettings.equal.avgPeriod;
      double Equal_factor = this.candleSettings.equal.factor;
      return (Equal_avgPeriod+1) ;

   }
   public RetCode cdlmatchinglow( int startIdx,
                                  int endIdx,
                                  double inOpen[],
                                  double inHigh[],
                                  double inLow[],
                                  double inClose[],
                                  MInteger outBegIdx,
                                  MInteger outNBElement,
                                  int outInteger[] )
   {
      double EqualPeriodTotal = 0;
      int i = 0;
      int outIdx = 0;
      int EqualTrailingIdx = 0;
      int lookbackTotal = 0;
      int Equal_rangeType = this.candleSettings.equal.rangeType;
      int Equal_avgPeriod = this.candleSettings.equal.avgPeriod;
      double Equal_factor = this.candleSettings.equal.factor;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackTotal = cdlmatchinglowLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      EqualPeriodTotal = 0;
      EqualTrailingIdx = (startIdx-Equal_avgPeriod);
      i = EqualTrailingIdx;
      while( (i<startIdx) ) {
         EqualPeriodTotal += ((Equal_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Equal_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( (((((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-1))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==(0-1)))&&(inClose[i]<=(inClose[(i-1)]+((Equal_factor * (((Equal_avgPeriod != 0) ? (EqualPeriodTotal / Equal_avgPeriod) : ((Equal_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Equal_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Equal_rangeType == 2) ? 2.0 : 1.0)))))))&&(inClose[i]>=(inClose[(i-1)]-((Equal_factor * (((Equal_avgPeriod != 0) ? (EqualPeriodTotal / Equal_avgPeriod) : ((Equal_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Equal_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Equal_rangeType == 2) ? 2.0 : 1.0))))))) ) {
            outInteger[outIdx++] = 100;
         } else {
            outInteger[outIdx++] = 0;
         }
         EqualPeriodTotal += (((Equal_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Equal_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))-((Equal_rangeType == 0) ? (Math.abs(inClose[(EqualTrailingIdx-1)] - inOpen[(EqualTrailingIdx-1)])) : ((Equal_rangeType == 1) ? (inHigh[(EqualTrailingIdx-1)] - inLow[(EqualTrailingIdx-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(EqualTrailingIdx-1)] - inLow[(EqualTrailingIdx-1)]) - Math.abs(inClose[(EqualTrailingIdx-1)] - inOpen[(EqualTrailingIdx-1)])) : 0.0))));
         i += 1;
         EqualTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdlmatchinglowLogic( int startIdx,
                                       int endIdx,
                                       double inOpen[],
                                       double inHigh[],
                                       double inLow[],
                                       double inClose[],
                                       MInteger outBegIdx,
                                       MInteger outNBElement,
                                       int outInteger[] )
   {
      double EqualPeriodTotal = 0;
      int i = 0;
      int outIdx = 0;
      int EqualTrailingIdx = 0;
      int lookbackTotal = 0;
      int Equal_rangeType = this.candleSettings.equal.rangeType;
      int Equal_avgPeriod = this.candleSettings.equal.avgPeriod;
      double Equal_factor = this.candleSettings.equal.factor;
      lookbackTotal = cdlmatchinglowLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      EqualPeriodTotal = 0;
      EqualTrailingIdx = (startIdx-Equal_avgPeriod);
      i = EqualTrailingIdx;
      while( (i<startIdx) ) {
         EqualPeriodTotal += ((Equal_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Equal_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( (((((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-1))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==(0-1)))&&(inClose[i]<=(inClose[(i-1)]+((Equal_factor * (((Equal_avgPeriod != 0) ? (EqualPeriodTotal / Equal_avgPeriod) : ((Equal_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Equal_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Equal_rangeType == 2) ? 2.0 : 1.0)))))))&&(inClose[i]>=(inClose[(i-1)]-((Equal_factor * (((Equal_avgPeriod != 0) ? (EqualPeriodTotal / Equal_avgPeriod) : ((Equal_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Equal_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Equal_rangeType == 2) ? 2.0 : 1.0))))))) ) {
            outInteger[outIdx++] = 100;
         } else {
            outInteger[outIdx++] = 0;
         }
         EqualPeriodTotal += (((Equal_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Equal_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))-((Equal_rangeType == 0) ? (Math.abs(inClose[(EqualTrailingIdx-1)] - inOpen[(EqualTrailingIdx-1)])) : ((Equal_rangeType == 1) ? (inHigh[(EqualTrailingIdx-1)] - inLow[(EqualTrailingIdx-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(EqualTrailingIdx-1)] - inLow[(EqualTrailingIdx-1)]) - Math.abs(inClose[(EqualTrailingIdx-1)] - inOpen[(EqualTrailingIdx-1)])) : 0.0))));
         i += 1;
         EqualTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdlmatchinglow( int startIdx,
                                  int endIdx,
                                  float inOpen[],
                                  float inHigh[],
                                  float inLow[],
                                  float inClose[],
                                  MInteger outBegIdx,
                                  MInteger outNBElement,
                                  int outInteger[] )
   {
      double EqualPeriodTotal = 0;
      int i = 0;
      int outIdx = 0;
      int EqualTrailingIdx = 0;
      int lookbackTotal = 0;
      int Equal_rangeType = this.candleSettings.equal.rangeType;
      int Equal_avgPeriod = this.candleSettings.equal.avgPeriod;
      double Equal_factor = this.candleSettings.equal.factor;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackTotal = cdlmatchinglowLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      EqualPeriodTotal = 0;
      EqualTrailingIdx = (startIdx-Equal_avgPeriod);
      i = EqualTrailingIdx;
      while( (i<startIdx) ) {
         EqualPeriodTotal += ((Equal_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Equal_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( (((((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-1))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==(0-1)))&&(inClose[i]<=(inClose[(i-1)]+((Equal_factor * (((Equal_avgPeriod != 0) ? (EqualPeriodTotal / Equal_avgPeriod) : ((Equal_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Equal_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Equal_rangeType == 2) ? 2.0 : 1.0)))))))&&(inClose[i]>=(inClose[(i-1)]-((Equal_factor * (((Equal_avgPeriod != 0) ? (EqualPeriodTotal / Equal_avgPeriod) : ((Equal_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Equal_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Equal_rangeType == 2) ? 2.0 : 1.0))))))) ) {
            outInteger[outIdx++] = 100;
         } else {
            outInteger[outIdx++] = 0;
         }
         EqualPeriodTotal += (((Equal_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Equal_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))-((Equal_rangeType == 0) ? (Math.abs(inClose[(EqualTrailingIdx-1)] - inOpen[(EqualTrailingIdx-1)])) : ((Equal_rangeType == 1) ? (inHigh[(EqualTrailingIdx-1)] - inLow[(EqualTrailingIdx-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(EqualTrailingIdx-1)] - inLow[(EqualTrailingIdx-1)]) - Math.abs(inClose[(EqualTrailingIdx-1)] - inOpen[(EqualTrailingIdx-1)])) : 0.0))));
         i += 1;
         EqualTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdlmatchinglowLogic( int startIdx,
                                       int endIdx,
                                       float inOpen[],
                                       float inHigh[],
                                       float inLow[],
                                       float inClose[],
                                       MInteger outBegIdx,
                                       MInteger outNBElement,
                                       int outInteger[] )
   {
      double EqualPeriodTotal = 0;
      int i = 0;
      int outIdx = 0;
      int EqualTrailingIdx = 0;
      int lookbackTotal = 0;
      int Equal_rangeType = this.candleSettings.equal.rangeType;
      int Equal_avgPeriod = this.candleSettings.equal.avgPeriod;
      double Equal_factor = this.candleSettings.equal.factor;
      lookbackTotal = cdlmatchinglowLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      EqualPeriodTotal = 0;
      EqualTrailingIdx = (startIdx-Equal_avgPeriod);
      i = EqualTrailingIdx;
      while( (i<startIdx) ) {
         EqualPeriodTotal += ((Equal_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Equal_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( (((((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-1))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==(0-1)))&&(inClose[i]<=(inClose[(i-1)]+((Equal_factor * (((Equal_avgPeriod != 0) ? (EqualPeriodTotal / Equal_avgPeriod) : ((Equal_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Equal_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Equal_rangeType == 2) ? 2.0 : 1.0)))))))&&(inClose[i]>=(inClose[(i-1)]-((Equal_factor * (((Equal_avgPeriod != 0) ? (EqualPeriodTotal / Equal_avgPeriod) : ((Equal_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Equal_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Equal_rangeType == 2) ? 2.0 : 1.0))))))) ) {
            outInteger[outIdx++] = 100;
         } else {
            outInteger[outIdx++] = 0;
         }
         EqualPeriodTotal += (((Equal_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Equal_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))-((Equal_rangeType == 0) ? (Math.abs(inClose[(EqualTrailingIdx-1)] - inOpen[(EqualTrailingIdx-1)])) : ((Equal_rangeType == 1) ? (inHigh[(EqualTrailingIdx-1)] - inLow[(EqualTrailingIdx-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(EqualTrailingIdx-1)] - inLow[(EqualTrailingIdx-1)]) - Math.abs(inClose[(EqualTrailingIdx-1)] - inOpen[(EqualTrailingIdx-1)])) : 0.0))));
         i += 1;
         EqualTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
