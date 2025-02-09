package com.servicenow.processmining.extensions.pm.model;

import java.io.Serializable;

public class ProcessMiningModelFilter
    implements Serializable
{
    private String id = null;
    private String name = null;
    private ProcessMiningModelFilterBreakdown filterBreakdown = null;
    private ProcessMiningModelFilterTransitions filterTransitions = null;
    private int caseFrequency = -1;
    private int variantCount = -1;
    private int totalDuration = -1;
    private int maxDuration = -1;
    private int minDuration = -1;
    private int avgDuration = -1;
    private int medianDuration = -1;
    private int stdDeviation = -1;

    public ProcessMiningModelFilter()
    {
    }

    public void setId(final String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return this.id;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    public void setFilterBreakdown(final ProcessMiningModelFilterBreakdown condition)
    {
        this.filterBreakdown = condition;
    }

    public ProcessMiningModelFilterBreakdown getFilterBreakdown()
    {
        return this.filterBreakdown;
    }

    public String getCondition()
    {
        StringBuffer sb = new StringBuffer();
        boolean processedFirst = false;
        if (getFilterBreakdown().getConditions() != null) {
            for (ProcessMiningModelFilterBreakdownCondition bd : getFilterBreakdown().getConditions()) {
                if (processedFirst) {
                    sb.append(" AND ");
                }
                sb.append(bd.getCondition());
                processedFirst = true;
            }
        }
        else {
            sb.append("<empty>");
        }

        return sb.toString();
    }

    public String getBreakdownConditionJSON()
    {
        String json = "";
        if (this.filterBreakdown != null) {
            json = this.filterBreakdown.getFilterBreakdownJSON();
        }

        return json;
    }

    public void setFilterTransitions(ProcessMiningModelFilterTransitions transitionsFilter)
    {
        this.filterTransitions = transitionsFilter;
    }

    public ProcessMiningModelFilterTransitions getFilterTransitions()
    {
        return this.filterTransitions;
    }

    public String getFilterTransitionsJSON()
    {
        String json = "";
        if (this.filterTransitions != null) {
            json = this.filterTransitions.getFilterTransitionsJSON();
        }

        return json;
    }

    public void setCaseFrequency(final int freq)
    {
        this.caseFrequency = freq;
    }

    public int getCaseFrequency()
    {
        return this.caseFrequency;
    }

    public void setVariantCount(final int count)
    {
        this.variantCount = count;
    }

    public int getVariantCount()
    {
        return this.variantCount;
    }

    public void setTotalDuration(final int total)
    {
        this.totalDuration = total;
    }

    public int getTotalDuration()
    {
        return this.totalDuration;
    }

    public void setMaxDuration(final int maxDuration)
    {
        this.maxDuration = maxDuration;
    }

    public int getMaxDuration()
    {
        return this.maxDuration;
    }

    public void setMinDuration(final int minDuration)
    {
        this.minDuration = minDuration;
    }

    public int getMinDuration()
    {
        return this.minDuration;
    }

    public void setAvgDuration(final int avgDuration)
    {
        this.avgDuration = avgDuration;
    }

    public int getAvgDuration()
    {
        return this.avgDuration;
    }

    public void setMedianDuration(final int medianDuration)
    {
        this.medianDuration = medianDuration;
    }

    public int getMedianDuration()
    {
        return this.medianDuration;
    }

    public void setStdDeviation(final int stdDeviation)
    {
        this.stdDeviation = stdDeviation;
    }

    public int getStdDeviation()
    {
        return this.stdDeviation;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        sb.append("[ Id: (" + id + "), Name: (" + name + "), caseFrequency: (" + caseFrequency + "), variantCount: (" + variantCount + "), totalDuration: (" + totalDuration + "),");
        sb.append(" MaxDuration: (" + maxDuration + "), MinDuration: (" + minDuration + "), AvgDuration: (" + avgDuration + "), medianDuration: (" + medianDuration + "), stdDev: (" + stdDeviation + "),\n");
        sb.append(" Filter Breakdown: (" + filterBreakdown + "),\n");
        sb.append(" Filter Transitions: (" + filterTransitions + ")]\n");

        return sb.toString();
    }
}
