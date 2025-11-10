package com.servicenow.processmining.extensions.pm.timeline;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.joda.time.DateTime;
import org.junit.Test;

public class InstanceTimelineTest
{
    @Test
    public void test()
    {
        loadInstanceTimeline();
    }

    private void loadInstanceTimeline()
    {
        Timeline it = new Timeline();
        String filePath = "/Users/eduardo.chiocconi/Downloads/sys_audit.csv";
        String line;
        String delimiter = "\",\"";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while ((line = br.readLine()) != null) {
                String[] values = line.split(delimiter);
                values[0] = values[0].substring(1);
                String instanceId = values[3];
                String startId = values[6];
                String endId = values[7];
                DateTime startTime = new DateTime();
                DateTime endTime = new DateTime().plusDays(1);
                InstanceTimelineItem item = new InstanceTimelineItem(instanceId, startId, endId, startTime, endTime);
                it.getTimeline().add(item);
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }
    }
}
