/* Generated */
   public int rsiLookback( int optInTimePeriod )
   {
      int retValue;
      retValue = (optInTimePeriod+this.unstablePeriod[FuncUnstId.Rsi.ordinal()]);
      if( (this.compatibility==Compatibility.Metastock) ) {
         retValue = (retValue-1);
      }
      return retValue ;

   }
   public RetCode rsi( int startIdx,
                       int endIdx,
                       double inReal[],
                       int optInTimePeriod,
                       MInteger outBegIdx,
                       MInteger outNBElement,
                       double outReal[] )
   {
      int outIdx = 0;
      int today = 0;
      int lookbackTotal = 0;
      int unstablePeriod = 0;
      int i = 0;
      double prevGain = 0;
      double prevLoss = 0;
      double prevValue = 0;
      double savePrevValue = 0;
      double tempValue1 = 0;
      double tempValue2 = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      outBegIdx.value = 0;
      outNBElement.value = 0;
      lookbackTotal = ((int)rsiLookback(optInTimePeriod));
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         return RetCode.Success ;
      }
      outIdx = 0;
      if( (optInTimePeriod==1) ) {
         outBegIdx.value = startIdx;
         i = ((int)((endIdx-startIdx)+1));
         outNBElement.value = ((int)i);
         System.arraycopy(inReal, startIdx, outReal, 0, (i*1));
         return RetCode.Success ;
      }
      today = (startIdx-lookbackTotal);
      prevValue = ((double)inReal[today]);
      unstablePeriod = this.unstablePeriod[FuncUnstId.Rsi.ordinal()];
      if( ((unstablePeriod==0)&&(this.compatibility==Compatibility.Metastock)) ) {
         savePrevValue = prevValue;
         prevGain = 0.0;
         prevLoss = 0.0;
         for( i = optInTimePeriod; (i>0); i -= 1 ) {
            tempValue1 = ((double)inReal[today]);
            today = (today+1);
            tempValue2 = (tempValue1-prevValue);
            prevValue = tempValue1;
            if( (tempValue2<0.0) ) {
               prevLoss -= tempValue2;
            } else {
               prevGain += tempValue2;
            }
         }
         tempValue1 = (prevLoss/((double)optInTimePeriod));
         tempValue2 = (prevGain/((double)optInTimePeriod));
         tempValue1 = (tempValue2+tempValue1);
         if( !(((-0.00000000000001 < tempValue1) && (tempValue1 < 0.00000000000001))) ) {
            outReal[outIdx] = (100.0*(tempValue2/tempValue1));
            outIdx = (outIdx+1);
         } else {
            outReal[outIdx] = 0.0;
            outIdx = (outIdx+1);
         }
         if( (today>endIdx) ) {
            outBegIdx.value = startIdx;
            outNBElement.value = outIdx;
            return RetCode.Success ;
         }
         today = (today-((int)optInTimePeriod));
         prevValue = savePrevValue;
      }
      prevGain = 0.0;
      prevLoss = 0.0;
      today = (today+1);
      for( i = optInTimePeriod; (i>0); i -= 1 ) {
         tempValue1 = ((double)inReal[today]);
         today = (today+1);
         tempValue2 = (tempValue1-prevValue);
         prevValue = tempValue1;
         if( (tempValue2<0.0) ) {
            prevLoss -= tempValue2;
         } else {
            prevGain += tempValue2;
         }
      }
      prevLoss /= ((double)optInTimePeriod);
      prevGain /= ((double)optInTimePeriod);
      if( (today>startIdx) ) {
         tempValue1 = (prevGain+prevLoss);
         if( !(((-0.00000000000001 < tempValue1) && (tempValue1 < 0.00000000000001))) ) {
            outReal[outIdx] = (100.0*(prevGain/tempValue1));
            outIdx = (outIdx+1);
         } else {
            outReal[outIdx] = 0.0;
            outIdx = (outIdx+1);
         }
      } else {
         while( (today<startIdx) ) {
            tempValue1 = ((double)inReal[today]);
            tempValue2 = (tempValue1-prevValue);
            prevValue = tempValue1;
            prevLoss *= ((double)(optInTimePeriod-1));
            prevGain *= ((double)(optInTimePeriod-1));
            if( (tempValue2<0.0) ) {
               prevLoss -= tempValue2;
            } else {
               prevGain += tempValue2;
            }
            prevLoss /= ((double)optInTimePeriod);
            prevGain /= ((double)optInTimePeriod);
            today = (today+1);
         }
      }
      while( (today<=endIdx) ) {
         tempValue1 = ((double)inReal[today]);
         today = (today+1);
         tempValue2 = (tempValue1-prevValue);
         prevValue = tempValue1;
         prevLoss *= ((double)(optInTimePeriod-1));
         prevGain *= ((double)(optInTimePeriod-1));
         if( (tempValue2<0.0) ) {
            prevLoss -= tempValue2;
         } else {
            prevGain += tempValue2;
         }
         prevLoss /= ((double)optInTimePeriod);
         prevGain /= ((double)optInTimePeriod);
         tempValue1 = (prevGain+prevLoss);
         if( !(((-0.00000000000001 < tempValue1) && (tempValue1 < 0.00000000000001))) ) {
            outReal[outIdx] = (100.0*(prevGain/tempValue1));
            outIdx = (outIdx+1);
         } else {
            outReal[outIdx] = 0.0;
            outIdx = (outIdx+1);
         }
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode rsiLogic( int startIdx,
                            int endIdx,
                            double inReal[],
                            int optInTimePeriod,
                            MInteger outBegIdx,
                            MInteger outNBElement,
                            double outReal[] )
   {
      int outIdx = 0;
      int today = 0;
      int lookbackTotal = 0;
      int unstablePeriod = 0;
      int i = 0;
      double prevGain = 0;
      double prevLoss = 0;
      double prevValue = 0;
      double savePrevValue = 0;
      double tempValue1 = 0;
      double tempValue2 = 0;
      outBegIdx.value = 0;
      outNBElement.value = 0;
      lookbackTotal = ((int)rsiLookback(optInTimePeriod));
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         return RetCode.Success ;
      }
      outIdx = 0;
      if( (optInTimePeriod==1) ) {
         outBegIdx.value = startIdx;
         i = ((int)((endIdx-startIdx)+1));
         outNBElement.value = ((int)i);
         System.arraycopy(inReal, startIdx, outReal, 0, (i*1));
         return RetCode.Success ;
      }
      today = (startIdx-lookbackTotal);
      prevValue = ((double)inReal[today]);
      unstablePeriod = this.unstablePeriod[FuncUnstId.Rsi.ordinal()];
      if( ((unstablePeriod==0)&&(this.compatibility==Compatibility.Metastock)) ) {
         savePrevValue = prevValue;
         prevGain = 0.0;
         prevLoss = 0.0;
         for( i = optInTimePeriod; (i>0); i -= 1 ) {
            tempValue1 = ((double)inReal[today]);
            today = (today+1);
            tempValue2 = (tempValue1-prevValue);
            prevValue = tempValue1;
            if( (tempValue2<0.0) ) {
               prevLoss -= tempValue2;
            } else {
               prevGain += tempValue2;
            }
         }
         tempValue1 = (prevLoss/((double)optInTimePeriod));
         tempValue2 = (prevGain/((double)optInTimePeriod));
         tempValue1 = (tempValue2+tempValue1);
         if( !(((-0.00000000000001 < tempValue1) && (tempValue1 < 0.00000000000001))) ) {
            outReal[outIdx] = (100.0*(tempValue2/tempValue1));
            outIdx = (outIdx+1);
         } else {
            outReal[outIdx] = 0.0;
            outIdx = (outIdx+1);
         }
         if( (today>endIdx) ) {
            outBegIdx.value = startIdx;
            outNBElement.value = outIdx;
            return RetCode.Success ;
         }
         today = (today-((int)optInTimePeriod));
         prevValue = savePrevValue;
      }
      prevGain = 0.0;
      prevLoss = 0.0;
      today = (today+1);
      for( i = optInTimePeriod; (i>0); i -= 1 ) {
         tempValue1 = ((double)inReal[today]);
         today = (today+1);
         tempValue2 = (tempValue1-prevValue);
         prevValue = tempValue1;
         if( (tempValue2<0.0) ) {
            prevLoss -= tempValue2;
         } else {
            prevGain += tempValue2;
         }
      }
      prevLoss /= ((double)optInTimePeriod);
      prevGain /= ((double)optInTimePeriod);
      if( (today>startIdx) ) {
         tempValue1 = (prevGain+prevLoss);
         if( !(((-0.00000000000001 < tempValue1) && (tempValue1 < 0.00000000000001))) ) {
            outReal[outIdx] = (100.0*(prevGain/tempValue1));
            outIdx = (outIdx+1);
         } else {
            outReal[outIdx] = 0.0;
            outIdx = (outIdx+1);
         }
      } else {
         while( (today<startIdx) ) {
            tempValue1 = ((double)inReal[today]);
            tempValue2 = (tempValue1-prevValue);
            prevValue = tempValue1;
            prevLoss *= ((double)(optInTimePeriod-1));
            prevGain *= ((double)(optInTimePeriod-1));
            if( (tempValue2<0.0) ) {
               prevLoss -= tempValue2;
            } else {
               prevGain += tempValue2;
            }
            prevLoss /= ((double)optInTimePeriod);
            prevGain /= ((double)optInTimePeriod);
            today = (today+1);
         }
      }
      while( (today<=endIdx) ) {
         tempValue1 = ((double)inReal[today]);
         today = (today+1);
         tempValue2 = (tempValue1-prevValue);
         prevValue = tempValue1;
         prevLoss *= ((double)(optInTimePeriod-1));
         prevGain *= ((double)(optInTimePeriod-1));
         if( (tempValue2<0.0) ) {
            prevLoss -= tempValue2;
         } else {
            prevGain += tempValue2;
         }
         prevLoss /= ((double)optInTimePeriod);
         prevGain /= ((double)optInTimePeriod);
         tempValue1 = (prevGain+prevLoss);
         if( !(((-0.00000000000001 < tempValue1) && (tempValue1 < 0.00000000000001))) ) {
            outReal[outIdx] = (100.0*(prevGain/tempValue1));
            outIdx = (outIdx+1);
         } else {
            outReal[outIdx] = 0.0;
            outIdx = (outIdx+1);
         }
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode rsi( int startIdx,
                       int endIdx,
                       float inReal[],
                       int optInTimePeriod,
                       MInteger outBegIdx,
                       MInteger outNBElement,
                       double outReal[] )
   {
      int outIdx = 0;
      int today = 0;
      int lookbackTotal = 0;
      int unstablePeriod = 0;
      int i = 0;
      double prevGain = 0;
      double prevLoss = 0;
      double prevValue = 0;
      double savePrevValue = 0;
      double tempValue1 = 0;
      double tempValue2 = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      outBegIdx.value = 0;
      outNBElement.value = 0;
      lookbackTotal = ((int)rsiLookback(optInTimePeriod));
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         return RetCode.Success ;
      }
      outIdx = 0;
      if( (optInTimePeriod==1) ) {
         outBegIdx.value = startIdx;
         i = ((int)((endIdx-startIdx)+1));
         outNBElement.value = ((int)i);
         System.arraycopy(inReal, startIdx, outReal, 0, (i*1));
         return RetCode.Success ;
      }
      today = (startIdx-lookbackTotal);
      prevValue = ((double)inReal[today]);
      unstablePeriod = this.unstablePeriod[FuncUnstId.Rsi.ordinal()];
      if( ((unstablePeriod==0)&&(this.compatibility==Compatibility.Metastock)) ) {
         savePrevValue = prevValue;
         prevGain = 0.0;
         prevLoss = 0.0;
         for( i = optInTimePeriod; (i>0); i -= 1 ) {
            tempValue1 = ((double)inReal[today]);
            today = (today+1);
            tempValue2 = (tempValue1-prevValue);
            prevValue = tempValue1;
            if( (tempValue2<0.0) ) {
               prevLoss -= tempValue2;
            } else {
               prevGain += tempValue2;
            }
         }
         tempValue1 = (prevLoss/((double)optInTimePeriod));
         tempValue2 = (prevGain/((double)optInTimePeriod));
         tempValue1 = (tempValue2+tempValue1);
         if( !(((-0.00000000000001 < tempValue1) && (tempValue1 < 0.00000000000001))) ) {
            outReal[outIdx] = (100.0*(tempValue2/tempValue1));
            outIdx = (outIdx+1);
         } else {
            outReal[outIdx] = 0.0;
            outIdx = (outIdx+1);
         }
         if( (today>endIdx) ) {
            outBegIdx.value = startIdx;
            outNBElement.value = outIdx;
            return RetCode.Success ;
         }
         today = (today-((int)optInTimePeriod));
         prevValue = savePrevValue;
      }
      prevGain = 0.0;
      prevLoss = 0.0;
      today = (today+1);
      for( i = optInTimePeriod; (i>0); i -= 1 ) {
         tempValue1 = ((double)inReal[today]);
         today = (today+1);
         tempValue2 = (tempValue1-prevValue);
         prevValue = tempValue1;
         if( (tempValue2<0.0) ) {
            prevLoss -= tempValue2;
         } else {
            prevGain += tempValue2;
         }
      }
      prevLoss /= ((double)optInTimePeriod);
      prevGain /= ((double)optInTimePeriod);
      if( (today>startIdx) ) {
         tempValue1 = (prevGain+prevLoss);
         if( !(((-0.00000000000001 < tempValue1) && (tempValue1 < 0.00000000000001))) ) {
            outReal[outIdx] = (100.0*(prevGain/tempValue1));
            outIdx = (outIdx+1);
         } else {
            outReal[outIdx] = 0.0;
            outIdx = (outIdx+1);
         }
      } else {
         while( (today<startIdx) ) {
            tempValue1 = ((double)inReal[today]);
            tempValue2 = (tempValue1-prevValue);
            prevValue = tempValue1;
            prevLoss *= ((double)(optInTimePeriod-1));
            prevGain *= ((double)(optInTimePeriod-1));
            if( (tempValue2<0.0) ) {
               prevLoss -= tempValue2;
            } else {
               prevGain += tempValue2;
            }
            prevLoss /= ((double)optInTimePeriod);
            prevGain /= ((double)optInTimePeriod);
            today = (today+1);
         }
      }
      while( (today<=endIdx) ) {
         tempValue1 = ((double)inReal[today]);
         today = (today+1);
         tempValue2 = (tempValue1-prevValue);
         prevValue = tempValue1;
         prevLoss *= ((double)(optInTimePeriod-1));
         prevGain *= ((double)(optInTimePeriod-1));
         if( (tempValue2<0.0) ) {
            prevLoss -= tempValue2;
         } else {
            prevGain += tempValue2;
         }
         prevLoss /= ((double)optInTimePeriod);
         prevGain /= ((double)optInTimePeriod);
         tempValue1 = (prevGain+prevLoss);
         if( !(((-0.00000000000001 < tempValue1) && (tempValue1 < 0.00000000000001))) ) {
            outReal[outIdx] = (100.0*(prevGain/tempValue1));
            outIdx = (outIdx+1);
         } else {
            outReal[outIdx] = 0.0;
            outIdx = (outIdx+1);
         }
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode rsiLogic( int startIdx,
                            int endIdx,
                            float inReal[],
                            int optInTimePeriod,
                            MInteger outBegIdx,
                            MInteger outNBElement,
                            double outReal[] )
   {
      int outIdx = 0;
      int today = 0;
      int lookbackTotal = 0;
      int unstablePeriod = 0;
      int i = 0;
      double prevGain = 0;
      double prevLoss = 0;
      double prevValue = 0;
      double savePrevValue = 0;
      double tempValue1 = 0;
      double tempValue2 = 0;
      outBegIdx.value = 0;
      outNBElement.value = 0;
      lookbackTotal = ((int)rsiLookback(optInTimePeriod));
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         return RetCode.Success ;
      }
      outIdx = 0;
      if( (optInTimePeriod==1) ) {
         outBegIdx.value = startIdx;
         i = ((int)((endIdx-startIdx)+1));
         outNBElement.value = ((int)i);
         System.arraycopy(inReal, startIdx, outReal, 0, (i*1));
         return RetCode.Success ;
      }
      today = (startIdx-lookbackTotal);
      prevValue = ((double)inReal[today]);
      unstablePeriod = this.unstablePeriod[FuncUnstId.Rsi.ordinal()];
      if( ((unstablePeriod==0)&&(this.compatibility==Compatibility.Metastock)) ) {
         savePrevValue = prevValue;
         prevGain = 0.0;
         prevLoss = 0.0;
         for( i = optInTimePeriod; (i>0); i -= 1 ) {
            tempValue1 = ((double)inReal[today]);
            today = (today+1);
            tempValue2 = (tempValue1-prevValue);
            prevValue = tempValue1;
            if( (tempValue2<0.0) ) {
               prevLoss -= tempValue2;
            } else {
               prevGain += tempValue2;
            }
         }
         tempValue1 = (prevLoss/((double)optInTimePeriod));
         tempValue2 = (prevGain/((double)optInTimePeriod));
         tempValue1 = (tempValue2+tempValue1);
         if( !(((-0.00000000000001 < tempValue1) && (tempValue1 < 0.00000000000001))) ) {
            outReal[outIdx] = (100.0*(tempValue2/tempValue1));
            outIdx = (outIdx+1);
         } else {
            outReal[outIdx] = 0.0;
            outIdx = (outIdx+1);
         }
         if( (today>endIdx) ) {
            outBegIdx.value = startIdx;
            outNBElement.value = outIdx;
            return RetCode.Success ;
         }
         today = (today-((int)optInTimePeriod));
         prevValue = savePrevValue;
      }
      prevGain = 0.0;
      prevLoss = 0.0;
      today = (today+1);
      for( i = optInTimePeriod; (i>0); i -= 1 ) {
         tempValue1 = ((double)inReal[today]);
         today = (today+1);
         tempValue2 = (tempValue1-prevValue);
         prevValue = tempValue1;
         if( (tempValue2<0.0) ) {
            prevLoss -= tempValue2;
         } else {
            prevGain += tempValue2;
         }
      }
      prevLoss /= ((double)optInTimePeriod);
      prevGain /= ((double)optInTimePeriod);
      if( (today>startIdx) ) {
         tempValue1 = (prevGain+prevLoss);
         if( !(((-0.00000000000001 < tempValue1) && (tempValue1 < 0.00000000000001))) ) {
            outReal[outIdx] = (100.0*(prevGain/tempValue1));
            outIdx = (outIdx+1);
         } else {
            outReal[outIdx] = 0.0;
            outIdx = (outIdx+1);
         }
      } else {
         while( (today<startIdx) ) {
            tempValue1 = ((double)inReal[today]);
            tempValue2 = (tempValue1-prevValue);
            prevValue = tempValue1;
            prevLoss *= ((double)(optInTimePeriod-1));
            prevGain *= ((double)(optInTimePeriod-1));
            if( (tempValue2<0.0) ) {
               prevLoss -= tempValue2;
            } else {
               prevGain += tempValue2;
            }
            prevLoss /= ((double)optInTimePeriod);
            prevGain /= ((double)optInTimePeriod);
            today = (today+1);
         }
      }
      while( (today<=endIdx) ) {
         tempValue1 = ((double)inReal[today]);
         today = (today+1);
         tempValue2 = (tempValue1-prevValue);
         prevValue = tempValue1;
         prevLoss *= ((double)(optInTimePeriod-1));
         prevGain *= ((double)(optInTimePeriod-1));
         if( (tempValue2<0.0) ) {
            prevLoss -= tempValue2;
         } else {
            prevGain += tempValue2;
         }
         prevLoss /= ((double)optInTimePeriod);
         prevGain /= ((double)optInTimePeriod);
         tempValue1 = (prevGain+prevLoss);
         if( !(((-0.00000000000001 < tempValue1) && (tempValue1 < 0.00000000000001))) ) {
            outReal[outIdx] = (100.0*(prevGain/tempValue1));
            outIdx = (outIdx+1);
         } else {
            outReal[outIdx] = 0.0;
            outIdx = (outIdx+1);
         }
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
