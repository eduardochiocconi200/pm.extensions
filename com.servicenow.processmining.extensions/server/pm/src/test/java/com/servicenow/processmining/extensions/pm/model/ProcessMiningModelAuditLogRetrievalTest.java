package com.servicenow.processmining.extensions.pm.model;

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
    private ServiceNowInstance instance = null;

    public ServiceNowInstance getInstance()
    {
        if (instance == null) {
            instance = new ServiceNowInstance(ServiceNowTestCredentials.getInstanceName(), ServiceNowTestCredentials.getUserName(), ServiceNowTestCredentials.getPassword());
        }

        return instance;
    }

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
        String processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/payload3-v.json");
        getInstance().setSNVersion(ServiceNowInstance.VANCOUVER);
        ProcessMiningModelParser parser = ProcessMiningModelParserFactory.getParser(getInstance(), "abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));
        ArrayList<String> processModelCaseIds = parser.getProcessMiningModel().getAllCaseIds();
        Assert.assertEquals(167, processModelCaseIds.size());

        processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/payload3-z.json");
        getInstance().setSNVersion(ServiceNowInstance.ZURICH);
        parser = ProcessMiningModelParserFactory.getParser(getInstance(), "abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));
        String processMiningModelVariantsJSONString = new TestUtility().loadProcessMiningModel("/model/payload3-variants-z.json");
        parser.parseAndAppendVariants(processMiningModelVariantsJSONString);

        processModelCaseIds = parser.getProcessMiningModel().getAllCaseIds();
        Assert.assertEquals(3109, processModelCaseIds.size());

        SysAuditLogDAOREST wfDAO = new SysAuditLogDAOREST(getInstance());
        SysAuditLog sysAuditLog = wfDAO.findByIds(new SysAuditLogPK("incident"), processModelCaseIds);
        Assert.assertNotNull(sysAuditLog);
        // We will assume there are minimum 3 audit logs per record.
        Assert.assertTrue(sysAuditLog.getLog().size() >= processModelCaseIds.size()*3);
    }

    private void test2()
    {
        String processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/filterPayload1-v.json");
        getInstance().setSNVersion(ServiceNowInstance.VANCOUVER);
        ProcessMiningModelParser parser = ProcessMiningModelParserFactory.getParser(getInstance(), "abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));
        Assert.assertEquals(1, parser.getProcessMiningModel().getFilters().size());
        ArrayList<String> processModelCaseIds = parser.getProcessMiningModel().getAllCaseIds();
        Assert.assertEquals(48, processModelCaseIds.size());

        processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/filterPayload1-z.json");
        getInstance().setSNVersion(ServiceNowInstance.ZURICH);
        parser = ProcessMiningModelParserFactory.getParser(getInstance(), "abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));
        String processMiningModelVariantsJSONString = new TestUtility().loadProcessMiningModel("/model/filterPayload1-variants-z.json");
        parser.parseAndAppendVariants(processMiningModelVariantsJSONString);

        processModelCaseIds = parser.getProcessMiningModel().getAllCaseIds();
        Assert.assertEquals(977, processModelCaseIds.size());

        ServiceNowInstance instance = new ServiceNowInstance(ServiceNowTestCredentials.getInstanceName(), ServiceNowTestCredentials.getUserName(), ServiceNowTestCredentials.getPassword());
        SysAuditLogDAOREST wfDAO = new SysAuditLogDAOREST(instance);
        SysAuditLog sysAuditLog = wfDAO.findByIds(new SysAuditLogPK("incident"), processModelCaseIds);
        Assert.assertNotNull(sysAuditLog);
        // We will assume there are minimum 3 audit logs per record.
        Assert.assertTrue(sysAuditLog.getLog().size() >= processModelCaseIds.size()*3);
    }

    private void test3()
    {
        String processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/filterPayloadIncidentPhoneChannel-v.json");
        getInstance().setSNVersion(ServiceNowInstance.VANCOUVER);
        ProcessMiningModelParser parser = ProcessMiningModelParserFactory.getParser(getInstance(), "abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));
        Assert.assertEquals(3, parser.getProcessMiningModel().getFilters().size());
        ArrayList<String> processModelCaseIds = parser.getProcessMiningModel().getAllCaseIds();
        Assert.assertEquals(39, processModelCaseIds.size());

        processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/filterPayloadIncidentPhoneChannel-z.json");
        getInstance().setSNVersion(ServiceNowInstance.ZURICH);
        parser = ProcessMiningModelParserFactory.getParser(getInstance(), "abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));
        String processMiningModelVariantsJSONString = new TestUtility().loadProcessMiningModel("/model/filterPayloadIncidentPhoneChannel-variants-z.json");
        parser.parseAndAppendVariants(processMiningModelVariantsJSONString);

        processModelCaseIds = parser.getProcessMiningModel().getAllCaseIds();
        Assert.assertEquals(396, processModelCaseIds.size());

        ServiceNowInstance instance = new ServiceNowInstance(ServiceNowTestCredentials.getInstanceName(), ServiceNowTestCredentials.getUserName(), ServiceNowTestCredentials.getPassword());
        SysAuditLogDAOREST wfDAO = new SysAuditLogDAOREST(instance);
        SysAuditLog sysAuditLog = wfDAO.findByIds(new SysAuditLogPK("incident"), processModelCaseIds);
        Assert.assertNotNull(sysAuditLog);
        // We will assume there are minimum 3 audit logs per record.
        Assert.assertTrue(sysAuditLog.getLog().size() >= processModelCaseIds.size()*3);
    }

    private void test4()
    {
        String processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/filterPayload5-w.json");
        getInstance().setSNVersion(ServiceNowInstance.WASHINGTON);
        ProcessMiningModelParser parser = ProcessMiningModelParserFactory.getParser(getInstance(), "abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));
        Assert.assertEquals(4, parser.getProcessMiningModel().getFilters().size());
        ArrayList<String> processModelCaseIds = parser.getProcessMiningModel().getAllCaseIds();
        Assert.assertEquals(173, processModelCaseIds.size());

        processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/filterPayload5-z.json");
        getInstance().setSNVersion(ServiceNowInstance.ZURICH);
        parser = ProcessMiningModelParserFactory.getParser(getInstance(), "abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));
        String processMiningModelVariantsJSONString = new TestUtility().loadProcessMiningModel("/model/filterPayload5-variants-z.json");
        parser.parseAndAppendVariants(processMiningModelVariantsJSONString);

        processModelCaseIds = parser.getProcessMiningModel().getAllCaseIds();
        Assert.assertEquals(1430, processModelCaseIds.size());

        ServiceNowInstance instance = new ServiceNowInstance(ServiceNowTestCredentials.getInstanceName(), ServiceNowTestCredentials.getUserName(), ServiceNowTestCredentials.getPassword());
        SysAuditLogDAOREST wfDAO = new SysAuditLogDAOREST(instance);
        SysAuditLog sysAuditLog = wfDAO.findByIds(new SysAuditLogPK("incident"), processModelCaseIds);
        Assert.assertNotNull(sysAuditLog);
        // We will assume there are minimum 3 audit logs per record.
        Assert.assertTrue(sysAuditLog.getLog().size() >= processModelCaseIds.size()*3);
    }

    private void test5()
    {
        String processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/filterPayload6-w.json");
        getInstance().setSNVersion(ServiceNowInstance.WASHINGTON);
        ProcessMiningModelParser parser = ProcessMiningModelParserFactory.getParser(getInstance(), "abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));
        Assert.assertEquals(5, parser.getProcessMiningModel().getFilters().size());
        ArrayList<String> processModelCaseIds = parser.getProcessMiningModel().getAllCaseIds();
        Assert.assertEquals(174, processModelCaseIds.size());

        processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/filterPayload6-z.json");
        getInstance().setSNVersion(ServiceNowInstance.ZURICH);
        parser = ProcessMiningModelParserFactory.getParser(getInstance(), "abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));
        String processMiningModelVariantsJSONString = new TestUtility().loadProcessMiningModel("/model/filterPayload6-variants-z.json");
        parser.parseAndAppendVariants(processMiningModelVariantsJSONString);

        processModelCaseIds = parser.getProcessMiningModel().getAllCaseIds();
        Assert.assertEquals(28, processModelCaseIds.size());

        ServiceNowInstance instance = new ServiceNowInstance(ServiceNowTestCredentials.getInstanceName(), ServiceNowTestCredentials.getUserName(), ServiceNowTestCredentials.getPassword());
        SysAuditLogDAOREST wfDAO = new SysAuditLogDAOREST(instance);
        SysAuditLog sysAuditLog = wfDAO.findByIds(new SysAuditLogPK("incident"), processModelCaseIds);
        Assert.assertNotNull(sysAuditLog);
        // We will assume there are minimum 3 audit logs per record.
        Assert.assertTrue(sysAuditLog.getLog().size() > processModelCaseIds.size()*3);
    }
}
