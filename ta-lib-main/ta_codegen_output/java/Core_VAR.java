/* Generated */
   public int varLookback( int optInTimePeriod, double optInNbDev )
   {
      return (optInTimePeriod-1) ;

   }
   public RetCode var( int startIdx,
                       int endIdx,
                       double inReal[],
                       int optInTimePeriod,
                       double optInNbDev,
                       MInteger outBegIdx,
                       MInteger outNBElement,
                       double outReal[] )
   {
      double tempReal = 0;
      double periodTotal1 = 0;
      double periodTotal2 = 0;
      double meanValue1 = 0;
      double meanValue2 = 0;
      int i = 0;
      int outIdx = 0;
      int trailingIdx = 0;
      int nbInitialElementNeeded = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      nbInitialElementNeeded = (optInTimePeriod-1);
      if( (startIdx<nbInitialElementNeeded) ) {
         startIdx = nbInitialElementNeeded;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      periodTotal1 = 0;
      periodTotal2 = 0;
      trailingIdx = (startIdx-nbInitialElementNeeded);
      i = trailingIdx;
      if( (optInTimePeriod>1) ) {
         while( (i<startIdx) ) {
            tempReal = inReal[i++];
            periodTotal1 += tempReal;
            tempReal *= tempReal;
            periodTotal2 += tempReal;
         }
      }
      outIdx = 0;
      do {
         tempReal = inReal[i++];
         periodTotal1 += tempReal;
         tempReal *= tempReal;
         periodTotal2 += tempReal;
         meanValue1 = (periodTotal1/optInTimePeriod);
         meanValue2 = (periodTotal2/optInTimePeriod);
         tempReal = inReal[trailingIdx++];
         periodTotal1 -= tempReal;
         tempReal *= tempReal;
         periodTotal2 -= tempReal;
         outReal[outIdx++] = (meanValue2-(meanValue1*meanValue1));
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode varLogic( int startIdx,
                            int endIdx,
                            double inReal[],
                            int optInTimePeriod,
                            double optInNbDev,
                            MInteger outBegIdx,
                            MInteger outNBElement,
                            double outReal[] )
   {
      double tempReal = 0;
      double periodTotal1 = 0;
      double periodTotal2 = 0;
      double meanValue1 = 0;
      double meanValue2 = 0;
      int i = 0;
      int outIdx = 0;
      int trailingIdx = 0;
      int nbInitialElementNeeded = 0;
      nbInitialElementNeeded = (optInTimePeriod-1);
      if( (startIdx<nbInitialElementNeeded) ) {
         startIdx = nbInitialElementNeeded;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      periodTotal1 = 0;
      periodTotal2 = 0;
      trailingIdx = (startIdx-nbInitialElementNeeded);
      i = trailingIdx;
      if( (optInTimePeriod>1) ) {
         while( (i<startIdx) ) {
            tempReal = inReal[i++];
            periodTotal1 += tempReal;
            tempReal *= tempReal;
            periodTotal2 += tempReal;
         }
      }
      outIdx = 0;
      do {
         tempReal = inReal[i++];
         periodTotal1 += tempReal;
         tempReal *= tempReal;
         periodTotal2 += tempReal;
         meanValue1 = (periodTotal1/optInTimePeriod);
         meanValue2 = (periodTotal2/optInTimePeriod);
         tempReal = inReal[trailingIdx++];
         periodTotal1 -= tempReal;
         tempReal *= tempReal;
         periodTotal2 -= tempReal;
         outReal[outIdx++] = (meanValue2-(meanValue1*meanValue1));
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode var( int startIdx,
                       int endIdx,
                       float inReal[],
                       int optInTimePeriod,
                       double optInNbDev,
                       MInteger outBegIdx,
                       MInteger outNBElement,
                       double outReal[] )
   {
      double tempReal = 0;
      double periodTotal1 = 0;
      double periodTotal2 = 0;
      double meanValue1 = 0;
      double meanValue2 = 0;
      int i = 0;
      int outIdx = 0;
      int trailingIdx = 0;
      int nbInitialElementNeeded = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      nbInitialElementNeeded = (optInTimePeriod-1);
      if( (startIdx<nbInitialElementNeeded) ) {
         startIdx = nbInitialElementNeeded;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      periodTotal1 = 0;
      periodTotal2 = 0;
      trailingIdx = (startIdx-nbInitialElementNeeded);
      i = trailingIdx;
      if( (optInTimePeriod>1) ) {
         while( (i<startIdx) ) {
            tempReal = inReal[i++];
            periodTotal1 += tempReal;
            tempReal *= tempReal;
            periodTotal2 += tempReal;
         }
      }
      outIdx = 0;
      do {
         tempReal = inReal[i++];
         periodTotal1 += tempReal;
         tempReal *= tempReal;
         periodTotal2 += tempReal;
         meanValue1 = (periodTotal1/optInTimePeriod);
         meanValue2 = (periodTotal2/optInTimePeriod);
         tempReal = inReal[trailingIdx++];
         periodTotal1 -= tempReal;
         tempReal *= tempReal;
         periodTotal2 -= tempReal;
         outReal[outIdx++] = (meanValue2-(meanValue1*meanValue1));
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode varLogic( int startIdx,
                            int endIdx,
                            float inReal[],
                            int optInTimePeriod,
                            double optInNbDev,
                            MInteger outBegIdx,
                            MInteger outNBElement,
                            double outReal[] )
   {
      double tempReal = 0;
      double periodTotal1 = 0;
      double periodTotal2 = 0;
      double meanValue1 = 0;
      double meanValue2 = 0;
      int i = 0;
      int outIdx = 0;
      int trailingIdx = 0;
      int nbInitialElementNeeded = 0;
      nbInitialElementNeeded = (optInTimePeriod-1);
      if( (startIdx<nbInitialElementNeeded) ) {
         startIdx = nbInitialElementNeeded;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      periodTotal1 = 0;
      periodTotal2 = 0;
      trailingIdx = (startIdx-nbInitialElementNeeded);
      i = trailingIdx;
      if( (optInTimePeriod>1) ) {
         while( (i<startIdx) ) {
            tempReal = inReal[i++];
            periodTotal1 += tempReal;
            tempReal *= tempReal;
            periodTotal2 += tempReal;
         }
      }
      outIdx = 0;
      do {
         tempReal = inReal[i++];
         periodTotal1 += tempReal;
         tempReal *= tempReal;
         periodTotal2 += tempReal;
         meanValue1 = (periodTotal1/optInTimePeriod);
         meanValue2 = (periodTotal2/optInTimePeriod);
         tempReal = inReal[trailingIdx++];
         periodTotal1 -= tempReal;
         tempReal *= tempReal;
         periodTotal2 -= tempReal;
         outReal[outIdx++] = (meanValue2-(meanValue1*meanValue1));
      } while( (i<=endIdx) );
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
