package com.servicenow.processmining.extensions.simulation.roi;

import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelNode;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelResources;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelTransition;
import com.servicenow.processmining.extensions.simulation.workflow.WorkflowSimulationSamples;

import java.util.ArrayList;
import java.util.HashMap;

public class WorkflowSimulationComparisonSamplesTest2
    extends WorkflowSimulationSamples
{
    public WorkflowSimulationComparisonSamplesTest2(final String numberOfInstances)
    {
        super(numberOfInstances);
    }

    @Override
    public String getProcessName()
    {
        return "SampleComparison1";
    }

    @Override
    public String getTableName()
    {
        return "id";
    }

    @Override
    public String getFieldName()
    {
        return "id";
    }

    @Override
    public String getVariantName()
    {
        return "Variant1";
    }

    public double getCreationIntervalDuration()
    {
        return 1.0;
    }

    @Override
    public ArrayList<String> getRoute()
    {
        ArrayList<String> route = new ArrayList<String>();
        route.add("1");
        route.add("2");
        route.add("3");
        route.add("4");
        route.add("5");
        route.add("6");        
        return route;
    }

    @Override
    public HashMap<String, ProcessMiningModelNode> getNodes()
    {
        if (nodes != null) {
            return nodes;
        }

        // Load Resources before we create the nodes, so we can associate Resources to
        // Nodes for simulation purposes.
        getResources();

        nodes = new HashMap<String, ProcessMiningModelNode>();

        ProcessMiningModelNode node1 = new ProcessMiningModelNode("1", "created");
        node1.setIsStart(true);
        node1.setActivityId("state");
        node1.setResources(resources.get("1"));
        nodes.put("1", node1);

        ProcessMiningModelNode node2 = new ProcessMiningModelNode("2", "New");
        node2.setActivityId("state");
        node2.setResources(resources.get("2"));
        nodes.put("2", node2);

        ProcessMiningModelNode node3 = new ProcessMiningModelNode("3", "Assigned");
        node3.setActivityId("state");
        node3.setResources(resources.get("3"));
        nodes.put("3", node3);

        ProcessMiningModelNode node4 = new ProcessMiningModelNode("4", "Work In Progress");
        node4.setActivityId("state");
        node4.setResources(resources.get("4"));
        nodes.put("4", node4);

        ProcessMiningModelNode node5 = new ProcessMiningModelNode("5", "Resolved");
        node5.setActivityId("state");
        node5.setResources(resources.get("5"));
        nodes.put("5", node5);

        ProcessMiningModelNode node6 = new ProcessMiningModelNode("6", "completed");
        node6.setIsEnd(true);
        node6.setActivityId("state");
        node6.setResources(resources.get("6"));
        nodes.put("6", node6);

        return nodes;
    }

    @Override
    public HashMap<String, ProcessMiningModelTransition> getTransitions()
    {
        if (transitions != null) {
            return transitions;
        }

        transitions = new HashMap<String, ProcessMiningModelTransition>();

        ProcessMiningModelTransition transition1 = new ProcessMiningModelTransition("1", "2");
        transition1.setCaseFrequency(Integer.valueOf(getNumberOfInstances()));
        transition1.setAbsoluteFrequency(Integer.valueOf(getNumberOfInstances()));
        transition1.setMaxReps(1);
        transition1.setMinDuration(0);
        transition1.setMaxDuration(0);
        transition1.setAvgDuration(0);
        transition1.setMedianDuration(0);
        transition1.setTotalDuration(0);
        transitions.put(transition1.getId(), transition1);

        ProcessMiningModelTransition transition2 = new ProcessMiningModelTransition("2", "3");
        transition2.setCaseFrequency(Integer.valueOf(getNumberOfInstances()));
        transition2.setAbsoluteFrequency(Integer.valueOf(getNumberOfInstances()));
        transition2.setMaxReps(1);
        transition2.setMinDuration(60);
        transition2.setMaxDuration(60);
        transition2.setAvgDuration(60);
        transition2.setMedianDuration(60);
        transition2.setTotalDuration(0);
        transitions.put(transition2.getId(), transition2);

        ProcessMiningModelTransition transition3 = new ProcessMiningModelTransition("3", "4");
        transition3.setCaseFrequency(Integer.valueOf(getNumberOfInstances()));
        transition3.setAbsoluteFrequency(Integer.valueOf(getNumberOfInstances()));
        transition3.setMaxReps(1);
        transition3.setMinDuration(84000);
        transition3.setMaxDuration(84000);
        transition3.setAvgDuration(84000);
        transition3.setMedianDuration(84000);
        transition3.setTotalDuration(0);
        transitions.put(transition3.getId(), transition3);

        ProcessMiningModelTransition transition4 = new ProcessMiningModelTransition("4", "5");
        transition4.setCaseFrequency(Integer.valueOf(getNumberOfInstances()));
        transition4.setAbsoluteFrequency(Integer.valueOf(getNumberOfInstances()));
        transition4.setMaxReps(1);
        transition4.setMinDuration(3600);
        transition4.setMaxDuration(3600);
        transition4.setAvgDuration(3600);
        transition4.setMedianDuration(3600);
        transition4.setTotalDuration(0);
        transitions.put(transition4.getId(), transition4);

        ProcessMiningModelTransition transition5 = new ProcessMiningModelTransition("5", "6");
        transition5.setCaseFrequency(Integer.valueOf(getNumberOfInstances()));
        transition5.setAbsoluteFrequency(Integer.valueOf(getNumberOfInstances()));
        transition5.setMaxReps(1);
        transition5.setMinDuration(259200);
        transition5.setMaxDuration(259200);
        transition5.setAvgDuration(259200);
        transition5.setMedianDuration(259200);
        transition5.setTotalDuration(0);
        transitions.put(transition5.getId(), transition5);

        return transitions;
    }

    @Override
    public HashMap<String, ProcessMiningModelResources> getResources()
    {
        if (resources != null) {
            return resources;
        }

        resources = new HashMap<String, ProcessMiningModelResources>();

        ProcessMiningModelResources resource1 = new ProcessMiningModelResources("1", "Created", ProcessMiningModelResources.UNLIMITED);
        resources.put("1", resource1);

        ProcessMiningModelResources resource2 = new ProcessMiningModelResources("2", "New", 1);
        resources.put("2", resource2);

        ProcessMiningModelResources resource3 = new ProcessMiningModelResources("3", "Assigned", 1);
        resources.put("3", resource3);

        ProcessMiningModelResources resource4 = new ProcessMiningModelResources("4", "Work In Progress", 1);
        resources.put("4", resource4);

        ProcessMiningModelResources resource5 = new ProcessMiningModelResources("5", "Resolved", 1);
        resources.put("5", resource5);

        ProcessMiningModelResources resource6 = new ProcessMiningModelResources("6", "Completed", ProcessMiningModelResources.UNLIMITED);
        resources.put("6", resource6);

        return resources;
    }
}