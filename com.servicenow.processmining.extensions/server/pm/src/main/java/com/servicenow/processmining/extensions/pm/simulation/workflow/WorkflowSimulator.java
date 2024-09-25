package com.servicenow.processmining.extensions.pm.simulation.workflow;

import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelVariant;
import com.servicenow.processmining.extensions.pm.simulation.core.ListQueue;
import com.servicenow.processmining.extensions.pm.simulation.core.Simulator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkflowSimulator
    extends Simulator
{
    private WorkflowInstanceSimulationState simulatorState = null;

    public WorkflowSimulator(final ProcessMiningModelVariant processModelVariant)
    {
        this.simulatorState = new WorkflowInstanceSimulationState(processModelVariant, this);
        this.events = new ListQueue();
    }

    public WorkflowInstanceSimulationState getSimulationState()
    {
        return this.simulatorState;
    }

    /**
     * Run the simulation. This means to trigger the processing of all added events.
     */
    public void run()
    {
        logger.info("Running simulation with (" + getSimulationState().getProcessModelVariant().getFrequency() + ") instances with increments of: (" + getSimulationState().getProcessModelVariant().getCreationIntervalDuration() + ")");
        setGenerator(new WorkflowInstanceGenerator(this, getSimulationState().getProcessModelVariant().getFrequency(), 
                getSimulationState().getProcessModelVariant().getCreationIntervalDuration()));
        logger.info("Starting Simulation for Process: (" + getSimulationState().getProcessModelVariant().getId() + ") and will run (" + getGenerator().getNumberOfSumulatorInstances() + ") instances starting at: (" + getStartTime() + ") with a spaced starting interval of: (" + getGenerator().getStartIncrementInverval() + ")");
        this.doAllEvents();
    }

    public boolean validateEmptyQueues()
    {
        if (events.size() != 0) {
            return false;
        }

        return getSimulationState().validateEmptyQueues();
    }

    public boolean validateCounters()
    {
        return getSimulationState().validateCounters();
    }

    private static final Logger logger = LoggerFactory.getLogger(WorkflowSimulator.class);
}