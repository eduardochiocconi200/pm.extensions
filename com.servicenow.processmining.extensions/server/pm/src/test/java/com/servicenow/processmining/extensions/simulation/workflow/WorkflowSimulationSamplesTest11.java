package com.servicenow.processmining.extensions.simulation.workflow;

import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelNode;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelParser;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelResources;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelTransition;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelVariant;
import com.servicenow.processmining.extensions.sn.core.TestUtility;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Assert;

public class WorkflowSimulationSamplesTest11
    extends WorkflowSimulationSamples
{
    public WorkflowSimulationSamplesTest11(final String numberOfInstances)
    {
        super(numberOfInstances);
    }

    public String getProcessName()
    {
        return "Sample11";
    }

    public String getVariantName()
    {
        return "Variant1";
    }

    public double getCreationIntervalDuration()
    {
        return 60.0;
    }

    @Override
    public ProcessMiningModelVariant getSample()
    {
        String processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/payload4-v.json");
        ProcessMiningModelParser parser = new ProcessMiningModelParser("abc");
        Assert.assertTrue(parser.parse(processMiningModelJSONString));

        ProcessMiningModelVariant variant = parser.getProcessMiningModel().getVariants().get("9e4f2646364eb4776a9351d2f75e2a18");
        attachResourcesToNodes(variant);
        variant.setCreationInterval(getCreationIntervalDuration());

        return variant;
    }    

    private void attachResourcesToNodes(final ProcessMiningModelVariant variant)
    {
        for (String nodeResourceIds: getResources().keySet()) {
            ProcessMiningModelNode node = variant.getNodes().get(nodeResourceIds);
            node.setResources(getResources().get(nodeResourceIds));
        }
    }

    @Override
    public ArrayList<String> getRoute()
    {
        return null;
    }

    @Override
    public HashMap<String, ProcessMiningModelNode> getNodes()
    {
        return null;
    }

    @Override
    public HashMap<String, ProcessMiningModelTransition> getTransitions()
    {
        return null;
    }

    @Override
    public HashMap<String, ProcessMiningModelResources> getResources()
    {
        if (resources != null) {
            return resources;
        }

        resources = new HashMap<String, ProcessMiningModelResources>();

        ProcessMiningModelResources resource1 = new ProcessMiningModelResources("2cf2659a1a3da45ce92bfcf8ee891195", "Resource Problem attached", 1);
        resources.put("2cf2659a1a3da45ce92bfcf8ee891195", resource1);

        ProcessMiningModelResources resource2 = new ProcessMiningModelResources("aa6c9d3603332072c4e0305cd4f0929a", "Resource Resolved", 1);
        resources.put("aa6c9d3603332072c4e0305cd4f0929a", resource2);

        ProcessMiningModelResources resource3 = new ProcessMiningModelResources("ff9936b92a853c3acb21567fa0cd870e", "Resource Completed", 1);
        resources.put("ff9936b92a853c3acb21567fa0cd870e", resource3);

        ProcessMiningModelResources resource4 = new ProcessMiningModelResources("24bbd1f096e1bb7eeab76e5a6078a464", "Resource Closed", ProcessMiningModelResources.UNLIMITED);
        resources.put("24bbd1f096e1bb7eeab76e5a6078a464", resource4);

        ProcessMiningModelResources resource5 = new ProcessMiningModelResources("96e3f3343efc685eebdec2266fa4ac2f", "Resource New", 1);
        resources.put("96e3f3343efc685eebdec2266fa4ac2f", resource5);

        ProcessMiningModelResources resource6 = new ProcessMiningModelResources("b095dc38e81104af34c4f56352271ab6", "Resource Created", ProcessMiningModelResources.UNLIMITED);
        resources.put("b095dc38e81104af34c4f56352271ab6", resource6);

        return resources;
    }
}