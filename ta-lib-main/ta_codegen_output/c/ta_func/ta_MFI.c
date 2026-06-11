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

TA_LIB_API int TA_MFI_Lookback( int optInTimePeriod )
{
   return (optInTimePeriod+TA_GLOBALS_UNSTABLE_PERIOD(TA_FUNC_UNST_MFI,Mfi));
}

TA_LIB_API TA_RetCode TA_MFI( int    startIdx,
                              int    endIdx,
                              const double inHigh[],
                              const double inLow[],
                              const double inClose[],
                              const double inVolume[],
                              int optInTimePeriod,
                              int          *outBegIdx,
                              int          *outNBElement,
                              double        outReal[] )
{
   double posSumMF;
   double negSumMF;
   double prevValue;
   double tempValue1;
   double tempValue2;
   int lookbackTotal;
   int outIdx;
   int i;
   int today;
   double mflow_positive[50];
   double mflow_negative[50];
   int mflow_Idx;

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
   if( !inVolume )
      return TA_BAD_PARAM;
   if( (int)optInTimePeriod == (int)0x80000000 )
      optInTimePeriod = 14;
   else if( (int)optInTimePeriod < 2 || (int)optInTimePeriod > 100000 )
      return TA_BAD_PARAM;
   if( !outReal )
      return TA_BAD_PARAM;

   mflow_Idx = 0;
   memset(mflow_positive,0,(optInTimePeriod*sizeof(double)));
   memset(mflow_negative,0,(optInTimePeriod*sizeof(double)));
   *outBegIdx= 0;
   *outNBElement= 0;
   lookbackTotal = (optInTimePeriod+TA_GLOBALS_UNSTABLE_PERIOD(TA_FUNC_UNST_MFI,Mfi));
   if( (startIdx<lookbackTotal) )
   {
      startIdx = lookbackTotal;
   }
   if( (startIdx>endIdx) )
   {
      return TA_SUCCESS;
   }
   outIdx = 0;
   today = (startIdx-lookbackTotal);
   prevValue = (((inHigh[today]+inLow[today])+inClose[today])/3.0);
   posSumMF = 0.0;
   negSumMF = 0.0;
   today += 1;
   for( i = optInTimePeriod; (i>0); i -= 1 )
   {
      tempValue1 = (((inHigh[today]+inLow[today])+inClose[today])/3.0);
      tempValue2 = (tempValue1-prevValue);
      prevValue = tempValue1;
      tempValue1 *= inVolume[today++];
      if( (tempValue2<0) )
      {
         mflow_negative[mflow_Idx] = tempValue1;
         negSumMF += tempValue1;
         mflow_positive[mflow_Idx] = 0.0;
      } else if( (tempValue2>0) )
      {
         mflow_positive[mflow_Idx] = tempValue1;
         posSumMF += tempValue1;
         mflow_negative[mflow_Idx] = 0.0;
      } else 
      {
         mflow_positive[mflow_Idx] = 0.0;
         mflow_negative[mflow_Idx] = 0.0;
      }
      mflow_Idx = ((mflow_Idx+1)%optInTimePeriod);
   }
   if( (today>startIdx) )
   {
      tempValue1 = (posSumMF+negSumMF);
      if( (tempValue1<1.0) )
      {
         outReal[outIdx++] = 0.0;
      } else 
      {
         outReal[outIdx++] = (100.0*(posSumMF/tempValue1));
      }
   } else 
   {
      while( (today<startIdx) )
      {
         posSumMF -= mflow_positive[mflow_Idx];
         negSumMF -= mflow_negative[mflow_Idx];
         tempValue1 = (((inHigh[today]+inLow[today])+inClose[today])/3.0);
         tempValue2 = (tempValue1-prevValue);
         prevValue = tempValue1;
         tempValue1 *= inVolume[today++];
         if( (tempValue2<0) )
         {
            mflow_negative[mflow_Idx] = tempValue1;
            negSumMF += tempValue1;
            mflow_positive[mflow_Idx] = 0.0;
         } else if( (tempValue2>0) )
         {
            mflow_positive[mflow_Idx] = tempValue1;
            posSumMF += tempValue1;
            mflow_negative[mflow_Idx] = 0.0;
         } else 
         {
            mflow_positive[mflow_Idx] = 0.0;
            mflow_negative[mflow_Idx] = 0.0;
         }
         mflow_Idx = ((mflow_Idx+1)%optInTimePeriod);
      }
   }
   while( (today<=endIdx) )
   {
      posSumMF -= mflow_positive[mflow_Idx];
      negSumMF -= mflow_negative[mflow_Idx];
      tempValue1 = (((inHigh[today]+inLow[today])+inClose[today])/3.0);
      tempValue2 = (tempValue1-prevValue);
      prevValue = tempValue1;
      tempValue1 *= inVolume[today++];
      if( (tempValue2<0) )
      {
         mflow_negative[mflow_Idx] = tempValue1;
         negSumMF += tempValue1;
         mflow_positive[mflow_Idx] = 0.0;
      } else if( (tempValue2>0) )
      {
         mflow_positive[mflow_Idx] = tempValue1;
         posSumMF += tempValue1;
         mflow_negative[mflow_Idx] = 0.0;
      } else 
      {
         mflow_positive[mflow_Idx] = 0.0;
         mflow_negative[mflow_Idx] = 0.0;
      }
      tempValue1 = (posSumMF+negSumMF);
      if( (tempValue1<1.0) )
      {
         outReal[outIdx++] = 0.0;
      } else 
      {
         outReal[outIdx++] = (100.0*(posSumMF/tempValue1));
      }
      mflow_Idx = ((mflow_Idx+1)%optInTimePeriod);
   }
   *outBegIdx= startIdx;
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_LIB_API TA_RetCode TA_MFI_Unguarded( int    startIdx,
                                        int    endIdx,
                                        const double inHigh[],
                                        const double inLow[],
                                        const double inClose[],
                                        const double inVolume[],
                                        int optInTimePeriod,
                                        int          *outBegIdx,
                                        int          *outNBElement,
                                        double        outReal[] )
{
   double posSumMF;
   double negSumMF;
   double prevValue;
   double tempValue1;
   double tempValue2;
   int lookbackTotal;
   int outIdx;
   int i;
   int today;
   double mflow_positive[50];
   double mflow_negative[50];
   int mflow_Idx;

   mflow_Idx = 0;
   memset(mflow_positive,0,(optInTimePeriod*sizeof(double)));
   memset(mflow_negative,0,(optInTimePeriod*sizeof(double)));
   *outBegIdx= 0;
   *outNBElement= 0;
   lookbackTotal = (optInTimePeriod+TA_GLOBALS_UNSTABLE_PERIOD(TA_FUNC_UNST_MFI,Mfi));
   if( (startIdx<lookbackTotal) )
   {
      startIdx = lookbackTotal;
   }
   if( (startIdx>endIdx) )
   {
      return TA_SUCCESS;
   }
   outIdx = 0;
   today = (startIdx-lookbackTotal);
   prevValue = (((inHigh[today]+inLow[today])+inClose[today])/3.0);
   posSumMF = 0.0;
   negSumMF = 0.0;
   today += 1;
   for( i = optInTimePeriod; (i>0); i -= 1 )
   {
      tempValue1 = (((inHigh[today]+inLow[today])+inClose[today])/3.0);
      tempValue2 = (tempValue1-prevValue);
      prevValue = tempValue1;
      tempValue1 *= inVolume[today++];
      if( (tempValue2<0) )
      {
         mflow_negative[mflow_Idx] = tempValue1;
         negSumMF += tempValue1;
         mflow_positive[mflow_Idx] = 0.0;
      } else if( (tempValue2>0) )
      {
         mflow_positive[mflow_Idx] = tempValue1;
         posSumMF += tempValue1;
         mflow_negative[mflow_Idx] = 0.0;
      } else 
      {
         mflow_positive[mflow_Idx] = 0.0;
         mflow_negative[mflow_Idx] = 0.0;
      }
      mflow_Idx = ((mflow_Idx+1)%optInTimePeriod);
   }
   if( (today>startIdx) )
   {
      tempValue1 = (posSumMF+negSumMF);
      if( (tempValue1<1.0) )
      {
         outReal[outIdx++] = 0.0;
      } else 
      {
         outReal[outIdx++] = (100.0*(posSumMF/tempValue1));
      }
   } else 
   {
      while( (today<startIdx) )
      {
         posSumMF -= mflow_positive[mflow_Idx];
         negSumMF -= mflow_negative[mflow_Idx];
         tempValue1 = (((inHigh[today]+inLow[today])+inClose[today])/3.0);
         tempValue2 = (tempValue1-prevValue);
         prevValue = tempValue1;
         tempValue1 *= inVolume[today++];
         if( (tempValue2<0) )
         {
            mflow_negative[mflow_Idx] = tempValue1;
            negSumMF += tempValue1;
            mflow_positive[mflow_Idx] = 0.0;
         } else if( (tempValue2>0) )
         {
            mflow_positive[mflow_Idx] = tempValue1;
            posSumMF += tempValue1;
            mflow_negative[mflow_Idx] = 0.0;
         } else 
         {
            mflow_positive[mflow_Idx] = 0.0;
            mflow_negative[mflow_Idx] = 0.0;
         }
         mflow_Idx = ((mflow_Idx+1)%optInTimePeriod);
      }
   }
   while( (today<=endIdx) )
   {
      posSumMF -= mflow_positive[mflow_Idx];
      negSumMF -= mflow_negative[mflow_Idx];
      tempValue1 = (((inHigh[today]+inLow[today])+inClose[today])/3.0);
      tempValue2 = (tempValue1-prevValue);
      prevValue = tempValue1;
      tempValue1 *= inVolume[today++];
      if( (tempValue2<0) )
      {
         mflow_negative[mflow_Idx] = tempValue1;
         negSumMF += tempValue1;
         mflow_positive[mflow_Idx] = 0.0;
      } else if( (tempValue2>0) )
      {
         mflow_positive[mflow_Idx] = tempValue1;
         posSumMF += tempValue1;
         mflow_negative[mflow_Idx] = 0.0;
      } else 
      {
         mflow_positive[mflow_Idx] = 0.0;
         mflow_negative[mflow_Idx] = 0.0;
      }
      tempValue1 = (posSumMF+negSumMF);
      if( (tempValue1<1.0) )
      {
         outReal[outIdx++] = 0.0;
      } else 
      {
         outReal[outIdx++] = (100.0*(posSumMF/tempValue1));
      }
      mflow_Idx = ((mflow_Idx+1)%optInTimePeriod);
   }
   *outBegIdx= startIdx;
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_MFI( int    startIdx,
                     int    endIdx,
                     const float inHigh[],
                     const float inLow[],
                     const float inClose[],
                     const float inVolume[],
                     int optInTimePeriod,
                     int          *outBegIdx,
                     int          *outNBElement,
                     double        outReal[] )
{
   double posSumMF;
   double negSumMF;
   double prevValue;
   double tempValue1;
   double tempValue2;
   int lookbackTotal;
   int outIdx;
   int i;
   int today;
   double mflow_positive[50];
   double mflow_negative[50];
   int mflow_Idx;

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
   if( !inVolume )
      return TA_BAD_PARAM;
   if( (int)optInTimePeriod == (int)0x80000000 )
      optInTimePeriod = 14;
   else if( (int)optInTimePeriod < 2 || (int)optInTimePeriod > 100000 )
      return TA_BAD_PARAM;
   if( !outReal )
      return TA_BAD_PARAM;

   mflow_Idx = 0;
   memset(mflow_positive,0,(optInTimePeriod*sizeof(double)));
   memset(mflow_negative,0,(optInTimePeriod*sizeof(double)));
   *outBegIdx= 0;
   *outNBElement= 0;
   lookbackTotal = (optInTimePeriod+TA_GLOBALS_UNSTABLE_PERIOD(TA_FUNC_UNST_MFI,Mfi));
   if( (startIdx<lookbackTotal) )
   {
      startIdx = lookbackTotal;
   }
   if( (startIdx>endIdx) )
   {
      return TA_SUCCESS;
   }
   outIdx = 0;
   today = (startIdx-lookbackTotal);
   prevValue = (((inHigh[today]+inLow[today])+inClose[today])/3.0);
   posSumMF = 0.0;
   negSumMF = 0.0;
   today += 1;
   for( i = optInTimePeriod; (i>0); i -= 1 )
   {
      tempValue1 = (((inHigh[today]+inLow[today])+inClose[today])/3.0);
      tempValue2 = (tempValue1-prevValue);
      prevValue = tempValue1;
      tempValue1 *= inVolume[today++];
      if( (tempValue2<0) )
      {
         mflow_negative[mflow_Idx] = tempValue1;
         negSumMF += tempValue1;
         mflow_positive[mflow_Idx] = 0.0;
      } else if( (tempValue2>0) )
      {
         mflow_positive[mflow_Idx] = tempValue1;
         posSumMF += tempValue1;
         mflow_negative[mflow_Idx] = 0.0;
      } else 
      {
         mflow_positive[mflow_Idx] = 0.0;
         mflow_negative[mflow_Idx] = 0.0;
      }
      mflow_Idx = ((mflow_Idx+1)%optInTimePeriod);
   }
   if( (today>startIdx) )
   {
      tempValue1 = (posSumMF+negSumMF);
      if( (tempValue1<1.0) )
      {
         outReal[outIdx++] = 0.0;
      } else 
      {
         outReal[outIdx++] = (100.0*(posSumMF/tempValue1));
      }
   } else 
   {
      while( (today<startIdx) )
      {
         posSumMF -= mflow_positive[mflow_Idx];
         negSumMF -= mflow_negative[mflow_Idx];
         tempValue1 = (((inHigh[today]+inLow[today])+inClose[today])/3.0);
         tempValue2 = (tempValue1-prevValue);
         prevValue = tempValue1;
         tempValue1 *= inVolume[today++];
         if( (tempValue2<0) )
         {
            mflow_negative[mflow_Idx] = tempValue1;
            negSumMF += tempValue1;
            mflow_positive[mflow_Idx] = 0.0;
         } else if( (tempValue2>0) )
         {
            mflow_positive[mflow_Idx] = tempValue1;
            posSumMF += tempValue1;
            mflow_negative[mflow_Idx] = 0.0;
         } else 
         {
            mflow_positive[mflow_Idx] = 0.0;
            mflow_negative[mflow_Idx] = 0.0;
         }
         mflow_Idx = ((mflow_Idx+1)%optInTimePeriod);
      }
   }
   while( (today<=endIdx) )
   {
      posSumMF -= mflow_positive[mflow_Idx];
      negSumMF -= mflow_negative[mflow_Idx];
      tempValue1 = (((inHigh[today]+inLow[today])+inClose[today])/3.0);
      tempValue2 = (tempValue1-prevValue);
      prevValue = tempValue1;
      tempValue1 *= inVolume[today++];
      if( (tempValue2<0) )
      {
         mflow_negative[mflow_Idx] = tempValue1;
         negSumMF += tempValue1;
         mflow_positive[mflow_Idx] = 0.0;
      } else if( (tempValue2>0) )
      {
         mflow_positive[mflow_Idx] = tempValue1;
         posSumMF += tempValue1;
         mflow_negative[mflow_Idx] = 0.0;
      } else 
      {
         mflow_positive[mflow_Idx] = 0.0;
         mflow_negative[mflow_Idx] = 0.0;
      }
      tempValue1 = (posSumMF+negSumMF);
      if( (tempValue1<1.0) )
      {
         outReal[outIdx++] = 0.0;
      } else 
      {
         outReal[outIdx++] = (100.0*(posSumMF/tempValue1));
      }
      mflow_Idx = ((mflow_Idx+1)%optInTimePeriod);
   }
   *outBegIdx= startIdx;
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_MFI_Unguarded( int    startIdx,
                               int    endIdx,
                               const float inHigh[],
                               const float inLow[],
                               const float inClose[],
                               const float inVolume[],
                               int optInTimePeriod,
                               int          *outBegIdx,
                               int          *outNBElement,
                               double        outReal[] )
{
   double posSumMF;
   double negSumMF;
   double prevValue;
   double tempValue1;
   double tempValue2;
   int lookbackTotal;
   int outIdx;
   int i;
   int today;
   double mflow_positive[50];
   double mflow_negative[50];
   int mflow_Idx;

   mflow_Idx = 0;
   memset(mflow_positive,0,(optInTimePeriod*sizeof(double)));
   memset(mflow_negative,0,(optInTimePeriod*sizeof(double)));
   *outBegIdx= 0;
   *outNBElement= 0;
   lookbackTotal = (optInTimePeriod+TA_GLOBALS_UNSTABLE_PERIOD(TA_FUNC_UNST_MFI,Mfi));
   if( (startIdx<lookbackTotal) )
   {
      startIdx = lookbackTotal;
   }
   if( (startIdx>endIdx) )
   {
      return TA_SUCCESS;
   }
   outIdx = 0;
   today = (startIdx-lookbackTotal);
   prevValue = (((inHigh[today]+inLow[today])+inClose[today])/3.0);
   posSumMF = 0.0;
   negSumMF = 0.0;
   today += 1;
   for( i = optInTimePeriod; (i>0); i -= 1 )
   {
      tempValue1 = (((inHigh[today]+inLow[today])+inClose[today])/3.0);
      tempValue2 = (tempValue1-prevValue);
      prevValue = tempValue1;
      tempValue1 *= inVolume[today++];
      if( (tempValue2<0) )
      {
         mflow_negative[mflow_Idx] = tempValue1;
         negSumMF += tempValue1;
         mflow_positive[mflow_Idx] = 0.0;
      } else if( (tempValue2>0) )
      {
         mflow_positive[mflow_Idx] = tempValue1;
         posSumMF += tempValue1;
         mflow_negative[mflow_Idx] = 0.0;
      } else 
      {
         mflow_positive[mflow_Idx] = 0.0;
         mflow_negative[mflow_Idx] = 0.0;
      }
      mflow_Idx = ((mflow_Idx+1)%optInTimePeriod);
   }
   if( (today>startIdx) )
   {
      tempValue1 = (posSumMF+negSumMF);
      if( (tempValue1<1.0) )
      {
         outReal[outIdx++] = 0.0;
      } else 
      {
         outReal[outIdx++] = (100.0*(posSumMF/tempValue1));
      }
   } else 
   {
      while( (today<startIdx) )
      {
         posSumMF -= mflow_positive[mflow_Idx];
         negSumMF -= mflow_negative[mflow_Idx];
         tempValue1 = (((inHigh[today]+inLow[today])+inClose[today])/3.0);
         tempValue2 = (tempValue1-prevValue);
         prevValue = tempValue1;
         tempValue1 *= inVolume[today++];
         if( (tempValue2<0) )
         {
            mflow_negative[mflow_Idx] = tempValue1;
            negSumMF += tempValue1;
            mflow_positive[mflow_Idx] = 0.0;
         } else if( (tempValue2>0) )
         {
            mflow_positive[mflow_Idx] = tempValue1;
            posSumMF += tempValue1;
            mflow_negative[mflow_Idx] = 0.0;
         } else 
         {
            mflow_positive[mflow_Idx] = 0.0;
            mflow_negative[mflow_Idx] = 0.0;
         }
         mflow_Idx = ((mflow_Idx+1)%optInTimePeriod);
      }
   }
   while( (today<=endIdx) )
   {
      posSumMF -= mflow_positive[mflow_Idx];
      negSumMF -= mflow_negative[mflow_Idx];
      tempValue1 = (((inHigh[today]+inLow[today])+inClose[today])/3.0);
      tempValue2 = (tempValue1-prevValue);
      prevValue = tempValue1;
      tempValue1 *= inVolume[today++];
      if( (tempValue2<0) )
      {
         mflow_negative[mflow_Idx] = tempValue1;
         negSumMF += tempValue1;
         mflow_positive[mflow_Idx] = 0.0;
      } else if( (tempValue2>0) )
      {
         mflow_positive[mflow_Idx] = tempValue1;
         posSumMF += tempValue1;
         mflow_negative[mflow_Idx] = 0.0;
      } else 
      {
         mflow_positive[mflow_Idx] = 0.0;
         mflow_negative[mflow_Idx] = 0.0;
      }
      tempValue1 = (posSumMF+negSumMF);
      if( (tempValue1<1.0) )
      {
         outReal[outIdx++] = 0.0;
      } else 
      {
         outReal[outIdx++] = (100.0*(posSumMF/tempValue1));
      }
      mflow_Idx = ((mflow_Idx+1)%optInTimePeriod);
   }
   *outBegIdx= startIdx;
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

