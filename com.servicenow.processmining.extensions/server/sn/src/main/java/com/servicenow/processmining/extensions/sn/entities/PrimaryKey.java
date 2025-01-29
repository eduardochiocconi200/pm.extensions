package com.servicenow.processmining.extensions.sn.entities;

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = SerializablePrimaryKey.class)
public abstract class PrimaryKey
    implements Serializable
{
    public PrimaryKey()
    {
    }

    public abstract boolean equals(Object other);
    public abstract int hashCode();

    private static final long serialVersionUID = 1L;
}