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

TA_LIB_API int TA_TRANGE_Lookback( void )
{
   return 1;
}

TA_LIB_API TA_RetCode TA_TRANGE( int    startIdx,
                                 int    endIdx,
                                 const double inHigh[],
                                 const double inLow[],
                                 const double inClose[],
                                 int          *outBegIdx,
                                 int          *outNBElement,
                                 double        outReal[] )
{
   int today;
   int outIdx;
   double val2;
   double val3;
   double greatest;
   double tempCY;
   double tempLT;
   double tempHT;

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
   if( !outReal )
      return TA_BAD_PARAM;

   if( (startIdx<1) )
   {
      startIdx = 1;
   }
   if( (startIdx>endIdx) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      return TA_SUCCESS;
   }
   outIdx = 0;
   today = startIdx;
   while( (today<=endIdx) )
   {
      tempLT = inLow[today];
      tempHT = inHigh[today];
      tempCY = inClose[(today-1)];
      greatest = (tempHT-tempLT);
      val2 = fabs((tempCY-tempHT));
      if( (val2>greatest) )
      {
         greatest = val2;
      }
      val3 = fabs((tempCY-tempLT));
      if( (val3>greatest) )
      {
         greatest = val3;
      }
      outReal[outIdx++] = greatest;
      today += 1;
   }
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_LIB_API TA_RetCode TA_TRANGE_Unguarded( int    startIdx,
                                           int    endIdx,
                                           const double inHigh[],
                                           const double inLow[],
                                           const double inClose[],
                                           int          *outBegIdx,
                                           int          *outNBElement,
                                           double        outReal[] )
{
   int today;
   int outIdx;
   double val2;
   double val3;
   double greatest;
   double tempCY;
   double tempLT;
   double tempHT;

   if( (startIdx<1) )
   {
      startIdx = 1;
   }
   if( (startIdx>endIdx) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      return TA_SUCCESS;
   }
   outIdx = 0;
   today = startIdx;
   while( (today<=endIdx) )
   {
      tempLT = inLow[today];
      tempHT = inHigh[today];
      tempCY = inClose[(today-1)];
      greatest = (tempHT-tempLT);
      val2 = fabs((tempCY-tempHT));
      if( (val2>greatest) )
      {
         greatest = val2;
      }
      val3 = fabs((tempCY-tempLT));
      if( (val3>greatest) )
      {
         greatest = val3;
      }
      outReal[outIdx++] = greatest;
      today += 1;
   }
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_TRANGE( int    startIdx,
                        int    endIdx,
                        const float inHigh[],
                        const float inLow[],
                        const float inClose[],
                        int          *outBegIdx,
                        int          *outNBElement,
                        double        outReal[] )
{
   int today;
   int outIdx;
   double val2;
   double val3;
   double greatest;
   double tempCY;
   double tempLT;
   double tempHT;

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
   if( !outReal )
      return TA_BAD_PARAM;

   if( (startIdx<1) )
   {
      startIdx = 1;
   }
   if( (startIdx>endIdx) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      return TA_SUCCESS;
   }
   outIdx = 0;
   today = startIdx;
   while( (today<=endIdx) )
   {
      tempLT = inLow[today];
      tempHT = inHigh[today];
      tempCY = inClose[(today-1)];
      greatest = (tempHT-tempLT);
      val2 = fabs((tempCY-tempHT));
      if( (val2>greatest) )
      {
         greatest = val2;
      }
      val3 = fabs((tempCY-tempLT));
      if( (val3>greatest) )
      {
         greatest = val3;
      }
      outReal[outIdx++] = greatest;
      today += 1;
   }
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_TRANGE_Unguarded( int    startIdx,
                                  int    endIdx,
                                  const float inHigh[],
                                  const float inLow[],
                                  const float inClose[],
                                  int          *outBegIdx,
                                  int          *outNBElement,
                                  double        outReal[] )
{
   int today;
   int outIdx;
   double val2;
   double val3;
   double greatest;
   double tempCY;
   double tempLT;
   double tempHT;

   if( (startIdx<1) )
   {
      startIdx = 1;
   }
   if( (startIdx>endIdx) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      return TA_SUCCESS;
   }
   outIdx = 0;
   today = startIdx;
   while( (today<=endIdx) )
   {
      tempLT = inLow[today];
      tempHT = inHigh[today];
      tempCY = inClose[(today-1)];
      greatest = (tempHT-tempLT);
      val2 = fabs((tempCY-tempHT));
      if( (val2>greatest) )
      {
         greatest = val2;
      }
      val3 = fabs((tempCY-tempLT));
      if( (val3>greatest) )
      {
         greatest = val3;
      }
      outReal[outIdx++] = greatest;
      today += 1;
   }
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

