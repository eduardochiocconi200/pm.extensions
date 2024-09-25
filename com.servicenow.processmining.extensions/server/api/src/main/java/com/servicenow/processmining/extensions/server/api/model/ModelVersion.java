package com.servicenow.processmining.extensions.server.api.model;

public class ModelVersion
    implements Comparable<ModelVersion>
{
    private String id = null;
    private String type = null;
    private String modelName = null;
    private String modelVersion = null;
    private String createdOn = null;
    private String updatedOn = null;
    private long averageExecutionTime = 0;
    private long successfulInstances = 0;
    private long failedInstances = 0;
    
    public ModelVersion(final String id, final String type, final String name, final String version)
    {
        this.id = id;
        this.type = type;
        this.modelName = name;
        this.modelVersion = version;
    }

    public String getId()
    {
        return this.id;
    }

    public String getType()
    {
        return this.type;
    }

    public String getModelName()
    {
        return this.modelName;
    }

    public String getModelVersion()
    {
        return this.modelVersion;
    }

    public void setCreatedOn(final String createdOn)
    {
        this.createdOn = createdOn;
    }

    public String getCreatedOn()
    {
        return this.createdOn;
    }

    public void setUpdatedOn(final String updatedOn)
    {
        this.updatedOn = updatedOn;
    }

    public String getUpdatedOn()
    {
        return this.updatedOn;
    }

    public void setAverageExecutionTime(final long avgExecTime)
    {
        this.averageExecutionTime = avgExecTime;
    }

    public long getAverageExecutionTime()
    {
        return this.averageExecutionTime;
    }

    public void setSuccessfulInstances(final long successfulExecutions)
    {
        this.successfulInstances = successfulExecutions;
    }
    
    public long getSuccessfulInstances()
    {
        return this.successfulInstances;
    }

    public void setFailedInstances(final long unsuccessfulExecutions)
    {
        this.failedInstances = unsuccessfulExecutions;
    }

    public long getFailedInstances()
    {
        return this.failedInstances;
    }

    @Override
    public int compareTo(ModelVersion mv)
    {
        return getModelName().compareTo(mv.getModelName());
    }

    public String toString()
    {
        return "[ModelVersion: (" + getId() + ", " + getType() + ", " + getModelName() + ", " + getModelVersion() + ", " + getCreatedOn() + ", " + getUpdatedOn() + ", " + getAverageExecutionTime() + ", " + getSuccessfulInstances() + ", " + getFailedInstances() + ")]";
    }

    public static final String WORKFLOW_TYPE = "workflow";
    public static final String FLOW_TYPE = "flow";
    public static final String SUBFLOW_TYPE = "subflow";
    public static final String PLAYBOOK_TYPE = "playbook";
}