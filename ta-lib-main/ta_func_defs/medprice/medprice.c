int medprice_lookback(void)
{
    /* This function have no lookback needed. */
    return 0;
}

TA_RetCode medprice(int startIdx, int endIdx, const double inHigh[], const double inLow[], int *outBegIdx, int *outNBElement, double outReal[])
{
    int outIdx, i;



    /* MEDPRICE = (High + Low ) / 2
    * This is the high and low of the same price bar.
    *
    * See MIDPRICE to use instead the highest high and lowest
    * low over multiple price bar.
    */

    outIdx = 0;

    for( i=startIdx; i <= endIdx; i++ )
    {
    outReal[outIdx++] = (inHigh[i]+inLow[i])/2.0;
    }

    *outNBElement = outIdx;
    *outBegIdx    = startIdx;

    return TA_SUCCESS;
}
