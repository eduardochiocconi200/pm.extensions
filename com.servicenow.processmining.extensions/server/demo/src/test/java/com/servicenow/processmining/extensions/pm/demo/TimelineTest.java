package com.servicenow.processmining.extensions.pm.demo;

import org.junit.Assert;

public class TimelineTest
{

    public static void main(String[] args)
    {
        long startTime = System.currentTimeMillis();
        // DemoModelParser parser = new DemoModelParser("src/test/resources/Incident Demo Data.xlsx", "TM-Demo-2");
        DemoModelParser parser = new DemoModelParser("/Users/eduardo.chiocconi/Downloads/Incident Demo Data.xlsx", "TM-Demo-3");
        Assert.assertTrue(parser.parse());

        DemoModelTimeline timeline = new DemoModelTimeline(parser.getModel());
        Assert.assertTrue(timeline.create());
        // Assert.assertTrue(timeline.printSorted());
        long endTime = System.currentTimeMillis();
        System.out.println("Execution Time: (" + (endTime - startTime) + ")");
    }
}