         static int VarLookback( int           optInTimePeriod  /* From 1 to 100000 */, double           optInNbDev  /* From -179769313486231570000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000 to 179769313486231570000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000 */ );

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode Var( int    startIdx,
                                        int    endIdx,
                                        SubArray<double>^ inReal,
                                        int optInTimePeriod,
                                        double optInNbDev,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        SubArray<double>^  outReal );

         static enum class RetCode Var( int    startIdx,
                                        int    endIdx,
                                        SubArray<float>^ inReal,
                                        int optInTimePeriod,
                                        double optInNbDev,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        SubArray<double>^  outReal );

         static enum class RetCode Var( int    startIdx,
                                        int    endIdx,
                                        cli::array<double>^ inReal,
                                        int optInTimePeriod,
                                        double optInNbDev,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal )
         { return Var( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal,0),
                         optInTimePeriod,
                         optInNbDev,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode Var( int    startIdx,
                                        int    endIdx,
                                        cli::array<float>^ inReal,
                                        int optInTimePeriod,
                                        double optInNbDev,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal )
         { return Var( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal,0),
                         optInTimePeriod,
                         optInNbDev,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode Var( int    startIdx,
                                        int    endIdx,
                                        cli::array<double>^ inReal,
                                        int optInTimePeriod,
                                        double optInNbDev,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal );
         static enum class RetCode Var( int    startIdx,
                                        int    endIdx,
                                        cli::array<float>^ inReal,
                                        int optInTimePeriod,
                                        double optInNbDev,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal );
#endif

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode VarLogic( int    startIdx,
                                             int    endIdx,
                                             SubArray<double>^ inReal,
                                             int optInTimePeriod,
                                             double optInNbDev,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             SubArray<double>^  outReal );

         static enum class RetCode VarLogic( int    startIdx,
                                             int    endIdx,
                                             SubArray<float>^ inReal,
                                             int optInTimePeriod,
                                             double optInNbDev,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             SubArray<double>^  outReal );

         static enum class RetCode VarLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<double>^ inReal,
                                             int optInTimePeriod,
                                             double optInNbDev,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal )
         { return VarLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal,0),
                         optInTimePeriod,
                         optInNbDev,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode VarLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<float>^ inReal,
                                             int optInTimePeriod,
                                             double optInNbDev,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal )
         { return VarLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal,0),
                         optInTimePeriod,
                         optInNbDev,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode VarLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<double>^ inReal,
                                             int optInTimePeriod,
                                             double optInNbDev,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal );
         static enum class RetCode VarLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<float>^ inReal,
                                             int optInTimePeriod,
                                             double optInNbDev,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal );
#endif

         #define TA_VAR Core::Var
         #define TA_VAR_Lookback Core::VarLookback
         #define TA_VAR_Logic Core::VarLogic
