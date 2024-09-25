package com.servicenow.processmining.extensions.pm.simulation.core;

public abstract class Event
    extends AbstractEvent
    implements Comparable<AbstractEvent>
{
    protected double time;
    protected long serialNumber;

    public Event(final double t, final long serialNumber)
    {
        this.time = t;
        this.serialNumber = serialNumber;
    }

    public double getTime()
    {
        return this.time;
    }

    public long getSerialNumber()
    {
        return this.serialNumber;
    }

    @Override
    public int compareTo(AbstractEvent o)
    {
        Event e = (Event) o;
        if (this.getTime() == e.getTime() && this.getSerialNumber() == e.getSerialNumber()) {
            return 0;
        }

        if (this.getTime() == e.getTime()) {
            if (this.getSerialNumber() < e.getSerialNumber()) {
                return -1;
            }
        }

        if (this.getTime() < e.getTime()) {
            return -1;
        }

        return 1;
    }
}