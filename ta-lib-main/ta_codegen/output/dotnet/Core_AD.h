         static int AdLookback( void );

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode Ad( int    startIdx,
                                       int    endIdx,
                                       SubArray<double>^ inHigh,
                                       SubArray<double>^ inLow,
                                       SubArray<double>^ inClose,
                                       SubArray<double>^ inVolume,
                                       [Out]int%    outBegIdx,
                                       [Out]int%    outNBElement,
                                       SubArray<double>^  outReal );

         static enum class RetCode Ad( int    startIdx,
                                       int    endIdx,
                                       SubArray<float>^ inHigh,
                                       SubArray<float>^ inLow,
                                       SubArray<float>^ inClose,
                                       SubArray<float>^ inVolume,
                                       [Out]int%    outBegIdx,
                                       [Out]int%    outNBElement,
                                       SubArray<double>^  outReal );

         static enum class RetCode Ad( int    startIdx,
                                       int    endIdx,
                                       cli::array<double>^ inHigh,
                                       cli::array<double>^ inLow,
                                       cli::array<double>^ inClose,
                                       cli::array<double>^ inVolume,
                                       [Out]int%    outBegIdx,
                                       [Out]int%    outNBElement,
                                       cli::array<double>^  outReal )
         { return Ad( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inHigh,0),
                         gcnew SubArrayFrom1D<double>(inLow,0),
                         gcnew SubArrayFrom1D<double>(inClose,0),
                         gcnew SubArrayFrom1D<double>(inVolume,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode Ad( int    startIdx,
                                       int    endIdx,
                                       cli::array<float>^ inHigh,
                                       cli::array<float>^ inLow,
                                       cli::array<float>^ inClose,
                                       cli::array<float>^ inVolume,
                                       [Out]int%    outBegIdx,
                                       [Out]int%    outNBElement,
                                       cli::array<double>^  outReal )
         { return Ad( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inHigh,0),
                         gcnew SubArrayFrom1D<float>(inLow,0),
                         gcnew SubArrayFrom1D<float>(inClose,0),
                         gcnew SubArrayFrom1D<float>(inVolume,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode Ad( int    startIdx,
                                       int    endIdx,
                                       cli::array<double>^ inHigh,
                                       cli::array<double>^ inLow,
                                       cli::array<double>^ inClose,
                                       cli::array<double>^ inVolume,
                                       [Out]int%    outBegIdx,
                                       [Out]int%    outNBElement,
                                       cli::array<double>^  outReal );
         static enum class RetCode Ad( int    startIdx,
                                       int    endIdx,
                                       cli::array<float>^ inHigh,
                                       cli::array<float>^ inLow,
                                       cli::array<float>^ inClose,
                                       cli::array<float>^ inVolume,
                                       [Out]int%    outBegIdx,
                                       [Out]int%    outNBElement,
                                       cli::array<double>^  outReal );
#endif

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode AdLogic( int    startIdx,
                                            int    endIdx,
                                            SubArray<double>^ inHigh,
                                            SubArray<double>^ inLow,
                                            SubArray<double>^ inClose,
                                            SubArray<double>^ inVolume,
                                            [Out]int%    outBegIdx,
                                            [Out]int%    outNBElement,
                                            SubArray<double>^  outReal );

         static enum class RetCode AdLogic( int    startIdx,
                                            int    endIdx,
                                            SubArray<float>^ inHigh,
                                            SubArray<float>^ inLow,
                                            SubArray<float>^ inClose,
                                            SubArray<float>^ inVolume,
                                            [Out]int%    outBegIdx,
                                            [Out]int%    outNBElement,
                                            SubArray<double>^  outReal );

         static enum class RetCode AdLogic( int    startIdx,
                                            int    endIdx,
                                            cli::array<double>^ inHigh,
                                            cli::array<double>^ inLow,
                                            cli::array<double>^ inClose,
                                            cli::array<double>^ inVolume,
                                            [Out]int%    outBegIdx,
                                            [Out]int%    outNBElement,
                                            cli::array<double>^  outReal )
         { return AdLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inHigh,0),
                         gcnew SubArrayFrom1D<double>(inLow,0),
                         gcnew SubArrayFrom1D<double>(inClose,0),
                         gcnew SubArrayFrom1D<double>(inVolume,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode AdLogic( int    startIdx,
                                            int    endIdx,
                                            cli::array<float>^ inHigh,
                                            cli::array<float>^ inLow,
                                            cli::array<float>^ inClose,
                                            cli::array<float>^ inVolume,
                                            [Out]int%    outBegIdx,
                                            [Out]int%    outNBElement,
                                            cli::array<double>^  outReal )
         { return AdLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inHigh,0),
                         gcnew SubArrayFrom1D<float>(inLow,0),
                         gcnew SubArrayFrom1D<float>(inClose,0),
                         gcnew SubArrayFrom1D<float>(inVolume,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode AdLogic( int    startIdx,
                                            int    endIdx,
                                            cli::array<double>^ inHigh,
                                            cli::array<double>^ inLow,
                                            cli::array<double>^ inClose,
                                            cli::array<double>^ inVolume,
                                            [Out]int%    outBegIdx,
                                            [Out]int%    outNBElement,
                                            cli::array<double>^  outReal );
         static enum class RetCode AdLogic( int    startIdx,
                                            int    endIdx,
                                            cli::array<float>^ inHigh,
                                            cli::array<float>^ inLow,
                                            cli::array<float>^ inClose,
                                            cli::array<float>^ inVolume,
                                            [Out]int%    outBegIdx,
                                            [Out]int%    outNBElement,
                                            cli::array<double>^  outReal );
#endif

         #define TA_AD Core::Ad
         #define TA_AD_Lookback Core::AdLookback
         #define TA_AD_Logic Core::AdLogic
