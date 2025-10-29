package com.servicenow.processmining.extensions.pm.simulation.workflow;

import com.servicenow.processmining.extensions.pm.simulation.roi.WorkflowSimulationComparison;

import org.junit.Assert;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkflowSimulatorTest
{
  @Test
  public void test()
  {
    logger.info("Starting Simulation Tests ...");
    test1();
    test2();
    test3();
    test4();
    test5();
    test6();
    test7();
    test8();
    test9();
    test10();
    test11();
    test1Volume();
  }

  private void test1()
  {
    logger.info("Starting Simulation Test1 ...");
    long testCaseStartTime = System.currentTimeMillis();
    long startTime = System.currentTimeMillis();
    WorkflowInstance.setDisplay(new PrintSimulationState());
    WorkflowSimulationSamples sample1 = new WorkflowSimulationSamplesTest1("2");
    WorkflowSimulator sim = new WorkflowSimulator(sample1.getModel(), sample1.getVariation(), sample1.getTableName(), sample1.getFieldName());
    Assert.assertTrue(sim.validateEmptyQueues());
    sim.run();
    Assert.assertTrue(sim.validateEmptyQueues());
    Assert.assertEquals(2, sim.getStatistics().getNumberOfCreatedInstances());
    Assert.assertEquals(600.0, sim.getStatistics().getTotalSimulationTime(), 0.0);
    long endTime = System.currentTimeMillis();
    logger.info("Completing Test Case in (" + (endTime - startTime) + ") ...");

    long testCaseEndTime = System.currentTimeMillis();
    logger.info("Completing Simulation Test1 in (" + (testCaseEndTime - testCaseStartTime) + ") ...");
  }

  private void test1Volume()
  {
    logger.info("Starting Simulation Test1 (Volume) ...");
    long testCaseStartTime = System.currentTimeMillis();
    long startTime = System.currentTimeMillis();
    WorkflowInstance.setDisplay(new PrintSimulationState());
    WorkflowSimulationSamples sample1 = new WorkflowSimulationSamplesTest1("2");
    WorkflowSimulator sim = new WorkflowSimulator(sample1.getModel(), sample1.getVariation(), sample1.getTableName(), sample1.getFieldName());    
    Assert.assertTrue(sim.validateEmptyQueues());
    sim.run();
    Assert.assertTrue(sim.validateEmptyQueues());
    Assert.assertEquals(2, sim.getStatistics().getNumberOfCreatedInstances());
    Assert.assertEquals(600.0, sim.getStatistics().getTotalSimulationTime(), 0.0);
    long endTime = System.currentTimeMillis();
    logger.info("Completing Test Case in (" + (endTime - startTime) + ") ...");

    startTime = System.currentTimeMillis();
    sample1 = new WorkflowSimulationSamplesTest1("100");
    WorkflowSimulator sim2 = new WorkflowSimulator(sample1.getModel(), sample1.getVariation(), sample1.getTableName(), sample1.getFieldName());
    Assert.assertTrue(sim2.validateEmptyQueues());
    sim2.run();
    Assert.assertTrue(sim2.validateEmptyQueues());
    Assert.assertEquals(100, sim2.getStatistics().getNumberOfCreatedInstances());
    Assert.assertEquals(30000.0, sim2.getStatistics().getTotalSimulationTime(), 0.0);
    endTime = System.currentTimeMillis();
    logger.info("Completing Test Case in (" + (endTime - startTime) + ") ...");
    WorkflowSimulationComparison comparison = new WorkflowSimulationComparison(sim, sim2);
    Assert.assertTrue(comparison.compare());

    startTime = System.currentTimeMillis();
    sample1 = new WorkflowSimulationSamplesTest1("1000");
    sim = new WorkflowSimulator(sample1.getModel(), sample1.getVariation(), sample1.getTableName(), sample1.getFieldName());
    Assert.assertTrue(sim.validateEmptyQueues());
    sim.run();
    Assert.assertTrue(sim.validateEmptyQueues());
    Assert.assertEquals(1000, sim.getStatistics().getNumberOfCreatedInstances());
    Assert.assertEquals(300000.0, sim.getStatistics().getTotalSimulationTime(), 0.0);
    endTime = System.currentTimeMillis();
    logger.info("Completing Test Case in (" + (endTime - startTime) + ") ...");

    startTime = System.currentTimeMillis();
    sample1 = new WorkflowSimulationSamplesTest1("10000");
    sim = new WorkflowSimulator(sample1.getModel(), sample1.getVariation(), sample1.getTableName(), sample1.getFieldName());
    Assert.assertTrue(sim.validateEmptyQueues());
    sim.run();
    Assert.assertTrue(sim.validateEmptyQueues());
    Assert.assertEquals(10000, sim.getStatistics().getNumberOfCreatedInstances());
    Assert.assertEquals(3000000.0, sim.getStatistics().getTotalSimulationTime(), 0.0);
    endTime = System.currentTimeMillis();
    logger.info("Completing Test Case in (" + (endTime - startTime) + ") ...");

    long testCaseEndTime = System.currentTimeMillis();
    logger.info("Completing Simulation Test1 (Volume) in (" + (testCaseEndTime - testCaseStartTime) + ") ...");
  }

  private void test2()
  {
    logger.info("Starting Simulation Test2 ...");
    WorkflowSimulationSamples sample2 = new WorkflowSimulationSamplesTest2("2");
    WorkflowSimulator sim = new WorkflowSimulator(sample2.getModel(), sample2.getVariation(), sample2.getTableName(), sample2.getFieldName());
    Assert.assertTrue(sim.validateEmptyQueues());
    sim.run();
    Assert.assertTrue(sim.validateEmptyQueues());
    Assert.assertEquals(2, sim.getStatistics().getNumberOfCreatedInstances());
    Assert.assertEquals(451.0, sim.getStatistics().getTotalSimulationTime(), 0.0);
    logger.info("Completing Simulation Test2 ...");
  }

  
  private void test3()
  {
    logger.info("Starting Simulation Test3 ...");
    WorkflowSimulationSamples sample3 = new WorkflowSimulationSamplesTest3("10");
    WorkflowSimulator sim = new WorkflowSimulator(sample3.getModel(), sample3.getVariation(), sample3.getTableName(), sample3.getFieldName());
    Assert.assertTrue(sim.validateEmptyQueues());
    sim.run();
    Assert.assertTrue(sim.validateEmptyQueues());
    Assert.assertEquals(10, sim.getStatistics().getNumberOfCreatedInstances());
    Assert.assertEquals(1740.0, sim.getStatistics().getTotalSimulationTime(), 0.0);
    logger.info("Completing Simulation Test3 ...");
  }
    
  private void test4()
  {
    logger.info("Starting Simulation Test4 ...");
    WorkflowSimulationSamples sample4 = new WorkflowSimulationSamplesTest4("10");
    WorkflowSimulator sim = new WorkflowSimulator(sample4.getModel(), sample4.getVariation(), sample4.getTableName(), sample4.getFieldName());
    Assert.assertTrue(sim.validateEmptyQueues());
    sim.run();
    Assert.assertTrue(sim.validateEmptyQueues());
    Assert.assertEquals(10, sim.getStatistics().getNumberOfCreatedInstances());
    Assert.assertEquals(1690.0, sim.getStatistics().getTotalSimulationTime(), 0.0);
    logger.info("Completing Simulation Test4 ...");
  }

  private void test5()
  {
    logger.info("Starting Simulation Test5 ...");
    WorkflowSimulationSamples sample5 = new WorkflowSimulationSamplesTest5("2");
    WorkflowSimulator sim = new WorkflowSimulator(sample5.getModel(), sample5.getVariation(), sample5.getTableName(), sample5.getFieldName());
    Assert.assertTrue(sim.validateEmptyQueues());
    sim.run();
    Assert.assertTrue(sim.validateEmptyQueues());
    Assert.assertEquals(2, sim.getStatistics().getNumberOfCreatedInstances());
    Assert.assertEquals(1354.0, sim.getStatistics().getTotalSimulationTime(), 0.0);
    logger.info("Completing Simulation Test5 ...");
  }

  private void test6()
  {
    logger.info("Starting Simulation Test6 ...");
    WorkflowSimulationSamples sample6 = new WorkflowSimulationSamplesTest6("3");
    WorkflowSimulator sim = new WorkflowSimulator(sample6.getModel(), sample6.getVariation(), sample6.getTableName(), sample6.getFieldName());
    Assert.assertTrue(sim.validateEmptyQueues());
    sim.run();
    Assert.assertTrue(sim.validateEmptyQueues());
    Assert.assertEquals(3, sim.getStatistics().getNumberOfCreatedInstances());
    Assert.assertEquals(2566.0, sim.getStatistics().getTotalSimulationTime(), 0.0);
    logger.info("Completing Simulation Test6 ...");
  }

  private void test7()
  {
    logger.info("Starting Simulation Test7 ...");
    WorkflowSimulationSamples sample7 = new WorkflowSimulationSamplesTest7("1");
    WorkflowSimulator sim = new WorkflowSimulator(sample7.getModel(), sample7.getVariation(), sample7.getTableName(), sample7.getFieldName());
    Assert.assertTrue(sim.validateEmptyQueues());
    sim.run();
    Assert.assertTrue(sim.validateEmptyQueues());
    Assert.assertEquals(1, sim.getStatistics().getNumberOfCreatedInstances());
    Assert.assertEquals(900.0, sim.getStatistics().getTotalSimulationTime(), 0.0);
    logger.info("Completed Simulation Test7 ...");
  }

  private void test8()
  {
    logger.info("Starting Simulation Test8 ...");
    WorkflowSimulationSamples sample8 = new WorkflowSimulationSamplesTest8("3");
    WorkflowSimulator sim = new WorkflowSimulator(sample8.getModel(), sample8.getVariation(), sample8.getTableName(), sample8.getFieldName());
    Assert.assertTrue(sim.validateEmptyQueues());
    sim.run();
    Assert.assertTrue(sim.validateEmptyQueues());
    Assert.assertEquals(3, sim.getStatistics().getNumberOfCreatedInstances());
    Assert.assertEquals(902.0, sim.getStatistics().getTotalSimulationTime(), 0.0);
    logger.info("Completed Simulation Test8 ...");
  }

  private void test9()
  {
    logger.info("Starting Simulation Test9 ...");
    WorkflowSimulationSamples sample9 = new WorkflowSimulationSamplesTest9("3");
    WorkflowSimulator sim = new WorkflowSimulator(sample9.getModel(), sample9.getVariation(), sample9.getTableName(), sample9.getFieldName());
    Assert.assertTrue(sim.validateEmptyQueues());
    sim.run();
    Assert.assertTrue(sim.validateEmptyQueues());
    Assert.assertEquals(3, sim.getStatistics().getNumberOfCreatedInstances());
    Assert.assertEquals(1806.0, sim.getStatistics().getTotalSimulationTime(), 0.0);
    logger.info("Completed Simulation Test9 ...");
  }

  private void test10()
  {
    logger.info("Starting Simulation Test10 ...");
    WorkflowSimulationSamples sample10 = new WorkflowSimulationSamplesTest10("1");
    WorkflowSimulator sim = new WorkflowSimulator(sample10.getModel(), sample10.getVariation(), sample10.getTableName(), sample10.getFieldName());
    Assert.assertTrue(sim.validateEmptyQueues());
    sim.run();
    Assert.assertTrue(sim.validateEmptyQueues());
    Assert.assertEquals(1, sim.getStatistics().getNumberOfCreatedInstances());
    Assert.assertEquals(33398852.0, sim.getStatistics().getTotalSimulationTime(), 0.0);

    sample10 = new WorkflowSimulationSamplesTest10("2");
    sim = new WorkflowSimulator(sample10.getModel(), sample10.getVariation(), sample10.getTableName(), sample10.getFieldName());
    Assert.assertTrue(sim.validateEmptyQueues());
    sim.run();
    Assert.assertTrue(sim.validateEmptyQueues());
    Assert.assertEquals(2, sim.getStatistics().getNumberOfCreatedInstances());
    Assert.assertEquals(69185181.0, sim.getStatistics().getTotalSimulationTime(), 0.0);
    logger.info("Completed Simulation Test10 ...");
  }

  private void test11()
  {
    logger.info("Starting Simulation Test11 ...");
    WorkflowSimulationSamples sample11 = new WorkflowSimulationSamplesTest11("10");
    WorkflowSimulator sim = new WorkflowSimulator(sample11.getModel(), sample11.getVariation(), sample11.getTableName(), sample11.getFieldName());
    Assert.assertTrue(sim.validateEmptyQueues());
    sim.run();
    Assert.assertTrue(sim.validateEmptyQueues());
    Assert.assertEquals(10, sim.getStatistics().getNumberOfCreatedInstances());
    Assert.assertEquals(442180277, sim.getStatistics().getTotalSimulationTime(), 0.0);
    logger.info("Completed Simulation Test12 ...");
  }

  private static final Logger logger = LoggerFactory.getLogger(WorkflowSimulatorTest.class);
}