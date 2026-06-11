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

TA_LIB_API int TA_SAR_Lookback( double optInAcceleration, double optInMaximum )
{
   return 1;
}

TA_LIB_API TA_RetCode TA_SAR( int    startIdx,
                              int    endIdx,
                              const double inHigh[],
                              const double inLow[],
                              double optInAcceleration,
                              double optInMaximum,
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
   double af;
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
   if( optInAcceleration == -4e37 )
      optInAcceleration = 0.02;
   else if( optInAcceleration < 0e0 || optInAcceleration > 1.7976931348623157e308 )
      return TA_BAD_PARAM;
   if( optInMaximum == -4e37 )
      optInMaximum = 0.2;
   else if( optInMaximum < 0e0 || optInMaximum > 1.7976931348623157e308 )
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
   af = optInAcceleration;
   if( (af>optInMaximum) )
   {
      optInAcceleration = optInMaximum;
      af = optInAcceleration;
   }
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
   *outBegIdx= startIdx;
   outIdx = 0;
   todayIdx = startIdx;
   newHigh = inHigh[(todayIdx-1)];
   newLow = inLow[(todayIdx-1)];
   if( (isLong==1) )
   {
      ep = inHigh[todayIdx];
      sar = newLow;
   } else 
   {
      ep = inLow[todayIdx];
      sar = newHigh;
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
            outReal[outIdx++] = sar;
            af = optInAcceleration;
            ep = newLow;
            sar = (sar+(af*(ep-sar)));
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
               af += optInAcceleration;
               if( (af>optInMaximum) )
               {
                  af = optInMaximum;
               }
            }
            sar = (sar+(af*(ep-sar)));
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
         outReal[outIdx++] = sar;
         af = optInAcceleration;
         ep = newHigh;
         sar = (sar+(af*(ep-sar)));
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
         outReal[outIdx++] = sar;
         if( (newLow<ep) )
         {
            ep = newLow;
            af += optInAcceleration;
            if( (af>optInMaximum) )
            {
               af = optInMaximum;
            }
         }
         sar = (sar+(af*(ep-sar)));
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

TA_LIB_API TA_RetCode TA_SAR_Unguarded( int    startIdx,
                                        int    endIdx,
                                        const double inHigh[],
                                        const double inLow[],
                                        double optInAcceleration,
                                        double optInMaximum,
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
   double af;
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
   af = optInAcceleration;
   if( (af>optInMaximum) )
   {
      optInAcceleration = optInMaximum;
      af = optInAcceleration;
   }
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
   *outBegIdx= startIdx;
   outIdx = 0;
   todayIdx = startIdx;
   newHigh = inHigh[(todayIdx-1)];
   newLow = inLow[(todayIdx-1)];
   if( (isLong==1) )
   {
      ep = inHigh[todayIdx];
      sar = newLow;
   } else 
   {
      ep = inLow[todayIdx];
      sar = newHigh;
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
            outReal[outIdx++] = sar;
            af = optInAcceleration;
            ep = newLow;
            sar = (sar+(af*(ep-sar)));
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
               af += optInAcceleration;
               if( (af>optInMaximum) )
               {
                  af = optInMaximum;
               }
            }
            sar = (sar+(af*(ep-sar)));
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
         outReal[outIdx++] = sar;
         af = optInAcceleration;
         ep = newHigh;
         sar = (sar+(af*(ep-sar)));
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
         outReal[outIdx++] = sar;
         if( (newLow<ep) )
         {
            ep = newLow;
            af += optInAcceleration;
            if( (af>optInMaximum) )
            {
               af = optInMaximum;
            }
         }
         sar = (sar+(af*(ep-sar)));
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

TA_RetCode TA_S_SAR( int    startIdx,
                     int    endIdx,
                     const float inHigh[],
                     const float inLow[],
                     double optInAcceleration,
                     double optInMaximum,
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
   double af;
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
   if( optInAcceleration == -4e37 )
      optInAcceleration = 0.02;
   else if( optInAcceleration < 0e0 || optInAcceleration > 1.7976931348623157e308 )
      return TA_BAD_PARAM;
   if( optInMaximum == -4e37 )
      optInMaximum = 0.2;
   else if( optInMaximum < 0e0 || optInMaximum > 1.7976931348623157e308 )
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
   af = optInAcceleration;
   if( (af>optInMaximum) )
   {
      optInAcceleration = optInMaximum;
      af = optInAcceleration;
   }
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
   *outBegIdx= startIdx;
   outIdx = 0;
   todayIdx = startIdx;
   newHigh = inHigh[(todayIdx-1)];
   newLow = inLow[(todayIdx-1)];
   if( (isLong==1) )
   {
      ep = inHigh[todayIdx];
      sar = newLow;
   } else 
   {
      ep = inLow[todayIdx];
      sar = newHigh;
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
            outReal[outIdx++] = sar;
            af = optInAcceleration;
            ep = newLow;
            sar = (sar+(af*(ep-sar)));
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
               af += optInAcceleration;
               if( (af>optInMaximum) )
               {
                  af = optInMaximum;
               }
            }
            sar = (sar+(af*(ep-sar)));
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
         outReal[outIdx++] = sar;
         af = optInAcceleration;
         ep = newHigh;
         sar = (sar+(af*(ep-sar)));
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
         outReal[outIdx++] = sar;
         if( (newLow<ep) )
         {
            ep = newLow;
            af += optInAcceleration;
            if( (af>optInMaximum) )
            {
               af = optInMaximum;
            }
         }
         sar = (sar+(af*(ep-sar)));
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

TA_RetCode TA_S_SAR_Unguarded( int    startIdx,
                               int    endIdx,
                               const float inHigh[],
                               const float inLow[],
                               double optInAcceleration,
                               double optInMaximum,
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
   double af;
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
   af = optInAcceleration;
   if( (af>optInMaximum) )
   {
      optInAcceleration = optInMaximum;
      af = optInAcceleration;
   }
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
   *outBegIdx= startIdx;
   outIdx = 0;
   todayIdx = startIdx;
   newHigh = inHigh[(todayIdx-1)];
   newLow = inLow[(todayIdx-1)];
   if( (isLong==1) )
   {
      ep = inHigh[todayIdx];
      sar = newLow;
   } else 
   {
      ep = inLow[todayIdx];
      sar = newHigh;
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
            outReal[outIdx++] = sar;
            af = optInAcceleration;
            ep = newLow;
            sar = (sar+(af*(ep-sar)));
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
               af += optInAcceleration;
               if( (af>optInMaximum) )
               {
                  af = optInMaximum;
               }
            }
            sar = (sar+(af*(ep-sar)));
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
         outReal[outIdx++] = sar;
         af = optInAcceleration;
         ep = newHigh;
         sar = (sar+(af*(ep-sar)));
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
         outReal[outIdx++] = sar;
         if( (newLow<ep) )
         {
            ep = newLow;
            af += optInAcceleration;
            if( (af>optInMaximum) )
            {
               af = optInMaximum;
            }
         }
         sar = (sar+(af*(ep-sar)));
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

