int sqrt_lookback(void)
{
    return 0;
}

TA_RetCode sqrt(int startIdx, int endIdx, const double inReal[], int *outBegIdx, int *outNBElement, double outReal[])
{
    int outIdx;
    int i;



    for( i=startIdx, outIdx=0; i <= endIdx; i++, outIdx++ )
    {
    outReal[outIdx] = sqrt(inReal[i]);
    }

    *outNBElement = outIdx;
    *outBegIdx    = startIdx;

    return TA_SUCCESS;
}
