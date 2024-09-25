package com.servicenow.processmining.extensions.bpmn;

import org.junit.Assert;
import org.junit.Test;

import com.servicenow.processmining.extensions.pm.bpmn.BPMNProcessGenerator;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelParser;
import com.servicenow.processmining.extensions.sn.core.TestUtility;

public class BPMNGenerationTest
{
    @Test
    public void test()
    {
        String processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/payload1-v.json");
        ProcessMiningModelParser parser = new ProcessMiningModelParser("abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));

        if (parser.getProcessMiningModel().getAggregate().getCaseCount() > 0) {
            BPMNProcessGenerator generator = new BPMNProcessGenerator(parser.getProcessMiningModel());
            generator.createBPMNProcessFile();
        }
    }    
}
