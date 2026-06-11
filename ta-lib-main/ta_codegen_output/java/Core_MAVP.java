/* Generated */
   public int mavpLookback( int optInMinPeriod, int optInMaxPeriod, MAType optInMAType )
   {
      return maLookback(optInMaxPeriod, optInMAType) ;

   }
   public RetCode mavp( int startIdx,
                        int endIdx,
                        double inReal[],
                        double inPeriods[],
                        int optInMinPeriod,
                        int optInMaxPeriod,
                        MAType optInMAType,
                        MInteger outBegIdx,
                        MInteger outNBElement,
                        double outReal[] )
   {
      int i = 0;
      int j = 0;
      int lookbackTotal = 0;
      int outputSize = 0;
      int tempInt = 0;
      int curPeriod = 0;
      int[] localPeriodArray;
      double[] localOutputArray;
      MInteger localBegIdx = new MInteger();
      MInteger localNbElement = new MInteger();
      RetCode retCode;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackTotal = maLookback(optInMaxPeriod, optInMAType);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      if( (lookbackTotal>startIdx) ) {
         tempInt = lookbackTotal;
      } else {
         tempInt = startIdx;
      }
      if( (tempInt>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outputSize = ((endIdx-tempInt)+1);
      localOutputArray = new double[(int)((outputSize*1))];
      localPeriodArray = new int[(int)((outputSize*1))];
      for( i = 0; (i<outputSize); i += 1 ) {
         tempInt = ((int)inPeriods[(startIdx+i)]);
         if( (tempInt<optInMinPeriod) ) {
            tempInt = optInMinPeriod;
         } else if( (tempInt>optInMaxPeriod) ) {
            tempInt = optInMaxPeriod;
         }
         localPeriodArray[i] = tempInt;
      }
      for( i = 0; (i<outputSize); i += 1 ) {
         curPeriod = localPeriodArray[i];
         if( (curPeriod!=0) ) {
            retCode = maLogic(startIdx, endIdx, inReal, curPeriod, optInMAType, localBegIdx, localNbElement, localOutputArray);
            if( (retCode!=RetCode.Success) ) {
               outBegIdx.value = 0;
               outNBElement.value = 0;
               return retCode ;
            }
            outReal[i] = localOutputArray[i];
            for( j = (i+1); (j<outputSize); j += 1 ) {
               if( (localPeriodArray[j]==curPeriod) ) {
                  localPeriodArray[j] = 0;
                  outReal[j] = localOutputArray[j];
               }
            }
         }
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outputSize;
      return RetCode.Success ;
   }
   public RetCode mavpLogic( int startIdx,
                             int endIdx,
                             double inReal[],
                             double inPeriods[],
                             int optInMinPeriod,
                             int optInMaxPeriod,
                             MAType optInMAType,
                             MInteger outBegIdx,
                             MInteger outNBElement,
                             double outReal[] )
   {
      int i = 0;
      int j = 0;
      int lookbackTotal = 0;
      int outputSize = 0;
      int tempInt = 0;
      int curPeriod = 0;
      int[] localPeriodArray;
      double[] localOutputArray;
      MInteger localBegIdx = new MInteger();
      MInteger localNbElement = new MInteger();
      RetCode retCode;
      lookbackTotal = maLookback(optInMaxPeriod, optInMAType);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      if( (lookbackTotal>startIdx) ) {
         tempInt = lookbackTotal;
      } else {
         tempInt = startIdx;
      }
      if( (tempInt>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outputSize = ((endIdx-tempInt)+1);
      localOutputArray = new double[(int)((outputSize*1))];
      localPeriodArray = new int[(int)((outputSize*1))];
      for( i = 0; (i<outputSize); i += 1 ) {
         tempInt = ((int)inPeriods[(startIdx+i)]);
         if( (tempInt<optInMinPeriod) ) {
            tempInt = optInMinPeriod;
         } else if( (tempInt>optInMaxPeriod) ) {
            tempInt = optInMaxPeriod;
         }
         localPeriodArray[i] = tempInt;
      }
      for( i = 0; (i<outputSize); i += 1 ) {
         curPeriod = localPeriodArray[i];
         if( (curPeriod!=0) ) {
            retCode = maLogic(startIdx, endIdx, inReal, curPeriod, optInMAType, localBegIdx, localNbElement, localOutputArray);
            if( (retCode!=RetCode.Success) ) {
               outBegIdx.value = 0;
               outNBElement.value = 0;
               return retCode ;
            }
            outReal[i] = localOutputArray[i];
            for( j = (i+1); (j<outputSize); j += 1 ) {
               if( (localPeriodArray[j]==curPeriod) ) {
                  localPeriodArray[j] = 0;
                  outReal[j] = localOutputArray[j];
               }
            }
         }
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outputSize;
      return RetCode.Success ;
   }
   public RetCode mavp( int startIdx,
                        int endIdx,
                        float inReal[],
                        float inPeriods[],
                        int optInMinPeriod,
                        int optInMaxPeriod,
                        MAType optInMAType,
                        MInteger outBegIdx,
                        MInteger outNBElement,
                        double outReal[] )
   {
      int i = 0;
      int j = 0;
      int lookbackTotal = 0;
      int outputSize = 0;
      int tempInt = 0;
      int curPeriod = 0;
      int[] localPeriodArray;
      double[] localOutputArray;
      MInteger localBegIdx = new MInteger();
      MInteger localNbElement = new MInteger();
      RetCode retCode;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackTotal = maLookback(optInMaxPeriod, optInMAType);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      if( (lookbackTotal>startIdx) ) {
         tempInt = lookbackTotal;
      } else {
         tempInt = startIdx;
      }
      if( (tempInt>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outputSize = ((endIdx-tempInt)+1);
      localOutputArray = new double[(int)((outputSize*1))];
      localPeriodArray = new int[(int)((outputSize*1))];
      for( i = 0; (i<outputSize); i += 1 ) {
         tempInt = ((int)inPeriods[(startIdx+i)]);
         if( (tempInt<optInMinPeriod) ) {
            tempInt = optInMinPeriod;
         } else if( (tempInt>optInMaxPeriod) ) {
            tempInt = optInMaxPeriod;
         }
         localPeriodArray[i] = tempInt;
      }
      for( i = 0; (i<outputSize); i += 1 ) {
         curPeriod = localPeriodArray[i];
         if( (curPeriod!=0) ) {
            retCode = maLogic(startIdx, endIdx, inReal, curPeriod, optInMAType, localBegIdx, localNbElement, localOutputArray);
            if( (retCode!=RetCode.Success) ) {
               outBegIdx.value = 0;
               outNBElement.value = 0;
               return retCode ;
            }
            outReal[i] = localOutputArray[i];
            for( j = (i+1); (j<outputSize); j += 1 ) {
               if( (localPeriodArray[j]==curPeriod) ) {
                  localPeriodArray[j] = 0;
                  outReal[j] = localOutputArray[j];
               }
            }
         }
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outputSize;
      return RetCode.Success ;
   }
   public RetCode mavpLogic( int startIdx,
                             int endIdx,
                             float inReal[],
                             float inPeriods[],
                             int optInMinPeriod,
                             int optInMaxPeriod,
                             MAType optInMAType,
                             MInteger outBegIdx,
                             MInteger outNBElement,
                             double outReal[] )
   {
      int i = 0;
      int j = 0;
      int lookbackTotal = 0;
      int outputSize = 0;
      int tempInt = 0;
      int curPeriod = 0;
      int[] localPeriodArray;
      double[] localOutputArray;
      MInteger localBegIdx = new MInteger();
      MInteger localNbElement = new MInteger();
      RetCode retCode;
      lookbackTotal = maLookback(optInMaxPeriod, optInMAType);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      if( (lookbackTotal>startIdx) ) {
         tempInt = lookbackTotal;
      } else {
         tempInt = startIdx;
      }
      if( (tempInt>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outputSize = ((endIdx-tempInt)+1);
      localOutputArray = new double[(int)((outputSize*1))];
      localPeriodArray = new int[(int)((outputSize*1))];
      for( i = 0; (i<outputSize); i += 1 ) {
         tempInt = ((int)inPeriods[(startIdx+i)]);
         if( (tempInt<optInMinPeriod) ) {
            tempInt = optInMinPeriod;
         } else if( (tempInt>optInMaxPeriod) ) {
            tempInt = optInMaxPeriod;
         }
         localPeriodArray[i] = tempInt;
      }
      for( i = 0; (i<outputSize); i += 1 ) {
         curPeriod = localPeriodArray[i];
         if( (curPeriod!=0) ) {
            retCode = maLogic(startIdx, endIdx, inReal, curPeriod, optInMAType, localBegIdx, localNbElement, localOutputArray);
            if( (retCode!=RetCode.Success) ) {
               outBegIdx.value = 0;
               outNBElement.value = 0;
               return retCode ;
            }
            outReal[i] = localOutputArray[i];
            for( j = (i+1); (j<outputSize); j += 1 ) {
               if( (localPeriodArray[j]==curPeriod) ) {
                  localPeriodArray[j] = 0;
                  outReal[j] = localOutputArray[j];
               }
            }
         }
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outputSize;
      return RetCode.Success ;
   }
