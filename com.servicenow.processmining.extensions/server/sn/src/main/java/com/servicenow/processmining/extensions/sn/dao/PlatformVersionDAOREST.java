package com.servicenow.processmining.extensions.sn.dao;

import com.servicenow.processmining.extensions.sn.core.ServiceNowInstance;
import com.servicenow.processmining.extensions.sn.core.ServiceNowRESTService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlatformVersionDAOREST
{
	private ServiceNowInstance instance = null;

	public PlatformVersionDAOREST(final ServiceNowInstance instance)
	{
		this.instance = instance;
	}

	public ServiceNowInstance getInstance()
	{
		return this.instance;
	}

	public String getVersion()
	{
		ServiceNowRESTService snrs = new ServiceNowRESTService(getInstance());
        String url = "https://" + getInstance().getInstance() + "/api/now/table/sys_properties?sysparm_query=name=glide.war&sysparm_display_value=false";
        String response = snrs.executeGetRequest(url);
		String version = null;

        if (response == null || response != null && response.equals("")) {
            logger.error("Could not properly connect to the ServiceNow Instance");
            return null;
        }

		logger.debug("PlatformVersion.url: (" + url + ")");
		logger.debug("PlatformVersion.response: (" + response + ")");
		if (response.indexOf("washingtondc") > 0) {
			version = ServiceNowInstance.WASHINGTON;
		}
		else if (response.indexOf("vancouver") > 0) {
			version = ServiceNowInstance.VANCOUVER;
		}
		else if (response.indexOf("utah") > 0) {
			version = ServiceNowInstance.UTAH;
		}
		else if (response.indexOf("xanadu") > 0) {
			version = ServiceNowInstance.XANADU;
		}
		else if (response.indexOf("yokohama") > 0) {
			version = ServiceNowInstance.YOKOHAMA;
		}
		else if (response.indexOf("zurich") > 0) {
			version = ServiceNowInstance.ZURICH;
		}
		else {
			version = ServiceNowInstance.UNKNOWN;
		}

		return version;
	}

	private static final Logger logger = LoggerFactory.getLogger(PlatformVersionDAOREST.class);
}