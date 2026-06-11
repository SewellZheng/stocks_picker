         static int BetaLookback( int           optInTimePeriod  /* From 1 to 100000 */ );

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode Beta( int    startIdx,
                                         int    endIdx,
                                         SubArray<double>^ inReal0,
                                         SubArray<double>^ inReal1,
                                         int optInTimePeriod,
                                         [Out]int%    outBegIdx,
                                         [Out]int%    outNBElement,
                                         SubArray<double>^  outReal );

         static enum class RetCode Beta( int    startIdx,
                                         int    endIdx,
                                         SubArray<float>^ inReal0,
                                         SubArray<float>^ inReal1,
                                         int optInTimePeriod,
                                         [Out]int%    outBegIdx,
                                         [Out]int%    outNBElement,
                                         SubArray<double>^  outReal );

         static enum class RetCode Beta( int    startIdx,
                                         int    endIdx,
                                         cli::array<double>^ inReal0,
                                         cli::array<double>^ inReal1,
                                         int optInTimePeriod,
                                         [Out]int%    outBegIdx,
                                         [Out]int%    outNBElement,
                                         cli::array<double>^  outReal )
         { return Beta( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal0,0),
                         gcnew SubArrayFrom1D<double>(inReal1,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode Beta( int    startIdx,
                                         int    endIdx,
                                         cli::array<float>^ inReal0,
                                         cli::array<float>^ inReal1,
                                         int optInTimePeriod,
                                         [Out]int%    outBegIdx,
                                         [Out]int%    outNBElement,
                                         cli::array<double>^  outReal )
         { return Beta( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal0,0),
                         gcnew SubArrayFrom1D<float>(inReal1,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode Beta( int    startIdx,
                                         int    endIdx,
                                         cli::array<double>^ inReal0,
                                         cli::array<double>^ inReal1,
                                         int optInTimePeriod,
                                         [Out]int%    outBegIdx,
                                         [Out]int%    outNBElement,
                                         cli::array<double>^  outReal );
         static enum class RetCode Beta( int    startIdx,
                                         int    endIdx,
                                         cli::array<float>^ inReal0,
                                         cli::array<float>^ inReal1,
                                         int optInTimePeriod,
                                         [Out]int%    outBegIdx,
                                         [Out]int%    outNBElement,
                                         cli::array<double>^  outReal );
#endif

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode BetaLogic( int    startIdx,
                                              int    endIdx,
                                              SubArray<double>^ inReal0,
                                              SubArray<double>^ inReal1,
                                              int optInTimePeriod,
                                              [Out]int%    outBegIdx,
                                              [Out]int%    outNBElement,
                                              SubArray<double>^  outReal );

         static enum class RetCode BetaLogic( int    startIdx,
                                              int    endIdx,
                                              SubArray<float>^ inReal0,
                                              SubArray<float>^ inReal1,
                                              int optInTimePeriod,
                                              [Out]int%    outBegIdx,
                                              [Out]int%    outNBElement,
                                              SubArray<double>^  outReal );

         static enum class RetCode BetaLogic( int    startIdx,
                                              int    endIdx,
                                              cli::array<double>^ inReal0,
                                              cli::array<double>^ inReal1,
                                              int optInTimePeriod,
                                              [Out]int%    outBegIdx,
                                              [Out]int%    outNBElement,
                                              cli::array<double>^  outReal )
         { return BetaLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal0,0),
                         gcnew SubArrayFrom1D<double>(inReal1,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode BetaLogic( int    startIdx,
                                              int    endIdx,
                                              cli::array<float>^ inReal0,
                                              cli::array<float>^ inReal1,
                                              int optInTimePeriod,
                                              [Out]int%    outBegIdx,
                                              [Out]int%    outNBElement,
                                              cli::array<double>^  outReal )
         { return BetaLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal0,0),
                         gcnew SubArrayFrom1D<float>(inReal1,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode BetaLogic( int    startIdx,
                                              int    endIdx,
                                              cli::array<double>^ inReal0,
                                              cli::array<double>^ inReal1,
                                              int optInTimePeriod,
                                              [Out]int%    outBegIdx,
                                              [Out]int%    outNBElement,
                                              cli::array<double>^  outReal );
         static enum class RetCode BetaLogic( int    startIdx,
                                              int    endIdx,
                                              cli::array<float>^ inReal0,
                                              cli::array<float>^ inReal1,
                                              int optInTimePeriod,
                                              [Out]int%    outBegIdx,
                                              [Out]int%    outNBElement,
                                              cli::array<double>^  outReal );
#endif

         #define TA_BETA Core::Beta
         #define TA_BETA_Lookback Core::BetaLookback
         #define TA_BETA_Logic Core::BetaLogic
