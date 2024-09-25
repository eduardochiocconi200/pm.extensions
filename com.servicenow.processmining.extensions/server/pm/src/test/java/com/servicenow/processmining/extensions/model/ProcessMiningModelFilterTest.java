package com.servicenow.processmining.extensions.model;

import com.servicenow.processmining.extensions.pm.dao.ProcessMiningModelFilterDAOREST;
import com.servicenow.processmining.extensions.pm.dao.ProcessMiningModelRetrieval;
import com.servicenow.processmining.extensions.pm.dao.ProcessMiningModelRetrievalVancouver;
import com.servicenow.processmining.extensions.pm.dao.ProcessMiningModelVersionDAOREST;
import com.servicenow.processmining.extensions.pm.entities.ProcessMiningModelVersionFilter;
import com.servicenow.processmining.extensions.pm.entities.ProcessMiningModelVersion;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelParser;
import com.servicenow.processmining.extensions.sn.core.ServiceNowInstance;

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
        String model1 = null;
        String model2 = null;
        ServiceNowInstance snInstance = getInstance();
        String modelVersionId = "ac0c7a1f93a80a506e79bb1e1dba10af";
        ProcessMiningModelRetrieval pmmr = new ProcessMiningModelRetrievalVancouver(snInstance, modelVersionId);
        if (pmmr.runEmptyFilter()) {
            ProcessMiningModelParser pmmp = new ProcessMiningModelParser(modelVersionId);
            model1 = pmmr.getProcessMiningModelJSONString();
            if (pmmp.parse(pmmr.getProcessMiningModelJSONString())) {
                logger.info("Retrieved and parsed Process Mining Model successfully!");
                ProcessMiningModelFilterDAOREST pmmfDAO = new ProcessMiningModelFilterDAOREST(instance);
                List<ProcessMiningModelVersionFilter> filters = pmmfDAO.findAllByProcessModel(modelVersionId, false);
                for (ProcessMiningModelVersionFilter filter : filters) {
                    pmmr = new ProcessMiningModelRetrievalVancouver(snInstance, modelVersionId);
                    if (pmmr.runBreakdownFilter(filter.getEntityId(), filter.getCondition())) {
                        ProcessMiningModelParser pmmp2 = new ProcessMiningModelParser(modelVersionId);
                        model2 = pmmr.getProcessMiningModelJSONString();
                        Assert.assertNotEquals(model1, model2);
                        if (pmmp2.parse(pmmr.getProcessMiningModelJSONString())) {
                            logger.info("Retrieved and parsed Process Mining Model successfully!");
                        }
                    }
                }
            }
        }
    }

    @Test
    public void test2()
    {
        ProcessMiningModelVersionDAOREST dao = new ProcessMiningModelVersionDAOREST(getInstance());
        for (ProcessMiningModelVersion version : dao.findAll()) {
            String modelVersionId = version.getPK().toString();
            ProcessMiningModelRetrieval pmmr = new ProcessMiningModelRetrievalVancouver(getInstance(), modelVersionId);
            if (pmmr.runEmptyFilter()) {
                ProcessMiningModelParser pmmp = new ProcessMiningModelParser(modelVersionId);
                if (pmmp.parse(pmmr.getProcessMiningModelJSONString())) {
                    logger.info("Retrieved and parsed Process Mining Model successfully!");
                    ProcessMiningModelFilterDAOREST pmmfDAO = new ProcessMiningModelFilterDAOREST(getInstance());
                    List<ProcessMiningModelVersionFilter> filters = pmmfDAO.findAllByProcessModel(modelVersionId, false);
                    for (ProcessMiningModelVersionFilter filter : filters) {
                        pmmr = new ProcessMiningModelRetrievalVancouver(getInstance(), modelVersionId);
                        if (pmmr.runBreakdownFilter(filter.getEntityId(), filter.getJSONCondition())) {
                            ProcessMiningModelParser pmmp2 = new ProcessMiningModelParser(modelVersionId);
                            if (pmmp2.parse(pmmr.getProcessMiningModelJSONString())) {
                                logger.info("Retrieved and parsed Process Mining Model successfully!");
                                Assert.assertTrue(true);
                            }
                            else {
                                Assert.assertTrue(false);
                            }
                        }
                    }
                }
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

    private static final String snInstance = "empechiocconi2";
    private static final String snUser = "admin";
    private static final String snPassword = "StarWars!1";

    private static final Logger logger = LoggerFactory.getLogger(ProcessMiningModelFilterTest.class);
}
