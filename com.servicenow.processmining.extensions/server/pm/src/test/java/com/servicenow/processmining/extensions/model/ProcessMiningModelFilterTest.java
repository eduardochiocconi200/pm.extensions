package com.servicenow.processmining.extensions.model;

import com.servicenow.processmining.extensions.pm.dao.ProcessMiningModelFilterDAOREST;
import com.servicenow.processmining.extensions.pm.dao.ProcessMiningModelRetrieval;
import com.servicenow.processmining.extensions.pm.dao.ProcessMiningModelRetrievalFactory;
import com.servicenow.processmining.extensions.pm.dao.ProcessMiningModelVersionDAOREST;
import com.servicenow.processmining.extensions.pm.entities.ProcessMiningModelVersionFilter;
import com.servicenow.processmining.extensions.pm.entities.ProcessMiningModelVersion;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelParser;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelParserFactory;
import com.servicenow.processmining.extensions.sn.core.ServiceNowInstance;
import com.servicenow.processmining.extensions.sn.core.ServiceNowTestCredentials;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessMiningModelFilterTest
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
                ProcessMiningModelFilterDAOREST pmmfDAO = new ProcessMiningModelFilterDAOREST(instance);
                List<ProcessMiningModelVersionFilter> filters = pmmfDAO.findAllByProcessModel(modelVersionId, false);
                Assert.assertEquals(pmmp.getProcessMiningModel().getFilters().size(), filters.size());
            }
            else {
                Assert.assertTrue(false);
            }
        }
    }

    @Test
    public void test2()
    {
        ProcessMiningModelVersionDAOREST dao = new ProcessMiningModelVersionDAOREST(getInstance());
        for (ProcessMiningModelVersion version : dao.findAll()) {
            String modelVersionId = version.getPK().toString();
            ProcessMiningModelRetrieval pmmr = ProcessMiningModelRetrievalFactory.getProcessMiningRetrieval(getInstance(), modelVersionId);
            if (pmmr.runEmptyFilter()) {
                ProcessMiningModelParser pmmp = ProcessMiningModelParserFactory.getParser(getInstance(), modelVersionId);
                if (pmmp.parse(pmmr.getProcessMiningModelJSONString())) {
                    logger.info("Retrieved and parsed Process Mining Model (" + pmmp.getProcessMiningModel().getName() + ") successfully!");
                    ProcessMiningModelFilterDAOREST pmmfDAO = new ProcessMiningModelFilterDAOREST(getInstance());
                    List<ProcessMiningModelVersionFilter> filters = pmmfDAO.findAllByProcessModel(modelVersionId, false);
                    Assert.assertEquals(pmmp.getProcessMiningModel().getFilters().size(), filters.size());
                }
                else {
                    Assert.assertTrue(false);
                }
            }
            else {
                Assert.assertTrue(false);
            }
        }
    }

    public ServiceNowInstance getInstance()
    {
        if (instance == null) {
            instance = new ServiceNowInstance(snInstance, snUser, snPassword);
        }

        return instance;
    }

    private static final String snInstance = ServiceNowTestCredentials.getInstanceName();
    private static final String snUser = ServiceNowTestCredentials.getUserName();
    private static final String snPassword = ServiceNowTestCredentials.getPassword();

    private static final Logger logger = LoggerFactory.getLogger(ProcessMiningModelFilterTest.class);
}
