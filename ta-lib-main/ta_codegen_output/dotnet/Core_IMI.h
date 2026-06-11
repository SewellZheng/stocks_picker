         static int ImiLookback( int           optInTimePeriod  /* From 2 to 100000 */ );

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode Imi( int    startIdx,
                                        int    endIdx,
                                        SubArray<double>^ inOpen,
                                        SubArray<double>^ inClose,
                                        int optInTimePeriod,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        SubArray<double>^  outReal );

         static enum class RetCode Imi( int    startIdx,
                                        int    endIdx,
                                        SubArray<float>^ inOpen,
                                        SubArray<float>^ inClose,
                                        int optInTimePeriod,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        SubArray<double>^  outReal );

         static enum class RetCode Imi( int    startIdx,
                                        int    endIdx,
                                        cli::array<double>^ inOpen,
                                        cli::array<double>^ inClose,
                                        int optInTimePeriod,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal )
         { return Imi( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inOpen,0),
                         gcnew SubArrayFrom1D<double>(inClose,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode Imi( int    startIdx,
                                        int    endIdx,
                                        cli::array<float>^ inOpen,
                                        cli::array<float>^ inClose,
                                        int optInTimePeriod,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal )
         { return Imi( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inOpen,0),
                         gcnew SubArrayFrom1D<float>(inClose,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode Imi( int    startIdx,
                                        int    endIdx,
                                        cli::array<double>^ inOpen,
                                        cli::array<double>^ inClose,
                                        int optInTimePeriod,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal );
         static enum class RetCode Imi( int    startIdx,
                                        int    endIdx,
                                        cli::array<float>^ inOpen,
                                        cli::array<float>^ inClose,
                                        int optInTimePeriod,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal );
#endif

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode ImiLogic( int    startIdx,
                                             int    endIdx,
                                             SubArray<double>^ inOpen,
                                             SubArray<double>^ inClose,
                                             int optInTimePeriod,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             SubArray<double>^  outReal );

         static enum class RetCode ImiLogic( int    startIdx,
                                             int    endIdx,
                                             SubArray<float>^ inOpen,
                                             SubArray<float>^ inClose,
                                             int optInTimePeriod,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             SubArray<double>^  outReal );

         static enum class RetCode ImiLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<double>^ inOpen,
                                             cli::array<double>^ inClose,
                                             int optInTimePeriod,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal )
         { return ImiLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inOpen,0),
                         gcnew SubArrayFrom1D<double>(inClose,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode ImiLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<float>^ inOpen,
                                             cli::array<float>^ inClose,
                                             int optInTimePeriod,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal )
         { return ImiLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inOpen,0),
                         gcnew SubArrayFrom1D<float>(inClose,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode ImiLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<double>^ inOpen,
                                             cli::array<double>^ inClose,
                                             int optInTimePeriod,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal );
         static enum class RetCode ImiLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<float>^ inOpen,
                                             cli::array<float>^ inClose,
                                             int optInTimePeriod,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal );
#endif

         #define TA_IMI Core::Imi
         #define TA_IMI_Lookback Core::ImiLookback
         #define TA_IMI_Logic Core::ImiLogic
