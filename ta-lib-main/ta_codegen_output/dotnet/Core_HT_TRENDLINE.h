         static int Ht_trendlineLookback( void );

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode Ht_trendline( int    startIdx,
                                                 int    endIdx,
                                                 SubArray<double>^ inReal,
                                                 [Out]int%    outBegIdx,
                                                 [Out]int%    outNBElement,
                                                 SubArray<double>^  outReal );

         static enum class RetCode Ht_trendline( int    startIdx,
                                                 int    endIdx,
                                                 SubArray<float>^ inReal,
                                                 [Out]int%    outBegIdx,
                                                 [Out]int%    outNBElement,
                                                 SubArray<double>^  outReal );

         static enum class RetCode Ht_trendline( int    startIdx,
                                                 int    endIdx,
                                                 cli::array<double>^ inReal,
                                                 [Out]int%    outBegIdx,
                                                 [Out]int%    outNBElement,
                                                 cli::array<double>^  outReal )
         { return Ht_trendline( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode Ht_trendline( int    startIdx,
                                                 int    endIdx,
                                                 cli::array<float>^ inReal,
                                                 [Out]int%    outBegIdx,
                                                 [Out]int%    outNBElement,
                                                 cli::array<double>^  outReal )
         { return Ht_trendline( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode Ht_trendline( int    startIdx,
                                                 int    endIdx,
                                                 cli::array<double>^ inReal,
                                                 [Out]int%    outBegIdx,
                                                 [Out]int%    outNBElement,
                                                 cli::array<double>^  outReal );
         static enum class RetCode Ht_trendline( int    startIdx,
                                                 int    endIdx,
                                                 cli::array<float>^ inReal,
                                                 [Out]int%    outBegIdx,
                                                 [Out]int%    outNBElement,
                                                 cli::array<double>^  outReal );
#endif

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode Ht_trendlineLogic( int    startIdx,
                                                      int    endIdx,
                                                      SubArray<double>^ inReal,
                                                      [Out]int%    outBegIdx,
                                                      [Out]int%    outNBElement,
                                                      SubArray<double>^  outReal );

         static enum class RetCode Ht_trendlineLogic( int    startIdx,
                                                      int    endIdx,
                                                      SubArray<float>^ inReal,
                                                      [Out]int%    outBegIdx,
                                                      [Out]int%    outNBElement,
                                                      SubArray<double>^  outReal );

         static enum class RetCode Ht_trendlineLogic( int    startIdx,
                                                      int    endIdx,
                                                      cli::array<double>^ inReal,
                                                      [Out]int%    outBegIdx,
                                                      [Out]int%    outNBElement,
                                                      cli::array<double>^  outReal )
         { return Ht_trendlineLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode Ht_trendlineLogic( int    startIdx,
                                                      int    endIdx,
                                                      cli::array<float>^ inReal,
                                                      [Out]int%    outBegIdx,
                                                      [Out]int%    outNBElement,
                                                      cli::array<double>^  outReal )
         { return Ht_trendlineLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode Ht_trendlineLogic( int    startIdx,
                                                      int    endIdx,
                                                      cli::array<double>^ inReal,
                                                      [Out]int%    outBegIdx,
                                                      [Out]int%    outNBElement,
                                                      cli::array<double>^  outReal );
         static enum class RetCode Ht_trendlineLogic( int    startIdx,
                                                      int    endIdx,
                                                      cli::array<float>^ inReal,
                                                      [Out]int%    outBegIdx,
                                                      [Out]int%    outNBElement,
                                                      cli::array<double>^  outReal );
#endif

         #define TA_HT_TRENDLINE Core::Ht_trendline
         #define TA_HT_TRENDLINE_Lookback Core::Ht_trendlineLookback
         #define TA_HT_TRENDLINE_Logic Core::Ht_trendlineLogic
