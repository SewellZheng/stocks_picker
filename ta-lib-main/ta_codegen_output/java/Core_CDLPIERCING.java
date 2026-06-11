/* Generated */
   public int cdlpiercingLookback( )
   {
      int BodyLong_rangeType = this.candleSettings.bodyLong.rangeType;
      int BodyLong_avgPeriod = this.candleSettings.bodyLong.avgPeriod;
      double BodyLong_factor = this.candleSettings.bodyLong.factor;
      return (BodyLong_avgPeriod+1) ;

   }
   public RetCode cdlpiercing( int startIdx,
                               int endIdx,
                               double inOpen[],
                               double inHigh[],
                               double inLow[],
                               double inClose[],
                               MInteger outBegIdx,
                               MInteger outNBElement,
                               int outInteger[] )
   {
      double[] BodyLongPeriodTotal = new double[2];
      int i = 0;
      int outIdx = 0;
      int totIdx = 0;
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
      lookbackTotal = cdlpiercingLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      BodyLongPeriodTotal[1] = 0;
      BodyLongPeriodTotal[0] = 0;
      BodyLongTrailingIdx = (startIdx-BodyLong_avgPeriod);
      i = BodyLongTrailingIdx;
      while( (i<startIdx) ) {
         BodyLongPeriodTotal[1] = (BodyLongPeriodTotal[1]+((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0))));
         BodyLongPeriodTotal[0] = (BodyLongPeriodTotal[0]+((BodyLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0))));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( ((((((((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-1))&&(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyLongPeriodTotal[1] / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==1))&&(Math.abs((inClose[i]-inOpen[i]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyLongPeriodTotal[0] / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))))&&(inOpen[i]<inLow[(i-1)]))&&(inClose[i]<inOpen[(i-1)]))&&(inClose[i]>(inClose[(i-1)]+(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))*0.5)))) ) {
            outInteger[outIdx++] = 100;
         } else {
            outInteger[outIdx++] = 0;
         }
         for( totIdx = 1; (totIdx>=0); totIdx -= 1 ) {
            BodyLongPeriodTotal[totIdx] = (BodyLongPeriodTotal[totIdx]+(((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((BodyLong_rangeType == 0) ? (Math.abs(inClose[(BodyLongTrailingIdx-totIdx)] - inOpen[(BodyLongTrailingIdx-totIdx)])) : ((BodyLong_rangeType == 1) ? (inHigh[(BodyLongTrailingIdx-totIdx)] - inLow[(BodyLongTrailingIdx-totIdx)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(BodyLongTrailingIdx-totIdx)] - inLow[(BodyLongTrailingIdx-totIdx)]) - Math.abs(inClose[(BodyLongTrailingIdx-totIdx)] - inOpen[(BodyLongTrailingIdx-totIdx)])) : 0.0)))));
         }
         i += 1;
         BodyLongTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdlpiercingLogic( int startIdx,
                                    int endIdx,
                                    double inOpen[],
                                    double inHigh[],
                                    double inLow[],
                                    double inClose[],
                                    MInteger outBegIdx,
                                    MInteger outNBElement,
                                    int outInteger[] )
   {
      double[] BodyLongPeriodTotal = new double[2];
      int i = 0;
      int outIdx = 0;
      int totIdx = 0;
      int BodyLongTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyLong_rangeType = this.candleSettings.bodyLong.rangeType;
      int BodyLong_avgPeriod = this.candleSettings.bodyLong.avgPeriod;
      double BodyLong_factor = this.candleSettings.bodyLong.factor;
      lookbackTotal = cdlpiercingLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      BodyLongPeriodTotal[1] = 0;
      BodyLongPeriodTotal[0] = 0;
      BodyLongTrailingIdx = (startIdx-BodyLong_avgPeriod);
      i = BodyLongTrailingIdx;
      while( (i<startIdx) ) {
         BodyLongPeriodTotal[1] = (BodyLongPeriodTotal[1]+((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0))));
         BodyLongPeriodTotal[0] = (BodyLongPeriodTotal[0]+((BodyLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0))));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( ((((((((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-1))&&(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyLongPeriodTotal[1] / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==1))&&(Math.abs((inClose[i]-inOpen[i]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyLongPeriodTotal[0] / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))))&&(inOpen[i]<inLow[(i-1)]))&&(inClose[i]<inOpen[(i-1)]))&&(inClose[i]>(inClose[(i-1)]+(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))*0.5)))) ) {
            outInteger[outIdx++] = 100;
         } else {
            outInteger[outIdx++] = 0;
         }
         for( totIdx = 1; (totIdx>=0); totIdx -= 1 ) {
            BodyLongPeriodTotal[totIdx] = (BodyLongPeriodTotal[totIdx]+(((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((BodyLong_rangeType == 0) ? (Math.abs(inClose[(BodyLongTrailingIdx-totIdx)] - inOpen[(BodyLongTrailingIdx-totIdx)])) : ((BodyLong_rangeType == 1) ? (inHigh[(BodyLongTrailingIdx-totIdx)] - inLow[(BodyLongTrailingIdx-totIdx)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(BodyLongTrailingIdx-totIdx)] - inLow[(BodyLongTrailingIdx-totIdx)]) - Math.abs(inClose[(BodyLongTrailingIdx-totIdx)] - inOpen[(BodyLongTrailingIdx-totIdx)])) : 0.0)))));
         }
         i += 1;
         BodyLongTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdlpiercing( int startIdx,
                               int endIdx,
                               float inOpen[],
                               float inHigh[],
                               float inLow[],
                               float inClose[],
                               MInteger outBegIdx,
                               MInteger outNBElement,
                               int outInteger[] )
   {
      double[] BodyLongPeriodTotal = new double[2];
      int i = 0;
      int outIdx = 0;
      int totIdx = 0;
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
      lookbackTotal = cdlpiercingLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      BodyLongPeriodTotal[1] = 0;
      BodyLongPeriodTotal[0] = 0;
      BodyLongTrailingIdx = (startIdx-BodyLong_avgPeriod);
      i = BodyLongTrailingIdx;
      while( (i<startIdx) ) {
         BodyLongPeriodTotal[1] = (BodyLongPeriodTotal[1]+((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0))));
         BodyLongPeriodTotal[0] = (BodyLongPeriodTotal[0]+((BodyLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0))));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( ((((((((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-1))&&(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyLongPeriodTotal[1] / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==1))&&(Math.abs((inClose[i]-inOpen[i]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyLongPeriodTotal[0] / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))))&&(inOpen[i]<inLow[(i-1)]))&&(inClose[i]<inOpen[(i-1)]))&&(inClose[i]>(inClose[(i-1)]+(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))*0.5)))) ) {
            outInteger[outIdx++] = 100;
         } else {
            outInteger[outIdx++] = 0;
         }
         for( totIdx = 1; (totIdx>=0); totIdx -= 1 ) {
            BodyLongPeriodTotal[totIdx] = (BodyLongPeriodTotal[totIdx]+(((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((BodyLong_rangeType == 0) ? (Math.abs(inClose[(BodyLongTrailingIdx-totIdx)] - inOpen[(BodyLongTrailingIdx-totIdx)])) : ((BodyLong_rangeType == 1) ? (inHigh[(BodyLongTrailingIdx-totIdx)] - inLow[(BodyLongTrailingIdx-totIdx)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(BodyLongTrailingIdx-totIdx)] - inLow[(BodyLongTrailingIdx-totIdx)]) - Math.abs(inClose[(BodyLongTrailingIdx-totIdx)] - inOpen[(BodyLongTrailingIdx-totIdx)])) : 0.0)))));
         }
         i += 1;
         BodyLongTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdlpiercingLogic( int startIdx,
                                    int endIdx,
                                    float inOpen[],
                                    float inHigh[],
                                    float inLow[],
                                    float inClose[],
                                    MInteger outBegIdx,
                                    MInteger outNBElement,
                                    int outInteger[] )
   {
      double[] BodyLongPeriodTotal = new double[2];
      int i = 0;
      int outIdx = 0;
      int totIdx = 0;
      int BodyLongTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyLong_rangeType = this.candleSettings.bodyLong.rangeType;
      int BodyLong_avgPeriod = this.candleSettings.bodyLong.avgPeriod;
      double BodyLong_factor = this.candleSettings.bodyLong.factor;
      lookbackTotal = cdlpiercingLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      BodyLongPeriodTotal[1] = 0;
      BodyLongPeriodTotal[0] = 0;
      BodyLongTrailingIdx = (startIdx-BodyLong_avgPeriod);
      i = BodyLongTrailingIdx;
      while( (i<startIdx) ) {
         BodyLongPeriodTotal[1] = (BodyLongPeriodTotal[1]+((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0))));
         BodyLongPeriodTotal[0] = (BodyLongPeriodTotal[0]+((BodyLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0))));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( ((((((((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-1))&&(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyLongPeriodTotal[1] / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==1))&&(Math.abs((inClose[i]-inOpen[i]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyLongPeriodTotal[0] / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))))&&(inOpen[i]<inLow[(i-1)]))&&(inClose[i]<inOpen[(i-1)]))&&(inClose[i]>(inClose[(i-1)]+(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))*0.5)))) ) {
            outInteger[outIdx++] = 100;
         } else {
            outInteger[outIdx++] = 0;
         }
         for( totIdx = 1; (totIdx>=0); totIdx -= 1 ) {
            BodyLongPeriodTotal[totIdx] = (BodyLongPeriodTotal[totIdx]+(((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((BodyLong_rangeType == 0) ? (Math.abs(inClose[(BodyLongTrailingIdx-totIdx)] - inOpen[(BodyLongTrailingIdx-totIdx)])) : ((BodyLong_rangeType == 1) ? (inHigh[(BodyLongTrailingIdx-totIdx)] - inLow[(BodyLongTrailingIdx-totIdx)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(BodyLongTrailingIdx-totIdx)] - inLow[(BodyLongTrailingIdx-totIdx)]) - Math.abs(inClose[(BodyLongTrailingIdx-totIdx)] - inOpen[(BodyLongTrailingIdx-totIdx)])) : 0.0)))));
         }
         i += 1;
         BodyLongTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
