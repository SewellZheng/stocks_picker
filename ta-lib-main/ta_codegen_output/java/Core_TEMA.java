/* Generated */
   public int temaLookback( int optInTimePeriod )
   {
      int retValue;
      retValue = emaLookback(optInTimePeriod);
      return (retValue*3) ;

   }
   public RetCode tema( int startIdx,
                        int endIdx,
                        double inReal[],
                        int optInTimePeriod,
                        MInteger outBegIdx,
                        MInteger outNBElement,
                        double outReal[] )
   {
      double[] firstEMA;
      double[] secondEMA;
      MInteger firstEMABegIdx = new MInteger();
      MInteger firstEMANbElement = new MInteger();
      MInteger secondEMABegIdx = new MInteger();
      MInteger secondEMANbElement = new MInteger();
      MInteger thirdEMABegIdx = new MInteger();
      MInteger thirdEMANbElement = new MInteger();
      int tempInt = 0;
      int outIdx = 0;
      int lookbackTotal = 0;
      int lookbackEMA = 0;
      int firstEMAIdx = 0;
      int secondEMAIdx = 0;
      RetCode retCode;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      outNBElement.value = 0;
      outBegIdx.value = 0;
      lookbackEMA = emaLookback(optInTimePeriod);
      lookbackTotal = (lookbackEMA*3);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         return RetCode.Success ;
      }
      tempInt = ((lookbackTotal+(endIdx-startIdx))+1);
      firstEMA = new double[(int)((tempInt*1))];
      retCode = emaLogic((startIdx-(lookbackEMA*2)), endIdx, inReal, optInTimePeriod, firstEMABegIdx, firstEMANbElement, firstEMA);
      if( ((retCode!=RetCode.Success)||(firstEMANbElement.value==0)) ) {
         return retCode ;
      }
      secondEMA = new double[(int)((firstEMANbElement.value*1))];
      retCode = emaLogic(0, (firstEMANbElement.value-1), firstEMA, optInTimePeriod, secondEMABegIdx, secondEMANbElement, secondEMA);
      if( ((retCode!=RetCode.Success)||(secondEMANbElement.value==0)) ) {
         return retCode ;
      }
      retCode = emaLogic(0, (secondEMANbElement.value-1), secondEMA, optInTimePeriod, thirdEMABegIdx, thirdEMANbElement, outReal);
      if( ((retCode!=RetCode.Success)||(thirdEMANbElement.value==0)) ) {
         return retCode ;
      }
      firstEMAIdx = (thirdEMABegIdx.value+secondEMABegIdx.value);
      secondEMAIdx = thirdEMABegIdx.value;
      outBegIdx.value = (firstEMAIdx+firstEMABegIdx.value);
      outIdx = 0;
      while( (outIdx<thirdEMANbElement.value) ) {
         outReal[outIdx] = (outReal[outIdx]+((3.0*firstEMA[firstEMAIdx++])-(3.0*secondEMA[secondEMAIdx++])));
         outIdx += 1;
      }
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode temaLogic( int startIdx,
                             int endIdx,
                             double inReal[],
                             int optInTimePeriod,
                             MInteger outBegIdx,
                             MInteger outNBElement,
                             double outReal[] )
   {
      double[] firstEMA;
      double[] secondEMA;
      MInteger firstEMABegIdx = new MInteger();
      MInteger firstEMANbElement = new MInteger();
      MInteger secondEMABegIdx = new MInteger();
      MInteger secondEMANbElement = new MInteger();
      MInteger thirdEMABegIdx = new MInteger();
      MInteger thirdEMANbElement = new MInteger();
      int tempInt = 0;
      int outIdx = 0;
      int lookbackTotal = 0;
      int lookbackEMA = 0;
      int firstEMAIdx = 0;
      int secondEMAIdx = 0;
      RetCode retCode;
      outNBElement.value = 0;
      outBegIdx.value = 0;
      lookbackEMA = emaLookback(optInTimePeriod);
      lookbackTotal = (lookbackEMA*3);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         return RetCode.Success ;
      }
      tempInt = ((lookbackTotal+(endIdx-startIdx))+1);
      firstEMA = new double[(int)((tempInt*1))];
      retCode = emaLogic((startIdx-(lookbackEMA*2)), endIdx, inReal, optInTimePeriod, firstEMABegIdx, firstEMANbElement, firstEMA);
      if( ((retCode!=RetCode.Success)||(firstEMANbElement.value==0)) ) {
         return retCode ;
      }
      secondEMA = new double[(int)((firstEMANbElement.value*1))];
      retCode = emaLogic(0, (firstEMANbElement.value-1), firstEMA, optInTimePeriod, secondEMABegIdx, secondEMANbElement, secondEMA);
      if( ((retCode!=RetCode.Success)||(secondEMANbElement.value==0)) ) {
         return retCode ;
      }
      retCode = emaLogic(0, (secondEMANbElement.value-1), secondEMA, optInTimePeriod, thirdEMABegIdx, thirdEMANbElement, outReal);
      if( ((retCode!=RetCode.Success)||(thirdEMANbElement.value==0)) ) {
         return retCode ;
      }
      firstEMAIdx = (thirdEMABegIdx.value+secondEMABegIdx.value);
      secondEMAIdx = thirdEMABegIdx.value;
      outBegIdx.value = (firstEMAIdx+firstEMABegIdx.value);
      outIdx = 0;
      while( (outIdx<thirdEMANbElement.value) ) {
         outReal[outIdx] = (outReal[outIdx]+((3.0*firstEMA[firstEMAIdx++])-(3.0*secondEMA[secondEMAIdx++])));
         outIdx += 1;
      }
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode tema( int startIdx,
                        int endIdx,
                        float inReal[],
                        int optInTimePeriod,
                        MInteger outBegIdx,
                        MInteger outNBElement,
                        double outReal[] )
   {
      double[] firstEMA;
      double[] secondEMA;
      MInteger firstEMABegIdx = new MInteger();
      MInteger firstEMANbElement = new MInteger();
      MInteger secondEMABegIdx = new MInteger();
      MInteger secondEMANbElement = new MInteger();
      MInteger thirdEMABegIdx = new MInteger();
      MInteger thirdEMANbElement = new MInteger();
      int tempInt = 0;
      int outIdx = 0;
      int lookbackTotal = 0;
      int lookbackEMA = 0;
      int firstEMAIdx = 0;
      int secondEMAIdx = 0;
      RetCode retCode;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      outNBElement.value = 0;
      outBegIdx.value = 0;
      lookbackEMA = emaLookback(optInTimePeriod);
      lookbackTotal = (lookbackEMA*3);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         return RetCode.Success ;
      }
      tempInt = ((lookbackTotal+(endIdx-startIdx))+1);
      firstEMA = new double[(int)((tempInt*1))];
      retCode = emaLogic((startIdx-(lookbackEMA*2)), endIdx, inReal, optInTimePeriod, firstEMABegIdx, firstEMANbElement, firstEMA);
      if( ((retCode!=RetCode.Success)||(firstEMANbElement.value==0)) ) {
         return retCode ;
      }
      secondEMA = new double[(int)((firstEMANbElement.value*1))];
      retCode = emaLogic(0, (firstEMANbElement.value-1), firstEMA, optInTimePeriod, secondEMABegIdx, secondEMANbElement, secondEMA);
      if( ((retCode!=RetCode.Success)||(secondEMANbElement.value==0)) ) {
         return retCode ;
      }
      retCode = emaLogic(0, (secondEMANbElement.value-1), secondEMA, optInTimePeriod, thirdEMABegIdx, thirdEMANbElement, outReal);
      if( ((retCode!=RetCode.Success)||(thirdEMANbElement.value==0)) ) {
         return retCode ;
      }
      firstEMAIdx = (thirdEMABegIdx.value+secondEMABegIdx.value);
      secondEMAIdx = thirdEMABegIdx.value;
      outBegIdx.value = (firstEMAIdx+firstEMABegIdx.value);
      outIdx = 0;
      while( (outIdx<thirdEMANbElement.value) ) {
         outReal[outIdx] = (outReal[outIdx]+((3.0*firstEMA[firstEMAIdx++])-(3.0*secondEMA[secondEMAIdx++])));
         outIdx += 1;
      }
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode temaLogic( int startIdx,
                             int endIdx,
                             float inReal[],
                             int optInTimePeriod,
                             MInteger outBegIdx,
                             MInteger outNBElement,
                             double outReal[] )
   {
      double[] firstEMA;
      double[] secondEMA;
      MInteger firstEMABegIdx = new MInteger();
      MInteger firstEMANbElement = new MInteger();
      MInteger secondEMABegIdx = new MInteger();
      MInteger secondEMANbElement = new MInteger();
      MInteger thirdEMABegIdx = new MInteger();
      MInteger thirdEMANbElement = new MInteger();
      int tempInt = 0;
      int outIdx = 0;
      int lookbackTotal = 0;
      int lookbackEMA = 0;
      int firstEMAIdx = 0;
      int secondEMAIdx = 0;
      RetCode retCode;
      outNBElement.value = 0;
      outBegIdx.value = 0;
      lookbackEMA = emaLookback(optInTimePeriod);
      lookbackTotal = (lookbackEMA*3);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         return RetCode.Success ;
      }
      tempInt = ((lookbackTotal+(endIdx-startIdx))+1);
      firstEMA = new double[(int)((tempInt*1))];
      retCode = emaLogic((startIdx-(lookbackEMA*2)), endIdx, inReal, optInTimePeriod, firstEMABegIdx, firstEMANbElement, firstEMA);
      if( ((retCode!=RetCode.Success)||(firstEMANbElement.value==0)) ) {
         return retCode ;
      }
      secondEMA = new double[(int)((firstEMANbElement.value*1))];
      retCode = emaLogic(0, (firstEMANbElement.value-1), firstEMA, optInTimePeriod, secondEMABegIdx, secondEMANbElement, secondEMA);
      if( ((retCode!=RetCode.Success)||(secondEMANbElement.value==0)) ) {
         return retCode ;
      }
      retCode = emaLogic(0, (secondEMANbElement.value-1), secondEMA, optInTimePeriod, thirdEMABegIdx, thirdEMANbElement, outReal);
      if( ((retCode!=RetCode.Success)||(thirdEMANbElement.value==0)) ) {
         return retCode ;
      }
      firstEMAIdx = (thirdEMABegIdx.value+secondEMABegIdx.value);
      secondEMAIdx = thirdEMABegIdx.value;
      outBegIdx.value = (firstEMAIdx+firstEMABegIdx.value);
      outIdx = 0;
      while( (outIdx<thirdEMANbElement.value) ) {
         outReal[outIdx] = (outReal[outIdx]+((3.0*firstEMA[firstEMAIdx++])-(3.0*secondEMA[secondEMAIdx++])));
         outIdx += 1;
      }
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
