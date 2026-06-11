/* Generated */
   public int sarextLookback( double optInStartValue, double optInOffsetOnReverse, double optInAccelerationInitLong, double optInAccelerationLong, double optInAccelerationMaxLong, double optInAccelerationInitShort, double optInAccelerationShort, double optInAccelerationMaxShort )
   {
      return 1 ;

   }
   public RetCode sarext( int startIdx,
                          int endIdx,
                          double inHigh[],
                          double inLow[],
                          double optInStartValue,
                          double optInOffsetOnReverse,
                          double optInAccelerationInitLong,
                          double optInAccelerationLong,
                          double optInAccelerationMaxLong,
                          double optInAccelerationInitShort,
                          double optInAccelerationShort,
                          double optInAccelerationMaxShort,
                          MInteger outBegIdx,
                          MInteger outNBElement,
                          double outReal[] )
   {
      RetCode retCode;
      int isLong = 0;
      int todayIdx = 0;
      int outIdx = 0;
      MInteger tempInt = new MInteger();
      double newHigh = 0;
      double newLow = 0;
      double prevHigh = 0;
      double prevLow = 0;
      double afLong = 0;
      double afShort = 0;
      double ep = 0;
      double sar = 0;
      double[] ep_temp = new double[1];
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      if( (startIdx<1) ) {
         startIdx = 1;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      afLong = optInAccelerationInitLong;
      afShort = optInAccelerationInitShort;
      if( (afLong>optInAccelerationMaxLong) ) {
         optInAccelerationInitLong = optInAccelerationMaxLong;
         afLong = optInAccelerationInitLong;
      }
      if( (optInAccelerationLong>optInAccelerationMaxLong) ) {
         optInAccelerationLong = optInAccelerationMaxLong;
      }
      if( (afShort>optInAccelerationMaxShort) ) {
         optInAccelerationInitShort = optInAccelerationMaxShort;
         afShort = optInAccelerationInitShort;
      }
      if( (optInAccelerationShort>optInAccelerationMaxShort) ) {
         optInAccelerationShort = optInAccelerationMaxShort;
      }
      if( (optInStartValue==0) ) {
         retCode = minusDmLogic(startIdx, startIdx, inHigh, inLow, 1, tempInt, tempInt, ep_temp);
         if( (ep_temp[0]>0) ) {
            isLong = 0;
         } else {
            isLong = 1;
         }
         if( (retCode!=RetCode.Success) ) {
            outBegIdx.value = 0;
            outNBElement.value = 0;
            return retCode ;
         }
      } else if( (optInStartValue>0) ) {
         isLong = 1;
      } else {
         isLong = 0;
      }
      outBegIdx.value = startIdx;
      outIdx = 0;
      todayIdx = startIdx;
      newHigh = inHigh[(todayIdx-1)];
      newLow = inLow[(todayIdx-1)];
      if( (optInStartValue==0) ) {
         if( (isLong==1) ) {
            ep = inHigh[todayIdx];
            sar = newLow;
         } else {
            ep = inLow[todayIdx];
            sar = newHigh;
         }
      } else if( (optInStartValue>0) ) {
         ep = inHigh[todayIdx];
         sar = optInStartValue;
      } else {
         ep = inLow[todayIdx];
         sar = Math.abs(optInStartValue);
      }
      newLow = inLow[todayIdx];
      newHigh = inHigh[todayIdx];
      while( (todayIdx<=endIdx) ) {
         prevLow = newLow;
         prevHigh = newHigh;
         newLow = inLow[todayIdx];
         newHigh = inHigh[todayIdx];
         todayIdx += 1;
         if( (isLong==1) ) {
            if( (newLow<=sar) ) {
               isLong = 0;
               sar = ep;
               if( (sar<prevHigh) ) {
                  sar = prevHigh;
               }
               if( (sar<newHigh) ) {
                  sar = newHigh;
               }
               if( (optInOffsetOnReverse!=0.0) ) {
                  sar += (sar*optInOffsetOnReverse);
               }
               outReal[outIdx++] = (0-sar);
               afShort = optInAccelerationInitShort;
               ep = newLow;
               sar = (sar+(afShort*(ep-sar)));
               if( (sar<prevHigh) ) {
                  sar = prevHigh;
               }
               if( (sar<newHigh) ) {
                  sar = newHigh;
               }
            } else {
               outReal[outIdx++] = sar;
               if( (newHigh>ep) ) {
                  ep = newHigh;
                  afLong += optInAccelerationLong;
                  if( (afLong>optInAccelerationMaxLong) ) {
                     afLong = optInAccelerationMaxLong;
                  }
               }
               sar = (sar+(afLong*(ep-sar)));
               if( (sar>prevLow) ) {
                  sar = prevLow;
               }
               if( (sar>newLow) ) {
                  sar = newLow;
               }
            }
         } else if( (newHigh>=sar) ) {
            isLong = 1;
            sar = ep;
            if( (sar>prevLow) ) {
               sar = prevLow;
            }
            if( (sar>newLow) ) {
               sar = newLow;
            }
            if( (optInOffsetOnReverse!=0.0) ) {
               sar -= (sar*optInOffsetOnReverse);
            }
            outReal[outIdx++] = sar;
            afLong = optInAccelerationInitLong;
            ep = newHigh;
            sar = (sar+(afLong*(ep-sar)));
            if( (sar>prevLow) ) {
               sar = prevLow;
            }
            if( (sar>newLow) ) {
               sar = newLow;
            }
         } else {
            outReal[outIdx++] = (0-sar);
            if( (newLow<ep) ) {
               ep = newLow;
               afShort += optInAccelerationShort;
               if( (afShort>optInAccelerationMaxShort) ) {
                  afShort = optInAccelerationMaxShort;
               }
            }
            sar = (sar+(afShort*(ep-sar)));
            if( (sar<prevHigh) ) {
               sar = prevHigh;
            }
            if( (sar<newHigh) ) {
               sar = newHigh;
            }
         }
      }
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode sarextLogic( int startIdx,
                               int endIdx,
                               double inHigh[],
                               double inLow[],
                               double optInStartValue,
                               double optInOffsetOnReverse,
                               double optInAccelerationInitLong,
                               double optInAccelerationLong,
                               double optInAccelerationMaxLong,
                               double optInAccelerationInitShort,
                               double optInAccelerationShort,
                               double optInAccelerationMaxShort,
                               MInteger outBegIdx,
                               MInteger outNBElement,
                               double outReal[] )
   {
      RetCode retCode;
      int isLong = 0;
      int todayIdx = 0;
      int outIdx = 0;
      MInteger tempInt = new MInteger();
      double newHigh = 0;
      double newLow = 0;
      double prevHigh = 0;
      double prevLow = 0;
      double afLong = 0;
      double afShort = 0;
      double ep = 0;
      double sar = 0;
      double[] ep_temp = new double[1];
      if( (startIdx<1) ) {
         startIdx = 1;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      afLong = optInAccelerationInitLong;
      afShort = optInAccelerationInitShort;
      if( (afLong>optInAccelerationMaxLong) ) {
         optInAccelerationInitLong = optInAccelerationMaxLong;
         afLong = optInAccelerationInitLong;
      }
      if( (optInAccelerationLong>optInAccelerationMaxLong) ) {
         optInAccelerationLong = optInAccelerationMaxLong;
      }
      if( (afShort>optInAccelerationMaxShort) ) {
         optInAccelerationInitShort = optInAccelerationMaxShort;
         afShort = optInAccelerationInitShort;
      }
      if( (optInAccelerationShort>optInAccelerationMaxShort) ) {
         optInAccelerationShort = optInAccelerationMaxShort;
      }
      if( (optInStartValue==0) ) {
         retCode = minusDmLogic(startIdx, startIdx, inHigh, inLow, 1, tempInt, tempInt, ep_temp);
         if( (ep_temp[0]>0) ) {
            isLong = 0;
         } else {
            isLong = 1;
         }
         if( (retCode!=RetCode.Success) ) {
            outBegIdx.value = 0;
            outNBElement.value = 0;
            return retCode ;
         }
      } else if( (optInStartValue>0) ) {
         isLong = 1;
      } else {
         isLong = 0;
      }
      outBegIdx.value = startIdx;
      outIdx = 0;
      todayIdx = startIdx;
      newHigh = inHigh[(todayIdx-1)];
      newLow = inLow[(todayIdx-1)];
      if( (optInStartValue==0) ) {
         if( (isLong==1) ) {
            ep = inHigh[todayIdx];
            sar = newLow;
         } else {
            ep = inLow[todayIdx];
            sar = newHigh;
         }
      } else if( (optInStartValue>0) ) {
         ep = inHigh[todayIdx];
         sar = optInStartValue;
      } else {
         ep = inLow[todayIdx];
         sar = Math.abs(optInStartValue);
      }
      newLow = inLow[todayIdx];
      newHigh = inHigh[todayIdx];
      while( (todayIdx<=endIdx) ) {
         prevLow = newLow;
         prevHigh = newHigh;
         newLow = inLow[todayIdx];
         newHigh = inHigh[todayIdx];
         todayIdx += 1;
         if( (isLong==1) ) {
            if( (newLow<=sar) ) {
               isLong = 0;
               sar = ep;
               if( (sar<prevHigh) ) {
                  sar = prevHigh;
               }
               if( (sar<newHigh) ) {
                  sar = newHigh;
               }
               if( (optInOffsetOnReverse!=0.0) ) {
                  sar += (sar*optInOffsetOnReverse);
               }
               outReal[outIdx++] = (0-sar);
               afShort = optInAccelerationInitShort;
               ep = newLow;
               sar = (sar+(afShort*(ep-sar)));
               if( (sar<prevHigh) ) {
                  sar = prevHigh;
               }
               if( (sar<newHigh) ) {
                  sar = newHigh;
               }
            } else {
               outReal[outIdx++] = sar;
               if( (newHigh>ep) ) {
                  ep = newHigh;
                  afLong += optInAccelerationLong;
                  if( (afLong>optInAccelerationMaxLong) ) {
                     afLong = optInAccelerationMaxLong;
                  }
               }
               sar = (sar+(afLong*(ep-sar)));
               if( (sar>prevLow) ) {
                  sar = prevLow;
               }
               if( (sar>newLow) ) {
                  sar = newLow;
               }
            }
         } else if( (newHigh>=sar) ) {
            isLong = 1;
            sar = ep;
            if( (sar>prevLow) ) {
               sar = prevLow;
            }
            if( (sar>newLow) ) {
               sar = newLow;
            }
            if( (optInOffsetOnReverse!=0.0) ) {
               sar -= (sar*optInOffsetOnReverse);
            }
            outReal[outIdx++] = sar;
            afLong = optInAccelerationInitLong;
            ep = newHigh;
            sar = (sar+(afLong*(ep-sar)));
            if( (sar>prevLow) ) {
               sar = prevLow;
            }
            if( (sar>newLow) ) {
               sar = newLow;
            }
         } else {
            outReal[outIdx++] = (0-sar);
            if( (newLow<ep) ) {
               ep = newLow;
               afShort += optInAccelerationShort;
               if( (afShort>optInAccelerationMaxShort) ) {
                  afShort = optInAccelerationMaxShort;
               }
            }
            sar = (sar+(afShort*(ep-sar)));
            if( (sar<prevHigh) ) {
               sar = prevHigh;
            }
            if( (sar<newHigh) ) {
               sar = newHigh;
            }
         }
      }
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode sarext( int startIdx,
                          int endIdx,
                          float inHigh[],
                          float inLow[],
                          double optInStartValue,
                          double optInOffsetOnReverse,
                          double optInAccelerationInitLong,
                          double optInAccelerationLong,
                          double optInAccelerationMaxLong,
                          double optInAccelerationInitShort,
                          double optInAccelerationShort,
                          double optInAccelerationMaxShort,
                          MInteger outBegIdx,
                          MInteger outNBElement,
                          double outReal[] )
   {
      RetCode retCode;
      int isLong = 0;
      int todayIdx = 0;
      int outIdx = 0;
      MInteger tempInt = new MInteger();
      double newHigh = 0;
      double newLow = 0;
      double prevHigh = 0;
      double prevLow = 0;
      double afLong = 0;
      double afShort = 0;
      double ep = 0;
      double sar = 0;
      double[] ep_temp = new double[1];
      if( startIdx < 0 ) {
         return RetCode.OutOfRangeStartIndex ;
      }
      if( (endIdx < 0) || (endIdx < startIdx)) {
         return RetCode.OutOfRangeEndIndex ;
      }
      if( (startIdx<1) ) {
         startIdx = 1;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      afLong = optInAccelerationInitLong;
      afShort = optInAccelerationInitShort;
      if( (afLong>optInAccelerationMaxLong) ) {
         optInAccelerationInitLong = optInAccelerationMaxLong;
         afLong = optInAccelerationInitLong;
      }
      if( (optInAccelerationLong>optInAccelerationMaxLong) ) {
         optInAccelerationLong = optInAccelerationMaxLong;
      }
      if( (afShort>optInAccelerationMaxShort) ) {
         optInAccelerationInitShort = optInAccelerationMaxShort;
         afShort = optInAccelerationInitShort;
      }
      if( (optInAccelerationShort>optInAccelerationMaxShort) ) {
         optInAccelerationShort = optInAccelerationMaxShort;
      }
      if( (optInStartValue==0) ) {
         retCode = minusDmLogic(startIdx, startIdx, inHigh, inLow, 1, tempInt, tempInt, ep_temp);
         if( (ep_temp[0]>0) ) {
            isLong = 0;
         } else {
            isLong = 1;
         }
         if( (retCode!=RetCode.Success) ) {
            outBegIdx.value = 0;
            outNBElement.value = 0;
            return retCode ;
         }
      } else if( (optInStartValue>0) ) {
         isLong = 1;
      } else {
         isLong = 0;
      }
      outBegIdx.value = startIdx;
      outIdx = 0;
      todayIdx = startIdx;
      newHigh = inHigh[(todayIdx-1)];
      newLow = inLow[(todayIdx-1)];
      if( (optInStartValue==0) ) {
         if( (isLong==1) ) {
            ep = inHigh[todayIdx];
            sar = newLow;
         } else {
            ep = inLow[todayIdx];
            sar = newHigh;
         }
      } else if( (optInStartValue>0) ) {
         ep = inHigh[todayIdx];
         sar = optInStartValue;
      } else {
         ep = inLow[todayIdx];
         sar = Math.abs(optInStartValue);
      }
      newLow = inLow[todayIdx];
      newHigh = inHigh[todayIdx];
      while( (todayIdx<=endIdx) ) {
         prevLow = newLow;
         prevHigh = newHigh;
         newLow = inLow[todayIdx];
         newHigh = inHigh[todayIdx];
         todayIdx += 1;
         if( (isLong==1) ) {
            if( (newLow<=sar) ) {
               isLong = 0;
               sar = ep;
               if( (sar<prevHigh) ) {
                  sar = prevHigh;
               }
               if( (sar<newHigh) ) {
                  sar = newHigh;
               }
               if( (optInOffsetOnReverse!=0.0) ) {
                  sar += (sar*optInOffsetOnReverse);
               }
               outReal[outIdx++] = (0-sar);
               afShort = optInAccelerationInitShort;
               ep = newLow;
               sar = (sar+(afShort*(ep-sar)));
               if( (sar<prevHigh) ) {
                  sar = prevHigh;
               }
               if( (sar<newHigh) ) {
                  sar = newHigh;
               }
            } else {
               outReal[outIdx++] = sar;
               if( (newHigh>ep) ) {
                  ep = newHigh;
                  afLong += optInAccelerationLong;
                  if( (afLong>optInAccelerationMaxLong) ) {
                     afLong = optInAccelerationMaxLong;
                  }
               }
               sar = (sar+(afLong*(ep-sar)));
               if( (sar>prevLow) ) {
                  sar = prevLow;
               }
               if( (sar>newLow) ) {
                  sar = newLow;
               }
            }
         } else if( (newHigh>=sar) ) {
            isLong = 1;
            sar = ep;
            if( (sar>prevLow) ) {
               sar = prevLow;
            }
            if( (sar>newLow) ) {
               sar = newLow;
            }
            if( (optInOffsetOnReverse!=0.0) ) {
               sar -= (sar*optInOffsetOnReverse);
            }
            outReal[outIdx++] = sar;
            afLong = optInAccelerationInitLong;
            ep = newHigh;
            sar = (sar+(afLong*(ep-sar)));
            if( (sar>prevLow) ) {
               sar = prevLow;
            }
            if( (sar>newLow) ) {
               sar = newLow;
            }
         } else {
            outReal[outIdx++] = (0-sar);
            if( (newLow<ep) ) {
               ep = newLow;
               afShort += optInAccelerationShort;
               if( (afShort>optInAccelerationMaxShort) ) {
                  afShort = optInAccelerationMaxShort;
               }
            }
            sar = (sar+(afShort*(ep-sar)));
            if( (sar<prevHigh) ) {
               sar = prevHigh;
            }
            if( (sar<newHigh) ) {
               sar = newHigh;
            }
         }
      }
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
   public RetCode sarextLogic( int startIdx,
                               int endIdx,
                               float inHigh[],
                               float inLow[],
                               double optInStartValue,
                               double optInOffsetOnReverse,
                               double optInAccelerationInitLong,
                               double optInAccelerationLong,
                               double optInAccelerationMaxLong,
                               double optInAccelerationInitShort,
                               double optInAccelerationShort,
                               double optInAccelerationMaxShort,
                               MInteger outBegIdx,
                               MInteger outNBElement,
                               double outReal[] )
   {
      RetCode retCode;
      int isLong = 0;
      int todayIdx = 0;
      int outIdx = 0;
      MInteger tempInt = new MInteger();
      double newHigh = 0;
      double newLow = 0;
      double prevHigh = 0;
      double prevLow = 0;
      double afLong = 0;
      double afShort = 0;
      double ep = 0;
      double sar = 0;
      double[] ep_temp = new double[1];
      if( (startIdx<1) ) {
         startIdx = 1;
      }
      if( (startIdx>endIdx) ) {
         outBegIdx.value = 0;
         outNBElement.value = 0;
         return RetCode.Success ;
      }
      afLong = optInAccelerationInitLong;
      afShort = optInAccelerationInitShort;
      if( (afLong>optInAccelerationMaxLong) ) {
         optInAccelerationInitLong = optInAccelerationMaxLong;
         afLong = optInAccelerationInitLong;
      }
      if( (optInAccelerationLong>optInAccelerationMaxLong) ) {
         optInAccelerationLong = optInAccelerationMaxLong;
      }
      if( (afShort>optInAccelerationMaxShort) ) {
         optInAccelerationInitShort = optInAccelerationMaxShort;
         afShort = optInAccelerationInitShort;
      }
      if( (optInAccelerationShort>optInAccelerationMaxShort) ) {
         optInAccelerationShort = optInAccelerationMaxShort;
      }
      if( (optInStartValue==0) ) {
         retCode = minusDmLogic(startIdx, startIdx, inHigh, inLow, 1, tempInt, tempInt, ep_temp);
         if( (ep_temp[0]>0) ) {
            isLong = 0;
         } else {
            isLong = 1;
         }
         if( (retCode!=RetCode.Success) ) {
            outBegIdx.value = 0;
            outNBElement.value = 0;
            return retCode ;
         }
      } else if( (optInStartValue>0) ) {
         isLong = 1;
      } else {
         isLong = 0;
      }
      outBegIdx.value = startIdx;
      outIdx = 0;
      todayIdx = startIdx;
      newHigh = inHigh[(todayIdx-1)];
      newLow = inLow[(todayIdx-1)];
      if( (optInStartValue==0) ) {
         if( (isLong==1) ) {
            ep = inHigh[todayIdx];
            sar = newLow;
         } else {
            ep = inLow[todayIdx];
            sar = newHigh;
         }
      } else if( (optInStartValue>0) ) {
         ep = inHigh[todayIdx];
         sar = optInStartValue;
      } else {
         ep = inLow[todayIdx];
         sar = Math.abs(optInStartValue);
      }
      newLow = inLow[todayIdx];
      newHigh = inHigh[todayIdx];
      while( (todayIdx<=endIdx) ) {
         prevLow = newLow;
         prevHigh = newHigh;
         newLow = inLow[todayIdx];
         newHigh = inHigh[todayIdx];
         todayIdx += 1;
         if( (isLong==1) ) {
            if( (newLow<=sar) ) {
               isLong = 0;
               sar = ep;
               if( (sar<prevHigh) ) {
                  sar = prevHigh;
               }
               if( (sar<newHigh) ) {
                  sar = newHigh;
               }
               if( (optInOffsetOnReverse!=0.0) ) {
                  sar += (sar*optInOffsetOnReverse);
               }
               outReal[outIdx++] = (0-sar);
               afShort = optInAccelerationInitShort;
               ep = newLow;
               sar = (sar+(afShort*(ep-sar)));
               if( (sar<prevHigh) ) {
                  sar = prevHigh;
               }
               if( (sar<newHigh) ) {
                  sar = newHigh;
               }
            } else {
               outReal[outIdx++] = sar;
               if( (newHigh>ep) ) {
                  ep = newHigh;
                  afLong += optInAccelerationLong;
                  if( (afLong>optInAccelerationMaxLong) ) {
                     afLong = optInAccelerationMaxLong;
                  }
               }
               sar = (sar+(afLong*(ep-sar)));
               if( (sar>prevLow) ) {
                  sar = prevLow;
               }
               if( (sar>newLow) ) {
                  sar = newLow;
               }
            }
         } else if( (newHigh>=sar) ) {
            isLong = 1;
            sar = ep;
            if( (sar>prevLow) ) {
               sar = prevLow;
            }
            if( (sar>newLow) ) {
               sar = newLow;
            }
            if( (optInOffsetOnReverse!=0.0) ) {
               sar -= (sar*optInOffsetOnReverse);
            }
            outReal[outIdx++] = sar;
            afLong = optInAccelerationInitLong;
            ep = newHigh;
            sar = (sar+(afLong*(ep-sar)));
            if( (sar>prevLow) ) {
               sar = prevLow;
            }
            if( (sar>newLow) ) {
               sar = newLow;
            }
         } else {
            outReal[outIdx++] = (0-sar);
            if( (newLow<ep) ) {
               ep = newLow;
               afShort += optInAccelerationShort;
               if( (afShort>optInAccelerationMaxShort) ) {
                  afShort = optInAccelerationMaxShort;
               }
            }
            sar = (sar+(afShort*(ep-sar)));
            if( (sar<prevHigh) ) {
               sar = prevHigh;
            }
            if( (sar<newHigh) ) {
               sar = newHigh;
            }
         }
      }
      outNBElement.value = outIdx;
      return RetCode.Success ;
   }
