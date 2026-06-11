/* Generated */
   public int trixLookback( int optInTimePeriod )
   {
      int emaLookback;
      emaLookback = emaLookback(optInTimePeriod);
      return ((emaLookback*3)+rocrLookback(1)) ;

   }
   public RetCode trix( int startIdx,
                        int endIdx,
                        double inReal[],
                        int optInTimePeriod,
                        MInteger outBegIdx,
                        MInteger outNBElement,
                        double outReal[] )
   {
      double[] tempBuffer;
      MInteger nbElement = new MInteger();
      MInteger begIdx = new MInteger();
      int totalLookback = 0;
      int emaLookback = 0;
      int rocLookback = 0;
      RetCode retCode;
      int nbElementToOutput = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      emaLookback = emaLookback(optInTimePeriod);
      rocLookback = rocrLookback(1);
      totalLookback = ((emaLookback*3)+rocLookback);
      if( (startIdx<totalLookback) ) {
         startIdx = totalLookback;
      }
      if( (startIdx>endIdx) ) {
         outNBElement.value = 0;
         outBegIdx.value = 0;
         return RetCode.Success ;
      }
      outBegIdx.value = startIdx;
      nbElementToOutput = (((endIdx-startIdx)+1)+totalLookback);
      tempBuffer = new double[(int)((nbElementToOutput*1))];
      retCode = emaLogic((startIdx-totalLookback), endIdx, inReal, optInTimePeriod, begIdx, nbElement, tempBuffer);
      if( ((retCode!=RetCode.Success)||(nbElement.value==0)) ) {
         outNBElement.value = 0;
         outBegIdx.value = 0;
         return retCode ;
      }
      nbElementToOutput -= 1;
      nbElementToOutput -= emaLookback;
      retCode = emaLogic(0, nbElementToOutput, tempBuffer, optInTimePeriod, begIdx, nbElement, tempBuffer);
      if( ((retCode!=RetCode.Success)||(nbElement.value==0)) ) {
         outNBElement.value = 0;
         outBegIdx.value = 0;
         return retCode ;
      }
      nbElementToOutput -= emaLookback;
      retCode = emaLogic(0, nbElementToOutput, tempBuffer, optInTimePeriod, begIdx, nbElement, tempBuffer);
      if( ((retCode!=RetCode.Success)||(nbElement.value==0)) ) {
         outNBElement.value = 0;
         outBegIdx.value = 0;
         return retCode ;
      }
      nbElementToOutput -= emaLookback;
      retCode = rocLogic(0, nbElementToOutput, tempBuffer, 1, begIdx, outNBElement, outReal);
      if( ((retCode!=RetCode.Success)||(((int)outNBElement.value)==0)) ) {
         outNBElement.value = 0;
         outBegIdx.value = 0;
         return retCode ;
      }
      return RetCode.Success ;
   }
   public RetCode trixLogic( int startIdx,
                             int endIdx,
                             double inReal[],
                             int optInTimePeriod,
                             MInteger outBegIdx,
                             MInteger outNBElement,
                             double outReal[] )
   {
      double[] tempBuffer;
      MInteger nbElement = new MInteger();
      MInteger begIdx = new MInteger();
      int totalLookback = 0;
      int emaLookback = 0;
      int rocLookback = 0;
      RetCode retCode;
      int nbElementToOutput = 0;
      emaLookback = emaLookback(optInTimePeriod);
      rocLookback = rocrLookback(1);
      totalLookback = ((emaLookback*3)+rocLookback);
      if( (startIdx<totalLookback) ) {
         startIdx = totalLookback;
      }
      if( (startIdx>endIdx) ) {
         outNBElement.value = 0;
         outBegIdx.value = 0;
         return RetCode.Success ;
      }
      outBegIdx.value = startIdx;
      nbElementToOutput = (((endIdx-startIdx)+1)+totalLookback);
      tempBuffer = new double[(int)((nbElementToOutput*1))];
      retCode = emaLogic((startIdx-totalLookback), endIdx, inReal, optInTimePeriod, begIdx, nbElement, tempBuffer);
      if( ((retCode!=RetCode.Success)||(nbElement.value==0)) ) {
         outNBElement.value = 0;
         outBegIdx.value = 0;
         return retCode ;
      }
      nbElementToOutput -= 1;
      nbElementToOutput -= emaLookback;
      retCode = emaLogic(0, nbElementToOutput, tempBuffer, optInTimePeriod, begIdx, nbElement, tempBuffer);
      if( ((retCode!=RetCode.Success)||(nbElement.value==0)) ) {
         outNBElement.value = 0;
         outBegIdx.value = 0;
         return retCode ;
      }
      nbElementToOutput -= emaLookback;
      retCode = emaLogic(0, nbElementToOutput, tempBuffer, optInTimePeriod, begIdx, nbElement, tempBuffer);
      if( ((retCode!=RetCode.Success)||(nbElement.value==0)) ) {
         outNBElement.value = 0;
         outBegIdx.value = 0;
         return retCode ;
      }
      nbElementToOutput -= emaLookback;
      retCode = rocLogic(0, nbElementToOutput, tempBuffer, 1, begIdx, outNBElement, outReal);
      if( ((retCode!=RetCode.Success)||(((int)outNBElement.value)==0)) ) {
         outNBElement.value = 0;
         outBegIdx.value = 0;
         return retCode ;
      }
      return RetCode.Success ;
   }
   public RetCode trix( int startIdx,
                        int endIdx,
                        float inReal[],
                        int optInTimePeriod,
                        MInteger outBegIdx,
                        MInteger outNBElement,
                        double outReal[] )
   {
      double[] tempBuffer;
      MInteger nbElement = new MInteger();
      MInteger begIdx = new MInteger();
      int totalLookback = 0;
      int emaLookback = 0;
      int rocLookback = 0;
      RetCode retCode;
      int nbElementToOutput = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      emaLookback = emaLookback(optInTimePeriod);
      rocLookback = rocrLookback(1);
      totalLookback = ((emaLookback*3)+rocLookback);
      if( (startIdx<totalLookback) ) {
         startIdx = totalLookback;
      }
      if( (startIdx>endIdx) ) {
         outNBElement.value = 0;
         outBegIdx.value = 0;
         return RetCode.Success ;
      }
      outBegIdx.value = startIdx;
      nbElementToOutput = (((endIdx-startIdx)+1)+totalLookback);
      tempBuffer = new double[(int)((nbElementToOutput*1))];
      retCode = emaLogic((startIdx-totalLookback), endIdx, inReal, optInTimePeriod, begIdx, nbElement, tempBuffer);
      if( ((retCode!=RetCode.Success)||(nbElement.value==0)) ) {
         outNBElement.value = 0;
         outBegIdx.value = 0;
         return retCode ;
      }
      nbElementToOutput -= 1;
      nbElementToOutput -= emaLookback;
      retCode = emaLogic(0, nbElementToOutput, tempBuffer, optInTimePeriod, begIdx, nbElement, tempBuffer);
      if( ((retCode!=RetCode.Success)||(nbElement.value==0)) ) {
         outNBElement.value = 0;
         outBegIdx.value = 0;
         return retCode ;
      }
      nbElementToOutput -= emaLookback;
      retCode = emaLogic(0, nbElementToOutput, tempBuffer, optInTimePeriod, begIdx, nbElement, tempBuffer);
      if( ((retCode!=RetCode.Success)||(nbElement.value==0)) ) {
         outNBElement.value = 0;
         outBegIdx.value = 0;
         return retCode ;
      }
      nbElementToOutput -= emaLookback;
      retCode = rocLogic(0, nbElementToOutput, tempBuffer, 1, begIdx, outNBElement, outReal);
      if( ((retCode!=RetCode.Success)||(((int)outNBElement.value)==0)) ) {
         outNBElement.value = 0;
         outBegIdx.value = 0;
         return retCode ;
      }
      return RetCode.Success ;
   }
   public RetCode trixLogic( int startIdx,
                             int endIdx,
                             float inReal[],
                             int optInTimePeriod,
                             MInteger outBegIdx,
                             MInteger outNBElement,
                             double outReal[] )
   {
      double[] tempBuffer;
      MInteger nbElement = new MInteger();
      MInteger begIdx = new MInteger();
      int totalLookback = 0;
      int emaLookback = 0;
      int rocLookback = 0;
      RetCode retCode;
      int nbElementToOutput = 0;
      emaLookback = emaLookback(optInTimePeriod);
      rocLookback = rocrLookback(1);
      totalLookback = ((emaLookback*3)+rocLookback);
      if( (startIdx<totalLookback) ) {
         startIdx = totalLookback;
      }
      if( (startIdx>endIdx) ) {
         outNBElement.value = 0;
         outBegIdx.value = 0;
         return RetCode.Success ;
      }
      outBegIdx.value = startIdx;
      nbElementToOutput = (((endIdx-startIdx)+1)+totalLookback);
      tempBuffer = new double[(int)((nbElementToOutput*1))];
      retCode = emaLogic((startIdx-totalLookback), endIdx, inReal, optInTimePeriod, begIdx, nbElement, tempBuffer);
      if( ((retCode!=RetCode.Success)||(nbElement.value==0)) ) {
         outNBElement.value = 0;
         outBegIdx.value = 0;
         return retCode ;
      }
      nbElementToOutput -= 1;
      nbElementToOutput -= emaLookback;
      retCode = emaLogic(0, nbElementToOutput, tempBuffer, optInTimePeriod, begIdx, nbElement, tempBuffer);
      if( ((retCode!=RetCode.Success)||(nbElement.value==0)) ) {
         outNBElement.value = 0;
         outBegIdx.value = 0;
         return retCode ;
      }
      nbElementToOutput -= emaLookback;
      retCode = emaLogic(0, nbElementToOutput, tempBuffer, optInTimePeriod, begIdx, nbElement, tempBuffer);
      if( ((retCode!=RetCode.Success)||(nbElement.value==0)) ) {
         outNBElement.value = 0;
         outBegIdx.value = 0;
         return retCode ;
      }
      nbElementToOutput -= emaLookback;
      retCode = rocLogic(0, nbElementToOutput, tempBuffer, 1, begIdx, outNBElement, outReal);
      if( ((retCode!=RetCode.Success)||(((int)outNBElement.value)==0)) ) {
         outNBElement.value = 0;
         outBegIdx.value = 0;
         return retCode ;
      }
      return RetCode.Success ;
   }
