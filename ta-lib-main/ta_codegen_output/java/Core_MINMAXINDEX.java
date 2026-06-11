/* Generated */
   public int minmaxindexLookback( int optInTimePeriod )
   {
      return (optInTimePeriod-1) ;

   }
   public RetCode minmaxindex( int startIdx,
                               int endIdx,
                               double inReal[],
                               int optInTimePeriod,
                               MInteger outBegIdx,
                               MInteger outNBElement,
                               int outMinIdx[],
                               int outMaxIdx[] )
   {
      double highest = 0;
      double lowest = 0;
      double tmpHigh = 0;
      double tmpLow = 0;
      int outIdx = 0;
      int nbInitialElementNeeded = 0;
      int trailingIdx = 0;
      int today = 0;
      int i = 0;
      int highestIdx = 0;
      int lowestIdx = 0;
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
      highestIdx = (0-1);
      highest = 0.0;
      lowestIdx = (0-1);
      lowest = 0.0;
      while( (today<=endIdx) ) {
         tmpHigh = inReal[today];
         tmpLow = tmpHigh;
         if( (highestIdx<trailingIdx) ) {
            highestIdx = trailingIdx;
            highest = inReal[highestIdx];
            i = highestIdx;
            while( (++i<=today) ) {
               tmpHigh = inReal[i];
               if( (tmpHigh>highest) ) {
                  highestIdx = i;
                  highest = tmpHigh;
               }
            }
         } else if( (tmpHigh>=highest) ) {
            highestIdx = today;
            highest = tmpHigh;
         }
         if( (lowestIdx<trailingIdx) ) {
            lowestIdx = trailingIdx;
            lowest = inReal[lowestIdx];
            i = lowestIdx;
            while( (++i<=today) ) {
               tmpLow = inReal[i];
               if( (tmpLow<lowest) ) {
                  lowestIdx = i;
                  lowest = tmpLow;
               }
            }
         } else if( (tmpLow<=lowest) ) {
            lowestIdx = today;
            lowest = tmpLow;
         }
         outMaxIdx[outIdx] = highestIdx;
         outMinIdx[outIdx] = lowestIdx;
         outIdx += 1;
         trailingIdx += 1;
         today += 1;
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode minmaxindexLogic( int startIdx,
                                    int endIdx,
                                    double inReal[],
                                    int optInTimePeriod,
                                    MInteger outBegIdx,
                                    MInteger outNBElement,
                                    int outMinIdx[],
                                    int outMaxIdx[] )
   {
      double highest = 0;
      double lowest = 0;
      double tmpHigh = 0;
      double tmpLow = 0;
      int outIdx = 0;
      int nbInitialElementNeeded = 0;
      int trailingIdx = 0;
      int today = 0;
      int i = 0;
      int highestIdx = 0;
      int lowestIdx = 0;
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
      highestIdx = (0-1);
      highest = 0.0;
      lowestIdx = (0-1);
      lowest = 0.0;
      while( (today<=endIdx) ) {
         tmpHigh = inReal[today];
         tmpLow = tmpHigh;
         if( (highestIdx<trailingIdx) ) {
            highestIdx = trailingIdx;
            highest = inReal[highestIdx];
            i = highestIdx;
            while( (++i<=today) ) {
               tmpHigh = inReal[i];
               if( (tmpHigh>highest) ) {
                  highestIdx = i;
                  highest = tmpHigh;
               }
            }
         } else if( (tmpHigh>=highest) ) {
            highestIdx = today;
            highest = tmpHigh;
         }
         if( (lowestIdx<trailingIdx) ) {
            lowestIdx = trailingIdx;
            lowest = inReal[lowestIdx];
            i = lowestIdx;
            while( (++i<=today) ) {
               tmpLow = inReal[i];
               if( (tmpLow<lowest) ) {
                  lowestIdx = i;
                  lowest = tmpLow;
               }
            }
         } else if( (tmpLow<=lowest) ) {
            lowestIdx = today;
            lowest = tmpLow;
         }
         outMaxIdx[outIdx] = highestIdx;
         outMinIdx[outIdx] = lowestIdx;
         outIdx += 1;
         trailingIdx += 1;
         today += 1;
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode minmaxindex( int startIdx,
                               int endIdx,
                               float inReal[],
                               int optInTimePeriod,
                               MInteger outBegIdx,
                               MInteger outNBElement,
                               int outMinIdx[],
                               int outMaxIdx[] )
   {
      double highest = 0;
      double lowest = 0;
      double tmpHigh = 0;
      double tmpLow = 0;
      int outIdx = 0;
      int nbInitialElementNeeded = 0;
      int trailingIdx = 0;
      int today = 0;
      int i = 0;
      int highestIdx = 0;
      int lowestIdx = 0;
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
      highestIdx = (0-1);
      highest = 0.0;
      lowestIdx = (0-1);
      lowest = 0.0;
      while( (today<=endIdx) ) {
         tmpHigh = inReal[today];
         tmpLow = tmpHigh;
         if( (highestIdx<trailingIdx) ) {
            highestIdx = trailingIdx;
            highest = inReal[highestIdx];
            i = highestIdx;
            while( (++i<=today) ) {
               tmpHigh = inReal[i];
               if( (tmpHigh>highest) ) {
                  highestIdx = i;
                  highest = tmpHigh;
               }
            }
         } else if( (tmpHigh>=highest) ) {
            highestIdx = today;
            highest = tmpHigh;
         }
         if( (lowestIdx<trailingIdx) ) {
            lowestIdx = trailingIdx;
            lowest = inReal[lowestIdx];
            i = lowestIdx;
            while( (++i<=today) ) {
               tmpLow = inReal[i];
               if( (tmpLow<lowest) ) {
                  lowestIdx = i;
                  lowest = tmpLow;
               }
            }
         } else if( (tmpLow<=lowest) ) {
            lowestIdx = today;
            lowest = tmpLow;
         }
         outMaxIdx[outIdx] = highestIdx;
         outMinIdx[outIdx] = lowestIdx;
         outIdx += 1;
         trailingIdx += 1;
         today += 1;
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode minmaxindexLogic( int startIdx,
                                    int endIdx,
                                    float inReal[],
                                    int optInTimePeriod,
                                    MInteger outBegIdx,
                                    MInteger outNBElement,
                                    int outMinIdx[],
                                    int outMaxIdx[] )
   {
      double highest = 0;
      double lowest = 0;
      double tmpHigh = 0;
      double tmpLow = 0;
      int outIdx = 0;
      int nbInitialElementNeeded = 0;
      int trailingIdx = 0;
      int today = 0;
      int i = 0;
      int highestIdx = 0;
      int lowestIdx = 0;
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
      highestIdx = (0-1);
      highest = 0.0;
      lowestIdx = (0-1);
      lowest = 0.0;
      while( (today<=endIdx) ) {
         tmpHigh = inReal[today];
         tmpLow = tmpHigh;
         if( (highestIdx<trailingIdx) ) {
            highestIdx = trailingIdx;
            highest = inReal[highestIdx];
            i = highestIdx;
            while( (++i<=today) ) {
               tmpHigh = inReal[i];
               if( (tmpHigh>highest) ) {
                  highestIdx = i;
                  highest = tmpHigh;
               }
            }
         } else if( (tmpHigh>=highest) ) {
            highestIdx = today;
            highest = tmpHigh;
         }
         if( (lowestIdx<trailingIdx) ) {
            lowestIdx = trailingIdx;
            lowest = inReal[lowestIdx];
            i = lowestIdx;
            while( (++i<=today) ) {
               tmpLow = inReal[i];
               if( (tmpLow<lowest) ) {
                  lowestIdx = i;
                  lowest = tmpLow;
               }
            }
         } else if( (tmpLow<=lowest) ) {
            lowestIdx = today;
            lowest = tmpLow;
         }
         outMaxIdx[outIdx] = highestIdx;
         outMinIdx[outIdx] = lowestIdx;
         outIdx += 1;
         trailingIdx += 1;
         today += 1;
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
