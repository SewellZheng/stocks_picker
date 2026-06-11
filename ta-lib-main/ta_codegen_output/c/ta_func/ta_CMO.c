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

TA_LIB_API int TA_CMO_Lookback( int optInTimePeriod )
{
   int retValue;
   retValue = (optInTimePeriod+TA_GLOBALS_UNSTABLE_PERIOD(TA_FUNC_UNST_CMO,Cmo));
   if( (TA_GLOBALS_COMPATIBILITY==ENUM_VALUE(Compatibility,TA_COMPATIBILITY_METASTOCK,Metastock)) )
   {
      retValue -= 1;
   }
   return retValue;
}

TA_LIB_API TA_RetCode TA_CMO( int    startIdx,
                              int    endIdx,
                              const double inReal[],
                              int optInTimePeriod,
                              int          *outBegIdx,
                              int          *outNBElement,
                              double        outReal[] )
{
   int outIdx;
   int today;
   int lookbackTotal;
   int unstablePeriod;
   int i;
   double prevGain;
   double prevLoss;
   double prevValue;
   double savePrevValue;
   double tempValue1;
   double tempValue2;
   double tempValue3;
   double tempValue4;

   if( startIdx < 0 )
      return TA_OUT_OF_RANGE_START_INDEX;
   if( (endIdx < 0) || (endIdx < startIdx) )
      return TA_OUT_OF_RANGE_END_INDEX;

   if( !inReal )
      return TA_BAD_PARAM;
   if( (int)optInTimePeriod == (int)0x80000000 )
      optInTimePeriod = 14;
   else if( (int)optInTimePeriod < 2 || (int)optInTimePeriod > 100000 )
      return TA_BAD_PARAM;
   if( !outReal )
      return TA_BAD_PARAM;

   *outBegIdx= 0;
   *outNBElement= 0;
   lookbackTotal = TA_CMO_Lookback(optInTimePeriod);
   if( (startIdx<lookbackTotal) )
   {
      startIdx = lookbackTotal;
   }
   if( (startIdx>endIdx) )
   {
      return TA_SUCCESS;
   }
   outIdx = 0;
   if( (optInTimePeriod==1) )
   {
      *outBegIdx= startIdx;
      i = ((endIdx-startIdx)+1);
      *outNBElement= i;
      memcpy(&outReal[0],&inReal[startIdx],(i*sizeof(double)));
      return TA_SUCCESS;
   }
   today = (startIdx-lookbackTotal);
   prevValue = inReal[today];
   unstablePeriod = TA_GLOBALS_UNSTABLE_PERIOD(TA_FUNC_UNST_CMO,Cmo);
   if( ((unstablePeriod==0)&&(TA_GLOBALS_COMPATIBILITY==ENUM_VALUE(Compatibility,TA_COMPATIBILITY_METASTOCK,Metastock))) )
   {
      savePrevValue = prevValue;
      prevGain = 0.0;
      prevLoss = 0.0;
      for( i = optInTimePeriod; (i>0); i -= 1 )
      {
         tempValue1 = inReal[today++];
         tempValue2 = (tempValue1-prevValue);
         prevValue = tempValue1;
         if( (tempValue2<0) )
         {
            prevLoss -= tempValue2;
         } else 
         {
            prevGain += tempValue2;
         }
      }
      tempValue1 = (prevLoss/optInTimePeriod);
      tempValue2 = (prevGain/optInTimePeriod);
      tempValue3 = (tempValue2-tempValue1);
      tempValue4 = (tempValue1+tempValue2);
      if( !(TA_IS_ZERO(tempValue4)) )
      {
         outReal[outIdx++] = (100*(tempValue3/tempValue4));
      } else 
      {
         outReal[outIdx++] = 0.0;
      }
      if( (today>endIdx) )
      {
         *outBegIdx= startIdx;
         *outNBElement= outIdx;
         return TA_SUCCESS;
      }
      today -= optInTimePeriod;
      prevValue = savePrevValue;
   }
   prevGain = 0.0;
   prevLoss = 0.0;
   today += 1;
   for( i = optInTimePeriod; (i>0); i -= 1 )
   {
      tempValue1 = inReal[today++];
      tempValue2 = (tempValue1-prevValue);
      prevValue = tempValue1;
      if( (tempValue2<0) )
      {
         prevLoss -= tempValue2;
      } else 
      {
         prevGain += tempValue2;
      }
   }
   prevLoss /= optInTimePeriod;
   prevGain /= optInTimePeriod;
   if( (today>startIdx) )
   {
      tempValue1 = (prevGain+prevLoss);
      if( !(TA_IS_ZERO(tempValue1)) )
      {
         outReal[outIdx++] = (100.0*((prevGain-prevLoss)/tempValue1));
      } else 
      {
         outReal[outIdx++] = 0.0;
      }
   } else 
   {
      while( (today<startIdx) )
      {
         tempValue1 = inReal[today];
         tempValue2 = (tempValue1-prevValue);
         prevValue = tempValue1;
         prevLoss *= (optInTimePeriod-1);
         prevGain *= (optInTimePeriod-1);
         if( (tempValue2<0) )
         {
            prevLoss -= tempValue2;
         } else 
         {
            prevGain += tempValue2;
         }
         prevLoss /= optInTimePeriod;
         prevGain /= optInTimePeriod;
         today += 1;
      }
   }
   while( (today<=endIdx) )
   {
      tempValue1 = inReal[today++];
      tempValue2 = (tempValue1-prevValue);
      prevValue = tempValue1;
      prevLoss *= (optInTimePeriod-1);
      prevGain *= (optInTimePeriod-1);
      if( (tempValue2<0) )
      {
         prevLoss -= tempValue2;
      } else 
      {
         prevGain += tempValue2;
      }
      prevLoss /= optInTimePeriod;
      prevGain /= optInTimePeriod;
      tempValue1 = (prevGain+prevLoss);
      if( !(TA_IS_ZERO(tempValue1)) )
      {
         outReal[outIdx++] = (100.0*((prevGain-prevLoss)/tempValue1));
      } else 
      {
         outReal[outIdx++] = 0.0;
      }
   }
   *outBegIdx= startIdx;
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_LIB_API TA_RetCode TA_CMO_Unguarded( int    startIdx,
                                        int    endIdx,
                                        const double inReal[],
                                        int optInTimePeriod,
                                        int          *outBegIdx,
                                        int          *outNBElement,
                                        double        outReal[] )
{
   int outIdx;
   int today;
   int lookbackTotal;
   int unstablePeriod;
   int i;
   double prevGain;
   double prevLoss;
   double prevValue;
   double savePrevValue;
   double tempValue1;
   double tempValue2;
   double tempValue3;
   double tempValue4;

   *outBegIdx= 0;
   *outNBElement= 0;
   lookbackTotal = TA_CMO_Lookback(optInTimePeriod);
   if( (startIdx<lookbackTotal) )
   {
      startIdx = lookbackTotal;
   }
   if( (startIdx>endIdx) )
   {
      return TA_SUCCESS;
   }
   outIdx = 0;
   if( (optInTimePeriod==1) )
   {
      *outBegIdx= startIdx;
      i = ((endIdx-startIdx)+1);
      *outNBElement= i;
      memcpy(&outReal[0],&inReal[startIdx],(i*sizeof(double)));
      return TA_SUCCESS;
   }
   today = (startIdx-lookbackTotal);
   prevValue = inReal[today];
   unstablePeriod = TA_GLOBALS_UNSTABLE_PERIOD(TA_FUNC_UNST_CMO,Cmo);
   if( ((unstablePeriod==0)&&(TA_GLOBALS_COMPATIBILITY==ENUM_VALUE(Compatibility,TA_COMPATIBILITY_METASTOCK,Metastock))) )
   {
      savePrevValue = prevValue;
      prevGain = 0.0;
      prevLoss = 0.0;
      for( i = optInTimePeriod; (i>0); i -= 1 )
      {
         tempValue1 = inReal[today++];
         tempValue2 = (tempValue1-prevValue);
         prevValue = tempValue1;
         if( (tempValue2<0) )
         {
            prevLoss -= tempValue2;
         } else 
         {
            prevGain += tempValue2;
         }
      }
      tempValue1 = (prevLoss/optInTimePeriod);
      tempValue2 = (prevGain/optInTimePeriod);
      tempValue3 = (tempValue2-tempValue1);
      tempValue4 = (tempValue1+tempValue2);
      if( !(TA_IS_ZERO(tempValue4)) )
      {
         outReal[outIdx++] = (100*(tempValue3/tempValue4));
      } else 
      {
         outReal[outIdx++] = 0.0;
      }
      if( (today>endIdx) )
      {
         *outBegIdx= startIdx;
         *outNBElement= outIdx;
         return TA_SUCCESS;
      }
      today -= optInTimePeriod;
      prevValue = savePrevValue;
   }
   prevGain = 0.0;
   prevLoss = 0.0;
   today += 1;
   for( i = optInTimePeriod; (i>0); i -= 1 )
   {
      tempValue1 = inReal[today++];
      tempValue2 = (tempValue1-prevValue);
      prevValue = tempValue1;
      if( (tempValue2<0) )
      {
         prevLoss -= tempValue2;
      } else 
      {
         prevGain += tempValue2;
      }
   }
   prevLoss /= optInTimePeriod;
   prevGain /= optInTimePeriod;
   if( (today>startIdx) )
   {
      tempValue1 = (prevGain+prevLoss);
      if( !(TA_IS_ZERO(tempValue1)) )
      {
         outReal[outIdx++] = (100.0*((prevGain-prevLoss)/tempValue1));
      } else 
      {
         outReal[outIdx++] = 0.0;
      }
   } else 
   {
      while( (today<startIdx) )
      {
         tempValue1 = inReal[today];
         tempValue2 = (tempValue1-prevValue);
         prevValue = tempValue1;
         prevLoss *= (optInTimePeriod-1);
         prevGain *= (optInTimePeriod-1);
         if( (tempValue2<0) )
         {
            prevLoss -= tempValue2;
         } else 
         {
            prevGain += tempValue2;
         }
         prevLoss /= optInTimePeriod;
         prevGain /= optInTimePeriod;
         today += 1;
      }
   }
   while( (today<=endIdx) )
   {
      tempValue1 = inReal[today++];
      tempValue2 = (tempValue1-prevValue);
      prevValue = tempValue1;
      prevLoss *= (optInTimePeriod-1);
      prevGain *= (optInTimePeriod-1);
      if( (tempValue2<0) )
      {
         prevLoss -= tempValue2;
      } else 
      {
         prevGain += tempValue2;
      }
      prevLoss /= optInTimePeriod;
      prevGain /= optInTimePeriod;
      tempValue1 = (prevGain+prevLoss);
      if( !(TA_IS_ZERO(tempValue1)) )
      {
         outReal[outIdx++] = (100.0*((prevGain-prevLoss)/tempValue1));
      } else 
      {
         outReal[outIdx++] = 0.0;
      }
   }
   *outBegIdx= startIdx;
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_CMO( int    startIdx,
                     int    endIdx,
                     const float inReal[],
                     int optInTimePeriod,
                     int          *outBegIdx,
                     int          *outNBElement,
                     double        outReal[] )
{
   int outIdx;
   int today;
   int lookbackTotal;
   int unstablePeriod;
   int i;
   double prevGain;
   double prevLoss;
   double prevValue;
   double savePrevValue;
   double tempValue1;
   double tempValue2;
   double tempValue3;
   double tempValue4;

   if( startIdx < 0 )
      return TA_OUT_OF_RANGE_START_INDEX;
   if( (endIdx < 0) || (endIdx < startIdx) )
      return TA_OUT_OF_RANGE_END_INDEX;

   if( !inReal )
      return TA_BAD_PARAM;
   if( (int)optInTimePeriod == (int)0x80000000 )
      optInTimePeriod = 14;
   else if( (int)optInTimePeriod < 2 || (int)optInTimePeriod > 100000 )
      return TA_BAD_PARAM;
   if( !outReal )
      return TA_BAD_PARAM;

   *outBegIdx= 0;
   *outNBElement= 0;
   lookbackTotal = TA_CMO_Lookback(optInTimePeriod);
   if( (startIdx<lookbackTotal) )
   {
      startIdx = lookbackTotal;
   }
   if( (startIdx>endIdx) )
   {
      return TA_SUCCESS;
   }
   outIdx = 0;
   if( (optInTimePeriod==1) )
   {
      *outBegIdx= startIdx;
      i = ((endIdx-startIdx)+1);
      *outNBElement= i;
      memcpy(&outReal[0],&inReal[startIdx],(i*sizeof(double)));
      return TA_SUCCESS;
   }
   today = (startIdx-lookbackTotal);
   prevValue = inReal[today];
   unstablePeriod = TA_GLOBALS_UNSTABLE_PERIOD(TA_FUNC_UNST_CMO,Cmo);
   if( ((unstablePeriod==0)&&(TA_GLOBALS_COMPATIBILITY==ENUM_VALUE(Compatibility,TA_COMPATIBILITY_METASTOCK,Metastock))) )
   {
      savePrevValue = prevValue;
      prevGain = 0.0;
      prevLoss = 0.0;
      for( i = optInTimePeriod; (i>0); i -= 1 )
      {
         tempValue1 = inReal[today++];
         tempValue2 = (tempValue1-prevValue);
         prevValue = tempValue1;
         if( (tempValue2<0) )
         {
            prevLoss -= tempValue2;
         } else 
         {
            prevGain += tempValue2;
         }
      }
      tempValue1 = (prevLoss/optInTimePeriod);
      tempValue2 = (prevGain/optInTimePeriod);
      tempValue3 = (tempValue2-tempValue1);
      tempValue4 = (tempValue1+tempValue2);
      if( !(TA_IS_ZERO(tempValue4)) )
      {
         outReal[outIdx++] = (100*(tempValue3/tempValue4));
      } else 
      {
         outReal[outIdx++] = 0.0;
      }
      if( (today>endIdx) )
      {
         *outBegIdx= startIdx;
         *outNBElement= outIdx;
         return TA_SUCCESS;
      }
      today -= optInTimePeriod;
      prevValue = savePrevValue;
   }
   prevGain = 0.0;
   prevLoss = 0.0;
   today += 1;
   for( i = optInTimePeriod; (i>0); i -= 1 )
   {
      tempValue1 = inReal[today++];
      tempValue2 = (tempValue1-prevValue);
      prevValue = tempValue1;
      if( (tempValue2<0) )
      {
         prevLoss -= tempValue2;
      } else 
      {
         prevGain += tempValue2;
      }
   }
   prevLoss /= optInTimePeriod;
   prevGain /= optInTimePeriod;
   if( (today>startIdx) )
   {
      tempValue1 = (prevGain+prevLoss);
      if( !(TA_IS_ZERO(tempValue1)) )
      {
         outReal[outIdx++] = (100.0*((prevGain-prevLoss)/tempValue1));
      } else 
      {
         outReal[outIdx++] = 0.0;
      }
   } else 
   {
      while( (today<startIdx) )
      {
         tempValue1 = inReal[today];
         tempValue2 = (tempValue1-prevValue);
         prevValue = tempValue1;
         prevLoss *= (optInTimePeriod-1);
         prevGain *= (optInTimePeriod-1);
         if( (tempValue2<0) )
         {
            prevLoss -= tempValue2;
         } else 
         {
            prevGain += tempValue2;
         }
         prevLoss /= optInTimePeriod;
         prevGain /= optInTimePeriod;
         today += 1;
      }
   }
   while( (today<=endIdx) )
   {
      tempValue1 = inReal[today++];
      tempValue2 = (tempValue1-prevValue);
      prevValue = tempValue1;
      prevLoss *= (optInTimePeriod-1);
      prevGain *= (optInTimePeriod-1);
      if( (tempValue2<0) )
      {
         prevLoss -= tempValue2;
      } else 
      {
         prevGain += tempValue2;
      }
      prevLoss /= optInTimePeriod;
      prevGain /= optInTimePeriod;
      tempValue1 = (prevGain+prevLoss);
      if( !(TA_IS_ZERO(tempValue1)) )
      {
         outReal[outIdx++] = (100.0*((prevGain-prevLoss)/tempValue1));
      } else 
      {
         outReal[outIdx++] = 0.0;
      }
   }
   *outBegIdx= startIdx;
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_CMO_Unguarded( int    startIdx,
                               int    endIdx,
                               const float inReal[],
                               int optInTimePeriod,
                               int          *outBegIdx,
                               int          *outNBElement,
                               double        outReal[] )
{
   int outIdx;
   int today;
   int lookbackTotal;
   int unstablePeriod;
   int i;
   double prevGain;
   double prevLoss;
   double prevValue;
   double savePrevValue;
   double tempValue1;
   double tempValue2;
   double tempValue3;
   double tempValue4;

   *outBegIdx= 0;
   *outNBElement= 0;
   lookbackTotal = TA_CMO_Lookback(optInTimePeriod);
   if( (startIdx<lookbackTotal) )
   {
      startIdx = lookbackTotal;
   }
   if( (startIdx>endIdx) )
   {
      return TA_SUCCESS;
   }
   outIdx = 0;
   if( (optInTimePeriod==1) )
   {
      *outBegIdx= startIdx;
      i = ((endIdx-startIdx)+1);
      *outNBElement= i;
      memcpy(&outReal[0],&inReal[startIdx],(i*sizeof(double)));
      return TA_SUCCESS;
   }
   today = (startIdx-lookbackTotal);
   prevValue = inReal[today];
   unstablePeriod = TA_GLOBALS_UNSTABLE_PERIOD(TA_FUNC_UNST_CMO,Cmo);
   if( ((unstablePeriod==0)&&(TA_GLOBALS_COMPATIBILITY==ENUM_VALUE(Compatibility,TA_COMPATIBILITY_METASTOCK,Metastock))) )
   {
      savePrevValue = prevValue;
      prevGain = 0.0;
      prevLoss = 0.0;
      for( i = optInTimePeriod; (i>0); i -= 1 )
      {
         tempValue1 = inReal[today++];
         tempValue2 = (tempValue1-prevValue);
         prevValue = tempValue1;
         if( (tempValue2<0) )
         {
            prevLoss -= tempValue2;
         } else 
         {
            prevGain += tempValue2;
         }
      }
      tempValue1 = (prevLoss/optInTimePeriod);
      tempValue2 = (prevGain/optInTimePeriod);
      tempValue3 = (tempValue2-tempValue1);
      tempValue4 = (tempValue1+tempValue2);
      if( !(TA_IS_ZERO(tempValue4)) )
      {
         outReal[outIdx++] = (100*(tempValue3/tempValue4));
      } else 
      {
         outReal[outIdx++] = 0.0;
      }
      if( (today>endIdx) )
      {
         *outBegIdx= startIdx;
         *outNBElement= outIdx;
         return TA_SUCCESS;
      }
      today -= optInTimePeriod;
      prevValue = savePrevValue;
   }
   prevGain = 0.0;
   prevLoss = 0.0;
   today += 1;
   for( i = optInTimePeriod; (i>0); i -= 1 )
   {
      tempValue1 = inReal[today++];
      tempValue2 = (tempValue1-prevValue);
      prevValue = tempValue1;
      if( (tempValue2<0) )
      {
         prevLoss -= tempValue2;
      } else 
      {
         prevGain += tempValue2;
      }
   }
   prevLoss /= optInTimePeriod;
   prevGain /= optInTimePeriod;
   if( (today>startIdx) )
   {
      tempValue1 = (prevGain+prevLoss);
      if( !(TA_IS_ZERO(tempValue1)) )
      {
         outReal[outIdx++] = (100.0*((prevGain-prevLoss)/tempValue1));
      } else 
      {
         outReal[outIdx++] = 0.0;
      }
   } else 
   {
      while( (today<startIdx) )
      {
         tempValue1 = inReal[today];
         tempValue2 = (tempValue1-prevValue);
         prevValue = tempValue1;
         prevLoss *= (optInTimePeriod-1);
         prevGain *= (optInTimePeriod-1);
         if( (tempValue2<0) )
         {
            prevLoss -= tempValue2;
         } else 
         {
            prevGain += tempValue2;
         }
         prevLoss /= optInTimePeriod;
         prevGain /= optInTimePeriod;
         today += 1;
      }
   }
   while( (today<=endIdx) )
   {
      tempValue1 = inReal[today++];
      tempValue2 = (tempValue1-prevValue);
      prevValue = tempValue1;
      prevLoss *= (optInTimePeriod-1);
      prevGain *= (optInTimePeriod-1);
      if( (tempValue2<0) )
      {
         prevLoss -= tempValue2;
      } else 
      {
         prevGain += tempValue2;
      }
      prevLoss /= optInTimePeriod;
      prevGain /= optInTimePeriod;
      tempValue1 = (prevGain+prevLoss);
      if( !(TA_IS_ZERO(tempValue1)) )
      {
         outReal[outIdx++] = (100.0*((prevGain-prevLoss)/tempValue1));
      } else 
      {
         outReal[outIdx++] = 0.0;
      }
   }
   *outBegIdx= startIdx;
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

