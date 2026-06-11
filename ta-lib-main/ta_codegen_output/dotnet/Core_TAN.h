         static int TanLookback( void );

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode Tan( int    startIdx,
                                        int    endIdx,
                                        SubArray<double>^ inReal,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        SubArray<double>^  outReal );

         static enum class RetCode Tan( int    startIdx,
                                        int    endIdx,
                                        SubArray<float>^ inReal,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        SubArray<double>^  outReal );

         static enum class RetCode Tan( int    startIdx,
                                        int    endIdx,
                                        cli::array<double>^ inReal,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal )
         { return Tan( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode Tan( int    startIdx,
                                        int    endIdx,
                                        cli::array<float>^ inReal,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal )
         { return Tan( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode Tan( int    startIdx,
                                        int    endIdx,
                                        cli::array<double>^ inReal,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal );
         static enum class RetCode Tan( int    startIdx,
                                        int    endIdx,
                                        cli::array<float>^ inReal,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal );
#endif

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode TanLogic( int    startIdx,
                                             int    endIdx,
                                             SubArray<double>^ inReal,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             SubArray<double>^  outReal );

         static enum class RetCode TanLogic( int    startIdx,
                                             int    endIdx,
                                             SubArray<float>^ inReal,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             SubArray<double>^  outReal );

         static enum class RetCode TanLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<double>^ inReal,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal )
         { return TanLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode TanLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<float>^ inReal,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal )
         { return TanLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode TanLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<double>^ inReal,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal );
         static enum class RetCode TanLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<float>^ inReal,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal );
#endif

         #define TA_TAN Core::Tan
         #define TA_TAN_Lookback Core::TanLookback
         #define TA_TAN_Logic Core::TanLogic
