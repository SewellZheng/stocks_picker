         static int Ht_dcperiodLookback( void );

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode Ht_dcperiod( int    startIdx,
                                                int    endIdx,
                                                SubArray<double>^ inReal,
                                                [Out]int%    outBegIdx,
                                                [Out]int%    outNBElement,
                                                SubArray<double>^  outReal );

         static enum class RetCode Ht_dcperiod( int    startIdx,
                                                int    endIdx,
                                                SubArray<float>^ inReal,
                                                [Out]int%    outBegIdx,
                                                [Out]int%    outNBElement,
                                                SubArray<double>^  outReal );

         static enum class RetCode Ht_dcperiod( int    startIdx,
                                                int    endIdx,
                                                cli::array<double>^ inReal,
                                                [Out]int%    outBegIdx,
                                                [Out]int%    outNBElement,
                                                cli::array<double>^  outReal )
         { return Ht_dcperiod( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode Ht_dcperiod( int    startIdx,
                                                int    endIdx,
                                                cli::array<float>^ inReal,
                                                [Out]int%    outBegIdx,
                                                [Out]int%    outNBElement,
                                                cli::array<double>^  outReal )
         { return Ht_dcperiod( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode Ht_dcperiod( int    startIdx,
                                                int    endIdx,
                                                cli::array<double>^ inReal,
                                                [Out]int%    outBegIdx,
                                                [Out]int%    outNBElement,
                                                cli::array<double>^  outReal );
         static enum class RetCode Ht_dcperiod( int    startIdx,
                                                int    endIdx,
                                                cli::array<float>^ inReal,
                                                [Out]int%    outBegIdx,
                                                [Out]int%    outNBElement,
                                                cli::array<double>^  outReal );
#endif

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode Ht_dcperiodLogic( int    startIdx,
                                                     int    endIdx,
                                                     SubArray<double>^ inReal,
                                                     [Out]int%    outBegIdx,
                                                     [Out]int%    outNBElement,
                                                     SubArray<double>^  outReal );

         static enum class RetCode Ht_dcperiodLogic( int    startIdx,
                                                     int    endIdx,
                                                     SubArray<float>^ inReal,
                                                     [Out]int%    outBegIdx,
                                                     [Out]int%    outNBElement,
                                                     SubArray<double>^  outReal );

         static enum class RetCode Ht_dcperiodLogic( int    startIdx,
                                                     int    endIdx,
                                                     cli::array<double>^ inReal,
                                                     [Out]int%    outBegIdx,
                                                     [Out]int%    outNBElement,
                                                     cli::array<double>^  outReal )
         { return Ht_dcperiodLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode Ht_dcperiodLogic( int    startIdx,
                                                     int    endIdx,
                                                     cli::array<float>^ inReal,
                                                     [Out]int%    outBegIdx,
                                                     [Out]int%    outNBElement,
                                                     cli::array<double>^  outReal )
         { return Ht_dcperiodLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode Ht_dcperiodLogic( int    startIdx,
                                                     int    endIdx,
                                                     cli::array<double>^ inReal,
                                                     [Out]int%    outBegIdx,
                                                     [Out]int%    outNBElement,
                                                     cli::array<double>^  outReal );
         static enum class RetCode Ht_dcperiodLogic( int    startIdx,
                                                     int    endIdx,
                                                     cli::array<float>^ inReal,
                                                     [Out]int%    outBegIdx,
                                                     [Out]int%    outNBElement,
                                                     cli::array<double>^  outReal );
#endif

         #define TA_HT_DCPERIOD Core::Ht_dcperiod
         #define TA_HT_DCPERIOD_Lookback Core::Ht_dcperiodLookback
         #define TA_HT_DCPERIOD_Logic Core::Ht_dcperiodLogic
