package com.servicenow.processmining.extensions.sc.transformers;

import com.servicenow.processmining.extensions.sc.entities.SysAuditEntry;
import com.servicenow.processmining.extensions.sc.entities.SysAuditLog;
import com.servicenow.processmining.extensions.sc.entities.SysAuditLogPK;
import com.servicenow.processmining.extensions.sc.entities.WorkflowHistory;
import com.servicenow.processmining.extensions.sc.entities.WorkflowHistoryEntry;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkflowHistoryToSysAuditLogTransformer
{
    private SysAuditLog auditLog = null;

    public WorkflowHistoryToSysAuditLogTransformer()
    {
    }

    public SysAuditLog convert(final WorkflowHistory wfHistory)
    {
        String tableName = wfHistory.getTableName();
        auditLog = new SysAuditLog(new SysAuditLogPK(tableName));
        int records = 0;
        for (WorkflowHistoryEntry entry : wfHistory.getConnectedHistory()) {
            SysAuditEntry saEntry = entry.toSysAuditEntry();
            auditLog.getLog().add(saEntry);
            records++;
        }

        logger.debug("Converted (" + records + ") records from Workflow History to SysAuditLog.");

        return auditLog;
    }

    private static final Logger logger = LoggerFactory.getLogger(WorkflowHistoryToSysAuditLogTransformer.class);

    public ArrayList<String> convertToWorkflowIds(WorkflowHistory wfHistory)
    {
        ArrayList<String> ids = new ArrayList<String>();
        for (WorkflowHistoryEntry entry : wfHistory.getConnectedHistory()) {
            ids.add(entry.getWorkflowInstanceId());
       }

        logger.debug("Converted (" + ids.size() + ") records from Workflow History to SysAuditLog.");

        return ids;
    }
}