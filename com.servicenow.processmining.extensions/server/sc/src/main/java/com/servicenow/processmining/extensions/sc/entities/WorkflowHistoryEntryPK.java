package com.servicenow.processmining.extensions.sc.entities;

import com.servicenow.processmining.extensions.sn.entities.PrimaryKey;

public class WorkflowHistoryEntryPK
    extends PrimaryKey
{
    private String sysId = null;

    public WorkflowHistoryEntryPK(final String pk)
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

        if (!(other instanceof WorkflowHistoryEntryPK)) {
            return false;
        }
        WorkflowHistoryEntryPK castOther = (WorkflowHistoryEntryPK) other;

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