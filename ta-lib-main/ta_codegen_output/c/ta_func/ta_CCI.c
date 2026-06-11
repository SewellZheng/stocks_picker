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

TA_LIB_API int TA_CCI_Lookback( int optInTimePeriod )
{
   return (optInTimePeriod-1);
}

TA_LIB_API TA_RetCode TA_CCI( int    startIdx,
                              int    endIdx,
                              const double inHigh[],
                              const double inLow[],
                              const double inClose[],
                              int optInTimePeriod,
                              int          *outBegIdx,
                              int          *outNBElement,
                              double        outReal[] )
{
   double tempReal;
   double tempReal2;
   double theAverage;
   double lastValue;
   int i;
   int j;
   int outIdx;
   int lookbackTotal;
   double circBuffer[30];
   int circBuffer_Idx;

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

   circBuffer_Idx = 0;
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
   memset(circBuffer,0,(optInTimePeriod*sizeof(double)));
   circBuffer_Idx = 0;
   i = (startIdx-lookbackTotal);
   if( (optInTimePeriod>1) )
   {
      while( (i<startIdx) )
      {
         circBuffer[circBuffer_Idx] = (((inHigh[i]+inLow[i])+inClose[i])/3);
         i += 1;
         circBuffer_Idx += 1;
         if( (circBuffer_Idx>=optInTimePeriod) )
         {
            circBuffer_Idx = 0;
         }
      }
   }
   outIdx = 0;
   do
   {
      lastValue = (((inHigh[i]+inLow[i])+inClose[i])/3);
      circBuffer[circBuffer_Idx] = lastValue;
      theAverage = 0;
      for( j = 0; (j<optInTimePeriod); j += 1 )
      {
         theAverage += circBuffer[j];
      }
      theAverage /= optInTimePeriod;
      tempReal2 = 0;
      for( j = 0; (j<optInTimePeriod); j += 1 )
      {
         tempReal2 += fabs((circBuffer[j]-theAverage));
      }
      tempReal = (lastValue-theAverage);
      if( ((tempReal!=0.0)&&(tempReal2!=0.0)) )
      {
         outReal[outIdx++] = (tempReal/(0.015*(tempReal2/optInTimePeriod)));
      } else 
      {
         outReal[outIdx++] = 0.0;
      }
      circBuffer_Idx += 1;
      if( (circBuffer_Idx>=optInTimePeriod) )
      {
         circBuffer_Idx = 0;
      }
      i += 1;
   } while( (i<=endIdx) );
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_LIB_API TA_RetCode TA_CCI_Unguarded( int    startIdx,
                                        int    endIdx,
                                        const double inHigh[],
                                        const double inLow[],
                                        const double inClose[],
                                        int optInTimePeriod,
                                        int          *outBegIdx,
                                        int          *outNBElement,
                                        double        outReal[] )
{
   double tempReal;
   double tempReal2;
   double theAverage;
   double lastValue;
   int i;
   int j;
   int outIdx;
   int lookbackTotal;
   double circBuffer[30];
   int circBuffer_Idx;

   circBuffer_Idx = 0;
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
   memset(circBuffer,0,(optInTimePeriod*sizeof(double)));
   circBuffer_Idx = 0;
   i = (startIdx-lookbackTotal);
   if( (optInTimePeriod>1) )
   {
      while( (i<startIdx) )
      {
         circBuffer[circBuffer_Idx] = (((inHigh[i]+inLow[i])+inClose[i])/3);
         i += 1;
         circBuffer_Idx += 1;
         if( (circBuffer_Idx>=optInTimePeriod) )
         {
            circBuffer_Idx = 0;
         }
      }
   }
   outIdx = 0;
   do
   {
      lastValue = (((inHigh[i]+inLow[i])+inClose[i])/3);
      circBuffer[circBuffer_Idx] = lastValue;
      theAverage = 0;
      for( j = 0; (j<optInTimePeriod); j += 1 )
      {
         theAverage += circBuffer[j];
      }
      theAverage /= optInTimePeriod;
      tempReal2 = 0;
      for( j = 0; (j<optInTimePeriod); j += 1 )
      {
         tempReal2 += fabs((circBuffer[j]-theAverage));
      }
      tempReal = (lastValue-theAverage);
      if( ((tempReal!=0.0)&&(tempReal2!=0.0)) )
      {
         outReal[outIdx++] = (tempReal/(0.015*(tempReal2/optInTimePeriod)));
      } else 
      {
         outReal[outIdx++] = 0.0;
      }
      circBuffer_Idx += 1;
      if( (circBuffer_Idx>=optInTimePeriod) )
      {
         circBuffer_Idx = 0;
      }
      i += 1;
   } while( (i<=endIdx) );
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_CCI( int    startIdx,
                     int    endIdx,
                     const float inHigh[],
                     const float inLow[],
                     const float inClose[],
                     int optInTimePeriod,
                     int          *outBegIdx,
                     int          *outNBElement,
                     double        outReal[] )
{
   double tempReal;
   double tempReal2;
   double theAverage;
   double lastValue;
   int i;
   int j;
   int outIdx;
   int lookbackTotal;
   double circBuffer[30];
   int circBuffer_Idx;

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

   circBuffer_Idx = 0;
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
   memset(circBuffer,0,(optInTimePeriod*sizeof(double)));
   circBuffer_Idx = 0;
   i = (startIdx-lookbackTotal);
   if( (optInTimePeriod>1) )
   {
      while( (i<startIdx) )
      {
         circBuffer[circBuffer_Idx] = (((inHigh[i]+inLow[i])+inClose[i])/3);
         i += 1;
         circBuffer_Idx += 1;
         if( (circBuffer_Idx>=optInTimePeriod) )
         {
            circBuffer_Idx = 0;
         }
      }
   }
   outIdx = 0;
   do
   {
      lastValue = (((inHigh[i]+inLow[i])+inClose[i])/3);
      circBuffer[circBuffer_Idx] = lastValue;
      theAverage = 0;
      for( j = 0; (j<optInTimePeriod); j += 1 )
      {
         theAverage += circBuffer[j];
      }
      theAverage /= optInTimePeriod;
      tempReal2 = 0;
      for( j = 0; (j<optInTimePeriod); j += 1 )
      {
         tempReal2 += fabs((circBuffer[j]-theAverage));
      }
      tempReal = (lastValue-theAverage);
      if( ((tempReal!=0.0)&&(tempReal2!=0.0)) )
      {
         outReal[outIdx++] = (tempReal/(0.015*(tempReal2/optInTimePeriod)));
      } else 
      {
         outReal[outIdx++] = 0.0;
      }
      circBuffer_Idx += 1;
      if( (circBuffer_Idx>=optInTimePeriod) )
      {
         circBuffer_Idx = 0;
      }
      i += 1;
   } while( (i<=endIdx) );
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_CCI_Unguarded( int    startIdx,
                               int    endIdx,
                               const float inHigh[],
                               const float inLow[],
                               const float inClose[],
                               int optInTimePeriod,
                               int          *outBegIdx,
                               int          *outNBElement,
                               double        outReal[] )
{
   double tempReal;
   double tempReal2;
   double theAverage;
   double lastValue;
   int i;
   int j;
   int outIdx;
   int lookbackTotal;
   double circBuffer[30];
   int circBuffer_Idx;

   circBuffer_Idx = 0;
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
   memset(circBuffer,0,(optInTimePeriod*sizeof(double)));
   circBuffer_Idx = 0;
   i = (startIdx-lookbackTotal);
   if( (optInTimePeriod>1) )
   {
      while( (i<startIdx) )
      {
         circBuffer[circBuffer_Idx] = (((inHigh[i]+inLow[i])+inClose[i])/3);
         i += 1;
         circBuffer_Idx += 1;
         if( (circBuffer_Idx>=optInTimePeriod) )
         {
            circBuffer_Idx = 0;
         }
      }
   }
   outIdx = 0;
   do
   {
      lastValue = (((inHigh[i]+inLow[i])+inClose[i])/3);
      circBuffer[circBuffer_Idx] = lastValue;
      theAverage = 0;
      for( j = 0; (j<optInTimePeriod); j += 1 )
      {
         theAverage += circBuffer[j];
      }
      theAverage /= optInTimePeriod;
      tempReal2 = 0;
      for( j = 0; (j<optInTimePeriod); j += 1 )
      {
         tempReal2 += fabs((circBuffer[j]-theAverage));
      }
      tempReal = (lastValue-theAverage);
      if( ((tempReal!=0.0)&&(tempReal2!=0.0)) )
      {
         outReal[outIdx++] = (tempReal/(0.015*(tempReal2/optInTimePeriod)));
      } else 
      {
         outReal[outIdx++] = 0.0;
      }
      circBuffer_Idx += 1;
      if( (circBuffer_Idx>=optInTimePeriod) )
      {
         circBuffer_Idx = 0;
      }
      i += 1;
   } while( (i<=endIdx) );
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

