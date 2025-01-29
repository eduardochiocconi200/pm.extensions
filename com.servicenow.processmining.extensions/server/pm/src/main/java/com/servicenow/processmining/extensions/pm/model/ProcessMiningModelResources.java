package com.servicenow.processmining.extensions.pm.model;

import java.io.Serializable;

public class ProcessMiningModelResources
    implements Serializable
{
    private String id = null;
    private String name = null;
    private int capacity = 0;

    public ProcessMiningModelResources(final String resourceId, final String resourceName, final int resourceCapacity)
    {
        this.id = resourceId;
        this.name = resourceName;
        this.capacity = resourceCapacity;
    }

    public String getId()
    {
        return this.id;
    }

    public String getName()
    {
        return "Resource '" + this.name + "'";
    }

    public int getCapacity()
    {
        return this.capacity;
    }

    public static final int UNLIMITED = -1;
}