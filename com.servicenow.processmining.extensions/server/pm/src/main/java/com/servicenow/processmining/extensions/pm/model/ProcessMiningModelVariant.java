package com.servicenow.processmining.extensions.pm.model;

import java.util.ArrayList;
import java.util.HashMap;

public class ProcessMiningModelVariant
{
    private String id = null;
    private String entityId = null;
    private ArrayList<String> path = null;
    private String routePath = null;
    private String translatedRoutePath = null;
    private HashMap<String, ProcessMiningModelNode> nodes = null;
    private ArrayList<String> startNodes = null;
    private ArrayList<String> middleNodes = null;
    private ArrayList<String> endNodes = null;
    private HashMap<String, ProcessMiningModelTransition> transitions = null;
    private int nodeCount = -1;
    private int frequency = -1;
    private int totalDuration = -1;
    private int maxDuration = -1;
    private int minDuration = -1;
    private int avgDuration = -1;
    private int stdDeviation = -1;
    private int medianDuration = -1;
    private double creationIntervalDuration = 0.0;
    private ArrayList<String> distinctNodes = null;
    private ArrayList<String> translatedDistinctNodes = null;
    private ArrayList<String> nonComplianceNote = null;
    private ProcessMiningModelVariant referenceVariant = null;
    
    public ProcessMiningModelVariant(final String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return this.id;
    }

    public void setEntityId(final String entityId)
    {
        this.entityId = entityId;
    }

    public String getEntityId()
    {
        return this.entityId;
    }

    public void setPath(final ArrayList<String> path)
    {
        this.path = path;
    }

    public boolean addNodeToPath(final String node)
    {
        return getPath().add(node);
    }

    public ArrayList<String> getPath()
    {
        if (path == null) {
            this.path = new ArrayList<String>();
        }

        return this.path;
    }

    public String getRouteNodes()
    {
        if (routePath == null) {
            routePath = "";
            boolean processedFirstElement = false;
            for (String node : getPath()) {
                if (processedFirstElement) {
                    routePath += ",";
                }
                routePath += node;
                processedFirstElement = true;
            }
        }

        return routePath;
    }

    public String getTranslatedRouteNodes()
    {
        if (translatedRoutePath == null) {
            translatedRoutePath = "";
            boolean processedFirstElement = false;
            for (String node : getPath()) {
                if (processedFirstElement) {
                    translatedRoutePath += ",";
                }
                translatedRoutePath += getNodes().get(node).getName();
                processedFirstElement = true;
            }
        }

        return translatedRoutePath;
    }

    public void setNodes(final ArrayList<ProcessMiningModelNode> nodes)
    {
        for (ProcessMiningModelNode node : nodes) {
            getNodes().put(node.getId(), node);
        }        
    }

    public void setNodes(final HashMap<String, ProcessMiningModelNode> nodes)
    {
        this.nodes = nodes;
    }

    public HashMap<String, ProcessMiningModelNode> getNodes()
    {
        if (nodes == null) {
            this.nodes = new HashMap<String, ProcessMiningModelNode>();
        }

        return this.nodes;
    }

    public ArrayList<String> getStartingNodes()
    {
        if (startNodes == null) {
            startNodes = new ArrayList<String>();
            for (ProcessMiningModelNode node : getNodes().values()) {
                if (node.getIsStart()) {
                    startNodes.add(node.getId());
                }
            }
        }

        return startNodes;
    }

    public ArrayList<String> getEndingNodes()
    {
        if (endNodes == null) {
            endNodes = new ArrayList<String>();
            for (ProcessMiningModelNode node : getNodes().values()) {
                if (node.getIsEnd()) {
                    endNodes.add(node.getId());
                }
            }
        }

        return endNodes;
    }

    public ArrayList<String> getMiddleNodes()
    {
        if (middleNodes == null) {
            ArrayList<String> middleNodes = new ArrayList<String>();
            for (ProcessMiningModelNode node : getNodes().values()) {
                if (!node.getIsStart() && !node.getIsEnd()) {
                    middleNodes.add(node.getId());
                }
            }
        }

        return middleNodes;
    }

    public void setTransitions(final HashMap<String, ProcessMiningModelTransition> transitions)
    {
        this.transitions = transitions;
    }

    public void addTransitions(final ArrayList<ProcessMiningModelTransition> transitions)
    {
        for (ProcessMiningModelTransition transition : transitions) {
            getTransitions().put(transition.getId(), transition);
        }        
    }

    public HashMap<String, ProcessMiningModelTransition> getTransitions()
    {
        if (transitions == null) {
            this.transitions = new HashMap<String, ProcessMiningModelTransition>();
        }

        return this.transitions;
    }

    public ProcessMiningModelTransition getTransition(final String fromNodeId, final String toNodeId)
    {
        String transitionId = fromNodeId + "-To-" + toNodeId;
        ProcessMiningModelTransition transition = getTransitions().get(transitionId);

        return transition;
    }

    public ArrayList<ProcessMiningModelTransition> getOutgoingTransition(String activityState)
    {
        // TODO: Optimize!
        ArrayList<ProcessMiningModelTransition> outTransitions = new ArrayList<ProcessMiningModelTransition>();
        for (ProcessMiningModelTransition t : getTransitions().values()) {
            if (t.getFrom().equals(activityState)) {
                outTransitions.add(t);
            }
        }

        return outTransitions;
    }    

    public void setNodeCount(final int nodeCount)
    {
        this.nodeCount = nodeCount;
    }

    public int getNodeCount()
    {
        return this.nodeCount;
    }

    public void setFrequency(final int frequency)
    {
        this.frequency = frequency;
    }

    public int getFrequency()
    {
        return this.frequency;
    }

    public void setTotalDuration(final int totalDuration)
    {
        this.totalDuration = totalDuration;
    }

    public int getTotalDuration()
    {
        return this.totalDuration;
    }

    public void setMaxDuration(final int maxDuration)
    {
        this.maxDuration = maxDuration;
    }

    public int getMaxDuration()
    {
        return this.maxDuration;
    }

    public void setMinDuration(final int minDuration)
    {
        this.minDuration = minDuration;
    }

    public int getMinDuration()
    {
        return this.minDuration;
    }

    public void setAvgDuration(final int avgDuration)
    {
        this.avgDuration = avgDuration;
    }

    public int getAvgDuration()
    {
        return this.avgDuration;
    }

    public void setStdDeviation(final int stdDeviation)
    {
        this.stdDeviation = stdDeviation;
    }

    public int getStdDeviation()
    {
        return this.stdDeviation;
    }

    public void setMedianDuration(final int medianDuration)
    {
        this.medianDuration = medianDuration;
    }

    public int getMedianDuration()
    {
        return this.medianDuration;
    }

    public void setCreationInterval(final double creationIntervalDuration)
    {
        this.creationIntervalDuration = creationIntervalDuration;
    }

    public double getCreationIntervalDuration()
    {
        return this.creationIntervalDuration;
    }

    public ArrayList<String> getDistinctNodes()
    {
        if (distinctNodes == null) {
            distinctNodes = new ArrayList<String>();
            for (String node : getPath()) {
                if (!distinctNodes.contains(node)) {
                    distinctNodes.add(node);
                }
            }
        }

        return distinctNodes;
    }

    public ArrayList<String> getTranslatedDistinctNodes()
    {
        if (translatedDistinctNodes == null) {
            translatedDistinctNodes = new ArrayList<String>();
            for (String node : getPath()) {
                if (!translatedDistinctNodes.contains(getNodes().get(node).getName())) {
                    String translatedNode = getNodes().get(node).getName();
                    translatedDistinctNodes.add(translatedNode);
                }
            }
        }

        return translatedDistinctNodes;
    }

    public boolean hasNonComplianceNote()
    {
        return this.nonComplianceNote != null && this.nonComplianceNote.size() > 0;
    }

    public void addNonComplianceDetails(final String message)
    {
        getNonComplianceDetails().add(message);
    }

    public ArrayList<String> getNonComplianceDetails()
    {
        if (this.nonComplianceNote == null) {
            this.nonComplianceNote = new ArrayList<String>();
        }

        return this.nonComplianceNote;
    }

    public void setReferenceVariant(final ProcessMiningModelVariant referenceVariant)
    {
        this.referenceVariant = referenceVariant;
    }

    public ProcessMiningModelVariant getReferenceVariant()
    {
        return this.referenceVariant;
    }

    public boolean isReferencePathVariation()
    {
        return (getAvgDuration() - getReferenceVariant().getAvgDuration()) == 0;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        
        sb.append("[Variant: \n");
        sb.append(" - Id: '" + getId() + "', Entity: '" + getEntityId() + "', Path: (" + getPath() + "), nodeCount: '" + getNodeCount() + "', Frequency: '" + getFrequency() + "', Total Duration: '" + getTotalDuration() + "', Max: '" + getMaxDuration() + "', Min: '" + getMinDuration() + "', Avg: '" + getAvgDuration() + "', Std: '" + getStdDeviation() + "', Median: '" + getMedianDuration() + "'\n");
        sb.append(" - Nodes:\n");
        for (ProcessMiningModelNode node : getNodes().values()) {
            sb.append("   - " + node.toString() + "\n");
        }
        sb.append(" - Transitions:\n");
        for (ProcessMiningModelTransition transition : getTransitions().values()) {
            sb.append("   - " + transition.toString() + "\n");
        }
        sb.append("]");

        return sb.toString();
    }

    public String toStringPrimaryValues()
    {
        StringBuffer sb = new StringBuffer();
        
        sb.append("[Variant: ");
        sb.append("Node Count: '" + getNodeCount() + "', Frequency: '" + getFrequency() + "', Total Duration: '" + getTotalDuration() + "', Avg: '" + getAvgDuration() + "', Path: (" + getPath() + ")]");

        return sb.toString();
    }
}