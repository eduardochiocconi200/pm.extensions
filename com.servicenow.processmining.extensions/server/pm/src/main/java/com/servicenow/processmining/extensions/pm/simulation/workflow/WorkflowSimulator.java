package com.servicenow.processmining.extensions.pm.simulation.workflow;

import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelVariant;
import com.servicenow.processmining.extensions.pm.simulation.core.ListQueue;
import com.servicenow.processmining.extensions.pm.simulation.core.SimulationGenerator;
import com.servicenow.processmining.extensions.pm.simulation.core.Simulator;
import com.servicenow.processmining.extensions.sc.entities.SysAuditLog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkflowSimulator
    extends Simulator
{
    private WorkflowInstanceSimulationState simulatorState = null;
    private int generationType = NO_GENERATION;
    private SimulationGenerator generator = null;
    private SysAuditLog sysAuditLog = null;

    public WorkflowSimulator(final ProcessMiningModelVariant processModelVariant, final int generationType)
    {
        this.simulatorState = new WorkflowInstanceSimulationState(processModelVariant, this);
        this.generationType = generationType;
        this.events = new ListQueue();
    }

    public WorkflowSimulator(final ProcessMiningModelVariant processModelVariant, final SysAuditLog log, final int generationType)
    {
        this.simulatorState = new WorkflowInstanceSimulationState(processModelVariant, this);
        this.sysAuditLog = log;
        this.generationType = generationType;
        this.events = new ListQueue();
    }


    public WorkflowInstanceSimulationState getSimulationState()
    {
        return this.simulatorState;
    }

    public int getGenerationType()
    {
        return this.generationType;
    }

    /**
     * Run the simulation. This means to trigger the processing of all added events.
     */
    public void run()
    {
        logger.info("Running simulation with (" + getSimulationState().getProcessModelVariant().getFrequency() + ") instances with increments of: (" + getSimulationState().getProcessModelVariant().getCreationIntervalDuration() + ")");
        setGenerator(getGenerator());
        switch (generationType) {
            case WorkflowSimulator.UNIFORM_INCREMENTAL_GENERATION:
                logger.info("Starting Simulation for Process: (" + getSimulationState().getProcessModelVariant().getId() + ") and will run (" + getGenerator().getNumberOfSumulatorInstances() + ") instances starting at: (" + getStartTime() + ") with a spaced starting interval of: (" + getGenerator().getStartIncrementInverval() + ")");
                break;
            case WorkflowSimulator.REPLAY_GENERATION:
                logger.info("Starting Replay Simulation for Process: (" + getSimulationState().getProcessModelVariant().getId() + ")");
                break;
            default:
                throw new RuntimeException("Invalid simulator generation type: (" + generationType + ")");
        }

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

    public SimulationGenerator getGenerator()
    {
        if (generator == null) {
            switch (generationType) {
                case WorkflowSimulator.UNIFORM_INCREMENTAL_GENERATION:
                    generator = new WorkflowInstanceGenerator(this, getSimulationState().getProcessModelVariant().getFrequency(),
                        getSimulationState().getProcessModelVariant().getCreationIntervalDuration());
                    break;
                case WorkflowSimulator.REPLAY_GENERATION:
                    generator = new WorkflowInstanceReplayGenerator(this, getSimulationState().getProcessModelVariant(), sysAuditLog);
                    break;
                default:
                    throw new RuntimeException("Invalid Simulation Generation Type: (" + generationType + ")");
            }
        }

        return generator;
    }

    public static final int NO_GENERATION = 0;
    public static final int UNIFORM_INCREMENTAL_GENERATION = 1;
    public static final int REPLAY_GENERATION = 2;

    private static final Logger logger = LoggerFactory.getLogger(WorkflowSimulator.class);
}
