/* Generated */
   public int plusDmLookback( int optInTimePeriod )
   {
      if( (optInTimePeriod>1) ) {
         return ((optInTimePeriod+this.unstablePeriod[FuncUnstId.PlusDM.ordinal()])-1) ;
      } else {
         return 1 ;
      }

   }
   public RetCode plusDm( int startIdx,
                          int endIdx,
                          double inHigh[],
                          double inLow[],
                          int optInTimePeriod,
                          MInteger outBegIdx,
                          MInteger outNBElement,
                          double outReal[] )
   {
      int today = 0;
      int lookbackTotal = 0;
      int outIdx = 0;
      double prevHigh = 0;
      double prevLow = 0;
      double tempReal = 0;
      double prevPlusDM = 0;
      double diffP = 0;
      double diffM = 0;
      int i = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      if( (optInTimePeriod>1) ) {
         lookbackTotal = ((optInTimePeriod+this.unstablePeriod[FuncUnstId.PlusDM.ordinal()])-1);
      } else {
         lookbackTotal = 1;
      }
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outIdx = 0;
      if( (optInTimePeriod<=1) ) {
         outBegIdx.value = startIdx;
         today = (startIdx-1);
         prevHigh = inHigh[today];
         prevLow = inLow[today];
         while( (today<endIdx) ) {
            today += 1;
            tempReal = inHigh[today];
            diffP = (tempReal-prevHigh);
            prevHigh = tempReal;
            tempReal = inLow[today];
            diffM = (prevLow-tempReal);
            prevLow = tempReal;
            if( ((diffP>0)&&(diffP>diffM)) ) {
               outReal[outIdx++] = diffP;
            } else {
               outReal[outIdx++] = 0;
            }
         }
         outNBElement.value = outIdx;
         return RetCode.Success ;
      }
      outBegIdx.value = startIdx;
      prevPlusDM = 0.0;
      today = (startIdx-lookbackTotal);
      prevHigh = inHigh[today];
      prevLow = inLow[today];
      i = (optInTimePeriod-1);
      while( (i-->0) ) {
         today += 1;
         tempReal = inHigh[today];
         diffP = (tempReal-prevHigh);
         prevHigh = tempReal;
         tempReal = inLow[today];
         diffM = (prevLow-tempReal);
         prevLow = tempReal;
         if( ((diffP>0)&&(diffP>diffM)) ) {
            prevPlusDM += diffP;
         }
      }
      i = this.unstablePeriod[FuncUnstId.PlusDM.ordinal()];
      while( (i--!=0) ) {
         today += 1;
         tempReal = inHigh[today];
         diffP = (tempReal-prevHigh);
         prevHigh = tempReal;
         tempReal = inLow[today];
         diffM = (prevLow-tempReal);
         prevLow = tempReal;
         if( ((diffP>0)&&(diffP>diffM)) ) {
            prevPlusDM = ((prevPlusDM-(prevPlusDM/optInTimePeriod))+diffP);
         } else {
            prevPlusDM = (prevPlusDM-(prevPlusDM/optInTimePeriod));
         }
      }
      outReal[0] = prevPlusDM;
      outIdx = 1;
      while( (today<endIdx) ) {
         today += 1;
         tempReal = inHigh[today];
         diffP = (tempReal-prevHigh);
         prevHigh = tempReal;
         tempReal = inLow[today];
         diffM = (prevLow-tempReal);
         prevLow = tempReal;
         if( ((diffP>0)&&(diffP>diffM)) ) {
            prevPlusDM = ((prevPlusDM-(prevPlusDM/optInTimePeriod))+diffP);
         } else {
            prevPlusDM = (prevPlusDM-(prevPlusDM/optInTimePeriod));
         }
         outReal[outIdx++] = prevPlusDM;
      }
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode plusDmLogic( int startIdx,
                               int endIdx,
                               double inHigh[],
                               double inLow[],
                               int optInTimePeriod,
                               MInteger outBegIdx,
                               MInteger outNBElement,
                               double outReal[] )
   {
      int today = 0;
      int lookbackTotal = 0;
      int outIdx = 0;
      double prevHigh = 0;
      double prevLow = 0;
      double tempReal = 0;
      double prevPlusDM = 0;
      double diffP = 0;
      double diffM = 0;
      int i = 0;
      if( (optInTimePeriod>1) ) {
         lookbackTotal = ((optInTimePeriod+this.unstablePeriod[FuncUnstId.PlusDM.ordinal()])-1);
      } else {
         lookbackTotal = 1;
      }
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outIdx = 0;
      if( (optInTimePeriod<=1) ) {
         outBegIdx.value = startIdx;
         today = (startIdx-1);
         prevHigh = inHigh[today];
         prevLow = inLow[today];
         while( (today<endIdx) ) {
            today += 1;
            tempReal = inHigh[today];
            diffP = (tempReal-prevHigh);
            prevHigh = tempReal;
            tempReal = inLow[today];
            diffM = (prevLow-tempReal);
            prevLow = tempReal;
            if( ((diffP>0)&&(diffP>diffM)) ) {
               outReal[outIdx++] = diffP;
            } else {
               outReal[outIdx++] = 0;
            }
         }
         outNBElement.value = outIdx;
         return RetCode.Success ;
      }
      outBegIdx.value = startIdx;
      prevPlusDM = 0.0;
      today = (startIdx-lookbackTotal);
      prevHigh = inHigh[today];
      prevLow = inLow[today];
      i = (optInTimePeriod-1);
      while( (i-->0) ) {
         today += 1;
         tempReal = inHigh[today];
         diffP = (tempReal-prevHigh);
         prevHigh = tempReal;
         tempReal = inLow[today];
         diffM = (prevLow-tempReal);
         prevLow = tempReal;
         if( ((diffP>0)&&(diffP>diffM)) ) {
            prevPlusDM += diffP;
         }
      }
      i = this.unstablePeriod[FuncUnstId.PlusDM.ordinal()];
      while( (i--!=0) ) {
         today += 1;
         tempReal = inHigh[today];
         diffP = (tempReal-prevHigh);
         prevHigh = tempReal;
         tempReal = inLow[today];
         diffM = (prevLow-tempReal);
         prevLow = tempReal;
         if( ((diffP>0)&&(diffP>diffM)) ) {
            prevPlusDM = ((prevPlusDM-(prevPlusDM/optInTimePeriod))+diffP);
         } else {
            prevPlusDM = (prevPlusDM-(prevPlusDM/optInTimePeriod));
         }
      }
      outReal[0] = prevPlusDM;
      outIdx = 1;
      while( (today<endIdx) ) {
         today += 1;
         tempReal = inHigh[today];
         diffP = (tempReal-prevHigh);
         prevHigh = tempReal;
         tempReal = inLow[today];
         diffM = (prevLow-tempReal);
         prevLow = tempReal;
         if( ((diffP>0)&&(diffP>diffM)) ) {
            prevPlusDM = ((prevPlusDM-(prevPlusDM/optInTimePeriod))+diffP);
         } else {
            prevPlusDM = (prevPlusDM-(prevPlusDM/optInTimePeriod));
         }
         outReal[outIdx++] = prevPlusDM;
      }
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode plusDm( int startIdx,
                          int endIdx,
                          float inHigh[],
                          float inLow[],
                          int optInTimePeriod,
                          MInteger outBegIdx,
                          MInteger outNBElement,
                          double outReal[] )
   {
      int today = 0;
      int lookbackTotal = 0;
      int outIdx = 0;
      double prevHigh = 0;
      double prevLow = 0;
      double tempReal = 0;
      double prevPlusDM = 0;
      double diffP = 0;
      double diffM = 0;
      int i = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      if( (optInTimePeriod>1) ) {
         lookbackTotal = ((optInTimePeriod+this.unstablePeriod[FuncUnstId.PlusDM.ordinal()])-1);
      } else {
         lookbackTotal = 1;
      }
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outIdx = 0;
      if( (optInTimePeriod<=1) ) {
         outBegIdx.value = startIdx;
         today = (startIdx-1);
         prevHigh = inHigh[today];
         prevLow = inLow[today];
         while( (today<endIdx) ) {
            today += 1;
            tempReal = inHigh[today];
            diffP = (tempReal-prevHigh);
            prevHigh = tempReal;
            tempReal = inLow[today];
            diffM = (prevLow-tempReal);
            prevLow = tempReal;
            if( ((diffP>0)&&(diffP>diffM)) ) {
               outReal[outIdx++] = diffP;
            } else {
               outReal[outIdx++] = 0;
            }
         }
         outNBElement.value = outIdx;
         return RetCode.Success ;
      }
      outBegIdx.value = startIdx;
      prevPlusDM = 0.0;
      today = (startIdx-lookbackTotal);
      prevHigh = inHigh[today];
      prevLow = inLow[today];
      i = (optInTimePeriod-1);
      while( (i-->0) ) {
         today += 1;
         tempReal = inHigh[today];
         diffP = (tempReal-prevHigh);
         prevHigh = tempReal;
         tempReal = inLow[today];
         diffM = (prevLow-tempReal);
         prevLow = tempReal;
         if( ((diffP>0)&&(diffP>diffM)) ) {
            prevPlusDM += diffP;
         }
      }
      i = this.unstablePeriod[FuncUnstId.PlusDM.ordinal()];
      while( (i--!=0) ) {
         today += 1;
         tempReal = inHigh[today];
         diffP = (tempReal-prevHigh);
         prevHigh = tempReal;
         tempReal = inLow[today];
         diffM = (prevLow-tempReal);
         prevLow = tempReal;
         if( ((diffP>0)&&(diffP>diffM)) ) {
            prevPlusDM = ((prevPlusDM-(prevPlusDM/optInTimePeriod))+diffP);
         } else {
            prevPlusDM = (prevPlusDM-(prevPlusDM/optInTimePeriod));
         }
      }
      outReal[0] = prevPlusDM;
      outIdx = 1;
      while( (today<endIdx) ) {
         today += 1;
         tempReal = inHigh[today];
         diffP = (tempReal-prevHigh);
         prevHigh = tempReal;
         tempReal = inLow[today];
         diffM = (prevLow-tempReal);
         prevLow = tempReal;
         if( ((diffP>0)&&(diffP>diffM)) ) {
            prevPlusDM = ((prevPlusDM-(prevPlusDM/optInTimePeriod))+diffP);
         } else {
            prevPlusDM = (prevPlusDM-(prevPlusDM/optInTimePeriod));
         }
         outReal[outIdx++] = prevPlusDM;
      }
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode plusDmLogic( int startIdx,
                               int endIdx,
                               float inHigh[],
                               float inLow[],
                               int optInTimePeriod,
                               MInteger outBegIdx,
                               MInteger outNBElement,
                               double outReal[] )
   {
      int today = 0;
      int lookbackTotal = 0;
      int outIdx = 0;
      double prevHigh = 0;
      double prevLow = 0;
      double tempReal = 0;
      double prevPlusDM = 0;
      double diffP = 0;
      double diffM = 0;
      int i = 0;
      if( (optInTimePeriod>1) ) {
         lookbackTotal = ((optInTimePeriod+this.unstablePeriod[FuncUnstId.PlusDM.ordinal()])-1);
      } else {
         lookbackTotal = 1;
      }
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outIdx = 0;
      if( (optInTimePeriod<=1) ) {
         outBegIdx.value = startIdx;
         today = (startIdx-1);
         prevHigh = inHigh[today];
         prevLow = inLow[today];
         while( (today<endIdx) ) {
            today += 1;
            tempReal = inHigh[today];
            diffP = (tempReal-prevHigh);
            prevHigh = tempReal;
            tempReal = inLow[today];
            diffM = (prevLow-tempReal);
            prevLow = tempReal;
            if( ((diffP>0)&&(diffP>diffM)) ) {
               outReal[outIdx++] = diffP;
            } else {
               outReal[outIdx++] = 0;
            }
         }
         outNBElement.value = outIdx;
         return RetCode.Success ;
      }
      outBegIdx.value = startIdx;
      prevPlusDM = 0.0;
      today = (startIdx-lookbackTotal);
      prevHigh = inHigh[today];
      prevLow = inLow[today];
      i = (optInTimePeriod-1);
      while( (i-->0) ) {
         today += 1;
         tempReal = inHigh[today];
         diffP = (tempReal-prevHigh);
         prevHigh = tempReal;
         tempReal = inLow[today];
         diffM = (prevLow-tempReal);
         prevLow = tempReal;
         if( ((diffP>0)&&(diffP>diffM)) ) {
            prevPlusDM += diffP;
         }
      }
      i = this.unstablePeriod[FuncUnstId.PlusDM.ordinal()];
      while( (i--!=0) ) {
         today += 1;
         tempReal = inHigh[today];
         diffP = (tempReal-prevHigh);
         prevHigh = tempReal;
         tempReal = inLow[today];
         diffM = (prevLow-tempReal);
         prevLow = tempReal;
         if( ((diffP>0)&&(diffP>diffM)) ) {
            prevPlusDM = ((prevPlusDM-(prevPlusDM/optInTimePeriod))+diffP);
         } else {
            prevPlusDM = (prevPlusDM-(prevPlusDM/optInTimePeriod));
         }
      }
      outReal[0] = prevPlusDM;
      outIdx = 1;
      while( (today<endIdx) ) {
         today += 1;
         tempReal = inHigh[today];
         diffP = (tempReal-prevHigh);
         prevHigh = tempReal;
         tempReal = inLow[today];
         diffM = (prevLow-tempReal);
         prevLow = tempReal;
         if( ((diffP>0)&&(diffP>diffM)) ) {
            prevPlusDM = ((prevPlusDM-(prevPlusDM/optInTimePeriod))+diffP);
         } else {
            prevPlusDM = (prevPlusDM-(prevPlusDM/optInTimePeriod));
         }
         outReal[outIdx++] = prevPlusDM;
      }
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
