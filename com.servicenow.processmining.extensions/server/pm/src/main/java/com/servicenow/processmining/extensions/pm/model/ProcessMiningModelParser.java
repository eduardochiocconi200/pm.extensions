package com.servicenow.processmining.extensions.pm.model;

import org.json.JSONArray;
import org.json.JSONObject;

public abstract class ProcessMiningModelParser
{
    protected String versionId = null;
    protected ProcessMiningModel pmm = null;
    protected JSONObject processModelJSON = null;
    protected String errors = null;
    
    public ProcessMiningModelParser(final String versionId)
    {
        this.versionId = versionId;
    }

    public String getVersionId()
    {
        return this.versionId;
    }

    public abstract boolean parse(final String processModelJSONString);

    public boolean parseAndAppendVariants(final String processModelVariantsJSONString)
    {
        return true;
    }

    protected boolean modelHasErrors()
    {
        if (processModelJSON.has("errors")) {
            JSONArray errorsObj = (JSONArray) processModelJSON.getJSONArray("errors");
            String errorMessage = errorsObj.getJSONObject(0).getString("message");
            String errorType = errorsObj.getJSONObject(0).getString("errorType");
            this.errors = errorType + " - " + errorMessage;
            return true;
        }

        return false;
    }

    protected void createEmptyProcessMiningModel()
    {
        this.pmm = new ProcessMiningModel(getVersionId());
    }

    protected void createJSONObject(final String processModelJSONString)
    {
        processModelJSON = new JSONObject(processModelJSONString);
    }

    public ProcessMiningModel getProcessMiningModel()
    {
        return pmm;
    }
}