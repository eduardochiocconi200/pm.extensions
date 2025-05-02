package com.servicenow.processmining.extensions.pm.dao;

import com.servicenow.processmining.extensions.sn.core.ServiceNowInstance;
import com.servicenow.processmining.extensions.sn.dao.PlatformVersionDAOREST;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessMiningModelRetrievalFactory
{
	private static boolean platformVersionChecked = false;

	private static void checkPlatformVersion(ServiceNowInstance snInstance)
	{
		if (!platformVersionChecked) {
			PlatformVersionDAOREST platformVerDAO = new PlatformVersionDAOREST(snInstance);
			snInstance.setSNVersion(platformVerDAO.getVersion());
		}
	}

	public static ProcessMiningModelRetrieval getProcessMiningRetrieval(final ServiceNowInstance snInstance, final String modelVersionId)
	{
		checkPlatformVersion(snInstance);

		String snVersion = snInstance.getSNVersion();
		if (snVersion.equals(ServiceNowInstance.YOKOHAMA)) {
			logger.debug("Creating Yokohama Filter Retriever");
			return new ProcessMiningModelRetrievalYokohama(snInstance, modelVersionId);
		}
		else if (snVersion.equals(ServiceNowInstance.XANADU)) {
			logger.debug("Creating Xanadu Filter Retriever");
			return new ProcessMiningModelRetrievalXanadu(snInstance, modelVersionId);
		}
		else if (snVersion.equals(ServiceNowInstance.WASHINGTON)) {
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
			throw new RuntimeException("Not supported ServiceNow Platform release: (" + snVersion + ")");
		}
	}

	public static ProcessMiningModelRetrieval getProcessMiningVariantRetrieval(final ServiceNowInstance snInstance, final String modelVersionId, final String entityId, final int numberOfVariants)
	{
		checkPlatformVersion(snInstance);

		String snVersion = snInstance.getSNVersion();
		if (snVersion.equals(ServiceNowInstance.YOKOHAMA)) {
			logger.debug("Creating Yokohama Filter Retriever");
			return new ProcessMiningVariantRetrievalYokohama(snInstance, modelVersionId, entityId, numberOfVariants);
		}
		else if (snVersion.equals(ServiceNowInstance.XANADU)) {
			logger.debug("Creating Xanadu Filter Retriever");
			return new ProcessMiningVariantRetrievalXanadu(snInstance, modelVersionId, entityId, numberOfVariants);
		}
		else if (snVersion.equals(ServiceNowInstance.WASHINGTON)) {
			logger.debug("Creating Washington/Xanadu Filter Retriever");
			return new ProcessMiningVariantRetrievalWashington(snInstance, modelVersionId, entityId, numberOfVariants);
		}
		else {
			throw new RuntimeException("Not supported ServiceNow Platform release: (" + snVersion + ")");
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(ProcessMiningModelFilterDAOREST.class);
}
