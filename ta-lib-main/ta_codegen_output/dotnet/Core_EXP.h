         static int ExpLookback( void );

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode Exp( int    startIdx,
                                        int    endIdx,
                                        SubArray<double>^ inReal,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        SubArray<double>^  outReal );

         static enum class RetCode Exp( int    startIdx,
                                        int    endIdx,
                                        SubArray<float>^ inReal,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        SubArray<double>^  outReal );

         static enum class RetCode Exp( int    startIdx,
                                        int    endIdx,
                                        cli::array<double>^ inReal,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal )
         { return Exp( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode Exp( int    startIdx,
                                        int    endIdx,
                                        cli::array<float>^ inReal,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal )
         { return Exp( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode Exp( int    startIdx,
                                        int    endIdx,
                                        cli::array<double>^ inReal,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal );
         static enum class RetCode Exp( int    startIdx,
                                        int    endIdx,
                                        cli::array<float>^ inReal,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal );
#endif

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode ExpLogic( int    startIdx,
                                             int    endIdx,
                                             SubArray<double>^ inReal,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             SubArray<double>^  outReal );

         static enum class RetCode ExpLogic( int    startIdx,
                                             int    endIdx,
                                             SubArray<float>^ inReal,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             SubArray<double>^  outReal );

         static enum class RetCode ExpLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<double>^ inReal,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal )
         { return ExpLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode ExpLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<float>^ inReal,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal )
         { return ExpLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode ExpLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<double>^ inReal,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal );
         static enum class RetCode ExpLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<float>^ inReal,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal );
#endif

         #define TA_EXP Core::Exp
         #define TA_EXP_Lookback Core::ExpLookback
         #define TA_EXP_Logic Core::ExpLogic
