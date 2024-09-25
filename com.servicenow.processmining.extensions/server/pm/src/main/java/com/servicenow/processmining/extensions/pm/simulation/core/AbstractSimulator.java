package com.servicenow.processmining.extensions.pm.simulation.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractSimulator
{
    public OrderedSet events = null;

    public void insert(AbstractEvent e)
    {
        events.insert(e);
        logger.debug(printEventList());
    }

    private String printEventList()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("\nState of Simulation Scheduled Events Queue: [\n");
        boolean hasProcessedFirstElement = false;
        for (AbstractEvent e : events.getElements().keySet()) {
            if (hasProcessedFirstElement) {
                sb.append(",\n");
            }
            sb.append(e.toString());
            hasProcessedFirstElement = true;
        }
        sb.append("]\n");

        return sb.toString();
    }

    public AbstractEvent cancel(AbstractEvent e)
    {
        throw new java.lang.RuntimeException("Method not implemented");
    }

    private static final Logger logger = LoggerFactory.getLogger(AbstractSimulator.class);
}