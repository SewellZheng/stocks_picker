int floor_lookback(void)
{
    return 0;
}

TA_RetCode floor(int startIdx, int endIdx, const double inReal[], int *outBegIdx, int *outNBElement, double outReal[])
{
    int outIdx;
    int i;



    for( i=startIdx, outIdx=0; i <= endIdx; i++, outIdx++ )
    {
    outReal[outIdx] = floor(inReal[i]);
    }

    *outNBElement = outIdx;
    *outBegIdx    = startIdx;

    return TA_SUCCESS;
}
