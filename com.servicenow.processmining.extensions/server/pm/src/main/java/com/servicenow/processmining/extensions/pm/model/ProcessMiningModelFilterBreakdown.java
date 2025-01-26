package com.servicenow.processmining.extensions.pm.model;

import java.io.Serializable;
import java.util.ArrayList;

public class ProcessMiningModelFilterBreakdown
    implements Serializable
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

        return getConditions().add(condition);
    }

    public ArrayList<ProcessMiningModelFilterBreakdownCondition> getConditions()
    {
        if (conditions == null) {
            conditions = new ArrayList<ProcessMiningModelFilterBreakdownCondition>();
        }

        return this.conditions;
    }

    public String getFilterBreakdownJSON()
    {
        String json = " { \"entityId\" : \"" + entityId + "\", [ ";

        boolean processedFirst = false;
        for (ProcessMiningModelFilterBreakdownCondition c : conditions) {
            if (processedFirst) {
                json += ", ";
            }
            json += c.getConditionJSON();

            processedFirst = true;
        }

        json += "]";
        json += "}";

        return json;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("[ entityId: (" + entityId + "), conditions: (" + conditions + ")]");
        return sb.toString();
    }
}
