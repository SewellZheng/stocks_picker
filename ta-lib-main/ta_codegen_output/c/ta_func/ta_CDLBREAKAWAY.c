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

TA_LIB_API int TA_CDLBREAKAWAY_Lookback( void )
{
   int BodyLong_rangeType = TA_Globals->candleSettings[TA_BodyLong].rangeType;
   int BodyLong_avgPeriod = TA_Globals->candleSettings[TA_BodyLong].avgPeriod;
   double BodyLong_factor = TA_Globals->candleSettings[TA_BodyLong].factor;
   return (BodyLong_avgPeriod+4);
}

TA_LIB_API TA_RetCode TA_CDLBREAKAWAY( int    startIdx,
                                       int    endIdx,
                                       const double inOpen[],
                                       const double inHigh[],
                                       const double inLow[],
                                       const double inClose[],
                                       int          *outBegIdx,
                                       int          *outNBElement,
                                       int        outInteger[] )
{
   double BodyLongPeriodTotal;
   int i;
   int outIdx;
   int BodyLongTrailingIdx;
   int lookbackTotal;
   int BodyLong_rangeType = TA_Globals->candleSettings[TA_BodyLong].rangeType;
   int BodyLong_avgPeriod = TA_Globals->candleSettings[TA_BodyLong].avgPeriod;
   double BodyLong_factor = TA_Globals->candleSettings[TA_BodyLong].factor;

   if( startIdx < 0 )
      return TA_OUT_OF_RANGE_START_INDEX;
   if( (endIdx < 0) || (endIdx < startIdx) )
      return TA_OUT_OF_RANGE_END_INDEX;

   if( !inOpen )
      return TA_BAD_PARAM;
   if( !inHigh )
      return TA_BAD_PARAM;
   if( !inLow )
      return TA_BAD_PARAM;
   if( !inClose )
      return TA_BAD_PARAM;
   if( !outInteger )
      return TA_BAD_PARAM;

   lookbackTotal = TA_CDLBREAKAWAY_Lookback();
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
   BodyLongPeriodTotal = 0;
   BodyLongTrailingIdx = (startIdx-BodyLong_avgPeriod);
   i = BodyLongTrailingIdx;
   while( (i<startIdx) )
   {
      BodyLongPeriodTotal += TA_CANDLERANGE(BodyLong,(i-4));
      i += 1;
   }
   i = startIdx;
   outIdx = 0;
   do
   {
      if( ((((((((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1)))==(((inClose[(i-3)]>=inOpen[(i-3)])) ? (1) : ((0-1))))&&((((inClose[(i-3)]>=inOpen[(i-3)])) ? (1) : ((0-1)))==(((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))))&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-(((inClose[i]>=inOpen[i])) ? (1) : ((0-1))))))&&(fabs((inClose[(i-4)]-inOpen[(i-4)]))>TA_CANDLEAVERAGE(BodyLong,BodyLongPeriodTotal,(i-4))))&&((((((((((((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1)))==(0-1))&&(((fmax(inOpen[(i-3)],inClose[(i-3)])<fmin(inOpen[(i-4)],inClose[(i-4)]))) ? (1) : (0)))&&(inHigh[(i-2)]<inHigh[(i-3)]))&&(inLow[(i-2)]<inLow[(i-3)]))&&(inHigh[(i-1)]<inHigh[(i-2)]))&&(inLow[(i-1)]<inLow[(i-2)]))&&(inClose[i]>inOpen[(i-3)]))&&(inClose[i]<inClose[(i-4)]))||(((((((((((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1)))==1)&&(((fmin(inOpen[(i-3)],inClose[(i-3)])>fmax(inOpen[(i-4)],inClose[(i-4)]))) ? (1) : (0)))&&(inHigh[(i-2)]>inHigh[(i-3)]))&&(inLow[(i-2)]>inLow[(i-3)]))&&(inHigh[(i-1)]>inHigh[(i-2)]))&&(inLow[(i-1)]>inLow[(i-2)]))&&(inClose[i]<inOpen[(i-3)]))&&(inClose[i]>inClose[(i-4)])))) )
      {
         outInteger[outIdx++] = ((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))*100);
      } else 
      {
         outInteger[outIdx++] = 0;
      }
      BodyLongPeriodTotal += (TA_CANDLERANGE(BodyLong,(i-4))-TA_CANDLERANGE(BodyLong,(BodyLongTrailingIdx-4)));
      i += 1;
      BodyLongTrailingIdx += 1;
   } while( (i<=endIdx) );
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_LIB_API TA_RetCode TA_CDLBREAKAWAY_Unguarded( int    startIdx,
                                                 int    endIdx,
                                                 const double inOpen[],
                                                 const double inHigh[],
                                                 const double inLow[],
                                                 const double inClose[],
                                                 int          *outBegIdx,
                                                 int          *outNBElement,
                                                 int        outInteger[] )
{
   double BodyLongPeriodTotal;
   int i;
   int outIdx;
   int BodyLongTrailingIdx;
   int lookbackTotal;
   int BodyLong_rangeType = TA_Globals->candleSettings[TA_BodyLong].rangeType;
   int BodyLong_avgPeriod = TA_Globals->candleSettings[TA_BodyLong].avgPeriod;
   double BodyLong_factor = TA_Globals->candleSettings[TA_BodyLong].factor;

   lookbackTotal = TA_CDLBREAKAWAY_Lookback();
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
   BodyLongPeriodTotal = 0;
   BodyLongTrailingIdx = (startIdx-BodyLong_avgPeriod);
   i = BodyLongTrailingIdx;
   while( (i<startIdx) )
   {
      BodyLongPeriodTotal += TA_CANDLERANGE(BodyLong,(i-4));
      i += 1;
   }
   i = startIdx;
   outIdx = 0;
   do
   {
      if( ((((((((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1)))==(((inClose[(i-3)]>=inOpen[(i-3)])) ? (1) : ((0-1))))&&((((inClose[(i-3)]>=inOpen[(i-3)])) ? (1) : ((0-1)))==(((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))))&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-(((inClose[i]>=inOpen[i])) ? (1) : ((0-1))))))&&(fabs((inClose[(i-4)]-inOpen[(i-4)]))>TA_CANDLEAVERAGE(BodyLong,BodyLongPeriodTotal,(i-4))))&&((((((((((((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1)))==(0-1))&&(((fmax(inOpen[(i-3)],inClose[(i-3)])<fmin(inOpen[(i-4)],inClose[(i-4)]))) ? (1) : (0)))&&(inHigh[(i-2)]<inHigh[(i-3)]))&&(inLow[(i-2)]<inLow[(i-3)]))&&(inHigh[(i-1)]<inHigh[(i-2)]))&&(inLow[(i-1)]<inLow[(i-2)]))&&(inClose[i]>inOpen[(i-3)]))&&(inClose[i]<inClose[(i-4)]))||(((((((((((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1)))==1)&&(((fmin(inOpen[(i-3)],inClose[(i-3)])>fmax(inOpen[(i-4)],inClose[(i-4)]))) ? (1) : (0)))&&(inHigh[(i-2)]>inHigh[(i-3)]))&&(inLow[(i-2)]>inLow[(i-3)]))&&(inHigh[(i-1)]>inHigh[(i-2)]))&&(inLow[(i-1)]>inLow[(i-2)]))&&(inClose[i]<inOpen[(i-3)]))&&(inClose[i]>inClose[(i-4)])))) )
      {
         outInteger[outIdx++] = ((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))*100);
      } else 
      {
         outInteger[outIdx++] = 0;
      }
      BodyLongPeriodTotal += (TA_CANDLERANGE(BodyLong,(i-4))-TA_CANDLERANGE(BodyLong,(BodyLongTrailingIdx-4)));
      i += 1;
      BodyLongTrailingIdx += 1;
   } while( (i<=endIdx) );
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_CDLBREAKAWAY( int    startIdx,
                              int    endIdx,
                              const float inOpen[],
                              const float inHigh[],
                              const float inLow[],
                              const float inClose[],
                              int          *outBegIdx,
                              int          *outNBElement,
                              int        outInteger[] )
{
   double BodyLongPeriodTotal;
   int i;
   int outIdx;
   int BodyLongTrailingIdx;
   int lookbackTotal;
   int BodyLong_rangeType = TA_Globals->candleSettings[TA_BodyLong].rangeType;
   int BodyLong_avgPeriod = TA_Globals->candleSettings[TA_BodyLong].avgPeriod;
   double BodyLong_factor = TA_Globals->candleSettings[TA_BodyLong].factor;

   if( startIdx < 0 )
      return TA_OUT_OF_RANGE_START_INDEX;
   if( (endIdx < 0) || (endIdx < startIdx) )
      return TA_OUT_OF_RANGE_END_INDEX;

   if( !inOpen )
      return TA_BAD_PARAM;
   if( !inHigh )
      return TA_BAD_PARAM;
   if( !inLow )
      return TA_BAD_PARAM;
   if( !inClose )
      return TA_BAD_PARAM;
   if( !outInteger )
      return TA_BAD_PARAM;

   lookbackTotal = TA_CDLBREAKAWAY_Lookback();
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
   BodyLongPeriodTotal = 0;
   BodyLongTrailingIdx = (startIdx-BodyLong_avgPeriod);
   i = BodyLongTrailingIdx;
   while( (i<startIdx) )
   {
      BodyLongPeriodTotal += TA_CANDLERANGE(BodyLong,(i-4));
      i += 1;
   }
   i = startIdx;
   outIdx = 0;
   do
   {
      if( ((((((((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1)))==(((inClose[(i-3)]>=inOpen[(i-3)])) ? (1) : ((0-1))))&&((((inClose[(i-3)]>=inOpen[(i-3)])) ? (1) : ((0-1)))==(((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))))&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-(((inClose[i]>=inOpen[i])) ? (1) : ((0-1))))))&&(fabs((inClose[(i-4)]-inOpen[(i-4)]))>TA_CANDLEAVERAGE(BodyLong,BodyLongPeriodTotal,(i-4))))&&((((((((((((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1)))==(0-1))&&(((fmax(inOpen[(i-3)],inClose[(i-3)])<fmin(inOpen[(i-4)],inClose[(i-4)]))) ? (1) : (0)))&&(inHigh[(i-2)]<inHigh[(i-3)]))&&(inLow[(i-2)]<inLow[(i-3)]))&&(inHigh[(i-1)]<inHigh[(i-2)]))&&(inLow[(i-1)]<inLow[(i-2)]))&&(inClose[i]>inOpen[(i-3)]))&&(inClose[i]<inClose[(i-4)]))||(((((((((((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1)))==1)&&(((fmin(inOpen[(i-3)],inClose[(i-3)])>fmax(inOpen[(i-4)],inClose[(i-4)]))) ? (1) : (0)))&&(inHigh[(i-2)]>inHigh[(i-3)]))&&(inLow[(i-2)]>inLow[(i-3)]))&&(inHigh[(i-1)]>inHigh[(i-2)]))&&(inLow[(i-1)]>inLow[(i-2)]))&&(inClose[i]<inOpen[(i-3)]))&&(inClose[i]>inClose[(i-4)])))) )
      {
         outInteger[outIdx++] = ((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))*100);
      } else 
      {
         outInteger[outIdx++] = 0;
      }
      BodyLongPeriodTotal += (TA_CANDLERANGE(BodyLong,(i-4))-TA_CANDLERANGE(BodyLong,(BodyLongTrailingIdx-4)));
      i += 1;
      BodyLongTrailingIdx += 1;
   } while( (i<=endIdx) );
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_CDLBREAKAWAY_Unguarded( int    startIdx,
                                        int    endIdx,
                                        const float inOpen[],
                                        const float inHigh[],
                                        const float inLow[],
                                        const float inClose[],
                                        int          *outBegIdx,
                                        int          *outNBElement,
                                        int        outInteger[] )
{
   double BodyLongPeriodTotal;
   int i;
   int outIdx;
   int BodyLongTrailingIdx;
   int lookbackTotal;
   int BodyLong_rangeType = TA_Globals->candleSettings[TA_BodyLong].rangeType;
   int BodyLong_avgPeriod = TA_Globals->candleSettings[TA_BodyLong].avgPeriod;
   double BodyLong_factor = TA_Globals->candleSettings[TA_BodyLong].factor;

   lookbackTotal = TA_CDLBREAKAWAY_Lookback();
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
   BodyLongPeriodTotal = 0;
   BodyLongTrailingIdx = (startIdx-BodyLong_avgPeriod);
   i = BodyLongTrailingIdx;
   while( (i<startIdx) )
   {
      BodyLongPeriodTotal += TA_CANDLERANGE(BodyLong,(i-4));
      i += 1;
   }
   i = startIdx;
   outIdx = 0;
   do
   {
      if( ((((((((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1)))==(((inClose[(i-3)]>=inOpen[(i-3)])) ? (1) : ((0-1))))&&((((inClose[(i-3)]>=inOpen[(i-3)])) ? (1) : ((0-1)))==(((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))))&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-(((inClose[i]>=inOpen[i])) ? (1) : ((0-1))))))&&(fabs((inClose[(i-4)]-inOpen[(i-4)]))>TA_CANDLEAVERAGE(BodyLong,BodyLongPeriodTotal,(i-4))))&&((((((((((((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1)))==(0-1))&&(((fmax(inOpen[(i-3)],inClose[(i-3)])<fmin(inOpen[(i-4)],inClose[(i-4)]))) ? (1) : (0)))&&(inHigh[(i-2)]<inHigh[(i-3)]))&&(inLow[(i-2)]<inLow[(i-3)]))&&(inHigh[(i-1)]<inHigh[(i-2)]))&&(inLow[(i-1)]<inLow[(i-2)]))&&(inClose[i]>inOpen[(i-3)]))&&(inClose[i]<inClose[(i-4)]))||(((((((((((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1)))==1)&&(((fmin(inOpen[(i-3)],inClose[(i-3)])>fmax(inOpen[(i-4)],inClose[(i-4)]))) ? (1) : (0)))&&(inHigh[(i-2)]>inHigh[(i-3)]))&&(inLow[(i-2)]>inLow[(i-3)]))&&(inHigh[(i-1)]>inHigh[(i-2)]))&&(inLow[(i-1)]>inLow[(i-2)]))&&(inClose[i]<inOpen[(i-3)]))&&(inClose[i]>inClose[(i-4)])))) )
      {
         outInteger[outIdx++] = ((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))*100);
      } else 
      {
         outInteger[outIdx++] = 0;
      }
      BodyLongPeriodTotal += (TA_CANDLERANGE(BodyLong,(i-4))-TA_CANDLERANGE(BodyLong,(BodyLongTrailingIdx-4)));
      i += 1;
      BodyLongTrailingIdx += 1;
   } while( (i<=endIdx) );
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

