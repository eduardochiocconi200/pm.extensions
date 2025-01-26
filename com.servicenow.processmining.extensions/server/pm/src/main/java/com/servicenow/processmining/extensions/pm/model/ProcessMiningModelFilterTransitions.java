package com.servicenow.processmining.extensions.pm.model;

import java.io.Serializable;
import java.util.ArrayList;

public class ProcessMiningModelFilterTransitions
    implements Serializable
{
    private ArrayList<ProcessMiningModelFilterTransitionsNode> transitionNodes = null;

    public ProcessMiningModelFilterTransitions()
    {
    }

    public void setTransitionNodes(final ArrayList<ProcessMiningModelFilterTransitionsNode> transitionNodes)
    {
        this.transitionNodes = transitionNodes;
    }

    public boolean addTransitionsNode(final ProcessMiningModelFilterTransitionsNode transitionNode)
    {
        return getTransitionNodes().add(transitionNode);
    }

    public ArrayList<ProcessMiningModelFilterTransitionsNode> getTransitionNodes()
    {
        if (transitionNodes == null) {
            transitionNodes = new ArrayList<ProcessMiningModelFilterTransitionsNode>();
        }

        return this.transitionNodes;
    }

    public String getFilterTransitionsJSON()
    {
        String json = " [ ";

        boolean processedFirst = false;
        for (ProcessMiningModelFilterTransitionsNode t : transitionNodes) {
            if (processedFirst) {
                json += ", ";
            }
            json += t.getTransitionNodeJSON();

            processedFirst = true;
        }

        json += "]";

        return json;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("[ Transition Nodes: (" + transitionNodes + ")]");
        return sb.toString();
    }
}
