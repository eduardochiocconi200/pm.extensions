package com.servicenow.processmining.extensions.pm.simulation.core;

public abstract class SimulationGenerator {
    public abstract int getNumberOfSumulatorInstances();

    public abstract int getNumberOfStartedSumulatorInstances();

    public abstract double getStartIncrementInverval();

    public abstract boolean createSimulationInstances();

    public abstract boolean createSimulationInstances(final double nextDispatchTime);
}