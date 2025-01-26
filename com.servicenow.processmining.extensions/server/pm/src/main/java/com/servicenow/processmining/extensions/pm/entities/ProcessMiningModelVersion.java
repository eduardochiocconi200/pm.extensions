package com.servicenow.processmining.extensions.pm.entities;

import com.servicenow.processmining.extensions.sn.entities.ServiceNowEntity;

public class ProcessMiningModelVersion
    extends ServiceNowEntity
{
    private String name = null;
    private String releaseName = null;
    private String workbenchId = null;
    private String lastMineTime = null;
    private String projectId = null;
    private int totalRecords = -1;
    private int routes = -1;
    private int avgDuration = -1;
    private int medianDuration = -1;
    private int stdDeviation = -1;

    public ProcessMiningModelVersion(ProcessMiningModelVersionPK pk)
    {
        super(pk);
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    public void setReleaseName(String releaseName)
    {
        this.releaseName = releaseName;
    }

    public String getReleaseName()
    {
        return this.releaseName;
    }

    public void setWorkbenchId(String workbench)
    {
        this.workbenchId = workbench;
    }

    public String getWorkbenchId()
    {
        return this.workbenchId;
    }

    public void setLastMinedTime(String lastMinedTime)
    {
        this.lastMineTime = lastMinedTime;
    }

    public String getLastMinedTime()
    {
        return this.lastMineTime;
    }

    public void setProjectId(String projectId)
    {
        this.projectId = projectId;
    }

    public String getProjectId()
    {
        return this.projectId;
    }

    public void setTotalRecords(final int totalRecords)
    {
        this.totalRecords = totalRecords;
    }

    public int getTotalRecords()
    {
        return this.totalRecords;
    }

    public void setRoutes(final int routes)
    {
        this.routes = routes;
    }

    public int getRoutes()
    {
        return this.routes;
    }

    public void setAvgDuration(final int avgDuration)
    {
        this.avgDuration = avgDuration;
    }

    public int getAvgDuration()
    {
        return this.avgDuration;
    }

    public void setMedianDuration(final int medianDuration)
    {
        this.medianDuration = medianDuration;
    }

    public int getMedianDuration()
    {
        return this.medianDuration;
    }

    public void setStdDeviation(final int stdDeviation)
    {
        this.stdDeviation = stdDeviation;
    }

    public int getStdDeviation()
    {
        return this.stdDeviation;
    }

    public String toString()
    {
        return "[ProcessMiningModelVersion: (" + getPK().toString() + "), Name: '" + getName() + "'', Release Name: '" + getReleaseName() + "' Workbench: '" + getWorkbenchId() + "', Last Time Mined: '" + getLastMinedTime() + "', Project Id: '" + getProjectId() + "', Records: '" + getTotalRecords() + "', Routes: '" + getRoutes() + "', Avg: '" + getAvgDuration() + "', Median: '" + getMedianDuration() + "', Std Dev: '" + getStdDeviation() + "']";
    }
}
