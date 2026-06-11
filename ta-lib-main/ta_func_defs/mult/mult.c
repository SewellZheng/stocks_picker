int mult_lookback(void)
{
    return 0;
}

TA_RetCode mult(int startIdx, int endIdx, const double inReal0[], const double inReal1[], int *outBegIdx, int *outNBElement, double outReal[])
{
    int outIdx;
    int i;

    outIdx = 0;
    i = startIdx;
    while( i <= endIdx ) {
    outReal[outIdx] = (double)(inReal0[i]*inReal1[i]);
    outIdx += 1;
    i += 1;
    }

    *outNBElement = outIdx;
    *outBegIdx    = startIdx;

    return TA_SUCCESS;
}
