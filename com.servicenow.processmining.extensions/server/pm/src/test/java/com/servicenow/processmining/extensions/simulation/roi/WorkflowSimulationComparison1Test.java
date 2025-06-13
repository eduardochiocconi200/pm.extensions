package com.servicenow.processmining.extensions.simulation.roi;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.servicenow.processmining.extensions.pm.simulation.roi.WorkflowSimulationComparison;
import com.servicenow.processmining.extensions.pm.simulation.workflow.PrintSimulationState;
import com.servicenow.processmining.extensions.pm.simulation.workflow.WorkflowInstance;
import com.servicenow.processmining.extensions.pm.simulation.workflow.WorkflowSimulator;
import com.servicenow.processmining.extensions.simulation.workflow.WorkflowSimulationSamples;
import com.servicenow.processmining.extensions.simulation.workflow.WorkflowSimulatorTest;

public class WorkflowSimulationComparison1Test
{
    public WorkflowSimulationComparison1Test()
    {
    }

    @Test
    public void test()
    {
        test1();
    }

    private void test1()
    {
        logger.info("Starting Simulation Test1 (Volume) ...");
        long startTime = System.currentTimeMillis();
        WorkflowInstance.setDisplay(new PrintSimulationState());
        WorkflowSimulationSamples baseline = new WorkflowSimulationComparisonSamplesTest1("10");
        WorkflowSimulator sim = new WorkflowSimulator(baseline.getModel(), baseline.getVariation(), baseline.getTableName(), baseline.getFieldName());
        Assert.assertTrue(sim.validateEmptyQueues());
        sim.run();
        Assert.assertTrue(sim.validateEmptyQueues());
        Assert.assertEquals(10, sim.getStatistics().getNumberOfCreatedInstances());
        Assert.assertEquals(18005160.0, sim.getStatistics().getTotalSimulationTime(), 0.0);
        long endTime = System.currentTimeMillis();
        logger.info("Completing Test Case in (" + (endTime - startTime) + ") ...");

        startTime = System.currentTimeMillis();
        WorkflowSimulationSamples newScenario1 = new WorkflowSimulationComparisonSamplesTest2("2");
        WorkflowSimulator sim2 = new WorkflowSimulator(newScenario1.getModel(), newScenario1.getVariation(), newScenario1.getTableName(), newScenario1.getFieldName());
        Assert.assertTrue(sim2.validateEmptyQueues());
        sim2.run();
        Assert.assertTrue(sim2.validateEmptyQueues());
        Assert.assertEquals(2, sim2.getStatistics().getNumberOfCreatedInstances());
        Assert.assertEquals(949868.0, sim2.getStatistics().getTotalSimulationTime(), 0.0);
        endTime = System.currentTimeMillis();
        logger.info("Completing Test Case in (" + (endTime - startTime) + ") ...");
        WorkflowSimulationComparison comparison = new WorkflowSimulationComparison(sim, sim2);
        Assert.assertTrue(comparison.compare());
    }

    private static final Logger logger = LoggerFactory.getLogger(WorkflowSimulatorTest.class);
}