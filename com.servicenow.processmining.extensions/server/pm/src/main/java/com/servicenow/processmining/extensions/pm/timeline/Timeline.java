package com.servicenow.processmining.extensions.pm.timeline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class Timeline
{
    private ArrayList<TimelineItem> timeline = null;
    private HashMap<DateTime, ArrayList<TaskFractionTimelineItem>> taskFractions = null;
    private ArrayList<TaskTimelineItem> tasksTimeline = null;

    public Timeline()
    {
    }

    public ArrayList<TimelineItem> getTimeline()
    {
        if (this.timeline == null) {
            this.timeline = new ArrayList<TimelineItem>();
        }

        return this.timeline;
    }

    public ArrayList<TimelineItem> getSortedTimeline()
    {
        Collections.sort(getTimeline());

        /*
        int i=0;
        for (TimelineItem ti : getTimeline()) {
            System.out.println("SORTED[" + i + "] = (" + ti.getEventTime() + "), Id: (" + ti.getId() + "), " + ti.getEndId() + ")");
            i++;
        }
        */

        return getTimeline();
    }

    public ArrayList<TaskTimelineItem> getTasksTimeline()
    {
        return this.tasksTimeline;
    }

    public ArrayList<TaskTimelineItem> getSortedTasksTimeline()
    {
        Collections.sort(getTasksTimeline());

        return this.tasksTimeline;
    }

    public boolean add(TimelineItem item)
    {
        return getTimeline().add(item);
    }

    public boolean completeChain()
    {
        for (int i=0; i < getTimeline().size(); i++) {
            TimelineItem ti = getTimeline().get(i);
            if (ti instanceof InstanceTimelineItem && ti.getNextEventTime() == null) {
                connectNextInstanceTimelineItem(i, ti);
            }
        }

        return true;
    }

    private void connectNextInstanceTimelineItem(int i, TimelineItem ti)
    {
        String id = ti.getId();
        for (int j=i+1; j < getTimeline().size(); j++) {
            TimelineItem tj = getTimeline().get(j);
            if (tj instanceof InstanceTimelineItem && id.equals(tj.getId())) {
                ti.setNextEventTime(tj.getEventTime());
                break;
            }
        }
    }

    public boolean print()
    {
        for (TimelineItem ti : getTimeline()) {
            System.out.println(ti.toString());
        }

        return true;
    }

    public boolean printSorted()
    {
        for (TimelineItem ti : getSortedTimeline()) {
            System.out.println(ti.toString());
        }

        return true;
    }

    public boolean chunkTasks()
    {
        if (!identifyTaskOverlaps()) {
            return false;
        }
        if (!createTaskFractions()) {
            return false;
        }
        if (!createSubTask()) {
            return false;
        }

        return true;
    }

    private boolean identifyTaskOverlaps()
    {
        HashMap<DateTime, TimelineItem> openTasks = new HashMap<DateTime, TimelineItem>();

        for (TimelineItem ti : getSortedTimeline()) {
            if (ti instanceof TaskTimelineItem) {
                // Add overlapping tasks (after cleanup) to timeline -event.
                for (DateTime taskTime : openTasks.keySet()) {
                    TimelineItem taskTI = openTasks.get(taskTime);
                    if (taskTI.getEventTime().getMillis() < ti.getEventTime().getMillis()) {
                        if (!taskTI.getOverlappingTasks().contains(ti.getEventTime())) {
                            taskTI.getOverlappingTasks().add(ti.getEventTime());
                        }
                    }
                }
                openTasks.put(ti.getEventTime(), ti);
            }
            else if (ti instanceof InstanceTimelineItem) {
                if (openTasks.size() > 0) {
                    // We need to check if we have to remove opened tasks
                    ArrayList<DateTime> removeTasks = new ArrayList<DateTime>();
                    for (DateTime taskTime : openTasks.keySet()) {
                        TimelineItem taskItem = openTasks.get(taskTime);
                        if (taskItem.getNextEventTime().getMillis() <= ti.getEventTime().getMillis()) {
                            removeTasks.add(taskTime);
                        }
                    }

                    for (DateTime taskTime : removeTasks) {
                        DateTime removedTaskEndTime = openTasks.get(taskTime).getNextEventTime();
                        openTasks.remove(taskTime);
                        // For each opened task, we need to mark that a task is being removed.
                        for (TimelineItem taskTI : openTasks.values()) {
                            if (!taskTI.getOverlappingTasks().contains(removedTaskEndTime)) {
                                taskTI.getOverlappingTasks().add(removedTaskEndTime);
                            }
                        }
                    }
                }
            }
        }

        /*
        System.out.println("TASKS WITH OVERLAPS");
        for (TimelineItem ti : getSortedTimeline()) {
            if (ti instanceof TaskTimelineItem) {
                System.out.println(ti);
            }
        }
        */

        return true;
    }

    private boolean createTaskFractions()
    {
        taskFractions = new HashMap<DateTime, ArrayList<TaskFractionTimelineItem>>();

        for (TimelineItem ti : getSortedTimeline()) {
            if (ti instanceof TaskTimelineItem) {
                TaskTimelineItem tti = (TaskTimelineItem) ti;
                String instanceId = tti.getId();
                String startId = tti.getStartId();
                String endId = tti.getEndId();
                DateTime startTime = tti.getEventTime();
                DateTime endTime = tti.getNextEventTime();
                DateTime previousStartTask = null;
                int i=1;
                String taskId = tti.getTaskId() + "-" + i;
                for (DateTime overlappingTime : tti.getOverlappingTasks()) {
                    DateTime fractionTaskStartTime = previousStartTask == null ? startTime : previousStartTask;
                    DateTime factionTaskEndTime = overlappingTime;
                    TaskFractionTimelineItem taskFraction = new TaskFractionTimelineItem(instanceId, taskId, 
                        tti, startId, endId, fractionTaskStartTime, factionTaskEndTime);
                    taskFraction.setApplicationName(tti.getApplicationName());
                    taskFraction.setHostName(tti.getHostName());
                    taskFraction.setMouseClickCount(tti.getMouseClickCount());
                    taskFraction.setScreenName(tti.getScreenName());
                    taskFraction.setURL(tti.getURL());
                    taskFraction.setUserId(tti.getUserId());
                    ArrayList<TaskFractionTimelineItem> fractionArray = taskFractions.get(fractionTaskStartTime);
                    if (fractionArray == null) {
                        fractionArray = new ArrayList<TaskFractionTimelineItem>();
                    }
                    fractionArray.add(taskFraction);
                    taskFractions.put(fractionTaskStartTime, fractionArray);
                    previousStartTask = overlappingTime;
                    i++;
                }

                DateTime fractionTaskStartTime = previousStartTask == null ? startTime : previousStartTask;
                DateTime factionTaskEndTime = endTime;
                taskId = tti.getTaskId() + "-" + i;                
                TaskFractionTimelineItem taskFraction = new TaskFractionTimelineItem(instanceId, taskId, 
                        tti, startId, endId, fractionTaskStartTime, factionTaskEndTime);
                taskFraction.setApplicationName(tti.getApplicationName());
                taskFraction.setHostName(tti.getHostName());
                taskFraction.setMouseClickCount(tti.getMouseClickCount());
                taskFraction.setScreenName(tti.getScreenName());
                taskFraction.setURL(tti.getURL());
                taskFraction.setUserId(tti.getUserId());

                ArrayList<TaskFractionTimelineItem> fractionArray = taskFractions.get(fractionTaskStartTime);
                if (fractionArray == null) {
                    fractionArray = new ArrayList<TaskFractionTimelineItem>();
                }
                fractionArray.add(taskFraction);
                taskFractions.put(fractionTaskStartTime, fractionArray);
            }
        }

        /*
        for (TaskFractionTimelineItem tf : taskFractions) {
            System.out.println(tf);
        }
        */

        return true;
    }

    private boolean createSubTask()
    {
        tasksTimeline = new ArrayList<TaskTimelineItem>();
        TreeMap<DateTime, ArrayList<TaskFractionTimelineItem>> sortedTaskFractions = new TreeMap<>();
        sortedTaskFractions.putAll(taskFractions);

        for (DateTime startTime : sortedTaskFractions.keySet()) {
            long numberOfFractionTasks = taskFractions.get(startTime).size();
            for (int i=0; i < numberOfFractionTasks; i++) {
                TaskFractionTimelineItem tf = taskFractions.get(startTime).get(i);
                String instanceId = tf.getId();
                String taskId = tf.getParentTask().getTaskId();
                String startId = tf.getStartId();
                String endId = tf.getEndId();
                long taskDuration = tf.getNextEventTime().getMillis()-tf.getEventTime().getMillis();
                long subTaskDuration = taskDuration/numberOfFractionTasks;
                long sTimeMillis = tf.getEventTime().getMillis() + (i*subTaskDuration);
                long eTimeMillis = tf.getEventTime().getMillis() + ((i+1)*(subTaskDuration));
                DateTime sTime = new DateTime(sTimeMillis).withZone(DateTimeZone.UTC);
                DateTime eTime = new DateTime(eTimeMillis).withZone(DateTimeZone.UTC);
                TaskTimelineItem tti = new TaskTimelineItem(instanceId, taskId, startId, endId, sTime, eTime);
                tti.setApplicationName(tf.getApplicationName());
                tti.setHostName(tf.getHostName());
                tti.setMouseClickCount(tf.getMouseClickCount());
                tti.setScreenName(tf.getScreenName());
                tti.setURL(tf.getURL());
                tti.setUserId(tf.getUserId());

                tasksTimeline.add(tti);
            }
        }

        /*
        for (TaskTimelineItem tti : tasksTimeline) {
            System.out.println(tti);
        }
        */

        return true;
    }
}
