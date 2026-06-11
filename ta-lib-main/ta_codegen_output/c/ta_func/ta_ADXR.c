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

TA_LIB_API int TA_ADXR_Lookback( int optInTimePeriod )
{
   if( (optInTimePeriod>1) )
   {
      return ((optInTimePeriod+TA_ADX_Lookback(optInTimePeriod))-1);
   } else 
   {
      return 3;
   }
}

TA_LIB_API TA_RetCode TA_ADXR( int    startIdx,
                               int    endIdx,
                               const double inHigh[],
                               const double inLow[],
                               const double inClose[],
                               int optInTimePeriod,
                               int          *outBegIdx,
                               int          *outNBElement,
                               double        outReal[] )
{
   double *adx;
   int adxrLookback;
   int i;
   int j;
   int outIdx;
   int nbElement;
   TA_RetCode retCode;

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
   if( (int)optInTimePeriod == (int)0x80000000 )
      optInTimePeriod = 14;
   else if( (int)optInTimePeriod < 2 || (int)optInTimePeriod > 100000 )
      return TA_BAD_PARAM;
   if( !outReal )
      return TA_BAD_PARAM;

   adxrLookback = TA_ADXR_Lookback(optInTimePeriod);
   if( (startIdx<adxrLookback) )
   {
      startIdx = adxrLookback;
   }
   if( (startIdx>endIdx) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      return TA_SUCCESS;
   }
   adx = malloc((((endIdx-startIdx)+optInTimePeriod)*sizeof(double)));
   if( !(adx) )
   {
      return TA_ALLOC_ERR;
   }
   retCode = TA_ADX_Unguarded((startIdx-(optInTimePeriod-1)),endIdx,inHigh,inLow,inClose,optInTimePeriod,outBegIdx,outNBElement,adx);
   if( (retCode!=TA_SUCCESS) )
   {
      free(adx);
      return retCode;
   }
   i = (optInTimePeriod-1);
   j = 0;
   outIdx = 0;
   nbElement = ((endIdx-startIdx)+2);
   while( (--nbElement!=0) )
   {
      outReal[outIdx++] = ((adx[i++]+adx[j++])/2.0);
   }
   free(adx);
   *outBegIdx= startIdx;
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_LIB_API TA_RetCode TA_ADXR_Unguarded( int    startIdx,
                                         int    endIdx,
                                         const double inHigh[],
                                         const double inLow[],
                                         const double inClose[],
                                         int optInTimePeriod,
                                         int          *outBegIdx,
                                         int          *outNBElement,
                                         double        outReal[] )
{
   double *adx;
   int adxrLookback;
   int i;
   int j;
   int outIdx;
   int nbElement;
   TA_RetCode retCode;

   adxrLookback = TA_ADXR_Lookback(optInTimePeriod);
   if( (startIdx<adxrLookback) )
   {
      startIdx = adxrLookback;
   }
   if( (startIdx>endIdx) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      return TA_SUCCESS;
   }
   adx = malloc((((endIdx-startIdx)+optInTimePeriod)*sizeof(double)));
   if( !(adx) )
   {
      return TA_ALLOC_ERR;
   }
   retCode = TA_ADX_Unguarded((startIdx-(optInTimePeriod-1)),endIdx,inHigh,inLow,inClose,optInTimePeriod,outBegIdx,outNBElement,adx);
   if( (retCode!=TA_SUCCESS) )
   {
      free(adx);
      return retCode;
   }
   i = (optInTimePeriod-1);
   j = 0;
   outIdx = 0;
   nbElement = ((endIdx-startIdx)+2);
   while( (--nbElement!=0) )
   {
      outReal[outIdx++] = ((adx[i++]+adx[j++])/2.0);
   }
   free(adx);
   *outBegIdx= startIdx;
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_ADXR( int    startIdx,
                      int    endIdx,
                      const float inHigh[],
                      const float inLow[],
                      const float inClose[],
                      int optInTimePeriod,
                      int          *outBegIdx,
                      int          *outNBElement,
                      double        outReal[] )
{
   double *adx;
   int adxrLookback;
   int i;
   int j;
   int outIdx;
   int nbElement;
   TA_RetCode retCode;

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
   if( (int)optInTimePeriod == (int)0x80000000 )
      optInTimePeriod = 14;
   else if( (int)optInTimePeriod < 2 || (int)optInTimePeriod > 100000 )
      return TA_BAD_PARAM;
   if( !outReal )
      return TA_BAD_PARAM;

   adxrLookback = TA_ADXR_Lookback(optInTimePeriod);
   if( (startIdx<adxrLookback) )
   {
      startIdx = adxrLookback;
   }
   if( (startIdx>endIdx) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      return TA_SUCCESS;
   }
   adx = malloc((((endIdx-startIdx)+optInTimePeriod)*sizeof(double)));
   if( !(adx) )
   {
      return TA_ALLOC_ERR;
   }
   retCode = TA_S_ADX_Unguarded((startIdx-(optInTimePeriod-1)),endIdx,inHigh,inLow,inClose,optInTimePeriod,outBegIdx,outNBElement,adx);
   if( (retCode!=TA_SUCCESS) )
   {
      free(adx);
      return retCode;
   }
   i = (optInTimePeriod-1);
   j = 0;
   outIdx = 0;
   nbElement = ((endIdx-startIdx)+2);
   while( (--nbElement!=0) )
   {
      outReal[outIdx++] = ((adx[i++]+adx[j++])/2.0);
   }
   free(adx);
   *outBegIdx= startIdx;
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_ADXR_Unguarded( int    startIdx,
                                int    endIdx,
                                const float inHigh[],
                                const float inLow[],
                                const float inClose[],
                                int optInTimePeriod,
                                int          *outBegIdx,
                                int          *outNBElement,
                                double        outReal[] )
{
   double *adx;
   int adxrLookback;
   int i;
   int j;
   int outIdx;
   int nbElement;
   TA_RetCode retCode;

   adxrLookback = TA_ADXR_Lookback(optInTimePeriod);
   if( (startIdx<adxrLookback) )
   {
      startIdx = adxrLookback;
   }
   if( (startIdx>endIdx) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      return TA_SUCCESS;
   }
   adx = malloc((((endIdx-startIdx)+optInTimePeriod)*sizeof(double)));
   if( !(adx) )
   {
      return TA_ALLOC_ERR;
   }
   retCode = TA_S_ADX_Unguarded((startIdx-(optInTimePeriod-1)),endIdx,inHigh,inLow,inClose,optInTimePeriod,outBegIdx,outNBElement,adx);
   if( (retCode!=TA_SUCCESS) )
   {
      free(adx);
      return retCode;
   }
   i = (optInTimePeriod-1);
   j = 0;
   outIdx = 0;
   nbElement = ((endIdx-startIdx)+2);
   while( (--nbElement!=0) )
   {
      outReal[outIdx++] = ((adx[i++]+adx[j++])/2.0);
   }
   free(adx);
   *outBegIdx= startIdx;
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

