         static int MinindexLookback( int           optInTimePeriod  /* From 2 to 100000 */ );

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode Minindex( int    startIdx,
                                             int    endIdx,
                                             SubArray<double>^ inReal,
                                             int optInTimePeriod,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             SubArray<int>^  outInteger );

         static enum class RetCode Minindex( int    startIdx,
                                             int    endIdx,
                                             SubArray<float>^ inReal,
                                             int optInTimePeriod,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             SubArray<int>^  outInteger );

         static enum class RetCode Minindex( int    startIdx,
                                             int    endIdx,
                                             cli::array<double>^ inReal,
                                             int optInTimePeriod,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<int>^  outInteger )
         { return Minindex( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<int>(outInteger,0) );
         }
         static enum class RetCode Minindex( int    startIdx,
                                             int    endIdx,
                                             cli::array<float>^ inReal,
                                             int optInTimePeriod,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<int>^  outInteger )
         { return Minindex( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<int>(outInteger,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode Minindex( int    startIdx,
                                             int    endIdx,
                                             cli::array<double>^ inReal,
                                             int optInTimePeriod,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<int>^  outInteger );
         static enum class RetCode Minindex( int    startIdx,
                                             int    endIdx,
                                             cli::array<float>^ inReal,
                                             int optInTimePeriod,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<int>^  outInteger );
#endif

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode MinindexLogic( int    startIdx,
                                                  int    endIdx,
                                                  SubArray<double>^ inReal,
                                                  int optInTimePeriod,
                                                  [Out]int%    outBegIdx,
                                                  [Out]int%    outNBElement,
                                                  SubArray<int>^  outInteger );

         static enum class RetCode MinindexLogic( int    startIdx,
                                                  int    endIdx,
                                                  SubArray<float>^ inReal,
                                                  int optInTimePeriod,
                                                  [Out]int%    outBegIdx,
                                                  [Out]int%    outNBElement,
                                                  SubArray<int>^  outInteger );

         static enum class RetCode MinindexLogic( int    startIdx,
                                                  int    endIdx,
                                                  cli::array<double>^ inReal,
                                                  int optInTimePeriod,
                                                  [Out]int%    outBegIdx,
                                                  [Out]int%    outNBElement,
                                                  cli::array<int>^  outInteger )
         { return MinindexLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<int>(outInteger,0) );
         }
         static enum class RetCode MinindexLogic( int    startIdx,
                                                  int    endIdx,
                                                  cli::array<float>^ inReal,
                                                  int optInTimePeriod,
                                                  [Out]int%    outBegIdx,
                                                  [Out]int%    outNBElement,
                                                  cli::array<int>^  outInteger )
         { return MinindexLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<int>(outInteger,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode MinindexLogic( int    startIdx,
                                                  int    endIdx,
                                                  cli::array<double>^ inReal,
                                                  int optInTimePeriod,
                                                  [Out]int%    outBegIdx,
                                                  [Out]int%    outNBElement,
                                                  cli::array<int>^  outInteger );
         static enum class RetCode MinindexLogic( int    startIdx,
                                                  int    endIdx,
                                                  cli::array<float>^ inReal,
                                                  int optInTimePeriod,
                                                  [Out]int%    outBegIdx,
                                                  [Out]int%    outNBElement,
                                                  cli::array<int>^  outInteger );
#endif

         #define TA_MININDEX Core::Minindex
         #define TA_MININDEX_Lookback Core::MinindexLookback
         #define TA_MININDEX_Logic Core::MinindexLogic
