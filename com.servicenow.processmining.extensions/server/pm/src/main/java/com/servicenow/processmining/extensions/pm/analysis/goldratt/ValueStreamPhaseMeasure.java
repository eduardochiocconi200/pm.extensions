package com.servicenow.processmining.extensions.pm.analysis.goldratt;

import java.util.HashMap;

public class ValueStreamPhaseMeasure
{
    private String variant = null;
    private int frequency = -1;
    private int touchpoints = -1;
    private transient HashMap<String, Integer> touchpointsPath = null;
    private long averageTime = -1;
    private long meanTime = -1;
    private long minTime = -1;
    private long maxTime = -1;

    public ValueStreamPhaseMeasure()
    {
    }

    public ValueStreamPhaseMeasure(final String variant, final int frequency, final long average, final long mean, final long min, final long max)
    {
        this.variant = variant;
        this.frequency = frequency;
        this.touchpoints = 1;
        this.averageTime = average;
        this.meanTime = mean;
        this.minTime = min;
        this.maxTime = max;
    }

    public void setVariant(final String variant)
    {
        this.variant = variant;
    }

    public String getVariant()
    {
        return this.variant;
    }

    public void setFrequency(final int frequency)
    {
        this.frequency = frequency;
    }

    public int getFrequency()
    {
        return this.frequency;
    }

    public int getTouchpoints()
    {
        return this.touchpoints;
    }


    public void setTouchpoints(int touchpoints)
    {
        this.touchpoints = touchpoints;
    }

    public void addTouchpointsPath(String phasePath)
    {
        if (getTouchpointsPath().get(phasePath) == null) {
            getTouchpointsPath().put(phasePath, 1);
        }
        else {
            int times = getTouchpointsPath().get(phasePath);
            getTouchpointsPath().remove(phasePath);
            getTouchpointsPath().put(phasePath, times+1);
        }
    }

    public HashMap<String, Integer> getTouchpointsPath()
    {
        if (this.touchpointsPath == null) {
            this.touchpointsPath = new HashMap<String, Integer>();
        }

        return this.touchpointsPath;
    }

    public void setAverageTime(final long avg)
    {
        this.averageTime = avg;
    }

    public long getAverageTime()
    {
        return this.averageTime;
    }

    public void setMeanTime(final long mean)
    {
        this.meanTime = mean;
    }

    public long getMeanTime()
    {
        return this.meanTime;
    }

    public void setMinTime(final long min)
    {
        this.minTime = min;
    }

    public long getMinTime()
    {
        return this.minTime;
    }

    public void setMaxTime(final long max)
    {
        this.maxTime = max;
    }
    public long getMaxTime()
    {
        return this.maxTime;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        sb.append("Freq: (" + getFrequency() + "), ");
        sb.append("Touch Points: (" + getTouchpoints() + "), ");
        sb.append("Avg: (" + getAverageTime() + "), ");
        sb.append("Mean: (" + getMeanTime() + "), ");
        sb.append("Min: (" + getMinTime() + "), ");
        sb.append("Max: (" + getMaxTime() + ")\n");
        sb.append("Phase paths:\n");
        boolean processedFirst = false;
        for (String key : getTouchpointsPath().keySet()) {
            if (processedFirst) {
                sb.append("\n");
            }
            sb.append(" - " + key + " x " + getTouchpointsPath().get(key) + " times.");
            processedFirst = true;
        }

        return sb.toString();
    }
}