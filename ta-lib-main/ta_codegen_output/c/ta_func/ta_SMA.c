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

TA_LIB_API int TA_SMA_Lookback( int optInTimePeriod )
{
   return (optInTimePeriod-1);
}

TA_LIB_API TA_RetCode TA_SMA( int    startIdx,
                              int    endIdx,
                              const double inReal[],
                              int optInTimePeriod,
                              int          *outBegIdx,
                              int          *outNBElement,
                              double        outReal[] )
{
   double periodTotal;
   double tempReal;
   int i;
   int outIdx;
   int trailingIdx;
   int lookbackTotal;

   if( startIdx < 0 )
      return TA_OUT_OF_RANGE_START_INDEX;
   if( (endIdx < 0) || (endIdx < startIdx) )
      return TA_OUT_OF_RANGE_END_INDEX;

   if( !inReal )
      return TA_BAD_PARAM;
   if( (int)optInTimePeriod == (int)0x80000000 )
      optInTimePeriod = 30;
   else if( (int)optInTimePeriod < 2 || (int)optInTimePeriod > 100000 )
      return TA_BAD_PARAM;
   if( !outReal )
      return TA_BAD_PARAM;

   lookbackTotal = ((int)(optInTimePeriod-1));
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
   periodTotal = 0.0;
   trailingIdx = (startIdx-lookbackTotal);
   i = trailingIdx;
   if( (optInTimePeriod>1) )
   {
      while( (i<startIdx) )
      {
         periodTotal += ((double)inReal[i]);
         i = (i+1);
      }
   }
   outIdx = 0;
   while( (i<=endIdx) )
   {
      periodTotal += ((double)inReal[i]);
      i = (i+1);
      tempReal = periodTotal;
      periodTotal -= ((double)inReal[trailingIdx]);
      trailingIdx = (trailingIdx+1);
      outReal[outIdx] = (tempReal/((double)optInTimePeriod));
      outIdx = (outIdx+1);
   }
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_LIB_API TA_RetCode TA_SMA_Unguarded( int    startIdx,
                                        int    endIdx,
                                        const double inReal[],
                                        int optInTimePeriod,
                                        int          *outBegIdx,
                                        int          *outNBElement,
                                        double        outReal[] )
{
   double periodTotal;
   double tempReal;
   int i;
   int outIdx;
   int trailingIdx;
   int lookbackTotal;

   lookbackTotal = ((int)(optInTimePeriod-1));
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
   periodTotal = 0.0;
   trailingIdx = (startIdx-lookbackTotal);
   i = trailingIdx;
   if( (optInTimePeriod>1) )
   {
      while( (i<startIdx) )
      {
         periodTotal += ((double)inReal[i]);
         i = (i+1);
      }
   }
   outIdx = 0;
   while( (i<=endIdx) )
   {
      periodTotal += ((double)inReal[i]);
      i = (i+1);
      tempReal = periodTotal;
      periodTotal -= ((double)inReal[trailingIdx]);
      trailingIdx = (trailingIdx+1);
      outReal[outIdx] = (tempReal/((double)optInTimePeriod));
      outIdx = (outIdx+1);
   }
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_SMA( int    startIdx,
                     int    endIdx,
                     const float inReal[],
                     int optInTimePeriod,
                     int          *outBegIdx,
                     int          *outNBElement,
                     double        outReal[] )
{
   double periodTotal;
   double tempReal;
   int i;
   int outIdx;
   int trailingIdx;
   int lookbackTotal;

   if( startIdx < 0 )
      return TA_OUT_OF_RANGE_START_INDEX;
   if( (endIdx < 0) || (endIdx < startIdx) )
      return TA_OUT_OF_RANGE_END_INDEX;

   if( !inReal )
      return TA_BAD_PARAM;
   if( (int)optInTimePeriod == (int)0x80000000 )
      optInTimePeriod = 30;
   else if( (int)optInTimePeriod < 2 || (int)optInTimePeriod > 100000 )
      return TA_BAD_PARAM;
   if( !outReal )
      return TA_BAD_PARAM;

   lookbackTotal = ((int)(optInTimePeriod-1));
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
   periodTotal = 0.0;
   trailingIdx = (startIdx-lookbackTotal);
   i = trailingIdx;
   if( (optInTimePeriod>1) )
   {
      while( (i<startIdx) )
      {
         periodTotal += ((double)inReal[i]);
         i = (i+1);
      }
   }
   outIdx = 0;
   while( (i<=endIdx) )
   {
      periodTotal += ((double)inReal[i]);
      i = (i+1);
      tempReal = periodTotal;
      periodTotal -= ((double)inReal[trailingIdx]);
      trailingIdx = (trailingIdx+1);
      outReal[outIdx] = (tempReal/((double)optInTimePeriod));
      outIdx = (outIdx+1);
   }
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_SMA_Unguarded( int    startIdx,
                               int    endIdx,
                               const float inReal[],
                               int optInTimePeriod,
                               int          *outBegIdx,
                               int          *outNBElement,
                               double        outReal[] )
{
   double periodTotal;
   double tempReal;
   int i;
   int outIdx;
   int trailingIdx;
   int lookbackTotal;

   lookbackTotal = ((int)(optInTimePeriod-1));
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
   periodTotal = 0.0;
   trailingIdx = (startIdx-lookbackTotal);
   i = trailingIdx;
   if( (optInTimePeriod>1) )
   {
      while( (i<startIdx) )
      {
         periodTotal += ((double)inReal[i]);
         i = (i+1);
      }
   }
   outIdx = 0;
   while( (i<=endIdx) )
   {
      periodTotal += ((double)inReal[i]);
      i = (i+1);
      tempReal = periodTotal;
      periodTotal -= ((double)inReal[trailingIdx]);
      trailingIdx = (trailingIdx+1);
      outReal[outIdx] = (tempReal/((double)optInTimePeriod));
      outIdx = (outIdx+1);
   }
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

