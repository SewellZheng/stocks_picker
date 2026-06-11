         static int TrangeLookback( void );

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode Trange( int    startIdx,
                                           int    endIdx,
                                           SubArray<double>^ inHigh,
                                           SubArray<double>^ inLow,
                                           SubArray<double>^ inClose,
                                           [Out]int%    outBegIdx,
                                           [Out]int%    outNBElement,
                                           SubArray<double>^  outReal );

         static enum class RetCode Trange( int    startIdx,
                                           int    endIdx,
                                           SubArray<float>^ inHigh,
                                           SubArray<float>^ inLow,
                                           SubArray<float>^ inClose,
                                           [Out]int%    outBegIdx,
                                           [Out]int%    outNBElement,
                                           SubArray<double>^  outReal );

         static enum class RetCode Trange( int    startIdx,
                                           int    endIdx,
                                           cli::array<double>^ inHigh,
                                           cli::array<double>^ inLow,
                                           cli::array<double>^ inClose,
                                           [Out]int%    outBegIdx,
                                           [Out]int%    outNBElement,
                                           cli::array<double>^  outReal )
         { return Trange( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inHigh,0),
                         gcnew SubArrayFrom1D<double>(inLow,0),
                         gcnew SubArrayFrom1D<double>(inClose,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode Trange( int    startIdx,
                                           int    endIdx,
                                           cli::array<float>^ inHigh,
                                           cli::array<float>^ inLow,
                                           cli::array<float>^ inClose,
                                           [Out]int%    outBegIdx,
                                           [Out]int%    outNBElement,
                                           cli::array<double>^  outReal )
         { return Trange( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inHigh,0),
                         gcnew SubArrayFrom1D<float>(inLow,0),
                         gcnew SubArrayFrom1D<float>(inClose,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode Trange( int    startIdx,
                                           int    endIdx,
                                           cli::array<double>^ inHigh,
                                           cli::array<double>^ inLow,
                                           cli::array<double>^ inClose,
                                           [Out]int%    outBegIdx,
                                           [Out]int%    outNBElement,
                                           cli::array<double>^  outReal );
         static enum class RetCode Trange( int    startIdx,
                                           int    endIdx,
                                           cli::array<float>^ inHigh,
                                           cli::array<float>^ inLow,
                                           cli::array<float>^ inClose,
                                           [Out]int%    outBegIdx,
                                           [Out]int%    outNBElement,
                                           cli::array<double>^  outReal );
#endif

#if defined( _MANAGED ) && defined( USE_SUBARRAY )
         static enum class RetCode TrangeLogic( int    startIdx,
                                                int    endIdx,
                                                SubArray<double>^ inHigh,
                                                SubArray<double>^ inLow,
                                                SubArray<double>^ inClose,
                                                [Out]int%    outBegIdx,
                                                [Out]int%    outNBElement,
                                                SubArray<double>^  outReal );

         static enum class RetCode TrangeLogic( int    startIdx,
                                                int    endIdx,
                                                SubArray<float>^ inHigh,
                                                SubArray<float>^ inLow,
                                                SubArray<float>^ inClose,
                                                [Out]int%    outBegIdx,
                                                [Out]int%    outNBElement,
                                                SubArray<double>^  outReal );

         static enum class RetCode TrangeLogic( int    startIdx,
                                                int    endIdx,
                                                cli::array<double>^ inHigh,
                                                cli::array<double>^ inLow,
                                                cli::array<double>^ inClose,
                                                [Out]int%    outBegIdx,
                                                [Out]int%    outNBElement,
                                                cli::array<double>^  outReal )
         { return TrangeLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<double>(inHigh,0),
                         gcnew SubArrayFrom1D<double>(inLow,0),
                         gcnew SubArrayFrom1D<double>(inClose,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
         static enum class RetCode TrangeLogic( int    startIdx,
                                                int    endIdx,
                                                cli::array<float>^ inHigh,
                                                cli::array<float>^ inLow,
                                                cli::array<float>^ inClose,
                                                [Out]int%    outBegIdx,
                                                [Out]int%    outNBElement,
                                                cli::array<double>^  outReal )
         { return TrangeLogic( startIdx, endIdx,
                         gcnew SubArrayFrom1D<float>(inHigh,0),
                         gcnew SubArrayFrom1D<float>(inLow,0),
                         gcnew SubArrayFrom1D<float>(inClose,0),
             outBegIdx,
             outNBElement,
               gcnew SubArrayFrom1D<double>(outReal,0) );
         }
#elif defined( _MANAGED )
         static enum class RetCode TrangeLogic( int    startIdx,
                                                int    endIdx,
                                                cli::array<double>^ inHigh,
                                                cli::array<double>^ inLow,
                                                cli::array<double>^ inClose,
                                                [Out]int%    outBegIdx,
                                                [Out]int%    outNBElement,
                                                cli::array<double>^  outReal );
         static enum class RetCode TrangeLogic( int    startIdx,
                                                int    endIdx,
                                                cli::array<float>^ inHigh,
                                                cli::array<float>^ inLow,
                                                cli::array<float>^ inClose,
                                                [Out]int%    outBegIdx,
                                                [Out]int%    outNBElement,
                                                cli::array<double>^  outReal );
#endif

         #define TA_TRANGE Core::Trange
         #define TA_TRANGE_Lookback Core::TrangeLookback
         #define TA_TRANGE_Logic Core::TrangeLogic
