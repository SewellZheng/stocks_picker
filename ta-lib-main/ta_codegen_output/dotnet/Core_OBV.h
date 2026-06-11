         static int ObvLookback( void );

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode Obv( int    startIdx,
                                        int    endIdx,
                                        SubArray<double>^ inReal,
                                        SubArray<double>^ inVolume,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        SubArray<double>^  outReal );

         static enum class RetCode Obv( int    startIdx,
                                        int    endIdx,
                                        SubArray<float>^ inReal,
                                        SubArray<float>^ inVolume,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        SubArray<double>^  outReal );

         static enum class RetCode Obv( int    startIdx,
                                        int    endIdx,
                                        cli::array<double>^ inReal,
                                        cli::array<double>^ inVolume,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal )
         { return Obv( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal,0),
                         gcnew SubArrayFrom1D<double>(inVolume,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode Obv( int    startIdx,
                                        int    endIdx,
                                        cli::array<float>^ inReal,
                                        cli::array<float>^ inVolume,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal )
         { return Obv( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal,0),
                         gcnew SubArrayFrom1D<float>(inVolume,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode Obv( int    startIdx,
                                        int    endIdx,
                                        cli::array<double>^ inReal,
                                        cli::array<double>^ inVolume,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal );
         static enum class RetCode Obv( int    startIdx,
                                        int    endIdx,
                                        cli::array<float>^ inReal,
                                        cli::array<float>^ inVolume,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal );
#endif

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode ObvLogic( int    startIdx,
                                             int    endIdx,
                                             SubArray<double>^ inReal,
                                             SubArray<double>^ inVolume,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             SubArray<double>^  outReal );

         static enum class RetCode ObvLogic( int    startIdx,
                                             int    endIdx,
                                             SubArray<float>^ inReal,
                                             SubArray<float>^ inVolume,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             SubArray<double>^  outReal );

         static enum class RetCode ObvLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<double>^ inReal,
                                             cli::array<double>^ inVolume,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal )
         { return ObvLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal,0),
                         gcnew SubArrayFrom1D<double>(inVolume,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode ObvLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<float>^ inReal,
                                             cli::array<float>^ inVolume,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal )
         { return ObvLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal,0),
                         gcnew SubArrayFrom1D<float>(inVolume,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode ObvLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<double>^ inReal,
                                             cli::array<double>^ inVolume,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal );
         static enum class RetCode ObvLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<float>^ inReal,
                                             cli::array<float>^ inVolume,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal );
#endif

         #define TA_OBV Core::Obv
         #define TA_OBV_Lookback Core::ObvLookback
         #define TA_OBV_Logic Core::ObvLogic
