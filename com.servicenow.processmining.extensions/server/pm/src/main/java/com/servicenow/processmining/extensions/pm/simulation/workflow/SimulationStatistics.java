package com.servicenow.processmining.extensions.pm.simulation.workflow;

public class SimulationStatistics
{
    private int numberOfCreatedInstances = 0;
    private double startTime = 0;
    private double endTime = 0;

    public SimulationStatistics()
    {
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
        sb.append("Total simulated workflow instances: " + getNumberOfCreatedInstances());

        return sb.toString();
    }
}