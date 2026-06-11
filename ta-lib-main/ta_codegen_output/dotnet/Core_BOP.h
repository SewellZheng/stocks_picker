         static int BopLookback( void );

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode Bop( int    startIdx,
                                        int    endIdx,
                                        SubArray<double>^ inOpen,
                                        SubArray<double>^ inHigh,
                                        SubArray<double>^ inLow,
                                        SubArray<double>^ inClose,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        SubArray<double>^  outReal );

         static enum class RetCode Bop( int    startIdx,
                                        int    endIdx,
                                        SubArray<float>^ inOpen,
                                        SubArray<float>^ inHigh,
                                        SubArray<float>^ inLow,
                                        SubArray<float>^ inClose,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        SubArray<double>^  outReal );

         static enum class RetCode Bop( int    startIdx,
                                        int    endIdx,
                                        cli::array<double>^ inOpen,
                                        cli::array<double>^ inHigh,
                                        cli::array<double>^ inLow,
                                        cli::array<double>^ inClose,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal )
         { return Bop( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inOpen,0),
                         gcnew SubArrayFrom1D<double>(inHigh,0),
                         gcnew SubArrayFrom1D<double>(inLow,0),
                         gcnew SubArrayFrom1D<double>(inClose,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode Bop( int    startIdx,
                                        int    endIdx,
                                        cli::array<float>^ inOpen,
                                        cli::array<float>^ inHigh,
                                        cli::array<float>^ inLow,
                                        cli::array<float>^ inClose,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal )
         { return Bop( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inOpen,0),
                         gcnew SubArrayFrom1D<float>(inHigh,0),
                         gcnew SubArrayFrom1D<float>(inLow,0),
                         gcnew SubArrayFrom1D<float>(inClose,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode Bop( int    startIdx,
                                        int    endIdx,
                                        cli::array<double>^ inOpen,
                                        cli::array<double>^ inHigh,
                                        cli::array<double>^ inLow,
                                        cli::array<double>^ inClose,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal );
         static enum class RetCode Bop( int    startIdx,
                                        int    endIdx,
                                        cli::array<float>^ inOpen,
                                        cli::array<float>^ inHigh,
                                        cli::array<float>^ inLow,
                                        cli::array<float>^ inClose,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal );
#endif

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode BopLogic( int    startIdx,
                                             int    endIdx,
                                             SubArray<double>^ inOpen,
                                             SubArray<double>^ inHigh,
                                             SubArray<double>^ inLow,
                                             SubArray<double>^ inClose,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             SubArray<double>^  outReal );

         static enum class RetCode BopLogic( int    startIdx,
                                             int    endIdx,
                                             SubArray<float>^ inOpen,
                                             SubArray<float>^ inHigh,
                                             SubArray<float>^ inLow,
                                             SubArray<float>^ inClose,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             SubArray<double>^  outReal );

         static enum class RetCode BopLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<double>^ inOpen,
                                             cli::array<double>^ inHigh,
                                             cli::array<double>^ inLow,
                                             cli::array<double>^ inClose,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal )
         { return BopLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inOpen,0),
                         gcnew SubArrayFrom1D<double>(inHigh,0),
                         gcnew SubArrayFrom1D<double>(inLow,0),
                         gcnew SubArrayFrom1D<double>(inClose,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode BopLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<float>^ inOpen,
                                             cli::array<float>^ inHigh,
                                             cli::array<float>^ inLow,
                                             cli::array<float>^ inClose,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal )
         { return BopLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inOpen,0),
                         gcnew SubArrayFrom1D<float>(inHigh,0),
                         gcnew SubArrayFrom1D<float>(inLow,0),
                         gcnew SubArrayFrom1D<float>(inClose,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode BopLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<double>^ inOpen,
                                             cli::array<double>^ inHigh,
                                             cli::array<double>^ inLow,
                                             cli::array<double>^ inClose,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal );
         static enum class RetCode BopLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<float>^ inOpen,
                                             cli::array<float>^ inHigh,
                                             cli::array<float>^ inLow,
                                             cli::array<float>^ inClose,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal );
#endif

         #define TA_BOP Core::Bop
         #define TA_BOP_Lookback Core::BopLookback
         #define TA_BOP_Logic Core::BopLogic
