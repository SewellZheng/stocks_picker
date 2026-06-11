         static int AsinLookback( void );

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode Asin( int    startIdx,
                                         int    endIdx,
                                         SubArray<double>^ inReal,
                                         [Out]int%    outBegIdx,
                                         [Out]int%    outNBElement,
                                         SubArray<double>^  outReal );

         static enum class RetCode Asin( int    startIdx,
                                         int    endIdx,
                                         SubArray<float>^ inReal,
                                         [Out]int%    outBegIdx,
                                         [Out]int%    outNBElement,
                                         SubArray<double>^  outReal );

         static enum class RetCode Asin( int    startIdx,
                                         int    endIdx,
                                         cli::array<double>^ inReal,
                                         [Out]int%    outBegIdx,
                                         [Out]int%    outNBElement,
                                         cli::array<double>^  outReal )
         { return Asin( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode Asin( int    startIdx,
                                         int    endIdx,
                                         cli::array<float>^ inReal,
                                         [Out]int%    outBegIdx,
                                         [Out]int%    outNBElement,
                                         cli::array<double>^  outReal )
         { return Asin( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode Asin( int    startIdx,
                                         int    endIdx,
                                         cli::array<double>^ inReal,
                                         [Out]int%    outBegIdx,
                                         [Out]int%    outNBElement,
                                         cli::array<double>^  outReal );
         static enum class RetCode Asin( int    startIdx,
                                         int    endIdx,
                                         cli::array<float>^ inReal,
                                         [Out]int%    outBegIdx,
                                         [Out]int%    outNBElement,
                                         cli::array<double>^  outReal );
#endif

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode AsinLogic( int    startIdx,
                                              int    endIdx,
                                              SubArray<double>^ inReal,
                                              [Out]int%    outBegIdx,
                                              [Out]int%    outNBElement,
                                              SubArray<double>^  outReal );

         static enum class RetCode AsinLogic( int    startIdx,
                                              int    endIdx,
                                              SubArray<float>^ inReal,
                                              [Out]int%    outBegIdx,
                                              [Out]int%    outNBElement,
                                              SubArray<double>^  outReal );

         static enum class RetCode AsinLogic( int    startIdx,
                                              int    endIdx,
                                              cli::array<double>^ inReal,
                                              [Out]int%    outBegIdx,
                                              [Out]int%    outNBElement,
                                              cli::array<double>^  outReal )
         { return AsinLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode AsinLogic( int    startIdx,
                                              int    endIdx,
                                              cli::array<float>^ inReal,
                                              [Out]int%    outBegIdx,
                                              [Out]int%    outNBElement,
                                              cli::array<double>^  outReal )
         { return AsinLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode AsinLogic( int    startIdx,
                                              int    endIdx,
                                              cli::array<double>^ inReal,
                                              [Out]int%    outBegIdx,
                                              [Out]int%    outNBElement,
                                              cli::array<double>^  outReal );
         static enum class RetCode AsinLogic( int    startIdx,
                                              int    endIdx,
                                              cli::array<float>^ inReal,
                                              [Out]int%    outBegIdx,
                                              [Out]int%    outNBElement,
                                              cli::array<double>^  outReal );
#endif

         #define TA_ASIN Core::Asin
         #define TA_ASIN_Lookback Core::AsinLookback
         #define TA_ASIN_Logic Core::AsinLogic
