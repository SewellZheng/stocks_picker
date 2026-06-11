/* Generated */
   public int trimaLookback( int optInTimePeriod )
   {
      return (optInTimePeriod-1) ;

   }
   public RetCode trima( int startIdx,
                         int endIdx,
                         double inReal[],
                         int optInTimePeriod,
                         MInteger outBegIdx,
                         MInteger outNBElement,
                         double outReal[] )
   {
      int lookbackTotal = 0;
      double numerator = 0;
      double numeratorSub = 0;
      double numeratorAdd = 0;
      int i = 0;
      int outIdx = 0;
      int todayIdx = 0;
      int trailingIdx = 0;
      int middleIdx = 0;
      double factor = 0;
      double tempReal = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackTotal = (optInTimePeriod-1);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outIdx = 0;
      if( ((optInTimePeriod%2)==1) ) {
         i = (optInTimePeriod>>1);
         factor = ((i+1)*(i+1));
         factor = (1.0/factor);
         trailingIdx = (startIdx-lookbackTotal);
         middleIdx = (trailingIdx+i);
         todayIdx = (middleIdx+i);
         numerator = 0.0;
         numeratorSub = 0.0;
         for( i = middleIdx; (i>=trailingIdx); i -= 1 ) {
            tempReal = inReal[i];
            numeratorSub += tempReal;
            numerator += numeratorSub;
         }
         numeratorAdd = 0.0;
         middleIdx += 1;
         for( i = middleIdx; (i<=todayIdx); i += 1 ) {
            tempReal = inReal[i];
            numeratorAdd += tempReal;
            numerator += numeratorAdd;
         }
         outIdx = 0;
         tempReal = inReal[trailingIdx++];
         outReal[outIdx++] = (numerator*factor);
         todayIdx += 1;
         while( (todayIdx<=endIdx) ) {
            numerator -= numeratorSub;
            numeratorSub -= tempReal;
            tempReal = inReal[middleIdx++];
            numeratorSub += tempReal;
            numerator += numeratorAdd;
            numeratorAdd -= tempReal;
            tempReal = inReal[todayIdx++];
            numeratorAdd += tempReal;
            numerator += tempReal;
            tempReal = inReal[trailingIdx++];
            outReal[outIdx++] = (numerator*factor);
         }
      } else {
         i = (optInTimePeriod>>1);
         factor = (i*(i+1));
         factor = (1.0/factor);
         trailingIdx = (startIdx-lookbackTotal);
         middleIdx = ((trailingIdx+i)-1);
         todayIdx = (middleIdx+i);
         numerator = 0.0;
         numeratorSub = 0.0;
         for( i = middleIdx; (i>=trailingIdx); i -= 1 ) {
            tempReal = inReal[i];
            numeratorSub += tempReal;
            numerator += numeratorSub;
         }
         numeratorAdd = 0.0;
         middleIdx += 1;
         for( i = middleIdx; (i<=todayIdx); i += 1 ) {
            tempReal = inReal[i];
            numeratorAdd += tempReal;
            numerator += numeratorAdd;
         }
         outIdx = 0;
         tempReal = inReal[trailingIdx++];
         outReal[outIdx++] = (numerator*factor);
         todayIdx += 1;
         while( (todayIdx<=endIdx) ) {
            numerator -= numeratorSub;
            numeratorSub -= tempReal;
            tempReal = inReal[middleIdx++];
            numeratorSub += tempReal;
            numeratorAdd -= tempReal;
            numerator += numeratorAdd;
            tempReal = inReal[todayIdx++];
            numeratorAdd += tempReal;
            numerator += tempReal;
            tempReal = inReal[trailingIdx++];
            outReal[outIdx++] = (numerator*factor);
         }
      }
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode trimaLogic( int startIdx,
                              int endIdx,
                              double inReal[],
                              int optInTimePeriod,
                              MInteger outBegIdx,
                              MInteger outNBElement,
                              double outReal[] )
   {
      int lookbackTotal = 0;
      double numerator = 0;
      double numeratorSub = 0;
      double numeratorAdd = 0;
      int i = 0;
      int outIdx = 0;
      int todayIdx = 0;
      int trailingIdx = 0;
      int middleIdx = 0;
      double factor = 0;
      double tempReal = 0;
      lookbackTotal = (optInTimePeriod-1);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outIdx = 0;
      if( ((optInTimePeriod%2)==1) ) {
         i = (optInTimePeriod>>1);
         factor = ((i+1)*(i+1));
         factor = (1.0/factor);
         trailingIdx = (startIdx-lookbackTotal);
         middleIdx = (trailingIdx+i);
         todayIdx = (middleIdx+i);
         numerator = 0.0;
         numeratorSub = 0.0;
         for( i = middleIdx; (i>=trailingIdx); i -= 1 ) {
            tempReal = inReal[i];
            numeratorSub += tempReal;
            numerator += numeratorSub;
         }
         numeratorAdd = 0.0;
         middleIdx += 1;
         for( i = middleIdx; (i<=todayIdx); i += 1 ) {
            tempReal = inReal[i];
            numeratorAdd += tempReal;
            numerator += numeratorAdd;
         }
         outIdx = 0;
         tempReal = inReal[trailingIdx++];
         outReal[outIdx++] = (numerator*factor);
         todayIdx += 1;
         while( (todayIdx<=endIdx) ) {
            numerator -= numeratorSub;
            numeratorSub -= tempReal;
            tempReal = inReal[middleIdx++];
            numeratorSub += tempReal;
            numerator += numeratorAdd;
            numeratorAdd -= tempReal;
            tempReal = inReal[todayIdx++];
            numeratorAdd += tempReal;
            numerator += tempReal;
            tempReal = inReal[trailingIdx++];
            outReal[outIdx++] = (numerator*factor);
         }
      } else {
         i = (optInTimePeriod>>1);
         factor = (i*(i+1));
         factor = (1.0/factor);
         trailingIdx = (startIdx-lookbackTotal);
         middleIdx = ((trailingIdx+i)-1);
         todayIdx = (middleIdx+i);
         numerator = 0.0;
         numeratorSub = 0.0;
         for( i = middleIdx; (i>=trailingIdx); i -= 1 ) {
            tempReal = inReal[i];
            numeratorSub += tempReal;
            numerator += numeratorSub;
         }
         numeratorAdd = 0.0;
         middleIdx += 1;
         for( i = middleIdx; (i<=todayIdx); i += 1 ) {
            tempReal = inReal[i];
            numeratorAdd += tempReal;
            numerator += numeratorAdd;
         }
         outIdx = 0;
         tempReal = inReal[trailingIdx++];
         outReal[outIdx++] = (numerator*factor);
         todayIdx += 1;
         while( (todayIdx<=endIdx) ) {
            numerator -= numeratorSub;
            numeratorSub -= tempReal;
            tempReal = inReal[middleIdx++];
            numeratorSub += tempReal;
            numeratorAdd -= tempReal;
            numerator += numeratorAdd;
            tempReal = inReal[todayIdx++];
            numeratorAdd += tempReal;
            numerator += tempReal;
            tempReal = inReal[trailingIdx++];
            outReal[outIdx++] = (numerator*factor);
         }
      }
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode trima( int startIdx,
                         int endIdx,
                         float inReal[],
                         int optInTimePeriod,
                         MInteger outBegIdx,
                         MInteger outNBElement,
                         double outReal[] )
   {
      int lookbackTotal = 0;
      double numerator = 0;
      double numeratorSub = 0;
      double numeratorAdd = 0;
      int i = 0;
      int outIdx = 0;
      int todayIdx = 0;
      int trailingIdx = 0;
      int middleIdx = 0;
      double factor = 0;
      double tempReal = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackTotal = (optInTimePeriod-1);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outIdx = 0;
      if( ((optInTimePeriod%2)==1) ) {
         i = (optInTimePeriod>>1);
         factor = ((i+1)*(i+1));
         factor = (1.0/factor);
         trailingIdx = (startIdx-lookbackTotal);
         middleIdx = (trailingIdx+i);
         todayIdx = (middleIdx+i);
         numerator = 0.0;
         numeratorSub = 0.0;
         for( i = middleIdx; (i>=trailingIdx); i -= 1 ) {
            tempReal = inReal[i];
            numeratorSub += tempReal;
            numerator += numeratorSub;
         }
         numeratorAdd = 0.0;
         middleIdx += 1;
         for( i = middleIdx; (i<=todayIdx); i += 1 ) {
            tempReal = inReal[i];
            numeratorAdd += tempReal;
            numerator += numeratorAdd;
         }
         outIdx = 0;
         tempReal = inReal[trailingIdx++];
         outReal[outIdx++] = (numerator*factor);
         todayIdx += 1;
         while( (todayIdx<=endIdx) ) {
            numerator -= numeratorSub;
            numeratorSub -= tempReal;
            tempReal = inReal[middleIdx++];
            numeratorSub += tempReal;
            numerator += numeratorAdd;
            numeratorAdd -= tempReal;
            tempReal = inReal[todayIdx++];
            numeratorAdd += tempReal;
            numerator += tempReal;
            tempReal = inReal[trailingIdx++];
            outReal[outIdx++] = (numerator*factor);
         }
      } else {
         i = (optInTimePeriod>>1);
         factor = (i*(i+1));
         factor = (1.0/factor);
         trailingIdx = (startIdx-lookbackTotal);
         middleIdx = ((trailingIdx+i)-1);
         todayIdx = (middleIdx+i);
         numerator = 0.0;
         numeratorSub = 0.0;
         for( i = middleIdx; (i>=trailingIdx); i -= 1 ) {
            tempReal = inReal[i];
            numeratorSub += tempReal;
            numerator += numeratorSub;
         }
         numeratorAdd = 0.0;
         middleIdx += 1;
         for( i = middleIdx; (i<=todayIdx); i += 1 ) {
            tempReal = inReal[i];
            numeratorAdd += tempReal;
            numerator += numeratorAdd;
         }
         outIdx = 0;
         tempReal = inReal[trailingIdx++];
         outReal[outIdx++] = (numerator*factor);
         todayIdx += 1;
         while( (todayIdx<=endIdx) ) {
            numerator -= numeratorSub;
            numeratorSub -= tempReal;
            tempReal = inReal[middleIdx++];
            numeratorSub += tempReal;
            numeratorAdd -= tempReal;
            numerator += numeratorAdd;
            tempReal = inReal[todayIdx++];
            numeratorAdd += tempReal;
            numerator += tempReal;
            tempReal = inReal[trailingIdx++];
            outReal[outIdx++] = (numerator*factor);
         }
      }
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
   public RetCode trimaLogic( int startIdx,
                              int endIdx,
                              float inReal[],
                              int optInTimePeriod,
                              MInteger outBegIdx,
                              MInteger outNBElement,
                              double outReal[] )
   {
      int lookbackTotal = 0;
      double numerator = 0;
      double numeratorSub = 0;
      double numeratorAdd = 0;
      int i = 0;
      int outIdx = 0;
      int todayIdx = 0;
      int trailingIdx = 0;
      int middleIdx = 0;
      double factor = 0;
      double tempReal = 0;
      lookbackTotal = (optInTimePeriod-1);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outIdx = 0;
      if( ((optInTimePeriod%2)==1) ) {
         i = (optInTimePeriod>>1);
         factor = ((i+1)*(i+1));
         factor = (1.0/factor);
         trailingIdx = (startIdx-lookbackTotal);
         middleIdx = (trailingIdx+i);
         todayIdx = (middleIdx+i);
         numerator = 0.0;
         numeratorSub = 0.0;
         for( i = middleIdx; (i>=trailingIdx); i -= 1 ) {
            tempReal = inReal[i];
            numeratorSub += tempReal;
            numerator += numeratorSub;
         }
         numeratorAdd = 0.0;
         middleIdx += 1;
         for( i = middleIdx; (i<=todayIdx); i += 1 ) {
            tempReal = inReal[i];
            numeratorAdd += tempReal;
            numerator += numeratorAdd;
         }
         outIdx = 0;
         tempReal = inReal[trailingIdx++];
         outReal[outIdx++] = (numerator*factor);
         todayIdx += 1;
         while( (todayIdx<=endIdx) ) {
            numerator -= numeratorSub;
            numeratorSub -= tempReal;
            tempReal = inReal[middleIdx++];
            numeratorSub += tempReal;
            numerator += numeratorAdd;
            numeratorAdd -= tempReal;
            tempReal = inReal[todayIdx++];
            numeratorAdd += tempReal;
            numerator += tempReal;
            tempReal = inReal[trailingIdx++];
            outReal[outIdx++] = (numerator*factor);
         }
      } else {
         i = (optInTimePeriod>>1);
         factor = (i*(i+1));
         factor = (1.0/factor);
         trailingIdx = (startIdx-lookbackTotal);
         middleIdx = ((trailingIdx+i)-1);
         todayIdx = (middleIdx+i);
         numerator = 0.0;
         numeratorSub = 0.0;
         for( i = middleIdx; (i>=trailingIdx); i -= 1 ) {
            tempReal = inReal[i];
            numeratorSub += tempReal;
            numerator += numeratorSub;
         }
         numeratorAdd = 0.0;
         middleIdx += 1;
         for( i = middleIdx; (i<=todayIdx); i += 1 ) {
            tempReal = inReal[i];
            numeratorAdd += tempReal;
            numerator += numeratorAdd;
         }
         outIdx = 0;
         tempReal = inReal[trailingIdx++];
         outReal[outIdx++] = (numerator*factor);
         todayIdx += 1;
         while( (todayIdx<=endIdx) ) {
            numerator -= numeratorSub;
            numeratorSub -= tempReal;
            tempReal = inReal[middleIdx++];
            numeratorSub += tempReal;
            numeratorAdd -= tempReal;
            numerator += numeratorAdd;
            tempReal = inReal[todayIdx++];
            numeratorAdd += tempReal;
            numerator += tempReal;
            tempReal = inReal[trailingIdx++];
            outReal[outIdx++] = (numerator*factor);
         }
      }
      outNBElement.value = outIdx;
      outBegIdx.value = startIdx;
      return RetCode.Success ;
   }
