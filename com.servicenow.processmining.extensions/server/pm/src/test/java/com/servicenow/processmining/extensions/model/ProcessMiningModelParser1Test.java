package com.servicenow.processmining.extensions.model;

import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelParser;
import com.servicenow.processmining.extensions.pm.report.ProcessMiningModelFilterDataSource;
import com.servicenow.processmining.extensions.sn.core.TestUtility;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        Assert.assertEquals(10, parser.getProcessMiningModel().getVariants().size());
        System.out.println("Keys: (" + parser.getProcessMiningModel().getVariants().keySet() + ")");
        Assert.assertEquals(160, parser.getProcessMiningModel().getVariants().get("f4dbf4607130d4eedcbb2b977d7f6f42").getCaseIds().size());
        Assert.assertEquals(11, parser.getProcessMiningModel().getVariants().get("84e2704c49e004085c8a7e4e68e49999").getCaseIds().size());
        Assert.assertEquals(3, parser.getProcessMiningModel().getVariants().get("9f1c43f8dbe6f94a319b0fca20163972").getCaseIds().size());
        Assert.assertEquals(33, parser.getProcessMiningModel().getVariants().get("2ccfdf270114304997b5c4e2796df02e").getCaseIds().size());
        Assert.assertEquals(64, parser.getProcessMiningModel().getVariants().get("09cd5db9bec8d6fa3897047c27db49cb").getCaseIds().size());
        Assert.assertEquals(22, parser.getProcessMiningModel().getVariants().get("b8918322789ef61f599c8b99533f2440").getCaseIds().size());
        Assert.assertEquals(68, parser.getProcessMiningModel().getVariants().get("74fffa0cd22d7aab39b384816a039ee5").getCaseIds().size());
        Assert.assertEquals(5, parser.getProcessMiningModel().getVariants().get("cd513baef3750bc7f152fb63cf6f8b2e").getCaseIds().size());
        Assert.assertEquals(5, parser.getProcessMiningModel().getVariants().get("6ef275c218b512da1caeb4cf23254a95").getCaseIds().size());
        Assert.assertEquals(4, parser.getProcessMiningModel().getVariants().get("5a12f781eac518bc2939cde766d97074").getCaseIds().size());

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