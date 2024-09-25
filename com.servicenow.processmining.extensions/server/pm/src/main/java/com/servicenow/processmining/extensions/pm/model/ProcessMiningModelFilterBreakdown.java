package com.servicenow.processmining.extensions.pm.model;

import java.util.ArrayList;

public class ProcessMiningModelFilterBreakdown
{
    private String entityId = null;
    private ArrayList<ProcessMiningModelFilterBreakdownCondition> conditions = null;

    public ProcessMiningModelFilterBreakdown()
    {
    }

    public void setEntityId(final String entityId)
    {
        this.entityId = entityId;
    }

    public String getEntityId()
    {
        return this.entityId;
    }

    public void setConditions(final ArrayList<ProcessMiningModelFilterBreakdownCondition> conditions)
    {
        this.conditions = conditions;
    }

    public boolean addCondition(final ProcessMiningModelFilterBreakdownCondition condition)
    {
        if (conditions == null) {
            conditions = new ArrayList<ProcessMiningModelFilterBreakdownCondition>();
        }

        return conditions.add(condition);
    }

    public ArrayList<ProcessMiningModelFilterBreakdownCondition> getConditions()
    {
        return this.conditions;
    }
}