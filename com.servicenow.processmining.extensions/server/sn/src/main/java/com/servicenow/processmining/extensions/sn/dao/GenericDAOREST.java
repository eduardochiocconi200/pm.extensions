package com.servicenow.processmining.extensions.sn.dao;

import com.servicenow.processmining.extensions.sn.core.ServiceNowInstance;
import com.servicenow.processmining.extensions.sn.entities.PrimaryKey;
import com.servicenow.processmining.extensions.sn.entities.ServiceNowEntity;

public abstract class GenericDAOREST<SNEC extends ServiceNowEntity, ID extends PrimaryKey>
    implements GenericDAO<SNEC, ID>
{
    private ServiceNowInstance instance = null;

    public GenericDAOREST(final ServiceNowInstance instance)
    {
        this.instance = instance;
    }

    public ServiceNowInstance getInstance()
    {
        return this.instance;
    }
}
