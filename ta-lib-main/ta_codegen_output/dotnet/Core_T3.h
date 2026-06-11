         static int T3Lookback( int           optInTimePeriod  /* From 2 to 100000 */, double           optInVFactor  /* From 0 to 1 */ );

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode T3( int    startIdx,
                                       int    endIdx,
                                       SubArray<double>^ inReal,
                                       int optInTimePeriod,
                                       double optInVFactor,
                                       [Out]int%    outBegIdx,
                                       [Out]int%    outNBElement,
                                       SubArray<double>^  outReal );

         static enum class RetCode T3( int    startIdx,
                                       int    endIdx,
                                       SubArray<float>^ inReal,
                                       int optInTimePeriod,
                                       double optInVFactor,
                                       [Out]int%    outBegIdx,
                                       [Out]int%    outNBElement,
                                       SubArray<double>^  outReal );

         static enum class RetCode T3( int    startIdx,
                                       int    endIdx,
                                       cli::array<double>^ inReal,
                                       int optInTimePeriod,
                                       double optInVFactor,
                                       [Out]int%    outBegIdx,
                                       [Out]int%    outNBElement,
                                       cli::array<double>^  outReal )
         { return T3( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal,0),
                         optInTimePeriod,
                         optInVFactor,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode T3( int    startIdx,
                                       int    endIdx,
                                       cli::array<float>^ inReal,
                                       int optInTimePeriod,
                                       double optInVFactor,
                                       [Out]int%    outBegIdx,
                                       [Out]int%    outNBElement,
                                       cli::array<double>^  outReal )
         { return T3( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal,0),
                         optInTimePeriod,
                         optInVFactor,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode T3( int    startIdx,
                                       int    endIdx,
                                       cli::array<double>^ inReal,
                                       int optInTimePeriod,
                                       double optInVFactor,
                                       [Out]int%    outBegIdx,
                                       [Out]int%    outNBElement,
                                       cli::array<double>^  outReal );
         static enum class RetCode T3( int    startIdx,
                                       int    endIdx,
                                       cli::array<float>^ inReal,
                                       int optInTimePeriod,
                                       double optInVFactor,
                                       [Out]int%    outBegIdx,
                                       [Out]int%    outNBElement,
                                       cli::array<double>^  outReal );
#endif

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode T3Logic( int    startIdx,
                                            int    endIdx,
                                            SubArray<double>^ inReal,
                                            int optInTimePeriod,
                                            double optInVFactor,
                                            [Out]int%    outBegIdx,
                                            [Out]int%    outNBElement,
                                            SubArray<double>^  outReal );

         static enum class RetCode T3Logic( int    startIdx,
                                            int    endIdx,
                                            SubArray<float>^ inReal,
                                            int optInTimePeriod,
                                            double optInVFactor,
                                            [Out]int%    outBegIdx,
                                            [Out]int%    outNBElement,
                                            SubArray<double>^  outReal );

         static enum class RetCode T3Logic( int    startIdx,
                                            int    endIdx,
                                            cli::array<double>^ inReal,
                                            int optInTimePeriod,
                                            double optInVFactor,
                                            [Out]int%    outBegIdx,
                                            [Out]int%    outNBElement,
                                            cli::array<double>^  outReal )
         { return T3Logic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal,0),
                         optInTimePeriod,
                         optInVFactor,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode T3Logic( int    startIdx,
                                            int    endIdx,
                                            cli::array<float>^ inReal,
                                            int optInTimePeriod,
                                            double optInVFactor,
                                            [Out]int%    outBegIdx,
                                            [Out]int%    outNBElement,
                                            cli::array<double>^  outReal )
         { return T3Logic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal,0),
                         optInTimePeriod,
                         optInVFactor,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode T3Logic( int    startIdx,
                                            int    endIdx,
                                            cli::array<double>^ inReal,
                                            int optInTimePeriod,
                                            double optInVFactor,
                                            [Out]int%    outBegIdx,
                                            [Out]int%    outNBElement,
                                            cli::array<double>^  outReal );
         static enum class RetCode T3Logic( int    startIdx,
                                            int    endIdx,
                                            cli::array<float>^ inReal,
                                            int optInTimePeriod,
                                            double optInVFactor,
                                            [Out]int%    outBegIdx,
                                            [Out]int%    outNBElement,
                                            cli::array<double>^  outReal );
#endif

         #define TA_T3 Core::T3
         #define TA_T3_Lookback Core::T3Lookback
         #define TA_T3_Logic Core::T3Logic
