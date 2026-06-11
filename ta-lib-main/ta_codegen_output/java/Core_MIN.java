/* Generated */
   public int minLookback( int optInTimePeriod )
   {
      return (optInTimePeriod-1) ;

   }
   public RetCode min( int startIdx,
                       int endIdx,
                       double inReal[],
                       int optInTimePeriod,
                       MInteger outBegIdx,
                       MInteger outNBElement,
                       double outReal[] )
   {
      double lowest = 0;
      double tmp = 0;
      int outIdx = 0;
      int nbInitialElementNeeded = 0;
      int trailingIdx = 0;
      int lowestIdx = 0;
      int today = 0;
      int i = 0;
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
      outIdx = 0;
      today = startIdx;
      trailingIdx = (startIdx-nbInitialElementNeeded);
      lowestIdx = (0-1);
      lowest = 0.0;
      while( (today<=endIdx) ) {
         tmp = inReal[today];
         if( (lowestIdx<trailingIdx) ) {
            lowestIdx = trailingIdx;
            lowest = inReal[lowestIdx];
            i = lowestIdx;
            while( (++i<=today) ) {
               tmp = inReal[i];
               if( (tmp<lowest) ) {
                  lowestIdx = i;
                  lowest = tmp;
               }
            }
         } else if( (tmp<=lowest) ) {
            lowestIdx = today;
            lowest = tmp;
         }
         outReal[outIdx++] = lowest;
         trailingIdx += 1;
         today += 1;
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode minLogic( int startIdx,
                            int endIdx,
                            double inReal[],
                            int optInTimePeriod,
                            MInteger outBegIdx,
                            MInteger outNBElement,
                            double outReal[] )
   {
      double lowest = 0;
      double tmp = 0;
      int outIdx = 0;
      int nbInitialElementNeeded = 0;
      int trailingIdx = 0;
      int lowestIdx = 0;
      int today = 0;
      int i = 0;
      nbInitialElementNeeded = (optInTimePeriod-1);
      if( (startIdx<nbInitialElementNeeded) ) {
         startIdx = nbInitialElementNeeded;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outIdx = 0;
      today = startIdx;
      trailingIdx = (startIdx-nbInitialElementNeeded);
      lowestIdx = (0-1);
      lowest = 0.0;
      while( (today<=endIdx) ) {
         tmp = inReal[today];
         if( (lowestIdx<trailingIdx) ) {
            lowestIdx = trailingIdx;
            lowest = inReal[lowestIdx];
            i = lowestIdx;
            while( (++i<=today) ) {
               tmp = inReal[i];
               if( (tmp<lowest) ) {
                  lowestIdx = i;
                  lowest = tmp;
               }
            }
         } else if( (tmp<=lowest) ) {
            lowestIdx = today;
            lowest = tmp;
         }
         outReal[outIdx++] = lowest;
         trailingIdx += 1;
         today += 1;
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode min( int startIdx,
                       int endIdx,
                       float inReal[],
                       int optInTimePeriod,
                       MInteger outBegIdx,
                       MInteger outNBElement,
                       double outReal[] )
   {
      double lowest = 0;
      double tmp = 0;
      int outIdx = 0;
      int nbInitialElementNeeded = 0;
      int trailingIdx = 0;
      int lowestIdx = 0;
      int today = 0;
      int i = 0;
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
      outIdx = 0;
      today = startIdx;
      trailingIdx = (startIdx-nbInitialElementNeeded);
      lowestIdx = (0-1);
      lowest = 0.0;
      while( (today<=endIdx) ) {
         tmp = inReal[today];
         if( (lowestIdx<trailingIdx) ) {
            lowestIdx = trailingIdx;
            lowest = inReal[lowestIdx];
            i = lowestIdx;
            while( (++i<=today) ) {
               tmp = inReal[i];
               if( (tmp<lowest) ) {
                  lowestIdx = i;
                  lowest = tmp;
               }
            }
         } else if( (tmp<=lowest) ) {
            lowestIdx = today;
            lowest = tmp;
         }
         outReal[outIdx++] = lowest;
         trailingIdx += 1;
         today += 1;
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode minLogic( int startIdx,
                            int endIdx,
                            float inReal[],
                            int optInTimePeriod,
                            MInteger outBegIdx,
                            MInteger outNBElement,
                            double outReal[] )
   {
      double lowest = 0;
      double tmp = 0;
      int outIdx = 0;
      int nbInitialElementNeeded = 0;
      int trailingIdx = 0;
      int lowestIdx = 0;
      int today = 0;
      int i = 0;
      nbInitialElementNeeded = (optInTimePeriod-1);
      if( (startIdx<nbInitialElementNeeded) ) {
         startIdx = nbInitialElementNeeded;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outIdx = 0;
      today = startIdx;
      trailingIdx = (startIdx-nbInitialElementNeeded);
      lowestIdx = (0-1);
      lowest = 0.0;
      while( (today<=endIdx) ) {
         tmp = inReal[today];
         if( (lowestIdx<trailingIdx) ) {
            lowestIdx = trailingIdx;
            lowest = inReal[lowestIdx];
            i = lowestIdx;
            while( (++i<=today) ) {
               tmp = inReal[i];
               if( (tmp<lowest) ) {
                  lowestIdx = i;
                  lowest = tmp;
               }
            }
         } else if( (tmp<=lowest) ) {
            lowestIdx = today;
            lowest = tmp;
         }
         outReal[outIdx++] = lowest;
         trailingIdx += 1;
         today += 1;
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
