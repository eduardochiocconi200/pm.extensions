package com.servicenow.processmining.extensions.pm.timeline;

import java.util.HashMap;

import org.joda.time.DateTime;

public class InstanceTimelineItem
    extends TimelineItem
{
    private String tableName = null;
    private String tabName = null;
    private String sysId = null;
    private HashMap<String, String> updateValues = null;
    
    public InstanceTimelineItem(final String instanceId, final String startId, final String endId, final DateTime previousTime, final DateTime eventTime)
    {
        super(instanceId, startId, endId, eventTime, previousTime, null);
    }

    public void setTableName(final String tableName)
    {
        this.tableName = tableName;
    }

    public String getTableName()
    {
        return this.tableName;
    }

    public void setTabName(final String tabName)
    {
        this.tabName = tabName;
    }

    public String getTabName()
    {
        return this.tabName;
    }

    public void setSysId(final String sysId)
    {
        this.sysId = sysId;
    }

    public String getSysId()
    {
        return this.sysId;
    }

    public void setUpdateValues(final HashMap<String, String> updateValues)
    {
        this.updateValues = updateValues;
    }

    public HashMap<String, String> getUpdateValues()
    {
        return this.updateValues;
    }

    public boolean isStartEvent()
    {
        if (getPreviousEventTime() == null || (getPreviousEventTime().getMillis() == getEventTime().getMillis())) {
            return true;
        }

        return false;
    }

    public String toString()
    {
        String s = "[" + getEventTime() + "] (" + getId() + ") - (Instance): From/To: (" + getStartId() + ", " + getEndId() + "),\n";
        s += "  Previous Time: (" + getPreviousEventTime() + "), Event Time: (" + getEventTime() + "), Next Event: (" + getNextEventTime() + "),\n";
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
}