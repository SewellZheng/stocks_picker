int midpoint_lookback(int           optInTimePeriod)
{
    return (optInTimePeriod-1);
}

TA_RetCode midpoint(int startIdx, int endIdx, const double inReal[], int optInTimePeriod, int *outBegIdx, int *outNBElement, double outReal[])
{
    double lowest, highest, tmp;
    int outIdx, nbInitialElementNeeded;
    int trailingIdx, today, i;



    /* Find the highest and lowest value of a timeserie
    * over the period.
    *      MIDPOINT = (Highest Value + Lowest Value)/2
    *
    * See MIDPRICE if the input is a price bar with a
    * high and low timeserie.
    */

    /* Identify the minimum number of price bar needed
    * to identify at least one output over the specified
    * period.
    */
    nbInitialElementNeeded = (optInTimePeriod-1);

    /* Move up the start index if there is not
    * enough initial data.
    */
    if( startIdx < nbInitialElementNeeded )
    startIdx = nbInitialElementNeeded;

    /* Make sure there is still something to evaluate. */
    if( startIdx > endIdx )
    {
    *outBegIdx = 0;
    *outNBElement = 0;
    return TA_SUCCESS;
    }

    /* Proceed with the calculation for the requested range.
    * Note that this algorithm allows the input and
    * output to be the same buffer.
    */
    outIdx = 0;
    today       = startIdx;
    trailingIdx = startIdx-nbInitialElementNeeded;

    while( today <= endIdx )
    {
    lowest  = inReal[trailingIdx++];
    highest = lowest;
    for( i=trailingIdx; i <= today; i++ )
    {
    tmp = inReal[i];
    if( tmp < lowest ) lowest= tmp;
    else if( tmp > highest) highest = tmp;
    }

    outReal[outIdx++] = (highest+lowest)/2.0;
    today++;
    }

    /* Keep the outBegIdx relative to the
    * caller input before returning.
    */
    *outBegIdx    = startIdx;
    *outNBElement = outIdx;

    return TA_SUCCESS;
}
