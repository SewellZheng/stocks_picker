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

TA_LIB_API int TA_KAMA_Lookback( int optInTimePeriod )
{
   return (optInTimePeriod+TA_GLOBALS_UNSTABLE_PERIOD(TA_FUNC_UNST_KAMA,Kama));
}

TA_LIB_API TA_RetCode TA_KAMA( int    startIdx,
                               int    endIdx,
                               const double inReal[],
                               int optInTimePeriod,
                               int          *outBegIdx,
                               int          *outNBElement,
                               double        outReal[] )
{
   double constMax;
   double constDiff;
   double tempReal;
   double tempReal2;
   double sumROC1;
   double periodROC;
   double prevKAMA;
   int i;
   int today;
   int outIdx;
   int lookbackTotal;
   int trailingIdx;
   double trailingValue;

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

   constMax = (2.0/(30.0+1.0));
   constDiff = ((2.0/(2.0+1.0))-constMax);
   *outBegIdx= 0;
   *outNBElement= 0;
   lookbackTotal = (optInTimePeriod+TA_GLOBALS_UNSTABLE_PERIOD(TA_FUNC_UNST_KAMA,Kama));
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
   sumROC1 = 0.0;
   today = (startIdx-lookbackTotal);
   trailingIdx = today;
   i = optInTimePeriod;
   while( (i-->0) )
   {
      tempReal = inReal[today++];
      tempReal -= inReal[today];
      sumROC1 += fabs(tempReal);
   }
   prevKAMA = inReal[(today-1)];
   tempReal = inReal[today];
   tempReal2 = inReal[trailingIdx++];
   periodROC = (tempReal-tempReal2);
   trailingValue = tempReal2;
   if( ((sumROC1<=periodROC)||TA_IS_ZERO(sumROC1)) )
   {
      tempReal = 1.0;
   } else 
   {
      tempReal = fabs((periodROC/sumROC1));
   }
   tempReal = ((tempReal*constDiff)+constMax);
   tempReal *= tempReal;
   prevKAMA = (((inReal[today++]-prevKAMA)*tempReal)+prevKAMA);
   while( (today<=startIdx) )
   {
      tempReal = inReal[today];
      tempReal2 = inReal[trailingIdx++];
      periodROC = (tempReal-tempReal2);
      sumROC1 -= fabs((trailingValue-tempReal2));
      sumROC1 += fabs((tempReal-inReal[(today-1)]));
      trailingValue = tempReal2;
      if( ((sumROC1<=periodROC)||TA_IS_ZERO(sumROC1)) )
      {
         tempReal = 1.0;
      } else 
      {
         tempReal = fabs((periodROC/sumROC1));
      }
      tempReal = ((tempReal*constDiff)+constMax);
      tempReal *= tempReal;
      prevKAMA = (((inReal[today++]-prevKAMA)*tempReal)+prevKAMA);
   }
   outReal[0] = prevKAMA;
   outIdx = 1;
   *outBegIdx= (today-1);
   while( (today<=endIdx) )
   {
      tempReal = inReal[today];
      tempReal2 = inReal[trailingIdx++];
      periodROC = (tempReal-tempReal2);
      sumROC1 -= fabs((trailingValue-tempReal2));
      sumROC1 += fabs((tempReal-inReal[(today-1)]));
      trailingValue = tempReal2;
      if( ((sumROC1<=periodROC)||TA_IS_ZERO(sumROC1)) )
      {
         tempReal = 1.0;
      } else 
      {
         tempReal = fabs((periodROC/sumROC1));
      }
      tempReal = ((tempReal*constDiff)+constMax);
      tempReal *= tempReal;
      prevKAMA = (((inReal[today++]-prevKAMA)*tempReal)+prevKAMA);
      outReal[outIdx++] = prevKAMA;
   }
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_LIB_API TA_RetCode TA_KAMA_Unguarded( int    startIdx,
                                         int    endIdx,
                                         const double inReal[],
                                         int optInTimePeriod,
                                         int          *outBegIdx,
                                         int          *outNBElement,
                                         double        outReal[] )
{
   double constMax;
   double constDiff;
   double tempReal;
   double tempReal2;
   double sumROC1;
   double periodROC;
   double prevKAMA;
   int i;
   int today;
   int outIdx;
   int lookbackTotal;
   int trailingIdx;
   double trailingValue;

   constMax = (2.0/(30.0+1.0));
   constDiff = ((2.0/(2.0+1.0))-constMax);
   *outBegIdx= 0;
   *outNBElement= 0;
   lookbackTotal = (optInTimePeriod+TA_GLOBALS_UNSTABLE_PERIOD(TA_FUNC_UNST_KAMA,Kama));
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
   sumROC1 = 0.0;
   today = (startIdx-lookbackTotal);
   trailingIdx = today;
   i = optInTimePeriod;
   while( (i-->0) )
   {
      tempReal = inReal[today++];
      tempReal -= inReal[today];
      sumROC1 += fabs(tempReal);
   }
   prevKAMA = inReal[(today-1)];
   tempReal = inReal[today];
   tempReal2 = inReal[trailingIdx++];
   periodROC = (tempReal-tempReal2);
   trailingValue = tempReal2;
   if( ((sumROC1<=periodROC)||TA_IS_ZERO(sumROC1)) )
   {
      tempReal = 1.0;
   } else 
   {
      tempReal = fabs((periodROC/sumROC1));
   }
   tempReal = ((tempReal*constDiff)+constMax);
   tempReal *= tempReal;
   prevKAMA = (((inReal[today++]-prevKAMA)*tempReal)+prevKAMA);
   while( (today<=startIdx) )
   {
      tempReal = inReal[today];
      tempReal2 = inReal[trailingIdx++];
      periodROC = (tempReal-tempReal2);
      sumROC1 -= fabs((trailingValue-tempReal2));
      sumROC1 += fabs((tempReal-inReal[(today-1)]));
      trailingValue = tempReal2;
      if( ((sumROC1<=periodROC)||TA_IS_ZERO(sumROC1)) )
      {
         tempReal = 1.0;
      } else 
      {
         tempReal = fabs((periodROC/sumROC1));
      }
      tempReal = ((tempReal*constDiff)+constMax);
      tempReal *= tempReal;
      prevKAMA = (((inReal[today++]-prevKAMA)*tempReal)+prevKAMA);
   }
   outReal[0] = prevKAMA;
   outIdx = 1;
   *outBegIdx= (today-1);
   while( (today<=endIdx) )
   {
      tempReal = inReal[today];
      tempReal2 = inReal[trailingIdx++];
      periodROC = (tempReal-tempReal2);
      sumROC1 -= fabs((trailingValue-tempReal2));
      sumROC1 += fabs((tempReal-inReal[(today-1)]));
      trailingValue = tempReal2;
      if( ((sumROC1<=periodROC)||TA_IS_ZERO(sumROC1)) )
      {
         tempReal = 1.0;
      } else 
      {
         tempReal = fabs((periodROC/sumROC1));
      }
      tempReal = ((tempReal*constDiff)+constMax);
      tempReal *= tempReal;
      prevKAMA = (((inReal[today++]-prevKAMA)*tempReal)+prevKAMA);
      outReal[outIdx++] = prevKAMA;
   }
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_KAMA( int    startIdx,
                      int    endIdx,
                      const float inReal[],
                      int optInTimePeriod,
                      int          *outBegIdx,
                      int          *outNBElement,
                      double        outReal[] )
{
   double constMax;
   double constDiff;
   double tempReal;
   double tempReal2;
   double sumROC1;
   double periodROC;
   double prevKAMA;
   int i;
   int today;
   int outIdx;
   int lookbackTotal;
   int trailingIdx;
   double trailingValue;

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

   constMax = (2.0/(30.0+1.0));
   constDiff = ((2.0/(2.0+1.0))-constMax);
   *outBegIdx= 0;
   *outNBElement= 0;
   lookbackTotal = (optInTimePeriod+TA_GLOBALS_UNSTABLE_PERIOD(TA_FUNC_UNST_KAMA,Kama));
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
   sumROC1 = 0.0;
   today = (startIdx-lookbackTotal);
   trailingIdx = today;
   i = optInTimePeriod;
   while( (i-->0) )
   {
      tempReal = inReal[today++];
      tempReal -= inReal[today];
      sumROC1 += fabs(tempReal);
   }
   prevKAMA = inReal[(today-1)];
   tempReal = inReal[today];
   tempReal2 = inReal[trailingIdx++];
   periodROC = (tempReal-tempReal2);
   trailingValue = tempReal2;
   if( ((sumROC1<=periodROC)||TA_IS_ZERO(sumROC1)) )
   {
      tempReal = 1.0;
   } else 
   {
      tempReal = fabs((periodROC/sumROC1));
   }
   tempReal = ((tempReal*constDiff)+constMax);
   tempReal *= tempReal;
   prevKAMA = (((inReal[today++]-prevKAMA)*tempReal)+prevKAMA);
   while( (today<=startIdx) )
   {
      tempReal = inReal[today];
      tempReal2 = inReal[trailingIdx++];
      periodROC = (tempReal-tempReal2);
      sumROC1 -= fabs((trailingValue-tempReal2));
      sumROC1 += fabs((tempReal-inReal[(today-1)]));
      trailingValue = tempReal2;
      if( ((sumROC1<=periodROC)||TA_IS_ZERO(sumROC1)) )
      {
         tempReal = 1.0;
      } else 
      {
         tempReal = fabs((periodROC/sumROC1));
      }
      tempReal = ((tempReal*constDiff)+constMax);
      tempReal *= tempReal;
      prevKAMA = (((inReal[today++]-prevKAMA)*tempReal)+prevKAMA);
   }
   outReal[0] = prevKAMA;
   outIdx = 1;
   *outBegIdx= (today-1);
   while( (today<=endIdx) )
   {
      tempReal = inReal[today];
      tempReal2 = inReal[trailingIdx++];
      periodROC = (tempReal-tempReal2);
      sumROC1 -= fabs((trailingValue-tempReal2));
      sumROC1 += fabs((tempReal-inReal[(today-1)]));
      trailingValue = tempReal2;
      if( ((sumROC1<=periodROC)||TA_IS_ZERO(sumROC1)) )
      {
         tempReal = 1.0;
      } else 
      {
         tempReal = fabs((periodROC/sumROC1));
      }
      tempReal = ((tempReal*constDiff)+constMax);
      tempReal *= tempReal;
      prevKAMA = (((inReal[today++]-prevKAMA)*tempReal)+prevKAMA);
      outReal[outIdx++] = prevKAMA;
   }
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_KAMA_Unguarded( int    startIdx,
                                int    endIdx,
                                const float inReal[],
                                int optInTimePeriod,
                                int          *outBegIdx,
                                int          *outNBElement,
                                double        outReal[] )
{
   double constMax;
   double constDiff;
   double tempReal;
   double tempReal2;
   double sumROC1;
   double periodROC;
   double prevKAMA;
   int i;
   int today;
   int outIdx;
   int lookbackTotal;
   int trailingIdx;
   double trailingValue;

   constMax = (2.0/(30.0+1.0));
   constDiff = ((2.0/(2.0+1.0))-constMax);
   *outBegIdx= 0;
   *outNBElement= 0;
   lookbackTotal = (optInTimePeriod+TA_GLOBALS_UNSTABLE_PERIOD(TA_FUNC_UNST_KAMA,Kama));
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
   sumROC1 = 0.0;
   today = (startIdx-lookbackTotal);
   trailingIdx = today;
   i = optInTimePeriod;
   while( (i-->0) )
   {
      tempReal = inReal[today++];
      tempReal -= inReal[today];
      sumROC1 += fabs(tempReal);
   }
   prevKAMA = inReal[(today-1)];
   tempReal = inReal[today];
   tempReal2 = inReal[trailingIdx++];
   periodROC = (tempReal-tempReal2);
   trailingValue = tempReal2;
   if( ((sumROC1<=periodROC)||TA_IS_ZERO(sumROC1)) )
   {
      tempReal = 1.0;
   } else 
   {
      tempReal = fabs((periodROC/sumROC1));
   }
   tempReal = ((tempReal*constDiff)+constMax);
   tempReal *= tempReal;
   prevKAMA = (((inReal[today++]-prevKAMA)*tempReal)+prevKAMA);
   while( (today<=startIdx) )
   {
      tempReal = inReal[today];
      tempReal2 = inReal[trailingIdx++];
      periodROC = (tempReal-tempReal2);
      sumROC1 -= fabs((trailingValue-tempReal2));
      sumROC1 += fabs((tempReal-inReal[(today-1)]));
      trailingValue = tempReal2;
      if( ((sumROC1<=periodROC)||TA_IS_ZERO(sumROC1)) )
      {
         tempReal = 1.0;
      } else 
      {
         tempReal = fabs((periodROC/sumROC1));
      }
      tempReal = ((tempReal*constDiff)+constMax);
      tempReal *= tempReal;
      prevKAMA = (((inReal[today++]-prevKAMA)*tempReal)+prevKAMA);
   }
   outReal[0] = prevKAMA;
   outIdx = 1;
   *outBegIdx= (today-1);
   while( (today<=endIdx) )
   {
      tempReal = inReal[today];
      tempReal2 = inReal[trailingIdx++];
      periodROC = (tempReal-tempReal2);
      sumROC1 -= fabs((trailingValue-tempReal2));
      sumROC1 += fabs((tempReal-inReal[(today-1)]));
      trailingValue = tempReal2;
      if( ((sumROC1<=periodROC)||TA_IS_ZERO(sumROC1)) )
      {
         tempReal = 1.0;
      } else 
      {
         tempReal = fabs((periodROC/sumROC1));
      }
      tempReal = ((tempReal*constDiff)+constMax);
      tempReal *= tempReal;
      prevKAMA = (((inReal[today++]-prevKAMA)*tempReal)+prevKAMA);
      outReal[outIdx++] = prevKAMA;
   }
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

