         static int SarLookback( double           optInAcceleration  /* From 0 to 179769313486231570000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000 */, double           optInMaximum  /* From 0 to 179769313486231570000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000 */ );

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode Sar( int    startIdx,
                                        int    endIdx,
                                        SubArray<double>^ inHigh,
                                        SubArray<double>^ inLow,
                                        double optInAcceleration,
                                        double optInMaximum,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        SubArray<double>^  outReal );

         static enum class RetCode Sar( int    startIdx,
                                        int    endIdx,
                                        SubArray<float>^ inHigh,
                                        SubArray<float>^ inLow,
                                        double optInAcceleration,
                                        double optInMaximum,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        SubArray<double>^  outReal );

         static enum class RetCode Sar( int    startIdx,
                                        int    endIdx,
                                        cli::array<double>^ inHigh,
                                        cli::array<double>^ inLow,
                                        double optInAcceleration,
                                        double optInMaximum,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal )
         { return Sar( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inHigh,0),
                         gcnew SubArrayFrom1D<double>(inLow,0),
                         optInAcceleration,
                         optInMaximum,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode Sar( int    startIdx,
                                        int    endIdx,
                                        cli::array<float>^ inHigh,
                                        cli::array<float>^ inLow,
                                        double optInAcceleration,
                                        double optInMaximum,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal )
         { return Sar( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inHigh,0),
                         gcnew SubArrayFrom1D<float>(inLow,0),
                         optInAcceleration,
                         optInMaximum,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode Sar( int    startIdx,
                                        int    endIdx,
                                        cli::array<double>^ inHigh,
                                        cli::array<double>^ inLow,
                                        double optInAcceleration,
                                        double optInMaximum,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal );
         static enum class RetCode Sar( int    startIdx,
                                        int    endIdx,
                                        cli::array<float>^ inHigh,
                                        cli::array<float>^ inLow,
                                        double optInAcceleration,
                                        double optInMaximum,
                                        [Out]int%    outBegIdx,
                                        [Out]int%    outNBElement,
                                        cli::array<double>^  outReal );
#endif

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode SarLogic( int    startIdx,
                                             int    endIdx,
                                             SubArray<double>^ inHigh,
                                             SubArray<double>^ inLow,
                                             double optInAcceleration,
                                             double optInMaximum,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             SubArray<double>^  outReal );

         static enum class RetCode SarLogic( int    startIdx,
                                             int    endIdx,
                                             SubArray<float>^ inHigh,
                                             SubArray<float>^ inLow,
                                             double optInAcceleration,
                                             double optInMaximum,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             SubArray<double>^  outReal );

         static enum class RetCode SarLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<double>^ inHigh,
                                             cli::array<double>^ inLow,
                                             double optInAcceleration,
                                             double optInMaximum,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal )
         { return SarLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inHigh,0),
                         gcnew SubArrayFrom1D<double>(inLow,0),
                         optInAcceleration,
                         optInMaximum,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode SarLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<float>^ inHigh,
                                             cli::array<float>^ inLow,
                                             double optInAcceleration,
                                             double optInMaximum,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal )
         { return SarLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inHigh,0),
                         gcnew SubArrayFrom1D<float>(inLow,0),
                         optInAcceleration,
                         optInMaximum,
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode SarLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<double>^ inHigh,
                                             cli::array<double>^ inLow,
                                             double optInAcceleration,
                                             double optInMaximum,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal );
         static enum class RetCode SarLogic( int    startIdx,
                                             int    endIdx,
                                             cli::array<float>^ inHigh,
                                             cli::array<float>^ inLow,
                                             double optInAcceleration,
                                             double optInMaximum,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal );
#endif

         #define TA_SAR Core::Sar
         #define TA_SAR_Lookback Core::SarLookback
         #define TA_SAR_Logic Core::SarLogic
