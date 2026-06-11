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

TA_LIB_API int TA_CDLCONCEALBABYSWALL_Lookback( void )
{
   int ShadowVeryShort_rangeType = TA_Globals->candleSettings[TA_ShadowVeryShort].rangeType;
   int ShadowVeryShort_avgPeriod = TA_Globals->candleSettings[TA_ShadowVeryShort].avgPeriod;
   double ShadowVeryShort_factor = TA_Globals->candleSettings[TA_ShadowVeryShort].factor;
   return (ShadowVeryShort_avgPeriod+3);
}

TA_LIB_API TA_RetCode TA_CDLCONCEALBABYSWALL( int    startIdx,
                                              int    endIdx,
                                              const double inOpen[],
                                              const double inHigh[],
                                              const double inLow[],
                                              const double inClose[],
                                              int          *outBegIdx,
                                              int          *outNBElement,
                                              int        outInteger[] )
{
   double ShadowVeryShortPeriodTotal[4];
   int i;
   int outIdx;
   int totIdx;
   int ShadowVeryShortTrailingIdx;
   int lookbackTotal;
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

   lookbackTotal = TA_CDLCONCEALBABYSWALL_Lookback();
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
   ShadowVeryShortPeriodTotal[3] = 0;
   ShadowVeryShortPeriodTotal[2] = 0;
   ShadowVeryShortPeriodTotal[1] = 0;
   ShadowVeryShortTrailingIdx = (startIdx-ShadowVeryShort_avgPeriod);
   i = ShadowVeryShortTrailingIdx;
   while( (i<startIdx) )
   {
      ShadowVeryShortPeriodTotal[3] = (ShadowVeryShortPeriodTotal[3]+TA_CANDLERANGE(ShadowVeryShort,(i-3)));
      ShadowVeryShortPeriodTotal[2] = (ShadowVeryShortPeriodTotal[2]+TA_CANDLERANGE(ShadowVeryShort,(i-2)));
      ShadowVeryShortPeriodTotal[1] = (ShadowVeryShortPeriodTotal[1]+TA_CANDLERANGE(ShadowVeryShort,(i-1)));
      i += 1;
   }
   i = startIdx;
   outIdx = 0;
   do
   {
      if( ((((((((((((((((inClose[(i-3)]>=inOpen[(i-3)])) ? (1) : ((0-1)))==(0-1))&&((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==(0-1)))&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-1)))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==(0-1)))&&(((((inClose[(i-3)]>=inOpen[(i-3)])) ? (inOpen[(i-3)]) : (inClose[(i-3)]))-inLow[(i-3)])<TA_CANDLEAVERAGE(ShadowVeryShort,ShadowVeryShortPeriodTotal[3],(i-3))))&&((inHigh[(i-3)]-(((inClose[(i-3)]>=inOpen[(i-3)])) ? (inClose[(i-3)]) : (inOpen[(i-3)])))<TA_CANDLEAVERAGE(ShadowVeryShort,ShadowVeryShortPeriodTotal[3],(i-3))))&&(((((inClose[(i-2)]>=inOpen[(i-2)])) ? (inOpen[(i-2)]) : (inClose[(i-2)]))-inLow[(i-2)])<TA_CANDLEAVERAGE(ShadowVeryShort,ShadowVeryShortPeriodTotal[2],(i-2))))&&((inHigh[(i-2)]-(((inClose[(i-2)]>=inOpen[(i-2)])) ? (inClose[(i-2)]) : (inOpen[(i-2)])))<TA_CANDLEAVERAGE(ShadowVeryShort,ShadowVeryShortPeriodTotal[2],(i-2))))&&(((fmax(inOpen[(i-1)],inClose[(i-1)])<fmin(inOpen[(i-2)],inClose[(i-2)]))) ? (1) : (0)))&&((inHigh[(i-1)]-(((inClose[(i-1)]>=inOpen[(i-1)])) ? (inClose[(i-1)]) : (inOpen[(i-1)])))>TA_CANDLEAVERAGE(ShadowVeryShort,ShadowVeryShortPeriodTotal[1],(i-1))))&&(inHigh[(i-1)]>inClose[(i-2)]))&&(inHigh[i]>inHigh[(i-1)]))&&(inLow[i]<inLow[(i-1)])) )
      {
         outInteger[outIdx++] = 100;
      } else 
      {
         outInteger[outIdx++] = 0;
      }
      for( totIdx = 3; (totIdx>=1); totIdx -= 1 )
      {
         ShadowVeryShortPeriodTotal[totIdx] = (ShadowVeryShortPeriodTotal[totIdx]+(TA_CANDLERANGE(ShadowVeryShort,(i-totIdx))-TA_CANDLERANGE(ShadowVeryShort,(ShadowVeryShortTrailingIdx-totIdx))));
      }
      i += 1;
      ShadowVeryShortTrailingIdx += 1;
   } while( (i<=endIdx) );
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_LIB_API TA_RetCode TA_CDLCONCEALBABYSWALL_Unguarded( int    startIdx,
                                                        int    endIdx,
                                                        const double inOpen[],
                                                        const double inHigh[],
                                                        const double inLow[],
                                                        const double inClose[],
                                                        int          *outBegIdx,
                                                        int          *outNBElement,
                                                        int        outInteger[] )
{
   double ShadowVeryShortPeriodTotal[4];
   int i;
   int outIdx;
   int totIdx;
   int ShadowVeryShortTrailingIdx;
   int lookbackTotal;
   int ShadowVeryShort_rangeType = TA_Globals->candleSettings[TA_ShadowVeryShort].rangeType;
   int ShadowVeryShort_avgPeriod = TA_Globals->candleSettings[TA_ShadowVeryShort].avgPeriod;
   double ShadowVeryShort_factor = TA_Globals->candleSettings[TA_ShadowVeryShort].factor;

   lookbackTotal = TA_CDLCONCEALBABYSWALL_Lookback();
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
   ShadowVeryShortPeriodTotal[3] = 0;
   ShadowVeryShortPeriodTotal[2] = 0;
   ShadowVeryShortPeriodTotal[1] = 0;
   ShadowVeryShortTrailingIdx = (startIdx-ShadowVeryShort_avgPeriod);
   i = ShadowVeryShortTrailingIdx;
   while( (i<startIdx) )
   {
      ShadowVeryShortPeriodTotal[3] = (ShadowVeryShortPeriodTotal[3]+TA_CANDLERANGE(ShadowVeryShort,(i-3)));
      ShadowVeryShortPeriodTotal[2] = (ShadowVeryShortPeriodTotal[2]+TA_CANDLERANGE(ShadowVeryShort,(i-2)));
      ShadowVeryShortPeriodTotal[1] = (ShadowVeryShortPeriodTotal[1]+TA_CANDLERANGE(ShadowVeryShort,(i-1)));
      i += 1;
   }
   i = startIdx;
   outIdx = 0;
   do
   {
      if( ((((((((((((((((inClose[(i-3)]>=inOpen[(i-3)])) ? (1) : ((0-1)))==(0-1))&&((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==(0-1)))&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-1)))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==(0-1)))&&(((((inClose[(i-3)]>=inOpen[(i-3)])) ? (inOpen[(i-3)]) : (inClose[(i-3)]))-inLow[(i-3)])<TA_CANDLEAVERAGE(ShadowVeryShort,ShadowVeryShortPeriodTotal[3],(i-3))))&&((inHigh[(i-3)]-(((inClose[(i-3)]>=inOpen[(i-3)])) ? (inClose[(i-3)]) : (inOpen[(i-3)])))<TA_CANDLEAVERAGE(ShadowVeryShort,ShadowVeryShortPeriodTotal[3],(i-3))))&&(((((inClose[(i-2)]>=inOpen[(i-2)])) ? (inOpen[(i-2)]) : (inClose[(i-2)]))-inLow[(i-2)])<TA_CANDLEAVERAGE(ShadowVeryShort,ShadowVeryShortPeriodTotal[2],(i-2))))&&((inHigh[(i-2)]-(((inClose[(i-2)]>=inOpen[(i-2)])) ? (inClose[(i-2)]) : (inOpen[(i-2)])))<TA_CANDLEAVERAGE(ShadowVeryShort,ShadowVeryShortPeriodTotal[2],(i-2))))&&(((fmax(inOpen[(i-1)],inClose[(i-1)])<fmin(inOpen[(i-2)],inClose[(i-2)]))) ? (1) : (0)))&&((inHigh[(i-1)]-(((inClose[(i-1)]>=inOpen[(i-1)])) ? (inClose[(i-1)]) : (inOpen[(i-1)])))>TA_CANDLEAVERAGE(ShadowVeryShort,ShadowVeryShortPeriodTotal[1],(i-1))))&&(inHigh[(i-1)]>inClose[(i-2)]))&&(inHigh[i]>inHigh[(i-1)]))&&(inLow[i]<inLow[(i-1)])) )
      {
         outInteger[outIdx++] = 100;
      } else 
      {
         outInteger[outIdx++] = 0;
      }
      for( totIdx = 3; (totIdx>=1); totIdx -= 1 )
      {
         ShadowVeryShortPeriodTotal[totIdx] = (ShadowVeryShortPeriodTotal[totIdx]+(TA_CANDLERANGE(ShadowVeryShort,(i-totIdx))-TA_CANDLERANGE(ShadowVeryShort,(ShadowVeryShortTrailingIdx-totIdx))));
      }
      i += 1;
      ShadowVeryShortTrailingIdx += 1;
   } while( (i<=endIdx) );
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_CDLCONCEALBABYSWALL( int    startIdx,
                                     int    endIdx,
                                     const float inOpen[],
                                     const float inHigh[],
                                     const float inLow[],
                                     const float inClose[],
                                     int          *outBegIdx,
                                     int          *outNBElement,
                                     int        outInteger[] )
{
   double ShadowVeryShortPeriodTotal[4];
   int i;
   int outIdx;
   int totIdx;
   int ShadowVeryShortTrailingIdx;
   int lookbackTotal;
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

   lookbackTotal = TA_CDLCONCEALBABYSWALL_Lookback();
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
   ShadowVeryShortPeriodTotal[3] = 0;
   ShadowVeryShortPeriodTotal[2] = 0;
   ShadowVeryShortPeriodTotal[1] = 0;
   ShadowVeryShortTrailingIdx = (startIdx-ShadowVeryShort_avgPeriod);
   i = ShadowVeryShortTrailingIdx;
   while( (i<startIdx) )
   {
      ShadowVeryShortPeriodTotal[3] = (ShadowVeryShortPeriodTotal[3]+TA_CANDLERANGE(ShadowVeryShort,(i-3)));
      ShadowVeryShortPeriodTotal[2] = (ShadowVeryShortPeriodTotal[2]+TA_CANDLERANGE(ShadowVeryShort,(i-2)));
      ShadowVeryShortPeriodTotal[1] = (ShadowVeryShortPeriodTotal[1]+TA_CANDLERANGE(ShadowVeryShort,(i-1)));
      i += 1;
   }
   i = startIdx;
   outIdx = 0;
   do
   {
      if( ((((((((((((((((inClose[(i-3)]>=inOpen[(i-3)])) ? (1) : ((0-1)))==(0-1))&&((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==(0-1)))&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-1)))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==(0-1)))&&(((((inClose[(i-3)]>=inOpen[(i-3)])) ? (inOpen[(i-3)]) : (inClose[(i-3)]))-inLow[(i-3)])<TA_CANDLEAVERAGE(ShadowVeryShort,ShadowVeryShortPeriodTotal[3],(i-3))))&&((inHigh[(i-3)]-(((inClose[(i-3)]>=inOpen[(i-3)])) ? (inClose[(i-3)]) : (inOpen[(i-3)])))<TA_CANDLEAVERAGE(ShadowVeryShort,ShadowVeryShortPeriodTotal[3],(i-3))))&&(((((inClose[(i-2)]>=inOpen[(i-2)])) ? (inOpen[(i-2)]) : (inClose[(i-2)]))-inLow[(i-2)])<TA_CANDLEAVERAGE(ShadowVeryShort,ShadowVeryShortPeriodTotal[2],(i-2))))&&((inHigh[(i-2)]-(((inClose[(i-2)]>=inOpen[(i-2)])) ? (inClose[(i-2)]) : (inOpen[(i-2)])))<TA_CANDLEAVERAGE(ShadowVeryShort,ShadowVeryShortPeriodTotal[2],(i-2))))&&(((fmax(inOpen[(i-1)],inClose[(i-1)])<fmin(inOpen[(i-2)],inClose[(i-2)]))) ? (1) : (0)))&&((inHigh[(i-1)]-(((inClose[(i-1)]>=inOpen[(i-1)])) ? (inClose[(i-1)]) : (inOpen[(i-1)])))>TA_CANDLEAVERAGE(ShadowVeryShort,ShadowVeryShortPeriodTotal[1],(i-1))))&&(inHigh[(i-1)]>inClose[(i-2)]))&&(inHigh[i]>inHigh[(i-1)]))&&(inLow[i]<inLow[(i-1)])) )
      {
         outInteger[outIdx++] = 100;
      } else 
      {
         outInteger[outIdx++] = 0;
      }
      for( totIdx = 3; (totIdx>=1); totIdx -= 1 )
      {
         ShadowVeryShortPeriodTotal[totIdx] = (ShadowVeryShortPeriodTotal[totIdx]+(TA_CANDLERANGE(ShadowVeryShort,(i-totIdx))-TA_CANDLERANGE(ShadowVeryShort,(ShadowVeryShortTrailingIdx-totIdx))));
      }
      i += 1;
      ShadowVeryShortTrailingIdx += 1;
   } while( (i<=endIdx) );
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_CDLCONCEALBABYSWALL_Unguarded( int    startIdx,
                                               int    endIdx,
                                               const float inOpen[],
                                               const float inHigh[],
                                               const float inLow[],
                                               const float inClose[],
                                               int          *outBegIdx,
                                               int          *outNBElement,
                                               int        outInteger[] )
{
   double ShadowVeryShortPeriodTotal[4];
   int i;
   int outIdx;
   int totIdx;
   int ShadowVeryShortTrailingIdx;
   int lookbackTotal;
   int ShadowVeryShort_rangeType = TA_Globals->candleSettings[TA_ShadowVeryShort].rangeType;
   int ShadowVeryShort_avgPeriod = TA_Globals->candleSettings[TA_ShadowVeryShort].avgPeriod;
   double ShadowVeryShort_factor = TA_Globals->candleSettings[TA_ShadowVeryShort].factor;

   lookbackTotal = TA_CDLCONCEALBABYSWALL_Lookback();
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
   ShadowVeryShortPeriodTotal[3] = 0;
   ShadowVeryShortPeriodTotal[2] = 0;
   ShadowVeryShortPeriodTotal[1] = 0;
   ShadowVeryShortTrailingIdx = (startIdx-ShadowVeryShort_avgPeriod);
   i = ShadowVeryShortTrailingIdx;
   while( (i<startIdx) )
   {
      ShadowVeryShortPeriodTotal[3] = (ShadowVeryShortPeriodTotal[3]+TA_CANDLERANGE(ShadowVeryShort,(i-3)));
      ShadowVeryShortPeriodTotal[2] = (ShadowVeryShortPeriodTotal[2]+TA_CANDLERANGE(ShadowVeryShort,(i-2)));
      ShadowVeryShortPeriodTotal[1] = (ShadowVeryShortPeriodTotal[1]+TA_CANDLERANGE(ShadowVeryShort,(i-1)));
      i += 1;
   }
   i = startIdx;
   outIdx = 0;
   do
   {
      if( ((((((((((((((((inClose[(i-3)]>=inOpen[(i-3)])) ? (1) : ((0-1)))==(0-1))&&((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==(0-1)))&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-1)))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==(0-1)))&&(((((inClose[(i-3)]>=inOpen[(i-3)])) ? (inOpen[(i-3)]) : (inClose[(i-3)]))-inLow[(i-3)])<TA_CANDLEAVERAGE(ShadowVeryShort,ShadowVeryShortPeriodTotal[3],(i-3))))&&((inHigh[(i-3)]-(((inClose[(i-3)]>=inOpen[(i-3)])) ? (inClose[(i-3)]) : (inOpen[(i-3)])))<TA_CANDLEAVERAGE(ShadowVeryShort,ShadowVeryShortPeriodTotal[3],(i-3))))&&(((((inClose[(i-2)]>=inOpen[(i-2)])) ? (inOpen[(i-2)]) : (inClose[(i-2)]))-inLow[(i-2)])<TA_CANDLEAVERAGE(ShadowVeryShort,ShadowVeryShortPeriodTotal[2],(i-2))))&&((inHigh[(i-2)]-(((inClose[(i-2)]>=inOpen[(i-2)])) ? (inClose[(i-2)]) : (inOpen[(i-2)])))<TA_CANDLEAVERAGE(ShadowVeryShort,ShadowVeryShortPeriodTotal[2],(i-2))))&&(((fmax(inOpen[(i-1)],inClose[(i-1)])<fmin(inOpen[(i-2)],inClose[(i-2)]))) ? (1) : (0)))&&((inHigh[(i-1)]-(((inClose[(i-1)]>=inOpen[(i-1)])) ? (inClose[(i-1)]) : (inOpen[(i-1)])))>TA_CANDLEAVERAGE(ShadowVeryShort,ShadowVeryShortPeriodTotal[1],(i-1))))&&(inHigh[(i-1)]>inClose[(i-2)]))&&(inHigh[i]>inHigh[(i-1)]))&&(inLow[i]<inLow[(i-1)])) )
      {
         outInteger[outIdx++] = 100;
      } else 
      {
         outInteger[outIdx++] = 0;
      }
      for( totIdx = 3; (totIdx>=1); totIdx -= 1 )
      {
         ShadowVeryShortPeriodTotal[totIdx] = (ShadowVeryShortPeriodTotal[totIdx]+(TA_CANDLERANGE(ShadowVeryShort,(i-totIdx))-TA_CANDLERANGE(ShadowVeryShort,(ShadowVeryShortTrailingIdx-totIdx))));
      }
      i += 1;
      ShadowVeryShortTrailingIdx += 1;
   } while( (i<=endIdx) );
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

