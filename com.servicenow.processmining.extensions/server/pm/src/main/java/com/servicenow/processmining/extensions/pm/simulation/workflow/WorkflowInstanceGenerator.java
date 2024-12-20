package com.servicenow.processmining.extensions.pm.simulation.workflow;

import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelVariant;
import com.servicenow.processmining.extensions.pm.simulation.core.SimulationGenerator;
import com.servicenow.processmining.extensions.pm.simulation.core.Simulator;

public class WorkflowInstanceGenerator
    extends SimulationGenerator
{
    private Simulator simulator = null;
    private ProcessMiningModelVariant variant = null;
    private int numberOfSimulatorWorkflowInstances = 0;
    private int numberOfCreatedSimulatorWorkflowInstances = 0;
    private double startIncrementInterval = 0.0;

    // Strategy 2 variables
    private double nextSimulationCreationTime = 0.0;

    public WorkflowInstanceGenerator(final Simulator simulator, final ProcessMiningModelVariant variant)
    {
        this.simulator = simulator;
        this.variant = variant;
        this.numberOfSimulatorWorkflowInstances = variant.getFrequency();
        this.startIncrementInterval = variant.getCreationIntervalDuration();
    }

    public Simulator getSimulator()
    {
        return this.simulator;
    }

    public ProcessMiningModelVariant getProcessMiningModelVariant()
    {
        return this.variant;
    }

    public int getNumberOfSumulatorInstances()
    {
        return this.numberOfSimulatorWorkflowInstances;
    }

    public int getNumberOfStartedSumulatorInstances()
    {
        return this.numberOfCreatedSimulatorWorkflowInstances;
    }

    public double getStartIncrementInverval()
    {
        return this.startIncrementInterval;
    }

    public boolean createSimulationInstances()
    {
        return createSimulationInstances(0);
    }

    public boolean createSimulationInstances(final double nextDispatchTime)
    {
        if (getNumberOfStartedSumulatorInstances() < getNumberOfSumulatorInstances()) {
            // dispatchStrategy1();
            // dispatchStrategy2();
            dispatchStrategy3(nextDispatchTime);
        }

        return true;
    }

    @SuppressWarnings("unused")
    private void dispatchStrategy1()
    {
        double startOffset = getSimulator().now();
        for (int i = 0; i < getNumberOfSumulatorInstances(); i++) {
            WorkflowInstance newInstance = new WorkflowVariationInstance(String.valueOf(i + 1), getSimulator(), getProcessMiningModelVariant());
            newInstance.create(startOffset);
            getSimulator().getStatistics().incrementCreatedInstances();
            startOffset += getStartIncrementInverval();
            numberOfCreatedSimulatorWorkflowInstances++;
        }
    }

    @SuppressWarnings("unused")
    private void dispatchStrategy2()
    {
        if (numberOfCreatedSimulatorWorkflowInstances == 0) {
            nextSimulationCreationTime = getSimulator().now();
        }
        int numberOfInstancesToCreate = 1;
        for (int i = 0; i < numberOfInstancesToCreate; i++) {
            WorkflowInstance newInstance = new WorkflowVariationInstance(String.valueOf(numberOfCreatedSimulatorWorkflowInstances + 1), getSimulator(), getProcessMiningModelVariant());
            newInstance.create(nextSimulationCreationTime);
            getSimulator().getStatistics().incrementCreatedInstances();
            nextSimulationCreationTime += getStartIncrementInverval();
            numberOfCreatedSimulatorWorkflowInstances++;
        }
    }

    private void dispatchStrategy3(final double nextDispatchTime)
    {
        if (numberOfCreatedSimulatorWorkflowInstances == 0) {
            nextSimulationCreationTime = getSimulator().now();
        }

        int maxBatchCreationSize = 2;
        int batchCreatedInstances = 0;
        while (nextSimulationCreationTime <= nextDispatchTime && batchCreatedInstances < maxBatchCreationSize && getNumberOfStartedSumulatorInstances() < getNumberOfSumulatorInstances()) {
            WorkflowInstance newInstance = new WorkflowVariationInstance(String.valueOf(numberOfCreatedSimulatorWorkflowInstances + 1), getSimulator(), getProcessMiningModelVariant());
            newInstance.create(nextSimulationCreationTime);
            getSimulator().getStatistics().incrementCreatedInstances();
            nextSimulationCreationTime += getStartIncrementInverval();
            numberOfCreatedSimulatorWorkflowInstances++;
            batchCreatedInstances++;
        }
    }
}