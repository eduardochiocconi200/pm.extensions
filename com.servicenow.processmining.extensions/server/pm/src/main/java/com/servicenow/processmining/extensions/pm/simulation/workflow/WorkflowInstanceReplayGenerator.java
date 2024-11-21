package com.servicenow.processmining.extensions.pm.simulation.workflow;

import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelVariant;
import com.servicenow.processmining.extensions.pm.simulation.core.SimulationGenerator;
import com.servicenow.processmining.extensions.pm.simulation.core.Simulator;
import com.servicenow.processmining.extensions.pm.simulation.serialization.SysAuditEntryComparator;
import com.servicenow.processmining.extensions.sc.entities.SysAuditEntry;
import com.servicenow.processmining.extensions.sc.entities.SysAuditLog;
import com.servicenow.processmining.extensions.sc.entities.SysAuditLogPK;

import java.sql.Timestamp;
import java.util.TreeSet;

public class WorkflowInstanceReplayGenerator
    extends SimulationGenerator
{
    private Simulator simulator = null;
    private ProcessMiningModelVariant processModelVariant = null;
    private SysAuditLog auditLog = null;
    private SysAuditLog sortedAuditLog = null;
    private int numberOfCreatedSimulatorWorkflowInstances = 0;
    private int lastCreationEventIndex = 0;
    private long firstReplayTimestamp = 0;

    public WorkflowInstanceReplayGenerator(final Simulator simulator, final ProcessMiningModelVariant processModelVariant, final SysAuditLog auditLog)
    {
        this.simulator = simulator;
        this.processModelVariant = processModelVariant;
        this.auditLog = auditLog;
        this.numberOfCreatedSimulatorWorkflowInstances = 0;
    }

    public Simulator getSimulator()
    {
        return this.simulator;
    }

    public ProcessMiningModelVariant getProcessModelVariant()
    {
        return this.processModelVariant;
    }

    public SysAuditLog getAuditLog()
    {
        return this.auditLog;
    }

    private SysAuditLog getSortedAuditLog()
    {
        if (sortedAuditLog == null) {
            TreeSet<SysAuditEntry> cronologicallySortedAuditEvents = new TreeSet<SysAuditEntry>(new SysAuditEntryComparator());
            for (SysAuditEntry entry : getAuditLog().getLog()) {
                cronologicallySortedAuditEvents.add(entry);
            }

            sortedAuditLog = new SysAuditLog((SysAuditLogPK) auditLog.getPK());
            for (SysAuditEntry sae: cronologicallySortedAuditEvents) {
                sortedAuditLog.getLog().add(sae);
            }

            // DEBUG
            /*
            for (SysAuditEntry sae: sortedAuditLog.getLog()) {
                System.out.println(sae.getSysCreatedOn() + "," + sae.getDocumentKey() + "," + sae.getFieldName() + "," + sae.getOldValue() + "," + sae.getNewValue());
            }
            */
        }

        return sortedAuditLog;
    }

    @Override
    public boolean createSimulationInstances()
    {
        int nextCreationEventIndex = getNextCreationEventIndex();
        if (nextCreationEventIndex >= 0) {
            dispatchStrategy(nextCreationEventIndex);
        }

        return true;
    }

    private int getNextCreationEventIndex()
    {
        for (int i=lastCreationEventIndex; i < getSortedAuditLog().getLog().size(); i++) {
            // If it is a creation event
            SysAuditEntry sae = getSortedAuditLog().getLog().get(i);
            if (sae.getFieldName().equals("opened_at")) {
                return i;
            }
        }

        return -1;
    }

    private void dispatchStrategy(final int nextCreationEventIndex)
    {
        SysAuditEntry nextCreationEventEntry = getSortedAuditLog().getLog().get(nextCreationEventIndex);
        WorkflowInstance newInstance = new WorkflowInstance(String.valueOf(numberOfCreatedSimulatorWorkflowInstances + 1), getSimulator());
        double newInstanceCreationTime = getAuditEntryTimeStamp(nextCreationEventEntry);
        newInstance.create(newInstanceCreationTime);
        getSimulator().getStatistics().incrementCreatedInstances();
        lastCreationEventIndex = nextCreationEventIndex+1;
    }

    private double getAuditEntryTimeStamp(final SysAuditEntry entry)
    {
        Timestamp ts = Timestamp.valueOf(entry.getSysCreatedOn());
        if (firstReplayTimestamp == 0) {
            firstReplayTimestamp = ts.getTime();
        }
        return ts.getTime() - firstReplayTimestamp;
    }

    @Override
    public int getNumberOfSumulatorInstances()
    {
        throw new UnsupportedOperationException("Unimplemented method 'getNumberOfSumulatorInstances'. This method should not be called with WorkflowInstanceReplayGenerator.");
    }

    @Override
    public int getNumberOfStartedSumulatorInstances()
    {
        throw new UnsupportedOperationException("Unimplemented method 'getNumberOfStartedSumulatorInstances'. This method should not be called with WorkflowInstanceReplayGenerator.");
    }

    @Override
    public double getStartIncrementInverval()
    {
        throw new UnsupportedOperationException("Unimplemented method 'getStartIncrementInverval'. This method should not be called with WorkflowInstanceReplayGenerator.");
    }

    @Override
    public boolean createSimulationInstances(double nextDispatchTime)
    {
        int nextCreationEventIndex = getNextCreationEventIndex();
        if (nextCreationEventIndex >= 0) {
            dispatchStrategy(nextCreationEventIndex);
        }

        return true;
    }
}