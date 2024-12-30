package com.servicenow.processmining.extensions.pm.simulation.workflow;

import com.servicenow.processmining.extensions.pm.simulation.core.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class WorkflowInstance
    implements MessageHandler
{
    private static MessageHandler display = null;
    private String id = null;
    private String activityState = null;
    private String currentPath = "";
    private WorkflowSimulator simulator = null;

    public WorkflowInstance(final String id, final Simulator simulator)
    {
        this.id = id;
        this.activityState = "";
        this.simulator = (WorkflowSimulator) simulator;
        getSimulator().getSimulationState().addWorkflowInstance(this);
    }

    public WorkflowSimulator getSimulator()
    {
        return this.simulator;
    }

    public String getId()
    {
        return this.id;
    }

    public String getActivityState()
    {
        return this.activityState;
    }

    public String getCurrentPath()
    {
        return this.currentPath;
    }

    public abstract void create(final double startOffset);

    public abstract String getNextNode();

    public abstract double getNextNodeCompletionTime(final String fromNode, final String toNode, final Message message);

    public void handle(final Message message)
    {
        route(message);

        if (display != null)
            display.handle(message);
    }

    private void route(final Message message)
    {
        getSimulator().validateCounters();
        logger.debug("Time: (" + getSimulator().now() + ") - [" + getId() + "] - BEGIN route: ------------------------------------------------------------------------");
        logger.debug("Time: (" + getSimulator().now() + ") - [" + getId() + "] - route: BEGIN - Current Path: ("
                + getCurrentPath() + ") - Simulation State\nStates   : "
                + getSimulator().getSimulationState().getActivityStateCounts() + "\nResources: "
                + getSimulator().getSimulationState().getActivityStateQueueResources() + "\nQueues   : "
                + getSimulator().getSimulationState().getActivityStateQueues());

        // If at the time of scheduling the event, the message type was REGULAR, we need
        // to double check the target node
        // has capacity to execute this request. It is possible, that at the time of
        // scheduling, the target node had
        // capacity, but in between the resource was taken by another scheduled event
        // between the time this message was
        // scheduled and its completion time.
        if (message.isRegularMessageType()) {
            if (!getSimulator().getSimulationState().doesCurrentNodeHasCapacity(message.getTo())) {
                // Then we need to enqueue it. We need to change the message type to ENQUEUE.
                message.updateMessageType(Message.ENQUEUE);
            }
        }

        // If we have enqueued this event and it was resumed (RESUME), then we need to
        // skip updating the state
        // as it was already done before the message was enqueued.
        if (!message.isResumedMessageType()) {
            updateActivityState(message);
        }

        String currentNode = null;
        String nextNode = null;
        String executedNode = message.getFrom();
        double nextNodeCompletionTime = 0;

        // If we receive a route for a WorkflowInstance that has been marked with no
        // capacity to be routed, then we need to enqueue it.
        if (message.isEnqueueMessageType()) {
            // At the time to scheduling a message in state ENQUEUE, the capacity of the
            // node was exceeded and as such
            // we needed to mark the message as ENQUEUE. However, it is possible that by the
            // time the message was
            // dispatched, that the node has free resources and we may be able to process
            // the message rather than
            // enqueuing. So we will check for this condition before scheduling.
            // If there is still no capacity, then enqueue.
            if (!getSimulator().getSimulationState().doesCurrentNodeHasCapacity(getActivityState())) {
                currentNode = getActivityState();
                nextNode = getNextNode();
                logger.debug("Time: (" + getSimulator().now() + ") - [" + message.getReferenceId()
                        + "] - Routing - There IS NO capacity target Node: (" + nextNode
                        + "). Will need to enqueue WorkflowInstance.");
                nextNodeCompletionTime = getSimulator().now() + message.getTime();
                getSimulator().getSimulationState().enqueueWorkflowInstanceDueToNoCapacity(this, message, currentNode, nextNode, nextNodeCompletionTime);
                getSimulator().getSimulationState().checkIfThereAreEnquedWorkflowInstances(executedNode);
            }
            else {
                // We correct the message type as REGULAR and we also increment the usage, since
                // originally the message type was ENQUEUE and
                // we did not increment the usage when updating the state. As we are correcting
                // the message type, we need to make this
                // increment effective.
                message.updateMessageType(Message.REGULAR);
                incrementUsage(getActivityState());
            }
        }

        // If we are not enqueing the message, then we proceed to process the message...
        if (message.isRegularMessageType() || message.isResumedMessageType()) {
            currentNode = getActivityState();
            nextNode = getNextNode();

            // If nextNode == null, then we reached the end of that path and we should stop
            // scheduling more events
            // for this WorkflowInstance. But we may still need to clean up the resource
            // queue.
            if (nextNode == null) {
                logger.debug("Time: (" + getSimulator().now() + ") - [" + message.getReferenceId()
                        + "] - Routing - WI Reached END of route.");
                getSimulator().getSimulationState().decreaseUsage(currentNode);
                getSimulator().getSimulationState().checkIfThereAreEnquedWorkflowInstances(executedNode);
            }
            else {
                logger.debug("Looking for transition: (" + currentNode + ", " + nextNode + ")");
                nextNodeCompletionTime += getNextNodeCompletionTime(currentNode, nextNode, message);
                logger.debug("Time: (" + getSimulator().now() + ") - [" + message.getReferenceId()
                        + "] - Routing - Creating Node: '" + currentNode
                        + "' completion event. When the event completes at :'" + currentNode
                        + "', it will be routed to Node: '" + nextNode + "'");

                // We need to determine if the target state has capacity to process this future
                // event.
                // If not, we will mark the WorkflowInstance, so that when it is finally
                // completed and needs to be
                // routed, we enqueue it.
                int messageType = Message.REGULAR;
                if (!getSimulator().getSimulationState().doesCurrentNodeHasCapacity(nextNode)) {
                    logger.debug("Time: (" + getSimulator().now() + ") - [" + message.getReferenceId()
                            + "] - Routing - Will need to enque when finally routed in target Node: (" + nextNode
                            + ").");
                    messageType = Message.ENQUEUE;
                }
                getSimulator().getSimulationState().scheduleNextEvent(this, currentNode, nextNode,
                        nextNodeCompletionTime, messageType);
                getSimulator().getSimulationState().checkIfThereAreEnquedWorkflowInstances(executedNode);
            }
        }
        logger.debug("Time: (" + getSimulator().now() + ") - [" + getId() + "] - route: - Current Path: (" + getCurrentPath()
                + ") - Simulation State\nStates   : " + getSimulator().getSimulationState().getActivityStateCounts()
                + "\nResources: " + getSimulator().getSimulationState().getActivityStateQueueResources()
                + "\nQueues   : " + getSimulator().getSimulationState().getActivityStateQueues());
        logger.debug("Time: (" + getSimulator().now() + ") - [" + getId()
                + "] - END route: --------------------------------------------------------------------------\n");
        getSimulator().validateCounters();
    }

    private void updateActivityState(final Message message)
    {
        logger.debug("Time: (" + getSimulator().now() + ") - [" + getId() + "] - UpdateActivityState: Completed: ("
                + message.getFrom() + ") and routing to: (" + message.getTo() + ")");
        logger.debug("Time: (" + getSimulator().now() + ") - [" + getId() + "] - UpdateActivityState: BEFORE\nCurrent Path: "
                + getCurrentPath() + "\nStates   : " + getSimulator().getSimulationState().getActivityStateCounts()
                + "\nResources: " + getSimulator().getSimulationState().getActivityStateQueueResources()
                + "\nQueues   : " + getSimulator().getSimulationState().getActivityStateQueues());
        if (getActivityState() != null && !getActivityState().equals("")) {
            decrementCount();
            decrementUsage(message.getFrom());
        }
        String newActivityState = message.getTo();
        this.activityState = newActivityState;
        incrementCount();
        if (!message.isEnqueueMessageType()) {
            incrementUsage(newActivityState);
        }
        updatePath();
        logger.debug("Time: (" + getSimulator().now() + ") - [" + getId() + "] - UpdateActivityState: AFTER\nCurrent Path: "
                + getCurrentPath() + "\nStates   : " + getSimulator().getSimulationState().getActivityStateCounts()
                + "\nResources: " + getSimulator().getSimulationState().getActivityStateQueueResources()
                + "\nQueues   : " + getSimulator().getSimulationState().getActivityStateQueues());
    }

    private void updatePath()
    {
        if (getCurrentPath() != null && !getCurrentPath().equals("")) {
            this.currentPath += ",";
        }
        this.currentPath += this.activityState;
    }

    private void incrementCount()
    {
        getSimulator().getSimulationState().incrementCount(getActivityState());
    }

    private void incrementUsage(final String nodeId)
    {
        getSimulator().getSimulationState().increaseUsage(nodeId);
    }

    private void decrementCount()
    {
        getSimulator().getSimulationState().decrementCount(getActivityState());
    }

    private void decrementUsage(final String nodeId)
    {
        getSimulator().getSimulationState().decreaseUsage(nodeId);
    }

    public static void printSummary(final Simulator simulator)
    {
        logger.debug("Time: (" + simulator.now() + ") - Simulator State:\nStates   : "
                + ((WorkflowSimulator) simulator).getSimulationState().getActivityStateCounts() + "\nResources: "
                + ((WorkflowSimulator) simulator).getSimulationState().getActivityStateQueueResources()
                + "\nQueues   : " + ((WorkflowSimulator) simulator).getSimulationState().getActivityStateQueues());
    }

    public static void setDisplay(final PrintSimulationState printSimulationState)
    {
        display = printSimulationState;
    }

    private static final Logger logger = LoggerFactory.getLogger(WorkflowInstance.class);
}
