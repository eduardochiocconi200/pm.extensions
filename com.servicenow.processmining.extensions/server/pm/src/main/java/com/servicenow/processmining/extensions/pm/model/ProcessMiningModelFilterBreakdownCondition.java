package com.servicenow.processmining.extensions.pm.model;

import java.io.Serializable;

public class ProcessMiningModelFilterBreakdownCondition
    implements Serializable
{
    private String condition = null;

    public ProcessMiningModelFilterBreakdownCondition()
    {
    }

    public void setCondition(final String condition)
    {
        this.condition = condition;
    }

    public String getCondition()
    {
        return this.condition;
    }

    public String getConditionJSON()
    {
        return "{ \"condition\" : \"" + condition + "\" }";
    }

    public String toString()
    {
        return "[ Condition: (" + condition + ") ]";
    }
}