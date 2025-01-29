package com.servicenow.processmining.extensions.pm.model;

import java.io.Serializable;

public class ProcessMiningModelTransition
    implements Serializable
{
    private String from = null;
    private String fromName = null;
    private String to = null;
    private String toName = null;
    private int absoluteFreq = -1;
    private int caseFreq = -1;
    private int maxReps = -1;
    private int totalDuration = -1;
    private int maxDuration = -1;
    private int minDuration = -1;
    private int avgDuration = -1;
    private int stdDeviation = -1;
    private int medianDuration = -1;

    public ProcessMiningModelTransition(final String from, final String to)
    {
        this.from = from;
        this.to = to;
    }

    public String getId()
    {
        return getFrom() + "-To-" + getTo();
    }

    public String getFrom()
    {
        return this.from;
    }

    public void setFromName(final String fromName)
    {
        this.fromName = fromName;
    }

    public String getFromName()
    {
        return this.fromName;
    }

    public String getTo()
    {
        return this.to;
    }

    public void setToName(final String toName)
    {
        this.toName = toName;
    }

    public String getToName()
    {
        return this.toName;
    }

    public void setAbsoluteFrequency(final int freq)
    {
        this.absoluteFreq = freq;
    }

    public int getAbsoluteFrequency()
    {
        return this.absoluteFreq;
    }

    public void setCaseFrequency(final int freq)
    {
        this.caseFreq = freq;
    }

    public int getCaseFrequency()
    {
        return this.caseFreq;
    }

    public void setMaxReps(final int maxReps)
    {
        this.maxReps = maxReps;
    }

    public int getMaxReps()
    {
        return this.maxReps;
    }

    public void setTotalDuration(final int totalDuration)
    {
        this.totalDuration = totalDuration;
    }

    public int getTotalDuration()
    {
        return this.totalDuration;
    }

    public void setMaxDuration(final int maxDuration)
    {
        this.maxDuration = maxDuration;
    }

    public int getMaxDuration()
    {
        return this.maxDuration;
    }

    public void setMinDuration(final int minDuration)
    {
        this.minDuration = minDuration;
    }

    public int getMinDuration()
    {
        return this.minDuration;
    }

    public void setAvgDuration(final int avgDuration)
    {
        this.avgDuration = avgDuration;
    }

    public int getAvgDuration()
    {
        return this.avgDuration;
    }

    public void setStdDeviation(final int stdDeviation)
    {
        this.stdDeviation = stdDeviation;
    }

    public int getStdDeviation()
    {
        return this.stdDeviation;
    }

    public void setMedianDuration(final int medianDuration)
    {
        this.medianDuration = medianDuration;
    }

    public int getMedianDeviation()
    {
        return this.medianDuration;
    }

    public String toString()
    {
        return "[ Transition: From: '" + getFrom() + "', From Name: '" + getFromName() + "', To: '" + getTo() + "', To Name: '" + getToName() + "', Abs.Freq: '" + getAbsoluteFrequency() + "', Case Freq: '" + getCaseFrequency() + "', maxReps: '" + getMaxReps() + "', Total Duration: '" + getTotalDuration() + "' Max Duration: '" + getMaxDuration() + "', Min Duration: '" + getMinDuration() + "', Avg Duration: '" + getAvgDuration() + "', Std Deviation: '" + getStdDeviation() + "', Median Duration: '" + getMedianDeviation() + "']";
    }
}
