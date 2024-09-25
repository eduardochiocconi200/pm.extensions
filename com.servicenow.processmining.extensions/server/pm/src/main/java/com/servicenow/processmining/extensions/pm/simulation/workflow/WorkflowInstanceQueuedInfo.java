package com.servicenow.processmining.extensions.pm.simulation.workflow;

public class WorkflowInstanceQueuedInfo
{
    private WorkflowInstance id = null;
    private String fromNode = null;
    private String toNode = null;
    private double completionTime = 0.0;

    public WorkflowInstanceQueuedInfo(final WorkflowInstance id, final String fromNode, final String toNode, final double completionTime)
    {
        this.id = id;
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.completionTime = completionTime;
    }

    public WorkflowInstance getId()
    {
        return this.id;
    }

    public String getFromNode()
    {
        return this.fromNode;
    }

    public String getToNode()
    {
        return toNode;
    }

    public double getCompletionTime()
    {
        return this.completionTime;
    }

    public String toString()
    {
        return "[ WorkflowInstanceQueueInfo : id: '" + getId().getId() + "'', from: '" + getFromNode() + "', to: '"
                + getToNode() + "', completion time: '" + getCompletionTime() + "']";
    }
}