package com.servicenow.processmining.extensions.report;

import org.junit.Assert;
import org.junit.Test;

import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelParser;
import com.servicenow.processmining.extensions.pm.report.ProcessMiningModelExcelReport;
import com.servicenow.processmining.extensions.sn.core.TestUtility;

public class ProcessMiningModelExcelReportTest
{
    @Test
    public void test()
    {
        boolean executeAll = true;
        if (executeAll) {
            test1();
            test2();
            test3();
            test4();
            test5();
            test6();
            test7();
            test8();
            test9();
        }
        else {
            test3();
        }
    }

    private void test1()
    {
        String processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/payload1-v.json");
        ProcessMiningModelParser parser = new ProcessMiningModelParser("abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));

        ProcessMiningModelExcelReport report = new ProcessMiningModelExcelReport(parser.getProcessMiningModel());
        report.createReport();
    }

    private void test2()
    {
        String processMiningModelJSONString = (new TestUtility()).loadProcessMiningModel("/model/payload2-v.json");
        ProcessMiningModelParser parser = new ProcessMiningModelParser("abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));

        ProcessMiningModelExcelReport report = new ProcessMiningModelExcelReport(parser.getProcessMiningModel());
        report.createReport();
    }

    private void test3()
    {
        String processMiningModelJSONString = (new TestUtility()).loadProcessMiningModel("/model/payload3-v.json");
        ProcessMiningModelParser parser = new ProcessMiningModelParser("abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));

        ProcessMiningModelExcelReport report = new ProcessMiningModelExcelReport(parser.getProcessMiningModel());
        report.createReport();
    }

    private void test4()
    {
        String processMiningModelJSONString = (new TestUtility()).loadProcessMiningModel("/model/payload3b-v.json");
        ProcessMiningModelParser parser = new ProcessMiningModelParser("abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));

        ProcessMiningModelExcelReport report = new ProcessMiningModelExcelReport(parser.getProcessMiningModel());
        report.createReport();
    }

    private void test5()
    {
        String processMiningModelJSONString = (new TestUtility()).loadProcessMiningModel("/model/payload4-v.json");
        ProcessMiningModelParser parser = new ProcessMiningModelParser("abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));

        ProcessMiningModelExcelReport report = new ProcessMiningModelExcelReport(parser.getProcessMiningModel());
        report.createReport();
    }

    private void test6()
    {
        String processMiningModelJSONString = (new TestUtility()).loadProcessMiningModel("/model/filterPayload1-v.json");
        ProcessMiningModelParser parser = new ProcessMiningModelParser("abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));

        ProcessMiningModelExcelReport report = new ProcessMiningModelExcelReport(parser.getProcessMiningModel());
        report.createReport();
    }

    private void test7()
    {
        String processMiningModelJSONString = (new TestUtility()).loadProcessMiningModel("/model/filterPayload2-v.json");
        ProcessMiningModelParser parser = new ProcessMiningModelParser("abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));

        ProcessMiningModelExcelReport report = new ProcessMiningModelExcelReport(parser.getProcessMiningModel());
        report.createReport();
    }

    private void test8()
    {
        String processMiningModelJSONString = (new TestUtility()).loadProcessMiningModel("/model/filterPayload3-v.json");
        ProcessMiningModelParser parser = new ProcessMiningModelParser("abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));

        ProcessMiningModelExcelReport report = new ProcessMiningModelExcelReport(parser.getProcessMiningModel());
        report.createReport();
    }

    private void test9()
    {
        String processMiningModelJSONString = (new TestUtility()).loadProcessMiningModel("/model/filterPayload4-v.json");
        ProcessMiningModelParser parser = new ProcessMiningModelParser("abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));

        ProcessMiningModelExcelReport report = new ProcessMiningModelExcelReport(parser.getProcessMiningModel());
        report.createReport();
    }
}