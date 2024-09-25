package com.servicenow.processmining.extensions.pm.simulation.workflow;

import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelNode;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelResources;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelTransition;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelVariant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkflowInstanceSimulationState
{
    private ProcessMiningModelVariant processModelVariant = null;
    private WorkflowSimulator simulator = null;
    private HashMap<String, Integer> activityStateCount = null;
    private HashMap<String, Vector<WorkflowInstanceQueuedInfo>> activityStateQueue = null;
    private HashMap<String, WorkflowInstanceResource> activityStateQueueResource = null;
    private java.util.Vector<WorkflowInstance> workflowInstances = null;

    public WorkflowInstanceSimulationState(final ProcessMiningModelVariant processModelVariant, final WorkflowSimulator simulator)
    {
        this.processModelVariant = processModelVariant;
        this.simulator = simulator;
        initializeWorkflowMetadata();
    }

    public ProcessMiningModelVariant getProcessModelVariant()
    {
        return this.processModelVariant;
    }

    public WorkflowSimulator getSimulator()
    {
        return this.simulator;
    }

    public HashMap<String, Integer> getActivityStateCount()
    {
        return activityStateCount;
    }

    public void incrementCount(final String currentActivityState)
    {
        int currentCount = getActivityStateCount().get(currentActivityState).intValue();
        getActivityStateCount().remove(currentActivityState);
        getActivityStateCount().put(currentActivityState, currentCount + 1);
    }

    public void decrementCount(final String currentActivityState)
    {
        int currentCount = getActivityStateCount().get(currentActivityState).intValue();
        getActivityStateCount().remove(currentActivityState);
        getActivityStateCount().put(currentActivityState, currentCount - 1);
    }

    public void increaseUsage(final String currentNode)
    {
        activityStateQueueResource.get(currentNode).increaseUsage();
    }

    public void decreaseUsage(final String currentNode)
    {
        activityStateQueueResource.get(currentNode).decreaseUsage();
    }

    public boolean validateCounters()
    {
        if (getActivityStateCount() != null) {
            for (String nodeId : getActivityStateCount().keySet()) {
                int activityStateCount = getActivityStateCount().get(nodeId).intValue();
                if (activityStateQueue != null) {
                    int activityStateQueueSize = activityStateQueue.get(nodeId).size();
                    int activityStateQueueResourceUsage = activityStateQueueResource.get(nodeId).getUsage();
                    if (activityStateCount != (activityStateQueueSize + activityStateQueueResourceUsage)) {
                        // END nodes are the only ones which may have out of balance, as we will not
                        // update the "usage" and "queues" as we reached
                        // the end of the process.
                        if (!isEndingNode(nodeId)) {
                            throw new RuntimeException(
                                    "WorkflowInstanceSimulationState's Counter out of balance. Node: [" + nodeId
                                            + "]. StateCount: '" + activityStateCount + "', QueueSize: '"
                                            + activityStateQueueSize + "', ResourceUsage: '"
                                            + activityStateQueueResourceUsage + "'");
                        }
                    }
                }
            }
        }

        return true;
    }

    private void initializeWorkflowMetadata()
    {
        workflowInstances = new java.util.Vector<WorkflowInstance>();
        activityStateCount = new HashMap<String, Integer>();
        activityStateQueue = new HashMap<String, Vector<WorkflowInstanceQueuedInfo>>();
        activityStateQueueResource = new HashMap<String, WorkflowInstanceResource>();

        for (ProcessMiningModelNode node : getProcessModelVariant().getNodes().values()) {
            activityStateCount.put(node.getId(), 0);
            activityStateQueue.put(node.getId(), new Vector<WorkflowInstanceQueuedInfo>());
            ProcessMiningModelResources nodeResources = node.getResources();
            boolean isStartingNode = getProcessModelVariant().getStartingNodes().contains(node.getId());
            boolean isEndingNode = getProcessModelVariant().getEndingNodes().contains(node.getId());
            if (isStartingNode || isEndingNode) {
                activityStateQueueResource.put(node.getId(), new WorkflowInstanceResource(nodeResources.getId(),
                        nodeResources.getName(), ProcessMiningModelResources.UNLIMITED));
            }
            else {
                activityStateQueueResource.put(node.getId(), new WorkflowInstanceResource(nodeResources.getId(),
                        nodeResources.getName(), nodeResources.getCapacity()));
            }
        }
    }

    public void addWorkflowInstance(final WorkflowInstance workflowInstance)
    {
        workflowInstances.addElement(workflowInstance);
    }

    public void checkIfThereAreEnquedWorkflowInstances(final String executedNode)
    {
        logger.debug("Time: (" + simulator.now() + ") - [*] - checkIfThereAreEnquedWorkflowInstances: Checking if we can dequeue some WorkflowInstances for: (" + executedNode + ")");
        // If there is resource capacity and there are enqueued WorkflowInstances for
        // this resource, dispatch the first one now that we release the resource.
        if (executedNode != null && !executedNode.equals("") && activityStateQueueResource.get(executedNode).hasCapacity()) {
            logger.debug("Time: (" + simulator.now() + ") - [*] - checkIfThereAreEnquedWorkflowInstances: The resource associated to Node: [" + executedNode + "] has capacity to process new work.");
            if (activityStateQueue.get(executedNode).size() > 0) {
                logger.debug("QUEUES: " + getActivityStateQueuesWithDetails());
                logger.debug(
                        "Time: (" + simulator.now() + ") - [*] - checkIfThereAreEnquedWorkflowInstances: There are ["
                                + activityStateQueue.get(executedNode).size() + "] WorkflowInstances enqueued in ["
                                + executedNode + "]. Will dequeue the first one and dispatch.");
                WorkflowInstanceQueuedInfo enqueuedWI = activityStateQueue.get(executedNode).firstElement();
                activityStateQueue.get(executedNode).removeElementAt(0);
                logger.debug("Time: (" + simulator.now() + ") - [*] - checkIfThereAreEnquedWorkflowInstances: Will process Enqueued WorkflowInstance: (" + enqueuedWI + ")");
                increaseUsage(enqueuedWI.getFromNode());
                Message enqueuedWINewEvent = new Message(enqueuedWI.getId(), enqueuedWI.getId().getId(),
                        enqueuedWI.getFromNode(), enqueuedWI.getToNode(),
                        getSimulator().now() + enqueuedWI.getCompletionTime(), Message.RESUME);
                logger.debug("Time: (" + simulator.now()
                        + ") - [*] - checkIfThereAreEnquedWorkflowInstances: The node: '" + executedNode
                        + "' has free resources. Removing WI:[" + enqueuedWI.getId().getId()
                        + "] from resource waiting queue and adding: [" + enqueuedWINewEvent + "] to events queue.");
                getSimulator().insert(enqueuedWINewEvent);
            }
            else {
                logger.debug("Time: (" + simulator.now()
                        + ") - [*] - checkIfThereAreEnquedWorkflowInstances: There are no WorkflowInstances enqueued in ["
                        + executedNode + "] to dequeue and dispatch.");
            }

            return;
        }
        else {
            logger.debug("Time: (" + simulator.now() + ") - [*] - checkIfThereAreEnquedWorkflowInstances: The resource associated to Node: [" + executedNode + "] DOES NOT have capacity to process new work.");
        }
    }

    public void scheduleNextEvent(final WorkflowInstance wi, final String currentNode, final String nextNode, final double nextNodeCompletionTime, final int messageType)
    {
        // We create the new event with the future state and send it to the Simulator
        // and enqueue it.
        Message newEvent = new Message(wi, wi.getId(), currentNode, nextNode,
                getSimulator().now() + nextNodeCompletionTime, messageType);
        // We need to specify WHEN the current Activity will finish. This is the
        // event we are pushing into the simulation so that when the time reaches,
        // then it will routed to the next activity in the process.
        logger.debug("Time: (" + getSimulator().now() + ") - [" + wi.getId() + "] - Routing - Scheduling New Event: " + newEvent.toString());
        getSimulator().insert(newEvent);
    }

    public void enqueueWorkflowInstanceDueToNoCapacity(final WorkflowInstance wi, final Message message, String currentNode, String nextNode, double nextNodeCompletionTime)
    {
        WorkflowInstanceQueuedInfo wiqi = new WorkflowInstanceQueuedInfo(wi, currentNode, nextNode, nextNodeCompletionTime);
        logger.debug("Time: (" + getSimulator().now() + ") - [" + message.getReferenceId() + "] - Routing - Enqueuing WI [" + wi.getId() + "] in Node: " + nextNode);
        activityStateQueue.get(currentNode).add(wiqi);
    }

    public boolean doesCurrentNodeHasCapacity(String currentNode)
    {
        if (activityStateQueue.get(currentNode) != null) {
            boolean hasWorkflowInstancesEnqueued = activityStateQueue.get(currentNode).size() > 0;
            return !hasWorkflowInstancesEnqueued && activityStateQueueResource.get(currentNode).hasCapacity();
        }

        return true;
    }

    public String getStartingNode()
    {
        if (getProcessModelVariant().getStartingNodes().size() > 1) {
            throw new RuntimeException("Need to Implement Logic when there is more than one starting transition");
        }

        return getProcessModelVariant().getStartingNodes().get(0);
    }

    public boolean isEndingNode(final String nodeId)
    {
        return getProcessModelVariant().getEndingNodes().contains(nodeId);
    }

    public String getNextNode(final String activityState, final String currentPath)
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
            activityStateQueueResource.get(startingNode).increaseUsage();
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

    public String getNextRouteNode(final String currentPath)
    {
        String routeNodes = getProcessModelVariant().getRouteNodes();
        String nextRouteNode = routeNodes.substring(currentPath.length() + 1);
        nextRouteNode = nextRouteNode.substring(0, nextRouteNode.indexOf(","));
        return nextRouteNode;
    }

    public String getActivityStateCounts()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        boolean processedFirstNode = false;
        for (String nodeId : getActivityStateCount().keySet()) {
            if (processedFirstNode) {
                sb.append(", ");
            }
            sb.append(" " + nodeId + ": " + getActivityStateCount().get(nodeId));
            processedFirstNode = true;
        }
        sb.append("]");

        return sb.toString();
    }

    public String getActivityStateQueues()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        boolean processedFirstNode = false;
        for (String nodeId : activityStateQueue.keySet()) {
            if (processedFirstNode) {
                sb.append(", ");
            }
            sb.append(" " + nodeId + ": " + activityStateQueue.get(nodeId).size());
            processedFirstNode = true;
        }
        sb.append("]");

        return sb.toString();
    }

    public String getActivityStateQueuesWithDetails()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("State Queues Details:\n");
        boolean processedFirstNode = false;
        for (String nodeId : activityStateQueue.keySet()) {
            if (processedFirstNode) {
                sb.append("\n");
            }
            sb.append("Node: [" + nodeId + "], Queue Size: '" + activityStateQueue.get(nodeId).size() + "'\n");
            for (WorkflowInstanceQueuedInfo wi : activityStateQueue.get(nodeId)) {
                sb.append(" - [" + wi + "]\n");
            }
            processedFirstNode = true;
        }
        sb.append("\n");

        return sb.toString();
    }

    public String getActivityStateQueueResources()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        boolean processedFirstNode = false;
        for (String nodeId : activityStateQueueResource.keySet()) {
            if (processedFirstNode) {
                sb.append(", ");
            }
            sb.append(" " + nodeId + ": " + activityStateQueueResource.get(nodeId).getUsage() + "/"
                    + activityStateQueueResource.get(nodeId).getCapacity());
            processedFirstNode = true;
        }
        sb.append("]");

        return sb.toString();
    }

    public boolean validateEmptyQueues()
    {
        if (getActivityStateCount() != null) {
            for (String nodeId : getActivityStateCount().keySet()) {
                Integer activityStateCount = getActivityStateCount().get(nodeId);
                if (activityStateCount.intValue() != 0) {
                    // Only END nodes can have a value higher than zero.
                    if (!getProcessModelVariant().getEndingNodes().contains(nodeId)) {
                        return false;
                    }
                }
            }
        }

        if (activityStateQueue != null) {
            for (String nodeId : activityStateQueue.keySet()) {
                if (activityStateQueue.get(nodeId).size() != 0) {
                    return false;
                }
            }
        }

        if (activityStateQueueResource != null) {
            for (String nodeId : activityStateQueueResource.keySet()) {
                if (activityStateQueueResource.get(nodeId).getUsage() != 0) {
                    return false;
                }
            }
        }

        return true;
    }

    private static final Logger logger = LoggerFactory.getLogger(WorkflowInstanceSimulationState.class);
}