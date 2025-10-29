package com.servicenow.processmining.extensions.pm.simulation.workflow;

import com.servicenow.processmining.extensions.pm.model.ProcessMiningModel;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelEntity;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelNode;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelParser;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelParserFactory;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelResources;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelTransition;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelVariant;
import com.servicenow.processmining.extensions.sn.core.ServiceNowInstance;
import com.servicenow.processmining.extensions.sn.core.TestUtility;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Assert;

public class WorkflowSimulationSamplesTest15
    extends WorkflowSimulationSamples
{
    private ProcessMiningModel model = null;

    public WorkflowSimulationSamplesTest15(final String numberOfInstances)
    {
        super(numberOfInstances);
    }

    @Override
    public String getProcessName()
    {
        return "Incident Analysis";
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
        return "N/A";
    }

    public double getCreationIntervalDuration()
    {
        return 0.0;
    }

    @Override
    public ProcessMiningModel getModel()
    {
        if (model == null) {
            String processMiningModelJSONString = new TestUtility().loadProcessMiningModel("/model/payload14-z.json");
            getInstance().setSNVersion(ServiceNowInstance.ZURICH);
            ProcessMiningModelParser parser = ProcessMiningModelParserFactory.getParser(getInstance(), "abc");
            Assert.assertTrue(parser.parse(processMiningModelJSONString));
            model = parser.getProcessMiningModel();
            attachResourcesToNodes();
        }

        return model;
    }

    @Override
    public ProcessMiningModelVariant getVariation()
    {
        return null;
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