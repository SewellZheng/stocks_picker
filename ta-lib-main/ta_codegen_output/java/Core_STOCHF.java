/* Generated */
   public int stochfLookback( int optInFastK_Period, int optInFastD_Period, MAType optInFastD_MAType )
   {
      int retValue;
      retValue = (optInFastK_Period-1);
      retValue += maLookback(optInFastD_Period, optInFastD_MAType);
      return retValue ;

   }
   public RetCode stochf( int startIdx,
                          int endIdx,
                          double inHigh[],
                          double inLow[],
                          double inClose[],
                          int optInFastK_Period,
                          int optInFastD_Period,
                          MAType optInFastD_MAType,
                          MInteger outBegIdx,
                          MInteger outNBElement,
                          double outFastK[],
                          double outFastD[] )
   {
      RetCode retCode;
      double lowest = 0;
      double highest = 0;
      double tmp = 0;
      double diff = 0;
      double[] tempBuffer;
      int outIdx = 0;
      int lowestIdx = 0;
      int highestIdx = 0;
      int lookbackTotal = 0;
      int lookbackK = 0;
      int lookbackFastD = 0;
      int trailingIdx = 0;
      int today = 0;
      int i = 0;
      int bufferIsAllocated = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackK = (optInFastK_Period-1);
      lookbackFastD = maLookback(optInFastD_Period, optInFastD_MAType);
      lookbackTotal = (lookbackK+lookbackFastD);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outIdx = 0;
      trailingIdx = (startIdx-lookbackTotal);
      today = (trailingIdx+lookbackK);
      highestIdx = (0-1);
      lowestIdx = highestIdx;
      lowest = 0.0;
      highest = lowest;
      diff = highest;
      bufferIsAllocated = 0;
      if( (((outFastK==inHigh)||(outFastK==inLow))||(outFastK==inClose)) ) {
         tempBuffer = outFastK;
      } else if( (((outFastD==inHigh)||(outFastD==inLow))||(outFastD==inClose)) ) {
         tempBuffer = outFastD;
      } else {
         bufferIsAllocated = 1;
         tempBuffer = new double[(int)((((endIdx-today)+1)*1))];
      }
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
            diff = ((highest-lowest)/100.0);
         } else if( (tmp<=lowest) ) {
            lowestIdx = today;
            lowest = tmp;
            diff = ((highest-lowest)/100.0);
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
            diff = ((highest-lowest)/100.0);
         } else if( (tmp>=highest) ) {
            highestIdx = today;
            highest = tmp;
            diff = ((highest-lowest)/100.0);
         }
         if( (diff!=0.0) ) {
            tempBuffer[outIdx++] = ((inClose[today]-lowest)/diff);
         } else {
            tempBuffer[outIdx++] = 0.0;
         }
         trailingIdx += 1;
         today += 1;
      }
      retCode = maLogic(0, (outIdx-1), tempBuffer, optInFastD_Period, optInFastD_MAType, outBegIdx, outNBElement, outFastD);
      if( ((retCode!=RetCode.Success)||(((int)outNBElement.value)==0)) ) {
         if( (bufferIsAllocated) != 0 ) {
         }
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      System.arraycopy(tempBuffer, lookbackFastD, outFastK, 0, (((int)outNBElement.value)*1));
      if( (bufferIsAllocated) != 0 ) {
      }
      if( (retCode!=RetCode.Success) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode stochfLogic( int startIdx,
                               int endIdx,
                               double inHigh[],
                               double inLow[],
                               double inClose[],
                               int optInFastK_Period,
                               int optInFastD_Period,
                               MAType optInFastD_MAType,
                               MInteger outBegIdx,
                               MInteger outNBElement,
                               double outFastK[],
                               double outFastD[] )
   {
      RetCode retCode;
      double lowest = 0;
      double highest = 0;
      double tmp = 0;
      double diff = 0;
      double[] tempBuffer;
      int outIdx = 0;
      int lowestIdx = 0;
      int highestIdx = 0;
      int lookbackTotal = 0;
      int lookbackK = 0;
      int lookbackFastD = 0;
      int trailingIdx = 0;
      int today = 0;
      int i = 0;
      int bufferIsAllocated = 0;
      lookbackK = (optInFastK_Period-1);
      lookbackFastD = maLookback(optInFastD_Period, optInFastD_MAType);
      lookbackTotal = (lookbackK+lookbackFastD);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outIdx = 0;
      trailingIdx = (startIdx-lookbackTotal);
      today = (trailingIdx+lookbackK);
      highestIdx = (0-1);
      lowestIdx = highestIdx;
      lowest = 0.0;
      highest = lowest;
      diff = highest;
      bufferIsAllocated = 0;
      if( (((outFastK==inHigh)||(outFastK==inLow))||(outFastK==inClose)) ) {
         tempBuffer = outFastK;
      } else if( (((outFastD==inHigh)||(outFastD==inLow))||(outFastD==inClose)) ) {
         tempBuffer = outFastD;
      } else {
         bufferIsAllocated = 1;
         tempBuffer = new double[(int)((((endIdx-today)+1)*1))];
      }
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
            diff = ((highest-lowest)/100.0);
         } else if( (tmp<=lowest) ) {
            lowestIdx = today;
            lowest = tmp;
            diff = ((highest-lowest)/100.0);
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
            diff = ((highest-lowest)/100.0);
         } else if( (tmp>=highest) ) {
            highestIdx = today;
            highest = tmp;
            diff = ((highest-lowest)/100.0);
         }
         if( (diff!=0.0) ) {
            tempBuffer[outIdx++] = ((inClose[today]-lowest)/diff);
         } else {
            tempBuffer[outIdx++] = 0.0;
         }
         trailingIdx += 1;
         today += 1;
      }
      retCode = maLogic(0, (outIdx-1), tempBuffer, optInFastD_Period, optInFastD_MAType, outBegIdx, outNBElement, outFastD);
      if( ((retCode!=RetCode.Success)||(((int)outNBElement.value)==0)) ) {
         if( (bufferIsAllocated) != 0 ) {
         }
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      System.arraycopy(tempBuffer, lookbackFastD, outFastK, 0, (((int)outNBElement.value)*1));
      if( (bufferIsAllocated) != 0 ) {
      }
      if( (retCode!=RetCode.Success) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode stochf( int startIdx,
                          int endIdx,
                          float inHigh[],
                          float inLow[],
                          float inClose[],
                          int optInFastK_Period,
                          int optInFastD_Period,
                          MAType optInFastD_MAType,
                          MInteger outBegIdx,
                          MInteger outNBElement,
                          double outFastK[],
                          double outFastD[] )
   {
      RetCode retCode;
      double lowest = 0;
      double highest = 0;
      double tmp = 0;
      double diff = 0;
      double[] tempBuffer;
      int outIdx = 0;
      int lowestIdx = 0;
      int highestIdx = 0;
      int lookbackTotal = 0;
      int lookbackK = 0;
      int lookbackFastD = 0;
      int trailingIdx = 0;
      int today = 0;
      int i = 0;
      int bufferIsAllocated = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackK = (optInFastK_Period-1);
      lookbackFastD = maLookback(optInFastD_Period, optInFastD_MAType);
      lookbackTotal = (lookbackK+lookbackFastD);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outIdx = 0;
      trailingIdx = (startIdx-lookbackTotal);
      today = (trailingIdx+lookbackK);
      highestIdx = (0-1);
      lowestIdx = highestIdx;
      lowest = 0.0;
      highest = lowest;
      diff = highest;
      bufferIsAllocated = 0;
      if( ((false||false)||false) ) {
         tempBuffer = outFastK;
      } else if( ((false||false)||false) ) {
         tempBuffer = outFastD;
      } else {
         bufferIsAllocated = 1;
         tempBuffer = new double[(int)((((endIdx-today)+1)*1))];
      }
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
            diff = ((highest-lowest)/100.0);
         } else if( (tmp<=lowest) ) {
            lowestIdx = today;
            lowest = tmp;
            diff = ((highest-lowest)/100.0);
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
            diff = ((highest-lowest)/100.0);
         } else if( (tmp>=highest) ) {
            highestIdx = today;
            highest = tmp;
            diff = ((highest-lowest)/100.0);
         }
         if( (diff!=0.0) ) {
            tempBuffer[outIdx++] = ((inClose[today]-lowest)/diff);
         } else {
            tempBuffer[outIdx++] = 0.0;
         }
         trailingIdx += 1;
         today += 1;
      }
      retCode = maLogic(0, (outIdx-1), tempBuffer, optInFastD_Period, optInFastD_MAType, outBegIdx, outNBElement, outFastD);
      if( ((retCode!=RetCode.Success)||(((int)outNBElement.value)==0)) ) {
         if( (bufferIsAllocated) != 0 ) {
         }
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      System.arraycopy(tempBuffer, lookbackFastD, outFastK, 0, (((int)outNBElement.value)*1));
      if( (bufferIsAllocated) != 0 ) {
      }
      if( (retCode!=RetCode.Success) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode stochfLogic( int startIdx,
                               int endIdx,
                               float inHigh[],
                               float inLow[],
                               float inClose[],
                               int optInFastK_Period,
                               int optInFastD_Period,
                               MAType optInFastD_MAType,
                               MInteger outBegIdx,
                               MInteger outNBElement,
                               double outFastK[],
                               double outFastD[] )
   {
      RetCode retCode;
      double lowest = 0;
      double highest = 0;
      double tmp = 0;
      double diff = 0;
      double[] tempBuffer;
      int outIdx = 0;
      int lowestIdx = 0;
      int highestIdx = 0;
      int lookbackTotal = 0;
      int lookbackK = 0;
      int lookbackFastD = 0;
      int trailingIdx = 0;
      int today = 0;
      int i = 0;
      int bufferIsAllocated = 0;
      lookbackK = (optInFastK_Period-1);
      lookbackFastD = maLookback(optInFastD_Period, optInFastD_MAType);
      lookbackTotal = (lookbackK+lookbackFastD);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outIdx = 0;
      trailingIdx = (startIdx-lookbackTotal);
      today = (trailingIdx+lookbackK);
      highestIdx = (0-1);
      lowestIdx = highestIdx;
      lowest = 0.0;
      highest = lowest;
      diff = highest;
      bufferIsAllocated = 0;
      if( ((false||false)||false) ) {
         tempBuffer = outFastK;
      } else if( ((false||false)||false) ) {
         tempBuffer = outFastD;
      } else {
         bufferIsAllocated = 1;
         tempBuffer = new double[(int)((((endIdx-today)+1)*1))];
      }
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
            diff = ((highest-lowest)/100.0);
         } else if( (tmp<=lowest) ) {
            lowestIdx = today;
            lowest = tmp;
            diff = ((highest-lowest)/100.0);
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
            diff = ((highest-lowest)/100.0);
         } else if( (tmp>=highest) ) {
            highestIdx = today;
            highest = tmp;
            diff = ((highest-lowest)/100.0);
         }
         if( (diff!=0.0) ) {
            tempBuffer[outIdx++] = ((inClose[today]-lowest)/diff);
         } else {
            tempBuffer[outIdx++] = 0.0;
         }
         trailingIdx += 1;
         today += 1;
      }
      retCode = maLogic(0, (outIdx-1), tempBuffer, optInFastD_Period, optInFastD_MAType, outBegIdx, outNBElement, outFastD);
      if( ((retCode!=RetCode.Success)||(((int)outNBElement.value)==0)) ) {
         if( (bufferIsAllocated) != 0 ) {
         }
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      System.arraycopy(tempBuffer, lookbackFastD, outFastK, 0, (((int)outNBElement.value)*1));
      if( (bufferIsAllocated) != 0 ) {
      }
      if( (retCode!=RetCode.Success) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
