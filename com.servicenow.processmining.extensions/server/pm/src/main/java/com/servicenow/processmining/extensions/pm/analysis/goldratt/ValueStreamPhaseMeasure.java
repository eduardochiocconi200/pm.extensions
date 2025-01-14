package com.servicenow.processmining.extensions.pm.analysis.goldratt;

import java.util.HashMap;

public class ValueStreamPhaseMeasure
{
    private String variant = null;
    private int frequency = -1;
    private int touchpoints = -1;
    private HashMap<String, Integer> touchpointsPath = null;
    private long averageTime = -1;
    private long meanTime = -1;
    private long minTime = -1;
    private long maxTime = -1;

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

    public String getVariantName()
    {
        return this.variant;
    }

    public int getFrequency()
    {
        return this.frequency;
    }

    public int getTouchPoints()
    {
        return this.touchpoints;
    }

    public void setTouchPoints(int touchpoints)
    {
        this.touchpoints = touchpoints;
    }

    public void addTouchPointsPath(String phasePath)
    {
        if (getTouchPointsPath().get(phasePath) == null) {
            getTouchPointsPath().put(phasePath, 1);
        }
        else {
            int times = getTouchPointsPath().get(phasePath);
            getTouchPointsPath().remove(phasePath);
            getTouchPointsPath().put(phasePath, times+1);
        }
    }

    public HashMap<String, Integer> getTouchPointsPath()
    {
        if (this.touchpointsPath == null) {
            this.touchpointsPath = new HashMap<String, Integer>();
        }

        return this.touchpointsPath;
    }

    public long getAverage()
    {
        return this.averageTime;
    }

    public long getMean()
    {
        return this.meanTime;
    }

    public long getMin()
    {
        return this.minTime;
    }

    public long getMax()
    {
        return this.maxTime;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        sb.append("Freq: (" + getFrequency() + "), ");
        sb.append("Touch Points: (" + getTouchPoints() + "), ");
        sb.append("Avg: (" + getAverage() + "), ");
        sb.append("Mean: (" + getMean() + "), ");
        sb.append("Min: (" + getMin() + "), ");
        sb.append("Max: (" + getMax() + ")\n");
        sb.append("Phase paths:\n");
        boolean processedFirst = false;
        for (String key : getTouchPointsPath().keySet()) {
            if (processedFirst) {
                sb.append("\n");
            }
            sb.append(" - " + key + " x " + getTouchPointsPath().get(key) + " times.");
            processedFirst = true;
        }

        return sb.toString();
    }
}