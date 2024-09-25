package com.servicenow.processmining.extensions.sc.entities;

import com.servicenow.processmining.extensions.sn.entities.ServiceNowEntity;

public class ServiceCatalogItem
    extends ServiceNowEntity
{
    private String name = null;
    private String workflowId = null;
    private String flowDesignerId = null;
    private String shortDescription = null;
    private String roles = null;
    private String scCatalogs = null;

    public ServiceCatalogItem(ServiceCatalogItemPK pk)
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

    public void setWorkflowId(final String wId)
    {
        this.workflowId = wId;
    }

    public String getWorkflowId()
    {
        return this.workflowId;
    }

    public void setFlowDesignerId(final String flowDesignerId)
    {
        this.flowDesignerId = flowDesignerId;
    }

    public String getFlowDesignerId()
    {
        return this.flowDesignerId;
    }

    public void setShortDescription(final String description)
    {
        this.shortDescription = description;
    }

    public String getShortDescription()
    {
        return this.shortDescription;
    }

    public void setRoles(final String roles)
    {
        this.roles = roles;
    }

    public String getRoles()
    {
        return this.roles;
    }

    public void setSCCatalogs(final String scCatalogs)
    {
        this.scCatalogs = scCatalogs;
    }

    public String getSCCatalogs()
    {
        return this.scCatalogs;
    }

    public String toString()
    {
        return "[ServiceCatalogItem: (" + getPK().toString() + ", " + getName() + ", " + getWorkflowId() + ", " + getFlowDesignerId() + ", " + getShortDescription() + ", " + getRoles() + ", " + getSCCatalogs() + ")]";
    }
}
