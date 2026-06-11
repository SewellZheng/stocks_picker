/* Generated */
   public int cdlgapsidesidewhiteLookback( )
   {
      int Equal_rangeType = this.candleSettings.equal.rangeType;
      int Equal_avgPeriod = this.candleSettings.equal.avgPeriod;
      double Equal_factor = this.candleSettings.equal.factor;
      int Near_rangeType = this.candleSettings.near.rangeType;
      int Near_avgPeriod = this.candleSettings.near.avgPeriod;
      double Near_factor = this.candleSettings.near.factor;
      return (Math.max(Near_avgPeriod, Equal_avgPeriod)+2) ;

   }
   public RetCode cdlgapsidesidewhite( int startIdx,
                                       int endIdx,
                                       double inOpen[],
                                       double inHigh[],
                                       double inLow[],
                                       double inClose[],
                                       MInteger outBegIdx,
                                       MInteger outNBElement,
                                       int outInteger[] )
   {
      double NearPeriodTotal = 0;
      double EqualPeriodTotal = 0;
      int i = 0;
      int outIdx = 0;
      int NearTrailingIdx = 0;
      int EqualTrailingIdx = 0;
      int lookbackTotal = 0;
      int Equal_rangeType = this.candleSettings.equal.rangeType;
      int Equal_avgPeriod = this.candleSettings.equal.avgPeriod;
      double Equal_factor = this.candleSettings.equal.factor;
      int Near_rangeType = this.candleSettings.near.rangeType;
      int Near_avgPeriod = this.candleSettings.near.avgPeriod;
      double Near_factor = this.candleSettings.near.factor;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackTotal = cdlgapsidesidewhiteLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      NearPeriodTotal = 0;
      EqualPeriodTotal = 0;
      NearTrailingIdx = (startIdx-Near_avgPeriod);
      EqualTrailingIdx = (startIdx-Equal_avgPeriod);
      i = NearTrailingIdx;
      while( (i<startIdx) ) {
         NearPeriodTotal += ((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)));
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
         if( (((((((((Math.min(inOpen[(i-1)], inClose[(i-1)])>Math.max(inOpen[(i-2)], inClose[(i-2)]))&&(Math.min(inOpen[i], inClose[i])>Math.max(inOpen[(i-2)], inClose[(i-2)])))||((Math.max(inOpen[(i-1)], inClose[(i-1)])<Math.min(inOpen[(i-2)], inClose[(i-2)]))&&(Math.max(inOpen[i], inClose[i])<Math.min(inOpen[(i-2)], inClose[(i-2)]))))&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==1))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==1))&&(Math.abs((inClose[i]-inOpen[i]))>=(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))-((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0)))))))&&(Math.abs((inClose[i]-inOpen[i]))<=(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))+((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0)))))))&&(inOpen[i]>=(inOpen[(i-1)]-((Equal_factor * (((Equal_avgPeriod != 0) ? (EqualPeriodTotal / Equal_avgPeriod) : ((Equal_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Equal_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Equal_rangeType == 2) ? 2.0 : 1.0)))))))&&(inOpen[i]<=(inOpen[(i-1)]+((Equal_factor * (((Equal_avgPeriod != 0) ? (EqualPeriodTotal / Equal_avgPeriod) : ((Equal_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Equal_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Equal_rangeType == 2) ? 2.0 : 1.0))))))) ) {
            outInteger[outIdx++] = (((Math.min(inOpen[(i-1)], inClose[(i-1)])>Math.max(inOpen[(i-2)], inClose[(i-2)]))) ? (100) : ((0-100)));
         } else {
            outInteger[outIdx++] = 0;
         }
         NearPeriodTotal += (((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))-((Near_rangeType == 0) ? (Math.abs(inClose[(NearTrailingIdx-1)] - inOpen[(NearTrailingIdx-1)])) : ((Near_rangeType == 1) ? (inHigh[(NearTrailingIdx-1)] - inLow[(NearTrailingIdx-1)]) : ((Near_rangeType == 2) ? ((inHigh[(NearTrailingIdx-1)] - inLow[(NearTrailingIdx-1)]) - Math.abs(inClose[(NearTrailingIdx-1)] - inOpen[(NearTrailingIdx-1)])) : 0.0))));
         EqualPeriodTotal += (((Equal_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Equal_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))-((Equal_rangeType == 0) ? (Math.abs(inClose[(EqualTrailingIdx-1)] - inOpen[(EqualTrailingIdx-1)])) : ((Equal_rangeType == 1) ? (inHigh[(EqualTrailingIdx-1)] - inLow[(EqualTrailingIdx-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(EqualTrailingIdx-1)] - inLow[(EqualTrailingIdx-1)]) - Math.abs(inClose[(EqualTrailingIdx-1)] - inOpen[(EqualTrailingIdx-1)])) : 0.0))));
         i += 1;
         NearTrailingIdx += 1;
         EqualTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdlgapsidesidewhiteLogic( int startIdx,
                                            int endIdx,
                                            double inOpen[],
                                            double inHigh[],
                                            double inLow[],
                                            double inClose[],
                                            MInteger outBegIdx,
                                            MInteger outNBElement,
                                            int outInteger[] )
   {
      double NearPeriodTotal = 0;
      double EqualPeriodTotal = 0;
      int i = 0;
      int outIdx = 0;
      int NearTrailingIdx = 0;
      int EqualTrailingIdx = 0;
      int lookbackTotal = 0;
      int Equal_rangeType = this.candleSettings.equal.rangeType;
      int Equal_avgPeriod = this.candleSettings.equal.avgPeriod;
      double Equal_factor = this.candleSettings.equal.factor;
      int Near_rangeType = this.candleSettings.near.rangeType;
      int Near_avgPeriod = this.candleSettings.near.avgPeriod;
      double Near_factor = this.candleSettings.near.factor;
      lookbackTotal = cdlgapsidesidewhiteLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      NearPeriodTotal = 0;
      EqualPeriodTotal = 0;
      NearTrailingIdx = (startIdx-Near_avgPeriod);
      EqualTrailingIdx = (startIdx-Equal_avgPeriod);
      i = NearTrailingIdx;
      while( (i<startIdx) ) {
         NearPeriodTotal += ((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)));
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
         if( (((((((((Math.min(inOpen[(i-1)], inClose[(i-1)])>Math.max(inOpen[(i-2)], inClose[(i-2)]))&&(Math.min(inOpen[i], inClose[i])>Math.max(inOpen[(i-2)], inClose[(i-2)])))||((Math.max(inOpen[(i-1)], inClose[(i-1)])<Math.min(inOpen[(i-2)], inClose[(i-2)]))&&(Math.max(inOpen[i], inClose[i])<Math.min(inOpen[(i-2)], inClose[(i-2)]))))&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==1))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==1))&&(Math.abs((inClose[i]-inOpen[i]))>=(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))-((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0)))))))&&(Math.abs((inClose[i]-inOpen[i]))<=(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))+((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0)))))))&&(inOpen[i]>=(inOpen[(i-1)]-((Equal_factor * (((Equal_avgPeriod != 0) ? (EqualPeriodTotal / Equal_avgPeriod) : ((Equal_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Equal_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Equal_rangeType == 2) ? 2.0 : 1.0)))))))&&(inOpen[i]<=(inOpen[(i-1)]+((Equal_factor * (((Equal_avgPeriod != 0) ? (EqualPeriodTotal / Equal_avgPeriod) : ((Equal_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Equal_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Equal_rangeType == 2) ? 2.0 : 1.0))))))) ) {
            outInteger[outIdx++] = (((Math.min(inOpen[(i-1)], inClose[(i-1)])>Math.max(inOpen[(i-2)], inClose[(i-2)]))) ? (100) : ((0-100)));
         } else {
            outInteger[outIdx++] = 0;
         }
         NearPeriodTotal += (((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))-((Near_rangeType == 0) ? (Math.abs(inClose[(NearTrailingIdx-1)] - inOpen[(NearTrailingIdx-1)])) : ((Near_rangeType == 1) ? (inHigh[(NearTrailingIdx-1)] - inLow[(NearTrailingIdx-1)]) : ((Near_rangeType == 2) ? ((inHigh[(NearTrailingIdx-1)] - inLow[(NearTrailingIdx-1)]) - Math.abs(inClose[(NearTrailingIdx-1)] - inOpen[(NearTrailingIdx-1)])) : 0.0))));
         EqualPeriodTotal += (((Equal_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Equal_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))-((Equal_rangeType == 0) ? (Math.abs(inClose[(EqualTrailingIdx-1)] - inOpen[(EqualTrailingIdx-1)])) : ((Equal_rangeType == 1) ? (inHigh[(EqualTrailingIdx-1)] - inLow[(EqualTrailingIdx-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(EqualTrailingIdx-1)] - inLow[(EqualTrailingIdx-1)]) - Math.abs(inClose[(EqualTrailingIdx-1)] - inOpen[(EqualTrailingIdx-1)])) : 0.0))));
         i += 1;
         NearTrailingIdx += 1;
         EqualTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdlgapsidesidewhite( int startIdx,
                                       int endIdx,
                                       float inOpen[],
                                       float inHigh[],
                                       float inLow[],
                                       float inClose[],
                                       MInteger outBegIdx,
                                       MInteger outNBElement,
                                       int outInteger[] )
   {
      double NearPeriodTotal = 0;
      double EqualPeriodTotal = 0;
      int i = 0;
      int outIdx = 0;
      int NearTrailingIdx = 0;
      int EqualTrailingIdx = 0;
      int lookbackTotal = 0;
      int Equal_rangeType = this.candleSettings.equal.rangeType;
      int Equal_avgPeriod = this.candleSettings.equal.avgPeriod;
      double Equal_factor = this.candleSettings.equal.factor;
      int Near_rangeType = this.candleSettings.near.rangeType;
      int Near_avgPeriod = this.candleSettings.near.avgPeriod;
      double Near_factor = this.candleSettings.near.factor;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackTotal = cdlgapsidesidewhiteLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      NearPeriodTotal = 0;
      EqualPeriodTotal = 0;
      NearTrailingIdx = (startIdx-Near_avgPeriod);
      EqualTrailingIdx = (startIdx-Equal_avgPeriod);
      i = NearTrailingIdx;
      while( (i<startIdx) ) {
         NearPeriodTotal += ((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)));
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
         if( (((((((((Math.min(inOpen[(i-1)], inClose[(i-1)])>Math.max(inOpen[(i-2)], inClose[(i-2)]))&&(Math.min(inOpen[i], inClose[i])>Math.max(inOpen[(i-2)], inClose[(i-2)])))||((Math.max(inOpen[(i-1)], inClose[(i-1)])<Math.min(inOpen[(i-2)], inClose[(i-2)]))&&(Math.max(inOpen[i], inClose[i])<Math.min(inOpen[(i-2)], inClose[(i-2)]))))&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==1))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==1))&&(Math.abs((inClose[i]-inOpen[i]))>=(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))-((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0)))))))&&(Math.abs((inClose[i]-inOpen[i]))<=(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))+((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0)))))))&&(inOpen[i]>=(inOpen[(i-1)]-((Equal_factor * (((Equal_avgPeriod != 0) ? (EqualPeriodTotal / Equal_avgPeriod) : ((Equal_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Equal_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Equal_rangeType == 2) ? 2.0 : 1.0)))))))&&(inOpen[i]<=(inOpen[(i-1)]+((Equal_factor * (((Equal_avgPeriod != 0) ? (EqualPeriodTotal / Equal_avgPeriod) : ((Equal_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Equal_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Equal_rangeType == 2) ? 2.0 : 1.0))))))) ) {
            outInteger[outIdx++] = (((Math.min(inOpen[(i-1)], inClose[(i-1)])>Math.max(inOpen[(i-2)], inClose[(i-2)]))) ? (100) : ((0-100)));
         } else {
            outInteger[outIdx++] = 0;
         }
         NearPeriodTotal += (((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))-((Near_rangeType == 0) ? (Math.abs(inClose[(NearTrailingIdx-1)] - inOpen[(NearTrailingIdx-1)])) : ((Near_rangeType == 1) ? (inHigh[(NearTrailingIdx-1)] - inLow[(NearTrailingIdx-1)]) : ((Near_rangeType == 2) ? ((inHigh[(NearTrailingIdx-1)] - inLow[(NearTrailingIdx-1)]) - Math.abs(inClose[(NearTrailingIdx-1)] - inOpen[(NearTrailingIdx-1)])) : 0.0))));
         EqualPeriodTotal += (((Equal_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Equal_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))-((Equal_rangeType == 0) ? (Math.abs(inClose[(EqualTrailingIdx-1)] - inOpen[(EqualTrailingIdx-1)])) : ((Equal_rangeType == 1) ? (inHigh[(EqualTrailingIdx-1)] - inLow[(EqualTrailingIdx-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(EqualTrailingIdx-1)] - inLow[(EqualTrailingIdx-1)]) - Math.abs(inClose[(EqualTrailingIdx-1)] - inOpen[(EqualTrailingIdx-1)])) : 0.0))));
         i += 1;
         NearTrailingIdx += 1;
         EqualTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdlgapsidesidewhiteLogic( int startIdx,
                                            int endIdx,
                                            float inOpen[],
                                            float inHigh[],
                                            float inLow[],
                                            float inClose[],
                                            MInteger outBegIdx,
                                            MInteger outNBElement,
                                            int outInteger[] )
   {
      double NearPeriodTotal = 0;
      double EqualPeriodTotal = 0;
      int i = 0;
      int outIdx = 0;
      int NearTrailingIdx = 0;
      int EqualTrailingIdx = 0;
      int lookbackTotal = 0;
      int Equal_rangeType = this.candleSettings.equal.rangeType;
      int Equal_avgPeriod = this.candleSettings.equal.avgPeriod;
      double Equal_factor = this.candleSettings.equal.factor;
      int Near_rangeType = this.candleSettings.near.rangeType;
      int Near_avgPeriod = this.candleSettings.near.avgPeriod;
      double Near_factor = this.candleSettings.near.factor;
      lookbackTotal = cdlgapsidesidewhiteLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      NearPeriodTotal = 0;
      EqualPeriodTotal = 0;
      NearTrailingIdx = (startIdx-Near_avgPeriod);
      EqualTrailingIdx = (startIdx-Equal_avgPeriod);
      i = NearTrailingIdx;
      while( (i<startIdx) ) {
         NearPeriodTotal += ((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)));
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
         if( (((((((((Math.min(inOpen[(i-1)], inClose[(i-1)])>Math.max(inOpen[(i-2)], inClose[(i-2)]))&&(Math.min(inOpen[i], inClose[i])>Math.max(inOpen[(i-2)], inClose[(i-2)])))||((Math.max(inOpen[(i-1)], inClose[(i-1)])<Math.min(inOpen[(i-2)], inClose[(i-2)]))&&(Math.max(inOpen[i], inClose[i])<Math.min(inOpen[(i-2)], inClose[(i-2)]))))&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==1))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==1))&&(Math.abs((inClose[i]-inOpen[i]))>=(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))-((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0)))))))&&(Math.abs((inClose[i]-inOpen[i]))<=(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))+((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0)))))))&&(inOpen[i]>=(inOpen[(i-1)]-((Equal_factor * (((Equal_avgPeriod != 0) ? (EqualPeriodTotal / Equal_avgPeriod) : ((Equal_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Equal_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Equal_rangeType == 2) ? 2.0 : 1.0)))))))&&(inOpen[i]<=(inOpen[(i-1)]+((Equal_factor * (((Equal_avgPeriod != 0) ? (EqualPeriodTotal / Equal_avgPeriod) : ((Equal_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Equal_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Equal_rangeType == 2) ? 2.0 : 1.0))))))) ) {
            outInteger[outIdx++] = (((Math.min(inOpen[(i-1)], inClose[(i-1)])>Math.max(inOpen[(i-2)], inClose[(i-2)]))) ? (100) : ((0-100)));
         } else {
            outInteger[outIdx++] = 0;
         }
         NearPeriodTotal += (((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))-((Near_rangeType == 0) ? (Math.abs(inClose[(NearTrailingIdx-1)] - inOpen[(NearTrailingIdx-1)])) : ((Near_rangeType == 1) ? (inHigh[(NearTrailingIdx-1)] - inLow[(NearTrailingIdx-1)]) : ((Near_rangeType == 2) ? ((inHigh[(NearTrailingIdx-1)] - inLow[(NearTrailingIdx-1)]) - Math.abs(inClose[(NearTrailingIdx-1)] - inOpen[(NearTrailingIdx-1)])) : 0.0))));
         EqualPeriodTotal += (((Equal_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Equal_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))-((Equal_rangeType == 0) ? (Math.abs(inClose[(EqualTrailingIdx-1)] - inOpen[(EqualTrailingIdx-1)])) : ((Equal_rangeType == 1) ? (inHigh[(EqualTrailingIdx-1)] - inLow[(EqualTrailingIdx-1)]) : ((Equal_rangeType == 2) ? ((inHigh[(EqualTrailingIdx-1)] - inLow[(EqualTrailingIdx-1)]) - Math.abs(inClose[(EqualTrailingIdx-1)] - inOpen[(EqualTrailingIdx-1)])) : 0.0))));
         i += 1;
         NearTrailingIdx += 1;
         EqualTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
