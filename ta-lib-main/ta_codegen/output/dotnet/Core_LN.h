         static int LnLookback( void );

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode Ln( int    startIdx,
                                       int    endIdx,
                                       SubArray<double>^ inReal,
                                       [Out]int%    outBegIdx,
                                       [Out]int%    outNBElement,
                                       SubArray<double>^  outReal );

         static enum class RetCode Ln( int    startIdx,
                                       int    endIdx,
                                       SubArray<float>^ inReal,
                                       [Out]int%    outBegIdx,
                                       [Out]int%    outNBElement,
                                       SubArray<double>^  outReal );

         static enum class RetCode Ln( int    startIdx,
                                       int    endIdx,
                                       cli::array<double>^ inReal,
                                       [Out]int%    outBegIdx,
                                       [Out]int%    outNBElement,
                                       cli::array<double>^  outReal )
         { return Ln( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode Ln( int    startIdx,
                                       int    endIdx,
                                       cli::array<float>^ inReal,
                                       [Out]int%    outBegIdx,
                                       [Out]int%    outNBElement,
                                       cli::array<double>^  outReal )
         { return Ln( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode Ln( int    startIdx,
                                       int    endIdx,
                                       cli::array<double>^ inReal,
                                       [Out]int%    outBegIdx,
                                       [Out]int%    outNBElement,
                                       cli::array<double>^  outReal );
         static enum class RetCode Ln( int    startIdx,
                                       int    endIdx,
                                       cli::array<float>^ inReal,
                                       [Out]int%    outBegIdx,
                                       [Out]int%    outNBElement,
                                       cli::array<double>^  outReal );
#endif

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode LnLogic( int    startIdx,
                                            int    endIdx,
                                            SubArray<double>^ inReal,
                                            [Out]int%    outBegIdx,
                                            [Out]int%    outNBElement,
                                            SubArray<double>^  outReal );

         static enum class RetCode LnLogic( int    startIdx,
                                            int    endIdx,
                                            SubArray<float>^ inReal,
                                            [Out]int%    outBegIdx,
                                            [Out]int%    outNBElement,
                                            SubArray<double>^  outReal );

         static enum class RetCode LnLogic( int    startIdx,
                                            int    endIdx,
                                            cli::array<double>^ inReal,
                                            [Out]int%    outBegIdx,
                                            [Out]int%    outNBElement,
                                            cli::array<double>^  outReal )
         { return LnLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inReal,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode LnLogic( int    startIdx,
                                            int    endIdx,
                                            cli::array<float>^ inReal,
                                            [Out]int%    outBegIdx,
                                            [Out]int%    outNBElement,
                                            cli::array<double>^  outReal )
         { return LnLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inReal,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode LnLogic( int    startIdx,
                                            int    endIdx,
                                            cli::array<double>^ inReal,
                                            [Out]int%    outBegIdx,
                                            [Out]int%    outNBElement,
                                            cli::array<double>^  outReal );
         static enum class RetCode LnLogic( int    startIdx,
                                            int    endIdx,
                                            cli::array<float>^ inReal,
                                            [Out]int%    outBegIdx,
                                            [Out]int%    outNBElement,
                                            cli::array<double>^  outReal );
#endif

         #define TA_LN Core::Ln
         #define TA_LN_Lookback Core::LnLookback
         #define TA_LN_Logic Core::LnLogic
