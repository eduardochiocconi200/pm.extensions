package com.servicenow.processmining.extensions.pm.model;

public class ProcessMiningModelEntity
{
    private String tableId = null; // Entity Id
    private String tableName = null;
    private String fieldId = null;
    private String fieldName = null;

    public ProcessMiningModelEntity(final String tableId, final String tableName, final String fieldId, final String fieldName)
    {
        this.tableId = tableId;
        this.tableName = tableName;
        this.fieldId = fieldId;
        this.fieldName = fieldName;
    }

    public String getTableId()
    {
        return this.tableId;
    }

    public String getTableName()
    {
        return this.tableName;
    }

    public String getFieldId()
    {
        return this.fieldId;
    }

    public String getFieldName()
    {
        return this.fieldName;
    }

    public String toString()
    {
        return "[Entity: Table Id: (" + getTableId() + "), Table Name: (" + getTableName() + ") Field Id: (" + getFieldId() + "), Field Name: (" + getFieldName() + ")]";
    }
}
