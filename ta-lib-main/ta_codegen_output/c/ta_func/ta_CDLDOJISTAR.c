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

TA_LIB_API int TA_CDLDOJISTAR_Lookback( void )
{
   int BodyDoji_rangeType = TA_Globals->candleSettings[TA_BodyDoji].rangeType;
   int BodyDoji_avgPeriod = TA_Globals->candleSettings[TA_BodyDoji].avgPeriod;
   double BodyDoji_factor = TA_Globals->candleSettings[TA_BodyDoji].factor;
   int BodyLong_rangeType = TA_Globals->candleSettings[TA_BodyLong].rangeType;
   int BodyLong_avgPeriod = TA_Globals->candleSettings[TA_BodyLong].avgPeriod;
   double BodyLong_factor = TA_Globals->candleSettings[TA_BodyLong].factor;
   return (fmax(BodyDoji_avgPeriod,BodyLong_avgPeriod)+1);
}

TA_LIB_API TA_RetCode TA_CDLDOJISTAR( int    startIdx,
                                      int    endIdx,
                                      const double inOpen[],
                                      const double inHigh[],
                                      const double inLow[],
                                      const double inClose[],
                                      int          *outBegIdx,
                                      int          *outNBElement,
                                      int        outInteger[] )
{
   double BodyDojiPeriodTotal;
   double BodyLongPeriodTotal;
   int i;
   int outIdx;
   int BodyDojiTrailingIdx;
   int BodyLongTrailingIdx;
   int lookbackTotal;
   int BodyDoji_rangeType = TA_Globals->candleSettings[TA_BodyDoji].rangeType;
   int BodyDoji_avgPeriod = TA_Globals->candleSettings[TA_BodyDoji].avgPeriod;
   double BodyDoji_factor = TA_Globals->candleSettings[TA_BodyDoji].factor;
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

   lookbackTotal = TA_CDLDOJISTAR_Lookback();
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
   BodyLongTrailingIdx = ((startIdx-1)-BodyLong_avgPeriod);
   BodyDojiTrailingIdx = (startIdx-BodyDoji_avgPeriod);
   i = BodyLongTrailingIdx;
   while( (i<(startIdx-1)) )
   {
      BodyLongPeriodTotal += TA_CANDLERANGE(BodyLong,i);
      i += 1;
   }
   i = BodyDojiTrailingIdx;
   while( (i<startIdx) )
   {
      BodyDojiPeriodTotal += TA_CANDLERANGE(BodyDoji,i);
      i += 1;
   }
   outIdx = 0;
   do
   {
      if( (((fabs((inClose[(i-1)]-inOpen[(i-1)]))>TA_CANDLEAVERAGE(BodyLong,BodyLongPeriodTotal,(i-1)))&&(fabs((inClose[i]-inOpen[i]))<=TA_CANDLEAVERAGE(BodyDoji,BodyDojiPeriodTotal,i)))&&((((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==1)&&(((fmin(inOpen[i],inClose[i])>fmax(inOpen[(i-1)],inClose[(i-1)]))) ? (1) : (0)))||(((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-1))&&(((fmax(inOpen[i],inClose[i])<fmin(inOpen[(i-1)],inClose[(i-1)]))) ? (1) : (0))))) )
      {
         outInteger[outIdx++] = ((0-(((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1))))*100);
      } else 
      {
         outInteger[outIdx++] = 0;
      }
      BodyLongPeriodTotal += (TA_CANDLERANGE(BodyLong,(i-1))-TA_CANDLERANGE(BodyLong,BodyLongTrailingIdx));
      BodyDojiPeriodTotal += (TA_CANDLERANGE(BodyDoji,i)-TA_CANDLERANGE(BodyDoji,BodyDojiTrailingIdx));
      i += 1;
      BodyLongTrailingIdx += 1;
      BodyDojiTrailingIdx += 1;
   } while( (i<=endIdx) );
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_LIB_API TA_RetCode TA_CDLDOJISTAR_Unguarded( int    startIdx,
                                                int    endIdx,
                                                const double inOpen[],
                                                const double inHigh[],
                                                const double inLow[],
                                                const double inClose[],
                                                int          *outBegIdx,
                                                int          *outNBElement,
                                                int        outInteger[] )
{
   double BodyDojiPeriodTotal;
   double BodyLongPeriodTotal;
   int i;
   int outIdx;
   int BodyDojiTrailingIdx;
   int BodyLongTrailingIdx;
   int lookbackTotal;
   int BodyDoji_rangeType = TA_Globals->candleSettings[TA_BodyDoji].rangeType;
   int BodyDoji_avgPeriod = TA_Globals->candleSettings[TA_BodyDoji].avgPeriod;
   double BodyDoji_factor = TA_Globals->candleSettings[TA_BodyDoji].factor;
   int BodyLong_rangeType = TA_Globals->candleSettings[TA_BodyLong].rangeType;
   int BodyLong_avgPeriod = TA_Globals->candleSettings[TA_BodyLong].avgPeriod;
   double BodyLong_factor = TA_Globals->candleSettings[TA_BodyLong].factor;

   lookbackTotal = TA_CDLDOJISTAR_Lookback();
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
   BodyLongTrailingIdx = ((startIdx-1)-BodyLong_avgPeriod);
   BodyDojiTrailingIdx = (startIdx-BodyDoji_avgPeriod);
   i = BodyLongTrailingIdx;
   while( (i<(startIdx-1)) )
   {
      BodyLongPeriodTotal += TA_CANDLERANGE(BodyLong,i);
      i += 1;
   }
   i = BodyDojiTrailingIdx;
   while( (i<startIdx) )
   {
      BodyDojiPeriodTotal += TA_CANDLERANGE(BodyDoji,i);
      i += 1;
   }
   outIdx = 0;
   do
   {
      if( (((fabs((inClose[(i-1)]-inOpen[(i-1)]))>TA_CANDLEAVERAGE(BodyLong,BodyLongPeriodTotal,(i-1)))&&(fabs((inClose[i]-inOpen[i]))<=TA_CANDLEAVERAGE(BodyDoji,BodyDojiPeriodTotal,i)))&&((((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==1)&&(((fmin(inOpen[i],inClose[i])>fmax(inOpen[(i-1)],inClose[(i-1)]))) ? (1) : (0)))||(((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-1))&&(((fmax(inOpen[i],inClose[i])<fmin(inOpen[(i-1)],inClose[(i-1)]))) ? (1) : (0))))) )
      {
         outInteger[outIdx++] = ((0-(((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1))))*100);
      } else 
      {
         outInteger[outIdx++] = 0;
      }
      BodyLongPeriodTotal += (TA_CANDLERANGE(BodyLong,(i-1))-TA_CANDLERANGE(BodyLong,BodyLongTrailingIdx));
      BodyDojiPeriodTotal += (TA_CANDLERANGE(BodyDoji,i)-TA_CANDLERANGE(BodyDoji,BodyDojiTrailingIdx));
      i += 1;
      BodyLongTrailingIdx += 1;
      BodyDojiTrailingIdx += 1;
   } while( (i<=endIdx) );
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_CDLDOJISTAR( int    startIdx,
                             int    endIdx,
                             const float inOpen[],
                             const float inHigh[],
                             const float inLow[],
                             const float inClose[],
                             int          *outBegIdx,
                             int          *outNBElement,
                             int        outInteger[] )
{
   double BodyDojiPeriodTotal;
   double BodyLongPeriodTotal;
   int i;
   int outIdx;
   int BodyDojiTrailingIdx;
   int BodyLongTrailingIdx;
   int lookbackTotal;
   int BodyDoji_rangeType = TA_Globals->candleSettings[TA_BodyDoji].rangeType;
   int BodyDoji_avgPeriod = TA_Globals->candleSettings[TA_BodyDoji].avgPeriod;
   double BodyDoji_factor = TA_Globals->candleSettings[TA_BodyDoji].factor;
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

   lookbackTotal = TA_CDLDOJISTAR_Lookback();
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
   BodyLongTrailingIdx = ((startIdx-1)-BodyLong_avgPeriod);
   BodyDojiTrailingIdx = (startIdx-BodyDoji_avgPeriod);
   i = BodyLongTrailingIdx;
   while( (i<(startIdx-1)) )
   {
      BodyLongPeriodTotal += TA_CANDLERANGE(BodyLong,i);
      i += 1;
   }
   i = BodyDojiTrailingIdx;
   while( (i<startIdx) )
   {
      BodyDojiPeriodTotal += TA_CANDLERANGE(BodyDoji,i);
      i += 1;
   }
   outIdx = 0;
   do
   {
      if( (((fabs((inClose[(i-1)]-inOpen[(i-1)]))>TA_CANDLEAVERAGE(BodyLong,BodyLongPeriodTotal,(i-1)))&&(fabs((inClose[i]-inOpen[i]))<=TA_CANDLEAVERAGE(BodyDoji,BodyDojiPeriodTotal,i)))&&((((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==1)&&(((fmin(inOpen[i],inClose[i])>fmax(inOpen[(i-1)],inClose[(i-1)]))) ? (1) : (0)))||(((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-1))&&(((fmax(inOpen[i],inClose[i])<fmin(inOpen[(i-1)],inClose[(i-1)]))) ? (1) : (0))))) )
      {
         outInteger[outIdx++] = ((0-(((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1))))*100);
      } else 
      {
         outInteger[outIdx++] = 0;
      }
      BodyLongPeriodTotal += (TA_CANDLERANGE(BodyLong,(i-1))-TA_CANDLERANGE(BodyLong,BodyLongTrailingIdx));
      BodyDojiPeriodTotal += (TA_CANDLERANGE(BodyDoji,i)-TA_CANDLERANGE(BodyDoji,BodyDojiTrailingIdx));
      i += 1;
      BodyLongTrailingIdx += 1;
      BodyDojiTrailingIdx += 1;
   } while( (i<=endIdx) );
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_CDLDOJISTAR_Unguarded( int    startIdx,
                                       int    endIdx,
                                       const float inOpen[],
                                       const float inHigh[],
                                       const float inLow[],
                                       const float inClose[],
                                       int          *outBegIdx,
                                       int          *outNBElement,
                                       int        outInteger[] )
{
   double BodyDojiPeriodTotal;
   double BodyLongPeriodTotal;
   int i;
   int outIdx;
   int BodyDojiTrailingIdx;
   int BodyLongTrailingIdx;
   int lookbackTotal;
   int BodyDoji_rangeType = TA_Globals->candleSettings[TA_BodyDoji].rangeType;
   int BodyDoji_avgPeriod = TA_Globals->candleSettings[TA_BodyDoji].avgPeriod;
   double BodyDoji_factor = TA_Globals->candleSettings[TA_BodyDoji].factor;
   int BodyLong_rangeType = TA_Globals->candleSettings[TA_BodyLong].rangeType;
   int BodyLong_avgPeriod = TA_Globals->candleSettings[TA_BodyLong].avgPeriod;
   double BodyLong_factor = TA_Globals->candleSettings[TA_BodyLong].factor;

   lookbackTotal = TA_CDLDOJISTAR_Lookback();
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
   BodyLongTrailingIdx = ((startIdx-1)-BodyLong_avgPeriod);
   BodyDojiTrailingIdx = (startIdx-BodyDoji_avgPeriod);
   i = BodyLongTrailingIdx;
   while( (i<(startIdx-1)) )
   {
      BodyLongPeriodTotal += TA_CANDLERANGE(BodyLong,i);
      i += 1;
   }
   i = BodyDojiTrailingIdx;
   while( (i<startIdx) )
   {
      BodyDojiPeriodTotal += TA_CANDLERANGE(BodyDoji,i);
      i += 1;
   }
   outIdx = 0;
   do
   {
      if( (((fabs((inClose[(i-1)]-inOpen[(i-1)]))>TA_CANDLEAVERAGE(BodyLong,BodyLongPeriodTotal,(i-1)))&&(fabs((inClose[i]-inOpen[i]))<=TA_CANDLEAVERAGE(BodyDoji,BodyDojiPeriodTotal,i)))&&((((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==1)&&(((fmin(inOpen[i],inClose[i])>fmax(inOpen[(i-1)],inClose[(i-1)]))) ? (1) : (0)))||(((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-1))&&(((fmax(inOpen[i],inClose[i])<fmin(inOpen[(i-1)],inClose[(i-1)]))) ? (1) : (0))))) )
      {
         outInteger[outIdx++] = ((0-(((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1))))*100);
      } else 
      {
         outInteger[outIdx++] = 0;
      }
      BodyLongPeriodTotal += (TA_CANDLERANGE(BodyLong,(i-1))-TA_CANDLERANGE(BodyLong,BodyLongTrailingIdx));
      BodyDojiPeriodTotal += (TA_CANDLERANGE(BodyDoji,i)-TA_CANDLERANGE(BodyDoji,BodyDojiTrailingIdx));
      i += 1;
      BodyLongTrailingIdx += 1;
      BodyDojiTrailingIdx += 1;
   } while( (i<=endIdx) );
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

