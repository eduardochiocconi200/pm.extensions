package com.servicenow.processmining.extensions.pm.simulation.workflow;

import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelNode;
import com.servicenow.processmining.extensions.pm.simulation.core.SimulationGenerator;
import com.servicenow.processmining.extensions.pm.simulation.core.Simulator;
import com.servicenow.processmining.extensions.pm.simulation.serialization.SysAuditEntryComparator;

import com.servicenow.processmining.extensions.sc.entities.SysAuditEntry;
import com.servicenow.processmining.extensions.sc.entities.SysAuditEntryPK;
import com.servicenow.processmining.extensions.sc.entities.SysAuditLog;
import com.servicenow.processmining.extensions.sc.entities.SysAuditLogPK;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkflowInstanceReplayGenerator
    extends SimulationGenerator
{
    private Simulator simulator = null;
    private SysAuditLog auditLog = null;
    private SysAuditLog sortedAuditLog = null;
    private int lastCreationEventIndex = 0;
    private long firstReplayTimestamp = 0;
    private HashMap<String, String> invalidDocumentKeys = null;

    public WorkflowInstanceReplayGenerator(final Simulator simulator, final SysAuditLog auditLog)
    {
        this.simulator = simulator;
        this.auditLog = auditLog;
    }

    public Simulator getSimulator()
    {
        return this.simulator;
    }

    public SysAuditLog getAuditLog()
    {
        return this.auditLog;
    }

    public SysAuditLog getSortedAuditLog()
    {
        if (sortedAuditLog == null) {
            identifyInvalidAuditLogs();
            logger.debug("Loading and sorting all events chronologically ...");
            TreeSet<SysAuditEntry> cronologicallySortedAuditEvents = new TreeSet<SysAuditEntry>(new SysAuditEntryComparator());
            HashMap<String, String> createdRecords = new HashMap<String, String>();
            for (SysAuditEntry entry : getAuditLog().getLog()) {
                if (invalidDocumentKeys.get(entry.getDocumentKey()) != null) {
                    continue;
                }
                if (createdRecords.get(entry.getDocumentKey()) == null) {
                    createdRecords.put(entry.getDocumentKey(), entry.getDocumentKey());
                    SysAuditEntry createdEntry = new SysAuditEntry(new SysAuditEntryPK(entry.getDocumentKey()));
                    createdEntry.setDocumentKey(entry.getDocumentKey());
                    createdEntry.setTableName(entry.getTableName());
                    createdEntry.setFieldName(CREATED_FIELD_ATTRIBUTE_NAME);
                    createdEntry.setReason(entry.getReason());
                    createdEntry.setSysCreatedOn(entry.getSysCreatedOn());
                    createdEntry.setSysCreatedBy(entry.getSysCreatedBy());
                    createdEntry.setNewValue(entry.getSysCreatedOn());
                    cronologicallySortedAuditEvents.add(createdEntry);
                }
                cronologicallySortedAuditEvents.add(entry);
            }

            sortedAuditLog = new SysAuditLog((SysAuditLogPK) auditLog.getPK());
            for (SysAuditEntry sae: cronologicallySortedAuditEvents) {
                sortedAuditLog.getLog().add(sae);
            }

            if (logger.isTraceEnabled()) {
                for (SysAuditEntry sae: sortedAuditLog.getLog()) {
                    logger.trace(sae.getSysCreatedOn() + "," + sae.getDocumentKey() + "," + sae.getFieldName() + "," + sae.getOldValue() + "," + sae.getNewValue());
                }
            }

            if ((getAuditLog().getLog().size() + createdRecords.size()) < sortedAuditLog.getLog().size()) {
                throw new RuntimeException("Sorted Audit Logs should have SAME OR SMALLER size. Log size is: (" + getAuditLog().getLog().size() + "). Sorted Log size is: (" + this.sortedAuditLog.getLog().size() + ")");
            }
        }

        return sortedAuditLog;
    }

    private void identifyInvalidAuditLogs()
    {
        if (invalidDocumentKeys == null) {
            invalidDocumentKeys = new HashMap<String, String>();
            HashMap<String, String> nodeValues = getNodeValues();
            for (SysAuditEntry entry : getAuditLog().getLog()) {
                if (nodeValues.get(entry.getNewValue()) == null) {
                    if (invalidDocumentKeys.get(entry.getDocumentKey()) == null) {
                        invalidDocumentKeys.put(entry.getDocumentKey(), entry.getDocumentKey());
                    }
                }
                else if (nodeValues.get(entry.getOldValue()) == null) {
                    if (invalidDocumentKeys.get(entry.getDocumentKey()) == null) {
                        invalidDocumentKeys.put(entry.getDocumentKey(), entry.getDocumentKey());
                    }
                }
            }
        }
    }

    private HashMap<String, String> getNodeValues()
    {
        WorkflowSimulator wis = (WorkflowSimulator) getSimulator();
        HashMap<String, String> nodeValues = new HashMap<String, String>();
        for (ProcessMiningModelNode node : wis.getSimulationState().getProcessModel().getNodes().values()) {
            nodeValues.put(node.getValue(), node.getValue());
        }

        return nodeValues;
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
        if (lastCreationEventIndex >= 0) {
            for (int i=lastCreationEventIndex; i < getSortedAuditLog().getLog().size(); i++) {
                // If it is a creation event ...
                SysAuditEntry sae = getSortedAuditLog().getLog().get(i);
                if (sae.getFieldName().equals(CREATED_FIELD_ATTRIBUTE_NAME)) {
                    return i;
                }
            }
        }

        lastCreationEventIndex = -1;
        return lastCreationEventIndex;
    }

    private void dispatchStrategy(final int nextCreationEventIndex)
    {
        SysAuditEntry nextCreationEventEntry = getSortedAuditLog().getLog().get(nextCreationEventIndex);
        WorkflowInstance newInstance = new WorkflowReplayInstance(nextCreationEventEntry.getDocumentKey(), nextCreationEventIndex, getSimulator());
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

    private final static String CREATED_FIELD_ATTRIBUTE_NAME = "created";
    private static final Logger logger = LoggerFactory.getLogger(WorkflowInstanceReplayGenerator.class);
}