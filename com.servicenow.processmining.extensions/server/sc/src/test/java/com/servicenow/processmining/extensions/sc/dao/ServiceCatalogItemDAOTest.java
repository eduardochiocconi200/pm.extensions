package com.servicenow.processmining.extensions.sc.dao;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.servicenow.processmining.extensions.sc.entities.ServiceCatalogItem;
import com.servicenow.processmining.extensions.sn.core.ServiceNowInstance;
import com.servicenow.processmining.extensions.sn.core.ServiceNowTestCredentials;

public class ServiceCatalogItemDAOTest
    extends BaseDAOTest
{
    @Test
    public void execute()
    {
        serviceCatalogItemsTest();
    }

    private void serviceCatalogItemsTest()
    {
        ServiceNowInstance instance = new ServiceNowInstance(snInstance, snUser, snPassword);
        ServiceCatalogItemDAOREST sciDAO = new ServiceCatalogItemDAOREST(instance);
        List<ServiceCatalogItem> scItems = sciDAO.findAll();
        List<ServiceCatalogItem> scActiveItems = sciDAO.findAllActive();
        Assert.assertTrue(scItems.size() >= scActiveItems.size());
    }

    private static final String snInstance = ServiceNowTestCredentials.getInstanceName();
    private static final String snUser = ServiceNowTestCredentials.getUserName();
    private static final String snPassword = ServiceNowTestCredentials.getPassword();
}
