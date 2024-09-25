package com.servicenow.processmining.extensions.pm.entities;

import com.servicenow.processmining.extensions.sn.entities.ServiceNowEntity;

public class ProcessMiningModelVersionFilter
    extends ServiceNowEntity
{
    private String name = null;
    private String condition = null;
    private String entityId = null;
    private String projectId = null;
    private int caseFrequency = -1;
    private int variantCount = -1;
    private int totalDuration = -1;
    private int maxDuration = -1;
    private int minDuration = -1;
    private int avgDuration = -1;
    private int medianDuration = -1;
    private int stdDeviation = -1;

    public ProcessMiningModelVersionFilter(ProcessMiningModelVersionFilterPK pk)
    {
        super(pk);
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    public void setEntityId(final String entityId)
    {
        this.entityId = entityId;
    }

    public String getEntityId()
    {
        return this.entityId;
    }

    public void setCondition(final String condition)
    {
        this.condition = condition;
    }

    public String getCondition()
    {
        return this.condition;
    }

    public String getJSONCondition()
    {
        return getCondition().replaceAll("AND", ",");
    }

    public void setProjectId(final String projectId)
    {
        this.projectId = projectId;
    }

    public String getProjectId()
    {
        return this.projectId;
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
        return "[ProcessMiningModelFilter: (" + getPK().toString() + "), Name: '" + getName() + "'', EntityId: '" + getEntityId() + "', Condition: '" + getCondition() + "', ProjectId: '" + getProjectId() + "', Records: '" + getCaseFrequency() + "', Routes: '" + getVariantCount() + "', Avg: '" + getAvgDuration() +"', Median: '" + getMedianDuration() + "', Std Dev: '" + getStdDeviation() + "']";
    }
}