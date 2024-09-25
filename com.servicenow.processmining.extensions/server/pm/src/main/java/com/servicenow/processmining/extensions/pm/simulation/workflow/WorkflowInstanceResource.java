package com.servicenow.processmining.extensions.pm.simulation.workflow;

public class WorkflowInstanceResource
{
    private String id = null;
    private String name = null;
    private int capacity = 0;
    private int usage = 0;

    public WorkflowInstanceResource(final String id, final String name, final int capacity)
    {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.usage = 0;
    }

    public String getId()
    {
        return this.id;
    }

    public String getName()
    {
        return this.name;
    }

    public int getCapacity()
    {
        return this.capacity;
    }

    public int getUsage()
    {
        return this.usage;
    }

    public boolean hasCapacity()
    {
        if (getCapacity() == UNLIMITED_CAPACITY) {
            return true;
        }

        return getUsage() < getCapacity();
    }

    public void increaseUsage()
    {
        if (this.capacity != -1 && this.usage >= this.capacity) {
            throw new RuntimeException("It is not possible to increase the resource [" + this.id + "] usage (" + this.usage + ") beyond its capacity: (" + this.capacity + ")");
        }

        this.usage++;
    }

    public void decreaseUsage()
    {
        if (this.usage == 0) {
            throw new RuntimeException("It is not possible to decrease the resource [" + this.id + "] usage (" + this.usage + ") below 0.");
        }

        this.usage--;
    }

    public String toString()
    {
        return "[ Resource Id: '" + getId() + "'', Name: '" + getName() + "'', Capacity: '" + getCapacity() + "'', Usage: '" + getUsage() + "']";
    }

    public static int UNLIMITED_CAPACITY = -1;
}