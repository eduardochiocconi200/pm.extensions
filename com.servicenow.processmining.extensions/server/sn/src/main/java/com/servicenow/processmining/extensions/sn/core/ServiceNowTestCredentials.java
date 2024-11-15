package com.servicenow.processmining.extensions.sn.core;

public class ServiceNowTestCredentials
{
    public static String getInstanceName()
    {
        return snInstance;
    }

    public static String getUserName()
    {
        return snUser;
    }

    public static String getPassword()
    {
        return snPassword;
    }

    private static final String snInstance = "empechiocconi2";
    private static final String snUser = "admin";
    private static final String snPassword = "StarWars!1";
}