/* Generated */
   public int sarLookback( double optInAcceleration, double optInMaximum )
   {
      return 1 ;

   }
   public RetCode sar( int startIdx,
                       int endIdx,
                       double inHigh[],
                       double inLow[],
                       double optInAcceleration,
                       double optInMaximum,
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
      double af = 0;
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
      af = optInAcceleration;
      if( (af>optInMaximum) ) {
         optInAcceleration = optInMaximum;
         af = optInAcceleration;
      }
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
      outBegIdx.value = startIdx;
      outIdx = 0;
      todayIdx = startIdx;
      newHigh = inHigh[(todayIdx-1)];
      newLow = inLow[(todayIdx-1)];
      if( (isLong==1) ) {
         ep = inHigh[todayIdx];
         sar = newLow;
      } else {
         ep = inLow[todayIdx];
         sar = newHigh;
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
               outReal[outIdx++] = sar;
               af = optInAcceleration;
               ep = newLow;
               sar = (sar+(af*(ep-sar)));
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
                  af += optInAcceleration;
                  if( (af>optInMaximum) ) {
                     af = optInMaximum;
                  }
               }
               sar = (sar+(af*(ep-sar)));
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
            outReal[outIdx++] = sar;
            af = optInAcceleration;
            ep = newHigh;
            sar = (sar+(af*(ep-sar)));
            if( (sar>prevLow) ) {
               sar = prevLow;
            }
            if( (sar>newLow) ) {
               sar = newLow;
            }
         } else {
            outReal[outIdx++] = sar;
            if( (newLow<ep) ) {
               ep = newLow;
               af += optInAcceleration;
               if( (af>optInMaximum) ) {
                  af = optInMaximum;
               }
            }
            sar = (sar+(af*(ep-sar)));
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
   public RetCode sarLogic( int startIdx,
                            int endIdx,
                            double inHigh[],
                            double inLow[],
                            double optInAcceleration,
                            double optInMaximum,
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
      double af = 0;
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
      af = optInAcceleration;
      if( (af>optInMaximum) ) {
         optInAcceleration = optInMaximum;
         af = optInAcceleration;
      }
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
      outBegIdx.value = startIdx;
      outIdx = 0;
      todayIdx = startIdx;
      newHigh = inHigh[(todayIdx-1)];
      newLow = inLow[(todayIdx-1)];
      if( (isLong==1) ) {
         ep = inHigh[todayIdx];
         sar = newLow;
      } else {
         ep = inLow[todayIdx];
         sar = newHigh;
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
               outReal[outIdx++] = sar;
               af = optInAcceleration;
               ep = newLow;
               sar = (sar+(af*(ep-sar)));
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
                  af += optInAcceleration;
                  if( (af>optInMaximum) ) {
                     af = optInMaximum;
                  }
               }
               sar = (sar+(af*(ep-sar)));
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
            outReal[outIdx++] = sar;
            af = optInAcceleration;
            ep = newHigh;
            sar = (sar+(af*(ep-sar)));
            if( (sar>prevLow) ) {
               sar = prevLow;
            }
            if( (sar>newLow) ) {
               sar = newLow;
            }
         } else {
            outReal[outIdx++] = sar;
            if( (newLow<ep) ) {
               ep = newLow;
               af += optInAcceleration;
               if( (af>optInMaximum) ) {
                  af = optInMaximum;
               }
            }
            sar = (sar+(af*(ep-sar)));
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
   public RetCode sar( int startIdx,
                       int endIdx,
                       float inHigh[],
                       float inLow[],
                       double optInAcceleration,
                       double optInMaximum,
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
      double af = 0;
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
      af = optInAcceleration;
      if( (af>optInMaximum) ) {
         optInAcceleration = optInMaximum;
         af = optInAcceleration;
      }
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
      outBegIdx.value = startIdx;
      outIdx = 0;
      todayIdx = startIdx;
      newHigh = inHigh[(todayIdx-1)];
      newLow = inLow[(todayIdx-1)];
      if( (isLong==1) ) {
         ep = inHigh[todayIdx];
         sar = newLow;
      } else {
         ep = inLow[todayIdx];
         sar = newHigh;
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
               outReal[outIdx++] = sar;
               af = optInAcceleration;
               ep = newLow;
               sar = (sar+(af*(ep-sar)));
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
                  af += optInAcceleration;
                  if( (af>optInMaximum) ) {
                     af = optInMaximum;
                  }
               }
               sar = (sar+(af*(ep-sar)));
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
            outReal[outIdx++] = sar;
            af = optInAcceleration;
            ep = newHigh;
            sar = (sar+(af*(ep-sar)));
            if( (sar>prevLow) ) {
               sar = prevLow;
            }
            if( (sar>newLow) ) {
               sar = newLow;
            }
         } else {
            outReal[outIdx++] = sar;
            if( (newLow<ep) ) {
               ep = newLow;
               af += optInAcceleration;
               if( (af>optInMaximum) ) {
                  af = optInMaximum;
               }
            }
            sar = (sar+(af*(ep-sar)));
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
   public RetCode sarLogic( int startIdx,
                            int endIdx,
                            float inHigh[],
                            float inLow[],
                            double optInAcceleration,
                            double optInMaximum,
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
      double af = 0;
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
      af = optInAcceleration;
      if( (af>optInMaximum) ) {
         optInAcceleration = optInMaximum;
         af = optInAcceleration;
      }
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
      outBegIdx.value = startIdx;
      outIdx = 0;
      todayIdx = startIdx;
      newHigh = inHigh[(todayIdx-1)];
      newLow = inLow[(todayIdx-1)];
      if( (isLong==1) ) {
         ep = inHigh[todayIdx];
         sar = newLow;
      } else {
         ep = inLow[todayIdx];
         sar = newHigh;
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
               outReal[outIdx++] = sar;
               af = optInAcceleration;
               ep = newLow;
               sar = (sar+(af*(ep-sar)));
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
                  af += optInAcceleration;
                  if( (af>optInMaximum) ) {
                     af = optInMaximum;
                  }
               }
               sar = (sar+(af*(ep-sar)));
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
            outReal[outIdx++] = sar;
            af = optInAcceleration;
            ep = newHigh;
            sar = (sar+(af*(ep-sar)));
            if( (sar>prevLow) ) {
               sar = prevLow;
            }
            if( (sar>newLow) ) {
               sar = newLow;
            }
         } else {
            outReal[outIdx++] = sar;
            if( (newLow<ep) ) {
               ep = newLow;
               af += optInAcceleration;
               if( (af>optInMaximum) ) {
                  af = optInMaximum;
               }
            }
            sar = (sar+(af*(ep-sar)));
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
