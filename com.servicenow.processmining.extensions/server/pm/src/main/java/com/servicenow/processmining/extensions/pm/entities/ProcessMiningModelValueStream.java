package com.servicenow.processmining.extensions.pm.entities;

import com.servicenow.processmining.extensions.pm.analysis.goldratt.ValueStream;
import com.servicenow.processmining.extensions.sn.entities.ServiceNowEntity;

public class ProcessMiningModelValueStream
    extends ServiceNowEntity
{
    private ValueStream valueStream = null;

    public ProcessMiningModelValueStream(ProcessMiningModelValueStreamPK pk)
    {
        super(pk);
    }

    public ValueStream getValueStream()
    {
        return this.valueStream;
    }

    public void setValueStream(final ValueStream vStream)
    {
        this.valueStream = vStream;
    }
}