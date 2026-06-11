         static int ApoLookback( int           optInFastPeriod  /* From 2 to 100000 */, int           optInSlowPeriod  /* From 2 to 100000 */, int           optInMAType );

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode Apo( int    startIdx,
                                        int    endIdx,
                                        SubArray<double>^ inReal,
                                        int optInFastPeriod,
                                        int optInSlowPeriod,
                                        int optInMAType,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        SubArray<double>^  outReal );

         static enum class RetCode Apo( int    startIdx,
                                        int    endIdx,
                                        SubArray<float>^ inReal,
                                        int optInFastPeriod,
                                        int optInSlowPeriod,
                                        int optInMAType,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        SubArray<double>^  outReal );

         static enum class RetCode Apo( int    startIdx,
                                        int    endIdx,
                                        cli::array<double>^ inReal,
                                        int optInFastPeriod,
                                        int optInSlowPeriod,
                                        int optInMAType,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal )
         { return Apo( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal,0),
                         optInFastPeriod,
                         optInSlowPeriod,
                         optInMAType,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode Apo( int    startIdx,
                                        int    endIdx,
                                        cli::array<float>^ inReal,
                                        int optInFastPeriod,
                                        int optInSlowPeriod,
                                        int optInMAType,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal )
         { return Apo( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal,0),
                         optInFastPeriod,
                         optInSlowPeriod,
                         optInMAType,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode Apo( int    startIdx,
                                        int    endIdx,
                                        cli::array<double>^ inReal,
                                        int optInFastPeriod,
                                        int optInSlowPeriod,
                                        int optInMAType,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal );
         static enum class RetCode Apo( int    startIdx,
                                        int    endIdx,
                                        cli::array<float>^ inReal,
                                        int optInFastPeriod,
                                        int optInSlowPeriod,
                                        int optInMAType,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal );
#endif

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode ApoLogic( int    startIdx,
                                             int    endIdx,
                                             SubArray<double>^ inReal,
                                             int optInFastPeriod,
                                             int optInSlowPeriod,
                                             int optInMAType,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             SubArray<double>^  outReal );

         static enum class RetCode ApoLogic( int    startIdx,
                                             int    endIdx,
                                             SubArray<float>^ inReal,
                                             int optInFastPeriod,
                                             int optInSlowPeriod,
                                             int optInMAType,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             SubArray<double>^  outReal );

         static enum class RetCode ApoLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<double>^ inReal,
                                             int optInFastPeriod,
                                             int optInSlowPeriod,
                                             int optInMAType,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal )
         { return ApoLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal,0),
                         optInFastPeriod,
                         optInSlowPeriod,
                         optInMAType,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode ApoLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<float>^ inReal,
                                             int optInFastPeriod,
                                             int optInSlowPeriod,
                                             int optInMAType,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal )
         { return ApoLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal,0),
                         optInFastPeriod,
                         optInSlowPeriod,
                         optInMAType,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode ApoLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<double>^ inReal,
                                             int optInFastPeriod,
                                             int optInSlowPeriod,
                                             int optInMAType,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal );
         static enum class RetCode ApoLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<float>^ inReal,
                                             int optInFastPeriod,
                                             int optInSlowPeriod,
                                             int optInMAType,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal );
#endif

         #define TA_APO Core::Apo
         #define TA_APO_Lookback Core::ApoLookback
         #define TA_APO_Logic Core::ApoLogic
