package com.servicenow.processmining.extensions.pm.timeline;

import org.joda.time.DateTime;

public class TaskTimelineItem
    extends TimelineItem
{
    private String taskId = null;
    private String userId = null;
    private String hostName = null;
    private String applicationName = null;
    private String screenName = null;
    private String url = null;
    private String mouseClickCount = null;

    public TaskTimelineItem(final String instanceId, final String taskId, final String startId, final String endId, final DateTime startTime, final DateTime endTime)
    {
        super(instanceId, startId, endId, startTime, null, endTime);
        this.taskId = taskId;
    }

    public String getTaskId()
    {
        return this.taskId;
    }

    public String toString()
    {
        String s = "[" + getEventTime() + "] (" + getId() + ") - (Task): Task Name: (" + getStartId() + "),\n";
        s += "  Event Time: (" + getEventTime() + "), Next Time: (" + getNextEventTime() + "),\n";
        s += "  Overlapping Tasks: ";
        if (getOverlappingTasks() != null && getOverlappingTasks().size() > 0) {
            s += getOverlappingTasks();
        }
        else {
            s += "No Overlapping Tasks";
        }

        s += "\n";

        return s;
    }

    public void setUserId(final String userId)
    {
        this.userId = userId;
    }

    public String getUserId()
    {
        return this. userId;
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

    public void setMouseClickCount(final String mouseClickCount)
    {
        this.mouseClickCount = mouseClickCount;
    }

    public String getMouseClickCount()
    {
        return this.mouseClickCount;
    }
}