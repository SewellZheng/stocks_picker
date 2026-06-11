/* Generated */
   public int macdextLookback( int optInFastPeriod, MAType optInFastMAType, int optInSlowPeriod, MAType optInSlowMAType, int optInSignalPeriod, MAType optInSignalMAType )
   {
      int tempInteger;
      int lookbackLargest;
      lookbackLargest = maLookback(optInFastPeriod, optInFastMAType);
      tempInteger = maLookback(optInSlowPeriod, optInSlowMAType);
      if( (tempInteger>lookbackLargest) ) {
         lookbackLargest = tempInteger;
      }
      return (lookbackLargest+maLookback(optInSignalPeriod, optInSignalMAType)) ;

   }
   public RetCode macdext( int startIdx,
                           int endIdx,
                           double inReal[],
                           int optInFastPeriod,
                           MAType optInFastMAType,
                           int optInSlowPeriod,
                           MAType optInSlowMAType,
                           int optInSignalPeriod,
                           MAType optInSignalMAType,
                           MInteger outBegIdx,
                           MInteger outNBElement,
                           double outMACD[],
                           double outMACDSignal[],
                           double outMACDHist[] )
   {
      double[] slowMABuffer;
      double[] fastMABuffer;
      RetCode retCode;
      int tempInteger = 0;
      MInteger outBegIdx1 = new MInteger();
      MInteger outNbElement1 = new MInteger();
      MInteger outBegIdx2 = new MInteger();
      MInteger outNbElement2 = new MInteger();
      int lookbackTotal = 0;
      int lookbackSignal = 0;
      int lookbackLargest = 0;
      int i = 0;
      MAType tempMAType;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      if( (optInSlowPeriod<optInFastPeriod) ) {
         tempInteger = optInSlowPeriod;
         optInSlowPeriod = optInFastPeriod;
         optInFastPeriod = tempInteger;
         tempMAType = optInSlowMAType;
         optInSlowMAType = optInFastMAType;
         optInFastMAType = tempMAType;
      }
      lookbackLargest = maLookback(optInFastPeriod, optInFastMAType);
      tempInteger = maLookback(optInSlowPeriod, optInSlowMAType);
      if( (tempInteger>lookbackLargest) ) {
         lookbackLargest = tempInteger;
      }
      lookbackSignal = maLookback(optInSignalPeriod, optInSignalMAType);
      lookbackTotal = (lookbackSignal+lookbackLargest);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      tempInteger = (((endIdx-startIdx)+1)+lookbackSignal);
      fastMABuffer = new double[(int)((tempInteger*1))];
      slowMABuffer = new double[(int)((tempInteger*1))];
      tempInteger = (startIdx-lookbackSignal);
      retCode = maLogic(tempInteger, endIdx, inReal, optInSlowPeriod, optInSlowMAType, outBegIdx1, outNbElement1, slowMABuffer);
      if( (retCode!=RetCode.Success) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      retCode = maLogic(tempInteger, endIdx, inReal, optInFastPeriod, optInFastMAType, outBegIdx2, outNbElement2, fastMABuffer);
      if( (retCode!=RetCode.Success) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      if( ((((outBegIdx1.value!=tempInteger)||(outBegIdx2.value!=tempInteger))||(outNbElement1.value!=outNbElement2.value))||(outNbElement1.value!=(((endIdx-startIdx)+1)+lookbackSignal))) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.BadParam ;
      }
      for( i = 0; (i<outNbElement1.value); i += 1 ) {
         fastMABuffer[i] = (fastMABuffer[i]-slowMABuffer[i]);
      }
      System.arraycopy(fastMABuffer, lookbackSignal, outMACD, 0, (((endIdx-startIdx)+1)*1));
      retCode = maLogic(0, (outNbElement1.value-1), fastMABuffer, optInSignalPeriod, optInSignalMAType, outBegIdx2, outNbElement2, outMACDSignal);
      if( (retCode!=RetCode.Success) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      for( i = 0; (i<outNbElement2.value); i += 1 ) {
         outMACDHist[i] = (outMACD[i]-outMACDSignal[i]);
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outNbElement2.value;
      return RetCode.Success ;
   }
   public RetCode macdextLogic( int startIdx,
                                int endIdx,
                                double inReal[],
                                int optInFastPeriod,
                                MAType optInFastMAType,
                                int optInSlowPeriod,
                                MAType optInSlowMAType,
                                int optInSignalPeriod,
                                MAType optInSignalMAType,
                                MInteger outBegIdx,
                                MInteger outNBElement,
                                double outMACD[],
                                double outMACDSignal[],
                                double outMACDHist[] )
   {
      double[] slowMABuffer;
      double[] fastMABuffer;
      RetCode retCode;
      int tempInteger = 0;
      MInteger outBegIdx1 = new MInteger();
      MInteger outNbElement1 = new MInteger();
      MInteger outBegIdx2 = new MInteger();
      MInteger outNbElement2 = new MInteger();
      int lookbackTotal = 0;
      int lookbackSignal = 0;
      int lookbackLargest = 0;
      int i = 0;
      MAType tempMAType;
      if( (optInSlowPeriod<optInFastPeriod) ) {
         tempInteger = optInSlowPeriod;
         optInSlowPeriod = optInFastPeriod;
         optInFastPeriod = tempInteger;
         tempMAType = optInSlowMAType;
         optInSlowMAType = optInFastMAType;
         optInFastMAType = tempMAType;
      }
      lookbackLargest = maLookback(optInFastPeriod, optInFastMAType);
      tempInteger = maLookback(optInSlowPeriod, optInSlowMAType);
      if( (tempInteger>lookbackLargest) ) {
         lookbackLargest = tempInteger;
      }
      lookbackSignal = maLookback(optInSignalPeriod, optInSignalMAType);
      lookbackTotal = (lookbackSignal+lookbackLargest);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      tempInteger = (((endIdx-startIdx)+1)+lookbackSignal);
      fastMABuffer = new double[(int)((tempInteger*1))];
      slowMABuffer = new double[(int)((tempInteger*1))];
      tempInteger = (startIdx-lookbackSignal);
      retCode = maLogic(tempInteger, endIdx, inReal, optInSlowPeriod, optInSlowMAType, outBegIdx1, outNbElement1, slowMABuffer);
      if( (retCode!=RetCode.Success) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      retCode = maLogic(tempInteger, endIdx, inReal, optInFastPeriod, optInFastMAType, outBegIdx2, outNbElement2, fastMABuffer);
      if( (retCode!=RetCode.Success) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      if( ((((outBegIdx1.value!=tempInteger)||(outBegIdx2.value!=tempInteger))||(outNbElement1.value!=outNbElement2.value))||(outNbElement1.value!=(((endIdx-startIdx)+1)+lookbackSignal))) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.BadParam ;
      }
      for( i = 0; (i<outNbElement1.value); i += 1 ) {
         fastMABuffer[i] = (fastMABuffer[i]-slowMABuffer[i]);
      }
      System.arraycopy(fastMABuffer, lookbackSignal, outMACD, 0, (((endIdx-startIdx)+1)*1));
      retCode = maLogic(0, (outNbElement1.value-1), fastMABuffer, optInSignalPeriod, optInSignalMAType, outBegIdx2, outNbElement2, outMACDSignal);
      if( (retCode!=RetCode.Success) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      for( i = 0; (i<outNbElement2.value); i += 1 ) {
         outMACDHist[i] = (outMACD[i]-outMACDSignal[i]);
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outNbElement2.value;
      return RetCode.Success ;
   }
   public RetCode macdext( int startIdx,
                           int endIdx,
                           float inReal[],
                           int optInFastPeriod,
                           MAType optInFastMAType,
                           int optInSlowPeriod,
                           MAType optInSlowMAType,
                           int optInSignalPeriod,
                           MAType optInSignalMAType,
                           MInteger outBegIdx,
                           MInteger outNBElement,
                           double outMACD[],
                           double outMACDSignal[],
                           double outMACDHist[] )
   {
      double[] slowMABuffer;
      double[] fastMABuffer;
      RetCode retCode;
      int tempInteger = 0;
      MInteger outBegIdx1 = new MInteger();
      MInteger outNbElement1 = new MInteger();
      MInteger outBegIdx2 = new MInteger();
      MInteger outNbElement2 = new MInteger();
      int lookbackTotal = 0;
      int lookbackSignal = 0;
      int lookbackLargest = 0;
      int i = 0;
      MAType tempMAType;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      if( (optInSlowPeriod<optInFastPeriod) ) {
         tempInteger = optInSlowPeriod;
         optInSlowPeriod = optInFastPeriod;
         optInFastPeriod = tempInteger;
         tempMAType = optInSlowMAType;
         optInSlowMAType = optInFastMAType;
         optInFastMAType = tempMAType;
      }
      lookbackLargest = maLookback(optInFastPeriod, optInFastMAType);
      tempInteger = maLookback(optInSlowPeriod, optInSlowMAType);
      if( (tempInteger>lookbackLargest) ) {
         lookbackLargest = tempInteger;
      }
      lookbackSignal = maLookback(optInSignalPeriod, optInSignalMAType);
      lookbackTotal = (lookbackSignal+lookbackLargest);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      tempInteger = (((endIdx-startIdx)+1)+lookbackSignal);
      fastMABuffer = new double[(int)((tempInteger*1))];
      slowMABuffer = new double[(int)((tempInteger*1))];
      tempInteger = (startIdx-lookbackSignal);
      retCode = maLogic(tempInteger, endIdx, inReal, optInSlowPeriod, optInSlowMAType, outBegIdx1, outNbElement1, slowMABuffer);
      if( (retCode!=RetCode.Success) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      retCode = maLogic(tempInteger, endIdx, inReal, optInFastPeriod, optInFastMAType, outBegIdx2, outNbElement2, fastMABuffer);
      if( (retCode!=RetCode.Success) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      if( ((((outBegIdx1.value!=tempInteger)||(outBegIdx2.value!=tempInteger))||(outNbElement1.value!=outNbElement2.value))||(outNbElement1.value!=(((endIdx-startIdx)+1)+lookbackSignal))) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.BadParam ;
      }
      for( i = 0; (i<outNbElement1.value); i += 1 ) {
         fastMABuffer[i] = (fastMABuffer[i]-slowMABuffer[i]);
      }
      System.arraycopy(fastMABuffer, lookbackSignal, outMACD, 0, (((endIdx-startIdx)+1)*1));
      retCode = maLogic(0, (outNbElement1.value-1), fastMABuffer, optInSignalPeriod, optInSignalMAType, outBegIdx2, outNbElement2, outMACDSignal);
      if( (retCode!=RetCode.Success) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      for( i = 0; (i<outNbElement2.value); i += 1 ) {
         outMACDHist[i] = (outMACD[i]-outMACDSignal[i]);
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outNbElement2.value;
      return RetCode.Success ;
   }
   public RetCode macdextLogic( int startIdx,
                                int endIdx,
                                float inReal[],
                                int optInFastPeriod,
                                MAType optInFastMAType,
                                int optInSlowPeriod,
                                MAType optInSlowMAType,
                                int optInSignalPeriod,
                                MAType optInSignalMAType,
                                MInteger outBegIdx,
                                MInteger outNBElement,
                                double outMACD[],
                                double outMACDSignal[],
                                double outMACDHist[] )
   {
      double[] slowMABuffer;
      double[] fastMABuffer;
      RetCode retCode;
      int tempInteger = 0;
      MInteger outBegIdx1 = new MInteger();
      MInteger outNbElement1 = new MInteger();
      MInteger outBegIdx2 = new MInteger();
      MInteger outNbElement2 = new MInteger();
      int lookbackTotal = 0;
      int lookbackSignal = 0;
      int lookbackLargest = 0;
      int i = 0;
      MAType tempMAType;
      if( (optInSlowPeriod<optInFastPeriod) ) {
         tempInteger = optInSlowPeriod;
         optInSlowPeriod = optInFastPeriod;
         optInFastPeriod = tempInteger;
         tempMAType = optInSlowMAType;
         optInSlowMAType = optInFastMAType;
         optInFastMAType = tempMAType;
      }
      lookbackLargest = maLookback(optInFastPeriod, optInFastMAType);
      tempInteger = maLookback(optInSlowPeriod, optInSlowMAType);
      if( (tempInteger>lookbackLargest) ) {
         lookbackLargest = tempInteger;
      }
      lookbackSignal = maLookback(optInSignalPeriod, optInSignalMAType);
      lookbackTotal = (lookbackSignal+lookbackLargest);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      tempInteger = (((endIdx-startIdx)+1)+lookbackSignal);
      fastMABuffer = new double[(int)((tempInteger*1))];
      slowMABuffer = new double[(int)((tempInteger*1))];
      tempInteger = (startIdx-lookbackSignal);
      retCode = maLogic(tempInteger, endIdx, inReal, optInSlowPeriod, optInSlowMAType, outBegIdx1, outNbElement1, slowMABuffer);
      if( (retCode!=RetCode.Success) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      retCode = maLogic(tempInteger, endIdx, inReal, optInFastPeriod, optInFastMAType, outBegIdx2, outNbElement2, fastMABuffer);
      if( (retCode!=RetCode.Success) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      if( ((((outBegIdx1.value!=tempInteger)||(outBegIdx2.value!=tempInteger))||(outNbElement1.value!=outNbElement2.value))||(outNbElement1.value!=(((endIdx-startIdx)+1)+lookbackSignal))) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.BadParam ;
      }
      for( i = 0; (i<outNbElement1.value); i += 1 ) {
         fastMABuffer[i] = (fastMABuffer[i]-slowMABuffer[i]);
      }
      System.arraycopy(fastMABuffer, lookbackSignal, outMACD, 0, (((endIdx-startIdx)+1)*1));
      retCode = maLogic(0, (outNbElement1.value-1), fastMABuffer, optInSignalPeriod, optInSignalMAType, outBegIdx2, outNbElement2, outMACDSignal);
      if( (retCode!=RetCode.Success) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      for( i = 0; (i<outNbElement2.value); i += 1 ) {
         outMACDHist[i] = (outMACD[i]-outMACDSignal[i]);
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outNbElement2.value;
      return RetCode.Success ;
   }
