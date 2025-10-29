package com.servicenow.processmining.extensions.pm.demo;

public class DemoModelTaskEntry
{
    private String userId = null;
    private String hostName = null;
    private String applicationName = null;
    private String screenName = null;
    private String url = null;
    private double percentageOfTotalTaskExecutionTime = -1;
    private int mouseClickCount = -1;

    public DemoModelTaskEntry()
    {
    }
    
    public void setUserId(final String userId)
    {
        this.userId = userId;
    }

    public String getUserId()
    {
        return this.userId;
    }

    public void setHostName(final String hostName)
    {
        this.hostName = hostName;
    }

    public String getHostName()
    {
        return this.hostName;
    }

    public void setApplicationName(final String applicationName)
    {
        this.applicationName = applicationName;
    }

    public String getApplicationName()
    {
        return this.applicationName;
    }

    public void setScreenName(final String screenName)
    {
        this.screenName = screenName;
    }

    public String getScreenName()
    {
        return this.screenName;
    }

    public void setURL(final String url)
    {
        this.url = url;
    }

    public String getURL()
    {
        return this.url;
    }

    public void setPercentageOfTotalTaskExecutionTime(final double percentageOfTotalTaskExecutionTime)
    {
        this.percentageOfTotalTaskExecutionTime = percentageOfTotalTaskExecutionTime;
    }

    public double getPercentageOfTotalTaskExecutionTime()
    {
        return this.percentageOfTotalTaskExecutionTime;
    }

    public void setMouseClickCount(final int mouseClickCount)
    {
        this.mouseClickCount = mouseClickCount;
    }

    public int getMouseClickCount()
    {
        return this.mouseClickCount;
    }
}
