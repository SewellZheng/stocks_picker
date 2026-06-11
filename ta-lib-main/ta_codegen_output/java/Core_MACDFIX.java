/* Generated */
   public int macdfixLookback( int optInSignalPeriod )
   {
      return (emaLookback(26)+emaLookback(optInSignalPeriod)) ;

   }
   public RetCode macdfix( int startIdx,
                           int endIdx,
                           double inReal[],
                           int optInSignalPeriod,
                           MInteger outBegIdx,
                           MInteger outNBElement,
                           double outMACD[],
                           double outMACDSignal[],
                           double outMACDHist[] )
   {
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      return macdLogic(startIdx, endIdx, inReal, 0, 0, optInSignalPeriod, outBegIdx, outNBElement, outMACD, outMACDSignal, outMACDHist) ;
   }
   public RetCode macdfixLogic( int startIdx,
                                int endIdx,
                                double inReal[],
                                int optInSignalPeriod,
                                MInteger outBegIdx,
                                MInteger outNBElement,
                                double outMACD[],
                                double outMACDSignal[],
                                double outMACDHist[] )
   {
      return macdLogic(startIdx, endIdx, inReal, 0, 0, optInSignalPeriod, outBegIdx, outNBElement, outMACD, outMACDSignal, outMACDHist) ;
   }
   public RetCode macdfix( int startIdx,
                           int endIdx,
                           float inReal[],
                           int optInSignalPeriod,
                           MInteger outBegIdx,
                           MInteger outNBElement,
                           double outMACD[],
                           double outMACDSignal[],
                           double outMACDHist[] )
   {
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      return macdLogic(startIdx, endIdx, inReal, 0, 0, optInSignalPeriod, outBegIdx, outNBElement, outMACD, outMACDSignal, outMACDHist) ;
   }
   public RetCode macdfixLogic( int startIdx,
                                int endIdx,
                                float inReal[],
                                int optInSignalPeriod,
                                MInteger outBegIdx,
                                MInteger outNBElement,
                                double outMACD[],
                                double outMACDSignal[],
                                double outMACDHist[] )
   {
      return macdLogic(startIdx, endIdx, inReal, 0, 0, optInSignalPeriod, outBegIdx, outNBElement, outMACD, outMACDSignal, outMACDHist) ;
   }
