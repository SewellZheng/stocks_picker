int macd_lookback(int           optInFastPeriod,                                             int           optInSlowPeriod,                                             int           optInSignalPeriod)
{
    int tempInteger;
    
    
    
    /* The lookback is driven by the signal line output.
    *
    * (must also account for the initial data consume
    *  by the slow period).
    */
    
    /* Make sure slow is really slower than
    * the fast period! if not, swap...
    */
    if( optInSlowPeriod < optInFastPeriod )
    {
    /* swap */
    tempInteger       = optInSlowPeriod;
    optInSlowPeriod = optInFastPeriod;
    optInFastPeriod = tempInteger;
    }
    
    return   ema_lookback( optInSlowPeriod   )
    + ema_lookback( optInSignalPeriod );
}

TA_RetCode macd(int startIdx, int endIdx, const double inReal[], int optInFastPeriod, int optInSlowPeriod, int optInSignalPeriod, int *outBegIdx, int *outNBElement, double outMACD[], double outMACDSignal[], double outMACDHist[])
{
    double *slowEMABuffer;
    double *fastEMABuffer;
    TA_RetCode retCode;
    int tempInteger;
    int outBegIdx1;
    int outNbElement1;
    int outBegIdx2;
    int outNbElement2;
    double slowK, fastK, signalK;
    int lookbackTotal, lookbackSignal;
    int useFixedK;
    int i;

    /* !!! A lot of speed optimization could be done
    * !!! with this function.
    * !!!
    * !!! A better approach would be to use ema
    * !!! just to get the seeding values for the
    * !!! fast and slow EMA. Then process the difference
    * !!! in an allocated buffer until enough data is
    * !!! available for the first signal value.
    * !!! From that point all the processing can
    * !!! be done in a tight loop.
    * !!!
    * !!! That approach will have the following
    * !!! advantage:
    * !!!   1) One mem allocation needed instead of two.
    * !!!   2) The mem allocation size will be only the
    * !!!      signal lookback period instead of the
    * !!!      whole range of data.
    * !!!   3) Processing will be done in a tight loop.
    * !!!      allowing to avoid a lot of memory store-load
    * !!!      operation.
    * !!!   4) The memcpy at the end will be eliminated!
    * !!!
    * !!! If only I had time....
    */

    /* Make sure slow is really slower than
    * the fast period! if not, swap...
    */
    if( optInSlowPeriod < optInFastPeriod )
    {
    /* swap */
    tempInteger       = optInSlowPeriod;
    optInSlowPeriod = optInFastPeriod;
    optInFastPeriod = tempInteger;
    }

    /* Catch special case for fix 26/12 MACD.
     * Use hardcoded k values matching the original algorithm. */
    useFixedK = 0;
    if( optInSlowPeriod == 0 )
    {
    optInSlowPeriod = 26;
    slowK = 0.075;
    useFixedK = 1;
    }
    else
    {
    slowK = 2.0 / ((double)(optInSlowPeriod + 1));
    }

    if( optInFastPeriod == 0 )
    {
    optInFastPeriod = 12;
    fastK = 0.15;
    useFixedK = 1;
    }
    else
    {
    fastK = 2.0 / ((double)(optInFastPeriod + 1));
    }

    signalK = 2.0 / ((double)(optInSignalPeriod + 1));
    lookbackSignal = ema_lookback( optInSignalPeriod );

    /* Move up the start index if there is not
    * enough initial data.
    */
    lookbackTotal =  lookbackSignal;
    lookbackTotal += ema_lookback( optInSlowPeriod );

    if( startIdx < lookbackTotal )
    startIdx = lookbackTotal;

    /* Make sure there is still something to evaluate. */
    if( startIdx > endIdx )
    {
    *outBegIdx = 0;
    *outNBElement = 0;
    return TA_SUCCESS;
    }

    /* Allocate intermediate buffer for fast/slow EMA. */
    tempInteger = (endIdx-startIdx)+1+lookbackSignal;
    fastEMABuffer = malloc((tempInteger) * sizeof(double));
    if( !fastEMABuffer )
    {
    *outBegIdx = 0;
    *outNBElement = 0;
    return TA_ALLOC_ERR;
    }

    slowEMABuffer = malloc((tempInteger) * sizeof(double));
    if( !slowEMABuffer )
    {
    *outBegIdx = 0;
    *outNBElement = 0;
    free(fastEMABuffer);
    return TA_ALLOC_ERR;
    }

    /* Calculate the slow EMA.
    *
    * Move back the startIdx to get enough data
    * for the signal period. That way, once the
    * signal calculation is done, all the output
    * will start at the requested 'startIdx'.
    */
    tempInteger = startIdx-lookbackSignal;

    /* Use ema_private when hardcoded k is needed (MACDFIX path).
     * Use ema() for the normal path — codegen handles double/float routing.
     */
    if( useFixedK )
    retCode = ema_private( tempInteger, endIdx,
    inReal, optInSlowPeriod, slowK,
    &outBegIdx1, &outNbElement1, slowEMABuffer );
    else
    retCode = ema( tempInteger, endIdx,
    inReal, optInSlowPeriod,
    &outBegIdx1, &outNbElement1, slowEMABuffer );

    if( retCode != TA_SUCCESS )
    {
    *outBegIdx = 0;
    *outNBElement = 0;
    free(fastEMABuffer);
    free(slowEMABuffer);
    return retCode;
    }

    /* Calculate the fast EMA. */
    if( useFixedK )
    retCode = ema_private( tempInteger, endIdx,
    inReal, optInFastPeriod, fastK,
    &outBegIdx2, &outNbElement2, fastEMABuffer );
    else
    retCode = ema( tempInteger, endIdx,
    inReal, optInFastPeriod,
    &outBegIdx2, &outNbElement2, fastEMABuffer );

    if( retCode != TA_SUCCESS )
    {
    *outBegIdx = 0;
    *outNBElement = 0;
    free(fastEMABuffer);
    free(slowEMABuffer);
    return retCode;
    }

    /* Parano tests. Will be removed eventually. */
    if( (outBegIdx1 != tempInteger) ||
    (outBegIdx2 != tempInteger) ||
    (outNbElement1 != outNbElement2) ||
    (outNbElement1 != (endIdx-startIdx)+1+lookbackSignal) )
    {
    *outBegIdx = 0;
    *outNBElement = 0;
    free(fastEMABuffer);
    free(slowEMABuffer);
    return TA_BAD_PARAM;
    }

    /* Calculate (fast EMA) - (slow EMA). */
    for( i=0; i < outNbElement1; i++ )
    fastEMABuffer[i] = fastEMABuffer[i] - slowEMABuffer[i];


    /* Copy the result into the output for the caller. */
    memcpy(outMACD, &fastEMABuffer[lookbackSignal], ((endIdx-startIdx)+1) * sizeof(double));

    /* Calculate the signal/trigger line (on double buffer, use ema_private). */
    retCode = ema_private( 0, outNbElement1-1,
    fastEMABuffer, optInSignalPeriod, signalK,
    &outBegIdx2, &outNbElement2, outMACDSignal );


    free(fastEMABuffer);
    free(slowEMABuffer);

    if( retCode != TA_SUCCESS )
    {
    *outBegIdx = 0;
    *outNBElement = 0;
    return retCode;
    }

    /* Calculate the histogram. */
    for( i=0; i < outNbElement2; i++ )
    outMACDHist[i] = outMACD[i]-outMACDSignal[i];


    /* All done! Indicate the output limits and return success. */
    *outBegIdx     = startIdx;
    *outNBElement  = outNbElement2;

    return TA_SUCCESS;
}
