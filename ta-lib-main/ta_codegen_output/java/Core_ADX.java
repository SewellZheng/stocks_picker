/* Generated */
   public int adxLookback( int optInTimePeriod )
   {
      return (((2*optInTimePeriod)+this.unstablePeriod[FuncUnstId.Adx.ordinal()])-1) ;

   }
   public RetCode adx( int startIdx,
                       int endIdx,
                       double inHigh[],
                       double inLow[],
                       double inClose[],
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
      double prevClose = 0;
      double prevMinusDM = 0;
      double prevPlusDM = 0;
      double prevTR = 0;
      double tempReal = 0;
      double tempReal2 = 0;
      double diffP = 0;
      double diffM = 0;
      double minusDI = 0;
      double plusDI = 0;
      double sumDX = 0;
      double prevADX = 0;
      int i = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackTotal = (((2*optInTimePeriod)+this.unstablePeriod[FuncUnstId.Adx.ordinal()])-1);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outIdx = 0;
      today = startIdx;
      outBegIdx.value = today;
      prevMinusDM = 0.0;
      prevPlusDM = 0.0;
      prevTR = 0.0;
      today = (startIdx-lookbackTotal);
      prevHigh = inHigh[today];
      prevLow = inLow[today];
      prevClose = inClose[today];
      i = (optInTimePeriod-1);
      while( (i-->0) ) {
         today += 1;
         tempReal = inHigh[today];
         diffP = (tempReal-prevHigh);
         prevHigh = tempReal;
         tempReal = inLow[today];
         diffM = (prevLow-tempReal);
         prevLow = tempReal;
         if( ((diffM>0)&&(diffP<diffM)) ) {
            prevMinusDM += diffM;
         } else if( ((diffP>0)&&(diffP>diffM)) ) {
            prevPlusDM += diffP;
         }
         double _true_range_0;
         double range_0 = (prevHigh-prevLow);
         double tmp_0 = Math.abs((prevHigh-prevClose));
         if( (tmp_0>range_0) ) {
            range_0 = tmp_0;
         }
         tmp_0 = Math.abs((prevLow-prevClose));
         if( (tmp_0>range_0) ) {
            range_0 = tmp_0;
         }
         _true_range_0 = range_0;
         tempReal = _true_range_0;
         prevTR += tempReal;
         prevClose = inClose[today];
      }
      sumDX = 0.0;
      i = optInTimePeriod;
      while( (i-->0) ) {
         today += 1;
         tempReal = inHigh[today];
         diffP = (tempReal-prevHigh);
         prevHigh = tempReal;
         tempReal = inLow[today];
         diffM = (prevLow-tempReal);
         prevLow = tempReal;
         prevMinusDM -= (prevMinusDM/optInTimePeriod);
         prevPlusDM -= (prevPlusDM/optInTimePeriod);
         if( ((diffM>0)&&(diffP<diffM)) ) {
            prevMinusDM += diffM;
         } else if( ((diffP>0)&&(diffP>diffM)) ) {
            prevPlusDM += diffP;
         }
         double _true_range_1;
         double range_1 = (prevHigh-prevLow);
         double tmp_1 = Math.abs((prevHigh-prevClose));
         if( (tmp_1>range_1) ) {
            range_1 = tmp_1;
         }
         tmp_1 = Math.abs((prevLow-prevClose));
         if( (tmp_1>range_1) ) {
            range_1 = tmp_1;
         }
         _true_range_1 = range_1;
         tempReal = _true_range_1;
         prevTR = ((prevTR-(prevTR/optInTimePeriod))+tempReal);
         prevClose = inClose[today];
         if( !(((-0.00000000000001 < prevTR) && (prevTR < 0.00000000000001))) ) {
            minusDI = (100.0*(prevMinusDM/prevTR));
            plusDI = (100.0*(prevPlusDM/prevTR));
            tempReal = (minusDI+plusDI);
            if( !(((-0.00000000000001 < tempReal) && (tempReal < 0.00000000000001))) ) {
               sumDX += (100.0*(Math.abs((minusDI-plusDI))/tempReal));
            }
         }
      }
      prevADX = (sumDX/optInTimePeriod);
      i = this.unstablePeriod[FuncUnstId.Adx.ordinal()];
      while( (i-->0) ) {
         today += 1;
         tempReal = inHigh[today];
         diffP = (tempReal-prevHigh);
         prevHigh = tempReal;
         tempReal = inLow[today];
         diffM = (prevLow-tempReal);
         prevLow = tempReal;
         prevMinusDM -= (prevMinusDM/optInTimePeriod);
         prevPlusDM -= (prevPlusDM/optInTimePeriod);
         if( ((diffM>0)&&(diffP<diffM)) ) {
            prevMinusDM += diffM;
         } else if( ((diffP>0)&&(diffP>diffM)) ) {
            prevPlusDM += diffP;
         }
         double _true_range_2;
         double range_2 = (prevHigh-prevLow);
         double tmp_2 = Math.abs((prevHigh-prevClose));
         if( (tmp_2>range_2) ) {
            range_2 = tmp_2;
         }
         tmp_2 = Math.abs((prevLow-prevClose));
         if( (tmp_2>range_2) ) {
            range_2 = tmp_2;
         }
         _true_range_2 = range_2;
         tempReal = _true_range_2;
         prevTR = ((prevTR-(prevTR/optInTimePeriod))+tempReal);
         prevClose = inClose[today];
         if( !(((-0.00000000000001 < prevTR) && (prevTR < 0.00000000000001))) ) {
            minusDI = (100.0*(prevMinusDM/prevTR));
            plusDI = (100.0*(prevPlusDM/prevTR));
            tempReal = (minusDI+plusDI);
            if( !(((-0.00000000000001 < tempReal) && (tempReal < 0.00000000000001))) ) {
               tempReal = (100.0*(Math.abs((minusDI-plusDI))/tempReal));
               prevADX = (((prevADX*(optInTimePeriod-1))+tempReal)/optInTimePeriod);
            }
         }
      }
      outReal[0] = prevADX;
      outIdx = 1;
      while( (today<endIdx) ) {
         today += 1;
         tempReal = inHigh[today];
         diffP = (tempReal-prevHigh);
         prevHigh = tempReal;
         tempReal = inLow[today];
         diffM = (prevLow-tempReal);
         prevLow = tempReal;
         prevMinusDM -= (prevMinusDM/optInTimePeriod);
         prevPlusDM -= (prevPlusDM/optInTimePeriod);
         if( ((diffM>0)&&(diffP<diffM)) ) {
            prevMinusDM += diffM;
         } else if( ((diffP>0)&&(diffP>diffM)) ) {
            prevPlusDM += diffP;
         }
         double _true_range_3;
         double range_3 = (prevHigh-prevLow);
         double tmp_3 = Math.abs((prevHigh-prevClose));
         if( (tmp_3>range_3) ) {
            range_3 = tmp_3;
         }
         tmp_3 = Math.abs((prevLow-prevClose));
         if( (tmp_3>range_3) ) {
            range_3 = tmp_3;
         }
         _true_range_3 = range_3;
         tempReal = _true_range_3;
         prevTR = ((prevTR-(prevTR/optInTimePeriod))+tempReal);
         prevClose = inClose[today];
         if( !(((-0.00000000000001 < prevTR) && (prevTR < 0.00000000000001))) ) {
            minusDI = (100.0*(prevMinusDM/prevTR));
            plusDI = (100.0*(prevPlusDM/prevTR));
            tempReal = (minusDI+plusDI);
            if( !(((-0.00000000000001 < tempReal) && (tempReal < 0.00000000000001))) ) {
               tempReal = (100.0*(Math.abs((minusDI-plusDI))/tempReal));
               prevADX = (((prevADX*(optInTimePeriod-1))+tempReal)/optInTimePeriod);
            }
         }
         outReal[outIdx++] = prevADX;
      }
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode adxLogic( int startIdx,
                            int endIdx,
                            double inHigh[],
                            double inLow[],
                            double inClose[],
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
      double prevClose = 0;
      double prevMinusDM = 0;
      double prevPlusDM = 0;
      double prevTR = 0;
      double tempReal = 0;
      double tempReal2 = 0;
      double diffP = 0;
      double diffM = 0;
      double minusDI = 0;
      double plusDI = 0;
      double sumDX = 0;
      double prevADX = 0;
      int i = 0;
      lookbackTotal = (((2*optInTimePeriod)+this.unstablePeriod[FuncUnstId.Adx.ordinal()])-1);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outIdx = 0;
      today = startIdx;
      outBegIdx.value = today;
      prevMinusDM = 0.0;
      prevPlusDM = 0.0;
      prevTR = 0.0;
      today = (startIdx-lookbackTotal);
      prevHigh = inHigh[today];
      prevLow = inLow[today];
      prevClose = inClose[today];
      i = (optInTimePeriod-1);
      while( (i-->0) ) {
         today += 1;
         tempReal = inHigh[today];
         diffP = (tempReal-prevHigh);
         prevHigh = tempReal;
         tempReal = inLow[today];
         diffM = (prevLow-tempReal);
         prevLow = tempReal;
         if( ((diffM>0)&&(diffP<diffM)) ) {
            prevMinusDM += diffM;
         } else if( ((diffP>0)&&(diffP>diffM)) ) {
            prevPlusDM += diffP;
         }
         double _true_range_0;
         double range_0 = (prevHigh-prevLow);
         double tmp_0 = Math.abs((prevHigh-prevClose));
         if( (tmp_0>range_0) ) {
            range_0 = tmp_0;
         }
         tmp_0 = Math.abs((prevLow-prevClose));
         if( (tmp_0>range_0) ) {
            range_0 = tmp_0;
         }
         _true_range_0 = range_0;
         tempReal = _true_range_0;
         prevTR += tempReal;
         prevClose = inClose[today];
      }
      sumDX = 0.0;
      i = optInTimePeriod;
      while( (i-->0) ) {
         today += 1;
         tempReal = inHigh[today];
         diffP = (tempReal-prevHigh);
         prevHigh = tempReal;
         tempReal = inLow[today];
         diffM = (prevLow-tempReal);
         prevLow = tempReal;
         prevMinusDM -= (prevMinusDM/optInTimePeriod);
         prevPlusDM -= (prevPlusDM/optInTimePeriod);
         if( ((diffM>0)&&(diffP<diffM)) ) {
            prevMinusDM += diffM;
         } else if( ((diffP>0)&&(diffP>diffM)) ) {
            prevPlusDM += diffP;
         }
         double _true_range_1;
         double range_1 = (prevHigh-prevLow);
         double tmp_1 = Math.abs((prevHigh-prevClose));
         if( (tmp_1>range_1) ) {
            range_1 = tmp_1;
         }
         tmp_1 = Math.abs((prevLow-prevClose));
         if( (tmp_1>range_1) ) {
            range_1 = tmp_1;
         }
         _true_range_1 = range_1;
         tempReal = _true_range_1;
         prevTR = ((prevTR-(prevTR/optInTimePeriod))+tempReal);
         prevClose = inClose[today];
         if( !(((-0.00000000000001 < prevTR) && (prevTR < 0.00000000000001))) ) {
            minusDI = (100.0*(prevMinusDM/prevTR));
            plusDI = (100.0*(prevPlusDM/prevTR));
            tempReal = (minusDI+plusDI);
            if( !(((-0.00000000000001 < tempReal) && (tempReal < 0.00000000000001))) ) {
               sumDX += (100.0*(Math.abs((minusDI-plusDI))/tempReal));
            }
         }
      }
      prevADX = (sumDX/optInTimePeriod);
      i = this.unstablePeriod[FuncUnstId.Adx.ordinal()];
      while( (i-->0) ) {
         today += 1;
         tempReal = inHigh[today];
         diffP = (tempReal-prevHigh);
         prevHigh = tempReal;
         tempReal = inLow[today];
         diffM = (prevLow-tempReal);
         prevLow = tempReal;
         prevMinusDM -= (prevMinusDM/optInTimePeriod);
         prevPlusDM -= (prevPlusDM/optInTimePeriod);
         if( ((diffM>0)&&(diffP<diffM)) ) {
            prevMinusDM += diffM;
         } else if( ((diffP>0)&&(diffP>diffM)) ) {
            prevPlusDM += diffP;
         }
         double _true_range_2;
         double range_2 = (prevHigh-prevLow);
         double tmp_2 = Math.abs((prevHigh-prevClose));
         if( (tmp_2>range_2) ) {
            range_2 = tmp_2;
         }
         tmp_2 = Math.abs((prevLow-prevClose));
         if( (tmp_2>range_2) ) {
            range_2 = tmp_2;
         }
         _true_range_2 = range_2;
         tempReal = _true_range_2;
         prevTR = ((prevTR-(prevTR/optInTimePeriod))+tempReal);
         prevClose = inClose[today];
         if( !(((-0.00000000000001 < prevTR) && (prevTR < 0.00000000000001))) ) {
            minusDI = (100.0*(prevMinusDM/prevTR));
            plusDI = (100.0*(prevPlusDM/prevTR));
            tempReal = (minusDI+plusDI);
            if( !(((-0.00000000000001 < tempReal) && (tempReal < 0.00000000000001))) ) {
               tempReal = (100.0*(Math.abs((minusDI-plusDI))/tempReal));
               prevADX = (((prevADX*(optInTimePeriod-1))+tempReal)/optInTimePeriod);
            }
         }
      }
      outReal[0] = prevADX;
      outIdx = 1;
      while( (today<endIdx) ) {
         today += 1;
         tempReal = inHigh[today];
         diffP = (tempReal-prevHigh);
         prevHigh = tempReal;
         tempReal = inLow[today];
         diffM = (prevLow-tempReal);
         prevLow = tempReal;
         prevMinusDM -= (prevMinusDM/optInTimePeriod);
         prevPlusDM -= (prevPlusDM/optInTimePeriod);
         if( ((diffM>0)&&(diffP<diffM)) ) {
            prevMinusDM += diffM;
         } else if( ((diffP>0)&&(diffP>diffM)) ) {
            prevPlusDM += diffP;
         }
         double _true_range_3;
         double range_3 = (prevHigh-prevLow);
         double tmp_3 = Math.abs((prevHigh-prevClose));
         if( (tmp_3>range_3) ) {
            range_3 = tmp_3;
         }
         tmp_3 = Math.abs((prevLow-prevClose));
         if( (tmp_3>range_3) ) {
            range_3 = tmp_3;
         }
         _true_range_3 = range_3;
         tempReal = _true_range_3;
         prevTR = ((prevTR-(prevTR/optInTimePeriod))+tempReal);
         prevClose = inClose[today];
         if( !(((-0.00000000000001 < prevTR) && (prevTR < 0.00000000000001))) ) {
            minusDI = (100.0*(prevMinusDM/prevTR));
            plusDI = (100.0*(prevPlusDM/prevTR));
            tempReal = (minusDI+plusDI);
            if( !(((-0.00000000000001 < tempReal) && (tempReal < 0.00000000000001))) ) {
               tempReal = (100.0*(Math.abs((minusDI-plusDI))/tempReal));
               prevADX = (((prevADX*(optInTimePeriod-1))+tempReal)/optInTimePeriod);
            }
         }
         outReal[outIdx++] = prevADX;
      }
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode adx( int startIdx,
                       int endIdx,
                       float inHigh[],
                       float inLow[],
                       float inClose[],
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
      double prevClose = 0;
      double prevMinusDM = 0;
      double prevPlusDM = 0;
      double prevTR = 0;
      double tempReal = 0;
      double tempReal2 = 0;
      double diffP = 0;
      double diffM = 0;
      double minusDI = 0;
      double plusDI = 0;
      double sumDX = 0;
      double prevADX = 0;
      int i = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackTotal = (((2*optInTimePeriod)+this.unstablePeriod[FuncUnstId.Adx.ordinal()])-1);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outIdx = 0;
      today = startIdx;
      outBegIdx.value = today;
      prevMinusDM = 0.0;
      prevPlusDM = 0.0;
      prevTR = 0.0;
      today = (startIdx-lookbackTotal);
      prevHigh = inHigh[today];
      prevLow = inLow[today];
      prevClose = inClose[today];
      i = (optInTimePeriod-1);
      while( (i-->0) ) {
         today += 1;
         tempReal = inHigh[today];
         diffP = (tempReal-prevHigh);
         prevHigh = tempReal;
         tempReal = inLow[today];
         diffM = (prevLow-tempReal);
         prevLow = tempReal;
         if( ((diffM>0)&&(diffP<diffM)) ) {
            prevMinusDM += diffM;
         } else if( ((diffP>0)&&(diffP>diffM)) ) {
            prevPlusDM += diffP;
         }
         double _true_range_0;
         double range_0 = (prevHigh-prevLow);
         double tmp_0 = Math.abs((prevHigh-prevClose));
         if( (tmp_0>range_0) ) {
            range_0 = tmp_0;
         }
         tmp_0 = Math.abs((prevLow-prevClose));
         if( (tmp_0>range_0) ) {
            range_0 = tmp_0;
         }
         _true_range_0 = range_0;
         tempReal = _true_range_0;
         prevTR += tempReal;
         prevClose = inClose[today];
      }
      sumDX = 0.0;
      i = optInTimePeriod;
      while( (i-->0) ) {
         today += 1;
         tempReal = inHigh[today];
         diffP = (tempReal-prevHigh);
         prevHigh = tempReal;
         tempReal = inLow[today];
         diffM = (prevLow-tempReal);
         prevLow = tempReal;
         prevMinusDM -= (prevMinusDM/optInTimePeriod);
         prevPlusDM -= (prevPlusDM/optInTimePeriod);
         if( ((diffM>0)&&(diffP<diffM)) ) {
            prevMinusDM += diffM;
         } else if( ((diffP>0)&&(diffP>diffM)) ) {
            prevPlusDM += diffP;
         }
         double _true_range_1;
         double range_1 = (prevHigh-prevLow);
         double tmp_1 = Math.abs((prevHigh-prevClose));
         if( (tmp_1>range_1) ) {
            range_1 = tmp_1;
         }
         tmp_1 = Math.abs((prevLow-prevClose));
         if( (tmp_1>range_1) ) {
            range_1 = tmp_1;
         }
         _true_range_1 = range_1;
         tempReal = _true_range_1;
         prevTR = ((prevTR-(prevTR/optInTimePeriod))+tempReal);
         prevClose = inClose[today];
         if( !(((-0.00000000000001 < prevTR) && (prevTR < 0.00000000000001))) ) {
            minusDI = (100.0*(prevMinusDM/prevTR));
            plusDI = (100.0*(prevPlusDM/prevTR));
            tempReal = (minusDI+plusDI);
            if( !(((-0.00000000000001 < tempReal) && (tempReal < 0.00000000000001))) ) {
               sumDX += (100.0*(Math.abs((minusDI-plusDI))/tempReal));
            }
         }
      }
      prevADX = (sumDX/optInTimePeriod);
      i = this.unstablePeriod[FuncUnstId.Adx.ordinal()];
      while( (i-->0) ) {
         today += 1;
         tempReal = inHigh[today];
         diffP = (tempReal-prevHigh);
         prevHigh = tempReal;
         tempReal = inLow[today];
         diffM = (prevLow-tempReal);
         prevLow = tempReal;
         prevMinusDM -= (prevMinusDM/optInTimePeriod);
         prevPlusDM -= (prevPlusDM/optInTimePeriod);
         if( ((diffM>0)&&(diffP<diffM)) ) {
            prevMinusDM += diffM;
         } else if( ((diffP>0)&&(diffP>diffM)) ) {
            prevPlusDM += diffP;
         }
         double _true_range_2;
         double range_2 = (prevHigh-prevLow);
         double tmp_2 = Math.abs((prevHigh-prevClose));
         if( (tmp_2>range_2) ) {
            range_2 = tmp_2;
         }
         tmp_2 = Math.abs((prevLow-prevClose));
         if( (tmp_2>range_2) ) {
            range_2 = tmp_2;
         }
         _true_range_2 = range_2;
         tempReal = _true_range_2;
         prevTR = ((prevTR-(prevTR/optInTimePeriod))+tempReal);
         prevClose = inClose[today];
         if( !(((-0.00000000000001 < prevTR) && (prevTR < 0.00000000000001))) ) {
            minusDI = (100.0*(prevMinusDM/prevTR));
            plusDI = (100.0*(prevPlusDM/prevTR));
            tempReal = (minusDI+plusDI);
            if( !(((-0.00000000000001 < tempReal) && (tempReal < 0.00000000000001))) ) {
               tempReal = (100.0*(Math.abs((minusDI-plusDI))/tempReal));
               prevADX = (((prevADX*(optInTimePeriod-1))+tempReal)/optInTimePeriod);
            }
         }
      }
      outReal[0] = prevADX;
      outIdx = 1;
      while( (today<endIdx) ) {
         today += 1;
         tempReal = inHigh[today];
         diffP = (tempReal-prevHigh);
         prevHigh = tempReal;
         tempReal = inLow[today];
         diffM = (prevLow-tempReal);
         prevLow = tempReal;
         prevMinusDM -= (prevMinusDM/optInTimePeriod);
         prevPlusDM -= (prevPlusDM/optInTimePeriod);
         if( ((diffM>0)&&(diffP<diffM)) ) {
            prevMinusDM += diffM;
         } else if( ((diffP>0)&&(diffP>diffM)) ) {
            prevPlusDM += diffP;
         }
         double _true_range_3;
         double range_3 = (prevHigh-prevLow);
         double tmp_3 = Math.abs((prevHigh-prevClose));
         if( (tmp_3>range_3) ) {
            range_3 = tmp_3;
         }
         tmp_3 = Math.abs((prevLow-prevClose));
         if( (tmp_3>range_3) ) {
            range_3 = tmp_3;
         }
         _true_range_3 = range_3;
         tempReal = _true_range_3;
         prevTR = ((prevTR-(prevTR/optInTimePeriod))+tempReal);
         prevClose = inClose[today];
         if( !(((-0.00000000000001 < prevTR) && (prevTR < 0.00000000000001))) ) {
            minusDI = (100.0*(prevMinusDM/prevTR));
            plusDI = (100.0*(prevPlusDM/prevTR));
            tempReal = (minusDI+plusDI);
            if( !(((-0.00000000000001 < tempReal) && (tempReal < 0.00000000000001))) ) {
               tempReal = (100.0*(Math.abs((minusDI-plusDI))/tempReal));
               prevADX = (((prevADX*(optInTimePeriod-1))+tempReal)/optInTimePeriod);
            }
         }
         outReal[outIdx++] = prevADX;
      }
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode adxLogic( int startIdx,
                            int endIdx,
                            float inHigh[],
                            float inLow[],
                            float inClose[],
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
      double prevClose = 0;
      double prevMinusDM = 0;
      double prevPlusDM = 0;
      double prevTR = 0;
      double tempReal = 0;
      double tempReal2 = 0;
      double diffP = 0;
      double diffM = 0;
      double minusDI = 0;
      double plusDI = 0;
      double sumDX = 0;
      double prevADX = 0;
      int i = 0;
      lookbackTotal = (((2*optInTimePeriod)+this.unstablePeriod[FuncUnstId.Adx.ordinal()])-1);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outIdx = 0;
      today = startIdx;
      outBegIdx.value = today;
      prevMinusDM = 0.0;
      prevPlusDM = 0.0;
      prevTR = 0.0;
      today = (startIdx-lookbackTotal);
      prevHigh = inHigh[today];
      prevLow = inLow[today];
      prevClose = inClose[today];
      i = (optInTimePeriod-1);
      while( (i-->0) ) {
         today += 1;
         tempReal = inHigh[today];
         diffP = (tempReal-prevHigh);
         prevHigh = tempReal;
         tempReal = inLow[today];
         diffM = (prevLow-tempReal);
         prevLow = tempReal;
         if( ((diffM>0)&&(diffP<diffM)) ) {
            prevMinusDM += diffM;
         } else if( ((diffP>0)&&(diffP>diffM)) ) {
            prevPlusDM += diffP;
         }
         double _true_range_0;
         double range_0 = (prevHigh-prevLow);
         double tmp_0 = Math.abs((prevHigh-prevClose));
         if( (tmp_0>range_0) ) {
            range_0 = tmp_0;
         }
         tmp_0 = Math.abs((prevLow-prevClose));
         if( (tmp_0>range_0) ) {
            range_0 = tmp_0;
         }
         _true_range_0 = range_0;
         tempReal = _true_range_0;
         prevTR += tempReal;
         prevClose = inClose[today];
      }
      sumDX = 0.0;
      i = optInTimePeriod;
      while( (i-->0) ) {
         today += 1;
         tempReal = inHigh[today];
         diffP = (tempReal-prevHigh);
         prevHigh = tempReal;
         tempReal = inLow[today];
         diffM = (prevLow-tempReal);
         prevLow = tempReal;
         prevMinusDM -= (prevMinusDM/optInTimePeriod);
         prevPlusDM -= (prevPlusDM/optInTimePeriod);
         if( ((diffM>0)&&(diffP<diffM)) ) {
            prevMinusDM += diffM;
         } else if( ((diffP>0)&&(diffP>diffM)) ) {
            prevPlusDM += diffP;
         }
         double _true_range_1;
         double range_1 = (prevHigh-prevLow);
         double tmp_1 = Math.abs((prevHigh-prevClose));
         if( (tmp_1>range_1) ) {
            range_1 = tmp_1;
         }
         tmp_1 = Math.abs((prevLow-prevClose));
         if( (tmp_1>range_1) ) {
            range_1 = tmp_1;
         }
         _true_range_1 = range_1;
         tempReal = _true_range_1;
         prevTR = ((prevTR-(prevTR/optInTimePeriod))+tempReal);
         prevClose = inClose[today];
         if( !(((-0.00000000000001 < prevTR) && (prevTR < 0.00000000000001))) ) {
            minusDI = (100.0*(prevMinusDM/prevTR));
            plusDI = (100.0*(prevPlusDM/prevTR));
            tempReal = (minusDI+plusDI);
            if( !(((-0.00000000000001 < tempReal) && (tempReal < 0.00000000000001))) ) {
               sumDX += (100.0*(Math.abs((minusDI-plusDI))/tempReal));
            }
         }
      }
      prevADX = (sumDX/optInTimePeriod);
      i = this.unstablePeriod[FuncUnstId.Adx.ordinal()];
      while( (i-->0) ) {
         today += 1;
         tempReal = inHigh[today];
         diffP = (tempReal-prevHigh);
         prevHigh = tempReal;
         tempReal = inLow[today];
         diffM = (prevLow-tempReal);
         prevLow = tempReal;
         prevMinusDM -= (prevMinusDM/optInTimePeriod);
         prevPlusDM -= (prevPlusDM/optInTimePeriod);
         if( ((diffM>0)&&(diffP<diffM)) ) {
            prevMinusDM += diffM;
         } else if( ((diffP>0)&&(diffP>diffM)) ) {
            prevPlusDM += diffP;
         }
         double _true_range_2;
         double range_2 = (prevHigh-prevLow);
         double tmp_2 = Math.abs((prevHigh-prevClose));
         if( (tmp_2>range_2) ) {
            range_2 = tmp_2;
         }
         tmp_2 = Math.abs((prevLow-prevClose));
         if( (tmp_2>range_2) ) {
            range_2 = tmp_2;
         }
         _true_range_2 = range_2;
         tempReal = _true_range_2;
         prevTR = ((prevTR-(prevTR/optInTimePeriod))+tempReal);
         prevClose = inClose[today];
         if( !(((-0.00000000000001 < prevTR) && (prevTR < 0.00000000000001))) ) {
            minusDI = (100.0*(prevMinusDM/prevTR));
            plusDI = (100.0*(prevPlusDM/prevTR));
            tempReal = (minusDI+plusDI);
            if( !(((-0.00000000000001 < tempReal) && (tempReal < 0.00000000000001))) ) {
               tempReal = (100.0*(Math.abs((minusDI-plusDI))/tempReal));
               prevADX = (((prevADX*(optInTimePeriod-1))+tempReal)/optInTimePeriod);
            }
         }
      }
      outReal[0] = prevADX;
      outIdx = 1;
      while( (today<endIdx) ) {
         today += 1;
         tempReal = inHigh[today];
         diffP = (tempReal-prevHigh);
         prevHigh = tempReal;
         tempReal = inLow[today];
         diffM = (prevLow-tempReal);
         prevLow = tempReal;
         prevMinusDM -= (prevMinusDM/optInTimePeriod);
         prevPlusDM -= (prevPlusDM/optInTimePeriod);
         if( ((diffM>0)&&(diffP<diffM)) ) {
            prevMinusDM += diffM;
         } else if( ((diffP>0)&&(diffP>diffM)) ) {
            prevPlusDM += diffP;
         }
         double _true_range_3;
         double range_3 = (prevHigh-prevLow);
         double tmp_3 = Math.abs((prevHigh-prevClose));
         if( (tmp_3>range_3) ) {
            range_3 = tmp_3;
         }
         tmp_3 = Math.abs((prevLow-prevClose));
         if( (tmp_3>range_3) ) {
            range_3 = tmp_3;
         }
         _true_range_3 = range_3;
         tempReal = _true_range_3;
         prevTR = ((prevTR-(prevTR/optInTimePeriod))+tempReal);
         prevClose = inClose[today];
         if( !(((-0.00000000000001 < prevTR) && (prevTR < 0.00000000000001))) ) {
            minusDI = (100.0*(prevMinusDM/prevTR));
            plusDI = (100.0*(prevPlusDM/prevTR));
            tempReal = (minusDI+plusDI);
            if( !(((-0.00000000000001 < tempReal) && (tempReal < 0.00000000000001))) ) {
               tempReal = (100.0*(Math.abs((minusDI-plusDI))/tempReal));
               prevADX = (((prevADX*(optInTimePeriod-1))+tempReal)/optInTimePeriod);
            }
         }
         outReal[outIdx++] = prevADX;
      }
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
