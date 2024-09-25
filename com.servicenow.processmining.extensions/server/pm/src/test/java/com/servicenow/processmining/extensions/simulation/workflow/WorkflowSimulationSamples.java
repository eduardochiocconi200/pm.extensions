package com.servicenow.processmining.extensions.simulation.workflow;

import java.util.ArrayList;
import java.util.HashMap;

import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelNode;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelResources;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelTransition;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelVariant;

public abstract class WorkflowSimulationSamples
{
    private String numberOfInstances = "";
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

    public ProcessMiningModelVariant getSample()
    {
        pm = new ProcessMiningModelVariant(getProcessName());
        pm.setCreationInterval(getCreationIntervalDuration());
        pm.setFrequency(Integer.valueOf(getNumberOfInstances()).intValue());
        pm.setNodes(getNodes());
        pm.setTransitions(getTransitions());
        pm.setPath(getRoute());

        return pm;
    }

    public abstract String getProcessName();

    public abstract String getVariantName();

    public abstract double getCreationIntervalDuration();

    public abstract HashMap<String, ProcessMiningModelNode> getNodes();

    public abstract ArrayList<String> getRoute();

    public abstract HashMap<String, ProcessMiningModelTransition> getTransitions();

    public abstract HashMap<String, ProcessMiningModelResources> getResources();
}