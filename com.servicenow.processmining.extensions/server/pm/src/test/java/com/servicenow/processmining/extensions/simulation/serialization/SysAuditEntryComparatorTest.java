package com.servicenow.processmining.extensions.simulation.serialization;

import org.junit.Assert;
import org.junit.Test;

import com.servicenow.processmining.extensions.pm.simulation.serialization.SysAuditEntryComparator;
import com.servicenow.processmining.extensions.sc.entities.SysAuditEntry;
import com.servicenow.processmining.extensions.sc.entities.SysAuditEntryPK;

public class SysAuditEntryComparatorTest
{
    @Test
    public void execute()
    {
        test1();
        test2();
    }

    private void test1()
    {
        SysAuditEntryComparator comparator = new SysAuditEntryComparator();

        SysAuditEntry e1 = new SysAuditEntry(new SysAuditEntryPK("308564e5335032109547d64a7e5c7b37"));
        e1.setDocumentKey("308564e5335032109547d64a7e5c7b37");
        e1.setTableName("incident");
        e1.setFieldName("created");
        e1.setReason("Simulation60");
        e1.setSysCreatedOn("2023-01-01 04:53:04");
        e1.setSysCreatedBy("admin");
        e1.setNewValue("1");

        SysAuditEntry e2 = new SysAuditEntry(new SysAuditEntryPK("308564e5335032109547d64a7e5c7b37"));
        e2.setDocumentKey("308564e5335032109547d64a7e5c7b37");
        e2.setTableName("incident");
        e2.setFieldName("created");
        e2.setReason("Simulation60");
        e2.setSysCreatedOn("2023-01-01 04:53:04");
        e2.setSysCreatedBy("admin");
        e2.setNewValue("1");

        Assert.assertEquals(0 , comparator.compare(e1, e2));
    }

    private void test2()
    {
        SysAuditEntryComparator comparator = new SysAuditEntryComparator();

        SysAuditEntry e1 = new SysAuditEntry(new SysAuditEntryPK("308564e5335032109547d64a7e5c7b37"));
        e1.setDocumentKey("308564e5335032109547d64a7e5c7b37");
        e1.setTableName("incident");
        e1.setFieldName("created");
        e1.setReason("Simulation60");
        e1.setSysCreatedOn("2023-01-01 03:19:24");
        e1.setSysCreatedBy("admin");
        e1.setNewValue("1");

        SysAuditEntry e2 = new SysAuditEntry(new SysAuditEntryPK("308564e5335032109547d64a7e5c7b37"));
        e2.setDocumentKey("308564e5335032109547d64a7e5c7b37");
        e2.setTableName("incident");
        e2.setFieldName("created");
        e2.setReason("Simulation60");
        e2.setSysCreatedOn("2023-01-01 04:53:04");
        e2.setSysCreatedBy("admin");
        e2.setNewValue("1");

        Assert.assertTrue(comparator.compare(e1, e2) < 0);
        Assert.assertTrue(comparator.compare(e2, e1) > 0);
    }
}