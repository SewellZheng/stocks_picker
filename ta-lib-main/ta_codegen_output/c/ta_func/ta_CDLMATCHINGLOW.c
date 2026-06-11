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

TA_LIB_API int TA_CDLMATCHINGLOW_Lookback( void )
{
   int Equal_rangeType = TA_Globals->candleSettings[TA_Equal].rangeType;
   int Equal_avgPeriod = TA_Globals->candleSettings[TA_Equal].avgPeriod;
   double Equal_factor = TA_Globals->candleSettings[TA_Equal].factor;
   return (Equal_avgPeriod+1);
}

TA_LIB_API TA_RetCode TA_CDLMATCHINGLOW( int    startIdx,
                                         int    endIdx,
                                         const double inOpen[],
                                         const double inHigh[],
                                         const double inLow[],
                                         const double inClose[],
                                         int          *outBegIdx,
                                         int          *outNBElement,
                                         int        outInteger[] )
{
   double EqualPeriodTotal;
   int i;
   int outIdx;
   int EqualTrailingIdx;
   int lookbackTotal;
   int Equal_rangeType = TA_Globals->candleSettings[TA_Equal].rangeType;
   int Equal_avgPeriod = TA_Globals->candleSettings[TA_Equal].avgPeriod;
   double Equal_factor = TA_Globals->candleSettings[TA_Equal].factor;

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

   lookbackTotal = TA_CDLMATCHINGLOW_Lookback();
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
   EqualPeriodTotal = 0;
   EqualTrailingIdx = (startIdx-Equal_avgPeriod);
   i = EqualTrailingIdx;
   while( (i<startIdx) )
   {
      EqualPeriodTotal += TA_CANDLERANGE(Equal,(i-1));
      i += 1;
   }
   i = startIdx;
   outIdx = 0;
   do
   {
      if( (((((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-1))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==(0-1)))&&(inClose[i]<=(inClose[(i-1)]+TA_CANDLEAVERAGE(Equal,EqualPeriodTotal,(i-1)))))&&(inClose[i]>=(inClose[(i-1)]-TA_CANDLEAVERAGE(Equal,EqualPeriodTotal,(i-1))))) )
      {
         outInteger[outIdx++] = 100;
      } else 
      {
         outInteger[outIdx++] = 0;
      }
      EqualPeriodTotal += (TA_CANDLERANGE(Equal,(i-1))-TA_CANDLERANGE(Equal,(EqualTrailingIdx-1)));
      i += 1;
      EqualTrailingIdx += 1;
   } while( (i<=endIdx) );
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_LIB_API TA_RetCode TA_CDLMATCHINGLOW_Unguarded( int    startIdx,
                                                   int    endIdx,
                                                   const double inOpen[],
                                                   const double inHigh[],
                                                   const double inLow[],
                                                   const double inClose[],
                                                   int          *outBegIdx,
                                                   int          *outNBElement,
                                                   int        outInteger[] )
{
   double EqualPeriodTotal;
   int i;
   int outIdx;
   int EqualTrailingIdx;
   int lookbackTotal;
   int Equal_rangeType = TA_Globals->candleSettings[TA_Equal].rangeType;
   int Equal_avgPeriod = TA_Globals->candleSettings[TA_Equal].avgPeriod;
   double Equal_factor = TA_Globals->candleSettings[TA_Equal].factor;

   lookbackTotal = TA_CDLMATCHINGLOW_Lookback();
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
   EqualPeriodTotal = 0;
   EqualTrailingIdx = (startIdx-Equal_avgPeriod);
   i = EqualTrailingIdx;
   while( (i<startIdx) )
   {
      EqualPeriodTotal += TA_CANDLERANGE(Equal,(i-1));
      i += 1;
   }
   i = startIdx;
   outIdx = 0;
   do
   {
      if( (((((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-1))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==(0-1)))&&(inClose[i]<=(inClose[(i-1)]+TA_CANDLEAVERAGE(Equal,EqualPeriodTotal,(i-1)))))&&(inClose[i]>=(inClose[(i-1)]-TA_CANDLEAVERAGE(Equal,EqualPeriodTotal,(i-1))))) )
      {
         outInteger[outIdx++] = 100;
      } else 
      {
         outInteger[outIdx++] = 0;
      }
      EqualPeriodTotal += (TA_CANDLERANGE(Equal,(i-1))-TA_CANDLERANGE(Equal,(EqualTrailingIdx-1)));
      i += 1;
      EqualTrailingIdx += 1;
   } while( (i<=endIdx) );
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_CDLMATCHINGLOW( int    startIdx,
                                int    endIdx,
                                const float inOpen[],
                                const float inHigh[],
                                const float inLow[],
                                const float inClose[],
                                int          *outBegIdx,
                                int          *outNBElement,
                                int        outInteger[] )
{
   double EqualPeriodTotal;
   int i;
   int outIdx;
   int EqualTrailingIdx;
   int lookbackTotal;
   int Equal_rangeType = TA_Globals->candleSettings[TA_Equal].rangeType;
   int Equal_avgPeriod = TA_Globals->candleSettings[TA_Equal].avgPeriod;
   double Equal_factor = TA_Globals->candleSettings[TA_Equal].factor;

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

   lookbackTotal = TA_CDLMATCHINGLOW_Lookback();
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
   EqualPeriodTotal = 0;
   EqualTrailingIdx = (startIdx-Equal_avgPeriod);
   i = EqualTrailingIdx;
   while( (i<startIdx) )
   {
      EqualPeriodTotal += TA_CANDLERANGE(Equal,(i-1));
      i += 1;
   }
   i = startIdx;
   outIdx = 0;
   do
   {
      if( (((((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-1))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==(0-1)))&&(inClose[i]<=(inClose[(i-1)]+TA_CANDLEAVERAGE(Equal,EqualPeriodTotal,(i-1)))))&&(inClose[i]>=(inClose[(i-1)]-TA_CANDLEAVERAGE(Equal,EqualPeriodTotal,(i-1))))) )
      {
         outInteger[outIdx++] = 100;
      } else 
      {
         outInteger[outIdx++] = 0;
      }
      EqualPeriodTotal += (TA_CANDLERANGE(Equal,(i-1))-TA_CANDLERANGE(Equal,(EqualTrailingIdx-1)));
      i += 1;
      EqualTrailingIdx += 1;
   } while( (i<=endIdx) );
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_CDLMATCHINGLOW_Unguarded( int    startIdx,
                                          int    endIdx,
                                          const float inOpen[],
                                          const float inHigh[],
                                          const float inLow[],
                                          const float inClose[],
                                          int          *outBegIdx,
                                          int          *outNBElement,
                                          int        outInteger[] )
{
   double EqualPeriodTotal;
   int i;
   int outIdx;
   int EqualTrailingIdx;
   int lookbackTotal;
   int Equal_rangeType = TA_Globals->candleSettings[TA_Equal].rangeType;
   int Equal_avgPeriod = TA_Globals->candleSettings[TA_Equal].avgPeriod;
   double Equal_factor = TA_Globals->candleSettings[TA_Equal].factor;

   lookbackTotal = TA_CDLMATCHINGLOW_Lookback();
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
   EqualPeriodTotal = 0;
   EqualTrailingIdx = (startIdx-Equal_avgPeriod);
   i = EqualTrailingIdx;
   while( (i<startIdx) )
   {
      EqualPeriodTotal += TA_CANDLERANGE(Equal,(i-1));
      i += 1;
   }
   i = startIdx;
   outIdx = 0;
   do
   {
      if( (((((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==(0-1))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==(0-1)))&&(inClose[i]<=(inClose[(i-1)]+TA_CANDLEAVERAGE(Equal,EqualPeriodTotal,(i-1)))))&&(inClose[i]>=(inClose[(i-1)]-TA_CANDLEAVERAGE(Equal,EqualPeriodTotal,(i-1))))) )
      {
         outInteger[outIdx++] = 100;
      } else 
      {
         outInteger[outIdx++] = 0;
      }
      EqualPeriodTotal += (TA_CANDLERANGE(Equal,(i-1))-TA_CANDLERANGE(Equal,(EqualTrailingIdx-1)));
      i += 1;
      EqualTrailingIdx += 1;
   } while( (i<=endIdx) );
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

