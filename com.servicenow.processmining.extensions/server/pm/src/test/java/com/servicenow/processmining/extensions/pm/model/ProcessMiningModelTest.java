package com.servicenow.processmining.extensions.pm.model;

import com.servicenow.processmining.extensions.pm.dao.ProcessMiningModelRetrieval;
import com.servicenow.processmining.extensions.pm.dao.ProcessMiningModelRetrievalFactory;
import com.servicenow.processmining.extensions.pm.dao.ProcessMiningModelVersionDAOREST;
import com.servicenow.processmining.extensions.pm.entities.ProcessMiningModelVersion;
import com.servicenow.processmining.extensions.sn.core.ServiceNowInstance;
import com.servicenow.processmining.extensions.sn.core.ServiceNowTestCredentials;

import org.junit.Assert;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessMiningModelTest
{
    private ServiceNowInstance instance = null;

    @Test
    public void test()
    {
        String modelVersionId = "ac0c7a1f93a80a506e79bb1e1dba10af";
        ProcessMiningModelRetrieval pmmr = ProcessMiningModelRetrievalFactory.getProcessMiningRetrieval(getInstance(), modelVersionId);
        if (pmmr.runEmptyFilter()) {
            ProcessMiningModelParser pmmp = ProcessMiningModelParserFactory.getParser(getInstance(), modelVersionId);
            if (pmmp.parse(pmmr.getProcessMiningModelJSONString())) {
                logger.info("Retrieved and parsed Process Mining Model successfully!");
            }
        }
    }

    @Test
    public void test2()
    {
        ProcessMiningModelVersionDAOREST dao = new ProcessMiningModelVersionDAOREST(getInstance());
        for (ProcessMiningModelVersion version : dao.findAll()) {
            // System.out.println("PROCESS: (" + version.getName() + ")\n, ID: (" + version.getProjectId() + ")");
            String modelVersionId = version.getPK().toString();
            ProcessMiningModelRetrieval pmmr = ProcessMiningModelRetrievalFactory.getProcessMiningRetrieval(getInstance(), modelVersionId);
            if (pmmr.runEmptyFilter()) {
                // System.out.println("QUERY RESULT: (" + pmmr.getProcessMiningModelJSONString() + ")");
                ProcessMiningModelParser pmmp = ProcessMiningModelParserFactory.getParser(getInstance(), modelVersionId);
                if (pmmp.parse(pmmr.getProcessMiningModelJSONString())) {
                    logger.info("Retrieved and parsed Process Mining Model successfully for project: (" + version.getName() + ") - (" + version.getLastMinedTime() + ").");
                    /*
	 	    System.out.println("Model Name: (" + pmmp.getProcessMiningModel().getName() + ").");
                    if (pmmp.getProcessMiningModel().getName().equals("Incident Analysis")) {
                        System.out.println("Model Name: (" + pmmp.getProcessMiningModel().getName() + ")");
                        System.out.println("Process Model JSON: (" + pmmr.getProcessMiningModelJSONString() + ")");
                    }
		    */
                }
            }
            Assert.assertNull(pmmr.getErrorMessage());
        }
    }

    public ServiceNowInstance getInstance()
    {
        if (instance == null) {
            instance = new ServiceNowInstance(ServiceNowTestCredentials.getInstanceName(), ServiceNowTestCredentials.getUserName(), ServiceNowTestCredentials.getPassword());
        }

        return instance;
    }

    private static final Logger logger = LoggerFactory.getLogger(ProcessMiningModelTest.class);
}
