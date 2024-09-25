package com.servicenow.processmining.extensions.server.api.model;

public class ServiceCatalogItemActionSimulate
    extends ServiceCatalogItemAction
{
    public ServiceCatalogItemActionSimulate(final String type, final String id)
    {
        super(type, id, SIMULATE);
    }

    @Override
    public String getResponse()
    {
        return this.response;
    }

    @Override
    public void setResponse(String response)
    {
        this.response = response;
    }
}
