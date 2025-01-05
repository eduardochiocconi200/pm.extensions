package com.servicenow.processmining.extensions.sc.entities;

import java.util.ArrayList;
import java.util.HashMap;

import com.servicenow.processmining.extensions.sn.entities.ServiceNowEntity;

public class SysAuditLog
    extends ServiceNowEntity
{
    private ArrayList<SysAuditEntry> log = null;
    private HashMap<String, ArrayList<SysAuditEntry>> fieldNameFilteredLogs = null;
    private HashMap<String, ArrayList<SysAuditEntry>> documentKeyFilteredLogs = null;

    public SysAuditLog(SysAuditLogPK pk)
    {
        super(pk);
        log = new ArrayList<SysAuditEntry>();
    }

    public ArrayList<SysAuditEntry> getLog()
    {
        return this.log;
    }

    public ArrayList<SysAuditEntry> getFieldNameFilteredLog(final String fieldName)
    {
        if (getFieldNameFilteredLogs().get(fieldName) == null) {
            ArrayList<SysAuditEntry> filteredLog = new ArrayList<SysAuditEntry>();
            for (SysAuditEntry entry : getLog()) {
                if (entry.getFieldName().equals(fieldName)) {
                    filteredLog.add(entry);
                }
            }

            getFieldNameFilteredLogs().put(fieldName, filteredLog);
        }

        return getFieldNameFilteredLogs().get(fieldName);
    }

    private HashMap<String, ArrayList<SysAuditEntry>> getFieldNameFilteredLogs()
    {
        if (fieldNameFilteredLogs == null) {
            this.fieldNameFilteredLogs = new HashMap<String, ArrayList<SysAuditEntry>>();
        }

        return this.fieldNameFilteredLogs;
    }

    public ArrayList<SysAuditEntry> getDocumentKeyFilteredLog(final String documentKey)
    {
        if (getDocumentKeyFilteredLogs().get(documentKey) == null) {
            ArrayList<SysAuditEntry> filteredLog = new ArrayList<SysAuditEntry>();
            for (SysAuditEntry entry : getLog()) {
                if (entry.getDocumentKey().equals(documentKey)) {
                    filteredLog.add(entry);
                }
            }

            getDocumentKeyFilteredLogs().put(documentKey, filteredLog);
        }

        return getDocumentKeyFilteredLogs().get(documentKey);
    }

    private HashMap<String, ArrayList<SysAuditEntry>> getDocumentKeyFilteredLogs()
    {
        if (documentKeyFilteredLogs == null) {
            this.documentKeyFilteredLogs = new HashMap<String, ArrayList<SysAuditEntry>>();
        }

        return this.documentKeyFilteredLogs;
    }
}