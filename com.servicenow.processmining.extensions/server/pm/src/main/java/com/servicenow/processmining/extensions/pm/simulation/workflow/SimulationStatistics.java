package com.servicenow.processmining.extensions.pm.simulation.workflow;

import com.servicenow.processmining.extensions.pm.simulation.core.Simulator;

public class SimulationStatistics
{
    private WorkflowSimulator simulator = null;
    private int numberOfCreatedInstances = 0;
    private double startTime = 0;
    private double endTime = 0;

    public SimulationStatistics(final Simulator simulator)
    {
        this.simulator = (WorkflowSimulator) simulator;
    }

    public WorkflowSimulator getSimulator()
    {
        return this.simulator;
    }

    public void incrementCreatedInstances()
    {
        this.numberOfCreatedInstances++;
    }

    public int getNumberOfCreatedInstances()
    {
        return this.numberOfCreatedInstances;
    }

    public void setStartTime(final double st)
    {
        this.startTime = st;
    }

    public double getStartTime()
    {
        return this.startTime;
    }

    public void setEndTime(final double et)
    {
        this.endTime = et;
    }

    public double getEndTime()
    {
        return this.endTime;
    }

    public double getTotalSimulationTime()
    {
        return getEndTime() - getStartTime();
    }

    public String getSummary()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("Total Simulation Time: " + getTotalSimulationTime() + "\n");
        sb.append("Total simulated workflow instances: " + getNumberOfCreatedInstances() + "\n");
        sb.append("Activity Simulation Stats:\n");
        for (String activityId : getSimulator().getSimulationState().getProcessModel().getNodes().keySet()) {
            String activityName = getSimulator().getSimulationState().getProcessModel().getNodes().get(activityId).getName();
            int activityResourceCapacity = -1;
            if (getSimulator().getSimulationState().getProcessModel().getNodes().get(activityId).getResources() != null) {
                activityResourceCapacity = getSimulator().getSimulationState().getProcessModel().getNodes().get(activityId).getResources().getCapacity();
                int activityMaxResourceUsedCapacity = getSimulator().getSimulationState().getMaxActivityStateCount().get(activityId);
                int activityMaxQueue = getSimulator().getSimulationState().getMaxActivityStateQueue().get(activityId);
                sb.append("Activity (" + activityId + "-" + activityName + "): Available Resource Capacity: (" + (activityResourceCapacity == -1 ? "Unlimited" : activityResourceCapacity)+ "), Max Used Resource Capacity: (" + activityMaxResourceUsedCapacity + "), Max Waiting Queue size: (" + activityMaxQueue + ")\n");
            }
        }

        return sb.toString();
    }
}