package com.servicenow.processmining.extensions.sc.entities;

import java.util.ArrayList;
import java.util.HashMap;

import com.servicenow.processmining.extensions.sn.entities.ServiceNowEntity;

public class SysAuditLog
    extends ServiceNowEntity
{
    private ArrayList<SysAuditEntry> log = null;
    private HashMap<String, ArrayList<SysAuditEntry>> filteredLogs = null;

    public SysAuditLog(SysAuditLogPK pk)
    {
        super(pk);
        log = new ArrayList<SysAuditEntry>();
    }

    public ArrayList<SysAuditEntry> getLog()
    {
        return this.log;
    }

    public ArrayList<SysAuditEntry> getFilteredLog(final String fieldName)
    {
        if (getFilteredLogs().get(fieldName) == null) {
            ArrayList<SysAuditEntry> filteredLog = new ArrayList<SysAuditEntry>();
            for (SysAuditEntry entry : getLog()) {
                if (entry.getFieldName().equals(fieldName)) {
                    filteredLog.add(entry);
                }
            }

            getFilteredLogs().put(fieldName, filteredLog);
        }

        return getFilteredLogs().get(fieldName);
    }

    private HashMap<String, ArrayList<SysAuditEntry>> getFilteredLogs()
    {
        if (filteredLogs == null) {
            this.filteredLogs = new HashMap<String, ArrayList<SysAuditEntry>>();
        }

        return this.filteredLogs;
    }
}