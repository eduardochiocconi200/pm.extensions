package com.servicenow.processmining.extensions.pm.demo;

public class DemoModelPathEntry
{
    private double time = 0.0;
    private String field = null;
    private String values;

    public DemoModelPathEntry(final double time, final String field, final String values)
    {
        this.time = time;
        this.field = field;
        this.values = values;
    }

    public double getTime()
    {
        return this.time;
    }

    public String getField()
    {
        return this.field;
    }

    public String getValues()
    {
        return this.values;
    }

    public String toString()
    {
        return "[" + getTime() + ", " + getField() + ", " + getValues() + "]";
    }
}