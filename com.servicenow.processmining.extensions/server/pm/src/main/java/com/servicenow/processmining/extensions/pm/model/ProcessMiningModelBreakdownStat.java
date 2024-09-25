package com.servicenow.processmining.extensions.pm.model;

public class ProcessMiningModelBreakdownStat
{
    private String label = null;
    private String value = null;
    private int caseCount = -1;
    private int variantCount = -1;
    private int avgDuration = -1;
    private int stdDeviation = -1;
    private int medianDuration = -1;
    private boolean highVolume = false;
    private boolean highVariability = false;

    public ProcessMiningModelBreakdownStat()
    {    
    }

    public void setLabel(final String label)
    {
        this.label = label;
    }

    public String getLabel()
    {
        return this.label;
    }

    public void setValue(final String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return this.value;
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

    public void setAvgDuration(final int avgDuration)
    {
        this.avgDuration = avgDuration;
    }

    public int getAvgDuration()
    {
        return this.avgDuration;
    }

    public void setStdDuration(final int stdDeviation)
    {
        this.stdDeviation = stdDeviation;
    }

    public int getStdDuration()
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

    public void setHighVolume(final boolean highVolume)
    {
        this.highVolume = highVolume;
    }

    public boolean getHighVolume()
    {
        return this.highVolume;
    }

    public void setHighVariability(final boolean highVariability)
    {
        this.highVariability = highVariability;
    }

    public boolean getHighVariability()
    {
        return this.highVariability;
    }

    public String getVariantToCaseCountRatioScore()
    {
        String score = "";
        score = (getHighVolume() ? "High" : "Low") + "Volume with ";
        score += (getHighVariability() ? "High" : "Low") + "Variability";

        return score;
    }

    public String toString()
    {
        return "[BreakdownStat: Label: '" + getLabel() + "', Case Count: '" + getCaseCount() + "', Variations: '" + getVariantCount() + "', Score: '" + getVariantToCaseCountRatioScore() + "']";
    }
}
