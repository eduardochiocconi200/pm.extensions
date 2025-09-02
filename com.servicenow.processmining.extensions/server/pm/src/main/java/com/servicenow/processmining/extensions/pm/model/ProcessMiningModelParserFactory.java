package com.servicenow.processmining.extensions.pm.model;

import com.servicenow.processmining.extensions.sn.core.ServiceNowInstance;

public class ProcessMiningModelParserFactory
{
    public static ProcessMiningModelParser getParser(final ServiceNowInstance instance, final String versionId)
    {
        if (instance.getSNVersion().equals(ServiceNowInstance.ZURICH)) {
            return new ProcessMiningModelParserZurich(versionId);
        }

        return new ProcessMiningModelParserGeneric(versionId);
    }
}
