package com.servicenow.processmining.extensions.pm.model;

import java.util.Comparator;

public class ProcessMiningModelVariantByFrequencyComparator
    implements Comparator<ProcessMiningModelVariant>
{
    public int compare(ProcessMiningModelVariant v1, ProcessMiningModelVariant v2) 
    {
        if (v2.getFrequency() == v1.getFrequency()) {
            return v2.getId().compareTo(v1.getId());
        }

        return v2.getFrequency()-v1.getFrequency();
    }
}