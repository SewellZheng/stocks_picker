int ln_lookback(void)
{
    return 0;
}

TA_RetCode ln(int startIdx, int endIdx, const double inReal[], int *outBegIdx, int *outNBElement, double outReal[])
{
    int outIdx;
    int i;



    for( i=startIdx, outIdx=0; i <= endIdx; i++, outIdx++ )
    {
    outReal[outIdx] = log(inReal[i]);
    }

    *outNBElement = outIdx;
    *outBegIdx    = startIdx;

    return TA_SUCCESS;
}
