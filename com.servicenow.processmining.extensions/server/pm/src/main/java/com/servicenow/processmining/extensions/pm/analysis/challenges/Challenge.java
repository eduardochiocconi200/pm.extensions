package com.servicenow.processmining.extensions.pm.analysis.challenges;

public class Challenge
{
    private String name = null;
    
    public Challenge(final String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    protected static final String TRIAGE_CHALLENGE_NAME = "Triage";
    protected static final String BAD_MULTITASKING_CHALLENGE_NAME = "Bad Multitasking";
    protected static final String SYNCHRONIZATION_CHALLENGE_NAME = "Synchronization";
    protected static final String STANDARIZATION_CHALLENGE_NAME = "Standarization";
    protected static final String SEGREGATION_CHALLENGE_NAME = "Segregation";
    protected static final String DOSAGE_CHALLENGE_NAME = "Dosage";
    protected static final String TIME_BUFFERS_CHALLENGE_NAME = "Time Buffers";
    protected static final String FULL_KIT_CHALLENGE_NAME = "Full Kit";
}