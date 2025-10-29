package com.servicenow.processmining.extensions;

import com.servicenow.processmining.extensions.pm.demo.DemoModelTaskEntry;

import java.util.ArrayList;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Assert;
import org.junit.Test;

// https://empechiocconi1.service-now.com/api/now/table/sys_audit?sysparm_display_value=false&sysparm_query=tablename=incident%5efieldnameINstate,assigned_to%5edocumentkeyIN816142952bbc7a90c9a1f9b36e91bf78%5esys_created_on%3E=2025-06-01%2009:33:20%5esys_created_on%3C=2025-10-24%2006:26:16&sysparm_fields=sys_id,documentkey,tablename,fieldname,oldvalue,newvalue,reason,sys_created_on,sys_created_by,sys_updated_on&sysparm_order=sys_created_on&sysparm_order_direction=desc

public class TaskMiningTaskCoverageWithinTimeWindowTest
{
    @Test
    public void test()
    {
        Date d = new Date(1748779478000L);
        Date dUTC = new Date(1748779478000L);
        System.out.println("d: (" + d + ")");
        System.out.println("dUTC: (" + dUTC + ")");
        DateTime previousUpdateTS = new DateTime(2025, 11, 01, 5, 4, 48, DateTimeZone.UTC);
        DateTime recordUpdateTS = new DateTime(2025, 11, 01, 6, 28, 38, DateTimeZone.UTC);;
        System.out.println("START: (" + previousUpdateTS + ")");
        System.out.println("END: (" + recordUpdateTS + ")");

        String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ";
        DateTimeFormatter formatter = DateTimeFormat.forPattern(pattern);
        String payload = "[\n";
        double totalTaskExecutionTimeInMillis = recordUpdateTS.getMillis() - previousUpdateTS.getMillis();

        boolean processedFirstEntry = false;
        Double taskDuration = 0.0;
        Double accumulatedTasksDuration = 0.0;
        for (DemoModelTaskEntry taskEntry : getTaskEntries()) {
            if (processedFirstEntry) {
                payload += ",\n";
            }

            // Make Proper time + timezone adjustments.
            String formattedDateTime = adjustTaskDateTime(previousUpdateTS, formatter, accumulatedTasksDuration);
            System.out.println("---> Formatted Date Time: (" + formattedDateTime + ")");
            taskDuration = ((taskEntry.getPercentageOfTotalTaskExecutionTime()*totalTaskExecutionTimeInMillis)/100.00);
            accumulatedTasksDuration += taskDuration;

            payload += "{\n";
            payload += "\"userId\": \"" + taskEntry.getUserId() + "\", \n";
            payload += "\"hostName\": \"" + taskEntry.getHostName() + "\", \n";
            payload += "\"applicationName\": \"" + taskEntry.getApplicationName() + "\", \n";
            payload += "\"screenName\": \"" + taskEntry.getScreenName() + "\", \n";
            payload += "\"url\": \"" + (taskEntry.getURL() == null ? "" : taskEntry.getURL()) + "\", \n";
            payload += "\"startDateTime\": \"" + formattedDateTime + "\", \n";
            payload += "\"duration\": " + (taskDuration/1000.00) + ", \n";
            payload += "\"mouseClickCount\": " + taskEntry.getMouseClickCount() + " \n";
            payload += "}";
            processedFirstEntry = true;
        }

        System.out.println("Total Task (Millis): (" + totalTaskExecutionTimeInMillis + ")");
        System.out.println("Total Accumulated (Millis): (" + accumulatedTasksDuration.doubleValue() + ")");
        Assert.assertEquals(totalTaskExecutionTimeInMillis, accumulatedTasksDuration.doubleValue(), 0.0);

        payload += "\n]";

        System.out.println("PAYLOAD: (" + payload + ")");
    }

    private ArrayList<DemoModelTaskEntry> getTaskEntries()
    {
        ArrayList<DemoModelTaskEntry> entries = new ArrayList<DemoModelTaskEntry>();

        DemoModelTaskEntry entry = new DemoModelTaskEntry();
        entry.setUserId("abraham.lincoln");
        entry.setHostName("host1");
        entry.setApplicationName("Splunk");
        entry.setScreenName("Splunk Log Viewer");
        entry.setPercentageOfTotalTaskExecutionTime(15);
        entry.setMouseClickCount(15);
        entries.add(entry);

        entry = new DemoModelTaskEntry();
        entry.setUserId("abraham.lincoln");
        entry.setHostName("host1");
        entry.setApplicationName("Excel");
        entry.setScreenName("KnownIssues.xlsx");
        entry.setPercentageOfTotalTaskExecutionTime(15);
        entry.setMouseClickCount(250);
        entries.add(entry);

        entry = new DemoModelTaskEntry();
        entry.setUserId("abraham.lincoln");
        entry.setHostName("host1");
        entry.setApplicationName("Excel");
        entry.setScreenName("KnownIssues.xlsx");
        entry.setPercentageOfTotalTaskExecutionTime(60);
        entry.setMouseClickCount(250);
        entries.add(entry);

        entry = new DemoModelTaskEntry();
        entry.setUserId("abraham.lincoln");
        entry.setHostName("host1");
        entry.setApplicationName("Excel");
        entry.setScreenName("KnownIssues.xlsx");
        entry.setPercentageOfTotalTaskExecutionTime(10);
        entry.setMouseClickCount(250);
        entries.add(entry);

        return entries;
    }

    private String adjustTaskDateTime(final DateTime recordUpdateTS, final DateTimeFormatter formatter, final Double duration)
    {
        DateTime taskDateTime = recordUpdateTS.plusMillis(duration.intValue());

        DateTimeZone localTimeZone = DateTimeZone.getDefault();
        DateTimeZone utcTimeZone = DateTimeZone.forID("UTC");

        // Get the offset in milliseconds for each time zone at the reference instant
        int localTZOffsetMillis = localTimeZone.getOffset(taskDateTime);
        int utcTZOffsetMillis = utcTimeZone.getOffset(taskDateTime);

        // Calculate the difference in offsets
        int offsetDifferenceMillis = utcTZOffsetMillis - localTZOffsetMillis;

        // Convert the difference to hours
        taskDateTime = taskDateTime.plusMillis(offsetDifferenceMillis);
       
        String formattedDateTime = formatter.print(taskDateTime);

        return formattedDateTime;
    }
}
