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
        // DemoModelParser parser = new DemoModelParser("src/test/resources/Incident Demo Data.xlsx");
        // DemoModelParser parser = new DemoModelParser("/Users/eduardo.chiocconi/Downloads/Incident Demo Data_DP2.xlsx");
        DemoModelParser parser = new DemoModelParser("/Users/eduardo.chiocconi/Downloads/Case Demo Data_EC1.xlsx", "ITSM-Demo-Data");
        Assert.assertTrue(parser.parse());

        ServiceNowInstance instance = new ServiceNowInstance(snInstance, snUser, snPassword);
        DemoModelCases cases = new DemoModelCases(parser.getModel(), instance);
        Assert.assertTrue(cases.create());
    }

//     protected static final String snInstance = "pascalewashproc2.service-now.com";
//     protected static final String snUser = "admin";
//     protected static final String snPassword = "Norway@67";

//    protected static final String snInstance = "processminingec1demo.service-now.com";
//    protected static final String snUser = "admin";
//    protected static final String snPassword = "323ElatiCtDanville!";

    protected static final String snInstance = ServiceNowTestCredentials.getInstanceName();
    protected static final String snUser = ServiceNowTestCredentials.getUserName();
    protected static final String snPassword = ServiceNowTestCredentials.getPassword();
}
