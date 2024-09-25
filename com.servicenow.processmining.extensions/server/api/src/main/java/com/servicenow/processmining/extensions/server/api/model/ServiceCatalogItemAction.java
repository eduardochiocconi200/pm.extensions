package com.servicenow.processmining.extensions.server.api.model;

public abstract class ServiceCatalogItemAction
{
    private String type = null;
    private String id = null;
    private String action = null;
    protected String response = null;

    public ServiceCatalogItemAction(final String type, final String id, final String action)
    {
        this.type = type;
        this.id = id;
        this.action = action;
    }

    public String getType()
    {
        return this.type;
    }

    public String getId()
    {
        return this.id;
    }

    public String getAction()
    {
        return this.action;
    }

    public abstract String getResponse();
    public abstract void setResponse(final String response);

    protected final static String MINE = "mine";
    protected final static String SIMULATE = "simulate";
}
