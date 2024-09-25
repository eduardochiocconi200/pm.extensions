package com.servicenow.processmining.extensions.pm.simulation.core;

import java.util.TreeMap;

public abstract class OrderedSet
{
    public abstract void insert(AbstractEvent x);
    public abstract AbstractEvent getFirst();
    public abstract AbstractEvent removeFirst();
    public abstract AbstractEvent remove(AbstractEvent x);
    public abstract TreeMap<AbstractEvent, AbstractEvent> getElements();
    public abstract int size();
}