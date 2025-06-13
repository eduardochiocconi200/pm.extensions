package com.servicenow.processmining.extensions.pm.simulation.roi;

public abstract class StatisticMetric
{
    private String name = null;

    public StatisticMetric(final String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }
}
