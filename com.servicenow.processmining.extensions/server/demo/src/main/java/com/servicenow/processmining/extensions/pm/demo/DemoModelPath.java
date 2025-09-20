package com.servicenow.processmining.extensions.pm.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import org.joda.time.DateTime;

public class DemoModelPath
{
    private String pathName = null;
    private double totalDuration = 0.0;
    private int count = 0;
    private DateTime creationStartTime = null;
    private double creationDelta = 0.0;
    private String table = null;
    private ArrayList<DemoModelPathEntry> entries = null;

    public DemoModelPath(final String pathName, final int count, final DateTime creationStartTime, final double creationDelta, final String table)
    {
        this.pathName = pathName;
        this.count = count;
        this.creationStartTime = creationStartTime;
        this.creationDelta = creationDelta;
        this.table = table;
    }

    public String getPathName()
    {
        return this.pathName;
    }

    public int getCount()
    {
        return this.count;
    }

    public DateTime getCreationStartTime()
    {
        return this.creationStartTime;
    }

    public double getCreationDelta()
    {
        return this.creationDelta;
    }

    public String getTable()
    {
        return this.table;
    }

    public void setTotalDuration(final double totalDuration)
    {
        this.totalDuration = totalDuration;
    }

    public double getTotalDuration()
    {
        return this.totalDuration;
    }

    public boolean addEntry(DemoModelPathEntry entry)
    {
        return getEntries().add(entry);
    }

    public ArrayList<DemoModelPathEntry> getEntries()
    {
        if (this.entries == null) {
            this.entries = new ArrayList<DemoModelPathEntry>();
        }

        return this.entries;
    }

    public HashMap<String, String> getInitialValues()
    {
        HashMap<String, String> values = new HashMap<String, String>();
        for (DemoModelPathEntry entry : getEntries()) {
            if (entry.getTime() == 0.0) {
                values.put(entry.getField(), entry.getValues());
            }
            else {
                break;
            }
        }

        return values;
    }

    public TreeMap<Double, HashMap<String, String>> getPostInitialValues()
    {
        TreeMap<Double, HashMap<String, String>> values = new TreeMap<Double, HashMap<String, String>>();
        double currentUpdateBatch = 0.0;
        for (DemoModelPathEntry entry : getEntries()) {
            if (entry.getTime() > 0.0) {
                if (currentUpdateBatch == 0.0) {
                    currentUpdateBatch = entry.getTime();
                    values.put(currentUpdateBatch, new HashMap<String, String>());
                }
                else if (currentUpdateBatch != entry.getTime()) {
                    currentUpdateBatch = entry.getTime();
                    values.put(currentUpdateBatch, new HashMap<String, String>());
                }
                values.get(currentUpdateBatch).put(entry.getField(), entry.getValues());
            }
        }

        return values;
    }
}
