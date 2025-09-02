package com.servicenow.processmining.extensions.bpmn;

import com.servicenow.processmining.extensions.pm.bpmn.BPMNProcessGenerator;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelParser;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelParserFactory;
import com.servicenow.processmining.extensions.sn.core.ServiceNowInstance;
import com.servicenow.processmining.extensions.sn.core.ServiceNowTestCredentials;
import com.servicenow.processmining.extensions.sn.core.TestUtility;

import org.junit.Assert;
import org.junit.Test;

public class BPMNGenerationTest
{
    private ServiceNowInstance instance = null;

        private ServiceNowInstance getInstance()
    {
        if (instance == null) {
            String snInstance = ServiceNowTestCredentials.getInstanceName();
            String user = ServiceNowTestCredentials.getUserName();
            String password = ServiceNowTestCredentials.getPassword();
            this.instance = new ServiceNowInstance(snInstance, user, password);
        }

        return instance;
    }

    @Test
    public void test()
    {
        test3();
        test1();
        test2();
        test3();
    }
    
    private void test1()
    {
        String processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/payload1-v.json");
        getInstance().setSNVersion(ServiceNowInstance.VANCOUVER);
        ProcessMiningModelParser parser = ProcessMiningModelParserFactory.getParser(getInstance(),"abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));

        if (parser.getProcessMiningModel().getAggregate().getCaseCount() > 0) {
            BPMNProcessGenerator generator = new BPMNProcessGenerator(parser.getProcessMiningModel());
            generator.createBPMNProcessFile();
        }
    }

    private void test2()
    {
        String processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/payload1-w.json");
        getInstance().setSNVersion(ServiceNowInstance.WASHINGTON);
        ProcessMiningModelParser parser = ProcessMiningModelParserFactory.getParser(getInstance(),"abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));

        if (parser.getProcessMiningModel().getAggregate().getCaseCount() > 0) {
            BPMNProcessGenerator generator = new BPMNProcessGenerator(parser.getProcessMiningModel());
            generator.createBPMNProcessFile();
        }
    }

    private void test3()
    {
        String processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/payload1-z.json");
        getInstance().setSNVersion(ServiceNowInstance.LATEST);
        ProcessMiningModelParser parser = ProcessMiningModelParserFactory.getParser(getInstance(),"abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));

        if (parser.getProcessMiningModel().getAggregate().getCaseCount() > 0) {
            BPMNProcessGenerator generator = new BPMNProcessGenerator(parser.getProcessMiningModel());
            generator.createBPMNProcessFile();
        }
    }
}