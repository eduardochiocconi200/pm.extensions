package com.servicenow.processmining.extensions.sc.entities;

import com.servicenow.processmining.extensions.sn.entities.ServiceNowEntity;

public class WorkflowVersion
    extends ServiceNowEntity
{
    private String name = null;
    private String description = null;
    private String table = null;
    private String workflowId = null;
    private String createdOn = null;
    private String lastUpdatedOn = null;

    public WorkflowVersion(WorkflowVersionPK pk)
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

    public void setDescription(final String desc)
    {
        this.description = desc;
    }

    public String getDescription()
    {
        return this.description;
    }

    public void setTable(final String table)
    {
        this.table = table;
    }

    public String getTable()
    {
        return this.table;
    }

    public void setWorkflowId(final String wfId)
    {
        this.workflowId = wfId;
    }

    public String getWorkflowId()
    {
        return this.workflowId;
    }

    public void setCreatedOn(final String created)
    {
        this.createdOn = created;
    }

    public String getCreatedOn()
    {
        return this.createdOn;
    }

    public void setLastUpdatedOn(final String lastUpdatedOn)
    {
        this.lastUpdatedOn = lastUpdatedOn;
    }

    public String getLastUpdatedOn()
    {
        return this.lastUpdatedOn;
    }

    public String toString()
    {
        return "[WorkflowVersion: (" + getPK().toString() + ", " + getName() + ", " + getDescription() + ", " + getTable() + ", " + getWorkflowId() + ", " + getCreatedOn() + ", " + getLastUpdatedOn() + ")]";
    }
}
