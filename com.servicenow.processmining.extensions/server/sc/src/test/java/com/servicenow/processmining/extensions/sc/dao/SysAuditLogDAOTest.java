package com.servicenow.processmining.extensions.sc.dao;

import com.servicenow.processmining.extensions.sc.entities.SysAuditLog;
import com.servicenow.processmining.extensions.sc.entities.SysAuditLogPK;
import com.servicenow.processmining.extensions.sn.core.ServiceNowInstance;

import java.util.ArrayList;

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
        sysAuditLog3Test();
    }

    private void sysAuditLog1Test()
    {
        ServiceNowInstance instance = new ServiceNowInstance(snInstance, snUser, snPassword);
        SysAuditLogDAOREST wfDAO = new SysAuditLogDAOREST(instance);
        SysAuditLog sysAuditLog = wfDAO.findById(new SysAuditLogPK("incident", 100));
        Assert.assertNotNull(sysAuditLog);
        Assert.assertTrue(sysAuditLog.getLog().size() > 0);
        logger.debug("Retrieved: (" + sysAuditLog.getLog().size() + ") history entries.");
        System.out.println("Retrieved: (" + sysAuditLog.getLog().size() + ") history entries.");
    }

    private void sysAuditLog2Test()
    {
        ServiceNowInstance instance = new ServiceNowInstance(snInstance, snUser, snPassword);
        SysAuditLogDAOREST wfDAO = new SysAuditLogDAOREST(instance);
        SysAuditLog sysAuditLog = wfDAO.findById(new SysAuditLogPK("incident", "state", 100));
        Assert.assertNotNull(sysAuditLog);
        Assert.assertTrue(sysAuditLog.getLog().size() > 0);
        logger.debug("Retrieved: (" + sysAuditLog.getLog().size() + ") history entries.");
        System.out.println("Retrieved: (" + sysAuditLog.getLog().size() + ") history entries.");
    }

    private void sysAuditLog3Test()
    {
        ServiceNowInstance instance = new ServiceNowInstance(snInstance, snUser, snPassword);
        ArrayList<String> incidentIds = new ArrayList<String>();

        SysAuditLogDAOREST wfDAO = new SysAuditLogDAOREST(instance);
        SysAuditLog sysAuditLog = wfDAO.findByIds(new SysAuditLogPK("incident", "state"), incidentIds);
        Assert.assertNotNull(sysAuditLog);
        Assert.assertTrue(sysAuditLog.getLog().size() > 0);
        logger.debug("Retrieved: (" + sysAuditLog.getLog().size() + ") history entries.");
        System.out.println("Retrieved: (" + sysAuditLog.getLog().size() + ") history entries.");
    }

    private static final Logger logger = LoggerFactory.getLogger(SysAuditLogDAOTest.class);
}
