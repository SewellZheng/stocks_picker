/* Generated */
   public int kamaLookback( int optInTimePeriod )
   {
      return (optInTimePeriod+this.unstablePeriod[FuncUnstId.Kama.ordinal()]) ;

   }
   public RetCode kama( int startIdx,
                        int endIdx,
                        double inReal[],
                        int optInTimePeriod,
                        MInteger outBegIdx,
                        MInteger outNBElement,
                        double outReal[] )
   {
      double constMax = 0;
      double constDiff = 0;
      double tempReal = 0;
      double tempReal2 = 0;
      double sumROC1 = 0;
      double periodROC = 0;
      double prevKAMA = 0;
      int i = 0;
      int today = 0;
      int outIdx = 0;
      int lookbackTotal = 0;
      int trailingIdx = 0;
      double trailingValue = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      constMax = (2.0/(30.0+1.0));
      constDiff = ((2.0/(2.0+1.0))-constMax);
      outBegIdx.value = 0;
      outNBElement.value = 0;
      lookbackTotal = (optInTimePeriod+this.unstablePeriod[FuncUnstId.Kama.ordinal()]);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      sumROC1 = 0.0;
      today = (startIdx-lookbackTotal);
      trailingIdx = today;
      i = optInTimePeriod;
      while( (i-->0) ) {
         tempReal = inReal[today++];
         tempReal -= inReal[today];
         sumROC1 += Math.abs(tempReal);
      }
      prevKAMA = inReal[(today-1)];
      tempReal = inReal[today];
      tempReal2 = inReal[trailingIdx++];
      periodROC = (tempReal-tempReal2);
      trailingValue = tempReal2;
      if( ((sumROC1<=periodROC)||((-0.00000000000001 < sumROC1) && (sumROC1 < 0.00000000000001))) ) {
         tempReal = 1.0;
      } else {
         tempReal = Math.abs((periodROC/sumROC1));
      }
      tempReal = ((tempReal*constDiff)+constMax);
      tempReal *= tempReal;
      prevKAMA = (((inReal[today++]-prevKAMA)*tempReal)+prevKAMA);
      while( (today<=startIdx) ) {
         tempReal = inReal[today];
         tempReal2 = inReal[trailingIdx++];
         periodROC = (tempReal-tempReal2);
         sumROC1 -= Math.abs((trailingValue-tempReal2));
         sumROC1 += Math.abs((tempReal-inReal[(today-1)]));
         trailingValue = tempReal2;
         if( ((sumROC1<=periodROC)||((-0.00000000000001 < sumROC1) && (sumROC1 < 0.00000000000001))) ) {
            tempReal = 1.0;
         } else {
            tempReal = Math.abs((periodROC/sumROC1));
         }
         tempReal = ((tempReal*constDiff)+constMax);
         tempReal *= tempReal;
         prevKAMA = (((inReal[today++]-prevKAMA)*tempReal)+prevKAMA);
      }
      outReal[0] = prevKAMA;
      outIdx = 1;
      outBegIdx.value = (today-1);
      while( (today<=endIdx) ) {
         tempReal = inReal[today];
         tempReal2 = inReal[trailingIdx++];
         periodROC = (tempReal-tempReal2);
         sumROC1 -= Math.abs((trailingValue-tempReal2));
         sumROC1 += Math.abs((tempReal-inReal[(today-1)]));
         trailingValue = tempReal2;
         if( ((sumROC1<=periodROC)||((-0.00000000000001 < sumROC1) && (sumROC1 < 0.00000000000001))) ) {
            tempReal = 1.0;
         } else {
            tempReal = Math.abs((periodROC/sumROC1));
         }
         tempReal = ((tempReal*constDiff)+constMax);
         tempReal *= tempReal;
         prevKAMA = (((inReal[today++]-prevKAMA)*tempReal)+prevKAMA);
         outReal[outIdx++] = prevKAMA;
      }
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode kamaLogic( int startIdx,
                             int endIdx,
                             double inReal[],
                             int optInTimePeriod,
                             MInteger outBegIdx,
                             MInteger outNBElement,
                             double outReal[] )
   {
      double constMax = 0;
      double constDiff = 0;
      double tempReal = 0;
      double tempReal2 = 0;
      double sumROC1 = 0;
      double periodROC = 0;
      double prevKAMA = 0;
      int i = 0;
      int today = 0;
      int outIdx = 0;
      int lookbackTotal = 0;
      int trailingIdx = 0;
      double trailingValue = 0;
      constMax = (2.0/(30.0+1.0));
      constDiff = ((2.0/(2.0+1.0))-constMax);
      outBegIdx.value = 0;
      outNBElement.value = 0;
      lookbackTotal = (optInTimePeriod+this.unstablePeriod[FuncUnstId.Kama.ordinal()]);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      sumROC1 = 0.0;
      today = (startIdx-lookbackTotal);
      trailingIdx = today;
      i = optInTimePeriod;
      while( (i-->0) ) {
         tempReal = inReal[today++];
         tempReal -= inReal[today];
         sumROC1 += Math.abs(tempReal);
      }
      prevKAMA = inReal[(today-1)];
      tempReal = inReal[today];
      tempReal2 = inReal[trailingIdx++];
      periodROC = (tempReal-tempReal2);
      trailingValue = tempReal2;
      if( ((sumROC1<=periodROC)||((-0.00000000000001 < sumROC1) && (sumROC1 < 0.00000000000001))) ) {
         tempReal = 1.0;
      } else {
         tempReal = Math.abs((periodROC/sumROC1));
      }
      tempReal = ((tempReal*constDiff)+constMax);
      tempReal *= tempReal;
      prevKAMA = (((inReal[today++]-prevKAMA)*tempReal)+prevKAMA);
      while( (today<=startIdx) ) {
         tempReal = inReal[today];
         tempReal2 = inReal[trailingIdx++];
         periodROC = (tempReal-tempReal2);
         sumROC1 -= Math.abs((trailingValue-tempReal2));
         sumROC1 += Math.abs((tempReal-inReal[(today-1)]));
         trailingValue = tempReal2;
         if( ((sumROC1<=periodROC)||((-0.00000000000001 < sumROC1) && (sumROC1 < 0.00000000000001))) ) {
            tempReal = 1.0;
         } else {
            tempReal = Math.abs((periodROC/sumROC1));
         }
         tempReal = ((tempReal*constDiff)+constMax);
         tempReal *= tempReal;
         prevKAMA = (((inReal[today++]-prevKAMA)*tempReal)+prevKAMA);
      }
      outReal[0] = prevKAMA;
      outIdx = 1;
      outBegIdx.value = (today-1);
      while( (today<=endIdx) ) {
         tempReal = inReal[today];
         tempReal2 = inReal[trailingIdx++];
         periodROC = (tempReal-tempReal2);
         sumROC1 -= Math.abs((trailingValue-tempReal2));
         sumROC1 += Math.abs((tempReal-inReal[(today-1)]));
         trailingValue = tempReal2;
         if( ((sumROC1<=periodROC)||((-0.00000000000001 < sumROC1) && (sumROC1 < 0.00000000000001))) ) {
            tempReal = 1.0;
         } else {
            tempReal = Math.abs((periodROC/sumROC1));
         }
         tempReal = ((tempReal*constDiff)+constMax);
         tempReal *= tempReal;
         prevKAMA = (((inReal[today++]-prevKAMA)*tempReal)+prevKAMA);
         outReal[outIdx++] = prevKAMA;
      }
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode kama( int startIdx,
                        int endIdx,
                        float inReal[],
                        int optInTimePeriod,
                        MInteger outBegIdx,
                        MInteger outNBElement,
                        double outReal[] )
   {
      double constMax = 0;
      double constDiff = 0;
      double tempReal = 0;
      double tempReal2 = 0;
      double sumROC1 = 0;
      double periodROC = 0;
      double prevKAMA = 0;
      int i = 0;
      int today = 0;
      int outIdx = 0;
      int lookbackTotal = 0;
      int trailingIdx = 0;
      double trailingValue = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      constMax = (2.0/(30.0+1.0));
      constDiff = ((2.0/(2.0+1.0))-constMax);
      outBegIdx.value = 0;
      outNBElement.value = 0;
      lookbackTotal = (optInTimePeriod+this.unstablePeriod[FuncUnstId.Kama.ordinal()]);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      sumROC1 = 0.0;
      today = (startIdx-lookbackTotal);
      trailingIdx = today;
      i = optInTimePeriod;
      while( (i-->0) ) {
         tempReal = inReal[today++];
         tempReal -= inReal[today];
         sumROC1 += Math.abs(tempReal);
      }
      prevKAMA = inReal[(today-1)];
      tempReal = inReal[today];
      tempReal2 = inReal[trailingIdx++];
      periodROC = (tempReal-tempReal2);
      trailingValue = tempReal2;
      if( ((sumROC1<=periodROC)||((-0.00000000000001 < sumROC1) && (sumROC1 < 0.00000000000001))) ) {
         tempReal = 1.0;
      } else {
         tempReal = Math.abs((periodROC/sumROC1));
      }
      tempReal = ((tempReal*constDiff)+constMax);
      tempReal *= tempReal;
      prevKAMA = (((inReal[today++]-prevKAMA)*tempReal)+prevKAMA);
      while( (today<=startIdx) ) {
         tempReal = inReal[today];
         tempReal2 = inReal[trailingIdx++];
         periodROC = (tempReal-tempReal2);
         sumROC1 -= Math.abs((trailingValue-tempReal2));
         sumROC1 += Math.abs((tempReal-inReal[(today-1)]));
         trailingValue = tempReal2;
         if( ((sumROC1<=periodROC)||((-0.00000000000001 < sumROC1) && (sumROC1 < 0.00000000000001))) ) {
            tempReal = 1.0;
         } else {
            tempReal = Math.abs((periodROC/sumROC1));
         }
         tempReal = ((tempReal*constDiff)+constMax);
         tempReal *= tempReal;
         prevKAMA = (((inReal[today++]-prevKAMA)*tempReal)+prevKAMA);
      }
      outReal[0] = prevKAMA;
      outIdx = 1;
      outBegIdx.value = (today-1);
      while( (today<=endIdx) ) {
         tempReal = inReal[today];
         tempReal2 = inReal[trailingIdx++];
         periodROC = (tempReal-tempReal2);
         sumROC1 -= Math.abs((trailingValue-tempReal2));
         sumROC1 += Math.abs((tempReal-inReal[(today-1)]));
         trailingValue = tempReal2;
         if( ((sumROC1<=periodROC)||((-0.00000000000001 < sumROC1) && (sumROC1 < 0.00000000000001))) ) {
            tempReal = 1.0;
         } else {
            tempReal = Math.abs((periodROC/sumROC1));
         }
         tempReal = ((tempReal*constDiff)+constMax);
         tempReal *= tempReal;
         prevKAMA = (((inReal[today++]-prevKAMA)*tempReal)+prevKAMA);
         outReal[outIdx++] = prevKAMA;
      }
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode kamaLogic( int startIdx,
                             int endIdx,
                             float inReal[],
                             int optInTimePeriod,
                             MInteger outBegIdx,
                             MInteger outNBElement,
                             double outReal[] )
   {
      double constMax = 0;
      double constDiff = 0;
      double tempReal = 0;
      double tempReal2 = 0;
      double sumROC1 = 0;
      double periodROC = 0;
      double prevKAMA = 0;
      int i = 0;
      int today = 0;
      int outIdx = 0;
      int lookbackTotal = 0;
      int trailingIdx = 0;
      double trailingValue = 0;
      constMax = (2.0/(30.0+1.0));
      constDiff = ((2.0/(2.0+1.0))-constMax);
      outBegIdx.value = 0;
      outNBElement.value = 0;
      lookbackTotal = (optInTimePeriod+this.unstablePeriod[FuncUnstId.Kama.ordinal()]);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      sumROC1 = 0.0;
      today = (startIdx-lookbackTotal);
      trailingIdx = today;
      i = optInTimePeriod;
      while( (i-->0) ) {
         tempReal = inReal[today++];
         tempReal -= inReal[today];
         sumROC1 += Math.abs(tempReal);
      }
      prevKAMA = inReal[(today-1)];
      tempReal = inReal[today];
      tempReal2 = inReal[trailingIdx++];
      periodROC = (tempReal-tempReal2);
      trailingValue = tempReal2;
      if( ((sumROC1<=periodROC)||((-0.00000000000001 < sumROC1) && (sumROC1 < 0.00000000000001))) ) {
         tempReal = 1.0;
      } else {
         tempReal = Math.abs((periodROC/sumROC1));
      }
      tempReal = ((tempReal*constDiff)+constMax);
      tempReal *= tempReal;
      prevKAMA = (((inReal[today++]-prevKAMA)*tempReal)+prevKAMA);
      while( (today<=startIdx) ) {
         tempReal = inReal[today];
         tempReal2 = inReal[trailingIdx++];
         periodROC = (tempReal-tempReal2);
         sumROC1 -= Math.abs((trailingValue-tempReal2));
         sumROC1 += Math.abs((tempReal-inReal[(today-1)]));
         trailingValue = tempReal2;
         if( ((sumROC1<=periodROC)||((-0.00000000000001 < sumROC1) && (sumROC1 < 0.00000000000001))) ) {
            tempReal = 1.0;
         } else {
            tempReal = Math.abs((periodROC/sumROC1));
         }
         tempReal = ((tempReal*constDiff)+constMax);
         tempReal *= tempReal;
         prevKAMA = (((inReal[today++]-prevKAMA)*tempReal)+prevKAMA);
      }
      outReal[0] = prevKAMA;
      outIdx = 1;
      outBegIdx.value = (today-1);
      while( (today<=endIdx) ) {
         tempReal = inReal[today];
         tempReal2 = inReal[trailingIdx++];
         periodROC = (tempReal-tempReal2);
         sumROC1 -= Math.abs((trailingValue-tempReal2));
         sumROC1 += Math.abs((tempReal-inReal[(today-1)]));
         trailingValue = tempReal2;
         if( ((sumROC1<=periodROC)||((-0.00000000000001 < sumROC1) && (sumROC1 < 0.00000000000001))) ) {
            tempReal = 1.0;
         } else {
            tempReal = Math.abs((periodROC/sumROC1));
         }
         tempReal = ((tempReal*constDiff)+constMax);
         tempReal *= tempReal;
         prevKAMA = (((inReal[today++]-prevKAMA)*tempReal)+prevKAMA);
         outReal[outIdx++] = prevKAMA;
      }
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
