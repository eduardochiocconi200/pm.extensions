package com.servicenow.processmining.extensions.pm.analysis.goldratt;

import java.util.ArrayList;

public class ValueStreamPhaseStatistics
{
    private ArrayList<ValueStreamPhaseMeasure> measures = null;
    private ValueStreamPhaseMeasure summary = null;

    public ValueStreamPhaseStatistics()
    {
    }

    public ArrayList<ValueStreamPhaseMeasure> getMeasure()
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

        for (ValueStreamPhaseMeasure m : getMeasure()) {
            frequency += m.getFrequency();
            touchpoints += m.getTouchPoints();
            for (String key : m.getTouchPointsPath().keySet()) {
                touchpointPaths.add(key);
            }
            avg += m.getAverage();
            mean += m.getMean();
            min = m.getMin() < min ? m.getMin() : min;
            max = m.getMax() > max ? m.getMax() : max;
        }

        this.summary = new ValueStreamPhaseMeasure("summary", frequency, avg, mean, min, max);
        this.summary.setTouchPoints(touchpoints);
        for (String key : touchpointPaths) {
            this.summary.addTouchPointsPath(key);
        }

        return true;
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