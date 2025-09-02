package com.servicenow.processmining.extensions.model;

import com.servicenow.processmining.extensions.pm.dao.ProcessMiningModelRetrieval;
import com.servicenow.processmining.extensions.pm.dao.ProcessMiningModelRetrievalFactory;
import com.servicenow.processmining.extensions.pm.dao.ProcessMiningModelVersionDAOREST;
import com.servicenow.processmining.extensions.pm.entities.ProcessMiningModelVersion;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelParser;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelParserFactory;
import com.servicenow.processmining.extensions.sn.core.ServiceNowInstance;
import com.servicenow.processmining.extensions.sn.core.ServiceNowTestCredentials;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessMiningVariantRetrievalTest
{
    private ServiceNowInstance instance = null;

    @Test
    public void test()
    {
        ProcessMiningModelVersionDAOREST dao = new ProcessMiningModelVersionDAOREST(getInstance());
        for (ProcessMiningModelVersion version : dao.findAll()) {
            String modelVersionId = version.getPK().toString();
            ProcessMiningModelRetrieval pmmr = ProcessMiningModelRetrievalFactory.getProcessMiningRetrieval(getInstance(), modelVersionId);
            if (pmmr.runEmptyFilter()) {
                ProcessMiningModelParser pmmp = ProcessMiningModelParserFactory.getParser(getInstance(), modelVersionId);
                if (pmmp.parse(pmmr.getProcessMiningModelJSONString())) {
                    logger.info("Retrieved and parsed Process Mining Model successfully for project: (" + version.getName() + ") - (" + version.getLastMinedTime() + ").");
                    String entityId = pmmp.getProcessMiningModel().getEntities().get(0).getTableId();
                    int numberOfVariants = pmmp.getProcessMiningModel().getTotalVariants();
                    ProcessMiningModelRetrieval pmmr2 = ProcessMiningModelRetrievalFactory.getProcessMiningVariantRetrieval(instance, modelVersionId, entityId, numberOfVariants);
                    Assert.assertTrue(pmmr2.runEmptyFilter());
                    ProcessMiningModelParser pmmp2 = ProcessMiningModelParserFactory.getParser(getInstance(), modelVersionId);
                    if (pmmp2.parse(pmmr2.getProcessMiningModelJSONString())) {
                        if (pmmp.getProcessMiningModel().getTotalVariants() != -1) {
                            Assert.assertEquals(pmmp.getProcessMiningModel().getTotalVariants(), pmmp2.getProcessMiningModel().getVariants().size());
                        }
                        else {
                            Assert.assertTrue(pmmp2.getProcessMiningModel().getVariants().size() <= 1000);
                        }
                    }
                }
            }
            Assert.assertNull(pmmr.getErrorMessage());
        }
    }

    public ServiceNowInstance getInstance()
    {
        if (instance == null) {
            instance = new ServiceNowInstance(snInstance, snUser, snPassword);
            instance.setSNVersion(ServiceNowInstance.LATEST);
        }

        return instance;
    }

    private static final String snInstance = ServiceNowTestCredentials.getInstanceName();
    private static final String snUser = ServiceNowTestCredentials.getUserName();
    private static final String snPassword = ServiceNowTestCredentials.getPassword();

    private static final Logger logger = LoggerFactory.getLogger(ProcessMiningModelTest.class);
}
