/* List of contributors:
 *
 *  Initial  Name/description
 *  -------------------------------------------------------------------
 *  MF       Mario Fortier
 *  AA       Andrew Atkinson
 *
 * Change history:
 *
 *  MMDDYY BY   Description
 *  -------------------------------------------------------------------
 *  112400 MF   Template creation.
 *  052603 MF   Adapt code to compile with .NET Managed C++
 *  020605 AA   Fix #1117656. NULL pointer assignement.
 */

int trix_lookback(int           optInTimePeriod)
{
   int emaLookback;

   emaLookback = ema_lookback( optInTimePeriod );
   return (emaLookback*3) + rocr_lookback( 1 );
}

TA_RetCode trix(int startIdx, int endIdx, const double inReal[], int optInTimePeriod, int *outBegIdx, int *outNBElement, double outReal[])
{
   double *tempBuffer;
   int nbElement;
   int begIdx;
   int totalLookback;
   int emaLookback, rocLookback;
   TA_RetCode retCode;
   int nbElementToOutput;

   /* Adjust the startIdx to account for the lookback. */
   emaLookback   = ema_lookback( optInTimePeriod );
   rocLookback   = rocr_lookback( 1 );
   totalLookback = (emaLookback*3) + rocLookback;

   if( startIdx < totalLookback )
      startIdx = totalLookback;

   /* Make sure there is still something to evaluate. */
   if( startIdx > endIdx )
   {
      *outNBElement = 0;
      *outBegIdx = 0;
      return TA_SUCCESS;
   }

   *outBegIdx = startIdx;

   nbElementToOutput = (endIdx-startIdx)+1+totalLookback;

   /* Allocate a temporary buffer for performing
    * the calculation.
    */
   tempBuffer = malloc((nbElementToOutput) * sizeof(double));

   if( !tempBuffer )
   {
      *outNBElement = 0;
      *outBegIdx = 0;
      return TA_ALLOC_ERR;
   }

   /* Calculate the first EMA */
   retCode = ema( (startIdx-totalLookback), endIdx, inReal,
      optInTimePeriod,
      &begIdx, &nbElement,
      tempBuffer );

   /* Verify for failure or if not enough data after
    * calculating the EMA.
    */
   if( (retCode != TA_SUCCESS ) || (nbElement == 0) )
   {
      *outNBElement = 0;
      *outBegIdx = 0;
      free(tempBuffer);
      return retCode;
   }

   nbElementToOutput--; /* Make this variable zero base from now on. */

   /* Calculate the second EMA */
   nbElementToOutput -= emaLookback;
   retCode = ema( 0, nbElementToOutput, tempBuffer,
      optInTimePeriod,
      &begIdx, &nbElement,
      tempBuffer );

   /* Verify for failure or if not enough data after
    * calculating the EMA.
    */
   if( (retCode != TA_SUCCESS ) || (nbElement == 0) )
   {
      *outNBElement = 0;
      *outBegIdx = 0;
      free(tempBuffer);
      return retCode;
   }

   /* Calculate the third EMA */
   nbElementToOutput -= emaLookback;
   retCode = ema( 0, nbElementToOutput, tempBuffer,
      optInTimePeriod,
      &begIdx, &nbElement,
      tempBuffer );

   /* Verify for failure or if not enough data after
    * calculating the EMA.
    */
   if( (retCode != TA_SUCCESS ) || (nbElement == 0) )
   {
      *outNBElement = 0;
      *outBegIdx = 0;
      free(tempBuffer);
      return retCode;
   }

   /* Calculate the 1-day Rate-Of-Change */
   nbElementToOutput -= emaLookback;
   retCode = roc( 0, nbElementToOutput,
      tempBuffer,
      1,  &begIdx, outNBElement,
      outReal );

   free(tempBuffer);
   /* Verify for failure or if not enough data after
    * calculating the rate-of-change.
    */
   if( (retCode != TA_SUCCESS ) || ((int)*outNBElement == 0) )
   {
      *outNBElement = 0;
      *outBegIdx = 0;
      return retCode;
   }

   return TA_SUCCESS;
}
