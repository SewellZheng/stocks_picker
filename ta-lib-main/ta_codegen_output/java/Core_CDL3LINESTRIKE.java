/* Generated */
   public int cdl3linestrikeLookback( )
   {
      int Near_rangeType = this.candleSettings.near.rangeType;
      int Near_avgPeriod = this.candleSettings.near.avgPeriod;
      double Near_factor = this.candleSettings.near.factor;
      return (Near_avgPeriod+3) ;

   }
   public RetCode cdl3linestrike( int startIdx,
                                  int endIdx,
                                  double inOpen[],
                                  double inHigh[],
                                  double inLow[],
                                  double inClose[],
                                  MInteger outBegIdx,
                                  MInteger outNBElement,
                                  int outInteger[] )
   {
      double[] NearPeriodTotal = new double[4];
      int i = 0;
      int outIdx = 0;
      int totIdx = 0;
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
      lookbackTotal = cdl3linestrikeLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      NearPeriodTotal[3] = 0;
      NearPeriodTotal[2] = 0;
      NearTrailingIdx = (startIdx-Near_avgPeriod);
      i = NearTrailingIdx;
      while( (i<startIdx) ) {
         NearPeriodTotal[3] = (NearPeriodTotal[3]+((Near_rangeType == 0) ? (Math.abs(inClose[(i-3)] - inOpen[(i-3)])) : ((Near_rangeType == 1) ? (inHigh[(i-3)] - inLow[(i-3)]) : ((Near_rangeType == 2) ? ((inHigh[(i-3)] - inLow[(i-3)]) - Math.abs(inClose[(i-3)] - inOpen[(i-3)])) : 0.0))));
         NearPeriodTotal[2] = (NearPeriodTotal[2]+((Near_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((Near_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((Near_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0))));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( (((((((((((inClose[(i-3)]>=inOpen[(i-3)])) ? (1) : ((0-1)))==(((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1))))&&((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==(((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==(0-(((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1))))))&&(inOpen[(i-2)]>=(Math.min(inOpen[(i-3)], inClose[(i-3)])-((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal[3] / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-3)] - inOpen[(i-3)])) : ((Near_rangeType == 1) ? (inHigh[(i-3)] - inLow[(i-3)]) : ((Near_rangeType == 2) ? ((inHigh[(i-3)] - inLow[(i-3)]) - Math.abs(inClose[(i-3)] - inOpen[(i-3)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0)))))))&&(inOpen[(i-2)]<=(Math.max(inOpen[(i-3)], inClose[(i-3)])+((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal[3] / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-3)] - inOpen[(i-3)])) : ((Near_rangeType == 1) ? (inHigh[(i-3)] - inLow[(i-3)]) : ((Near_rangeType == 2) ? ((inHigh[(i-3)] - inLow[(i-3)]) - Math.abs(inClose[(i-3)] - inOpen[(i-3)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0)))))))&&(inOpen[(i-1)]>=(Math.min(inOpen[(i-2)], inClose[(i-2)])-((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal[2] / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((Near_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((Near_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0)))))))&&(inOpen[(i-1)]<=(Math.max(inOpen[(i-2)], inClose[(i-2)])+((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal[2] / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((Near_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((Near_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0)))))))&&(((((((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==1)&&(inClose[(i-1)]>inClose[(i-2)]))&&(inClose[(i-2)]>inClose[(i-3)]))&&(inOpen[i]>inClose[(i-1)]))&&(inClose[i]<inOpen[(i-3)]))||((((((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-1))&&(inClose[(i-1)]<inClose[(i-2)]))&&(inClose[(i-2)]<inClose[(i-3)]))&&(inOpen[i]<inClose[(i-1)]))&&(inClose[i]>inOpen[(i-3)])))) ) {
            outInteger[outIdx++] = ((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))*100);
         } else {
            outInteger[outIdx++] = 0;
         }
         for( totIdx = 3; (totIdx>=2); totIdx -= 1 ) {
            NearPeriodTotal[totIdx] = (NearPeriodTotal[totIdx]+(((Near_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((Near_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((Near_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((Near_rangeType == 0) ? (Math.abs(inClose[(NearTrailingIdx-totIdx)] - inOpen[(NearTrailingIdx-totIdx)])) : ((Near_rangeType == 1) ? (inHigh[(NearTrailingIdx-totIdx)] - inLow[(NearTrailingIdx-totIdx)]) : ((Near_rangeType == 2) ? ((inHigh[(NearTrailingIdx-totIdx)] - inLow[(NearTrailingIdx-totIdx)]) - Math.abs(inClose[(NearTrailingIdx-totIdx)] - inOpen[(NearTrailingIdx-totIdx)])) : 0.0)))));
         }
         i += 1;
         NearTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdl3linestrikeLogic( int startIdx,
                                       int endIdx,
                                       double inOpen[],
                                       double inHigh[],
                                       double inLow[],
                                       double inClose[],
                                       MInteger outBegIdx,
                                       MInteger outNBElement,
                                       int outInteger[] )
   {
      double[] NearPeriodTotal = new double[4];
      int i = 0;
      int outIdx = 0;
      int totIdx = 0;
      int NearTrailingIdx = 0;
      int lookbackTotal = 0;
      int Near_rangeType = this.candleSettings.near.rangeType;
      int Near_avgPeriod = this.candleSettings.near.avgPeriod;
      double Near_factor = this.candleSettings.near.factor;
      lookbackTotal = cdl3linestrikeLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      NearPeriodTotal[3] = 0;
      NearPeriodTotal[2] = 0;
      NearTrailingIdx = (startIdx-Near_avgPeriod);
      i = NearTrailingIdx;
      while( (i<startIdx) ) {
         NearPeriodTotal[3] = (NearPeriodTotal[3]+((Near_rangeType == 0) ? (Math.abs(inClose[(i-3)] - inOpen[(i-3)])) : ((Near_rangeType == 1) ? (inHigh[(i-3)] - inLow[(i-3)]) : ((Near_rangeType == 2) ? ((inHigh[(i-3)] - inLow[(i-3)]) - Math.abs(inClose[(i-3)] - inOpen[(i-3)])) : 0.0))));
         NearPeriodTotal[2] = (NearPeriodTotal[2]+((Near_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((Near_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((Near_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0))));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( (((((((((((inClose[(i-3)]>=inOpen[(i-3)])) ? (1) : ((0-1)))==(((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1))))&&((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==(((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==(0-(((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1))))))&&(inOpen[(i-2)]>=(Math.min(inOpen[(i-3)], inClose[(i-3)])-((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal[3] / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-3)] - inOpen[(i-3)])) : ((Near_rangeType == 1) ? (inHigh[(i-3)] - inLow[(i-3)]) : ((Near_rangeType == 2) ? ((inHigh[(i-3)] - inLow[(i-3)]) - Math.abs(inClose[(i-3)] - inOpen[(i-3)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0)))))))&&(inOpen[(i-2)]<=(Math.max(inOpen[(i-3)], inClose[(i-3)])+((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal[3] / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-3)] - inOpen[(i-3)])) : ((Near_rangeType == 1) ? (inHigh[(i-3)] - inLow[(i-3)]) : ((Near_rangeType == 2) ? ((inHigh[(i-3)] - inLow[(i-3)]) - Math.abs(inClose[(i-3)] - inOpen[(i-3)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0)))))))&&(inOpen[(i-1)]>=(Math.min(inOpen[(i-2)], inClose[(i-2)])-((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal[2] / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((Near_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((Near_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0)))))))&&(inOpen[(i-1)]<=(Math.max(inOpen[(i-2)], inClose[(i-2)])+((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal[2] / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((Near_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((Near_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0)))))))&&(((((((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==1)&&(inClose[(i-1)]>inClose[(i-2)]))&&(inClose[(i-2)]>inClose[(i-3)]))&&(inOpen[i]>inClose[(i-1)]))&&(inClose[i]<inOpen[(i-3)]))||((((((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-1))&&(inClose[(i-1)]<inClose[(i-2)]))&&(inClose[(i-2)]<inClose[(i-3)]))&&(inOpen[i]<inClose[(i-1)]))&&(inClose[i]>inOpen[(i-3)])))) ) {
            outInteger[outIdx++] = ((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))*100);
         } else {
            outInteger[outIdx++] = 0;
         }
         for( totIdx = 3; (totIdx>=2); totIdx -= 1 ) {
            NearPeriodTotal[totIdx] = (NearPeriodTotal[totIdx]+(((Near_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((Near_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((Near_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((Near_rangeType == 0) ? (Math.abs(inClose[(NearTrailingIdx-totIdx)] - inOpen[(NearTrailingIdx-totIdx)])) : ((Near_rangeType == 1) ? (inHigh[(NearTrailingIdx-totIdx)] - inLow[(NearTrailingIdx-totIdx)]) : ((Near_rangeType == 2) ? ((inHigh[(NearTrailingIdx-totIdx)] - inLow[(NearTrailingIdx-totIdx)]) - Math.abs(inClose[(NearTrailingIdx-totIdx)] - inOpen[(NearTrailingIdx-totIdx)])) : 0.0)))));
         }
         i += 1;
         NearTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdl3linestrike( int startIdx,
                                  int endIdx,
                                  float inOpen[],
                                  float inHigh[],
                                  float inLow[],
                                  float inClose[],
                                  MInteger outBegIdx,
                                  MInteger outNBElement,
                                  int outInteger[] )
   {
      double[] NearPeriodTotal = new double[4];
      int i = 0;
      int outIdx = 0;
      int totIdx = 0;
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
      lookbackTotal = cdl3linestrikeLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      NearPeriodTotal[3] = 0;
      NearPeriodTotal[2] = 0;
      NearTrailingIdx = (startIdx-Near_avgPeriod);
      i = NearTrailingIdx;
      while( (i<startIdx) ) {
         NearPeriodTotal[3] = (NearPeriodTotal[3]+((Near_rangeType == 0) ? (Math.abs(inClose[(i-3)] - inOpen[(i-3)])) : ((Near_rangeType == 1) ? (inHigh[(i-3)] - inLow[(i-3)]) : ((Near_rangeType == 2) ? ((inHigh[(i-3)] - inLow[(i-3)]) - Math.abs(inClose[(i-3)] - inOpen[(i-3)])) : 0.0))));
         NearPeriodTotal[2] = (NearPeriodTotal[2]+((Near_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((Near_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((Near_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0))));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( (((((((((((inClose[(i-3)]>=inOpen[(i-3)])) ? (1) : ((0-1)))==(((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1))))&&((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==(((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==(0-(((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1))))))&&(inOpen[(i-2)]>=(Math.min(inOpen[(i-3)], inClose[(i-3)])-((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal[3] / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-3)] - inOpen[(i-3)])) : ((Near_rangeType == 1) ? (inHigh[(i-3)] - inLow[(i-3)]) : ((Near_rangeType == 2) ? ((inHigh[(i-3)] - inLow[(i-3)]) - Math.abs(inClose[(i-3)] - inOpen[(i-3)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0)))))))&&(inOpen[(i-2)]<=(Math.max(inOpen[(i-3)], inClose[(i-3)])+((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal[3] / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-3)] - inOpen[(i-3)])) : ((Near_rangeType == 1) ? (inHigh[(i-3)] - inLow[(i-3)]) : ((Near_rangeType == 2) ? ((inHigh[(i-3)] - inLow[(i-3)]) - Math.abs(inClose[(i-3)] - inOpen[(i-3)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0)))))))&&(inOpen[(i-1)]>=(Math.min(inOpen[(i-2)], inClose[(i-2)])-((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal[2] / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((Near_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((Near_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0)))))))&&(inOpen[(i-1)]<=(Math.max(inOpen[(i-2)], inClose[(i-2)])+((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal[2] / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((Near_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((Near_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0)))))))&&(((((((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==1)&&(inClose[(i-1)]>inClose[(i-2)]))&&(inClose[(i-2)]>inClose[(i-3)]))&&(inOpen[i]>inClose[(i-1)]))&&(inClose[i]<inOpen[(i-3)]))||((((((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-1))&&(inClose[(i-1)]<inClose[(i-2)]))&&(inClose[(i-2)]<inClose[(i-3)]))&&(inOpen[i]<inClose[(i-1)]))&&(inClose[i]>inOpen[(i-3)])))) ) {
            outInteger[outIdx++] = ((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))*100);
         } else {
            outInteger[outIdx++] = 0;
         }
         for( totIdx = 3; (totIdx>=2); totIdx -= 1 ) {
            NearPeriodTotal[totIdx] = (NearPeriodTotal[totIdx]+(((Near_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((Near_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((Near_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((Near_rangeType == 0) ? (Math.abs(inClose[(NearTrailingIdx-totIdx)] - inOpen[(NearTrailingIdx-totIdx)])) : ((Near_rangeType == 1) ? (inHigh[(NearTrailingIdx-totIdx)] - inLow[(NearTrailingIdx-totIdx)]) : ((Near_rangeType == 2) ? ((inHigh[(NearTrailingIdx-totIdx)] - inLow[(NearTrailingIdx-totIdx)]) - Math.abs(inClose[(NearTrailingIdx-totIdx)] - inOpen[(NearTrailingIdx-totIdx)])) : 0.0)))));
         }
         i += 1;
         NearTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdl3linestrikeLogic( int startIdx,
                                       int endIdx,
                                       float inOpen[],
                                       float inHigh[],
                                       float inLow[],
                                       float inClose[],
                                       MInteger outBegIdx,
                                       MInteger outNBElement,
                                       int outInteger[] )
   {
      double[] NearPeriodTotal = new double[4];
      int i = 0;
      int outIdx = 0;
      int totIdx = 0;
      int NearTrailingIdx = 0;
      int lookbackTotal = 0;
      int Near_rangeType = this.candleSettings.near.rangeType;
      int Near_avgPeriod = this.candleSettings.near.avgPeriod;
      double Near_factor = this.candleSettings.near.factor;
      lookbackTotal = cdl3linestrikeLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      NearPeriodTotal[3] = 0;
      NearPeriodTotal[2] = 0;
      NearTrailingIdx = (startIdx-Near_avgPeriod);
      i = NearTrailingIdx;
      while( (i<startIdx) ) {
         NearPeriodTotal[3] = (NearPeriodTotal[3]+((Near_rangeType == 0) ? (Math.abs(inClose[(i-3)] - inOpen[(i-3)])) : ((Near_rangeType == 1) ? (inHigh[(i-3)] - inLow[(i-3)]) : ((Near_rangeType == 2) ? ((inHigh[(i-3)] - inLow[(i-3)]) - Math.abs(inClose[(i-3)] - inOpen[(i-3)])) : 0.0))));
         NearPeriodTotal[2] = (NearPeriodTotal[2]+((Near_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((Near_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((Near_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0))));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( (((((((((((inClose[(i-3)]>=inOpen[(i-3)])) ? (1) : ((0-1)))==(((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1))))&&((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==(((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==(0-(((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1))))))&&(inOpen[(i-2)]>=(Math.min(inOpen[(i-3)], inClose[(i-3)])-((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal[3] / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-3)] - inOpen[(i-3)])) : ((Near_rangeType == 1) ? (inHigh[(i-3)] - inLow[(i-3)]) : ((Near_rangeType == 2) ? ((inHigh[(i-3)] - inLow[(i-3)]) - Math.abs(inClose[(i-3)] - inOpen[(i-3)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0)))))))&&(inOpen[(i-2)]<=(Math.max(inOpen[(i-3)], inClose[(i-3)])+((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal[3] / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-3)] - inOpen[(i-3)])) : ((Near_rangeType == 1) ? (inHigh[(i-3)] - inLow[(i-3)]) : ((Near_rangeType == 2) ? ((inHigh[(i-3)] - inLow[(i-3)]) - Math.abs(inClose[(i-3)] - inOpen[(i-3)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0)))))))&&(inOpen[(i-1)]>=(Math.min(inOpen[(i-2)], inClose[(i-2)])-((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal[2] / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((Near_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((Near_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0)))))))&&(inOpen[(i-1)]<=(Math.max(inOpen[(i-2)], inClose[(i-2)])+((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal[2] / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((Near_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((Near_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0)))))))&&(((((((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==1)&&(inClose[(i-1)]>inClose[(i-2)]))&&(inClose[(i-2)]>inClose[(i-3)]))&&(inOpen[i]>inClose[(i-1)]))&&(inClose[i]<inOpen[(i-3)]))||((((((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-1))&&(inClose[(i-1)]<inClose[(i-2)]))&&(inClose[(i-2)]<inClose[(i-3)]))&&(inOpen[i]<inClose[(i-1)]))&&(inClose[i]>inOpen[(i-3)])))) ) {
            outInteger[outIdx++] = ((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))*100);
         } else {
            outInteger[outIdx++] = 0;
         }
         for( totIdx = 3; (totIdx>=2); totIdx -= 1 ) {
            NearPeriodTotal[totIdx] = (NearPeriodTotal[totIdx]+(((Near_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((Near_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((Near_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((Near_rangeType == 0) ? (Math.abs(inClose[(NearTrailingIdx-totIdx)] - inOpen[(NearTrailingIdx-totIdx)])) : ((Near_rangeType == 1) ? (inHigh[(NearTrailingIdx-totIdx)] - inLow[(NearTrailingIdx-totIdx)]) : ((Near_rangeType == 2) ? ((inHigh[(NearTrailingIdx-totIdx)] - inLow[(NearTrailingIdx-totIdx)]) - Math.abs(inClose[(NearTrailingIdx-totIdx)] - inOpen[(NearTrailingIdx-totIdx)])) : 0.0)))));
         }
         i += 1;
         NearTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
