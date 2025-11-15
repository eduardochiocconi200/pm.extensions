package com.servicenow.processmining.extensions.pm.demo;

import com.servicenow.processmining.extensions.sn.core.ServiceNowInstance;
import com.servicenow.processmining.extensions.sn.core.ServiceNowTestCredentials;

import org.junit.Assert;
import org.junit.Test;

public class DemoTest
{
    @Test
    public void test()
    {
        long startTime = System.currentTimeMillis();
        // DemoModelParser parser = new DemoModelParser("src/test/resources/Incident Demo Data.xlsx", "TM-Demo-2");
        DemoModelParser parser = new DemoModelParser("/Users/eduardo.chiocconi/Downloads/Incident Demo Data.xlsx", "TM-Demo-2");
        Assert.assertTrue(parser.parse());

        ServiceNowInstance instance = new ServiceNowInstance(snInstance, snUser, snPassword);
        DemoModelCases cases = new DemoModelCases(parser.getModel(), instance);
        Assert.assertTrue(cases.create());
        DemoModelTimeline timeline = new DemoModelTimeline(parser.getModel());
        Assert.assertTrue(timeline.create());

        long endTime = System.currentTimeMillis();
        System.out.println("Execution Time: (" + (endTime - startTime) + ")");
    }

    protected static final String snInstance = ServiceNowTestCredentials.getInstanceName();
    protected static final String snUser = ServiceNowTestCredentials.getUserName();
    protected static final String snPassword = ServiceNowTestCredentials.getPassword();
}
