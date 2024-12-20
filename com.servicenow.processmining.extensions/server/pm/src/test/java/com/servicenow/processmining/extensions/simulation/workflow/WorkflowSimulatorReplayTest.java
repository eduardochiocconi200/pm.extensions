package com.servicenow.processmining.extensions.simulation.workflow;

import com.servicenow.processmining.extensions.pm.simulation.serialization.AuditLogSerializer;
import com.servicenow.processmining.extensions.pm.simulation.workflow.PrintSimulationState;
import com.servicenow.processmining.extensions.pm.simulation.workflow.WorkflowInstance;
import com.servicenow.processmining.extensions.pm.simulation.workflow.WorkflowSimulator;

import com.servicenow.processmining.extensions.sc.entities.SysAuditEntry;
import com.servicenow.processmining.extensions.sc.entities.SysAuditLog;
import com.servicenow.processmining.extensions.sc.entities.SysAuditLogPK;

import com.servicenow.processmining.extensions.sn.core.TestUtility;

import org.junit.Assert;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkflowSimulatorReplayTest
{
  private AuditLogSerializer serializer = null;

  @Test
  public void test()
  {
    logger.info("Starting Simulation Replay Tests ...");
    boolean runOne = true;
    if (runOne) {
      test2();
    }
    else {
      test1();
      test2();  
    }
  }

  private void test1()
  {
    logger.info("Starting Simulation Replay Test1 ...");
    long testCaseStartTime = System.currentTimeMillis();
    long startTime = System.currentTimeMillis();
    WorkflowInstance.setDisplay(new PrintSimulationState());
    WorkflowSimulationSamples sample1 = new WorkflowSimulationSamplesTest1("2");
    WorkflowSimulator sim = new WorkflowSimulator(sample1.getModel(), getSysAuditLog1(), "incident", "state");
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

  private void test2()
  {
    logger.info("Starting Simulation Replay Test2 ...");
    long testCaseStartTime = System.currentTimeMillis();
    long startTime = System.currentTimeMillis();
    WorkflowInstance.setDisplay(new PrintSimulationState());
    WorkflowSimulationSamples sample12 = new WorkflowSimulationSamplesTest12("2");
    WorkflowSimulator sim = new WorkflowSimulator(sample12.getModel(), getSysAuditLog2(), "incident", "state");
    Assert.assertTrue(sim.validateEmptyQueues());
    sim.run();
    Assert.assertTrue(sim.validateEmptyQueues());
    Assert.assertEquals(1, sim.getStatistics().getNumberOfCreatedInstances());
    // First event: 2024-01-27 02:24:42
    // Last event : 2024-02-19 18:41:44
    Assert.assertEquals(300.0, sim.getStatistics().getTotalSimulationTime(), 0.0);
    long endTime = System.currentTimeMillis();
    logger.info("Completing Test Case in (" + (endTime - startTime) + ") ...");

    long testCaseEndTime = System.currentTimeMillis();
    logger.info("Completing Simulation Replay Test1 in (" + (testCaseEndTime - testCaseStartTime) + ") ...");
  }

  private void loadAuditData()
  {
    if (serializer == null) {
      serializer = new AuditLogSerializer();
      String auditLogJSONString = new TestUtility().loadProcessMiningModel("/simulation/audit-log-1.json");
      Assert.assertTrue(serializer.parse(auditLogJSONString));
    }
  }

  // All records
  private SysAuditLog getSysAuditLog1()
  {
    loadAuditData();
    SysAuditLog stateAuditLog = new SysAuditLog((SysAuditLogPK) serializer.getLog().getPK());
    stateAuditLog.getLog().addAll(serializer.getLog().getFieldNameFilteredLog("state"));

    return stateAuditLog;
  }

  // Load one record: da3a07c7936886506e79bb1e1dba1025
  private SysAuditLog getSysAuditLog2()
  {
    loadAuditData();
    SysAuditLog stateAuditLog = new SysAuditLog((SysAuditLogPK) serializer.getLog().getPK());

    for (SysAuditEntry se : serializer.getLog().getFieldNameFilteredLog("state")) {
      if (se.getDocumentKey().equals("030a0347936886506e79bb1e1dba104a")) {
        stateAuditLog.getLog().add(se);
      }
    }

    Assert.assertEquals(8, stateAuditLog.getLog().size());

    return stateAuditLog;
  }

  // Load 2 records
  private SysAuditLog getSysAuditLog3()
  {
    loadAuditData();
    SysAuditLog stateAuditLog = new SysAuditLog((SysAuditLogPK) serializer.getLog().getPK());

    for (SysAuditEntry se : serializer.getLog().getFieldNameFilteredLog("state")) {
      if (se.getDocumentKey().equals("030a0347936886506e79bb1e1dba104a") ||
          se.getDocumentKey().equals("9d2a0787936886506e79bb1e1dba10c3")) {
        stateAuditLog.getLog().add(se);
      }
    }

    Assert.assertEquals(8, stateAuditLog.getLog().size());

    return stateAuditLog;
  }

  // Load 4 records
  private SysAuditLog getSysAuditLog4()
  {
    loadAuditData();
    SysAuditLog stateAuditLog = new SysAuditLog((SysAuditLogPK) serializer.getLog().getPK());

    for (SysAuditEntry se : serializer.getLog().getFieldNameFilteredLog("state")) {
      if (se.getDocumentKey().equals("030a0347936886506e79bb1e1dba104a") ||
          se.getDocumentKey().equals("9d2a0787936886506e79bb1e1dba10c3") ||
          se.getDocumentKey().equals("e42a8387936886506e79bb1e1dba1013") ||
          se.getDocumentKey().equals("7a2a4b87936886506e79bb1e1dba107c")) {
        stateAuditLog.getLog().add(se);
      }
    }

    Assert.assertEquals(8, stateAuditLog.getLog().size());

    return stateAuditLog;
  }

  private static final Logger logger = LoggerFactory.getLogger(WorkflowSimulatorReplayTest.class);
}