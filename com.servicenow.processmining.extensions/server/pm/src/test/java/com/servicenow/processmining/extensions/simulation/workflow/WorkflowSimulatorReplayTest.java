package com.servicenow.processmining.extensions.simulation.workflow;

import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelVariant;
import com.servicenow.processmining.extensions.pm.simulation.serialization.AuditLogSerializer;
import com.servicenow.processmining.extensions.pm.simulation.workflow.PrintSimulationState;
import com.servicenow.processmining.extensions.pm.simulation.workflow.WorkflowInstance;
import com.servicenow.processmining.extensions.pm.simulation.workflow.WorkflowSimulator;

import com.servicenow.processmining.extensions.sc.entities.SysAuditLog;
import com.servicenow.processmining.extensions.sc.entities.SysAuditLogPK;
import com.servicenow.processmining.extensions.sn.core.TestUtility;

import org.junit.Assert;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkflowSimulatorReplayTest
{
  @Test
  public void test()
  {
    logger.info("Starting Simulation Replay Tests ...");
    test1();
  }

  private void test1()
  {
    logger.info("Starting Simulation Replay Test1 ...");
    long testCaseStartTime = System.currentTimeMillis();
    long startTime = System.currentTimeMillis();
    WorkflowInstance.setDisplay(new PrintSimulationState());
    ProcessMiningModelVariant sample1 = new WorkflowSimulationSamplesTest1("2").getSample();
    WorkflowSimulator sim = new WorkflowSimulator(sample1, getSysAuditLog1(), WorkflowSimulator.REPLAY_GENERATION);
    Assert.assertTrue(sim.validateEmptyQueues());
    sim.run();
    Assert.assertTrue(sim.validateEmptyQueues());
    Assert.assertEquals(167, sim.getStatistics().getNumberOfCreatedInstances());
    Assert.assertEquals(2.4340893E9, sim.getStatistics().getTotalSimulationTime(), 0.0);
    long endTime = System.currentTimeMillis();
    logger.info("Completing Test Case in (" + (endTime - startTime) + ") ...");

    long testCaseEndTime = System.currentTimeMillis();
    logger.info("Completing Simulation Replay Test1 in (" + (testCaseEndTime - testCaseStartTime) + ") ...");
  }

  private SysAuditLog getSysAuditLog1()
  {
      String auditLogJSONString = new TestUtility().loadProcessMiningModel("/simulation/audit-log-1.json");
      AuditLogSerializer serializer = new AuditLogSerializer();
      Assert.assertTrue(serializer.parse(auditLogJSONString));
      SysAuditLog stateAuditLog = new SysAuditLog((SysAuditLogPK) serializer.getLog().getPK());
      stateAuditLog.getLog().addAll(serializer.getLog().getFieldNameFilteredLog("state"));

      return stateAuditLog;
  }

  private static final Logger logger = LoggerFactory.getLogger(WorkflowSimulatorReplayTest.class);
}