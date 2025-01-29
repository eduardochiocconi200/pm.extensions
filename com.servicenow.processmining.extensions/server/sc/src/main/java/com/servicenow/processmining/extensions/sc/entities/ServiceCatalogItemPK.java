package com.servicenow.processmining.extensions.sc.entities;

import com.servicenow.processmining.extensions.sn.entities.PrimaryKey;

public class ServiceCatalogItemPK
    extends PrimaryKey
{
    private String sysName = null;

    public ServiceCatalogItemPK()
    {
        super();
    }

    public ServiceCatalogItemPK(final String pk)
    {
        super();
        sysName = pk;
    }

    public void setSysName(final String sysName)
    {
        this.sysName = sysName;
    }

    public String getSysName()
    {
        return this.sysName;
    }

    public boolean equals(Object other)
    {
        if (this == other) {
            return true;
        }

        if (!(other instanceof ServiceCatalogItemPK)) {
            return false;
        }
        ServiceCatalogItemPK castOther = (ServiceCatalogItemPK) other;

        return this.sysName.equals(castOther.sysName);
    }

    public int hashCode()
    {
        final int prime = 31;
        int hash = 17;
        hash = hash * prime + this.sysName.hashCode();
        return hash;
    }

    public String toString()
    {
        return getSysName();
    }
}
