package com.servicenow.processmining.extensions.sc.dao;

import com.servicenow.processmining.extensions.sn.core.ServiceNowTestCredentials;

public class BaseDAOTest
{
    protected static final String snInstance = ServiceNowTestCredentials.getInstanceName();
    protected static final String snUser = ServiceNowTestCredentials.getUserName();
    protected static final String snPassword = ServiceNowTestCredentials.getPassword();
}