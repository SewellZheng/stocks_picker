         static int DivLookback( void );

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode Div( int    startIdx,
                                        int    endIdx,
                                        SubArray<double>^ inReal0,
                                        SubArray<double>^ inReal1,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        SubArray<double>^  outReal );

         static enum class RetCode Div( int    startIdx,
                                        int    endIdx,
                                        SubArray<float>^ inReal0,
                                        SubArray<float>^ inReal1,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        SubArray<double>^  outReal );

         static enum class RetCode Div( int    startIdx,
                                        int    endIdx,
                                        cli::array<double>^ inReal0,
                                        cli::array<double>^ inReal1,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal )
         { return Div( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal0,0),
                         gcnew SubArrayFrom1D<double>(inReal1,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode Div( int    startIdx,
                                        int    endIdx,
                                        cli::array<float>^ inReal0,
                                        cli::array<float>^ inReal1,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal )
         { return Div( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal0,0),
                         gcnew SubArrayFrom1D<float>(inReal1,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode Div( int    startIdx,
                                        int    endIdx,
                                        cli::array<double>^ inReal0,
                                        cli::array<double>^ inReal1,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal );
         static enum class RetCode Div( int    startIdx,
                                        int    endIdx,
                                        cli::array<float>^ inReal0,
                                        cli::array<float>^ inReal1,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal );
#endif

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode DivLogic( int    startIdx,
                                             int    endIdx,
                                             SubArray<double>^ inReal0,
                                             SubArray<double>^ inReal1,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             SubArray<double>^  outReal );

         static enum class RetCode DivLogic( int    startIdx,
                                             int    endIdx,
                                             SubArray<float>^ inReal0,
                                             SubArray<float>^ inReal1,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             SubArray<double>^  outReal );

         static enum class RetCode DivLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<double>^ inReal0,
                                             cli::array<double>^ inReal1,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal )
         { return DivLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal0,0),
                         gcnew SubArrayFrom1D<double>(inReal1,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode DivLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<float>^ inReal0,
                                             cli::array<float>^ inReal1,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal )
         { return DivLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal0,0),
                         gcnew SubArrayFrom1D<float>(inReal1,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode DivLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<double>^ inReal0,
                                             cli::array<double>^ inReal1,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal );
         static enum class RetCode DivLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<float>^ inReal0,
                                             cli::array<float>^ inReal1,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal );
#endif

         #define TA_DIV Core::Div
         #define TA_DIV_Lookback Core::DivLookback
         #define TA_DIV_Logic Core::DivLogic
