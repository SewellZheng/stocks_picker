/* Generated */
   public int cdladvanceblockLookback( )
   {
      int BodyLong_rangeType = this.candleSettings.bodyLong.rangeType;
      int BodyLong_avgPeriod = this.candleSettings.bodyLong.avgPeriod;
      double BodyLong_factor = this.candleSettings.bodyLong.factor;
      int Far_rangeType = this.candleSettings.far.rangeType;
      int Far_avgPeriod = this.candleSettings.far.avgPeriod;
      double Far_factor = this.candleSettings.far.factor;
      int Near_rangeType = this.candleSettings.near.rangeType;
      int Near_avgPeriod = this.candleSettings.near.avgPeriod;
      double Near_factor = this.candleSettings.near.factor;
      int ShadowLong_rangeType = this.candleSettings.shadowLong.rangeType;
      int ShadowLong_avgPeriod = this.candleSettings.shadowLong.avgPeriod;
      double ShadowLong_factor = this.candleSettings.shadowLong.factor;
      int ShadowShort_rangeType = this.candleSettings.shadowShort.rangeType;
      int ShadowShort_avgPeriod = this.candleSettings.shadowShort.avgPeriod;
      double ShadowShort_factor = this.candleSettings.shadowShort.factor;
      return (Math.max(Math.max(Math.max(ShadowLong_avgPeriod, ShadowShort_avgPeriod), Math.max(Far_avgPeriod, Near_avgPeriod)), BodyLong_avgPeriod)+2) ;

   }
   public RetCode cdladvanceblock( int startIdx,
                                   int endIdx,
                                   double inOpen[],
                                   double inHigh[],
                                   double inLow[],
                                   double inClose[],
                                   MInteger outBegIdx,
                                   MInteger outNBElement,
                                   int outInteger[] )
   {
      double[] ShadowShortPeriodTotal = new double[3];
      double[] ShadowLongPeriodTotal = new double[2];
      double[] NearPeriodTotal = new double[3];
      double[] FarPeriodTotal = new double[3];
      double BodyLongPeriodTotal = 0;
      int i = 0;
      int outIdx = 0;
      int totIdx = 0;
      int BodyLongTrailingIdx = 0;
      int ShadowShortTrailingIdx = 0;
      int ShadowLongTrailingIdx = 0;
      int NearTrailingIdx = 0;
      int FarTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyLong_rangeType = this.candleSettings.bodyLong.rangeType;
      int BodyLong_avgPeriod = this.candleSettings.bodyLong.avgPeriod;
      double BodyLong_factor = this.candleSettings.bodyLong.factor;
      int Far_rangeType = this.candleSettings.far.rangeType;
      int Far_avgPeriod = this.candleSettings.far.avgPeriod;
      double Far_factor = this.candleSettings.far.factor;
      int Near_rangeType = this.candleSettings.near.rangeType;
      int Near_avgPeriod = this.candleSettings.near.avgPeriod;
      double Near_factor = this.candleSettings.near.factor;
      int ShadowLong_rangeType = this.candleSettings.shadowLong.rangeType;
      int ShadowLong_avgPeriod = this.candleSettings.shadowLong.avgPeriod;
      double ShadowLong_factor = this.candleSettings.shadowLong.factor;
      int ShadowShort_rangeType = this.candleSettings.shadowShort.rangeType;
      int ShadowShort_avgPeriod = this.candleSettings.shadowShort.avgPeriod;
      double ShadowShort_factor = this.candleSettings.shadowShort.factor;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackTotal = cdladvanceblockLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      ShadowShortPeriodTotal[2] = 0;
      ShadowShortPeriodTotal[1] = 0;
      ShadowShortPeriodTotal[0] = 0;
      ShadowShortTrailingIdx = (startIdx-ShadowShort_avgPeriod);
      ShadowLongPeriodTotal[1] = 0;
      ShadowLongPeriodTotal[0] = 0;
      ShadowLongTrailingIdx = (startIdx-ShadowLong_avgPeriod);
      NearPeriodTotal[2] = 0;
      NearPeriodTotal[1] = 0;
      NearPeriodTotal[0] = 0;
      NearTrailingIdx = (startIdx-Near_avgPeriod);
      FarPeriodTotal[2] = 0;
      FarPeriodTotal[1] = 0;
      FarPeriodTotal[0] = 0;
      FarTrailingIdx = (startIdx-Far_avgPeriod);
      BodyLongPeriodTotal = 0;
      BodyLongTrailingIdx = (startIdx-BodyLong_avgPeriod);
      i = ShadowShortTrailingIdx;
      while( (i<startIdx) ) {
         ShadowShortPeriodTotal[2] = (ShadowShortPeriodTotal[2]+((ShadowShort_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((ShadowShort_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((ShadowShort_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0))));
         ShadowShortPeriodTotal[1] = (ShadowShortPeriodTotal[1]+((ShadowShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0))));
         ShadowShortPeriodTotal[0] = (ShadowShortPeriodTotal[0]+((ShadowShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0))));
         i += 1;
      }
      i = ShadowLongTrailingIdx;
      while( (i<startIdx) ) {
         ShadowLongPeriodTotal[1] = (ShadowLongPeriodTotal[1]+((ShadowLong_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowLong_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowLong_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0))));
         ShadowLongPeriodTotal[0] = (ShadowLongPeriodTotal[0]+((ShadowLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0))));
         i += 1;
      }
      i = NearTrailingIdx;
      while( (i<startIdx) ) {
         NearPeriodTotal[2] = (NearPeriodTotal[2]+((Near_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((Near_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((Near_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0))));
         NearPeriodTotal[1] = (NearPeriodTotal[1]+((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0))));
         i += 1;
      }
      i = FarTrailingIdx;
      while( (i<startIdx) ) {
         FarPeriodTotal[2] = (FarPeriodTotal[2]+((Far_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((Far_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((Far_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0))));
         FarPeriodTotal[1] = (FarPeriodTotal[1]+((Far_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Far_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Far_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0))));
         i += 1;
      }
      i = BodyLongTrailingIdx;
      while( (i<startIdx) ) {
         BodyLongPeriodTotal += ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( (((((((((((((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==1)&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==1))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==1))&&(inClose[i]>inClose[(i-1)]))&&(inClose[(i-1)]>inClose[(i-2)]))&&(inOpen[(i-1)]>inOpen[(i-2)]))&&(inOpen[(i-1)]<=(inClose[(i-2)]+((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal[2] / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((Near_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((Near_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0)))))))&&(inOpen[i]>inOpen[(i-1)]))&&(inOpen[i]<=(inClose[(i-1)]+((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal[1] / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0)))))))&&(Math.abs((inClose[(i-2)]-inOpen[(i-2)]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyLongPeriodTotal / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))))&&((inHigh[(i-2)]-(((inClose[(i-2)]>=inOpen[(i-2)])) ? (inClose[(i-2)]) : (inOpen[(i-2)])))<((ShadowShort_factor * (((ShadowShort_avgPeriod != 0) ? (ShadowShortPeriodTotal[2] / ShadowShort_avgPeriod) : ((ShadowShort_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((ShadowShort_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((ShadowShort_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((ShadowShort_rangeType == 2) ? 2.0 : 1.0))))))&&(((((Math.abs((inClose[(i-1)]-inOpen[(i-1)]))<(Math.abs((inClose[(i-2)]-inOpen[(i-2)]))-((Far_factor * (((Far_avgPeriod != 0) ? (FarPeriodTotal[2] / Far_avgPeriod) : ((Far_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((Far_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((Far_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((Far_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.abs((inClose[i]-inOpen[i]))<(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))+((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal[1] / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0)))))))||(Math.abs((inClose[i]-inOpen[i]))<(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))-((Far_factor * (((Far_avgPeriod != 0) ? (FarPeriodTotal[1] / Far_avgPeriod) : ((Far_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Far_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Far_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Far_rangeType == 2) ? 2.0 : 1.0)))))))||(((Math.abs((inClose[i]-inOpen[i]))<Math.abs((inClose[(i-1)]-inOpen[(i-1)])))&&(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))<Math.abs((inClose[(i-2)]-inOpen[(i-2)]))))&&(((inHigh[i]-(((inClose[i]>=inOpen[i])) ? (inClose[i]) : (inOpen[i])))>((ShadowShort_factor * (((ShadowShort_avgPeriod != 0) ? (ShadowShortPeriodTotal[0] / ShadowShort_avgPeriod) : ((ShadowShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowShort_rangeType == 2) ? 2.0 : 1.0)))))||((inHigh[(i-1)]-(((inClose[(i-1)]>=inOpen[(i-1)])) ? (inClose[(i-1)]) : (inOpen[(i-1)])))>((ShadowShort_factor * (((ShadowShort_avgPeriod != 0) ? (ShadowShortPeriodTotal[1] / ShadowShort_avgPeriod) : ((ShadowShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((ShadowShort_rangeType == 2) ? 2.0 : 1.0))))))))||((Math.abs((inClose[i]-inOpen[i]))<Math.abs((inClose[(i-1)]-inOpen[(i-1)])))&&((inHigh[i]-(((inClose[i]>=inOpen[i])) ? (inClose[i]) : (inOpen[i])))>((ShadowLong_factor * (((ShadowLong_avgPeriod != 0) ? (ShadowLongPeriodTotal[0] / ShadowLong_avgPeriod) : ((ShadowLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowLong_rangeType == 2) ? 2.0 : 1.0)))))))) ) {
            outInteger[outIdx++] = (0-100);
         } else {
            outInteger[outIdx++] = 0;
         }
         for( totIdx = 2; (totIdx>=0); totIdx -= 1 ) {
            ShadowShortPeriodTotal[totIdx] = (ShadowShortPeriodTotal[totIdx]+(((ShadowShort_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((ShadowShort_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((ShadowShort_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((ShadowShort_rangeType == 0) ? (Math.abs(inClose[(ShadowShortTrailingIdx-totIdx)] - inOpen[(ShadowShortTrailingIdx-totIdx)])) : ((ShadowShort_rangeType == 1) ? (inHigh[(ShadowShortTrailingIdx-totIdx)] - inLow[(ShadowShortTrailingIdx-totIdx)]) : ((ShadowShort_rangeType == 2) ? ((inHigh[(ShadowShortTrailingIdx-totIdx)] - inLow[(ShadowShortTrailingIdx-totIdx)]) - Math.abs(inClose[(ShadowShortTrailingIdx-totIdx)] - inOpen[(ShadowShortTrailingIdx-totIdx)])) : 0.0)))));
         }
         for( totIdx = 1; (totIdx>=0); totIdx -= 1 ) {
            ShadowLongPeriodTotal[totIdx] = (ShadowLongPeriodTotal[totIdx]+(((ShadowLong_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((ShadowLong_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((ShadowLong_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((ShadowLong_rangeType == 0) ? (Math.abs(inClose[(ShadowLongTrailingIdx-totIdx)] - inOpen[(ShadowLongTrailingIdx-totIdx)])) : ((ShadowLong_rangeType == 1) ? (inHigh[(ShadowLongTrailingIdx-totIdx)] - inLow[(ShadowLongTrailingIdx-totIdx)]) : ((ShadowLong_rangeType == 2) ? ((inHigh[(ShadowLongTrailingIdx-totIdx)] - inLow[(ShadowLongTrailingIdx-totIdx)]) - Math.abs(inClose[(ShadowLongTrailingIdx-totIdx)] - inOpen[(ShadowLongTrailingIdx-totIdx)])) : 0.0)))));
         }
         for( totIdx = 2; (totIdx>=1); totIdx -= 1 ) {
            FarPeriodTotal[totIdx] = (FarPeriodTotal[totIdx]+(((Far_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((Far_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((Far_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((Far_rangeType == 0) ? (Math.abs(inClose[(FarTrailingIdx-totIdx)] - inOpen[(FarTrailingIdx-totIdx)])) : ((Far_rangeType == 1) ? (inHigh[(FarTrailingIdx-totIdx)] - inLow[(FarTrailingIdx-totIdx)]) : ((Far_rangeType == 2) ? ((inHigh[(FarTrailingIdx-totIdx)] - inLow[(FarTrailingIdx-totIdx)]) - Math.abs(inClose[(FarTrailingIdx-totIdx)] - inOpen[(FarTrailingIdx-totIdx)])) : 0.0)))));
            NearPeriodTotal[totIdx] = (NearPeriodTotal[totIdx]+(((Near_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((Near_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((Near_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((Near_rangeType == 0) ? (Math.abs(inClose[(NearTrailingIdx-totIdx)] - inOpen[(NearTrailingIdx-totIdx)])) : ((Near_rangeType == 1) ? (inHigh[(NearTrailingIdx-totIdx)] - inLow[(NearTrailingIdx-totIdx)]) : ((Near_rangeType == 2) ? ((inHigh[(NearTrailingIdx-totIdx)] - inLow[(NearTrailingIdx-totIdx)]) - Math.abs(inClose[(NearTrailingIdx-totIdx)] - inOpen[(NearTrailingIdx-totIdx)])) : 0.0)))));
         }
         BodyLongPeriodTotal += (((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))-((BodyLong_rangeType == 0) ? (Math.abs(inClose[(BodyLongTrailingIdx-2)] - inOpen[(BodyLongTrailingIdx-2)])) : ((BodyLong_rangeType == 1) ? (inHigh[(BodyLongTrailingIdx-2)] - inLow[(BodyLongTrailingIdx-2)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(BodyLongTrailingIdx-2)] - inLow[(BodyLongTrailingIdx-2)]) - Math.abs(inClose[(BodyLongTrailingIdx-2)] - inOpen[(BodyLongTrailingIdx-2)])) : 0.0))));
         i += 1;
         ShadowShortTrailingIdx += 1;
         ShadowLongTrailingIdx += 1;
         NearTrailingIdx += 1;
         FarTrailingIdx += 1;
         BodyLongTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdladvanceblockLogic( int startIdx,
                                        int endIdx,
                                        double inOpen[],
                                        double inHigh[],
                                        double inLow[],
                                        double inClose[],
                                        MInteger outBegIdx,
                                        MInteger outNBElement,
                                        int outInteger[] )
   {
      double[] ShadowShortPeriodTotal = new double[3];
      double[] ShadowLongPeriodTotal = new double[2];
      double[] NearPeriodTotal = new double[3];
      double[] FarPeriodTotal = new double[3];
      double BodyLongPeriodTotal = 0;
      int i = 0;
      int outIdx = 0;
      int totIdx = 0;
      int BodyLongTrailingIdx = 0;
      int ShadowShortTrailingIdx = 0;
      int ShadowLongTrailingIdx = 0;
      int NearTrailingIdx = 0;
      int FarTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyLong_rangeType = this.candleSettings.bodyLong.rangeType;
      int BodyLong_avgPeriod = this.candleSettings.bodyLong.avgPeriod;
      double BodyLong_factor = this.candleSettings.bodyLong.factor;
      int Far_rangeType = this.candleSettings.far.rangeType;
      int Far_avgPeriod = this.candleSettings.far.avgPeriod;
      double Far_factor = this.candleSettings.far.factor;
      int Near_rangeType = this.candleSettings.near.rangeType;
      int Near_avgPeriod = this.candleSettings.near.avgPeriod;
      double Near_factor = this.candleSettings.near.factor;
      int ShadowLong_rangeType = this.candleSettings.shadowLong.rangeType;
      int ShadowLong_avgPeriod = this.candleSettings.shadowLong.avgPeriod;
      double ShadowLong_factor = this.candleSettings.shadowLong.factor;
      int ShadowShort_rangeType = this.candleSettings.shadowShort.rangeType;
      int ShadowShort_avgPeriod = this.candleSettings.shadowShort.avgPeriod;
      double ShadowShort_factor = this.candleSettings.shadowShort.factor;
      lookbackTotal = cdladvanceblockLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      ShadowShortPeriodTotal[2] = 0;
      ShadowShortPeriodTotal[1] = 0;
      ShadowShortPeriodTotal[0] = 0;
      ShadowShortTrailingIdx = (startIdx-ShadowShort_avgPeriod);
      ShadowLongPeriodTotal[1] = 0;
      ShadowLongPeriodTotal[0] = 0;
      ShadowLongTrailingIdx = (startIdx-ShadowLong_avgPeriod);
      NearPeriodTotal[2] = 0;
      NearPeriodTotal[1] = 0;
      NearPeriodTotal[0] = 0;
      NearTrailingIdx = (startIdx-Near_avgPeriod);
      FarPeriodTotal[2] = 0;
      FarPeriodTotal[1] = 0;
      FarPeriodTotal[0] = 0;
      FarTrailingIdx = (startIdx-Far_avgPeriod);
      BodyLongPeriodTotal = 0;
      BodyLongTrailingIdx = (startIdx-BodyLong_avgPeriod);
      i = ShadowShortTrailingIdx;
      while( (i<startIdx) ) {
         ShadowShortPeriodTotal[2] = (ShadowShortPeriodTotal[2]+((ShadowShort_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((ShadowShort_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((ShadowShort_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0))));
         ShadowShortPeriodTotal[1] = (ShadowShortPeriodTotal[1]+((ShadowShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0))));
         ShadowShortPeriodTotal[0] = (ShadowShortPeriodTotal[0]+((ShadowShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0))));
         i += 1;
      }
      i = ShadowLongTrailingIdx;
      while( (i<startIdx) ) {
         ShadowLongPeriodTotal[1] = (ShadowLongPeriodTotal[1]+((ShadowLong_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowLong_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowLong_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0))));
         ShadowLongPeriodTotal[0] = (ShadowLongPeriodTotal[0]+((ShadowLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0))));
         i += 1;
      }
      i = NearTrailingIdx;
      while( (i<startIdx) ) {
         NearPeriodTotal[2] = (NearPeriodTotal[2]+((Near_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((Near_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((Near_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0))));
         NearPeriodTotal[1] = (NearPeriodTotal[1]+((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0))));
         i += 1;
      }
      i = FarTrailingIdx;
      while( (i<startIdx) ) {
         FarPeriodTotal[2] = (FarPeriodTotal[2]+((Far_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((Far_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((Far_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0))));
         FarPeriodTotal[1] = (FarPeriodTotal[1]+((Far_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Far_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Far_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0))));
         i += 1;
      }
      i = BodyLongTrailingIdx;
      while( (i<startIdx) ) {
         BodyLongPeriodTotal += ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( (((((((((((((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==1)&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==1))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==1))&&(inClose[i]>inClose[(i-1)]))&&(inClose[(i-1)]>inClose[(i-2)]))&&(inOpen[(i-1)]>inOpen[(i-2)]))&&(inOpen[(i-1)]<=(inClose[(i-2)]+((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal[2] / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((Near_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((Near_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0)))))))&&(inOpen[i]>inOpen[(i-1)]))&&(inOpen[i]<=(inClose[(i-1)]+((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal[1] / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0)))))))&&(Math.abs((inClose[(i-2)]-inOpen[(i-2)]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyLongPeriodTotal / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))))&&((inHigh[(i-2)]-(((inClose[(i-2)]>=inOpen[(i-2)])) ? (inClose[(i-2)]) : (inOpen[(i-2)])))<((ShadowShort_factor * (((ShadowShort_avgPeriod != 0) ? (ShadowShortPeriodTotal[2] / ShadowShort_avgPeriod) : ((ShadowShort_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((ShadowShort_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((ShadowShort_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((ShadowShort_rangeType == 2) ? 2.0 : 1.0))))))&&(((((Math.abs((inClose[(i-1)]-inOpen[(i-1)]))<(Math.abs((inClose[(i-2)]-inOpen[(i-2)]))-((Far_factor * (((Far_avgPeriod != 0) ? (FarPeriodTotal[2] / Far_avgPeriod) : ((Far_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((Far_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((Far_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((Far_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.abs((inClose[i]-inOpen[i]))<(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))+((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal[1] / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0)))))))||(Math.abs((inClose[i]-inOpen[i]))<(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))-((Far_factor * (((Far_avgPeriod != 0) ? (FarPeriodTotal[1] / Far_avgPeriod) : ((Far_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Far_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Far_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Far_rangeType == 2) ? 2.0 : 1.0)))))))||(((Math.abs((inClose[i]-inOpen[i]))<Math.abs((inClose[(i-1)]-inOpen[(i-1)])))&&(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))<Math.abs((inClose[(i-2)]-inOpen[(i-2)]))))&&(((inHigh[i]-(((inClose[i]>=inOpen[i])) ? (inClose[i]) : (inOpen[i])))>((ShadowShort_factor * (((ShadowShort_avgPeriod != 0) ? (ShadowShortPeriodTotal[0] / ShadowShort_avgPeriod) : ((ShadowShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowShort_rangeType == 2) ? 2.0 : 1.0)))))||((inHigh[(i-1)]-(((inClose[(i-1)]>=inOpen[(i-1)])) ? (inClose[(i-1)]) : (inOpen[(i-1)])))>((ShadowShort_factor * (((ShadowShort_avgPeriod != 0) ? (ShadowShortPeriodTotal[1] / ShadowShort_avgPeriod) : ((ShadowShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((ShadowShort_rangeType == 2) ? 2.0 : 1.0))))))))||((Math.abs((inClose[i]-inOpen[i]))<Math.abs((inClose[(i-1)]-inOpen[(i-1)])))&&((inHigh[i]-(((inClose[i]>=inOpen[i])) ? (inClose[i]) : (inOpen[i])))>((ShadowLong_factor * (((ShadowLong_avgPeriod != 0) ? (ShadowLongPeriodTotal[0] / ShadowLong_avgPeriod) : ((ShadowLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowLong_rangeType == 2) ? 2.0 : 1.0)))))))) ) {
            outInteger[outIdx++] = (0-100);
         } else {
            outInteger[outIdx++] = 0;
         }
         for( totIdx = 2; (totIdx>=0); totIdx -= 1 ) {
            ShadowShortPeriodTotal[totIdx] = (ShadowShortPeriodTotal[totIdx]+(((ShadowShort_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((ShadowShort_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((ShadowShort_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((ShadowShort_rangeType == 0) ? (Math.abs(inClose[(ShadowShortTrailingIdx-totIdx)] - inOpen[(ShadowShortTrailingIdx-totIdx)])) : ((ShadowShort_rangeType == 1) ? (inHigh[(ShadowShortTrailingIdx-totIdx)] - inLow[(ShadowShortTrailingIdx-totIdx)]) : ((ShadowShort_rangeType == 2) ? ((inHigh[(ShadowShortTrailingIdx-totIdx)] - inLow[(ShadowShortTrailingIdx-totIdx)]) - Math.abs(inClose[(ShadowShortTrailingIdx-totIdx)] - inOpen[(ShadowShortTrailingIdx-totIdx)])) : 0.0)))));
         }
         for( totIdx = 1; (totIdx>=0); totIdx -= 1 ) {
            ShadowLongPeriodTotal[totIdx] = (ShadowLongPeriodTotal[totIdx]+(((ShadowLong_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((ShadowLong_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((ShadowLong_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((ShadowLong_rangeType == 0) ? (Math.abs(inClose[(ShadowLongTrailingIdx-totIdx)] - inOpen[(ShadowLongTrailingIdx-totIdx)])) : ((ShadowLong_rangeType == 1) ? (inHigh[(ShadowLongTrailingIdx-totIdx)] - inLow[(ShadowLongTrailingIdx-totIdx)]) : ((ShadowLong_rangeType == 2) ? ((inHigh[(ShadowLongTrailingIdx-totIdx)] - inLow[(ShadowLongTrailingIdx-totIdx)]) - Math.abs(inClose[(ShadowLongTrailingIdx-totIdx)] - inOpen[(ShadowLongTrailingIdx-totIdx)])) : 0.0)))));
         }
         for( totIdx = 2; (totIdx>=1); totIdx -= 1 ) {
            FarPeriodTotal[totIdx] = (FarPeriodTotal[totIdx]+(((Far_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((Far_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((Far_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((Far_rangeType == 0) ? (Math.abs(inClose[(FarTrailingIdx-totIdx)] - inOpen[(FarTrailingIdx-totIdx)])) : ((Far_rangeType == 1) ? (inHigh[(FarTrailingIdx-totIdx)] - inLow[(FarTrailingIdx-totIdx)]) : ((Far_rangeType == 2) ? ((inHigh[(FarTrailingIdx-totIdx)] - inLow[(FarTrailingIdx-totIdx)]) - Math.abs(inClose[(FarTrailingIdx-totIdx)] - inOpen[(FarTrailingIdx-totIdx)])) : 0.0)))));
            NearPeriodTotal[totIdx] = (NearPeriodTotal[totIdx]+(((Near_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((Near_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((Near_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((Near_rangeType == 0) ? (Math.abs(inClose[(NearTrailingIdx-totIdx)] - inOpen[(NearTrailingIdx-totIdx)])) : ((Near_rangeType == 1) ? (inHigh[(NearTrailingIdx-totIdx)] - inLow[(NearTrailingIdx-totIdx)]) : ((Near_rangeType == 2) ? ((inHigh[(NearTrailingIdx-totIdx)] - inLow[(NearTrailingIdx-totIdx)]) - Math.abs(inClose[(NearTrailingIdx-totIdx)] - inOpen[(NearTrailingIdx-totIdx)])) : 0.0)))));
         }
         BodyLongPeriodTotal += (((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))-((BodyLong_rangeType == 0) ? (Math.abs(inClose[(BodyLongTrailingIdx-2)] - inOpen[(BodyLongTrailingIdx-2)])) : ((BodyLong_rangeType == 1) ? (inHigh[(BodyLongTrailingIdx-2)] - inLow[(BodyLongTrailingIdx-2)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(BodyLongTrailingIdx-2)] - inLow[(BodyLongTrailingIdx-2)]) - Math.abs(inClose[(BodyLongTrailingIdx-2)] - inOpen[(BodyLongTrailingIdx-2)])) : 0.0))));
         i += 1;
         ShadowShortTrailingIdx += 1;
         ShadowLongTrailingIdx += 1;
         NearTrailingIdx += 1;
         FarTrailingIdx += 1;
         BodyLongTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdladvanceblock( int startIdx,
                                   int endIdx,
                                   float inOpen[],
                                   float inHigh[],
                                   float inLow[],
                                   float inClose[],
                                   MInteger outBegIdx,
                                   MInteger outNBElement,
                                   int outInteger[] )
   {
      double[] ShadowShortPeriodTotal = new double[3];
      double[] ShadowLongPeriodTotal = new double[2];
      double[] NearPeriodTotal = new double[3];
      double[] FarPeriodTotal = new double[3];
      double BodyLongPeriodTotal = 0;
      int i = 0;
      int outIdx = 0;
      int totIdx = 0;
      int BodyLongTrailingIdx = 0;
      int ShadowShortTrailingIdx = 0;
      int ShadowLongTrailingIdx = 0;
      int NearTrailingIdx = 0;
      int FarTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyLong_rangeType = this.candleSettings.bodyLong.rangeType;
      int BodyLong_avgPeriod = this.candleSettings.bodyLong.avgPeriod;
      double BodyLong_factor = this.candleSettings.bodyLong.factor;
      int Far_rangeType = this.candleSettings.far.rangeType;
      int Far_avgPeriod = this.candleSettings.far.avgPeriod;
      double Far_factor = this.candleSettings.far.factor;
      int Near_rangeType = this.candleSettings.near.rangeType;
      int Near_avgPeriod = this.candleSettings.near.avgPeriod;
      double Near_factor = this.candleSettings.near.factor;
      int ShadowLong_rangeType = this.candleSettings.shadowLong.rangeType;
      int ShadowLong_avgPeriod = this.candleSettings.shadowLong.avgPeriod;
      double ShadowLong_factor = this.candleSettings.shadowLong.factor;
      int ShadowShort_rangeType = this.candleSettings.shadowShort.rangeType;
      int ShadowShort_avgPeriod = this.candleSettings.shadowShort.avgPeriod;
      double ShadowShort_factor = this.candleSettings.shadowShort.factor;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackTotal = cdladvanceblockLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      ShadowShortPeriodTotal[2] = 0;
      ShadowShortPeriodTotal[1] = 0;
      ShadowShortPeriodTotal[0] = 0;
      ShadowShortTrailingIdx = (startIdx-ShadowShort_avgPeriod);
      ShadowLongPeriodTotal[1] = 0;
      ShadowLongPeriodTotal[0] = 0;
      ShadowLongTrailingIdx = (startIdx-ShadowLong_avgPeriod);
      NearPeriodTotal[2] = 0;
      NearPeriodTotal[1] = 0;
      NearPeriodTotal[0] = 0;
      NearTrailingIdx = (startIdx-Near_avgPeriod);
      FarPeriodTotal[2] = 0;
      FarPeriodTotal[1] = 0;
      FarPeriodTotal[0] = 0;
      FarTrailingIdx = (startIdx-Far_avgPeriod);
      BodyLongPeriodTotal = 0;
      BodyLongTrailingIdx = (startIdx-BodyLong_avgPeriod);
      i = ShadowShortTrailingIdx;
      while( (i<startIdx) ) {
         ShadowShortPeriodTotal[2] = (ShadowShortPeriodTotal[2]+((ShadowShort_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((ShadowShort_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((ShadowShort_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0))));
         ShadowShortPeriodTotal[1] = (ShadowShortPeriodTotal[1]+((ShadowShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0))));
         ShadowShortPeriodTotal[0] = (ShadowShortPeriodTotal[0]+((ShadowShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0))));
         i += 1;
      }
      i = ShadowLongTrailingIdx;
      while( (i<startIdx) ) {
         ShadowLongPeriodTotal[1] = (ShadowLongPeriodTotal[1]+((ShadowLong_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowLong_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowLong_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0))));
         ShadowLongPeriodTotal[0] = (ShadowLongPeriodTotal[0]+((ShadowLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0))));
         i += 1;
      }
      i = NearTrailingIdx;
      while( (i<startIdx) ) {
         NearPeriodTotal[2] = (NearPeriodTotal[2]+((Near_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((Near_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((Near_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0))));
         NearPeriodTotal[1] = (NearPeriodTotal[1]+((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0))));
         i += 1;
      }
      i = FarTrailingIdx;
      while( (i<startIdx) ) {
         FarPeriodTotal[2] = (FarPeriodTotal[2]+((Far_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((Far_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((Far_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0))));
         FarPeriodTotal[1] = (FarPeriodTotal[1]+((Far_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Far_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Far_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0))));
         i += 1;
      }
      i = BodyLongTrailingIdx;
      while( (i<startIdx) ) {
         BodyLongPeriodTotal += ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( (((((((((((((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==1)&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==1))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==1))&&(inClose[i]>inClose[(i-1)]))&&(inClose[(i-1)]>inClose[(i-2)]))&&(inOpen[(i-1)]>inOpen[(i-2)]))&&(inOpen[(i-1)]<=(inClose[(i-2)]+((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal[2] / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((Near_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((Near_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0)))))))&&(inOpen[i]>inOpen[(i-1)]))&&(inOpen[i]<=(inClose[(i-1)]+((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal[1] / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0)))))))&&(Math.abs((inClose[(i-2)]-inOpen[(i-2)]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyLongPeriodTotal / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))))&&((inHigh[(i-2)]-(((inClose[(i-2)]>=inOpen[(i-2)])) ? (inClose[(i-2)]) : (inOpen[(i-2)])))<((ShadowShort_factor * (((ShadowShort_avgPeriod != 0) ? (ShadowShortPeriodTotal[2] / ShadowShort_avgPeriod) : ((ShadowShort_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((ShadowShort_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((ShadowShort_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((ShadowShort_rangeType == 2) ? 2.0 : 1.0))))))&&(((((Math.abs((inClose[(i-1)]-inOpen[(i-1)]))<(Math.abs((inClose[(i-2)]-inOpen[(i-2)]))-((Far_factor * (((Far_avgPeriod != 0) ? (FarPeriodTotal[2] / Far_avgPeriod) : ((Far_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((Far_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((Far_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((Far_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.abs((inClose[i]-inOpen[i]))<(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))+((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal[1] / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0)))))))||(Math.abs((inClose[i]-inOpen[i]))<(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))-((Far_factor * (((Far_avgPeriod != 0) ? (FarPeriodTotal[1] / Far_avgPeriod) : ((Far_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Far_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Far_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Far_rangeType == 2) ? 2.0 : 1.0)))))))||(((Math.abs((inClose[i]-inOpen[i]))<Math.abs((inClose[(i-1)]-inOpen[(i-1)])))&&(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))<Math.abs((inClose[(i-2)]-inOpen[(i-2)]))))&&(((inHigh[i]-(((inClose[i]>=inOpen[i])) ? (inClose[i]) : (inOpen[i])))>((ShadowShort_factor * (((ShadowShort_avgPeriod != 0) ? (ShadowShortPeriodTotal[0] / ShadowShort_avgPeriod) : ((ShadowShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowShort_rangeType == 2) ? 2.0 : 1.0)))))||((inHigh[(i-1)]-(((inClose[(i-1)]>=inOpen[(i-1)])) ? (inClose[(i-1)]) : (inOpen[(i-1)])))>((ShadowShort_factor * (((ShadowShort_avgPeriod != 0) ? (ShadowShortPeriodTotal[1] / ShadowShort_avgPeriod) : ((ShadowShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((ShadowShort_rangeType == 2) ? 2.0 : 1.0))))))))||((Math.abs((inClose[i]-inOpen[i]))<Math.abs((inClose[(i-1)]-inOpen[(i-1)])))&&((inHigh[i]-(((inClose[i]>=inOpen[i])) ? (inClose[i]) : (inOpen[i])))>((ShadowLong_factor * (((ShadowLong_avgPeriod != 0) ? (ShadowLongPeriodTotal[0] / ShadowLong_avgPeriod) : ((ShadowLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowLong_rangeType == 2) ? 2.0 : 1.0)))))))) ) {
            outInteger[outIdx++] = (0-100);
         } else {
            outInteger[outIdx++] = 0;
         }
         for( totIdx = 2; (totIdx>=0); totIdx -= 1 ) {
            ShadowShortPeriodTotal[totIdx] = (ShadowShortPeriodTotal[totIdx]+(((ShadowShort_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((ShadowShort_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((ShadowShort_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((ShadowShort_rangeType == 0) ? (Math.abs(inClose[(ShadowShortTrailingIdx-totIdx)] - inOpen[(ShadowShortTrailingIdx-totIdx)])) : ((ShadowShort_rangeType == 1) ? (inHigh[(ShadowShortTrailingIdx-totIdx)] - inLow[(ShadowShortTrailingIdx-totIdx)]) : ((ShadowShort_rangeType == 2) ? ((inHigh[(ShadowShortTrailingIdx-totIdx)] - inLow[(ShadowShortTrailingIdx-totIdx)]) - Math.abs(inClose[(ShadowShortTrailingIdx-totIdx)] - inOpen[(ShadowShortTrailingIdx-totIdx)])) : 0.0)))));
         }
         for( totIdx = 1; (totIdx>=0); totIdx -= 1 ) {
            ShadowLongPeriodTotal[totIdx] = (ShadowLongPeriodTotal[totIdx]+(((ShadowLong_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((ShadowLong_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((ShadowLong_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((ShadowLong_rangeType == 0) ? (Math.abs(inClose[(ShadowLongTrailingIdx-totIdx)] - inOpen[(ShadowLongTrailingIdx-totIdx)])) : ((ShadowLong_rangeType == 1) ? (inHigh[(ShadowLongTrailingIdx-totIdx)] - inLow[(ShadowLongTrailingIdx-totIdx)]) : ((ShadowLong_rangeType == 2) ? ((inHigh[(ShadowLongTrailingIdx-totIdx)] - inLow[(ShadowLongTrailingIdx-totIdx)]) - Math.abs(inClose[(ShadowLongTrailingIdx-totIdx)] - inOpen[(ShadowLongTrailingIdx-totIdx)])) : 0.0)))));
         }
         for( totIdx = 2; (totIdx>=1); totIdx -= 1 ) {
            FarPeriodTotal[totIdx] = (FarPeriodTotal[totIdx]+(((Far_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((Far_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((Far_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((Far_rangeType == 0) ? (Math.abs(inClose[(FarTrailingIdx-totIdx)] - inOpen[(FarTrailingIdx-totIdx)])) : ((Far_rangeType == 1) ? (inHigh[(FarTrailingIdx-totIdx)] - inLow[(FarTrailingIdx-totIdx)]) : ((Far_rangeType == 2) ? ((inHigh[(FarTrailingIdx-totIdx)] - inLow[(FarTrailingIdx-totIdx)]) - Math.abs(inClose[(FarTrailingIdx-totIdx)] - inOpen[(FarTrailingIdx-totIdx)])) : 0.0)))));
            NearPeriodTotal[totIdx] = (NearPeriodTotal[totIdx]+(((Near_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((Near_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((Near_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((Near_rangeType == 0) ? (Math.abs(inClose[(NearTrailingIdx-totIdx)] - inOpen[(NearTrailingIdx-totIdx)])) : ((Near_rangeType == 1) ? (inHigh[(NearTrailingIdx-totIdx)] - inLow[(NearTrailingIdx-totIdx)]) : ((Near_rangeType == 2) ? ((inHigh[(NearTrailingIdx-totIdx)] - inLow[(NearTrailingIdx-totIdx)]) - Math.abs(inClose[(NearTrailingIdx-totIdx)] - inOpen[(NearTrailingIdx-totIdx)])) : 0.0)))));
         }
         BodyLongPeriodTotal += (((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))-((BodyLong_rangeType == 0) ? (Math.abs(inClose[(BodyLongTrailingIdx-2)] - inOpen[(BodyLongTrailingIdx-2)])) : ((BodyLong_rangeType == 1) ? (inHigh[(BodyLongTrailingIdx-2)] - inLow[(BodyLongTrailingIdx-2)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(BodyLongTrailingIdx-2)] - inLow[(BodyLongTrailingIdx-2)]) - Math.abs(inClose[(BodyLongTrailingIdx-2)] - inOpen[(BodyLongTrailingIdx-2)])) : 0.0))));
         i += 1;
         ShadowShortTrailingIdx += 1;
         ShadowLongTrailingIdx += 1;
         NearTrailingIdx += 1;
         FarTrailingIdx += 1;
         BodyLongTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode cdladvanceblockLogic( int startIdx,
                                        int endIdx,
                                        float inOpen[],
                                        float inHigh[],
                                        float inLow[],
                                        float inClose[],
                                        MInteger outBegIdx,
                                        MInteger outNBElement,
                                        int outInteger[] )
   {
      double[] ShadowShortPeriodTotal = new double[3];
      double[] ShadowLongPeriodTotal = new double[2];
      double[] NearPeriodTotal = new double[3];
      double[] FarPeriodTotal = new double[3];
      double BodyLongPeriodTotal = 0;
      int i = 0;
      int outIdx = 0;
      int totIdx = 0;
      int BodyLongTrailingIdx = 0;
      int ShadowShortTrailingIdx = 0;
      int ShadowLongTrailingIdx = 0;
      int NearTrailingIdx = 0;
      int FarTrailingIdx = 0;
      int lookbackTotal = 0;
      int BodyLong_rangeType = this.candleSettings.bodyLong.rangeType;
      int BodyLong_avgPeriod = this.candleSettings.bodyLong.avgPeriod;
      double BodyLong_factor = this.candleSettings.bodyLong.factor;
      int Far_rangeType = this.candleSettings.far.rangeType;
      int Far_avgPeriod = this.candleSettings.far.avgPeriod;
      double Far_factor = this.candleSettings.far.factor;
      int Near_rangeType = this.candleSettings.near.rangeType;
      int Near_avgPeriod = this.candleSettings.near.avgPeriod;
      double Near_factor = this.candleSettings.near.factor;
      int ShadowLong_rangeType = this.candleSettings.shadowLong.rangeType;
      int ShadowLong_avgPeriod = this.candleSettings.shadowLong.avgPeriod;
      double ShadowLong_factor = this.candleSettings.shadowLong.factor;
      int ShadowShort_rangeType = this.candleSettings.shadowShort.rangeType;
      int ShadowShort_avgPeriod = this.candleSettings.shadowShort.avgPeriod;
      double ShadowShort_factor = this.candleSettings.shadowShort.factor;
      lookbackTotal = cdladvanceblockLookback();
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      ShadowShortPeriodTotal[2] = 0;
      ShadowShortPeriodTotal[1] = 0;
      ShadowShortPeriodTotal[0] = 0;
      ShadowShortTrailingIdx = (startIdx-ShadowShort_avgPeriod);
      ShadowLongPeriodTotal[1] = 0;
      ShadowLongPeriodTotal[0] = 0;
      ShadowLongTrailingIdx = (startIdx-ShadowLong_avgPeriod);
      NearPeriodTotal[2] = 0;
      NearPeriodTotal[1] = 0;
      NearPeriodTotal[0] = 0;
      NearTrailingIdx = (startIdx-Near_avgPeriod);
      FarPeriodTotal[2] = 0;
      FarPeriodTotal[1] = 0;
      FarPeriodTotal[0] = 0;
      FarTrailingIdx = (startIdx-Far_avgPeriod);
      BodyLongPeriodTotal = 0;
      BodyLongTrailingIdx = (startIdx-BodyLong_avgPeriod);
      i = ShadowShortTrailingIdx;
      while( (i<startIdx) ) {
         ShadowShortPeriodTotal[2] = (ShadowShortPeriodTotal[2]+((ShadowShort_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((ShadowShort_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((ShadowShort_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0))));
         ShadowShortPeriodTotal[1] = (ShadowShortPeriodTotal[1]+((ShadowShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0))));
         ShadowShortPeriodTotal[0] = (ShadowShortPeriodTotal[0]+((ShadowShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0))));
         i += 1;
      }
      i = ShadowLongTrailingIdx;
      while( (i<startIdx) ) {
         ShadowLongPeriodTotal[1] = (ShadowLongPeriodTotal[1]+((ShadowLong_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowLong_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowLong_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0))));
         ShadowLongPeriodTotal[0] = (ShadowLongPeriodTotal[0]+((ShadowLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0))));
         i += 1;
      }
      i = NearTrailingIdx;
      while( (i<startIdx) ) {
         NearPeriodTotal[2] = (NearPeriodTotal[2]+((Near_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((Near_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((Near_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0))));
         NearPeriodTotal[1] = (NearPeriodTotal[1]+((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0))));
         i += 1;
      }
      i = FarTrailingIdx;
      while( (i<startIdx) ) {
         FarPeriodTotal[2] = (FarPeriodTotal[2]+((Far_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((Far_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((Far_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0))));
         FarPeriodTotal[1] = (FarPeriodTotal[1]+((Far_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Far_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Far_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0))));
         i += 1;
      }
      i = BodyLongTrailingIdx;
      while( (i<startIdx) ) {
         BodyLongPeriodTotal += ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)));
         i += 1;
      }
      i = startIdx;
      outIdx = 0;
      do {
         if( (((((((((((((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==1)&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==1))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==1))&&(inClose[i]>inClose[(i-1)]))&&(inClose[(i-1)]>inClose[(i-2)]))&&(inOpen[(i-1)]>inOpen[(i-2)]))&&(inOpen[(i-1)]<=(inClose[(i-2)]+((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal[2] / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((Near_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((Near_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0)))))))&&(inOpen[i]>inOpen[(i-1)]))&&(inOpen[i]<=(inClose[(i-1)]+((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal[1] / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0)))))))&&(Math.abs((inClose[(i-2)]-inOpen[(i-2)]))>((BodyLong_factor * (((BodyLong_avgPeriod != 0) ? (BodyLongPeriodTotal / BodyLong_avgPeriod) : ((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((BodyLong_rangeType == 2) ? 2.0 : 1.0))))))&&((inHigh[(i-2)]-(((inClose[(i-2)]>=inOpen[(i-2)])) ? (inClose[(i-2)]) : (inOpen[(i-2)])))<((ShadowShort_factor * (((ShadowShort_avgPeriod != 0) ? (ShadowShortPeriodTotal[2] / ShadowShort_avgPeriod) : ((ShadowShort_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((ShadowShort_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((ShadowShort_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((ShadowShort_rangeType == 2) ? 2.0 : 1.0))))))&&(((((Math.abs((inClose[(i-1)]-inOpen[(i-1)]))<(Math.abs((inClose[(i-2)]-inOpen[(i-2)]))-((Far_factor * (((Far_avgPeriod != 0) ? (FarPeriodTotal[2] / Far_avgPeriod) : ((Far_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((Far_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((Far_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))) / ((Far_rangeType == 2) ? 2.0 : 1.0))))))&&(Math.abs((inClose[i]-inOpen[i]))<(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))+((Near_factor * (((Near_avgPeriod != 0) ? (NearPeriodTotal[1] / Near_avgPeriod) : ((Near_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Near_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Near_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Near_rangeType == 2) ? 2.0 : 1.0)))))))||(Math.abs((inClose[i]-inOpen[i]))<(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))-((Far_factor * (((Far_avgPeriod != 0) ? (FarPeriodTotal[1] / Far_avgPeriod) : ((Far_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((Far_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((Far_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((Far_rangeType == 2) ? 2.0 : 1.0)))))))||(((Math.abs((inClose[i]-inOpen[i]))<Math.abs((inClose[(i-1)]-inOpen[(i-1)])))&&(Math.abs((inClose[(i-1)]-inOpen[(i-1)]))<Math.abs((inClose[(i-2)]-inOpen[(i-2)]))))&&(((inHigh[i]-(((inClose[i]>=inOpen[i])) ? (inClose[i]) : (inOpen[i])))>((ShadowShort_factor * (((ShadowShort_avgPeriod != 0) ? (ShadowShortPeriodTotal[0] / ShadowShort_avgPeriod) : ((ShadowShort_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowShort_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowShort_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowShort_rangeType == 2) ? 2.0 : 1.0)))))||((inHigh[(i-1)]-(((inClose[(i-1)]>=inOpen[(i-1)])) ? (inClose[(i-1)]) : (inOpen[(i-1)])))>((ShadowShort_factor * (((ShadowShort_avgPeriod != 0) ? (ShadowShortPeriodTotal[1] / ShadowShort_avgPeriod) : ((ShadowShort_rangeType == 0) ? (Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : ((ShadowShort_rangeType == 1) ? (inHigh[(i-1)] - inLow[(i-1)]) : ((ShadowShort_rangeType == 2) ? ((inHigh[(i-1)] - inLow[(i-1)]) - Math.abs(inClose[(i-1)] - inOpen[(i-1)])) : 0.0)))) / ((ShadowShort_rangeType == 2) ? 2.0 : 1.0))))))))||((Math.abs((inClose[i]-inOpen[i]))<Math.abs((inClose[(i-1)]-inOpen[(i-1)])))&&((inHigh[i]-(((inClose[i]>=inOpen[i])) ? (inClose[i]) : (inOpen[i])))>((ShadowLong_factor * (((ShadowLong_avgPeriod != 0) ? (ShadowLongPeriodTotal[0] / ShadowLong_avgPeriod) : ((ShadowLong_rangeType == 0) ? (Math.abs(inClose[i] - inOpen[i])) : ((ShadowLong_rangeType == 1) ? (inHigh[i] - inLow[i]) : ((ShadowLong_rangeType == 2) ? ((inHigh[i] - inLow[i]) - Math.abs(inClose[i] - inOpen[i])) : 0.0)))) / ((ShadowLong_rangeType == 2) ? 2.0 : 1.0)))))))) ) {
            outInteger[outIdx++] = (0-100);
         } else {
            outInteger[outIdx++] = 0;
         }
         for( totIdx = 2; (totIdx>=0); totIdx -= 1 ) {
            ShadowShortPeriodTotal[totIdx] = (ShadowShortPeriodTotal[totIdx]+(((ShadowShort_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((ShadowShort_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((ShadowShort_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((ShadowShort_rangeType == 0) ? (Math.abs(inClose[(ShadowShortTrailingIdx-totIdx)] - inOpen[(ShadowShortTrailingIdx-totIdx)])) : ((ShadowShort_rangeType == 1) ? (inHigh[(ShadowShortTrailingIdx-totIdx)] - inLow[(ShadowShortTrailingIdx-totIdx)]) : ((ShadowShort_rangeType == 2) ? ((inHigh[(ShadowShortTrailingIdx-totIdx)] - inLow[(ShadowShortTrailingIdx-totIdx)]) - Math.abs(inClose[(ShadowShortTrailingIdx-totIdx)] - inOpen[(ShadowShortTrailingIdx-totIdx)])) : 0.0)))));
         }
         for( totIdx = 1; (totIdx>=0); totIdx -= 1 ) {
            ShadowLongPeriodTotal[totIdx] = (ShadowLongPeriodTotal[totIdx]+(((ShadowLong_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((ShadowLong_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((ShadowLong_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((ShadowLong_rangeType == 0) ? (Math.abs(inClose[(ShadowLongTrailingIdx-totIdx)] - inOpen[(ShadowLongTrailingIdx-totIdx)])) : ((ShadowLong_rangeType == 1) ? (inHigh[(ShadowLongTrailingIdx-totIdx)] - inLow[(ShadowLongTrailingIdx-totIdx)]) : ((ShadowLong_rangeType == 2) ? ((inHigh[(ShadowLongTrailingIdx-totIdx)] - inLow[(ShadowLongTrailingIdx-totIdx)]) - Math.abs(inClose[(ShadowLongTrailingIdx-totIdx)] - inOpen[(ShadowLongTrailingIdx-totIdx)])) : 0.0)))));
         }
         for( totIdx = 2; (totIdx>=1); totIdx -= 1 ) {
            FarPeriodTotal[totIdx] = (FarPeriodTotal[totIdx]+(((Far_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((Far_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((Far_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((Far_rangeType == 0) ? (Math.abs(inClose[(FarTrailingIdx-totIdx)] - inOpen[(FarTrailingIdx-totIdx)])) : ((Far_rangeType == 1) ? (inHigh[(FarTrailingIdx-totIdx)] - inLow[(FarTrailingIdx-totIdx)]) : ((Far_rangeType == 2) ? ((inHigh[(FarTrailingIdx-totIdx)] - inLow[(FarTrailingIdx-totIdx)]) - Math.abs(inClose[(FarTrailingIdx-totIdx)] - inOpen[(FarTrailingIdx-totIdx)])) : 0.0)))));
            NearPeriodTotal[totIdx] = (NearPeriodTotal[totIdx]+(((Near_rangeType == 0) ? (Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : ((Near_rangeType == 1) ? (inHigh[(i-totIdx)] - inLow[(i-totIdx)]) : ((Near_rangeType == 2) ? ((inHigh[(i-totIdx)] - inLow[(i-totIdx)]) - Math.abs(inClose[(i-totIdx)] - inOpen[(i-totIdx)])) : 0.0)))-((Near_rangeType == 0) ? (Math.abs(inClose[(NearTrailingIdx-totIdx)] - inOpen[(NearTrailingIdx-totIdx)])) : ((Near_rangeType == 1) ? (inHigh[(NearTrailingIdx-totIdx)] - inLow[(NearTrailingIdx-totIdx)]) : ((Near_rangeType == 2) ? ((inHigh[(NearTrailingIdx-totIdx)] - inLow[(NearTrailingIdx-totIdx)]) - Math.abs(inClose[(NearTrailingIdx-totIdx)] - inOpen[(NearTrailingIdx-totIdx)])) : 0.0)))));
         }
         BodyLongPeriodTotal += (((BodyLong_rangeType == 0) ? (Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : ((BodyLong_rangeType == 1) ? (inHigh[(i-2)] - inLow[(i-2)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(i-2)] - inLow[(i-2)]) - Math.abs(inClose[(i-2)] - inOpen[(i-2)])) : 0.0)))-((BodyLong_rangeType == 0) ? (Math.abs(inClose[(BodyLongTrailingIdx-2)] - inOpen[(BodyLongTrailingIdx-2)])) : ((BodyLong_rangeType == 1) ? (inHigh[(BodyLongTrailingIdx-2)] - inLow[(BodyLongTrailingIdx-2)]) : ((BodyLong_rangeType == 2) ? ((inHigh[(BodyLongTrailingIdx-2)] - inLow[(BodyLongTrailingIdx-2)]) - Math.abs(inClose[(BodyLongTrailingIdx-2)] - inOpen[(BodyLongTrailingIdx-2)])) : 0.0))));
         i += 1;
         ShadowShortTrailingIdx += 1;
         ShadowLongTrailingIdx += 1;
         NearTrailingIdx += 1;
         FarTrailingIdx += 1;
         BodyLongTrailingIdx += 1;
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
