package com.servicenow.processmining.extensions.sn.entities;

import java.io.Serializable;

public abstract class PrimaryKey
    implements Serializable
{
    public abstract boolean equals(Object other);
    public abstract int hashCode();
    private static final long serialVersionUID = 1L;
}
