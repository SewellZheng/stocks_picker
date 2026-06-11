         static int PpoLookback( int           optInFastPeriod  /* From 2 to 100000 */, int           optInSlowPeriod  /* From 2 to 100000 */, int           optInMAType );

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode Ppo( int    startIdx,
                                        int    endIdx,
                                        SubArray<double>^ inReal,
                                        int optInFastPeriod,
                                        int optInSlowPeriod,
                                        int optInMAType,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        SubArray<double>^  outReal );

         static enum class RetCode Ppo( int    startIdx,
                                        int    endIdx,
                                        SubArray<float>^ inReal,
                                        int optInFastPeriod,
                                        int optInSlowPeriod,
                                        int optInMAType,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        SubArray<double>^  outReal );

         static enum class RetCode Ppo( int    startIdx,
                                        int    endIdx,
                                        cli::array<double>^ inReal,
                                        int optInFastPeriod,
                                        int optInSlowPeriod,
                                        int optInMAType,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal )
         { return Ppo( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal,0),
                         optInFastPeriod,
                         optInSlowPeriod,
                         optInMAType,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode Ppo( int    startIdx,
                                        int    endIdx,
                                        cli::array<float>^ inReal,
                                        int optInFastPeriod,
                                        int optInSlowPeriod,
                                        int optInMAType,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal )
         { return Ppo( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal,0),
                         optInFastPeriod,
                         optInSlowPeriod,
                         optInMAType,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode Ppo( int    startIdx,
                                        int    endIdx,
                                        cli::array<double>^ inReal,
                                        int optInFastPeriod,
                                        int optInSlowPeriod,
                                        int optInMAType,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal );
         static enum class RetCode Ppo( int    startIdx,
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
         static enum class RetCode PpoLogic( int    startIdx,
                                             int    endIdx,
                                             SubArray<double>^ inReal,
                                             int optInFastPeriod,
                                             int optInSlowPeriod,
                                             int optInMAType,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             SubArray<double>^  outReal );

         static enum class RetCode PpoLogic( int    startIdx,
                                             int    endIdx,
                                             SubArray<float>^ inReal,
                                             int optInFastPeriod,
                                             int optInSlowPeriod,
                                             int optInMAType,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             SubArray<double>^  outReal );

         static enum class RetCode PpoLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<double>^ inReal,
                                             int optInFastPeriod,
                                             int optInSlowPeriod,
                                             int optInMAType,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal )
         { return PpoLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal,0),
                         optInFastPeriod,
                         optInSlowPeriod,
                         optInMAType,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode PpoLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<float>^ inReal,
                                             int optInFastPeriod,
                                             int optInSlowPeriod,
                                             int optInMAType,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal )
         { return PpoLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal,0),
                         optInFastPeriod,
                         optInSlowPeriod,
                         optInMAType,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode PpoLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<double>^ inReal,
                                             int optInFastPeriod,
                                             int optInSlowPeriod,
                                             int optInMAType,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal );
         static enum class RetCode PpoLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<float>^ inReal,
                                             int optInFastPeriod,
                                             int optInSlowPeriod,
                                             int optInMAType,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal );
#endif

         #define TA_PPO Core::Ppo
         #define TA_PPO_Lookback Core::PpoLookback
         #define TA_PPO_Logic Core::PpoLogic
