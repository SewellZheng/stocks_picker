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

TA_LIB_API int TA_MINUS_DM_Lookback( int optInTimePeriod )
{
   if( (optInTimePeriod>1) )
   {
      return ((optInTimePeriod+TA_GLOBALS_UNSTABLE_PERIOD(TA_FUNC_UNST_MINUS_DM,Minus_dm))-1);
   } else 
   {
      return 1;
   }
}

TA_LIB_API TA_RetCode TA_MINUS_DM( int    startIdx,
                                   int    endIdx,
                                   const double inHigh[],
                                   const double inLow[],
                                   int optInTimePeriod,
                                   int          *outBegIdx,
                                   int          *outNBElement,
                                   double        outReal[] )
{
   int today;
   int lookbackTotal;
   int outIdx;
   double prevHigh;
   double prevLow;
   double tempReal;
   double prevMinusDM;
   double diffP;
   double diffM;
   int i;

   if( startIdx < 0 )
      return TA_OUT_OF_RANGE_START_INDEX;
   if( (endIdx < 0) || (endIdx < startIdx) )
      return TA_OUT_OF_RANGE_END_INDEX;

   if( !inHigh )
      return TA_BAD_PARAM;
   if( !inLow )
      return TA_BAD_PARAM;
   if( (int)optInTimePeriod == (int)0x80000000 )
      optInTimePeriod = 14;
   else if( (int)optInTimePeriod < 1 || (int)optInTimePeriod > 100000 )
      return TA_BAD_PARAM;
   if( !outReal )
      return TA_BAD_PARAM;

   if( (optInTimePeriod>1) )
   {
      lookbackTotal = ((optInTimePeriod+TA_GLOBALS_UNSTABLE_PERIOD(TA_FUNC_UNST_MINUS_DM,Minus_dm))-1);
   } else 
   {
      lookbackTotal = 1;
   }
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
   if( (optInTimePeriod<=1) )
   {
      *outBegIdx= startIdx;
      today = (startIdx-1);
      prevHigh = inHigh[today];
      prevLow = inLow[today];
      while( (today<endIdx) )
      {
         today += 1;
         tempReal = inHigh[today];
         diffP = (tempReal-prevHigh);
         prevHigh = tempReal;
         tempReal = inLow[today];
         diffM = (prevLow-tempReal);
         prevLow = tempReal;
         if( ((diffM>0)&&(diffP<diffM)) )
         {
            outReal[outIdx++] = diffM;
         } else 
         {
            outReal[outIdx++] = 0;
         }
      }
      *outNBElement= outIdx;
      return TA_SUCCESS;
   }
   *outBegIdx= startIdx;
   prevMinusDM = 0.0;
   today = (startIdx-lookbackTotal);
   prevHigh = inHigh[today];
   prevLow = inLow[today];
   i = (optInTimePeriod-1);
   while( (i-->0) )
   {
      today += 1;
      tempReal = inHigh[today];
      diffP = (tempReal-prevHigh);
      prevHigh = tempReal;
      tempReal = inLow[today];
      diffM = (prevLow-tempReal);
      prevLow = tempReal;
      if( ((diffM>0)&&(diffP<diffM)) )
      {
         prevMinusDM += diffM;
      }
   }
   i = TA_GLOBALS_UNSTABLE_PERIOD(TA_FUNC_UNST_MINUS_DM,Minus_dm);
   while( (i--!=0) )
   {
      today += 1;
      tempReal = inHigh[today];
      diffP = (tempReal-prevHigh);
      prevHigh = tempReal;
      tempReal = inLow[today];
      diffM = (prevLow-tempReal);
      prevLow = tempReal;
      if( ((diffM>0)&&(diffP<diffM)) )
      {
         prevMinusDM = ((prevMinusDM-(prevMinusDM/optInTimePeriod))+diffM);
      } else 
      {
         prevMinusDM = (prevMinusDM-(prevMinusDM/optInTimePeriod));
      }
   }
   outReal[0] = prevMinusDM;
   outIdx = 1;
   while( (today<endIdx) )
   {
      today += 1;
      tempReal = inHigh[today];
      diffP = (tempReal-prevHigh);
      prevHigh = tempReal;
      tempReal = inLow[today];
      diffM = (prevLow-tempReal);
      prevLow = tempReal;
      if( ((diffM>0)&&(diffP<diffM)) )
      {
         prevMinusDM = ((prevMinusDM-(prevMinusDM/optInTimePeriod))+diffM);
      } else 
      {
         prevMinusDM = (prevMinusDM-(prevMinusDM/optInTimePeriod));
      }
      outReal[outIdx++] = prevMinusDM;
   }
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_LIB_API TA_RetCode TA_MINUS_DM_Unguarded( int    startIdx,
                                             int    endIdx,
                                             const double inHigh[],
                                             const double inLow[],
                                             int optInTimePeriod,
                                             int          *outBegIdx,
                                             int          *outNBElement,
                                             double        outReal[] )
{
   int today;
   int lookbackTotal;
   int outIdx;
   double prevHigh;
   double prevLow;
   double tempReal;
   double prevMinusDM;
   double diffP;
   double diffM;
   int i;

   if( (optInTimePeriod>1) )
   {
      lookbackTotal = ((optInTimePeriod+TA_GLOBALS_UNSTABLE_PERIOD(TA_FUNC_UNST_MINUS_DM,Minus_dm))-1);
   } else 
   {
      lookbackTotal = 1;
   }
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
   if( (optInTimePeriod<=1) )
   {
      *outBegIdx= startIdx;
      today = (startIdx-1);
      prevHigh = inHigh[today];
      prevLow = inLow[today];
      while( (today<endIdx) )
      {
         today += 1;
         tempReal = inHigh[today];
         diffP = (tempReal-prevHigh);
         prevHigh = tempReal;
         tempReal = inLow[today];
         diffM = (prevLow-tempReal);
         prevLow = tempReal;
         if( ((diffM>0)&&(diffP<diffM)) )
         {
            outReal[outIdx++] = diffM;
         } else 
         {
            outReal[outIdx++] = 0;
         }
      }
      *outNBElement= outIdx;
      return TA_SUCCESS;
   }
   *outBegIdx= startIdx;
   prevMinusDM = 0.0;
   today = (startIdx-lookbackTotal);
   prevHigh = inHigh[today];
   prevLow = inLow[today];
   i = (optInTimePeriod-1);
   while( (i-->0) )
   {
      today += 1;
      tempReal = inHigh[today];
      diffP = (tempReal-prevHigh);
      prevHigh = tempReal;
      tempReal = inLow[today];
      diffM = (prevLow-tempReal);
      prevLow = tempReal;
      if( ((diffM>0)&&(diffP<diffM)) )
      {
         prevMinusDM += diffM;
      }
   }
   i = TA_GLOBALS_UNSTABLE_PERIOD(TA_FUNC_UNST_MINUS_DM,Minus_dm);
   while( (i--!=0) )
   {
      today += 1;
      tempReal = inHigh[today];
      diffP = (tempReal-prevHigh);
      prevHigh = tempReal;
      tempReal = inLow[today];
      diffM = (prevLow-tempReal);
      prevLow = tempReal;
      if( ((diffM>0)&&(diffP<diffM)) )
      {
         prevMinusDM = ((prevMinusDM-(prevMinusDM/optInTimePeriod))+diffM);
      } else 
      {
         prevMinusDM = (prevMinusDM-(prevMinusDM/optInTimePeriod));
      }
   }
   outReal[0] = prevMinusDM;
   outIdx = 1;
   while( (today<endIdx) )
   {
      today += 1;
      tempReal = inHigh[today];
      diffP = (tempReal-prevHigh);
      prevHigh = tempReal;
      tempReal = inLow[today];
      diffM = (prevLow-tempReal);
      prevLow = tempReal;
      if( ((diffM>0)&&(diffP<diffM)) )
      {
         prevMinusDM = ((prevMinusDM-(prevMinusDM/optInTimePeriod))+diffM);
      } else 
      {
         prevMinusDM = (prevMinusDM-(prevMinusDM/optInTimePeriod));
      }
      outReal[outIdx++] = prevMinusDM;
   }
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_MINUS_DM( int    startIdx,
                          int    endIdx,
                          const float inHigh[],
                          const float inLow[],
                          int optInTimePeriod,
                          int          *outBegIdx,
                          int          *outNBElement,
                          double        outReal[] )
{
   int today;
   int lookbackTotal;
   int outIdx;
   double prevHigh;
   double prevLow;
   double tempReal;
   double prevMinusDM;
   double diffP;
   double diffM;
   int i;

   if( startIdx < 0 )
      return TA_OUT_OF_RANGE_START_INDEX;
   if( (endIdx < 0) || (endIdx < startIdx) )
      return TA_OUT_OF_RANGE_END_INDEX;

   if( !inHigh )
      return TA_BAD_PARAM;
   if( !inLow )
      return TA_BAD_PARAM;
   if( (int)optInTimePeriod == (int)0x80000000 )
      optInTimePeriod = 14;
   else if( (int)optInTimePeriod < 1 || (int)optInTimePeriod > 100000 )
      return TA_BAD_PARAM;
   if( !outReal )
      return TA_BAD_PARAM;

   if( (optInTimePeriod>1) )
   {
      lookbackTotal = ((optInTimePeriod+TA_GLOBALS_UNSTABLE_PERIOD(TA_FUNC_UNST_MINUS_DM,Minus_dm))-1);
   } else 
   {
      lookbackTotal = 1;
   }
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
   if( (optInTimePeriod<=1) )
   {
      *outBegIdx= startIdx;
      today = (startIdx-1);
      prevHigh = inHigh[today];
      prevLow = inLow[today];
      while( (today<endIdx) )
      {
         today += 1;
         tempReal = inHigh[today];
         diffP = (tempReal-prevHigh);
         prevHigh = tempReal;
         tempReal = inLow[today];
         diffM = (prevLow-tempReal);
         prevLow = tempReal;
         if( ((diffM>0)&&(diffP<diffM)) )
         {
            outReal[outIdx++] = diffM;
         } else 
         {
            outReal[outIdx++] = 0;
         }
      }
      *outNBElement= outIdx;
      return TA_SUCCESS;
   }
   *outBegIdx= startIdx;
   prevMinusDM = 0.0;
   today = (startIdx-lookbackTotal);
   prevHigh = inHigh[today];
   prevLow = inLow[today];
   i = (optInTimePeriod-1);
   while( (i-->0) )
   {
      today += 1;
      tempReal = inHigh[today];
      diffP = (tempReal-prevHigh);
      prevHigh = tempReal;
      tempReal = inLow[today];
      diffM = (prevLow-tempReal);
      prevLow = tempReal;
      if( ((diffM>0)&&(diffP<diffM)) )
      {
         prevMinusDM += diffM;
      }
   }
   i = TA_GLOBALS_UNSTABLE_PERIOD(TA_FUNC_UNST_MINUS_DM,Minus_dm);
   while( (i--!=0) )
   {
      today += 1;
      tempReal = inHigh[today];
      diffP = (tempReal-prevHigh);
      prevHigh = tempReal;
      tempReal = inLow[today];
      diffM = (prevLow-tempReal);
      prevLow = tempReal;
      if( ((diffM>0)&&(diffP<diffM)) )
      {
         prevMinusDM = ((prevMinusDM-(prevMinusDM/optInTimePeriod))+diffM);
      } else 
      {
         prevMinusDM = (prevMinusDM-(prevMinusDM/optInTimePeriod));
      }
   }
   outReal[0] = prevMinusDM;
   outIdx = 1;
   while( (today<endIdx) )
   {
      today += 1;
      tempReal = inHigh[today];
      diffP = (tempReal-prevHigh);
      prevHigh = tempReal;
      tempReal = inLow[today];
      diffM = (prevLow-tempReal);
      prevLow = tempReal;
      if( ((diffM>0)&&(diffP<diffM)) )
      {
         prevMinusDM = ((prevMinusDM-(prevMinusDM/optInTimePeriod))+diffM);
      } else 
      {
         prevMinusDM = (prevMinusDM-(prevMinusDM/optInTimePeriod));
      }
      outReal[outIdx++] = prevMinusDM;
   }
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_MINUS_DM_Unguarded( int    startIdx,
                                    int    endIdx,
                                    const float inHigh[],
                                    const float inLow[],
                                    int optInTimePeriod,
                                    int          *outBegIdx,
                                    int          *outNBElement,
                                    double        outReal[] )
{
   int today;
   int lookbackTotal;
   int outIdx;
   double prevHigh;
   double prevLow;
   double tempReal;
   double prevMinusDM;
   double diffP;
   double diffM;
   int i;

   if( (optInTimePeriod>1) )
   {
      lookbackTotal = ((optInTimePeriod+TA_GLOBALS_UNSTABLE_PERIOD(TA_FUNC_UNST_MINUS_DM,Minus_dm))-1);
   } else 
   {
      lookbackTotal = 1;
   }
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
   if( (optInTimePeriod<=1) )
   {
      *outBegIdx= startIdx;
      today = (startIdx-1);
      prevHigh = inHigh[today];
      prevLow = inLow[today];
      while( (today<endIdx) )
      {
         today += 1;
         tempReal = inHigh[today];
         diffP = (tempReal-prevHigh);
         prevHigh = tempReal;
         tempReal = inLow[today];
         diffM = (prevLow-tempReal);
         prevLow = tempReal;
         if( ((diffM>0)&&(diffP<diffM)) )
         {
            outReal[outIdx++] = diffM;
         } else 
         {
            outReal[outIdx++] = 0;
         }
      }
      *outNBElement= outIdx;
      return TA_SUCCESS;
   }
   *outBegIdx= startIdx;
   prevMinusDM = 0.0;
   today = (startIdx-lookbackTotal);
   prevHigh = inHigh[today];
   prevLow = inLow[today];
   i = (optInTimePeriod-1);
   while( (i-->0) )
   {
      today += 1;
      tempReal = inHigh[today];
      diffP = (tempReal-prevHigh);
      prevHigh = tempReal;
      tempReal = inLow[today];
      diffM = (prevLow-tempReal);
      prevLow = tempReal;
      if( ((diffM>0)&&(diffP<diffM)) )
      {
         prevMinusDM += diffM;
      }
   }
   i = TA_GLOBALS_UNSTABLE_PERIOD(TA_FUNC_UNST_MINUS_DM,Minus_dm);
   while( (i--!=0) )
   {
      today += 1;
      tempReal = inHigh[today];
      diffP = (tempReal-prevHigh);
      prevHigh = tempReal;
      tempReal = inLow[today];
      diffM = (prevLow-tempReal);
      prevLow = tempReal;
      if( ((diffM>0)&&(diffP<diffM)) )
      {
         prevMinusDM = ((prevMinusDM-(prevMinusDM/optInTimePeriod))+diffM);
      } else 
      {
         prevMinusDM = (prevMinusDM-(prevMinusDM/optInTimePeriod));
      }
   }
   outReal[0] = prevMinusDM;
   outIdx = 1;
   while( (today<endIdx) )
   {
      today += 1;
      tempReal = inHigh[today];
      diffP = (tempReal-prevHigh);
      prevHigh = tempReal;
      tempReal = inLow[today];
      diffM = (prevLow-tempReal);
      prevLow = tempReal;
      if( ((diffM>0)&&(diffP<diffM)) )
      {
         prevMinusDM = ((prevMinusDM-(prevMinusDM/optInTimePeriod))+diffM);
      } else 
      {
         prevMinusDM = (prevMinusDM-(prevMinusDM/optInTimePeriod));
      }
      outReal[outIdx++] = prevMinusDM;
   }
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

