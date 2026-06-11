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

TA_LIB_API int TA_SAREXT_Lookback( double optInStartValue, double optInOffsetOnReverse, double optInAccelerationInitLong, double optInAccelerationLong, double optInAccelerationMaxLong, double optInAccelerationInitShort, double optInAccelerationShort, double optInAccelerationMaxShort )
{
   return 1;
}

TA_LIB_API TA_RetCode TA_SAREXT( int    startIdx,
                                 int    endIdx,
                                 const double inHigh[],
                                 const double inLow[],
                                 double optInStartValue,
                                 double optInOffsetOnReverse,
                                 double optInAccelerationInitLong,
                                 double optInAccelerationLong,
                                 double optInAccelerationMaxLong,
                                 double optInAccelerationInitShort,
                                 double optInAccelerationShort,
                                 double optInAccelerationMaxShort,
                                 int          *outBegIdx,
                                 int          *outNBElement,
                                 double        outReal[] )
{
   TA_RetCode retCode;
   int isLong;
   int todayIdx;
   int outIdx;
   int tempInt;
   double newHigh;
   double newLow;
   double prevHigh;
   double prevLow;
   double afLong;
   double afShort;
   double ep;
   double sar;
   double ep_temp[1];

   if( startIdx < 0 )
      return TA_OUT_OF_RANGE_START_INDEX;
   if( (endIdx < 0) || (endIdx < startIdx) )
      return TA_OUT_OF_RANGE_END_INDEX;

   if( !inHigh )
      return TA_BAD_PARAM;
   if( !inLow )
      return TA_BAD_PARAM;
   if( optInStartValue == -4e37 )
      optInStartValue = 0;
   if( optInOffsetOnReverse == -4e37 )
      optInOffsetOnReverse = 0;
   else if( optInOffsetOnReverse < 0e0 || optInOffsetOnReverse > 1.7976931348623157e308 )
      return TA_BAD_PARAM;
   if( optInAccelerationInitLong == -4e37 )
      optInAccelerationInitLong = 0.02;
   else if( optInAccelerationInitLong < 0e0 || optInAccelerationInitLong > 1.7976931348623157e308 )
      return TA_BAD_PARAM;
   if( optInAccelerationLong == -4e37 )
      optInAccelerationLong = 0.02;
   else if( optInAccelerationLong < 0e0 || optInAccelerationLong > 1.7976931348623157e308 )
      return TA_BAD_PARAM;
   if( optInAccelerationMaxLong == -4e37 )
      optInAccelerationMaxLong = 0.2;
   else if( optInAccelerationMaxLong < 0e0 || optInAccelerationMaxLong > 1.7976931348623157e308 )
      return TA_BAD_PARAM;
   if( optInAccelerationInitShort == -4e37 )
      optInAccelerationInitShort = 0.02;
   else if( optInAccelerationInitShort < 0e0 || optInAccelerationInitShort > 1.7976931348623157e308 )
      return TA_BAD_PARAM;
   if( optInAccelerationShort == -4e37 )
      optInAccelerationShort = 0.02;
   else if( optInAccelerationShort < 0e0 || optInAccelerationShort > 1.7976931348623157e308 )
      return TA_BAD_PARAM;
   if( optInAccelerationMaxShort == -4e37 )
      optInAccelerationMaxShort = 0.2;
   else if( optInAccelerationMaxShort < 0e0 || optInAccelerationMaxShort > 1.7976931348623157e308 )
      return TA_BAD_PARAM;
   if( !outReal )
      return TA_BAD_PARAM;

   if( (startIdx<1) )
   {
      startIdx = 1;
   }
   if( (startIdx>endIdx) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      return TA_SUCCESS;
   }
   afLong = optInAccelerationInitLong;
   afShort = optInAccelerationInitShort;
   if( (afLong>optInAccelerationMaxLong) )
   {
      optInAccelerationInitLong = optInAccelerationMaxLong;
      afLong = optInAccelerationInitLong;
   }
   if( (optInAccelerationLong>optInAccelerationMaxLong) )
   {
      optInAccelerationLong = optInAccelerationMaxLong;
   }
   if( (afShort>optInAccelerationMaxShort) )
   {
      optInAccelerationInitShort = optInAccelerationMaxShort;
      afShort = optInAccelerationInitShort;
   }
   if( (optInAccelerationShort>optInAccelerationMaxShort) )
   {
      optInAccelerationShort = optInAccelerationMaxShort;
   }
   if( (optInStartValue==0) )
   {
      retCode = TA_MINUS_DM_Unguarded(startIdx,startIdx,inHigh,inLow,1,&tempInt,&tempInt,ep_temp);
      if( (ep_temp[0]>0) )
      {
         isLong = 0;
      } else 
      {
         isLong = 1;
      }
      if( (retCode!=TA_SUCCESS) )
      {
         *outBegIdx= 0;
         *outNBElement= 0;
         return retCode;
      }
   } else if( (optInStartValue>0) )
   {
      isLong = 1;
   } else 
   {
      isLong = 0;
   }
   *outBegIdx= startIdx;
   outIdx = 0;
   todayIdx = startIdx;
   newHigh = inHigh[(todayIdx-1)];
   newLow = inLow[(todayIdx-1)];
   if( (optInStartValue==0) )
   {
      if( (isLong==1) )
      {
         ep = inHigh[todayIdx];
         sar = newLow;
      } else 
      {
         ep = inLow[todayIdx];
         sar = newHigh;
      }
   } else if( (optInStartValue>0) )
   {
      ep = inHigh[todayIdx];
      sar = optInStartValue;
   } else 
   {
      ep = inLow[todayIdx];
      sar = fabs(optInStartValue);
   }
   newLow = inLow[todayIdx];
   newHigh = inHigh[todayIdx];
   while( (todayIdx<=endIdx) )
   {
      prevLow = newLow;
      prevHigh = newHigh;
      newLow = inLow[todayIdx];
      newHigh = inHigh[todayIdx];
      todayIdx += 1;
      if( (isLong==1) )
      {
         if( (newLow<=sar) )
         {
            isLong = 0;
            sar = ep;
            if( (sar<prevHigh) )
            {
               sar = prevHigh;
            }
            if( (sar<newHigh) )
            {
               sar = newHigh;
            }
            if( (optInOffsetOnReverse!=0.0) )
            {
               sar += (sar*optInOffsetOnReverse);
            }
            outReal[outIdx++] = (0-sar);
            afShort = optInAccelerationInitShort;
            ep = newLow;
            sar = (sar+(afShort*(ep-sar)));
            if( (sar<prevHigh) )
            {
               sar = prevHigh;
            }
            if( (sar<newHigh) )
            {
               sar = newHigh;
            }
         } else 
         {
            outReal[outIdx++] = sar;
            if( (newHigh>ep) )
            {
               ep = newHigh;
               afLong += optInAccelerationLong;
               if( (afLong>optInAccelerationMaxLong) )
               {
                  afLong = optInAccelerationMaxLong;
               }
            }
            sar = (sar+(afLong*(ep-sar)));
            if( (sar>prevLow) )
            {
               sar = prevLow;
            }
            if( (sar>newLow) )
            {
               sar = newLow;
            }
         }
      } else if( (newHigh>=sar) )
      {
         isLong = 1;
         sar = ep;
         if( (sar>prevLow) )
         {
            sar = prevLow;
         }
         if( (sar>newLow) )
         {
            sar = newLow;
         }
         if( (optInOffsetOnReverse!=0.0) )
         {
            sar -= (sar*optInOffsetOnReverse);
         }
         outReal[outIdx++] = sar;
         afLong = optInAccelerationInitLong;
         ep = newHigh;
         sar = (sar+(afLong*(ep-sar)));
         if( (sar>prevLow) )
         {
            sar = prevLow;
         }
         if( (sar>newLow) )
         {
            sar = newLow;
         }
      } else 
      {
         outReal[outIdx++] = (0-sar);
         if( (newLow<ep) )
         {
            ep = newLow;
            afShort += optInAccelerationShort;
            if( (afShort>optInAccelerationMaxShort) )
            {
               afShort = optInAccelerationMaxShort;
            }
         }
         sar = (sar+(afShort*(ep-sar)));
         if( (sar<prevHigh) )
         {
            sar = prevHigh;
         }
         if( (sar<newHigh) )
         {
            sar = newHigh;
         }
      }
   }
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_LIB_API TA_RetCode TA_SAREXT_Unguarded( int    startIdx,
                                           int    endIdx,
                                           const double inHigh[],
                                           const double inLow[],
                                           double optInStartValue,
                                           double optInOffsetOnReverse,
                                           double optInAccelerationInitLong,
                                           double optInAccelerationLong,
                                           double optInAccelerationMaxLong,
                                           double optInAccelerationInitShort,
                                           double optInAccelerationShort,
                                           double optInAccelerationMaxShort,
                                           int          *outBegIdx,
                                           int          *outNBElement,
                                           double        outReal[] )
{
   TA_RetCode retCode;
   int isLong;
   int todayIdx;
   int outIdx;
   int tempInt;
   double newHigh;
   double newLow;
   double prevHigh;
   double prevLow;
   double afLong;
   double afShort;
   double ep;
   double sar;
   double ep_temp[1];

   if( (startIdx<1) )
   {
      startIdx = 1;
   }
   if( (startIdx>endIdx) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      return TA_SUCCESS;
   }
   afLong = optInAccelerationInitLong;
   afShort = optInAccelerationInitShort;
   if( (afLong>optInAccelerationMaxLong) )
   {
      optInAccelerationInitLong = optInAccelerationMaxLong;
      afLong = optInAccelerationInitLong;
   }
   if( (optInAccelerationLong>optInAccelerationMaxLong) )
   {
      optInAccelerationLong = optInAccelerationMaxLong;
   }
   if( (afShort>optInAccelerationMaxShort) )
   {
      optInAccelerationInitShort = optInAccelerationMaxShort;
      afShort = optInAccelerationInitShort;
   }
   if( (optInAccelerationShort>optInAccelerationMaxShort) )
   {
      optInAccelerationShort = optInAccelerationMaxShort;
   }
   if( (optInStartValue==0) )
   {
      retCode = TA_MINUS_DM_Unguarded(startIdx,startIdx,inHigh,inLow,1,&tempInt,&tempInt,ep_temp);
      if( (ep_temp[0]>0) )
      {
         isLong = 0;
      } else 
      {
         isLong = 1;
      }
      if( (retCode!=TA_SUCCESS) )
      {
         *outBegIdx= 0;
         *outNBElement= 0;
         return retCode;
      }
   } else if( (optInStartValue>0) )
   {
      isLong = 1;
   } else 
   {
      isLong = 0;
   }
   *outBegIdx= startIdx;
   outIdx = 0;
   todayIdx = startIdx;
   newHigh = inHigh[(todayIdx-1)];
   newLow = inLow[(todayIdx-1)];
   if( (optInStartValue==0) )
   {
      if( (isLong==1) )
      {
         ep = inHigh[todayIdx];
         sar = newLow;
      } else 
      {
         ep = inLow[todayIdx];
         sar = newHigh;
      }
   } else if( (optInStartValue>0) )
   {
      ep = inHigh[todayIdx];
      sar = optInStartValue;
   } else 
   {
      ep = inLow[todayIdx];
      sar = fabs(optInStartValue);
   }
   newLow = inLow[todayIdx];
   newHigh = inHigh[todayIdx];
   while( (todayIdx<=endIdx) )
   {
      prevLow = newLow;
      prevHigh = newHigh;
      newLow = inLow[todayIdx];
      newHigh = inHigh[todayIdx];
      todayIdx += 1;
      if( (isLong==1) )
      {
         if( (newLow<=sar) )
         {
            isLong = 0;
            sar = ep;
            if( (sar<prevHigh) )
            {
               sar = prevHigh;
            }
            if( (sar<newHigh) )
            {
               sar = newHigh;
            }
            if( (optInOffsetOnReverse!=0.0) )
            {
               sar += (sar*optInOffsetOnReverse);
            }
            outReal[outIdx++] = (0-sar);
            afShort = optInAccelerationInitShort;
            ep = newLow;
            sar = (sar+(afShort*(ep-sar)));
            if( (sar<prevHigh) )
            {
               sar = prevHigh;
            }
            if( (sar<newHigh) )
            {
               sar = newHigh;
            }
         } else 
         {
            outReal[outIdx++] = sar;
            if( (newHigh>ep) )
            {
               ep = newHigh;
               afLong += optInAccelerationLong;
               if( (afLong>optInAccelerationMaxLong) )
               {
                  afLong = optInAccelerationMaxLong;
               }
            }
            sar = (sar+(afLong*(ep-sar)));
            if( (sar>prevLow) )
            {
               sar = prevLow;
            }
            if( (sar>newLow) )
            {
               sar = newLow;
            }
         }
      } else if( (newHigh>=sar) )
      {
         isLong = 1;
         sar = ep;
         if( (sar>prevLow) )
         {
            sar = prevLow;
         }
         if( (sar>newLow) )
         {
            sar = newLow;
         }
         if( (optInOffsetOnReverse!=0.0) )
         {
            sar -= (sar*optInOffsetOnReverse);
         }
         outReal[outIdx++] = sar;
         afLong = optInAccelerationInitLong;
         ep = newHigh;
         sar = (sar+(afLong*(ep-sar)));
         if( (sar>prevLow) )
         {
            sar = prevLow;
         }
         if( (sar>newLow) )
         {
            sar = newLow;
         }
      } else 
      {
         outReal[outIdx++] = (0-sar);
         if( (newLow<ep) )
         {
            ep = newLow;
            afShort += optInAccelerationShort;
            if( (afShort>optInAccelerationMaxShort) )
            {
               afShort = optInAccelerationMaxShort;
            }
         }
         sar = (sar+(afShort*(ep-sar)));
         if( (sar<prevHigh) )
         {
            sar = prevHigh;
         }
         if( (sar<newHigh) )
         {
            sar = newHigh;
         }
      }
   }
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_SAREXT( int    startIdx,
                        int    endIdx,
                        const float inHigh[],
                        const float inLow[],
                        double optInStartValue,
                        double optInOffsetOnReverse,
                        double optInAccelerationInitLong,
                        double optInAccelerationLong,
                        double optInAccelerationMaxLong,
                        double optInAccelerationInitShort,
                        double optInAccelerationShort,
                        double optInAccelerationMaxShort,
                        int          *outBegIdx,
                        int          *outNBElement,
                        double        outReal[] )
{
   TA_RetCode retCode;
   int isLong;
   int todayIdx;
   int outIdx;
   int tempInt;
   double newHigh;
   double newLow;
   double prevHigh;
   double prevLow;
   double afLong;
   double afShort;
   double ep;
   double sar;
   double ep_temp[1];

   if( startIdx < 0 )
      return TA_OUT_OF_RANGE_START_INDEX;
   if( (endIdx < 0) || (endIdx < startIdx) )
      return TA_OUT_OF_RANGE_END_INDEX;

   if( !inHigh )
      return TA_BAD_PARAM;
   if( !inLow )
      return TA_BAD_PARAM;
   if( optInStartValue == -4e37 )
      optInStartValue = 0;
   if( optInOffsetOnReverse == -4e37 )
      optInOffsetOnReverse = 0;
   else if( optInOffsetOnReverse < 0e0 || optInOffsetOnReverse > 1.7976931348623157e308 )
      return TA_BAD_PARAM;
   if( optInAccelerationInitLong == -4e37 )
      optInAccelerationInitLong = 0.02;
   else if( optInAccelerationInitLong < 0e0 || optInAccelerationInitLong > 1.7976931348623157e308 )
      return TA_BAD_PARAM;
   if( optInAccelerationLong == -4e37 )
      optInAccelerationLong = 0.02;
   else if( optInAccelerationLong < 0e0 || optInAccelerationLong > 1.7976931348623157e308 )
      return TA_BAD_PARAM;
   if( optInAccelerationMaxLong == -4e37 )
      optInAccelerationMaxLong = 0.2;
   else if( optInAccelerationMaxLong < 0e0 || optInAccelerationMaxLong > 1.7976931348623157e308 )
      return TA_BAD_PARAM;
   if( optInAccelerationInitShort == -4e37 )
      optInAccelerationInitShort = 0.02;
   else if( optInAccelerationInitShort < 0e0 || optInAccelerationInitShort > 1.7976931348623157e308 )
      return TA_BAD_PARAM;
   if( optInAccelerationShort == -4e37 )
      optInAccelerationShort = 0.02;
   else if( optInAccelerationShort < 0e0 || optInAccelerationShort > 1.7976931348623157e308 )
      return TA_BAD_PARAM;
   if( optInAccelerationMaxShort == -4e37 )
      optInAccelerationMaxShort = 0.2;
   else if( optInAccelerationMaxShort < 0e0 || optInAccelerationMaxShort > 1.7976931348623157e308 )
      return TA_BAD_PARAM;
   if( !outReal )
      return TA_BAD_PARAM;

   if( (startIdx<1) )
   {
      startIdx = 1;
   }
   if( (startIdx>endIdx) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      return TA_SUCCESS;
   }
   afLong = optInAccelerationInitLong;
   afShort = optInAccelerationInitShort;
   if( (afLong>optInAccelerationMaxLong) )
   {
      optInAccelerationInitLong = optInAccelerationMaxLong;
      afLong = optInAccelerationInitLong;
   }
   if( (optInAccelerationLong>optInAccelerationMaxLong) )
   {
      optInAccelerationLong = optInAccelerationMaxLong;
   }
   if( (afShort>optInAccelerationMaxShort) )
   {
      optInAccelerationInitShort = optInAccelerationMaxShort;
      afShort = optInAccelerationInitShort;
   }
   if( (optInAccelerationShort>optInAccelerationMaxShort) )
   {
      optInAccelerationShort = optInAccelerationMaxShort;
   }
   if( (optInStartValue==0) )
   {
      retCode = TA_S_MINUS_DM_Unguarded(startIdx,startIdx,inHigh,inLow,1,&tempInt,&tempInt,ep_temp);
      if( (ep_temp[0]>0) )
      {
         isLong = 0;
      } else 
      {
         isLong = 1;
      }
      if( (retCode!=TA_SUCCESS) )
      {
         *outBegIdx= 0;
         *outNBElement= 0;
         return retCode;
      }
   } else if( (optInStartValue>0) )
   {
      isLong = 1;
   } else 
   {
      isLong = 0;
   }
   *outBegIdx= startIdx;
   outIdx = 0;
   todayIdx = startIdx;
   newHigh = inHigh[(todayIdx-1)];
   newLow = inLow[(todayIdx-1)];
   if( (optInStartValue==0) )
   {
      if( (isLong==1) )
      {
         ep = inHigh[todayIdx];
         sar = newLow;
      } else 
      {
         ep = inLow[todayIdx];
         sar = newHigh;
      }
   } else if( (optInStartValue>0) )
   {
      ep = inHigh[todayIdx];
      sar = optInStartValue;
   } else 
   {
      ep = inLow[todayIdx];
      sar = fabs(optInStartValue);
   }
   newLow = inLow[todayIdx];
   newHigh = inHigh[todayIdx];
   while( (todayIdx<=endIdx) )
   {
      prevLow = newLow;
      prevHigh = newHigh;
      newLow = inLow[todayIdx];
      newHigh = inHigh[todayIdx];
      todayIdx += 1;
      if( (isLong==1) )
      {
         if( (newLow<=sar) )
         {
            isLong = 0;
            sar = ep;
            if( (sar<prevHigh) )
            {
               sar = prevHigh;
            }
            if( (sar<newHigh) )
            {
               sar = newHigh;
            }
            if( (optInOffsetOnReverse!=0.0) )
            {
               sar += (sar*optInOffsetOnReverse);
            }
            outReal[outIdx++] = (0-sar);
            afShort = optInAccelerationInitShort;
            ep = newLow;
            sar = (sar+(afShort*(ep-sar)));
            if( (sar<prevHigh) )
            {
               sar = prevHigh;
            }
            if( (sar<newHigh) )
            {
               sar = newHigh;
            }
         } else 
         {
            outReal[outIdx++] = sar;
            if( (newHigh>ep) )
            {
               ep = newHigh;
               afLong += optInAccelerationLong;
               if( (afLong>optInAccelerationMaxLong) )
               {
                  afLong = optInAccelerationMaxLong;
               }
            }
            sar = (sar+(afLong*(ep-sar)));
            if( (sar>prevLow) )
            {
               sar = prevLow;
            }
            if( (sar>newLow) )
            {
               sar = newLow;
            }
         }
      } else if( (newHigh>=sar) )
      {
         isLong = 1;
         sar = ep;
         if( (sar>prevLow) )
         {
            sar = prevLow;
         }
         if( (sar>newLow) )
         {
            sar = newLow;
         }
         if( (optInOffsetOnReverse!=0.0) )
         {
            sar -= (sar*optInOffsetOnReverse);
         }
         outReal[outIdx++] = sar;
         afLong = optInAccelerationInitLong;
         ep = newHigh;
         sar = (sar+(afLong*(ep-sar)));
         if( (sar>prevLow) )
         {
            sar = prevLow;
         }
         if( (sar>newLow) )
         {
            sar = newLow;
         }
      } else 
      {
         outReal[outIdx++] = (0-sar);
         if( (newLow<ep) )
         {
            ep = newLow;
            afShort += optInAccelerationShort;
            if( (afShort>optInAccelerationMaxShort) )
            {
               afShort = optInAccelerationMaxShort;
            }
         }
         sar = (sar+(afShort*(ep-sar)));
         if( (sar<prevHigh) )
         {
            sar = prevHigh;
         }
         if( (sar<newHigh) )
         {
            sar = newHigh;
         }
      }
   }
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_SAREXT_Unguarded( int    startIdx,
                                  int    endIdx,
                                  const float inHigh[],
                                  const float inLow[],
                                  double optInStartValue,
                                  double optInOffsetOnReverse,
                                  double optInAccelerationInitLong,
                                  double optInAccelerationLong,
                                  double optInAccelerationMaxLong,
                                  double optInAccelerationInitShort,
                                  double optInAccelerationShort,
                                  double optInAccelerationMaxShort,
                                  int          *outBegIdx,
                                  int          *outNBElement,
                                  double        outReal[] )
{
   TA_RetCode retCode;
   int isLong;
   int todayIdx;
   int outIdx;
   int tempInt;
   double newHigh;
   double newLow;
   double prevHigh;
   double prevLow;
   double afLong;
   double afShort;
   double ep;
   double sar;
   double ep_temp[1];

   if( (startIdx<1) )
   {
      startIdx = 1;
   }
   if( (startIdx>endIdx) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      return TA_SUCCESS;
   }
   afLong = optInAccelerationInitLong;
   afShort = optInAccelerationInitShort;
   if( (afLong>optInAccelerationMaxLong) )
   {
      optInAccelerationInitLong = optInAccelerationMaxLong;
      afLong = optInAccelerationInitLong;
   }
   if( (optInAccelerationLong>optInAccelerationMaxLong) )
   {
      optInAccelerationLong = optInAccelerationMaxLong;
   }
   if( (afShort>optInAccelerationMaxShort) )
   {
      optInAccelerationInitShort = optInAccelerationMaxShort;
      afShort = optInAccelerationInitShort;
   }
   if( (optInAccelerationShort>optInAccelerationMaxShort) )
   {
      optInAccelerationShort = optInAccelerationMaxShort;
   }
   if( (optInStartValue==0) )
   {
      retCode = TA_S_MINUS_DM_Unguarded(startIdx,startIdx,inHigh,inLow,1,&tempInt,&tempInt,ep_temp);
      if( (ep_temp[0]>0) )
      {
         isLong = 0;
      } else 
      {
         isLong = 1;
      }
      if( (retCode!=TA_SUCCESS) )
      {
         *outBegIdx= 0;
         *outNBElement= 0;
         return retCode;
      }
   } else if( (optInStartValue>0) )
   {
      isLong = 1;
   } else 
   {
      isLong = 0;
   }
   *outBegIdx= startIdx;
   outIdx = 0;
   todayIdx = startIdx;
   newHigh = inHigh[(todayIdx-1)];
   newLow = inLow[(todayIdx-1)];
   if( (optInStartValue==0) )
   {
      if( (isLong==1) )
      {
         ep = inHigh[todayIdx];
         sar = newLow;
      } else 
      {
         ep = inLow[todayIdx];
         sar = newHigh;
      }
   } else if( (optInStartValue>0) )
   {
      ep = inHigh[todayIdx];
      sar = optInStartValue;
   } else 
   {
      ep = inLow[todayIdx];
      sar = fabs(optInStartValue);
   }
   newLow = inLow[todayIdx];
   newHigh = inHigh[todayIdx];
   while( (todayIdx<=endIdx) )
   {
      prevLow = newLow;
      prevHigh = newHigh;
      newLow = inLow[todayIdx];
      newHigh = inHigh[todayIdx];
      todayIdx += 1;
      if( (isLong==1) )
      {
         if( (newLow<=sar) )
         {
            isLong = 0;
            sar = ep;
            if( (sar<prevHigh) )
            {
               sar = prevHigh;
            }
            if( (sar<newHigh) )
            {
               sar = newHigh;
            }
            if( (optInOffsetOnReverse!=0.0) )
            {
               sar += (sar*optInOffsetOnReverse);
            }
            outReal[outIdx++] = (0-sar);
            afShort = optInAccelerationInitShort;
            ep = newLow;
            sar = (sar+(afShort*(ep-sar)));
            if( (sar<prevHigh) )
            {
               sar = prevHigh;
            }
            if( (sar<newHigh) )
            {
               sar = newHigh;
            }
         } else 
         {
            outReal[outIdx++] = sar;
            if( (newHigh>ep) )
            {
               ep = newHigh;
               afLong += optInAccelerationLong;
               if( (afLong>optInAccelerationMaxLong) )
               {
                  afLong = optInAccelerationMaxLong;
               }
            }
            sar = (sar+(afLong*(ep-sar)));
            if( (sar>prevLow) )
            {
               sar = prevLow;
            }
            if( (sar>newLow) )
            {
               sar = newLow;
            }
         }
      } else if( (newHigh>=sar) )
      {
         isLong = 1;
         sar = ep;
         if( (sar>prevLow) )
         {
            sar = prevLow;
         }
         if( (sar>newLow) )
         {
            sar = newLow;
         }
         if( (optInOffsetOnReverse!=0.0) )
         {
            sar -= (sar*optInOffsetOnReverse);
         }
         outReal[outIdx++] = sar;
         afLong = optInAccelerationInitLong;
         ep = newHigh;
         sar = (sar+(afLong*(ep-sar)));
         if( (sar>prevLow) )
         {
            sar = prevLow;
         }
         if( (sar>newLow) )
         {
            sar = newLow;
         }
      } else 
      {
         outReal[outIdx++] = (0-sar);
         if( (newLow<ep) )
         {
            ep = newLow;
            afShort += optInAccelerationShort;
            if( (afShort>optInAccelerationMaxShort) )
            {
               afShort = optInAccelerationMaxShort;
            }
         }
         sar = (sar+(afShort*(ep-sar)));
         if( (sar<prevHigh) )
         {
            sar = prevHigh;
         }
         if( (sar<newHigh) )
         {
            sar = newHigh;
         }
      }
   }
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

