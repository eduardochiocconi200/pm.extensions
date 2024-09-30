package com.servicenow.processmining.extensions.sc.dao;

import org.junit.Assert;
import org.junit.Test;

import com.servicenow.processmining.extensions.sn.core.ServiceNowInstance;
import com.servicenow.processmining.extensions.sn.dao.PlatformVersionDAOREST;

public class PlatformVersionTest
{
    @Test
    public void test()
    {
        ServiceNowInstance instance = new ServiceNowInstance(snInstance, snUser, snPassword);
        PlatformVersionDAOREST dao = new PlatformVersionDAOREST(instance);
        String version = dao.getVersion();
        Assert.assertNotNull(version);
    }

    protected static final String snInstance = "empechiocconi2";
    protected static final String snUser = "admin";
    protected static final String snPassword = "StarWars!1";
}
