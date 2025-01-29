package com.servicenow.processmining.extensions.sn.entities;

import java.io.Serializable;

public abstract class ServiceNowEntity
    implements Serializable
{
    private PrimaryKey key = null;

    public ServiceNowEntity()
    {
    }

    public ServiceNowEntity(final PrimaryKey pk)
    {
        this.key = pk;
    }

    public void setPK(final PrimaryKey pk)
    {
        this.key = pk;
    }

    public PrimaryKey getPK()
    {
        return this.key;
    }

    private static final long serialVersionUID = 1L;
}