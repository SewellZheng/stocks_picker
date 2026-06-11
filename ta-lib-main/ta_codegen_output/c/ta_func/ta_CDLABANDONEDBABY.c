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

TA_LIB_API int TA_CDLABANDONEDBABY_Lookback( double optInPenetration )
{
   int BodyDoji_rangeType = TA_Globals->candleSettings[TA_BodyDoji].rangeType;
   int BodyDoji_avgPeriod = TA_Globals->candleSettings[TA_BodyDoji].avgPeriod;
   double BodyDoji_factor = TA_Globals->candleSettings[TA_BodyDoji].factor;
   int BodyLong_rangeType = TA_Globals->candleSettings[TA_BodyLong].rangeType;
   int BodyLong_avgPeriod = TA_Globals->candleSettings[TA_BodyLong].avgPeriod;
   double BodyLong_factor = TA_Globals->candleSettings[TA_BodyLong].factor;
   int BodyShort_rangeType = TA_Globals->candleSettings[TA_BodyShort].rangeType;
   int BodyShort_avgPeriod = TA_Globals->candleSettings[TA_BodyShort].avgPeriod;
   double BodyShort_factor = TA_Globals->candleSettings[TA_BodyShort].factor;
   return (fmax(fmax(BodyDoji_avgPeriod,BodyLong_avgPeriod),BodyShort_avgPeriod)+2);
}

TA_LIB_API TA_RetCode TA_CDLABANDONEDBABY( int    startIdx,
                                           int    endIdx,
                                           const double inOpen[],
                                           const double inHigh[],
                                           const double inLow[],
                                           const double inClose[],
                                           double optInPenetration,
                                           int          *outBegIdx,
                                           int          *outNBElement,
                                           int        outInteger[] )
{
   double BodyDojiPeriodTotal;
   double BodyLongPeriodTotal;
   double BodyShortPeriodTotal;
   int i;
   int outIdx;
   int BodyDojiTrailingIdx;
   int BodyLongTrailingIdx;
   int BodyShortTrailingIdx;
   int lookbackTotal;
   int BodyDoji_rangeType = TA_Globals->candleSettings[TA_BodyDoji].rangeType;
   int BodyDoji_avgPeriod = TA_Globals->candleSettings[TA_BodyDoji].avgPeriod;
   double BodyDoji_factor = TA_Globals->candleSettings[TA_BodyDoji].factor;
   int BodyLong_rangeType = TA_Globals->candleSettings[TA_BodyLong].rangeType;
   int BodyLong_avgPeriod = TA_Globals->candleSettings[TA_BodyLong].avgPeriod;
   double BodyLong_factor = TA_Globals->candleSettings[TA_BodyLong].factor;
   int BodyShort_rangeType = TA_Globals->candleSettings[TA_BodyShort].rangeType;
   int BodyShort_avgPeriod = TA_Globals->candleSettings[TA_BodyShort].avgPeriod;
   double BodyShort_factor = TA_Globals->candleSettings[TA_BodyShort].factor;

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
   if( optInPenetration == -4e37 )
      optInPenetration = 0.3;
   else if( optInPenetration < 0e0 || optInPenetration > 1.7976931348623157e308 )
      return TA_BAD_PARAM;
   if( !outInteger )
      return TA_BAD_PARAM;

   lookbackTotal = TA_CDLABANDONEDBABY_Lookback(optInPenetration);
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
   BodyDojiPeriodTotal = 0;
   BodyShortPeriodTotal = 0;
   BodyLongTrailingIdx = ((startIdx-2)-BodyLong_avgPeriod);
   BodyDojiTrailingIdx = ((startIdx-1)-BodyDoji_avgPeriod);
   BodyShortTrailingIdx = (startIdx-BodyShort_avgPeriod);
   i = BodyLongTrailingIdx;
   while( (i<(startIdx-2)) )
   {
      BodyLongPeriodTotal += TA_CANDLERANGE(BodyLong,i);
      i += 1;
   }
   i = BodyDojiTrailingIdx;
   while( (i<(startIdx-1)) )
   {
      BodyDojiPeriodTotal += TA_CANDLERANGE(BodyDoji,i);
      i += 1;
   }
   i = BodyShortTrailingIdx;
   while( (i<startIdx) )
   {
      BodyShortPeriodTotal += TA_CANDLERANGE(BodyShort,i);
      i += 1;
   }
   i = startIdx;
   outIdx = 0;
   do
   {
      if( ((((fabs((inClose[(i-2)]-inOpen[(i-2)]))>TA_CANDLEAVERAGE(BodyLong,BodyLongPeriodTotal,(i-2)))&&(fabs((inClose[(i-1)]-inOpen[(i-1)]))<=TA_CANDLEAVERAGE(BodyDoji,BodyDojiPeriodTotal,(i-1))))&&(fabs((inClose[i]-inOpen[i]))>TA_CANDLEAVERAGE(BodyShort,BodyShortPeriodTotal,i)))&&(((((((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==1)&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==(0-1)))&&(inClose[i]<(inClose[(i-2)]-(fabs((inClose[(i-2)]-inOpen[(i-2)]))*optInPenetration))))&&(((inLow[(i-1)]>inHigh[(i-2)])) ? (1) : (0)))&&(((inHigh[i]<inLow[(i-1)])) ? (1) : (0)))||((((((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==(0-1))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==1))&&(inClose[i]>(inClose[(i-2)]+(fabs((inClose[(i-2)]-inOpen[(i-2)]))*optInPenetration))))&&(((inHigh[(i-1)]<inLow[(i-2)])) ? (1) : (0)))&&(((inLow[i]>inHigh[(i-1)])) ? (1) : (0))))) )
      {
         outInteger[outIdx++] = ((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))*100);
      } else 
      {
         outInteger[outIdx++] = 0;
      }
      BodyLongPeriodTotal += (TA_CANDLERANGE(BodyLong,(i-2))-TA_CANDLERANGE(BodyLong,BodyLongTrailingIdx));
      BodyDojiPeriodTotal += (TA_CANDLERANGE(BodyDoji,(i-1))-TA_CANDLERANGE(BodyDoji,BodyDojiTrailingIdx));
      BodyShortPeriodTotal += (TA_CANDLERANGE(BodyShort,i)-TA_CANDLERANGE(BodyShort,BodyShortTrailingIdx));
      i += 1;
      BodyLongTrailingIdx += 1;
      BodyDojiTrailingIdx += 1;
      BodyShortTrailingIdx += 1;
   } while( (i<=endIdx) );
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_LIB_API TA_RetCode TA_CDLABANDONEDBABY_Unguarded( int    startIdx,
                                                     int    endIdx,
                                                     const double inOpen[],
                                                     const double inHigh[],
                                                     const double inLow[],
                                                     const double inClose[],
                                                     double optInPenetration,
                                                     int          *outBegIdx,
                                                     int          *outNBElement,
                                                     int        outInteger[] )
{
   double BodyDojiPeriodTotal;
   double BodyLongPeriodTotal;
   double BodyShortPeriodTotal;
   int i;
   int outIdx;
   int BodyDojiTrailingIdx;
   int BodyLongTrailingIdx;
   int BodyShortTrailingIdx;
   int lookbackTotal;
   int BodyDoji_rangeType = TA_Globals->candleSettings[TA_BodyDoji].rangeType;
   int BodyDoji_avgPeriod = TA_Globals->candleSettings[TA_BodyDoji].avgPeriod;
   double BodyDoji_factor = TA_Globals->candleSettings[TA_BodyDoji].factor;
   int BodyLong_rangeType = TA_Globals->candleSettings[TA_BodyLong].rangeType;
   int BodyLong_avgPeriod = TA_Globals->candleSettings[TA_BodyLong].avgPeriod;
   double BodyLong_factor = TA_Globals->candleSettings[TA_BodyLong].factor;
   int BodyShort_rangeType = TA_Globals->candleSettings[TA_BodyShort].rangeType;
   int BodyShort_avgPeriod = TA_Globals->candleSettings[TA_BodyShort].avgPeriod;
   double BodyShort_factor = TA_Globals->candleSettings[TA_BodyShort].factor;

   lookbackTotal = TA_CDLABANDONEDBABY_Lookback(optInPenetration);
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
   BodyDojiPeriodTotal = 0;
   BodyShortPeriodTotal = 0;
   BodyLongTrailingIdx = ((startIdx-2)-BodyLong_avgPeriod);
   BodyDojiTrailingIdx = ((startIdx-1)-BodyDoji_avgPeriod);
   BodyShortTrailingIdx = (startIdx-BodyShort_avgPeriod);
   i = BodyLongTrailingIdx;
   while( (i<(startIdx-2)) )
   {
      BodyLongPeriodTotal += TA_CANDLERANGE(BodyLong,i);
      i += 1;
   }
   i = BodyDojiTrailingIdx;
   while( (i<(startIdx-1)) )
   {
      BodyDojiPeriodTotal += TA_CANDLERANGE(BodyDoji,i);
      i += 1;
   }
   i = BodyShortTrailingIdx;
   while( (i<startIdx) )
   {
      BodyShortPeriodTotal += TA_CANDLERANGE(BodyShort,i);
      i += 1;
   }
   i = startIdx;
   outIdx = 0;
   do
   {
      if( ((((fabs((inClose[(i-2)]-inOpen[(i-2)]))>TA_CANDLEAVERAGE(BodyLong,BodyLongPeriodTotal,(i-2)))&&(fabs((inClose[(i-1)]-inOpen[(i-1)]))<=TA_CANDLEAVERAGE(BodyDoji,BodyDojiPeriodTotal,(i-1))))&&(fabs((inClose[i]-inOpen[i]))>TA_CANDLEAVERAGE(BodyShort,BodyShortPeriodTotal,i)))&&(((((((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==1)&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==(0-1)))&&(inClose[i]<(inClose[(i-2)]-(fabs((inClose[(i-2)]-inOpen[(i-2)]))*optInPenetration))))&&(((inLow[(i-1)]>inHigh[(i-2)])) ? (1) : (0)))&&(((inHigh[i]<inLow[(i-1)])) ? (1) : (0)))||((((((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==(0-1))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==1))&&(inClose[i]>(inClose[(i-2)]+(fabs((inClose[(i-2)]-inOpen[(i-2)]))*optInPenetration))))&&(((inHigh[(i-1)]<inLow[(i-2)])) ? (1) : (0)))&&(((inLow[i]>inHigh[(i-1)])) ? (1) : (0))))) )
      {
         outInteger[outIdx++] = ((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))*100);
      } else 
      {
         outInteger[outIdx++] = 0;
      }
      BodyLongPeriodTotal += (TA_CANDLERANGE(BodyLong,(i-2))-TA_CANDLERANGE(BodyLong,BodyLongTrailingIdx));
      BodyDojiPeriodTotal += (TA_CANDLERANGE(BodyDoji,(i-1))-TA_CANDLERANGE(BodyDoji,BodyDojiTrailingIdx));
      BodyShortPeriodTotal += (TA_CANDLERANGE(BodyShort,i)-TA_CANDLERANGE(BodyShort,BodyShortTrailingIdx));
      i += 1;
      BodyLongTrailingIdx += 1;
      BodyDojiTrailingIdx += 1;
      BodyShortTrailingIdx += 1;
   } while( (i<=endIdx) );
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_CDLABANDONEDBABY( int    startIdx,
                                  int    endIdx,
                                  const float inOpen[],
                                  const float inHigh[],
                                  const float inLow[],
                                  const float inClose[],
                                  double optInPenetration,
                                  int          *outBegIdx,
                                  int          *outNBElement,
                                  int        outInteger[] )
{
   double BodyDojiPeriodTotal;
   double BodyLongPeriodTotal;
   double BodyShortPeriodTotal;
   int i;
   int outIdx;
   int BodyDojiTrailingIdx;
   int BodyLongTrailingIdx;
   int BodyShortTrailingIdx;
   int lookbackTotal;
   int BodyDoji_rangeType = TA_Globals->candleSettings[TA_BodyDoji].rangeType;
   int BodyDoji_avgPeriod = TA_Globals->candleSettings[TA_BodyDoji].avgPeriod;
   double BodyDoji_factor = TA_Globals->candleSettings[TA_BodyDoji].factor;
   int BodyLong_rangeType = TA_Globals->candleSettings[TA_BodyLong].rangeType;
   int BodyLong_avgPeriod = TA_Globals->candleSettings[TA_BodyLong].avgPeriod;
   double BodyLong_factor = TA_Globals->candleSettings[TA_BodyLong].factor;
   int BodyShort_rangeType = TA_Globals->candleSettings[TA_BodyShort].rangeType;
   int BodyShort_avgPeriod = TA_Globals->candleSettings[TA_BodyShort].avgPeriod;
   double BodyShort_factor = TA_Globals->candleSettings[TA_BodyShort].factor;

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
   if( optInPenetration == -4e37 )
      optInPenetration = 0.3;
   else if( optInPenetration < 0e0 || optInPenetration > 1.7976931348623157e308 )
      return TA_BAD_PARAM;
   if( !outInteger )
      return TA_BAD_PARAM;

   lookbackTotal = TA_CDLABANDONEDBABY_Lookback(optInPenetration);
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
   BodyDojiPeriodTotal = 0;
   BodyShortPeriodTotal = 0;
   BodyLongTrailingIdx = ((startIdx-2)-BodyLong_avgPeriod);
   BodyDojiTrailingIdx = ((startIdx-1)-BodyDoji_avgPeriod);
   BodyShortTrailingIdx = (startIdx-BodyShort_avgPeriod);
   i = BodyLongTrailingIdx;
   while( (i<(startIdx-2)) )
   {
      BodyLongPeriodTotal += TA_CANDLERANGE(BodyLong,i);
      i += 1;
   }
   i = BodyDojiTrailingIdx;
   while( (i<(startIdx-1)) )
   {
      BodyDojiPeriodTotal += TA_CANDLERANGE(BodyDoji,i);
      i += 1;
   }
   i = BodyShortTrailingIdx;
   while( (i<startIdx) )
   {
      BodyShortPeriodTotal += TA_CANDLERANGE(BodyShort,i);
      i += 1;
   }
   i = startIdx;
   outIdx = 0;
   do
   {
      if( ((((fabs((inClose[(i-2)]-inOpen[(i-2)]))>TA_CANDLEAVERAGE(BodyLong,BodyLongPeriodTotal,(i-2)))&&(fabs((inClose[(i-1)]-inOpen[(i-1)]))<=TA_CANDLEAVERAGE(BodyDoji,BodyDojiPeriodTotal,(i-1))))&&(fabs((inClose[i]-inOpen[i]))>TA_CANDLEAVERAGE(BodyShort,BodyShortPeriodTotal,i)))&&(((((((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==1)&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==(0-1)))&&(inClose[i]<(inClose[(i-2)]-(fabs((inClose[(i-2)]-inOpen[(i-2)]))*optInPenetration))))&&(((inLow[(i-1)]>inHigh[(i-2)])) ? (1) : (0)))&&(((inHigh[i]<inLow[(i-1)])) ? (1) : (0)))||((((((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==(0-1))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==1))&&(inClose[i]>(inClose[(i-2)]+(fabs((inClose[(i-2)]-inOpen[(i-2)]))*optInPenetration))))&&(((inHigh[(i-1)]<inLow[(i-2)])) ? (1) : (0)))&&(((inLow[i]>inHigh[(i-1)])) ? (1) : (0))))) )
      {
         outInteger[outIdx++] = ((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))*100);
      } else 
      {
         outInteger[outIdx++] = 0;
      }
      BodyLongPeriodTotal += (TA_CANDLERANGE(BodyLong,(i-2))-TA_CANDLERANGE(BodyLong,BodyLongTrailingIdx));
      BodyDojiPeriodTotal += (TA_CANDLERANGE(BodyDoji,(i-1))-TA_CANDLERANGE(BodyDoji,BodyDojiTrailingIdx));
      BodyShortPeriodTotal += (TA_CANDLERANGE(BodyShort,i)-TA_CANDLERANGE(BodyShort,BodyShortTrailingIdx));
      i += 1;
      BodyLongTrailingIdx += 1;
      BodyDojiTrailingIdx += 1;
      BodyShortTrailingIdx += 1;
   } while( (i<=endIdx) );
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_CDLABANDONEDBABY_Unguarded( int    startIdx,
                                            int    endIdx,
                                            const float inOpen[],
                                            const float inHigh[],
                                            const float inLow[],
                                            const float inClose[],
                                            double optInPenetration,
                                            int          *outBegIdx,
                                            int          *outNBElement,
                                            int        outInteger[] )
{
   double BodyDojiPeriodTotal;
   double BodyLongPeriodTotal;
   double BodyShortPeriodTotal;
   int i;
   int outIdx;
   int BodyDojiTrailingIdx;
   int BodyLongTrailingIdx;
   int BodyShortTrailingIdx;
   int lookbackTotal;
   int BodyDoji_rangeType = TA_Globals->candleSettings[TA_BodyDoji].rangeType;
   int BodyDoji_avgPeriod = TA_Globals->candleSettings[TA_BodyDoji].avgPeriod;
   double BodyDoji_factor = TA_Globals->candleSettings[TA_BodyDoji].factor;
   int BodyLong_rangeType = TA_Globals->candleSettings[TA_BodyLong].rangeType;
   int BodyLong_avgPeriod = TA_Globals->candleSettings[TA_BodyLong].avgPeriod;
   double BodyLong_factor = TA_Globals->candleSettings[TA_BodyLong].factor;
   int BodyShort_rangeType = TA_Globals->candleSettings[TA_BodyShort].rangeType;
   int BodyShort_avgPeriod = TA_Globals->candleSettings[TA_BodyShort].avgPeriod;
   double BodyShort_factor = TA_Globals->candleSettings[TA_BodyShort].factor;

   lookbackTotal = TA_CDLABANDONEDBABY_Lookback(optInPenetration);
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
   BodyDojiPeriodTotal = 0;
   BodyShortPeriodTotal = 0;
   BodyLongTrailingIdx = ((startIdx-2)-BodyLong_avgPeriod);
   BodyDojiTrailingIdx = ((startIdx-1)-BodyDoji_avgPeriod);
   BodyShortTrailingIdx = (startIdx-BodyShort_avgPeriod);
   i = BodyLongTrailingIdx;
   while( (i<(startIdx-2)) )
   {
      BodyLongPeriodTotal += TA_CANDLERANGE(BodyLong,i);
      i += 1;
   }
   i = BodyDojiTrailingIdx;
   while( (i<(startIdx-1)) )
   {
      BodyDojiPeriodTotal += TA_CANDLERANGE(BodyDoji,i);
      i += 1;
   }
   i = BodyShortTrailingIdx;
   while( (i<startIdx) )
   {
      BodyShortPeriodTotal += TA_CANDLERANGE(BodyShort,i);
      i += 1;
   }
   i = startIdx;
   outIdx = 0;
   do
   {
      if( ((((fabs((inClose[(i-2)]-inOpen[(i-2)]))>TA_CANDLEAVERAGE(BodyLong,BodyLongPeriodTotal,(i-2)))&&(fabs((inClose[(i-1)]-inOpen[(i-1)]))<=TA_CANDLEAVERAGE(BodyDoji,BodyDojiPeriodTotal,(i-1))))&&(fabs((inClose[i]-inOpen[i]))>TA_CANDLEAVERAGE(BodyShort,BodyShortPeriodTotal,i)))&&(((((((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==1)&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==(0-1)))&&(inClose[i]<(inClose[(i-2)]-(fabs((inClose[(i-2)]-inOpen[(i-2)]))*optInPenetration))))&&(((inLow[(i-1)]>inHigh[(i-2)])) ? (1) : (0)))&&(((inHigh[i]<inLow[(i-1)])) ? (1) : (0)))||((((((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==(0-1))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==1))&&(inClose[i]>(inClose[(i-2)]+(fabs((inClose[(i-2)]-inOpen[(i-2)]))*optInPenetration))))&&(((inHigh[(i-1)]<inLow[(i-2)])) ? (1) : (0)))&&(((inLow[i]>inHigh[(i-1)])) ? (1) : (0))))) )
      {
         outInteger[outIdx++] = ((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))*100);
      } else 
      {
         outInteger[outIdx++] = 0;
      }
      BodyLongPeriodTotal += (TA_CANDLERANGE(BodyLong,(i-2))-TA_CANDLERANGE(BodyLong,BodyLongTrailingIdx));
      BodyDojiPeriodTotal += (TA_CANDLERANGE(BodyDoji,(i-1))-TA_CANDLERANGE(BodyDoji,BodyDojiTrailingIdx));
      BodyShortPeriodTotal += (TA_CANDLERANGE(BodyShort,i)-TA_CANDLERANGE(BodyShort,BodyShortTrailingIdx));
      i += 1;
      BodyLongTrailingIdx += 1;
      BodyDojiTrailingIdx += 1;
      BodyShortTrailingIdx += 1;
   } while( (i<=endIdx) );
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

