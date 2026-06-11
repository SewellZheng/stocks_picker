/* Generated */
   public int aroonLookback( int optInTimePeriod )
   {
      return optInTimePeriod ;

   }
   public RetCode aroon( int startIdx,
                         int endIdx,
                         double inHigh[],
                         double inLow[],
                         int optInTimePeriod,
                         MInteger outBegIdx,
                         MInteger outNBElement,
                         double outAroonDown[],
                         double outAroonUp[] )
   {
      double lowest = 0;
      double highest = 0;
      double tmp = 0;
      double factor = 0;
      int outIdx = 0;
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
      if( (startIdx<optInTimePeriod) ) {
         startIdx = optInTimePeriod;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outIdx = 0;
      today = startIdx;
      trailingIdx = (startIdx-optInTimePeriod);
      lowestIdx = (0-1);
      highestIdx = (0-1);
      lowest = 0.0;
      highest = 0.0;
      factor = (((double)100.0)/((double)optInTimePeriod));
      while( (today<=endIdx) ) {
         tmp = inLow[today];
         if( (lowestIdx<trailingIdx) ) {
            lowestIdx = trailingIdx;
            lowest = inLow[lowestIdx];
            i = lowestIdx;
            while( (++i<=today) ) {
               tmp = inLow[i];
               if( (tmp<=lowest) ) {
                  lowestIdx = i;
                  lowest = tmp;
               }
            }
         } else if( (tmp<=lowest) ) {
            lowestIdx = today;
            lowest = tmp;
         }
         tmp = inHigh[today];
         if( (highestIdx<trailingIdx) ) {
            highestIdx = trailingIdx;
            highest = inHigh[highestIdx];
            i = highestIdx;
            while( (++i<=today) ) {
               tmp = inHigh[i];
               if( (tmp>=highest) ) {
                  highestIdx = i;
                  highest = tmp;
               }
            }
         } else if( (tmp>=highest) ) {
            highestIdx = today;
            highest = tmp;
         }
         outAroonUp[outIdx] = (factor*(optInTimePeriod-(today-highestIdx)));
         outAroonDown[outIdx] = (factor*(optInTimePeriod-(today-lowestIdx)));
         outIdx += 1;
         trailingIdx += 1;
         today += 1;
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode aroonLogic( int startIdx,
                              int endIdx,
                              double inHigh[],
                              double inLow[],
                              int optInTimePeriod,
                              MInteger outBegIdx,
                              MInteger outNBElement,
                              double outAroonDown[],
                              double outAroonUp[] )
   {
      double lowest = 0;
      double highest = 0;
      double tmp = 0;
      double factor = 0;
      int outIdx = 0;
      int trailingIdx = 0;
      int lowestIdx = 0;
      int highestIdx = 0;
      int today = 0;
      int i = 0;
      if( (startIdx<optInTimePeriod) ) {
         startIdx = optInTimePeriod;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outIdx = 0;
      today = startIdx;
      trailingIdx = (startIdx-optInTimePeriod);
      lowestIdx = (0-1);
      highestIdx = (0-1);
      lowest = 0.0;
      highest = 0.0;
      factor = (((double)100.0)/((double)optInTimePeriod));
      while( (today<=endIdx) ) {
         tmp = inLow[today];
         if( (lowestIdx<trailingIdx) ) {
            lowestIdx = trailingIdx;
            lowest = inLow[lowestIdx];
            i = lowestIdx;
            while( (++i<=today) ) {
               tmp = inLow[i];
               if( (tmp<=lowest) ) {
                  lowestIdx = i;
                  lowest = tmp;
               }
            }
         } else if( (tmp<=lowest) ) {
            lowestIdx = today;
            lowest = tmp;
         }
         tmp = inHigh[today];
         if( (highestIdx<trailingIdx) ) {
            highestIdx = trailingIdx;
            highest = inHigh[highestIdx];
            i = highestIdx;
            while( (++i<=today) ) {
               tmp = inHigh[i];
               if( (tmp>=highest) ) {
                  highestIdx = i;
                  highest = tmp;
               }
            }
         } else if( (tmp>=highest) ) {
            highestIdx = today;
            highest = tmp;
         }
         outAroonUp[outIdx] = (factor*(optInTimePeriod-(today-highestIdx)));
         outAroonDown[outIdx] = (factor*(optInTimePeriod-(today-lowestIdx)));
         outIdx += 1;
         trailingIdx += 1;
         today += 1;
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode aroon( int startIdx,
                         int endIdx,
                         float inHigh[],
                         float inLow[],
                         int optInTimePeriod,
                         MInteger outBegIdx,
                         MInteger outNBElement,
                         double outAroonDown[],
                         double outAroonUp[] )
   {
      double lowest = 0;
      double highest = 0;
      double tmp = 0;
      double factor = 0;
      int outIdx = 0;
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
      if( (startIdx<optInTimePeriod) ) {
         startIdx = optInTimePeriod;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outIdx = 0;
      today = startIdx;
      trailingIdx = (startIdx-optInTimePeriod);
      lowestIdx = (0-1);
      highestIdx = (0-1);
      lowest = 0.0;
      highest = 0.0;
      factor = (((double)100.0)/((double)optInTimePeriod));
      while( (today<=endIdx) ) {
         tmp = inLow[today];
         if( (lowestIdx<trailingIdx) ) {
            lowestIdx = trailingIdx;
            lowest = inLow[lowestIdx];
            i = lowestIdx;
            while( (++i<=today) ) {
               tmp = inLow[i];
               if( (tmp<=lowest) ) {
                  lowestIdx = i;
                  lowest = tmp;
               }
            }
         } else if( (tmp<=lowest) ) {
            lowestIdx = today;
            lowest = tmp;
         }
         tmp = inHigh[today];
         if( (highestIdx<trailingIdx) ) {
            highestIdx = trailingIdx;
            highest = inHigh[highestIdx];
            i = highestIdx;
            while( (++i<=today) ) {
               tmp = inHigh[i];
               if( (tmp>=highest) ) {
                  highestIdx = i;
                  highest = tmp;
               }
            }
         } else if( (tmp>=highest) ) {
            highestIdx = today;
            highest = tmp;
         }
         outAroonUp[outIdx] = (factor*(optInTimePeriod-(today-highestIdx)));
         outAroonDown[outIdx] = (factor*(optInTimePeriod-(today-lowestIdx)));
         outIdx += 1;
         trailingIdx += 1;
         today += 1;
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode aroonLogic( int startIdx,
                              int endIdx,
                              float inHigh[],
                              float inLow[],
                              int optInTimePeriod,
                              MInteger outBegIdx,
                              MInteger outNBElement,
                              double outAroonDown[],
                              double outAroonUp[] )
   {
      double lowest = 0;
      double highest = 0;
      double tmp = 0;
      double factor = 0;
      int outIdx = 0;
      int trailingIdx = 0;
      int lowestIdx = 0;
      int highestIdx = 0;
      int today = 0;
      int i = 0;
      if( (startIdx<optInTimePeriod) ) {
         startIdx = optInTimePeriod;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outIdx = 0;
      today = startIdx;
      trailingIdx = (startIdx-optInTimePeriod);
      lowestIdx = (0-1);
      highestIdx = (0-1);
      lowest = 0.0;
      highest = 0.0;
      factor = (((double)100.0)/((double)optInTimePeriod));
      while( (today<=endIdx) ) {
         tmp = inLow[today];
         if( (lowestIdx<trailingIdx) ) {
            lowestIdx = trailingIdx;
            lowest = inLow[lowestIdx];
            i = lowestIdx;
            while( (++i<=today) ) {
               tmp = inLow[i];
               if( (tmp<=lowest) ) {
                  lowestIdx = i;
                  lowest = tmp;
               }
            }
         } else if( (tmp<=lowest) ) {
            lowestIdx = today;
            lowest = tmp;
         }
         tmp = inHigh[today];
         if( (highestIdx<trailingIdx) ) {
            highestIdx = trailingIdx;
            highest = inHigh[highestIdx];
            i = highestIdx;
            while( (++i<=today) ) {
               tmp = inHigh[i];
               if( (tmp>=highest) ) {
                  highestIdx = i;
                  highest = tmp;
               }
            }
         } else if( (tmp>=highest) ) {
            highestIdx = today;
            highest = tmp;
         }
         outAroonUp[outIdx] = (factor*(optInTimePeriod-(today-highestIdx)));
         outAroonDown[outIdx] = (factor*(optInTimePeriod-(today-lowestIdx)));
         outIdx += 1;
         trailingIdx += 1;
         today += 1;
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
