package com.servicenow.processmining.extensions.pm.timeline;

import org.joda.time.DateTime;

public class InstanceTimelineItem
    extends TimelineItem
{
    public InstanceTimelineItem(final String instanceId, final String startId, final String endId, final DateTime previousTime, final DateTime eventTime)
    {
        super(instanceId, startId, endId, eventTime, previousTime, null);
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