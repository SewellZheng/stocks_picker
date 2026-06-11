         static int Log10Lookback( void );

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode Log10( int    startIdx,
                                          int    endIdx,
                                          SubArray<double>^ inReal,
                                          [Out]int%    outBegIdx,
                                          [Out]int%    outNBElement,
                                          SubArray<double>^  outReal );

         static enum class RetCode Log10( int    startIdx,
                                          int    endIdx,
                                          SubArray<float>^ inReal,
                                          [Out]int%    outBegIdx,
                                          [Out]int%    outNBElement,
                                          SubArray<double>^  outReal );

         static enum class RetCode Log10( int    startIdx,
                                          int    endIdx,
                                          cli::array<double>^ inReal,
                                          [Out]int%    outBegIdx,
                                          [Out]int%    outNBElement,
                                          cli::array<double>^  outReal )
         { return Log10( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode Log10( int    startIdx,
                                          int    endIdx,
                                          cli::array<float>^ inReal,
                                          [Out]int%    outBegIdx,
                                          [Out]int%    outNBElement,
                                          cli::array<double>^  outReal )
         { return Log10( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode Log10( int    startIdx,
                                          int    endIdx,
                                          cli::array<double>^ inReal,
                                          [Out]int%    outBegIdx,
                                          [Out]int%    outNBElement,
                                          cli::array<double>^  outReal );
         static enum class RetCode Log10( int    startIdx,
                                          int    endIdx,
                                          cli::array<float>^ inReal,
                                          [Out]int%    outBegIdx,
                                          [Out]int%    outNBElement,
                                          cli::array<double>^  outReal );
#endif

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode Log10Logic( int    startIdx,
                                               int    endIdx,
                                               SubArray<double>^ inReal,
                                               [Out]int%    outBegIdx,
                                               [Out]int%    outNBElement,
                                               SubArray<double>^  outReal );

         static enum class RetCode Log10Logic( int    startIdx,
                                               int    endIdx,
                                               SubArray<float>^ inReal,
                                               [Out]int%    outBegIdx,
                                               [Out]int%    outNBElement,
                                               SubArray<double>^  outReal );

         static enum class RetCode Log10Logic( int    startIdx,
                                               int    endIdx,
                                               cli::array<double>^ inReal,
                                               [Out]int%    outBegIdx,
                                               [Out]int%    outNBElement,
                                               cli::array<double>^  outReal )
         { return Log10Logic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode Log10Logic( int    startIdx,
                                               int    endIdx,
                                               cli::array<float>^ inReal,
                                               [Out]int%    outBegIdx,
                                               [Out]int%    outNBElement,
                                               cli::array<double>^  outReal )
         { return Log10Logic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode Log10Logic( int    startIdx,
                                               int    endIdx,
                                               cli::array<double>^ inReal,
                                               [Out]int%    outBegIdx,
                                               [Out]int%    outNBElement,
                                               cli::array<double>^  outReal );
         static enum class RetCode Log10Logic( int    startIdx,
                                               int    endIdx,
                                               cli::array<float>^ inReal,
                                               [Out]int%    outBegIdx,
                                               [Out]int%    outNBElement,
                                               cli::array<double>^  outReal );
#endif

         #define TA_LOG10 Core::Log10
         #define TA_LOG10_Lookback Core::Log10Lookback
         #define TA_LOG10_Logic Core::Log10Logic
