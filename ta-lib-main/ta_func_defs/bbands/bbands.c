int bbands_lookback(int           optInTimePeriod,                                               double        optInNbDevUp,                                               double        optInNbDevDn,                                               TA_MAType     optInMAType)
{
    (void)optInNbDevUp;
    (void)optInNbDevDn;
    
    /* The lookback is driven by the middle band moving average. */
    return ma_lookback( optInTimePeriod, optInMAType );
}

TA_RetCode bbands(int startIdx, int endIdx, const double inReal[], int optInTimePeriod, double optInNbDevUp, double optInNbDevDn, TA_MAType optInMAType, int *outBegIdx, int *outNBElement, double outRealUpperBand[], double outRealMiddleBand[], double outRealLowerBand[])
{
    TA_RetCode retCode;
    int i;
    double tempReal, tempReal2;
    double *tempBuffer1;
    double *tempBuffer2;



    /* Identify TWO temporary buffer among the outputs.
    *
    * These temporary buffers allows to perform the
    * calculation without any memory allocation.
    *
    * Whenever possible, make the tempBuffer1 be the
    * middle band output. This will save one copy operation.
    */
    if( inReal == outRealUpperBand )
    {
    tempBuffer1 = outRealMiddleBand;
    tempBuffer2 = outRealLowerBand;
    }
    else if( inReal == outRealLowerBand )
    {
    tempBuffer1 = outRealMiddleBand;
    tempBuffer2 = outRealUpperBand;
    }
    else if( inReal == outRealMiddleBand )
    {
    tempBuffer1 = outRealLowerBand;
    tempBuffer2 = outRealUpperBand;
    }
    else
    {
    tempBuffer1 = outRealMiddleBand;
    tempBuffer2 = outRealUpperBand;
    }
    /* Check that the caller is not doing tricky things.
    * (like using the input buffer in two output!)
    */
    if( (tempBuffer1 == inReal) || (tempBuffer2 == inReal) )
    return TA_BAD_PARAM;


    /* Calculate the middle band, which is a moving average.
    * The other two bands will simply add/substract the
    * standard deviation from this middle band.
    */
    retCode = ma( startIdx, endIdx, inReal,
    optInTimePeriod, optInMAType,
    outBegIdx, outNBElement, tempBuffer1 );

    if( (retCode != TA_SUCCESS ) || ((int)*outNBElement == 0) )
    {
    *outNBElement = 0;
    return retCode;
    }

    /* Calculate the standard deviation into tempBuffer2. */
    if( optInMAType == TA_MAType_SMA )
    {
    /* A small speed optimization by re-using the
    * already calculated SMA.
    */
    /* Inline stddev_using_precalc_ma */
    {
    double _tempReal, _periodTotal2, _meanValue2;
    int _outIdx;
    int _startSum, _endSum;
    _startSum = 1 + (int)*outBegIdx - optInTimePeriod;
    _endSum = (int)*outBegIdx;
    _periodTotal2 = 0;
    for( _outIdx = _startSum; _outIdx < _endSum; _outIdx++ )
    {
    _tempReal = inReal[_outIdx];
    _tempReal *= _tempReal;
    _periodTotal2 += _tempReal;
    }
    for( _outIdx = 0; _outIdx < (int)*outNBElement; _outIdx++, _startSum++, _endSum++ )
    {
    _tempReal = inReal[_endSum];
    _tempReal *= _tempReal;
    _periodTotal2 += _tempReal;
    _meanValue2 = _periodTotal2 / optInTimePeriod;
    _tempReal = inReal[_startSum];
    _tempReal *= _tempReal;
    _periodTotal2 -= _tempReal;
    _tempReal = tempBuffer1[_outIdx];
    _tempReal *= _tempReal;
    _meanValue2 -= _tempReal;
    if( !TA_IS_ZERO_OR_NEG(_meanValue2) )
    tempBuffer2[_outIdx] = sqrt(_meanValue2);
    else
    tempBuffer2[_outIdx] = 0.0;
    }
    }
    }
    else
    {
    /* Calculate the Standard Deviation */
    retCode = stddev( (int)*outBegIdx, endIdx, inReal,
    optInTimePeriod, 1.0,
    outBegIdx, outNBElement, tempBuffer2 );

    if( retCode != TA_SUCCESS )
    {
    *outNBElement = 0;
    return retCode;
    }
    }

    /* Copy the MA calculation into the middle band ouput, unless
    * the calculation was done into it already!
    */
    if( tempBuffer1 != outRealMiddleBand )
    {
    memcpy(outRealMiddleBand, tempBuffer1, (*outNBElement) * sizeof(double));
    }

    /* Now do a tight loop to calculate the upper/lower band at
    * the same time.
    *
    * All the following 5 loops are doing the same, except there
    * is an attempt to speed optimize by eliminating uneeded
    * multiplication.
    */
    if( optInNbDevUp == optInNbDevDn )
    {
    if(  optInNbDevUp == 1.0 )
    {
    /* No standard deviation multiplier needed. */
    for( i=0; i < (int)*outNBElement; i++ )
    {
    tempReal  = tempBuffer2[i];
    tempReal2 = outRealMiddleBand[i];
    outRealUpperBand[i] = tempReal2 + tempReal;
    outRealLowerBand[i] = tempReal2 - tempReal;
    }
    }
    else
    {
    /* Upper/lower band use the same standard deviation multiplier. */
    for( i=0; i < (int)*outNBElement; i++ )
    {
    tempReal  = tempBuffer2[i] * optInNbDevUp;
    tempReal2 = outRealMiddleBand[i];
    outRealUpperBand[i] = tempReal2 + tempReal;
    outRealLowerBand[i] = tempReal2 - tempReal;
    }
    }
    }
    else if( optInNbDevUp == 1.0 )
    {
    /* Only lower band has a standard deviation multiplier. */
    for( i=0; i < (int)*outNBElement; i++ )
    {
    tempReal  = tempBuffer2[i];
    tempReal2 = outRealMiddleBand[i];
    outRealUpperBand[i] = tempReal2 + tempReal;
    outRealLowerBand[i] = tempReal2 - (tempReal * optInNbDevDn);
    }
    }
    else if( optInNbDevDn == 1.0 )
    {
    /* Only upper band has a standard deviation multiplier. */
    for( i=0; i < (int)*outNBElement; i++ )
    {
    tempReal  = tempBuffer2[i];
    tempReal2 = outRealMiddleBand[i];
    outRealLowerBand[i] = tempReal2 - tempReal;
    outRealUpperBand[i] = tempReal2 + (tempReal * optInNbDevUp);
    }
    }
    else
    {
    /* Upper/lower band have distinctive standard deviation multiplier. */
    for( i=0; i < (int)*outNBElement; i++ )
    {
    tempReal  = tempBuffer2[i];
    tempReal2 = outRealMiddleBand[i];
    outRealUpperBand[i] = tempReal2 + (tempReal * optInNbDevUp);
    outRealLowerBand[i] = tempReal2 - (tempReal * optInNbDevDn);
    }
    }

    return TA_SUCCESS;
}
