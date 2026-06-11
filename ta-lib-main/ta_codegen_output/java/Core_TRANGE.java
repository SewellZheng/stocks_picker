/* Generated */
   public int trangeLookback( )
   {
      return 1 ;

   }
   public RetCode trange( int startIdx,
                          int endIdx,
                          double inHigh[],
                          double inLow[],
                          double inClose[],
                          MInteger outBegIdx,
                          MInteger outNBElement,
                          double outReal[] )
   {
      int today = 0;
      int outIdx = 0;
      double val2 = 0;
      double val3 = 0;
      double greatest = 0;
      double tempCY = 0;
      double tempLT = 0;
      double tempHT = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      if( (startIdx<1) ) {
         startIdx = 1;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outIdx = 0;
      today = startIdx;
      while( (today<=endIdx) ) {
         tempLT = inLow[today];
         tempHT = inHigh[today];
         tempCY = inClose[(today-1)];
         greatest = (tempHT-tempLT);
         val2 = Math.abs((tempCY-tempHT));
         if( (val2>greatest) ) {
            greatest = val2;
         }
         val3 = Math.abs((tempCY-tempLT));
         if( (val3>greatest) ) {
            greatest = val3;
         }
         outReal[outIdx++] = greatest;
         today += 1;
      }
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode trangeLogic( int startIdx,
                               int endIdx,
                               double inHigh[],
                               double inLow[],
                               double inClose[],
                               MInteger outBegIdx,
                               MInteger outNBElement,
                               double outReal[] )
   {
      int today = 0;
      int outIdx = 0;
      double val2 = 0;
      double val3 = 0;
      double greatest = 0;
      double tempCY = 0;
      double tempLT = 0;
      double tempHT = 0;
      if( (startIdx<1) ) {
         startIdx = 1;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outIdx = 0;
      today = startIdx;
      while( (today<=endIdx) ) {
         tempLT = inLow[today];
         tempHT = inHigh[today];
         tempCY = inClose[(today-1)];
         greatest = (tempHT-tempLT);
         val2 = Math.abs((tempCY-tempHT));
         if( (val2>greatest) ) {
            greatest = val2;
         }
         val3 = Math.abs((tempCY-tempLT));
         if( (val3>greatest) ) {
            greatest = val3;
         }
         outReal[outIdx++] = greatest;
         today += 1;
      }
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode trange( int startIdx,
                          int endIdx,
                          float inHigh[],
                          float inLow[],
                          float inClose[],
                          MInteger outBegIdx,
                          MInteger outNBElement,
                          double outReal[] )
   {
      int today = 0;
      int outIdx = 0;
      double val2 = 0;
      double val3 = 0;
      double greatest = 0;
      double tempCY = 0;
      double tempLT = 0;
      double tempHT = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      if( (startIdx<1) ) {
         startIdx = 1;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outIdx = 0;
      today = startIdx;
      while( (today<=endIdx) ) {
         tempLT = inLow[today];
         tempHT = inHigh[today];
         tempCY = inClose[(today-1)];
         greatest = (tempHT-tempLT);
         val2 = Math.abs((tempCY-tempHT));
         if( (val2>greatest) ) {
            greatest = val2;
         }
         val3 = Math.abs((tempCY-tempLT));
         if( (val3>greatest) ) {
            greatest = val3;
         }
         outReal[outIdx++] = greatest;
         today += 1;
      }
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode trangeLogic( int startIdx,
                               int endIdx,
                               float inHigh[],
                               float inLow[],
                               float inClose[],
                               MInteger outBegIdx,
                               MInteger outNBElement,
                               double outReal[] )
   {
      int today = 0;
      int outIdx = 0;
      double val2 = 0;
      double val3 = 0;
      double greatest = 0;
      double tempCY = 0;
      double tempLT = 0;
      double tempHT = 0;
      if( (startIdx<1) ) {
         startIdx = 1;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outIdx = 0;
      today = startIdx;
      while( (today<=endIdx) ) {
         tempLT = inLow[today];
         tempHT = inHigh[today];
         tempCY = inClose[(today-1)];
         greatest = (tempHT-tempLT);
         val2 = Math.abs((tempCY-tempHT));
         if( (val2>greatest) ) {
            greatest = val2;
         }
         val3 = Math.abs((tempCY-tempLT));
         if( (val3>greatest) ) {
            greatest = val3;
         }
         outReal[outIdx++] = greatest;
         today += 1;
      }
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
