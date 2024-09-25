package com.servicenow.processmining.extensions.pm.model;

import java.util.ArrayList;

public class ProcessMiningModelFilterTransitions
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
        if (transitionNodes == null) {
            transitionNodes = new ArrayList<ProcessMiningModelFilterTransitionsNode>();
        }

        return transitionNodes.add(transitionNode);
    }

    public ArrayList<ProcessMiningModelFilterTransitionsNode> getTransitionNodes()
    {
        return this.transitionNodes;
    }
    
}
