/* Generated */
   public int linearregLookback( int optInTimePeriod )
   {
      return (optInTimePeriod-1) ;

   }
   public RetCode linearreg( int startIdx,
                             int endIdx,
                             double inReal[],
                             int optInTimePeriod,
                             MInteger outBegIdx,
                             MInteger outNBElement,
                             double outReal[] )
   {
      int outIdx = 0;
      int today = 0;
      int lookbackTotal = 0;
      double SumX = 0;
      double SumXY = 0;
      double SumY = 0;
      double SumXSqr = 0;
      double Divisor = 0;
      double m = 0;
      double b = 0;
      int i = 0;
      double tempValue1 = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackTotal = linearregLookback(optInTimePeriod);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outIdx = 0;
      today = startIdx;
      SumX = ((optInTimePeriod*(optInTimePeriod-1))*0.5);
      SumXSqr = (((optInTimePeriod*(optInTimePeriod-1))*((2*optInTimePeriod)-1))/6);
      Divisor = ((SumX*SumX)-(optInTimePeriod*SumXSqr));
      while( (today<=endIdx) ) {
         SumXY = 0;
         SumY = 0;
         for( i = optInTimePeriod; (i--!=0);  ) {
            tempValue1 = inReal[(today-i)];
            SumY += tempValue1;
            SumXY += (((double)i)*tempValue1);
         }
         m = (((optInTimePeriod*SumXY)-(SumX*SumY))/Divisor);
         b = ((SumY-(m*SumX))/((double)optInTimePeriod));
         outReal[outIdx++] = (b+(m*((double)(optInTimePeriod-1))));
         today += 1;
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode linearregLogic( int startIdx,
                                  int endIdx,
                                  double inReal[],
                                  int optInTimePeriod,
                                  MInteger outBegIdx,
                                  MInteger outNBElement,
                                  double outReal[] )
   {
      int outIdx = 0;
      int today = 0;
      int lookbackTotal = 0;
      double SumX = 0;
      double SumXY = 0;
      double SumY = 0;
      double SumXSqr = 0;
      double Divisor = 0;
      double m = 0;
      double b = 0;
      int i = 0;
      double tempValue1 = 0;
      lookbackTotal = linearregLookback(optInTimePeriod);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outIdx = 0;
      today = startIdx;
      SumX = ((optInTimePeriod*(optInTimePeriod-1))*0.5);
      SumXSqr = (((optInTimePeriod*(optInTimePeriod-1))*((2*optInTimePeriod)-1))/6);
      Divisor = ((SumX*SumX)-(optInTimePeriod*SumXSqr));
      while( (today<=endIdx) ) {
         SumXY = 0;
         SumY = 0;
         for( i = optInTimePeriod; (i--!=0);  ) {
            tempValue1 = inReal[(today-i)];
            SumY += tempValue1;
            SumXY += (((double)i)*tempValue1);
         }
         m = (((optInTimePeriod*SumXY)-(SumX*SumY))/Divisor);
         b = ((SumY-(m*SumX))/((double)optInTimePeriod));
         outReal[outIdx++] = (b+(m*((double)(optInTimePeriod-1))));
         today += 1;
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode linearreg( int startIdx,
                             int endIdx,
                             float inReal[],
                             int optInTimePeriod,
                             MInteger outBegIdx,
                             MInteger outNBElement,
                             double outReal[] )
   {
      int outIdx = 0;
      int today = 0;
      int lookbackTotal = 0;
      double SumX = 0;
      double SumXY = 0;
      double SumY = 0;
      double SumXSqr = 0;
      double Divisor = 0;
      double m = 0;
      double b = 0;
      int i = 0;
      double tempValue1 = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackTotal = linearregLookback(optInTimePeriod);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outIdx = 0;
      today = startIdx;
      SumX = ((optInTimePeriod*(optInTimePeriod-1))*0.5);
      SumXSqr = (((optInTimePeriod*(optInTimePeriod-1))*((2*optInTimePeriod)-1))/6);
      Divisor = ((SumX*SumX)-(optInTimePeriod*SumXSqr));
      while( (today<=endIdx) ) {
         SumXY = 0;
         SumY = 0;
         for( i = optInTimePeriod; (i--!=0);  ) {
            tempValue1 = inReal[(today-i)];
            SumY += tempValue1;
            SumXY += (((double)i)*tempValue1);
         }
         m = (((optInTimePeriod*SumXY)-(SumX*SumY))/Divisor);
         b = ((SumY-(m*SumX))/((double)optInTimePeriod));
         outReal[outIdx++] = (b+(m*((double)(optInTimePeriod-1))));
         today += 1;
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode linearregLogic( int startIdx,
                                  int endIdx,
                                  float inReal[],
                                  int optInTimePeriod,
                                  MInteger outBegIdx,
                                  MInteger outNBElement,
                                  double outReal[] )
   {
      int outIdx = 0;
      int today = 0;
      int lookbackTotal = 0;
      double SumX = 0;
      double SumXY = 0;
      double SumY = 0;
      double SumXSqr = 0;
      double Divisor = 0;
      double m = 0;
      double b = 0;
      int i = 0;
      double tempValue1 = 0;
      lookbackTotal = linearregLookback(optInTimePeriod);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outIdx = 0;
      today = startIdx;
      SumX = ((optInTimePeriod*(optInTimePeriod-1))*0.5);
      SumXSqr = (((optInTimePeriod*(optInTimePeriod-1))*((2*optInTimePeriod)-1))/6);
      Divisor = ((SumX*SumX)-(optInTimePeriod*SumXSqr));
      while( (today<=endIdx) ) {
         SumXY = 0;
         SumY = 0;
         for( i = optInTimePeriod; (i--!=0);  ) {
            tempValue1 = inReal[(today-i)];
            SumY += tempValue1;
            SumXY += (((double)i)*tempValue1);
         }
         m = (((optInTimePeriod*SumXY)-(SumX*SumY))/Divisor);
         b = ((SumY-(m*SumX))/((double)optInTimePeriod));
         outReal[outIdx++] = (b+(m*((double)(optInTimePeriod-1))));
         today += 1;
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
