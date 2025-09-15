package com.servicenow.processmining.extensions.pm.model;

import java.io.Serializable;

public class ProcessMiningModelNode
    implements Serializable
{
    private String id = null;
    private String name = null;
    private String activityId = null;
    private String entityId = null;
    private boolean isStart = false;
    private boolean isEnd = false;
    private int absoluteFreq = -1;
    private int caseFreq = -1;
    private int maxReps = -1;
    private String fieldLabel = null;
    private String field = null;
    private String value = null;
    private ProcessMiningModelResources resources = null;
    
    public ProcessMiningModelNode(final String id, final String name)
    {
        this.id = id;
        this.name = name;
    }

    public String getId()
    {
        return this.id;
    }
    
    public String getName()
    {
        return this.name;
    }

    public void setActivityId(final String activityId)
    {
        this.activityId = activityId;
    }

    public String getActivityId()
    {
        return this.activityId;
    }

    public void setEntityId(final String entityId)
    {
        this.entityId = entityId;
    }

    public String getEntityId()
    {
        return this.entityId;
    }

    public void setIsStart(final boolean isStart)
    {
        this.isStart = isStart;
    }

    public boolean getIsStart()
    {
        return this.isStart;
    }

    public void setIsEnd(final boolean isEnd)
    {
        this.isEnd = isEnd;
    }

    public boolean getIsEnd()
    {
        return this.isEnd;
    }

    public void setAbsoluteFrequency(final int absoluteFreq)
    {
        this.absoluteFreq = absoluteFreq;
    }

    public int getAbsoluteFrequency()
    {
        return this.absoluteFreq;
    }

    public void setCaseFrequency(final int caseFreq)
    {
        this.caseFreq = caseFreq;
    }

    public int getCaseFrequency()
    {
        return this.caseFreq;
    }

    public void setMaxReps(final int maxReps)
    {
        this.maxReps = maxReps;
    }

    public int getMaxReps()
    {
        return this.maxReps;
    }

    public void setFieldLabel(final String fieldLabel)
    {
        this.fieldLabel = fieldLabel;
    }

    public String getFieldLabel()
    {
        return this.fieldLabel;
    }

    public void setField(final String field)
    {
        this.field = field;
    }

    public String getField()
    {
        return this.field;
    }

    public void setValue(final String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return this.value;
    }


    public void setResources(final ProcessMiningModelResources resources)
    {
        this.resources = resources;
    }

    public ProcessMiningModelResources getResources()
    {
        return this.resources;
    }

    public String toString()
    {
        return "[Node: Id: '" + getId() + "', Name: '" + getName() + "', Activity: '" + getActivityId() + "', Entity: '" + getEntityId() + "', Start: '" + getIsStart() + "', End: '" + getIsEnd() + "', AbsoluteFreq: '" + getAbsoluteFrequency() + "', CaseFreq: '" + getCaseFrequency() + "', MaxReps: '" + getMaxReps() + "', FieldLabel: '" + getFieldLabel() + "', Field: '" + getField() + "', Value: '" + getValue() + "']";
    }
}
