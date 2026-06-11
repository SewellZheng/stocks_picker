/* Generated */
   public int atrLookback( int optInTimePeriod )
   {
      return (optInTimePeriod+this.unstablePeriod[FuncUnstId.Atr.ordinal()]) ;

   }
   public RetCode atr( int startIdx,
                       int endIdx,
                       double inHigh[],
                       double inLow[],
                       double inClose[],
                       int optInTimePeriod,
                       MInteger outBegIdx,
                       MInteger outNBElement,
                       double outReal[] )
   {
      RetCode retCode;
      int outIdx = 0;
      int today = 0;
      int lookbackTotal = 0;
      int nbATR = 0;
      MInteger outBegIdx1 = new MInteger();
      MInteger outNbElement1 = new MInteger();
      double[] prevATR = new double[1];
      double[] tempBuffer;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      outBegIdx.value = 0;
      outNBElement.value = 0;
      lookbackTotal = atrLookback(optInTimePeriod);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         return RetCode.Success ;
      }
      if( (optInTimePeriod<=1) ) {
         return trangeLogic(startIdx, endIdx, inHigh, inLow, inClose, outBegIdx, outNBElement, outReal) ;
      }
      tempBuffer = new double[(int)((((lookbackTotal+(endIdx-startIdx))+1)*1))];
      retCode = trangeLogic(((startIdx-lookbackTotal)+1), endIdx, inHigh, inLow, inClose, outBegIdx1, outNbElement1, tempBuffer);
      if( (retCode!=RetCode.Success) ) {
         return retCode ;
      }
      retCode = smaLogic((optInTimePeriod-1), (optInTimePeriod-1), tempBuffer, optInTimePeriod, outBegIdx1, outNbElement1, prevATR);
      if( (retCode!=RetCode.Success) ) {
         return retCode ;
      }
      today = optInTimePeriod;
      outIdx = this.unstablePeriod[FuncUnstId.Atr.ordinal()];
      while( (outIdx!=0) ) {
         prevATR[0] *= (optInTimePeriod-1);
         prevATR[0] += tempBuffer[today++];
         prevATR[0] /= optInTimePeriod;
         outIdx -= 1;
      }
      outIdx = 1;
      outReal[0] = prevATR[0];
      nbATR = ((endIdx-startIdx)+1);
      while( (--nbATR!=0) ) {
         prevATR[0] *= (optInTimePeriod-1);
         prevATR[0] += tempBuffer[today++];
         prevATR[0] /= optInTimePeriod;
         outReal[outIdx++] = prevATR[0];
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outIdx;
      return retCode ;
   }
   public RetCode atrLogic( int startIdx,
                            int endIdx,
                            double inHigh[],
                            double inLow[],
                            double inClose[],
                            int optInTimePeriod,
                            MInteger outBegIdx,
                            MInteger outNBElement,
                            double outReal[] )
   {
      RetCode retCode;
      int outIdx = 0;
      int today = 0;
      int lookbackTotal = 0;
      int nbATR = 0;
      MInteger outBegIdx1 = new MInteger();
      MInteger outNbElement1 = new MInteger();
      double[] prevATR = new double[1];
      double[] tempBuffer;
      outBegIdx.value = 0;
      outNBElement.value = 0;
      lookbackTotal = atrLookback(optInTimePeriod);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         return RetCode.Success ;
      }
      if( (optInTimePeriod<=1) ) {
         return trangeLogic(startIdx, endIdx, inHigh, inLow, inClose, outBegIdx, outNBElement, outReal) ;
      }
      tempBuffer = new double[(int)((((lookbackTotal+(endIdx-startIdx))+1)*1))];
      retCode = trangeLogic(((startIdx-lookbackTotal)+1), endIdx, inHigh, inLow, inClose, outBegIdx1, outNbElement1, tempBuffer);
      if( (retCode!=RetCode.Success) ) {
         return retCode ;
      }
      retCode = smaLogic((optInTimePeriod-1), (optInTimePeriod-1), tempBuffer, optInTimePeriod, outBegIdx1, outNbElement1, prevATR);
      if( (retCode!=RetCode.Success) ) {
         return retCode ;
      }
      today = optInTimePeriod;
      outIdx = this.unstablePeriod[FuncUnstId.Atr.ordinal()];
      while( (outIdx!=0) ) {
         prevATR[0] *= (optInTimePeriod-1);
         prevATR[0] += tempBuffer[today++];
         prevATR[0] /= optInTimePeriod;
         outIdx -= 1;
      }
      outIdx = 1;
      outReal[0] = prevATR[0];
      nbATR = ((endIdx-startIdx)+1);
      while( (--nbATR!=0) ) {
         prevATR[0] *= (optInTimePeriod-1);
         prevATR[0] += tempBuffer[today++];
         prevATR[0] /= optInTimePeriod;
         outReal[outIdx++] = prevATR[0];
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outIdx;
      return retCode ;
   }
   public RetCode atr( int startIdx,
                       int endIdx,
                       float inHigh[],
                       float inLow[],
                       float inClose[],
                       int optInTimePeriod,
                       MInteger outBegIdx,
                       MInteger outNBElement,
                       double outReal[] )
   {
      RetCode retCode;
      int outIdx = 0;
      int today = 0;
      int lookbackTotal = 0;
      int nbATR = 0;
      MInteger outBegIdx1 = new MInteger();
      MInteger outNbElement1 = new MInteger();
      double[] prevATR = new double[1];
      double[] tempBuffer;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      outBegIdx.value = 0;
      outNBElement.value = 0;
      lookbackTotal = atrLookback(optInTimePeriod);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         return RetCode.Success ;
      }
      if( (optInTimePeriod<=1) ) {
         return trangeLogic(startIdx, endIdx, inHigh, inLow, inClose, outBegIdx, outNBElement, outReal) ;
      }
      tempBuffer = new double[(int)((((lookbackTotal+(endIdx-startIdx))+1)*1))];
      retCode = trangeLogic(((startIdx-lookbackTotal)+1), endIdx, inHigh, inLow, inClose, outBegIdx1, outNbElement1, tempBuffer);
      if( (retCode!=RetCode.Success) ) {
         return retCode ;
      }
      retCode = smaLogic((optInTimePeriod-1), (optInTimePeriod-1), tempBuffer, optInTimePeriod, outBegIdx1, outNbElement1, prevATR);
      if( (retCode!=RetCode.Success) ) {
         return retCode ;
      }
      today = optInTimePeriod;
      outIdx = this.unstablePeriod[FuncUnstId.Atr.ordinal()];
      while( (outIdx!=0) ) {
         prevATR[0] *= (optInTimePeriod-1);
         prevATR[0] += tempBuffer[today++];
         prevATR[0] /= optInTimePeriod;
         outIdx -= 1;
      }
      outIdx = 1;
      outReal[0] = prevATR[0];
      nbATR = ((endIdx-startIdx)+1);
      while( (--nbATR!=0) ) {
         prevATR[0] *= (optInTimePeriod-1);
         prevATR[0] += tempBuffer[today++];
         prevATR[0] /= optInTimePeriod;
         outReal[outIdx++] = prevATR[0];
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outIdx;
      return retCode ;
   }
   public RetCode atrLogic( int startIdx,
                            int endIdx,
                            float inHigh[],
                            float inLow[],
                            float inClose[],
                            int optInTimePeriod,
                            MInteger outBegIdx,
                            MInteger outNBElement,
                            double outReal[] )
   {
      RetCode retCode;
      int outIdx = 0;
      int today = 0;
      int lookbackTotal = 0;
      int nbATR = 0;
      MInteger outBegIdx1 = new MInteger();
      MInteger outNbElement1 = new MInteger();
      double[] prevATR = new double[1];
      double[] tempBuffer;
      outBegIdx.value = 0;
      outNBElement.value = 0;
      lookbackTotal = atrLookback(optInTimePeriod);
      if( (startIdx<lookbackTotal) ) {
         startIdx = lookbackTotal;
      }
      if( (startIdx>endIdx) ) {
         return RetCode.Success ;
      }
      if( (optInTimePeriod<=1) ) {
         return trangeLogic(startIdx, endIdx, inHigh, inLow, inClose, outBegIdx, outNBElement, outReal) ;
      }
      tempBuffer = new double[(int)((((lookbackTotal+(endIdx-startIdx))+1)*1))];
      retCode = trangeLogic(((startIdx-lookbackTotal)+1), endIdx, inHigh, inLow, inClose, outBegIdx1, outNbElement1, tempBuffer);
      if( (retCode!=RetCode.Success) ) {
         return retCode ;
      }
      retCode = smaLogic((optInTimePeriod-1), (optInTimePeriod-1), tempBuffer, optInTimePeriod, outBegIdx1, outNbElement1, prevATR);
      if( (retCode!=RetCode.Success) ) {
         return retCode ;
      }
      today = optInTimePeriod;
      outIdx = this.unstablePeriod[FuncUnstId.Atr.ordinal()];
      while( (outIdx!=0) ) {
         prevATR[0] *= (optInTimePeriod-1);
         prevATR[0] += tempBuffer[today++];
         prevATR[0] /= optInTimePeriod;
         outIdx -= 1;
      }
      outIdx = 1;
      outReal[0] = prevATR[0];
      nbATR = ((endIdx-startIdx)+1);
      while( (--nbATR!=0) ) {
         prevATR[0] *= (optInTimePeriod-1);
         prevATR[0] += tempBuffer[today++];
         prevATR[0] /= optInTimePeriod;
         outReal[outIdx++] = prevATR[0];
      }
      outBegIdx.value = startIdx;
      outNBElement.value = outIdx;
      return retCode ;
   }
