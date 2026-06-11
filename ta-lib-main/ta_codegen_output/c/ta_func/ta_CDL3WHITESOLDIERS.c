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

TA_LIB_API int TA_CDL3WHITESOLDIERS_Lookback( void )
{
   int BodyShort_rangeType = TA_Globals->candleSettings[TA_BodyShort].rangeType;
   int BodyShort_avgPeriod = TA_Globals->candleSettings[TA_BodyShort].avgPeriod;
   double BodyShort_factor = TA_Globals->candleSettings[TA_BodyShort].factor;
   int Far_rangeType = TA_Globals->candleSettings[TA_Far].rangeType;
   int Far_avgPeriod = TA_Globals->candleSettings[TA_Far].avgPeriod;
   double Far_factor = TA_Globals->candleSettings[TA_Far].factor;
   int Near_rangeType = TA_Globals->candleSettings[TA_Near].rangeType;
   int Near_avgPeriod = TA_Globals->candleSettings[TA_Near].avgPeriod;
   double Near_factor = TA_Globals->candleSettings[TA_Near].factor;
   int ShadowVeryShort_rangeType = TA_Globals->candleSettings[TA_ShadowVeryShort].rangeType;
   int ShadowVeryShort_avgPeriod = TA_Globals->candleSettings[TA_ShadowVeryShort].avgPeriod;
   double ShadowVeryShort_factor = TA_Globals->candleSettings[TA_ShadowVeryShort].factor;
   return (fmax(fmax(ShadowVeryShort_avgPeriod,BodyShort_avgPeriod),fmax(Far_avgPeriod,Near_avgPeriod))+2);
}

TA_LIB_API TA_RetCode TA_CDL3WHITESOLDIERS( int    startIdx,
                                            int    endIdx,
                                            const double inOpen[],
                                            const double inHigh[],
                                            const double inLow[],
                                            const double inClose[],
                                            int          *outBegIdx,
                                            int          *outNBElement,
                                            int        outInteger[] )
{
   double ShadowVeryShortPeriodTotal[3];
   double NearPeriodTotal[3];
   double FarPeriodTotal[3];
   double BodyShortPeriodTotal;
   int i;
   int outIdx;
   int totIdx;
   int ShadowVeryShortTrailingIdx;
   int NearTrailingIdx;
   int FarTrailingIdx;
   int BodyShortTrailingIdx;
   int lookbackTotal;
   int BodyShort_rangeType = TA_Globals->candleSettings[TA_BodyShort].rangeType;
   int BodyShort_avgPeriod = TA_Globals->candleSettings[TA_BodyShort].avgPeriod;
   double BodyShort_factor = TA_Globals->candleSettings[TA_BodyShort].factor;
   int Far_rangeType = TA_Globals->candleSettings[TA_Far].rangeType;
   int Far_avgPeriod = TA_Globals->candleSettings[TA_Far].avgPeriod;
   double Far_factor = TA_Globals->candleSettings[TA_Far].factor;
   int Near_rangeType = TA_Globals->candleSettings[TA_Near].rangeType;
   int Near_avgPeriod = TA_Globals->candleSettings[TA_Near].avgPeriod;
   double Near_factor = TA_Globals->candleSettings[TA_Near].factor;
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

   lookbackTotal = TA_CDL3WHITESOLDIERS_Lookback();
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
   ShadowVeryShortPeriodTotal[2] = 0;
   ShadowVeryShortPeriodTotal[1] = 0;
   ShadowVeryShortPeriodTotal[0] = 0;
   ShadowVeryShortTrailingIdx = (startIdx-ShadowVeryShort_avgPeriod);
   NearPeriodTotal[2] = 0;
   NearPeriodTotal[1] = 0;
   NearPeriodTotal[0] = 0;
   NearTrailingIdx = (startIdx-Near_avgPeriod);
   FarPeriodTotal[2] = 0;
   FarPeriodTotal[1] = 0;
   FarPeriodTotal[0] = 0;
   FarTrailingIdx = (startIdx-Far_avgPeriod);
   BodyShortPeriodTotal = 0;
   BodyShortTrailingIdx = (startIdx-BodyShort_avgPeriod);
   i = ShadowVeryShortTrailingIdx;
   while( (i<startIdx) )
   {
      ShadowVeryShortPeriodTotal[2] = (ShadowVeryShortPeriodTotal[2]+TA_CANDLERANGE(ShadowVeryShort,(i-2)));
      ShadowVeryShortPeriodTotal[1] = (ShadowVeryShortPeriodTotal[1]+TA_CANDLERANGE(ShadowVeryShort,(i-1)));
      ShadowVeryShortPeriodTotal[0] = (ShadowVeryShortPeriodTotal[0]+TA_CANDLERANGE(ShadowVeryShort,i));
      i += 1;
   }
   i = NearTrailingIdx;
   while( (i<startIdx) )
   {
      NearPeriodTotal[2] = (NearPeriodTotal[2]+TA_CANDLERANGE(Near,(i-2)));
      NearPeriodTotal[1] = (NearPeriodTotal[1]+TA_CANDLERANGE(Near,(i-1)));
      i += 1;
   }
   i = FarTrailingIdx;
   while( (i<startIdx) )
   {
      FarPeriodTotal[2] = (FarPeriodTotal[2]+TA_CANDLERANGE(Far,(i-2)));
      FarPeriodTotal[1] = (FarPeriodTotal[1]+TA_CANDLERANGE(Far,(i-1)));
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
      if( ((((((((((((((((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==1)&&((inHigh[(i-2)]-(((inClose[(i-2)]>=inOpen[(i-2)])) ? (inClose[(i-2)]) : (inOpen[(i-2)])))<TA_CANDLEAVERAGE(ShadowVeryShort,ShadowVeryShortPeriodTotal[2],(i-2))))&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==1))&&((inHigh[(i-1)]-(((inClose[(i-1)]>=inOpen[(i-1)])) ? (inClose[(i-1)]) : (inOpen[(i-1)])))<TA_CANDLEAVERAGE(ShadowVeryShort,ShadowVeryShortPeriodTotal[1],(i-1))))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==1))&&((inHigh[i]-(((inClose[i]>=inOpen[i])) ? (inClose[i]) : (inOpen[i])))<TA_CANDLEAVERAGE(ShadowVeryShort,ShadowVeryShortPeriodTotal[0],i)))&&(inClose[i]>inClose[(i-1)]))&&(inClose[(i-1)]>inClose[(i-2)]))&&(inOpen[(i-1)]>inOpen[(i-2)]))&&(inOpen[(i-1)]<=(inClose[(i-2)]+TA_CANDLEAVERAGE(Near,NearPeriodTotal[2],(i-2)))))&&(inOpen[i]>inOpen[(i-1)]))&&(inOpen[i]<=(inClose[(i-1)]+TA_CANDLEAVERAGE(Near,NearPeriodTotal[1],(i-1)))))&&(fabs((inClose[(i-1)]-inOpen[(i-1)]))>(fabs((inClose[(i-2)]-inOpen[(i-2)]))-TA_CANDLEAVERAGE(Far,FarPeriodTotal[2],(i-2)))))&&(fabs((inClose[i]-inOpen[i]))>(fabs((inClose[(i-1)]-inOpen[(i-1)]))-TA_CANDLEAVERAGE(Far,FarPeriodTotal[1],(i-1)))))&&(fabs((inClose[i]-inOpen[i]))>TA_CANDLEAVERAGE(BodyShort,BodyShortPeriodTotal,i))) )
      {
         outInteger[outIdx++] = 100;
      } else 
      {
         outInteger[outIdx++] = 0;
      }
      for( totIdx = 2; (totIdx>=0); totIdx -= 1 )
      {
         ShadowVeryShortPeriodTotal[totIdx] = (ShadowVeryShortPeriodTotal[totIdx]+(TA_CANDLERANGE(ShadowVeryShort,(i-totIdx))-TA_CANDLERANGE(ShadowVeryShort,(ShadowVeryShortTrailingIdx-totIdx))));
      }
      for( totIdx = 2; (totIdx>=1); totIdx -= 1 )
      {
         FarPeriodTotal[totIdx] = (FarPeriodTotal[totIdx]+(TA_CANDLERANGE(Far,(i-totIdx))-TA_CANDLERANGE(Far,(FarTrailingIdx-totIdx))));
         NearPeriodTotal[totIdx] = (NearPeriodTotal[totIdx]+(TA_CANDLERANGE(Near,(i-totIdx))-TA_CANDLERANGE(Near,(NearTrailingIdx-totIdx))));
      }
      BodyShortPeriodTotal += (TA_CANDLERANGE(BodyShort,i)-TA_CANDLERANGE(BodyShort,BodyShortTrailingIdx));
      i += 1;
      ShadowVeryShortTrailingIdx += 1;
      NearTrailingIdx += 1;
      FarTrailingIdx += 1;
      BodyShortTrailingIdx += 1;
   } while( (i<=endIdx) );
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_LIB_API TA_RetCode TA_CDL3WHITESOLDIERS_Unguarded( int    startIdx,
                                                      int    endIdx,
                                                      const double inOpen[],
                                                      const double inHigh[],
                                                      const double inLow[],
                                                      const double inClose[],
                                                      int          *outBegIdx,
                                                      int          *outNBElement,
                                                      int        outInteger[] )
{
   double ShadowVeryShortPeriodTotal[3];
   double NearPeriodTotal[3];
   double FarPeriodTotal[3];
   double BodyShortPeriodTotal;
   int i;
   int outIdx;
   int totIdx;
   int ShadowVeryShortTrailingIdx;
   int NearTrailingIdx;
   int FarTrailingIdx;
   int BodyShortTrailingIdx;
   int lookbackTotal;
   int BodyShort_rangeType = TA_Globals->candleSettings[TA_BodyShort].rangeType;
   int BodyShort_avgPeriod = TA_Globals->candleSettings[TA_BodyShort].avgPeriod;
   double BodyShort_factor = TA_Globals->candleSettings[TA_BodyShort].factor;
   int Far_rangeType = TA_Globals->candleSettings[TA_Far].rangeType;
   int Far_avgPeriod = TA_Globals->candleSettings[TA_Far].avgPeriod;
   double Far_factor = TA_Globals->candleSettings[TA_Far].factor;
   int Near_rangeType = TA_Globals->candleSettings[TA_Near].rangeType;
   int Near_avgPeriod = TA_Globals->candleSettings[TA_Near].avgPeriod;
   double Near_factor = TA_Globals->candleSettings[TA_Near].factor;
   int ShadowVeryShort_rangeType = TA_Globals->candleSettings[TA_ShadowVeryShort].rangeType;
   int ShadowVeryShort_avgPeriod = TA_Globals->candleSettings[TA_ShadowVeryShort].avgPeriod;
   double ShadowVeryShort_factor = TA_Globals->candleSettings[TA_ShadowVeryShort].factor;

   lookbackTotal = TA_CDL3WHITESOLDIERS_Lookback();
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
   ShadowVeryShortPeriodTotal[2] = 0;
   ShadowVeryShortPeriodTotal[1] = 0;
   ShadowVeryShortPeriodTotal[0] = 0;
   ShadowVeryShortTrailingIdx = (startIdx-ShadowVeryShort_avgPeriod);
   NearPeriodTotal[2] = 0;
   NearPeriodTotal[1] = 0;
   NearPeriodTotal[0] = 0;
   NearTrailingIdx = (startIdx-Near_avgPeriod);
   FarPeriodTotal[2] = 0;
   FarPeriodTotal[1] = 0;
   FarPeriodTotal[0] = 0;
   FarTrailingIdx = (startIdx-Far_avgPeriod);
   BodyShortPeriodTotal = 0;
   BodyShortTrailingIdx = (startIdx-BodyShort_avgPeriod);
   i = ShadowVeryShortTrailingIdx;
   while( (i<startIdx) )
   {
      ShadowVeryShortPeriodTotal[2] = (ShadowVeryShortPeriodTotal[2]+TA_CANDLERANGE(ShadowVeryShort,(i-2)));
      ShadowVeryShortPeriodTotal[1] = (ShadowVeryShortPeriodTotal[1]+TA_CANDLERANGE(ShadowVeryShort,(i-1)));
      ShadowVeryShortPeriodTotal[0] = (ShadowVeryShortPeriodTotal[0]+TA_CANDLERANGE(ShadowVeryShort,i));
      i += 1;
   }
   i = NearTrailingIdx;
   while( (i<startIdx) )
   {
      NearPeriodTotal[2] = (NearPeriodTotal[2]+TA_CANDLERANGE(Near,(i-2)));
      NearPeriodTotal[1] = (NearPeriodTotal[1]+TA_CANDLERANGE(Near,(i-1)));
      i += 1;
   }
   i = FarTrailingIdx;
   while( (i<startIdx) )
   {
      FarPeriodTotal[2] = (FarPeriodTotal[2]+TA_CANDLERANGE(Far,(i-2)));
      FarPeriodTotal[1] = (FarPeriodTotal[1]+TA_CANDLERANGE(Far,(i-1)));
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
      if( ((((((((((((((((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==1)&&((inHigh[(i-2)]-(((inClose[(i-2)]>=inOpen[(i-2)])) ? (inClose[(i-2)]) : (inOpen[(i-2)])))<TA_CANDLEAVERAGE(ShadowVeryShort,ShadowVeryShortPeriodTotal[2],(i-2))))&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==1))&&((inHigh[(i-1)]-(((inClose[(i-1)]>=inOpen[(i-1)])) ? (inClose[(i-1)]) : (inOpen[(i-1)])))<TA_CANDLEAVERAGE(ShadowVeryShort,ShadowVeryShortPeriodTotal[1],(i-1))))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==1))&&((inHigh[i]-(((inClose[i]>=inOpen[i])) ? (inClose[i]) : (inOpen[i])))<TA_CANDLEAVERAGE(ShadowVeryShort,ShadowVeryShortPeriodTotal[0],i)))&&(inClose[i]>inClose[(i-1)]))&&(inClose[(i-1)]>inClose[(i-2)]))&&(inOpen[(i-1)]>inOpen[(i-2)]))&&(inOpen[(i-1)]<=(inClose[(i-2)]+TA_CANDLEAVERAGE(Near,NearPeriodTotal[2],(i-2)))))&&(inOpen[i]>inOpen[(i-1)]))&&(inOpen[i]<=(inClose[(i-1)]+TA_CANDLEAVERAGE(Near,NearPeriodTotal[1],(i-1)))))&&(fabs((inClose[(i-1)]-inOpen[(i-1)]))>(fabs((inClose[(i-2)]-inOpen[(i-2)]))-TA_CANDLEAVERAGE(Far,FarPeriodTotal[2],(i-2)))))&&(fabs((inClose[i]-inOpen[i]))>(fabs((inClose[(i-1)]-inOpen[(i-1)]))-TA_CANDLEAVERAGE(Far,FarPeriodTotal[1],(i-1)))))&&(fabs((inClose[i]-inOpen[i]))>TA_CANDLEAVERAGE(BodyShort,BodyShortPeriodTotal,i))) )
      {
         outInteger[outIdx++] = 100;
      } else 
      {
         outInteger[outIdx++] = 0;
      }
      for( totIdx = 2; (totIdx>=0); totIdx -= 1 )
      {
         ShadowVeryShortPeriodTotal[totIdx] = (ShadowVeryShortPeriodTotal[totIdx]+(TA_CANDLERANGE(ShadowVeryShort,(i-totIdx))-TA_CANDLERANGE(ShadowVeryShort,(ShadowVeryShortTrailingIdx-totIdx))));
      }
      for( totIdx = 2; (totIdx>=1); totIdx -= 1 )
      {
         FarPeriodTotal[totIdx] = (FarPeriodTotal[totIdx]+(TA_CANDLERANGE(Far,(i-totIdx))-TA_CANDLERANGE(Far,(FarTrailingIdx-totIdx))));
         NearPeriodTotal[totIdx] = (NearPeriodTotal[totIdx]+(TA_CANDLERANGE(Near,(i-totIdx))-TA_CANDLERANGE(Near,(NearTrailingIdx-totIdx))));
      }
      BodyShortPeriodTotal += (TA_CANDLERANGE(BodyShort,i)-TA_CANDLERANGE(BodyShort,BodyShortTrailingIdx));
      i += 1;
      ShadowVeryShortTrailingIdx += 1;
      NearTrailingIdx += 1;
      FarTrailingIdx += 1;
      BodyShortTrailingIdx += 1;
   } while( (i<=endIdx) );
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_CDL3WHITESOLDIERS( int    startIdx,
                                   int    endIdx,
                                   const float inOpen[],
                                   const float inHigh[],
                                   const float inLow[],
                                   const float inClose[],
                                   int          *outBegIdx,
                                   int          *outNBElement,
                                   int        outInteger[] )
{
   double ShadowVeryShortPeriodTotal[3];
   double NearPeriodTotal[3];
   double FarPeriodTotal[3];
   double BodyShortPeriodTotal;
   int i;
   int outIdx;
   int totIdx;
   int ShadowVeryShortTrailingIdx;
   int NearTrailingIdx;
   int FarTrailingIdx;
   int BodyShortTrailingIdx;
   int lookbackTotal;
   int BodyShort_rangeType = TA_Globals->candleSettings[TA_BodyShort].rangeType;
   int BodyShort_avgPeriod = TA_Globals->candleSettings[TA_BodyShort].avgPeriod;
   double BodyShort_factor = TA_Globals->candleSettings[TA_BodyShort].factor;
   int Far_rangeType = TA_Globals->candleSettings[TA_Far].rangeType;
   int Far_avgPeriod = TA_Globals->candleSettings[TA_Far].avgPeriod;
   double Far_factor = TA_Globals->candleSettings[TA_Far].factor;
   int Near_rangeType = TA_Globals->candleSettings[TA_Near].rangeType;
   int Near_avgPeriod = TA_Globals->candleSettings[TA_Near].avgPeriod;
   double Near_factor = TA_Globals->candleSettings[TA_Near].factor;
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

   lookbackTotal = TA_CDL3WHITESOLDIERS_Lookback();
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
   ShadowVeryShortPeriodTotal[2] = 0;
   ShadowVeryShortPeriodTotal[1] = 0;
   ShadowVeryShortPeriodTotal[0] = 0;
   ShadowVeryShortTrailingIdx = (startIdx-ShadowVeryShort_avgPeriod);
   NearPeriodTotal[2] = 0;
   NearPeriodTotal[1] = 0;
   NearPeriodTotal[0] = 0;
   NearTrailingIdx = (startIdx-Near_avgPeriod);
   FarPeriodTotal[2] = 0;
   FarPeriodTotal[1] = 0;
   FarPeriodTotal[0] = 0;
   FarTrailingIdx = (startIdx-Far_avgPeriod);
   BodyShortPeriodTotal = 0;
   BodyShortTrailingIdx = (startIdx-BodyShort_avgPeriod);
   i = ShadowVeryShortTrailingIdx;
   while( (i<startIdx) )
   {
      ShadowVeryShortPeriodTotal[2] = (ShadowVeryShortPeriodTotal[2]+TA_CANDLERANGE(ShadowVeryShort,(i-2)));
      ShadowVeryShortPeriodTotal[1] = (ShadowVeryShortPeriodTotal[1]+TA_CANDLERANGE(ShadowVeryShort,(i-1)));
      ShadowVeryShortPeriodTotal[0] = (ShadowVeryShortPeriodTotal[0]+TA_CANDLERANGE(ShadowVeryShort,i));
      i += 1;
   }
   i = NearTrailingIdx;
   while( (i<startIdx) )
   {
      NearPeriodTotal[2] = (NearPeriodTotal[2]+TA_CANDLERANGE(Near,(i-2)));
      NearPeriodTotal[1] = (NearPeriodTotal[1]+TA_CANDLERANGE(Near,(i-1)));
      i += 1;
   }
   i = FarTrailingIdx;
   while( (i<startIdx) )
   {
      FarPeriodTotal[2] = (FarPeriodTotal[2]+TA_CANDLERANGE(Far,(i-2)));
      FarPeriodTotal[1] = (FarPeriodTotal[1]+TA_CANDLERANGE(Far,(i-1)));
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
      if( ((((((((((((((((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==1)&&((inHigh[(i-2)]-(((inClose[(i-2)]>=inOpen[(i-2)])) ? (inClose[(i-2)]) : (inOpen[(i-2)])))<TA_CANDLEAVERAGE(ShadowVeryShort,ShadowVeryShortPeriodTotal[2],(i-2))))&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==1))&&((inHigh[(i-1)]-(((inClose[(i-1)]>=inOpen[(i-1)])) ? (inClose[(i-1)]) : (inOpen[(i-1)])))<TA_CANDLEAVERAGE(ShadowVeryShort,ShadowVeryShortPeriodTotal[1],(i-1))))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==1))&&((inHigh[i]-(((inClose[i]>=inOpen[i])) ? (inClose[i]) : (inOpen[i])))<TA_CANDLEAVERAGE(ShadowVeryShort,ShadowVeryShortPeriodTotal[0],i)))&&(inClose[i]>inClose[(i-1)]))&&(inClose[(i-1)]>inClose[(i-2)]))&&(inOpen[(i-1)]>inOpen[(i-2)]))&&(inOpen[(i-1)]<=(inClose[(i-2)]+TA_CANDLEAVERAGE(Near,NearPeriodTotal[2],(i-2)))))&&(inOpen[i]>inOpen[(i-1)]))&&(inOpen[i]<=(inClose[(i-1)]+TA_CANDLEAVERAGE(Near,NearPeriodTotal[1],(i-1)))))&&(fabs((inClose[(i-1)]-inOpen[(i-1)]))>(fabs((inClose[(i-2)]-inOpen[(i-2)]))-TA_CANDLEAVERAGE(Far,FarPeriodTotal[2],(i-2)))))&&(fabs((inClose[i]-inOpen[i]))>(fabs((inClose[(i-1)]-inOpen[(i-1)]))-TA_CANDLEAVERAGE(Far,FarPeriodTotal[1],(i-1)))))&&(fabs((inClose[i]-inOpen[i]))>TA_CANDLEAVERAGE(BodyShort,BodyShortPeriodTotal,i))) )
      {
         outInteger[outIdx++] = 100;
      } else 
      {
         outInteger[outIdx++] = 0;
      }
      for( totIdx = 2; (totIdx>=0); totIdx -= 1 )
      {
         ShadowVeryShortPeriodTotal[totIdx] = (ShadowVeryShortPeriodTotal[totIdx]+(TA_CANDLERANGE(ShadowVeryShort,(i-totIdx))-TA_CANDLERANGE(ShadowVeryShort,(ShadowVeryShortTrailingIdx-totIdx))));
      }
      for( totIdx = 2; (totIdx>=1); totIdx -= 1 )
      {
         FarPeriodTotal[totIdx] = (FarPeriodTotal[totIdx]+(TA_CANDLERANGE(Far,(i-totIdx))-TA_CANDLERANGE(Far,(FarTrailingIdx-totIdx))));
         NearPeriodTotal[totIdx] = (NearPeriodTotal[totIdx]+(TA_CANDLERANGE(Near,(i-totIdx))-TA_CANDLERANGE(Near,(NearTrailingIdx-totIdx))));
      }
      BodyShortPeriodTotal += (TA_CANDLERANGE(BodyShort,i)-TA_CANDLERANGE(BodyShort,BodyShortTrailingIdx));
      i += 1;
      ShadowVeryShortTrailingIdx += 1;
      NearTrailingIdx += 1;
      FarTrailingIdx += 1;
      BodyShortTrailingIdx += 1;
   } while( (i<=endIdx) );
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_CDL3WHITESOLDIERS_Unguarded( int    startIdx,
                                             int    endIdx,
                                             const float inOpen[],
                                             const float inHigh[],
                                             const float inLow[],
                                             const float inClose[],
                                             int          *outBegIdx,
                                             int          *outNBElement,
                                             int        outInteger[] )
{
   double ShadowVeryShortPeriodTotal[3];
   double NearPeriodTotal[3];
   double FarPeriodTotal[3];
   double BodyShortPeriodTotal;
   int i;
   int outIdx;
   int totIdx;
   int ShadowVeryShortTrailingIdx;
   int NearTrailingIdx;
   int FarTrailingIdx;
   int BodyShortTrailingIdx;
   int lookbackTotal;
   int BodyShort_rangeType = TA_Globals->candleSettings[TA_BodyShort].rangeType;
   int BodyShort_avgPeriod = TA_Globals->candleSettings[TA_BodyShort].avgPeriod;
   double BodyShort_factor = TA_Globals->candleSettings[TA_BodyShort].factor;
   int Far_rangeType = TA_Globals->candleSettings[TA_Far].rangeType;
   int Far_avgPeriod = TA_Globals->candleSettings[TA_Far].avgPeriod;
   double Far_factor = TA_Globals->candleSettings[TA_Far].factor;
   int Near_rangeType = TA_Globals->candleSettings[TA_Near].rangeType;
   int Near_avgPeriod = TA_Globals->candleSettings[TA_Near].avgPeriod;
   double Near_factor = TA_Globals->candleSettings[TA_Near].factor;
   int ShadowVeryShort_rangeType = TA_Globals->candleSettings[TA_ShadowVeryShort].rangeType;
   int ShadowVeryShort_avgPeriod = TA_Globals->candleSettings[TA_ShadowVeryShort].avgPeriod;
   double ShadowVeryShort_factor = TA_Globals->candleSettings[TA_ShadowVeryShort].factor;

   lookbackTotal = TA_CDL3WHITESOLDIERS_Lookback();
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
   ShadowVeryShortPeriodTotal[2] = 0;
   ShadowVeryShortPeriodTotal[1] = 0;
   ShadowVeryShortPeriodTotal[0] = 0;
   ShadowVeryShortTrailingIdx = (startIdx-ShadowVeryShort_avgPeriod);
   NearPeriodTotal[2] = 0;
   NearPeriodTotal[1] = 0;
   NearPeriodTotal[0] = 0;
   NearTrailingIdx = (startIdx-Near_avgPeriod);
   FarPeriodTotal[2] = 0;
   FarPeriodTotal[1] = 0;
   FarPeriodTotal[0] = 0;
   FarTrailingIdx = (startIdx-Far_avgPeriod);
   BodyShortPeriodTotal = 0;
   BodyShortTrailingIdx = (startIdx-BodyShort_avgPeriod);
   i = ShadowVeryShortTrailingIdx;
   while( (i<startIdx) )
   {
      ShadowVeryShortPeriodTotal[2] = (ShadowVeryShortPeriodTotal[2]+TA_CANDLERANGE(ShadowVeryShort,(i-2)));
      ShadowVeryShortPeriodTotal[1] = (ShadowVeryShortPeriodTotal[1]+TA_CANDLERANGE(ShadowVeryShort,(i-1)));
      ShadowVeryShortPeriodTotal[0] = (ShadowVeryShortPeriodTotal[0]+TA_CANDLERANGE(ShadowVeryShort,i));
      i += 1;
   }
   i = NearTrailingIdx;
   while( (i<startIdx) )
   {
      NearPeriodTotal[2] = (NearPeriodTotal[2]+TA_CANDLERANGE(Near,(i-2)));
      NearPeriodTotal[1] = (NearPeriodTotal[1]+TA_CANDLERANGE(Near,(i-1)));
      i += 1;
   }
   i = FarTrailingIdx;
   while( (i<startIdx) )
   {
      FarPeriodTotal[2] = (FarPeriodTotal[2]+TA_CANDLERANGE(Far,(i-2)));
      FarPeriodTotal[1] = (FarPeriodTotal[1]+TA_CANDLERANGE(Far,(i-1)));
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
      if( ((((((((((((((((((inClose[(i-2)]>=inOpen[(i-2)])) ? (1) : ((0-1)))==1)&&((inHigh[(i-2)]-(((inClose[(i-2)]>=inOpen[(i-2)])) ? (inClose[(i-2)]) : (inOpen[(i-2)])))<TA_CANDLEAVERAGE(ShadowVeryShort,ShadowVeryShortPeriodTotal[2],(i-2))))&&((((inClose[(i-1)]>=inOpen[(i-1)])) ? (1) : ((0-1)))==1))&&((inHigh[(i-1)]-(((inClose[(i-1)]>=inOpen[(i-1)])) ? (inClose[(i-1)]) : (inOpen[(i-1)])))<TA_CANDLEAVERAGE(ShadowVeryShort,ShadowVeryShortPeriodTotal[1],(i-1))))&&((((inClose[i]>=inOpen[i])) ? (1) : ((0-1)))==1))&&((inHigh[i]-(((inClose[i]>=inOpen[i])) ? (inClose[i]) : (inOpen[i])))<TA_CANDLEAVERAGE(ShadowVeryShort,ShadowVeryShortPeriodTotal[0],i)))&&(inClose[i]>inClose[(i-1)]))&&(inClose[(i-1)]>inClose[(i-2)]))&&(inOpen[(i-1)]>inOpen[(i-2)]))&&(inOpen[(i-1)]<=(inClose[(i-2)]+TA_CANDLEAVERAGE(Near,NearPeriodTotal[2],(i-2)))))&&(inOpen[i]>inOpen[(i-1)]))&&(inOpen[i]<=(inClose[(i-1)]+TA_CANDLEAVERAGE(Near,NearPeriodTotal[1],(i-1)))))&&(fabs((inClose[(i-1)]-inOpen[(i-1)]))>(fabs((inClose[(i-2)]-inOpen[(i-2)]))-TA_CANDLEAVERAGE(Far,FarPeriodTotal[2],(i-2)))))&&(fabs((inClose[i]-inOpen[i]))>(fabs((inClose[(i-1)]-inOpen[(i-1)]))-TA_CANDLEAVERAGE(Far,FarPeriodTotal[1],(i-1)))))&&(fabs((inClose[i]-inOpen[i]))>TA_CANDLEAVERAGE(BodyShort,BodyShortPeriodTotal,i))) )
      {
         outInteger[outIdx++] = 100;
      } else 
      {
         outInteger[outIdx++] = 0;
      }
      for( totIdx = 2; (totIdx>=0); totIdx -= 1 )
      {
         ShadowVeryShortPeriodTotal[totIdx] = (ShadowVeryShortPeriodTotal[totIdx]+(TA_CANDLERANGE(ShadowVeryShort,(i-totIdx))-TA_CANDLERANGE(ShadowVeryShort,(ShadowVeryShortTrailingIdx-totIdx))));
      }
      for( totIdx = 2; (totIdx>=1); totIdx -= 1 )
      {
         FarPeriodTotal[totIdx] = (FarPeriodTotal[totIdx]+(TA_CANDLERANGE(Far,(i-totIdx))-TA_CANDLERANGE(Far,(FarTrailingIdx-totIdx))));
         NearPeriodTotal[totIdx] = (NearPeriodTotal[totIdx]+(TA_CANDLERANGE(Near,(i-totIdx))-TA_CANDLERANGE(Near,(NearTrailingIdx-totIdx))));
      }
      BodyShortPeriodTotal += (TA_CANDLERANGE(BodyShort,i)-TA_CANDLERANGE(BodyShort,BodyShortTrailingIdx));
      i += 1;
      ShadowVeryShortTrailingIdx += 1;
      NearTrailingIdx += 1;
      FarTrailingIdx += 1;
      BodyShortTrailingIdx += 1;
   } while( (i<=endIdx) );
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

