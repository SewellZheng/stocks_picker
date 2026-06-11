/* Generated */
   public int stochLookback( int optInFastK_Period, int optInSlowK_Period, MAType optInSlowK_MAType, int optInSlowD_Period, MAType optInSlowD_MAType )
   {
      int retValue;
      retValue = (optInFastK_Period-1);
      retValue += maLookback(optInSlowK_Period, optInSlowK_MAType);
      retValue += maLookback(optInSlowD_Period, optInSlowD_MAType);
      return retValue ;

   }
   public RetCode stoch( int startIdx,
                         int endIdx,
                         double inHigh[],
                         double inLow[],
                         double inClose[],
                         int optInFastK_Period,
                         int optInSlowK_Period,
                         MAType optInSlowK_MAType,
                         int optInSlowD_Period,
                         MAType optInSlowD_MAType,
                         MInteger outBegIdx,
                         MInteger outNBElement,
                         double outSlowK[],
                         double outSlowD[] )
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
      int lookbackKSlow = 0;
      int lookbackDSlow = 0;
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
      lookbackKSlow = maLookback(optInSlowK_Period, optInSlowK_MAType);
      lookbackDSlow = maLookback(optInSlowD_Period, optInSlowD_MAType);
      lookbackTotal = ((lookbackK+lookbackDSlow)+lookbackKSlow);
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
      if( (((outSlowK==inHigh)||(outSlowK==inLow))||(outSlowK==inClose)) ) {
         tempBuffer = outSlowK;
      } else if( (((outSlowD==inHigh)||(outSlowD==inLow))||(outSlowD==inClose)) ) {
         tempBuffer = outSlowD;
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
      retCode = maLogic(0, (outIdx-1), tempBuffer, optInSlowK_Period, optInSlowK_MAType, outBegIdx, outNBElement, tempBuffer);
      if( ((retCode!=RetCode.Success)||(((int)outNBElement.value)==0)) ) {
         if( (bufferIsAllocated) != 0 ) {
         }
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      retCode = maLogic(0, (((int)outNBElement.value)-1), tempBuffer, optInSlowD_Period, optInSlowD_MAType, outBegIdx, outNBElement, outSlowD);
      System.arraycopy(tempBuffer, lookbackDSlow, outSlowK, 0, (((int)outNBElement.value)*1));
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
   public RetCode stochLogic( int startIdx,
                              int endIdx,
                              double inHigh[],
                              double inLow[],
                              double inClose[],
                              int optInFastK_Period,
                              int optInSlowK_Period,
                              MAType optInSlowK_MAType,
                              int optInSlowD_Period,
                              MAType optInSlowD_MAType,
                              MInteger outBegIdx,
                              MInteger outNBElement,
                              double outSlowK[],
                              double outSlowD[] )
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
      int lookbackKSlow = 0;
      int lookbackDSlow = 0;
      int trailingIdx = 0;
      int today = 0;
      int i = 0;
      int bufferIsAllocated = 0;
      lookbackK = (optInFastK_Period-1);
      lookbackKSlow = maLookback(optInSlowK_Period, optInSlowK_MAType);
      lookbackDSlow = maLookback(optInSlowD_Period, optInSlowD_MAType);
      lookbackTotal = ((lookbackK+lookbackDSlow)+lookbackKSlow);
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
      if( (((outSlowK==inHigh)||(outSlowK==inLow))||(outSlowK==inClose)) ) {
         tempBuffer = outSlowK;
      } else if( (((outSlowD==inHigh)||(outSlowD==inLow))||(outSlowD==inClose)) ) {
         tempBuffer = outSlowD;
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
      retCode = maLogic(0, (outIdx-1), tempBuffer, optInSlowK_Period, optInSlowK_MAType, outBegIdx, outNBElement, tempBuffer);
      if( ((retCode!=RetCode.Success)||(((int)outNBElement.value)==0)) ) {
         if( (bufferIsAllocated) != 0 ) {
         }
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      retCode = maLogic(0, (((int)outNBElement.value)-1), tempBuffer, optInSlowD_Period, optInSlowD_MAType, outBegIdx, outNBElement, outSlowD);
      System.arraycopy(tempBuffer, lookbackDSlow, outSlowK, 0, (((int)outNBElement.value)*1));
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
   public RetCode stoch( int startIdx,
                         int endIdx,
                         float inHigh[],
                         float inLow[],
                         float inClose[],
                         int optInFastK_Period,
                         int optInSlowK_Period,
                         MAType optInSlowK_MAType,
                         int optInSlowD_Period,
                         MAType optInSlowD_MAType,
                         MInteger outBegIdx,
                         MInteger outNBElement,
                         double outSlowK[],
                         double outSlowD[] )
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
      int lookbackKSlow = 0;
      int lookbackDSlow = 0;
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
      lookbackKSlow = maLookback(optInSlowK_Period, optInSlowK_MAType);
      lookbackDSlow = maLookback(optInSlowD_Period, optInSlowD_MAType);
      lookbackTotal = ((lookbackK+lookbackDSlow)+lookbackKSlow);
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
         tempBuffer = outSlowK;
      } else if( ((false||false)||false) ) {
         tempBuffer = outSlowD;
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
      retCode = maLogic(0, (outIdx-1), tempBuffer, optInSlowK_Period, optInSlowK_MAType, outBegIdx, outNBElement, tempBuffer);
      if( ((retCode!=RetCode.Success)||(((int)outNBElement.value)==0)) ) {
         if( (bufferIsAllocated) != 0 ) {
         }
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      retCode = maLogic(0, (((int)outNBElement.value)-1), tempBuffer, optInSlowD_Period, optInSlowD_MAType, outBegIdx, outNBElement, outSlowD);
      System.arraycopy(tempBuffer, lookbackDSlow, outSlowK, 0, (((int)outNBElement.value)*1));
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
   public RetCode stochLogic( int startIdx,
                              int endIdx,
                              float inHigh[],
                              float inLow[],
                              float inClose[],
                              int optInFastK_Period,
                              int optInSlowK_Period,
                              MAType optInSlowK_MAType,
                              int optInSlowD_Period,
                              MAType optInSlowD_MAType,
                              MInteger outBegIdx,
                              MInteger outNBElement,
                              double outSlowK[],
                              double outSlowD[] )
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
      int lookbackKSlow = 0;
      int lookbackDSlow = 0;
      int trailingIdx = 0;
      int today = 0;
      int i = 0;
      int bufferIsAllocated = 0;
      lookbackK = (optInFastK_Period-1);
      lookbackKSlow = maLookback(optInSlowK_Period, optInSlowK_MAType);
      lookbackDSlow = maLookback(optInSlowD_Period, optInSlowD_MAType);
      lookbackTotal = ((lookbackK+lookbackDSlow)+lookbackKSlow);
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
         tempBuffer = outSlowK;
      } else if( ((false||false)||false) ) {
         tempBuffer = outSlowD;
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
      retCode = maLogic(0, (outIdx-1), tempBuffer, optInSlowK_Period, optInSlowK_MAType, outBegIdx, outNBElement, tempBuffer);
      if( ((retCode!=RetCode.Success)||(((int)outNBElement.value)==0)) ) {
         if( (bufferIsAllocated) != 0 ) {
         }
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return retCode ;
      }
      retCode = maLogic(0, (((int)outNBElement.value)-1), tempBuffer, optInSlowD_Period, optInSlowD_MAType, outBegIdx, outNBElement, outSlowD);
      System.arraycopy(tempBuffer, lookbackDSlow, outSlowK, 0, (((int)outNBElement.value)*1));
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
