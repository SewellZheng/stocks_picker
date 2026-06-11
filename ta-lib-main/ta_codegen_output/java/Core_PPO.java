/* Generated */
   public int ppoLookback( int optInFastPeriod, int optInSlowPeriod, MAType optInMAType )
   {
      return maLookback(Math.max(optInSlowPeriod, optInFastPeriod), optInMAType) ;

   }
   public RetCode ppo( int startIdx,
                       int endIdx,
                       double inReal[],
                       int optInFastPeriod,
                       int optInSlowPeriod,
                       MAType optInMAType,
                       MInteger outBegIdx,
                       MInteger outNBElement,
                       double outReal[] )
   {
      double[] tempBuffer;
      RetCode retCode;
      double tempReal = 0;
      int tempInteger = 0;
      MInteger outBegIdx1 = new MInteger();
      MInteger outNbElement1 = new MInteger();
      MInteger outBegIdx2 = new MInteger();
      MInteger outNbElement2 = new MInteger();
      int i = 0;
      int j = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      tempBuffer = new double[(int)((((endIdx-startIdx)+1)*1))];
      if( (optInSlowPeriod<optInFastPeriod) ) {
         tempInteger = optInSlowPeriod;
         optInSlowPeriod = optInFastPeriod;
         optInFastPeriod = tempInteger;
      }
      retCode = maLogic(startIdx, endIdx, inReal, optInFastPeriod, optInMAType, outBegIdx2, outNbElement2, tempBuffer);
      if( (retCode==RetCode.Success) ) {
         retCode = maLogic(startIdx, endIdx, inReal, optInSlowPeriod, optInMAType, outBegIdx1, outNbElement1, outReal);
         if( (retCode==RetCode.Success) ) {
            tempInteger = (outBegIdx1.value-outBegIdx2.value);
            for( i = 0, j = tempInteger; (i<outNbElement1.value); i += 1, j += 1 ) {
               tempReal = outReal[i];
               if( !(((-0.00000000000001 < tempReal) && (tempReal < 0.00000000000001))) ) {
                  outReal[i] = (((tempBuffer[j]-tempReal)/tempReal)*100.0);
               } else {
                  outReal[i] = 0.0;
               }
            }
            outBegIdx.value = outBegIdx1.value;
            outNBElement.value = outNbElement1.value;
         }
      }
      return retCode ;
   }
   public RetCode ppoLogic( int startIdx,
                            int endIdx,
                            double inReal[],
                            int optInFastPeriod,
                            int optInSlowPeriod,
                            MAType optInMAType,
                            MInteger outBegIdx,
                            MInteger outNBElement,
                            double outReal[] )
   {
      double[] tempBuffer;
      RetCode retCode;
      double tempReal = 0;
      int tempInteger = 0;
      MInteger outBegIdx1 = new MInteger();
      MInteger outNbElement1 = new MInteger();
      MInteger outBegIdx2 = new MInteger();
      MInteger outNbElement2 = new MInteger();
      int i = 0;
      int j = 0;
      tempBuffer = new double[(int)((((endIdx-startIdx)+1)*1))];
      if( (optInSlowPeriod<optInFastPeriod) ) {
         tempInteger = optInSlowPeriod;
         optInSlowPeriod = optInFastPeriod;
         optInFastPeriod = tempInteger;
      }
      retCode = maLogic(startIdx, endIdx, inReal, optInFastPeriod, optInMAType, outBegIdx2, outNbElement2, tempBuffer);
      if( (retCode==RetCode.Success) ) {
         retCode = maLogic(startIdx, endIdx, inReal, optInSlowPeriod, optInMAType, outBegIdx1, outNbElement1, outReal);
         if( (retCode==RetCode.Success) ) {
            tempInteger = (outBegIdx1.value-outBegIdx2.value);
            for( i = 0, j = tempInteger; (i<outNbElement1.value); i += 1, j += 1 ) {
               tempReal = outReal[i];
               if( !(((-0.00000000000001 < tempReal) && (tempReal < 0.00000000000001))) ) {
                  outReal[i] = (((tempBuffer[j]-tempReal)/tempReal)*100.0);
               } else {
                  outReal[i] = 0.0;
               }
            }
            outBegIdx.value = outBegIdx1.value;
            outNBElement.value = outNbElement1.value;
         }
      }
      return retCode ;
   }
   public RetCode ppo( int startIdx,
                       int endIdx,
                       float inReal[],
                       int optInFastPeriod,
                       int optInSlowPeriod,
                       MAType optInMAType,
                       MInteger outBegIdx,
                       MInteger outNBElement,
                       double outReal[] )
   {
      double[] tempBuffer;
      RetCode retCode;
      double tempReal = 0;
      int tempInteger = 0;
      MInteger outBegIdx1 = new MInteger();
      MInteger outNbElement1 = new MInteger();
      MInteger outBegIdx2 = new MInteger();
      MInteger outNbElement2 = new MInteger();
      int i = 0;
      int j = 0;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      tempBuffer = new double[(int)((((endIdx-startIdx)+1)*1))];
      if( (optInSlowPeriod<optInFastPeriod) ) {
         tempInteger = optInSlowPeriod;
         optInSlowPeriod = optInFastPeriod;
         optInFastPeriod = tempInteger;
      }
      retCode = maLogic(startIdx, endIdx, inReal, optInFastPeriod, optInMAType, outBegIdx2, outNbElement2, tempBuffer);
      if( (retCode==RetCode.Success) ) {
         retCode = maLogic(startIdx, endIdx, inReal, optInSlowPeriod, optInMAType, outBegIdx1, outNbElement1, outReal);
         if( (retCode==RetCode.Success) ) {
            tempInteger = (outBegIdx1.value-outBegIdx2.value);
            for( i = 0, j = tempInteger; (i<outNbElement1.value); i += 1, j += 1 ) {
               tempReal = outReal[i];
               if( !(((-0.00000000000001 < tempReal) && (tempReal < 0.00000000000001))) ) {
                  outReal[i] = (((tempBuffer[j]-tempReal)/tempReal)*100.0);
               } else {
                  outReal[i] = 0.0;
               }
            }
            outBegIdx.value = outBegIdx1.value;
            outNBElement.value = outNbElement1.value;
         }
      }
      return retCode ;
   }
   public RetCode ppoLogic( int startIdx,
                            int endIdx,
                            float inReal[],
                            int optInFastPeriod,
                            int optInSlowPeriod,
                            MAType optInMAType,
                            MInteger outBegIdx,
                            MInteger outNBElement,
                            double outReal[] )
   {
      double[] tempBuffer;
      RetCode retCode;
      double tempReal = 0;
      int tempInteger = 0;
      MInteger outBegIdx1 = new MInteger();
      MInteger outNbElement1 = new MInteger();
      MInteger outBegIdx2 = new MInteger();
      MInteger outNbElement2 = new MInteger();
      int i = 0;
      int j = 0;
      tempBuffer = new double[(int)((((endIdx-startIdx)+1)*1))];
      if( (optInSlowPeriod<optInFastPeriod) ) {
         tempInteger = optInSlowPeriod;
         optInSlowPeriod = optInFastPeriod;
         optInFastPeriod = tempInteger;
      }
      retCode = maLogic(startIdx, endIdx, inReal, optInFastPeriod, optInMAType, outBegIdx2, outNbElement2, tempBuffer);
      if( (retCode==RetCode.Success) ) {
         retCode = maLogic(startIdx, endIdx, inReal, optInSlowPeriod, optInMAType, outBegIdx1, outNbElement1, outReal);
         if( (retCode==RetCode.Success) ) {
            tempInteger = (outBegIdx1.value-outBegIdx2.value);
            for( i = 0, j = tempInteger; (i<outNbElement1.value); i += 1, j += 1 ) {
               tempReal = outReal[i];
               if( !(((-0.00000000000001 < tempReal) && (tempReal < 0.00000000000001))) ) {
                  outReal[i] = (((tempBuffer[j]-tempReal)/tempReal)*100.0);
               } else {
                  outReal[i] = 0.0;
               }
            }
            outBegIdx.value = outBegIdx1.value;
            outNBElement.value = outNbElement1.value;
         }
      }
      return retCode ;
   }
