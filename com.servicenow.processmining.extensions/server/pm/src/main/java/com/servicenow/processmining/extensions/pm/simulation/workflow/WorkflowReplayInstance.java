package com.servicenow.processmining.extensions.pm.simulation.workflow;

import com.servicenow.processmining.extensions.pm.simulation.core.*;
import com.servicenow.processmining.extensions.sc.entities.SysAuditEntry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkflowReplayInstance
    extends WorkflowInstance
{
    private int currentAuditEntryIndex = 0;

    public WorkflowReplayInstance(final String id, final int startAuditEntryIndex, final Simulator simulator)
    {
        super(id, simulator);
        currentAuditEntryIndex = startAuditEntryIndex;
    }

    public void create(final double startOffset)
    {
        String fromNode = "";
        String toNode = null;
        WorkflowInstanceReplayGenerator generator = ((WorkflowInstanceReplayGenerator) getSimulator().getGenerator());
        for (int i=currentAuditEntryIndex; i < generator.getSortedAuditLog().getLog().size(); i++) {
            SysAuditEntry sae = generator.getSortedAuditLog().getLog().get(i);
            if (sae.getFieldName().equals(getSimulator().getFieldName())) {
                toNode = getSimulator().getSimulationState().getProcessModel().getNodeKeyByValue(sae.getNewValue());
                break;
            }
            currentAuditEntryIndex++;
        }
        Message newEvent = new Message(this, getId(), fromNode, toNode,
                startOffset, Message.REGULAR);
        getSimulator().insert(newEvent);
    }

    public String getNextNode()
    {
        String nextNode = "";
        WorkflowInstanceReplayGenerator generator = ((WorkflowInstanceReplayGenerator) getSimulator().getGenerator());
        for (int i=currentAuditEntryIndex+1; i < generator.getSortedAuditLog().getLog().size(); i++) {
            SysAuditEntry sae = generator.getSortedAuditLog().getLog().get(i);
            if (sae.getFieldName().equals(getSimulator().getFieldName())) {
                nextNode = getSimulator().getSimulationState().getProcessModel().getNodeKeyByValue(sae.getNewValue());
                break;
            }
            currentAuditEntryIndex++;
        }

        return nextNode;
    }

    private static final Logger logger = LoggerFactory.getLogger(WorkflowReplayInstance.class);
}