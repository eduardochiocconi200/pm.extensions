package com.servicenow.processmining.extensions.simulation.serialization;

import org.junit.Assert;
import org.junit.Test;

import com.servicenow.processmining.extensions.pm.simulation.serialization.AuditLogSerializer;
import com.servicenow.processmining.extensions.sn.core.TestUtility;

public class Serialization1Test
{
    @Test
    public void execute()
    {
        test1();
    }

    private void test1()
    {
        String auditLogJSONString = new TestUtility().loadProcessMiningModel("/simulation/audit-log-1.json");
        AuditLogSerializer serializer = new AuditLogSerializer();
        Assert.assertTrue(serializer.parse(auditLogJSONString));
        Assert.assertEquals(857, serializer.getLog().getLog().size());
    }
}
