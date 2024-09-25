package com.servicenow.processmining.extensions.sc.entities;

import com.servicenow.processmining.extensions.sn.entities.ServiceNowEntity;

public class WorkflowHistoryEntry
    extends ServiceNowEntity
{
    private String tableName = "wf_history";
    private String fieldName = "activity";
    private String workflowInstanceId = null;
    private String activityName = null;
    private String oldValue = null;
    private String newValue = null;
    private String index = null;
    private String sysCreatedOn = null;
    private String sysCreatedBy = "system";

    public WorkflowHistoryEntry(WorkflowHistoryEntryPK pk)
    {
        super(pk);
    }

    public String getTableName()
    {
        return this.tableName;
    }

    public String getFieldName()
    {
        return this.fieldName;
    }

    public void setWorkflowInstanceId(final String id)
    {
        this.workflowInstanceId = id;
    }

    public String getWorkflowInstanceId()
    {
        return this.workflowInstanceId;
    }

    public void setActivityName(final String name)
    {
        this.activityName = name;
    }

    public String getActivityName()
    {
        return this.activityName;
    }

    public void setOldValue(final String oldValue)
    {
        this.oldValue = oldValue;
    }

    public String getOldValue()
    {
        return this.oldValue;
    }

    public void setNewValue(final String newValue)
    {
        this.newValue = newValue;
    }

    public String getNewValue()
    {
        return this.newValue;
    }

    public void setIndex(final String index)
    {
        this.index = index;
    }

    public String getIndex()
    {
        return this.index;
    }

    public void setSysCreatedOn(final String createdOn)
    {
        this.sysCreatedOn = createdOn;
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

    public SysAuditEntry toSysAuditEntry()
    {
        SysAuditEntry entry = new SysAuditEntry(new SysAuditEntryPK(((WorkflowHistoryEntryPK) getPK()).getSysId()));
        entry.setTableName(WF_INSTANCE_TABLE);
        entry.setFieldName(WF_INSTANCE_STATE_ATTRIBUTE);
        entry.setDocumentKey(getWorkflowInstanceId());
        entry.setOldValue(getOldValue());
        entry.setNewValue(getNewValue());
        entry.setUser(getSysCreatedBy());
        entry.setSysCreatedOn(getSysCreatedOn());
        entry.setSysCreatedBy(getSysCreatedBy());

        return entry;
    }

    public String toString()
    {
        return "[WorkflowHistoryEntry: (" + getPK().toString() + ", " + getWorkflowInstanceId() + ", " + getActivityName() + ", " + getIndex() + ", " + getSysCreatedOn() + ", " + getSysCreatedBy() + ")]";
    }

    private static final String WF_INSTANCE_TABLE = "wf_context";
    private static final String WF_INSTANCE_STATE_ATTRIBUTE = "state";
}
