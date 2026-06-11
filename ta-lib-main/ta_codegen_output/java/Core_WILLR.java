/* Generated */
   public int willrLookback( int optInTimePeriod )
   {
      return (optInTimePeriod-1) ;

   }
   public RetCode willr( int startIdx,
                         int endIdx,
                         double inHigh[],
                         double inLow[],
                         double inClose[],
                         int optInTimePeriod,
                         MInteger outBegIdx,
                         MInteger outNBElement,
                         double outReal[] )
   {
      double lowest = 0;
      double highest = 0;
      double tmp = 0;
      double diff = 0;
      int outIdx = 0;
      int nbInitialElementNeeded = 0;
      int trailingIdx = 0;
      int lowestIdx = 0;
      int highestIdx = 0;
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
      diff = 0.0;
      outIdx = 0;
      today = startIdx;
      trailingIdx = (startIdx-nbInitialElementNeeded);
      highestIdx = (0-1);
      lowestIdx = highestIdx;
      lowest = 0.0;
      highest = lowest;
      diff = highest;
      while( (today<=endIdx) ) {
         tmp = inLow[today];
         if( (lowestIdx<trailingIdx) ) {
            lowestIdx = trailingIdx;
            lowest = inLow[lowestIdx];
            i = lowestIdx;
            while( (++i<=today) ) {
               tmp = inLow[i];
               if( (tmp<lowest) ) {
                  lowestIdx = i;
                  lowest = tmp;
               }
            }
            diff = ((highest-lowest)/(0-100.0));
         } else if( (tmp<=lowest) ) {
            lowestIdx = today;
            lowest = tmp;
            diff = ((highest-lowest)/(0-100.0));
         }
         tmp = inHigh[today];
         if( (highestIdx<trailingIdx) ) {
            highestIdx = trailingIdx;
            highest = inHigh[highestIdx];
            i = highestIdx;
            while( (++i<=today) ) {
               tmp = inHigh[i];
               if( (tmp>highest) ) {
                  highestIdx = i;
                  highest = tmp;
               }
            }
            diff = ((highest-lowest)/(0-100.0));
         } else if( (tmp>=highest) ) {
            highestIdx = today;
            highest = tmp;
            diff = ((highest-lowest)/(0-100.0));
         }
         if( (diff!=0.0) ) {
            outReal[outIdx++] = ((highest-inClose[today])/diff);
         } else {
            outReal[outIdx++] = 0.0;
         }
         trailingIdx += 1;
         today += 1;
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode willrLogic( int startIdx,
                              int endIdx,
                              double inHigh[],
                              double inLow[],
                              double inClose[],
                              int optInTimePeriod,
                              MInteger outBegIdx,
                              MInteger outNBElement,
                              double outReal[] )
   {
      double lowest = 0;
      double highest = 0;
      double tmp = 0;
      double diff = 0;
      int outIdx = 0;
      int nbInitialElementNeeded = 0;
      int trailingIdx = 0;
      int lowestIdx = 0;
      int highestIdx = 0;
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
      diff = 0.0;
      outIdx = 0;
      today = startIdx;
      trailingIdx = (startIdx-nbInitialElementNeeded);
      highestIdx = (0-1);
      lowestIdx = highestIdx;
      lowest = 0.0;
      highest = lowest;
      diff = highest;
      while( (today<=endIdx) ) {
         tmp = inLow[today];
         if( (lowestIdx<trailingIdx) ) {
            lowestIdx = trailingIdx;
            lowest = inLow[lowestIdx];
            i = lowestIdx;
            while( (++i<=today) ) {
               tmp = inLow[i];
               if( (tmp<lowest) ) {
                  lowestIdx = i;
                  lowest = tmp;
               }
            }
            diff = ((highest-lowest)/(0-100.0));
         } else if( (tmp<=lowest) ) {
            lowestIdx = today;
            lowest = tmp;
            diff = ((highest-lowest)/(0-100.0));
         }
         tmp = inHigh[today];
         if( (highestIdx<trailingIdx) ) {
            highestIdx = trailingIdx;
            highest = inHigh[highestIdx];
            i = highestIdx;
            while( (++i<=today) ) {
               tmp = inHigh[i];
               if( (tmp>highest) ) {
                  highestIdx = i;
                  highest = tmp;
               }
            }
            diff = ((highest-lowest)/(0-100.0));
         } else if( (tmp>=highest) ) {
            highestIdx = today;
            highest = tmp;
            diff = ((highest-lowest)/(0-100.0));
         }
         if( (diff!=0.0) ) {
            outReal[outIdx++] = ((highest-inClose[today])/diff);
         } else {
            outReal[outIdx++] = 0.0;
         }
         trailingIdx += 1;
         today += 1;
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode willr( int startIdx,
                         int endIdx,
                         float inHigh[],
                         float inLow[],
                         float inClose[],
                         int optInTimePeriod,
                         MInteger outBegIdx,
                         MInteger outNBElement,
                         double outReal[] )
   {
      double lowest = 0;
      double highest = 0;
      double tmp = 0;
      double diff = 0;
      int outIdx = 0;
      int nbInitialElementNeeded = 0;
      int trailingIdx = 0;
      int lowestIdx = 0;
      int highestIdx = 0;
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
      diff = 0.0;
      outIdx = 0;
      today = startIdx;
      trailingIdx = (startIdx-nbInitialElementNeeded);
      highestIdx = (0-1);
      lowestIdx = highestIdx;
      lowest = 0.0;
      highest = lowest;
      diff = highest;
      while( (today<=endIdx) ) {
         tmp = inLow[today];
         if( (lowestIdx<trailingIdx) ) {
            lowestIdx = trailingIdx;
            lowest = inLow[lowestIdx];
            i = lowestIdx;
            while( (++i<=today) ) {
               tmp = inLow[i];
               if( (tmp<lowest) ) {
                  lowestIdx = i;
                  lowest = tmp;
               }
            }
            diff = ((highest-lowest)/(0-100.0));
         } else if( (tmp<=lowest) ) {
            lowestIdx = today;
            lowest = tmp;
            diff = ((highest-lowest)/(0-100.0));
         }
         tmp = inHigh[today];
         if( (highestIdx<trailingIdx) ) {
            highestIdx = trailingIdx;
            highest = inHigh[highestIdx];
            i = highestIdx;
            while( (++i<=today) ) {
               tmp = inHigh[i];
               if( (tmp>highest) ) {
                  highestIdx = i;
                  highest = tmp;
               }
            }
            diff = ((highest-lowest)/(0-100.0));
         } else if( (tmp>=highest) ) {
            highestIdx = today;
            highest = tmp;
            diff = ((highest-lowest)/(0-100.0));
         }
         if( (diff!=0.0) ) {
            outReal[outIdx++] = ((highest-inClose[today])/diff);
         } else {
            outReal[outIdx++] = 0.0;
         }
         trailingIdx += 1;
         today += 1;
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode willrLogic( int startIdx,
                              int endIdx,
                              float inHigh[],
                              float inLow[],
                              float inClose[],
                              int optInTimePeriod,
                              MInteger outBegIdx,
                              MInteger outNBElement,
                              double outReal[] )
   {
      double lowest = 0;
      double highest = 0;
      double tmp = 0;
      double diff = 0;
      int outIdx = 0;
      int nbInitialElementNeeded = 0;
      int trailingIdx = 0;
      int lowestIdx = 0;
      int highestIdx = 0;
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
      diff = 0.0;
      outIdx = 0;
      today = startIdx;
      trailingIdx = (startIdx-nbInitialElementNeeded);
      highestIdx = (0-1);
      lowestIdx = highestIdx;
      lowest = 0.0;
      highest = lowest;
      diff = highest;
      while( (today<=endIdx) ) {
         tmp = inLow[today];
         if( (lowestIdx<trailingIdx) ) {
            lowestIdx = trailingIdx;
            lowest = inLow[lowestIdx];
            i = lowestIdx;
            while( (++i<=today) ) {
               tmp = inLow[i];
               if( (tmp<lowest) ) {
                  lowestIdx = i;
                  lowest = tmp;
               }
            }
            diff = ((highest-lowest)/(0-100.0));
         } else if( (tmp<=lowest) ) {
            lowestIdx = today;
            lowest = tmp;
            diff = ((highest-lowest)/(0-100.0));
         }
         tmp = inHigh[today];
         if( (highestIdx<trailingIdx) ) {
            highestIdx = trailingIdx;
            highest = inHigh[highestIdx];
            i = highestIdx;
            while( (++i<=today) ) {
               tmp = inHigh[i];
               if( (tmp>highest) ) {
                  highestIdx = i;
                  highest = tmp;
               }
            }
            diff = ((highest-lowest)/(0-100.0));
         } else if( (tmp>=highest) ) {
            highestIdx = today;
            highest = tmp;
            diff = ((highest-lowest)/(0-100.0));
         }
         if( (diff!=0.0) ) {
            outReal[outIdx++] = ((highest-inClose[today])/diff);
         } else {
            outReal[outIdx++] = 0.0;
         }
         trailingIdx += 1;
         today += 1;
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
