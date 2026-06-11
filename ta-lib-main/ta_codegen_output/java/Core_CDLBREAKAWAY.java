/* Generated */
   public int cdlbreakawayLookback( )
   {
      int BodyLong_rangeType = this.candleSettings.bodyLong.rangeType;
      int BodyLong_avgPeriod = this.candleSettings.bodyLong.avgPeriod;
      double BodyLong_factor = this.candleSettings.bodyLong.factor;
      return (BodyLong_avgPeriod+4) ;

   }
   public RetCode cdlbreakaway( int startIdx,
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
      int i = 0;
      int outIdx = 0;
      int BodyLongTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyLong_rangeType = this.candleSettings.bodyLong.rangeType;
      int BodyLong_avgPeriod = this.candleSettings.bodyLong.avgPeriod;
      double BodyLong_factor = this.candleSettings.bodyLong.factor;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackTotal = cdlbreakawayLookback();
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
      i = BodyLongTrailingIdx;
      while( (i<startIdx) ) {
         BodyLongPeriodTotal += ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-4)] - inOpen[(i-4)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-4)] - inLow[(i-4)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-4)] - inLow[(i-4)]) - Math.abs(inClose[(i-4)] - inOpen[(i-4)])) : 0.0)));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( ((((((((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1)))==(((inClose[(i-3)]>=inOpen[(i-3)])) ? (1) : ((0-1))))&&((((inClose[(i-3)]>=inOpen[(i-3)])) ? (1) : ((0-1)))==(((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))))&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-(((inClose[i]>=inOpen[i])) ? (1) : ((0-1))))))&&(Math.abs((inClose[(i-4)]-inOpen[(i-4)]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyLongPeriodTotal / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-4)] - inOpen[(i-4)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-4)] - inLow[(i-4)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-4)] - inLow[(i-4)]) - Math.abs(inClose[(i-4)] - inOpen[(i-4)])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))))&&((((((((((((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1)))==(0-1))&&(Math.max(inOpen[(i-3)], inClose[(i-3)])<Math.min(inOpen[(i-4)], inClose[(i-4)])))&&(inHigh[(i-2)]<inHigh[(i-3)]))&&(inLow[(i-2)]<inLow[(i-3)]))&&(inHigh[(i-1)]<inHigh[(i-2)]))&&(inLow[(i-1)]<inLow[(i-2)]))&&(inClose[i]>inOpen[(i-3)]))&&(inClose[i]<inClose[(i-4)]))||(((((((((((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1)))==1)&&(Math.min(inOpen[(i-3)], inClose[(i-3)])>Math.max(inOpen[(i-4)], inClose[(i-4)])))&&(inHigh[(i-2)]>inHigh[(i-3)]))&&(inLow[(i-2)]>inLow[(i-3)]))&&(inHigh[(i-1)]>inHigh[(i-2)]))&&(inLow[(i-1)]>inLow[(i-2)]))&&(inClose[i]<inOpen[(i-3)]))&&(inClose[i]>inClose[(i-4)])))) ) {
            outInteger[outIdx++] = ((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))*100);
         } else {
            outInteger[outIdx++] = 0;
         }
         BodyLongPeriodTotal += (((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-4)] - inOpen[(i-4)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-4)] - inLow[(i-4)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-4)] - inLow[(i-4)]) - Math.abs(inClose[(i-4)] - inOpen[(i-4)])) : 0.0)))-((BodyLong_rangeType == 0) ? (Math.abs(inClose[(BodyLongTrailingIdx-4)] - inOpen[(BodyLongTrailingIdx-4)])) : ((BodyLong_rangeType == 1) ? (inHigh[(BodyLongTrailingIdx-4)] - inLow[(BodyLongTrailingIdx-4)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(BodyLongTrailingIdx-4)] - inLow[(BodyLongTrailingIdx-4)]) - Math.abs(inClose[(BodyLongTrailingIdx-4)] - inOpen[(BodyLongTrailingIdx-4)])) : 0.0))));
         i += 1;
         BodyLongTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdlbreakawayLogic( int startIdx,
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
      int i = 0;
      int outIdx = 0;
      int BodyLongTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyLong_rangeType = this.candleSettings.bodyLong.rangeType;
      int BodyLong_avgPeriod = this.candleSettings.bodyLong.avgPeriod;
      double BodyLong_factor = this.candleSettings.bodyLong.factor;
      lookbackTotal = cdlbreakawayLookback();
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
      i = BodyLongTrailingIdx;
      while( (i<startIdx) ) {
         BodyLongPeriodTotal += ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-4)] - inOpen[(i-4)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-4)] - inLow[(i-4)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-4)] - inLow[(i-4)]) - Math.abs(inClose[(i-4)] - inOpen[(i-4)])) : 0.0)));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( ((((((((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1)))==(((inClose[(i-3)]>=inOpen[(i-3)])) ? (1) : ((0-1))))&&((((inClose[(i-3)]>=inOpen[(i-3)])) ? (1) : ((0-1)))==(((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))))&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-(((inClose[i]>=inOpen[i])) ? (1) : ((0-1))))))&&(Math.abs((inClose[(i-4)]-inOpen[(i-4)]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyLongPeriodTotal / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-4)] - inOpen[(i-4)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-4)] - inLow[(i-4)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-4)] - inLow[(i-4)]) - Math.abs(inClose[(i-4)] - inOpen[(i-4)])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))))&&((((((((((((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1)))==(0-1))&&(Math.max(inOpen[(i-3)], inClose[(i-3)])<Math.min(inOpen[(i-4)], inClose[(i-4)])))&&(inHigh[(i-2)]<inHigh[(i-3)]))&&(inLow[(i-2)]<inLow[(i-3)]))&&(inHigh[(i-1)]<inHigh[(i-2)]))&&(inLow[(i-1)]<inLow[(i-2)]))&&(inClose[i]>inOpen[(i-3)]))&&(inClose[i]<inClose[(i-4)]))||(((((((((((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1)))==1)&&(Math.min(inOpen[(i-3)], inClose[(i-3)])>Math.max(inOpen[(i-4)], inClose[(i-4)])))&&(inHigh[(i-2)]>inHigh[(i-3)]))&&(inLow[(i-2)]>inLow[(i-3)]))&&(inHigh[(i-1)]>inHigh[(i-2)]))&&(inLow[(i-1)]>inLow[(i-2)]))&&(inClose[i]<inOpen[(i-3)]))&&(inClose[i]>inClose[(i-4)])))) ) {
            outInteger[outIdx++] = ((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))*100);
         } else {
            outInteger[outIdx++] = 0;
         }
         BodyLongPeriodTotal += (((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-4)] - inOpen[(i-4)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-4)] - inLow[(i-4)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-4)] - inLow[(i-4)]) - Math.abs(inClose[(i-4)] - inOpen[(i-4)])) : 0.0)))-((BodyLong_rangeType == 0) ? (Math.abs(inClose[(BodyLongTrailingIdx-4)] - inOpen[(BodyLongTrailingIdx-4)])) : ((BodyLong_rangeType == 1) ? (inHigh[(BodyLongTrailingIdx-4)] - inLow[(BodyLongTrailingIdx-4)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(BodyLongTrailingIdx-4)] - inLow[(BodyLongTrailingIdx-4)]) - Math.abs(inClose[(BodyLongTrailingIdx-4)] - inOpen[(BodyLongTrailingIdx-4)])) : 0.0))));
         i += 1;
         BodyLongTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdlbreakaway( int startIdx,
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
      int i = 0;
      int outIdx = 0;
      int BodyLongTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyLong_rangeType = this.candleSettings.bodyLong.rangeType;
      int BodyLong_avgPeriod = this.candleSettings.bodyLong.avgPeriod;
      double BodyLong_factor = this.candleSettings.bodyLong.factor;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackTotal = cdlbreakawayLookback();
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
      i = BodyLongTrailingIdx;
      while( (i<startIdx) ) {
         BodyLongPeriodTotal += ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-4)] - inOpen[(i-4)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-4)] - inLow[(i-4)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-4)] - inLow[(i-4)]) - Math.abs(inClose[(i-4)] - inOpen[(i-4)])) : 0.0)));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( ((((((((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1)))==(((inClose[(i-3)]>=inOpen[(i-3)])) ? (1) : ((0-1))))&&((((inClose[(i-3)]>=inOpen[(i-3)])) ? (1) : ((0-1)))==(((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))))&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-(((inClose[i]>=inOpen[i])) ? (1) : ((0-1))))))&&(Math.abs((inClose[(i-4)]-inOpen[(i-4)]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyLongPeriodTotal / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-4)] - inOpen[(i-4)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-4)] - inLow[(i-4)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-4)] - inLow[(i-4)]) - Math.abs(inClose[(i-4)] - inOpen[(i-4)])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))))&&((((((((((((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1)))==(0-1))&&(Math.max(inOpen[(i-3)], inClose[(i-3)])<Math.min(inOpen[(i-4)], inClose[(i-4)])))&&(inHigh[(i-2)]<inHigh[(i-3)]))&&(inLow[(i-2)]<inLow[(i-3)]))&&(inHigh[(i-1)]<inHigh[(i-2)]))&&(inLow[(i-1)]<inLow[(i-2)]))&&(inClose[i]>inOpen[(i-3)]))&&(inClose[i]<inClose[(i-4)]))||(((((((((((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1)))==1)&&(Math.min(inOpen[(i-3)], inClose[(i-3)])>Math.max(inOpen[(i-4)], inClose[(i-4)])))&&(inHigh[(i-2)]>inHigh[(i-3)]))&&(inLow[(i-2)]>inLow[(i-3)]))&&(inHigh[(i-1)]>inHigh[(i-2)]))&&(inLow[(i-1)]>inLow[(i-2)]))&&(inClose[i]<inOpen[(i-3)]))&&(inClose[i]>inClose[(i-4)])))) ) {
            outInteger[outIdx++] = ((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))*100);
         } else {
            outInteger[outIdx++] = 0;
         }
         BodyLongPeriodTotal += (((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-4)] - inOpen[(i-4)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-4)] - inLow[(i-4)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-4)] - inLow[(i-4)]) - Math.abs(inClose[(i-4)] - inOpen[(i-4)])) : 0.0)))-((BodyLong_rangeType == 0) ? (Math.abs(inClose[(BodyLongTrailingIdx-4)] - inOpen[(BodyLongTrailingIdx-4)])) : ((BodyLong_rangeType == 1) ? (inHigh[(BodyLongTrailingIdx-4)] - inLow[(BodyLongTrailingIdx-4)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(BodyLongTrailingIdx-4)] - inLow[(BodyLongTrailingIdx-4)]) - Math.abs(inClose[(BodyLongTrailingIdx-4)] - inOpen[(BodyLongTrailingIdx-4)])) : 0.0))));
         i += 1;
         BodyLongTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdlbreakawayLogic( int startIdx,
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
      int i = 0;
      int outIdx = 0;
      int BodyLongTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyLong_rangeType = this.candleSettings.bodyLong.rangeType;
      int BodyLong_avgPeriod = this.candleSettings.bodyLong.avgPeriod;
      double BodyLong_factor = this.candleSettings.bodyLong.factor;
      lookbackTotal = cdlbreakawayLookback();
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
      i = BodyLongTrailingIdx;
      while( (i<startIdx) ) {
         BodyLongPeriodTotal += ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-4)] - inOpen[(i-4)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-4)] - inLow[(i-4)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-4)] - inLow[(i-4)]) - Math.abs(inClose[(i-4)] - inOpen[(i-4)])) : 0.0)));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( ((((((((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1)))==(((inClose[(i-3)]>=inOpen[(i-3)])) ? (1) : ((0-1))))&&((((inClose[(i-3)]>=inOpen[(i-3)])) ? (1) : ((0-1)))==(((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))))&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-(((inClose[i]>=inOpen[i])) ? (1) : ((0-1))))))&&(Math.abs((inClose[(i-4)]-inOpen[(i-4)]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyLongPeriodTotal / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-4)] - inOpen[(i-4)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-4)] - inLow[(i-4)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-4)] - inLow[(i-4)]) - Math.abs(inClose[(i-4)] - inOpen[(i-4)])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))))&&((((((((((((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1)))==(0-1))&&(Math.max(inOpen[(i-3)], inClose[(i-3)])<Math.min(inOpen[(i-4)], inClose[(i-4)])))&&(inHigh[(i-2)]<inHigh[(i-3)]))&&(inLow[(i-2)]<inLow[(i-3)]))&&(inHigh[(i-1)]<inHigh[(i-2)]))&&(inLow[(i-1)]<inLow[(i-2)]))&&(inClose[i]>inOpen[(i-3)]))&&(inClose[i]<inClose[(i-4)]))||(((((((((((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1)))==1)&&(Math.min(inOpen[(i-3)], inClose[(i-3)])>Math.max(inOpen[(i-4)], inClose[(i-4)])))&&(inHigh[(i-2)]>inHigh[(i-3)]))&&(inLow[(i-2)]>inLow[(i-3)]))&&(inHigh[(i-1)]>inHigh[(i-2)]))&&(inLow[(i-1)]>inLow[(i-2)]))&&(inClose[i]<inOpen[(i-3)]))&&(inClose[i]>inClose[(i-4)])))) ) {
            outInteger[outIdx++] = ((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))*100);
         } else {
            outInteger[outIdx++] = 0;
         }
         BodyLongPeriodTotal += (((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-4)] - inOpen[(i-4)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-4)] - inLow[(i-4)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-4)] - inLow[(i-4)]) - Math.abs(inClose[(i-4)] - inOpen[(i-4)])) : 0.0)))-((BodyLong_rangeType == 0) ? (Math.abs(inClose[(BodyLongTrailingIdx-4)] - inOpen[(BodyLongTrailingIdx-4)])) : ((BodyLong_rangeType == 1) ? (inHigh[(BodyLongTrailingIdx-4)] - inLow[(BodyLongTrailingIdx-4)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(BodyLongTrailingIdx-4)] - inLow[(BodyLongTrailingIdx-4)]) - Math.abs(inClose[(BodyLongTrailingIdx-4)] - inOpen[(BodyLongTrailingIdx-4)])) : 0.0))));
         i += 1;
         BodyLongTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
