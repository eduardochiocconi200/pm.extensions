package com.servicenow.processmining.extensions.sc.entities;

import com.servicenow.processmining.extensions.sn.entities.PrimaryKey;

public class SysAuditLogPK
    extends PrimaryKey
{
    private String tableName = null;

    public SysAuditLogPK(final String tableName)
    {
        super();
        this.tableName = tableName;
    }

    public String getTableName()
    {
        return this.tableName;
    }

    public boolean equals(Object other)
    {
        if (this == other) {
            return true;
        }

        if (!(other instanceof SysAuditLogPK)) {
            return false;
        }
        SysAuditLogPK castOther = (SysAuditLogPK) other;

        return this.tableName.equals(castOther.tableName);
    }

    public int hashCode()
    {
        final int prime = 31;
        int hash = 17;
        hash = hash * prime + this.tableName.hashCode();
        return hash;
    }

    public String toString()
    {
        return this.tableName;
    }
}