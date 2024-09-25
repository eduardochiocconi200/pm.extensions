package com.servicenow.processmining.extensions.pm.entities;

import com.servicenow.processmining.extensions.sn.entities.PrimaryKey;

public class ProcessMiningModelVersionPK
    extends PrimaryKey
{
    private String sysId = null;

    public ProcessMiningModelVersionPK(final String pk)
    {
        super();
        sysId = pk;
    }

    public String getSysId()
    {
        return this.sysId;
    }

    public boolean equals(Object other)
    {
        if (this == other) {
            return true;
        }

        if (!(other instanceof ProcessMiningModelVersionPK)) {
            return false;
        }
        ProcessMiningModelVersionPK castOther = (ProcessMiningModelVersionPK) other;

        return this.sysId.equals(castOther.sysId);
    }

    public int hashCode()
    {
        final int prime = 31;
        int hash = 17;
        hash = hash * prime + this.sysId.hashCode();
        return hash;
    }

    public String toString()
    {
        return getSysId();
    }
}