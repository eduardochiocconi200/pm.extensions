package com.servicenow.processmining.extensions.sc.entities;

import java.util.ArrayList;

import com.servicenow.processmining.extensions.sn.entities.ServiceNowEntity;

public class SysAuditLog
    extends ServiceNowEntity
{
    private ArrayList<SysAuditEntry> log = null;

    public SysAuditLog(SysAuditLogPK pk)
    {
        super(pk);
        log = new ArrayList<SysAuditEntry>();
    }

    public ArrayList<SysAuditEntry> getLog()
    {
        return this.log;
    }
}