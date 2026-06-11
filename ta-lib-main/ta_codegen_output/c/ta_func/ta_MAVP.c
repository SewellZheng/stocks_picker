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

TA_LIB_API int TA_MAVP_Lookback( int optInMinPeriod, int optInMaxPeriod, TA_MAType optInMAType )
{
   return TA_MA_Lookback(optInMaxPeriod,optInMAType);
}

TA_LIB_API TA_RetCode TA_MAVP( int    startIdx,
                               int    endIdx,
                               const double inReal[],
                               const double inPeriods[],
                               int optInMinPeriod,
                               int optInMaxPeriod,
                               TA_MAType optInMAType,
                               int          *outBegIdx,
                               int          *outNBElement,
                               double        outReal[] )
{
   int i;
   int j;
   int lookbackTotal;
   int outputSize;
   int tempInt;
   int curPeriod;
   int *localPeriodArray;
   double *localOutputArray;
   int localBegIdx;
   int localNbElement;
   TA_RetCode retCode;

   if( startIdx < 0 )
      return TA_OUT_OF_RANGE_START_INDEX;
   if( (endIdx < 0) || (endIdx < startIdx) )
      return TA_OUT_OF_RANGE_END_INDEX;

   if( !inReal )
      return TA_BAD_PARAM;
   if( !inPeriods )
      return TA_BAD_PARAM;
   if( (int)optInMinPeriod == (int)0x80000000 )
      optInMinPeriod = 2;
   else if( (int)optInMinPeriod < 2 || (int)optInMinPeriod > 100000 )
      return TA_BAD_PARAM;
   if( (int)optInMaxPeriod == (int)0x80000000 )
      optInMaxPeriod = 30;
   else if( (int)optInMaxPeriod < 2 || (int)optInMaxPeriod > 100000 )
      return TA_BAD_PARAM;
   if( (int)optInMAType == (int)0x80000000 )
      optInMAType = 0;
   if( !outReal )
      return TA_BAD_PARAM;

   lookbackTotal = TA_MA_Lookback(optInMaxPeriod,optInMAType);
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
   if( (lookbackTotal>startIdx) )
   {
      tempInt = lookbackTotal;
   } else 
   {
      tempInt = startIdx;
   }
   if( (tempInt>endIdx) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      return TA_SUCCESS;
   }
   outputSize = ((endIdx-tempInt)+1);
   localOutputArray = malloc((outputSize*sizeof(double)));
   localPeriodArray = malloc((outputSize*sizeof(int)));
   for( i = 0; (i<outputSize); i += 1 )
   {
      tempInt = ((int)inPeriods[(startIdx+i)]);
      if( (tempInt<optInMinPeriod) )
      {
         tempInt = optInMinPeriod;
      } else if( (tempInt>optInMaxPeriod) )
      {
         tempInt = optInMaxPeriod;
      }
      localPeriodArray[i] = tempInt;
   }
   for( i = 0; (i<outputSize); i += 1 )
   {
      curPeriod = localPeriodArray[i];
      if( (curPeriod!=0) )
      {
         retCode = TA_MA_Unguarded(startIdx,endIdx,inReal,curPeriod,optInMAType,&localBegIdx,&localNbElement,localOutputArray);
         if( (retCode!=TA_SUCCESS) )
         {
            free(localOutputArray);
            free(localPeriodArray);
            *outBegIdx= 0;
            *outNBElement= 0;
            return retCode;
         }
         outReal[i] = localOutputArray[i];
         for( j = (i+1); (j<outputSize); j += 1 )
         {
            if( (localPeriodArray[j]==curPeriod) )
            {
               localPeriodArray[j] = 0;
               outReal[j] = localOutputArray[j];
            }
         }
      }
   }
   free(localOutputArray);
   free(localPeriodArray);
   *outBegIdx= startIdx;
   *outNBElement= outputSize;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_LIB_API TA_RetCode TA_MAVP_Unguarded( int    startIdx,
                                         int    endIdx,
                                         const double inReal[],
                                         const double inPeriods[],
                                         int optInMinPeriod,
                                         int optInMaxPeriod,
                                         TA_MAType optInMAType,
                                         int          *outBegIdx,
                                         int          *outNBElement,
                                         double        outReal[] )
{
   int i;
   int j;
   int lookbackTotal;
   int outputSize;
   int tempInt;
   int curPeriod;
   int *localPeriodArray;
   double *localOutputArray;
   int localBegIdx;
   int localNbElement;
   TA_RetCode retCode;

   lookbackTotal = TA_MA_Lookback(optInMaxPeriod,optInMAType);
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
   if( (lookbackTotal>startIdx) )
   {
      tempInt = lookbackTotal;
   } else 
   {
      tempInt = startIdx;
   }
   if( (tempInt>endIdx) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      return TA_SUCCESS;
   }
   outputSize = ((endIdx-tempInt)+1);
   localOutputArray = malloc((outputSize*sizeof(double)));
   localPeriodArray = malloc((outputSize*sizeof(int)));
   for( i = 0; (i<outputSize); i += 1 )
   {
      tempInt = ((int)inPeriods[(startIdx+i)]);
      if( (tempInt<optInMinPeriod) )
      {
         tempInt = optInMinPeriod;
      } else if( (tempInt>optInMaxPeriod) )
      {
         tempInt = optInMaxPeriod;
      }
      localPeriodArray[i] = tempInt;
   }
   for( i = 0; (i<outputSize); i += 1 )
   {
      curPeriod = localPeriodArray[i];
      if( (curPeriod!=0) )
      {
         retCode = TA_MA_Unguarded(startIdx,endIdx,inReal,curPeriod,optInMAType,&localBegIdx,&localNbElement,localOutputArray);
         if( (retCode!=TA_SUCCESS) )
         {
            free(localOutputArray);
            free(localPeriodArray);
            *outBegIdx= 0;
            *outNBElement= 0;
            return retCode;
         }
         outReal[i] = localOutputArray[i];
         for( j = (i+1); (j<outputSize); j += 1 )
         {
            if( (localPeriodArray[j]==curPeriod) )
            {
               localPeriodArray[j] = 0;
               outReal[j] = localOutputArray[j];
            }
         }
      }
   }
   free(localOutputArray);
   free(localPeriodArray);
   *outBegIdx= startIdx;
   *outNBElement= outputSize;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_MAVP( int    startIdx,
                      int    endIdx,
                      const float inReal[],
                      const float inPeriods[],
                      int optInMinPeriod,
                      int optInMaxPeriod,
                      TA_MAType optInMAType,
                      int          *outBegIdx,
                      int          *outNBElement,
                      double        outReal[] )
{
   int i;
   int j;
   int lookbackTotal;
   int outputSize;
   int tempInt;
   int curPeriod;
   int *localPeriodArray;
   double *localOutputArray;
   int localBegIdx;
   int localNbElement;
   TA_RetCode retCode;

   if( startIdx < 0 )
      return TA_OUT_OF_RANGE_START_INDEX;
   if( (endIdx < 0) || (endIdx < startIdx) )
      return TA_OUT_OF_RANGE_END_INDEX;

   if( !inReal )
      return TA_BAD_PARAM;
   if( !inPeriods )
      return TA_BAD_PARAM;
   if( (int)optInMinPeriod == (int)0x80000000 )
      optInMinPeriod = 2;
   else if( (int)optInMinPeriod < 2 || (int)optInMinPeriod > 100000 )
      return TA_BAD_PARAM;
   if( (int)optInMaxPeriod == (int)0x80000000 )
      optInMaxPeriod = 30;
   else if( (int)optInMaxPeriod < 2 || (int)optInMaxPeriod > 100000 )
      return TA_BAD_PARAM;
   if( (int)optInMAType == (int)0x80000000 )
      optInMAType = 0;
   if( !outReal )
      return TA_BAD_PARAM;

   lookbackTotal = TA_MA_Lookback(optInMaxPeriod,optInMAType);
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
   if( (lookbackTotal>startIdx) )
   {
      tempInt = lookbackTotal;
   } else 
   {
      tempInt = startIdx;
   }
   if( (tempInt>endIdx) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      return TA_SUCCESS;
   }
   outputSize = ((endIdx-tempInt)+1);
   localOutputArray = malloc((outputSize*sizeof(double)));
   localPeriodArray = malloc((outputSize*sizeof(int)));
   for( i = 0; (i<outputSize); i += 1 )
   {
      tempInt = ((int)inPeriods[(startIdx+i)]);
      if( (tempInt<optInMinPeriod) )
      {
         tempInt = optInMinPeriod;
      } else if( (tempInt>optInMaxPeriod) )
      {
         tempInt = optInMaxPeriod;
      }
      localPeriodArray[i] = tempInt;
   }
   for( i = 0; (i<outputSize); i += 1 )
   {
      curPeriod = localPeriodArray[i];
      if( (curPeriod!=0) )
      {
         retCode = TA_S_MA_Unguarded(startIdx,endIdx,inReal,curPeriod,optInMAType,&localBegIdx,&localNbElement,localOutputArray);
         if( (retCode!=TA_SUCCESS) )
         {
            free(localOutputArray);
            free(localPeriodArray);
            *outBegIdx= 0;
            *outNBElement= 0;
            return retCode;
         }
         outReal[i] = localOutputArray[i];
         for( j = (i+1); (j<outputSize); j += 1 )
         {
            if( (localPeriodArray[j]==curPeriod) )
            {
               localPeriodArray[j] = 0;
               outReal[j] = localOutputArray[j];
            }
         }
      }
   }
   free(localOutputArray);
   free(localPeriodArray);
   *outBegIdx= startIdx;
   *outNBElement= outputSize;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_MAVP_Unguarded( int    startIdx,
                                int    endIdx,
                                const float inReal[],
                                const float inPeriods[],
                                int optInMinPeriod,
                                int optInMaxPeriod,
                                TA_MAType optInMAType,
                                int          *outBegIdx,
                                int          *outNBElement,
                                double        outReal[] )
{
   int i;
   int j;
   int lookbackTotal;
   int outputSize;
   int tempInt;
   int curPeriod;
   int *localPeriodArray;
   double *localOutputArray;
   int localBegIdx;
   int localNbElement;
   TA_RetCode retCode;

   lookbackTotal = TA_MA_Lookback(optInMaxPeriod,optInMAType);
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
   if( (lookbackTotal>startIdx) )
   {
      tempInt = lookbackTotal;
   } else 
   {
      tempInt = startIdx;
   }
   if( (tempInt>endIdx) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      return TA_SUCCESS;
   }
   outputSize = ((endIdx-tempInt)+1);
   localOutputArray = malloc((outputSize*sizeof(double)));
   localPeriodArray = malloc((outputSize*sizeof(int)));
   for( i = 0; (i<outputSize); i += 1 )
   {
      tempInt = ((int)inPeriods[(startIdx+i)]);
      if( (tempInt<optInMinPeriod) )
      {
         tempInt = optInMinPeriod;
      } else if( (tempInt>optInMaxPeriod) )
      {
         tempInt = optInMaxPeriod;
      }
      localPeriodArray[i] = tempInt;
   }
   for( i = 0; (i<outputSize); i += 1 )
   {
      curPeriod = localPeriodArray[i];
      if( (curPeriod!=0) )
      {
         retCode = TA_S_MA_Unguarded(startIdx,endIdx,inReal,curPeriod,optInMAType,&localBegIdx,&localNbElement,localOutputArray);
         if( (retCode!=TA_SUCCESS) )
         {
            free(localOutputArray);
            free(localPeriodArray);
            *outBegIdx= 0;
            *outNBElement= 0;
            return retCode;
         }
         outReal[i] = localOutputArray[i];
         for( j = (i+1); (j<outputSize); j += 1 )
         {
            if( (localPeriodArray[j]==curPeriod) )
            {
               localPeriodArray[j] = 0;
               outReal[j] = localOutputArray[j];
            }
         }
      }
   }
   free(localOutputArray);
   free(localPeriodArray);
   *outBegIdx= startIdx;
   *outNBElement= outputSize;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

