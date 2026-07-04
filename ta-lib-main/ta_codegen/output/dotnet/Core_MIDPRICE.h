         static int MidpriceLookback( int           optInTimePeriod  /* From 2 to 100000 */ );

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode Midprice( int    startIdx,
                                             int    endIdx,
                                             SubArray<double>^ inHigh,
                                             SubArray<double>^ inLow,
                                             int optInTimePeriod,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             SubArray<double>^  outReal );

         static enum class RetCode Midprice( int    startIdx,
                                             int    endIdx,
                                             SubArray<float>^ inHigh,
                                             SubArray<float>^ inLow,
                                             int optInTimePeriod,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             SubArray<double>^  outReal );

         static enum class RetCode Midprice( int    startIdx,
                                             int    endIdx,
                                             cli::array<double>^ inHigh,
                                             cli::array<double>^ inLow,
                                             int optInTimePeriod,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal )
         { return Midprice( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inHigh,0),
                         gcnew SubArrayFrom1D<double>(inLow,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode Midprice( int    startIdx,
                                             int    endIdx,
                                             cli::array<float>^ inHigh,
                                             cli::array<float>^ inLow,
                                             int optInTimePeriod,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal )
         { return Midprice( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inHigh,0),
                         gcnew SubArrayFrom1D<float>(inLow,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode Midprice( int    startIdx,
                                             int    endIdx,
                                             cli::array<double>^ inHigh,
                                             cli::array<double>^ inLow,
                                             int optInTimePeriod,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal );
         static enum class RetCode Midprice( int    startIdx,
                                             int    endIdx,
                                             cli::array<float>^ inHigh,
                                             cli::array<float>^ inLow,
                                             int optInTimePeriod,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal );
#endif

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode MidpriceLogic( int    startIdx,
                                                  int    endIdx,
                                                  SubArray<double>^ inHigh,
                                                  SubArray<double>^ inLow,
                                                  int optInTimePeriod,
                                                  [Out]int%    outBegIdx,
                                                  [Out]int%    outNBElement,
                                                  SubArray<double>^  outReal );

         static enum class RetCode MidpriceLogic( int    startIdx,
                                                  int    endIdx,
                                                  SubArray<float>^ inHigh,
                                                  SubArray<float>^ inLow,
                                                  int optInTimePeriod,
                                                  [Out]int%    outBegIdx,
                                                  [Out]int%    outNBElement,
                                                  SubArray<double>^  outReal );

         static enum class RetCode MidpriceLogic( int    startIdx,
                                                  int    endIdx,
                                                  cli::array<double>^ inHigh,
                                                  cli::array<double>^ inLow,
                                                  int optInTimePeriod,
                                                  [Out]int%    outBegIdx,
                                                  [Out]int%    outNBElement,
                                                  cli::array<double>^  outReal )
         { return MidpriceLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inHigh,0),
                         gcnew SubArrayFrom1D<double>(inLow,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode MidpriceLogic( int    startIdx,
                                                  int    endIdx,
                                                  cli::array<float>^ inHigh,
                                                  cli::array<float>^ inLow,
                                                  int optInTimePeriod,
                                                  [Out]int%    outBegIdx,
                                                  [Out]int%    outNBElement,
                                                  cli::array<double>^  outReal )
         { return MidpriceLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inHigh,0),
                         gcnew SubArrayFrom1D<float>(inLow,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode MidpriceLogic( int    startIdx,
                                                  int    endIdx,
                                                  cli::array<double>^ inHigh,
                                                  cli::array<double>^ inLow,
                                                  int optInTimePeriod,
                                                  [Out]int%    outBegIdx,
                                                  [Out]int%    outNBElement,
                                                  cli::array<double>^  outReal );
         static enum class RetCode MidpriceLogic( int    startIdx,
                                                  int    endIdx,
                                                  cli::array<float>^ inHigh,
                                                  cli::array<float>^ inLow,
                                                  int optInTimePeriod,
                                                  [Out]int%    outBegIdx,
                                                  [Out]int%    outNBElement,
                                                  cli::array<double>^  outReal );
#endif

         #define TA_MIDPRICE Core::Midprice
         #define TA_MIDPRICE_Lookback Core::MidpriceLookback
         #define TA_MIDPRICE_Logic Core::MidpriceLogic
