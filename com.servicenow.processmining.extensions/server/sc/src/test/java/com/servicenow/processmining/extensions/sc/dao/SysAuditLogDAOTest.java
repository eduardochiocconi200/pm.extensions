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
        sysAuditLogTest();
    }

    private void sysAuditLogTest()
    {
        ServiceNowInstance instance = new ServiceNowInstance(snInstance, snUser, snPassword);
        SysAuditLogDAOREST wfDAO = new SysAuditLogDAOREST(instance);
        SysAuditLog sysAuditLog = wfDAO.findById(new SysAuditLogPK("incident"));
        Assert.assertNotNull(sysAuditLog);
        Assert.assertTrue(sysAuditLog.getLog().size() > 0);
        logger.debug("Retrieved: (" + sysAuditLog.getLog().size() + ") history entries.");
    }

    private static final Logger logger = LoggerFactory.getLogger(SysAuditLogDAOTest.class);
}
