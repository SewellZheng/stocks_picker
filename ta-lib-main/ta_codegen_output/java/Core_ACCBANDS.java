/* Generated */
   public int accbandsLookback( int optInTimePeriod )
   {
      return smaLookback(optInTimePeriod) ;

   }
   public RetCode accbands( int startIdx,
                            int endIdx,
                            double inHigh[],
                            double inLow[],
                            double inClose[],
                            int optInTimePeriod,
                            MInteger outBegIdx,
                            MInteger outNBElement,
                            double outRealUpperBand[],
                            double outRealMiddleBand[],
                            double outRealLowerBand[] )
   {
      RetCode retCode;
      double[] tempBuffer1;
      double[] tempBuffer2;
      MInteger outBegIdxDummy = new MInteger();
      MInteger outNbElementDummy = new MInteger();
      int i = 0;
      int j = 0;
      int outputSize = 0;
      int bufferSize = 0;
      int lookbackTotal = 0;
      double tempReal = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackTotal = smaLookback(optInTimePeriod);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outputSize = ((endIdx-startIdx)+1);
      bufferSize = (outputSize+lookbackTotal);
      tempBuffer1 = new double[(int)((bufferSize*1))];
      tempBuffer2 = new double[(int)((bufferSize*1))];
      for( j = 0, i = (startIdx-lookbackTotal); (i<=endIdx); i += 1, j += 1 ) {
         tempReal = (inHigh[i]+inLow[i]);
         if( !(((-0.00000000000001 < tempReal) && (tempReal < 0.00000000000001))) ) {
            tempReal = ((4*(inHigh[i]-inLow[i]))/tempReal);
            tempBuffer1[j] = (inHigh[i]*(1+tempReal));
            tempBuffer2[j] = (inLow[i]*(1-tempReal));
         } else {
            tempBuffer1[j] = inHigh[i];
            tempBuffer2[j] = inLow[i];
         }
      }
      retCode = smaLogic(startIdx, endIdx, inClose, optInTimePeriod, outBegIdxDummy, outNbElementDummy, outRealMiddleBand);
      if( ((retCode!=RetCode.Success)||(((int)outNbElementDummy.value)!=outputSize)) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      retCode = smaLogic(0, (bufferSize-1), tempBuffer1, optInTimePeriod, outBegIdxDummy, outNbElementDummy, outRealUpperBand);
      if( ((retCode!=RetCode.Success)||(((int)outNbElementDummy.value)!=outputSize)) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      retCode = smaLogic(0, (bufferSize-1), tempBuffer2, optInTimePeriod, outBegIdxDummy, outNbElementDummy, outRealLowerBand);
      if( ((retCode!=RetCode.Success)||(((int)outNbElementDummy.value)!=outputSize)) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outputSize;
      return RetCode.Success ;
   }
   public RetCode accbandsLogic( int startIdx,
                                 int endIdx,
                                 double inHigh[],
                                 double inLow[],
                                 double inClose[],
                                 int optInTimePeriod,
                                 MInteger outBegIdx,
                                 MInteger outNBElement,
                                 double outRealUpperBand[],
                                 double outRealMiddleBand[],
                                 double outRealLowerBand[] )
   {
      RetCode retCode;
      double[] tempBuffer1;
      double[] tempBuffer2;
      MInteger outBegIdxDummy = new MInteger();
      MInteger outNbElementDummy = new MInteger();
      int i = 0;
      int j = 0;
      int outputSize = 0;
      int bufferSize = 0;
      int lookbackTotal = 0;
      double tempReal = 0;
      lookbackTotal = smaLookback(optInTimePeriod);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outputSize = ((endIdx-startIdx)+1);
      bufferSize = (outputSize+lookbackTotal);
      tempBuffer1 = new double[(int)((bufferSize*1))];
      tempBuffer2 = new double[(int)((bufferSize*1))];
      for( j = 0, i = (startIdx-lookbackTotal); (i<=endIdx); i += 1, j += 1 ) {
         tempReal = (inHigh[i]+inLow[i]);
         if( !(((-0.00000000000001 < tempReal) && (tempReal < 0.00000000000001))) ) {
            tempReal = ((4*(inHigh[i]-inLow[i]))/tempReal);
            tempBuffer1[j] = (inHigh[i]*(1+tempReal));
            tempBuffer2[j] = (inLow[i]*(1-tempReal));
         } else {
            tempBuffer1[j] = inHigh[i];
            tempBuffer2[j] = inLow[i];
         }
      }
      retCode = smaLogic(startIdx, endIdx, inClose, optInTimePeriod, outBegIdxDummy, outNbElementDummy, outRealMiddleBand);
      if( ((retCode!=RetCode.Success)||(((int)outNbElementDummy.value)!=outputSize)) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      retCode = smaLogic(0, (bufferSize-1), tempBuffer1, optInTimePeriod, outBegIdxDummy, outNbElementDummy, outRealUpperBand);
      if( ((retCode!=RetCode.Success)||(((int)outNbElementDummy.value)!=outputSize)) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      retCode = smaLogic(0, (bufferSize-1), tempBuffer2, optInTimePeriod, outBegIdxDummy, outNbElementDummy, outRealLowerBand);
      if( ((retCode!=RetCode.Success)||(((int)outNbElementDummy.value)!=outputSize)) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outputSize;
      return RetCode.Success ;
   }
   public RetCode accbands( int startIdx,
                            int endIdx,
                            float inHigh[],
                            float inLow[],
                            float inClose[],
                            int optInTimePeriod,
                            MInteger outBegIdx,
                            MInteger outNBElement,
                            double outRealUpperBand[],
                            double outRealMiddleBand[],
                            double outRealLowerBand[] )
   {
      RetCode retCode;
      double[] tempBuffer1;
      double[] tempBuffer2;
      MInteger outBegIdxDummy = new MInteger();
      MInteger outNbElementDummy = new MInteger();
      int i = 0;
      int j = 0;
      int outputSize = 0;
      int bufferSize = 0;
      int lookbackTotal = 0;
      double tempReal = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackTotal = smaLookback(optInTimePeriod);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outputSize = ((endIdx-startIdx)+1);
      bufferSize = (outputSize+lookbackTotal);
      tempBuffer1 = new double[(int)((bufferSize*1))];
      tempBuffer2 = new double[(int)((bufferSize*1))];
      for( j = 0, i = (startIdx-lookbackTotal); (i<=endIdx); i += 1, j += 1 ) {
         tempReal = (inHigh[i]+inLow[i]);
         if( !(((-0.00000000000001 < tempReal) && (tempReal < 0.00000000000001))) ) {
            tempReal = ((4*(inHigh[i]-inLow[i]))/tempReal);
            tempBuffer1[j] = (inHigh[i]*(1+tempReal));
            tempBuffer2[j] = (inLow[i]*(1-tempReal));
         } else {
            tempBuffer1[j] = inHigh[i];
            tempBuffer2[j] = inLow[i];
         }
      }
      retCode = smaLogic(startIdx, endIdx, inClose, optInTimePeriod, outBegIdxDummy, outNbElementDummy, outRealMiddleBand);
      if( ((retCode!=RetCode.Success)||(((int)outNbElementDummy.value)!=outputSize)) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      retCode = smaLogic(0, (bufferSize-1), tempBuffer1, optInTimePeriod, outBegIdxDummy, outNbElementDummy, outRealUpperBand);
      if( ((retCode!=RetCode.Success)||(((int)outNbElementDummy.value)!=outputSize)) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      retCode = smaLogic(0, (bufferSize-1), tempBuffer2, optInTimePeriod, outBegIdxDummy, outNbElementDummy, outRealLowerBand);
      if( ((retCode!=RetCode.Success)||(((int)outNbElementDummy.value)!=outputSize)) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outputSize;
      return RetCode.Success ;
   }
   public RetCode accbandsLogic( int startIdx,
                                 int endIdx,
                                 float inHigh[],
                                 float inLow[],
                                 float inClose[],
                                 int optInTimePeriod,
                                 MInteger outBegIdx,
                                 MInteger outNBElement,
                                 double outRealUpperBand[],
                                 double outRealMiddleBand[],
                                 double outRealLowerBand[] )
   {
      RetCode retCode;
      double[] tempBuffer1;
      double[] tempBuffer2;
      MInteger outBegIdxDummy = new MInteger();
      MInteger outNbElementDummy = new MInteger();
      int i = 0;
      int j = 0;
      int outputSize = 0;
      int bufferSize = 0;
      int lookbackTotal = 0;
      double tempReal = 0;
      lookbackTotal = smaLookback(optInTimePeriod);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outputSize = ((endIdx-startIdx)+1);
      bufferSize = (outputSize+lookbackTotal);
      tempBuffer1 = new double[(int)((bufferSize*1))];
      tempBuffer2 = new double[(int)((bufferSize*1))];
      for( j = 0, i = (startIdx-lookbackTotal); (i<=endIdx); i += 1, j += 1 ) {
         tempReal = (inHigh[i]+inLow[i]);
         if( !(((-0.00000000000001 < tempReal) && (tempReal < 0.00000000000001))) ) {
            tempReal = ((4*(inHigh[i]-inLow[i]))/tempReal);
            tempBuffer1[j] = (inHigh[i]*(1+tempReal));
            tempBuffer2[j] = (inLow[i]*(1-tempReal));
         } else {
            tempBuffer1[j] = inHigh[i];
            tempBuffer2[j] = inLow[i];
         }
      }
      retCode = smaLogic(startIdx, endIdx, inClose, optInTimePeriod, outBegIdxDummy, outNbElementDummy, outRealMiddleBand);
      if( ((retCode!=RetCode.Success)||(((int)outNbElementDummy.value)!=outputSize)) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      retCode = smaLogic(0, (bufferSize-1), tempBuffer1, optInTimePeriod, outBegIdxDummy, outNbElementDummy, outRealUpperBand);
      if( ((retCode!=RetCode.Success)||(((int)outNbElementDummy.value)!=outputSize)) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      retCode = smaLogic(0, (bufferSize-1), tempBuffer2, optInTimePeriod, outBegIdxDummy, outNbElementDummy, outRealLowerBand);
      if( ((retCode!=RetCode.Success)||(((int)outNbElementDummy.value)!=outputSize)) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outputSize;
      return RetCode.Success ;
   }
