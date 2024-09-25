package com.servicenow.processmining.extensions.pm.dao;

import com.servicenow.processmining.extensions.sn.core.ServiceNowInstance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessMiningModelRetrievalFactory
{
	public static ProcessMiningModelRetrieval getProcessMiningRetrieval(final ServiceNowInstance snInstance, final String modelVersionId)
	{
        String snVersion = snInstance.getSNVersion();
		if (snVersion.equals(ServiceNowInstance.WASHINGTON)) {
			logger.debug("Creating Washington Filter Retriever");
			return new ProcessMiningModelRetrievalWashington(snInstance, modelVersionId);
		}
		else if (snVersion.equals(ServiceNowInstance.VANCOUVER)) {
			logger.debug("Creating Vancouver Filter Retriever");
			return new ProcessMiningModelRetrievalVancouver(snInstance, modelVersionId);
		}
		else if (snVersion.equals(ServiceNowInstance.UTAH)) {
			logger.debug("Creating Utah Filter Retriever");
			return new ProcessMiningModelRetrievalUtah(snInstance, modelVersionId);
		}
		else {
			throw new RuntimeException("Not supported ServiceNow Platform release");
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(ProcessMiningModelFilterDAOREST.class);
}