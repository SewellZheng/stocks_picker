         static int Linearreg_slopeLookback( int           optInTimePeriod  /* From 2 to 100000 */ );

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode Linearreg_slope( int    startIdx,
                                                    int    endIdx,
                                                    SubArray<double>^ inReal,
                                                    int optInTimePeriod,
                                                    [Out]int%    outBegIdx,
                                                    [Out]int%    outNBElement,
                                                    SubArray<double>^  outReal );

         static enum class RetCode Linearreg_slope( int    startIdx,
                                                    int    endIdx,
                                                    SubArray<float>^ inReal,
                                                    int optInTimePeriod,
                                                    [Out]int%    outBegIdx,
                                                    [Out]int%    outNBElement,
                                                    SubArray<double>^  outReal );

         static enum class RetCode Linearreg_slope( int    startIdx,
                                                    int    endIdx,
                                                    cli::array<double>^ inReal,
                                                    int optInTimePeriod,
                                                    [Out]int%    outBegIdx,
                                                    [Out]int%    outNBElement,
                                                    cli::array<double>^  outReal )
         { return Linearreg_slope( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode Linearreg_slope( int    startIdx,
                                                    int    endIdx,
                                                    cli::array<float>^ inReal,
                                                    int optInTimePeriod,
                                                    [Out]int%    outBegIdx,
                                                    [Out]int%    outNBElement,
                                                    cli::array<double>^  outReal )
         { return Linearreg_slope( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode Linearreg_slope( int    startIdx,
                                                    int    endIdx,
                                                    cli::array<double>^ inReal,
                                                    int optInTimePeriod,
                                                    [Out]int%    outBegIdx,
                                                    [Out]int%    outNBElement,
                                                    cli::array<double>^  outReal );
         static enum class RetCode Linearreg_slope( int    startIdx,
                                                    int    endIdx,
                                                    cli::array<float>^ inReal,
                                                    int optInTimePeriod,
                                                    [Out]int%    outBegIdx,
                                                    [Out]int%    outNBElement,
                                                    cli::array<double>^  outReal );
#endif

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode Linearreg_slopeLogic( int    startIdx,
                                                         int    endIdx,
                                                         SubArray<double>^ inReal,
                                                         int optInTimePeriod,
                                                         [Out]int%    outBegIdx,
                                                         [Out]int%    outNBElement,
                                                         SubArray<double>^  outReal );

         static enum class RetCode Linearreg_slopeLogic( int    startIdx,
                                                         int    endIdx,
                                                         SubArray<float>^ inReal,
                                                         int optInTimePeriod,
                                                         [Out]int%    outBegIdx,
                                                         [Out]int%    outNBElement,
                                                         SubArray<double>^  outReal );

         static enum class RetCode Linearreg_slopeLogic( int    startIdx,
                                                         int    endIdx,
                                                         cli::array<double>^ inReal,
                                                         int optInTimePeriod,
                                                         [Out]int%    outBegIdx,
                                                         [Out]int%    outNBElement,
                                                         cli::array<double>^  outReal )
         { return Linearreg_slopeLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode Linearreg_slopeLogic( int    startIdx,
                                                         int    endIdx,
                                                         cli::array<float>^ inReal,
                                                         int optInTimePeriod,
                                                         [Out]int%    outBegIdx,
                                                         [Out]int%    outNBElement,
                                                         cli::array<double>^  outReal )
         { return Linearreg_slopeLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode Linearreg_slopeLogic( int    startIdx,
                                                         int    endIdx,
                                                         cli::array<double>^ inReal,
                                                         int optInTimePeriod,
                                                         [Out]int%    outBegIdx,
                                                         [Out]int%    outNBElement,
                                                         cli::array<double>^  outReal );
         static enum class RetCode Linearreg_slopeLogic( int    startIdx,
                                                         int    endIdx,
                                                         cli::array<float>^ inReal,
                                                         int optInTimePeriod,
                                                         [Out]int%    outBegIdx,
                                                         [Out]int%    outNBElement,
                                                         cli::array<double>^  outReal );
#endif

         #define TA_LINEARREG_SLOPE Core::Linearreg_slope
         #define TA_LINEARREG_SLOPE_Lookback Core::Linearreg_slopeLookback
         #define TA_LINEARREG_SLOPE_Logic Core::Linearreg_slopeLogic
