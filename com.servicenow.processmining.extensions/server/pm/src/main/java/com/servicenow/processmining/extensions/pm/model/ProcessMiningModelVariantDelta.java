package com.servicenow.processmining.extensions.pm.model;

public class ProcessMiningModelVariantDelta
{
    private ProcessMiningModelVariant variant1 = null;
    private ProcessMiningModelVariant variant2 = null;

    public ProcessMiningModelVariantDelta(final ProcessMiningModelVariant v1, final ProcessMiningModelVariant v2)
    {
        this.variant1 = v1;
        this.variant2 = v2;
    }

    public ProcessMiningModelVariant getVariant1()
    {
        return this.variant1;
    }

    public ProcessMiningModelVariant getVariant2()
    {
        return this.variant2;
    }

    public boolean isSlower()
    {
        return variant1.getAvgDuration() < variant2.getAvgDuration();
    }

    public boolean hasSameNumberOfSteps()
    {
        return variant1.getNodeCount() == variant2.getNodeCount();
    }

    public boolean hasLessSteps()
    {
        return variant1.getNodeCount() < variant2.getNodeCount();
    }

    public boolean isReworkPath()
    {
        return true;
    }    
}
