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

TA_LIB_API int TA_CDLHIKKAKE_Lookback( void )
{
   return 5;
}

TA_LIB_API TA_RetCode TA_CDLHIKKAKE( int    startIdx,
                                     int    endIdx,
                                     const double inOpen[],
                                     const double inHigh[],
                                     const double inLow[],
                                     const double inClose[],
                                     int          *outBegIdx,
                                     int          *outNBElement,
                                     int        outInteger[] )
{
   int i;
   int outIdx;
   int lookbackTotal;
   int patternIdx;
   int patternResult;

   if( startIdx < 0 )
      return TA_OUT_OF_RANGE_START_INDEX;
   if( (endIdx < 0) || (endIdx < startIdx) )
      return TA_OUT_OF_RANGE_END_INDEX;

   if( !inOpen )
      return TA_BAD_PARAM;
   if( !inHigh )
      return TA_BAD_PARAM;
   if( !inLow )
      return TA_BAD_PARAM;
   if( !inClose )
      return TA_BAD_PARAM;
   if( !outInteger )
      return TA_BAD_PARAM;

   lookbackTotal = TA_CDLHIKKAKE_Lookback();
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
   patternIdx = 0;
   patternResult = 0;
   i = (startIdx-3);
   while( (i<startIdx) )
   {
      if( (((inHigh[(i-1)]<inHigh[(i-2)])&&(inLow[(i-1)]>inLow[(i-2)]))&&(((inHigh[i]<inHigh[(i-1)])&&(inLow[i]<inLow[(i-1)]))||((inHigh[i]>inHigh[(i-1)])&&(inLow[i]>inLow[(i-1)])))) )
      {
         patternResult = (100*(((inHigh[i]<inHigh[(i-1)])) ? (1) : ((0-1))));
         patternIdx = i;
      } else if( ((i<=(patternIdx+3))&&(((patternResult>0)&&(inClose[i]>inHigh[(patternIdx-1)]))||((patternResult<0)&&(inClose[i]<inLow[(patternIdx-1)])))) )
      {
         patternIdx = 0;
      }
      i += 1;
   }
   i = startIdx;
   outIdx = 0;
   do
   {
      if( (((inHigh[(i-1)]<inHigh[(i-2)])&&(inLow[(i-1)]>inLow[(i-2)]))&&(((inHigh[i]<inHigh[(i-1)])&&(inLow[i]<inLow[(i-1)]))||((inHigh[i]>inHigh[(i-1)])&&(inLow[i]>inLow[(i-1)])))) )
      {
         patternResult = (100*(((inHigh[i]<inHigh[(i-1)])) ? (1) : ((0-1))));
         patternIdx = i;
         outInteger[outIdx++] = patternResult;
      } else if( ((i<=(patternIdx+3))&&(((patternResult>0)&&(inClose[i]>inHigh[(patternIdx-1)]))||((patternResult<0)&&(inClose[i]<inLow[(patternIdx-1)])))) )
      {
         outInteger[outIdx++] = (patternResult+(100*(((patternResult>0)) ? (1) : ((0-1)))));
         patternIdx = 0;
      } else 
      {
         outInteger[outIdx++] = 0;
      }
      i += 1;
   } while( (i<=endIdx) );
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_LIB_API TA_RetCode TA_CDLHIKKAKE_Unguarded( int    startIdx,
                                               int    endIdx,
                                               const double inOpen[],
                                               const double inHigh[],
                                               const double inLow[],
                                               const double inClose[],
                                               int          *outBegIdx,
                                               int          *outNBElement,
                                               int        outInteger[] )
{
   int i;
   int outIdx;
   int lookbackTotal;
   int patternIdx;
   int patternResult;

   lookbackTotal = TA_CDLHIKKAKE_Lookback();
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
   patternIdx = 0;
   patternResult = 0;
   i = (startIdx-3);
   while( (i<startIdx) )
   {
      if( (((inHigh[(i-1)]<inHigh[(i-2)])&&(inLow[(i-1)]>inLow[(i-2)]))&&(((inHigh[i]<inHigh[(i-1)])&&(inLow[i]<inLow[(i-1)]))||((inHigh[i]>inHigh[(i-1)])&&(inLow[i]>inLow[(i-1)])))) )
      {
         patternResult = (100*(((inHigh[i]<inHigh[(i-1)])) ? (1) : ((0-1))));
         patternIdx = i;
      } else if( ((i<=(patternIdx+3))&&(((patternResult>0)&&(inClose[i]>inHigh[(patternIdx-1)]))||((patternResult<0)&&(inClose[i]<inLow[(patternIdx-1)])))) )
      {
         patternIdx = 0;
      }
      i += 1;
   }
   i = startIdx;
   outIdx = 0;
   do
   {
      if( (((inHigh[(i-1)]<inHigh[(i-2)])&&(inLow[(i-1)]>inLow[(i-2)]))&&(((inHigh[i]<inHigh[(i-1)])&&(inLow[i]<inLow[(i-1)]))||((inHigh[i]>inHigh[(i-1)])&&(inLow[i]>inLow[(i-1)])))) )
      {
         patternResult = (100*(((inHigh[i]<inHigh[(i-1)])) ? (1) : ((0-1))));
         patternIdx = i;
         outInteger[outIdx++] = patternResult;
      } else if( ((i<=(patternIdx+3))&&(((patternResult>0)&&(inClose[i]>inHigh[(patternIdx-1)]))||((patternResult<0)&&(inClose[i]<inLow[(patternIdx-1)])))) )
      {
         outInteger[outIdx++] = (patternResult+(100*(((patternResult>0)) ? (1) : ((0-1)))));
         patternIdx = 0;
      } else 
      {
         outInteger[outIdx++] = 0;
      }
      i += 1;
   } while( (i<=endIdx) );
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_CDLHIKKAKE( int    startIdx,
                            int    endIdx,
                            const float inOpen[],
                            const float inHigh[],
                            const float inLow[],
                            const float inClose[],
                            int          *outBegIdx,
                            int          *outNBElement,
                            int        outInteger[] )
{
   int i;
   int outIdx;
   int lookbackTotal;
   int patternIdx;
   int patternResult;

   if( startIdx < 0 )
      return TA_OUT_OF_RANGE_START_INDEX;
   if( (endIdx < 0) || (endIdx < startIdx) )
      return TA_OUT_OF_RANGE_END_INDEX;

   if( !inOpen )
      return TA_BAD_PARAM;
   if( !inHigh )
      return TA_BAD_PARAM;
   if( !inLow )
      return TA_BAD_PARAM;
   if( !inClose )
      return TA_BAD_PARAM;
   if( !outInteger )
      return TA_BAD_PARAM;

   lookbackTotal = TA_CDLHIKKAKE_Lookback();
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
   patternIdx = 0;
   patternResult = 0;
   i = (startIdx-3);
   while( (i<startIdx) )
   {
      if( (((inHigh[(i-1)]<inHigh[(i-2)])&&(inLow[(i-1)]>inLow[(i-2)]))&&(((inHigh[i]<inHigh[(i-1)])&&(inLow[i]<inLow[(i-1)]))||((inHigh[i]>inHigh[(i-1)])&&(inLow[i]>inLow[(i-1)])))) )
      {
         patternResult = (100*(((inHigh[i]<inHigh[(i-1)])) ? (1) : ((0-1))));
         patternIdx = i;
      } else if( ((i<=(patternIdx+3))&&(((patternResult>0)&&(inClose[i]>inHigh[(patternIdx-1)]))||((patternResult<0)&&(inClose[i]<inLow[(patternIdx-1)])))) )
      {
         patternIdx = 0;
      }
      i += 1;
   }
   i = startIdx;
   outIdx = 0;
   do
   {
      if( (((inHigh[(i-1)]<inHigh[(i-2)])&&(inLow[(i-1)]>inLow[(i-2)]))&&(((inHigh[i]<inHigh[(i-1)])&&(inLow[i]<inLow[(i-1)]))||((inHigh[i]>inHigh[(i-1)])&&(inLow[i]>inLow[(i-1)])))) )
      {
         patternResult = (100*(((inHigh[i]<inHigh[(i-1)])) ? (1) : ((0-1))));
         patternIdx = i;
         outInteger[outIdx++] = patternResult;
      } else if( ((i<=(patternIdx+3))&&(((patternResult>0)&&(inClose[i]>inHigh[(patternIdx-1)]))||((patternResult<0)&&(inClose[i]<inLow[(patternIdx-1)])))) )
      {
         outInteger[outIdx++] = (patternResult+(100*(((patternResult>0)) ? (1) : ((0-1)))));
         patternIdx = 0;
      } else 
      {
         outInteger[outIdx++] = 0;
      }
      i += 1;
   } while( (i<=endIdx) );
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_CDLHIKKAKE_Unguarded( int    startIdx,
                                      int    endIdx,
                                      const float inOpen[],
                                      const float inHigh[],
                                      const float inLow[],
                                      const float inClose[],
                                      int          *outBegIdx,
                                      int          *outNBElement,
                                      int        outInteger[] )
{
   int i;
   int outIdx;
   int lookbackTotal;
   int patternIdx;
   int patternResult;

   lookbackTotal = TA_CDLHIKKAKE_Lookback();
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
   patternIdx = 0;
   patternResult = 0;
   i = (startIdx-3);
   while( (i<startIdx) )
   {
      if( (((inHigh[(i-1)]<inHigh[(i-2)])&&(inLow[(i-1)]>inLow[(i-2)]))&&(((inHigh[i]<inHigh[(i-1)])&&(inLow[i]<inLow[(i-1)]))||((inHigh[i]>inHigh[(i-1)])&&(inLow[i]>inLow[(i-1)])))) )
      {
         patternResult = (100*(((inHigh[i]<inHigh[(i-1)])) ? (1) : ((0-1))));
         patternIdx = i;
      } else if( ((i<=(patternIdx+3))&&(((patternResult>0)&&(inClose[i]>inHigh[(patternIdx-1)]))||((patternResult<0)&&(inClose[i]<inLow[(patternIdx-1)])))) )
      {
         patternIdx = 0;
      }
      i += 1;
   }
   i = startIdx;
   outIdx = 0;
   do
   {
      if( (((inHigh[(i-1)]<inHigh[(i-2)])&&(inLow[(i-1)]>inLow[(i-2)]))&&(((inHigh[i]<inHigh[(i-1)])&&(inLow[i]<inLow[(i-1)]))||((inHigh[i]>inHigh[(i-1)])&&(inLow[i]>inLow[(i-1)])))) )
      {
         patternResult = (100*(((inHigh[i]<inHigh[(i-1)])) ? (1) : ((0-1))));
         patternIdx = i;
         outInteger[outIdx++] = patternResult;
      } else if( ((i<=(patternIdx+3))&&(((patternResult>0)&&(inClose[i]>inHigh[(patternIdx-1)]))||((patternResult<0)&&(inClose[i]<inLow[(patternIdx-1)])))) )
      {
         outInteger[outIdx++] = (patternResult+(100*(((patternResult>0)) ? (1) : ((0-1)))));
         patternIdx = 0;
      } else 
      {
         outInteger[outIdx++] = 0;
      }
      i += 1;
   } while( (i<=endIdx) );
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

