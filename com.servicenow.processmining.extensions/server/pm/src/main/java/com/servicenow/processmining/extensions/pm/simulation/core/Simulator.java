package com.servicenow.processmining.extensions.pm.simulation.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.servicenow.processmining.extensions.pm.simulation.workflow.SimulationStatistics;

public class Simulator
    extends AbstractSimulator
{
    private double time = 0.0;
    private SimulationStatistics statistics = null;
    private SimulationGenerator generator = null;

    public Simulator()
    {
        statistics = new SimulationStatistics();
    }

    public void setGenerator(final SimulationGenerator generator)
    {
        this.generator = generator;
    }

    public SimulationStatistics getStatistics()
    {
        return this.statistics;
    }

    public SimulationGenerator getGenerator()
    {
        return this.generator;
    }

    public double now()
    {
        return time;
    }

    public double getStartTime()
    {
        return now();
    }

    public void doAllEvents()
    {
        Event e = null;
        statistics.setStartTime(now());

        // Start generating ALL or a subset of the simulation instances so we start
        // loading the events queue.
        getGenerator().createSimulationInstances();

        // Then we start processing the elements in the queue.
        while ((e = (Event) events.getFirst()) != null) {
            double nextEvenTime = e.getTime();
            // If we are incrementally adding generated instances, then we need to check if
            // we need to load
            // more into the queue before we process the next event.
            getGenerator().createSimulationInstances(nextEvenTime);

            // With the incremental creation instance strategy, we need to check if there
            // were new messages with a lower
            // timestamp added to the queue and really pick the new with the youngest time.
            e = (Event) events.getFirst();
            time = e.getTime();
            logger.debug("Time: (" + now() + ") - Dispatching Next Event from Event's Queue: (" + e + ")\n");
            events.removeFirst();
            e.execute(this);
        }
        statistics.setEndTime(now());

        logger.info("Simulation Ended!");
        logger.info("Simulation Statistics:\n" + getStatistics().getSummary());
    }

    private static final Logger logger = LoggerFactory.getLogger(Simulator.class);
}