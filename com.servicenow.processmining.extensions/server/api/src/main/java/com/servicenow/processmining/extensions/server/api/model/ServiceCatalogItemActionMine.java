package com.servicenow.processmining.extensions.server.api.model;

public class ServiceCatalogItemActionMine
    extends ServiceCatalogItemAction
{
    public ServiceCatalogItemActionMine(final String type, final String id)
    {
        super(type, id, MINE);
    }

    @Override
    public String getResponse()
    {
        return this.response;
    }

    @Override
    public void setResponse(String response)
    {
        if (response != null) {
            this.response = "{ \"status\" : \"scheduled\", \"jobid\" : \"" + response + "\" }";
        }
        else {
            this.response = "{ \"status\" : \"failed\", \"jobid\" : \"\" }";
        }
    }
}
