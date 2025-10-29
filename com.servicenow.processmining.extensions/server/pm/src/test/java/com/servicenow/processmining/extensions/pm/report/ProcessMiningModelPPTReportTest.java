package com.servicenow.processmining.extensions.pm.report;

import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelParser;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelParserFactory;
import com.servicenow.processmining.extensions.sn.core.ServiceNowInstance;
import com.servicenow.processmining.extensions.sn.core.ServiceNowTestCredentials;
import com.servicenow.processmining.extensions.sn.core.TestUtility;

import org.junit.Assert;
import org.junit.Test;

public class ProcessMiningModelPPTReportTest
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
    public void test()
    {
        boolean runSingleTest = true;
        if (runSingleTest) {
            test3();
        }
        else {
            test1();
            test2();
            test3();
        }
    }

    private void test1()
    {
        getInstance().setSNVersion(ServiceNowInstance.VANCOUVER);
        String processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/filterPayloadIncidentEmailChannel-v.json");
        ProcessMiningModelParser parser = ProcessMiningModelParserFactory.getParser(getInstance(), "abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));

        ProcessMiningModelFilterPowerpointReport report = new ProcessMiningModelFilterPowerpointReport(parser.getProcessMiningModel());
        report.createReport();
    }

    // Test with 1 Happy Path Filters.
    private void test2()
    {
        getInstance().setSNVersion(ServiceNowInstance.WASHINGTON);
        String processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/filterPayload5-w.json");
        ProcessMiningModelParser parser = ProcessMiningModelParserFactory.getParser(getInstance(), "abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));

        ProcessMiningModelFilterPowerpointReport report = new ProcessMiningModelFilterPowerpointReport(parser.getProcessMiningModel());
        report.createReport();
    }

    // Test with 2 Happy Path Filters.
    private void test3()
    {
        getInstance().setSNVersion(ServiceNowInstance.WASHINGTON);
        String processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/filterPayload6-w.json");
        ProcessMiningModelParser parser = ProcessMiningModelParserFactory.getParser(getInstance(), "abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));

        ProcessMiningModelFilterPowerpointReport report = new ProcessMiningModelFilterPowerpointReport(parser.getProcessMiningModel());
        report.createReport();
    }
}