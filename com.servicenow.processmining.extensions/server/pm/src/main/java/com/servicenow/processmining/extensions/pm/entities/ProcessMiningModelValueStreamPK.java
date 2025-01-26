package com.servicenow.processmining.extensions.pm.entities;

import com.servicenow.processmining.extensions.sn.entities.PrimaryKey;

public class ProcessMiningModelValueStreamPK
    extends PrimaryKey
{
    private String sysId = null;

    public ProcessMiningModelValueStreamPK(final String pk)
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

        if (!(other instanceof ProcessMiningModelValueStreamPK)) {
            return false;
        }
        ProcessMiningModelValueStreamPK castOther = (ProcessMiningModelValueStreamPK) other;

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