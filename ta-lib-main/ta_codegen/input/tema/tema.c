/* List of contributors:
 *
 *  Initial  Name/description
 *  -------------------------------------------------------------------
 *  MF       Mario Fortier
 *
 *
 * Change history:
 *
 *  MMDDYY BY     Description
 *  -------------------------------------------------------------------
 *  112400 MF     Template creation.
 *  052603 MF     Adapt code to compile with .NET Managed C++
 *  070226 MF,CC  Allow period of 1: output is an exact copy of the
 *                input, consistent with TA_MA (issues #48, #59). The
 *                natural math (3*e1 - 3*e2 + e3 with e1=e2=e3=x) is
 *                exact on x86 but not under FMA contraction (ARM64
 *                clang leaves ~1e-14 residue), so the copy is explicit.
 */

int tema_lookback(int           optInTimePeriod)
{
   int retValue;

   /* Get lookack for one EMA. */
   retValue = ema_lookback( optInTimePeriod );

   return retValue * 3;
}

TA_RetCode tema(int startIdx, int endIdx, const double inReal[], int optInTimePeriod, int *outBegIdx, int *outNBElement, double outReal[])
{
   double *firstEMA;
   double *secondEMA;
   int firstEMABegIdx;
   int firstEMANbElement;
   int secondEMABegIdx;
   int secondEMANbElement;
   int thirdEMABegIdx;
   int thirdEMANbElement;

   int tempInt, outIdx, lookbackTotal, lookbackEMA;
   int firstEMAIdx, secondEMAIdx;

   TA_RetCode retCode;

   /* For an explanation of this function, please read:
    *
    * Stocks & Commodities V. 12:1 (11-19):
    *   Smoothing Data With Faster Moving Averages
    * Stocks & Commodities V. 12:2 (72-80):
    *   Smoothing Data With Less Lag
    *
    * Both magazine articles written by Patrick G. Mulloy
    *
    * Essentially, a TEMA of time serie 't' is:
    *   EMA1 = EMA(t,period)
    *   EMA2 = EMA(EMA(t,period),period)
    *   EMA3 = EMA(EMA(EMA(t,period),period))
    *   TEMA = 3*EMA1 - 3*EMA2 + EMA3
    *
    * TEMA offers a moving average with less lags then the
    * traditional EMA.
    *
    * Do not confuse a TEMA with EMA3. Both are called "Triple EMA"
    * in the litterature.
    *
    * DEMA is very similar (and from the same author).
    */

   /* Will change only on success. */
   *outNBElement = 0;
   *outBegIdx = 0;

   /* Adjust startIdx to account for the lookback period. */
   lookbackEMA = ema_lookback( optInTimePeriod );
   lookbackTotal = lookbackEMA * 3;

   if( startIdx < lookbackTotal )
      startIdx = lookbackTotal;

   /* Make sure there is still something to evaluate. */
   if( startIdx > endIdx )
      return TA_SUCCESS;

   /* No smoothing at period of 1: the output is a copy of the input
    * (same convention as TA_MA for every MAType). Explicit because the
    * 3*e1 - 3*e2 + e3 composition cancels exactly only without FMA
    * contraction; ARM64 fused multiply-add leaves ~1e-14 residue.
    */
   if( optInTimePeriod == 1 )
   {
      *outBegIdx = startIdx;
      outIdx = 0;
      while( startIdx <= endIdx )
         outReal[outIdx++] = inReal[startIdx++];
      *outNBElement = outIdx;
      return TA_SUCCESS;
   }

   /* Allocate a temporary buffer for the firstEMA. */
   tempInt = lookbackTotal+(endIdx-startIdx)+1;
   firstEMA = malloc((tempInt) * sizeof(double));
   if( !firstEMA )
      return TA_ALLOC_ERR;

   /* Calculate the first EMA */
   retCode = ema( startIdx-(lookbackEMA*2), endIdx, inReal,
      optInTimePeriod,
      &firstEMABegIdx, &firstEMANbElement,
      firstEMA );

   /* Verify for failure or if not enough data after
    * calculating the first EMA.
    */
   if( (retCode != TA_SUCCESS ) || (firstEMANbElement == 0) )
   {
      free(firstEMA);
      return retCode;
   }

   /* Allocate a temporary buffer for storing the EMA2 */
   secondEMA = malloc((firstEMANbElement) * sizeof(double));
   if( !secondEMA )
   {
      free(firstEMA);
      return TA_ALLOC_ERR;
   }

   retCode = ema( 0, firstEMANbElement-1, firstEMA,
      optInTimePeriod,
      &secondEMABegIdx, &secondEMANbElement,
      secondEMA );

   /* Return empty output on failure or if not enough data after
    * calculating the second EMA.
    */
   if( (retCode != TA_SUCCESS ) || (secondEMANbElement == 0) )
   {
      free(firstEMA);
      free(secondEMA);
      return retCode;
   }

   /* Calculate the EMA3 into the caller provided output. */
   retCode = ema( 0, secondEMANbElement-1, secondEMA,
      optInTimePeriod,
      &thirdEMABegIdx, &thirdEMANbElement,
      outReal );

   /* Return empty output on failure or if not enough data after
    * calculating the third EMA.
    */
   if( (retCode != TA_SUCCESS ) || (thirdEMANbElement == 0) )
   {
      free(firstEMA);
      free(secondEMA);
      return retCode;
   }

   /* Indicate where the output starts relative to
    * the caller input.
    */
   firstEMAIdx  = thirdEMABegIdx + secondEMABegIdx;
   secondEMAIdx = thirdEMABegIdx;
   *outBegIdx = firstEMAIdx + firstEMABegIdx;

   /* Do the TEMA:
    *  Iterate through the EMA3 (output buffer) and adjust
    *  the value by using the EMA2 and EMA1.
    */
   outIdx = 0;
   while( outIdx < thirdEMANbElement )
   {
      outReal[outIdx] += (3.0*firstEMA[firstEMAIdx++]) - (3.0*secondEMA[secondEMAIdx++]);
      outIdx++;
   }

   free(firstEMA);
   free(secondEMA);

   /* Indicates to the caller the number of output
    * successfully calculated.
    */
   *outNBElement = outIdx;

   return TA_SUCCESS;
}
