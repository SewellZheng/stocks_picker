/* Generated */
   public int emaLookback( int optInTimePeriod )
   {
      return ((optInTimePeriod-1)+this.unstablePeriod[FuncUnstId.Ema.ordinal()]) ;

   }
   public RetCode emaPrivate( int startIdx,
                              int endIdx,
                              double inReal[],
                              int optInTimePeriod,
                              double optInK_1,
                              MInteger outBegIdx,
                              MInteger outNBElement,
                              double outReal[] )
   {
      double tempReal = 0;
      double prevMA = 0;
      int i = 0;
      int today = 0;
      int outIdx = 0;
      int lookbackTotal = 0;
      lookbackTotal = emaLookback(optInTimePeriod);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outBegIdx.value = startIdx;
      if( (this.compatibility==Compatibility.Default) ) {
         today = (startIdx-lookbackTotal);
         i = optInTimePeriod;
         tempReal = 0.0;
         while( (i-->0) ) {
            tempReal += inReal[today++];
         }
         prevMA = (tempReal/optInTimePeriod);
      } else {
         prevMA = inReal[0];
         today = 1;
      }
      while( (today<=startIdx) ) {
         prevMA = (((inReal[today++]-prevMA)*optInK_1)+prevMA);
      }
      outReal[0] = prevMA;
      outIdx = 1;
      while( (today<=endIdx) ) {
         prevMA = (((inReal[today++]-prevMA)*optInK_1)+prevMA);
         outReal[outIdx++] = prevMA;
      }
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode emaPrivate( int startIdx,
                              int endIdx,
                              float inReal[],
                              int optInTimePeriod,
                              double optInK_1,
                              MInteger outBegIdx,
                              MInteger outNBElement,
                              double outReal[] )
   {
      double tempReal = 0;
      double prevMA = 0;
      int i = 0;
      int today = 0;
      int outIdx = 0;
      int lookbackTotal = 0;
      lookbackTotal = emaLookback(optInTimePeriod);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outBegIdx.value = startIdx;
      if( (this.compatibility==Compatibility.Default) ) {
         today = (startIdx-lookbackTotal);
         i = optInTimePeriod;
         tempReal = 0.0;
         while( (i-->0) ) {
            tempReal += inReal[today++];
         }
         prevMA = (tempReal/optInTimePeriod);
      } else {
         prevMA = inReal[0];
         today = 1;
      }
      while( (today<=startIdx) ) {
         prevMA = (((inReal[today++]-prevMA)*optInK_1)+prevMA);
      }
      outReal[0] = prevMA;
      outIdx = 1;
      while( (today<=endIdx) ) {
         prevMA = (((inReal[today++]-prevMA)*optInK_1)+prevMA);
         outReal[outIdx++] = prevMA;
      }
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode ema( int startIdx,
                       int endIdx,
                       double inReal[],
                       int optInTimePeriod,
                       MInteger outBegIdx,
                       MInteger outNBElement,
                       double outReal[] )
   {
      double optInK_1 = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      optInK_1 = (2.0/((double)(optInTimePeriod+1)));
      return emaPrivate(startIdx, endIdx, inReal, optInTimePeriod, optInK_1, outBegIdx, outNBElement, outReal) ;
   }
   public RetCode emaLogic( int startIdx,
                            int endIdx,
                            double inReal[],
                            int optInTimePeriod,
                            MInteger outBegIdx,
                            MInteger outNBElement,
                            double outReal[] )
   {
      double optInK_1 = 0;
      optInK_1 = (2.0/((double)(optInTimePeriod+1)));
      return emaPrivate(startIdx, endIdx, inReal, optInTimePeriod, optInK_1, outBegIdx, outNBElement, outReal) ;
   }
   public RetCode ema( int startIdx,
                       int endIdx,
                       float inReal[],
                       int optInTimePeriod,
                       MInteger outBegIdx,
                       MInteger outNBElement,
                       double outReal[] )
   {
      double tempReal = 0;
      double prevMA = 0;
      int i = 0;
      int today = 0;
      int outIdx = 0;
      int lookbackTotal = 0;
      double optInK_1 = (2.0/(double)((optInTimePeriod+1)));
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackTotal = emaLookback(optInTimePeriod);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outBegIdx.value = startIdx;
      if( (this.compatibility==Compatibility.Default) ) {
         today = (startIdx-lookbackTotal);
         i = optInTimePeriod;
         tempReal = 0.0;
         while( (i-->0) ) {
            tempReal += inReal[today++];
         }
         prevMA = (tempReal/optInTimePeriod);
      } else {
         prevMA = inReal[0];
         today = 1;
      }
      while( (today<=startIdx) ) {
         prevMA = (((inReal[today++]-prevMA)*optInK_1)+prevMA);
      }
      outReal[0] = prevMA;
      outIdx = 1;
      while( (today<=endIdx) ) {
         prevMA = (((inReal[today++]-prevMA)*optInK_1)+prevMA);
         outReal[outIdx++] = prevMA;
      }
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode emaLogic( int startIdx,
                            int endIdx,
                            float inReal[],
                            int optInTimePeriod,
                            MInteger outBegIdx,
                            MInteger outNBElement,
                            double outReal[] )
   {
      double tempReal = 0;
      double prevMA = 0;
      int i = 0;
      int today = 0;
      int outIdx = 0;
      int lookbackTotal = 0;
      double optInK_1 = (2.0/(double)((optInTimePeriod+1)));
      lookbackTotal = emaLookback(optInTimePeriod);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outBegIdx.value = startIdx;
      if( (this.compatibility==Compatibility.Default) ) {
         today = (startIdx-lookbackTotal);
         i = optInTimePeriod;
         tempReal = 0.0;
         while( (i-->0) ) {
            tempReal += inReal[today++];
         }
         prevMA = (tempReal/optInTimePeriod);
      } else {
         prevMA = inReal[0];
         today = 1;
      }
      while( (today<=startIdx) ) {
         prevMA = (((inReal[today++]-prevMA)*optInK_1)+prevMA);
      }
      outReal[0] = prevMA;
      outIdx = 1;
      while( (today<=endIdx) ) {
         prevMA = (((inReal[today++]-prevMA)*optInK_1)+prevMA);
         outReal[outIdx++] = prevMA;
      }
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
