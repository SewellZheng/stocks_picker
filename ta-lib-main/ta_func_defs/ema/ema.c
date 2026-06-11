int ema_lookback(int           optInTimePeriod)
{
    return optInTimePeriod - 1 + TA_GetUnstablePeriod(TA_FUNC_UNST_EMA);
}

TA_RetCode ema(int startIdx, int endIdx, const double *inReal,
               int optInTimePeriod,
               int *outBegIdx, int *outNBElement, double *outReal)
{
    double optInK_1 = 2.0 / ((double)(optInTimePeriod + 1));
    return ema_private(startIdx, endIdx, inReal, optInTimePeriod, optInK_1,
                       outBegIdx, outNBElement, outReal);
}

TA_RetCode ema_private(int startIdx, int endIdx, const double *inReal,
                       int optInTimePeriod, double optInK_1,
                         int *outBegIdx, int *outNBElement, double *outReal)
{
    double tempReal, prevMA;
    int i, today, outIdx, lookbackTotal;

    lookbackTotal = ema_lookback( optInTimePeriod );

    if( startIdx < lookbackTotal )
    startIdx = lookbackTotal;

    if( startIdx > endIdx )
    {
    *outBegIdx = 0;
    *outNBElement = 0;
    return TA_SUCCESS;
    }
    *outBegIdx = startIdx;

    if( TA_GetCompatibility() == TA_COMPATIBILITY_DEFAULT )
    {
    today = startIdx-lookbackTotal;
    i = optInTimePeriod;
    tempReal = 0.0;
    while( i-- > 0 )
    tempReal += inReal[today++];

    prevMA = tempReal / optInTimePeriod;
    }
    else
    {
    prevMA = inReal[0];
    today = 1;
    }

    while( today <= startIdx )
    prevMA = ((inReal[today++]-prevMA)*optInK_1) + prevMA;

    outReal[0] = prevMA;
    outIdx = 1;

    while( today <= endIdx )
    {
    prevMA = ((inReal[today++]-prevMA)*optInK_1) + prevMA;
    outReal[outIdx++] = prevMA;
    }

    *outNBElement = outIdx;

    return TA_SUCCESS;
}
