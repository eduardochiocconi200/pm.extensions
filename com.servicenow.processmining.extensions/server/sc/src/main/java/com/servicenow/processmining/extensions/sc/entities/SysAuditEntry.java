package com.servicenow.processmining.extensions.sc.entities;

import com.servicenow.processmining.extensions.sn.entities.ServiceNowEntity;

public class SysAuditEntry
    extends ServiceNowEntity
{
    private String tableName = null;
    private String documentKey = null; // this is the workflow instance id (the case id in Process Mining terms).
    private String fieldName = "state"; // we will always map the state diagram.
    private String newValue = null; // cannot be null
    private String oldValue = null; // can be null
    private String user = "admin"; // we will pretend it is executed by the admin user.
    private String sysCreatedOn = null;
    private String sysCreatedBy = "system"; // use this default.
    private String reason = "";

    public SysAuditEntry(SysAuditEntryPK pk)
    {
        super(pk);
    }

    public void setTableName(final String name)
    {
        this.tableName = name;
    }

    public String getTableName()
    {
        return this.tableName;
    }

    public void setDocumentKey(final String key)
    {
        this.documentKey = key;
    }

    public String getDocumentKey()
    {
        return this.documentKey;
    }

    public void setFieldName(final String fieldName)
    {
        this.fieldName = fieldName;
    }

    public String getFieldName()
    {
        return this.fieldName;
    }

    public void setNewValue(final String value)
    {
        this.newValue = value;
    }

    public String getNewValue()
    {
        return this.newValue;
    }

    public void setOldValue(final String value)
    {
        this.oldValue = value;
    }

    public String getOldValue()
    {
        return this.oldValue;
    }

    public void setUser(final String user)
    {
        this.user = user;
    }

    public String getUser()
    {
        return this.user;
    }

    public void setSysCreatedOn(final String created)
    {
        this.sysCreatedOn = created;
    }

    public String getSysCreatedOn()
    {
        return this.sysCreatedOn;
    }

    public void setSysCreatedBy(final String createdBy)
    {
        this.sysCreatedBy = createdBy;
    }

    public String getSysCreatedBy()
    {
        return this.sysCreatedBy;
    }

    public void setReason(final String reason)
    {
        this.reason = reason;
    }

    public String getReason()
    {
        return this.reason;
    }

    public String toJSON()
    {
        return "{" +
               "\"" + "reason" + "\":\"" + getReason() + "\"" +
               "," +
               "\"" + "tablename" + "\":\"" + getTableName() + "\"" +
               "," +
               "\"" + "documentkey" + "\":\"" + getDocumentKey() + "\"" +
               "," +
               "\"" + "fieldname" + "\":\"" + getFieldName() + "\"" +
               "," +
               "\"" + "oldvalue" + "\":\"" + getOldValue() + "\"" +
               "," +
               "\"" + "newvalue" + "\":\"" + getNewValue() + "\"" +
               "," +
               "\"" + "user" + "\":\"" + getUser() + "\"" +
               "," +
               "\"" + "sys_created_on" + "\":\"" + getSysCreatedOn() + "\"" +
               "," +
               "\"" + "sys_created_by" + "\":\"" + getSysCreatedBy() + "\"" +
               "}";
    }

    public String toString()
    {
        return "[SysAuditEntry: (" + getDocumentKey() + ", " + getTableName() + ", " + getFieldName() + ", " + getOldValue() + ", " + getNewValue() + ", " + getUser() + ", " + getSysCreatedOn() + ", " + getSysCreatedBy() + ")]";
    }
}