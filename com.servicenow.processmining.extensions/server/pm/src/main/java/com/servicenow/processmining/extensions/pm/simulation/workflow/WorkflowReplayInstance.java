package com.servicenow.processmining.extensions.pm.simulation.workflow;

import com.servicenow.processmining.extensions.pm.simulation.core.*;
import com.servicenow.processmining.extensions.sc.entities.SysAuditEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkflowReplayInstance
    extends WorkflowInstance
{
    private int firstAuditEntryIndex = -1;
    private int currentAuditEntryIndex = 0;

    public WorkflowReplayInstance(final String id, final int startAuditEntryIndex, final Simulator simulator)
    {
        super(id, simulator);
        currentAuditEntryIndex = startAuditEntryIndex;
    }

    @Override
    public void create(final double startOffset)
    {
        String fromNode = getSimulator().getSimulationState().getProcessModel().getStartingNodes().get(0);
        if (getSimulator().getSimulationState().getProcessModel().getStartingNodes().size() > 1) {
            throw new RuntimeException("There is more than one starting node. We need to improve the logic to determine which is the right starting node.");
        }
        String toNode = null;
        currentAuditEntryIndex = 0;
        WorkflowInstanceReplayGenerator generator = ((WorkflowInstanceReplayGenerator) getSimulator().getGenerator());
        for (int i=currentAuditEntryIndex; i < generator.getSortedAuditLog().getLog().size(); i++) {
            SysAuditEntry sae = generator.getSortedAuditLog().getLog().get(i);
            if (firstAuditEntryIndex == -1 && sae.getDocumentKey().equals(this.getId())) {
                this.firstAuditEntryIndex = i;
            }
            if (sae.getFieldName().equals(getSimulator().getFieldName()) && sae.getDocumentKey().equals(this.getId())) {
                toNode = getSimulator().getSimulationState().getProcessModel().getNodeKeyByValue(sae.getOldValue());
                break;
            }
            currentAuditEntryIndex++;
            if (currentAuditEntryIndex >= generator.getSortedAuditLog().getLog().size()) {
                throw new RuntimeException("NO1");
            }
        }
        Message newEvent = new Message(this, getId(), fromNode, toNode, startOffset, Message.REGULAR);
        getSimulator().insert(newEvent);
    }

    @Override
    public String getNextNode()
    {
        if (getSimulator().getSimulationState().getProcessModel().getEndingNodes().size() > 1) {
            throw new RuntimeException("There is more than one ending node. We need to improve the logic to determine which is the right ending node.");
        }
        else if (getSimulator().getSimulationState().getProcessModel().getEndingNodes().get(0).equals(this.getActivityState())) {
            return null;    
        }

        String nextNode = null;
        WorkflowInstanceReplayGenerator generator = ((WorkflowInstanceReplayGenerator) getSimulator().getGenerator());
        for (int i=currentAuditEntryIndex; i < generator.getSortedAuditLog().getLog().size(); i++) {
            SysAuditEntry sae = generator.getSortedAuditLog().getLog().get(i);
            currentAuditEntryIndex++;
            if (sae.getDocumentKey().equals(getId()) && sae.getFieldName().equals(getSimulator().getFieldName())) {
                nextNode = getSimulator().getSimulationState().getProcessModel().getNodeKeyByValue(sae.getNewValue());
                break;
            }
        }

        if (nextNode == null) {
            if (currentAuditEntryIndex == generator.getSortedAuditLog().getLog().size()) {
                nextNode = getSimulator().getSimulationState().getProcessModel().getEndingNodes().get(0);
            }
        }

        return nextNode;
    }

    @Override
    public double getNextNodeCompletionTime(final String fromNode, final String toNode, final Message message)
    {
        Date toNodeTS = null;
        Date fromNodeTS = null;
        if (toNode.equals(getSimulator().getSimulationState().getProcessModel().getEndingNodes().get(0))) {
            if (getSimulator().getSimulationState().getProcessModel().getEndingNodes().size() > 1) {
                throw new RuntimeException("More than one ending node. Need to fix logic.");
            }
            return 0.0;
        }

        WorkflowInstanceReplayGenerator generator = ((WorkflowInstanceReplayGenerator) getSimulator().getGenerator());
        int startCount = currentAuditEntryIndex == generator.getSortedAuditLog().getLog().size() ? currentAuditEntryIndex-1 : currentAuditEntryIndex;
        for (int i=startCount; i >= 0; i--) {
            SysAuditEntry sae = generator.getSortedAuditLog().getLog().get(i);
            if (sae.getDocumentKey().equals(getId())) {
                if (i > 0 && sae.getFieldName().equals(getSimulator().getFieldName())) {
                    if (toNodeTS == null) {
                        try {
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                            toNodeTS = formatter.parse(sae.getSysCreatedOn());
                        }
                        catch (ParseException e) {
                            throw new RuntimeException("Invalid timestamp format. This should not have happened");
                        }
                    }
                    else if (fromNodeTS == null) {
                        try {
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                            fromNodeTS = formatter.parse(sae.getSysCreatedOn());
                        }
                        catch (ParseException e) {
                            throw new RuntimeException("Invalid timestamp format. This should not have happened");
                        }
                    }
                }
                else if (i == firstAuditEntryIndex) {
                    if (fromNodeTS == null) {
                        try {
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                            fromNodeTS = formatter.parse(sae.getSysCreatedOn());
                        }
                        catch (ParseException e) {
                            throw new RuntimeException("Invalid timestamp format. This should not have happened");
                        }
                    }
                }
            }
        }

        double nextNodeCompletionTime = toNodeTS.getTime()-fromNodeTS.getTime();
        logger.debug("Id: (" + getId() + "). Completion Time (" + fromNode + ") -> (" + toNode + ") = (" + nextNodeCompletionTime + ")");

        return nextNodeCompletionTime;
    }

    private void printSysLogForInstance()
    {
        WorkflowInstanceReplayGenerator generator = ((WorkflowInstanceReplayGenerator) getSimulator().getGenerator());
        int i=0;
        for (SysAuditEntry sae : generator.getSortedAuditLog().getLog()) {
            if (sae.getDocumentKey().equals(this.getId())) {
                System.out.println("[" + this.getId() + "] - (" + i + ") = (" + sae.toJSON() + ")");
            }
            i++;
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(WorkflowReplayInstance.class);
}
