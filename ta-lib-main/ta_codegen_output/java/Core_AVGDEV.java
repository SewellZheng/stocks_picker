/* Generated */
   public int avgdevLookback( int optInTimePeriod )
   {
      return (optInTimePeriod-1) ;

   }
   public RetCode avgdev( int startIdx,
                          int endIdx,
                          double inReal[],
                          int optInTimePeriod,
                          MInteger outBegIdx,
                          MInteger outNBElement,
                          double outReal[] )
   {
      int today = 0;
      int outIdx = 0;
      int lookback = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookback = (optInTimePeriod-1);
      if( (startIdx<lookback) ) {
         startIdx = lookback;
      }
      today = startIdx;
      if( (today>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outBegIdx.value = today;
      outIdx = 0;
      while( (today<=endIdx) ) {
         double todaySum;
         double todayDev;
         int i;
         todaySum = 0.0;
         for( i = 0; (i<optInTimePeriod); i += 1 ) {
            todaySum += inReal[(today-i)];
         }
         todayDev = 0.0;
         for( i = 0; (i<optInTimePeriod); i += 1 ) {
            todayDev += Math.abs((inReal[(today-i)]-(todaySum/optInTimePeriod)));
         }
         outReal[outIdx] = (todayDev/optInTimePeriod);
         outIdx += 1;
         today += 1;
      }
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode avgdevLogic( int startIdx,
                               int endIdx,
                               double inReal[],
                               int optInTimePeriod,
                               MInteger outBegIdx,
                               MInteger outNBElement,
                               double outReal[] )
   {
      int today = 0;
      int outIdx = 0;
      int lookback = 0;
      lookback = (optInTimePeriod-1);
      if( (startIdx<lookback) ) {
         startIdx = lookback;
      }
      today = startIdx;
      if( (today>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outBegIdx.value = today;
      outIdx = 0;
      while( (today<=endIdx) ) {
         double todaySum;
         double todayDev;
         int i;
         todaySum = 0.0;
         for( i = 0; (i<optInTimePeriod); i += 1 ) {
            todaySum += inReal[(today-i)];
         }
         todayDev = 0.0;
         for( i = 0; (i<optInTimePeriod); i += 1 ) {
            todayDev += Math.abs((inReal[(today-i)]-(todaySum/optInTimePeriod)));
         }
         outReal[outIdx] = (todayDev/optInTimePeriod);
         outIdx += 1;
         today += 1;
      }
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode avgdev( int startIdx,
                          int endIdx,
                          float inReal[],
                          int optInTimePeriod,
                          MInteger outBegIdx,
                          MInteger outNBElement,
                          double outReal[] )
   {
      int today = 0;
      int outIdx = 0;
      int lookback = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookback = (optInTimePeriod-1);
      if( (startIdx<lookback) ) {
         startIdx = lookback;
      }
      today = startIdx;
      if( (today>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outBegIdx.value = today;
      outIdx = 0;
      while( (today<=endIdx) ) {
         double todaySum;
         double todayDev;
         int i;
         todaySum = 0.0;
         for( i = 0; (i<optInTimePeriod); i += 1 ) {
            todaySum += inReal[(today-i)];
         }
         todayDev = 0.0;
         for( i = 0; (i<optInTimePeriod); i += 1 ) {
            todayDev += Math.abs((inReal[(today-i)]-(todaySum/optInTimePeriod)));
         }
         outReal[outIdx] = (todayDev/optInTimePeriod);
         outIdx += 1;
         today += 1;
      }
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode avgdevLogic( int startIdx,
                               int endIdx,
                               float inReal[],
                               int optInTimePeriod,
                               MInteger outBegIdx,
                               MInteger outNBElement,
                               double outReal[] )
   {
      int today = 0;
      int outIdx = 0;
      int lookback = 0;
      lookback = (optInTimePeriod-1);
      if( (startIdx<lookback) ) {
         startIdx = lookback;
      }
      today = startIdx;
      if( (today>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outBegIdx.value = today;
      outIdx = 0;
      while( (today<=endIdx) ) {
         double todaySum;
         double todayDev;
         int i;
         todaySum = 0.0;
         for( i = 0; (i<optInTimePeriod); i += 1 ) {
            todaySum += inReal[(today-i)];
         }
         todayDev = 0.0;
         for( i = 0; (i<optInTimePeriod); i += 1 ) {
            todayDev += Math.abs((inReal[(today-i)]-(todaySum/optInTimePeriod)));
         }
         outReal[outIdx] = (todayDev/optInTimePeriod);
         outIdx += 1;
         today += 1;
      }
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
