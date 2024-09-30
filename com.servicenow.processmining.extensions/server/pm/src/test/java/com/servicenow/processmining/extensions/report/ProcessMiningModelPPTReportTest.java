package com.servicenow.processmining.extensions.report;

import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelParser;
import com.servicenow.processmining.extensions.pm.report.ProcessMiningModelFilterPowerpointReport;
import com.servicenow.processmining.extensions.sn.core.TestUtility;

import org.junit.Assert;
import org.junit.Test;

public class ProcessMiningModelPPTReportTest
{
    @Test
    public void test()
    {
        boolean runSingleTest = true;
        if (!runSingleTest) {
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
        String processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/filterPayloadIncidentEmailChannel-v.json");
        ProcessMiningModelParser parser = new ProcessMiningModelParser("abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));

        ProcessMiningModelFilterPowerpointReport report = new ProcessMiningModelFilterPowerpointReport(parser.getProcessMiningModel());
        report.createReport();
    }

    // Test with 1 Happy Path Filters.
    private void test2()
    {
        String processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/filterPayload5-w.json");
        ProcessMiningModelParser parser = new ProcessMiningModelParser("abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));

        ProcessMiningModelFilterPowerpointReport report = new ProcessMiningModelFilterPowerpointReport(parser.getProcessMiningModel());
        report.createReport();
    }

    // Test with 2 Happy Path Filters.
    private void test3()
    {
        String processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/filterPayload5-w.json");
        ProcessMiningModelParser parser = new ProcessMiningModelParser("abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));

        ProcessMiningModelFilterPowerpointReport report = new ProcessMiningModelFilterPowerpointReport(parser.getProcessMiningModel());
        report.createReport();
    }
}