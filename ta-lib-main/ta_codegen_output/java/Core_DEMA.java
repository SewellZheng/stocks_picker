/* Generated */
   public int demaLookback( int optInTimePeriod )
   {
      return (emaLookback(optInTimePeriod)*2) ;

   }
   public RetCode dema( int startIdx,
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
      int tempInt = 0;
      int outIdx = 0;
      int firstEMAIdx = 0;
      int lookbackTotal = 0;
      int lookbackEMA = 0;
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
      lookbackTotal = (lookbackEMA*2);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         return RetCode.Success ;
      }
      if( (inReal==outReal) ) {
         firstEMA = outReal;
      } else {
         tempInt = ((lookbackTotal+(endIdx-startIdx))+1);
         firstEMA = new double[(int)((tempInt*1))];
      }
      retCode = emaLogic((startIdx-lookbackEMA), endIdx, inReal, optInTimePeriod, firstEMABegIdx, firstEMANbElement, firstEMA);
      if( ((retCode!=RetCode.Success)||(firstEMANbElement.value==0)) ) {
         if( (firstEMA!=outReal) ) {
         }
         return retCode ;
      }
      secondEMA = new double[(int)((firstEMANbElement.value*1))];
      retCode = emaLogic(0, (firstEMANbElement.value-1), firstEMA, optInTimePeriod, secondEMABegIdx, secondEMANbElement, secondEMA);
      if( ((retCode!=RetCode.Success)||(secondEMANbElement.value==0)) ) {
         if( (firstEMA!=outReal) ) {
         }
         return retCode ;
      }
      firstEMAIdx = secondEMABegIdx.value;
      outIdx = 0;
      while( (outIdx<secondEMANbElement.value) ) {
         outReal[outIdx] = ((2.0*firstEMA[firstEMAIdx++])-secondEMA[outIdx]);
         outIdx += 1;
      }
      if( (firstEMA!=outReal) ) {
      }
      outBegIdx.value = (firstEMABegIdx.value+secondEMABegIdx.value);
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode demaLogic( int startIdx,
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
      int tempInt = 0;
      int outIdx = 0;
      int firstEMAIdx = 0;
      int lookbackTotal = 0;
      int lookbackEMA = 0;
      RetCode retCode;
      outNBElement.value = 0;
      outBegIdx.value = 0;
      lookbackEMA = emaLookback(optInTimePeriod);
      lookbackTotal = (lookbackEMA*2);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         return RetCode.Success ;
      }
      if( (inReal==outReal) ) {
         firstEMA = outReal;
      } else {
         tempInt = ((lookbackTotal+(endIdx-startIdx))+1);
         firstEMA = new double[(int)((tempInt*1))];
      }
      retCode = emaLogic((startIdx-lookbackEMA), endIdx, inReal, optInTimePeriod, firstEMABegIdx, firstEMANbElement, firstEMA);
      if( ((retCode!=RetCode.Success)||(firstEMANbElement.value==0)) ) {
         if( (firstEMA!=outReal) ) {
         }
         return retCode ;
      }
      secondEMA = new double[(int)((firstEMANbElement.value*1))];
      retCode = emaLogic(0, (firstEMANbElement.value-1), firstEMA, optInTimePeriod, secondEMABegIdx, secondEMANbElement, secondEMA);
      if( ((retCode!=RetCode.Success)||(secondEMANbElement.value==0)) ) {
         if( (firstEMA!=outReal) ) {
         }
         return retCode ;
      }
      firstEMAIdx = secondEMABegIdx.value;
      outIdx = 0;
      while( (outIdx<secondEMANbElement.value) ) {
         outReal[outIdx] = ((2.0*firstEMA[firstEMAIdx++])-secondEMA[outIdx]);
         outIdx += 1;
      }
      if( (firstEMA!=outReal) ) {
      }
      outBegIdx.value = (firstEMABegIdx.value+secondEMABegIdx.value);
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode dema( int startIdx,
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
      int tempInt = 0;
      int outIdx = 0;
      int firstEMAIdx = 0;
      int lookbackTotal = 0;
      int lookbackEMA = 0;
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
      lookbackTotal = (lookbackEMA*2);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         return RetCode.Success ;
      }
      if( false ) {
         firstEMA = outReal;
      } else {
         tempInt = ((lookbackTotal+(endIdx-startIdx))+1);
         firstEMA = new double[(int)((tempInt*1))];
      }
      retCode = emaLogic((startIdx-lookbackEMA), endIdx, inReal, optInTimePeriod, firstEMABegIdx, firstEMANbElement, firstEMA);
      if( ((retCode!=RetCode.Success)||(firstEMANbElement.value==0)) ) {
         if( (firstEMA!=outReal) ) {
         }
         return retCode ;
      }
      secondEMA = new double[(int)((firstEMANbElement.value*1))];
      retCode = emaLogic(0, (firstEMANbElement.value-1), firstEMA, optInTimePeriod, secondEMABegIdx, secondEMANbElement, secondEMA);
      if( ((retCode!=RetCode.Success)||(secondEMANbElement.value==0)) ) {
         if( (firstEMA!=outReal) ) {
         }
         return retCode ;
      }
      firstEMAIdx = secondEMABegIdx.value;
      outIdx = 0;
      while( (outIdx<secondEMANbElement.value) ) {
         outReal[outIdx] = ((2.0*firstEMA[firstEMAIdx++])-secondEMA[outIdx]);
         outIdx += 1;
      }
      if( (firstEMA!=outReal) ) {
      }
      outBegIdx.value = (firstEMABegIdx.value+secondEMABegIdx.value);
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode demaLogic( int startIdx,
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
      int tempInt = 0;
      int outIdx = 0;
      int firstEMAIdx = 0;
      int lookbackTotal = 0;
      int lookbackEMA = 0;
      RetCode retCode;
      outNBElement.value = 0;
      outBegIdx.value = 0;
      lookbackEMA = emaLookback(optInTimePeriod);
      lookbackTotal = (lookbackEMA*2);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         return RetCode.Success ;
      }
      if( false ) {
         firstEMA = outReal;
      } else {
         tempInt = ((lookbackTotal+(endIdx-startIdx))+1);
         firstEMA = new double[(int)((tempInt*1))];
      }
      retCode = emaLogic((startIdx-lookbackEMA), endIdx, inReal, optInTimePeriod, firstEMABegIdx, firstEMANbElement, firstEMA);
      if( ((retCode!=RetCode.Success)||(firstEMANbElement.value==0)) ) {
         if( (firstEMA!=outReal) ) {
         }
         return retCode ;
      }
      secondEMA = new double[(int)((firstEMANbElement.value*1))];
      retCode = emaLogic(0, (firstEMANbElement.value-1), firstEMA, optInTimePeriod, secondEMABegIdx, secondEMANbElement, secondEMA);
      if( ((retCode!=RetCode.Success)||(secondEMANbElement.value==0)) ) {
         if( (firstEMA!=outReal) ) {
         }
         return retCode ;
      }
      firstEMAIdx = secondEMABegIdx.value;
      outIdx = 0;
      while( (outIdx<secondEMANbElement.value) ) {
         outReal[outIdx] = ((2.0*firstEMA[firstEMAIdx++])-secondEMA[outIdx]);
         outIdx += 1;
      }
      if( (firstEMA!=outReal) ) {
      }
      outBegIdx.value = (firstEMABegIdx.value+secondEMABegIdx.value);
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
