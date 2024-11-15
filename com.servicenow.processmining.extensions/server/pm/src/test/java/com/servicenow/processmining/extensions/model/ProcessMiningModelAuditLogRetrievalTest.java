package com.servicenow.processmining.extensions.model;

import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelParser;
import com.servicenow.processmining.extensions.sc.dao.SysAuditLogDAOREST;
import com.servicenow.processmining.extensions.sc.entities.SysAuditLog;
import com.servicenow.processmining.extensions.sc.entities.SysAuditLogPK;
import com.servicenow.processmining.extensions.sn.core.ServiceNowInstance;
import com.servicenow.processmining.extensions.sn.core.ServiceNowTestCredentials;
import com.servicenow.processmining.extensions.sn.core.TestUtility;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

public class ProcessMiningModelAuditLogRetrievalTest
{
    @Test
    public void execute()
    {
        test1();
        test2();
        test3();
        test4();
        test5();
    }

    private void test1()
    {
        String processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/payload2-v.json");
        ProcessMiningModelParser parser = new ProcessMiningModelParser("abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));
        ArrayList<String> processModelCaseIds = parser.getProcessMiningModel().getAllCaseIds();
        Assert.assertEquals(288, processModelCaseIds.size());

        ServiceNowInstance instance = new ServiceNowInstance(ServiceNowTestCredentials.getInstanceName(), ServiceNowTestCredentials.getUserName(), ServiceNowTestCredentials.getPassword());
        SysAuditLogDAOREST wfDAO = new SysAuditLogDAOREST(instance);
        SysAuditLog sysAuditLog = wfDAO.findByIds(new SysAuditLogPK("incident"), processModelCaseIds);
        Assert.assertNotNull(sysAuditLog);
        // We will assume there are minimum 3 audit logs per record.
        Assert.assertTrue(sysAuditLog.getLog().size() > processModelCaseIds.size()*3);
    }

    private void test2()
    {
        String processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/filterPayload1-v.json");
        ProcessMiningModelParser parser = new ProcessMiningModelParser("abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));
        Assert.assertEquals(1, parser.getProcessMiningModel().getFilters().size());
        ArrayList<String> processModelCaseIds = parser.getProcessMiningModel().getAllCaseIds();
        Assert.assertEquals(48, processModelCaseIds.size());

        ServiceNowInstance instance = new ServiceNowInstance(ServiceNowTestCredentials.getInstanceName(), ServiceNowTestCredentials.getUserName(), ServiceNowTestCredentials.getPassword());
        SysAuditLogDAOREST wfDAO = new SysAuditLogDAOREST(instance);
        SysAuditLog sysAuditLog = wfDAO.findByIds(new SysAuditLogPK("incident"), processModelCaseIds);
        Assert.assertNotNull(sysAuditLog);
        // We will assume there are minimum 3 audit logs per record.
        Assert.assertTrue(sysAuditLog.getLog().size() > processModelCaseIds.size()*3);
    }

    private void test3()
    {
        String processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/filterPayloadIncidentPhoneChannel-v.json");
        ProcessMiningModelParser parser = new ProcessMiningModelParser("abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));
        Assert.assertEquals(3, parser.getProcessMiningModel().getFilters().size());
        ArrayList<String> processModelCaseIds = parser.getProcessMiningModel().getAllCaseIds();
        Assert.assertEquals(39, processModelCaseIds.size());

        ServiceNowInstance instance = new ServiceNowInstance(ServiceNowTestCredentials.getInstanceName(), ServiceNowTestCredentials.getUserName(), ServiceNowTestCredentials.getPassword());
        SysAuditLogDAOREST wfDAO = new SysAuditLogDAOREST(instance);
        SysAuditLog sysAuditLog = wfDAO.findByIds(new SysAuditLogPK("incident"), processModelCaseIds);
        Assert.assertNotNull(sysAuditLog);
        // We will assume there are minimum 3 audit logs per record.
        Assert.assertTrue(sysAuditLog.getLog().size() > processModelCaseIds.size()*3);
    }

    private void test4()
    {
        String processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/filterPayload5-w.json");
        ProcessMiningModelParser parser = new ProcessMiningModelParser("abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));
        Assert.assertEquals(4, parser.getProcessMiningModel().getFilters().size());
        ArrayList<String> processModelCaseIds = parser.getProcessMiningModel().getAllCaseIds();
        Assert.assertEquals(173, processModelCaseIds.size());

        ServiceNowInstance instance = new ServiceNowInstance(ServiceNowTestCredentials.getInstanceName(), ServiceNowTestCredentials.getUserName(), ServiceNowTestCredentials.getPassword());
        SysAuditLogDAOREST wfDAO = new SysAuditLogDAOREST(instance);
        SysAuditLog sysAuditLog = wfDAO.findByIds(new SysAuditLogPK("incident"), processModelCaseIds);
        Assert.assertNotNull(sysAuditLog);
        // We will assume there are minimum 3 audit logs per record.
        Assert.assertTrue(sysAuditLog.getLog().size() > processModelCaseIds.size()*3);
    }

    private void test5()
    {
        String processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/filterPayload6-w.json");
        ProcessMiningModelParser parser = new ProcessMiningModelParser("abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));
        Assert.assertEquals(5, parser.getProcessMiningModel().getFilters().size());
        ArrayList<String> processModelCaseIds = parser.getProcessMiningModel().getAllCaseIds();
        Assert.assertEquals(174, processModelCaseIds.size());

        ServiceNowInstance instance = new ServiceNowInstance(ServiceNowTestCredentials.getInstanceName(), ServiceNowTestCredentials.getUserName(), ServiceNowTestCredentials.getPassword());
        SysAuditLogDAOREST wfDAO = new SysAuditLogDAOREST(instance);
        SysAuditLog sysAuditLog = wfDAO.findByIds(new SysAuditLogPK("incident"), processModelCaseIds);
        Assert.assertNotNull(sysAuditLog);
        // We will assume there are minimum 3 audit logs per record.
        Assert.assertTrue(sysAuditLog.getLog().size() > processModelCaseIds.size()*3);
    }
}
