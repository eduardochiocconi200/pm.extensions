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
        boolean runAll = false;
        if (!runAll) {
            test2();
        }
        else {
            test1();
            test2();
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

    private void test2()
    {
        String processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/filterPayload5-w.json");
        ProcessMiningModelParser parser = new ProcessMiningModelParser("abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));

        ProcessMiningModelFilterPowerpointReport report = new ProcessMiningModelFilterPowerpointReport(parser.getProcessMiningModel());
        report.createReport();
    }
}