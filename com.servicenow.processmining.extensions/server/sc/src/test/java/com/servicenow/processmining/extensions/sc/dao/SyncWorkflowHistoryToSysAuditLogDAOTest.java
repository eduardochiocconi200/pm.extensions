package com.servicenow.processmining.extensions.sc.dao;

import com.servicenow.processmining.extensions.sc.entities.SysAuditEntry;
import com.servicenow.processmining.extensions.sc.entities.SysAuditLog;
import com.servicenow.processmining.extensions.sc.entities.SysAuditLogPK;
import com.servicenow.processmining.extensions.sc.entities.WorkflowHistory;
import com.servicenow.processmining.extensions.sc.entities.WorkflowHistoryPK;
import com.servicenow.processmining.extensions.sc.transformers.WorkflowHistoryToSysAuditLogTransformer;
import com.servicenow.processmining.extensions.sn.core.ServiceNowInstance;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SyncWorkflowHistoryToSysAuditLogDAOTest
    extends BaseDAOTest
{
    @Test
    public void execute()
    {
        test1();
    }

    private void test1()
    {
        ServiceNowInstance instance = new ServiceNowInstance(snInstance, snUser, snPassword);

        WorkflowHistoryDAOREST wfDAO = new WorkflowHistoryDAOREST(instance);
        WorkflowHistory wfHistory = wfDAO.findById(new WorkflowHistoryPK("e96d4083d722210051b1c257ed610347"));
        Assert.assertTrue(wfHistory.getHistory().size() > 0);
        logger.debug("Retrieved: (" + wfHistory.getConnectedHistory().size() + ") history entries.");

        WorkflowHistoryToSysAuditLogTransformer transformer = new WorkflowHistoryToSysAuditLogTransformer();
        SysAuditLog auditLog = transformer.convert(wfHistory);
        ArrayList<String> wfHistoryIds = transformer.convertToWorkflowIds(wfHistory);

        long start = System.currentTimeMillis();
        SysAuditLogDAOREST auditDAO = new SysAuditLogDAOREST(instance);
        SysAuditLog sysAuditLog = auditDAO.findById(new SysAuditLogPK("wf_context"));
        long end = System.currentTimeMillis();
        Assert.assertNotNull(sysAuditLog);
        logger.debug("Completed findById (" + sysAuditLog.getLog().size() + ") SysAuditLog records in (" + (end - start) + ")");

        start = System.currentTimeMillis();
        SysAuditLogDAOREST auditDAO2 = new SysAuditLogDAOREST(instance);
        SysAuditLog sysAuditLog2 = auditDAO2.findByIds(new SysAuditLogPK("wf_context"), wfHistoryIds);
        Assert.assertNotNull(sysAuditLog2);
        end = System.currentTimeMillis();
        logger.debug("Completed findByIds (" + sysAuditLog2.getLog().size() + ") SysAuditLog records in (" + (end - start) + ")");

        start = System.currentTimeMillis();
        logger.debug("Will start deleting (" + sysAuditLog.getLog().size() + ") SysAuditLog records ...");
        for (SysAuditEntry sea : sysAuditLog.getLog()) {
            logger.debug(sea.toString());
            auditDAO.delete(sea);
        }
        end = System.currentTimeMillis();
        logger.debug("Completed deleting (" + auditLog.getLog().size() + ") SysAuditLog records in (" + (end - start) + ")");

        start = System.currentTimeMillis();
        auditDAO = new SysAuditLogDAOREST(instance);
        sysAuditLog = auditDAO.findById(new SysAuditLogPK("wf_context"));
        end = System.currentTimeMillis();
        Assert.assertNotNull(sysAuditLog);
        logger.debug("Completed findById (" + sysAuditLog.getLog().size() + ") SysAuditLog records in (" + (end - start) + ")");

/*
        logger.debug("Will start adding (" + auditLog.getLog().size() + ") SysAuditLog records ...");
        start = System.currentTimeMillis();
        for (SysAuditEntry sea : auditLog.getLog()) {
            logger.debug(sea.toString());
            auditDAO.insert(sea);
        }
        end = System.currentTimeMillis();
        logger.debug("Completed adding (" + auditLog.getLog().size() + ") SysAuditLog records in (" + (end - start) + ")");

        sysAuditLog = auditDAO.findById(new SysAuditLogPK("wf_context"));
        Assert.assertNotNull(sysAuditLog);
        Assert.assertTrue(sysAuditLog.getLog().size() > 0);
        start = System.currentTimeMillis();
        logger.debug("Will start deleting (" + auditLog.getLog().size() + ") SysAuditLog records ...");
        for (SysAuditEntry sea : sysAuditLog.getLog()) {
            logger.debug(sea.toString());
            auditDAO.delete(sea);
        }
        end = System.currentTimeMillis();
        logger.debug("Completed deleting (" + auditLog.getLog().size() + ") SysAuditLog records in (" + (end - start) + ")");
*/        
    }

    private static final Logger logger = LoggerFactory.getLogger(SyncWorkflowHistoryToSysAuditLogDAOTest.class);
}