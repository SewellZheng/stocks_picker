         static int TrixLookback( int           optInTimePeriod  /* From 1 to 100000 */ );

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode Trix( int    startIdx,
                                         int    endIdx,
                                         SubArray<double>^ inReal,
                                         int optInTimePeriod,
                                         [Out]int%    outBegIdx,
                                         [Out]int%    outNBElement,
                                         SubArray<double>^  outReal );

         static enum class RetCode Trix( int    startIdx,
                                         int    endIdx,
                                         SubArray<float>^ inReal,
                                         int optInTimePeriod,
                                         [Out]int%    outBegIdx,
                                         [Out]int%    outNBElement,
                                         SubArray<double>^  outReal );

         static enum class RetCode Trix( int    startIdx,
                                         int    endIdx,
                                         cli::array<double>^ inReal,
                                         int optInTimePeriod,
                                         [Out]int%    outBegIdx,
                                         [Out]int%    outNBElement,
                                         cli::array<double>^  outReal )
         { return Trix( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode Trix( int    startIdx,
                                         int    endIdx,
                                         cli::array<float>^ inReal,
                                         int optInTimePeriod,
                                         [Out]int%    outBegIdx,
                                         [Out]int%    outNBElement,
                                         cli::array<double>^  outReal )
         { return Trix( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode Trix( int    startIdx,
                                         int    endIdx,
                                         cli::array<double>^ inReal,
                                         int optInTimePeriod,
                                         [Out]int%    outBegIdx,
                                         [Out]int%    outNBElement,
                                         cli::array<double>^  outReal );
         static enum class RetCode Trix( int    startIdx,
                                         int    endIdx,
                                         cli::array<float>^ inReal,
                                         int optInTimePeriod,
                                         [Out]int%    outBegIdx,
                                         [Out]int%    outNBElement,
                                         cli::array<double>^  outReal );
#endif

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode TrixLogic( int    startIdx,
                                              int    endIdx,
                                              SubArray<double>^ inReal,
                                              int optInTimePeriod,
                                              [Out]int%    outBegIdx,
                                              [Out]int%    outNBElement,
                                              SubArray<double>^  outReal );

         static enum class RetCode TrixLogic( int    startIdx,
                                              int    endIdx,
                                              SubArray<float>^ inReal,
                                              int optInTimePeriod,
                                              [Out]int%    outBegIdx,
                                              [Out]int%    outNBElement,
                                              SubArray<double>^  outReal );

         static enum class RetCode TrixLogic( int    startIdx,
                                              int    endIdx,
                                              cli::array<double>^ inReal,
                                              int optInTimePeriod,
                                              [Out]int%    outBegIdx,
                                              [Out]int%    outNBElement,
                                              cli::array<double>^  outReal )
         { return TrixLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode TrixLogic( int    startIdx,
                                              int    endIdx,
                                              cli::array<float>^ inReal,
                                              int optInTimePeriod,
                                              [Out]int%    outBegIdx,
                                              [Out]int%    outNBElement,
                                              cli::array<double>^  outReal )
         { return TrixLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode TrixLogic( int    startIdx,
                                              int    endIdx,
                                              cli::array<double>^ inReal,
                                              int optInTimePeriod,
                                              [Out]int%    outBegIdx,
                                              [Out]int%    outNBElement,
                                              cli::array<double>^  outReal );
         static enum class RetCode TrixLogic( int    startIdx,
                                              int    endIdx,
                                              cli::array<float>^ inReal,
                                              int optInTimePeriod,
                                              [Out]int%    outBegIdx,
                                              [Out]int%    outNBElement,
                                              cli::array<double>^  outReal );
#endif

         #define TA_TRIX Core::Trix
         #define TA_TRIX_Lookback Core::TrixLookback
         #define TA_TRIX_Logic Core::TrixLogic
