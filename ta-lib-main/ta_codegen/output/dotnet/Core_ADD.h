         static int AddLookback( void );

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode Add( int    startIdx,
                                        int    endIdx,
                                        SubArray<double>^ inReal0,
                                        SubArray<double>^ inReal1,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        SubArray<double>^  outReal );

         static enum class RetCode Add( int    startIdx,
                                        int    endIdx,
                                        SubArray<float>^ inReal0,
                                        SubArray<float>^ inReal1,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        SubArray<double>^  outReal );

         static enum class RetCode Add( int    startIdx,
                                        int    endIdx,
                                        cli::array<double>^ inReal0,
                                        cli::array<double>^ inReal1,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal )
         { return Add( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal0,0),
                         gcnew SubArrayFrom1D<double>(inReal1,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode Add( int    startIdx,
                                        int    endIdx,
                                        cli::array<float>^ inReal0,
                                        cli::array<float>^ inReal1,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal )
         { return Add( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal0,0),
                         gcnew SubArrayFrom1D<float>(inReal1,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode Add( int    startIdx,
                                        int    endIdx,
                                        cli::array<double>^ inReal0,
                                        cli::array<double>^ inReal1,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal );
         static enum class RetCode Add( int    startIdx,
                                        int    endIdx,
                                        cli::array<float>^ inReal0,
                                        cli::array<float>^ inReal1,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal );
#endif

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode AddLogic( int    startIdx,
                                             int    endIdx,
                                             SubArray<double>^ inReal0,
                                             SubArray<double>^ inReal1,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             SubArray<double>^  outReal );

         static enum class RetCode AddLogic( int    startIdx,
                                             int    endIdx,
                                             SubArray<float>^ inReal0,
                                             SubArray<float>^ inReal1,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             SubArray<double>^  outReal );

         static enum class RetCode AddLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<double>^ inReal0,
                                             cli::array<double>^ inReal1,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal )
         { return AddLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal0,0),
                         gcnew SubArrayFrom1D<double>(inReal1,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode AddLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<float>^ inReal0,
                                             cli::array<float>^ inReal1,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal )
         { return AddLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal0,0),
                         gcnew SubArrayFrom1D<float>(inReal1,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode AddLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<double>^ inReal0,
                                             cli::array<double>^ inReal1,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal );
         static enum class RetCode AddLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<float>^ inReal0,
                                             cli::array<float>^ inReal1,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal );
#endif

         #define TA_ADD Core::Add
         #define TA_ADD_Lookback Core::AddLookback
         #define TA_ADD_Logic Core::AddLogic
