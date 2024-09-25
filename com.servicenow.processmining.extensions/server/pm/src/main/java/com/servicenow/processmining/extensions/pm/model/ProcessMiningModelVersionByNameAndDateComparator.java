package com.servicenow.processmining.extensions.pm.model;

import java.util.Comparator;

import com.servicenow.processmining.extensions.pm.entities.ProcessMiningModelVersion;

public class ProcessMiningModelVersionByNameAndDateComparator
    implements Comparator<ProcessMiningModelVersion>
{
    public int compare(ProcessMiningModelVersion v1, ProcessMiningModelVersion v2) 
    {
        int c = v1.getName().compareTo(v2.getName());
        if (c == 0) {
            c = v2.getLastMinedTime().compareTo(v1.getLastMinedTime());
        }

        return c;
    }
}
