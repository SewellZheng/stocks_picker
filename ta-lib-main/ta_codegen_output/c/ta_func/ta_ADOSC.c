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

TA_LIB_API int TA_ADOSC_Lookback( int optInFastPeriod, int optInSlowPeriod )
{
   int slowestPeriod;
   if( (optInFastPeriod<optInSlowPeriod) )
   {
      slowestPeriod = optInSlowPeriod;
   } else 
   {
      slowestPeriod = optInFastPeriod;
   }
   return TA_EMA_Lookback(slowestPeriod);
}

TA_LIB_API TA_RetCode TA_ADOSC( int    startIdx,
                                int    endIdx,
                                const double inHigh[],
                                const double inLow[],
                                const double inClose[],
                                const double inVolume[],
                                int optInFastPeriod,
                                int optInSlowPeriod,
                                int          *outBegIdx,
                                int          *outNBElement,
                                double        outReal[] )
{
   int today;
   int outIdx;
   int lookbackTotal;
   int slowestPeriod;
   double high;
   double low;
   double close;
   double tmp;
   double slowEMA;
   double slowk;
   double one_minus_slowk;
   double fastEMA;
   double fastk;
   double one_minus_fastk;
   double ad;

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
   if( !inVolume )
      return TA_BAD_PARAM;
   if( (int)optInFastPeriod == (int)0x80000000 )
      optInFastPeriod = 3;
   else if( (int)optInFastPeriod < 2 || (int)optInFastPeriod > 100000 )
      return TA_BAD_PARAM;
   if( (int)optInSlowPeriod == (int)0x80000000 )
      optInSlowPeriod = 10;
   else if( (int)optInSlowPeriod < 2 || (int)optInSlowPeriod > 100000 )
      return TA_BAD_PARAM;
   if( !outReal )
      return TA_BAD_PARAM;

   if( (optInFastPeriod<optInSlowPeriod) )
   {
      slowestPeriod = optInSlowPeriod;
   } else 
   {
      slowestPeriod = optInFastPeriod;
   }
   lookbackTotal = TA_EMA_Lookback(slowestPeriod);
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
   today = (startIdx-lookbackTotal);
   ad = 0.0;
   fastk = (2.0/(((double)optInFastPeriod)+1.0));
   one_minus_fastk = (1.0-fastk);
   slowk = (2.0/(((double)optInSlowPeriod)+1.0));
   one_minus_slowk = (1.0-slowk);
   high = inHigh[today];
   low = inLow[today];
   tmp = (high-low);
   close = inClose[today];
   if( (tmp>0.0) )
   {
      ad += ((((close-low)-(high-close))/tmp)*((double)inVolume[today]));
   }
   today += 1;
   fastEMA = ad;
   slowEMA = ad;
   while( (today<startIdx) )
   {
      high = inHigh[today];
      low = inLow[today];
      tmp = (high-low);
      close = inClose[today];
      if( (tmp>0.0) )
      {
         ad += ((((close-low)-(high-close))/tmp)*((double)inVolume[today]));
      }
      today += 1;
      fastEMA = ((fastk*ad)+(one_minus_fastk*fastEMA));
      slowEMA = ((slowk*ad)+(one_minus_slowk*slowEMA));
   }
   outIdx = 0;
   while( (today<=endIdx) )
   {
      high = inHigh[today];
      low = inLow[today];
      tmp = (high-low);
      close = inClose[today];
      if( (tmp>0.0) )
      {
         ad += ((((close-low)-(high-close))/tmp)*((double)inVolume[today]));
      }
      today += 1;
      fastEMA = ((fastk*ad)+(one_minus_fastk*fastEMA));
      slowEMA = ((slowk*ad)+(one_minus_slowk*slowEMA));
      outReal[outIdx++] = (fastEMA-slowEMA);
   }
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_LIB_API TA_RetCode TA_ADOSC_Unguarded( int    startIdx,
                                          int    endIdx,
                                          const double inHigh[],
                                          const double inLow[],
                                          const double inClose[],
                                          const double inVolume[],
                                          int optInFastPeriod,
                                          int optInSlowPeriod,
                                          int          *outBegIdx,
                                          int          *outNBElement,
                                          double        outReal[] )
{
   int today;
   int outIdx;
   int lookbackTotal;
   int slowestPeriod;
   double high;
   double low;
   double close;
   double tmp;
   double slowEMA;
   double slowk;
   double one_minus_slowk;
   double fastEMA;
   double fastk;
   double one_minus_fastk;
   double ad;

   if( (optInFastPeriod<optInSlowPeriod) )
   {
      slowestPeriod = optInSlowPeriod;
   } else 
   {
      slowestPeriod = optInFastPeriod;
   }
   lookbackTotal = TA_EMA_Lookback(slowestPeriod);
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
   today = (startIdx-lookbackTotal);
   ad = 0.0;
   fastk = (2.0/(((double)optInFastPeriod)+1.0));
   one_minus_fastk = (1.0-fastk);
   slowk = (2.0/(((double)optInSlowPeriod)+1.0));
   one_minus_slowk = (1.0-slowk);
   high = inHigh[today];
   low = inLow[today];
   tmp = (high-low);
   close = inClose[today];
   if( (tmp>0.0) )
   {
      ad += ((((close-low)-(high-close))/tmp)*((double)inVolume[today]));
   }
   today += 1;
   fastEMA = ad;
   slowEMA = ad;
   while( (today<startIdx) )
   {
      high = inHigh[today];
      low = inLow[today];
      tmp = (high-low);
      close = inClose[today];
      if( (tmp>0.0) )
      {
         ad += ((((close-low)-(high-close))/tmp)*((double)inVolume[today]));
      }
      today += 1;
      fastEMA = ((fastk*ad)+(one_minus_fastk*fastEMA));
      slowEMA = ((slowk*ad)+(one_minus_slowk*slowEMA));
   }
   outIdx = 0;
   while( (today<=endIdx) )
   {
      high = inHigh[today];
      low = inLow[today];
      tmp = (high-low);
      close = inClose[today];
      if( (tmp>0.0) )
      {
         ad += ((((close-low)-(high-close))/tmp)*((double)inVolume[today]));
      }
      today += 1;
      fastEMA = ((fastk*ad)+(one_minus_fastk*fastEMA));
      slowEMA = ((slowk*ad)+(one_minus_slowk*slowEMA));
      outReal[outIdx++] = (fastEMA-slowEMA);
   }
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_ADOSC( int    startIdx,
                       int    endIdx,
                       const float inHigh[],
                       const float inLow[],
                       const float inClose[],
                       const float inVolume[],
                       int optInFastPeriod,
                       int optInSlowPeriod,
                       int          *outBegIdx,
                       int          *outNBElement,
                       double        outReal[] )
{
   int today;
   int outIdx;
   int lookbackTotal;
   int slowestPeriod;
   double high;
   double low;
   double close;
   double tmp;
   double slowEMA;
   double slowk;
   double one_minus_slowk;
   double fastEMA;
   double fastk;
   double one_minus_fastk;
   double ad;

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
   if( !inVolume )
      return TA_BAD_PARAM;
   if( (int)optInFastPeriod == (int)0x80000000 )
      optInFastPeriod = 3;
   else if( (int)optInFastPeriod < 2 || (int)optInFastPeriod > 100000 )
      return TA_BAD_PARAM;
   if( (int)optInSlowPeriod == (int)0x80000000 )
      optInSlowPeriod = 10;
   else if( (int)optInSlowPeriod < 2 || (int)optInSlowPeriod > 100000 )
      return TA_BAD_PARAM;
   if( !outReal )
      return TA_BAD_PARAM;

   if( (optInFastPeriod<optInSlowPeriod) )
   {
      slowestPeriod = optInSlowPeriod;
   } else 
   {
      slowestPeriod = optInFastPeriod;
   }
   lookbackTotal = TA_EMA_Lookback(slowestPeriod);
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
   today = (startIdx-lookbackTotal);
   ad = 0.0;
   fastk = (2.0/(((double)optInFastPeriod)+1.0));
   one_minus_fastk = (1.0-fastk);
   slowk = (2.0/(((double)optInSlowPeriod)+1.0));
   one_minus_slowk = (1.0-slowk);
   high = inHigh[today];
   low = inLow[today];
   tmp = (high-low);
   close = inClose[today];
   if( (tmp>0.0) )
   {
      ad += ((((close-low)-(high-close))/tmp)*((double)inVolume[today]));
   }
   today += 1;
   fastEMA = ad;
   slowEMA = ad;
   while( (today<startIdx) )
   {
      high = inHigh[today];
      low = inLow[today];
      tmp = (high-low);
      close = inClose[today];
      if( (tmp>0.0) )
      {
         ad += ((((close-low)-(high-close))/tmp)*((double)inVolume[today]));
      }
      today += 1;
      fastEMA = ((fastk*ad)+(one_minus_fastk*fastEMA));
      slowEMA = ((slowk*ad)+(one_minus_slowk*slowEMA));
   }
   outIdx = 0;
   while( (today<=endIdx) )
   {
      high = inHigh[today];
      low = inLow[today];
      tmp = (high-low);
      close = inClose[today];
      if( (tmp>0.0) )
      {
         ad += ((((close-low)-(high-close))/tmp)*((double)inVolume[today]));
      }
      today += 1;
      fastEMA = ((fastk*ad)+(one_minus_fastk*fastEMA));
      slowEMA = ((slowk*ad)+(one_minus_slowk*slowEMA));
      outReal[outIdx++] = (fastEMA-slowEMA);
   }
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_ADOSC_Unguarded( int    startIdx,
                                 int    endIdx,
                                 const float inHigh[],
                                 const float inLow[],
                                 const float inClose[],
                                 const float inVolume[],
                                 int optInFastPeriod,
                                 int optInSlowPeriod,
                                 int          *outBegIdx,
                                 int          *outNBElement,
                                 double        outReal[] )
{
   int today;
   int outIdx;
   int lookbackTotal;
   int slowestPeriod;
   double high;
   double low;
   double close;
   double tmp;
   double slowEMA;
   double slowk;
   double one_minus_slowk;
   double fastEMA;
   double fastk;
   double one_minus_fastk;
   double ad;

   if( (optInFastPeriod<optInSlowPeriod) )
   {
      slowestPeriod = optInSlowPeriod;
   } else 
   {
      slowestPeriod = optInFastPeriod;
   }
   lookbackTotal = TA_EMA_Lookback(slowestPeriod);
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
   today = (startIdx-lookbackTotal);
   ad = 0.0;
   fastk = (2.0/(((double)optInFastPeriod)+1.0));
   one_minus_fastk = (1.0-fastk);
   slowk = (2.0/(((double)optInSlowPeriod)+1.0));
   one_minus_slowk = (1.0-slowk);
   high = inHigh[today];
   low = inLow[today];
   tmp = (high-low);
   close = inClose[today];
   if( (tmp>0.0) )
   {
      ad += ((((close-low)-(high-close))/tmp)*((double)inVolume[today]));
   }
   today += 1;
   fastEMA = ad;
   slowEMA = ad;
   while( (today<startIdx) )
   {
      high = inHigh[today];
      low = inLow[today];
      tmp = (high-low);
      close = inClose[today];
      if( (tmp>0.0) )
      {
         ad += ((((close-low)-(high-close))/tmp)*((double)inVolume[today]));
      }
      today += 1;
      fastEMA = ((fastk*ad)+(one_minus_fastk*fastEMA));
      slowEMA = ((slowk*ad)+(one_minus_slowk*slowEMA));
   }
   outIdx = 0;
   while( (today<=endIdx) )
   {
      high = inHigh[today];
      low = inLow[today];
      tmp = (high-low);
      close = inClose[today];
      if( (tmp>0.0) )
      {
         ad += ((((close-low)-(high-close))/tmp)*((double)inVolume[today]));
      }
      today += 1;
      fastEMA = ((fastk*ad)+(one_minus_fastk*fastEMA));
      slowEMA = ((slowk*ad)+(one_minus_slowk*slowEMA));
      outReal[outIdx++] = (fastEMA-slowEMA);
   }
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

