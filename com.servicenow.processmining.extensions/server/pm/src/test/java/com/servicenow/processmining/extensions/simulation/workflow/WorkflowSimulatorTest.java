package com.servicenow.processmining.extensions.simulation.workflow;

import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelVariant;
import com.servicenow.processmining.extensions.pm.simulation.workflow.PrintSimulationState;
import com.servicenow.processmining.extensions.pm.simulation.workflow.WorkflowInstance;
import com.servicenow.processmining.extensions.pm.simulation.workflow.WorkflowSimulator;

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
    ProcessMiningModelVariant sample1 = new WorkflowSimulationSamplesTest1("2").getSample();
    WorkflowSimulator sim = new WorkflowSimulator(sample1, WorkflowSimulator.UNIFORM_INCREMENTAL_GENERATION);
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
    ProcessMiningModelVariant sample1 = new WorkflowSimulationSamplesTest1("2").getSample();
    WorkflowSimulator sim = new WorkflowSimulator(sample1, WorkflowSimulator.UNIFORM_INCREMENTAL_GENERATION);
    Assert.assertTrue(sim.validateEmptyQueues());
    sim.run();
    Assert.assertTrue(sim.validateEmptyQueues());
    Assert.assertEquals(2, sim.getStatistics().getNumberOfCreatedInstances());
    Assert.assertEquals(600.0, sim.getStatistics().getTotalSimulationTime(), 0.0);
    long endTime = System.currentTimeMillis();
    logger.info("Completing Test Case in (" + (endTime - startTime) + ") ...");

    startTime = System.currentTimeMillis();
    sample1 = new WorkflowSimulationSamplesTest1("100").getSample();
    sim = new WorkflowSimulator(sample1, WorkflowSimulator.UNIFORM_INCREMENTAL_GENERATION);
    Assert.assertTrue(sim.validateEmptyQueues());
    sim.run();
    Assert.assertTrue(sim.validateEmptyQueues());
    Assert.assertEquals(100, sim.getStatistics().getNumberOfCreatedInstances());
    Assert.assertEquals(30000.0, sim.getStatistics().getTotalSimulationTime(), 0.0);
    endTime = System.currentTimeMillis();
    logger.info("Completing Test Case in (" + (endTime - startTime) + ") ...");

    startTime = System.currentTimeMillis();
    sample1 = new WorkflowSimulationSamplesTest1("1000").getSample();
    sim = new WorkflowSimulator(sample1, WorkflowSimulator.UNIFORM_INCREMENTAL_GENERATION);
    Assert.assertTrue(sim.validateEmptyQueues());
    sim.run();
    Assert.assertTrue(sim.validateEmptyQueues());
    Assert.assertEquals(1000, sim.getStatistics().getNumberOfCreatedInstances());
    Assert.assertEquals(300000.0, sim.getStatistics().getTotalSimulationTime(), 0.0);
    endTime = System.currentTimeMillis();
    logger.info("Completing Test Case in (" + (endTime - startTime) + ") ...");

    startTime = System.currentTimeMillis();
    sample1 = new WorkflowSimulationSamplesTest1("10000").getSample();
    sim = new WorkflowSimulator(sample1, WorkflowSimulator.UNIFORM_INCREMENTAL_GENERATION);
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
    ProcessMiningModelVariant sample2 = new WorkflowSimulationSamplesTest2("2").getSample();
    WorkflowSimulator sim = new WorkflowSimulator(sample2, WorkflowSimulator.UNIFORM_INCREMENTAL_GENERATION);
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
    ProcessMiningModelVariant sample3 = new WorkflowSimulationSamplesTest3("10").getSample();
    WorkflowSimulator sim = new WorkflowSimulator(sample3, WorkflowSimulator.UNIFORM_INCREMENTAL_GENERATION);
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
    ProcessMiningModelVariant sample4 = new WorkflowSimulationSamplesTest4("10").getSample();
    WorkflowSimulator sim = new WorkflowSimulator(sample4, WorkflowSimulator.UNIFORM_INCREMENTAL_GENERATION);
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
    ProcessMiningModelVariant sample5 = new WorkflowSimulationSamplesTest5("2").getSample();
    WorkflowSimulator sim = new WorkflowSimulator(sample5, WorkflowSimulator.UNIFORM_INCREMENTAL_GENERATION);
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
    ProcessMiningModelVariant sample6 = new WorkflowSimulationSamplesTest6("3").getSample();
    WorkflowSimulator sim = new WorkflowSimulator(sample6, WorkflowSimulator.UNIFORM_INCREMENTAL_GENERATION);
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
    ProcessMiningModelVariant sample7 = new WorkflowSimulationSamplesTest7("1").getSample();
    WorkflowSimulator sim = new WorkflowSimulator(sample7, WorkflowSimulator.UNIFORM_INCREMENTAL_GENERATION);
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
    ProcessMiningModelVariant sample8 = new WorkflowSimulationSamplesTest8("3").getSample();
    WorkflowSimulator sim = new WorkflowSimulator(sample8, WorkflowSimulator.UNIFORM_INCREMENTAL_GENERATION);
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
    ProcessMiningModelVariant sample9 = new WorkflowSimulationSamplesTest9("3").getSample();
    WorkflowSimulator sim = new WorkflowSimulator(sample9, WorkflowSimulator.UNIFORM_INCREMENTAL_GENERATION);
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
    ProcessMiningModelVariant sample10 = new WorkflowSimulationSamplesTest10("1").getSample();
    WorkflowSimulator sim = new WorkflowSimulator(sample10, WorkflowSimulator.UNIFORM_INCREMENTAL_GENERATION);
    Assert.assertTrue(sim.validateEmptyQueues());
    sim.run();
    Assert.assertTrue(sim.validateEmptyQueues());
    Assert.assertEquals(1, sim.getStatistics().getNumberOfCreatedInstances());
    Assert.assertEquals(49200628.0, sim.getStatistics().getTotalSimulationTime(), 0.0);

    sample10 = new WorkflowSimulationSamplesTest10("2").getSample();
    sim = new WorkflowSimulator(sample10, WorkflowSimulator.UNIFORM_INCREMENTAL_GENERATION);
    Assert.assertTrue(sim.validateEmptyQueues());
    sim.run();
    Assert.assertTrue(sim.validateEmptyQueues());
    Assert.assertEquals(2, sim.getStatistics().getNumberOfCreatedInstances());
    Assert.assertEquals(5.0666096E7, sim.getStatistics().getTotalSimulationTime(), 0.0);
    logger.info("Completed Simulation Test10 ...");
  }

  private void test11()
  {
    logger.info("Starting Simulation Test11 ...");
    ProcessMiningModelVariant sample11 = new WorkflowSimulationSamplesTest11("398").getSample();
    WorkflowSimulator sim = new WorkflowSimulator(sample11, WorkflowSimulator.UNIFORM_INCREMENTAL_GENERATION);
    Assert.assertTrue(sim.validateEmptyQueues());
    sim.run();
    Assert.assertTrue(sim.validateEmptyQueues());
    Assert.assertEquals(398, sim.getStatistics().getNumberOfCreatedInstances());
    Assert.assertEquals(3.5568779512E10, sim.getStatistics().getTotalSimulationTime(), 0.0);
    logger.info("Completed Simulation Test12 ...");
  }

  private static final Logger logger = LoggerFactory.getLogger(WorkflowSimulatorTest.class);
}