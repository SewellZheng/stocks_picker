/* Generated */
   public int t3Lookback( int optInTimePeriod, double optInVFactor )
   {
      return ((6*(optInTimePeriod-1))+this.unstablePeriod[FuncUnstId.T3.ordinal()]) ;

   }
   public RetCode t3( int startIdx,
                      int endIdx,
                      double inReal[],
                      int optInTimePeriod,
                      double optInVFactor,
                      MInteger outBegIdx,
                      MInteger outNBElement,
                      double outReal[] )
   {
      int outIdx = 0;
      int lookbackTotal = 0;
      int today = 0;
      int i = 0;
      double k = 0;
      double one_minus_k = 0;
      double e1 = 0;
      double e2 = 0;
      double e3 = 0;
      double e4 = 0;
      double e5 = 0;
      double e6 = 0;
      double c1 = 0;
      double c2 = 0;
      double c3 = 0;
      double c4 = 0;
      double tempReal = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackTotal = ((6*(optInTimePeriod-1))+this.unstablePeriod[FuncUnstId.T3.ordinal()]);
      if( (startIdx<=lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outNBElement.value = 0;
         outBegIdx.value = 0;
         return RetCode.Success ;
      }
      outBegIdx.value = startIdx;
      today = (startIdx-lookbackTotal);
      k = (2.0/(optInTimePeriod+1.0));
      one_minus_k = (1.0-k);
      tempReal = inReal[today++];
      for( i = (optInTimePeriod-1); (i>0); i -= 1 ) {
         tempReal += inReal[today++];
      }
      e1 = (tempReal/optInTimePeriod);
      tempReal = e1;
      for( i = (optInTimePeriod-1); (i>0); i -= 1 ) {
         e1 = ((k*inReal[today++])+(one_minus_k*e1));
         tempReal += e1;
      }
      e2 = (tempReal/optInTimePeriod);
      tempReal = e2;
      for( i = (optInTimePeriod-1); (i>0); i -= 1 ) {
         e1 = ((k*inReal[today++])+(one_minus_k*e1));
         e2 = ((k*e1)+(one_minus_k*e2));
         tempReal += e2;
      }
      e3 = (tempReal/optInTimePeriod);
      tempReal = e3;
      for( i = (optInTimePeriod-1); (i>0); i -= 1 ) {
         e1 = ((k*inReal[today++])+(one_minus_k*e1));
         e2 = ((k*e1)+(one_minus_k*e2));
         e3 = ((k*e2)+(one_minus_k*e3));
         tempReal += e3;
      }
      e4 = (tempReal/optInTimePeriod);
      tempReal = e4;
      for( i = (optInTimePeriod-1); (i>0); i -= 1 ) {
         e1 = ((k*inReal[today++])+(one_minus_k*e1));
         e2 = ((k*e1)+(one_minus_k*e2));
         e3 = ((k*e2)+(one_minus_k*e3));
         e4 = ((k*e3)+(one_minus_k*e4));
         tempReal += e4;
      }
      e5 = (tempReal/optInTimePeriod);
      tempReal = e5;
      for( i = (optInTimePeriod-1); (i>0); i -= 1 ) {
         e1 = ((k*inReal[today++])+(one_minus_k*e1));
         e2 = ((k*e1)+(one_minus_k*e2));
         e3 = ((k*e2)+(one_minus_k*e3));
         e4 = ((k*e3)+(one_minus_k*e4));
         e5 = ((k*e4)+(one_minus_k*e5));
         tempReal += e5;
      }
      e6 = (tempReal/optInTimePeriod);
      while( (today<=startIdx) ) {
         e1 = ((k*inReal[today++])+(one_minus_k*e1));
         e2 = ((k*e1)+(one_minus_k*e2));
         e3 = ((k*e2)+(one_minus_k*e3));
         e4 = ((k*e3)+(one_minus_k*e4));
         e5 = ((k*e4)+(one_minus_k*e5));
         e6 = ((k*e5)+(one_minus_k*e6));
      }
      tempReal = (optInVFactor*optInVFactor);
      c1 = (0-(tempReal*optInVFactor));
      c2 = (3.0*(tempReal-c1));
      c3 = (((0-6.0)*tempReal)-(3.0*(optInVFactor-c1)));
      c4 = (((1.0+(3.0*optInVFactor))-c1)+(3.0*tempReal));
      outIdx = 0;
      outReal[outIdx++] = ((((c1*e6)+(c2*e5))+(c3*e4))+(c4*e3));
      while( (today<=endIdx) ) {
         e1 = ((k*inReal[today++])+(one_minus_k*e1));
         e2 = ((k*e1)+(one_minus_k*e2));
         e3 = ((k*e2)+(one_minus_k*e3));
         e4 = ((k*e3)+(one_minus_k*e4));
         e5 = ((k*e4)+(one_minus_k*e5));
         e6 = ((k*e5)+(one_minus_k*e6));
         outReal[outIdx++] = ((((c1*e6)+(c2*e5))+(c3*e4))+(c4*e3));
      }
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode t3Logic( int startIdx,
                           int endIdx,
                           double inReal[],
                           int optInTimePeriod,
                           double optInVFactor,
                           MInteger outBegIdx,
                           MInteger outNBElement,
                           double outReal[] )
   {
      int outIdx = 0;
      int lookbackTotal = 0;
      int today = 0;
      int i = 0;
      double k = 0;
      double one_minus_k = 0;
      double e1 = 0;
      double e2 = 0;
      double e3 = 0;
      double e4 = 0;
      double e5 = 0;
      double e6 = 0;
      double c1 = 0;
      double c2 = 0;
      double c3 = 0;
      double c4 = 0;
      double tempReal = 0;
      lookbackTotal = ((6*(optInTimePeriod-1))+this.unstablePeriod[FuncUnstId.T3.ordinal()]);
      if( (startIdx<=lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outNBElement.value = 0;
         outBegIdx.value = 0;
         return RetCode.Success ;
      }
      outBegIdx.value = startIdx;
      today = (startIdx-lookbackTotal);
      k = (2.0/(optInTimePeriod+1.0));
      one_minus_k = (1.0-k);
      tempReal = inReal[today++];
      for( i = (optInTimePeriod-1); (i>0); i -= 1 ) {
         tempReal += inReal[today++];
      }
      e1 = (tempReal/optInTimePeriod);
      tempReal = e1;
      for( i = (optInTimePeriod-1); (i>0); i -= 1 ) {
         e1 = ((k*inReal[today++])+(one_minus_k*e1));
         tempReal += e1;
      }
      e2 = (tempReal/optInTimePeriod);
      tempReal = e2;
      for( i = (optInTimePeriod-1); (i>0); i -= 1 ) {
         e1 = ((k*inReal[today++])+(one_minus_k*e1));
         e2 = ((k*e1)+(one_minus_k*e2));
         tempReal += e2;
      }
      e3 = (tempReal/optInTimePeriod);
      tempReal = e3;
      for( i = (optInTimePeriod-1); (i>0); i -= 1 ) {
         e1 = ((k*inReal[today++])+(one_minus_k*e1));
         e2 = ((k*e1)+(one_minus_k*e2));
         e3 = ((k*e2)+(one_minus_k*e3));
         tempReal += e3;
      }
      e4 = (tempReal/optInTimePeriod);
      tempReal = e4;
      for( i = (optInTimePeriod-1); (i>0); i -= 1 ) {
         e1 = ((k*inReal[today++])+(one_minus_k*e1));
         e2 = ((k*e1)+(one_minus_k*e2));
         e3 = ((k*e2)+(one_minus_k*e3));
         e4 = ((k*e3)+(one_minus_k*e4));
         tempReal += e4;
      }
      e5 = (tempReal/optInTimePeriod);
      tempReal = e5;
      for( i = (optInTimePeriod-1); (i>0); i -= 1 ) {
         e1 = ((k*inReal[today++])+(one_minus_k*e1));
         e2 = ((k*e1)+(one_minus_k*e2));
         e3 = ((k*e2)+(one_minus_k*e3));
         e4 = ((k*e3)+(one_minus_k*e4));
         e5 = ((k*e4)+(one_minus_k*e5));
         tempReal += e5;
      }
      e6 = (tempReal/optInTimePeriod);
      while( (today<=startIdx) ) {
         e1 = ((k*inReal[today++])+(one_minus_k*e1));
         e2 = ((k*e1)+(one_minus_k*e2));
         e3 = ((k*e2)+(one_minus_k*e3));
         e4 = ((k*e3)+(one_minus_k*e4));
         e5 = ((k*e4)+(one_minus_k*e5));
         e6 = ((k*e5)+(one_minus_k*e6));
      }
      tempReal = (optInVFactor*optInVFactor);
      c1 = (0-(tempReal*optInVFactor));
      c2 = (3.0*(tempReal-c1));
      c3 = (((0-6.0)*tempReal)-(3.0*(optInVFactor-c1)));
      c4 = (((1.0+(3.0*optInVFactor))-c1)+(3.0*tempReal));
      outIdx = 0;
      outReal[outIdx++] = ((((c1*e6)+(c2*e5))+(c3*e4))+(c4*e3));
      while( (today<=endIdx) ) {
         e1 = ((k*inReal[today++])+(one_minus_k*e1));
         e2 = ((k*e1)+(one_minus_k*e2));
         e3 = ((k*e2)+(one_minus_k*e3));
         e4 = ((k*e3)+(one_minus_k*e4));
         e5 = ((k*e4)+(one_minus_k*e5));
         e6 = ((k*e5)+(one_minus_k*e6));
         outReal[outIdx++] = ((((c1*e6)+(c2*e5))+(c3*e4))+(c4*e3));
      }
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode t3( int startIdx,
                      int endIdx,
                      float inReal[],
                      int optInTimePeriod,
                      double optInVFactor,
                      MInteger outBegIdx,
                      MInteger outNBElement,
                      double outReal[] )
   {
      int outIdx = 0;
      int lookbackTotal = 0;
      int today = 0;
      int i = 0;
      double k = 0;
      double one_minus_k = 0;
      double e1 = 0;
      double e2 = 0;
      double e3 = 0;
      double e4 = 0;
      double e5 = 0;
      double e6 = 0;
      double c1 = 0;
      double c2 = 0;
      double c3 = 0;
      double c4 = 0;
      double tempReal = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      lookbackTotal = ((6*(optInTimePeriod-1))+this.unstablePeriod[FuncUnstId.T3.ordinal()]);
      if( (startIdx<=lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outNBElement.value = 0;
         outBegIdx.value = 0;
         return RetCode.Success ;
      }
      outBegIdx.value = startIdx;
      today = (startIdx-lookbackTotal);
      k = (2.0/(optInTimePeriod+1.0));
      one_minus_k = (1.0-k);
      tempReal = inReal[today++];
      for( i = (optInTimePeriod-1); (i>0); i -= 1 ) {
         tempReal += inReal[today++];
      }
      e1 = (tempReal/optInTimePeriod);
      tempReal = e1;
      for( i = (optInTimePeriod-1); (i>0); i -= 1 ) {
         e1 = ((k*inReal[today++])+(one_minus_k*e1));
         tempReal += e1;
      }
      e2 = (tempReal/optInTimePeriod);
      tempReal = e2;
      for( i = (optInTimePeriod-1); (i>0); i -= 1 ) {
         e1 = ((k*inReal[today++])+(one_minus_k*e1));
         e2 = ((k*e1)+(one_minus_k*e2));
         tempReal += e2;
      }
      e3 = (tempReal/optInTimePeriod);
      tempReal = e3;
      for( i = (optInTimePeriod-1); (i>0); i -= 1 ) {
         e1 = ((k*inReal[today++])+(one_minus_k*e1));
         e2 = ((k*e1)+(one_minus_k*e2));
         e3 = ((k*e2)+(one_minus_k*e3));
         tempReal += e3;
      }
      e4 = (tempReal/optInTimePeriod);
      tempReal = e4;
      for( i = (optInTimePeriod-1); (i>0); i -= 1 ) {
         e1 = ((k*inReal[today++])+(one_minus_k*e1));
         e2 = ((k*e1)+(one_minus_k*e2));
         e3 = ((k*e2)+(one_minus_k*e3));
         e4 = ((k*e3)+(one_minus_k*e4));
         tempReal += e4;
      }
      e5 = (tempReal/optInTimePeriod);
      tempReal = e5;
      for( i = (optInTimePeriod-1); (i>0); i -= 1 ) {
         e1 = ((k*inReal[today++])+(one_minus_k*e1));
         e2 = ((k*e1)+(one_minus_k*e2));
         e3 = ((k*e2)+(one_minus_k*e3));
         e4 = ((k*e3)+(one_minus_k*e4));
         e5 = ((k*e4)+(one_minus_k*e5));
         tempReal += e5;
      }
      e6 = (tempReal/optInTimePeriod);
      while( (today<=startIdx) ) {
         e1 = ((k*inReal[today++])+(one_minus_k*e1));
         e2 = ((k*e1)+(one_minus_k*e2));
         e3 = ((k*e2)+(one_minus_k*e3));
         e4 = ((k*e3)+(one_minus_k*e4));
         e5 = ((k*e4)+(one_minus_k*e5));
         e6 = ((k*e5)+(one_minus_k*e6));
      }
      tempReal = (optInVFactor*optInVFactor);
      c1 = (0-(tempReal*optInVFactor));
      c2 = (3.0*(tempReal-c1));
      c3 = (((0-6.0)*tempReal)-(3.0*(optInVFactor-c1)));
      c4 = (((1.0+(3.0*optInVFactor))-c1)+(3.0*tempReal));
      outIdx = 0;
      outReal[outIdx++] = ((((c1*e6)+(c2*e5))+(c3*e4))+(c4*e3));
      while( (today<=endIdx) ) {
         e1 = ((k*inReal[today++])+(one_minus_k*e1));
         e2 = ((k*e1)+(one_minus_k*e2));
         e3 = ((k*e2)+(one_minus_k*e3));
         e4 = ((k*e3)+(one_minus_k*e4));
         e5 = ((k*e4)+(one_minus_k*e5));
         e6 = ((k*e5)+(one_minus_k*e6));
         outReal[outIdx++] = ((((c1*e6)+(c2*e5))+(c3*e4))+(c4*e3));
      }
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode t3Logic( int startIdx,
                           int endIdx,
                           float inReal[],
                           int optInTimePeriod,
                           double optInVFactor,
                           MInteger outBegIdx,
                           MInteger outNBElement,
                           double outReal[] )
   {
      int outIdx = 0;
      int lookbackTotal = 0;
      int today = 0;
      int i = 0;
      double k = 0;
      double one_minus_k = 0;
      double e1 = 0;
      double e2 = 0;
      double e3 = 0;
      double e4 = 0;
      double e5 = 0;
      double e6 = 0;
      double c1 = 0;
      double c2 = 0;
      double c3 = 0;
      double c4 = 0;
      double tempReal = 0;
      lookbackTotal = ((6*(optInTimePeriod-1))+this.unstablePeriod[FuncUnstId.T3.ordinal()]);
      if( (startIdx<=lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outNBElement.value = 0;
         outBegIdx.value = 0;
         return RetCode.Success ;
      }
      outBegIdx.value = startIdx;
      today = (startIdx-lookbackTotal);
      k = (2.0/(optInTimePeriod+1.0));
      one_minus_k = (1.0-k);
      tempReal = inReal[today++];
      for( i = (optInTimePeriod-1); (i>0); i -= 1 ) {
         tempReal += inReal[today++];
      }
      e1 = (tempReal/optInTimePeriod);
      tempReal = e1;
      for( i = (optInTimePeriod-1); (i>0); i -= 1 ) {
         e1 = ((k*inReal[today++])+(one_minus_k*e1));
         tempReal += e1;
      }
      e2 = (tempReal/optInTimePeriod);
      tempReal = e2;
      for( i = (optInTimePeriod-1); (i>0); i -= 1 ) {
         e1 = ((k*inReal[today++])+(one_minus_k*e1));
         e2 = ((k*e1)+(one_minus_k*e2));
         tempReal += e2;
      }
      e3 = (tempReal/optInTimePeriod);
      tempReal = e3;
      for( i = (optInTimePeriod-1); (i>0); i -= 1 ) {
         e1 = ((k*inReal[today++])+(one_minus_k*e1));
         e2 = ((k*e1)+(one_minus_k*e2));
         e3 = ((k*e2)+(one_minus_k*e3));
         tempReal += e3;
      }
      e4 = (tempReal/optInTimePeriod);
      tempReal = e4;
      for( i = (optInTimePeriod-1); (i>0); i -= 1 ) {
         e1 = ((k*inReal[today++])+(one_minus_k*e1));
         e2 = ((k*e1)+(one_minus_k*e2));
         e3 = ((k*e2)+(one_minus_k*e3));
         e4 = ((k*e3)+(one_minus_k*e4));
         tempReal += e4;
      }
      e5 = (tempReal/optInTimePeriod);
      tempReal = e5;
      for( i = (optInTimePeriod-1); (i>0); i -= 1 ) {
         e1 = ((k*inReal[today++])+(one_minus_k*e1));
         e2 = ((k*e1)+(one_minus_k*e2));
         e3 = ((k*e2)+(one_minus_k*e3));
         e4 = ((k*e3)+(one_minus_k*e4));
         e5 = ((k*e4)+(one_minus_k*e5));
         tempReal += e5;
      }
      e6 = (tempReal/optInTimePeriod);
      while( (today<=startIdx) ) {
         e1 = ((k*inReal[today++])+(one_minus_k*e1));
         e2 = ((k*e1)+(one_minus_k*e2));
         e3 = ((k*e2)+(one_minus_k*e3));
         e4 = ((k*e3)+(one_minus_k*e4));
         e5 = ((k*e4)+(one_minus_k*e5));
         e6 = ((k*e5)+(one_minus_k*e6));
      }
      tempReal = (optInVFactor*optInVFactor);
      c1 = (0-(tempReal*optInVFactor));
      c2 = (3.0*(tempReal-c1));
      c3 = (((0-6.0)*tempReal)-(3.0*(optInVFactor-c1)));
      c4 = (((1.0+(3.0*optInVFactor))-c1)+(3.0*tempReal));
      outIdx = 0;
      outReal[outIdx++] = ((((c1*e6)+(c2*e5))+(c3*e4))+(c4*e3));
      while( (today<=endIdx) ) {
         e1 = ((k*inReal[today++])+(one_minus_k*e1));
         e2 = ((k*e1)+(one_minus_k*e2));
         e3 = ((k*e2)+(one_minus_k*e3));
         e4 = ((k*e3)+(one_minus_k*e4));
         e5 = ((k*e4)+(one_minus_k*e5));
         e6 = ((k*e5)+(one_minus_k*e6));
         outReal[outIdx++] = ((((c1*e6)+(c2*e5))+(c3*e4))+(c4*e3));
      }
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
