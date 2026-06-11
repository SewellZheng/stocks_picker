/* Generated */
   public int bbandsLookback( int optInTimePeriod, double optInNbDevUp, double optInNbDevDn, MAType optInMAType )
   {
      return maLookback(optInTimePeriod, optInMAType) ;

   }
   public RetCode bbands( int startIdx,
                          int endIdx,
                          double inReal[],
                          int optInTimePeriod,
                          double optInNbDevUp,
                          double optInNbDevDn,
                          MAType optInMAType,
                          MInteger outBegIdx,
                          MInteger outNBElement,
                          double outRealUpperBand[],
                          double outRealMiddleBand[],
                          double outRealLowerBand[] )
   {
      RetCode retCode;
      int i = 0;
      double tempReal = 0;
      double tempReal2 = 0;
      double[] tempBuffer1;
      double[] tempBuffer2;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      if( (inReal==outRealUpperBand) ) {
         tempBuffer1 = outRealMiddleBand;
         tempBuffer2 = outRealLowerBand;
      } else if( (inReal==outRealLowerBand) ) {
         tempBuffer1 = outRealMiddleBand;
         tempBuffer2 = outRealUpperBand;
      } else if( (inReal==outRealMiddleBand) ) {
         tempBuffer1 = outRealLowerBand;
         tempBuffer2 = outRealUpperBand;
      } else {
         tempBuffer1 = outRealMiddleBand;
         tempBuffer2 = outRealUpperBand;
      }
      if( ((tempBuffer1==inReal)||(tempBuffer2==inReal)) ) {
         return RetCode.BadParam ;
      }
      retCode = maLogic(startIdx, endIdx, inReal, optInTimePeriod, optInMAType, outBegIdx, outNBElement, tempBuffer1);
      if( ((retCode!=RetCode.Success)||(((int)outNBElement.value)==0)) ) {
         outNBElement.value = 0;
         return retCode ;
      }
      if( (optInMAType==MAType.Sma) ) {
         double _tempReal;
         double _periodTotal2;
         double _meanValue2;
         int _outIdx;
         int _startSum;
         int _endSum;
         _startSum = ((1+((int)outBegIdx.value))-optInTimePeriod);
         _endSum = ((int)outBegIdx.value);
         _periodTotal2 = 0;
         for( _outIdx = _startSum; (_outIdx<_endSum); _outIdx += 1 ) {
            _tempReal = inReal[_outIdx];
            _tempReal *= _tempReal;
            _periodTotal2 += _tempReal;
         }
         for( _outIdx = 0; (_outIdx<((int)outNBElement.value)); _outIdx += 1, _startSum += 1, _endSum += 1 ) {
            _tempReal = inReal[_endSum];
            _tempReal *= _tempReal;
            _periodTotal2 += _tempReal;
            _meanValue2 = (_periodTotal2/optInTimePeriod);
            _tempReal = inReal[_startSum];
            _tempReal *= _tempReal;
            _periodTotal2 -= _tempReal;
            _tempReal = tempBuffer1[_outIdx];
            _tempReal *= _tempReal;
            _meanValue2 -= _tempReal;
            if( !((_meanValue2 < 0.00000000000001)) ) {
               tempBuffer2[_outIdx] = Math.sqrt(_meanValue2);
            } else {
               tempBuffer2[_outIdx] = 0.0;
            }
         }
      } else {
         retCode = stddevLogic(((int)outBegIdx.value), endIdx, inReal, optInTimePeriod, 1.0, outBegIdx, outNBElement, tempBuffer2);
         if( (retCode!=RetCode.Success) ) {
            outNBElement.value = 0;
            return retCode ;
         }
      }
      if( (tempBuffer1!=outRealMiddleBand) ) {
         System.arraycopy(tempBuffer1, 0, outRealMiddleBand, 0, (outNBElement.value*1));
      }
      if( (optInNbDevUp==optInNbDevDn) ) {
         if( (optInNbDevUp==1.0) ) {
            for( i = 0; (i<((int)outNBElement.value)); i += 1 ) {
               tempReal = tempBuffer2[i];
               tempReal2 = outRealMiddleBand[i];
               outRealUpperBand[i] = (tempReal2+tempReal);
               outRealLowerBand[i] = (tempReal2-tempReal);
            }
         } else {
            for( i = 0; (i<((int)outNBElement.value)); i += 1 ) {
               tempReal = (tempBuffer2[i]*optInNbDevUp);
               tempReal2 = outRealMiddleBand[i];
               outRealUpperBand[i] = (tempReal2+tempReal);
               outRealLowerBand[i] = (tempReal2-tempReal);
            }
         }
      } else if( (optInNbDevUp==1.0) ) {
         for( i = 0; (i<((int)outNBElement.value)); i += 1 ) {
            tempReal = tempBuffer2[i];
            tempReal2 = outRealMiddleBand[i];
            outRealUpperBand[i] = (tempReal2+tempReal);
            outRealLowerBand[i] = (tempReal2-(tempReal*optInNbDevDn));
         }
      } else if( (optInNbDevDn==1.0) ) {
         for( i = 0; (i<((int)outNBElement.value)); i += 1 ) {
            tempReal = tempBuffer2[i];
            tempReal2 = outRealMiddleBand[i];
            outRealLowerBand[i] = (tempReal2-tempReal);
            outRealUpperBand[i] = (tempReal2+(tempReal*optInNbDevUp));
         }
      } else {
         for( i = 0; (i<((int)outNBElement.value)); i += 1 ) {
            tempReal = tempBuffer2[i];
            tempReal2 = outRealMiddleBand[i];
            outRealUpperBand[i] = (tempReal2+(tempReal*optInNbDevUp));
            outRealLowerBand[i] = (tempReal2-(tempReal*optInNbDevDn));
         }
      }
      return RetCode.Success ;
   }
   public RetCode bbandsLogic( int startIdx,
                               int endIdx,
                               double inReal[],
                               int optInTimePeriod,
                               double optInNbDevUp,
                               double optInNbDevDn,
                               MAType optInMAType,
                               MInteger outBegIdx,
                               MInteger outNBElement,
                               double outRealUpperBand[],
                               double outRealMiddleBand[],
                               double outRealLowerBand[] )
   {
      RetCode retCode;
      int i = 0;
      double tempReal = 0;
      double tempReal2 = 0;
      double[] tempBuffer1;
      double[] tempBuffer2;
      if( (inReal==outRealUpperBand) ) {
         tempBuffer1 = outRealMiddleBand;
         tempBuffer2 = outRealLowerBand;
      } else if( (inReal==outRealLowerBand) ) {
         tempBuffer1 = outRealMiddleBand;
         tempBuffer2 = outRealUpperBand;
      } else if( (inReal==outRealMiddleBand) ) {
         tempBuffer1 = outRealLowerBand;
         tempBuffer2 = outRealUpperBand;
      } else {
         tempBuffer1 = outRealMiddleBand;
         tempBuffer2 = outRealUpperBand;
      }
      if( ((tempBuffer1==inReal)||(tempBuffer2==inReal)) ) {
         return RetCode.BadParam ;
      }
      retCode = maLogic(startIdx, endIdx, inReal, optInTimePeriod, optInMAType, outBegIdx, outNBElement, tempBuffer1);
      if( ((retCode!=RetCode.Success)||(((int)outNBElement.value)==0)) ) {
         outNBElement.value = 0;
         return retCode ;
      }
      if( (optInMAType==MAType.Sma) ) {
         double _tempReal;
         double _periodTotal2;
         double _meanValue2;
         int _outIdx;
         int _startSum;
         int _endSum;
         _startSum = ((1+((int)outBegIdx.value))-optInTimePeriod);
         _endSum = ((int)outBegIdx.value);
         _periodTotal2 = 0;
         for( _outIdx = _startSum; (_outIdx<_endSum); _outIdx += 1 ) {
            _tempReal = inReal[_outIdx];
            _tempReal *= _tempReal;
            _periodTotal2 += _tempReal;
         }
         for( _outIdx = 0; (_outIdx<((int)outNBElement.value)); _outIdx += 1, _startSum += 1, _endSum += 1 ) {
            _tempReal = inReal[_endSum];
            _tempReal *= _tempReal;
            _periodTotal2 += _tempReal;
            _meanValue2 = (_periodTotal2/optInTimePeriod);
            _tempReal = inReal[_startSum];
            _tempReal *= _tempReal;
            _periodTotal2 -= _tempReal;
            _tempReal = tempBuffer1[_outIdx];
            _tempReal *= _tempReal;
            _meanValue2 -= _tempReal;
            if( !((_meanValue2 < 0.00000000000001)) ) {
               tempBuffer2[_outIdx] = Math.sqrt(_meanValue2);
            } else {
               tempBuffer2[_outIdx] = 0.0;
            }
         }
      } else {
         retCode = stddevLogic(((int)outBegIdx.value), endIdx, inReal, optInTimePeriod, 1.0, outBegIdx, outNBElement, tempBuffer2);
         if( (retCode!=RetCode.Success) ) {
            outNBElement.value = 0;
            return retCode ;
         }
      }
      if( (tempBuffer1!=outRealMiddleBand) ) {
         System.arraycopy(tempBuffer1, 0, outRealMiddleBand, 0, (outNBElement.value*1));
      }
      if( (optInNbDevUp==optInNbDevDn) ) {
         if( (optInNbDevUp==1.0) ) {
            for( i = 0; (i<((int)outNBElement.value)); i += 1 ) {
               tempReal = tempBuffer2[i];
               tempReal2 = outRealMiddleBand[i];
               outRealUpperBand[i] = (tempReal2+tempReal);
               outRealLowerBand[i] = (tempReal2-tempReal);
            }
         } else {
            for( i = 0; (i<((int)outNBElement.value)); i += 1 ) {
               tempReal = (tempBuffer2[i]*optInNbDevUp);
               tempReal2 = outRealMiddleBand[i];
               outRealUpperBand[i] = (tempReal2+tempReal);
               outRealLowerBand[i] = (tempReal2-tempReal);
            }
         }
      } else if( (optInNbDevUp==1.0) ) {
         for( i = 0; (i<((int)outNBElement.value)); i += 1 ) {
            tempReal = tempBuffer2[i];
            tempReal2 = outRealMiddleBand[i];
            outRealUpperBand[i] = (tempReal2+tempReal);
            outRealLowerBand[i] = (tempReal2-(tempReal*optInNbDevDn));
         }
      } else if( (optInNbDevDn==1.0) ) {
         for( i = 0; (i<((int)outNBElement.value)); i += 1 ) {
            tempReal = tempBuffer2[i];
            tempReal2 = outRealMiddleBand[i];
            outRealLowerBand[i] = (tempReal2-tempReal);
            outRealUpperBand[i] = (tempReal2+(tempReal*optInNbDevUp));
         }
      } else {
         for( i = 0; (i<((int)outNBElement.value)); i += 1 ) {
            tempReal = tempBuffer2[i];
            tempReal2 = outRealMiddleBand[i];
            outRealUpperBand[i] = (tempReal2+(tempReal*optInNbDevUp));
            outRealLowerBand[i] = (tempReal2-(tempReal*optInNbDevDn));
         }
      }
      return RetCode.Success ;
   }
   public RetCode bbands( int startIdx,
                          int endIdx,
                          float inReal[],
                          int optInTimePeriod,
                          double optInNbDevUp,
                          double optInNbDevDn,
                          MAType optInMAType,
                          MInteger outBegIdx,
                          MInteger outNBElement,
                          double outRealUpperBand[],
                          double outRealMiddleBand[],
                          double outRealLowerBand[] )
   {
      RetCode retCode;
      int i = 0;
      double tempReal = 0;
      double tempReal2 = 0;
      double[] tempBuffer1;
      double[] tempBuffer2;
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      if( false ) {
         tempBuffer1 = outRealMiddleBand;
         tempBuffer2 = outRealLowerBand;
      } else if( false ) {
         tempBuffer1 = outRealMiddleBand;
         tempBuffer2 = outRealUpperBand;
      } else if( false ) {
         tempBuffer1 = outRealLowerBand;
         tempBuffer2 = outRealUpperBand;
      } else {
         tempBuffer1 = outRealMiddleBand;
         tempBuffer2 = outRealUpperBand;
      }
      if( (false||false) ) {
         return RetCode.BadParam ;
      }
      retCode = maLogic(startIdx, endIdx, inReal, optInTimePeriod, optInMAType, outBegIdx, outNBElement, tempBuffer1);
      if( ((retCode!=RetCode.Success)||(((int)outNBElement.value)==0)) ) {
         outNBElement.value = 0;
         return retCode ;
      }
      if( (optInMAType==MAType.Sma) ) {
         double _tempReal;
         double _periodTotal2;
         double _meanValue2;
         int _outIdx;
         int _startSum;
         int _endSum;
         _startSum = ((1+((int)outBegIdx.value))-optInTimePeriod);
         _endSum = ((int)outBegIdx.value);
         _periodTotal2 = 0;
         for( _outIdx = _startSum; (_outIdx<_endSum); _outIdx += 1 ) {
            _tempReal = inReal[_outIdx];
            _tempReal *= _tempReal;
            _periodTotal2 += _tempReal;
         }
         for( _outIdx = 0; (_outIdx<((int)outNBElement.value)); _outIdx += 1, _startSum += 1, _endSum += 1 ) {
            _tempReal = inReal[_endSum];
            _tempReal *= _tempReal;
            _periodTotal2 += _tempReal;
            _meanValue2 = (_periodTotal2/optInTimePeriod);
            _tempReal = inReal[_startSum];
            _tempReal *= _tempReal;
            _periodTotal2 -= _tempReal;
            _tempReal = tempBuffer1[_outIdx];
            _tempReal *= _tempReal;
            _meanValue2 -= _tempReal;
            if( !((_meanValue2 < 0.00000000000001)) ) {
               tempBuffer2[_outIdx] = Math.sqrt(_meanValue2);
            } else {
               tempBuffer2[_outIdx] = 0.0;
            }
         }
      } else {
         retCode = stddevLogic(((int)outBegIdx.value), endIdx, inReal, optInTimePeriod, 1.0, outBegIdx, outNBElement, tempBuffer2);
         if( (retCode!=RetCode.Success) ) {
            outNBElement.value = 0;
            return retCode ;
         }
      }
      if( (tempBuffer1!=outRealMiddleBand) ) {
         System.arraycopy(tempBuffer1, 0, outRealMiddleBand, 0, (outNBElement.value*1));
      }
      if( (optInNbDevUp==optInNbDevDn) ) {
         if( (optInNbDevUp==1.0) ) {
            for( i = 0; (i<((int)outNBElement.value)); i += 1 ) {
               tempReal = tempBuffer2[i];
               tempReal2 = outRealMiddleBand[i];
               outRealUpperBand[i] = (tempReal2+tempReal);
               outRealLowerBand[i] = (tempReal2-tempReal);
            }
         } else {
            for( i = 0; (i<((int)outNBElement.value)); i += 1 ) {
               tempReal = (tempBuffer2[i]*optInNbDevUp);
               tempReal2 = outRealMiddleBand[i];
               outRealUpperBand[i] = (tempReal2+tempReal);
               outRealLowerBand[i] = (tempReal2-tempReal);
            }
         }
      } else if( (optInNbDevUp==1.0) ) {
         for( i = 0; (i<((int)outNBElement.value)); i += 1 ) {
            tempReal = tempBuffer2[i];
            tempReal2 = outRealMiddleBand[i];
            outRealUpperBand[i] = (tempReal2+tempReal);
            outRealLowerBand[i] = (tempReal2-(tempReal*optInNbDevDn));
         }
      } else if( (optInNbDevDn==1.0) ) {
         for( i = 0; (i<((int)outNBElement.value)); i += 1 ) {
            tempReal = tempBuffer2[i];
            tempReal2 = outRealMiddleBand[i];
            outRealLowerBand[i] = (tempReal2-tempReal);
            outRealUpperBand[i] = (tempReal2+(tempReal*optInNbDevUp));
         }
      } else {
         for( i = 0; (i<((int)outNBElement.value)); i += 1 ) {
            tempReal = tempBuffer2[i];
            tempReal2 = outRealMiddleBand[i];
            outRealUpperBand[i] = (tempReal2+(tempReal*optInNbDevUp));
            outRealLowerBand[i] = (tempReal2-(tempReal*optInNbDevDn));
         }
      }
      return RetCode.Success ;
   }
   public RetCode bbandsLogic( int startIdx,
                               int endIdx,
                               float inReal[],
                               int optInTimePeriod,
                               double optInNbDevUp,
                               double optInNbDevDn,
                               MAType optInMAType,
                               MInteger outBegIdx,
                               MInteger outNBElement,
                               double outRealUpperBand[],
                               double outRealMiddleBand[],
                               double outRealLowerBand[] )
   {
      RetCode retCode;
      int i = 0;
      double tempReal = 0;
      double tempReal2 = 0;
      double[] tempBuffer1;
      double[] tempBuffer2;
      if( false ) {
         tempBuffer1 = outRealMiddleBand;
         tempBuffer2 = outRealLowerBand;
      } else if( false ) {
         tempBuffer1 = outRealMiddleBand;
         tempBuffer2 = outRealUpperBand;
      } else if( false ) {
         tempBuffer1 = outRealLowerBand;
         tempBuffer2 = outRealUpperBand;
      } else {
         tempBuffer1 = outRealMiddleBand;
         tempBuffer2 = outRealUpperBand;
      }
      if( (false||false) ) {
         return RetCode.BadParam ;
      }
      retCode = maLogic(startIdx, endIdx, inReal, optInTimePeriod, optInMAType, outBegIdx, outNBElement, tempBuffer1);
      if( ((retCode!=RetCode.Success)||(((int)outNBElement.value)==0)) ) {
         outNBElement.value = 0;
         return retCode ;
      }
      if( (optInMAType==MAType.Sma) ) {
         double _tempReal;
         double _periodTotal2;
         double _meanValue2;
         int _outIdx;
         int _startSum;
         int _endSum;
         _startSum = ((1+((int)outBegIdx.value))-optInTimePeriod);
         _endSum = ((int)outBegIdx.value);
         _periodTotal2 = 0;
         for( _outIdx = _startSum; (_outIdx<_endSum); _outIdx += 1 ) {
            _tempReal = inReal[_outIdx];
            _tempReal *= _tempReal;
            _periodTotal2 += _tempReal;
         }
         for( _outIdx = 0; (_outIdx<((int)outNBElement.value)); _outIdx += 1, _startSum += 1, _endSum += 1 ) {
            _tempReal = inReal[_endSum];
            _tempReal *= _tempReal;
            _periodTotal2 += _tempReal;
            _meanValue2 = (_periodTotal2/optInTimePeriod);
            _tempReal = inReal[_startSum];
            _tempReal *= _tempReal;
            _periodTotal2 -= _tempReal;
            _tempReal = tempBuffer1[_outIdx];
            _tempReal *= _tempReal;
            _meanValue2 -= _tempReal;
            if( !((_meanValue2 < 0.00000000000001)) ) {
               tempBuffer2[_outIdx] = Math.sqrt(_meanValue2);
            } else {
               tempBuffer2[_outIdx] = 0.0;
            }
         }
      } else {
         retCode = stddevLogic(((int)outBegIdx.value), endIdx, inReal, optInTimePeriod, 1.0, outBegIdx, outNBElement, tempBuffer2);
         if( (retCode!=RetCode.Success) ) {
            outNBElement.value = 0;
            return retCode ;
         }
      }
      if( (tempBuffer1!=outRealMiddleBand) ) {
         System.arraycopy(tempBuffer1, 0, outRealMiddleBand, 0, (outNBElement.value*1));
      }
      if( (optInNbDevUp==optInNbDevDn) ) {
         if( (optInNbDevUp==1.0) ) {
            for( i = 0; (i<((int)outNBElement.value)); i += 1 ) {
               tempReal = tempBuffer2[i];
               tempReal2 = outRealMiddleBand[i];
               outRealUpperBand[i] = (tempReal2+tempReal);
               outRealLowerBand[i] = (tempReal2-tempReal);
            }
         } else {
            for( i = 0; (i<((int)outNBElement.value)); i += 1 ) {
               tempReal = (tempBuffer2[i]*optInNbDevUp);
               tempReal2 = outRealMiddleBand[i];
               outRealUpperBand[i] = (tempReal2+tempReal);
               outRealLowerBand[i] = (tempReal2-tempReal);
            }
         }
      } else if( (optInNbDevUp==1.0) ) {
         for( i = 0; (i<((int)outNBElement.value)); i += 1 ) {
            tempReal = tempBuffer2[i];
            tempReal2 = outRealMiddleBand[i];
            outRealUpperBand[i] = (tempReal2+tempReal);
            outRealLowerBand[i] = (tempReal2-(tempReal*optInNbDevDn));
         }
      } else if( (optInNbDevDn==1.0) ) {
         for( i = 0; (i<((int)outNBElement.value)); i += 1 ) {
            tempReal = tempBuffer2[i];
            tempReal2 = outRealMiddleBand[i];
            outRealLowerBand[i] = (tempReal2-tempReal);
            outRealUpperBand[i] = (tempReal2+(tempReal*optInNbDevUp));
         }
      } else {
         for( i = 0; (i<((int)outNBElement.value)); i += 1 ) {
            tempReal = tempBuffer2[i];
            tempReal2 = outRealMiddleBand[i];
            outRealUpperBand[i] = (tempReal2+(tempReal*optInNbDevUp));
            outRealLowerBand[i] = (tempReal2-(tempReal*optInNbDevDn));
         }
      }
      return RetCode.Success ;
   }
