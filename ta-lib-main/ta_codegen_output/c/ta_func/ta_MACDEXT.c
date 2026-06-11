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

TA_LIB_API int TA_MACDEXT_Lookback( int optInFastPeriod, TA_MAType optInFastMAType, int optInSlowPeriod, TA_MAType optInSlowMAType, int optInSignalPeriod, TA_MAType optInSignalMAType )
{
   int tempInteger;
   int lookbackLargest;
   lookbackLargest = TA_MA_Lookback(optInFastPeriod,optInFastMAType);
   tempInteger = TA_MA_Lookback(optInSlowPeriod,optInSlowMAType);
   if( (tempInteger>lookbackLargest) )
   {
      lookbackLargest = tempInteger;
   }
   return (lookbackLargest+TA_MA_Lookback(optInSignalPeriod,optInSignalMAType));
}

TA_LIB_API TA_RetCode TA_MACDEXT( int    startIdx,
                                  int    endIdx,
                                  const double inReal[],
                                  int optInFastPeriod,
                                  TA_MAType optInFastMAType,
                                  int optInSlowPeriod,
                                  TA_MAType optInSlowMAType,
                                  int optInSignalPeriod,
                                  TA_MAType optInSignalMAType,
                                  int          *outBegIdx,
                                  int          *outNBElement,
                                  double        outMACD[],
                                  double        outMACDSignal[],
                                  double        outMACDHist[] )
{
   double *slowMABuffer;
   double *fastMABuffer;
   TA_RetCode retCode;
   int tempInteger;
   int outBegIdx1;
   int outNbElement1;
   int outBegIdx2;
   int outNbElement2;
   int lookbackTotal;
   int lookbackSignal;
   int lookbackLargest;
   int i;
   int tempMAType;

   if( startIdx < 0 )
      return TA_OUT_OF_RANGE_START_INDEX;
   if( (endIdx < 0) || (endIdx < startIdx) )
      return TA_OUT_OF_RANGE_END_INDEX;

   if( !inReal )
      return TA_BAD_PARAM;
   if( (int)optInFastPeriod == (int)0x80000000 )
      optInFastPeriod = 12;
   else if( (int)optInFastPeriod < 2 || (int)optInFastPeriod > 100000 )
      return TA_BAD_PARAM;
   if( (int)optInFastMAType == (int)0x80000000 )
      optInFastMAType = 0;
   if( (int)optInSlowPeriod == (int)0x80000000 )
      optInSlowPeriod = 26;
   else if( (int)optInSlowPeriod < 2 || (int)optInSlowPeriod > 100000 )
      return TA_BAD_PARAM;
   if( (int)optInSlowMAType == (int)0x80000000 )
      optInSlowMAType = 0;
   if( (int)optInSignalPeriod == (int)0x80000000 )
      optInSignalPeriod = 9;
   else if( (int)optInSignalPeriod < 1 || (int)optInSignalPeriod > 100000 )
      return TA_BAD_PARAM;
   if( (int)optInSignalMAType == (int)0x80000000 )
      optInSignalMAType = 0;
   if( !outMACD )
      return TA_BAD_PARAM;
   if( !outMACDSignal )
      return TA_BAD_PARAM;
   if( !outMACDHist )
      return TA_BAD_PARAM;

   if( (optInSlowPeriod<optInFastPeriod) )
   {
      tempInteger = optInSlowPeriod;
      optInSlowPeriod = optInFastPeriod;
      optInFastPeriod = tempInteger;
      tempMAType = optInSlowMAType;
      optInSlowMAType = optInFastMAType;
      optInFastMAType = tempMAType;
   }
   lookbackLargest = TA_MA_Lookback(optInFastPeriod,optInFastMAType);
   tempInteger = TA_MA_Lookback(optInSlowPeriod,optInSlowMAType);
   if( (tempInteger>lookbackLargest) )
   {
      lookbackLargest = tempInteger;
   }
   lookbackSignal = TA_MA_Lookback(optInSignalPeriod,optInSignalMAType);
   lookbackTotal = (lookbackSignal+lookbackLargest);
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
   tempInteger = (((endIdx-startIdx)+1)+lookbackSignal);
   fastMABuffer = malloc((tempInteger*sizeof(double)));
   if( !(fastMABuffer) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      return TA_ALLOC_ERR;
   }
   slowMABuffer = malloc((tempInteger*sizeof(double)));
   if( !(slowMABuffer) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      free(fastMABuffer);
      return TA_ALLOC_ERR;
   }
   tempInteger = (startIdx-lookbackSignal);
   retCode = TA_MA_Unguarded(tempInteger,endIdx,inReal,optInSlowPeriod,optInSlowMAType,&outBegIdx1,&outNbElement1,slowMABuffer);
   if( (retCode!=TA_SUCCESS) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      free(fastMABuffer);
      free(slowMABuffer);
      return retCode;
   }
   retCode = TA_MA_Unguarded(tempInteger,endIdx,inReal,optInFastPeriod,optInFastMAType,&outBegIdx2,&outNbElement2,fastMABuffer);
   if( (retCode!=TA_SUCCESS) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      free(fastMABuffer);
      free(slowMABuffer);
      return retCode;
   }
   if( ((((outBegIdx1!=tempInteger)||(outBegIdx2!=tempInteger))||(outNbElement1!=outNbElement2))||(outNbElement1!=(((endIdx-startIdx)+1)+lookbackSignal))) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      free(fastMABuffer);
      free(slowMABuffer);
      return TA_BAD_PARAM;
   }
   for( i = 0; (i<outNbElement1); i += 1 )
   {
      fastMABuffer[i] = (fastMABuffer[i]-slowMABuffer[i]);
   }
   memcpy(outMACD,&fastMABuffer[lookbackSignal],(((endIdx-startIdx)+1)*sizeof(double)));
   retCode = TA_MA_Unguarded(0,(outNbElement1-1),fastMABuffer,optInSignalPeriod,optInSignalMAType,&outBegIdx2,&outNbElement2,outMACDSignal);
   free(fastMABuffer);
   free(slowMABuffer);
   if( (retCode!=TA_SUCCESS) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      return retCode;
   }
   for( i = 0; (i<outNbElement2); i += 1 )
   {
      outMACDHist[i] = (outMACD[i]-outMACDSignal[i]);
   }
   *outBegIdx= startIdx;
   *outNBElement= outNbElement2;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_LIB_API TA_RetCode TA_MACDEXT_Unguarded( int    startIdx,
                                            int    endIdx,
                                            const double inReal[],
                                            int optInFastPeriod,
                                            TA_MAType optInFastMAType,
                                            int optInSlowPeriod,
                                            TA_MAType optInSlowMAType,
                                            int optInSignalPeriod,
                                            TA_MAType optInSignalMAType,
                                            int          *outBegIdx,
                                            int          *outNBElement,
                                            double        outMACD[],
                                            double        outMACDSignal[],
                                            double        outMACDHist[] )
{
   double *slowMABuffer;
   double *fastMABuffer;
   TA_RetCode retCode;
   int tempInteger;
   int outBegIdx1;
   int outNbElement1;
   int outBegIdx2;
   int outNbElement2;
   int lookbackTotal;
   int lookbackSignal;
   int lookbackLargest;
   int i;
   int tempMAType;

   if( (optInSlowPeriod<optInFastPeriod) )
   {
      tempInteger = optInSlowPeriod;
      optInSlowPeriod = optInFastPeriod;
      optInFastPeriod = tempInteger;
      tempMAType = optInSlowMAType;
      optInSlowMAType = optInFastMAType;
      optInFastMAType = tempMAType;
   }
   lookbackLargest = TA_MA_Lookback(optInFastPeriod,optInFastMAType);
   tempInteger = TA_MA_Lookback(optInSlowPeriod,optInSlowMAType);
   if( (tempInteger>lookbackLargest) )
   {
      lookbackLargest = tempInteger;
   }
   lookbackSignal = TA_MA_Lookback(optInSignalPeriod,optInSignalMAType);
   lookbackTotal = (lookbackSignal+lookbackLargest);
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
   tempInteger = (((endIdx-startIdx)+1)+lookbackSignal);
   fastMABuffer = malloc((tempInteger*sizeof(double)));
   if( !(fastMABuffer) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      return TA_ALLOC_ERR;
   }
   slowMABuffer = malloc((tempInteger*sizeof(double)));
   if( !(slowMABuffer) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      free(fastMABuffer);
      return TA_ALLOC_ERR;
   }
   tempInteger = (startIdx-lookbackSignal);
   retCode = TA_MA_Unguarded(tempInteger,endIdx,inReal,optInSlowPeriod,optInSlowMAType,&outBegIdx1,&outNbElement1,slowMABuffer);
   if( (retCode!=TA_SUCCESS) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      free(fastMABuffer);
      free(slowMABuffer);
      return retCode;
   }
   retCode = TA_MA_Unguarded(tempInteger,endIdx,inReal,optInFastPeriod,optInFastMAType,&outBegIdx2,&outNbElement2,fastMABuffer);
   if( (retCode!=TA_SUCCESS) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      free(fastMABuffer);
      free(slowMABuffer);
      return retCode;
   }
   if( ((((outBegIdx1!=tempInteger)||(outBegIdx2!=tempInteger))||(outNbElement1!=outNbElement2))||(outNbElement1!=(((endIdx-startIdx)+1)+lookbackSignal))) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      free(fastMABuffer);
      free(slowMABuffer);
      return TA_BAD_PARAM;
   }
   for( i = 0; (i<outNbElement1); i += 1 )
   {
      fastMABuffer[i] = (fastMABuffer[i]-slowMABuffer[i]);
   }
   memcpy(outMACD,&fastMABuffer[lookbackSignal],(((endIdx-startIdx)+1)*sizeof(double)));
   retCode = TA_MA_Unguarded(0,(outNbElement1-1),fastMABuffer,optInSignalPeriod,optInSignalMAType,&outBegIdx2,&outNbElement2,outMACDSignal);
   free(fastMABuffer);
   free(slowMABuffer);
   if( (retCode!=TA_SUCCESS) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      return retCode;
   }
   for( i = 0; (i<outNbElement2); i += 1 )
   {
      outMACDHist[i] = (outMACD[i]-outMACDSignal[i]);
   }
   *outBegIdx= startIdx;
   *outNBElement= outNbElement2;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_MACDEXT( int    startIdx,
                         int    endIdx,
                         const float inReal[],
                         int optInFastPeriod,
                         TA_MAType optInFastMAType,
                         int optInSlowPeriod,
                         TA_MAType optInSlowMAType,
                         int optInSignalPeriod,
                         TA_MAType optInSignalMAType,
                         int          *outBegIdx,
                         int          *outNBElement,
                         double        outMACD[],
                         double        outMACDSignal[],
                         double        outMACDHist[] )
{
   double *slowMABuffer;
   double *fastMABuffer;
   TA_RetCode retCode;
   int tempInteger;
   int outBegIdx1;
   int outNbElement1;
   int outBegIdx2;
   int outNbElement2;
   int lookbackTotal;
   int lookbackSignal;
   int lookbackLargest;
   int i;
   int tempMAType;

   if( startIdx < 0 )
      return TA_OUT_OF_RANGE_START_INDEX;
   if( (endIdx < 0) || (endIdx < startIdx) )
      return TA_OUT_OF_RANGE_END_INDEX;

   if( !inReal )
      return TA_BAD_PARAM;
   if( (int)optInFastPeriod == (int)0x80000000 )
      optInFastPeriod = 12;
   else if( (int)optInFastPeriod < 2 || (int)optInFastPeriod > 100000 )
      return TA_BAD_PARAM;
   if( (int)optInFastMAType == (int)0x80000000 )
      optInFastMAType = 0;
   if( (int)optInSlowPeriod == (int)0x80000000 )
      optInSlowPeriod = 26;
   else if( (int)optInSlowPeriod < 2 || (int)optInSlowPeriod > 100000 )
      return TA_BAD_PARAM;
   if( (int)optInSlowMAType == (int)0x80000000 )
      optInSlowMAType = 0;
   if( (int)optInSignalPeriod == (int)0x80000000 )
      optInSignalPeriod = 9;
   else if( (int)optInSignalPeriod < 1 || (int)optInSignalPeriod > 100000 )
      return TA_BAD_PARAM;
   if( (int)optInSignalMAType == (int)0x80000000 )
      optInSignalMAType = 0;
   if( !outMACD )
      return TA_BAD_PARAM;
   if( !outMACDSignal )
      return TA_BAD_PARAM;
   if( !outMACDHist )
      return TA_BAD_PARAM;

   if( (optInSlowPeriod<optInFastPeriod) )
   {
      tempInteger = optInSlowPeriod;
      optInSlowPeriod = optInFastPeriod;
      optInFastPeriod = tempInteger;
      tempMAType = optInSlowMAType;
      optInSlowMAType = optInFastMAType;
      optInFastMAType = tempMAType;
   }
   lookbackLargest = TA_MA_Lookback(optInFastPeriod,optInFastMAType);
   tempInteger = TA_MA_Lookback(optInSlowPeriod,optInSlowMAType);
   if( (tempInteger>lookbackLargest) )
   {
      lookbackLargest = tempInteger;
   }
   lookbackSignal = TA_MA_Lookback(optInSignalPeriod,optInSignalMAType);
   lookbackTotal = (lookbackSignal+lookbackLargest);
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
   tempInteger = (((endIdx-startIdx)+1)+lookbackSignal);
   fastMABuffer = malloc((tempInteger*sizeof(double)));
   if( !(fastMABuffer) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      return TA_ALLOC_ERR;
   }
   slowMABuffer = malloc((tempInteger*sizeof(double)));
   if( !(slowMABuffer) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      free(fastMABuffer);
      return TA_ALLOC_ERR;
   }
   tempInteger = (startIdx-lookbackSignal);
   retCode = TA_S_MA_Unguarded(tempInteger,endIdx,inReal,optInSlowPeriod,optInSlowMAType,&outBegIdx1,&outNbElement1,slowMABuffer);
   if( (retCode!=TA_SUCCESS) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      free(fastMABuffer);
      free(slowMABuffer);
      return retCode;
   }
   retCode = TA_S_MA_Unguarded(tempInteger,endIdx,inReal,optInFastPeriod,optInFastMAType,&outBegIdx2,&outNbElement2,fastMABuffer);
   if( (retCode!=TA_SUCCESS) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      free(fastMABuffer);
      free(slowMABuffer);
      return retCode;
   }
   if( ((((outBegIdx1!=tempInteger)||(outBegIdx2!=tempInteger))||(outNbElement1!=outNbElement2))||(outNbElement1!=(((endIdx-startIdx)+1)+lookbackSignal))) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      free(fastMABuffer);
      free(slowMABuffer);
      return TA_BAD_PARAM;
   }
   for( i = 0; (i<outNbElement1); i += 1 )
   {
      fastMABuffer[i] = (fastMABuffer[i]-slowMABuffer[i]);
   }
   memcpy(outMACD,&fastMABuffer[lookbackSignal],(((endIdx-startIdx)+1)*sizeof(double)));
   retCode = TA_MA_Unguarded(0,(outNbElement1-1),fastMABuffer,optInSignalPeriod,optInSignalMAType,&outBegIdx2,&outNbElement2,outMACDSignal);
   free(fastMABuffer);
   free(slowMABuffer);
   if( (retCode!=TA_SUCCESS) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      return retCode;
   }
   for( i = 0; (i<outNbElement2); i += 1 )
   {
      outMACDHist[i] = (outMACD[i]-outMACDSignal[i]);
   }
   *outBegIdx= startIdx;
   *outNBElement= outNbElement2;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_MACDEXT_Unguarded( int    startIdx,
                                   int    endIdx,
                                   const float inReal[],
                                   int optInFastPeriod,
                                   TA_MAType optInFastMAType,
                                   int optInSlowPeriod,
                                   TA_MAType optInSlowMAType,
                                   int optInSignalPeriod,
                                   TA_MAType optInSignalMAType,
                                   int          *outBegIdx,
                                   int          *outNBElement,
                                   double        outMACD[],
                                   double        outMACDSignal[],
                                   double        outMACDHist[] )
{
   double *slowMABuffer;
   double *fastMABuffer;
   TA_RetCode retCode;
   int tempInteger;
   int outBegIdx1;
   int outNbElement1;
   int outBegIdx2;
   int outNbElement2;
   int lookbackTotal;
   int lookbackSignal;
   int lookbackLargest;
   int i;
   int tempMAType;

   if( (optInSlowPeriod<optInFastPeriod) )
   {
      tempInteger = optInSlowPeriod;
      optInSlowPeriod = optInFastPeriod;
      optInFastPeriod = tempInteger;
      tempMAType = optInSlowMAType;
      optInSlowMAType = optInFastMAType;
      optInFastMAType = tempMAType;
   }
   lookbackLargest = TA_MA_Lookback(optInFastPeriod,optInFastMAType);
   tempInteger = TA_MA_Lookback(optInSlowPeriod,optInSlowMAType);
   if( (tempInteger>lookbackLargest) )
   {
      lookbackLargest = tempInteger;
   }
   lookbackSignal = TA_MA_Lookback(optInSignalPeriod,optInSignalMAType);
   lookbackTotal = (lookbackSignal+lookbackLargest);
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
   tempInteger = (((endIdx-startIdx)+1)+lookbackSignal);
   fastMABuffer = malloc((tempInteger*sizeof(double)));
   if( !(fastMABuffer) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      return TA_ALLOC_ERR;
   }
   slowMABuffer = malloc((tempInteger*sizeof(double)));
   if( !(slowMABuffer) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      free(fastMABuffer);
      return TA_ALLOC_ERR;
   }
   tempInteger = (startIdx-lookbackSignal);
   retCode = TA_S_MA_Unguarded(tempInteger,endIdx,inReal,optInSlowPeriod,optInSlowMAType,&outBegIdx1,&outNbElement1,slowMABuffer);
   if( (retCode!=TA_SUCCESS) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      free(fastMABuffer);
      free(slowMABuffer);
      return retCode;
   }
   retCode = TA_S_MA_Unguarded(tempInteger,endIdx,inReal,optInFastPeriod,optInFastMAType,&outBegIdx2,&outNbElement2,fastMABuffer);
   if( (retCode!=TA_SUCCESS) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      free(fastMABuffer);
      free(slowMABuffer);
      return retCode;
   }
   if( ((((outBegIdx1!=tempInteger)||(outBegIdx2!=tempInteger))||(outNbElement1!=outNbElement2))||(outNbElement1!=(((endIdx-startIdx)+1)+lookbackSignal))) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      free(fastMABuffer);
      free(slowMABuffer);
      return TA_BAD_PARAM;
   }
   for( i = 0; (i<outNbElement1); i += 1 )
   {
      fastMABuffer[i] = (fastMABuffer[i]-slowMABuffer[i]);
   }
   memcpy(outMACD,&fastMABuffer[lookbackSignal],(((endIdx-startIdx)+1)*sizeof(double)));
   retCode = TA_MA_Unguarded(0,(outNbElement1-1),fastMABuffer,optInSignalPeriod,optInSignalMAType,&outBegIdx2,&outNbElement2,outMACDSignal);
   free(fastMABuffer);
   free(slowMABuffer);
   if( (retCode!=TA_SUCCESS) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      return retCode;
   }
   for( i = 0; (i<outNbElement2); i += 1 )
   {
      outMACDHist[i] = (outMACD[i]-outMACDSignal[i]);
   }
   *outBegIdx= startIdx;
   *outNBElement= outNbElement2;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

