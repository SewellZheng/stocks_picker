         static int CdleveningstarLookback( double           optInPenetration  /* From 0 to 179769313486231570000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000 */ );

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode Cdleveningstar( int    startIdx,
                                                   int    endIdx,
                                                   SubArray<double>^ inOpen,
                                                   SubArray<double>^ inHigh,
                                                   SubArray<double>^ inLow,
                                                   SubArray<double>^ inClose,
                                                   double optInPenetration,
                                                   [Out]int%    outBegIdx,
                                                   [Out]int%    outNBElement,
                                                   SubArray<int>^  outInteger );

         static enum class RetCode Cdleveningstar( int    startIdx,
                                                   int    endIdx,
                                                   SubArray<float>^ inOpen,
                                                   SubArray<float>^ inHigh,
                                                   SubArray<float>^ inLow,
                                                   SubArray<float>^ inClose,
                                                   double optInPenetration,
                                                   [Out]int%    outBegIdx,
                                                   [Out]int%    outNBElement,
                                                   SubArray<int>^  outInteger );

         static enum class RetCode Cdleveningstar( int    startIdx,
                                                   int    endIdx,
                                                   cli::array<double>^ inOpen,
                                                   cli::array<double>^ inHigh,
                                                   cli::array<double>^ inLow,
                                                   cli::array<double>^ inClose,
                                                   double optInPenetration,
                                                   [Out]int%    outBegIdx,
                                                   [Out]int%    outNBElement,
                                                   cli::array<int>^  outInteger )
         { return Cdleveningstar( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inOpen,0),
                         gcnew SubArrayFrom1D<double>(inHigh,0),
                         gcnew SubArrayFrom1D<double>(inLow,0),
                         gcnew SubArrayFrom1D<double>(inClose,0),
                         optInPenetration,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<int>(outInteger,0) );
         }
         static enum class RetCode Cdleveningstar( int    startIdx,
                                                   int    endIdx,
                                                   cli::array<float>^ inOpen,
                                                   cli::array<float>^ inHigh,
                                                   cli::array<float>^ inLow,
                                                   cli::array<float>^ inClose,
                                                   double optInPenetration,
                                                   [Out]int%    outBegIdx,
                                                   [Out]int%    outNBElement,
                                                   cli::array<int>^  outInteger )
         { return Cdleveningstar( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inOpen,0),
                         gcnew SubArrayFrom1D<float>(inHigh,0),
                         gcnew SubArrayFrom1D<float>(inLow,0),
                         gcnew SubArrayFrom1D<float>(inClose,0),
                         optInPenetration,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<int>(outInteger,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode Cdleveningstar( int    startIdx,
                                                   int    endIdx,
                                                   cli::array<double>^ inOpen,
                                                   cli::array<double>^ inHigh,
                                                   cli::array<double>^ inLow,
                                                   cli::array<double>^ inClose,
                                                   double optInPenetration,
                                                   [Out]int%    outBegIdx,
                                                   [Out]int%    outNBElement,
                                                   cli::array<int>^  outInteger );
         static enum class RetCode Cdleveningstar( int    startIdx,
                                                   int    endIdx,
                                                   cli::array<float>^ inOpen,
                                                   cli::array<float>^ inHigh,
                                                   cli::array<float>^ inLow,
                                                   cli::array<float>^ inClose,
                                                   double optInPenetration,
                                                   [Out]int%    outBegIdx,
                                                   [Out]int%    outNBElement,
                                                   cli::array<int>^  outInteger );
#endif

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode CdleveningstarLogic( int    startIdx,
                                                        int    endIdx,
                                                        SubArray<double>^ inOpen,
                                                        SubArray<double>^ inHigh,
                                                        SubArray<double>^ inLow,
                                                        SubArray<double>^ inClose,
                                                        double optInPenetration,
                                                        [Out]int%    outBegIdx,
                                                        [Out]int%    outNBElement,
                                                        SubArray<int>^  outInteger );

         static enum class RetCode CdleveningstarLogic( int    startIdx,
                                                        int    endIdx,
                                                        SubArray<float>^ inOpen,
                                                        SubArray<float>^ inHigh,
                                                        SubArray<float>^ inLow,
                                                        SubArray<float>^ inClose,
                                                        double optInPenetration,
                                                        [Out]int%    outBegIdx,
                                                        [Out]int%    outNBElement,
                                                        SubArray<int>^  outInteger );

         static enum class RetCode CdleveningstarLogic( int    startIdx,
                                                        int    endIdx,
                                                        cli::array<double>^ inOpen,
                                                        cli::array<double>^ inHigh,
                                                        cli::array<double>^ inLow,
                                                        cli::array<double>^ inClose,
                                                        double optInPenetration,
                                                        [Out]int%    outBegIdx,
                                                        [Out]int%    outNBElement,
                                                        cli::array<int>^  outInteger )
         { return CdleveningstarLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inOpen,0),
                         gcnew SubArrayFrom1D<double>(inHigh,0),
                         gcnew SubArrayFrom1D<double>(inLow,0),
                         gcnew SubArrayFrom1D<double>(inClose,0),
                         optInPenetration,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<int>(outInteger,0) );
         }
         static enum class RetCode CdleveningstarLogic( int    startIdx,
                                                        int    endIdx,
                                                        cli::array<float>^ inOpen,
                                                        cli::array<float>^ inHigh,
                                                        cli::array<float>^ inLow,
                                                        cli::array<float>^ inClose,
                                                        double optInPenetration,
                                                        [Out]int%    outBegIdx,
                                                        [Out]int%    outNBElement,
                                                        cli::array<int>^  outInteger )
         { return CdleveningstarLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inOpen,0),
                         gcnew SubArrayFrom1D<float>(inHigh,0),
                         gcnew SubArrayFrom1D<float>(inLow,0),
                         gcnew SubArrayFrom1D<float>(inClose,0),
                         optInPenetration,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<int>(outInteger,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode CdleveningstarLogic( int    startIdx,
                                                        int    endIdx,
                                                        cli::array<double>^ inOpen,
                                                        cli::array<double>^ inHigh,
                                                        cli::array<double>^ inLow,
                                                        cli::array<double>^ inClose,
                                                        double optInPenetration,
                                                        [Out]int%    outBegIdx,
                                                        [Out]int%    outNBElement,
                                                        cli::array<int>^  outInteger );
         static enum class RetCode CdleveningstarLogic( int    startIdx,
                                                        int    endIdx,
                                                        cli::array<float>^ inOpen,
                                                        cli::array<float>^ inHigh,
                                                        cli::array<float>^ inLow,
                                                        cli::array<float>^ inClose,
                                                        double optInPenetration,
                                                        [Out]int%    outBegIdx,
                                                        [Out]int%    outNBElement,
                                                        cli::array<int>^  outInteger );
#endif

         #define TA_CDLEVENINGSTAR Core::Cdleveningstar
         #define TA_CDLEVENINGSTAR_Lookback Core::CdleveningstarLookback
         #define TA_CDLEVENINGSTAR_Logic Core::CdleveningstarLogic
