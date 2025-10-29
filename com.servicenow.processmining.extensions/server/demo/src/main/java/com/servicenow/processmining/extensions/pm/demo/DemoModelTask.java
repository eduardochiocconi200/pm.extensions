package com.servicenow.processmining.extensions.pm.demo;

import java.util.ArrayList;

public class DemoModelTask
{
    public String taskId = null;
    public String taskName = null;
    private ArrayList<DemoModelTaskEntry> entries = null;

    public DemoModelTask(final String taskId, final String taskName)
    {
        this.taskId = taskId;
        this.taskName = taskName;
    }

    public String getTaskId()
    {
        return this.taskId;
    }

    public String getTaskName()
    {
        return this.taskName;
    }

    public ArrayList<DemoModelTaskEntry> getEntries()
    {
        if (this.entries == null) {
            this.entries = new ArrayList<DemoModelTaskEntry>();
        }

        return this.entries;
    }

    public boolean addEntry(DemoModelTaskEntry entry)
    {
        return getEntries().add(entry);
    }
}
