package com.servicenow.processmining.extensions.pm.demo;

import com.servicenow.processmining.extensions.pm.timeline.InstanceTimelineItem;
import com.servicenow.processmining.extensions.pm.timeline.TaskTimelineItem;
import com.servicenow.processmining.extensions.pm.timeline.Timeline;

import java.util.HashMap;
import java.util.Random;
import java.util.TreeMap;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemoModelTimeline
{
    private DemoModel model = null;
    private Timeline timeline = null;
    private boolean addTimeRandomness = true;

    public DemoModelTimeline(final DemoModel model)
    {
        this.model = model;
    }

    public DemoModel getModel()
    {
        return this.model;
    }

    public Timeline getTimeline()
    {
        if (this.timeline == null) {
            this.timeline = new Timeline();
        }

        return this.timeline;
    }

    public boolean create()
    {
        for (DemoModelPath path : getModel().getPaths()) {
            DateTime batchStartTime = DateTime.now();
            DateTime startCreationOfRecords = path.getCreationStartTime();

            // DateTime pathFirstStartTime = startCreationOfRecords.minusSeconds((int)path.getTotalDuration()).withZone(DateTimeZone.UTC);
            DateTime pathFirstStartTime = startCreationOfRecords;
            System.out.println("Creating [" + path.getCount() + "] [" + path.getTable() + "] records for path defined in Tab: [" + path.getPathName() + "] starting on (" + startCreationOfRecords + "). (A '.' will be printed for each created record. Be patient!)");
            for (int count=0; count < path.getCount(); count++) {
                if (!createCase(path, pathFirstStartTime)) {
                    return false;
                }
                System.out.print(".");
                pathFirstStartTime = pathFirstStartTime.plusSeconds((int)path.getCreationDelta());
            }
            double elapsedTime = DateTime.now().minus(batchStartTime.getMillis()).getMillis() / 1000.0 / 60.0;
            System.out.println("\nCreated (" + path.getCount() + ") " + path.getTable() + " records along with its audit log records in (" + elapsedTime + ") mins.");
        }

        if (!getTimeline().completeChain()) {
            System.err.println("Could not Complete Chain.");
            return false;
        }

        if (!getTimeline().chunkTasks()) {
            System.err.println("Could not Chunk Tasks.");
            return false;
        }

        return true;
    }

    private boolean createCase(final DemoModelPath path, final DateTime createdOn)
    {
        // Create the record with initial values ...
        if (!createRecord(path, createdOn)) {
            return false;
        }

        // Perform the necessary subsequent actions to mimic the user behavior progressing the case to closure.
        if (!updateRecord(path, createdOn)) {
            return false;
        }

        return true;
    }

    private boolean createRecord(final DemoModelPath path, final DateTime createdOn)
    {
        String instanceId = String.valueOf(createdOn.getMillis());
        String startId = "created";
        String endId = path.getInitialValues().get("state");
        if (endId == null || (endId != null && endId.equals(""))) {
            throw new RuntimeException("Creation event (offset = 0) does not have any state specified value.");
        }
        DateTime previousTime = createdOn;
        DateTime eventTime = createdOn;
        InstanceTimelineItem item = new InstanceTimelineItem(instanceId, startId, endId, previousTime, eventTime);
        item.setUpdateValues(path.getInitialValues());
        item.setTableName(path.getTable());

        return getTimeline().add(item);
    }

    private boolean updateRecord(final DemoModelPath path, final DateTime createdOn)
    {
        TreeMap<Double, HashMap<String, String>> updateBatches = path.getPostInitialValues();
        DateTime recordUpdateTS = createdOn;
        Double previousUpdateTS = 0.0;
        Double previousOriginalUpdateTS = 0.0;
        Double adjustedUpdateTime = 0.0;
        String taskName = null;
        for (Double updateTime : updateBatches.keySet()) {
            logger.debug("Update Batch: (" + updateTime + ") = (" + updateBatches.get(updateTime) + ")");
            adjustedUpdateTime = updateTime;
            if (previousUpdateTS == 0.0) {
                recordUpdateTS = recordUpdateTS.plusSeconds(updateTime.intValue());
            }
            else {
                recordUpdateTS = recordUpdateTS.plusSeconds(updateTime.intValue() - previousUpdateTS.intValue());
                if (addTimeRandomness) {
                    long beforeTimeUpdate = recordUpdateTS.getMillis();
                    recordUpdateTS = adjustRandomVariation(recordUpdateTS);
                    long afterTimeUpdate = recordUpdateTS.getMillis();
                    adjustedUpdateTime = updateTime + ((afterTimeUpdate-beforeTimeUpdate)/1000);
                }
            }

            if (!processUpdateRecord(path, createdOn, updateTime, recordUpdateTS, previousUpdateTS, previousOriginalUpdateTS, updateBatches)) {
                return false;
            }

            if (taskName != null) {
                if (!processCaseTask(createdOn, previousUpdateTS, adjustedUpdateTime, taskName)) {
                    return false;
                }
            }
            taskName = null;

            if (updateBatches.get(updateTime).get(DemoModelPath.TASK_SCRIPT_FIELD_NAME) != null) {
                taskName = updateBatches.get(updateTime).get(DemoModelPath.TASK_SCRIPT_FIELD_NAME);
            }

            previousOriginalUpdateTS = updateTime;
            previousUpdateTS = adjustedUpdateTime;
        }

        return true;
    }

    private boolean processUpdateRecord(final DemoModelPath path, final DateTime createdOn, final Double updateTime, final DateTime recordUpdateTS, final Double previousUpdateTime, final Double previousOriginalUpdateTime, final TreeMap<Double, HashMap<String, String>> updateBatches)
    {
        HashMap<String, String> updateTimeUpdates = updateBatches.get(updateTime);
        HashMap<String, String> previousTimeUpdates = previousUpdateTime.equals(0.0) ? path.getInitialValues() : updateBatches.get(previousOriginalUpdateTime);
        String instanceId = String.valueOf(createdOn.getMillis());
        String startId = previousTimeUpdates.get("state");
        String endId = updateTimeUpdates.get("state");
        if (endId == null || (endId != null && endId.equals(""))) {
            throw new RuntimeException("Creation event (offset = 0) does not have any state specified");
        }
        DateTime previousTime = createdOn.plusMillis(previousUpdateTime.intValue()*1000).withZone(DateTimeZone.UTC);
        DateTime eventTime = recordUpdateTS;
        InstanceTimelineItem item = new InstanceTimelineItem(instanceId, startId, endId, previousTime, eventTime);
        item.setUpdateValues(path.getPostInitialValues().get(updateTime));
        item.setTableName(path.getTable());

        return getTimeline().add(item);
    }

    private boolean processCaseTask(final DateTime createdOn, final Double taskStartTime, final Double taskEndTime, final String taskName)
    {
        Double taskDuration = 0.0;
        Double accumulatedTasksDuration = 0.0;
        DateTime taskStartDateTime = createdOn.plusMillis(taskStartTime.intValue()*1000).withZone(DateTimeZone.UTC);
        DateTime taskEndDateTime = createdOn.plusMillis(taskEndTime.intValue()*1000).withZone(DateTimeZone.UTC);
        double totalTaskExecutionTimeInMillis = taskEndDateTime.getMillis() - taskStartDateTime.getMillis();

        DemoModelTask dmt = getModel().getTask(taskName);
        TaskTimelineItem previousItem = null;
        for (DemoModelTaskEntry taskEntry : dmt.getEntries()) {
            // Make Proper time + timezone adjustments.
            taskDuration = ((taskEntry.getPercentageOfTotalTaskExecutionTime()*totalTaskExecutionTimeInMillis)/100.00);
            accumulatedTasksDuration += taskDuration;
            String instanceId = String.valueOf(createdOn.getMillis());
            String taskId = taskEntry.getApplicationName();
            String startId = taskEntry.getApplicationName();
            String endId = taskEntry.getApplicationName();
            DateTime startTime = previousItem == null ? taskStartDateTime : previousItem.getNextEventTime();
            DateTime endTime = taskStartDateTime.plusMillis(accumulatedTasksDuration.intValue());
            TaskTimelineItem item = new TaskTimelineItem(instanceId, taskId, startId, endId, startTime, endTime);
            item.setApplicationName(taskEntry.getApplicationName());
            item.setHostName(taskEntry.getHostName());
            item.setMouseClickCount(String.valueOf(taskEntry.getMouseClickCount()));
            item.setScreenName(taskEntry.getScreenName());
            item.setURL(taskEntry.getURL());
            item.setUserId(taskEntry.getUserId());
            getTimeline().add(item);
            if (previousItem != null) {
                previousItem.setNextEventTime(startTime);
            }
            previousItem = item;
        }

        previousItem.setNextEventTime(taskEndDateTime);

        if (!taskEndDateTime.isEqual(taskStartDateTime.plusMillis(accumulatedTasksDuration.intValue()))) {
            throw new RuntimeException("INVALID TASK");
        }

        return true;
    }

    // Let's add a random deviation to the createdOn to avoid all data being equally sparsed.
    private DateTime adjustRandomVariation(final DateTime recordUpdateTS)
    {
        Random random = new Random();
        // We will create a randomness of 2 mins (120 seconds) + o - the next time.
        int seconds = (int) random.nextDouble(120);
        seconds = (seconds % 2 == 0) ? seconds : (seconds * -1);
        DateTime adjustedTime = recordUpdateTS.plusSeconds(seconds).withZone(DateTimeZone.UTC);

        return adjustedTime;
    }

    public boolean print()
    {
        return getTimeline().print();
    }

    public boolean printSorted()
    {
        return getTimeline().printSorted();
    }

    private static final Logger logger = LoggerFactory.getLogger(DemoModelTimeline.class);
}
