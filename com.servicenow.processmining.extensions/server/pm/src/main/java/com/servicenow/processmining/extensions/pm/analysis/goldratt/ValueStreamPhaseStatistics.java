package com.servicenow.processmining.extensions.pm.analysis.goldratt;

import java.util.ArrayList;

public class ValueStreamPhaseStatistics
{
    private ArrayList<ValueStreamPhaseMeasure> measures = null;
    private ValueStreamPhaseMeasure summary = null;

    public ValueStreamPhaseStatistics()
    {
    }

    public void setMeasures(final ArrayList<ValueStreamPhaseMeasure> measures)
    {
        this.measures = measures;
    }

    public ArrayList<ValueStreamPhaseMeasure> getMeasures()
    {
        if  (this.measures == null) {
            this.measures = new ArrayList<ValueStreamPhaseMeasure>();
        }

        return this.measures;
    }

    public boolean computeSummary()
    {
        int frequency = 0;
        int touchpoints = 0;
        ArrayList<String> touchpointPaths = new ArrayList<String>();
        long avg = 0;
        long mean = 0;
        long min = 0;
        long max = 0;

        for (ValueStreamPhaseMeasure m : getMeasures()) {
            frequency += m.getFrequency();
            touchpoints += m.getTouchpoints();
            for (String key : m.getTouchpointsPath().keySet()) {
                touchpointPaths.add(key);
            }
            avg += m.getAverageTime();
            mean += m.getMeanTime();
            min = m.getMinTime() < min ? m.getMinTime() : min;
            max = m.getMaxTime() > max ? m.getMaxTime() : max;
        }

        this.summary = new ValueStreamPhaseMeasure("summary", frequency, avg, mean, min, max);
        this.summary.setTouchpoints(touchpoints);
        for (String key : touchpointPaths) {
            this.summary.addTouchpointsPath(key);
        }

        return true;
    }

    public void setSummary(final ValueStreamPhaseMeasure summary)
    {
        this.summary = summary;
    }

    public ValueStreamPhaseMeasure getSummary()
    {
        return this.summary;
    }

    public String toString()
    {
        return getSummary().toString();
    }
}