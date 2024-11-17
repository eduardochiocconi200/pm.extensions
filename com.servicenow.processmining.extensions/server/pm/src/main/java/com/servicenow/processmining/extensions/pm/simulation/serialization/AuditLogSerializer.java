package com.servicenow.processmining.extensions.pm.simulation.serialization;

import org.json.JSONArray;

import com.servicenow.processmining.extensions.sc.entities.SysAuditEntry;
import com.servicenow.processmining.extensions.sc.entities.SysAuditEntryPK;
import com.servicenow.processmining.extensions.sc.entities.SysAuditLog;
import com.servicenow.processmining.extensions.sc.entities.SysAuditLogPK;

public class AuditLogSerializer
{
    private SysAuditLog sysAuditLog = null;

    public AuditLogSerializer()
    {
    }
    
    public boolean parse(final String jsonPayload)
    {
        SysAuditLogPK pk = new SysAuditLogPK("a");
        sysAuditLog = new SysAuditLog(pk);

        JSONArray auditLogEntries = new JSONArray(jsonPayload);
        long counter = 0;
        for (int i = 0; i < auditLogEntries.length(); i++) {
            String tablename = auditLogEntries.getJSONObject(i).getString("tablename");
            String documentkey = auditLogEntries.getJSONObject(i).getString("documentkey");
            String fieldname = auditLogEntries.getJSONObject(i).getString("fieldname");
            String oldvalue = auditLogEntries.getJSONObject(i).getString("oldvalue");
            String newvalue = auditLogEntries.getJSONObject(i).getString("newvalue");
            String user = auditLogEntries.getJSONObject(i).getString("user");
            String sys_created_on = auditLogEntries.getJSONObject(i).getString("sys_created_on");
            String sys_created_by = auditLogEntries.getJSONObject(i).getString("sys_created_by");

            SysAuditEntry e = new SysAuditEntry(new SysAuditEntryPK(String.valueOf(counter++)));
            e.setDocumentKey(documentkey);
            e.setFieldName(fieldname);
            e.setNewValue(newvalue);
            e.setOldValue(oldvalue);
            e.setSysCreatedBy(sys_created_by);
            e.setSysCreatedOn(sys_created_on);
            e.setTableName(tablename);
            e.setUser(user);

            sysAuditLog.getLog().add(e);
        }

        return true;
    }

    public SysAuditLog getLog()
    {
        return this.sysAuditLog;
    }
}
