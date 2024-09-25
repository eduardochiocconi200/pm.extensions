package com.servicenow.processmining.extensions.sc.dao;

import com.servicenow.processmining.extensions.sc.entities.FlowVersion;
import com.servicenow.processmining.extensions.sn.core.ServiceNowInstance;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class FlowVersionDAOTest
    extends BaseDAOTest
{
    @Test
    public void execute()
    {
        flowsTest();
    }

    private void flowsTest()
    {
        ServiceNowInstance instance = new ServiceNowInstance(snInstance, snUser, snPassword);
        FlowVersionDAOREST fDAO = new FlowVersionDAOREST(instance);
        List<FlowVersion> fItems = fDAO.findAll();
        List<FlowVersion> fActiveItems = fDAO.findAllActive();
        Assert.assertTrue(fItems.size() >= fActiveItems.size());
    }
}
