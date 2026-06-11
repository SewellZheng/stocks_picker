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

TA_LIB_API int TA_TRIMA_Lookback( int optInTimePeriod )
{
   return (optInTimePeriod-1);
}

TA_LIB_API TA_RetCode TA_TRIMA( int    startIdx,
                                int    endIdx,
                                const double inReal[],
                                int optInTimePeriod,
                                int          *outBegIdx,
                                int          *outNBElement,
                                double        outReal[] )
{
   int lookbackTotal;
   double numerator;
   double numeratorSub;
   double numeratorAdd;
   int i;
   int outIdx;
   int todayIdx;
   int trailingIdx;
   int middleIdx;
   double factor;
   double tempReal;

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

   lookbackTotal = (optInTimePeriod-1);
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
   outIdx = 0;
   if( ((optInTimePeriod%2)==1) )
   {
      i = (optInTimePeriod>>1);
      factor = ((i+1)*(i+1));
      factor = (1.0/factor);
      trailingIdx = (startIdx-lookbackTotal);
      middleIdx = (trailingIdx+i);
      todayIdx = (middleIdx+i);
      numerator = 0.0;
      numeratorSub = 0.0;
      for( i = middleIdx; (i>=trailingIdx); i -= 1 )
      {
         tempReal = inReal[i];
         numeratorSub += tempReal;
         numerator += numeratorSub;
      }
      numeratorAdd = 0.0;
      middleIdx += 1;
      for( i = middleIdx; (i<=todayIdx); i += 1 )
      {
         tempReal = inReal[i];
         numeratorAdd += tempReal;
         numerator += numeratorAdd;
      }
      outIdx = 0;
      tempReal = inReal[trailingIdx++];
      outReal[outIdx++] = (numerator*factor);
      todayIdx += 1;
      while( (todayIdx<=endIdx) )
      {
         numerator -= numeratorSub;
         numeratorSub -= tempReal;
         tempReal = inReal[middleIdx++];
         numeratorSub += tempReal;
         numerator += numeratorAdd;
         numeratorAdd -= tempReal;
         tempReal = inReal[todayIdx++];
         numeratorAdd += tempReal;
         numerator += tempReal;
         tempReal = inReal[trailingIdx++];
         outReal[outIdx++] = (numerator*factor);
      }
   } else 
   {
      i = (optInTimePeriod>>1);
      factor = (i*(i+1));
      factor = (1.0/factor);
      trailingIdx = (startIdx-lookbackTotal);
      middleIdx = ((trailingIdx+i)-1);
      todayIdx = (middleIdx+i);
      numerator = 0.0;
      numeratorSub = 0.0;
      for( i = middleIdx; (i>=trailingIdx); i -= 1 )
      {
         tempReal = inReal[i];
         numeratorSub += tempReal;
         numerator += numeratorSub;
      }
      numeratorAdd = 0.0;
      middleIdx += 1;
      for( i = middleIdx; (i<=todayIdx); i += 1 )
      {
         tempReal = inReal[i];
         numeratorAdd += tempReal;
         numerator += numeratorAdd;
      }
      outIdx = 0;
      tempReal = inReal[trailingIdx++];
      outReal[outIdx++] = (numerator*factor);
      todayIdx += 1;
      while( (todayIdx<=endIdx) )
      {
         numerator -= numeratorSub;
         numeratorSub -= tempReal;
         tempReal = inReal[middleIdx++];
         numeratorSub += tempReal;
         numeratorAdd -= tempReal;
         numerator += numeratorAdd;
         tempReal = inReal[todayIdx++];
         numeratorAdd += tempReal;
         numerator += tempReal;
         tempReal = inReal[trailingIdx++];
         outReal[outIdx++] = (numerator*factor);
      }
   }
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_LIB_API TA_RetCode TA_TRIMA_Unguarded( int    startIdx,
                                          int    endIdx,
                                          const double inReal[],
                                          int optInTimePeriod,
                                          int          *outBegIdx,
                                          int          *outNBElement,
                                          double        outReal[] )
{
   int lookbackTotal;
   double numerator;
   double numeratorSub;
   double numeratorAdd;
   int i;
   int outIdx;
   int todayIdx;
   int trailingIdx;
   int middleIdx;
   double factor;
   double tempReal;

   lookbackTotal = (optInTimePeriod-1);
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
   outIdx = 0;
   if( ((optInTimePeriod%2)==1) )
   {
      i = (optInTimePeriod>>1);
      factor = ((i+1)*(i+1));
      factor = (1.0/factor);
      trailingIdx = (startIdx-lookbackTotal);
      middleIdx = (trailingIdx+i);
      todayIdx = (middleIdx+i);
      numerator = 0.0;
      numeratorSub = 0.0;
      for( i = middleIdx; (i>=trailingIdx); i -= 1 )
      {
         tempReal = inReal[i];
         numeratorSub += tempReal;
         numerator += numeratorSub;
      }
      numeratorAdd = 0.0;
      middleIdx += 1;
      for( i = middleIdx; (i<=todayIdx); i += 1 )
      {
         tempReal = inReal[i];
         numeratorAdd += tempReal;
         numerator += numeratorAdd;
      }
      outIdx = 0;
      tempReal = inReal[trailingIdx++];
      outReal[outIdx++] = (numerator*factor);
      todayIdx += 1;
      while( (todayIdx<=endIdx) )
      {
         numerator -= numeratorSub;
         numeratorSub -= tempReal;
         tempReal = inReal[middleIdx++];
         numeratorSub += tempReal;
         numerator += numeratorAdd;
         numeratorAdd -= tempReal;
         tempReal = inReal[todayIdx++];
         numeratorAdd += tempReal;
         numerator += tempReal;
         tempReal = inReal[trailingIdx++];
         outReal[outIdx++] = (numerator*factor);
      }
   } else 
   {
      i = (optInTimePeriod>>1);
      factor = (i*(i+1));
      factor = (1.0/factor);
      trailingIdx = (startIdx-lookbackTotal);
      middleIdx = ((trailingIdx+i)-1);
      todayIdx = (middleIdx+i);
      numerator = 0.0;
      numeratorSub = 0.0;
      for( i = middleIdx; (i>=trailingIdx); i -= 1 )
      {
         tempReal = inReal[i];
         numeratorSub += tempReal;
         numerator += numeratorSub;
      }
      numeratorAdd = 0.0;
      middleIdx += 1;
      for( i = middleIdx; (i<=todayIdx); i += 1 )
      {
         tempReal = inReal[i];
         numeratorAdd += tempReal;
         numerator += numeratorAdd;
      }
      outIdx = 0;
      tempReal = inReal[trailingIdx++];
      outReal[outIdx++] = (numerator*factor);
      todayIdx += 1;
      while( (todayIdx<=endIdx) )
      {
         numerator -= numeratorSub;
         numeratorSub -= tempReal;
         tempReal = inReal[middleIdx++];
         numeratorSub += tempReal;
         numeratorAdd -= tempReal;
         numerator += numeratorAdd;
         tempReal = inReal[todayIdx++];
         numeratorAdd += tempReal;
         numerator += tempReal;
         tempReal = inReal[trailingIdx++];
         outReal[outIdx++] = (numerator*factor);
      }
   }
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_TRIMA( int    startIdx,
                       int    endIdx,
                       const float inReal[],
                       int optInTimePeriod,
                       int          *outBegIdx,
                       int          *outNBElement,
                       double        outReal[] )
{
   int lookbackTotal;
   double numerator;
   double numeratorSub;
   double numeratorAdd;
   int i;
   int outIdx;
   int todayIdx;
   int trailingIdx;
   int middleIdx;
   double factor;
   double tempReal;

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

   lookbackTotal = (optInTimePeriod-1);
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
   outIdx = 0;
   if( ((optInTimePeriod%2)==1) )
   {
      i = (optInTimePeriod>>1);
      factor = ((i+1)*(i+1));
      factor = (1.0/factor);
      trailingIdx = (startIdx-lookbackTotal);
      middleIdx = (trailingIdx+i);
      todayIdx = (middleIdx+i);
      numerator = 0.0;
      numeratorSub = 0.0;
      for( i = middleIdx; (i>=trailingIdx); i -= 1 )
      {
         tempReal = inReal[i];
         numeratorSub += tempReal;
         numerator += numeratorSub;
      }
      numeratorAdd = 0.0;
      middleIdx += 1;
      for( i = middleIdx; (i<=todayIdx); i += 1 )
      {
         tempReal = inReal[i];
         numeratorAdd += tempReal;
         numerator += numeratorAdd;
      }
      outIdx = 0;
      tempReal = inReal[trailingIdx++];
      outReal[outIdx++] = (numerator*factor);
      todayIdx += 1;
      while( (todayIdx<=endIdx) )
      {
         numerator -= numeratorSub;
         numeratorSub -= tempReal;
         tempReal = inReal[middleIdx++];
         numeratorSub += tempReal;
         numerator += numeratorAdd;
         numeratorAdd -= tempReal;
         tempReal = inReal[todayIdx++];
         numeratorAdd += tempReal;
         numerator += tempReal;
         tempReal = inReal[trailingIdx++];
         outReal[outIdx++] = (numerator*factor);
      }
   } else 
   {
      i = (optInTimePeriod>>1);
      factor = (i*(i+1));
      factor = (1.0/factor);
      trailingIdx = (startIdx-lookbackTotal);
      middleIdx = ((trailingIdx+i)-1);
      todayIdx = (middleIdx+i);
      numerator = 0.0;
      numeratorSub = 0.0;
      for( i = middleIdx; (i>=trailingIdx); i -= 1 )
      {
         tempReal = inReal[i];
         numeratorSub += tempReal;
         numerator += numeratorSub;
      }
      numeratorAdd = 0.0;
      middleIdx += 1;
      for( i = middleIdx; (i<=todayIdx); i += 1 )
      {
         tempReal = inReal[i];
         numeratorAdd += tempReal;
         numerator += numeratorAdd;
      }
      outIdx = 0;
      tempReal = inReal[trailingIdx++];
      outReal[outIdx++] = (numerator*factor);
      todayIdx += 1;
      while( (todayIdx<=endIdx) )
      {
         numerator -= numeratorSub;
         numeratorSub -= tempReal;
         tempReal = inReal[middleIdx++];
         numeratorSub += tempReal;
         numeratorAdd -= tempReal;
         numerator += numeratorAdd;
         tempReal = inReal[todayIdx++];
         numeratorAdd += tempReal;
         numerator += tempReal;
         tempReal = inReal[trailingIdx++];
         outReal[outIdx++] = (numerator*factor);
      }
   }
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_TRIMA_Unguarded( int    startIdx,
                                 int    endIdx,
                                 const float inReal[],
                                 int optInTimePeriod,
                                 int          *outBegIdx,
                                 int          *outNBElement,
                                 double        outReal[] )
{
   int lookbackTotal;
   double numerator;
   double numeratorSub;
   double numeratorAdd;
   int i;
   int outIdx;
   int todayIdx;
   int trailingIdx;
   int middleIdx;
   double factor;
   double tempReal;

   lookbackTotal = (optInTimePeriod-1);
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
   outIdx = 0;
   if( ((optInTimePeriod%2)==1) )
   {
      i = (optInTimePeriod>>1);
      factor = ((i+1)*(i+1));
      factor = (1.0/factor);
      trailingIdx = (startIdx-lookbackTotal);
      middleIdx = (trailingIdx+i);
      todayIdx = (middleIdx+i);
      numerator = 0.0;
      numeratorSub = 0.0;
      for( i = middleIdx; (i>=trailingIdx); i -= 1 )
      {
         tempReal = inReal[i];
         numeratorSub += tempReal;
         numerator += numeratorSub;
      }
      numeratorAdd = 0.0;
      middleIdx += 1;
      for( i = middleIdx; (i<=todayIdx); i += 1 )
      {
         tempReal = inReal[i];
         numeratorAdd += tempReal;
         numerator += numeratorAdd;
      }
      outIdx = 0;
      tempReal = inReal[trailingIdx++];
      outReal[outIdx++] = (numerator*factor);
      todayIdx += 1;
      while( (todayIdx<=endIdx) )
      {
         numerator -= numeratorSub;
         numeratorSub -= tempReal;
         tempReal = inReal[middleIdx++];
         numeratorSub += tempReal;
         numerator += numeratorAdd;
         numeratorAdd -= tempReal;
         tempReal = inReal[todayIdx++];
         numeratorAdd += tempReal;
         numerator += tempReal;
         tempReal = inReal[trailingIdx++];
         outReal[outIdx++] = (numerator*factor);
      }
   } else 
   {
      i = (optInTimePeriod>>1);
      factor = (i*(i+1));
      factor = (1.0/factor);
      trailingIdx = (startIdx-lookbackTotal);
      middleIdx = ((trailingIdx+i)-1);
      todayIdx = (middleIdx+i);
      numerator = 0.0;
      numeratorSub = 0.0;
      for( i = middleIdx; (i>=trailingIdx); i -= 1 )
      {
         tempReal = inReal[i];
         numeratorSub += tempReal;
         numerator += numeratorSub;
      }
      numeratorAdd = 0.0;
      middleIdx += 1;
      for( i = middleIdx; (i<=todayIdx); i += 1 )
      {
         tempReal = inReal[i];
         numeratorAdd += tempReal;
         numerator += numeratorAdd;
      }
      outIdx = 0;
      tempReal = inReal[trailingIdx++];
      outReal[outIdx++] = (numerator*factor);
      todayIdx += 1;
      while( (todayIdx<=endIdx) )
      {
         numerator -= numeratorSub;
         numeratorSub -= tempReal;
         tempReal = inReal[middleIdx++];
         numeratorSub += tempReal;
         numeratorAdd -= tempReal;
         numerator += numeratorAdd;
         tempReal = inReal[todayIdx++];
         numeratorAdd += tempReal;
         numerator += tempReal;
         tempReal = inReal[trailingIdx++];
         outReal[outIdx++] = (numerator*factor);
      }
   }
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

