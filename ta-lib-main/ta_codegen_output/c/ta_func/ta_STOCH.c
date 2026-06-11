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

TA_LIB_API int TA_STOCH_Lookback( int optInFastK_Period, int optInSlowK_Period, TA_MAType optInSlowK_MAType, int optInSlowD_Period, TA_MAType optInSlowD_MAType )
{
   int retValue;
   retValue = (optInFastK_Period-1);
   retValue += TA_MA_Lookback(optInSlowK_Period,optInSlowK_MAType);
   retValue += TA_MA_Lookback(optInSlowD_Period,optInSlowD_MAType);
   return retValue;
}

TA_LIB_API TA_RetCode TA_STOCH( int    startIdx,
                                int    endIdx,
                                const double inHigh[],
                                const double inLow[],
                                const double inClose[],
                                int optInFastK_Period,
                                int optInSlowK_Period,
                                TA_MAType optInSlowK_MAType,
                                int optInSlowD_Period,
                                TA_MAType optInSlowD_MAType,
                                int          *outBegIdx,
                                int          *outNBElement,
                                double        outSlowK[],
                                double        outSlowD[] )
{
   TA_RetCode retCode;
   double lowest;
   double highest;
   double tmp;
   double diff;
   double *tempBuffer;
   int outIdx;
   int lowestIdx;
   int highestIdx;
   int lookbackTotal;
   int lookbackK;
   int lookbackKSlow;
   int lookbackDSlow;
   int trailingIdx;
   int today;
   int i;
   int bufferIsAllocated;

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
   if( (int)optInFastK_Period == (int)0x80000000 )
      optInFastK_Period = 5;
   else if( (int)optInFastK_Period < 1 || (int)optInFastK_Period > 100000 )
      return TA_BAD_PARAM;
   if( (int)optInSlowK_Period == (int)0x80000000 )
      optInSlowK_Period = 3;
   else if( (int)optInSlowK_Period < 1 || (int)optInSlowK_Period > 100000 )
      return TA_BAD_PARAM;
   if( (int)optInSlowK_MAType == (int)0x80000000 )
      optInSlowK_MAType = 0;
   if( (int)optInSlowD_Period == (int)0x80000000 )
      optInSlowD_Period = 3;
   else if( (int)optInSlowD_Period < 1 || (int)optInSlowD_Period > 100000 )
      return TA_BAD_PARAM;
   if( (int)optInSlowD_MAType == (int)0x80000000 )
      optInSlowD_MAType = 0;
   if( !outSlowK )
      return TA_BAD_PARAM;
   if( !outSlowD )
      return TA_BAD_PARAM;

   lookbackK = (optInFastK_Period-1);
   lookbackKSlow = TA_MA_Lookback(optInSlowK_Period,optInSlowK_MAType);
   lookbackDSlow = TA_MA_Lookback(optInSlowD_Period,optInSlowD_MAType);
   lookbackTotal = ((lookbackK+lookbackDSlow)+lookbackKSlow);
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
   trailingIdx = (startIdx-lookbackTotal);
   today = (trailingIdx+lookbackK);
   highestIdx = (0-1);
   lowestIdx = highestIdx;
   lowest = 0.0;
   highest = lowest;
   diff = highest;
   bufferIsAllocated = 0;
   if( (((outSlowK==inHigh)||(outSlowK==inLow))||(outSlowK==inClose)) )
   {
      tempBuffer = outSlowK;
   } else if( (((outSlowD==inHigh)||(outSlowD==inLow))||(outSlowD==inClose)) )
   {
      tempBuffer = outSlowD;
   } else 
   {
      bufferIsAllocated = 1;
      tempBuffer = malloc((((endIdx-today)+1)*sizeof(double)));
   }
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
         diff = ((highest-lowest)/100.0);
      } else if( (tmp<=lowest) )
      {
         lowestIdx = today;
         lowest = tmp;
         diff = ((highest-lowest)/100.0);
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
         diff = ((highest-lowest)/100.0);
      } else if( (tmp>=highest) )
      {
         highestIdx = today;
         highest = tmp;
         diff = ((highest-lowest)/100.0);
      }
      if( (diff!=0.0) )
      {
         tempBuffer[outIdx++] = ((inClose[today]-lowest)/diff);
      } else 
      {
         tempBuffer[outIdx++] = 0.0;
      }
      trailingIdx += 1;
      today += 1;
   }
   retCode = TA_MA_Unguarded(0,(outIdx-1),tempBuffer,optInSlowK_Period,optInSlowK_MAType,outBegIdx,outNBElement,tempBuffer);
   if( ((retCode!=TA_SUCCESS)||(((int)*outNBElement)==0)) )
   {
      if( bufferIsAllocated )
      {
         free(tempBuffer);
      }
      *outBegIdx= 0;
      *outNBElement= 0;
      return retCode;
   }
   retCode = TA_MA_Unguarded(0,(((int)*outNBElement)-1),tempBuffer,optInSlowD_Period,optInSlowD_MAType,outBegIdx,outNBElement,outSlowD);
   memcpy(outSlowK,&tempBuffer[lookbackDSlow],(((int)*outNBElement)*sizeof(double)));
   if( bufferIsAllocated )
   {
      free(tempBuffer);
   }
   if( (retCode!=TA_SUCCESS) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      return retCode;
   }
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_LIB_API TA_RetCode TA_STOCH_Unguarded( int    startIdx,
                                          int    endIdx,
                                          const double inHigh[],
                                          const double inLow[],
                                          const double inClose[],
                                          int optInFastK_Period,
                                          int optInSlowK_Period,
                                          TA_MAType optInSlowK_MAType,
                                          int optInSlowD_Period,
                                          TA_MAType optInSlowD_MAType,
                                          int          *outBegIdx,
                                          int          *outNBElement,
                                          double        outSlowK[],
                                          double        outSlowD[] )
{
   TA_RetCode retCode;
   double lowest;
   double highest;
   double tmp;
   double diff;
   double *tempBuffer;
   int outIdx;
   int lowestIdx;
   int highestIdx;
   int lookbackTotal;
   int lookbackK;
   int lookbackKSlow;
   int lookbackDSlow;
   int trailingIdx;
   int today;
   int i;
   int bufferIsAllocated;

   lookbackK = (optInFastK_Period-1);
   lookbackKSlow = TA_MA_Lookback(optInSlowK_Period,optInSlowK_MAType);
   lookbackDSlow = TA_MA_Lookback(optInSlowD_Period,optInSlowD_MAType);
   lookbackTotal = ((lookbackK+lookbackDSlow)+lookbackKSlow);
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
   trailingIdx = (startIdx-lookbackTotal);
   today = (trailingIdx+lookbackK);
   highestIdx = (0-1);
   lowestIdx = highestIdx;
   lowest = 0.0;
   highest = lowest;
   diff = highest;
   bufferIsAllocated = 0;
   if( (((outSlowK==inHigh)||(outSlowK==inLow))||(outSlowK==inClose)) )
   {
      tempBuffer = outSlowK;
   } else if( (((outSlowD==inHigh)||(outSlowD==inLow))||(outSlowD==inClose)) )
   {
      tempBuffer = outSlowD;
   } else 
   {
      bufferIsAllocated = 1;
      tempBuffer = malloc((((endIdx-today)+1)*sizeof(double)));
   }
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
         diff = ((highest-lowest)/100.0);
      } else if( (tmp<=lowest) )
      {
         lowestIdx = today;
         lowest = tmp;
         diff = ((highest-lowest)/100.0);
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
         diff = ((highest-lowest)/100.0);
      } else if( (tmp>=highest) )
      {
         highestIdx = today;
         highest = tmp;
         diff = ((highest-lowest)/100.0);
      }
      if( (diff!=0.0) )
      {
         tempBuffer[outIdx++] = ((inClose[today]-lowest)/diff);
      } else 
      {
         tempBuffer[outIdx++] = 0.0;
      }
      trailingIdx += 1;
      today += 1;
   }
   retCode = TA_MA_Unguarded(0,(outIdx-1),tempBuffer,optInSlowK_Period,optInSlowK_MAType,outBegIdx,outNBElement,tempBuffer);
   if( ((retCode!=TA_SUCCESS)||(((int)*outNBElement)==0)) )
   {
      if( bufferIsAllocated )
      {
         free(tempBuffer);
      }
      *outBegIdx= 0;
      *outNBElement= 0;
      return retCode;
   }
   retCode = TA_MA_Unguarded(0,(((int)*outNBElement)-1),tempBuffer,optInSlowD_Period,optInSlowD_MAType,outBegIdx,outNBElement,outSlowD);
   memcpy(outSlowK,&tempBuffer[lookbackDSlow],(((int)*outNBElement)*sizeof(double)));
   if( bufferIsAllocated )
   {
      free(tempBuffer);
   }
   if( (retCode!=TA_SUCCESS) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      return retCode;
   }
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_STOCH( int    startIdx,
                       int    endIdx,
                       const float inHigh[],
                       const float inLow[],
                       const float inClose[],
                       int optInFastK_Period,
                       int optInSlowK_Period,
                       TA_MAType optInSlowK_MAType,
                       int optInSlowD_Period,
                       TA_MAType optInSlowD_MAType,
                       int          *outBegIdx,
                       int          *outNBElement,
                       double        outSlowK[],
                       double        outSlowD[] )
{
   TA_RetCode retCode;
   double lowest;
   double highest;
   double tmp;
   double diff;
   double *tempBuffer;
   int outIdx;
   int lowestIdx;
   int highestIdx;
   int lookbackTotal;
   int lookbackK;
   int lookbackKSlow;
   int lookbackDSlow;
   int trailingIdx;
   int today;
   int i;
   int bufferIsAllocated;

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
   if( (int)optInFastK_Period == (int)0x80000000 )
      optInFastK_Period = 5;
   else if( (int)optInFastK_Period < 1 || (int)optInFastK_Period > 100000 )
      return TA_BAD_PARAM;
   if( (int)optInSlowK_Period == (int)0x80000000 )
      optInSlowK_Period = 3;
   else if( (int)optInSlowK_Period < 1 || (int)optInSlowK_Period > 100000 )
      return TA_BAD_PARAM;
   if( (int)optInSlowK_MAType == (int)0x80000000 )
      optInSlowK_MAType = 0;
   if( (int)optInSlowD_Period == (int)0x80000000 )
      optInSlowD_Period = 3;
   else if( (int)optInSlowD_Period < 1 || (int)optInSlowD_Period > 100000 )
      return TA_BAD_PARAM;
   if( (int)optInSlowD_MAType == (int)0x80000000 )
      optInSlowD_MAType = 0;
   if( !outSlowK )
      return TA_BAD_PARAM;
   if( !outSlowD )
      return TA_BAD_PARAM;

   lookbackK = (optInFastK_Period-1);
   lookbackKSlow = TA_MA_Lookback(optInSlowK_Period,optInSlowK_MAType);
   lookbackDSlow = TA_MA_Lookback(optInSlowD_Period,optInSlowD_MAType);
   lookbackTotal = ((lookbackK+lookbackDSlow)+lookbackKSlow);
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
   trailingIdx = (startIdx-lookbackTotal);
   today = (trailingIdx+lookbackK);
   highestIdx = (0-1);
   lowestIdx = highestIdx;
   lowest = 0.0;
   highest = lowest;
   diff = highest;
   bufferIsAllocated = 0;
   if( ((((void *)outSlowK==(void *)inHigh)||((void *)outSlowK==(void *)inLow))||((void *)outSlowK==(void *)inClose)) )
   {
      tempBuffer = outSlowK;
   } else if( ((((void *)outSlowD==(void *)inHigh)||((void *)outSlowD==(void *)inLow))||((void *)outSlowD==(void *)inClose)) )
   {
      tempBuffer = outSlowD;
   } else 
   {
      bufferIsAllocated = 1;
      tempBuffer = malloc((((endIdx-today)+1)*sizeof(double)));
   }
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
         diff = ((highest-lowest)/100.0);
      } else if( (tmp<=lowest) )
      {
         lowestIdx = today;
         lowest = tmp;
         diff = ((highest-lowest)/100.0);
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
         diff = ((highest-lowest)/100.0);
      } else if( (tmp>=highest) )
      {
         highestIdx = today;
         highest = tmp;
         diff = ((highest-lowest)/100.0);
      }
      if( (diff!=0.0) )
      {
         tempBuffer[outIdx++] = ((inClose[today]-lowest)/diff);
      } else 
      {
         tempBuffer[outIdx++] = 0.0;
      }
      trailingIdx += 1;
      today += 1;
   }
   retCode = TA_MA_Unguarded(0,(outIdx-1),tempBuffer,optInSlowK_Period,optInSlowK_MAType,outBegIdx,outNBElement,tempBuffer);
   if( ((retCode!=TA_SUCCESS)||(((int)*outNBElement)==0)) )
   {
      if( bufferIsAllocated )
      {
         free(tempBuffer);
      }
      *outBegIdx= 0;
      *outNBElement= 0;
      return retCode;
   }
   retCode = TA_MA_Unguarded(0,(((int)*outNBElement)-1),tempBuffer,optInSlowD_Period,optInSlowD_MAType,outBegIdx,outNBElement,outSlowD);
   memcpy(outSlowK,&tempBuffer[lookbackDSlow],(((int)*outNBElement)*sizeof(double)));
   if( bufferIsAllocated )
   {
      free(tempBuffer);
   }
   if( (retCode!=TA_SUCCESS) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      return retCode;
   }
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_STOCH_Unguarded( int    startIdx,
                                 int    endIdx,
                                 const float inHigh[],
                                 const float inLow[],
                                 const float inClose[],
                                 int optInFastK_Period,
                                 int optInSlowK_Period,
                                 TA_MAType optInSlowK_MAType,
                                 int optInSlowD_Period,
                                 TA_MAType optInSlowD_MAType,
                                 int          *outBegIdx,
                                 int          *outNBElement,
                                 double        outSlowK[],
                                 double        outSlowD[] )
{
   TA_RetCode retCode;
   double lowest;
   double highest;
   double tmp;
   double diff;
   double *tempBuffer;
   int outIdx;
   int lowestIdx;
   int highestIdx;
   int lookbackTotal;
   int lookbackK;
   int lookbackKSlow;
   int lookbackDSlow;
   int trailingIdx;
   int today;
   int i;
   int bufferIsAllocated;

   lookbackK = (optInFastK_Period-1);
   lookbackKSlow = TA_MA_Lookback(optInSlowK_Period,optInSlowK_MAType);
   lookbackDSlow = TA_MA_Lookback(optInSlowD_Period,optInSlowD_MAType);
   lookbackTotal = ((lookbackK+lookbackDSlow)+lookbackKSlow);
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
   trailingIdx = (startIdx-lookbackTotal);
   today = (trailingIdx+lookbackK);
   highestIdx = (0-1);
   lowestIdx = highestIdx;
   lowest = 0.0;
   highest = lowest;
   diff = highest;
   bufferIsAllocated = 0;
   if( ((((void *)outSlowK==(void *)inHigh)||((void *)outSlowK==(void *)inLow))||((void *)outSlowK==(void *)inClose)) )
   {
      tempBuffer = outSlowK;
   } else if( ((((void *)outSlowD==(void *)inHigh)||((void *)outSlowD==(void *)inLow))||((void *)outSlowD==(void *)inClose)) )
   {
      tempBuffer = outSlowD;
   } else 
   {
      bufferIsAllocated = 1;
      tempBuffer = malloc((((endIdx-today)+1)*sizeof(double)));
   }
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
         diff = ((highest-lowest)/100.0);
      } else if( (tmp<=lowest) )
      {
         lowestIdx = today;
         lowest = tmp;
         diff = ((highest-lowest)/100.0);
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
         diff = ((highest-lowest)/100.0);
      } else if( (tmp>=highest) )
      {
         highestIdx = today;
         highest = tmp;
         diff = ((highest-lowest)/100.0);
      }
      if( (diff!=0.0) )
      {
         tempBuffer[outIdx++] = ((inClose[today]-lowest)/diff);
      } else 
      {
         tempBuffer[outIdx++] = 0.0;
      }
      trailingIdx += 1;
      today += 1;
   }
   retCode = TA_MA_Unguarded(0,(outIdx-1),tempBuffer,optInSlowK_Period,optInSlowK_MAType,outBegIdx,outNBElement,tempBuffer);
   if( ((retCode!=TA_SUCCESS)||(((int)*outNBElement)==0)) )
   {
      if( bufferIsAllocated )
      {
         free(tempBuffer);
      }
      *outBegIdx= 0;
      *outNBElement= 0;
      return retCode;
   }
   retCode = TA_MA_Unguarded(0,(((int)*outNBElement)-1),tempBuffer,optInSlowD_Period,optInSlowD_MAType,outBegIdx,outNBElement,outSlowD);
   memcpy(outSlowK,&tempBuffer[lookbackDSlow],(((int)*outNBElement)*sizeof(double)));
   if( bufferIsAllocated )
   {
      free(tempBuffer);
   }
   if( (retCode!=TA_SUCCESS) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      return retCode;
   }
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

