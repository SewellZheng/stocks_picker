         static int Minus_dmLookback( int           optInTimePeriod  /* From 1 to 100000 */ );

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode Minus_dm( int    startIdx,
                                             int    endIdx,
                                             SubArray<double>^ inHigh,
                                             SubArray<double>^ inLow,
                                             int optInTimePeriod,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             SubArray<double>^  outReal );

         static enum class RetCode Minus_dm( int    startIdx,
                                             int    endIdx,
                                             SubArray<float>^ inHigh,
                                             SubArray<float>^ inLow,
                                             int optInTimePeriod,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             SubArray<double>^  outReal );

         static enum class RetCode Minus_dm( int    startIdx,
                                             int    endIdx,
                                             cli::array<double>^ inHigh,
                                             cli::array<double>^ inLow,
                                             int optInTimePeriod,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal )
         { return Minus_dm( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inHigh,0),
                         gcnew SubArrayFrom1D<double>(inLow,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode Minus_dm( int    startIdx,
                                             int    endIdx,
                                             cli::array<float>^ inHigh,
                                             cli::array<float>^ inLow,
                                             int optInTimePeriod,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal )
         { return Minus_dm( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inHigh,0),
                         gcnew SubArrayFrom1D<float>(inLow,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode Minus_dm( int    startIdx,
                                             int    endIdx,
                                             cli::array<double>^ inHigh,
                                             cli::array<double>^ inLow,
                                             int optInTimePeriod,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal );
         static enum class RetCode Minus_dm( int    startIdx,
                                             int    endIdx,
                                             cli::array<float>^ inHigh,
                                             cli::array<float>^ inLow,
                                             int optInTimePeriod,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal );
#endif

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode Minus_dmLogic( int    startIdx,
                                                  int    endIdx,
                                                  SubArray<double>^ inHigh,
                                                  SubArray<double>^ inLow,
                                                  int optInTimePeriod,
                                                  [Out]int%    outBegIdx,
                                                  [Out]int%    outNBElement,
                                                  SubArray<double>^  outReal );

         static enum class RetCode Minus_dmLogic( int    startIdx,
                                                  int    endIdx,
                                                  SubArray<float>^ inHigh,
                                                  SubArray<float>^ inLow,
                                                  int optInTimePeriod,
                                                  [Out]int%    outBegIdx,
                                                  [Out]int%    outNBElement,
                                                  SubArray<double>^  outReal );

         static enum class RetCode Minus_dmLogic( int    startIdx,
                                                  int    endIdx,
                                                  cli::array<double>^ inHigh,
                                                  cli::array<double>^ inLow,
                                                  int optInTimePeriod,
                                                  [Out]int%    outBegIdx,
                                                  [Out]int%    outNBElement,
                                                  cli::array<double>^  outReal )
         { return Minus_dmLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inHigh,0),
                         gcnew SubArrayFrom1D<double>(inLow,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode Minus_dmLogic( int    startIdx,
                                                  int    endIdx,
                                                  cli::array<float>^ inHigh,
                                                  cli::array<float>^ inLow,
                                                  int optInTimePeriod,
                                                  [Out]int%    outBegIdx,
                                                  [Out]int%    outNBElement,
                                                  cli::array<double>^  outReal )
         { return Minus_dmLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inHigh,0),
                         gcnew SubArrayFrom1D<float>(inLow,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode Minus_dmLogic( int    startIdx,
                                                  int    endIdx,
                                                  cli::array<double>^ inHigh,
                                                  cli::array<double>^ inLow,
                                                  int optInTimePeriod,
                                                  [Out]int%    outBegIdx,
                                                  [Out]int%    outNBElement,
                                                  cli::array<double>^  outReal );
         static enum class RetCode Minus_dmLogic( int    startIdx,
                                                  int    endIdx,
                                                  cli::array<float>^ inHigh,
                                                  cli::array<float>^ inLow,
                                                  int optInTimePeriod,
                                                  [Out]int%    outBegIdx,
                                                  [Out]int%    outNBElement,
                                                  cli::array<double>^  outReal );
#endif

         #define TA_MINUS_DM Core::Minus_dm
         #define TA_MINUS_DM_Lookback Core::Minus_dmLookback
         #define TA_MINUS_DM_Logic Core::Minus_dmLogic
