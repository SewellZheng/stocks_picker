/* Generated */
   public int cdlrisefall3methodsLookback( )
   {
      int BodyLong_rangeType = this.candleSettings.bodyLong.rangeType;
      int BodyLong_avgPeriod = this.candleSettings.bodyLong.avgPeriod;
      double BodyLong_factor = this.candleSettings.bodyLong.factor;
      int BodyShort_rangeType = this.candleSettings.bodyShort.rangeType;
      int BodyShort_avgPeriod = this.candleSettings.bodyShort.avgPeriod;
      double BodyShort_factor = this.candleSettings.bodyShort.factor;
      return (Math.max(BodyShort_avgPeriod, BodyLong_avgPeriod)+4) ;

   }
   public RetCode cdlrisefall3methods( int startIdx,
                                       int endIdx,
                                       double inOpen[],
                                       double inHigh[],
                                       double inLow[],
                                       double inClose[],
                                       MInteger outBegIdx,
                                       MInteger outNBElement,
                                       int outInteger[] )
   {
      double[] BodyPeriodTotal = new double[5];
      int i = 0;
      int outIdx = 0;
      int totIdx = 0;
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
      lookbackTotal = cdlrisefall3methodsLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      BodyPeriodTotal[4] = 0;
      BodyPeriodTotal[3] = 0;
      BodyPeriodTotal[2] = 0;
      BodyPeriodTotal[1] = 0;
      BodyPeriodTotal[0] = 0;
      BodyShortTrailingIdx = (startIdx-BodyShort_avgPeriod);
      BodyLongTrailingIdx = (startIdx-BodyLong_avgPeriod);
      i = BodyShortTrailingIdx;
      while( (i<startIdx) ) {
         BodyPeriodTotal[3] = (BodyPeriodTotal[3]+((BodyShort_rangeType == 0) ? (Math.abs(inClose[(i-3)] - inOpen[(i-3)])) : ((BodyShort_rangeType == 1) ? (inHigh[(i-3)] - inLow[(i-3)]) : ((BodyShort_rangeType == 2) ? ((inHigh[(i-3)] - inLow[(i-3)]) - Math.abs(inClose[(i-3)] - inOpen[(i-3)])) : 0.0))));
         BodyPeriodTotal[2] = (BodyPeriodTotal[2]+((BodyShort_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyShort_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyShort_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0))));
         BodyPeriodTotal[1] = (BodyPeriodTotal[1]+((BodyShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((BodyShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((BodyShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0))));
         i += 1;
      }
      i = BodyLongTrailingIdx;
      while( (i<startIdx) ) {
         BodyPeriodTotal[4] = (BodyPeriodTotal[4]+((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-4)] - inOpen[(i-4)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-4)] - inLow[(i-4)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-4)] - inLow[(i-4)]) - Math.abs(inClose[(i-4)] - inOpen[(i-4)])) : 0.0))));
         BodyPeriodTotal[0] = (BodyPeriodTotal[0]+((BodyLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0))));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( ((((((((((((((((((((((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1)))==(0-(((inClose[(i-3)]>=inOpen[(i-3)])) ? (1) : ((0-1)))))&&((((inClose[(i-3)]>=inOpen[(i-3)])) ? (1) : ((0-1)))==(((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))))&&((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==(((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))))&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-(((inClose[i]>=inOpen[i])) ? (1) : ((0-1))))))&&(Math.min(inOpen[(i-3)], inClose[(i-3)])<inHigh[(i-4)]))&&(Math.max(inOpen[(i-3)], inClose[(i-3)])>inLow[(i-4)]))&&(Math.min(inOpen[(i-2)], inClose[(i-2)])<inHigh[(i-4)]))&&(Math.max(inOpen[(i-2)], inClose[(i-2)])>inLow[(i-4)]))&&(Math.min(inOpen[(i-1)], inClose[(i-1)])<inHigh[(i-4)]))&&(Math.max(inOpen[(i-1)], inClose[(i-1)])>inLow[(i-4)]))&&((inClose[(i-2)]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))<(inClose[(i-3)]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))))&&((inClose[(i-1)]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))<(inClose[(i-2)]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))))&&((inOpen[i]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))>(inClose[(i-1)]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))))&&((inClose[i]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))>(inClose[(i-4)]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))))&&(Math.abs((inClose[(i-4)]-inOpen[(i-4)]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyPeriodTotal[4] / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-4)] - inOpen[(i-4)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-4)] - inLow[(i-4)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-4)] - inLow[(i-4)]) - Math.abs(inClose[(i-4)] - inOpen[(i-4)])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.abs((inClose[(i-3)]-inOpen[(i-3)]))<((BodyShort_factor * (((BodyShort_avgPeriod != 0) ? (BodyPeriodTotal[3] / BodyShort_avgPeriod) : ((BodyShort_rangeType == 0) ? (Math.abs(inClose[(i-3)] - inOpen[(i-3)])) : ((BodyShort_rangeType == 1) ? (inHigh[(i-3)] - inLow[(i-3)]) : ((BodyShort_rangeType == 2) ? ((inHigh[(i-3)] - inLow[(i-3)]) - Math.abs(inClose[(i-3)] - inOpen[(i-3)])) : 0.0)))) / ((BodyShort_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.abs((inClose[(i-2)]-inOpen[(i-2)]))<((BodyShort_factor * (((BodyShort_avgPeriod != 0) ? (BodyPeriodTotal[2] / BodyShort_avgPeriod) : ((BodyShort_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyShort_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyShort_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((BodyShort_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))<((BodyShort_factor * (((BodyShort_avgPeriod != 0) ? (BodyPeriodTotal[1] / BodyShort_avgPeriod) : ((BodyShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((BodyShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((BodyShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((BodyShort_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.abs((inClose[i]-inOpen[i]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyPeriodTotal[0] / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0)))))) ) {
            outInteger[outIdx++] = (100*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))));
         } else {
            outInteger[outIdx++] = 0;
         }
         BodyPeriodTotal[4] = (BodyPeriodTotal[4]+(((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-4)] - inOpen[(i-4)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-4)] - inLow[(i-4)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-4)] - inLow[(i-4)]) - Math.abs(inClose[(i-4)] - inOpen[(i-4)])) : 0.0)))-((BodyLong_rangeType == 0) ? (Math.abs(inClose[(BodyLongTrailingIdx-4)] - inOpen[(BodyLongTrailingIdx-4)])) : ((BodyLong_rangeType == 1) ? (inHigh[(BodyLongTrailingIdx-4)] - inLow[(BodyLongTrailingIdx-4)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(BodyLongTrailingIdx-4)] - inLow[(BodyLongTrailingIdx-4)]) - Math.abs(inClose[(BodyLongTrailingIdx-4)] - inOpen[(BodyLongTrailingIdx-4)])) : 0.0)))));
         for( totIdx = 3; (totIdx>=1); totIdx -= 1 ) {
            BodyPeriodTotal[totIdx] = (BodyPeriodTotal[totIdx]+(((BodyShort_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((BodyShort_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((BodyShort_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((BodyShort_rangeType == 0) ? (Math.abs(inClose[(BodyShortTrailingIdx-totIdx)] - inOpen[(BodyShortTrailingIdx-totIdx)])) : ((BodyShort_rangeType == 1) ? (inHigh[(BodyShortTrailingIdx-totIdx)] - inLow[(BodyShortTrailingIdx-totIdx)]) : ((BodyShort_rangeType == 2) ? ((inHigh[(BodyShortTrailingIdx-totIdx)] - inLow[(BodyShortTrailingIdx-totIdx)]) - Math.abs(inClose[(BodyShortTrailingIdx-totIdx)] - inOpen[(BodyShortTrailingIdx-totIdx)])) : 0.0)))));
         }
         BodyPeriodTotal[0] = (BodyPeriodTotal[0]+(((BodyLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((BodyLong_rangeType == 0) ? (Math.abs(inClose[BodyLongTrailingIdx] - inOpen[BodyLongTrailingIdx])) : ((BodyLong_rangeType == 1) ? (inHigh[BodyLongTrailingIdx] - inLow[BodyLongTrailingIdx]) : ((BodyLong_rangeType == 2) ? ((inHigh[BodyLongTrailingIdx] - inLow[BodyLongTrailingIdx]) - Math.abs(inClose[BodyLongTrailingIdx] - inOpen[BodyLongTrailingIdx])) : 0.0)))));
         i += 1;
         BodyShortTrailingIdx += 1;
         BodyLongTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdlrisefall3methodsLogic( int startIdx,
                                            int endIdx,
                                            double inOpen[],
                                            double inHigh[],
                                            double inLow[],
                                            double inClose[],
                                            MInteger outBegIdx,
                                            MInteger outNBElement,
                                            int outInteger[] )
   {
      double[] BodyPeriodTotal = new double[5];
      int i = 0;
      int outIdx = 0;
      int totIdx = 0;
      int BodyShortTrailingIdx = 0;
      int BodyLongTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyLong_rangeType = this.candleSettings.bodyLong.rangeType;
      int BodyLong_avgPeriod = this.candleSettings.bodyLong.avgPeriod;
      double BodyLong_factor = this.candleSettings.bodyLong.factor;
      int BodyShort_rangeType = this.candleSettings.bodyShort.rangeType;
      int BodyShort_avgPeriod = this.candleSettings.bodyShort.avgPeriod;
      double BodyShort_factor = this.candleSettings.bodyShort.factor;
      lookbackTotal = cdlrisefall3methodsLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      BodyPeriodTotal[4] = 0;
      BodyPeriodTotal[3] = 0;
      BodyPeriodTotal[2] = 0;
      BodyPeriodTotal[1] = 0;
      BodyPeriodTotal[0] = 0;
      BodyShortTrailingIdx = (startIdx-BodyShort_avgPeriod);
      BodyLongTrailingIdx = (startIdx-BodyLong_avgPeriod);
      i = BodyShortTrailingIdx;
      while( (i<startIdx) ) {
         BodyPeriodTotal[3] = (BodyPeriodTotal[3]+((BodyShort_rangeType == 0) ? (Math.abs(inClose[(i-3)] - inOpen[(i-3)])) : ((BodyShort_rangeType == 1) ? (inHigh[(i-3)] - inLow[(i-3)]) : ((BodyShort_rangeType == 2) ? ((inHigh[(i-3)] - inLow[(i-3)]) - Math.abs(inClose[(i-3)] - inOpen[(i-3)])) : 0.0))));
         BodyPeriodTotal[2] = (BodyPeriodTotal[2]+((BodyShort_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyShort_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyShort_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0))));
         BodyPeriodTotal[1] = (BodyPeriodTotal[1]+((BodyShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((BodyShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((BodyShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0))));
         i += 1;
      }
      i = BodyLongTrailingIdx;
      while( (i<startIdx) ) {
         BodyPeriodTotal[4] = (BodyPeriodTotal[4]+((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-4)] - inOpen[(i-4)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-4)] - inLow[(i-4)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-4)] - inLow[(i-4)]) - Math.abs(inClose[(i-4)] - inOpen[(i-4)])) : 0.0))));
         BodyPeriodTotal[0] = (BodyPeriodTotal[0]+((BodyLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0))));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( ((((((((((((((((((((((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1)))==(0-(((inClose[(i-3)]>=inOpen[(i-3)])) ? (1) : ((0-1)))))&&((((inClose[(i-3)]>=inOpen[(i-3)])) ? (1) : ((0-1)))==(((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))))&&((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==(((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))))&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-(((inClose[i]>=inOpen[i])) ? (1) : ((0-1))))))&&(Math.min(inOpen[(i-3)], inClose[(i-3)])<inHigh[(i-4)]))&&(Math.max(inOpen[(i-3)], inClose[(i-3)])>inLow[(i-4)]))&&(Math.min(inOpen[(i-2)], inClose[(i-2)])<inHigh[(i-4)]))&&(Math.max(inOpen[(i-2)], inClose[(i-2)])>inLow[(i-4)]))&&(Math.min(inOpen[(i-1)], inClose[(i-1)])<inHigh[(i-4)]))&&(Math.max(inOpen[(i-1)], inClose[(i-1)])>inLow[(i-4)]))&&((inClose[(i-2)]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))<(inClose[(i-3)]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))))&&((inClose[(i-1)]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))<(inClose[(i-2)]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))))&&((inOpen[i]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))>(inClose[(i-1)]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))))&&((inClose[i]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))>(inClose[(i-4)]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))))&&(Math.abs((inClose[(i-4)]-inOpen[(i-4)]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyPeriodTotal[4] / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-4)] - inOpen[(i-4)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-4)] - inLow[(i-4)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-4)] - inLow[(i-4)]) - Math.abs(inClose[(i-4)] - inOpen[(i-4)])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.abs((inClose[(i-3)]-inOpen[(i-3)]))<((BodyShort_factor * (((BodyShort_avgPeriod != 0) ? (BodyPeriodTotal[3] / BodyShort_avgPeriod) : ((BodyShort_rangeType == 0) ? (Math.abs(inClose[(i-3)] - inOpen[(i-3)])) : ((BodyShort_rangeType == 1) ? (inHigh[(i-3)] - inLow[(i-3)]) : ((BodyShort_rangeType == 2) ? ((inHigh[(i-3)] - inLow[(i-3)]) - Math.abs(inClose[(i-3)] - inOpen[(i-3)])) : 0.0)))) / ((BodyShort_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.abs((inClose[(i-2)]-inOpen[(i-2)]))<((BodyShort_factor * (((BodyShort_avgPeriod != 0) ? (BodyPeriodTotal[2] / BodyShort_avgPeriod) : ((BodyShort_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyShort_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyShort_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((BodyShort_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))<((BodyShort_factor * (((BodyShort_avgPeriod != 0) ? (BodyPeriodTotal[1] / BodyShort_avgPeriod) : ((BodyShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((BodyShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((BodyShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((BodyShort_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.abs((inClose[i]-inOpen[i]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyPeriodTotal[0] / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0)))))) ) {
            outInteger[outIdx++] = (100*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))));
         } else {
            outInteger[outIdx++] = 0;
         }
         BodyPeriodTotal[4] = (BodyPeriodTotal[4]+(((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-4)] - inOpen[(i-4)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-4)] - inLow[(i-4)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-4)] - inLow[(i-4)]) - Math.abs(inClose[(i-4)] - inOpen[(i-4)])) : 0.0)))-((BodyLong_rangeType == 0) ? (Math.abs(inClose[(BodyLongTrailingIdx-4)] - inOpen[(BodyLongTrailingIdx-4)])) : ((BodyLong_rangeType == 1) ? (inHigh[(BodyLongTrailingIdx-4)] - inLow[(BodyLongTrailingIdx-4)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(BodyLongTrailingIdx-4)] - inLow[(BodyLongTrailingIdx-4)]) - Math.abs(inClose[(BodyLongTrailingIdx-4)] - inOpen[(BodyLongTrailingIdx-4)])) : 0.0)))));
         for( totIdx = 3; (totIdx>=1); totIdx -= 1 ) {
            BodyPeriodTotal[totIdx] = (BodyPeriodTotal[totIdx]+(((BodyShort_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((BodyShort_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((BodyShort_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((BodyShort_rangeType == 0) ? (Math.abs(inClose[(BodyShortTrailingIdx-totIdx)] - inOpen[(BodyShortTrailingIdx-totIdx)])) : ((BodyShort_rangeType == 1) ? (inHigh[(BodyShortTrailingIdx-totIdx)] - inLow[(BodyShortTrailingIdx-totIdx)]) : ((BodyShort_rangeType == 2) ? ((inHigh[(BodyShortTrailingIdx-totIdx)] - inLow[(BodyShortTrailingIdx-totIdx)]) - Math.abs(inClose[(BodyShortTrailingIdx-totIdx)] - inOpen[(BodyShortTrailingIdx-totIdx)])) : 0.0)))));
         }
         BodyPeriodTotal[0] = (BodyPeriodTotal[0]+(((BodyLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((BodyLong_rangeType == 0) ? (Math.abs(inClose[BodyLongTrailingIdx] - inOpen[BodyLongTrailingIdx])) : ((BodyLong_rangeType == 1) ? (inHigh[BodyLongTrailingIdx] - inLow[BodyLongTrailingIdx]) : ((BodyLong_rangeType == 2) ? ((inHigh[BodyLongTrailingIdx] - inLow[BodyLongTrailingIdx]) - Math.abs(inClose[BodyLongTrailingIdx] - inOpen[BodyLongTrailingIdx])) : 0.0)))));
         i += 1;
         BodyShortTrailingIdx += 1;
         BodyLongTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdlrisefall3methods( int startIdx,
                                       int endIdx,
                                       float inOpen[],
                                       float inHigh[],
                                       float inLow[],
                                       float inClose[],
                                       MInteger outBegIdx,
                                       MInteger outNBElement,
                                       int outInteger[] )
   {
      double[] BodyPeriodTotal = new double[5];
      int i = 0;
      int outIdx = 0;
      int totIdx = 0;
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
      lookbackTotal = cdlrisefall3methodsLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      BodyPeriodTotal[4] = 0;
      BodyPeriodTotal[3] = 0;
      BodyPeriodTotal[2] = 0;
      BodyPeriodTotal[1] = 0;
      BodyPeriodTotal[0] = 0;
      BodyShortTrailingIdx = (startIdx-BodyShort_avgPeriod);
      BodyLongTrailingIdx = (startIdx-BodyLong_avgPeriod);
      i = BodyShortTrailingIdx;
      while( (i<startIdx) ) {
         BodyPeriodTotal[3] = (BodyPeriodTotal[3]+((BodyShort_rangeType == 0) ? (Math.abs(inClose[(i-3)] - inOpen[(i-3)])) : ((BodyShort_rangeType == 1) ? (inHigh[(i-3)] - inLow[(i-3)]) : ((BodyShort_rangeType == 2) ? ((inHigh[(i-3)] - inLow[(i-3)]) - Math.abs(inClose[(i-3)] - inOpen[(i-3)])) : 0.0))));
         BodyPeriodTotal[2] = (BodyPeriodTotal[2]+((BodyShort_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyShort_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyShort_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0))));
         BodyPeriodTotal[1] = (BodyPeriodTotal[1]+((BodyShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((BodyShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((BodyShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0))));
         i += 1;
      }
      i = BodyLongTrailingIdx;
      while( (i<startIdx) ) {
         BodyPeriodTotal[4] = (BodyPeriodTotal[4]+((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-4)] - inOpen[(i-4)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-4)] - inLow[(i-4)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-4)] - inLow[(i-4)]) - Math.abs(inClose[(i-4)] - inOpen[(i-4)])) : 0.0))));
         BodyPeriodTotal[0] = (BodyPeriodTotal[0]+((BodyLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0))));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( ((((((((((((((((((((((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1)))==(0-(((inClose[(i-3)]>=inOpen[(i-3)])) ? (1) : ((0-1)))))&&((((inClose[(i-3)]>=inOpen[(i-3)])) ? (1) : ((0-1)))==(((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))))&&((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==(((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))))&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-(((inClose[i]>=inOpen[i])) ? (1) : ((0-1))))))&&(Math.min(inOpen[(i-3)], inClose[(i-3)])<inHigh[(i-4)]))&&(Math.max(inOpen[(i-3)], inClose[(i-3)])>inLow[(i-4)]))&&(Math.min(inOpen[(i-2)], inClose[(i-2)])<inHigh[(i-4)]))&&(Math.max(inOpen[(i-2)], inClose[(i-2)])>inLow[(i-4)]))&&(Math.min(inOpen[(i-1)], inClose[(i-1)])<inHigh[(i-4)]))&&(Math.max(inOpen[(i-1)], inClose[(i-1)])>inLow[(i-4)]))&&((inClose[(i-2)]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))<(inClose[(i-3)]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))))&&((inClose[(i-1)]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))<(inClose[(i-2)]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))))&&((inOpen[i]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))>(inClose[(i-1)]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))))&&((inClose[i]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))>(inClose[(i-4)]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))))&&(Math.abs((inClose[(i-4)]-inOpen[(i-4)]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyPeriodTotal[4] / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-4)] - inOpen[(i-4)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-4)] - inLow[(i-4)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-4)] - inLow[(i-4)]) - Math.abs(inClose[(i-4)] - inOpen[(i-4)])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.abs((inClose[(i-3)]-inOpen[(i-3)]))<((BodyShort_factor * (((BodyShort_avgPeriod != 0) ? (BodyPeriodTotal[3] / BodyShort_avgPeriod) : ((BodyShort_rangeType == 0) ? (Math.abs(inClose[(i-3)] - inOpen[(i-3)])) : ((BodyShort_rangeType == 1) ? (inHigh[(i-3)] - inLow[(i-3)]) : ((BodyShort_rangeType == 2) ? ((inHigh[(i-3)] - inLow[(i-3)]) - Math.abs(inClose[(i-3)] - inOpen[(i-3)])) : 0.0)))) / ((BodyShort_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.abs((inClose[(i-2)]-inOpen[(i-2)]))<((BodyShort_factor * (((BodyShort_avgPeriod != 0) ? (BodyPeriodTotal[2] / BodyShort_avgPeriod) : ((BodyShort_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyShort_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyShort_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((BodyShort_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))<((BodyShort_factor * (((BodyShort_avgPeriod != 0) ? (BodyPeriodTotal[1] / BodyShort_avgPeriod) : ((BodyShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((BodyShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((BodyShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((BodyShort_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.abs((inClose[i]-inOpen[i]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyPeriodTotal[0] / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0)))))) ) {
            outInteger[outIdx++] = (100*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))));
         } else {
            outInteger[outIdx++] = 0;
         }
         BodyPeriodTotal[4] = (BodyPeriodTotal[4]+(((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-4)] - inOpen[(i-4)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-4)] - inLow[(i-4)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-4)] - inLow[(i-4)]) - Math.abs(inClose[(i-4)] - inOpen[(i-4)])) : 0.0)))-((BodyLong_rangeType == 0) ? (Math.abs(inClose[(BodyLongTrailingIdx-4)] - inOpen[(BodyLongTrailingIdx-4)])) : ((BodyLong_rangeType == 1) ? (inHigh[(BodyLongTrailingIdx-4)] - inLow[(BodyLongTrailingIdx-4)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(BodyLongTrailingIdx-4)] - inLow[(BodyLongTrailingIdx-4)]) - Math.abs(inClose[(BodyLongTrailingIdx-4)] - inOpen[(BodyLongTrailingIdx-4)])) : 0.0)))));
         for( totIdx = 3; (totIdx>=1); totIdx -= 1 ) {
            BodyPeriodTotal[totIdx] = (BodyPeriodTotal[totIdx]+(((BodyShort_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((BodyShort_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((BodyShort_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((BodyShort_rangeType == 0) ? (Math.abs(inClose[(BodyShortTrailingIdx-totIdx)] - inOpen[(BodyShortTrailingIdx-totIdx)])) : ((BodyShort_rangeType == 1) ? (inHigh[(BodyShortTrailingIdx-totIdx)] - inLow[(BodyShortTrailingIdx-totIdx)]) : ((BodyShort_rangeType == 2) ? ((inHigh[(BodyShortTrailingIdx-totIdx)] - inLow[(BodyShortTrailingIdx-totIdx)]) - Math.abs(inClose[(BodyShortTrailingIdx-totIdx)] - inOpen[(BodyShortTrailingIdx-totIdx)])) : 0.0)))));
         }
         BodyPeriodTotal[0] = (BodyPeriodTotal[0]+(((BodyLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((BodyLong_rangeType == 0) ? (Math.abs(inClose[BodyLongTrailingIdx] - inOpen[BodyLongTrailingIdx])) : ((BodyLong_rangeType == 1) ? (inHigh[BodyLongTrailingIdx] - inLow[BodyLongTrailingIdx]) : ((BodyLong_rangeType == 2) ? ((inHigh[BodyLongTrailingIdx] - inLow[BodyLongTrailingIdx]) - Math.abs(inClose[BodyLongTrailingIdx] - inOpen[BodyLongTrailingIdx])) : 0.0)))));
         i += 1;
         BodyShortTrailingIdx += 1;
         BodyLongTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdlrisefall3methodsLogic( int startIdx,
                                            int endIdx,
                                            float inOpen[],
                                            float inHigh[],
                                            float inLow[],
                                            float inClose[],
                                            MInteger outBegIdx,
                                            MInteger outNBElement,
                                            int outInteger[] )
   {
      double[] BodyPeriodTotal = new double[5];
      int i = 0;
      int outIdx = 0;
      int totIdx = 0;
      int BodyShortTrailingIdx = 0;
      int BodyLongTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyLong_rangeType = this.candleSettings.bodyLong.rangeType;
      int BodyLong_avgPeriod = this.candleSettings.bodyLong.avgPeriod;
      double BodyLong_factor = this.candleSettings.bodyLong.factor;
      int BodyShort_rangeType = this.candleSettings.bodyShort.rangeType;
      int BodyShort_avgPeriod = this.candleSettings.bodyShort.avgPeriod;
      double BodyShort_factor = this.candleSettings.bodyShort.factor;
      lookbackTotal = cdlrisefall3methodsLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      BodyPeriodTotal[4] = 0;
      BodyPeriodTotal[3] = 0;
      BodyPeriodTotal[2] = 0;
      BodyPeriodTotal[1] = 0;
      BodyPeriodTotal[0] = 0;
      BodyShortTrailingIdx = (startIdx-BodyShort_avgPeriod);
      BodyLongTrailingIdx = (startIdx-BodyLong_avgPeriod);
      i = BodyShortTrailingIdx;
      while( (i<startIdx) ) {
         BodyPeriodTotal[3] = (BodyPeriodTotal[3]+((BodyShort_rangeType == 0) ? (Math.abs(inClose[(i-3)] - inOpen[(i-3)])) : ((BodyShort_rangeType == 1) ? (inHigh[(i-3)] - inLow[(i-3)]) : ((BodyShort_rangeType == 2) ? ((inHigh[(i-3)] - inLow[(i-3)]) - Math.abs(inClose[(i-3)] - inOpen[(i-3)])) : 0.0))));
         BodyPeriodTotal[2] = (BodyPeriodTotal[2]+((BodyShort_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyShort_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyShort_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0))));
         BodyPeriodTotal[1] = (BodyPeriodTotal[1]+((BodyShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((BodyShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((BodyShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0))));
         i += 1;
      }
      i = BodyLongTrailingIdx;
      while( (i<startIdx) ) {
         BodyPeriodTotal[4] = (BodyPeriodTotal[4]+((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-4)] - inOpen[(i-4)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-4)] - inLow[(i-4)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-4)] - inLow[(i-4)]) - Math.abs(inClose[(i-4)] - inOpen[(i-4)])) : 0.0))));
         BodyPeriodTotal[0] = (BodyPeriodTotal[0]+((BodyLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0))));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( ((((((((((((((((((((((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1)))==(0-(((inClose[(i-3)]>=inOpen[(i-3)])) ? (1) : ((0-1)))))&&((((inClose[(i-3)]>=inOpen[(i-3)])) ? (1) : ((0-1)))==(((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))))&&((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==(((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))))&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-(((inClose[i]>=inOpen[i])) ? (1) : ((0-1))))))&&(Math.min(inOpen[(i-3)], inClose[(i-3)])<inHigh[(i-4)]))&&(Math.max(inOpen[(i-3)], inClose[(i-3)])>inLow[(i-4)]))&&(Math.min(inOpen[(i-2)], inClose[(i-2)])<inHigh[(i-4)]))&&(Math.max(inOpen[(i-2)], inClose[(i-2)])>inLow[(i-4)]))&&(Math.min(inOpen[(i-1)], inClose[(i-1)])<inHigh[(i-4)]))&&(Math.max(inOpen[(i-1)], inClose[(i-1)])>inLow[(i-4)]))&&((inClose[(i-2)]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))<(inClose[(i-3)]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))))&&((inClose[(i-1)]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))<(inClose[(i-2)]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))))&&((inOpen[i]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))>(inClose[(i-1)]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))))&&((inClose[i]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))>(inClose[(i-4)]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))))&&(Math.abs((inClose[(i-4)]-inOpen[(i-4)]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyPeriodTotal[4] / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-4)] - inOpen[(i-4)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-4)] - inLow[(i-4)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-4)] - inLow[(i-4)]) - Math.abs(inClose[(i-4)] - inOpen[(i-4)])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.abs((inClose[(i-3)]-inOpen[(i-3)]))<((BodyShort_factor * (((BodyShort_avgPeriod != 0) ? (BodyPeriodTotal[3] / BodyShort_avgPeriod) : ((BodyShort_rangeType == 0) ? (Math.abs(inClose[(i-3)] - inOpen[(i-3)])) : ((BodyShort_rangeType == 1) ? (inHigh[(i-3)] - inLow[(i-3)]) : ((BodyShort_rangeType == 2) ? ((inHigh[(i-3)] - inLow[(i-3)]) - Math.abs(inClose[(i-3)] - inOpen[(i-3)])) : 0.0)))) / ((BodyShort_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.abs((inClose[(i-2)]-inOpen[(i-2)]))<((BodyShort_factor * (((BodyShort_avgPeriod != 0) ? (BodyPeriodTotal[2] / BodyShort_avgPeriod) : ((BodyShort_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyShort_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyShort_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((BodyShort_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))<((BodyShort_factor * (((BodyShort_avgPeriod != 0) ? (BodyPeriodTotal[1] / BodyShort_avgPeriod) : ((BodyShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((BodyShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((BodyShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((BodyShort_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.abs((inClose[i]-inOpen[i]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyPeriodTotal[0] / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0)))))) ) {
            outInteger[outIdx++] = (100*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))));
         } else {
            outInteger[outIdx++] = 0;
         }
         BodyPeriodTotal[4] = (BodyPeriodTotal[4]+(((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-4)] - inOpen[(i-4)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-4)] - inLow[(i-4)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-4)] - inLow[(i-4)]) - Math.abs(inClose[(i-4)] - inOpen[(i-4)])) : 0.0)))-((BodyLong_rangeType == 0) ? (Math.abs(inClose[(BodyLongTrailingIdx-4)] - inOpen[(BodyLongTrailingIdx-4)])) : ((BodyLong_rangeType == 1) ? (inHigh[(BodyLongTrailingIdx-4)] - inLow[(BodyLongTrailingIdx-4)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(BodyLongTrailingIdx-4)] - inLow[(BodyLongTrailingIdx-4)]) - Math.abs(inClose[(BodyLongTrailingIdx-4)] - inOpen[(BodyLongTrailingIdx-4)])) : 0.0)))));
         for( totIdx = 3; (totIdx>=1); totIdx -= 1 ) {
            BodyPeriodTotal[totIdx] = (BodyPeriodTotal[totIdx]+(((BodyShort_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((BodyShort_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((BodyShort_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((BodyShort_rangeType == 0) ? (Math.abs(inClose[(BodyShortTrailingIdx-totIdx)] - inOpen[(BodyShortTrailingIdx-totIdx)])) : ((BodyShort_rangeType == 1) ? (inHigh[(BodyShortTrailingIdx-totIdx)] - inLow[(BodyShortTrailingIdx-totIdx)]) : ((BodyShort_rangeType == 2) ? ((inHigh[(BodyShortTrailingIdx-totIdx)] - inLow[(BodyShortTrailingIdx-totIdx)]) - Math.abs(inClose[(BodyShortTrailingIdx-totIdx)] - inOpen[(BodyShortTrailingIdx-totIdx)])) : 0.0)))));
         }
         BodyPeriodTotal[0] = (BodyPeriodTotal[0]+(((BodyLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((BodyLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((BodyLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))-((BodyLong_rangeType == 0) ? (Math.abs(inClose[BodyLongTrailingIdx] - inOpen[BodyLongTrailingIdx])) : ((BodyLong_rangeType == 1) ? (inHigh[BodyLongTrailingIdx] - inLow[BodyLongTrailingIdx]) : ((BodyLong_rangeType == 2) ? ((inHigh[BodyLongTrailingIdx] - inLow[BodyLongTrailingIdx]) - Math.abs(inClose[BodyLongTrailingIdx] - inOpen[BodyLongTrailingIdx])) : 0.0)))));
         i += 1;
         BodyShortTrailingIdx += 1;
         BodyLongTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
