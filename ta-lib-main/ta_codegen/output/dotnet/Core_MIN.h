         static int MinLookback( int           optInTimePeriod  /* From 2 to 100000 */ );

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode Min( int    startIdx,
                                        int    endIdx,
                                        SubArray<double>^ inReal,
                                        int optInTimePeriod,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        SubArray<double>^  outReal );

         static enum class RetCode Min( int    startIdx,
                                        int    endIdx,
                                        SubArray<float>^ inReal,
                                        int optInTimePeriod,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        SubArray<double>^  outReal );

         static enum class RetCode Min( int    startIdx,
                                        int    endIdx,
                                        cli::array<double>^ inReal,
                                        int optInTimePeriod,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal )
         { return Min( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode Min( int    startIdx,
                                        int    endIdx,
                                        cli::array<float>^ inReal,
                                        int optInTimePeriod,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal )
         { return Min( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode Min( int    startIdx,
                                        int    endIdx,
                                        cli::array<double>^ inReal,
                                        int optInTimePeriod,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal );
         static enum class RetCode Min( int    startIdx,
                                        int    endIdx,
                                        cli::array<float>^ inReal,
                                        int optInTimePeriod,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal );
#endif

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode MinLogic( int    startIdx,
                                             int    endIdx,
                                             SubArray<double>^ inReal,
                                             int optInTimePeriod,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             SubArray<double>^  outReal );

         static enum class RetCode MinLogic( int    startIdx,
                                             int    endIdx,
                                             SubArray<float>^ inReal,
                                             int optInTimePeriod,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             SubArray<double>^  outReal );

         static enum class RetCode MinLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<double>^ inReal,
                                             int optInTimePeriod,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal )
         { return MinLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode MinLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<float>^ inReal,
                                             int optInTimePeriod,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal )
         { return MinLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode MinLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<double>^ inReal,
                                             int optInTimePeriod,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal );
         static enum class RetCode MinLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<float>^ inReal,
                                             int optInTimePeriod,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal );
#endif

         #define TA_MIN Core::Min
         #define TA_MIN_Lookback Core::MinLookback
         #define TA_MIN_Logic Core::MinLogic
