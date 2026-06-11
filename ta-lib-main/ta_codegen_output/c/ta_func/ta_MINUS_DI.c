/* TA-LIB Copyright (c) 1999-2025, Mario Fortier
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or
* without modification, are permitted provided that the following
* conditions are met:
*
* - Redistributions of source code must retain the above copyright
*   notice, this list of conditions and the following disclaimer.
*
* - Redistributions in binary form must reproduce the above copyright
*   notice, this list of conditions and the following disclaimer in
*   the documentation and/or other materials provided with the
*   distribution.
*
* - Neither name of author nor the names of its contributors
*   may be used to endorse or promote products derived from this
*   software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
* ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
* LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
* FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
* REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
* INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
* (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
* OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
* INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
* WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
* OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
* EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

#include <string.h>
#include <math.h>
#include "ta_func.h"
#include "ta_utility.h"
#include "ta_memory.h"

TA_LIB_API int TA_MINUS_DI_Lookback( int optInTimePeriod )
{
   if( (optInTimePeriod>1) )
   {
      return (optInTimePeriod+TA_GLOBALS_UNSTABLE_PERIOD(TA_FUNC_UNST_MINUS_DI,Minus_di));
   } else 
   {
      return 1;
   }
}

TA_LIB_API TA_RetCode TA_MINUS_DI( int    startIdx,
                                   int    endIdx,
                                   const double inHigh[],
                                   const double inLow[],
                                   const double inClose[],
                                   int optInTimePeriod,
                                   int          *outBegIdx,
                                   int          *outNBElement,
                                   double        outReal[] )
{
   int today;
   int lookbackTotal;
   int outIdx;
   double prevHigh;
   double prevLow;
   double prevClose;
   double prevMinusDM;
   double prevTR;
   double tempReal;
   double tempReal2;
   double diffP;
   double diffM;
   int i;

   if( startIdx < 0 )
      return TA_OUT_OF_RANGE_START_INDEX;
   if( (endIdx < 0) || (endIdx < startIdx) )
      return TA_OUT_OF_RANGE_END_INDEX;

   if( !inHigh )
      return TA_BAD_PARAM;
   if( !inLow )
      return TA_BAD_PARAM;
   if( !inClose )
      return TA_BAD_PARAM;
   if( (int)optInTimePeriod == (int)0x80000000 )
      optInTimePeriod = 14;
   else if( (int)optInTimePeriod < 1 || (int)optInTimePeriod > 100000 )
      return TA_BAD_PARAM;
   if( !outReal )
      return TA_BAD_PARAM;

   if( (optInTimePeriod>1) )
   {
      lookbackTotal = (optInTimePeriod+TA_GLOBALS_UNSTABLE_PERIOD(TA_FUNC_UNST_MINUS_DI,Minus_di));
   } else 
   {
      lookbackTotal = 1;
   }
   if( (startIdx<lookbackTotal) )
   {
      startIdx = lookbackTotal;
   }
   if( (startIdx>endIdx) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      return TA_SUCCESS;
   }
   outIdx = 0;
   if( (optInTimePeriod<=1) )
   {
      *outBegIdx= startIdx;
      today = (startIdx-1);
      prevHigh = inHigh[today];
      prevLow = inLow[today];
      prevClose = inClose[today];
      while( (today<endIdx) )
      {
         today += 1;
         tempReal = inHigh[today];
         diffP = (tempReal-prevHigh);
         prevHigh = tempReal;
         tempReal = inLow[today];
         diffM = (prevLow-tempReal);
         prevLow = tempReal;
         if( ((diffM>0)&&(diffP<diffM)) )
         {
            double _true_range_0;
            double range_0 = (prevHigh-prevLow);
            double tmp_0 = fabs((prevHigh-prevClose));
            if( (tmp_0>range_0) )
            {
               range_0 = tmp_0;
            }
            tmp_0 = fabs((prevLow-prevClose));
            if( (tmp_0>range_0) )
            {
               range_0 = tmp_0;
            }
            _true_range_0 = range_0;
            tempReal = _true_range_0;
            if( TA_IS_ZERO(tempReal) )
            {
               outReal[outIdx++] = ((double)0.0);
            } else 
            {
               outReal[outIdx++] = (diffM/tempReal);
            }
         } else 
         {
            outReal[outIdx++] = ((double)0.0);
         }
         prevClose = inClose[today];
      }
      *outNBElement= outIdx;
      return TA_SUCCESS;
   }
   today = startIdx;
   *outBegIdx= today;
   prevMinusDM = 0.0;
   prevTR = 0.0;
   today = (startIdx-lookbackTotal);
   prevHigh = inHigh[today];
   prevLow = inLow[today];
   prevClose = inClose[today];
   i = (optInTimePeriod-1);
   while( (i-->0) )
   {
      today += 1;
      tempReal = inHigh[today];
      diffP = (tempReal-prevHigh);
      prevHigh = tempReal;
      tempReal = inLow[today];
      diffM = (prevLow-tempReal);
      prevLow = tempReal;
      if( ((diffM>0)&&(diffP<diffM)) )
      {
         prevMinusDM += diffM;
      }
      double _true_range_1;
      double range_1 = (prevHigh-prevLow);
      double tmp_1 = fabs((prevHigh-prevClose));
      if( (tmp_1>range_1) )
      {
         range_1 = tmp_1;
      }
      tmp_1 = fabs((prevLow-prevClose));
      if( (tmp_1>range_1) )
      {
         range_1 = tmp_1;
      }
      _true_range_1 = range_1;
      tempReal = _true_range_1;
      prevTR += tempReal;
      prevClose = inClose[today];
   }
   i = (TA_GLOBALS_UNSTABLE_PERIOD(TA_FUNC_UNST_MINUS_DI,Minus_di)+1);
   while( (i--!=0) )
   {
      today += 1;
      tempReal = inHigh[today];
      diffP = (tempReal-prevHigh);
      prevHigh = tempReal;
      tempReal = inLow[today];
      diffM = (prevLow-tempReal);
      prevLow = tempReal;
      if( ((diffM>0)&&(diffP<diffM)) )
      {
         prevMinusDM = ((prevMinusDM-(prevMinusDM/optInTimePeriod))+diffM);
      } else 
      {
         prevMinusDM = (prevMinusDM-(prevMinusDM/optInTimePeriod));
      }
      double _true_range_2;
      double range_2 = (prevHigh-prevLow);
      double tmp_2 = fabs((prevHigh-prevClose));
      if( (tmp_2>range_2) )
      {
         range_2 = tmp_2;
      }
      tmp_2 = fabs((prevLow-prevClose));
      if( (tmp_2>range_2) )
      {
         range_2 = tmp_2;
      }
      _true_range_2 = range_2;
      tempReal = _true_range_2;
      prevTR = ((prevTR-(prevTR/optInTimePeriod))+tempReal);
      prevClose = inClose[today];
   }
   if( !(TA_IS_ZERO(prevTR)) )
   {
      outReal[0] = (100.0*(prevMinusDM/prevTR));
   } else 
   {
      outReal[0] = 0.0;
   }
   outIdx = 1;
   while( (today<endIdx) )
   {
      today += 1;
      tempReal = inHigh[today];
      diffP = (tempReal-prevHigh);
      prevHigh = tempReal;
      tempReal = inLow[today];
      diffM = (prevLow-tempReal);
      prevLow = tempReal;
      if( ((diffM>0)&&(diffP<diffM)) )
      {
         prevMinusDM = ((prevMinusDM-(prevMinusDM/optInTimePeriod))+diffM);
      } else 
      {
         prevMinusDM = (prevMinusDM-(prevMinusDM/optInTimePeriod));
      }
      double _true_range_3;
      double range_3 = (prevHigh-prevLow);
      double tmp_3 = fabs((prevHigh-prevClose));
      if( (tmp_3>range_3) )
      {
         range_3 = tmp_3;
      }
      tmp_3 = fabs((prevLow-prevClose));
      if( (tmp_3>range_3) )
      {
         range_3 = tmp_3;
      }
      _true_range_3 = range_3;
      tempReal = _true_range_3;
      prevTR = ((prevTR-(prevTR/optInTimePeriod))+tempReal);
      prevClose = inClose[today];
      if( !(TA_IS_ZERO(prevTR)) )
      {
         outReal[outIdx++] = (100.0*(prevMinusDM/prevTR));
      } else 
      {
         outReal[outIdx++] = 0.0;
      }
   }
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_LIB_API TA_RetCode TA_MINUS_DI_Unguarded( int    startIdx,
                                             int    endIdx,
                                             const double inHigh[],
                                             const double inLow[],
                                             const double inClose[],
                                             int optInTimePeriod,
                                             int          *outBegIdx,
                                             int          *outNBElement,
                                             double        outReal[] )
{
   int today;
   int lookbackTotal;
   int outIdx;
   double prevHigh;
   double prevLow;
   double prevClose;
   double prevMinusDM;
   double prevTR;
   double tempReal;
   double tempReal2;
   double diffP;
   double diffM;
   int i;

   if( (optInTimePeriod>1) )
   {
      lookbackTotal = (optInTimePeriod+TA_GLOBALS_UNSTABLE_PERIOD(TA_FUNC_UNST_MINUS_DI,Minus_di));
   } else 
   {
      lookbackTotal = 1;
   }
   if( (startIdx<lookbackTotal) )
   {
      startIdx = lookbackTotal;
   }
   if( (startIdx>endIdx) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      return TA_SUCCESS;
   }
   outIdx = 0;
   if( (optInTimePeriod<=1) )
   {
      *outBegIdx= startIdx;
      today = (startIdx-1);
      prevHigh = inHigh[today];
      prevLow = inLow[today];
      prevClose = inClose[today];
      while( (today<endIdx) )
      {
         today += 1;
         tempReal = inHigh[today];
         diffP = (tempReal-prevHigh);
         prevHigh = tempReal;
         tempReal = inLow[today];
         diffM = (prevLow-tempReal);
         prevLow = tempReal;
         if( ((diffM>0)&&(diffP<diffM)) )
         {
            double _true_range_0;
            double range_0 = (prevHigh-prevLow);
            double tmp_0 = fabs((prevHigh-prevClose));
            if( (tmp_0>range_0) )
            {
               range_0 = tmp_0;
            }
            tmp_0 = fabs((prevLow-prevClose));
            if( (tmp_0>range_0) )
            {
               range_0 = tmp_0;
            }
            _true_range_0 = range_0;
            tempReal = _true_range_0;
            if( TA_IS_ZERO(tempReal) )
            {
               outReal[outIdx++] = ((double)0.0);
            } else 
            {
               outReal[outIdx++] = (diffM/tempReal);
            }
         } else 
         {
            outReal[outIdx++] = ((double)0.0);
         }
         prevClose = inClose[today];
      }
      *outNBElement= outIdx;
      return TA_SUCCESS;
   }
   today = startIdx;
   *outBegIdx= today;
   prevMinusDM = 0.0;
   prevTR = 0.0;
   today = (startIdx-lookbackTotal);
   prevHigh = inHigh[today];
   prevLow = inLow[today];
   prevClose = inClose[today];
   i = (optInTimePeriod-1);
   while( (i-->0) )
   {
      today += 1;
      tempReal = inHigh[today];
      diffP = (tempReal-prevHigh);
      prevHigh = tempReal;
      tempReal = inLow[today];
      diffM = (prevLow-tempReal);
      prevLow = tempReal;
      if( ((diffM>0)&&(diffP<diffM)) )
      {
         prevMinusDM += diffM;
      }
      double _true_range_1;
      double range_1 = (prevHigh-prevLow);
      double tmp_1 = fabs((prevHigh-prevClose));
      if( (tmp_1>range_1) )
      {
         range_1 = tmp_1;
      }
      tmp_1 = fabs((prevLow-prevClose));
      if( (tmp_1>range_1) )
      {
         range_1 = tmp_1;
      }
      _true_range_1 = range_1;
      tempReal = _true_range_1;
      prevTR += tempReal;
      prevClose = inClose[today];
   }
   i = (TA_GLOBALS_UNSTABLE_PERIOD(TA_FUNC_UNST_MINUS_DI,Minus_di)+1);
   while( (i--!=0) )
   {
      today += 1;
      tempReal = inHigh[today];
      diffP = (tempReal-prevHigh);
      prevHigh = tempReal;
      tempReal = inLow[today];
      diffM = (prevLow-tempReal);
      prevLow = tempReal;
      if( ((diffM>0)&&(diffP<diffM)) )
      {
         prevMinusDM = ((prevMinusDM-(prevMinusDM/optInTimePeriod))+diffM);
      } else 
      {
         prevMinusDM = (prevMinusDM-(prevMinusDM/optInTimePeriod));
      }
      double _true_range_2;
      double range_2 = (prevHigh-prevLow);
      double tmp_2 = fabs((prevHigh-prevClose));
      if( (tmp_2>range_2) )
      {
         range_2 = tmp_2;
      }
      tmp_2 = fabs((prevLow-prevClose));
      if( (tmp_2>range_2) )
      {
         range_2 = tmp_2;
      }
      _true_range_2 = range_2;
      tempReal = _true_range_2;
      prevTR = ((prevTR-(prevTR/optInTimePeriod))+tempReal);
      prevClose = inClose[today];
   }
   if( !(TA_IS_ZERO(prevTR)) )
   {
      outReal[0] = (100.0*(prevMinusDM/prevTR));
   } else 
   {
      outReal[0] = 0.0;
   }
   outIdx = 1;
   while( (today<endIdx) )
   {
      today += 1;
      tempReal = inHigh[today];
      diffP = (tempReal-prevHigh);
      prevHigh = tempReal;
      tempReal = inLow[today];
      diffM = (prevLow-tempReal);
      prevLow = tempReal;
      if( ((diffM>0)&&(diffP<diffM)) )
      {
         prevMinusDM = ((prevMinusDM-(prevMinusDM/optInTimePeriod))+diffM);
      } else 
      {
         prevMinusDM = (prevMinusDM-(prevMinusDM/optInTimePeriod));
      }
      double _true_range_3;
      double range_3 = (prevHigh-prevLow);
      double tmp_3 = fabs((prevHigh-prevClose));
      if( (tmp_3>range_3) )
      {
         range_3 = tmp_3;
      }
      tmp_3 = fabs((prevLow-prevClose));
      if( (tmp_3>range_3) )
      {
         range_3 = tmp_3;
      }
      _true_range_3 = range_3;
      tempReal = _true_range_3;
      prevTR = ((prevTR-(prevTR/optInTimePeriod))+tempReal);
      prevClose = inClose[today];
      if( !(TA_IS_ZERO(prevTR)) )
      {
         outReal[outIdx++] = (100.0*(prevMinusDM/prevTR));
      } else 
      {
         outReal[outIdx++] = 0.0;
      }
   }
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_MINUS_DI( int    startIdx,
                          int    endIdx,
                          const float inHigh[],
                          const float inLow[],
                          const float inClose[],
                          int optInTimePeriod,
                          int          *outBegIdx,
                          int          *outNBElement,
                          double        outReal[] )
{
   int today;
   int lookbackTotal;
   int outIdx;
   double prevHigh;
   double prevLow;
   double prevClose;
   double prevMinusDM;
   double prevTR;
   double tempReal;
   double tempReal2;
   double diffP;
   double diffM;
   int i;

   if( startIdx < 0 )
      return TA_OUT_OF_RANGE_START_INDEX;
   if( (endIdx < 0) || (endIdx < startIdx) )
      return TA_OUT_OF_RANGE_END_INDEX;

   if( !inHigh )
      return TA_BAD_PARAM;
   if( !inLow )
      return TA_BAD_PARAM;
   if( !inClose )
      return TA_BAD_PARAM;
   if( (int)optInTimePeriod == (int)0x80000000 )
      optInTimePeriod = 14;
   else if( (int)optInTimePeriod < 1 || (int)optInTimePeriod > 100000 )
      return TA_BAD_PARAM;
   if( !outReal )
      return TA_BAD_PARAM;

   if( (optInTimePeriod>1) )
   {
      lookbackTotal = (optInTimePeriod+TA_GLOBALS_UNSTABLE_PERIOD(TA_FUNC_UNST_MINUS_DI,Minus_di));
   } else 
   {
      lookbackTotal = 1;
   }
   if( (startIdx<lookbackTotal) )
   {
      startIdx = lookbackTotal;
   }
   if( (startIdx>endIdx) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      return TA_SUCCESS;
   }
   outIdx = 0;
   if( (optInTimePeriod<=1) )
   {
      *outBegIdx= startIdx;
      today = (startIdx-1);
      prevHigh = inHigh[today];
      prevLow = inLow[today];
      prevClose = inClose[today];
      while( (today<endIdx) )
      {
         today += 1;
         tempReal = inHigh[today];
         diffP = (tempReal-prevHigh);
         prevHigh = tempReal;
         tempReal = inLow[today];
         diffM = (prevLow-tempReal);
         prevLow = tempReal;
         if( ((diffM>0)&&(diffP<diffM)) )
         {
            double _true_range_0;
            double range_0 = (prevHigh-prevLow);
            double tmp_0 = fabs((prevHigh-prevClose));
            if( (tmp_0>range_0) )
            {
               range_0 = tmp_0;
            }
            tmp_0 = fabs((prevLow-prevClose));
            if( (tmp_0>range_0) )
            {
               range_0 = tmp_0;
            }
            _true_range_0 = range_0;
            tempReal = _true_range_0;
            if( TA_IS_ZERO(tempReal) )
            {
               outReal[outIdx++] = ((double)0.0);
            } else 
            {
               outReal[outIdx++] = (diffM/tempReal);
            }
         } else 
         {
            outReal[outIdx++] = ((double)0.0);
         }
         prevClose = inClose[today];
      }
      *outNBElement= outIdx;
      return TA_SUCCESS;
   }
   today = startIdx;
   *outBegIdx= today;
   prevMinusDM = 0.0;
   prevTR = 0.0;
   today = (startIdx-lookbackTotal);
   prevHigh = inHigh[today];
   prevLow = inLow[today];
   prevClose = inClose[today];
   i = (optInTimePeriod-1);
   while( (i-->0) )
   {
      today += 1;
      tempReal = inHigh[today];
      diffP = (tempReal-prevHigh);
      prevHigh = tempReal;
      tempReal = inLow[today];
      diffM = (prevLow-tempReal);
      prevLow = tempReal;
      if( ((diffM>0)&&(diffP<diffM)) )
      {
         prevMinusDM += diffM;
      }
      double _true_range_1;
      double range_1 = (prevHigh-prevLow);
      double tmp_1 = fabs((prevHigh-prevClose));
      if( (tmp_1>range_1) )
      {
         range_1 = tmp_1;
      }
      tmp_1 = fabs((prevLow-prevClose));
      if( (tmp_1>range_1) )
      {
         range_1 = tmp_1;
      }
      _true_range_1 = range_1;
      tempReal = _true_range_1;
      prevTR += tempReal;
      prevClose = inClose[today];
   }
   i = (TA_GLOBALS_UNSTABLE_PERIOD(TA_FUNC_UNST_MINUS_DI,Minus_di)+1);
   while( (i--!=0) )
   {
      today += 1;
      tempReal = inHigh[today];
      diffP = (tempReal-prevHigh);
      prevHigh = tempReal;
      tempReal = inLow[today];
      diffM = (prevLow-tempReal);
      prevLow = tempReal;
      if( ((diffM>0)&&(diffP<diffM)) )
      {
         prevMinusDM = ((prevMinusDM-(prevMinusDM/optInTimePeriod))+diffM);
      } else 
      {
         prevMinusDM = (prevMinusDM-(prevMinusDM/optInTimePeriod));
      }
      double _true_range_2;
      double range_2 = (prevHigh-prevLow);
      double tmp_2 = fabs((prevHigh-prevClose));
      if( (tmp_2>range_2) )
      {
         range_2 = tmp_2;
      }
      tmp_2 = fabs((prevLow-prevClose));
      if( (tmp_2>range_2) )
      {
         range_2 = tmp_2;
      }
      _true_range_2 = range_2;
      tempReal = _true_range_2;
      prevTR = ((prevTR-(prevTR/optInTimePeriod))+tempReal);
      prevClose = inClose[today];
   }
   if( !(TA_IS_ZERO(prevTR)) )
   {
      outReal[0] = (100.0*(prevMinusDM/prevTR));
   } else 
   {
      outReal[0] = 0.0;
   }
   outIdx = 1;
   while( (today<endIdx) )
   {
      today += 1;
      tempReal = inHigh[today];
      diffP = (tempReal-prevHigh);
      prevHigh = tempReal;
      tempReal = inLow[today];
      diffM = (prevLow-tempReal);
      prevLow = tempReal;
      if( ((diffM>0)&&(diffP<diffM)) )
      {
         prevMinusDM = ((prevMinusDM-(prevMinusDM/optInTimePeriod))+diffM);
      } else 
      {
         prevMinusDM = (prevMinusDM-(prevMinusDM/optInTimePeriod));
      }
      double _true_range_3;
      double range_3 = (prevHigh-prevLow);
      double tmp_3 = fabs((prevHigh-prevClose));
      if( (tmp_3>range_3) )
      {
         range_3 = tmp_3;
      }
      tmp_3 = fabs((prevLow-prevClose));
      if( (tmp_3>range_3) )
      {
         range_3 = tmp_3;
      }
      _true_range_3 = range_3;
      tempReal = _true_range_3;
      prevTR = ((prevTR-(prevTR/optInTimePeriod))+tempReal);
      prevClose = inClose[today];
      if( !(TA_IS_ZERO(prevTR)) )
      {
         outReal[outIdx++] = (100.0*(prevMinusDM/prevTR));
      } else 
      {
         outReal[outIdx++] = 0.0;
      }
   }
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_MINUS_DI_Unguarded( int    startIdx,
                                    int    endIdx,
                                    const float inHigh[],
                                    const float inLow[],
                                    const float inClose[],
                                    int optInTimePeriod,
                                    int          *outBegIdx,
                                    int          *outNBElement,
                                    double        outReal[] )
{
   int today;
   int lookbackTotal;
   int outIdx;
   double prevHigh;
   double prevLow;
   double prevClose;
   double prevMinusDM;
   double prevTR;
   double tempReal;
   double tempReal2;
   double diffP;
   double diffM;
   int i;

   if( (optInTimePeriod>1) )
   {
      lookbackTotal = (optInTimePeriod+TA_GLOBALS_UNSTABLE_PERIOD(TA_FUNC_UNST_MINUS_DI,Minus_di));
   } else 
   {
      lookbackTotal = 1;
   }
   if( (startIdx<lookbackTotal) )
   {
      startIdx = lookbackTotal;
   }
   if( (startIdx>endIdx) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      return TA_SUCCESS;
   }
   outIdx = 0;
   if( (optInTimePeriod<=1) )
   {
      *outBegIdx= startIdx;
      today = (startIdx-1);
      prevHigh = inHigh[today];
      prevLow = inLow[today];
      prevClose = inClose[today];
      while( (today<endIdx) )
      {
         today += 1;
         tempReal = inHigh[today];
         diffP = (tempReal-prevHigh);
         prevHigh = tempReal;
         tempReal = inLow[today];
         diffM = (prevLow-tempReal);
         prevLow = tempReal;
         if( ((diffM>0)&&(diffP<diffM)) )
         {
            double _true_range_0;
            double range_0 = (prevHigh-prevLow);
            double tmp_0 = fabs((prevHigh-prevClose));
            if( (tmp_0>range_0) )
            {
               range_0 = tmp_0;
            }
            tmp_0 = fabs((prevLow-prevClose));
            if( (tmp_0>range_0) )
            {
               range_0 = tmp_0;
            }
            _true_range_0 = range_0;
            tempReal = _true_range_0;
            if( TA_IS_ZERO(tempReal) )
            {
               outReal[outIdx++] = ((double)0.0);
            } else 
            {
               outReal[outIdx++] = (diffM/tempReal);
            }
         } else 
         {
            outReal[outIdx++] = ((double)0.0);
         }
         prevClose = inClose[today];
      }
      *outNBElement= outIdx;
      return TA_SUCCESS;
   }
   today = startIdx;
   *outBegIdx= today;
   prevMinusDM = 0.0;
   prevTR = 0.0;
   today = (startIdx-lookbackTotal);
   prevHigh = inHigh[today];
   prevLow = inLow[today];
   prevClose = inClose[today];
   i = (optInTimePeriod-1);
   while( (i-->0) )
   {
      today += 1;
      tempReal = inHigh[today];
      diffP = (tempReal-prevHigh);
      prevHigh = tempReal;
      tempReal = inLow[today];
      diffM = (prevLow-tempReal);
      prevLow = tempReal;
      if( ((diffM>0)&&(diffP<diffM)) )
      {
         prevMinusDM += diffM;
      }
      double _true_range_1;
      double range_1 = (prevHigh-prevLow);
      double tmp_1 = fabs((prevHigh-prevClose));
      if( (tmp_1>range_1) )
      {
         range_1 = tmp_1;
      }
      tmp_1 = fabs((prevLow-prevClose));
      if( (tmp_1>range_1) )
      {
         range_1 = tmp_1;
      }
      _true_range_1 = range_1;
      tempReal = _true_range_1;
      prevTR += tempReal;
      prevClose = inClose[today];
   }
   i = (TA_GLOBALS_UNSTABLE_PERIOD(TA_FUNC_UNST_MINUS_DI,Minus_di)+1);
   while( (i--!=0) )
   {
      today += 1;
      tempReal = inHigh[today];
      diffP = (tempReal-prevHigh);
      prevHigh = tempReal;
      tempReal = inLow[today];
      diffM = (prevLow-tempReal);
      prevLow = tempReal;
      if( ((diffM>0)&&(diffP<diffM)) )
      {
         prevMinusDM = ((prevMinusDM-(prevMinusDM/optInTimePeriod))+diffM);
      } else 
      {
         prevMinusDM = (prevMinusDM-(prevMinusDM/optInTimePeriod));
      }
      double _true_range_2;
      double range_2 = (prevHigh-prevLow);
      double tmp_2 = fabs((prevHigh-prevClose));
      if( (tmp_2>range_2) )
      {
         range_2 = tmp_2;
      }
      tmp_2 = fabs((prevLow-prevClose));
      if( (tmp_2>range_2) )
      {
         range_2 = tmp_2;
      }
      _true_range_2 = range_2;
      tempReal = _true_range_2;
      prevTR = ((prevTR-(prevTR/optInTimePeriod))+tempReal);
      prevClose = inClose[today];
   }
   if( !(TA_IS_ZERO(prevTR)) )
   {
      outReal[0] = (100.0*(prevMinusDM/prevTR));
   } else 
   {
      outReal[0] = 0.0;
   }
   outIdx = 1;
   while( (today<endIdx) )
   {
      today += 1;
      tempReal = inHigh[today];
      diffP = (tempReal-prevHigh);
      prevHigh = tempReal;
      tempReal = inLow[today];
      diffM = (prevLow-tempReal);
      prevLow = tempReal;
      if( ((diffM>0)&&(diffP<diffM)) )
      {
         prevMinusDM = ((prevMinusDM-(prevMinusDM/optInTimePeriod))+diffM);
      } else 
      {
         prevMinusDM = (prevMinusDM-(prevMinusDM/optInTimePeriod));
      }
      double _true_range_3;
      double range_3 = (prevHigh-prevLow);
      double tmp_3 = fabs((prevHigh-prevClose));
      if( (tmp_3>range_3) )
      {
         range_3 = tmp_3;
      }
      tmp_3 = fabs((prevLow-prevClose));
      if( (tmp_3>range_3) )
      {
         range_3 = tmp_3;
      }
      _true_range_3 = range_3;
      tempReal = _true_range_3;
      prevTR = ((prevTR-(prevTR/optInTimePeriod))+tempReal);
      prevClose = inClose[today];
      if( !(TA_IS_ZERO(prevTR)) )
      {
         outReal[outIdx++] = (100.0*(prevMinusDM/prevTR));
      } else 
      {
         outReal[outIdx++] = 0.0;
      }
   }
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

