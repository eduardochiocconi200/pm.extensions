package com.servicenow.processmining.extensions.report;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelParser;
import com.servicenow.processmining.extensions.pm.report.ProcessMiningModelFilterDataSource;
import com.servicenow.processmining.extensions.sn.core.TestUtility;

public class ProcessMiningModelParser1Test
{
    @Test
    public void test()
    {
        boolean singleTest = false;
        if (singleTest) {
            test18();
        }
        else {
            test1();
            test2();
            test3();
            test4();
            test5();
            test6();
            test7();
            test8();
            test9();
            test10();
            test11();
            test12();
            test13();
            test14();
            test15();
            test16();
            test17();
            test18();
        }
    }

    public void test1()
    {
        String processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/payload1-v.json");
        ProcessMiningModelParser parser = new ProcessMiningModelParser("abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));

        // Run Variation Analysis.
        ProcessMiningModelFilterDataSource ds = new ProcessMiningModelFilterDataSource(parser.getProcessMiningModel());
        Assert.assertTrue(ds.runAllAnalysis());
        logger.debug(ds.getFindings().toString());
    }

    public void test2()
    {
        String processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/payload2-v.json");
        ProcessMiningModelParser parser = new ProcessMiningModelParser("abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));

        // Run Variation Analysis.
        ProcessMiningModelFilterDataSource ds = new ProcessMiningModelFilterDataSource(parser.getProcessMiningModel());
        Assert.assertTrue(ds.runAllAnalysis());
        logger.debug(ds.getFindings().toString());
    }

    public void test3()
    {
        String processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/payload3-v.json");
        ProcessMiningModelParser parser = new ProcessMiningModelParser("abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));

        // Run Variation Analysis.
        ProcessMiningModelFilterDataSource ds = new ProcessMiningModelFilterDataSource(parser.getProcessMiningModel());
        Assert.assertTrue(ds.runAllAnalysis());
        logger.debug(ds.getFindings().toString());
    }

    public void test4()
    {
        String processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/payload4-v.json");
        ProcessMiningModelParser parser = new ProcessMiningModelParser("abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));

        // Run Variation Analysis.
        ProcessMiningModelFilterDataSource ds = new ProcessMiningModelFilterDataSource(parser.getProcessMiningModel());
        Assert.assertTrue(ds.runAllAnalysis());
        logger.debug(ds.getFindings().toString());
    }

    public void test5()
    {
        String processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/payload3b-v.json");
        ProcessMiningModelParser parser = new ProcessMiningModelParser("abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));

        // Run Variation Analysis.
        ProcessMiningModelFilterDataSource ds = new ProcessMiningModelFilterDataSource(parser.getProcessMiningModel());
        Assert.assertTrue(ds.runAllAnalysis());
        logger.debug(ds.getFindings().toString());
    }

    public void test6()
    {
        String processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/filterPayload1-v.json");
        ProcessMiningModelParser parser = new ProcessMiningModelParser("abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));
        Assert.assertEquals(1, parser.getProcessMiningModel().getFilters().size());

        // Run Variation Analysis.
        ProcessMiningModelFilterDataSource ds = new ProcessMiningModelFilterDataSource(parser.getProcessMiningModel());
        Assert.assertTrue(ds.runAllAnalysis());
        logger.debug(ds.getFindings().toString());
    }

    public void test7()
    {
        String processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/filterPayload2-v.json");
        ProcessMiningModelParser parser = new ProcessMiningModelParser("abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));
        Assert.assertEquals(3, parser.getProcessMiningModel().getFilters().size());

        // Run Variation Analysis.
        ProcessMiningModelFilterDataSource ds = new ProcessMiningModelFilterDataSource(parser.getProcessMiningModel());
        Assert.assertTrue(ds.runAllAnalysis());
        logger.debug(ds.getFindings().toString());
    }

    public void test8()
    {
        String processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/filterPayload3-v.json");
        ProcessMiningModelParser parser = new ProcessMiningModelParser("abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));
        Assert.assertEquals(3, parser.getProcessMiningModel().getFilters().size());

        // Run Variation Analysis.
        ProcessMiningModelFilterDataSource ds = new ProcessMiningModelFilterDataSource(parser.getProcessMiningModel());
        Assert.assertTrue(ds.runAllAnalysis());
        logger.debug(ds.getFindings().toString());
    }

    public void test9()
    {
        String processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/filterPayload4-v.json");
        ProcessMiningModelParser parser = new ProcessMiningModelParser("abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));
        Assert.assertEquals(3, parser.getProcessMiningModel().getFilters().size());

        // Run Variation Analysis.
        ProcessMiningModelFilterDataSource ds = new ProcessMiningModelFilterDataSource(parser.getProcessMiningModel());
        Assert.assertTrue(ds.runAllAnalysis());
        logger.debug(ds.getFindings().toString());
    }

    public void test10()
    {
        String processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/filterPayloadIncidentEmailChannel-v.json");
        ProcessMiningModelParser parser = new ProcessMiningModelParser("abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));
        Assert.assertEquals(3, parser.getProcessMiningModel().getFilters().size());

        // Run Variation Analysis.
        ProcessMiningModelFilterDataSource ds = new ProcessMiningModelFilterDataSource("Email Channel", parser.getProcessMiningModel());
        Assert.assertTrue(ds.runAllAnalysis());
        logger.debug(ds.getFindings().toString());
    }

    public void test11()
    {
        String processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/filterPayloadIncidentP1PhoneChannel-v.json");
        ProcessMiningModelParser parser = new ProcessMiningModelParser("abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));
        Assert.assertEquals(3, parser.getProcessMiningModel().getFilters().size());

        // Run Variation Analysis.
        ProcessMiningModelFilterDataSource ds = new ProcessMiningModelFilterDataSource("Phone Channel & P1", parser.getProcessMiningModel());
        Assert.assertTrue(ds.runAllAnalysis());
        logger.debug(ds.getFindings().toString());
    }

    public void test12()
    {
        String processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/filterPayloadIncidentPhoneChannel-v.json");
        ProcessMiningModelParser parser = new ProcessMiningModelParser("abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));
        Assert.assertEquals(3, parser.getProcessMiningModel().getFilters().size());

        // Run Variation Analysis.
        ProcessMiningModelFilterDataSource ds = new ProcessMiningModelFilterDataSource("Phone Channel", parser.getProcessMiningModel());
        Assert.assertTrue(ds.runAllAnalysis());
        logger.debug(ds.getFindings().toString());
    }

    public void test13()
    {
        String processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/payload5-u-cirion.json");
        ProcessMiningModelParser parser = new ProcessMiningModelParser("abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));
        Assert.assertEquals(4, parser.getProcessMiningModel().getFilters().size());

        // Run Variation Analysis.
        ProcessMiningModelFilterDataSource ds = new ProcessMiningModelFilterDataSource(parser.getProcessMiningModel());
        Assert.assertTrue(ds.runAllAnalysis());
        logger.debug(ds.getFindings().toString());
    }

    public void test14()
    {
        String processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/payload6-u-cirion.json");
        ProcessMiningModelParser parser = new ProcessMiningModelParser("abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));
        Assert.assertEquals(3, parser.getProcessMiningModel().getFilters().size());

        // Run Variation Analysis.
        ProcessMiningModelFilterDataSource ds = new ProcessMiningModelFilterDataSource(parser.getProcessMiningModel());
        Assert.assertTrue(ds.runAllAnalysis());
        logger.debug(ds.getFindings().toString());
    }

    public void test15()
    {
        String processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/payload7-v.json");
        ProcessMiningModelParser parser = new ProcessMiningModelParser("abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));
        Assert.assertEquals(0, parser.getProcessMiningModel().getFilters().size());

        // Run Variation Analysis.
        ProcessMiningModelFilterDataSource ds = new ProcessMiningModelFilterDataSource(parser.getProcessMiningModel());
        Assert.assertTrue(ds.runAllAnalysis());
        logger.debug(ds.getFindings().toString());
    }

    public void test16()
    {
        String processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/payload8-w.json");
        ProcessMiningModelParser parser = new ProcessMiningModelParser("abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));
        Assert.assertEquals(0, parser.getProcessMiningModel().getFilters().size());

        // Run Variation Analysis.
        ProcessMiningModelFilterDataSource ds = new ProcessMiningModelFilterDataSource(parser.getProcessMiningModel());
        Assert.assertTrue(ds.runAllAnalysis());
        logger.debug(ds.getFindings().toString());
    }

    public void test17()
    {
        String processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/filterPayload5-w.json");
        ProcessMiningModelParser parser = new ProcessMiningModelParser("abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));
        Assert.assertEquals(4, parser.getProcessMiningModel().getFilters().size());

        // Run Variation Analysis.
        ProcessMiningModelFilterDataSource ds = new ProcessMiningModelFilterDataSource(parser.getProcessMiningModel());
        Assert.assertTrue(ds.runAllAnalysis());
        logger.debug(ds.getFindings().toString());
    }

    public void test18()
    {
        String processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/filterPayload6-w.json");
        ProcessMiningModelParser parser = new ProcessMiningModelParser("abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));
        Assert.assertEquals(5, parser.getProcessMiningModel().getFilters().size());

        // Run Variation Analysis.
        ProcessMiningModelFilterDataSource ds = new ProcessMiningModelFilterDataSource(parser.getProcessMiningModel());
        Assert.assertTrue(ds.runAllAnalysis());
        logger.debug(ds.getFindings().toString());
    }

    private static final Logger logger = LoggerFactory.getLogger(ProcessMiningModelParser1Test.class);
}