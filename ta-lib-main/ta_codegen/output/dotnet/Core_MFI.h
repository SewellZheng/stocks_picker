         static int MfiLookback( int           optInTimePeriod  /* From 2 to 100000 */ );

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode Mfi( int    startIdx,
                                        int    endIdx,
                                        SubArray<double>^ inHigh,
                                        SubArray<double>^ inLow,
                                        SubArray<double>^ inClose,
                                        SubArray<double>^ inVolume,
                                        int optInTimePeriod,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        SubArray<double>^  outReal );

         static enum class RetCode Mfi( int    startIdx,
                                        int    endIdx,
                                        SubArray<float>^ inHigh,
                                        SubArray<float>^ inLow,
                                        SubArray<float>^ inClose,
                                        SubArray<float>^ inVolume,
                                        int optInTimePeriod,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        SubArray<double>^  outReal );

         static enum class RetCode Mfi( int    startIdx,
                                        int    endIdx,
                                        cli::array<double>^ inHigh,
                                        cli::array<double>^ inLow,
                                        cli::array<double>^ inClose,
                                        cli::array<double>^ inVolume,
                                        int optInTimePeriod,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal )
         { return Mfi( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inHigh,0),
                         gcnew SubArrayFrom1D<double>(inLow,0),
                         gcnew SubArrayFrom1D<double>(inClose,0),
                         gcnew SubArrayFrom1D<double>(inVolume,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode Mfi( int    startIdx,
                                        int    endIdx,
                                        cli::array<float>^ inHigh,
                                        cli::array<float>^ inLow,
                                        cli::array<float>^ inClose,
                                        cli::array<float>^ inVolume,
                                        int optInTimePeriod,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal )
         { return Mfi( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inHigh,0),
                         gcnew SubArrayFrom1D<float>(inLow,0),
                         gcnew SubArrayFrom1D<float>(inClose,0),
                         gcnew SubArrayFrom1D<float>(inVolume,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode Mfi( int    startIdx,
                                        int    endIdx,
                                        cli::array<double>^ inHigh,
                                        cli::array<double>^ inLow,
                                        cli::array<double>^ inClose,
                                        cli::array<double>^ inVolume,
                                        int optInTimePeriod,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal );
         static enum class RetCode Mfi( int    startIdx,
                                        int    endIdx,
                                        cli::array<float>^ inHigh,
                                        cli::array<float>^ inLow,
                                        cli::array<float>^ inClose,
                                        cli::array<float>^ inVolume,
                                        int optInTimePeriod,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal );
#endif

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode MfiLogic( int    startIdx,
                                             int    endIdx,
                                             SubArray<double>^ inHigh,
                                             SubArray<double>^ inLow,
                                             SubArray<double>^ inClose,
                                             SubArray<double>^ inVolume,
                                             int optInTimePeriod,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             SubArray<double>^  outReal );

         static enum class RetCode MfiLogic( int    startIdx,
                                             int    endIdx,
                                             SubArray<float>^ inHigh,
                                             SubArray<float>^ inLow,
                                             SubArray<float>^ inClose,
                                             SubArray<float>^ inVolume,
                                             int optInTimePeriod,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             SubArray<double>^  outReal );

         static enum class RetCode MfiLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<double>^ inHigh,
                                             cli::array<double>^ inLow,
                                             cli::array<double>^ inClose,
                                             cli::array<double>^ inVolume,
                                             int optInTimePeriod,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal )
         { return MfiLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inHigh,0),
                         gcnew SubArrayFrom1D<double>(inLow,0),
                         gcnew SubArrayFrom1D<double>(inClose,0),
                         gcnew SubArrayFrom1D<double>(inVolume,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode MfiLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<float>^ inHigh,
                                             cli::array<float>^ inLow,
                                             cli::array<float>^ inClose,
                                             cli::array<float>^ inVolume,
                                             int optInTimePeriod,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal )
         { return MfiLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inHigh,0),
                         gcnew SubArrayFrom1D<float>(inLow,0),
                         gcnew SubArrayFrom1D<float>(inClose,0),
                         gcnew SubArrayFrom1D<float>(inVolume,0),
                         optInTimePeriod,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode MfiLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<double>^ inHigh,
                                             cli::array<double>^ inLow,
                                             cli::array<double>^ inClose,
                                             cli::array<double>^ inVolume,
                                             int optInTimePeriod,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal );
         static enum class RetCode MfiLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<float>^ inHigh,
                                             cli::array<float>^ inLow,
                                             cli::array<float>^ inClose,
                                             cli::array<float>^ inVolume,
                                             int optInTimePeriod,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal );
#endif

         #define TA_MFI Core::Mfi
         #define TA_MFI_Lookback Core::MfiLookback
         #define TA_MFI_Logic Core::MfiLogic
