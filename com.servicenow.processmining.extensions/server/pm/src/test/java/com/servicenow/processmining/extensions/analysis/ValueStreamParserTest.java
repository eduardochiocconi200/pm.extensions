package com.servicenow.processmining.extensions.analysis;

import com.servicenow.processmining.extensions.pm.analysis.goldratt.ValueStreamAnalysis;
import com.servicenow.processmining.extensions.pm.analysis.goldratt.ValueStreamParser;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelParser;
import com.servicenow.processmining.extensions.sn.core.ServiceNowInstance;
import com.servicenow.processmining.extensions.sn.core.ServiceNowTestCredentials;
import com.servicenow.processmining.extensions.sn.core.TestUtility;

import org.junit.Assert;
import org.junit.Test;

public class ValueStreamParserTest
{
    private ServiceNowInstance instance = null;

    @Test
    public void execute()
    {
        test1();
    }

    private void test1()
    {
        String processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/filterPayload5-w.json");
        ProcessMiningModelParser parser = new ProcessMiningModelParser("48f9d6f82b6e5a50c09efdac5e91bf3d");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));

        String valueStreamJSONString = new TestUtility().loadProcessMiningModel("/analysis/valueStream1.json");
        ValueStreamParser vsp = new ValueStreamParser();
        Assert.assertTrue(vsp.parse(valueStreamJSONString));
        System.out.println(vsp.getValueStream().toString());

        ValueStreamAnalysis vsa = new ValueStreamAnalysis(getInstance(), parser.getProcessMiningModel(), vsp.getValueStream());
        Assert.assertTrue(vsa.run());
    }

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
}
