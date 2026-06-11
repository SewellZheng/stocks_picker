         static int MedpriceLookback( void );

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode Medprice( int    startIdx,
                                             int    endIdx,
                                             SubArray<double>^ inHigh,
                                             SubArray<double>^ inLow,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             SubArray<double>^  outReal );

         static enum class RetCode Medprice( int    startIdx,
                                             int    endIdx,
                                             SubArray<float>^ inHigh,
                                             SubArray<float>^ inLow,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             SubArray<double>^  outReal );

         static enum class RetCode Medprice( int    startIdx,
                                             int    endIdx,
                                             cli::array<double>^ inHigh,
                                             cli::array<double>^ inLow,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal )
         { return Medprice( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inHigh,0),
                         gcnew SubArrayFrom1D<double>(inLow,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode Medprice( int    startIdx,
                                             int    endIdx,
                                             cli::array<float>^ inHigh,
                                             cli::array<float>^ inLow,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal )
         { return Medprice( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inHigh,0),
                         gcnew SubArrayFrom1D<float>(inLow,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode Medprice( int    startIdx,
                                             int    endIdx,
                                             cli::array<double>^ inHigh,
                                             cli::array<double>^ inLow,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal );
         static enum class RetCode Medprice( int    startIdx,
                                             int    endIdx,
                                             cli::array<float>^ inHigh,
                                             cli::array<float>^ inLow,
                                             [Out]int%    outBegIdx,
                                             [Out]int%    outNBElement,
                                             cli::array<double>^  outReal );
#endif

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode MedpriceLogic( int    startIdx,
                                                  int    endIdx,
                                                  SubArray<double>^ inHigh,
                                                  SubArray<double>^ inLow,
                                                  [Out]int%    outBegIdx,
                                                  [Out]int%    outNBElement,
                                                  SubArray<double>^  outReal );

         static enum class RetCode MedpriceLogic( int    startIdx,
                                                  int    endIdx,
                                                  SubArray<float>^ inHigh,
                                                  SubArray<float>^ inLow,
                                                  [Out]int%    outBegIdx,
                                                  [Out]int%    outNBElement,
                                                  SubArray<double>^  outReal );

         static enum class RetCode MedpriceLogic( int    startIdx,
                                                  int    endIdx,
                                                  cli::array<double>^ inHigh,
                                                  cli::array<double>^ inLow,
                                                  [Out]int%    outBegIdx,
                                                  [Out]int%    outNBElement,
                                                  cli::array<double>^  outReal )
         { return MedpriceLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inHigh,0),
                         gcnew SubArrayFrom1D<double>(inLow,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode MedpriceLogic( int    startIdx,
                                                  int    endIdx,
                                                  cli::array<float>^ inHigh,
                                                  cli::array<float>^ inLow,
                                                  [Out]int%    outBegIdx,
                                                  [Out]int%    outNBElement,
                                                  cli::array<double>^  outReal )
         { return MedpriceLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inHigh,0),
                         gcnew SubArrayFrom1D<float>(inLow,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode MedpriceLogic( int    startIdx,
                                                  int    endIdx,
                                                  cli::array<double>^ inHigh,
                                                  cli::array<double>^ inLow,
                                                  [Out]int%    outBegIdx,
                                                  [Out]int%    outNBElement,
                                                  cli::array<double>^  outReal );
         static enum class RetCode MedpriceLogic( int    startIdx,
                                                  int    endIdx,
                                                  cli::array<float>^ inHigh,
                                                  cli::array<float>^ inLow,
                                                  [Out]int%    outBegIdx,
                                                  [Out]int%    outNBElement,
                                                  cli::array<double>^  outReal );
#endif

         #define TA_MEDPRICE Core::Medprice
         #define TA_MEDPRICE_Lookback Core::MedpriceLookback
         #define TA_MEDPRICE_Logic Core::MedpriceLogic
