package com.servicenow.processmining.extensions.server.api.controller;

import com.servicenow.processmining.extensions.sn.core.ServiceNowInstance;
import com.servicenow.processmining.extensions.sn.core.ServiceNowTestCredentials;
import com.servicenow.processmining.extensions.sn.dao.PlatformVersionDAOREST;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseController
{
	private String snInstance = null;
	private String snUser = null;
	private String snPassword = null;
	private ServiceNowInstance instance = null;

	protected void setSNInstance(final String instance)
	{
		this.snInstance = instance;
	}

	protected String getSNInstance()
	{
		return this.snInstance;
	}

	protected void setSNUser(final String user)
	{
		this.snUser = user;
	}

	protected String getSNUser()
	{
		return this.snUser;
	}

	protected void setSNPassword(final String password)
	{
		this.snPassword = password;
	}

	protected String getSNPassword()
	{
		return this.snPassword;
	}

	public ServiceNowInstance getInstance()
	{
		if (instance == null) {
			if (snInstance == null) {
				snInstance = snSampleInstance;
				snUser = snSampleUser;
				snPassword = snSamplePassword;
			}
			instance = new ServiceNowInstance(snInstance, snUser, snPassword);
			PlatformVersionDAOREST platformVerDAO = new PlatformVersionDAOREST(instance);
			instance.setSNVersion(platformVerDAO.getVersion());
			logger.debug("Connecting to: (" + snInstance + ") with user: (" + snUser + ") to platform release: (" + instance.getSNVersion() + ")");
		}

		return instance;
	}

	public void invalidateInstance()
	{
		this.instance = null;
	}

	protected static final String CROSS_ORIGIN_DOMAIN = "http://localhost:3000";
	// protected static final String CROSS_ORIGIN_DOMAIN = ServiceNowTestCredentials.getInstanceName();
    protected static final String snSampleInstance = ServiceNowTestCredentials.getInstanceName();
    protected static final String snSampleUser = ServiceNowTestCredentials.getUserName();
    protected static final String snSamplePassword = ServiceNowTestCredentials.getPassword();

	private static final Logger logger = LoggerFactory.getLogger(BaseController.class);	
}
