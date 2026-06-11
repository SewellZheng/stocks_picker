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

TA_LIB_API int TA_NATR_Lookback( int optInTimePeriod )
{
   return (optInTimePeriod+TA_GLOBALS_UNSTABLE_PERIOD(TA_FUNC_UNST_NATR,Natr));
}

TA_LIB_API TA_RetCode TA_NATR( int    startIdx,
                               int    endIdx,
                               const double inHigh[],
                               const double inLow[],
                               const double inClose[],
                               int optInTimePeriod,
                               int          *outBegIdx,
                               int          *outNBElement,
                               double        outReal[] )
{
   TA_RetCode retCode;
   int outIdx;
   int today;
   int lookbackTotal;
   int nbATR;
   int outBegIdx1;
   int outNbElement1;
   double prevATR;
   double tempValue;
   double *tempBuffer;

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
   else if( (int)optInTimePeriod < 1 || (int)optInTimePeriod > 100000 )
      return TA_BAD_PARAM;
   if( !outReal )
      return TA_BAD_PARAM;

   *outBegIdx= 0;
   *outNBElement= 0;
   lookbackTotal = TA_NATR_Lookback(optInTimePeriod);
   if( (startIdx<lookbackTotal) )
   {
      startIdx = lookbackTotal;
   }
   if( (startIdx>endIdx) )
   {
      return TA_SUCCESS;
   }
   if( (optInTimePeriod<=1) )
   {
      return TA_TRANGE_Unguarded(startIdx,endIdx,inHigh,inLow,inClose,outBegIdx,outNBElement,outReal);
   }
   tempBuffer = malloc((((lookbackTotal+(endIdx-startIdx))+1)*sizeof(double)));
   retCode = TA_TRANGE_Unguarded(((startIdx-lookbackTotal)+1),endIdx,inHigh,inLow,inClose,&outBegIdx1,&outNbElement1,tempBuffer);
   if( (retCode!=TA_SUCCESS) )
   {
      free(tempBuffer);
      return retCode;
   }
   retCode = TA_SMA_Unguarded((optInTimePeriod-1),(optInTimePeriod-1),tempBuffer,optInTimePeriod,&outBegIdx1,&outNbElement1,&prevATR);
   if( (retCode!=TA_SUCCESS) )
   {
      free(tempBuffer);
      return retCode;
   }
   today = optInTimePeriod;
   outIdx = TA_GLOBALS_UNSTABLE_PERIOD(TA_FUNC_UNST_NATR,Natr);
   while( (outIdx!=0) )
   {
      prevATR *= (optInTimePeriod-1);
      prevATR += tempBuffer[today++];
      prevATR /= optInTimePeriod;
      outIdx -= 1;
   }
   outIdx = 1;
   tempValue = inClose[today];
   if( !(TA_IS_ZERO(tempValue)) )
   {
      outReal[0] = ((prevATR/tempValue)*100.0);
   } else 
   {
      outReal[0] = 0.0;
   }
   nbATR = ((endIdx-startIdx)+1);
   while( (--nbATR!=0) )
   {
      prevATR *= (optInTimePeriod-1);
      prevATR += tempBuffer[today++];
      prevATR /= optInTimePeriod;
      tempValue = inClose[today];
      if( !(TA_IS_ZERO(tempValue)) )
      {
         outReal[outIdx] = ((prevATR/tempValue)*100.0);
      } else 
      {
         outReal[0] = 0.0;
      }
      outIdx += 1;
   }
   *outBegIdx= startIdx;
   *outNBElement= outIdx;
   free(tempBuffer);
   return retCode;

   return TA_SUCCESS;
}

TA_LIB_API TA_RetCode TA_NATR_Unguarded( int    startIdx,
                                         int    endIdx,
                                         const double inHigh[],
                                         const double inLow[],
                                         const double inClose[],
                                         int optInTimePeriod,
                                         int          *outBegIdx,
                                         int          *outNBElement,
                                         double        outReal[] )
{
   TA_RetCode retCode;
   int outIdx;
   int today;
   int lookbackTotal;
   int nbATR;
   int outBegIdx1;
   int outNbElement1;
   double prevATR;
   double tempValue;
   double *tempBuffer;

   *outBegIdx= 0;
   *outNBElement= 0;
   lookbackTotal = TA_NATR_Lookback(optInTimePeriod);
   if( (startIdx<lookbackTotal) )
   {
      startIdx = lookbackTotal;
   }
   if( (startIdx>endIdx) )
   {
      return TA_SUCCESS;
   }
   if( (optInTimePeriod<=1) )
   {
      return TA_TRANGE_Unguarded(startIdx,endIdx,inHigh,inLow,inClose,outBegIdx,outNBElement,outReal);
   }
   tempBuffer = malloc((((lookbackTotal+(endIdx-startIdx))+1)*sizeof(double)));
   retCode = TA_TRANGE_Unguarded(((startIdx-lookbackTotal)+1),endIdx,inHigh,inLow,inClose,&outBegIdx1,&outNbElement1,tempBuffer);
   if( (retCode!=TA_SUCCESS) )
   {
      free(tempBuffer);
      return retCode;
   }
   retCode = TA_SMA_Unguarded((optInTimePeriod-1),(optInTimePeriod-1),tempBuffer,optInTimePeriod,&outBegIdx1,&outNbElement1,&prevATR);
   if( (retCode!=TA_SUCCESS) )
   {
      free(tempBuffer);
      return retCode;
   }
   today = optInTimePeriod;
   outIdx = TA_GLOBALS_UNSTABLE_PERIOD(TA_FUNC_UNST_NATR,Natr);
   while( (outIdx!=0) )
   {
      prevATR *= (optInTimePeriod-1);
      prevATR += tempBuffer[today++];
      prevATR /= optInTimePeriod;
      outIdx -= 1;
   }
   outIdx = 1;
   tempValue = inClose[today];
   if( !(TA_IS_ZERO(tempValue)) )
   {
      outReal[0] = ((prevATR/tempValue)*100.0);
   } else 
   {
      outReal[0] = 0.0;
   }
   nbATR = ((endIdx-startIdx)+1);
   while( (--nbATR!=0) )
   {
      prevATR *= (optInTimePeriod-1);
      prevATR += tempBuffer[today++];
      prevATR /= optInTimePeriod;
      tempValue = inClose[today];
      if( !(TA_IS_ZERO(tempValue)) )
      {
         outReal[outIdx] = ((prevATR/tempValue)*100.0);
      } else 
      {
         outReal[0] = 0.0;
      }
      outIdx += 1;
   }
   *outBegIdx= startIdx;
   *outNBElement= outIdx;
   free(tempBuffer);
   return retCode;

   return TA_SUCCESS;
}

TA_RetCode TA_S_NATR( int    startIdx,
                      int    endIdx,
                      const float inHigh[],
                      const float inLow[],
                      const float inClose[],
                      int optInTimePeriod,
                      int          *outBegIdx,
                      int          *outNBElement,
                      double        outReal[] )
{
   TA_RetCode retCode;
   int outIdx;
   int today;
   int lookbackTotal;
   int nbATR;
   int outBegIdx1;
   int outNbElement1;
   double prevATR;
   double tempValue;
   double *tempBuffer;

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
   else if( (int)optInTimePeriod < 1 || (int)optInTimePeriod > 100000 )
      return TA_BAD_PARAM;
   if( !outReal )
      return TA_BAD_PARAM;

   *outBegIdx= 0;
   *outNBElement= 0;
   lookbackTotal = TA_NATR_Lookback(optInTimePeriod);
   if( (startIdx<lookbackTotal) )
   {
      startIdx = lookbackTotal;
   }
   if( (startIdx>endIdx) )
   {
      return TA_SUCCESS;
   }
   if( (optInTimePeriod<=1) )
   {
      return TA_S_TRANGE_Unguarded(startIdx,endIdx,inHigh,inLow,inClose,outBegIdx,outNBElement,outReal);
   }
   tempBuffer = malloc((((lookbackTotal+(endIdx-startIdx))+1)*sizeof(double)));
   retCode = TA_S_TRANGE_Unguarded(((startIdx-lookbackTotal)+1),endIdx,inHigh,inLow,inClose,&outBegIdx1,&outNbElement1,tempBuffer);
   if( (retCode!=TA_SUCCESS) )
   {
      free(tempBuffer);
      return retCode;
   }
   retCode = TA_SMA_Unguarded((optInTimePeriod-1),(optInTimePeriod-1),tempBuffer,optInTimePeriod,&outBegIdx1,&outNbElement1,&prevATR);
   if( (retCode!=TA_SUCCESS) )
   {
      free(tempBuffer);
      return retCode;
   }
   today = optInTimePeriod;
   outIdx = TA_GLOBALS_UNSTABLE_PERIOD(TA_FUNC_UNST_NATR,Natr);
   while( (outIdx!=0) )
   {
      prevATR *= (optInTimePeriod-1);
      prevATR += tempBuffer[today++];
      prevATR /= optInTimePeriod;
      outIdx -= 1;
   }
   outIdx = 1;
   tempValue = inClose[today];
   if( !(TA_IS_ZERO(tempValue)) )
   {
      outReal[0] = ((prevATR/tempValue)*100.0);
   } else 
   {
      outReal[0] = 0.0;
   }
   nbATR = ((endIdx-startIdx)+1);
   while( (--nbATR!=0) )
   {
      prevATR *= (optInTimePeriod-1);
      prevATR += tempBuffer[today++];
      prevATR /= optInTimePeriod;
      tempValue = inClose[today];
      if( !(TA_IS_ZERO(tempValue)) )
      {
         outReal[outIdx] = ((prevATR/tempValue)*100.0);
      } else 
      {
         outReal[0] = 0.0;
      }
      outIdx += 1;
   }
   *outBegIdx= startIdx;
   *outNBElement= outIdx;
   free(tempBuffer);
   return retCode;

   return TA_SUCCESS;
}

TA_RetCode TA_S_NATR_Unguarded( int    startIdx,
                                int    endIdx,
                                const float inHigh[],
                                const float inLow[],
                                const float inClose[],
                                int optInTimePeriod,
                                int          *outBegIdx,
                                int          *outNBElement,
                                double        outReal[] )
{
   TA_RetCode retCode;
   int outIdx;
   int today;
   int lookbackTotal;
   int nbATR;
   int outBegIdx1;
   int outNbElement1;
   double prevATR;
   double tempValue;
   double *tempBuffer;

   *outBegIdx= 0;
   *outNBElement= 0;
   lookbackTotal = TA_NATR_Lookback(optInTimePeriod);
   if( (startIdx<lookbackTotal) )
   {
      startIdx = lookbackTotal;
   }
   if( (startIdx>endIdx) )
   {
      return TA_SUCCESS;
   }
   if( (optInTimePeriod<=1) )
   {
      return TA_S_TRANGE_Unguarded(startIdx,endIdx,inHigh,inLow,inClose,outBegIdx,outNBElement,outReal);
   }
   tempBuffer = malloc((((lookbackTotal+(endIdx-startIdx))+1)*sizeof(double)));
   retCode = TA_S_TRANGE_Unguarded(((startIdx-lookbackTotal)+1),endIdx,inHigh,inLow,inClose,&outBegIdx1,&outNbElement1,tempBuffer);
   if( (retCode!=TA_SUCCESS) )
   {
      free(tempBuffer);
      return retCode;
   }
   retCode = TA_SMA_Unguarded((optInTimePeriod-1),(optInTimePeriod-1),tempBuffer,optInTimePeriod,&outBegIdx1,&outNbElement1,&prevATR);
   if( (retCode!=TA_SUCCESS) )
   {
      free(tempBuffer);
      return retCode;
   }
   today = optInTimePeriod;
   outIdx = TA_GLOBALS_UNSTABLE_PERIOD(TA_FUNC_UNST_NATR,Natr);
   while( (outIdx!=0) )
   {
      prevATR *= (optInTimePeriod-1);
      prevATR += tempBuffer[today++];
      prevATR /= optInTimePeriod;
      outIdx -= 1;
   }
   outIdx = 1;
   tempValue = inClose[today];
   if( !(TA_IS_ZERO(tempValue)) )
   {
      outReal[0] = ((prevATR/tempValue)*100.0);
   } else 
   {
      outReal[0] = 0.0;
   }
   nbATR = ((endIdx-startIdx)+1);
   while( (--nbATR!=0) )
   {
      prevATR *= (optInTimePeriod-1);
      prevATR += tempBuffer[today++];
      prevATR /= optInTimePeriod;
      tempValue = inClose[today];
      if( !(TA_IS_ZERO(tempValue)) )
      {
         outReal[outIdx] = ((prevATR/tempValue)*100.0);
      } else 
      {
         outReal[0] = 0.0;
      }
      outIdx += 1;
   }
   *outBegIdx= startIdx;
   *outNBElement= outIdx;
   free(tempBuffer);
   return retCode;

   return TA_SUCCESS;
}

