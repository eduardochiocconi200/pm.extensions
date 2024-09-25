package com.servicenow.processmining.extensions.sc.dao;

import com.servicenow.processmining.extensions.sc.entities.WorkflowVersion;
import com.servicenow.processmining.extensions.sn.core.ServiceNowInstance;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class WorkflowVersionDAOTest
    extends BaseDAOTest
{
    @Test
    public void execute()
    {
        workflowsTest();
    }

    private void workflowsTest()
    {
        ServiceNowInstance instance = new ServiceNowInstance(snInstance, snUser, snPassword);
        WorkflowVersionDAOREST wfDAO = new WorkflowVersionDAOREST(instance);
        List<WorkflowVersion> wfItems = wfDAO.findAll();
        List<WorkflowVersion> wfActiveItems = wfDAO.findAllActive();
        Assert.assertTrue(wfItems.size() >= wfActiveItems.size());
    }
}
