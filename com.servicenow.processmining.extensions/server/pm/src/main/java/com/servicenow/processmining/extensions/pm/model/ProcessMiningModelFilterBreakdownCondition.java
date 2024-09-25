package com.servicenow.processmining.extensions.pm.model;

public class ProcessMiningModelFilterBreakdownCondition
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
}