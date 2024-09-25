package com.servicenow.processmining.extensions.sc.entities;

import java.util.ArrayList;

import com.servicenow.processmining.extensions.sn.entities.ServiceNowEntity;

public class WorkflowHistory
    extends ServiceNowEntity
{
    boolean fixedOldAndNewValueSequence = false;
    private ArrayList<WorkflowHistoryEntry> history = null;

    public WorkflowHistory(WorkflowHistoryPK pk)
    {
        super(pk);
        history = new ArrayList<WorkflowHistoryEntry>();
    }

    public String getTableName()
    {
        if (history != null && history.size() > 0) {
            return history.get(0).getTableName();
        }

        return null;
    }

    public ArrayList<WorkflowHistoryEntry> getHistory()
    {
        return this.history;
    }

    public ArrayList<WorkflowHistoryEntry> getConnectedHistory()
    {
        if (!fixedOldAndNewValueSequence) {
            String previousValue = "";
            for (WorkflowHistoryEntry wfe : history) {
                if (wfe.getIndex().equals("0")) {
                    wfe.setOldValue("");
                }
                else {
                    wfe.setOldValue(previousValue);
                }
                wfe.setNewValue(wfe.getActivityName());
                previousValue = wfe.getActivityName();
            }
            fixedOldAndNewValueSequence = true;
        }

        return this.history;
    }
}
