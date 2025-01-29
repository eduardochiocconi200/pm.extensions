package com.servicenow.processmining.extensions.sc.entities;

import com.servicenow.processmining.extensions.sn.entities.PrimaryKey;

public class SysAuditLogPK
    extends PrimaryKey
{
    private String tableName = null;
    private String fieldName = null;
    private int resultSetSize = DEFAULT_RESULT_SET_SIZE;

    public SysAuditLogPK()
    {
        super();
        this.resultSetSize = DEFAULT_RESULT_SET_SIZE;
    }

    public SysAuditLogPK(final String tableName)
    {
        super();
        this.tableName = tableName;
        this.resultSetSize = DEFAULT_RESULT_SET_SIZE;
    }

    public SysAuditLogPK(final String tableName, final int resultSetSize)
    {
        super();
        this.tableName = tableName;
        this.resultSetSize = resultSetSize;
    }

    public SysAuditLogPK(final String tableName, final String fieldName)
    {
        super();
        this.tableName = tableName;
        this.fieldName = fieldName;
        this.resultSetSize = DEFAULT_RESULT_SET_SIZE;
    }

    public SysAuditLogPK(final String tableName, final String fieldName, final int resultSetSize)
    {
        super();
        this.tableName = tableName;
        this.fieldName = fieldName;
        this.resultSetSize = resultSetSize;
    }

    public void setTableName(final String tableName)
    {
        this.tableName = tableName;
    }

    public String getTableName()
    {
        return this.tableName;
    }

    public void setFieldName(final String fieldName)
    {
        this.fieldName = fieldName;
    }

    public String getFieldName()
    {
        return this.fieldName;
    }

    public void setResultSetSize(final int size)
    {
        this.resultSetSize = size;
    }

    public int getResultSetSize()
    {
        return this.resultSetSize;
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

        if (this.fieldName != null && castOther.fieldName != null) {
            return this.tableName.equals(castOther.tableName) && this.fieldName.equals(castOther.fieldName);
        }

        return this.tableName.equals(castOther.tableName);
    }

    public int hashCode()
    {
        final int prime = 31;
        int hash = 17;
        hash = hash * prime + this.tableName.hashCode();
        if (this.fieldName != null) {
            hash = hash * prime + this.fieldName.hashCode();
        }

        return hash;
    }

    public String toString()
    {
        if (this.fieldName != null) {
            return this.tableName + ":" + this.fieldName;
        }

        return this.tableName;
    }

    private static final int DEFAULT_RESULT_SET_SIZE = 10000;
}