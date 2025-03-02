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

public class WorkflowSimulationSamplesTest12
    extends WorkflowSimulationSamples
{
    private ProcessMiningModel model = null;

    public WorkflowSimulationSamplesTest12(final String numberOfInstances)
    {
        super(numberOfInstances);
    }

    @Override
    public String getProcessName()
    {
        return "Incident State Analysis (2024)";
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
        return "00e508354daf6aa52c2155c6c9662a1a";
    }

    public double getCreationIntervalDuration()
    {
        return 0.0;
    }

    @Override
    public ProcessMiningModel getModel()
    {
        if (model == null) {
            String processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/filterPayload5-w.json");
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
                ProcessMiningModelResources r = new ProcessMiningModelResources(node.getId(), node.getName(), ProcessMiningModelResources.UNLIMITED);
                resources.put(node.getId(), r);    
            }
        }

        return resources;
    }
}