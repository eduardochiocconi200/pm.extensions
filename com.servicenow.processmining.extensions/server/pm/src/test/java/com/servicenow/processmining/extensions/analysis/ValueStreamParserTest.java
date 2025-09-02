package com.servicenow.processmining.extensions.analysis;

import com.servicenow.processmining.extensions.pm.analysis.goldratt.ValueStreamAnalysis;
import com.servicenow.processmining.extensions.pm.analysis.goldratt.ValueStreamParser;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelParser;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelParserFactory;
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
        getInstance().setSNVersion(ServiceNowInstance.WASHINGTON);
        ProcessMiningModelParser parser = ProcessMiningModelParserFactory.getParser(getInstance(), "fc3a14dd2b67a210c9a1f9b36e91bf0f");
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
