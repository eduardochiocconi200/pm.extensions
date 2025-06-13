package com.servicenow.processmining.extensions.pm.simulation.workflow;

import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelEntity;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelNode;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelTransition;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelVariant;
import com.servicenow.processmining.extensions.pm.simulation.core.*;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkflowVariationInstance
    extends WorkflowInstance
{
    private ProcessMiningModelVariant variant = null;

    public WorkflowVariationInstance(final String id, final Simulator simulator, final ProcessMiningModelVariant variant)
    {
        super(id, simulator);
        this.variant = variant;
    }

    public ProcessMiningModelVariant getProcessModelVariant()
    {
        return this.variant;
    }

    @Override
    public void create(final double startOffset)
    {
        String fromNode = "";
        String toNode = getSimulator().getSimulationState().getProcessModel().getStartingNodes().get(0);
        if (getSimulator().getSimulationState().getProcessModel().getStartingNodes().size() > 1) {
            ProcessMiningModelEntity entity = getSimulator().getSimulationState().getProcessModel().getEntity(getSimulator().getTableName(), getSimulator().getFieldName());
            for (String startingNode : getSimulator().getSimulationState().getProcessModel().getStartingNodes()) {
                ProcessMiningModelNode sNode = getSimulator().getSimulationState().getProcessModel().getNodes().get(startingNode);
                if (sNode.getEntityId().equals(entity.getTableId())) {
                    toNode = startingNode;
                    break;
                }
            }
        }
        Message newEvent = new Message(this, getId(), fromNode, toNode,
                startOffset, Message.REGULAR);
        getSimulator().insert(newEvent);
    }

    @Override
    public String getNextNode()
    {
        String nextNode = getNextNode(getActivityState(), getCurrentPath());
        logger.debug("WorkflowVariationInstance.getNextNode() -> (" + nextNode + ")");

        return nextNode;
    }

    private String getNextNode(final String activityState, final String currentPath)
    {
        // If the current activity state is empty, it means we need to assume it is one
        // of the starting nodes.
        // As we return one of the starting nodes, let's make sure we properly account
        // for the WorkflowInstance count in the stats.
        if (activityState.equals("")) {
            if (getProcessModelVariant().getStartingNodes().size() > 1) {
                throw new RuntimeException("Need to Implement Logic when there is more than one starting transition");
            }

            String startingNode = getProcessModelVariant().getStartingNodes().get(0);
            getSimulator().getSimulationState().increaseUsage(getId(), startingNode);
            return startingNode;
        }

        ArrayList<ProcessMiningModelTransition> outgoingTransitions = getProcessModelVariant().getOutgoingTransition(activityState);
        // If there are no outgoing transitions, then we reached the end!
        if (outgoingTransitions.size() == 0) {
            return null;
        }

        String nextRouteNode = null;
        if (outgoingTransitions.size() > 1) {
            nextRouteNode = getNextRouteNode(currentPath);
        }
        else {
            nextRouteNode = outgoingTransitions.get(0).getTo();
        }

        return nextRouteNode;
    }

    private String getNextRouteNode(final String currentPath)
    {
        String routeNodes = getProcessModelVariant().getRouteNodes();
        String nextRouteNode = routeNodes.substring(currentPath.length() + 1);
        nextRouteNode = nextRouteNode.substring(0, nextRouteNode.indexOf(","));
        return nextRouteNode;
    }

    @Override
    public double getNextNodeCompletionTime(final String fromNode, final String toNode, final Message message)
    {
        ProcessMiningModelTransition transition = getSimulator().getSimulationState().getProcessModel().getTransition(fromNode, toNode);
        double nextNodeCompletionTime = Double.valueOf(transition.getAvgDuration()).doubleValue();

        return nextNodeCompletionTime;
}

    private static final Logger logger = LoggerFactory.getLogger(WorkflowVariationInstance.class);
}