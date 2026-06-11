         static int AtrLookback( int           optInTimePeriod  /* From 1 to 100000 */ );

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode Atr( int    startIdx,
                                        int    endIdx,
                                        SubArray<double>^ inHigh,
                                        SubArray<double>^ inLow,
                                        SubArray<double>^ inClose,
                                        int optInTimePeriod,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        SubArray<double>^  outReal );

         static enum class RetCode Atr( int    startIdx,
                                        int    endIdx,
                                        SubArray<float>^ inHigh,
                                        SubArray<float>^ inLow,
                                        SubArray<float>^ inClose,
                                        int optInTimePeriod,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        SubArray<double>^  outReal );

         static enum class RetCode Atr( int    startIdx,
                                        int    endIdx,
                                        cli::array<double>^ inHigh,
                                        cli::array<double>^ inLow,
                                        cli::array<double>^ inClose,
                                        int optInTimePeriod,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal )
         { return Atr( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inHigh,0),
                         gcnew SubArrayFrom1D<double>(inLow,0),
                         gcnew SubArrayFrom1D<double>(inClose,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode Atr( int    startIdx,
                                        int    endIdx,
                                        cli::array<float>^ inHigh,
                                        cli::array<float>^ inLow,
                                        cli::array<float>^ inClose,
                                        int optInTimePeriod,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal )
         { return Atr( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inHigh,0),
                         gcnew SubArrayFrom1D<float>(inLow,0),
                         gcnew SubArrayFrom1D<float>(inClose,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode Atr( int    startIdx,
                                        int    endIdx,
                                        cli::array<double>^ inHigh,
                                        cli::array<double>^ inLow,
                                        cli::array<double>^ inClose,
                                        int optInTimePeriod,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal );
         static enum class RetCode Atr( int    startIdx,
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
         static enum class RetCode AtrLogic( int    startIdx,
                                             int    endIdx,
                                             SubArray<double>^ inHigh,
                                             SubArray<double>^ inLow,
                                             SubArray<double>^ inClose,
                                             int optInTimePeriod,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             SubArray<double>^  outReal );

         static enum class RetCode AtrLogic( int    startIdx,
                                             int    endIdx,
                                             SubArray<float>^ inHigh,
                                             SubArray<float>^ inLow,
                                             SubArray<float>^ inClose,
                                             int optInTimePeriod,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             SubArray<double>^  outReal );

         static enum class RetCode AtrLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<double>^ inHigh,
                                             cli::array<double>^ inLow,
                                             cli::array<double>^ inClose,
                                             int optInTimePeriod,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal )
         { return AtrLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inHigh,0),
                         gcnew SubArrayFrom1D<double>(inLow,0),
                         gcnew SubArrayFrom1D<double>(inClose,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode AtrLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<float>^ inHigh,
                                             cli::array<float>^ inLow,
                                             cli::array<float>^ inClose,
                                             int optInTimePeriod,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal )
         { return AtrLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inHigh,0),
                         gcnew SubArrayFrom1D<float>(inLow,0),
                         gcnew SubArrayFrom1D<float>(inClose,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode AtrLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<double>^ inHigh,
                                             cli::array<double>^ inLow,
                                             cli::array<double>^ inClose,
                                             int optInTimePeriod,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal );
         static enum class RetCode AtrLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<float>^ inHigh,
                                             cli::array<float>^ inLow,
                                             cli::array<float>^ inClose,
                                             int optInTimePeriod,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal );
#endif

         #define TA_ATR Core::Atr
         #define TA_ATR_Lookback Core::AtrLookback
         #define TA_ATR_Logic Core::AtrLogic
