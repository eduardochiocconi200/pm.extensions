package com.servicenow.processmining.extensions.sc.entities;

import com.servicenow.processmining.extensions.sn.entities.PrimaryKey;

public class WorkflowHistoryPK
    extends PrimaryKey
{
    private String workflowVersionId = null;

    public WorkflowHistoryPK(final String workflowVersionId)
    {
        super();
        this.workflowVersionId = workflowVersionId;
    }

    public String getWorkflowVersionId()
    {
        return this.workflowVersionId;
    }

    public boolean equals(Object other)
    {
        if (this == other) {
            return true;
        }

        if (!(other instanceof WorkflowHistoryPK)) {
            return false;
        }
        WorkflowHistoryPK castOther = (WorkflowHistoryPK) other;

        return this.workflowVersionId.equals(castOther.workflowVersionId);
    }

    public int hashCode()
    {
        final int prime = 31;
        int hash = 17;
        hash = hash * prime + this.workflowVersionId.hashCode();
        return hash;
    }

    public String toString()
    {
        return getWorkflowVersionId();
    }
}