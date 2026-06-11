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

TA_LIB_API int TA_MINMAX_Lookback( int optInTimePeriod )
{
   return (optInTimePeriod-1);
}

TA_LIB_API TA_RetCode TA_MINMAX( int    startIdx,
                                 int    endIdx,
                                 const double inReal[],
                                 int optInTimePeriod,
                                 int          *outBegIdx,
                                 int          *outNBElement,
                                 double        outMin[],
                                 double        outMax[] )
{
   double highest;
   double lowest;
   double tmpHigh;
   double tmpLow;
   int outIdx;
   int nbInitialElementNeeded;
   int trailingIdx;
   int today;
   int i;
   int highestIdx;
   int lowestIdx;

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
   if( !outMin )
      return TA_BAD_PARAM;
   if( !outMax )
      return TA_BAD_PARAM;

   nbInitialElementNeeded = (optInTimePeriod-1);
   if( (startIdx<nbInitialElementNeeded) )
   {
      startIdx = nbInitialElementNeeded;
   }
   if( (startIdx>endIdx) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      return TA_SUCCESS;
   }
   outIdx = 0;
   today = startIdx;
   trailingIdx = (startIdx-nbInitialElementNeeded);
   highestIdx = (0-1);
   highest = 0.0;
   lowestIdx = (0-1);
   lowest = 0.0;
   while( (today<=endIdx) )
   {
      tmpHigh = inReal[today];
      tmpLow = tmpHigh;
      if( (highestIdx<trailingIdx) )
      {
         highestIdx = trailingIdx;
         highest = inReal[highestIdx];
         i = highestIdx;
         while( (++i<=today) )
         {
            tmpHigh = inReal[i];
            if( (tmpHigh>highest) )
            {
               highestIdx = i;
               highest = tmpHigh;
            }
         }
      } else if( (tmpHigh>=highest) )
      {
         highestIdx = today;
         highest = tmpHigh;
      }
      if( (lowestIdx<trailingIdx) )
      {
         lowestIdx = trailingIdx;
         lowest = inReal[lowestIdx];
         i = lowestIdx;
         while( (++i<=today) )
         {
            tmpLow = inReal[i];
            if( (tmpLow<lowest) )
            {
               lowestIdx = i;
               lowest = tmpLow;
            }
         }
      } else if( (tmpLow<=lowest) )
      {
         lowestIdx = today;
         lowest = tmpLow;
      }
      outMax[outIdx] = highest;
      outMin[outIdx] = lowest;
      outIdx += 1;
      trailingIdx += 1;
      today += 1;
   }
   *outBegIdx= startIdx;
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_LIB_API TA_RetCode TA_MINMAX_Unguarded( int    startIdx,
                                           int    endIdx,
                                           const double inReal[],
                                           int optInTimePeriod,
                                           int          *outBegIdx,
                                           int          *outNBElement,
                                           double        outMin[],
                                           double        outMax[] )
{
   double highest;
   double lowest;
   double tmpHigh;
   double tmpLow;
   int outIdx;
   int nbInitialElementNeeded;
   int trailingIdx;
   int today;
   int i;
   int highestIdx;
   int lowestIdx;

   nbInitialElementNeeded = (optInTimePeriod-1);
   if( (startIdx<nbInitialElementNeeded) )
   {
      startIdx = nbInitialElementNeeded;
   }
   if( (startIdx>endIdx) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      return TA_SUCCESS;
   }
   outIdx = 0;
   today = startIdx;
   trailingIdx = (startIdx-nbInitialElementNeeded);
   highestIdx = (0-1);
   highest = 0.0;
   lowestIdx = (0-1);
   lowest = 0.0;
   while( (today<=endIdx) )
   {
      tmpHigh = inReal[today];
      tmpLow = tmpHigh;
      if( (highestIdx<trailingIdx) )
      {
         highestIdx = trailingIdx;
         highest = inReal[highestIdx];
         i = highestIdx;
         while( (++i<=today) )
         {
            tmpHigh = inReal[i];
            if( (tmpHigh>highest) )
            {
               highestIdx = i;
               highest = tmpHigh;
            }
         }
      } else if( (tmpHigh>=highest) )
      {
         highestIdx = today;
         highest = tmpHigh;
      }
      if( (lowestIdx<trailingIdx) )
      {
         lowestIdx = trailingIdx;
         lowest = inReal[lowestIdx];
         i = lowestIdx;
         while( (++i<=today) )
         {
            tmpLow = inReal[i];
            if( (tmpLow<lowest) )
            {
               lowestIdx = i;
               lowest = tmpLow;
            }
         }
      } else if( (tmpLow<=lowest) )
      {
         lowestIdx = today;
         lowest = tmpLow;
      }
      outMax[outIdx] = highest;
      outMin[outIdx] = lowest;
      outIdx += 1;
      trailingIdx += 1;
      today += 1;
   }
   *outBegIdx= startIdx;
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_MINMAX( int    startIdx,
                        int    endIdx,
                        const float inReal[],
                        int optInTimePeriod,
                        int          *outBegIdx,
                        int          *outNBElement,
                        double        outMin[],
                        double        outMax[] )
{
   double highest;
   double lowest;
   double tmpHigh;
   double tmpLow;
   int outIdx;
   int nbInitialElementNeeded;
   int trailingIdx;
   int today;
   int i;
   int highestIdx;
   int lowestIdx;

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
   if( !outMin )
      return TA_BAD_PARAM;
   if( !outMax )
      return TA_BAD_PARAM;

   nbInitialElementNeeded = (optInTimePeriod-1);
   if( (startIdx<nbInitialElementNeeded) )
   {
      startIdx = nbInitialElementNeeded;
   }
   if( (startIdx>endIdx) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      return TA_SUCCESS;
   }
   outIdx = 0;
   today = startIdx;
   trailingIdx = (startIdx-nbInitialElementNeeded);
   highestIdx = (0-1);
   highest = 0.0;
   lowestIdx = (0-1);
   lowest = 0.0;
   while( (today<=endIdx) )
   {
      tmpHigh = inReal[today];
      tmpLow = tmpHigh;
      if( (highestIdx<trailingIdx) )
      {
         highestIdx = trailingIdx;
         highest = inReal[highestIdx];
         i = highestIdx;
         while( (++i<=today) )
         {
            tmpHigh = inReal[i];
            if( (tmpHigh>highest) )
            {
               highestIdx = i;
               highest = tmpHigh;
            }
         }
      } else if( (tmpHigh>=highest) )
      {
         highestIdx = today;
         highest = tmpHigh;
      }
      if( (lowestIdx<trailingIdx) )
      {
         lowestIdx = trailingIdx;
         lowest = inReal[lowestIdx];
         i = lowestIdx;
         while( (++i<=today) )
         {
            tmpLow = inReal[i];
            if( (tmpLow<lowest) )
            {
               lowestIdx = i;
               lowest = tmpLow;
            }
         }
      } else if( (tmpLow<=lowest) )
      {
         lowestIdx = today;
         lowest = tmpLow;
      }
      outMax[outIdx] = highest;
      outMin[outIdx] = lowest;
      outIdx += 1;
      trailingIdx += 1;
      today += 1;
   }
   *outBegIdx= startIdx;
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_MINMAX_Unguarded( int    startIdx,
                                  int    endIdx,
                                  const float inReal[],
                                  int optInTimePeriod,
                                  int          *outBegIdx,
                                  int          *outNBElement,
                                  double        outMin[],
                                  double        outMax[] )
{
   double highest;
   double lowest;
   double tmpHigh;
   double tmpLow;
   int outIdx;
   int nbInitialElementNeeded;
   int trailingIdx;
   int today;
   int i;
   int highestIdx;
   int lowestIdx;

   nbInitialElementNeeded = (optInTimePeriod-1);
   if( (startIdx<nbInitialElementNeeded) )
   {
      startIdx = nbInitialElementNeeded;
   }
   if( (startIdx>endIdx) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      return TA_SUCCESS;
   }
   outIdx = 0;
   today = startIdx;
   trailingIdx = (startIdx-nbInitialElementNeeded);
   highestIdx = (0-1);
   highest = 0.0;
   lowestIdx = (0-1);
   lowest = 0.0;
   while( (today<=endIdx) )
   {
      tmpHigh = inReal[today];
      tmpLow = tmpHigh;
      if( (highestIdx<trailingIdx) )
      {
         highestIdx = trailingIdx;
         highest = inReal[highestIdx];
         i = highestIdx;
         while( (++i<=today) )
         {
            tmpHigh = inReal[i];
            if( (tmpHigh>highest) )
            {
               highestIdx = i;
               highest = tmpHigh;
            }
         }
      } else if( (tmpHigh>=highest) )
      {
         highestIdx = today;
         highest = tmpHigh;
      }
      if( (lowestIdx<trailingIdx) )
      {
         lowestIdx = trailingIdx;
         lowest = inReal[lowestIdx];
         i = lowestIdx;
         while( (++i<=today) )
         {
            tmpLow = inReal[i];
            if( (tmpLow<lowest) )
            {
               lowestIdx = i;
               lowest = tmpLow;
            }
         }
      } else if( (tmpLow<=lowest) )
      {
         lowestIdx = today;
         lowest = tmpLow;
      }
      outMax[outIdx] = highest;
      outMin[outIdx] = lowest;
      outIdx += 1;
      trailingIdx += 1;
      today += 1;
   }
   *outBegIdx= startIdx;
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

