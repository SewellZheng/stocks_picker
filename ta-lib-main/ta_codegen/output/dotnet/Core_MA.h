         static int MaLookback( int           optInTimePeriod  /* From 1 to 100000 */, int           optInMAType );

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode Ma( int    startIdx,
                                       int    endIdx,
                                       SubArray<double>^ inReal,
                                       int optInTimePeriod,
                                       int optInMAType,
                                       [Out]int%    outBegIdx,
                                       [Out]int%    outNBElement,
                                       SubArray<double>^  outReal );

         static enum class RetCode Ma( int    startIdx,
                                       int    endIdx,
                                       SubArray<float>^ inReal,
                                       int optInTimePeriod,
                                       int optInMAType,
                                       [Out]int%    outBegIdx,
                                       [Out]int%    outNBElement,
                                       SubArray<double>^  outReal );

         static enum class RetCode Ma( int    startIdx,
                                       int    endIdx,
                                       cli::array<double>^ inReal,
                                       int optInTimePeriod,
                                       int optInMAType,
                                       [Out]int%    outBegIdx,
                                       [Out]int%    outNBElement,
                                       cli::array<double>^  outReal )
         { return Ma( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal,0),
                         optInTimePeriod,
                         optInMAType,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode Ma( int    startIdx,
                                       int    endIdx,
                                       cli::array<float>^ inReal,
                                       int optInTimePeriod,
                                       int optInMAType,
                                       [Out]int%    outBegIdx,
                                       [Out]int%    outNBElement,
                                       cli::array<double>^  outReal )
         { return Ma( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal,0),
                         optInTimePeriod,
                         optInMAType,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode Ma( int    startIdx,
                                       int    endIdx,
                                       cli::array<double>^ inReal,
                                       int optInTimePeriod,
                                       int optInMAType,
                                       [Out]int%    outBegIdx,
                                       [Out]int%    outNBElement,
                                       cli::array<double>^  outReal );
         static enum class RetCode Ma( int    startIdx,
                                       int    endIdx,
                                       cli::array<float>^ inReal,
                                       int optInTimePeriod,
                                       int optInMAType,
                                       [Out]int%    outBegIdx,
                                       [Out]int%    outNBElement,
                                       cli::array<double>^  outReal );
#endif

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode MaLogic( int    startIdx,
                                            int    endIdx,
                                            SubArray<double>^ inReal,
                                            int optInTimePeriod,
                                            int optInMAType,
                                            [Out]int%    outBegIdx,
                                            [Out]int%    outNBElement,
                                            SubArray<double>^  outReal );

         static enum class RetCode MaLogic( int    startIdx,
                                            int    endIdx,
                                            SubArray<float>^ inReal,
                                            int optInTimePeriod,
                                            int optInMAType,
                                            [Out]int%    outBegIdx,
                                            [Out]int%    outNBElement,
                                            SubArray<double>^  outReal );

         static enum class RetCode MaLogic( int    startIdx,
                                            int    endIdx,
                                            cli::array<double>^ inReal,
                                            int optInTimePeriod,
                                            int optInMAType,
                                            [Out]int%    outBegIdx,
                                            [Out]int%    outNBElement,
                                            cli::array<double>^  outReal )
         { return MaLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal,0),
                         optInTimePeriod,
                         optInMAType,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode MaLogic( int    startIdx,
                                            int    endIdx,
                                            cli::array<float>^ inReal,
                                            int optInTimePeriod,
                                            int optInMAType,
                                            [Out]int%    outBegIdx,
                                            [Out]int%    outNBElement,
                                            cli::array<double>^  outReal )
         { return MaLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal,0),
                         optInTimePeriod,
                         optInMAType,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode MaLogic( int    startIdx,
                                            int    endIdx,
                                            cli::array<double>^ inReal,
                                            int optInTimePeriod,
                                            int optInMAType,
                                            [Out]int%    outBegIdx,
                                            [Out]int%    outNBElement,
                                            cli::array<double>^  outReal );
         static enum class RetCode MaLogic( int    startIdx,
                                            int    endIdx,
                                            cli::array<float>^ inReal,
                                            int optInTimePeriod,
                                            int optInMAType,
                                            [Out]int%    outBegIdx,
                                            [Out]int%    outNBElement,
                                            cli::array<double>^  outReal );
#endif

         #define TA_MA Core::Ma
         #define TA_MA_Lookback Core::MaLookback
         #define TA_MA_Logic Core::MaLogic
