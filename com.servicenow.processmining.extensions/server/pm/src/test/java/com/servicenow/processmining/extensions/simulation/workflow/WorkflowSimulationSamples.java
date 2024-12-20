package com.servicenow.processmining.extensions.simulation.workflow;

import com.servicenow.processmining.extensions.pm.model.ProcessMiningModel;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelEntity;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelNode;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelResources;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelTransition;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelVariant;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class WorkflowSimulationSamples
{
    private String numberOfInstances = "";
    protected ProcessMiningModel model = null;
    protected ProcessMiningModelVariant pm = null;
    protected HashMap<String, ProcessMiningModelNode> nodes = null;
    protected HashMap<String, ProcessMiningModelTransition> transitions = null;
    protected HashMap<String, ProcessMiningModelResources> resources = null;

    public WorkflowSimulationSamples(final String numberOfInstances)
    {
        this.numberOfInstances = numberOfInstances;
    }

    public String getNumberOfInstances()
    {
        return this.numberOfInstances;
    }

    public ProcessMiningModel getModel()
    {
        if (model == null) {
            model = new ProcessMiningModel(getProcessName());
            model.setNodes(getModelNodes());
            model.setTransitions(getModelTransitions());
        }

        return model;
    }

    private ArrayList<ProcessMiningModelNode> getModelNodes()
    {
        ArrayList<ProcessMiningModelNode> modelNodes = new ArrayList<ProcessMiningModelNode>();
        for (ProcessMiningModelNode node : getNodes().values()) {
            modelNodes.add(node);
        }

        return modelNodes;
    }

    private ArrayList<ProcessMiningModelTransition> getModelTransitions()
    {
        ArrayList<ProcessMiningModelTransition> modelTransitions = new ArrayList<ProcessMiningModelTransition>();
        for (ProcessMiningModelTransition transition : getTransitions().values()) {
            modelTransitions.add(transition);
        }

        return modelTransitions;
    }

    public ProcessMiningModelVariant getVariation()
    {
        pm = new ProcessMiningModelVariant(getProcessName());
        pm.setCreationInterval(getCreationIntervalDuration());
        pm.setFrequency(Integer.valueOf(getNumberOfInstances()).intValue());
        pm.setNodes(getNodes());
        pm.setTransitions(getTransitions());
        pm.setPath(getRoute());    

        return pm;
    }

    protected void attachResourcesToNodes()
    {
        for (String nodeResourceId: getResources().keySet()) {
            ProcessMiningModelNode node = getModel().getNodes().get(nodeResourceId);
            if (node.getId().equals(nodeResourceId)) { 
                node.setResources(getResources().get(nodeResourceId));
            }
        }
    }

    public abstract String getProcessName();

    public abstract String getTableName();

    public abstract String getFieldName();

    public abstract String getVariantName();

    public abstract double getCreationIntervalDuration();

    public abstract HashMap<String, ProcessMiningModelNode> getNodes();

    public abstract ArrayList<String> getRoute();

    public abstract HashMap<String, ProcessMiningModelTransition> getTransitions();

    public HashMap<String, ProcessMiningModelResources> getResources()
    {
        if (resources != null) {
            return resources;
        }

        resources = new HashMap<String, ProcessMiningModelResources>();

        ProcessMiningModelEntity entity = getModel().getEntity(getTableName(), getFieldName());
        for (ProcessMiningModelNode node : getModel().getNodes().values()) {
            if (entity.getTableId().equals(node.getEntityId())) {
                ProcessMiningModelResources r = new ProcessMiningModelResources(node.getId(), node.getName(), 1);
                resources.put(node.getId(), r);    
            }
        }

        return resources;
    }
}