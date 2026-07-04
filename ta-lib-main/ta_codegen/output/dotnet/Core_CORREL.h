         static int CorrelLookback( int           optInTimePeriod  /* From 1 to 100000 */ );

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode Correl( int    startIdx,
                                           int    endIdx,
                                           SubArray<double>^ inReal0,
                                           SubArray<double>^ inReal1,
                                           int optInTimePeriod,
                                           [Out]int%    outBegIdx,
                                           [Out]int%    outNBElement,
                                           SubArray<double>^  outReal );

         static enum class RetCode Correl( int    startIdx,
                                           int    endIdx,
                                           SubArray<float>^ inReal0,
                                           SubArray<float>^ inReal1,
                                           int optInTimePeriod,
                                           [Out]int%    outBegIdx,
                                           [Out]int%    outNBElement,
                                           SubArray<double>^  outReal );

         static enum class RetCode Correl( int    startIdx,
                                           int    endIdx,
                                           cli::array<double>^ inReal0,
                                           cli::array<double>^ inReal1,
                                           int optInTimePeriod,
                                           [Out]int%    outBegIdx,
                                           [Out]int%    outNBElement,
                                           cli::array<double>^  outReal )
         { return Correl( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal0,0),
                         gcnew SubArrayFrom1D<double>(inReal1,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode Correl( int    startIdx,
                                           int    endIdx,
                                           cli::array<float>^ inReal0,
                                           cli::array<float>^ inReal1,
                                           int optInTimePeriod,
                                           [Out]int%    outBegIdx,
                                           [Out]int%    outNBElement,
                                           cli::array<double>^  outReal )
         { return Correl( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal0,0),
                         gcnew SubArrayFrom1D<float>(inReal1,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode Correl( int    startIdx,
                                           int    endIdx,
                                           cli::array<double>^ inReal0,
                                           cli::array<double>^ inReal1,
                                           int optInTimePeriod,
                                           [Out]int%    outBegIdx,
                                           [Out]int%    outNBElement,
                                           cli::array<double>^  outReal );
         static enum class RetCode Correl( int    startIdx,
                                           int    endIdx,
                                           cli::array<float>^ inReal0,
                                           cli::array<float>^ inReal1,
                                           int optInTimePeriod,
                                           [Out]int%    outBegIdx,
                                           [Out]int%    outNBElement,
                                           cli::array<double>^  outReal );
#endif

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode CorrelLogic( int    startIdx,
                                                int    endIdx,
                                                SubArray<double>^ inReal0,
                                                SubArray<double>^ inReal1,
                                                int optInTimePeriod,
                                                [Out]int%    outBegIdx,
                                                [Out]int%    outNBElement,
                                                SubArray<double>^  outReal );

         static enum class RetCode CorrelLogic( int    startIdx,
                                                int    endIdx,
                                                SubArray<float>^ inReal0,
                                                SubArray<float>^ inReal1,
                                                int optInTimePeriod,
                                                [Out]int%    outBegIdx,
                                                [Out]int%    outNBElement,
                                                SubArray<double>^  outReal );

         static enum class RetCode CorrelLogic( int    startIdx,
                                                int    endIdx,
                                                cli::array<double>^ inReal0,
                                                cli::array<double>^ inReal1,
                                                int optInTimePeriod,
                                                [Out]int%    outBegIdx,
                                                [Out]int%    outNBElement,
                                                cli::array<double>^  outReal )
         { return CorrelLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal0,0),
                         gcnew SubArrayFrom1D<double>(inReal1,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode CorrelLogic( int    startIdx,
                                                int    endIdx,
                                                cli::array<float>^ inReal0,
                                                cli::array<float>^ inReal1,
                                                int optInTimePeriod,
                                                [Out]int%    outBegIdx,
                                                [Out]int%    outNBElement,
                                                cli::array<double>^  outReal )
         { return CorrelLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal0,0),
                         gcnew SubArrayFrom1D<float>(inReal1,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode CorrelLogic( int    startIdx,
                                                int    endIdx,
                                                cli::array<double>^ inReal0,
                                                cli::array<double>^ inReal1,
                                                int optInTimePeriod,
                                                [Out]int%    outBegIdx,
                                                [Out]int%    outNBElement,
                                                cli::array<double>^  outReal );
         static enum class RetCode CorrelLogic( int    startIdx,
                                                int    endIdx,
                                                cli::array<float>^ inReal0,
                                                cli::array<float>^ inReal1,
                                                int optInTimePeriod,
                                                [Out]int%    outBegIdx,
                                                [Out]int%    outNBElement,
                                                cli::array<double>^  outReal );
#endif

         #define TA_CORREL Core::Correl
         #define TA_CORREL_Lookback Core::CorrelLookback
         #define TA_CORREL_Logic Core::CorrelLogic
