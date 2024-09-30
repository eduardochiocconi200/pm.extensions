package com.servicenow.processmining.extensions.pm.model.detectors;

import com.servicenow.processmining.extensions.pm.model.ProcessMiningModel;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelFilter;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelFilterTransitions;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelFilterTransitionsNode;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelNode;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelTransition;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelVariant;
import com.servicenow.processmining.extensions.sn.core.TimeUtility;

import java.util.ArrayList;
import java.util.HashMap;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComplianceDetector
{
    private ProcessMiningModel model = null;
    private HashMap<String, Graph<String, DefaultEdge>> referencePathGraphs = null;
    private HashMap<String, ProcessMiningModelVariant> referencePathVariants = null;

    public ComplianceDetector(final ProcessMiningModel model)
    {
        this.model = model;
    }

    public ProcessMiningModel getModel()
    {
        return this.model;
    }

    private HashMap<String, Graph<String, DefaultEdge>> getReferencePathGraphs()
    {
        if (referencePathGraphs == null) {
            this.referencePathGraphs = new HashMap<String, Graph<String, DefaultEdge>>();
        }

        return referencePathGraphs;
    }

    private HashMap<String, ProcessMiningModelVariant> getReferencePathVariants()
    {
        if (referencePathVariants == null) {
            this.referencePathVariants = new HashMap<String, ProcessMiningModelVariant>();
        }

        return referencePathVariants;
    }

    private boolean hasReferencePath()
    {
        loadReferencePath();

        return referencePathGraphs != null && referencePathGraphs.size() > 0;
    }

    private void loadReferencePath()
    {
        int referencePathCount = 0;
        if (getModel().getFilters().size() > 0) {
            for (ProcessMiningModelFilter filter : getModel().getFilters().values()) {
                if (filter.getFilterTransitions() != null) {
                    if (isHappyPath(filter)) {
                        ProcessMiningModelVariant v = getReferencePathVariant(filter, referencePathCount++);
                        getReferencePathGraphs().put(v.getId(), getGraphForVariant(v));
                        getReferencePathVariants().put(v.getId(), v);
                    }
                }
            }
        }
    }

    private boolean isHappyPath(final ProcessMiningModelFilter filter)
    {
        if (filter.getName() != null && filter.getName().startsWith("Happy Path")) {
            return true;
        }

        return false;
    }

    private DefaultDirectedGraph<String, DefaultEdge> getGraphForVariant(final ProcessMiningModelVariant variant)
    {
        DefaultDirectedGraph<String, DefaultEdge> g = new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
        for (String node : variant.getDistinctNodes()) {
            // logger.debug("Add Vertex: (" + getModel().getNodes().get(node).getName() + ")");
            // g.addVertex(getModel().getNodes().get(node).getName());
            logger.debug("Add Vertex: (" + node + ")");
            g.addVertex(node);
        }

        for (String fromNode : variant.getDistinctNodes()) {
            for (ProcessMiningModelTransition transition : variant.getOutgoingTransition(fromNode)) {
                // logger.debug("Add Transition: (" + getModel().getNodes().get(transition.getFrom()).getName() + ", " + getModel().getNodes().get(transition.getTo()).getName() + ")");
                // g.addEdge(getModel().getNodes().get(transition.getFrom()).getName(), getModel().getNodes().get(transition.getTo()).getName());
                logger.debug("Add Transition: (" + transition.getFrom() + ", " + transition.getTo() + ")");
                g.addEdge(transition.getFrom(), transition.getTo());
            }
        }

        return g;
    }

    private ProcessMiningModelVariant getReferencePathVariant(final ProcessMiningModelFilter filter, final int referencePathCount)
    {
        ProcessMiningModelFilterTransitions filterTransitions = filter.getFilterTransitions();
        ProcessMiningModelVariant pmmv = new ProcessMiningModelVariant("reference_path" + String.valueOf(referencePathCount));
        ArrayList<ProcessMiningModelNode> nodes = new ArrayList<ProcessMiningModelNode>();
        ArrayList<ProcessMiningModelTransition> transitions = new ArrayList<ProcessMiningModelTransition>();
        boolean hasPassedFirstNode = false;
        ProcessMiningModelNode previousNode = null;
        for (ProcessMiningModelFilterTransitionsNode node : filterTransitions.getTransitionNodes()) {
            String nodeKey = getModel().getNodeKeyByValue(node.getValues());
            String nodeLabel = getModel().getNodeLabelByValue(node.getValues());
            logger.debug("Adding Node: (" + nodeKey + ") - (" + nodeLabel + ")");
            pmmv.addNodeToPath(nodeKey);
            ProcessMiningModelNode pmmn = new ProcessMiningModelNode(nodeKey, nodeLabel);
            nodes.add(pmmn);
            if (hasPassedFirstNode) {
                // If we cannot find a transition, it is because it may be a rework loop back.
                // In this case, we need to make sure there is a target transition to an already traversed node.
                // And then skip that node and reconnect the path without the intermediate node.
                if (getModel().getTransition(previousNode.getId(), pmmn.getId()) == null) {
                    if(!isThereAReworkLoop(previousNode, nodes)) {
                        throw new RuntimeException("Cannot find a rework loop from: (" + previousNode.getName() + ") to any previously traversed node!");
                    }
                    previousNode = nodes.get(nodes.size()-3);
                    if (getModel().getTransition(previousNode.getId(), pmmn.getId()) == null) {
                        throw new RuntimeException("Need to review linear definition of Happy Path with rework loops logic!");
                    }
                }
                ProcessMiningModelTransition transition = new ProcessMiningModelTransition(previousNode.getId(), pmmn.getId());
                transitions.add(transition);
                logger.debug("Adding Transition: (" + previousNode.getId() + ") - (" + pmmn.getId() + ") = (" + getModel().getTransition(previousNode.getId(), pmmn.getId()).getAvgDuration() + ")");
            }
            hasPassedFirstNode = true;
            previousNode = pmmn;
        }

        pmmv.setNodeCount(filterTransitions.getTransitionNodes().size());
        pmmv.setFrequency(filter.getCaseFrequency());
        pmmv.setTotalDuration(filter.getTotalDuration());
        pmmv.setMaxDuration(filter.getMaxDuration());
        pmmv.setMinDuration(filter.getMinDuration());
        pmmv.setAvgDuration(filter.getAvgDuration());
        pmmv.setMedianDuration(filter.getMedianDuration());
        pmmv.setStdDeviation(filter.getStdDeviation());
        pmmv.setNodes(nodes);
        pmmv.addTransitions(transitions);

        return pmmv;
    }

    private boolean isThereAReworkLoop(final ProcessMiningModelNode previousNode, final ArrayList<ProcessMiningModelNode> nodes)
    {
        for (ProcessMiningModelNode node : nodes) {
            if  (getModel().getTransition(previousNode.getId(), node.getId()) != null) {
                logger.debug("Found rework transition: (" + previousNode.getName() + ", " + node.getName() + ")");
                return true;
            }
        }

        return false;
    }

    public boolean run()
    {
        if (hasReferencePath()) {
            for (ProcessMiningModelVariant variant : getModel().getVariantsSortedByFrequency()) {
                checkVariantAgainstReferencePaths(variant);
            }
        }

        return true;
    }

    private boolean checkVariantAgainstReferencePaths(final ProcessMiningModelVariant variant)
    {
        boolean isCompliant = false;
        for (String referencePathVariantId : getReferencePathGraphs().keySet()) {
            Graph<String, DefaultEdge> referencePathGraph = getReferencePathGraphs().get(referencePathVariantId);
            ProcessMiningModelVariant referencePathVariant = getReferencePathVariants().get(referencePathVariantId);
            // checkVariantAgainstReferencePath(variant, getReferencePathVariants().get(referencePathVariant), referencePathGraph);
            if (isCompliantWithReferencePath(variant, referencePathVariant, referencePathGraph)) {
                addCompliantDetails(variant, referencePathVariant);
                isCompliant = true;
                break;
            }
        }

        if (!isCompliant) {
            addNonCompliantDetails(variant);
        }

        return true;
    }

    private void addCompliantDetails(final ProcessMiningModelVariant variant, final ProcessMiningModelVariant referencePathVariant)
    {
        variant.addNonComplianceDetails("The path: " + getModel().getVariantLabeledPath(variant) + " is compliant with the following reference path.");
        variant.addNonComplianceDetails("Reference Path: (" + getModel().getVariantLabeledPath(referencePathVariant) + ").");

        String variantComparativePerformance = "slower";
        int variantComparativeTimeDifference = TimeUtility.secondsToHours(variant.getAvgDuration() - referencePathVariant.getAvgDuration());
        if (referencePathVariant.getAvgDuration() > variant.getAvgDuration()) {
            variantComparativePerformance = "faster";
            variantComparativeTimeDifference = TimeUtility.secondsToHours(referencePathVariant.getAvgDuration() - variant.getAvgDuration());
        }
        if (variantComparativeTimeDifference == 0) {
            variant.addNonComplianceDetails("This variant overlaps 100% with this reference path.");
        }
        else {
            variant.addNonComplianceDetails("This variant's end-to-end time is ~'" + TimeUtility.secondsToHours(variant.getAvgDuration())+ "' hours and it is ~'" + variantComparativeTimeDifference + "' hours " + variantComparativePerformance + " on average than the reference path (~" + TimeUtility.secondsToHours(referencePathVariant.getAvgDuration()) + " hours).");
        }
    }

    private void addNonCompliantDetails(final ProcessMiningModelVariant variant)
    {
        int count = 1;
        variant.addNonComplianceDetails("The path: " + getModel().getVariantLabeledPath(variant) + " is NOT compliant with any of the following reference paths:");
        for (ProcessMiningModelVariant referencePathVariant : getReferencePathVariants().values()) {
            Graph<String, DefaultEdge> referencePathGraph = getReferencePathGraphs().get(referencePathVariant.getId());
            String nonComplianceReason = getNonComplianceReason(variant, referencePathVariant, referencePathGraph);
            variant.addNonComplianceDetails("Reference Path (" + count++ + "): (" + getModel().getVariantLabeledPath(referencePathVariant) + "). Reason: " + nonComplianceReason);
        }
    }

    private boolean isCompliantWithReferencePath(final ProcessMiningModelVariant variant, final ProcessMiningModelVariant referencePathVariant, final Graph<String, DefaultEdge> referencePathGraph)
    {
        String previousNode = null;
        boolean processedFirstNode = false;
        variant.setReferenceVariant(referencePathVariant);

        logger.debug("NODES:");
        for (ProcessMiningModelNode n : getModel().getNodes().values()) {
            logger.debug("[" + n.getId() + "] = (" + n.getName() + ")");
        }
        logger.debug("REFERENCE PATH: (" + referencePathGraph + ")");
        int nodeTraversalPosition = 0;
        boolean isCompliant = true;
        for (String node : variant.getPath()) {
            if (previousNode != null) {
                logger.debug("Checking (" + previousNode + ") to (" + node + ")");
                logger.debug("Checking (" + getModel().getNodes().get(previousNode).getName() + ") to (" + getModel().getNodes().get(node).getName() + ")");
            }
        
            if (processedFirstNode) {
                // First we need to check if both nodes exists. If one of the nodes do not exist, then this is not an overlap with the reference path.
                if (!referencePathGraph.containsVertex(previousNode)) {
                    isCompliant = false;
                    break;
                }
                else if (!referencePathGraph.containsVertex(node)) {
                    isCompliant = false;
                    break;
                }
                // If there is no specific edge/transition, then we may want to check if the target node is a node we have already traversed.
                // This is an indication of a rework loop. If we go back to that node, eventually the flow will get to the source node again,
                // or through a different path...
                else if (!referencePathGraph.containsEdge(previousNode, node)) {
                    logger.debug("No direct transition. Checking for rework loop...");
                    if (!variant.getPath().subList(0, nodeTraversalPosition).contains(node)) {
                        isCompliant = false;
                        break;    
                    }
                }
            }
            processedFirstNode = true;
            previousNode = node;
            nodeTraversalPosition++;
        }

        return isCompliant;
    }

    private String getNonComplianceReason(final ProcessMiningModelVariant variant, final ProcessMiningModelVariant referencePathVariant, final Graph<String, DefaultEdge> referencePathGraph)
    {
        String nonComplianceReason = "";
        String previousNode = null;
        boolean processedFirstNode = false;
        variant.setReferenceVariant(referencePathVariant);

        logger.debug("NODES:");
        for (ProcessMiningModelNode n : getModel().getNodes().values()) {
            logger.debug("[" + n.getId() + "] = (" + n.getName() + ")");
        }
        logger.debug("REFERENCE PATH: (" + referencePathGraph + ")");
        int nodeTraversalPosition = 0;
        for (String node : variant.getPath()) {
            if (previousNode != null) {
                logger.debug("Checking (" + previousNode + ") to (" + node + ")");
                logger.debug("Checking (" + getModel().getNodes().get(previousNode).getName() + ") to (" + getModel().getNodes().get(node).getName() + ")");
            }

            if (processedFirstNode) {
                // First we need to check if both nodes exists. If one of the nodes do not exist, then this is not an overlap with the reference path.
                if (!referencePathGraph.containsVertex(previousNode)) {
                    nonComplianceReason = "The node: (" + getModel().getNodes().get(previousNode).getName() + ") is not in the reference path.";
                    break;
                }
                else if (!referencePathGraph.containsVertex(node)) {
                    nonComplianceReason = "The node: (" + getModel().getNodes().get(node).getName() + ") is not in the reference path.";
                    break;
                }
                // If there is no specific edge/transition, then we may want to check if the target node is a node we have already traversed.
                // This is an indication of a rework loop. If we go back to that node, eventually the flow will get to the source node again,
                // or through a different path...
                else if (!referencePathGraph.containsEdge(previousNode, node)) {
                    logger.debug("No direct transition. Checking for rework loop...");
                    if (!variant.getPath().subList(0, nodeTraversalPosition).contains(node)) {
                        nonComplianceReason = "There is no transition from (" + getModel().getNodes().get(previousNode).getName() + ") to (" + getModel().getNodes().get(node).getName() + ") in the reference path.";
                        break;
                    }
                }
            }
            processedFirstNode = true;
            previousNode = node;
            nodeTraversalPosition++;
        }

        return nonComplianceReason;
    }

    private static final Logger logger = LoggerFactory.getLogger(ComplianceDetector.class);
}
