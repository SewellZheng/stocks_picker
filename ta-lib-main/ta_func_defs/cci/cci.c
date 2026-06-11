int cci_lookback(int           optInTimePeriod)
{
    return (optInTimePeriod-1);
}

TA_RetCode cci(int startIdx, int endIdx, const double inHigh[], const double inLow[], const double inClose[], int optInTimePeriod, int *outBegIdx, int *outNBElement, double outReal[])
{
    double tempReal, tempReal2, theAverage, lastValue;
    int i, j, outIdx, lookbackTotal;

    /* This ptr will points on a circular buffer of
    * at least "optInTimePeriod" element.
    */
    double circBuffer[30]; int circBuffer_Idx = 0;



    /* Identify the minimum number of price bar needed
    * to calculate at least one output.
    */
    lookbackTotal = (optInTimePeriod-1);

    /* Move up the start index if there is not
    * enough initial data.
    */
    if( startIdx < lookbackTotal )
    startIdx = lookbackTotal;

    /* Make sure there is still something to evaluate. */
    if( startIdx > endIdx )
    {
    *outBegIdx = 0;
    *outNBElement = 0;
    return TA_SUCCESS;
    }

    /* Allocate a circular buffer equal to the requested
    * period.
    */
    memset(circBuffer, 0, (optInTimePeriod) * sizeof(double)); circBuffer_Idx = 0;

    /* Do the MA calculation using tight loops. */

    /* Add-up the initial period, except for the last value.
    * Fill up the circular buffer at the same time.
    */
    i=startIdx-lookbackTotal;
    if( optInTimePeriod > 1 )
    {
    while( i < startIdx )
    {
    circBuffer[circBuffer_Idx] = (inHigh[i]+inLow[i]+inClose[i])/3;
    i++;
    circBuffer_Idx++; if( circBuffer_Idx >= optInTimePeriod ) circBuffer_Idx = 0;
    }
    }

    /* Proceed with the calculation for the requested range.
    * Note that this algorithm allows the inReal and
    * outReal to be the same buffer.
    */
    outIdx = 0;
    do
    {
    lastValue = (inHigh[i]+inLow[i]+inClose[i])/3;
    circBuffer[circBuffer_Idx] = lastValue;

    /* Calculate the average for the whole period. */
    theAverage = 0;
    for( j=0; j < optInTimePeriod; j++ )
    theAverage += circBuffer[j];
    theAverage /= optInTimePeriod;

    /* Do the summation of the ABS(TypePrice-average)
    * for the whole period.
    */
    tempReal2 = 0;
    for( j=0; j < optInTimePeriod; j++ )
    tempReal2 += fabs(circBuffer[j]-theAverage);

    /* And finally, the CCI... */
    tempReal = lastValue-theAverage;

    if( (tempReal != 0.0) && (tempReal2 != 0.0) )
    {
    outReal[outIdx++] = tempReal/(0.015*(tempReal2/optInTimePeriod));
    }
    else
    outReal[outIdx++] = 0.0;

    /* Move forward the circular buffer indexes. */
    circBuffer_Idx++; if( circBuffer_Idx >= optInTimePeriod ) circBuffer_Idx = 0;

    i++;
    } while( i <= endIdx );

    /* All done. Indicate the output limits and return. */
    *outNBElement = outIdx;
    *outBegIdx    = startIdx;

    /* Free the circular buffer if it was dynamically allocated. */
    /* circular buffer cleanup (stack-allocated, no-op) */

    return TA_SUCCESS;
}
