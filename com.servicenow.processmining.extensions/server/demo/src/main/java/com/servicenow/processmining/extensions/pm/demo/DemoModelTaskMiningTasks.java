package com.servicenow.processmining.extensions.pm.demo;

import com.servicenow.processmining.extensions.pm.timeline.TaskTimelineItem;
import com.servicenow.processmining.extensions.pm.timeline.TimelineItem;
import com.servicenow.processmining.extensions.sn.core.ServiceNowInstance;
import com.servicenow.processmining.extensions.sn.core.ServiceNowRESTService;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemoModelTaskMiningTasks
{
    private DemoModelTimeline timeline = null;
    private ServiceNowInstance instance = null;

    public DemoModelTaskMiningTasks(final DemoModelTimeline timeline, final ServiceNowInstance instance)
    {
        this.timeline = timeline;
        this.instance = instance;
    }

    public DemoModelTimeline getTimeline()
    {
        return this.timeline;
    }

    private ServiceNowInstance getInstance()
    {
        return this.instance;
    }

    public boolean createRecords()
    {
        for (int i=0; i < getTimeline().getTimeline().getSortedTimeline().size(); i++) {
            TimelineItem ti = getTimeline().getTimeline().getSortedTimeline().get(i);
            if (ti instanceof TaskTimelineItem) {
                if (!processCaseTask((TaskTimelineItem) ti)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean processCaseTask(final TaskTimelineItem taskItem)
    {
        ServiceNowRESTService snrs = new ServiceNowRESTService(getInstance());
        String url = "https://" + getInstance().getInstance() + "/api/sn_tm_core/taskmininginteraction?sysparm_display_value=false";
        String tasksPayload = createTaskPayload(taskItem);
        String response = snrs.executePostRequest(url, tasksPayload);
        if (response == null || response != null && response.equals("")) {
            logger.error("Failed invoking " + "https://" + getInstance().getInstance() + "/api/sn_tm_core/taskmininginteraction endpoint.");
            logger.error("Error Code: (" + snrs.getErrorStatusCode() + ") - (" + snrs.getErrorMessage() + ")");
            return false;
        }

        return true;
    }

    private String createTaskPayload(final TaskTimelineItem taskItem)
    {
        String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ";
        DateTimeFormatter formatter = DateTimeFormat.forPattern(pattern);
        String payload = "[\n";

        // Make Proper time + timezone adjustments.
        String formattedDateTime = adjustTaskDateTime(taskItem.getEventTime(), formatter);

        payload += "{\n";
        payload += "\"userId\": \"" + taskItem.getUserId() + "\", \n";
        payload += "\"hostName\": \"" + taskItem.getHostName() + "\", \n";
        payload += "\"applicationName\": \"" + taskItem.getApplicationName() + "\", \n";
        payload += "\"screenName\": \"" + taskItem.getScreenName() + "\", \n";
        payload += "\"url\": \"" + (taskItem.getURL() == null ? "" : taskItem.getURL()) + "\", \n";
        payload += "\"startDateTime\": \"" + formattedDateTime + "\", \n";
        payload += "\"duration\": " + (taskItem.getDurationInMillis()/1000) + ", \n";
        payload += "\"mouseClickCount\": " + taskItem.getMouseClickCount() + " \n";
        payload += "}";

        payload += "\n]";
    
        return payload;
    }

    private String adjustTaskDateTime(final DateTime startTaskTS, final DateTimeFormatter formatter)
    {
        String formattedDateTime = formatter.print(startTaskTS);

        return formattedDateTime;
    }

    private static final Logger logger = LoggerFactory.getLogger(DemoModelTaskMiningTasks.class);    
}