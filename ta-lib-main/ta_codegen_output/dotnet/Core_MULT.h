         static int MultLookback( void );

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode Mult( int    startIdx,
                                         int    endIdx,
                                         SubArray<double>^ inReal0,
                                         SubArray<double>^ inReal1,
                                         [Out]int%    outBegIdx,
                                         [Out]int%    outNBElement,
                                         SubArray<double>^  outReal );

         static enum class RetCode Mult( int    startIdx,
                                         int    endIdx,
                                         SubArray<float>^ inReal0,
                                         SubArray<float>^ inReal1,
                                         [Out]int%    outBegIdx,
                                         [Out]int%    outNBElement,
                                         SubArray<double>^  outReal );

         static enum class RetCode Mult( int    startIdx,
                                         int    endIdx,
                                         cli::array<double>^ inReal0,
                                         cli::array<double>^ inReal1,
                                         [Out]int%    outBegIdx,
                                         [Out]int%    outNBElement,
                                         cli::array<double>^  outReal )
         { return Mult( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal0,0),
                         gcnew SubArrayFrom1D<double>(inReal1,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode Mult( int    startIdx,
                                         int    endIdx,
                                         cli::array<float>^ inReal0,
                                         cli::array<float>^ inReal1,
                                         [Out]int%    outBegIdx,
                                         [Out]int%    outNBElement,
                                         cli::array<double>^  outReal )
         { return Mult( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal0,0),
                         gcnew SubArrayFrom1D<float>(inReal1,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode Mult( int    startIdx,
                                         int    endIdx,
                                         cli::array<double>^ inReal0,
                                         cli::array<double>^ inReal1,
                                         [Out]int%    outBegIdx,
                                         [Out]int%    outNBElement,
                                         cli::array<double>^  outReal );
         static enum class RetCode Mult( int    startIdx,
                                         int    endIdx,
                                         cli::array<float>^ inReal0,
                                         cli::array<float>^ inReal1,
                                         [Out]int%    outBegIdx,
                                         [Out]int%    outNBElement,
                                         cli::array<double>^  outReal );
#endif

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode MultLogic( int    startIdx,
                                              int    endIdx,
                                              SubArray<double>^ inReal0,
                                              SubArray<double>^ inReal1,
                                              [Out]int%    outBegIdx,
                                              [Out]int%    outNBElement,
                                              SubArray<double>^  outReal );

         static enum class RetCode MultLogic( int    startIdx,
                                              int    endIdx,
                                              SubArray<float>^ inReal0,
                                              SubArray<float>^ inReal1,
                                              [Out]int%    outBegIdx,
                                              [Out]int%    outNBElement,
                                              SubArray<double>^  outReal );

         static enum class RetCode MultLogic( int    startIdx,
                                              int    endIdx,
                                              cli::array<double>^ inReal0,
                                              cli::array<double>^ inReal1,
                                              [Out]int%    outBegIdx,
                                              [Out]int%    outNBElement,
                                              cli::array<double>^  outReal )
         { return MultLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal0,0),
                         gcnew SubArrayFrom1D<double>(inReal1,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode MultLogic( int    startIdx,
                                              int    endIdx,
                                              cli::array<float>^ inReal0,
                                              cli::array<float>^ inReal1,
                                              [Out]int%    outBegIdx,
                                              [Out]int%    outNBElement,
                                              cli::array<double>^  outReal )
         { return MultLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal0,0),
                         gcnew SubArrayFrom1D<float>(inReal1,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode MultLogic( int    startIdx,
                                              int    endIdx,
                                              cli::array<double>^ inReal0,
                                              cli::array<double>^ inReal1,
                                              [Out]int%    outBegIdx,
                                              [Out]int%    outNBElement,
                                              cli::array<double>^  outReal );
         static enum class RetCode MultLogic( int    startIdx,
                                              int    endIdx,
                                              cli::array<float>^ inReal0,
                                              cli::array<float>^ inReal1,
                                              [Out]int%    outBegIdx,
                                              [Out]int%    outNBElement,
                                              cli::array<double>^  outReal );
#endif

         #define TA_MULT Core::Mult
         #define TA_MULT_Lookback Core::MultLookback
         #define TA_MULT_Logic Core::MultLogic
