package com.servicenow.processmining.extensions.pm.demo;

import org.junit.Assert;
import org.junit.Test;

import com.servicenow.processmining.extensions.sn.core.ServiceNowInstance;
import com.servicenow.processmining.extensions.sn.core.ServiceNowTestCredentials;

public class DemoTest
{
    @Test
    public void test()
    {
        DemoModelParser parser = new DemoModelParser("src/test/resources/Incident Demo Data.xlsx");
        Assert.assertTrue(parser.parse());

        ServiceNowInstance instance = new ServiceNowInstance(snInstance, snUser, snPassword);
        DemoModelCases cases = new DemoModelCases(parser.getModel(), instance);
        Assert.assertTrue(cases.create());
    }

    protected static final String snInstance = ServiceNowTestCredentials.getInstanceName();
    protected static final String snUser = ServiceNowTestCredentials.getUserName();
    protected static final String snPassword = ServiceNowTestCredentials.getPassword();
}