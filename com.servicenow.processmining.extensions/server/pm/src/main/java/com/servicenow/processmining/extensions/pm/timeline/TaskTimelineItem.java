package com.servicenow.processmining.extensions.pm.timeline;

import org.joda.time.DateTime;

public class TaskTimelineItem
    extends TimelineItem
{
    private String taskId = null;

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
}