int obv_lookback(void)
{
    /* This function have no lookback needed. */
    return 0;
}

TA_RetCode obv(int startIdx, int endIdx, const double inReal[], const double inVolume[], int *outBegIdx, int *outNBElement, double outReal[])
{
    int i;
    int outIdx;
    double prevReal, tempReal, prevOBV;


    prevOBV  = inVolume[startIdx];
    prevReal = inReal[startIdx];
    outIdx = 0;

    for(i=startIdx; i <= endIdx; i++ )
    {
    tempReal = inReal[i];
    if( tempReal > prevReal )
    prevOBV += inVolume[i];
    else if( tempReal < prevReal )
    prevOBV -= inVolume[i];

    outReal[outIdx++] = prevOBV;
    prevReal = tempReal;
    }

    *outBegIdx = startIdx;
    *outNBElement = outIdx;

    return TA_SUCCESS;
}
