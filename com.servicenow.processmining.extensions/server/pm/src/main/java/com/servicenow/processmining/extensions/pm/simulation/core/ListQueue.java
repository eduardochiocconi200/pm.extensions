package com.servicenow.processmining.extensions.pm.simulation.core;

import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListQueue
    extends OrderedSet
{
    private TreeMap<AbstractEvent, AbstractEvent> elements = new TreeMap<AbstractEvent, AbstractEvent>();

    public void insert(AbstractEvent x)
    {
        elements.put(x, x);
    }

    public AbstractEvent getFirst()
    {
        if (elements.size() == 0)
            return null;

        Event eKey = (Event) elements.firstEntry().getKey();

        return eKey;
    }

    public AbstractEvent removeFirst()
    {
        // printListQueue("BEFORE REMOVE. Size: (" + elements.size() + "), ");
        if (elements.size() == 0)
            return null;

        Event eKey = (Event) elements.firstEntry().getKey();
        elements.remove(eKey);
        // printListQueue("AFTER REMOVE. Size: (" + elements.size() + "), ");

        return eKey;
    }

    public AbstractEvent remove(AbstractEvent x)
    {
        if (elements.size() > 0) {
            Event e = (Event) elements.get(x);
            elements.remove(e);
            return e;
        }

        return null;
    }

    public TreeMap<AbstractEvent, AbstractEvent> getElements()
    {
        return this.elements;
    }

    public int size()
    {
        return elements.size();
    }

    public void printListQueue(final String prefix)
    {
        logger.debug(prefix + "Queue Elements: [" + getElements().values() + "]");
    }

    private static final Logger logger = LoggerFactory.getLogger(ListQueue.class);
}