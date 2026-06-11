/* Generated */
   public int adoscLookback( int optInFastPeriod, int optInSlowPeriod )
   {
      int slowestPeriod;
      if( (optInFastPeriod<optInSlowPeriod) ) {
         slowestPeriod = optInSlowPeriod;
      } else {
         slowestPeriod = optInFastPeriod;
      }
      return emaLookback(slowestPeriod) ;

   }
   public RetCode adosc( int startIdx,
                         int endIdx,
                         double inHigh[],
                         double inLow[],
                         double inClose[],
                         double inVolume[],
                         int optInFastPeriod,
                         int optInSlowPeriod,
                         MInteger outBegIdx,
                         MInteger outNBElement,
                         double outReal[] )
   {
      int today = 0;
      int outIdx = 0;
      int lookbackTotal = 0;
      int slowestPeriod = 0;
      double high = 0;
      double low = 0;
      double close = 0;
      double tmp = 0;
      double slowEMA = 0;
      double slowk = 0;
      double one_minus_slowk = 0;
      double fastEMA = 0;
      double fastk = 0;
      double one_minus_fastk = 0;
      double ad = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      if( (optInFastPeriod<optInSlowPeriod) ) {
         slowestPeriod = optInSlowPeriod;
      } else {
         slowestPeriod = optInFastPeriod;
      }
      lookbackTotal = emaLookback(slowestPeriod);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outBegIdx.value = startIdx;
      today = (startIdx-lookbackTotal);
      ad = 0.0;
      fastk = (2.0/(((double)optInFastPeriod)+1.0));
      one_minus_fastk = (1.0-fastk);
      slowk = (2.0/(((double)optInSlowPeriod)+1.0));
      one_minus_slowk = (1.0-slowk);
      high = inHigh[today];
      low = inLow[today];
      tmp = (high-low);
      close = inClose[today];
      if( (tmp>0.0) ) {
         ad += ((((close-low)-(high-close))/tmp)*((double)inVolume[today]));
      }
      today += 1;
      fastEMA = ad;
      slowEMA = ad;
      while( (today<startIdx) ) {
         high = inHigh[today];
         low = inLow[today];
         tmp = (high-low);
         close = inClose[today];
         if( (tmp>0.0) ) {
            ad += ((((close-low)-(high-close))/tmp)*((double)inVolume[today]));
         }
         today += 1;
         fastEMA = ((fastk*ad)+(one_minus_fastk*fastEMA));
         slowEMA = ((slowk*ad)+(one_minus_slowk*slowEMA));
      }
      outIdx = 0;
      while( (today<=endIdx) ) {
         high = inHigh[today];
         low = inLow[today];
         tmp = (high-low);
         close = inClose[today];
         if( (tmp>0.0) ) {
            ad += ((((close-low)-(high-close))/tmp)*((double)inVolume[today]));
         }
         today += 1;
         fastEMA = ((fastk*ad)+(one_minus_fastk*fastEMA));
         slowEMA = ((slowk*ad)+(one_minus_slowk*slowEMA));
         outReal[outIdx++] = (fastEMA-slowEMA);
      }
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode adoscLogic( int startIdx,
                              int endIdx,
                              double inHigh[],
                              double inLow[],
                              double inClose[],
                              double inVolume[],
                              int optInFastPeriod,
                              int optInSlowPeriod,
                              MInteger outBegIdx,
                              MInteger outNBElement,
                              double outReal[] )
   {
      int today = 0;
      int outIdx = 0;
      int lookbackTotal = 0;
      int slowestPeriod = 0;
      double high = 0;
      double low = 0;
      double close = 0;
      double tmp = 0;
      double slowEMA = 0;
      double slowk = 0;
      double one_minus_slowk = 0;
      double fastEMA = 0;
      double fastk = 0;
      double one_minus_fastk = 0;
      double ad = 0;
      if( (optInFastPeriod<optInSlowPeriod) ) {
         slowestPeriod = optInSlowPeriod;
      } else {
         slowestPeriod = optInFastPeriod;
      }
      lookbackTotal = emaLookback(slowestPeriod);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outBegIdx.value = startIdx;
      today = (startIdx-lookbackTotal);
      ad = 0.0;
      fastk = (2.0/(((double)optInFastPeriod)+1.0));
      one_minus_fastk = (1.0-fastk);
      slowk = (2.0/(((double)optInSlowPeriod)+1.0));
      one_minus_slowk = (1.0-slowk);
      high = inHigh[today];
      low = inLow[today];
      tmp = (high-low);
      close = inClose[today];
      if( (tmp>0.0) ) {
         ad += ((((close-low)-(high-close))/tmp)*((double)inVolume[today]));
      }
      today += 1;
      fastEMA = ad;
      slowEMA = ad;
      while( (today<startIdx) ) {
         high = inHigh[today];
         low = inLow[today];
         tmp = (high-low);
         close = inClose[today];
         if( (tmp>0.0) ) {
            ad += ((((close-low)-(high-close))/tmp)*((double)inVolume[today]));
         }
         today += 1;
         fastEMA = ((fastk*ad)+(one_minus_fastk*fastEMA));
         slowEMA = ((slowk*ad)+(one_minus_slowk*slowEMA));
      }
      outIdx = 0;
      while( (today<=endIdx) ) {
         high = inHigh[today];
         low = inLow[today];
         tmp = (high-low);
         close = inClose[today];
         if( (tmp>0.0) ) {
            ad += ((((close-low)-(high-close))/tmp)*((double)inVolume[today]));
         }
         today += 1;
         fastEMA = ((fastk*ad)+(one_minus_fastk*fastEMA));
         slowEMA = ((slowk*ad)+(one_minus_slowk*slowEMA));
         outReal[outIdx++] = (fastEMA-slowEMA);
      }
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode adosc( int startIdx,
                         int endIdx,
                         float inHigh[],
                         float inLow[],
                         float inClose[],
                         float inVolume[],
                         int optInFastPeriod,
                         int optInSlowPeriod,
                         MInteger outBegIdx,
                         MInteger outNBElement,
                         double outReal[] )
   {
      int today = 0;
      int outIdx = 0;
      int lookbackTotal = 0;
      int slowestPeriod = 0;
      double high = 0;
      double low = 0;
      double close = 0;
      double tmp = 0;
      double slowEMA = 0;
      double slowk = 0;
      double one_minus_slowk = 0;
      double fastEMA = 0;
      double fastk = 0;
      double one_minus_fastk = 0;
      double ad = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      if( (optInFastPeriod<optInSlowPeriod) ) {
         slowestPeriod = optInSlowPeriod;
      } else {
         slowestPeriod = optInFastPeriod;
      }
      lookbackTotal = emaLookback(slowestPeriod);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outBegIdx.value = startIdx;
      today = (startIdx-lookbackTotal);
      ad = 0.0;
      fastk = (2.0/(((double)optInFastPeriod)+1.0));
      one_minus_fastk = (1.0-fastk);
      slowk = (2.0/(((double)optInSlowPeriod)+1.0));
      one_minus_slowk = (1.0-slowk);
      high = inHigh[today];
      low = inLow[today];
      tmp = (high-low);
      close = inClose[today];
      if( (tmp>0.0) ) {
         ad += ((((close-low)-(high-close))/tmp)*((double)inVolume[today]));
      }
      today += 1;
      fastEMA = ad;
      slowEMA = ad;
      while( (today<startIdx) ) {
         high = inHigh[today];
         low = inLow[today];
         tmp = (high-low);
         close = inClose[today];
         if( (tmp>0.0) ) {
            ad += ((((close-low)-(high-close))/tmp)*((double)inVolume[today]));
         }
         today += 1;
         fastEMA = ((fastk*ad)+(one_minus_fastk*fastEMA));
         slowEMA = ((slowk*ad)+(one_minus_slowk*slowEMA));
      }
      outIdx = 0;
      while( (today<=endIdx) ) {
         high = inHigh[today];
         low = inLow[today];
         tmp = (high-low);
         close = inClose[today];
         if( (tmp>0.0) ) {
            ad += ((((close-low)-(high-close))/tmp)*((double)inVolume[today]));
         }
         today += 1;
         fastEMA = ((fastk*ad)+(one_minus_fastk*fastEMA));
         slowEMA = ((slowk*ad)+(one_minus_slowk*slowEMA));
         outReal[outIdx++] = (fastEMA-slowEMA);
      }
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode adoscLogic( int startIdx,
                              int endIdx,
                              float inHigh[],
                              float inLow[],
                              float inClose[],
                              float inVolume[],
                              int optInFastPeriod,
                              int optInSlowPeriod,
                              MInteger outBegIdx,
                              MInteger outNBElement,
                              double outReal[] )
   {
      int today = 0;
      int outIdx = 0;
      int lookbackTotal = 0;
      int slowestPeriod = 0;
      double high = 0;
      double low = 0;
      double close = 0;
      double tmp = 0;
      double slowEMA = 0;
      double slowk = 0;
      double one_minus_slowk = 0;
      double fastEMA = 0;
      double fastk = 0;
      double one_minus_fastk = 0;
      double ad = 0;
      if( (optInFastPeriod<optInSlowPeriod) ) {
         slowestPeriod = optInSlowPeriod;
      } else {
         slowestPeriod = optInFastPeriod;
      }
      lookbackTotal = emaLookback(slowestPeriod);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outBegIdx.value = startIdx;
      today = (startIdx-lookbackTotal);
      ad = 0.0;
      fastk = (2.0/(((double)optInFastPeriod)+1.0));
      one_minus_fastk = (1.0-fastk);
      slowk = (2.0/(((double)optInSlowPeriod)+1.0));
      one_minus_slowk = (1.0-slowk);
      high = inHigh[today];
      low = inLow[today];
      tmp = (high-low);
      close = inClose[today];
      if( (tmp>0.0) ) {
         ad += ((((close-low)-(high-close))/tmp)*((double)inVolume[today]));
      }
      today += 1;
      fastEMA = ad;
      slowEMA = ad;
      while( (today<startIdx) ) {
         high = inHigh[today];
         low = inLow[today];
         tmp = (high-low);
         close = inClose[today];
         if( (tmp>0.0) ) {
            ad += ((((close-low)-(high-close))/tmp)*((double)inVolume[today]));
         }
         today += 1;
         fastEMA = ((fastk*ad)+(one_minus_fastk*fastEMA));
         slowEMA = ((slowk*ad)+(one_minus_slowk*slowEMA));
      }
      outIdx = 0;
      while( (today<=endIdx) ) {
         high = inHigh[today];
         low = inLow[today];
         tmp = (high-low);
         close = inClose[today];
         if( (tmp>0.0) ) {
            ad += ((((close-low)-(high-close))/tmp)*((double)inVolume[today]));
         }
         today += 1;
         fastEMA = ((fastk*ad)+(one_minus_fastk*fastEMA));
         slowEMA = ((slowk*ad)+(one_minus_slowk*slowEMA));
         outReal[outIdx++] = (fastEMA-slowEMA);
      }
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
