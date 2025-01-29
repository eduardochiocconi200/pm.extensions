package com.servicenow.processmining.extensions.pm.analysis.goldratt;

import java.util.ArrayList;

public class ValueStreamPhase
{
    private String name = null;
    private ArrayList<String> nodes = null;
    private ValueStreamPhaseStatistics statistics = null;

    public ValueStreamPhase()
    {
    }

    public ValueStreamPhase(final String name)
    {
        this.name = name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    public ArrayList<String> getNodes()
    {
        if (this.nodes == null) {
            this.nodes = new ArrayList<String>();
        }

        return this.nodes;
    }

    public ValueStreamPhaseStatistics getStatistics()
    {
        if (this.statistics == null) {
            this.statistics = new ValueStreamPhaseStatistics();
        }

        return this.statistics;
    }
}
