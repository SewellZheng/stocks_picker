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

TA_LIB_API int TA_AROONOSC_Lookback( int optInTimePeriod )
{
   return optInTimePeriod;
}

TA_LIB_API TA_RetCode TA_AROONOSC( int    startIdx,
                                   int    endIdx,
                                   const double inHigh[],
                                   const double inLow[],
                                   int optInTimePeriod,
                                   int          *outBegIdx,
                                   int          *outNBElement,
                                   double        outReal[] )
{
   double lowest;
   double highest;
   double tmp;
   double factor;
   double aroon;
   int outIdx;
   int trailingIdx;
   int lowestIdx;
   int highestIdx;
   int today;
   int i;

   if( startIdx < 0 )
      return TA_OUT_OF_RANGE_START_INDEX;
   if( (endIdx < 0) || (endIdx < startIdx) )
      return TA_OUT_OF_RANGE_END_INDEX;

   if( !inHigh )
      return TA_BAD_PARAM;
   if( !inLow )
      return TA_BAD_PARAM;
   if( (int)optInTimePeriod == (int)0x80000000 )
      optInTimePeriod = 14;
   else if( (int)optInTimePeriod < 2 || (int)optInTimePeriod > 100000 )
      return TA_BAD_PARAM;
   if( !outReal )
      return TA_BAD_PARAM;

   if( (startIdx<optInTimePeriod) )
   {
      startIdx = optInTimePeriod;
   }
   if( (startIdx>endIdx) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      return TA_SUCCESS;
   }
   outIdx = 0;
   today = startIdx;
   trailingIdx = (startIdx-optInTimePeriod);
   lowestIdx = (0-1);
   highestIdx = (0-1);
   lowest = 0.0;
   highest = 0.0;
   factor = (((double)100.0)/((double)optInTimePeriod));
   while( (today<=endIdx) )
   {
      tmp = inLow[today];
      if( (lowestIdx<trailingIdx) )
      {
         lowestIdx = trailingIdx;
         lowest = inLow[lowestIdx];
         i = lowestIdx;
         while( (++i<=today) )
         {
            tmp = inLow[i];
            if( (tmp<=lowest) )
            {
               lowestIdx = i;
               lowest = tmp;
            }
         }
      } else if( (tmp<=lowest) )
      {
         lowestIdx = today;
         lowest = tmp;
      }
      tmp = inHigh[today];
      if( (highestIdx<trailingIdx) )
      {
         highestIdx = trailingIdx;
         highest = inHigh[highestIdx];
         i = highestIdx;
         while( (++i<=today) )
         {
            tmp = inHigh[i];
            if( (tmp>=highest) )
            {
               highestIdx = i;
               highest = tmp;
            }
         }
      } else if( (tmp>=highest) )
      {
         highestIdx = today;
         highest = tmp;
      }
      aroon = (factor*(highestIdx-lowestIdx));
      outReal[outIdx] = aroon;
      outIdx += 1;
      trailingIdx += 1;
      today += 1;
   }
   *outBegIdx= startIdx;
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_LIB_API TA_RetCode TA_AROONOSC_Unguarded( int    startIdx,
                                             int    endIdx,
                                             const double inHigh[],
                                             const double inLow[],
                                             int optInTimePeriod,
                                             int          *outBegIdx,
                                             int          *outNBElement,
                                             double        outReal[] )
{
   double lowest;
   double highest;
   double tmp;
   double factor;
   double aroon;
   int outIdx;
   int trailingIdx;
   int lowestIdx;
   int highestIdx;
   int today;
   int i;

   if( (startIdx<optInTimePeriod) )
   {
      startIdx = optInTimePeriod;
   }
   if( (startIdx>endIdx) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      return TA_SUCCESS;
   }
   outIdx = 0;
   today = startIdx;
   trailingIdx = (startIdx-optInTimePeriod);
   lowestIdx = (0-1);
   highestIdx = (0-1);
   lowest = 0.0;
   highest = 0.0;
   factor = (((double)100.0)/((double)optInTimePeriod));
   while( (today<=endIdx) )
   {
      tmp = inLow[today];
      if( (lowestIdx<trailingIdx) )
      {
         lowestIdx = trailingIdx;
         lowest = inLow[lowestIdx];
         i = lowestIdx;
         while( (++i<=today) )
         {
            tmp = inLow[i];
            if( (tmp<=lowest) )
            {
               lowestIdx = i;
               lowest = tmp;
            }
         }
      } else if( (tmp<=lowest) )
      {
         lowestIdx = today;
         lowest = tmp;
      }
      tmp = inHigh[today];
      if( (highestIdx<trailingIdx) )
      {
         highestIdx = trailingIdx;
         highest = inHigh[highestIdx];
         i = highestIdx;
         while( (++i<=today) )
         {
            tmp = inHigh[i];
            if( (tmp>=highest) )
            {
               highestIdx = i;
               highest = tmp;
            }
         }
      } else if( (tmp>=highest) )
      {
         highestIdx = today;
         highest = tmp;
      }
      aroon = (factor*(highestIdx-lowestIdx));
      outReal[outIdx] = aroon;
      outIdx += 1;
      trailingIdx += 1;
      today += 1;
   }
   *outBegIdx= startIdx;
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_AROONOSC( int    startIdx,
                          int    endIdx,
                          const float inHigh[],
                          const float inLow[],
                          int optInTimePeriod,
                          int          *outBegIdx,
                          int          *outNBElement,
                          double        outReal[] )
{
   double lowest;
   double highest;
   double tmp;
   double factor;
   double aroon;
   int outIdx;
   int trailingIdx;
   int lowestIdx;
   int highestIdx;
   int today;
   int i;

   if( startIdx < 0 )
      return TA_OUT_OF_RANGE_START_INDEX;
   if( (endIdx < 0) || (endIdx < startIdx) )
      return TA_OUT_OF_RANGE_END_INDEX;

   if( !inHigh )
      return TA_BAD_PARAM;
   if( !inLow )
      return TA_BAD_PARAM;
   if( (int)optInTimePeriod == (int)0x80000000 )
      optInTimePeriod = 14;
   else if( (int)optInTimePeriod < 2 || (int)optInTimePeriod > 100000 )
      return TA_BAD_PARAM;
   if( !outReal )
      return TA_BAD_PARAM;

   if( (startIdx<optInTimePeriod) )
   {
      startIdx = optInTimePeriod;
   }
   if( (startIdx>endIdx) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      return TA_SUCCESS;
   }
   outIdx = 0;
   today = startIdx;
   trailingIdx = (startIdx-optInTimePeriod);
   lowestIdx = (0-1);
   highestIdx = (0-1);
   lowest = 0.0;
   highest = 0.0;
   factor = (((double)100.0)/((double)optInTimePeriod));
   while( (today<=endIdx) )
   {
      tmp = inLow[today];
      if( (lowestIdx<trailingIdx) )
      {
         lowestIdx = trailingIdx;
         lowest = inLow[lowestIdx];
         i = lowestIdx;
         while( (++i<=today) )
         {
            tmp = inLow[i];
            if( (tmp<=lowest) )
            {
               lowestIdx = i;
               lowest = tmp;
            }
         }
      } else if( (tmp<=lowest) )
      {
         lowestIdx = today;
         lowest = tmp;
      }
      tmp = inHigh[today];
      if( (highestIdx<trailingIdx) )
      {
         highestIdx = trailingIdx;
         highest = inHigh[highestIdx];
         i = highestIdx;
         while( (++i<=today) )
         {
            tmp = inHigh[i];
            if( (tmp>=highest) )
            {
               highestIdx = i;
               highest = tmp;
            }
         }
      } else if( (tmp>=highest) )
      {
         highestIdx = today;
         highest = tmp;
      }
      aroon = (factor*(highestIdx-lowestIdx));
      outReal[outIdx] = aroon;
      outIdx += 1;
      trailingIdx += 1;
      today += 1;
   }
   *outBegIdx= startIdx;
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_AROONOSC_Unguarded( int    startIdx,
                                    int    endIdx,
                                    const float inHigh[],
                                    const float inLow[],
                                    int optInTimePeriod,
                                    int          *outBegIdx,
                                    int          *outNBElement,
                                    double        outReal[] )
{
   double lowest;
   double highest;
   double tmp;
   double factor;
   double aroon;
   int outIdx;
   int trailingIdx;
   int lowestIdx;
   int highestIdx;
   int today;
   int i;

   if( (startIdx<optInTimePeriod) )
   {
      startIdx = optInTimePeriod;
   }
   if( (startIdx>endIdx) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      return TA_SUCCESS;
   }
   outIdx = 0;
   today = startIdx;
   trailingIdx = (startIdx-optInTimePeriod);
   lowestIdx = (0-1);
   highestIdx = (0-1);
   lowest = 0.0;
   highest = 0.0;
   factor = (((double)100.0)/((double)optInTimePeriod));
   while( (today<=endIdx) )
   {
      tmp = inLow[today];
      if( (lowestIdx<trailingIdx) )
      {
         lowestIdx = trailingIdx;
         lowest = inLow[lowestIdx];
         i = lowestIdx;
         while( (++i<=today) )
         {
            tmp = inLow[i];
            if( (tmp<=lowest) )
            {
               lowestIdx = i;
               lowest = tmp;
            }
         }
      } else if( (tmp<=lowest) )
      {
         lowestIdx = today;
         lowest = tmp;
      }
      tmp = inHigh[today];
      if( (highestIdx<trailingIdx) )
      {
         highestIdx = trailingIdx;
         highest = inHigh[highestIdx];
         i = highestIdx;
         while( (++i<=today) )
         {
            tmp = inHigh[i];
            if( (tmp>=highest) )
            {
               highestIdx = i;
               highest = tmp;
            }
         }
      } else if( (tmp>=highest) )
      {
         highestIdx = today;
         highest = tmp;
      }
      aroon = (factor*(highestIdx-lowestIdx));
      outReal[outIdx] = aroon;
      outIdx += 1;
      trailingIdx += 1;
      today += 1;
   }
   *outBegIdx= startIdx;
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

