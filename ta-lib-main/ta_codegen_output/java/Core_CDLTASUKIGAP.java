/* Generated */
   public int cdltasukigapLookback( )
   {
      int Near_rangeType = this.candleSettings.near.rangeType;
      int Near_avgPeriod = this.candleSettings.near.avgPeriod;
      double Near_factor = this.candleSettings.near.factor;
      return (Near_avgPeriod+2) ;

   }
   public RetCode cdltasukigap( int startIdx,
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
      int i = 0;
      int outIdx = 0;
      int NearTrailingIdx = 0;
      int lookbackTotal = 0;
      int Near_rangeType = this.candleSettings.near.rangeType;
      int Near_avgPeriod = this.candleSettings.near.avgPeriod;
      double Near_factor = this.candleSettings.near.factor;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackTotal = cdltasukigapLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      NearPeriodTotal = 0;
      NearTrailingIdx = (startIdx-Near_avgPeriod);
      i = NearTrailingIdx;
      while( (i<startIdx) ) {
         NearPeriodTotal += ((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( (((((((((Math.min(inOpen[(i-1)], inClose[(i-1)])>Math.max(inOpen[(i-2)], inClose[(i-2)]))&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==1))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==(0-1)))&&(inOpen[i]<inClose[(i-1)]))&&(inOpen[i]>inOpen[(i-1)]))&&(inClose[i]<inOpen[(i-1)]))&&(inClose[i]>Math.max(inClose[(i-2)], inOpen[(i-2)])))&&(Math.abs((Math.abs((inClose[(i-1)]-inOpen[(i-1)]))-Math.abs((inClose[i]-inOpen[i]))))<((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0))))))||((((((((Math.max(inOpen[(i-1)], inClose[(i-1)])<Math.min(inOpen[(i-2)], inClose[(i-2)]))&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-1)))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==1))&&(inOpen[i]<inOpen[(i-1)]))&&(inOpen[i]>inClose[(i-1)]))&&(inClose[i]>inOpen[(i-1)]))&&(inClose[i]<Math.min(inClose[(i-2)], inOpen[(i-2)])))&&(Math.abs((Math.abs((inClose[(i-1)]-inOpen[(i-1)]))-Math.abs((inClose[i]-inOpen[i]))))<((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0))))))) ) {
            outInteger[outIdx++] = ((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))*100);
         } else {
            outInteger[outIdx++] = 0;
         }
         NearPeriodTotal += (((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))-((Near_rangeType == 0) ? (Math.abs(inClose[(NearTrailingIdx-1)] - inOpen[(NearTrailingIdx-1)])) : ((Near_rangeType == 1) ? (inHigh[(NearTrailingIdx-1)] - inLow[(NearTrailingIdx-1)]) : ((Near_rangeType == 2) ? ((inHigh[(NearTrailingIdx-1)] - inLow[(NearTrailingIdx-1)]) - Math.abs(inClose[(NearTrailingIdx-1)] - inOpen[(NearTrailingIdx-1)])) : 0.0))));
         i += 1;
         NearTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdltasukigapLogic( int startIdx,
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
      int i = 0;
      int outIdx = 0;
      int NearTrailingIdx = 0;
      int lookbackTotal = 0;
      int Near_rangeType = this.candleSettings.near.rangeType;
      int Near_avgPeriod = this.candleSettings.near.avgPeriod;
      double Near_factor = this.candleSettings.near.factor;
      lookbackTotal = cdltasukigapLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      NearPeriodTotal = 0;
      NearTrailingIdx = (startIdx-Near_avgPeriod);
      i = NearTrailingIdx;
      while( (i<startIdx) ) {
         NearPeriodTotal += ((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( (((((((((Math.min(inOpen[(i-1)], inClose[(i-1)])>Math.max(inOpen[(i-2)], inClose[(i-2)]))&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==1))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==(0-1)))&&(inOpen[i]<inClose[(i-1)]))&&(inOpen[i]>inOpen[(i-1)]))&&(inClose[i]<inOpen[(i-1)]))&&(inClose[i]>Math.max(inClose[(i-2)], inOpen[(i-2)])))&&(Math.abs((Math.abs((inClose[(i-1)]-inOpen[(i-1)]))-Math.abs((inClose[i]-inOpen[i]))))<((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0))))))||((((((((Math.max(inOpen[(i-1)], inClose[(i-1)])<Math.min(inOpen[(i-2)], inClose[(i-2)]))&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-1)))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==1))&&(inOpen[i]<inOpen[(i-1)]))&&(inOpen[i]>inClose[(i-1)]))&&(inClose[i]>inOpen[(i-1)]))&&(inClose[i]<Math.min(inClose[(i-2)], inOpen[(i-2)])))&&(Math.abs((Math.abs((inClose[(i-1)]-inOpen[(i-1)]))-Math.abs((inClose[i]-inOpen[i]))))<((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0))))))) ) {
            outInteger[outIdx++] = ((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))*100);
         } else {
            outInteger[outIdx++] = 0;
         }
         NearPeriodTotal += (((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))-((Near_rangeType == 0) ? (Math.abs(inClose[(NearTrailingIdx-1)] - inOpen[(NearTrailingIdx-1)])) : ((Near_rangeType == 1) ? (inHigh[(NearTrailingIdx-1)] - inLow[(NearTrailingIdx-1)]) : ((Near_rangeType == 2) ? ((inHigh[(NearTrailingIdx-1)] - inLow[(NearTrailingIdx-1)]) - Math.abs(inClose[(NearTrailingIdx-1)] - inOpen[(NearTrailingIdx-1)])) : 0.0))));
         i += 1;
         NearTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdltasukigap( int startIdx,
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
      int i = 0;
      int outIdx = 0;
      int NearTrailingIdx = 0;
      int lookbackTotal = 0;
      int Near_rangeType = this.candleSettings.near.rangeType;
      int Near_avgPeriod = this.candleSettings.near.avgPeriod;
      double Near_factor = this.candleSettings.near.factor;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackTotal = cdltasukigapLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      NearPeriodTotal = 0;
      NearTrailingIdx = (startIdx-Near_avgPeriod);
      i = NearTrailingIdx;
      while( (i<startIdx) ) {
         NearPeriodTotal += ((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( (((((((((Math.min(inOpen[(i-1)], inClose[(i-1)])>Math.max(inOpen[(i-2)], inClose[(i-2)]))&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==1))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==(0-1)))&&(inOpen[i]<inClose[(i-1)]))&&(inOpen[i]>inOpen[(i-1)]))&&(inClose[i]<inOpen[(i-1)]))&&(inClose[i]>Math.max(inClose[(i-2)], inOpen[(i-2)])))&&(Math.abs((Math.abs((inClose[(i-1)]-inOpen[(i-1)]))-Math.abs((inClose[i]-inOpen[i]))))<((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0))))))||((((((((Math.max(inOpen[(i-1)], inClose[(i-1)])<Math.min(inOpen[(i-2)], inClose[(i-2)]))&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-1)))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==1))&&(inOpen[i]<inOpen[(i-1)]))&&(inOpen[i]>inClose[(i-1)]))&&(inClose[i]>inOpen[(i-1)]))&&(inClose[i]<Math.min(inClose[(i-2)], inOpen[(i-2)])))&&(Math.abs((Math.abs((inClose[(i-1)]-inOpen[(i-1)]))-Math.abs((inClose[i]-inOpen[i]))))<((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0))))))) ) {
            outInteger[outIdx++] = ((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))*100);
         } else {
            outInteger[outIdx++] = 0;
         }
         NearPeriodTotal += (((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))-((Near_rangeType == 0) ? (Math.abs(inClose[(NearTrailingIdx-1)] - inOpen[(NearTrailingIdx-1)])) : ((Near_rangeType == 1) ? (inHigh[(NearTrailingIdx-1)] - inLow[(NearTrailingIdx-1)]) : ((Near_rangeType == 2) ? ((inHigh[(NearTrailingIdx-1)] - inLow[(NearTrailingIdx-1)]) - Math.abs(inClose[(NearTrailingIdx-1)] - inOpen[(NearTrailingIdx-1)])) : 0.0))));
         i += 1;
         NearTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdltasukigapLogic( int startIdx,
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
      int i = 0;
      int outIdx = 0;
      int NearTrailingIdx = 0;
      int lookbackTotal = 0;
      int Near_rangeType = this.candleSettings.near.rangeType;
      int Near_avgPeriod = this.candleSettings.near.avgPeriod;
      double Near_factor = this.candleSettings.near.factor;
      lookbackTotal = cdltasukigapLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      NearPeriodTotal = 0;
      NearTrailingIdx = (startIdx-Near_avgPeriod);
      i = NearTrailingIdx;
      while( (i<startIdx) ) {
         NearPeriodTotal += ((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( (((((((((Math.min(inOpen[(i-1)], inClose[(i-1)])>Math.max(inOpen[(i-2)], inClose[(i-2)]))&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==1))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==(0-1)))&&(inOpen[i]<inClose[(i-1)]))&&(inOpen[i]>inOpen[(i-1)]))&&(inClose[i]<inOpen[(i-1)]))&&(inClose[i]>Math.max(inClose[(i-2)], inOpen[(i-2)])))&&(Math.abs((Math.abs((inClose[(i-1)]-inOpen[(i-1)]))-Math.abs((inClose[i]-inOpen[i]))))<((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0))))))||((((((((Math.max(inOpen[(i-1)], inClose[(i-1)])<Math.min(inOpen[(i-2)], inClose[(i-2)]))&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-1)))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==1))&&(inOpen[i]<inOpen[(i-1)]))&&(inOpen[i]>inClose[(i-1)]))&&(inClose[i]>inOpen[(i-1)]))&&(inClose[i]<Math.min(inClose[(i-2)], inOpen[(i-2)])))&&(Math.abs((Math.abs((inClose[(i-1)]-inOpen[(i-1)]))-Math.abs((inClose[i]-inOpen[i]))))<((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0))))))) ) {
            outInteger[outIdx++] = ((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))*100);
         } else {
            outInteger[outIdx++] = 0;
         }
         NearPeriodTotal += (((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))-((Near_rangeType == 0) ? (Math.abs(inClose[(NearTrailingIdx-1)] - inOpen[(NearTrailingIdx-1)])) : ((Near_rangeType == 1) ? (inHigh[(NearTrailingIdx-1)] - inLow[(NearTrailingIdx-1)]) : ((Near_rangeType == 2) ? ((inHigh[(NearTrailingIdx-1)] - inLow[(NearTrailingIdx-1)]) - Math.abs(inClose[(NearTrailingIdx-1)] - inOpen[(NearTrailingIdx-1)])) : 0.0))));
         i += 1;
         NearTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
