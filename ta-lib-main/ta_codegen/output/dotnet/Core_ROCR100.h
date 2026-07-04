         static int Rocr100Lookback( int           optInTimePeriod  /* From 1 to 100000 */ );

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode Rocr100( int    startIdx,
                                            int    endIdx,
                                            SubArray<double>^ inReal,
                                            int optInTimePeriod,
                                            [Out]int%    outBegIdx,
                                            [Out]int%    outNBElement,
                                            SubArray<double>^  outReal );

         static enum class RetCode Rocr100( int    startIdx,
                                            int    endIdx,
                                            SubArray<float>^ inReal,
                                            int optInTimePeriod,
                                            [Out]int%    outBegIdx,
                                            [Out]int%    outNBElement,
                                            SubArray<double>^  outReal );

         static enum class RetCode Rocr100( int    startIdx,
                                            int    endIdx,
                                            cli::array<double>^ inReal,
                                            int optInTimePeriod,
                                            [Out]int%    outBegIdx,
                                            [Out]int%    outNBElement,
                                            cli::array<double>^  outReal )
         { return Rocr100( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode Rocr100( int    startIdx,
                                            int    endIdx,
                                            cli::array<float>^ inReal,
                                            int optInTimePeriod,
                                            [Out]int%    outBegIdx,
                                            [Out]int%    outNBElement,
                                            cli::array<double>^  outReal )
         { return Rocr100( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode Rocr100( int    startIdx,
                                            int    endIdx,
                                            cli::array<double>^ inReal,
                                            int optInTimePeriod,
                                            [Out]int%    outBegIdx,
                                            [Out]int%    outNBElement,
                                            cli::array<double>^  outReal );
         static enum class RetCode Rocr100( int    startIdx,
                                            int    endIdx,
                                            cli::array<float>^ inReal,
                                            int optInTimePeriod,
                                            [Out]int%    outBegIdx,
                                            [Out]int%    outNBElement,
                                            cli::array<double>^  outReal );
#endif

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode Rocr100Logic( int    startIdx,
                                                 int    endIdx,
                                                 SubArray<double>^ inReal,
                                                 int optInTimePeriod,
                                                 [Out]int%    outBegIdx,
                                                 [Out]int%    outNBElement,
                                                 SubArray<double>^  outReal );

         static enum class RetCode Rocr100Logic( int    startIdx,
                                                 int    endIdx,
                                                 SubArray<float>^ inReal,
                                                 int optInTimePeriod,
                                                 [Out]int%    outBegIdx,
                                                 [Out]int%    outNBElement,
                                                 SubArray<double>^  outReal );

         static enum class RetCode Rocr100Logic( int    startIdx,
                                                 int    endIdx,
                                                 cli::array<double>^ inReal,
                                                 int optInTimePeriod,
                                                 [Out]int%    outBegIdx,
                                                 [Out]int%    outNBElement,
                                                 cli::array<double>^  outReal )
         { return Rocr100Logic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode Rocr100Logic( int    startIdx,
                                                 int    endIdx,
                                                 cli::array<float>^ inReal,
                                                 int optInTimePeriod,
                                                 [Out]int%    outBegIdx,
                                                 [Out]int%    outNBElement,
                                                 cli::array<double>^  outReal )
         { return Rocr100Logic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode Rocr100Logic( int    startIdx,
                                                 int    endIdx,
                                                 cli::array<double>^ inReal,
                                                 int optInTimePeriod,
                                                 [Out]int%    outBegIdx,
                                                 [Out]int%    outNBElement,
                                                 cli::array<double>^  outReal );
         static enum class RetCode Rocr100Logic( int    startIdx,
                                                 int    endIdx,
                                                 cli::array<float>^ inReal,
                                                 int optInTimePeriod,
                                                 [Out]int%    outBegIdx,
                                                 [Out]int%    outNBElement,
                                                 cli::array<double>^  outReal );
#endif

         #define TA_ROCR100 Core::Rocr100
         #define TA_ROCR100_Lookback Core::Rocr100Lookback
         #define TA_ROCR100_Logic Core::Rocr100Logic
