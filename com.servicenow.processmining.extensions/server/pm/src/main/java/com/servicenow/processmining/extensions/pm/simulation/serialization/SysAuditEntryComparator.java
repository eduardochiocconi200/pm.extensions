package com.servicenow.processmining.extensions.pm.simulation.serialization;

import java.util.Comparator;

import com.servicenow.processmining.extensions.sc.entities.SysAuditEntry;

public class SysAuditEntryComparator
    implements Comparator<SysAuditEntry>
{
    public int compare(SysAuditEntry e1, SysAuditEntry e2) 
    {
        if (e2.getSysCreatedOn().equals(e1.getSysCreatedOn())) {
            if (e1.getDocumentKey().equals(e2.getDocumentKey())) {
                return e1.getFieldName().compareTo(e2.getFieldName());
            }

            return e1.getDocumentKey().compareTo(e2.getDocumentKey());
        }

        return e1.getSysCreatedOn().compareTo(e2.getSysCreatedOn());
    }    
}