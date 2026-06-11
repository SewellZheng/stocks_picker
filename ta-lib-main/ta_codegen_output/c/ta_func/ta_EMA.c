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

TA_LIB_API int TA_EMA_Lookback( int optInTimePeriod )
{
   return ((optInTimePeriod-1)+TA_GLOBALS_UNSTABLE_PERIOD(TA_FUNC_UNST_EMA,Ema));
}

TA_LIB_API TA_RetCode TA_EMA_Private( int    startIdx,
                                      int    endIdx,
                                      const double inReal[],
                                      int optInTimePeriod,
                                      double optInK_1,
                                      int          *outBegIdx,
                                      int          *outNBElement,
                                      double        outReal[] )
{
   double tempReal;
   double prevMA;
   int i;
   int today;
   int outIdx;
   int lookbackTotal;

   lookbackTotal = TA_EMA_Lookback(optInTimePeriod);
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
   *outBegIdx= startIdx;
   if( (TA_GLOBALS_COMPATIBILITY==ENUM_VALUE(Compatibility,TA_COMPATIBILITY_DEFAULT,Default)) )
   {
      today = (startIdx-lookbackTotal);
      i = optInTimePeriod;
      tempReal = 0.0;
      while( (i-->0) )
      {
         tempReal += inReal[today++];
      }
      prevMA = (tempReal/optInTimePeriod);
   } else 
   {
      prevMA = inReal[0];
      today = 1;
   }
   while( (today<=startIdx) )
   {
      prevMA = (((inReal[today++]-prevMA)*optInK_1)+prevMA);
   }
   outReal[0] = prevMA;
   outIdx = 1;
   while( (today<=endIdx) )
   {
      prevMA = (((inReal[today++]-prevMA)*optInK_1)+prevMA);
      outReal[outIdx++] = prevMA;
   }
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_LIB_API TA_RetCode TA_EMA_Unguarded( int    startIdx,
                                        int    endIdx,
                                        const double inReal[],
                                        int optInTimePeriod,
                                        int          *outBegIdx,
                                        int          *outNBElement,
                                        double        outReal[] )
{
   double optInK_1;

   optInK_1 = (2.0/((double)(optInTimePeriod+1)));
   return TA_EMA_Private(startIdx,endIdx,inReal,optInTimePeriod,optInK_1,outBegIdx,outNBElement,outReal);

   return TA_SUCCESS;
}

TA_LIB_API TA_RetCode TA_EMA( int    startIdx,
                              int    endIdx,
                              const double inReal[],
                              int optInTimePeriod,
                              int          *outBegIdx,
                              int          *outNBElement,
                              double        outReal[] )
{
   double optInK_1;

   if( startIdx < 0 )
      return TA_OUT_OF_RANGE_START_INDEX;
   if( (endIdx < 0) || (endIdx < startIdx) )
      return TA_OUT_OF_RANGE_END_INDEX;

   if( !inReal )
      return TA_BAD_PARAM;
   if( (int)optInTimePeriod == (int)0x80000000 )
      optInTimePeriod = 30;
   else if( (int)optInTimePeriod < 2 || (int)optInTimePeriod > 100000 )
      return TA_BAD_PARAM;
   if( !outReal )
      return TA_BAD_PARAM;

   optInK_1 = (2.0/((double)(optInTimePeriod+1)));
   return TA_EMA_Private(startIdx,endIdx,inReal,optInTimePeriod,optInK_1,outBegIdx,outNBElement,outReal);

   return TA_SUCCESS;
}

TA_RetCode TA_S_EMA_Unguarded( int    startIdx,
                               int    endIdx,
                               const float inReal[],
                               int optInTimePeriod,
                               int          *outBegIdx,
                               int          *outNBElement,
                               double        outReal[] )
{
   double tempReal;
   double prevMA;
   int i;
   int today;
   int outIdx;
   int lookbackTotal;
   double optInK_1 = (2.0/((double)(optInTimePeriod+1)));

   lookbackTotal = TA_EMA_Lookback(optInTimePeriod);
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
   *outBegIdx= startIdx;
   if( (TA_GLOBALS_COMPATIBILITY==ENUM_VALUE(Compatibility,TA_COMPATIBILITY_DEFAULT,Default)) )
   {
      today = (startIdx-lookbackTotal);
      i = optInTimePeriod;
      tempReal = 0.0;
      while( (i-->0) )
      {
         tempReal += inReal[today++];
      }
      prevMA = (tempReal/optInTimePeriod);
   } else 
   {
      prevMA = inReal[0];
      today = 1;
   }
   while( (today<=startIdx) )
   {
      prevMA = (((inReal[today++]-prevMA)*optInK_1)+prevMA);
   }
   outReal[0] = prevMA;
   outIdx = 1;
   while( (today<=endIdx) )
   {
      prevMA = (((inReal[today++]-prevMA)*optInK_1)+prevMA);
      outReal[outIdx++] = prevMA;
   }
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_EMA( int    startIdx,
                     int    endIdx,
                     const float inReal[],
                     int optInTimePeriod,
                     int          *outBegIdx,
                     int          *outNBElement,
                     double        outReal[] )
{
   double tempReal;
   double prevMA;
   int i;
   int today;
   int outIdx;
   int lookbackTotal;
   double optInK_1 = (2.0/((double)(optInTimePeriod+1)));

   if( startIdx < 0 )
      return TA_OUT_OF_RANGE_START_INDEX;
   if( (endIdx < 0) || (endIdx < startIdx) )
      return TA_OUT_OF_RANGE_END_INDEX;

   if( !inReal )
      return TA_BAD_PARAM;
   if( (int)optInTimePeriod == (int)0x80000000 )
      optInTimePeriod = 30;
   else if( (int)optInTimePeriod < 2 || (int)optInTimePeriod > 100000 )
      return TA_BAD_PARAM;
   if( !outReal )
      return TA_BAD_PARAM;

   lookbackTotal = TA_EMA_Lookback(optInTimePeriod);
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
   *outBegIdx= startIdx;
   if( (TA_GLOBALS_COMPATIBILITY==ENUM_VALUE(Compatibility,TA_COMPATIBILITY_DEFAULT,Default)) )
   {
      today = (startIdx-lookbackTotal);
      i = optInTimePeriod;
      tempReal = 0.0;
      while( (i-->0) )
      {
         tempReal += inReal[today++];
      }
      prevMA = (tempReal/optInTimePeriod);
   } else 
   {
      prevMA = inReal[0];
      today = 1;
   }
   while( (today<=startIdx) )
   {
      prevMA = (((inReal[today++]-prevMA)*optInK_1)+prevMA);
   }
   outReal[0] = prevMA;
   outIdx = 1;
   while( (today<=endIdx) )
   {
      prevMA = (((inReal[today++]-prevMA)*optInK_1)+prevMA);
      outReal[outIdx++] = prevMA;
   }
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

