package com.servicenow.processmining.extensions.sc.dao;

import com.servicenow.processmining.extensions.sc.entities.WorkflowHistory;
import com.servicenow.processmining.extensions.sc.entities.WorkflowHistoryPK;
import com.servicenow.processmining.extensions.sn.core.ServiceNowInstance;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkflowHistoryDAOTest
    extends BaseDAOTest
{
    @Test
    public void execute()
    {
        workflowHistoryTest();
    }

    private void workflowHistoryTest()
    {
        ServiceNowInstance instance = new ServiceNowInstance(snInstance, snUser, snPassword);
        WorkflowHistoryDAOREST wfDAO = new WorkflowHistoryDAOREST(instance);
        WorkflowHistory wfHistory = wfDAO.findById(new WorkflowHistoryPK("e96d4083d722210051b1c257ed610347"));
        Assert.assertTrue(wfHistory.getHistory().size() > 0);
        logger.debug("Retrieved: (" + wfHistory.getHistory().size() + ") history entries.");
    }

    private static final Logger logger = LoggerFactory.getLogger(WorkflowHistoryDAOTest.class);
}
