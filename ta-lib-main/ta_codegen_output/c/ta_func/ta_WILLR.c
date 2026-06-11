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

TA_LIB_API int TA_WILLR_Lookback( int optInTimePeriod )
{
   return (optInTimePeriod-1);
}

TA_LIB_API TA_RetCode TA_WILLR( int    startIdx,
                                int    endIdx,
                                const double inHigh[],
                                const double inLow[],
                                const double inClose[],
                                int optInTimePeriod,
                                int          *outBegIdx,
                                int          *outNBElement,
                                double        outReal[] )
{
   double lowest;
   double highest;
   double tmp;
   double diff;
   int outIdx;
   int nbInitialElementNeeded;
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
   if( !inClose )
      return TA_BAD_PARAM;
   if( (int)optInTimePeriod == (int)0x80000000 )
      optInTimePeriod = 14;
   else if( (int)optInTimePeriod < 2 || (int)optInTimePeriod > 100000 )
      return TA_BAD_PARAM;
   if( !outReal )
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
   diff = 0.0;
   outIdx = 0;
   today = startIdx;
   trailingIdx = (startIdx-nbInitialElementNeeded);
   highestIdx = (0-1);
   lowestIdx = highestIdx;
   lowest = 0.0;
   highest = lowest;
   diff = highest;
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
            if( (tmp<lowest) )
            {
               lowestIdx = i;
               lowest = tmp;
            }
         }
         diff = ((highest-lowest)/(0-100.0));
      } else if( (tmp<=lowest) )
      {
         lowestIdx = today;
         lowest = tmp;
         diff = ((highest-lowest)/(0-100.0));
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
            if( (tmp>highest) )
            {
               highestIdx = i;
               highest = tmp;
            }
         }
         diff = ((highest-lowest)/(0-100.0));
      } else if( (tmp>=highest) )
      {
         highestIdx = today;
         highest = tmp;
         diff = ((highest-lowest)/(0-100.0));
      }
      if( (diff!=0.0) )
      {
         outReal[outIdx++] = ((highest-inClose[today])/diff);
      } else 
      {
         outReal[outIdx++] = 0.0;
      }
      trailingIdx += 1;
      today += 1;
   }
   *outBegIdx= startIdx;
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_LIB_API TA_RetCode TA_WILLR_Unguarded( int    startIdx,
                                          int    endIdx,
                                          const double inHigh[],
                                          const double inLow[],
                                          const double inClose[],
                                          int optInTimePeriod,
                                          int          *outBegIdx,
                                          int          *outNBElement,
                                          double        outReal[] )
{
   double lowest;
   double highest;
   double tmp;
   double diff;
   int outIdx;
   int nbInitialElementNeeded;
   int trailingIdx;
   int lowestIdx;
   int highestIdx;
   int today;
   int i;

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
   diff = 0.0;
   outIdx = 0;
   today = startIdx;
   trailingIdx = (startIdx-nbInitialElementNeeded);
   highestIdx = (0-1);
   lowestIdx = highestIdx;
   lowest = 0.0;
   highest = lowest;
   diff = highest;
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
            if( (tmp<lowest) )
            {
               lowestIdx = i;
               lowest = tmp;
            }
         }
         diff = ((highest-lowest)/(0-100.0));
      } else if( (tmp<=lowest) )
      {
         lowestIdx = today;
         lowest = tmp;
         diff = ((highest-lowest)/(0-100.0));
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
            if( (tmp>highest) )
            {
               highestIdx = i;
               highest = tmp;
            }
         }
         diff = ((highest-lowest)/(0-100.0));
      } else if( (tmp>=highest) )
      {
         highestIdx = today;
         highest = tmp;
         diff = ((highest-lowest)/(0-100.0));
      }
      if( (diff!=0.0) )
      {
         outReal[outIdx++] = ((highest-inClose[today])/diff);
      } else 
      {
         outReal[outIdx++] = 0.0;
      }
      trailingIdx += 1;
      today += 1;
   }
   *outBegIdx= startIdx;
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_WILLR( int    startIdx,
                       int    endIdx,
                       const float inHigh[],
                       const float inLow[],
                       const float inClose[],
                       int optInTimePeriod,
                       int          *outBegIdx,
                       int          *outNBElement,
                       double        outReal[] )
{
   double lowest;
   double highest;
   double tmp;
   double diff;
   int outIdx;
   int nbInitialElementNeeded;
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
   if( !inClose )
      return TA_BAD_PARAM;
   if( (int)optInTimePeriod == (int)0x80000000 )
      optInTimePeriod = 14;
   else if( (int)optInTimePeriod < 2 || (int)optInTimePeriod > 100000 )
      return TA_BAD_PARAM;
   if( !outReal )
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
   diff = 0.0;
   outIdx = 0;
   today = startIdx;
   trailingIdx = (startIdx-nbInitialElementNeeded);
   highestIdx = (0-1);
   lowestIdx = highestIdx;
   lowest = 0.0;
   highest = lowest;
   diff = highest;
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
            if( (tmp<lowest) )
            {
               lowestIdx = i;
               lowest = tmp;
            }
         }
         diff = ((highest-lowest)/(0-100.0));
      } else if( (tmp<=lowest) )
      {
         lowestIdx = today;
         lowest = tmp;
         diff = ((highest-lowest)/(0-100.0));
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
            if( (tmp>highest) )
            {
               highestIdx = i;
               highest = tmp;
            }
         }
         diff = ((highest-lowest)/(0-100.0));
      } else if( (tmp>=highest) )
      {
         highestIdx = today;
         highest = tmp;
         diff = ((highest-lowest)/(0-100.0));
      }
      if( (diff!=0.0) )
      {
         outReal[outIdx++] = ((highest-inClose[today])/diff);
      } else 
      {
         outReal[outIdx++] = 0.0;
      }
      trailingIdx += 1;
      today += 1;
   }
   *outBegIdx= startIdx;
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_WILLR_Unguarded( int    startIdx,
                                 int    endIdx,
                                 const float inHigh[],
                                 const float inLow[],
                                 const float inClose[],
                                 int optInTimePeriod,
                                 int          *outBegIdx,
                                 int          *outNBElement,
                                 double        outReal[] )
{
   double lowest;
   double highest;
   double tmp;
   double diff;
   int outIdx;
   int nbInitialElementNeeded;
   int trailingIdx;
   int lowestIdx;
   int highestIdx;
   int today;
   int i;

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
   diff = 0.0;
   outIdx = 0;
   today = startIdx;
   trailingIdx = (startIdx-nbInitialElementNeeded);
   highestIdx = (0-1);
   lowestIdx = highestIdx;
   lowest = 0.0;
   highest = lowest;
   diff = highest;
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
            if( (tmp<lowest) )
            {
               lowestIdx = i;
               lowest = tmp;
            }
         }
         diff = ((highest-lowest)/(0-100.0));
      } else if( (tmp<=lowest) )
      {
         lowestIdx = today;
         lowest = tmp;
         diff = ((highest-lowest)/(0-100.0));
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
            if( (tmp>highest) )
            {
               highestIdx = i;
               highest = tmp;
            }
         }
         diff = ((highest-lowest)/(0-100.0));
      } else if( (tmp>=highest) )
      {
         highestIdx = today;
         highest = tmp;
         diff = ((highest-lowest)/(0-100.0));
      }
      if( (diff!=0.0) )
      {
         outReal[outIdx++] = ((highest-inClose[today])/diff);
      } else 
      {
         outReal[outIdx++] = 0.0;
      }
      trailingIdx += 1;
      today += 1;
   }
   *outBegIdx= startIdx;
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

