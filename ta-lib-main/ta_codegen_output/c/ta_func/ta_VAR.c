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

TA_LIB_API int TA_VAR_Lookback( int optInTimePeriod, double optInNbDev )
{
   return (optInTimePeriod-1);
}

TA_LIB_API TA_RetCode TA_VAR( int    startIdx,
                              int    endIdx,
                              const double inReal[],
                              int optInTimePeriod,
                              double optInNbDev,
                              int          *outBegIdx,
                              int          *outNBElement,
                              double        outReal[] )
{
   double tempReal;
   double periodTotal1;
   double periodTotal2;
   double meanValue1;
   double meanValue2;
   int i;
   int outIdx;
   int trailingIdx;
   int nbInitialElementNeeded;

   if( startIdx < 0 )
      return TA_OUT_OF_RANGE_START_INDEX;
   if( (endIdx < 0) || (endIdx < startIdx) )
      return TA_OUT_OF_RANGE_END_INDEX;

   if( !inReal )
      return TA_BAD_PARAM;
   if( (int)optInTimePeriod == (int)0x80000000 )
      optInTimePeriod = 5;
   else if( (int)optInTimePeriod < 1 || (int)optInTimePeriod > 100000 )
      return TA_BAD_PARAM;
   if( optInNbDev == -4e37 )
      optInNbDev = 1;
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
   periodTotal1 = 0;
   periodTotal2 = 0;
   trailingIdx = (startIdx-nbInitialElementNeeded);
   i = trailingIdx;
   if( (optInTimePeriod>1) )
   {
      while( (i<startIdx) )
      {
         tempReal = inReal[i++];
         periodTotal1 += tempReal;
         tempReal *= tempReal;
         periodTotal2 += tempReal;
      }
   }
   outIdx = 0;
   do
   {
      tempReal = inReal[i++];
      periodTotal1 += tempReal;
      tempReal *= tempReal;
      periodTotal2 += tempReal;
      meanValue1 = (periodTotal1/optInTimePeriod);
      meanValue2 = (periodTotal2/optInTimePeriod);
      tempReal = inReal[trailingIdx++];
      periodTotal1 -= tempReal;
      tempReal *= tempReal;
      periodTotal2 -= tempReal;
      outReal[outIdx++] = (meanValue2-(meanValue1*meanValue1));
   } while( (i<=endIdx) );
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_LIB_API TA_RetCode TA_VAR_Unguarded( int    startIdx,
                                        int    endIdx,
                                        const double inReal[],
                                        int optInTimePeriod,
                                        double optInNbDev,
                                        int          *outBegIdx,
                                        int          *outNBElement,
                                        double        outReal[] )
{
   double tempReal;
   double periodTotal1;
   double periodTotal2;
   double meanValue1;
   double meanValue2;
   int i;
   int outIdx;
   int trailingIdx;
   int nbInitialElementNeeded;

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
   periodTotal1 = 0;
   periodTotal2 = 0;
   trailingIdx = (startIdx-nbInitialElementNeeded);
   i = trailingIdx;
   if( (optInTimePeriod>1) )
   {
      while( (i<startIdx) )
      {
         tempReal = inReal[i++];
         periodTotal1 += tempReal;
         tempReal *= tempReal;
         periodTotal2 += tempReal;
      }
   }
   outIdx = 0;
   do
   {
      tempReal = inReal[i++];
      periodTotal1 += tempReal;
      tempReal *= tempReal;
      periodTotal2 += tempReal;
      meanValue1 = (periodTotal1/optInTimePeriod);
      meanValue2 = (periodTotal2/optInTimePeriod);
      tempReal = inReal[trailingIdx++];
      periodTotal1 -= tempReal;
      tempReal *= tempReal;
      periodTotal2 -= tempReal;
      outReal[outIdx++] = (meanValue2-(meanValue1*meanValue1));
   } while( (i<=endIdx) );
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_VAR( int    startIdx,
                     int    endIdx,
                     const float inReal[],
                     int optInTimePeriod,
                     double optInNbDev,
                     int          *outBegIdx,
                     int          *outNBElement,
                     double        outReal[] )
{
   double tempReal;
   double periodTotal1;
   double periodTotal2;
   double meanValue1;
   double meanValue2;
   int i;
   int outIdx;
   int trailingIdx;
   int nbInitialElementNeeded;

   if( startIdx < 0 )
      return TA_OUT_OF_RANGE_START_INDEX;
   if( (endIdx < 0) || (endIdx < startIdx) )
      return TA_OUT_OF_RANGE_END_INDEX;

   if( !inReal )
      return TA_BAD_PARAM;
   if( (int)optInTimePeriod == (int)0x80000000 )
      optInTimePeriod = 5;
   else if( (int)optInTimePeriod < 1 || (int)optInTimePeriod > 100000 )
      return TA_BAD_PARAM;
   if( optInNbDev == -4e37 )
      optInNbDev = 1;
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
   periodTotal1 = 0;
   periodTotal2 = 0;
   trailingIdx = (startIdx-nbInitialElementNeeded);
   i = trailingIdx;
   if( (optInTimePeriod>1) )
   {
      while( (i<startIdx) )
      {
         tempReal = inReal[i++];
         periodTotal1 += tempReal;
         tempReal *= tempReal;
         periodTotal2 += tempReal;
      }
   }
   outIdx = 0;
   do
   {
      tempReal = inReal[i++];
      periodTotal1 += tempReal;
      tempReal *= tempReal;
      periodTotal2 += tempReal;
      meanValue1 = (periodTotal1/optInTimePeriod);
      meanValue2 = (periodTotal2/optInTimePeriod);
      tempReal = inReal[trailingIdx++];
      periodTotal1 -= tempReal;
      tempReal *= tempReal;
      periodTotal2 -= tempReal;
      outReal[outIdx++] = (meanValue2-(meanValue1*meanValue1));
   } while( (i<=endIdx) );
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_VAR_Unguarded( int    startIdx,
                               int    endIdx,
                               const float inReal[],
                               int optInTimePeriod,
                               double optInNbDev,
                               int          *outBegIdx,
                               int          *outNBElement,
                               double        outReal[] )
{
   double tempReal;
   double periodTotal1;
   double periodTotal2;
   double meanValue1;
   double meanValue2;
   int i;
   int outIdx;
   int trailingIdx;
   int nbInitialElementNeeded;

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
   periodTotal1 = 0;
   periodTotal2 = 0;
   trailingIdx = (startIdx-nbInitialElementNeeded);
   i = trailingIdx;
   if( (optInTimePeriod>1) )
   {
      while( (i<startIdx) )
      {
         tempReal = inReal[i++];
         periodTotal1 += tempReal;
         tempReal *= tempReal;
         periodTotal2 += tempReal;
      }
   }
   outIdx = 0;
   do
   {
      tempReal = inReal[i++];
      periodTotal1 += tempReal;
      tempReal *= tempReal;
      periodTotal2 += tempReal;
      meanValue1 = (periodTotal1/optInTimePeriod);
      meanValue2 = (periodTotal2/optInTimePeriod);
      tempReal = inReal[trailingIdx++];
      periodTotal1 -= tempReal;
      tempReal *= tempReal;
      periodTotal2 -= tempReal;
      outReal[outIdx++] = (meanValue2-(meanValue1*meanValue1));
   } while( (i<=endIdx) );
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

