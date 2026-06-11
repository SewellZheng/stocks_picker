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

TA_LIB_API int TA_CDLRISEFALL3METHODS_Lookback( void )
{
   int BodyLong_rangeType = TA_Globals->candleSettings[TA_BodyLong].rangeType;
   int BodyLong_avgPeriod = TA_Globals->candleSettings[TA_BodyLong].avgPeriod;
   double BodyLong_factor = TA_Globals->candleSettings[TA_BodyLong].factor;
   int BodyShort_rangeType = TA_Globals->candleSettings[TA_BodyShort].rangeType;
   int BodyShort_avgPeriod = TA_Globals->candleSettings[TA_BodyShort].avgPeriod;
   double BodyShort_factor = TA_Globals->candleSettings[TA_BodyShort].factor;
   return (fmax(BodyShort_avgPeriod,BodyLong_avgPeriod)+4);
}

TA_LIB_API TA_RetCode TA_CDLRISEFALL3METHODS( int    startIdx,
                                              int    endIdx,
                                              const double inOpen[],
                                              const double inHigh[],
                                              const double inLow[],
                                              const double inClose[],
                                              int          *outBegIdx,
                                              int          *outNBElement,
                                              int        outInteger[] )
{
   double BodyPeriodTotal[5];
   int i;
   int outIdx;
   int totIdx;
   int BodyShortTrailingIdx;
   int BodyLongTrailingIdx;
   int lookbackTotal;
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
   if( !outInteger )
      return TA_BAD_PARAM;

   lookbackTotal = TA_CDLRISEFALL3METHODS_Lookback();
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
   BodyPeriodTotal[4] = 0;
   BodyPeriodTotal[3] = 0;
   BodyPeriodTotal[2] = 0;
   BodyPeriodTotal[1] = 0;
   BodyPeriodTotal[0] = 0;
   BodyShortTrailingIdx = (startIdx-BodyShort_avgPeriod);
   BodyLongTrailingIdx = (startIdx-BodyLong_avgPeriod);
   i = BodyShortTrailingIdx;
   while( (i<startIdx) )
   {
      BodyPeriodTotal[3] = (BodyPeriodTotal[3]+TA_CANDLERANGE(BodyShort,(i-3)));
      BodyPeriodTotal[2] = (BodyPeriodTotal[2]+TA_CANDLERANGE(BodyShort,(i-2)));
      BodyPeriodTotal[1] = (BodyPeriodTotal[1]+TA_CANDLERANGE(BodyShort,(i-1)));
      i += 1;
   }
   i = BodyLongTrailingIdx;
   while( (i<startIdx) )
   {
      BodyPeriodTotal[4] = (BodyPeriodTotal[4]+TA_CANDLERANGE(BodyLong,(i-4)));
      BodyPeriodTotal[0] = (BodyPeriodTotal[0]+TA_CANDLERANGE(BodyLong,i));
      i += 1;
   }
   i = startIdx;
   outIdx = 0;
   do
   {
      if( ((((((((((((((((((((((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1)))==(0-(((inClose[(i-3)]>=inOpen[(i-3)])) ? (1) : ((0-1)))))&&((((inClose[(i-3)]>=inOpen[(i-3)])) ? (1) : ((0-1)))==(((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))))&&((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==(((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))))&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-(((inClose[i]>=inOpen[i])) ? (1) : ((0-1))))))&&(fmin(inOpen[(i-3)],inClose[(i-3)])<inHigh[(i-4)]))&&(fmax(inOpen[(i-3)],inClose[(i-3)])>inLow[(i-4)]))&&(fmin(inOpen[(i-2)],inClose[(i-2)])<inHigh[(i-4)]))&&(fmax(inOpen[(i-2)],inClose[(i-2)])>inLow[(i-4)]))&&(fmin(inOpen[(i-1)],inClose[(i-1)])<inHigh[(i-4)]))&&(fmax(inOpen[(i-1)],inClose[(i-1)])>inLow[(i-4)]))&&((inClose[(i-2)]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))<(inClose[(i-3)]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))))&&((inClose[(i-1)]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))<(inClose[(i-2)]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))))&&((inOpen[i]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))>(inClose[(i-1)]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))))&&((inClose[i]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))>(inClose[(i-4)]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))))&&(fabs((inClose[(i-4)]-inOpen[(i-4)]))>TA_CANDLEAVERAGE(BodyLong,BodyPeriodTotal[4],(i-4))))&&(fabs((inClose[(i-3)]-inOpen[(i-3)]))<TA_CANDLEAVERAGE(BodyShort,BodyPeriodTotal[3],(i-3))))&&(fabs((inClose[(i-2)]-inOpen[(i-2)]))<TA_CANDLEAVERAGE(BodyShort,BodyPeriodTotal[2],(i-2))))&&(fabs((inClose[(i-1)]-inOpen[(i-1)]))<TA_CANDLEAVERAGE(BodyShort,BodyPeriodTotal[1],(i-1))))&&(fabs((inClose[i]-inOpen[i]))>TA_CANDLEAVERAGE(BodyLong,BodyPeriodTotal[0],i))) )
      {
         outInteger[outIdx++] = (100*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))));
      } else 
      {
         outInteger[outIdx++] = 0;
      }
      BodyPeriodTotal[4] = (BodyPeriodTotal[4]+(TA_CANDLERANGE(BodyLong,(i-4))-TA_CANDLERANGE(BodyLong,(BodyLongTrailingIdx-4))));
      for( totIdx = 3; (totIdx>=1); totIdx -= 1 )
      {
         BodyPeriodTotal[totIdx] = (BodyPeriodTotal[totIdx]+(TA_CANDLERANGE(BodyShort,(i-totIdx))-TA_CANDLERANGE(BodyShort,(BodyShortTrailingIdx-totIdx))));
      }
      BodyPeriodTotal[0] = (BodyPeriodTotal[0]+(TA_CANDLERANGE(BodyLong,i)-TA_CANDLERANGE(BodyLong,BodyLongTrailingIdx)));
      i += 1;
      BodyShortTrailingIdx += 1;
      BodyLongTrailingIdx += 1;
   } while( (i<=endIdx) );
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_LIB_API TA_RetCode TA_CDLRISEFALL3METHODS_Unguarded( int    startIdx,
                                                        int    endIdx,
                                                        const double inOpen[],
                                                        const double inHigh[],
                                                        const double inLow[],
                                                        const double inClose[],
                                                        int          *outBegIdx,
                                                        int          *outNBElement,
                                                        int        outInteger[] )
{
   double BodyPeriodTotal[5];
   int i;
   int outIdx;
   int totIdx;
   int BodyShortTrailingIdx;
   int BodyLongTrailingIdx;
   int lookbackTotal;
   int BodyLong_rangeType = TA_Globals->candleSettings[TA_BodyLong].rangeType;
   int BodyLong_avgPeriod = TA_Globals->candleSettings[TA_BodyLong].avgPeriod;
   double BodyLong_factor = TA_Globals->candleSettings[TA_BodyLong].factor;
   int BodyShort_rangeType = TA_Globals->candleSettings[TA_BodyShort].rangeType;
   int BodyShort_avgPeriod = TA_Globals->candleSettings[TA_BodyShort].avgPeriod;
   double BodyShort_factor = TA_Globals->candleSettings[TA_BodyShort].factor;

   lookbackTotal = TA_CDLRISEFALL3METHODS_Lookback();
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
   BodyPeriodTotal[4] = 0;
   BodyPeriodTotal[3] = 0;
   BodyPeriodTotal[2] = 0;
   BodyPeriodTotal[1] = 0;
   BodyPeriodTotal[0] = 0;
   BodyShortTrailingIdx = (startIdx-BodyShort_avgPeriod);
   BodyLongTrailingIdx = (startIdx-BodyLong_avgPeriod);
   i = BodyShortTrailingIdx;
   while( (i<startIdx) )
   {
      BodyPeriodTotal[3] = (BodyPeriodTotal[3]+TA_CANDLERANGE(BodyShort,(i-3)));
      BodyPeriodTotal[2] = (BodyPeriodTotal[2]+TA_CANDLERANGE(BodyShort,(i-2)));
      BodyPeriodTotal[1] = (BodyPeriodTotal[1]+TA_CANDLERANGE(BodyShort,(i-1)));
      i += 1;
   }
   i = BodyLongTrailingIdx;
   while( (i<startIdx) )
   {
      BodyPeriodTotal[4] = (BodyPeriodTotal[4]+TA_CANDLERANGE(BodyLong,(i-4)));
      BodyPeriodTotal[0] = (BodyPeriodTotal[0]+TA_CANDLERANGE(BodyLong,i));
      i += 1;
   }
   i = startIdx;
   outIdx = 0;
   do
   {
      if( ((((((((((((((((((((((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1)))==(0-(((inClose[(i-3)]>=inOpen[(i-3)])) ? (1) : ((0-1)))))&&((((inClose[(i-3)]>=inOpen[(i-3)])) ? (1) : ((0-1)))==(((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))))&&((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==(((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))))&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-(((inClose[i]>=inOpen[i])) ? (1) : ((0-1))))))&&(fmin(inOpen[(i-3)],inClose[(i-3)])<inHigh[(i-4)]))&&(fmax(inOpen[(i-3)],inClose[(i-3)])>inLow[(i-4)]))&&(fmin(inOpen[(i-2)],inClose[(i-2)])<inHigh[(i-4)]))&&(fmax(inOpen[(i-2)],inClose[(i-2)])>inLow[(i-4)]))&&(fmin(inOpen[(i-1)],inClose[(i-1)])<inHigh[(i-4)]))&&(fmax(inOpen[(i-1)],inClose[(i-1)])>inLow[(i-4)]))&&((inClose[(i-2)]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))<(inClose[(i-3)]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))))&&((inClose[(i-1)]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))<(inClose[(i-2)]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))))&&((inOpen[i]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))>(inClose[(i-1)]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))))&&((inClose[i]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))>(inClose[(i-4)]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))))&&(fabs((inClose[(i-4)]-inOpen[(i-4)]))>TA_CANDLEAVERAGE(BodyLong,BodyPeriodTotal[4],(i-4))))&&(fabs((inClose[(i-3)]-inOpen[(i-3)]))<TA_CANDLEAVERAGE(BodyShort,BodyPeriodTotal[3],(i-3))))&&(fabs((inClose[(i-2)]-inOpen[(i-2)]))<TA_CANDLEAVERAGE(BodyShort,BodyPeriodTotal[2],(i-2))))&&(fabs((inClose[(i-1)]-inOpen[(i-1)]))<TA_CANDLEAVERAGE(BodyShort,BodyPeriodTotal[1],(i-1))))&&(fabs((inClose[i]-inOpen[i]))>TA_CANDLEAVERAGE(BodyLong,BodyPeriodTotal[0],i))) )
      {
         outInteger[outIdx++] = (100*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))));
      } else 
      {
         outInteger[outIdx++] = 0;
      }
      BodyPeriodTotal[4] = (BodyPeriodTotal[4]+(TA_CANDLERANGE(BodyLong,(i-4))-TA_CANDLERANGE(BodyLong,(BodyLongTrailingIdx-4))));
      for( totIdx = 3; (totIdx>=1); totIdx -= 1 )
      {
         BodyPeriodTotal[totIdx] = (BodyPeriodTotal[totIdx]+(TA_CANDLERANGE(BodyShort,(i-totIdx))-TA_CANDLERANGE(BodyShort,(BodyShortTrailingIdx-totIdx))));
      }
      BodyPeriodTotal[0] = (BodyPeriodTotal[0]+(TA_CANDLERANGE(BodyLong,i)-TA_CANDLERANGE(BodyLong,BodyLongTrailingIdx)));
      i += 1;
      BodyShortTrailingIdx += 1;
      BodyLongTrailingIdx += 1;
   } while( (i<=endIdx) );
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_CDLRISEFALL3METHODS( int    startIdx,
                                     int    endIdx,
                                     const float inOpen[],
                                     const float inHigh[],
                                     const float inLow[],
                                     const float inClose[],
                                     int          *outBegIdx,
                                     int          *outNBElement,
                                     int        outInteger[] )
{
   double BodyPeriodTotal[5];
   int i;
   int outIdx;
   int totIdx;
   int BodyShortTrailingIdx;
   int BodyLongTrailingIdx;
   int lookbackTotal;
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
   if( !outInteger )
      return TA_BAD_PARAM;

   lookbackTotal = TA_CDLRISEFALL3METHODS_Lookback();
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
   BodyPeriodTotal[4] = 0;
   BodyPeriodTotal[3] = 0;
   BodyPeriodTotal[2] = 0;
   BodyPeriodTotal[1] = 0;
   BodyPeriodTotal[0] = 0;
   BodyShortTrailingIdx = (startIdx-BodyShort_avgPeriod);
   BodyLongTrailingIdx = (startIdx-BodyLong_avgPeriod);
   i = BodyShortTrailingIdx;
   while( (i<startIdx) )
   {
      BodyPeriodTotal[3] = (BodyPeriodTotal[3]+TA_CANDLERANGE(BodyShort,(i-3)));
      BodyPeriodTotal[2] = (BodyPeriodTotal[2]+TA_CANDLERANGE(BodyShort,(i-2)));
      BodyPeriodTotal[1] = (BodyPeriodTotal[1]+TA_CANDLERANGE(BodyShort,(i-1)));
      i += 1;
   }
   i = BodyLongTrailingIdx;
   while( (i<startIdx) )
   {
      BodyPeriodTotal[4] = (BodyPeriodTotal[4]+TA_CANDLERANGE(BodyLong,(i-4)));
      BodyPeriodTotal[0] = (BodyPeriodTotal[0]+TA_CANDLERANGE(BodyLong,i));
      i += 1;
   }
   i = startIdx;
   outIdx = 0;
   do
   {
      if( ((((((((((((((((((((((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1)))==(0-(((inClose[(i-3)]>=inOpen[(i-3)])) ? (1) : ((0-1)))))&&((((inClose[(i-3)]>=inOpen[(i-3)])) ? (1) : ((0-1)))==(((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))))&&((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==(((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))))&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-(((inClose[i]>=inOpen[i])) ? (1) : ((0-1))))))&&(fmin(inOpen[(i-3)],inClose[(i-3)])<inHigh[(i-4)]))&&(fmax(inOpen[(i-3)],inClose[(i-3)])>inLow[(i-4)]))&&(fmin(inOpen[(i-2)],inClose[(i-2)])<inHigh[(i-4)]))&&(fmax(inOpen[(i-2)],inClose[(i-2)])>inLow[(i-4)]))&&(fmin(inOpen[(i-1)],inClose[(i-1)])<inHigh[(i-4)]))&&(fmax(inOpen[(i-1)],inClose[(i-1)])>inLow[(i-4)]))&&((inClose[(i-2)]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))<(inClose[(i-3)]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))))&&((inClose[(i-1)]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))<(inClose[(i-2)]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))))&&((inOpen[i]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))>(inClose[(i-1)]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))))&&((inClose[i]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))>(inClose[(i-4)]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))))&&(fabs((inClose[(i-4)]-inOpen[(i-4)]))>TA_CANDLEAVERAGE(BodyLong,BodyPeriodTotal[4],(i-4))))&&(fabs((inClose[(i-3)]-inOpen[(i-3)]))<TA_CANDLEAVERAGE(BodyShort,BodyPeriodTotal[3],(i-3))))&&(fabs((inClose[(i-2)]-inOpen[(i-2)]))<TA_CANDLEAVERAGE(BodyShort,BodyPeriodTotal[2],(i-2))))&&(fabs((inClose[(i-1)]-inOpen[(i-1)]))<TA_CANDLEAVERAGE(BodyShort,BodyPeriodTotal[1],(i-1))))&&(fabs((inClose[i]-inOpen[i]))>TA_CANDLEAVERAGE(BodyLong,BodyPeriodTotal[0],i))) )
      {
         outInteger[outIdx++] = (100*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))));
      } else 
      {
         outInteger[outIdx++] = 0;
      }
      BodyPeriodTotal[4] = (BodyPeriodTotal[4]+(TA_CANDLERANGE(BodyLong,(i-4))-TA_CANDLERANGE(BodyLong,(BodyLongTrailingIdx-4))));
      for( totIdx = 3; (totIdx>=1); totIdx -= 1 )
      {
         BodyPeriodTotal[totIdx] = (BodyPeriodTotal[totIdx]+(TA_CANDLERANGE(BodyShort,(i-totIdx))-TA_CANDLERANGE(BodyShort,(BodyShortTrailingIdx-totIdx))));
      }
      BodyPeriodTotal[0] = (BodyPeriodTotal[0]+(TA_CANDLERANGE(BodyLong,i)-TA_CANDLERANGE(BodyLong,BodyLongTrailingIdx)));
      i += 1;
      BodyShortTrailingIdx += 1;
      BodyLongTrailingIdx += 1;
   } while( (i<=endIdx) );
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_CDLRISEFALL3METHODS_Unguarded( int    startIdx,
                                               int    endIdx,
                                               const float inOpen[],
                                               const float inHigh[],
                                               const float inLow[],
                                               const float inClose[],
                                               int          *outBegIdx,
                                               int          *outNBElement,
                                               int        outInteger[] )
{
   double BodyPeriodTotal[5];
   int i;
   int outIdx;
   int totIdx;
   int BodyShortTrailingIdx;
   int BodyLongTrailingIdx;
   int lookbackTotal;
   int BodyLong_rangeType = TA_Globals->candleSettings[TA_BodyLong].rangeType;
   int BodyLong_avgPeriod = TA_Globals->candleSettings[TA_BodyLong].avgPeriod;
   double BodyLong_factor = TA_Globals->candleSettings[TA_BodyLong].factor;
   int BodyShort_rangeType = TA_Globals->candleSettings[TA_BodyShort].rangeType;
   int BodyShort_avgPeriod = TA_Globals->candleSettings[TA_BodyShort].avgPeriod;
   double BodyShort_factor = TA_Globals->candleSettings[TA_BodyShort].factor;

   lookbackTotal = TA_CDLRISEFALL3METHODS_Lookback();
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
   BodyPeriodTotal[4] = 0;
   BodyPeriodTotal[3] = 0;
   BodyPeriodTotal[2] = 0;
   BodyPeriodTotal[1] = 0;
   BodyPeriodTotal[0] = 0;
   BodyShortTrailingIdx = (startIdx-BodyShort_avgPeriod);
   BodyLongTrailingIdx = (startIdx-BodyLong_avgPeriod);
   i = BodyShortTrailingIdx;
   while( (i<startIdx) )
   {
      BodyPeriodTotal[3] = (BodyPeriodTotal[3]+TA_CANDLERANGE(BodyShort,(i-3)));
      BodyPeriodTotal[2] = (BodyPeriodTotal[2]+TA_CANDLERANGE(BodyShort,(i-2)));
      BodyPeriodTotal[1] = (BodyPeriodTotal[1]+TA_CANDLERANGE(BodyShort,(i-1)));
      i += 1;
   }
   i = BodyLongTrailingIdx;
   while( (i<startIdx) )
   {
      BodyPeriodTotal[4] = (BodyPeriodTotal[4]+TA_CANDLERANGE(BodyLong,(i-4)));
      BodyPeriodTotal[0] = (BodyPeriodTotal[0]+TA_CANDLERANGE(BodyLong,i));
      i += 1;
   }
   i = startIdx;
   outIdx = 0;
   do
   {
      if( ((((((((((((((((((((((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1)))==(0-(((inClose[(i-3)]>=inOpen[(i-3)])) ? (1) : ((0-1)))))&&((((inClose[(i-3)]>=inOpen[(i-3)])) ? (1) : ((0-1)))==(((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))))&&((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==(((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))))&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-(((inClose[i]>=inOpen[i])) ? (1) : ((0-1))))))&&(fmin(inOpen[(i-3)],inClose[(i-3)])<inHigh[(i-4)]))&&(fmax(inOpen[(i-3)],inClose[(i-3)])>inLow[(i-4)]))&&(fmin(inOpen[(i-2)],inClose[(i-2)])<inHigh[(i-4)]))&&(fmax(inOpen[(i-2)],inClose[(i-2)])>inLow[(i-4)]))&&(fmin(inOpen[(i-1)],inClose[(i-1)])<inHigh[(i-4)]))&&(fmax(inOpen[(i-1)],inClose[(i-1)])>inLow[(i-4)]))&&((inClose[(i-2)]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))<(inClose[(i-3)]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))))&&((inClose[(i-1)]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))<(inClose[(i-2)]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))))&&((inOpen[i]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))>(inClose[(i-1)]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))))&&((inClose[i]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))>(inClose[(i-4)]*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))))))&&(fabs((inClose[(i-4)]-inOpen[(i-4)]))>TA_CANDLEAVERAGE(BodyLong,BodyPeriodTotal[4],(i-4))))&&(fabs((inClose[(i-3)]-inOpen[(i-3)]))<TA_CANDLEAVERAGE(BodyShort,BodyPeriodTotal[3],(i-3))))&&(fabs((inClose[(i-2)]-inOpen[(i-2)]))<TA_CANDLEAVERAGE(BodyShort,BodyPeriodTotal[2],(i-2))))&&(fabs((inClose[(i-1)]-inOpen[(i-1)]))<TA_CANDLEAVERAGE(BodyShort,BodyPeriodTotal[1],(i-1))))&&(fabs((inClose[i]-inOpen[i]))>TA_CANDLEAVERAGE(BodyLong,BodyPeriodTotal[0],i))) )
      {
         outInteger[outIdx++] = (100*(((inClose[(i-4)]>=inOpen[(i-4)])) ? (1) : ((0-1))));
      } else 
      {
         outInteger[outIdx++] = 0;
      }
      BodyPeriodTotal[4] = (BodyPeriodTotal[4]+(TA_CANDLERANGE(BodyLong,(i-4))-TA_CANDLERANGE(BodyLong,(BodyLongTrailingIdx-4))));
      for( totIdx = 3; (totIdx>=1); totIdx -= 1 )
      {
         BodyPeriodTotal[totIdx] = (BodyPeriodTotal[totIdx]+(TA_CANDLERANGE(BodyShort,(i-totIdx))-TA_CANDLERANGE(BodyShort,(BodyShortTrailingIdx-totIdx))));
      }
      BodyPeriodTotal[0] = (BodyPeriodTotal[0]+(TA_CANDLERANGE(BodyLong,i)-TA_CANDLERANGE(BodyLong,BodyLongTrailingIdx)));
      i += 1;
      BodyShortTrailingIdx += 1;
      BodyLongTrailingIdx += 1;
   } while( (i<=endIdx) );
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

