package com.servicenow.processmining.extensions.pm.dao;

import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelFilter;
import com.servicenow.processmining.extensions.sn.core.ServiceNowInstance;
import com.servicenow.processmining.extensions.sn.core.ServiceNowRESTService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ProcessMiningModelRetrieval
{
    protected ServiceNowInstance instance = null;
    protected String modelVersionId;
    protected String processMiningModelJSONString = null;
    protected String errorMessage = null;

    public ProcessMiningModelRetrieval(final ServiceNowInstance instance, final String modelVersionId)
    {
        this.instance = instance;
        this.modelVersionId = modelVersionId;
    }

    public ServiceNowInstance getInstance()
    {
        return instance;
    }

    public String getProcessModelVersionId()
    {
        return this.modelVersionId;
    }

    public String getProcessMiningModelJSONString()
    {
        return this.processMiningModelJSONString;
    }

    public boolean runEmptyFilter()
    {
        boolean result = true;
        ServiceNowRESTService snrs = new ServiceNowRESTService(getInstance());
        String url = "https://" + getInstance().getInstance() + ".service-now.com/api/now/graphql";
        logger.debug("Retrieving Process Mining GraphQL for versionId: '" + getProcessModelVersionId() + "'");
        String payload = getEmptyFilterPayload();
        processMiningModelJSONString = snrs.executePostRequest(url, payload);
        if (processMiningModelJSONString == null || (processMiningModelJSONString != null && processMiningModelJSONString.equals(""))) {
            logger.error("Error: (" + snrs.getErrorStatusCode() + ") -> (" + snrs.getErrorMessage() + ")");
            errorMessage = "Error: (" + snrs.getErrorStatusCode() + ") -> (" + snrs.getErrorMessage() + ")";
            result = false;
        }
        else {
            logger.debug("RESPONSE: (" + processMiningModelJSONString + ")");
            if (processMiningModelJSONString.indexOf(",\"errors\":[") > 0) {
                errorMessage = processMiningModelJSONString;
                processMiningModelJSONString = null;
                result = false;
            }
        }

        return result;
    }

    protected abstract String getEmptyFilterPayload();

    public boolean runFilter(final String entityId, final ProcessMiningModelFilter filter)
    {
        boolean result = true;
        ServiceNowRESTService snrs = new ServiceNowRESTService(getInstance());
        String url = "https://" + getInstance().getInstance() + ".service-now.com/api/now/graphql";
        logger.debug("Retrieving Process Mining GraphQL for versionId: '" + getProcessModelVersionId() + "'");
        String payload = getFilterPayload(entityId, filter);
        logger.debug("Payload: (" + payload + ")");
        processMiningModelJSONString = snrs.executePostRequest(url, payload);
        if (processMiningModelJSONString == null || (processMiningModelJSONString != null && processMiningModelJSONString.equals(""))) {
            logger.error("Error: (" + snrs.getErrorStatusCode() + ") -> (" + snrs.getErrorMessage() + ")");
            errorMessage = "Error: (" + snrs.getErrorStatusCode() + ") -> (" + snrs.getErrorMessage() + ")";
            result = false;
        }
        else {
            logger.debug("RESPONSE: (" + processMiningModelJSONString + ")");
            if (processMiningModelJSONString.indexOf(",\"errors\":[") > 0) {
                errorMessage = processMiningModelJSONString;
                processMiningModelJSONString = null;
                result = false;
            }
        }

        return result;
    }

    protected abstract String getFilterPayload(final String entityId, final ProcessMiningModelFilter filter);

    public String getErrorMessage()
    {
        return this.errorMessage;
    }

    private static final Logger logger = LoggerFactory.getLogger(ProcessMiningModelRetrieval.class);
}
