/* List of contributors:
 *
 *  Initial  Name/description
 *  -------------------------------------------------------------------
 *  MF       Mario Fortier
 *
 *
 * Change history:
 *
 *  MMDDYY BY   Description
 *  -------------------------------------------------------------------
 *  010102 MF   Template creation.
 *  052603 MF   Adapt code to compile with .NET Managed C++
 *
 */

int dema_lookback(int           optInTimePeriod)
{
   /* Get lookback for one EMA.
    * Multiply by two (because double smoothing).
    */
   return ema_lookback( optInTimePeriod ) * 2;
}

TA_RetCode dema(int startIdx, int endIdx, const double inReal[], int optInTimePeriod, int *outBegIdx, int *outNBElement, double outReal[])
{
   double *firstEMA;
   double *secondEMA;
   int firstEMABegIdx;
   int firstEMANbElement;
   int secondEMABegIdx;
   int secondEMANbElement;
   int tempInt, outIdx, firstEMAIdx, lookbackTotal, lookbackEMA;
   TA_RetCode retCode;

   /* For an explanation of this function, please read
    *
    * Stocks & Commodities V. 12:1 (11-19):
    *   Smoothing Data With Faster Moving Averages
    * Stocks & Commodities V. 12:2 (72-80):
    *   Smoothing Data With Less Lag
    *
    * Both magazine articles written by Patrick G. Mulloy
    *
    * Essentially, a DEMA of time serie 't' is:
    *   EMA2 = EMA(EMA(t,period),period)
    *   DEMA = 2*EMA(t,period)- EMA2
    *
    * DEMA offers a moving average with less lags then the
    * traditional EMA.
    *
    * Do not confuse a DEMA with the EMA2. Both are called
    * "Double EMA" in the litterature, but EMA2 is a simple
    * EMA of an EMA, while DEMA is a compostie of a single
    * EMA with EMA2.
    *
    * TEMA is very similar (and from the same author).
    */

   /* Will change only on success. */
   *outNBElement = 0;
   *outBegIdx = 0;

   /* Adjust startIdx to account for the lookback period. */
   lookbackEMA = ema_lookback( optInTimePeriod );
   lookbackTotal = lookbackEMA * 2;

   if( startIdx < lookbackTotal )
      startIdx = lookbackTotal;

   /* Make sure there is still something to evaluate. */
   if( startIdx > endIdx )
      return TA_SUCCESS;

   /* Allocate a temporary buffer for the firstEMA.
    *
    * When possible, re-use the outputBuffer for temp
    * calculation.
    */
   if( inReal == outReal )
      firstEMA = outReal;
   else
   {
      tempInt = lookbackTotal+(endIdx-startIdx)+1;
      firstEMA = malloc((tempInt) * sizeof(double));
      if( !firstEMA )
         return TA_ALLOC_ERR;
   }

   /* Calculate the first EMA */
   retCode = ema( startIdx-lookbackEMA, endIdx, inReal,
      optInTimePeriod,
      &firstEMABegIdx, &firstEMANbElement,
      firstEMA );

   /* Verify for failure or if not enough data after
    * calculating the first EMA.
    */
   if( (retCode != TA_SUCCESS) || (firstEMANbElement == 0) )
   {
      if (firstEMA != outReal) { free(firstEMA); }
         return retCode;
   }

   /* Allocate a temporary buffer for storing the EMA of the EMA. */
   secondEMA = malloc((firstEMANbElement) * sizeof(double));

   if( !secondEMA )
   {
      if (firstEMA != outReal) { free(firstEMA); }
         return TA_ALLOC_ERR;
   }

   retCode = ema( 0, firstEMANbElement-1, firstEMA,
      optInTimePeriod,
      &secondEMABegIdx, &secondEMANbElement,
      secondEMA );

   /* Return empty output on failure or if not enough data after
    * calculating the second EMA.
    */
   if( (retCode != TA_SUCCESS) || (secondEMANbElement == 0) )
   {
      if (firstEMA != outReal) { free(firstEMA); }
         free(secondEMA);
      return retCode;
   }

   /* Iterate through the second EMA and write the DEMA into
    * the output.
    */
   firstEMAIdx = secondEMABegIdx;
   outIdx = 0;
   while( outIdx < secondEMANbElement )
   {
      outReal[outIdx] = (2.0*firstEMA[firstEMAIdx++]) - secondEMA[outIdx];
      outIdx++;
   }

   if (firstEMA != outReal) { free(firstEMA); }
      free(secondEMA);

   /* Succeed. Indicate where the output starts relative to
    * the caller input.
    */
   *outBegIdx    = firstEMABegIdx + secondEMABegIdx;
   *outNBElement = outIdx;

   return TA_SUCCESS;
}
