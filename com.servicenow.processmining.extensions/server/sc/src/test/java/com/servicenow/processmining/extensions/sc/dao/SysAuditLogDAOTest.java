package com.servicenow.processmining.extensions.sc.dao;

import com.servicenow.processmining.extensions.sc.entities.SysAuditLog;
import com.servicenow.processmining.extensions.sc.entities.SysAuditLogPK;
import com.servicenow.processmining.extensions.sn.core.ServiceNowInstance;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SysAuditLogDAOTest
    extends BaseDAOTest
{
    @Test
    public void execute()
    {
        sysAuditLog1Test();
        sysAuditLog2Test();
    }

    private void sysAuditLog1Test()
    {
        ServiceNowInstance instance = new ServiceNowInstance(snInstance, snUser, snPassword);
        SysAuditLogDAOREST wfDAO = new SysAuditLogDAOREST(instance);
        SysAuditLog sysAuditLog = wfDAO.findById(new SysAuditLogPK("incident", 10));
        Assert.assertNotNull(sysAuditLog);
        Assert.assertTrue(sysAuditLog.getLog().size() > 0 && sysAuditLog.getLog().size() <= 10);
        logger.debug("Retrieved: (" + sysAuditLog.getLog().size() + ") history entries.");
    }

    private void sysAuditLog2Test()
    {
        ServiceNowInstance instance = new ServiceNowInstance(snInstance, snUser, snPassword);
        SysAuditLogDAOREST wfDAO = new SysAuditLogDAOREST(instance);
        long startTime = System.currentTimeMillis();
        SysAuditLog sysAuditLog = wfDAO.findById(new SysAuditLogPK("incident", "state", 10));
        long endTime = System.currentTimeMillis();
        logger.info("Long query completed in: (" + (endTime-startTime) + ") milliseconds.");
        Assert.assertNotNull(sysAuditLog);
        Assert.assertTrue(sysAuditLog.getLog().size() > 0 && sysAuditLog.getLog().size() <= 10);
        logger.debug("Retrieved: (" + sysAuditLog.getLog().size() + ") history entries.");
    }

    private static final Logger logger = LoggerFactory.getLogger(SysAuditLogDAOTest.class);
}