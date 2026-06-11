         static int NatrLookback( int           optInTimePeriod  /* From 1 to 100000 */ );

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode Natr( int    startIdx,
                                         int    endIdx,
                                         SubArray<double>^ inHigh,
                                         SubArray<double>^ inLow,
                                         SubArray<double>^ inClose,
                                         int optInTimePeriod,
                                         [Out]int%    outBegIdx,
                                         [Out]int%    outNBElement,
                                         SubArray<double>^  outReal );

         static enum class RetCode Natr( int    startIdx,
                                         int    endIdx,
                                         SubArray<float>^ inHigh,
                                         SubArray<float>^ inLow,
                                         SubArray<float>^ inClose,
                                         int optInTimePeriod,
                                         [Out]int%    outBegIdx,
                                         [Out]int%    outNBElement,
                                         SubArray<double>^  outReal );

         static enum class RetCode Natr( int    startIdx,
                                         int    endIdx,
                                         cli::array<double>^ inHigh,
                                         cli::array<double>^ inLow,
                                         cli::array<double>^ inClose,
                                         int optInTimePeriod,
                                         [Out]int%    outBegIdx,
                                         [Out]int%    outNBElement,
                                         cli::array<double>^  outReal )
         { return Natr( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inHigh,0),
                         gcnew SubArrayFrom1D<double>(inLow,0),
                         gcnew SubArrayFrom1D<double>(inClose,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode Natr( int    startIdx,
                                         int    endIdx,
                                         cli::array<float>^ inHigh,
                                         cli::array<float>^ inLow,
                                         cli::array<float>^ inClose,
                                         int optInTimePeriod,
                                         [Out]int%    outBegIdx,
                                         [Out]int%    outNBElement,
                                         cli::array<double>^  outReal )
         { return Natr( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inHigh,0),
                         gcnew SubArrayFrom1D<float>(inLow,0),
                         gcnew SubArrayFrom1D<float>(inClose,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode Natr( int    startIdx,
                                         int    endIdx,
                                         cli::array<double>^ inHigh,
                                         cli::array<double>^ inLow,
                                         cli::array<double>^ inClose,
                                         int optInTimePeriod,
                                         [Out]int%    outBegIdx,
                                         [Out]int%    outNBElement,
                                         cli::array<double>^  outReal );
         static enum class RetCode Natr( int    startIdx,
                                         int    endIdx,
                                         cli::array<float>^ inHigh,
                                         cli::array<float>^ inLow,
                                         cli::array<float>^ inClose,
                                         int optInTimePeriod,
                                         [Out]int%    outBegIdx,
                                         [Out]int%    outNBElement,
                                         cli::array<double>^  outReal );
#endif

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode NatrLogic( int    startIdx,
                                              int    endIdx,
                                              SubArray<double>^ inHigh,
                                              SubArray<double>^ inLow,
                                              SubArray<double>^ inClose,
                                              int optInTimePeriod,
                                              [Out]int%    outBegIdx,
                                              [Out]int%    outNBElement,
                                              SubArray<double>^  outReal );

         static enum class RetCode NatrLogic( int    startIdx,
                                              int    endIdx,
                                              SubArray<float>^ inHigh,
                                              SubArray<float>^ inLow,
                                              SubArray<float>^ inClose,
                                              int optInTimePeriod,
                                              [Out]int%    outBegIdx,
                                              [Out]int%    outNBElement,
                                              SubArray<double>^  outReal );

         static enum class RetCode NatrLogic( int    startIdx,
                                              int    endIdx,
                                              cli::array<double>^ inHigh,
                                              cli::array<double>^ inLow,
                                              cli::array<double>^ inClose,
                                              int optInTimePeriod,
                                              [Out]int%    outBegIdx,
                                              [Out]int%    outNBElement,
                                              cli::array<double>^  outReal )
         { return NatrLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inHigh,0),
                         gcnew SubArrayFrom1D<double>(inLow,0),
                         gcnew SubArrayFrom1D<double>(inClose,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode NatrLogic( int    startIdx,
                                              int    endIdx,
                                              cli::array<float>^ inHigh,
                                              cli::array<float>^ inLow,
                                              cli::array<float>^ inClose,
                                              int optInTimePeriod,
                                              [Out]int%    outBegIdx,
                                              [Out]int%    outNBElement,
                                              cli::array<double>^  outReal )
         { return NatrLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inHigh,0),
                         gcnew SubArrayFrom1D<float>(inLow,0),
                         gcnew SubArrayFrom1D<float>(inClose,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode NatrLogic( int    startIdx,
                                              int    endIdx,
                                              cli::array<double>^ inHigh,
                                              cli::array<double>^ inLow,
                                              cli::array<double>^ inClose,
                                              int optInTimePeriod,
                                              [Out]int%    outBegIdx,
                                              [Out]int%    outNBElement,
                                              cli::array<double>^  outReal );
         static enum class RetCode NatrLogic( int    startIdx,
                                              int    endIdx,
                                              cli::array<float>^ inHigh,
                                              cli::array<float>^ inLow,
                                              cli::array<float>^ inClose,
                                              int optInTimePeriod,
                                              [Out]int%    outBegIdx,
                                              [Out]int%    outNBElement,
                                              cli::array<double>^  outReal );
#endif

         #define TA_NATR Core::Natr
         #define TA_NATR_Lookback Core::NatrLookback
         #define TA_NATR_Logic Core::NatrLogic
