/* Generated */
   public int macdLookback( int optInFastPeriod, int optInSlowPeriod, int optInSignalPeriod )
   {
      int tempInteger;
      if( (optInSlowPeriod<optInFastPeriod) ) {
         tempInteger = optInSlowPeriod;
         optInSlowPeriod = optInFastPeriod;
         optInFastPeriod = tempInteger;
      }
      return (emaLookback(optInSlowPeriod)+emaLookback(optInSignalPeriod)) ;

   }
   public RetCode macd( int startIdx,
                        int endIdx,
                        double inReal[],
                        int optInFastPeriod,
                        int optInSlowPeriod,
                        int optInSignalPeriod,
                        MInteger outBegIdx,
                        MInteger outNBElement,
                        double outMACD[],
                        double outMACDSignal[],
                        double outMACDHist[] )
   {
      double[] slowEMABuffer;
      double[] fastEMABuffer;
      RetCode retCode;
      int tempInteger = 0;
      MInteger outBegIdx1 = new MInteger();
      MInteger outNbElement1 = new MInteger();
      MInteger outBegIdx2 = new MInteger();
      MInteger outNbElement2 = new MInteger();
      double slowK = 0;
      double fastK = 0;
      double signalK = 0;
      int lookbackTotal = 0;
      int lookbackSignal = 0;
      int useFixedK = 0;
      int i = 0;
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
      }
      useFixedK = 0;
      if( (optInSlowPeriod==0) ) {
         optInSlowPeriod = 26;
         slowK = 0.075;
         useFixedK = 1;
      } else {
         slowK = (2.0/((double)(optInSlowPeriod+1)));
      }
      if( (optInFastPeriod==0) ) {
         optInFastPeriod = 12;
         fastK = 0.15;
         useFixedK = 1;
      } else {
         fastK = (2.0/((double)(optInFastPeriod+1)));
      }
      signalK = (2.0/((double)(optInSignalPeriod+1)));
      lookbackSignal = emaLookback(optInSignalPeriod);
      lookbackTotal = lookbackSignal;
      lookbackTotal += emaLookback(optInSlowPeriod);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      tempInteger = (((endIdx-startIdx)+1)+lookbackSignal);
      fastEMABuffer = new double[(int)((tempInteger*1))];
      slowEMABuffer = new double[(int)((tempInteger*1))];
      tempInteger = (startIdx-lookbackSignal);
      if( (useFixedK) != 0 ) {
         retCode = emaPrivate(tempInteger, endIdx, inReal, optInSlowPeriod, slowK, outBegIdx1, outNbElement1, slowEMABuffer);
      } else {
         retCode = emaLogic(tempInteger, endIdx, inReal, optInSlowPeriod, outBegIdx1, outNbElement1, slowEMABuffer);
      }
      if( (retCode!=RetCode.Success) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      if( (useFixedK) != 0 ) {
         retCode = emaPrivate(tempInteger, endIdx, inReal, optInFastPeriod, fastK, outBegIdx2, outNbElement2, fastEMABuffer);
      } else {
         retCode = emaLogic(tempInteger, endIdx, inReal, optInFastPeriod, outBegIdx2, outNbElement2, fastEMABuffer);
      }
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
         fastEMABuffer[i] = (fastEMABuffer[i]-slowEMABuffer[i]);
      }
      System.arraycopy(fastEMABuffer, lookbackSignal, outMACD, 0, (((endIdx-startIdx)+1)*1));
      retCode = emaPrivate(0, (outNbElement1.value-1), fastEMABuffer, optInSignalPeriod, signalK, outBegIdx2, outNbElement2, outMACDSignal);
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
   public RetCode macdLogic( int startIdx,
                             int endIdx,
                             double inReal[],
                             int optInFastPeriod,
                             int optInSlowPeriod,
                             int optInSignalPeriod,
                             MInteger outBegIdx,
                             MInteger outNBElement,
                             double outMACD[],
                             double outMACDSignal[],
                             double outMACDHist[] )
   {
      double[] slowEMABuffer;
      double[] fastEMABuffer;
      RetCode retCode;
      int tempInteger = 0;
      MInteger outBegIdx1 = new MInteger();
      MInteger outNbElement1 = new MInteger();
      MInteger outBegIdx2 = new MInteger();
      MInteger outNbElement2 = new MInteger();
      double slowK = 0;
      double fastK = 0;
      double signalK = 0;
      int lookbackTotal = 0;
      int lookbackSignal = 0;
      int useFixedK = 0;
      int i = 0;
      if( (optInSlowPeriod<optInFastPeriod) ) {
         tempInteger = optInSlowPeriod;
         optInSlowPeriod = optInFastPeriod;
         optInFastPeriod = tempInteger;
      }
      useFixedK = 0;
      if( (optInSlowPeriod==0) ) {
         optInSlowPeriod = 26;
         slowK = 0.075;
         useFixedK = 1;
      } else {
         slowK = (2.0/((double)(optInSlowPeriod+1)));
      }
      if( (optInFastPeriod==0) ) {
         optInFastPeriod = 12;
         fastK = 0.15;
         useFixedK = 1;
      } else {
         fastK = (2.0/((double)(optInFastPeriod+1)));
      }
      signalK = (2.0/((double)(optInSignalPeriod+1)));
      lookbackSignal = emaLookback(optInSignalPeriod);
      lookbackTotal = lookbackSignal;
      lookbackTotal += emaLookback(optInSlowPeriod);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      tempInteger = (((endIdx-startIdx)+1)+lookbackSignal);
      fastEMABuffer = new double[(int)((tempInteger*1))];
      slowEMABuffer = new double[(int)((tempInteger*1))];
      tempInteger = (startIdx-lookbackSignal);
      if( (useFixedK) != 0 ) {
         retCode = emaPrivate(tempInteger, endIdx, inReal, optInSlowPeriod, slowK, outBegIdx1, outNbElement1, slowEMABuffer);
      } else {
         retCode = emaLogic(tempInteger, endIdx, inReal, optInSlowPeriod, outBegIdx1, outNbElement1, slowEMABuffer);
      }
      if( (retCode!=RetCode.Success) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      if( (useFixedK) != 0 ) {
         retCode = emaPrivate(tempInteger, endIdx, inReal, optInFastPeriod, fastK, outBegIdx2, outNbElement2, fastEMABuffer);
      } else {
         retCode = emaLogic(tempInteger, endIdx, inReal, optInFastPeriod, outBegIdx2, outNbElement2, fastEMABuffer);
      }
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
         fastEMABuffer[i] = (fastEMABuffer[i]-slowEMABuffer[i]);
      }
      System.arraycopy(fastEMABuffer, lookbackSignal, outMACD, 0, (((endIdx-startIdx)+1)*1));
      retCode = emaPrivate(0, (outNbElement1.value-1), fastEMABuffer, optInSignalPeriod, signalK, outBegIdx2, outNbElement2, outMACDSignal);
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
   public RetCode macd( int startIdx,
                        int endIdx,
                        float inReal[],
                        int optInFastPeriod,
                        int optInSlowPeriod,
                        int optInSignalPeriod,
                        MInteger outBegIdx,
                        MInteger outNBElement,
                        double outMACD[],
                        double outMACDSignal[],
                        double outMACDHist[] )
   {
      double[] slowEMABuffer;
      double[] fastEMABuffer;
      RetCode retCode;
      int tempInteger = 0;
      MInteger outBegIdx1 = new MInteger();
      MInteger outNbElement1 = new MInteger();
      MInteger outBegIdx2 = new MInteger();
      MInteger outNbElement2 = new MInteger();
      double slowK = 0;
      double fastK = 0;
      double signalK = 0;
      int lookbackTotal = 0;
      int lookbackSignal = 0;
      int useFixedK = 0;
      int i = 0;
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
      }
      useFixedK = 0;
      if( (optInSlowPeriod==0) ) {
         optInSlowPeriod = 26;
         slowK = 0.075;
         useFixedK = 1;
      } else {
         slowK = (2.0/((double)(optInSlowPeriod+1)));
      }
      if( (optInFastPeriod==0) ) {
         optInFastPeriod = 12;
         fastK = 0.15;
         useFixedK = 1;
      } else {
         fastK = (2.0/((double)(optInFastPeriod+1)));
      }
      signalK = (2.0/((double)(optInSignalPeriod+1)));
      lookbackSignal = emaLookback(optInSignalPeriod);
      lookbackTotal = lookbackSignal;
      lookbackTotal += emaLookback(optInSlowPeriod);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      tempInteger = (((endIdx-startIdx)+1)+lookbackSignal);
      fastEMABuffer = new double[(int)((tempInteger*1))];
      slowEMABuffer = new double[(int)((tempInteger*1))];
      tempInteger = (startIdx-lookbackSignal);
      if( (useFixedK) != 0 ) {
         retCode = emaPrivate(tempInteger, endIdx, inReal, optInSlowPeriod, slowK, outBegIdx1, outNbElement1, slowEMABuffer);
      } else {
         retCode = emaLogic(tempInteger, endIdx, inReal, optInSlowPeriod, outBegIdx1, outNbElement1, slowEMABuffer);
      }
      if( (retCode!=RetCode.Success) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      if( (useFixedK) != 0 ) {
         retCode = emaPrivate(tempInteger, endIdx, inReal, optInFastPeriod, fastK, outBegIdx2, outNbElement2, fastEMABuffer);
      } else {
         retCode = emaLogic(tempInteger, endIdx, inReal, optInFastPeriod, outBegIdx2, outNbElement2, fastEMABuffer);
      }
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
         fastEMABuffer[i] = (fastEMABuffer[i]-slowEMABuffer[i]);
      }
      System.arraycopy(fastEMABuffer, lookbackSignal, outMACD, 0, (((endIdx-startIdx)+1)*1));
      retCode = emaPrivate(0, (outNbElement1.value-1), fastEMABuffer, optInSignalPeriod, signalK, outBegIdx2, outNbElement2, outMACDSignal);
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
   public RetCode macdLogic( int startIdx,
                             int endIdx,
                             float inReal[],
                             int optInFastPeriod,
                             int optInSlowPeriod,
                             int optInSignalPeriod,
                             MInteger outBegIdx,
                             MInteger outNBElement,
                             double outMACD[],
                             double outMACDSignal[],
                             double outMACDHist[] )
   {
      double[] slowEMABuffer;
      double[] fastEMABuffer;
      RetCode retCode;
      int tempInteger = 0;
      MInteger outBegIdx1 = new MInteger();
      MInteger outNbElement1 = new MInteger();
      MInteger outBegIdx2 = new MInteger();
      MInteger outNbElement2 = new MInteger();
      double slowK = 0;
      double fastK = 0;
      double signalK = 0;
      int lookbackTotal = 0;
      int lookbackSignal = 0;
      int useFixedK = 0;
      int i = 0;
      if( (optInSlowPeriod<optInFastPeriod) ) {
         tempInteger = optInSlowPeriod;
         optInSlowPeriod = optInFastPeriod;
         optInFastPeriod = tempInteger;
      }
      useFixedK = 0;
      if( (optInSlowPeriod==0) ) {
         optInSlowPeriod = 26;
         slowK = 0.075;
         useFixedK = 1;
      } else {
         slowK = (2.0/((double)(optInSlowPeriod+1)));
      }
      if( (optInFastPeriod==0) ) {
         optInFastPeriod = 12;
         fastK = 0.15;
         useFixedK = 1;
      } else {
         fastK = (2.0/((double)(optInFastPeriod+1)));
      }
      signalK = (2.0/((double)(optInSignalPeriod+1)));
      lookbackSignal = emaLookback(optInSignalPeriod);
      lookbackTotal = lookbackSignal;
      lookbackTotal += emaLookback(optInSlowPeriod);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      tempInteger = (((endIdx-startIdx)+1)+lookbackSignal);
      fastEMABuffer = new double[(int)((tempInteger*1))];
      slowEMABuffer = new double[(int)((tempInteger*1))];
      tempInteger = (startIdx-lookbackSignal);
      if( (useFixedK) != 0 ) {
         retCode = emaPrivate(tempInteger, endIdx, inReal, optInSlowPeriod, slowK, outBegIdx1, outNbElement1, slowEMABuffer);
      } else {
         retCode = emaLogic(tempInteger, endIdx, inReal, optInSlowPeriod, outBegIdx1, outNbElement1, slowEMABuffer);
      }
      if( (retCode!=RetCode.Success) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      if( (useFixedK) != 0 ) {
         retCode = emaPrivate(tempInteger, endIdx, inReal, optInFastPeriod, fastK, outBegIdx2, outNbElement2, fastEMABuffer);
      } else {
         retCode = emaLogic(tempInteger, endIdx, inReal, optInFastPeriod, outBegIdx2, outNbElement2, fastEMABuffer);
      }
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
         fastEMABuffer[i] = (fastEMABuffer[i]-slowEMABuffer[i]);
      }
      System.arraycopy(fastEMABuffer, lookbackSignal, outMACD, 0, (((endIdx-startIdx)+1)*1));
      retCode = emaPrivate(0, (outNbElement1.value-1), fastEMABuffer, optInSignalPeriod, signalK, outBegIdx2, outNbElement2, outMACDSignal);
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
