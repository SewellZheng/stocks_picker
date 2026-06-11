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

TA_LIB_API int TA_BETA_Lookback( int optInTimePeriod )
{
   return optInTimePeriod;
}

TA_LIB_API TA_RetCode TA_BETA( int    startIdx,
                               int    endIdx,
                               const double inReal0[],
                               const double inReal1[],
                               int optInTimePeriod,
                               int          *outBegIdx,
                               int          *outNBElement,
                               double        outReal[] )
{
   double S_xx;
   double S_xy;
   double S_x;
   double S_y;
   double last_price_x;
   double last_price_y;
   double trailing_last_price_x;
   double trailing_last_price_y;
   double tmp_real;
   double x;
   double y;
   double n;
   int i;
   int outIdx;
   int trailingIdx;
   int nbInitialElementNeeded;

   if( startIdx < 0 )
      return TA_OUT_OF_RANGE_START_INDEX;
   if( (endIdx < 0) || (endIdx < startIdx) )
      return TA_OUT_OF_RANGE_END_INDEX;

   if( !inReal0 )
      return TA_BAD_PARAM;
   if( !inReal1 )
      return TA_BAD_PARAM;
   if( (int)optInTimePeriod == (int)0x80000000 )
      optInTimePeriod = 5;
   else if( (int)optInTimePeriod < 1 || (int)optInTimePeriod > 100000 )
      return TA_BAD_PARAM;
   if( !outReal )
      return TA_BAD_PARAM;

   S_xx = 0.0;
   S_xy = 0.0;
   S_x = 0.0;
   S_y = 0.0;
   last_price_x = 0.0;
   last_price_y = 0.0;
   trailing_last_price_x = 0.0;
   trailing_last_price_y = 0.0;
   tmp_real = 0.0;
   n = 0.0;
   nbInitialElementNeeded = optInTimePeriod;
   if( (startIdx<nbInitialElementNeeded) )
   {
      startIdx = nbInitialElementNeeded;
   }
   if( (startIdx>endIdx) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      return TA_SUCCESS;
   }
   trailingIdx = (startIdx-nbInitialElementNeeded);
   trailing_last_price_x = inReal0[trailingIdx];
   last_price_x = trailing_last_price_x;
   trailing_last_price_y = inReal1[trailingIdx];
   last_price_y = trailing_last_price_y;
   i = ++trailingIdx;
   while( (i<startIdx) )
   {
      tmp_real = inReal0[i];
      if( !(TA_IS_ZERO(last_price_x)) )
      {
         x = ((tmp_real-last_price_x)/last_price_x);
      } else 
      {
         x = 0.0;
      }
      last_price_x = tmp_real;
      tmp_real = inReal1[i++];
      if( !(TA_IS_ZERO(last_price_y)) )
      {
         y = ((tmp_real-last_price_y)/last_price_y);
      } else 
      {
         y = 0.0;
      }
      last_price_y = tmp_real;
      S_xx += (x*x);
      S_xy += (x*y);
      S_x += x;
      S_y += y;
   }
   outIdx = 0;
   n = ((double)optInTimePeriod);
   do
   {
      tmp_real = inReal0[i];
      if( !(TA_IS_ZERO(last_price_x)) )
      {
         x = ((tmp_real-last_price_x)/last_price_x);
      } else 
      {
         x = 0.0;
      }
      last_price_x = tmp_real;
      tmp_real = inReal1[i++];
      if( !(TA_IS_ZERO(last_price_y)) )
      {
         y = ((tmp_real-last_price_y)/last_price_y);
      } else 
      {
         y = 0.0;
      }
      last_price_y = tmp_real;
      S_xx += (x*x);
      S_xy += (x*y);
      S_x += x;
      S_y += y;
      tmp_real = inReal0[trailingIdx];
      if( !(TA_IS_ZERO(trailing_last_price_x)) )
      {
         x = ((tmp_real-trailing_last_price_x)/trailing_last_price_x);
      } else 
      {
         x = 0.0;
      }
      trailing_last_price_x = tmp_real;
      tmp_real = inReal1[trailingIdx++];
      if( !(TA_IS_ZERO(trailing_last_price_y)) )
      {
         y = ((tmp_real-trailing_last_price_y)/trailing_last_price_y);
      } else 
      {
         y = 0.0;
      }
      trailing_last_price_y = tmp_real;
      tmp_real = ((n*S_xx)-(S_x*S_x));
      if( !(TA_IS_ZERO(tmp_real)) )
      {
         outReal[outIdx++] = (((n*S_xy)-(S_x*S_y))/tmp_real);
      } else 
      {
         outReal[outIdx++] = 0.0;
      }
      S_xx -= (x*x);
      S_xy -= (x*y);
      S_x -= x;
      S_y -= y;
   } while( (i<=endIdx) );
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_LIB_API TA_RetCode TA_BETA_Unguarded( int    startIdx,
                                         int    endIdx,
                                         const double inReal0[],
                                         const double inReal1[],
                                         int optInTimePeriod,
                                         int          *outBegIdx,
                                         int          *outNBElement,
                                         double        outReal[] )
{
   double S_xx;
   double S_xy;
   double S_x;
   double S_y;
   double last_price_x;
   double last_price_y;
   double trailing_last_price_x;
   double trailing_last_price_y;
   double tmp_real;
   double x;
   double y;
   double n;
   int i;
   int outIdx;
   int trailingIdx;
   int nbInitialElementNeeded;

   S_xx = 0.0;
   S_xy = 0.0;
   S_x = 0.0;
   S_y = 0.0;
   last_price_x = 0.0;
   last_price_y = 0.0;
   trailing_last_price_x = 0.0;
   trailing_last_price_y = 0.0;
   tmp_real = 0.0;
   n = 0.0;
   nbInitialElementNeeded = optInTimePeriod;
   if( (startIdx<nbInitialElementNeeded) )
   {
      startIdx = nbInitialElementNeeded;
   }
   if( (startIdx>endIdx) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      return TA_SUCCESS;
   }
   trailingIdx = (startIdx-nbInitialElementNeeded);
   trailing_last_price_x = inReal0[trailingIdx];
   last_price_x = trailing_last_price_x;
   trailing_last_price_y = inReal1[trailingIdx];
   last_price_y = trailing_last_price_y;
   i = ++trailingIdx;
   while( (i<startIdx) )
   {
      tmp_real = inReal0[i];
      if( !(TA_IS_ZERO(last_price_x)) )
      {
         x = ((tmp_real-last_price_x)/last_price_x);
      } else 
      {
         x = 0.0;
      }
      last_price_x = tmp_real;
      tmp_real = inReal1[i++];
      if( !(TA_IS_ZERO(last_price_y)) )
      {
         y = ((tmp_real-last_price_y)/last_price_y);
      } else 
      {
         y = 0.0;
      }
      last_price_y = tmp_real;
      S_xx += (x*x);
      S_xy += (x*y);
      S_x += x;
      S_y += y;
   }
   outIdx = 0;
   n = ((double)optInTimePeriod);
   do
   {
      tmp_real = inReal0[i];
      if( !(TA_IS_ZERO(last_price_x)) )
      {
         x = ((tmp_real-last_price_x)/last_price_x);
      } else 
      {
         x = 0.0;
      }
      last_price_x = tmp_real;
      tmp_real = inReal1[i++];
      if( !(TA_IS_ZERO(last_price_y)) )
      {
         y = ((tmp_real-last_price_y)/last_price_y);
      } else 
      {
         y = 0.0;
      }
      last_price_y = tmp_real;
      S_xx += (x*x);
      S_xy += (x*y);
      S_x += x;
      S_y += y;
      tmp_real = inReal0[trailingIdx];
      if( !(TA_IS_ZERO(trailing_last_price_x)) )
      {
         x = ((tmp_real-trailing_last_price_x)/trailing_last_price_x);
      } else 
      {
         x = 0.0;
      }
      trailing_last_price_x = tmp_real;
      tmp_real = inReal1[trailingIdx++];
      if( !(TA_IS_ZERO(trailing_last_price_y)) )
      {
         y = ((tmp_real-trailing_last_price_y)/trailing_last_price_y);
      } else 
      {
         y = 0.0;
      }
      trailing_last_price_y = tmp_real;
      tmp_real = ((n*S_xx)-(S_x*S_x));
      if( !(TA_IS_ZERO(tmp_real)) )
      {
         outReal[outIdx++] = (((n*S_xy)-(S_x*S_y))/tmp_real);
      } else 
      {
         outReal[outIdx++] = 0.0;
      }
      S_xx -= (x*x);
      S_xy -= (x*y);
      S_x -= x;
      S_y -= y;
   } while( (i<=endIdx) );
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_BETA( int    startIdx,
                      int    endIdx,
                      const float inReal0[],
                      const float inReal1[],
                      int optInTimePeriod,
                      int          *outBegIdx,
                      int          *outNBElement,
                      double        outReal[] )
{
   double S_xx;
   double S_xy;
   double S_x;
   double S_y;
   double last_price_x;
   double last_price_y;
   double trailing_last_price_x;
   double trailing_last_price_y;
   double tmp_real;
   double x;
   double y;
   double n;
   int i;
   int outIdx;
   int trailingIdx;
   int nbInitialElementNeeded;

   if( startIdx < 0 )
      return TA_OUT_OF_RANGE_START_INDEX;
   if( (endIdx < 0) || (endIdx < startIdx) )
      return TA_OUT_OF_RANGE_END_INDEX;

   if( !inReal0 )
      return TA_BAD_PARAM;
   if( !inReal1 )
      return TA_BAD_PARAM;
   if( (int)optInTimePeriod == (int)0x80000000 )
      optInTimePeriod = 5;
   else if( (int)optInTimePeriod < 1 || (int)optInTimePeriod > 100000 )
      return TA_BAD_PARAM;
   if( !outReal )
      return TA_BAD_PARAM;

   S_xx = 0.0;
   S_xy = 0.0;
   S_x = 0.0;
   S_y = 0.0;
   last_price_x = 0.0;
   last_price_y = 0.0;
   trailing_last_price_x = 0.0;
   trailing_last_price_y = 0.0;
   tmp_real = 0.0;
   n = 0.0;
   nbInitialElementNeeded = optInTimePeriod;
   if( (startIdx<nbInitialElementNeeded) )
   {
      startIdx = nbInitialElementNeeded;
   }
   if( (startIdx>endIdx) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      return TA_SUCCESS;
   }
   trailingIdx = (startIdx-nbInitialElementNeeded);
   trailing_last_price_x = inReal0[trailingIdx];
   last_price_x = trailing_last_price_x;
   trailing_last_price_y = inReal1[trailingIdx];
   last_price_y = trailing_last_price_y;
   i = ++trailingIdx;
   while( (i<startIdx) )
   {
      tmp_real = inReal0[i];
      if( !(TA_IS_ZERO(last_price_x)) )
      {
         x = ((tmp_real-last_price_x)/last_price_x);
      } else 
      {
         x = 0.0;
      }
      last_price_x = tmp_real;
      tmp_real = inReal1[i++];
      if( !(TA_IS_ZERO(last_price_y)) )
      {
         y = ((tmp_real-last_price_y)/last_price_y);
      } else 
      {
         y = 0.0;
      }
      last_price_y = tmp_real;
      S_xx += (x*x);
      S_xy += (x*y);
      S_x += x;
      S_y += y;
   }
   outIdx = 0;
   n = ((double)optInTimePeriod);
   do
   {
      tmp_real = inReal0[i];
      if( !(TA_IS_ZERO(last_price_x)) )
      {
         x = ((tmp_real-last_price_x)/last_price_x);
      } else 
      {
         x = 0.0;
      }
      last_price_x = tmp_real;
      tmp_real = inReal1[i++];
      if( !(TA_IS_ZERO(last_price_y)) )
      {
         y = ((tmp_real-last_price_y)/last_price_y);
      } else 
      {
         y = 0.0;
      }
      last_price_y = tmp_real;
      S_xx += (x*x);
      S_xy += (x*y);
      S_x += x;
      S_y += y;
      tmp_real = inReal0[trailingIdx];
      if( !(TA_IS_ZERO(trailing_last_price_x)) )
      {
         x = ((tmp_real-trailing_last_price_x)/trailing_last_price_x);
      } else 
      {
         x = 0.0;
      }
      trailing_last_price_x = tmp_real;
      tmp_real = inReal1[trailingIdx++];
      if( !(TA_IS_ZERO(trailing_last_price_y)) )
      {
         y = ((tmp_real-trailing_last_price_y)/trailing_last_price_y);
      } else 
      {
         y = 0.0;
      }
      trailing_last_price_y = tmp_real;
      tmp_real = ((n*S_xx)-(S_x*S_x));
      if( !(TA_IS_ZERO(tmp_real)) )
      {
         outReal[outIdx++] = (((n*S_xy)-(S_x*S_y))/tmp_real);
      } else 
      {
         outReal[outIdx++] = 0.0;
      }
      S_xx -= (x*x);
      S_xy -= (x*y);
      S_x -= x;
      S_y -= y;
   } while( (i<=endIdx) );
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

TA_RetCode TA_S_BETA_Unguarded( int    startIdx,
                                int    endIdx,
                                const float inReal0[],
                                const float inReal1[],
                                int optInTimePeriod,
                                int          *outBegIdx,
                                int          *outNBElement,
                                double        outReal[] )
{
   double S_xx;
   double S_xy;
   double S_x;
   double S_y;
   double last_price_x;
   double last_price_y;
   double trailing_last_price_x;
   double trailing_last_price_y;
   double tmp_real;
   double x;
   double y;
   double n;
   int i;
   int outIdx;
   int trailingIdx;
   int nbInitialElementNeeded;

   S_xx = 0.0;
   S_xy = 0.0;
   S_x = 0.0;
   S_y = 0.0;
   last_price_x = 0.0;
   last_price_y = 0.0;
   trailing_last_price_x = 0.0;
   trailing_last_price_y = 0.0;
   tmp_real = 0.0;
   n = 0.0;
   nbInitialElementNeeded = optInTimePeriod;
   if( (startIdx<nbInitialElementNeeded) )
   {
      startIdx = nbInitialElementNeeded;
   }
   if( (startIdx>endIdx) )
   {
      *outBegIdx= 0;
      *outNBElement= 0;
      return TA_SUCCESS;
   }
   trailingIdx = (startIdx-nbInitialElementNeeded);
   trailing_last_price_x = inReal0[trailingIdx];
   last_price_x = trailing_last_price_x;
   trailing_last_price_y = inReal1[trailingIdx];
   last_price_y = trailing_last_price_y;
   i = ++trailingIdx;
   while( (i<startIdx) )
   {
      tmp_real = inReal0[i];
      if( !(TA_IS_ZERO(last_price_x)) )
      {
         x = ((tmp_real-last_price_x)/last_price_x);
      } else 
      {
         x = 0.0;
      }
      last_price_x = tmp_real;
      tmp_real = inReal1[i++];
      if( !(TA_IS_ZERO(last_price_y)) )
      {
         y = ((tmp_real-last_price_y)/last_price_y);
      } else 
      {
         y = 0.0;
      }
      last_price_y = tmp_real;
      S_xx += (x*x);
      S_xy += (x*y);
      S_x += x;
      S_y += y;
   }
   outIdx = 0;
   n = ((double)optInTimePeriod);
   do
   {
      tmp_real = inReal0[i];
      if( !(TA_IS_ZERO(last_price_x)) )
      {
         x = ((tmp_real-last_price_x)/last_price_x);
      } else 
      {
         x = 0.0;
      }
      last_price_x = tmp_real;
      tmp_real = inReal1[i++];
      if( !(TA_IS_ZERO(last_price_y)) )
      {
         y = ((tmp_real-last_price_y)/last_price_y);
      } else 
      {
         y = 0.0;
      }
      last_price_y = tmp_real;
      S_xx += (x*x);
      S_xy += (x*y);
      S_x += x;
      S_y += y;
      tmp_real = inReal0[trailingIdx];
      if( !(TA_IS_ZERO(trailing_last_price_x)) )
      {
         x = ((tmp_real-trailing_last_price_x)/trailing_last_price_x);
      } else 
      {
         x = 0.0;
      }
      trailing_last_price_x = tmp_real;
      tmp_real = inReal1[trailingIdx++];
      if( !(TA_IS_ZERO(trailing_last_price_y)) )
      {
         y = ((tmp_real-trailing_last_price_y)/trailing_last_price_y);
      } else 
      {
         y = 0.0;
      }
      trailing_last_price_y = tmp_real;
      tmp_real = ((n*S_xx)-(S_x*S_x));
      if( !(TA_IS_ZERO(tmp_real)) )
      {
         outReal[outIdx++] = (((n*S_xy)-(S_x*S_y))/tmp_real);
      } else 
      {
         outReal[outIdx++] = 0.0;
      }
      S_xx -= (x*x);
      S_xy -= (x*y);
      S_x -= x;
      S_y -= y;
   } while( (i<=endIdx) );
   *outNBElement= outIdx;
   *outBegIdx= startIdx;
   return TA_SUCCESS;

   return TA_SUCCESS;
}

