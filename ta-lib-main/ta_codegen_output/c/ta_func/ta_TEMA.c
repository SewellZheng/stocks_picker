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

TA_LIB_API int TA_TEMA_Lookback( int optInTimePeriod )
{
   int retValue;
   retValue = TA_EMA_Lookback(optInTimePeriod);
   return (retValue*3);
}

TA_LIB_API TA_RetCode TA_TEMA( int    startIdx,
                               int    endIdx,
                               const double inReal[],
                               int optInTimePeriod,
                               int          *outBegIdx,
                               int          *outNBElement,
                               double        outReal[] )
{
   double *firstEMA;
   double *secondEMA;
   int firstEMABegIdx;
   int firstEMANbElement;
   int secondEMABegIdx;
   int secondEMANbElement;
   int thirdEMABegIdx;
   int thirdEMANbElement;
   int tempInt;
   int outIdx;
   int lookbackTotal;
   int lookbackEMA;
   int firstEMAIdx;
   int secondEMAIdx;
   TA_RetCode retCode;

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

   *outNBElement= 0;
   *outBegIdx= 0;
   lookbackEMA = TA_EMA_Lookback(optInTimePeriod);
   lookbackTotal = (lookbackEMA*3);
   if( (startIdx<lookbackTotal) )
   {
      startIdx = lookbackTotal;
   }
   if( (startIdx>endIdx) )
   {
      return TA_SUCCESS;
   }
   tempInt = ((lookbackTotal+(endIdx-startIdx))+1);
   firstEMA = malloc((tempInt*sizeof(double)));
   if( !(firstEMA) )
   {
      return TA_ALLOC_ERR;
   }
   retCode = TA_EMA_Unguarded((startIdx-(lookbackEMA*2)),endIdx,inReal,optInTimePeriod,&firstEMABegIdx,&firstEMANbElement,firstEMA);
   if( ((retCode!=TA_SUCCESS)||(firstEMANbElement==0)) )
   {
      free(firstEMA);
      return retCode;
   }
   secondEMA = malloc((firstEMANbElement*sizeof(double)));
   if( !(secondEMA) )
   {
      free(firstEMA);
      return TA_ALLOC_ERR;
   }
   retCode = TA_EMA_Unguarded(0,(firstEMANbElement-1),firstEMA,optInTimePeriod,&secondEMABegIdx,&secondEMANbElement,secondEMA);
   if( ((retCode!=TA_SUCCESS)||(secondEMANbElement==0)) )
   {
      free(firstEMA);
      free(secondEMA);
      return retCode;
   }
   retCode = TA_EMA_Unguarded(0,(secondEMANbElement-1),secondEMA,optInTimePeriod,&thirdEMABegIdx,&thirdEMANbElement,outReal);
   if( ((retCode!=TA_SUCCESS)||(thirdEMANbElement==0)) )
   {
      free(firstEMA);
      free(secondEMA);
      return retCode;
   }
   firstEMAIdx = (thirdEMABegIdx+secondEMABegIdx);
   secondEMAIdx = thirdEMABegIdx;
   *outBegIdx= (firstEMAIdx+firstEMABegIdx);
   outIdx = 0;
   while( (outIdx<thirdEMANbElement) )
   {
      outReal[outIdx] = (outReal[outIdx]+((3.0*firstEMA[firstEMAIdx++])-(3.0*secondEMA[secondEMAIdx++])));
      outIdx += 1;
   }
   free(firstEMA);
   free(secondEMA);
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_LIB_API TA_RetCode TA_TEMA_Unguarded( int    startIdx,
                                         int    endIdx,
                                         const double inReal[],
                                         int optInTimePeriod,
                                         int          *outBegIdx,
                                         int          *outNBElement,
                                         double        outReal[] )
{
   double *firstEMA;
   double *secondEMA;
   int firstEMABegIdx;
   int firstEMANbElement;
   int secondEMABegIdx;
   int secondEMANbElement;
   int thirdEMABegIdx;
   int thirdEMANbElement;
   int tempInt;
   int outIdx;
   int lookbackTotal;
   int lookbackEMA;
   int firstEMAIdx;
   int secondEMAIdx;
   TA_RetCode retCode;

   *outNBElement= 0;
   *outBegIdx= 0;
   lookbackEMA = TA_EMA_Lookback(optInTimePeriod);
   lookbackTotal = (lookbackEMA*3);
   if( (startIdx<lookbackTotal) )
   {
      startIdx = lookbackTotal;
   }
   if( (startIdx>endIdx) )
   {
      return TA_SUCCESS;
   }
   tempInt = ((lookbackTotal+(endIdx-startIdx))+1);
   firstEMA = malloc((tempInt*sizeof(double)));
   if( !(firstEMA) )
   {
      return TA_ALLOC_ERR;
   }
   retCode = TA_EMA_Unguarded((startIdx-(lookbackEMA*2)),endIdx,inReal,optInTimePeriod,&firstEMABegIdx,&firstEMANbElement,firstEMA);
   if( ((retCode!=TA_SUCCESS)||(firstEMANbElement==0)) )
   {
      free(firstEMA);
      return retCode;
   }
   secondEMA = malloc((firstEMANbElement*sizeof(double)));
   if( !(secondEMA) )
   {
      free(firstEMA);
      return TA_ALLOC_ERR;
   }
   retCode = TA_EMA_Unguarded(0,(firstEMANbElement-1),firstEMA,optInTimePeriod,&secondEMABegIdx,&secondEMANbElement,secondEMA);
   if( ((retCode!=TA_SUCCESS)||(secondEMANbElement==0)) )
   {
      free(firstEMA);
      free(secondEMA);
      return retCode;
   }
   retCode = TA_EMA_Unguarded(0,(secondEMANbElement-1),secondEMA,optInTimePeriod,&thirdEMABegIdx,&thirdEMANbElement,outReal);
   if( ((retCode!=TA_SUCCESS)||(thirdEMANbElement==0)) )
   {
      free(firstEMA);
      free(secondEMA);
      return retCode;
   }
   firstEMAIdx = (thirdEMABegIdx+secondEMABegIdx);
   secondEMAIdx = thirdEMABegIdx;
   *outBegIdx= (firstEMAIdx+firstEMABegIdx);
   outIdx = 0;
   while( (outIdx<thirdEMANbElement) )
   {
      outReal[outIdx] = (outReal[outIdx]+((3.0*firstEMA[firstEMAIdx++])-(3.0*secondEMA[secondEMAIdx++])));
      outIdx += 1;
   }
   free(firstEMA);
   free(secondEMA);
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_TEMA( int    startIdx,
                      int    endIdx,
                      const float inReal[],
                      int optInTimePeriod,
                      int          *outBegIdx,
                      int          *outNBElement,
                      double        outReal[] )
{
   double *firstEMA;
   double *secondEMA;
   int firstEMABegIdx;
   int firstEMANbElement;
   int secondEMABegIdx;
   int secondEMANbElement;
   int thirdEMABegIdx;
   int thirdEMANbElement;
   int tempInt;
   int outIdx;
   int lookbackTotal;
   int lookbackEMA;
   int firstEMAIdx;
   int secondEMAIdx;
   TA_RetCode retCode;

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

   *outNBElement= 0;
   *outBegIdx= 0;
   lookbackEMA = TA_EMA_Lookback(optInTimePeriod);
   lookbackTotal = (lookbackEMA*3);
   if( (startIdx<lookbackTotal) )
   {
      startIdx = lookbackTotal;
   }
   if( (startIdx>endIdx) )
   {
      return TA_SUCCESS;
   }
   tempInt = ((lookbackTotal+(endIdx-startIdx))+1);
   firstEMA = malloc((tempInt*sizeof(double)));
   if( !(firstEMA) )
   {
      return TA_ALLOC_ERR;
   }
   retCode = TA_S_EMA_Unguarded((startIdx-(lookbackEMA*2)),endIdx,inReal,optInTimePeriod,&firstEMABegIdx,&firstEMANbElement,firstEMA);
   if( ((retCode!=TA_SUCCESS)||(firstEMANbElement==0)) )
   {
      free(firstEMA);
      return retCode;
   }
   secondEMA = malloc((firstEMANbElement*sizeof(double)));
   if( !(secondEMA) )
   {
      free(firstEMA);
      return TA_ALLOC_ERR;
   }
   retCode = TA_EMA_Unguarded(0,(firstEMANbElement-1),firstEMA,optInTimePeriod,&secondEMABegIdx,&secondEMANbElement,secondEMA);
   if( ((retCode!=TA_SUCCESS)||(secondEMANbElement==0)) )
   {
      free(firstEMA);
      free(secondEMA);
      return retCode;
   }
   retCode = TA_EMA_Unguarded(0,(secondEMANbElement-1),secondEMA,optInTimePeriod,&thirdEMABegIdx,&thirdEMANbElement,outReal);
   if( ((retCode!=TA_SUCCESS)||(thirdEMANbElement==0)) )
   {
      free(firstEMA);
      free(secondEMA);
      return retCode;
   }
   firstEMAIdx = (thirdEMABegIdx+secondEMABegIdx);
   secondEMAIdx = thirdEMABegIdx;
   *outBegIdx= (firstEMAIdx+firstEMABegIdx);
   outIdx = 0;
   while( (outIdx<thirdEMANbElement) )
   {
      outReal[outIdx] = (outReal[outIdx]+((3.0*firstEMA[firstEMAIdx++])-(3.0*secondEMA[secondEMAIdx++])));
      outIdx += 1;
   }
   free(firstEMA);
   free(secondEMA);
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_TEMA_Unguarded( int    startIdx,
                                int    endIdx,
                                const float inReal[],
                                int optInTimePeriod,
                                int          *outBegIdx,
                                int          *outNBElement,
                                double        outReal[] )
{
   double *firstEMA;
   double *secondEMA;
   int firstEMABegIdx;
   int firstEMANbElement;
   int secondEMABegIdx;
   int secondEMANbElement;
   int thirdEMABegIdx;
   int thirdEMANbElement;
   int tempInt;
   int outIdx;
   int lookbackTotal;
   int lookbackEMA;
   int firstEMAIdx;
   int secondEMAIdx;
   TA_RetCode retCode;

   *outNBElement= 0;
   *outBegIdx= 0;
   lookbackEMA = TA_EMA_Lookback(optInTimePeriod);
   lookbackTotal = (lookbackEMA*3);
   if( (startIdx<lookbackTotal) )
   {
      startIdx = lookbackTotal;
   }
   if( (startIdx>endIdx) )
   {
      return TA_SUCCESS;
   }
   tempInt = ((lookbackTotal+(endIdx-startIdx))+1);
   firstEMA = malloc((tempInt*sizeof(double)));
   if( !(firstEMA) )
   {
      return TA_ALLOC_ERR;
   }
   retCode = TA_S_EMA_Unguarded((startIdx-(lookbackEMA*2)),endIdx,inReal,optInTimePeriod,&firstEMABegIdx,&firstEMANbElement,firstEMA);
   if( ((retCode!=TA_SUCCESS)||(firstEMANbElement==0)) )
   {
      free(firstEMA);
      return retCode;
   }
   secondEMA = malloc((firstEMANbElement*sizeof(double)));
   if( !(secondEMA) )
   {
      free(firstEMA);
      return TA_ALLOC_ERR;
   }
   retCode = TA_EMA_Unguarded(0,(firstEMANbElement-1),firstEMA,optInTimePeriod,&secondEMABegIdx,&secondEMANbElement,secondEMA);
   if( ((retCode!=TA_SUCCESS)||(secondEMANbElement==0)) )
   {
      free(firstEMA);
      free(secondEMA);
      return retCode;
   }
   retCode = TA_EMA_Unguarded(0,(secondEMANbElement-1),secondEMA,optInTimePeriod,&thirdEMABegIdx,&thirdEMANbElement,outReal);
   if( ((retCode!=TA_SUCCESS)||(thirdEMANbElement==0)) )
   {
      free(firstEMA);
      free(secondEMA);
      return retCode;
   }
   firstEMAIdx = (thirdEMABegIdx+secondEMABegIdx);
   secondEMAIdx = thirdEMABegIdx;
   *outBegIdx= (firstEMAIdx+firstEMABegIdx);
   outIdx = 0;
   while( (outIdx<thirdEMANbElement) )
   {
      outReal[outIdx] = (outReal[outIdx]+((3.0*firstEMA[firstEMAIdx++])-(3.0*secondEMA[secondEMAIdx++])));
      outIdx += 1;
   }
   free(firstEMA);
   free(secondEMA);
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

