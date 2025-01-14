package com.servicenow.processmining.extensions.pm.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

public class ProcessMiningModel
{
    private String versionId = null;
    private String projectId = null;
    private String name = null;
    private ArrayList<ProcessMiningModelEntity> entities = null;
    private HashMap<String, ProcessMiningModelFilter> filters = null;
    private HashMap<String, ProcessMiningModelNode> nodes = null;
    private ArrayList<String> startNodes = null;
    private ArrayList<String> middleNodes = null;
    private ArrayList<String> endNodes = null;
    private HashMap<String, ProcessMiningModelTransition> transitions = null;
    private HashMap<String, ProcessMiningModelBreakdown> breakdowns = null;
    private int totalVariants = -1;
    private HashMap<String, ProcessMiningModelVariant> variants = null;
    private TreeSet<ProcessMiningModelVariant> variantsByFrequency = null;
    private ProcessMiningModelAggregate aggregate = null;
    private HashMap<String, ProcessMiningModelVariant> referencePathVariants = null;

    public ProcessMiningModel(final String versionId)
    {
        this.versionId = versionId;
    }

    public String getVersionId()
    {
        return this.versionId;
    }

    public void setProjectId(final String projectId)
    {
        this.projectId = projectId;
    }

    public String getProjectId()
    {
        return this.projectId;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    public ArrayList<ProcessMiningModelEntity> getEntities()
    {
        if (entities == null) {
            entities = new ArrayList<ProcessMiningModelEntity>();
        }

        return entities;
    }

    public ProcessMiningModelEntity getEntity(String tableName, String fieldName)
    {
        for (ProcessMiningModelEntity entity : getEntities()) {
            if (entity.getTableName().equals(tableName)) {
                if (entity.getFieldName().equals(fieldName)) {
                    return entity;
                }
            }
        }

        return null;
    }

    public HashMap<String, ProcessMiningModelFilter> getFilters()
    {
        if (this.filters == null) {
            this.filters = new HashMap<String, ProcessMiningModelFilter>();
        }
    
        return this.filters;
    }

    public boolean addFilter(final ProcessMiningModelFilter filter)
    {
        if (getFilters().get(filter.getId()) == null) {
            getFilters().put(filter.getId(), filter);
            return true;
        }

        return false;
    }

    public void setFilters(ArrayList<ProcessMiningModelFilter> filters)
    {
        for (ProcessMiningModelFilter filter : filters) {
            addFilter(filter);
        }
    }    

    public HashMap<String, ProcessMiningModelNode> getNodes()
    {
        if (this.nodes == null) {
            this.nodes = new HashMap<String, ProcessMiningModelNode>();
        }
    
        return this.nodes;
    }

    public boolean addNode(final ProcessMiningModelNode node)
    {
        if (getNodes().get(node.getId()) == null) {
            getNodes().put(node.getId(), node);
            return true;
        }

        return false;
    }

    public void setNodes(ArrayList<ProcessMiningModelNode> nodes)
    {
        for (ProcessMiningModelNode node : nodes) {
            addNode(node);
        }
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
            middleNodes = new ArrayList<String>();
            for (ProcessMiningModelNode node : getNodes().values()) {
                if (!node.getIsStart() && !node.getIsEnd()) {
                    middleNodes.add(node.getId());
                }
            }
        }

        return middleNodes;
    }

    public String getNodeKeyByValue(final String value)
    {
        String key = null;
        for (ProcessMiningModelNode node : getNodes().values()) {
            if (node.getValue() != null && node.getValue().equals(value)) {
                key = node.getId();
                break;
            }
        }

        return key;
    }

    public String getNodeKeyByName(final String name)
    {
        String key = null;
        for (ProcessMiningModelNode node : getNodes().values()) {
            if (node.getName() != null && node.getName().equals(name)) {
                key = node.getId();
                break;
            }
        }

        return key;
    }

    public String getNodeLabelByValue(final String value)
    {
        String label = null;
        for (ProcessMiningModelNode node : getNodes().values()) {
            if (node.getValue() != null && node.getValue().equals(value)) {
                label = node.getName();
                break;
            }
        }

        return label;
    }

    public void setTransitions(ArrayList<ProcessMiningModelTransition> transitions)
    {
        for (ProcessMiningModelTransition transition : transitions) {
            addTransition(transition);
        }
    }

    public HashMap<String, ProcessMiningModelTransition> getTransitions()
    {
        if (this.transitions == null) {
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

    public boolean addTransition(ProcessMiningModelTransition transition)
    {
        String transitionId = transition.getId();
        if (getTransitions().get(transitionId) == null) {
            getTransitions().put(transitionId, transition);
            return true;
        }

        return false;
    }

    public void addTransitions(ArrayList<ProcessMiningModelTransition> transitions)
    {
        for (ProcessMiningModelTransition t : transitions) {
            addTransition(t);
        }
    }

    public void synchronizeTransitionNames()
    {
        for (ProcessMiningModelTransition t : getTransitions().values()) {
            String fromName = getNodes().get(t.getFrom()).getName();
            String toName = getNodes().get(t.getTo()).getName();
            t.setFromName(fromName);
            t.setToName(toName);
        }
    }

    public boolean hasMoreThanOneOutgoingTransition(String fromNodeId)
    {
        int times = 0;
        for (ProcessMiningModelTransition t : getTransitions().values()) {
            if (t.getFrom().equals(fromNodeId)) {
                times++;
            }
            if (times > 1) {
                return true;
            }
        }

        return false;
    }

    public HashMap<String, ProcessMiningModelBreakdown> getBreakdowns()
    {
        if (this.breakdowns == null) {
            this.breakdowns = new HashMap<String, ProcessMiningModelBreakdown>();
        }

        return this.breakdowns;
    }

    public boolean addBreakdowns(ArrayList<ProcessMiningModelBreakdown> breakdowns)
    {
        for (ProcessMiningModelBreakdown bd : breakdowns) {
            if (getBreakdowns().get(bd.getField()) == null) {
                getBreakdowns().put(bd.getField(), bd);
            }
        }

        return false;
    }

    public void setTotalVariants(final int totalVariants)
    {
        this.totalVariants = totalVariants;
    }

    public int getTotalVariants()
    {
        return this.totalVariants;
    }

    public HashMap<String, ProcessMiningModelVariant> getVariants()
    {
        if (this.variants == null) {
            this.variants = new HashMap<String, ProcessMiningModelVariant>();
        }
    
        return this.variants;
    }

    public TreeSet<ProcessMiningModelVariant> getVariantsSortedByFrequency()
    {
        variantsByFrequency = new TreeSet<ProcessMiningModelVariant>(new ProcessMiningModelVariantByFrequencyComparator());
        for (ProcessMiningModelVariant variant : getVariants().values()) {
            variantsByFrequency.add(variant);
        }

        return this.variantsByFrequency;
    }

    public boolean addVariant(ProcessMiningModelVariant variant)
    {
        if (getVariants().get(variant.getId()) == null) {
            getVariants().put(variant.getId(), variant);
            return true;
        }

        return false;
    }

    public ArrayList<String> getVariantLabeledPath(ProcessMiningModelVariant referencePathVariant)
    {
        ArrayList<String> labeledPath = new ArrayList<String>();
        for (String node : referencePathVariant.getPath()) {
            labeledPath.add(getNodes().get(node).getName());
        }
    
        return labeledPath;
    }

    public ArrayList<String> getAllCaseIds()
    {
        ArrayList<String> caseIds = new ArrayList<String>();

        for (ProcessMiningModelVariant variant  : getVariants().values()) {
            caseIds.addAll(variant.getCaseIds());
        }

        return caseIds;
    }

    public void setAggregate(final ProcessMiningModelAggregate aggregate)
    {
        this.aggregate = aggregate;
    }

    public ProcessMiningModelAggregate getAggregate()
    {
        return this.aggregate;
    }

    public void setReferencePathVariants(final HashMap<String, ProcessMiningModelVariant> referencePathVariants)
    {
        this.referencePathVariants = referencePathVariants;
    }

    public HashMap<String, ProcessMiningModelVariant> getReferencePathVariants()
    {
        return this.referencePathVariants;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        if (getAggregate() != null) {
            sb.append("[ProcessMiningModel: Total Records: '" + getAggregate().getCaseCount() + "', Avg Cycle Time: '" + getAggregate().getAvgCaseDuration() + "']");
        }
        else {
            sb.append("[ProcessMiningModel - Variant]");
        }
        
        return sb.toString();
    }
}
