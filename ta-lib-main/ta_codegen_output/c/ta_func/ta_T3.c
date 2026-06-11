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

TA_LIB_API int TA_T3_Lookback( int optInTimePeriod, double optInVFactor )
{
   return ((6*(optInTimePeriod-1))+TA_GLOBALS_UNSTABLE_PERIOD(TA_FUNC_UNST_T3,T3));
}

TA_LIB_API TA_RetCode TA_T3( int    startIdx,
                             int    endIdx,
                             const double inReal[],
                             int optInTimePeriod,
                             double optInVFactor,
                             int          *outBegIdx,
                             int          *outNBElement,
                             double        outReal[] )
{
   int outIdx;
   int lookbackTotal;
   int today;
   int i;
   double k;
   double one_minus_k;
   double e1;
   double e2;
   double e3;
   double e4;
   double e5;
   double e6;
   double c1;
   double c2;
   double c3;
   double c4;
   double tempReal;

   if( startIdx < 0 )
      return TA_OUT_OF_RANGE_START_INDEX;
   if( (endIdx < 0) || (endIdx < startIdx) )
      return TA_OUT_OF_RANGE_END_INDEX;

   if( !inReal )
      return TA_BAD_PARAM;
   if( (int)optInTimePeriod == (int)0x80000000 )
      optInTimePeriod = 5;
   else if( (int)optInTimePeriod < 2 || (int)optInTimePeriod > 100000 )
      return TA_BAD_PARAM;
   if( optInVFactor == -4e37 )
      optInVFactor = 0.7;
   else if( optInVFactor < 0e0 || optInVFactor > 1e0 )
      return TA_BAD_PARAM;
   if( !outReal )
      return TA_BAD_PARAM;

   lookbackTotal = ((6*(optInTimePeriod-1))+TA_GLOBALS_UNSTABLE_PERIOD(TA_FUNC_UNST_T3,T3));
   if( (startIdx<=lookbackTotal) )
   {
      startIdx = lookbackTotal;
   }
   if( (startIdx>endIdx) )
   {
      *outNBElement= 0;
      *outBegIdx= 0;
      return TA_SUCCESS;
   }
   *outBegIdx= startIdx;
   today = (startIdx-lookbackTotal);
   k = (2.0/(optInTimePeriod+1.0));
   one_minus_k = (1.0-k);
   tempReal = inReal[today++];
   for( i = (optInTimePeriod-1); (i>0); i -= 1 )
   {
      tempReal += inReal[today++];
   }
   e1 = (tempReal/optInTimePeriod);
   tempReal = e1;
   for( i = (optInTimePeriod-1); (i>0); i -= 1 )
   {
      e1 = ((k*inReal[today++])+(one_minus_k*e1));
      tempReal += e1;
   }
   e2 = (tempReal/optInTimePeriod);
   tempReal = e2;
   for( i = (optInTimePeriod-1); (i>0); i -= 1 )
   {
      e1 = ((k*inReal[today++])+(one_minus_k*e1));
      e2 = ((k*e1)+(one_minus_k*e2));
      tempReal += e2;
   }
   e3 = (tempReal/optInTimePeriod);
   tempReal = e3;
   for( i = (optInTimePeriod-1); (i>0); i -= 1 )
   {
      e1 = ((k*inReal[today++])+(one_minus_k*e1));
      e2 = ((k*e1)+(one_minus_k*e2));
      e3 = ((k*e2)+(one_minus_k*e3));
      tempReal += e3;
   }
   e4 = (tempReal/optInTimePeriod);
   tempReal = e4;
   for( i = (optInTimePeriod-1); (i>0); i -= 1 )
   {
      e1 = ((k*inReal[today++])+(one_minus_k*e1));
      e2 = ((k*e1)+(one_minus_k*e2));
      e3 = ((k*e2)+(one_minus_k*e3));
      e4 = ((k*e3)+(one_minus_k*e4));
      tempReal += e4;
   }
   e5 = (tempReal/optInTimePeriod);
   tempReal = e5;
   for( i = (optInTimePeriod-1); (i>0); i -= 1 )
   {
      e1 = ((k*inReal[today++])+(one_minus_k*e1));
      e2 = ((k*e1)+(one_minus_k*e2));
      e3 = ((k*e2)+(one_minus_k*e3));
      e4 = ((k*e3)+(one_minus_k*e4));
      e5 = ((k*e4)+(one_minus_k*e5));
      tempReal += e5;
   }
   e6 = (tempReal/optInTimePeriod);
   while( (today<=startIdx) )
   {
      e1 = ((k*inReal[today++])+(one_minus_k*e1));
      e2 = ((k*e1)+(one_minus_k*e2));
      e3 = ((k*e2)+(one_minus_k*e3));
      e4 = ((k*e3)+(one_minus_k*e4));
      e5 = ((k*e4)+(one_minus_k*e5));
      e6 = ((k*e5)+(one_minus_k*e6));
   }
   tempReal = (optInVFactor*optInVFactor);
   c1 = (0-(tempReal*optInVFactor));
   c2 = (3.0*(tempReal-c1));
   c3 = (((0-6.0)*tempReal)-(3.0*(optInVFactor-c1)));
   c4 = (((1.0+(3.0*optInVFactor))-c1)+(3.0*tempReal));
   outIdx = 0;
   outReal[outIdx++] = ((((c1*e6)+(c2*e5))+(c3*e4))+(c4*e3));
   while( (today<=endIdx) )
   {
      e1 = ((k*inReal[today++])+(one_minus_k*e1));
      e2 = ((k*e1)+(one_minus_k*e2));
      e3 = ((k*e2)+(one_minus_k*e3));
      e4 = ((k*e3)+(one_minus_k*e4));
      e5 = ((k*e4)+(one_minus_k*e5));
      e6 = ((k*e5)+(one_minus_k*e6));
      outReal[outIdx++] = ((((c1*e6)+(c2*e5))+(c3*e4))+(c4*e3));
   }
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_LIB_API TA_RetCode TA_T3_Unguarded( int    startIdx,
                                       int    endIdx,
                                       const double inReal[],
                                       int optInTimePeriod,
                                       double optInVFactor,
                                       int          *outBegIdx,
                                       int          *outNBElement,
                                       double        outReal[] )
{
   int outIdx;
   int lookbackTotal;
   int today;
   int i;
   double k;
   double one_minus_k;
   double e1;
   double e2;
   double e3;
   double e4;
   double e5;
   double e6;
   double c1;
   double c2;
   double c3;
   double c4;
   double tempReal;

   lookbackTotal = ((6*(optInTimePeriod-1))+TA_GLOBALS_UNSTABLE_PERIOD(TA_FUNC_UNST_T3,T3));
   if( (startIdx<=lookbackTotal) )
   {
      startIdx = lookbackTotal;
   }
   if( (startIdx>endIdx) )
   {
      *outNBElement= 0;
      *outBegIdx= 0;
      return TA_SUCCESS;
   }
   *outBegIdx= startIdx;
   today = (startIdx-lookbackTotal);
   k = (2.0/(optInTimePeriod+1.0));
   one_minus_k = (1.0-k);
   tempReal = inReal[today++];
   for( i = (optInTimePeriod-1); (i>0); i -= 1 )
   {
      tempReal += inReal[today++];
   }
   e1 = (tempReal/optInTimePeriod);
   tempReal = e1;
   for( i = (optInTimePeriod-1); (i>0); i -= 1 )
   {
      e1 = ((k*inReal[today++])+(one_minus_k*e1));
      tempReal += e1;
   }
   e2 = (tempReal/optInTimePeriod);
   tempReal = e2;
   for( i = (optInTimePeriod-1); (i>0); i -= 1 )
   {
      e1 = ((k*inReal[today++])+(one_minus_k*e1));
      e2 = ((k*e1)+(one_minus_k*e2));
      tempReal += e2;
   }
   e3 = (tempReal/optInTimePeriod);
   tempReal = e3;
   for( i = (optInTimePeriod-1); (i>0); i -= 1 )
   {
      e1 = ((k*inReal[today++])+(one_minus_k*e1));
      e2 = ((k*e1)+(one_minus_k*e2));
      e3 = ((k*e2)+(one_minus_k*e3));
      tempReal += e3;
   }
   e4 = (tempReal/optInTimePeriod);
   tempReal = e4;
   for( i = (optInTimePeriod-1); (i>0); i -= 1 )
   {
      e1 = ((k*inReal[today++])+(one_minus_k*e1));
      e2 = ((k*e1)+(one_minus_k*e2));
      e3 = ((k*e2)+(one_minus_k*e3));
      e4 = ((k*e3)+(one_minus_k*e4));
      tempReal += e4;
   }
   e5 = (tempReal/optInTimePeriod);
   tempReal = e5;
   for( i = (optInTimePeriod-1); (i>0); i -= 1 )
   {
      e1 = ((k*inReal[today++])+(one_minus_k*e1));
      e2 = ((k*e1)+(one_minus_k*e2));
      e3 = ((k*e2)+(one_minus_k*e3));
      e4 = ((k*e3)+(one_minus_k*e4));
      e5 = ((k*e4)+(one_minus_k*e5));
      tempReal += e5;
   }
   e6 = (tempReal/optInTimePeriod);
   while( (today<=startIdx) )
   {
      e1 = ((k*inReal[today++])+(one_minus_k*e1));
      e2 = ((k*e1)+(one_minus_k*e2));
      e3 = ((k*e2)+(one_minus_k*e3));
      e4 = ((k*e3)+(one_minus_k*e4));
      e5 = ((k*e4)+(one_minus_k*e5));
      e6 = ((k*e5)+(one_minus_k*e6));
   }
   tempReal = (optInVFactor*optInVFactor);
   c1 = (0-(tempReal*optInVFactor));
   c2 = (3.0*(tempReal-c1));
   c3 = (((0-6.0)*tempReal)-(3.0*(optInVFactor-c1)));
   c4 = (((1.0+(3.0*optInVFactor))-c1)+(3.0*tempReal));
   outIdx = 0;
   outReal[outIdx++] = ((((c1*e6)+(c2*e5))+(c3*e4))+(c4*e3));
   while( (today<=endIdx) )
   {
      e1 = ((k*inReal[today++])+(one_minus_k*e1));
      e2 = ((k*e1)+(one_minus_k*e2));
      e3 = ((k*e2)+(one_minus_k*e3));
      e4 = ((k*e3)+(one_minus_k*e4));
      e5 = ((k*e4)+(one_minus_k*e5));
      e6 = ((k*e5)+(one_minus_k*e6));
      outReal[outIdx++] = ((((c1*e6)+(c2*e5))+(c3*e4))+(c4*e3));
   }
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_T3( int    startIdx,
                    int    endIdx,
                    const float inReal[],
                    int optInTimePeriod,
                    double optInVFactor,
                    int          *outBegIdx,
                    int          *outNBElement,
                    double        outReal[] )
{
   int outIdx;
   int lookbackTotal;
   int today;
   int i;
   double k;
   double one_minus_k;
   double e1;
   double e2;
   double e3;
   double e4;
   double e5;
   double e6;
   double c1;
   double c2;
   double c3;
   double c4;
   double tempReal;

   if( startIdx < 0 )
      return TA_OUT_OF_RANGE_START_INDEX;
   if( (endIdx < 0) || (endIdx < startIdx) )
      return TA_OUT_OF_RANGE_END_INDEX;

   if( !inReal )
      return TA_BAD_PARAM;
   if( (int)optInTimePeriod == (int)0x80000000 )
      optInTimePeriod = 5;
   else if( (int)optInTimePeriod < 2 || (int)optInTimePeriod > 100000 )
      return TA_BAD_PARAM;
   if( optInVFactor == -4e37 )
      optInVFactor = 0.7;
   else if( optInVFactor < 0e0 || optInVFactor > 1e0 )
      return TA_BAD_PARAM;
   if( !outReal )
      return TA_BAD_PARAM;

   lookbackTotal = ((6*(optInTimePeriod-1))+TA_GLOBALS_UNSTABLE_PERIOD(TA_FUNC_UNST_T3,T3));
   if( (startIdx<=lookbackTotal) )
   {
      startIdx = lookbackTotal;
   }
   if( (startIdx>endIdx) )
   {
      *outNBElement= 0;
      *outBegIdx= 0;
      return TA_SUCCESS;
   }
   *outBegIdx= startIdx;
   today = (startIdx-lookbackTotal);
   k = (2.0/(optInTimePeriod+1.0));
   one_minus_k = (1.0-k);
   tempReal = inReal[today++];
   for( i = (optInTimePeriod-1); (i>0); i -= 1 )
   {
      tempReal += inReal[today++];
   }
   e1 = (tempReal/optInTimePeriod);
   tempReal = e1;
   for( i = (optInTimePeriod-1); (i>0); i -= 1 )
   {
      e1 = ((k*inReal[today++])+(one_minus_k*e1));
      tempReal += e1;
   }
   e2 = (tempReal/optInTimePeriod);
   tempReal = e2;
   for( i = (optInTimePeriod-1); (i>0); i -= 1 )
   {
      e1 = ((k*inReal[today++])+(one_minus_k*e1));
      e2 = ((k*e1)+(one_minus_k*e2));
      tempReal += e2;
   }
   e3 = (tempReal/optInTimePeriod);
   tempReal = e3;
   for( i = (optInTimePeriod-1); (i>0); i -= 1 )
   {
      e1 = ((k*inReal[today++])+(one_minus_k*e1));
      e2 = ((k*e1)+(one_minus_k*e2));
      e3 = ((k*e2)+(one_minus_k*e3));
      tempReal += e3;
   }
   e4 = (tempReal/optInTimePeriod);
   tempReal = e4;
   for( i = (optInTimePeriod-1); (i>0); i -= 1 )
   {
      e1 = ((k*inReal[today++])+(one_minus_k*e1));
      e2 = ((k*e1)+(one_minus_k*e2));
      e3 = ((k*e2)+(one_minus_k*e3));
      e4 = ((k*e3)+(one_minus_k*e4));
      tempReal += e4;
   }
   e5 = (tempReal/optInTimePeriod);
   tempReal = e5;
   for( i = (optInTimePeriod-1); (i>0); i -= 1 )
   {
      e1 = ((k*inReal[today++])+(one_minus_k*e1));
      e2 = ((k*e1)+(one_minus_k*e2));
      e3 = ((k*e2)+(one_minus_k*e3));
      e4 = ((k*e3)+(one_minus_k*e4));
      e5 = ((k*e4)+(one_minus_k*e5));
      tempReal += e5;
   }
   e6 = (tempReal/optInTimePeriod);
   while( (today<=startIdx) )
   {
      e1 = ((k*inReal[today++])+(one_minus_k*e1));
      e2 = ((k*e1)+(one_minus_k*e2));
      e3 = ((k*e2)+(one_minus_k*e3));
      e4 = ((k*e3)+(one_minus_k*e4));
      e5 = ((k*e4)+(one_minus_k*e5));
      e6 = ((k*e5)+(one_minus_k*e6));
   }
   tempReal = (optInVFactor*optInVFactor);
   c1 = (0-(tempReal*optInVFactor));
   c2 = (3.0*(tempReal-c1));
   c3 = (((0-6.0)*tempReal)-(3.0*(optInVFactor-c1)));
   c4 = (((1.0+(3.0*optInVFactor))-c1)+(3.0*tempReal));
   outIdx = 0;
   outReal[outIdx++] = ((((c1*e6)+(c2*e5))+(c3*e4))+(c4*e3));
   while( (today<=endIdx) )
   {
      e1 = ((k*inReal[today++])+(one_minus_k*e1));
      e2 = ((k*e1)+(one_minus_k*e2));
      e3 = ((k*e2)+(one_minus_k*e3));
      e4 = ((k*e3)+(one_minus_k*e4));
      e5 = ((k*e4)+(one_minus_k*e5));
      e6 = ((k*e5)+(one_minus_k*e6));
      outReal[outIdx++] = ((((c1*e6)+(c2*e5))+(c3*e4))+(c4*e3));
   }
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_T3_Unguarded( int    startIdx,
                              int    endIdx,
                              const float inReal[],
                              int optInTimePeriod,
                              double optInVFactor,
                              int          *outBegIdx,
                              int          *outNBElement,
                              double        outReal[] )
{
   int outIdx;
   int lookbackTotal;
   int today;
   int i;
   double k;
   double one_minus_k;
   double e1;
   double e2;
   double e3;
   double e4;
   double e5;
   double e6;
   double c1;
   double c2;
   double c3;
   double c4;
   double tempReal;

   lookbackTotal = ((6*(optInTimePeriod-1))+TA_GLOBALS_UNSTABLE_PERIOD(TA_FUNC_UNST_T3,T3));
   if( (startIdx<=lookbackTotal) )
   {
      startIdx = lookbackTotal;
   }
   if( (startIdx>endIdx) )
   {
      *outNBElement= 0;
      *outBegIdx= 0;
      return TA_SUCCESS;
   }
   *outBegIdx= startIdx;
   today = (startIdx-lookbackTotal);
   k = (2.0/(optInTimePeriod+1.0));
   one_minus_k = (1.0-k);
   tempReal = inReal[today++];
   for( i = (optInTimePeriod-1); (i>0); i -= 1 )
   {
      tempReal += inReal[today++];
   }
   e1 = (tempReal/optInTimePeriod);
   tempReal = e1;
   for( i = (optInTimePeriod-1); (i>0); i -= 1 )
   {
      e1 = ((k*inReal[today++])+(one_minus_k*e1));
      tempReal += e1;
   }
   e2 = (tempReal/optInTimePeriod);
   tempReal = e2;
   for( i = (optInTimePeriod-1); (i>0); i -= 1 )
   {
      e1 = ((k*inReal[today++])+(one_minus_k*e1));
      e2 = ((k*e1)+(one_minus_k*e2));
      tempReal += e2;
   }
   e3 = (tempReal/optInTimePeriod);
   tempReal = e3;
   for( i = (optInTimePeriod-1); (i>0); i -= 1 )
   {
      e1 = ((k*inReal[today++])+(one_minus_k*e1));
      e2 = ((k*e1)+(one_minus_k*e2));
      e3 = ((k*e2)+(one_minus_k*e3));
      tempReal += e3;
   }
   e4 = (tempReal/optInTimePeriod);
   tempReal = e4;
   for( i = (optInTimePeriod-1); (i>0); i -= 1 )
   {
      e1 = ((k*inReal[today++])+(one_minus_k*e1));
      e2 = ((k*e1)+(one_minus_k*e2));
      e3 = ((k*e2)+(one_minus_k*e3));
      e4 = ((k*e3)+(one_minus_k*e4));
      tempReal += e4;
   }
   e5 = (tempReal/optInTimePeriod);
   tempReal = e5;
   for( i = (optInTimePeriod-1); (i>0); i -= 1 )
   {
      e1 = ((k*inReal[today++])+(one_minus_k*e1));
      e2 = ((k*e1)+(one_minus_k*e2));
      e3 = ((k*e2)+(one_minus_k*e3));
      e4 = ((k*e3)+(one_minus_k*e4));
      e5 = ((k*e4)+(one_minus_k*e5));
      tempReal += e5;
   }
   e6 = (tempReal/optInTimePeriod);
   while( (today<=startIdx) )
   {
      e1 = ((k*inReal[today++])+(one_minus_k*e1));
      e2 = ((k*e1)+(one_minus_k*e2));
      e3 = ((k*e2)+(one_minus_k*e3));
      e4 = ((k*e3)+(one_minus_k*e4));
      e5 = ((k*e4)+(one_minus_k*e5));
      e6 = ((k*e5)+(one_minus_k*e6));
   }
   tempReal = (optInVFactor*optInVFactor);
   c1 = (0-(tempReal*optInVFactor));
   c2 = (3.0*(tempReal-c1));
   c3 = (((0-6.0)*tempReal)-(3.0*(optInVFactor-c1)));
   c4 = (((1.0+(3.0*optInVFactor))-c1)+(3.0*tempReal));
   outIdx = 0;
   outReal[outIdx++] = ((((c1*e6)+(c2*e5))+(c3*e4))+(c4*e3));
   while( (today<=endIdx) )
   {
      e1 = ((k*inReal[today++])+(one_minus_k*e1));
      e2 = ((k*e1)+(one_minus_k*e2));
      e3 = ((k*e2)+(one_minus_k*e3));
      e4 = ((k*e3)+(one_minus_k*e4));
      e5 = ((k*e4)+(one_minus_k*e5));
      e6 = ((k*e5)+(one_minus_k*e6));
      outReal[outIdx++] = ((((c1*e6)+(c2*e5))+(c3*e4))+(c4*e3));
   }
   *outNBElement= outIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

