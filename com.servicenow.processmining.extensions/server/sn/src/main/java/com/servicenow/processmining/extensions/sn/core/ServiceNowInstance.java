package com.servicenow.processmining.extensions.sn.core;

public class ServiceNowInstance
{
    private String instance = null;
    private String user = null;
    private String password = null;
    private String snVersion = UNKNOWN;

    public ServiceNowInstance(final String instance, final String user, final String password)
    {
        this.instance = instance;
        this.user = user;
        this.password = password;
        this.snVersion = UNKNOWN;
    }

    public String getInstance()
    {
        return this.instance;
    }

    public String getUser()
    {
        return this.user;
    }

    public String getPassword()
    {
        return this.password;
    }

    public void setSNVersion(final String version)
    {
        this.snVersion = version;
    }

    public String getSNVersion()
    {
        return this.snVersion;
    }

    public static final String UNKNOWN = "UNKNOWN";
    public static final String UTAH = "UTAH";
    public static final String VANCOUVER = "VANCOUVER";
    public static final String WASHINGTON = "WASHINGTON";
    public static final String XANADU = "XANADU";
}