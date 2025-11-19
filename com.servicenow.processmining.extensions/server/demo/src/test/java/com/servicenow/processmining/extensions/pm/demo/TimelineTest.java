package com.servicenow.processmining.extensions.pm.demo;

import org.junit.Assert;

import com.servicenow.processmining.extensions.sn.core.ServiceNowInstance;
import com.servicenow.processmining.extensions.sn.core.ServiceNowTestCredentials;

public class TimelineTest
{

    public static void main(String[] args)
    {
        boolean createInstances = true;
        boolean createTasks = true;

        long startTime = System.currentTimeMillis();
        // DemoModelParser parser = new DemoModelParser("src/test/resources/Incident Demo Data.xlsx", "TM-Demo-2");
        // DemoModelParser parser = new DemoModelParser("/Users/eduardo.chiocconi/Downloads/PM TM Demo Data.xlsx", "TM-Demo-4");
        DemoModelParser parser = new DemoModelParser("/Users/eduardo.chiocconi/Downloads/Incident Demo Data.xlsx", "TM-Demo-3");
        Assert.assertTrue(parser.parse());

        DemoModelTimeline timeline = new DemoModelTimeline(parser.getModel());
        Assert.assertTrue(timeline.create());
        // Assert.assertTrue(timeline.printSorted());

        ServiceNowInstance instance = new ServiceNowInstance(snInstance, snUser, snPassword);

        if (createInstances) {
            // We first create all the records and create the audit trail by updating them ...
            DemoModelProcessMiningInstances instances = new DemoModelProcessMiningInstances(timeline, instance);
            Assert.assertTrue(instances.createRecords());
        }

        if (createTasks) {
            // Then we fill in the task details.
            DemoModelTaskMiningTasks taskMiningTasks = new DemoModelTaskMiningTasks(timeline, instance);
            Assert.assertTrue(taskMiningTasks.createRecords());
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Execution Time: (" + (endTime - startTime) + ")");
    }

    protected static final String snInstance = ServiceNowTestCredentials.getInstanceName();
    protected static final String snUser = ServiceNowTestCredentials.getUserName();
    protected static final String snPassword = ServiceNowTestCredentials.getPassword();
}