package com.servicenow.processmining.extensions.pm.demo;

import org.junit.Assert;
import org.junit.Test;

import com.servicenow.processmining.extensions.sn.core.ServiceNowInstance;

public class DemoTest
{
    @Test
    public void test()
    {
        DemoModelParser parser = new DemoModelParser("/Users/eduardo.chiocconi/Desktop/Demo Scripts/Incidents Evaluation Project/Demo Data.xlsx");
        Assert.assertTrue(parser.parse());

        ServiceNowInstance instance = new ServiceNowInstance(snInstance, snUser, snPassword);
        DemoModelCases cases = new DemoModelCases(parser.getModel(), instance);
        Assert.assertTrue(cases.create());
    }

    protected static final String snInstance = "processminingec1demo.service-now.com";
    protected static final String snUser = "admin";
    protected static final String snPassword = "323ElatiCtDanville!";
}
