package com.servicenow.processmining.extensions.simulation.workflow;

import com.servicenow.processmining.extensions.pm.model.ProcessMiningModel;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelEntity;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelNode;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelParser;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelResources;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelTransition;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelVariant;
import com.servicenow.processmining.extensions.sn.core.TestUtility;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Assert;

public class WorkflowSimulationSamplesTest10
    extends WorkflowSimulationSamples
{
    private ProcessMiningModel model = null;

    public WorkflowSimulationSamplesTest10(final String numberOfInstances)
    {
        super(numberOfInstances);
    }

    @Override
    public String getProcessName()
    {
        return "Sample10";
    }

    @Override
    public String getTableName()
    {
        return "incident";
    }

    @Override
    public String getFieldName()
    {
        return "state";
    }

    @Override
    public String getVariantName()
    {
        return "9e4f2646364eb4776a9351d2f75e2a18";
    }

    public double getCreationIntervalDuration()
    {
        return 1.0;
    }

    @Override
    public ProcessMiningModel getModel()
    {
        if (model == null) {
            String processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/payload4-w.json");
            ProcessMiningModelParser parser = new ProcessMiningModelParser("abc");
            Assert.assertTrue(parser.parse(processMiningModelJSONString));
            model = parser.getProcessMiningModel();
            attachResourcesToNodes();
        }

        return model;
    }

    @Override
    public ProcessMiningModelVariant getVariation()
    {
        ProcessMiningModelVariant variant = getModel().getVariants().get(getVariantName());
        variant.setCreationInterval(getCreationIntervalDuration());
        // Let's overwrite the frequency.
        variant.setFrequency(Integer.valueOf(getNumberOfInstances()).intValue());

        return variant;
    }

    @Override
    public ArrayList<String> getRoute()
    {
        return null;
    }

    @Override
    public HashMap<String, ProcessMiningModelNode> getNodes()
    {
        return getModel().getNodes();
    }

    @Override
    public HashMap<String, ProcessMiningModelTransition> getTransitions()
    {
        return getModel().getTransitions();
    }

    @Override
    public HashMap<String, ProcessMiningModelResources> getResources()
    {
        if (resources != null) {
            return resources;
        }

        resources = new HashMap<String, ProcessMiningModelResources>();

        ProcessMiningModelEntity entity = getModel().getEntity(getTableName(), getFieldName());
        for (ProcessMiningModelNode node : model.getNodes().values()) {
            if (entity.getTableId().equals(node.getEntityId())) {
                ProcessMiningModelResources r = new ProcessMiningModelResources(node.getId(), node.getName(), 1);
                resources.put(node.getId(), r);    
            }
        }

        return resources;
    }
}