int wclprice_lookback(void)
{
    /* This function have no lookback needed. */
    return 0;
}

TA_RetCode wclprice(int startIdx, int endIdx, const double inHigh[], const double inLow[], const double inClose[], int *outBegIdx, int *outNBElement, double outReal[])
{
    int outIdx, i;



    /* Weighted Close Price = (High + Low + (Close*2) ) / 4 */

    outIdx = 0;

    for( i= startIdx; i <= endIdx; i++ )
    {
    outReal[outIdx++] = ( inHigh [i] +
    inLow  [i] +
    (inClose[i]*2.0) ) / 4.0;
    }

    *outNBElement = outIdx;
    *outBegIdx    = startIdx;

    return TA_SUCCESS;
}
