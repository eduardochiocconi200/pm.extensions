package com.servicenow.processmining.extensions.pm.analysis.goldratt;

import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModel;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelVariant;

public class ValueStream
{
    @JsonIgnore
    private ProcessMiningModel model = null;
    private ArrayList<String> nodes = null;
    private ArrayList<ValueStreamPhase> phases = null;
    private HashMap<String, Integer> nodesToPhaseMap = null;
    
    public ValueStream()
    {
    }

    public ValueStream(final ProcessMiningModel model)
    {
        this.model = model;
    }

    public void setModel(final ProcessMiningModel model)
    {
        this.model = model;
    }

    public ProcessMiningModel getModel()
    {
        return this.model;
    }

    public void setNodes(final ArrayList<String> nodes)
    {
        this.nodes = nodes;
    }

    public ArrayList<String> getNodes()
    {
        if (this.nodes == null) {
            this.nodes = new ArrayList<String>();
        }

        return this.nodes;
    }

    public ArrayList<ValueStreamPhase> getPhases()
    {
        if (this.phases == null) {
            this.phases = new ArrayList<ValueStreamPhase>();
        }

        return this.phases;
    }

    private HashMap<String, Integer> getNodesToPhaseMap()
    {
        if (this.nodesToPhaseMap == null) {
            this.nodesToPhaseMap = new HashMap<String, Integer>();
            Integer phase = Integer.valueOf(1);
            for (ValueStreamPhase vsp : getPhases()) {
                for (String node : vsp.getNodes()) {
                    String nodeId = getModel().getNodeKeyByName(node);
                    this.nodesToPhaseMap.put(nodeId, phase);
                }
                phase = Integer.valueOf(phase.intValue() + 1);
            }
        }

        if (this.nodesToPhaseMap.size() != getModel().getNodes().size()) {
            throw new RuntimeException("Not all nodes in the Process Model (" + this.nodesToPhaseMap.size() + ") are mapped to the Value Stream (" + getModel().getNodes().size() + "). Please make sure all nodes are mapped to a phase.");
        }

        return this.nodesToPhaseMap;
    }

    public int getVariantLastPhaseIndex(final ProcessMiningModelVariant variant, final int currentPhase)
    {
        for (int i=variant.getPath().size()-1; i >= 0; i--) {
            int pathNodePhase = getNodesToPhaseMap().get(variant.getPath().get(i));
            if (pathNodePhase == currentPhase) {
                return i;
            }
        }

        return -1;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("Phases: \n");
        for (ValueStreamPhase vsp : getPhases()) {
            sb.append("Phase [" + vsp.getName() + "]\n - Nodes: (");
            boolean processedFirst = false;
            for (String node : vsp.getNodes()) {
                if (processedFirst) {
                    sb.append(", ");
                }
                sb.append(node);
                processedFirst = true;
            }
            sb.append("),\n");
            sb.append(" - Statistics:\n");
            sb.append("   - Summary: " + vsp.getStatistics().getSummary() + "\n");
            sb.append("   - Measurements:\n");
            processedFirst = false;
            for (ValueStreamPhaseMeasure m : vsp.getStatistics().getMeasures()) {
                if (processedFirst) {
                    sb.append(", ");
                }
                sb.append("     - " + m);
                processedFirst = true;
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}