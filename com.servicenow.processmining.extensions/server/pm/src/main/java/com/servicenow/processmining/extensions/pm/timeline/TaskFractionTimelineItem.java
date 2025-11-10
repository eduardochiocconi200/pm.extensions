package com.servicenow.processmining.extensions.pm.timeline;

import org.joda.time.DateTime;

public class TaskFractionTimelineItem
    extends TaskTimelineItem
{
    private TaskTimelineItem parentTask = null;

    public TaskFractionTimelineItem(final String instanceId, final String taskId, final TaskTimelineItem parentTask, final String startId, final String endId, final DateTime startTime, final DateTime endTime)
    {
        super(instanceId, taskId, startId, endId, startTime, endTime);
        this.parentTask = parentTask;
    }

    public TaskTimelineItem getParentTask()
    {
        return this.parentTask;
    }

    public String toString()
    {
        String s = "[" + getEventTime() + "] (" + getId() + ") - (TaskFraction): Task Name: (" + getStartId() + "), Parent Task Name: (" + getParentTask().getStartId() + "), \n";
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