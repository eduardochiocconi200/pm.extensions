package com.servicenow.processmining.extensions.pm.simulation.workflow;

import com.servicenow.processmining.extensions.pm.model.ProcessMiningModel;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelVariant;
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
    private ProcessMiningModelVariant variant = null;
    private SimulationGenerator generator = null;
    private SysAuditLog sysAuditLog = null;
    private String tableName = null;
    private String fieldName = null;

    public WorkflowSimulator(final ProcessMiningModel model, final ProcessMiningModelVariant processModelVariant, final String tableName, final String fieldName)
    {
        super();
        this.simulatorState = new WorkflowInstanceSimulationState(model, this);
        this.variant = processModelVariant;
        this.generationType = UNIFORM_INCREMENTAL_GENERATION;
        this.tableName = tableName;
        this.fieldName = fieldName;
    }

    public WorkflowSimulator(final ProcessMiningModel model, final SysAuditLog log, final String tableName, final String fieldName)
    {
        super();
        this.simulatorState = new WorkflowInstanceSimulationState(model, this);
        this.sysAuditLog = log;
        this.generationType = REPLAY_GENERATION;
        this.tableName = tableName;
        this.fieldName = fieldName;
    }

    public WorkflowInstanceSimulationState getSimulationState()
    {
        return this.simulatorState;
    }

    public int getGenerationType()
    {
        return this.generationType;
    }

    public String getTableName()
    {
        return this.tableName;
    }

    public String getFieldName()
    {
        return this.fieldName;
    }

    public ProcessMiningModelVariant getProcessModelVariant()
    {
        return this.variant;
    }

    /**
     * Run the simulation. This means to trigger the processing of all added events.
     */
    public void run()
    {
        setGenerator(getGenerator());
        switch (generationType) {
            case WorkflowSimulator.UNIFORM_INCREMENTAL_GENERATION:
            logger.info("Running simulation with (" + getProcessModelVariant().getFrequency() + ") instances with increments of: (" + getProcessModelVariant().getCreationIntervalDuration() + ")");
                logger.info("Starting Simulation for Process: (" + getSimulationState().getProcessModel().getName() + ") and will run (" + getGenerator().getNumberOfSumulatorInstances() + ") instances starting at: (" + getStartTime() + ") with a spaced starting interval of: (" + getGenerator().getStartIncrementInverval() + ")");
                break;
            case WorkflowSimulator.REPLAY_GENERATION:
                logger.info("Starting Replay Simulation for Process: (" + getSimulationState().getProcessModel().getName() + ")");
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
                    generator = new WorkflowInstanceGenerator(this, getProcessModelVariant());
                    break;
                case WorkflowSimulator.REPLAY_GENERATION:
                    generator = new WorkflowInstanceReplayGenerator(this, sysAuditLog);
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
