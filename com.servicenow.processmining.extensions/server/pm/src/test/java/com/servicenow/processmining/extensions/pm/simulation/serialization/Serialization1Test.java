package com.servicenow.processmining.extensions.pm.simulation.serialization;

import com.servicenow.processmining.extensions.sn.core.TestUtility;

import org.junit.Assert;
import org.junit.Test;

public class Serialization1Test
{
    @Test
    public void execute()
    {
        test1();
    }

    private void test1()
    {
        String auditLogJSONString = new TestUtility().loadProcessMiningAuditLogs("/simulation/audit-log-1.json");
        AuditLogSerializer serializer = new AuditLogSerializer();
        Assert.assertTrue(serializer.parse(auditLogJSONString));
        Assert.assertEquals(7953, serializer.getLog().getLog().size());
    }
}
