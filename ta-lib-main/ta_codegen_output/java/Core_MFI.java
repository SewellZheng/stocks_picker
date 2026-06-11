/* Generated */
   public int mfiLookback( int optInTimePeriod )
   {
      return (optInTimePeriod+this.unstablePeriod[FuncUnstId.Mfi.ordinal()]) ;

   }
   public RetCode mfi( int startIdx,
                       int endIdx,
                       double inHigh[],
                       double inLow[],
                       double inClose[],
                       double inVolume[],
                       int optInTimePeriod,
                       MInteger outBegIdx,
                       MInteger outNBElement,
                       double outReal[] )
   {
      double posSumMF = 0;
      double negSumMF = 0;
      double prevValue = 0;
      double tempValue1 = 0;
      double tempValue2 = 0;
      int lookbackTotal = 0;
      int outIdx = 0;
      int i = 0;
      int today = 0;
      double[] mflow_positive = new double[50];
      double[] mflow_negative = new double[50];
      int mflow_Idx = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      mflow_Idx = 0;
      java.util.Arrays.fill(mflow_positive, 0, (int)((optInTimePeriod*1)), 0.0);
      java.util.Arrays.fill(mflow_negative, 0, (int)((optInTimePeriod*1)), 0.0);
      outBegIdx.value = 0;
      outNBElement.value = 0;
      lookbackTotal = (optInTimePeriod+this.unstablePeriod[FuncUnstId.Mfi.ordinal()]);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         return RetCode.Success ;
      }
      outIdx = 0;
      today = (startIdx-lookbackTotal);
      prevValue = (((inHigh[today]+inLow[today])+inClose[today])/3.0);
      posSumMF = 0.0;
      negSumMF = 0.0;
      today += 1;
      for( i = optInTimePeriod; (i>0); i -= 1 ) {
         tempValue1 = (((inHigh[today]+inLow[today])+inClose[today])/3.0);
         tempValue2 = (tempValue1-prevValue);
         prevValue = tempValue1;
         tempValue1 *= inVolume[today++];
         if( (tempValue2<0) ) {
            mflow_negative[mflow_Idx] = tempValue1;
            negSumMF += tempValue1;
            mflow_positive[mflow_Idx] = 0.0;
         } else if( (tempValue2>0) ) {
            mflow_positive[mflow_Idx] = tempValue1;
            posSumMF += tempValue1;
            mflow_negative[mflow_Idx] = 0.0;
         } else {
            mflow_positive[mflow_Idx] = 0.0;
            mflow_negative[mflow_Idx] = 0.0;
         }
         mflow_Idx = ((mflow_Idx+1)%optInTimePeriod);
      }
      if( (today>startIdx) ) {
         tempValue1 = (posSumMF+negSumMF);
         if( (tempValue1<1.0) ) {
            outReal[outIdx++] = 0.0;
         } else {
            outReal[outIdx++] = (100.0*(posSumMF/tempValue1));
         }
      } else {
         while( (today<startIdx) ) {
            posSumMF -= mflow_positive[mflow_Idx];
            negSumMF -= mflow_negative[mflow_Idx];
            tempValue1 = (((inHigh[today]+inLow[today])+inClose[today])/3.0);
            tempValue2 = (tempValue1-prevValue);
            prevValue = tempValue1;
            tempValue1 *= inVolume[today++];
            if( (tempValue2<0) ) {
               mflow_negative[mflow_Idx] = tempValue1;
               negSumMF += tempValue1;
               mflow_positive[mflow_Idx] = 0.0;
            } else if( (tempValue2>0) ) {
               mflow_positive[mflow_Idx] = tempValue1;
               posSumMF += tempValue1;
               mflow_negative[mflow_Idx] = 0.0;
            } else {
               mflow_positive[mflow_Idx] = 0.0;
               mflow_negative[mflow_Idx] = 0.0;
            }
            mflow_Idx = ((mflow_Idx+1)%optInTimePeriod);
         }
      }
      while( (today<=endIdx) ) {
         posSumMF -= mflow_positive[mflow_Idx];
         negSumMF -= mflow_negative[mflow_Idx];
         tempValue1 = (((inHigh[today]+inLow[today])+inClose[today])/3.0);
         tempValue2 = (tempValue1-prevValue);
         prevValue = tempValue1;
         tempValue1 *= inVolume[today++];
         if( (tempValue2<0) ) {
            mflow_negative[mflow_Idx] = tempValue1;
            negSumMF += tempValue1;
            mflow_positive[mflow_Idx] = 0.0;
         } else if( (tempValue2>0) ) {
            mflow_positive[mflow_Idx] = tempValue1;
            posSumMF += tempValue1;
            mflow_negative[mflow_Idx] = 0.0;
         } else {
            mflow_positive[mflow_Idx] = 0.0;
            mflow_negative[mflow_Idx] = 0.0;
         }
         tempValue1 = (posSumMF+negSumMF);
         if( (tempValue1<1.0) ) {
            outReal[outIdx++] = 0.0;
         } else {
            outReal[outIdx++] = (100.0*(posSumMF/tempValue1));
         }
         mflow_Idx = ((mflow_Idx+1)%optInTimePeriod);
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode mfiLogic( int startIdx,
                            int endIdx,
                            double inHigh[],
                            double inLow[],
                            double inClose[],
                            double inVolume[],
                            int optInTimePeriod,
                            MInteger outBegIdx,
                            MInteger outNBElement,
                            double outReal[] )
   {
      double posSumMF = 0;
      double negSumMF = 0;
      double prevValue = 0;
      double tempValue1 = 0;
      double tempValue2 = 0;
      int lookbackTotal = 0;
      int outIdx = 0;
      int i = 0;
      int today = 0;
      double[] mflow_positive = new double[50];
      double[] mflow_negative = new double[50];
      int mflow_Idx = 0;
      mflow_Idx = 0;
      java.util.Arrays.fill(mflow_positive, 0, (int)((optInTimePeriod*1)), 0.0);
      java.util.Arrays.fill(mflow_negative, 0, (int)((optInTimePeriod*1)), 0.0);
      outBegIdx.value = 0;
      outNBElement.value = 0;
      lookbackTotal = (optInTimePeriod+this.unstablePeriod[FuncUnstId.Mfi.ordinal()]);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         return RetCode.Success ;
      }
      outIdx = 0;
      today = (startIdx-lookbackTotal);
      prevValue = (((inHigh[today]+inLow[today])+inClose[today])/3.0);
      posSumMF = 0.0;
      negSumMF = 0.0;
      today += 1;
      for( i = optInTimePeriod; (i>0); i -= 1 ) {
         tempValue1 = (((inHigh[today]+inLow[today])+inClose[today])/3.0);
         tempValue2 = (tempValue1-prevValue);
         prevValue = tempValue1;
         tempValue1 *= inVolume[today++];
         if( (tempValue2<0) ) {
            mflow_negative[mflow_Idx] = tempValue1;
            negSumMF += tempValue1;
            mflow_positive[mflow_Idx] = 0.0;
         } else if( (tempValue2>0) ) {
            mflow_positive[mflow_Idx] = tempValue1;
            posSumMF += tempValue1;
            mflow_negative[mflow_Idx] = 0.0;
         } else {
            mflow_positive[mflow_Idx] = 0.0;
            mflow_negative[mflow_Idx] = 0.0;
         }
         mflow_Idx = ((mflow_Idx+1)%optInTimePeriod);
      }
      if( (today>startIdx) ) {
         tempValue1 = (posSumMF+negSumMF);
         if( (tempValue1<1.0) ) {
            outReal[outIdx++] = 0.0;
         } else {
            outReal[outIdx++] = (100.0*(posSumMF/tempValue1));
         }
      } else {
         while( (today<startIdx) ) {
            posSumMF -= mflow_positive[mflow_Idx];
            negSumMF -= mflow_negative[mflow_Idx];
            tempValue1 = (((inHigh[today]+inLow[today])+inClose[today])/3.0);
            tempValue2 = (tempValue1-prevValue);
            prevValue = tempValue1;
            tempValue1 *= inVolume[today++];
            if( (tempValue2<0) ) {
               mflow_negative[mflow_Idx] = tempValue1;
               negSumMF += tempValue1;
               mflow_positive[mflow_Idx] = 0.0;
            } else if( (tempValue2>0) ) {
               mflow_positive[mflow_Idx] = tempValue1;
               posSumMF += tempValue1;
               mflow_negative[mflow_Idx] = 0.0;
            } else {
               mflow_positive[mflow_Idx] = 0.0;
               mflow_negative[mflow_Idx] = 0.0;
            }
            mflow_Idx = ((mflow_Idx+1)%optInTimePeriod);
         }
      }
      while( (today<=endIdx) ) {
         posSumMF -= mflow_positive[mflow_Idx];
         negSumMF -= mflow_negative[mflow_Idx];
         tempValue1 = (((inHigh[today]+inLow[today])+inClose[today])/3.0);
         tempValue2 = (tempValue1-prevValue);
         prevValue = tempValue1;
         tempValue1 *= inVolume[today++];
         if( (tempValue2<0) ) {
            mflow_negative[mflow_Idx] = tempValue1;
            negSumMF += tempValue1;
            mflow_positive[mflow_Idx] = 0.0;
         } else if( (tempValue2>0) ) {
            mflow_positive[mflow_Idx] = tempValue1;
            posSumMF += tempValue1;
            mflow_negative[mflow_Idx] = 0.0;
         } else {
            mflow_positive[mflow_Idx] = 0.0;
            mflow_negative[mflow_Idx] = 0.0;
         }
         tempValue1 = (posSumMF+negSumMF);
         if( (tempValue1<1.0) ) {
            outReal[outIdx++] = 0.0;
         } else {
            outReal[outIdx++] = (100.0*(posSumMF/tempValue1));
         }
         mflow_Idx = ((mflow_Idx+1)%optInTimePeriod);
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode mfi( int startIdx,
                       int endIdx,
                       float inHigh[],
                       float inLow[],
                       float inClose[],
                       float inVolume[],
                       int optInTimePeriod,
                       MInteger outBegIdx,
                       MInteger outNBElement,
                       double outReal[] )
   {
      double posSumMF = 0;
      double negSumMF = 0;
      double prevValue = 0;
      double tempValue1 = 0;
      double tempValue2 = 0;
      int lookbackTotal = 0;
      int outIdx = 0;
      int i = 0;
      int today = 0;
      double[] mflow_positive = new double[50];
      double[] mflow_negative = new double[50];
      int mflow_Idx = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      mflow_Idx = 0;
      java.util.Arrays.fill(mflow_positive, 0, (int)((optInTimePeriod*1)), 0.0);
      java.util.Arrays.fill(mflow_negative, 0, (int)((optInTimePeriod*1)), 0.0);
      outBegIdx.value = 0;
      outNBElement.value = 0;
      lookbackTotal = (optInTimePeriod+this.unstablePeriod[FuncUnstId.Mfi.ordinal()]);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         return RetCode.Success ;
      }
      outIdx = 0;
      today = (startIdx-lookbackTotal);
      prevValue = (((inHigh[today]+inLow[today])+inClose[today])/3.0);
      posSumMF = 0.0;
      negSumMF = 0.0;
      today += 1;
      for( i = optInTimePeriod; (i>0); i -= 1 ) {
         tempValue1 = (((inHigh[today]+inLow[today])+inClose[today])/3.0);
         tempValue2 = (tempValue1-prevValue);
         prevValue = tempValue1;
         tempValue1 *= inVolume[today++];
         if( (tempValue2<0) ) {
            mflow_negative[mflow_Idx] = tempValue1;
            negSumMF += tempValue1;
            mflow_positive[mflow_Idx] = 0.0;
         } else if( (tempValue2>0) ) {
            mflow_positive[mflow_Idx] = tempValue1;
            posSumMF += tempValue1;
            mflow_negative[mflow_Idx] = 0.0;
         } else {
            mflow_positive[mflow_Idx] = 0.0;
            mflow_negative[mflow_Idx] = 0.0;
         }
         mflow_Idx = ((mflow_Idx+1)%optInTimePeriod);
      }
      if( (today>startIdx) ) {
         tempValue1 = (posSumMF+negSumMF);
         if( (tempValue1<1.0) ) {
            outReal[outIdx++] = 0.0;
         } else {
            outReal[outIdx++] = (100.0*(posSumMF/tempValue1));
         }
      } else {
         while( (today<startIdx) ) {
            posSumMF -= mflow_positive[mflow_Idx];
            negSumMF -= mflow_negative[mflow_Idx];
            tempValue1 = (((inHigh[today]+inLow[today])+inClose[today])/3.0);
            tempValue2 = (tempValue1-prevValue);
            prevValue = tempValue1;
            tempValue1 *= inVolume[today++];
            if( (tempValue2<0) ) {
               mflow_negative[mflow_Idx] = tempValue1;
               negSumMF += tempValue1;
               mflow_positive[mflow_Idx] = 0.0;
            } else if( (tempValue2>0) ) {
               mflow_positive[mflow_Idx] = tempValue1;
               posSumMF += tempValue1;
               mflow_negative[mflow_Idx] = 0.0;
            } else {
               mflow_positive[mflow_Idx] = 0.0;
               mflow_negative[mflow_Idx] = 0.0;
            }
            mflow_Idx = ((mflow_Idx+1)%optInTimePeriod);
         }
      }
      while( (today<=endIdx) ) {
         posSumMF -= mflow_positive[mflow_Idx];
         negSumMF -= mflow_negative[mflow_Idx];
         tempValue1 = (((inHigh[today]+inLow[today])+inClose[today])/3.0);
         tempValue2 = (tempValue1-prevValue);
         prevValue = tempValue1;
         tempValue1 *= inVolume[today++];
         if( (tempValue2<0) ) {
            mflow_negative[mflow_Idx] = tempValue1;
            negSumMF += tempValue1;
            mflow_positive[mflow_Idx] = 0.0;
         } else if( (tempValue2>0) ) {
            mflow_positive[mflow_Idx] = tempValue1;
            posSumMF += tempValue1;
            mflow_negative[mflow_Idx] = 0.0;
         } else {
            mflow_positive[mflow_Idx] = 0.0;
            mflow_negative[mflow_Idx] = 0.0;
         }
         tempValue1 = (posSumMF+negSumMF);
         if( (tempValue1<1.0) ) {
            outReal[outIdx++] = 0.0;
         } else {
            outReal[outIdx++] = (100.0*(posSumMF/tempValue1));
         }
         mflow_Idx = ((mflow_Idx+1)%optInTimePeriod);
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode mfiLogic( int startIdx,
                            int endIdx,
                            float inHigh[],
                            float inLow[],
                            float inClose[],
                            float inVolume[],
                            int optInTimePeriod,
                            MInteger outBegIdx,
                            MInteger outNBElement,
                            double outReal[] )
   {
      double posSumMF = 0;
      double negSumMF = 0;
      double prevValue = 0;
      double tempValue1 = 0;
      double tempValue2 = 0;
      int lookbackTotal = 0;
      int outIdx = 0;
      int i = 0;
      int today = 0;
      double[] mflow_positive = new double[50];
      double[] mflow_negative = new double[50];
      int mflow_Idx = 0;
      mflow_Idx = 0;
      java.util.Arrays.fill(mflow_positive, 0, (int)((optInTimePeriod*1)), 0.0);
      java.util.Arrays.fill(mflow_negative, 0, (int)((optInTimePeriod*1)), 0.0);
      outBegIdx.value = 0;
      outNBElement.value = 0;
      lookbackTotal = (optInTimePeriod+this.unstablePeriod[FuncUnstId.Mfi.ordinal()]);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         return RetCode.Success ;
      }
      outIdx = 0;
      today = (startIdx-lookbackTotal);
      prevValue = (((inHigh[today]+inLow[today])+inClose[today])/3.0);
      posSumMF = 0.0;
      negSumMF = 0.0;
      today += 1;
      for( i = optInTimePeriod; (i>0); i -= 1 ) {
         tempValue1 = (((inHigh[today]+inLow[today])+inClose[today])/3.0);
         tempValue2 = (tempValue1-prevValue);
         prevValue = tempValue1;
         tempValue1 *= inVolume[today++];
         if( (tempValue2<0) ) {
            mflow_negative[mflow_Idx] = tempValue1;
            negSumMF += tempValue1;
            mflow_positive[mflow_Idx] = 0.0;
         } else if( (tempValue2>0) ) {
            mflow_positive[mflow_Idx] = tempValue1;
            posSumMF += tempValue1;
            mflow_negative[mflow_Idx] = 0.0;
         } else {
            mflow_positive[mflow_Idx] = 0.0;
            mflow_negative[mflow_Idx] = 0.0;
         }
         mflow_Idx = ((mflow_Idx+1)%optInTimePeriod);
      }
      if( (today>startIdx) ) {
         tempValue1 = (posSumMF+negSumMF);
         if( (tempValue1<1.0) ) {
            outReal[outIdx++] = 0.0;
         } else {
            outReal[outIdx++] = (100.0*(posSumMF/tempValue1));
         }
      } else {
         while( (today<startIdx) ) {
            posSumMF -= mflow_positive[mflow_Idx];
            negSumMF -= mflow_negative[mflow_Idx];
            tempValue1 = (((inHigh[today]+inLow[today])+inClose[today])/3.0);
            tempValue2 = (tempValue1-prevValue);
            prevValue = tempValue1;
            tempValue1 *= inVolume[today++];
            if( (tempValue2<0) ) {
               mflow_negative[mflow_Idx] = tempValue1;
               negSumMF += tempValue1;
               mflow_positive[mflow_Idx] = 0.0;
            } else if( (tempValue2>0) ) {
               mflow_positive[mflow_Idx] = tempValue1;
               posSumMF += tempValue1;
               mflow_negative[mflow_Idx] = 0.0;
            } else {
               mflow_positive[mflow_Idx] = 0.0;
               mflow_negative[mflow_Idx] = 0.0;
            }
            mflow_Idx = ((mflow_Idx+1)%optInTimePeriod);
         }
      }
      while( (today<=endIdx) ) {
         posSumMF -= mflow_positive[mflow_Idx];
         negSumMF -= mflow_negative[mflow_Idx];
         tempValue1 = (((inHigh[today]+inLow[today])+inClose[today])/3.0);
         tempValue2 = (tempValue1-prevValue);
         prevValue = tempValue1;
         tempValue1 *= inVolume[today++];
         if( (tempValue2<0) ) {
            mflow_negative[mflow_Idx] = tempValue1;
            negSumMF += tempValue1;
            mflow_positive[mflow_Idx] = 0.0;
         } else if( (tempValue2>0) ) {
            mflow_positive[mflow_Idx] = tempValue1;
            posSumMF += tempValue1;
            mflow_negative[mflow_Idx] = 0.0;
         } else {
            mflow_positive[mflow_Idx] = 0.0;
            mflow_negative[mflow_Idx] = 0.0;
         }
         tempValue1 = (posSumMF+negSumMF);
         if( (tempValue1<1.0) ) {
            outReal[outIdx++] = 0.0;
         } else {
            outReal[outIdx++] = (100.0*(posSumMF/tempValue1));
         }
         mflow_Idx = ((mflow_Idx+1)%optInTimePeriod);
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
