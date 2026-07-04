         static int Linearreg_angleLookback( int           optInTimePeriod  /* From 2 to 100000 */ );

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode Linearreg_angle( int    startIdx,
                                                    int    endIdx,
                                                    SubArray<double>^ inReal,
                                                    int optInTimePeriod,
                                                    [Out]int%    outBegIdx,
                                                    [Out]int%    outNBElement,
                                                    SubArray<double>^  outReal );

         static enum class RetCode Linearreg_angle( int    startIdx,
                                                    int    endIdx,
                                                    SubArray<float>^ inReal,
                                                    int optInTimePeriod,
                                                    [Out]int%    outBegIdx,
                                                    [Out]int%    outNBElement,
                                                    SubArray<double>^  outReal );

         static enum class RetCode Linearreg_angle( int    startIdx,
                                                    int    endIdx,
                                                    cli::array<double>^ inReal,
                                                    int optInTimePeriod,
                                                    [Out]int%    outBegIdx,
                                                    [Out]int%    outNBElement,
                                                    cli::array<double>^  outReal )
         { return Linearreg_angle( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode Linearreg_angle( int    startIdx,
                                                    int    endIdx,
                                                    cli::array<float>^ inReal,
                                                    int optInTimePeriod,
                                                    [Out]int%    outBegIdx,
                                                    [Out]int%    outNBElement,
                                                    cli::array<double>^  outReal )
         { return Linearreg_angle( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode Linearreg_angle( int    startIdx,
                                                    int    endIdx,
                                                    cli::array<double>^ inReal,
                                                    int optInTimePeriod,
                                                    [Out]int%    outBegIdx,
                                                    [Out]int%    outNBElement,
                                                    cli::array<double>^  outReal );
         static enum class RetCode Linearreg_angle( int    startIdx,
                                                    int    endIdx,
                                                    cli::array<float>^ inReal,
                                                    int optInTimePeriod,
                                                    [Out]int%    outBegIdx,
                                                    [Out]int%    outNBElement,
                                                    cli::array<double>^  outReal );
#endif

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode Linearreg_angleLogic( int    startIdx,
                                                         int    endIdx,
                                                         SubArray<double>^ inReal,
                                                         int optInTimePeriod,
                                                         [Out]int%    outBegIdx,
                                                         [Out]int%    outNBElement,
                                                         SubArray<double>^  outReal );

         static enum class RetCode Linearreg_angleLogic( int    startIdx,
                                                         int    endIdx,
                                                         SubArray<float>^ inReal,
                                                         int optInTimePeriod,
                                                         [Out]int%    outBegIdx,
                                                         [Out]int%    outNBElement,
                                                         SubArray<double>^  outReal );

         static enum class RetCode Linearreg_angleLogic( int    startIdx,
                                                         int    endIdx,
                                                         cli::array<double>^ inReal,
                                                         int optInTimePeriod,
                                                         [Out]int%    outBegIdx,
                                                         [Out]int%    outNBElement,
                                                         cli::array<double>^  outReal )
         { return Linearreg_angleLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode Linearreg_angleLogic( int    startIdx,
                                                         int    endIdx,
                                                         cli::array<float>^ inReal,
                                                         int optInTimePeriod,
                                                         [Out]int%    outBegIdx,
                                                         [Out]int%    outNBElement,
                                                         cli::array<double>^  outReal )
         { return Linearreg_angleLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode Linearreg_angleLogic( int    startIdx,
                                                         int    endIdx,
                                                         cli::array<double>^ inReal,
                                                         int optInTimePeriod,
                                                         [Out]int%    outBegIdx,
                                                         [Out]int%    outNBElement,
                                                         cli::array<double>^  outReal );
         static enum class RetCode Linearreg_angleLogic( int    startIdx,
                                                         int    endIdx,
                                                         cli::array<float>^ inReal,
                                                         int optInTimePeriod,
                                                         [Out]int%    outBegIdx,
                                                         [Out]int%    outNBElement,
                                                         cli::array<double>^  outReal );
#endif

         #define TA_LINEARREG_ANGLE Core::Linearreg_angle
         #define TA_LINEARREG_ANGLE_Lookback Core::Linearreg_angleLookback
         #define TA_LINEARREG_ANGLE_Logic Core::Linearreg_angleLogic
