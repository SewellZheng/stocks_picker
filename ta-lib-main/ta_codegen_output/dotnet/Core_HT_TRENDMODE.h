         static int Ht_trendmodeLookback( void );

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode Ht_trendmode( int    startIdx,
                                                 int    endIdx,
                                                 SubArray<double>^ inReal,
                                                 [Out]int%    outBegIdx,
                                                 [Out]int%    outNBElement,
                                                 SubArray<int>^  outInteger );

         static enum class RetCode Ht_trendmode( int    startIdx,
                                                 int    endIdx,
                                                 SubArray<float>^ inReal,
                                                 [Out]int%    outBegIdx,
                                                 [Out]int%    outNBElement,
                                                 SubArray<int>^  outInteger );

         static enum class RetCode Ht_trendmode( int    startIdx,
                                                 int    endIdx,
                                                 cli::array<double>^ inReal,
                                                 [Out]int%    outBegIdx,
                                                 [Out]int%    outNBElement,
                                                 cli::array<int>^  outInteger )
         { return Ht_trendmode( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<int>(outInteger,0) );
         }
         static enum class RetCode Ht_trendmode( int    startIdx,
                                                 int    endIdx,
                                                 cli::array<float>^ inReal,
                                                 [Out]int%    outBegIdx,
                                                 [Out]int%    outNBElement,
                                                 cli::array<int>^  outInteger )
         { return Ht_trendmode( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<int>(outInteger,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode Ht_trendmode( int    startIdx,
                                                 int    endIdx,
                                                 cli::array<double>^ inReal,
                                                 [Out]int%    outBegIdx,
                                                 [Out]int%    outNBElement,
                                                 cli::array<int>^  outInteger );
         static enum class RetCode Ht_trendmode( int    startIdx,
                                                 int    endIdx,
                                                 cli::array<float>^ inReal,
                                                 [Out]int%    outBegIdx,
                                                 [Out]int%    outNBElement,
                                                 cli::array<int>^  outInteger );
#endif

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode Ht_trendmodeLogic( int    startIdx,
                                                      int    endIdx,
                                                      SubArray<double>^ inReal,
                                                      [Out]int%    outBegIdx,
                                                      [Out]int%    outNBElement,
                                                      SubArray<int>^  outInteger );

         static enum class RetCode Ht_trendmodeLogic( int    startIdx,
                                                      int    endIdx,
                                                      SubArray<float>^ inReal,
                                                      [Out]int%    outBegIdx,
                                                      [Out]int%    outNBElement,
                                                      SubArray<int>^  outInteger );

         static enum class RetCode Ht_trendmodeLogic( int    startIdx,
                                                      int    endIdx,
                                                      cli::array<double>^ inReal,
                                                      [Out]int%    outBegIdx,
                                                      [Out]int%    outNBElement,
                                                      cli::array<int>^  outInteger )
         { return Ht_trendmodeLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<int>(outInteger,0) );
         }
         static enum class RetCode Ht_trendmodeLogic( int    startIdx,
                                                      int    endIdx,
                                                      cli::array<float>^ inReal,
                                                      [Out]int%    outBegIdx,
                                                      [Out]int%    outNBElement,
                                                      cli::array<int>^  outInteger )
         { return Ht_trendmodeLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<int>(outInteger,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode Ht_trendmodeLogic( int    startIdx,
                                                      int    endIdx,
                                                      cli::array<double>^ inReal,
                                                      [Out]int%    outBegIdx,
                                                      [Out]int%    outNBElement,
                                                      cli::array<int>^  outInteger );
         static enum class RetCode Ht_trendmodeLogic( int    startIdx,
                                                      int    endIdx,
                                                      cli::array<float>^ inReal,
                                                      [Out]int%    outBegIdx,
                                                      [Out]int%    outNBElement,
                                                      cli::array<int>^  outInteger );
#endif

         #define TA_HT_TRENDMODE Core::Ht_trendmode
         #define TA_HT_TRENDMODE_Lookback Core::Ht_trendmodeLookback
         #define TA_HT_TRENDMODE_Logic Core::Ht_trendmodeLogic
