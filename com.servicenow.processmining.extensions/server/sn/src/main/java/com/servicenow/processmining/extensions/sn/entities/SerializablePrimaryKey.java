package com.servicenow.processmining.extensions.sn.entities;

public class SerializablePrimaryKey
    extends PrimaryKey
{
    public SerializablePrimaryKey()
    {
    }

    @Override
    public boolean equals(Object other)
    {
        throw new UnsupportedOperationException("Unimplemented method 'equals'");
    }

    @Override
    public int hashCode()
    {
        throw new UnsupportedOperationException("Unimplemented method 'hashCode'");
    }    
}
