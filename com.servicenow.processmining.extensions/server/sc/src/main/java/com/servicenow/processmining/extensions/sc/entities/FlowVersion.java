package com.servicenow.processmining.extensions.sc.entities;

import com.servicenow.processmining.extensions.sn.entities.ServiceNowEntity;

public class FlowVersion
    extends ServiceNowEntity
{
    private String name = null;
    private String description = null;
    private String type = null;
    private String parentFlowId = null;
    private String createdOn = null;
    private String lastUpdatedOn = null;

    public FlowVersion(FlowVersionPK pk)
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

    public void setType(final String type)
    {
        this.type = type;
    }

    public String getType()
    {
        return this.type;
    }

    public void setParentFlowId(final String parentFlowId)
    {
        this.parentFlowId = parentFlowId;
    }

    public String getParentFlowId()
    {
        return this.parentFlowId;
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
        return "[FlowVersion: (" + getPK().toString() + ", " + getName() + ", " + getDescription() + ", " + getType() + ", " + getParentFlowId() + ", " + getCreatedOn() + ", " + getLastUpdatedOn() + ")]";
    }
}
