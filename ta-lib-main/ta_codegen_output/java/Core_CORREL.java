/* Generated */
   public int correlLookback( int optInTimePeriod )
   {
      return (optInTimePeriod-1) ;

   }
   public RetCode correl( int startIdx,
                          int endIdx,
                          double inReal0[],
                          double inReal1[],
                          int optInTimePeriod,
                          MInteger outBegIdx,
                          MInteger outNBElement,
                          double outReal[] )
   {
      double sumXY = 0;
      double sumX = 0;
      double sumY = 0;
      double sumX2 = 0;
      double sumY2 = 0;
      double x = 0;
      double y = 0;
      double trailingX = 0;
      double trailingY = 0;
      double tempReal = 0;
      int lookbackTotal = 0;
      int today = 0;
      int trailingIdx = 0;
      int outIdx = 0;
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
      outBegIdx.value = startIdx;
      trailingIdx = (startIdx-lookbackTotal);
      sumY2 = 0.0;
      sumX2 = sumY2;
      sumY = sumX2;
      sumX = sumY;
      sumXY = sumX;
      for( today = trailingIdx; (today<=startIdx); today += 1 ) {
         x = inReal0[today];
         sumX += x;
         sumX2 += (x*x);
         y = inReal1[today];
         sumXY += (x*y);
         sumY += y;
         sumY2 += (y*y);
      }
      trailingX = inReal0[trailingIdx];
      trailingY = inReal1[trailingIdx++];
      tempReal = ((sumX2-((sumX*sumX)/optInTimePeriod))*(sumY2-((sumY*sumY)/optInTimePeriod)));
      if( !((tempReal < 0.00000000000001)) ) {
         outReal[0] = ((sumXY-((sumX*sumY)/optInTimePeriod))/Math.sqrt(tempReal));
      } else {
         outReal[0] = 0.0;
      }
      outIdx = 1;
      while( (today<=endIdx) ) {
         sumX -= trailingX;
         sumX2 -= (trailingX*trailingX);
         sumXY -= (trailingX*trailingY);
         sumY -= trailingY;
         sumY2 -= (trailingY*trailingY);
         x = inReal0[today];
         sumX += x;
         sumX2 += (x*x);
         y = inReal1[today++];
         sumXY += (x*y);
         sumY += y;
         sumY2 += (y*y);
         trailingX = inReal0[trailingIdx];
         trailingY = inReal1[trailingIdx++];
         tempReal = ((sumX2-((sumX*sumX)/optInTimePeriod))*(sumY2-((sumY*sumY)/optInTimePeriod)));
         if( !((tempReal < 0.00000000000001)) ) {
            outReal[outIdx++] = ((sumXY-((sumX*sumY)/optInTimePeriod))/Math.sqrt(tempReal));
         } else {
            outReal[outIdx++] = 0.0;
         }
      }
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode correlLogic( int startIdx,
                               int endIdx,
                               double inReal0[],
                               double inReal1[],
                               int optInTimePeriod,
                               MInteger outBegIdx,
                               MInteger outNBElement,
                               double outReal[] )
   {
      double sumXY = 0;
      double sumX = 0;
      double sumY = 0;
      double sumX2 = 0;
      double sumY2 = 0;
      double x = 0;
      double y = 0;
      double trailingX = 0;
      double trailingY = 0;
      double tempReal = 0;
      int lookbackTotal = 0;
      int today = 0;
      int trailingIdx = 0;
      int outIdx = 0;
      lookbackTotal = (optInTimePeriod-1);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outBegIdx.value = startIdx;
      trailingIdx = (startIdx-lookbackTotal);
      sumY2 = 0.0;
      sumX2 = sumY2;
      sumY = sumX2;
      sumX = sumY;
      sumXY = sumX;
      for( today = trailingIdx; (today<=startIdx); today += 1 ) {
         x = inReal0[today];
         sumX += x;
         sumX2 += (x*x);
         y = inReal1[today];
         sumXY += (x*y);
         sumY += y;
         sumY2 += (y*y);
      }
      trailingX = inReal0[trailingIdx];
      trailingY = inReal1[trailingIdx++];
      tempReal = ((sumX2-((sumX*sumX)/optInTimePeriod))*(sumY2-((sumY*sumY)/optInTimePeriod)));
      if( !((tempReal < 0.00000000000001)) ) {
         outReal[0] = ((sumXY-((sumX*sumY)/optInTimePeriod))/Math.sqrt(tempReal));
      } else {
         outReal[0] = 0.0;
      }
      outIdx = 1;
      while( (today<=endIdx) ) {
         sumX -= trailingX;
         sumX2 -= (trailingX*trailingX);
         sumXY -= (trailingX*trailingY);
         sumY -= trailingY;
         sumY2 -= (trailingY*trailingY);
         x = inReal0[today];
         sumX += x;
         sumX2 += (x*x);
         y = inReal1[today++];
         sumXY += (x*y);
         sumY += y;
         sumY2 += (y*y);
         trailingX = inReal0[trailingIdx];
         trailingY = inReal1[trailingIdx++];
         tempReal = ((sumX2-((sumX*sumX)/optInTimePeriod))*(sumY2-((sumY*sumY)/optInTimePeriod)));
         if( !((tempReal < 0.00000000000001)) ) {
            outReal[outIdx++] = ((sumXY-((sumX*sumY)/optInTimePeriod))/Math.sqrt(tempReal));
         } else {
            outReal[outIdx++] = 0.0;
         }
      }
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode correl( int startIdx,
                          int endIdx,
                          float inReal0[],
                          float inReal1[],
                          int optInTimePeriod,
                          MInteger outBegIdx,
                          MInteger outNBElement,
                          double outReal[] )
   {
      double sumXY = 0;
      double sumX = 0;
      double sumY = 0;
      double sumX2 = 0;
      double sumY2 = 0;
      double x = 0;
      double y = 0;
      double trailingX = 0;
      double trailingY = 0;
      double tempReal = 0;
      int lookbackTotal = 0;
      int today = 0;
      int trailingIdx = 0;
      int outIdx = 0;
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
      outBegIdx.value = startIdx;
      trailingIdx = (startIdx-lookbackTotal);
      sumY2 = 0.0;
      sumX2 = sumY2;
      sumY = sumX2;
      sumX = sumY;
      sumXY = sumX;
      for( today = trailingIdx; (today<=startIdx); today += 1 ) {
         x = inReal0[today];
         sumX += x;
         sumX2 += (x*x);
         y = inReal1[today];
         sumXY += (x*y);
         sumY += y;
         sumY2 += (y*y);
      }
      trailingX = inReal0[trailingIdx];
      trailingY = inReal1[trailingIdx++];
      tempReal = ((sumX2-((sumX*sumX)/optInTimePeriod))*(sumY2-((sumY*sumY)/optInTimePeriod)));
      if( !((tempReal < 0.00000000000001)) ) {
         outReal[0] = ((sumXY-((sumX*sumY)/optInTimePeriod))/Math.sqrt(tempReal));
      } else {
         outReal[0] = 0.0;
      }
      outIdx = 1;
      while( (today<=endIdx) ) {
         sumX -= trailingX;
         sumX2 -= (trailingX*trailingX);
         sumXY -= (trailingX*trailingY);
         sumY -= trailingY;
         sumY2 -= (trailingY*trailingY);
         x = inReal0[today];
         sumX += x;
         sumX2 += (x*x);
         y = inReal1[today++];
         sumXY += (x*y);
         sumY += y;
         sumY2 += (y*y);
         trailingX = inReal0[trailingIdx];
         trailingY = inReal1[trailingIdx++];
         tempReal = ((sumX2-((sumX*sumX)/optInTimePeriod))*(sumY2-((sumY*sumY)/optInTimePeriod)));
         if( !((tempReal < 0.00000000000001)) ) {
            outReal[outIdx++] = ((sumXY-((sumX*sumY)/optInTimePeriod))/Math.sqrt(tempReal));
         } else {
            outReal[outIdx++] = 0.0;
         }
      }
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode correlLogic( int startIdx,
                               int endIdx,
                               float inReal0[],
                               float inReal1[],
                               int optInTimePeriod,
                               MInteger outBegIdx,
                               MInteger outNBElement,
                               double outReal[] )
   {
      double sumXY = 0;
      double sumX = 0;
      double sumY = 0;
      double sumX2 = 0;
      double sumY2 = 0;
      double x = 0;
      double y = 0;
      double trailingX = 0;
      double trailingY = 0;
      double tempReal = 0;
      int lookbackTotal = 0;
      int today = 0;
      int trailingIdx = 0;
      int outIdx = 0;
      lookbackTotal = (optInTimePeriod-1);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      outBegIdx.value = startIdx;
      trailingIdx = (startIdx-lookbackTotal);
      sumY2 = 0.0;
      sumX2 = sumY2;
      sumY = sumX2;
      sumX = sumY;
      sumXY = sumX;
      for( today = trailingIdx; (today<=startIdx); today += 1 ) {
         x = inReal0[today];
         sumX += x;
         sumX2 += (x*x);
         y = inReal1[today];
         sumXY += (x*y);
         sumY += y;
         sumY2 += (y*y);
      }
      trailingX = inReal0[trailingIdx];
      trailingY = inReal1[trailingIdx++];
      tempReal = ((sumX2-((sumX*sumX)/optInTimePeriod))*(sumY2-((sumY*sumY)/optInTimePeriod)));
      if( !((tempReal < 0.00000000000001)) ) {
         outReal[0] = ((sumXY-((sumX*sumY)/optInTimePeriod))/Math.sqrt(tempReal));
      } else {
         outReal[0] = 0.0;
      }
      outIdx = 1;
      while( (today<=endIdx) ) {
         sumX -= trailingX;
         sumX2 -= (trailingX*trailingX);
         sumXY -= (trailingX*trailingY);
         sumY -= trailingY;
         sumY2 -= (trailingY*trailingY);
         x = inReal0[today];
         sumX += x;
         sumX2 += (x*x);
         y = inReal1[today++];
         sumXY += (x*y);
         sumY += y;
         sumY2 += (y*y);
         trailingX = inReal0[trailingIdx];
         trailingY = inReal1[trailingIdx++];
         tempReal = ((sumX2-((sumX*sumX)/optInTimePeriod))*(sumY2-((sumY*sumY)/optInTimePeriod)));
         if( !((tempReal < 0.00000000000001)) ) {
            outReal[outIdx++] = ((sumXY-((sumX*sumY)/optInTimePeriod))/Math.sqrt(tempReal));
         } else {
            outReal[outIdx++] = 0.0;
         }
      }
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
