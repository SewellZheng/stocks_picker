/* Generated */
   public int stochrsiLookback( int optInTimePeriod, int optInFastK_Period, int optInFastD_Period, MAType optInFastD_MAType )
   {
      int retValue;
      retValue = (rsiLookback(optInTimePeriod)+stochfLookback(optInFastK_Period, optInFastD_Period, optInFastD_MAType));
      return retValue ;

   }
   public RetCode stochrsi( int startIdx,
                            int endIdx,
                            double inReal[],
                            int optInTimePeriod,
                            int optInFastK_Period,
                            int optInFastD_Period,
                            MAType optInFastD_MAType,
                            MInteger outBegIdx,
                            MInteger outNBElement,
                            double outFastK[],
                            double outFastD[] )
   {
      double[] tempRSIBuffer;
      RetCode retCode;
      int lookbackTotal = 0;
      int lookbackSTOCHF = 0;
      int tempArraySize = 0;
      MInteger outBegIdx1 = new MInteger();
      MInteger outBegIdx2 = new MInteger();
      MInteger outNbElement1 = new MInteger();
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      outBegIdx.value = 0;
      outNBElement.value = 0;
      lookbackSTOCHF = stochfLookback(optInFastK_Period, optInFastD_Period, optInFastD_MAType);
      lookbackTotal = (rsiLookback(optInTimePeriod)+lookbackSTOCHF);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outBegIdx.value = startIdx;
      tempArraySize = (((endIdx-startIdx)+1)+lookbackSTOCHF);
      tempRSIBuffer = new double[(int)((tempArraySize*1))];
      retCode = rsiLogic((startIdx-lookbackSTOCHF), endIdx, inReal, optInTimePeriod, outBegIdx1, outNbElement1, tempRSIBuffer);
      if( ((retCode!=RetCode.Success)||(outNbElement1.value==0)) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      retCode = stochfLogic(0, (tempArraySize-1), tempRSIBuffer, tempRSIBuffer, tempRSIBuffer, optInFastK_Period, optInFastD_Period, optInFastD_MAType, outBegIdx2, outNBElement, outFastK, outFastD);
      if( ((retCode!=RetCode.Success)||(((int)outNBElement.value)==0)) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      return RetCode.Success ;
   }
   public RetCode stochrsiLogic( int startIdx,
                                 int endIdx,
                                 double inReal[],
                                 int optInTimePeriod,
                                 int optInFastK_Period,
                                 int optInFastD_Period,
                                 MAType optInFastD_MAType,
                                 MInteger outBegIdx,
                                 MInteger outNBElement,
                                 double outFastK[],
                                 double outFastD[] )
   {
      double[] tempRSIBuffer;
      RetCode retCode;
      int lookbackTotal = 0;
      int lookbackSTOCHF = 0;
      int tempArraySize = 0;
      MInteger outBegIdx1 = new MInteger();
      MInteger outBegIdx2 = new MInteger();
      MInteger outNbElement1 = new MInteger();
      outBegIdx.value = 0;
      outNBElement.value = 0;
      lookbackSTOCHF = stochfLookback(optInFastK_Period, optInFastD_Period, optInFastD_MAType);
      lookbackTotal = (rsiLookback(optInTimePeriod)+lookbackSTOCHF);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outBegIdx.value = startIdx;
      tempArraySize = (((endIdx-startIdx)+1)+lookbackSTOCHF);
      tempRSIBuffer = new double[(int)((tempArraySize*1))];
      retCode = rsiLogic((startIdx-lookbackSTOCHF), endIdx, inReal, optInTimePeriod, outBegIdx1, outNbElement1, tempRSIBuffer);
      if( ((retCode!=RetCode.Success)||(outNbElement1.value==0)) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      retCode = stochfLogic(0, (tempArraySize-1), tempRSIBuffer, tempRSIBuffer, tempRSIBuffer, optInFastK_Period, optInFastD_Period, optInFastD_MAType, outBegIdx2, outNBElement, outFastK, outFastD);
      if( ((retCode!=RetCode.Success)||(((int)outNBElement.value)==0)) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      return RetCode.Success ;
   }
   public RetCode stochrsi( int startIdx,
                            int endIdx,
                            float inReal[],
                            int optInTimePeriod,
                            int optInFastK_Period,
                            int optInFastD_Period,
                            MAType optInFastD_MAType,
                            MInteger outBegIdx,
                            MInteger outNBElement,
                            double outFastK[],
                            double outFastD[] )
   {
      double[] tempRSIBuffer;
      RetCode retCode;
      int lookbackTotal = 0;
      int lookbackSTOCHF = 0;
      int tempArraySize = 0;
      MInteger outBegIdx1 = new MInteger();
      MInteger outBegIdx2 = new MInteger();
      MInteger outNbElement1 = new MInteger();
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      outBegIdx.value = 0;
      outNBElement.value = 0;
      lookbackSTOCHF = stochfLookback(optInFastK_Period, optInFastD_Period, optInFastD_MAType);
      lookbackTotal = (rsiLookback(optInTimePeriod)+lookbackSTOCHF);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outBegIdx.value = startIdx;
      tempArraySize = (((endIdx-startIdx)+1)+lookbackSTOCHF);
      tempRSIBuffer = new double[(int)((tempArraySize*1))];
      retCode = rsiLogic((startIdx-lookbackSTOCHF), endIdx, inReal, optInTimePeriod, outBegIdx1, outNbElement1, tempRSIBuffer);
      if( ((retCode!=RetCode.Success)||(outNbElement1.value==0)) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      retCode = stochfLogic(0, (tempArraySize-1), tempRSIBuffer, tempRSIBuffer, tempRSIBuffer, optInFastK_Period, optInFastD_Period, optInFastD_MAType, outBegIdx2, outNBElement, outFastK, outFastD);
      if( ((retCode!=RetCode.Success)||(((int)outNBElement.value)==0)) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      return RetCode.Success ;
   }
   public RetCode stochrsiLogic( int startIdx,
                                 int endIdx,
                                 float inReal[],
                                 int optInTimePeriod,
                                 int optInFastK_Period,
                                 int optInFastD_Period,
                                 MAType optInFastD_MAType,
                                 MInteger outBegIdx,
                                 MInteger outNBElement,
                                 double outFastK[],
                                 double outFastD[] )
   {
      double[] tempRSIBuffer;
      RetCode retCode;
      int lookbackTotal = 0;
      int lookbackSTOCHF = 0;
      int tempArraySize = 0;
      MInteger outBegIdx1 = new MInteger();
      MInteger outBegIdx2 = new MInteger();
      MInteger outNbElement1 = new MInteger();
      outBegIdx.value = 0;
      outNBElement.value = 0;
      lookbackSTOCHF = stochfLookback(optInFastK_Period, optInFastD_Period, optInFastD_MAType);
      lookbackTotal = (rsiLookback(optInTimePeriod)+lookbackSTOCHF);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outBegIdx.value = startIdx;
      tempArraySize = (((endIdx-startIdx)+1)+lookbackSTOCHF);
      tempRSIBuffer = new double[(int)((tempArraySize*1))];
      retCode = rsiLogic((startIdx-lookbackSTOCHF), endIdx, inReal, optInTimePeriod, outBegIdx1, outNbElement1, tempRSIBuffer);
      if( ((retCode!=RetCode.Success)||(outNbElement1.value==0)) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      retCode = stochfLogic(0, (tempArraySize-1), tempRSIBuffer, tempRSIBuffer, tempRSIBuffer, optInFastK_Period, optInFastD_Period, optInFastD_MAType, outBegIdx2, outNBElement, outFastK, outFastD);
      if( ((retCode!=RetCode.Success)||(((int)outNBElement.value)==0)) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      return RetCode.Success ;
   }
