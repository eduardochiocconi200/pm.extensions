package com.servicenow.processmining.extensions.pm.model;

public class ProcessMiningModelAggregate
{
    private int caseCount = -1;
    private int variantCount = -1;
    private int minCaseDuration = -1;
    private int maxCaseDuration = -1;
    private int avgCaseDuration = -1;
    private int stdDeviation = -1;
    private int medianDuration = -1;

    public ProcessMiningModelAggregate()
    {
    }

    public void setCaseCount(final int caseCount)
    {
        this.caseCount = caseCount;
    }

    public int getCaseCount()
    {
        return this.caseCount;
    }

    public void setVariantCount(final int variantCount)
    {
        this.variantCount = variantCount;
    }

    public int getVariantCount()
    {
        return this.variantCount;
    }

    public void setMinCaseDuration(final int minDuration)
    {
        this.minCaseDuration = minDuration;
    }

    public int getMinCaseDuration()
    {
        return this.minCaseDuration;
    }

    public void setMaxCaseDuration(final int maxDuration)
    {
        this.maxCaseDuration = maxDuration;
    }

    public int getMaxCaseDuration()
    {
        return this.maxCaseDuration;
    }
    
    public void setAvgCaseDuration(final int avgDuration)
    {
        this.avgCaseDuration = avgDuration;
    }

    public int getAvgCaseDuration()
    {
        return this.avgCaseDuration;
    }

    public void setStdDeviation(final int stdDeviation)
    {
        this.stdDeviation = stdDeviation;
    }

    public int getStdDeviation()
    {
        return this.stdDeviation;
    }

    public void setMedianDuration(final int medianDuration)
    {
        this.medianDuration = medianDuration;
    }

    public int getMedianDuration()
    {
        return this.medianDuration;
    }
}
