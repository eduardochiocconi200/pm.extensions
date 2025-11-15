package com.servicenow.processmining.extensions.pm.timeline;

import java.util.ArrayList;

import org.joda.time.DateTime;

public class TimelineItem
    implements Comparable<TimelineItem>
{
    private String id = null;
    private String startId = null;
    private String endId = null;
    private DateTime eventTime = null;
    private DateTime previousEventTime = null;
    private DateTime nextEventTime = null;
    private ArrayList<DateTime> overlappingTasks = null;

    public TimelineItem(final String id, final String startId, final String endId, final DateTime eventTime, final DateTime previousTime, final DateTime nextTime)
    {
        this.id = id;
        this.startId = startId;
        this.endId = endId;
        this.eventTime = eventTime;
        this.previousEventTime = previousTime;
        this.nextEventTime = nextTime;
    }

    public String getId()
    {
        return this.id;
    }

    public String getStartId()
    {
        return this.startId;
    }

    public String getEndId()
    {
        return this.endId;
    }

    public DateTime getEventTime()
    {
        return this.eventTime;
    }

    public DateTime getPreviousEventTime()
    {
        return this.previousEventTime;
    }

    public void setNextEventTime(DateTime eventTime)
    {
        this.nextEventTime = eventTime;
    }

    public DateTime getNextEventTime()
    {
        return this.nextEventTime;
    }

    public Double getDurationInMillis()
    {
        return Double.valueOf(getNextEventTime().getMillis() - getEventTime().getMillis());
    }

    public ArrayList<DateTime> getOverlappingTasks()
    {
        if (this.overlappingTasks == null) {
            this.overlappingTasks = new ArrayList<DateTime>();
        }

        return this.overlappingTasks;
    }

    @Override
    public int compareTo(TimelineItem other)
    {
        if (getEventTime().getMillis() < other.getEventTime().getMillis()) {
            return -1;
        }
        else if (getEventTime().getMillis() == other.getEventTime().getMillis()) {
            if (this instanceof InstanceTimelineItem && other instanceof TaskTimelineItem) {
                return -1;
            }
        }

        return 1;
    }
}