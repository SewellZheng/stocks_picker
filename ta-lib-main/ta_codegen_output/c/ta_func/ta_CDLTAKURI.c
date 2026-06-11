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

TA_LIB_API int TA_CDLTAKURI_Lookback( void )
{
   int BodyDoji_rangeType = TA_Globals->candleSettings[TA_BodyDoji].rangeType;
   int BodyDoji_avgPeriod = TA_Globals->candleSettings[TA_BodyDoji].avgPeriod;
   double BodyDoji_factor = TA_Globals->candleSettings[TA_BodyDoji].factor;
   int ShadowVeryLong_rangeType = TA_Globals->candleSettings[TA_ShadowVeryLong].rangeType;
   int ShadowVeryLong_avgPeriod = TA_Globals->candleSettings[TA_ShadowVeryLong].avgPeriod;
   double ShadowVeryLong_factor = TA_Globals->candleSettings[TA_ShadowVeryLong].factor;
   int ShadowVeryShort_rangeType = TA_Globals->candleSettings[TA_ShadowVeryShort].rangeType;
   int ShadowVeryShort_avgPeriod = TA_Globals->candleSettings[TA_ShadowVeryShort].avgPeriod;
   double ShadowVeryShort_factor = TA_Globals->candleSettings[TA_ShadowVeryShort].factor;
   return fmax(fmax(BodyDoji_avgPeriod,ShadowVeryShort_avgPeriod),ShadowVeryLong_avgPeriod);
}

TA_LIB_API TA_RetCode TA_CDLTAKURI( int    startIdx,
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
   double ShadowVeryShortPeriodTotal;
   double ShadowVeryLongPeriodTotal;
   int i;
   int outIdx;
   int BodyDojiTrailingIdx;
   int ShadowVeryShortTrailingIdx;
   int ShadowVeryLongTrailingIdx;
   int lookbackTotal;
   int BodyDoji_rangeType = TA_Globals->candleSettings[TA_BodyDoji].rangeType;
   int BodyDoji_avgPeriod = TA_Globals->candleSettings[TA_BodyDoji].avgPeriod;
   double BodyDoji_factor = TA_Globals->candleSettings[TA_BodyDoji].factor;
   int ShadowVeryLong_rangeType = TA_Globals->candleSettings[TA_ShadowVeryLong].rangeType;
   int ShadowVeryLong_avgPeriod = TA_Globals->candleSettings[TA_ShadowVeryLong].avgPeriod;
   double ShadowVeryLong_factor = TA_Globals->candleSettings[TA_ShadowVeryLong].factor;
   int ShadowVeryShort_rangeType = TA_Globals->candleSettings[TA_ShadowVeryShort].rangeType;
   int ShadowVeryShort_avgPeriod = TA_Globals->candleSettings[TA_ShadowVeryShort].avgPeriod;
   double ShadowVeryShort_factor = TA_Globals->candleSettings[TA_ShadowVeryShort].factor;

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

   lookbackTotal = TA_CDLTAKURI_Lookback();
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
   BodyDojiPeriodTotal = 0;
   BodyDojiTrailingIdx = (startIdx-BodyDoji_avgPeriod);
   ShadowVeryShortPeriodTotal = 0;
   ShadowVeryShortTrailingIdx = (startIdx-ShadowVeryShort_avgPeriod);
   ShadowVeryLongPeriodTotal = 0;
   ShadowVeryLongTrailingIdx = (startIdx-ShadowVeryLong_avgPeriod);
   i = BodyDojiTrailingIdx;
   while( (i<startIdx) )
   {
      BodyDojiPeriodTotal += TA_CANDLERANGE(BodyDoji,i);
      i += 1;
   }
   i = ShadowVeryShortTrailingIdx;
   while( (i<startIdx) )
   {
      ShadowVeryShortPeriodTotal += TA_CANDLERANGE(ShadowVeryShort,i);
      i += 1;
   }
   i = ShadowVeryLongTrailingIdx;
   while( (i<startIdx) )
   {
      ShadowVeryLongPeriodTotal += TA_CANDLERANGE(ShadowVeryLong,i);
      i += 1;
   }
   outIdx = 0;
   do
   {
      if( (((fabs((inClose[i]-inOpen[i]))<=TA_CANDLEAVERAGE(BodyDoji,BodyDojiPeriodTotal,i))&&((inHigh[i]-(((inClose[i]>=inOpen[i])) ? (inClose[i]) : (inOpen[i])))<TA_CANDLEAVERAGE(ShadowVeryShort,ShadowVeryShortPeriodTotal,i)))&&(((((inClose[i]>=inOpen[i])) ? (inOpen[i]) : (inClose[i]))-inLow[i])>TA_CANDLEAVERAGE(ShadowVeryLong,ShadowVeryLongPeriodTotal,i))) )
      {
         outInteger[outIdx++] = 100;
      } else 
      {
         outInteger[outIdx++] = 0;
      }
      BodyDojiPeriodTotal += (TA_CANDLERANGE(BodyDoji,i)-TA_CANDLERANGE(BodyDoji,BodyDojiTrailingIdx));
      ShadowVeryShortPeriodTotal += (TA_CANDLERANGE(ShadowVeryShort,i)-TA_CANDLERANGE(ShadowVeryShort,ShadowVeryShortTrailingIdx));
      ShadowVeryLongPeriodTotal += (TA_CANDLERANGE(ShadowVeryLong,i)-TA_CANDLERANGE(ShadowVeryLong,ShadowVeryLongTrailingIdx));
      i += 1;
      BodyDojiTrailingIdx += 1;
      ShadowVeryShortTrailingIdx += 1;
      ShadowVeryLongTrailingIdx += 1;
   } while( (i<=endIdx) );
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_LIB_API TA_RetCode TA_CDLTAKURI_Unguarded( int    startIdx,
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
   double ShadowVeryShortPeriodTotal;
   double ShadowVeryLongPeriodTotal;
   int i;
   int outIdx;
   int BodyDojiTrailingIdx;
   int ShadowVeryShortTrailingIdx;
   int ShadowVeryLongTrailingIdx;
   int lookbackTotal;
   int BodyDoji_rangeType = TA_Globals->candleSettings[TA_BodyDoji].rangeType;
   int BodyDoji_avgPeriod = TA_Globals->candleSettings[TA_BodyDoji].avgPeriod;
   double BodyDoji_factor = TA_Globals->candleSettings[TA_BodyDoji].factor;
   int ShadowVeryLong_rangeType = TA_Globals->candleSettings[TA_ShadowVeryLong].rangeType;
   int ShadowVeryLong_avgPeriod = TA_Globals->candleSettings[TA_ShadowVeryLong].avgPeriod;
   double ShadowVeryLong_factor = TA_Globals->candleSettings[TA_ShadowVeryLong].factor;
   int ShadowVeryShort_rangeType = TA_Globals->candleSettings[TA_ShadowVeryShort].rangeType;
   int ShadowVeryShort_avgPeriod = TA_Globals->candleSettings[TA_ShadowVeryShort].avgPeriod;
   double ShadowVeryShort_factor = TA_Globals->candleSettings[TA_ShadowVeryShort].factor;

   lookbackTotal = TA_CDLTAKURI_Lookback();
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
   BodyDojiPeriodTotal = 0;
   BodyDojiTrailingIdx = (startIdx-BodyDoji_avgPeriod);
   ShadowVeryShortPeriodTotal = 0;
   ShadowVeryShortTrailingIdx = (startIdx-ShadowVeryShort_avgPeriod);
   ShadowVeryLongPeriodTotal = 0;
   ShadowVeryLongTrailingIdx = (startIdx-ShadowVeryLong_avgPeriod);
   i = BodyDojiTrailingIdx;
   while( (i<startIdx) )
   {
      BodyDojiPeriodTotal += TA_CANDLERANGE(BodyDoji,i);
      i += 1;
   }
   i = ShadowVeryShortTrailingIdx;
   while( (i<startIdx) )
   {
      ShadowVeryShortPeriodTotal += TA_CANDLERANGE(ShadowVeryShort,i);
      i += 1;
   }
   i = ShadowVeryLongTrailingIdx;
   while( (i<startIdx) )
   {
      ShadowVeryLongPeriodTotal += TA_CANDLERANGE(ShadowVeryLong,i);
      i += 1;
   }
   outIdx = 0;
   do
   {
      if( (((fabs((inClose[i]-inOpen[i]))<=TA_CANDLEAVERAGE(BodyDoji,BodyDojiPeriodTotal,i))&&((inHigh[i]-(((inClose[i]>=inOpen[i])) ? (inClose[i]) : (inOpen[i])))<TA_CANDLEAVERAGE(ShadowVeryShort,ShadowVeryShortPeriodTotal,i)))&&(((((inClose[i]>=inOpen[i])) ? (inOpen[i]) : (inClose[i]))-inLow[i])>TA_CANDLEAVERAGE(ShadowVeryLong,ShadowVeryLongPeriodTotal,i))) )
      {
         outInteger[outIdx++] = 100;
      } else 
      {
         outInteger[outIdx++] = 0;
      }
      BodyDojiPeriodTotal += (TA_CANDLERANGE(BodyDoji,i)-TA_CANDLERANGE(BodyDoji,BodyDojiTrailingIdx));
      ShadowVeryShortPeriodTotal += (TA_CANDLERANGE(ShadowVeryShort,i)-TA_CANDLERANGE(ShadowVeryShort,ShadowVeryShortTrailingIdx));
      ShadowVeryLongPeriodTotal += (TA_CANDLERANGE(ShadowVeryLong,i)-TA_CANDLERANGE(ShadowVeryLong,ShadowVeryLongTrailingIdx));
      i += 1;
      BodyDojiTrailingIdx += 1;
      ShadowVeryShortTrailingIdx += 1;
      ShadowVeryLongTrailingIdx += 1;
   } while( (i<=endIdx) );
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_CDLTAKURI( int    startIdx,
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
   double ShadowVeryShortPeriodTotal;
   double ShadowVeryLongPeriodTotal;
   int i;
   int outIdx;
   int BodyDojiTrailingIdx;
   int ShadowVeryShortTrailingIdx;
   int ShadowVeryLongTrailingIdx;
   int lookbackTotal;
   int BodyDoji_rangeType = TA_Globals->candleSettings[TA_BodyDoji].rangeType;
   int BodyDoji_avgPeriod = TA_Globals->candleSettings[TA_BodyDoji].avgPeriod;
   double BodyDoji_factor = TA_Globals->candleSettings[TA_BodyDoji].factor;
   int ShadowVeryLong_rangeType = TA_Globals->candleSettings[TA_ShadowVeryLong].rangeType;
   int ShadowVeryLong_avgPeriod = TA_Globals->candleSettings[TA_ShadowVeryLong].avgPeriod;
   double ShadowVeryLong_factor = TA_Globals->candleSettings[TA_ShadowVeryLong].factor;
   int ShadowVeryShort_rangeType = TA_Globals->candleSettings[TA_ShadowVeryShort].rangeType;
   int ShadowVeryShort_avgPeriod = TA_Globals->candleSettings[TA_ShadowVeryShort].avgPeriod;
   double ShadowVeryShort_factor = TA_Globals->candleSettings[TA_ShadowVeryShort].factor;

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

   lookbackTotal = TA_CDLTAKURI_Lookback();
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
   BodyDojiPeriodTotal = 0;
   BodyDojiTrailingIdx = (startIdx-BodyDoji_avgPeriod);
   ShadowVeryShortPeriodTotal = 0;
   ShadowVeryShortTrailingIdx = (startIdx-ShadowVeryShort_avgPeriod);
   ShadowVeryLongPeriodTotal = 0;
   ShadowVeryLongTrailingIdx = (startIdx-ShadowVeryLong_avgPeriod);
   i = BodyDojiTrailingIdx;
   while( (i<startIdx) )
   {
      BodyDojiPeriodTotal += TA_CANDLERANGE(BodyDoji,i);
      i += 1;
   }
   i = ShadowVeryShortTrailingIdx;
   while( (i<startIdx) )
   {
      ShadowVeryShortPeriodTotal += TA_CANDLERANGE(ShadowVeryShort,i);
      i += 1;
   }
   i = ShadowVeryLongTrailingIdx;
   while( (i<startIdx) )
   {
      ShadowVeryLongPeriodTotal += TA_CANDLERANGE(ShadowVeryLong,i);
      i += 1;
   }
   outIdx = 0;
   do
   {
      if( (((fabs((inClose[i]-inOpen[i]))<=TA_CANDLEAVERAGE(BodyDoji,BodyDojiPeriodTotal,i))&&((inHigh[i]-(((inClose[i]>=inOpen[i])) ? (inClose[i]) : (inOpen[i])))<TA_CANDLEAVERAGE(ShadowVeryShort,ShadowVeryShortPeriodTotal,i)))&&(((((inClose[i]>=inOpen[i])) ? (inOpen[i]) : (inClose[i]))-inLow[i])>TA_CANDLEAVERAGE(ShadowVeryLong,ShadowVeryLongPeriodTotal,i))) )
      {
         outInteger[outIdx++] = 100;
      } else 
      {
         outInteger[outIdx++] = 0;
      }
      BodyDojiPeriodTotal += (TA_CANDLERANGE(BodyDoji,i)-TA_CANDLERANGE(BodyDoji,BodyDojiTrailingIdx));
      ShadowVeryShortPeriodTotal += (TA_CANDLERANGE(ShadowVeryShort,i)-TA_CANDLERANGE(ShadowVeryShort,ShadowVeryShortTrailingIdx));
      ShadowVeryLongPeriodTotal += (TA_CANDLERANGE(ShadowVeryLong,i)-TA_CANDLERANGE(ShadowVeryLong,ShadowVeryLongTrailingIdx));
      i += 1;
      BodyDojiTrailingIdx += 1;
      ShadowVeryShortTrailingIdx += 1;
      ShadowVeryLongTrailingIdx += 1;
   } while( (i<=endIdx) );
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_CDLTAKURI_Unguarded( int    startIdx,
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
   double ShadowVeryShortPeriodTotal;
   double ShadowVeryLongPeriodTotal;
   int i;
   int outIdx;
   int BodyDojiTrailingIdx;
   int ShadowVeryShortTrailingIdx;
   int ShadowVeryLongTrailingIdx;
   int lookbackTotal;
   int BodyDoji_rangeType = TA_Globals->candleSettings[TA_BodyDoji].rangeType;
   int BodyDoji_avgPeriod = TA_Globals->candleSettings[TA_BodyDoji].avgPeriod;
   double BodyDoji_factor = TA_Globals->candleSettings[TA_BodyDoji].factor;
   int ShadowVeryLong_rangeType = TA_Globals->candleSettings[TA_ShadowVeryLong].rangeType;
   int ShadowVeryLong_avgPeriod = TA_Globals->candleSettings[TA_ShadowVeryLong].avgPeriod;
   double ShadowVeryLong_factor = TA_Globals->candleSettings[TA_ShadowVeryLong].factor;
   int ShadowVeryShort_rangeType = TA_Globals->candleSettings[TA_ShadowVeryShort].rangeType;
   int ShadowVeryShort_avgPeriod = TA_Globals->candleSettings[TA_ShadowVeryShort].avgPeriod;
   double ShadowVeryShort_factor = TA_Globals->candleSettings[TA_ShadowVeryShort].factor;

   lookbackTotal = TA_CDLTAKURI_Lookback();
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
   BodyDojiPeriodTotal = 0;
   BodyDojiTrailingIdx = (startIdx-BodyDoji_avgPeriod);
   ShadowVeryShortPeriodTotal = 0;
   ShadowVeryShortTrailingIdx = (startIdx-ShadowVeryShort_avgPeriod);
   ShadowVeryLongPeriodTotal = 0;
   ShadowVeryLongTrailingIdx = (startIdx-ShadowVeryLong_avgPeriod);
   i = BodyDojiTrailingIdx;
   while( (i<startIdx) )
   {
      BodyDojiPeriodTotal += TA_CANDLERANGE(BodyDoji,i);
      i += 1;
   }
   i = ShadowVeryShortTrailingIdx;
   while( (i<startIdx) )
   {
      ShadowVeryShortPeriodTotal += TA_CANDLERANGE(ShadowVeryShort,i);
      i += 1;
   }
   i = ShadowVeryLongTrailingIdx;
   while( (i<startIdx) )
   {
      ShadowVeryLongPeriodTotal += TA_CANDLERANGE(ShadowVeryLong,i);
      i += 1;
   }
   outIdx = 0;
   do
   {
      if( (((fabs((inClose[i]-inOpen[i]))<=TA_CANDLEAVERAGE(BodyDoji,BodyDojiPeriodTotal,i))&&((inHigh[i]-(((inClose[i]>=inOpen[i])) ? (inClose[i]) : (inOpen[i])))<TA_CANDLEAVERAGE(ShadowVeryShort,ShadowVeryShortPeriodTotal,i)))&&(((((inClose[i]>=inOpen[i])) ? (inOpen[i]) : (inClose[i]))-inLow[i])>TA_CANDLEAVERAGE(ShadowVeryLong,ShadowVeryLongPeriodTotal,i))) )
      {
         outInteger[outIdx++] = 100;
      } else 
      {
         outInteger[outIdx++] = 0;
      }
      BodyDojiPeriodTotal += (TA_CANDLERANGE(BodyDoji,i)-TA_CANDLERANGE(BodyDoji,BodyDojiTrailingIdx));
      ShadowVeryShortPeriodTotal += (TA_CANDLERANGE(ShadowVeryShort,i)-TA_CANDLERANGE(ShadowVeryShort,ShadowVeryShortTrailingIdx));
      ShadowVeryLongPeriodTotal += (TA_CANDLERANGE(ShadowVeryLong,i)-TA_CANDLERANGE(ShadowVeryLong,ShadowVeryLongTrailingIdx));
      i += 1;
      BodyDojiTrailingIdx += 1;
      ShadowVeryShortTrailingIdx += 1;
      ShadowVeryLongTrailingIdx += 1;
   } while( (i<=endIdx) );
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

