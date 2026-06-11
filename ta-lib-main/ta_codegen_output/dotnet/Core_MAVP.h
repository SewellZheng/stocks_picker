         static int MavpLookback( int           optInMinPeriod  /* From 2 to 100000 */, int           optInMaxPeriod  /* From 2 to 100000 */, int           optInMAType );

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode Mavp( int    startIdx,
                                         int    endIdx,
                                         SubArray<double>^ inReal,
                                         SubArray<double>^ inPeriods,
                                         int optInMinPeriod,
                                         int optInMaxPeriod,
                                         int optInMAType,
                                         [Out]int%    outBegIdx,
                                         [Out]int%    outNBElement,
                                         SubArray<double>^  outReal );

         static enum class RetCode Mavp( int    startIdx,
                                         int    endIdx,
                                         SubArray<float>^ inReal,
                                         SubArray<float>^ inPeriods,
                                         int optInMinPeriod,
                                         int optInMaxPeriod,
                                         int optInMAType,
                                         [Out]int%    outBegIdx,
                                         [Out]int%    outNBElement,
                                         SubArray<double>^  outReal );

         static enum class RetCode Mavp( int    startIdx,
                                         int    endIdx,
                                         cli::array<double>^ inReal,
                                         cli::array<double>^ inPeriods,
                                         int optInMinPeriod,
                                         int optInMaxPeriod,
                                         int optInMAType,
                                         [Out]int%    outBegIdx,
                                         [Out]int%    outNBElement,
                                         cli::array<double>^  outReal )
         { return Mavp( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal,0),
                         gcnew SubArrayFrom1D<double>(inPeriods,0),
                         optInMinPeriod,
                         optInMaxPeriod,
                         optInMAType,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode Mavp( int    startIdx,
                                         int    endIdx,
                                         cli::array<float>^ inReal,
                                         cli::array<float>^ inPeriods,
                                         int optInMinPeriod,
                                         int optInMaxPeriod,
                                         int optInMAType,
                                         [Out]int%    outBegIdx,
                                         [Out]int%    outNBElement,
                                         cli::array<double>^  outReal )
         { return Mavp( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal,0),
                         gcnew SubArrayFrom1D<float>(inPeriods,0),
                         optInMinPeriod,
                         optInMaxPeriod,
                         optInMAType,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode Mavp( int    startIdx,
                                         int    endIdx,
                                         cli::array<double>^ inReal,
                                         cli::array<double>^ inPeriods,
                                         int optInMinPeriod,
                                         int optInMaxPeriod,
                                         int optInMAType,
                                         [Out]int%    outBegIdx,
                                         [Out]int%    outNBElement,
                                         cli::array<double>^  outReal );
         static enum class RetCode Mavp( int    startIdx,
                                         int    endIdx,
                                         cli::array<float>^ inReal,
                                         cli::array<float>^ inPeriods,
                                         int optInMinPeriod,
                                         int optInMaxPeriod,
                                         int optInMAType,
                                         [Out]int%    outBegIdx,
                                         [Out]int%    outNBElement,
                                         cli::array<double>^  outReal );
#endif

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode MavpLogic( int    startIdx,
                                              int    endIdx,
                                              SubArray<double>^ inReal,
                                              SubArray<double>^ inPeriods,
                                              int optInMinPeriod,
                                              int optInMaxPeriod,
                                              int optInMAType,
                                              [Out]int%    outBegIdx,
                                              [Out]int%    outNBElement,
                                              SubArray<double>^  outReal );

         static enum class RetCode MavpLogic( int    startIdx,
                                              int    endIdx,
                                              SubArray<float>^ inReal,
                                              SubArray<float>^ inPeriods,
                                              int optInMinPeriod,
                                              int optInMaxPeriod,
                                              int optInMAType,
                                              [Out]int%    outBegIdx,
                                              [Out]int%    outNBElement,
                                              SubArray<double>^  outReal );

         static enum class RetCode MavpLogic( int    startIdx,
                                              int    endIdx,
                                              cli::array<double>^ inReal,
                                              cli::array<double>^ inPeriods,
                                              int optInMinPeriod,
                                              int optInMaxPeriod,
                                              int optInMAType,
                                              [Out]int%    outBegIdx,
                                              [Out]int%    outNBElement,
                                              cli::array<double>^  outReal )
         { return MavpLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal,0),
                         gcnew SubArrayFrom1D<double>(inPeriods,0),
                         optInMinPeriod,
                         optInMaxPeriod,
                         optInMAType,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode MavpLogic( int    startIdx,
                                              int    endIdx,
                                              cli::array<float>^ inReal,
                                              cli::array<float>^ inPeriods,
                                              int optInMinPeriod,
                                              int optInMaxPeriod,
                                              int optInMAType,
                                              [Out]int%    outBegIdx,
                                              [Out]int%    outNBElement,
                                              cli::array<double>^  outReal )
         { return MavpLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal,0),
                         gcnew SubArrayFrom1D<float>(inPeriods,0),
                         optInMinPeriod,
                         optInMaxPeriod,
                         optInMAType,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode MavpLogic( int    startIdx,
                                              int    endIdx,
                                              cli::array<double>^ inReal,
                                              cli::array<double>^ inPeriods,
                                              int optInMinPeriod,
                                              int optInMaxPeriod,
                                              int optInMAType,
                                              [Out]int%    outBegIdx,
                                              [Out]int%    outNBElement,
                                              cli::array<double>^  outReal );
         static enum class RetCode MavpLogic( int    startIdx,
                                              int    endIdx,
                                              cli::array<float>^ inReal,
                                              cli::array<float>^ inPeriods,
                                              int optInMinPeriod,
                                              int optInMaxPeriod,
                                              int optInMAType,
                                              [Out]int%    outBegIdx,
                                              [Out]int%    outNBElement,
                                              cli::array<double>^  outReal );
#endif

         #define TA_MAVP Core::Mavp
         #define TA_MAVP_Lookback Core::MavpLookback
         #define TA_MAVP_Logic Core::MavpLogic
