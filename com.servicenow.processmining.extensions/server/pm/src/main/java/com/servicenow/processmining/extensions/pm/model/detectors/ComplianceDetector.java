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
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComplianceDetector
{
    private ProcessMiningModel model = null;
    private Graph<String, DefaultEdge> modelGraph = null;
    private HashMap<String, Graph<String, DefaultEdge>> referencePathGraphs = null;
    private HashMap<String, ProcessMiningModelVariant> referencePathVariants = null;
    private HashMap<String, Graph<String, DefaultEdge>> variantGraphs = null;

    public ComplianceDetector(final ProcessMiningModel model)
    {
        this.model = model;
    }

    public ProcessMiningModel getModel()
    {
        return this.model;
    }

    private Graph<String, DefaultEdge> getModelGraph()
    {
        if (modelGraph == null) {
            loadModelGraph();
        }

        return modelGraph;
    }

    private boolean loadModelGraph()
    {
        modelGraph = new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
        for (ProcessMiningModelNode node : getModel().getNodes().values()) {
            // logger.debug("Add Vertex: (" + getModel().getNodes().get(node).getName() + ")");
            // g.addVertex(getModel().getNodes().get(node).getName());
            logger.debug("Model - Add Vertex: (" + node.getId() + ") - (" + node.getName() + ")");
            modelGraph.addVertex(node.getId());
        }

        for (ProcessMiningModelTransition transition : getModel().getTransitions().values()) {
            logger.debug("Model - Add Transition: (" + transition.getFrom() + ", " + transition.getTo() + ") - (" + getModel().getNodes().get(transition.getFrom()).getName() + ", " + getModel().getNodes().get(transition.getTo()).getName()+ ")");
            modelGraph.addEdge(transition.getFrom(), transition.getTo());
        }

        return true;
    }

    private HashMap<String, Graph<String, DefaultEdge>> getReferencePathGraphs()
    {
        if (referencePathGraphs == null) {
            this.referencePathGraphs = new HashMap<String, Graph<String, DefaultEdge>>();
        }

        return referencePathGraphs;
    }

    private HashMap<String, Graph<String, DefaultEdge>> getVariantGraphs()
    {
        if (variantGraphs == null) {
            this.variantGraphs = new HashMap<String, Graph<String, DefaultEdge>>();
        }

        return variantGraphs;
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
        logger.debug("Creating Graph from Variant: (" + variant.getTranslatedRouteNodes() + ")");
        Graph<String, DefaultEdge> result = getVariantGraphs().get(variant.getId());
        if (result == null) {
            DefaultDirectedGraph<String, DefaultEdge> g = new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
            for (String node : variant.getDistinctNodes()) {
                logger.debug("Variant - Add Vertex: (" + node + ") - (" + getModel().getNodes().get(node).getName() + ")");
                g.addVertex(node);
            }

            for (String fromNode : variant.getDistinctNodes()) {
                for (ProcessMiningModelTransition transition : variant.getOutgoingTransition(fromNode)) {
                    logger.debug("Variant - Add Transition: (" + transition.getFrom() + ", " + transition.getTo() + ") - (" + getModel().getNodes().get(transition.getFrom()).getName() + ", " + getModel().getNodes().get(transition.getTo()).getName()+ ")");
                    g.addEdge(transition.getFrom(), transition.getTo());
                }
            }

            result = g;
            getVariantGraphs().put(variant.getId(), g);
        }

        return (DefaultDirectedGraph<String, DefaultEdge>) result;
    }

    private ProcessMiningModelVariant getReferencePathVariant(final ProcessMiningModelFilter filter, final int referencePathCount)
    {
        ProcessMiningModelFilterTransitions filterTransitions = filter.getFilterTransitions();
        ProcessMiningModelVariant pmmv = new ProcessMiningModelVariant("ref_path" + String.valueOf(referencePathCount));
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
                    if(!isThereAReworkLoopAndAPathForwardTo(previousNode, pmmn, nodes, transitions)) {
                        throw new RuntimeException("Cannot find a rework loop from: (" + previousNode.getName() + ") to any previously traversed node!");
                    }
                }
                else {
                    ProcessMiningModelTransition transition = new ProcessMiningModelTransition(previousNode.getId(), pmmn.getId());
                    transitions.add(transition);
                    logger.debug("Adding Transition: (" + previousNode.getId() + ") - (" + pmmn.getId() + ") = (" + getModel().getTransition(previousNode.getId(), pmmn.getId()).getAvgDuration() + ")");
                }
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

    private boolean isThereAReworkLoopAndAPathForwardTo(final ProcessMiningModelNode currentNode, final ProcessMiningModelNode toNode, final ArrayList<ProcessMiningModelNode> traversedNodes, ArrayList<ProcessMiningModelTransition> traversedTransitions)
    {
        for (ProcessMiningModelNode node : traversedNodes) {
            // First, we need to check if there is a back transition (re-work loop) from the current node to any
            // previously travesed node.
            if  (getModel().getTransition(currentNode.getId(), node.getId()) != null) {
                logger.debug("Found rework transition: (" + currentNode.getName() + ", " + node.getName() + ")");
                // Once we find a re-work loop, we need to check if we can find a path from the current, to the "toNode"
                // node via a different path.
                String fromNode = node.getId();
                AllDirectedPaths<String, DefaultEdge> allPaths = new AllDirectedPaths<String, DefaultEdge>(getModelGraph());
                // Get all the paths between the from and to nodes.
                List<GraphPath<String, DefaultEdge>> connectingPaths = allPaths.getAllPaths(fromNode, toNode.getId(), true, null);
                // If there are paths, we need to add them all, as a the predicate is "eventually followed by", so any path is viable.
                for (GraphPath<String, DefaultEdge> path : connectingPaths) {
                    // For each Graph, we need to traverse their transitions and make sure we are not adding duplicates, but any delta.
                    for (DefaultEdge pathTransition : path.getEdgeList()) {
                        String edge = pathTransition.toString();
                        String source = edge.substring(1, edge.indexOf(" "));
                        String target = edge.substring(edge.lastIndexOf(":") + 2, edge.length()-1);
                        ProcessMiningModelTransition transition = new ProcessMiningModelTransition(source, target);
                        boolean foundTransition = false;
                        for (ProcessMiningModelTransition t : traversedTransitions) {
                            if (t.getFrom().equals(source) && t.getTo().equals(target)) {
                                foundTransition = true;
                                break;
                            }
                        }
                        if (!foundTransition) {
                            traversedTransitions.add(transition);
                        }
                    }
                }

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
        logger.debug("Checking Variant: (" + variant.getId() + ") = (" + variant.getTranslatedRouteNodes() + ") against Reference Paths!");
        if (variant.getId().equals("17b96756825e1d693a3f34b85ae49bf0")) {
            System.out.println("FOUND");
        }
        for (String referencePathVariantId : getReferencePathVariants().keySet()) {
            logger.debug("Checking against Reference Path: (" + referencePathVariantId + ")");
            ProcessMiningModelVariant referencePathVariant = getReferencePathVariants().get(referencePathVariantId);
            // Graph<String, DefaultEdge> variantGraph = getGraphForVariant(variant);
            Graph<String, DefaultEdge> referencePathVariantGraph = getGraphForVariant(referencePathVariant);
            // if (isCompliantWithReferencePath(referencePathVariant, variant, variantGraph)) {
            if (isCompliantWithReferencePath(variant, referencePathVariant, referencePathVariantGraph)) {
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
        variant.addNonComplianceDetails("The variant path: " + getModel().getVariantLabeledPath(variant) + " IS compliant with the following reference path.");
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
        variant.addNonComplianceDetails("The variant path: " + getModel().getVariantLabeledPath(variant) + " is NOT compliant with any of the following reference paths:");
        for (ProcessMiningModelVariant referencePathVariant : getReferencePathVariants().values()) {
            Graph<String, DefaultEdge> referencePathGraph = getReferencePathGraphs().get(referencePathVariant.getId());
            String nonComplianceReason = getNonComplianceReason(variant, referencePathVariant, referencePathGraph);
            variant.addNonComplianceDetails("Reference Path (" + count++ + "): (" + getModel().getVariantLabeledPath(referencePathVariant) + "). Reason: " + nonComplianceReason);
        }
    }

    /**
     * Being compliant means two primary conditions:
     * 1. Both variants include the SAME nodes (not a subset or a superset).
     * 2. The variant should be an overlay of the reference path.
     * @param referencePathVariant
     * @param variant
     * @param variantGraph
     * @return
     */
    private boolean isCompliantWithReferencePath(final ProcessMiningModelVariant variant, final ProcessMiningModelVariant referencePathVariant, final Graph<String, DefaultEdge> referencePathVariantGraph)
    {
        logger.debug("NODES:");
        for (ProcessMiningModelNode n : getModel().getNodes().values()) {
            logger.debug("[" + n.getId() + "] = (" + n.getName() + ")");
        }
        boolean sameNodes = variantHasSameNodesAsReferencePath(variant, referencePathVariant);
        boolean isOverlay = variantIsOverlayOfReferencePath(variant, referencePathVariant, referencePathVariantGraph);

        return sameNodes && isOverlay;
    }

    private boolean variantHasSameNodesAsReferencePath(final ProcessMiningModelVariant referencePathVariant, final ProcessMiningModelVariant variant)
    {
        referencePathVariant.getDistinctNodes().sort(null);
        variant.getDistinctNodes().sort(null);
        System.out.println("REFERENCE DISTINCT NODES (" + referencePathVariant.getId() + "): " + referencePathVariant.getTranslatedDistinctNodes());
        System.out.println("VARIANT DISTINCT NODES (" + variant.getId() + "): " + variant.getTranslatedDistinctNodes());
        boolean sameNodes = referencePathVariant.getDistinctNodes().equals(variant.getDistinctNodes());
        return sameNodes;
    }

    private boolean variantIsOverlayOfReferencePath(final ProcessMiningModelVariant variant, final ProcessMiningModelVariant referencePathVariant, final Graph<String, DefaultEdge> referencePathVariantGraph)
    {
        logger.debug("TRANSLATED VARIANT PATH (" + variant.getId() + "): (" + variant.getTranslatedRouteNodes()  + ")");
        logger.debug("TRANSLATED REFERENCE PATH (" + referencePathVariant.getId() + "): (" + referencePathVariant.getTranslatedRouteNodes() + ")");
        String previousNode = null;
        boolean processedFirstNode = false;
        int nodeTraversalPosition = 0;
        boolean isOverlay = true;
        for (String node : variant.getPath()) {
            if (previousNode != null) {
                logger.debug("Checking (" + previousNode + ") to (" + node + ")");
                logger.debug("Checking (" + getModel().getNodes().get(previousNode).getName() + ") to (" + getModel().getNodes().get(node).getName() + ")");
            }
        
            if (processedFirstNode) {
                // First we need to check if both nodes exists. If one of the nodes do not exist, then this is not an overlap with the reference path.
                if (!referencePathVariantGraph.containsVertex(previousNode)) {
                    isOverlay = false;
                    break;
                }
                else if (!referencePathVariantGraph.containsVertex(node)) {
                    isOverlay = false;
                    break;
                }
                // If there is no specific edge/transition, then we may want to check if the target node is a node we have already traversed.
                // This is an indication of a rework loop. If we go back to that node, eventually the flow will get to the source node again,
                // or through a different path...
                else if (!referencePathVariantGraph.containsEdge(previousNode, node)) {
                    logger.debug("No direct transition. Checking for rework loop...");
                    System.out.println("LIST: (" + variant.getPath().subList(0, nodeTraversalPosition) + "). Has (" + node + ")");
                    if (!variant.getPath().subList(0, nodeTraversalPosition).contains(node)) {
                        isOverlay = false;
                        break;    
                    }
                }
            }
            processedFirstNode = true;
            previousNode = node;
            nodeTraversalPosition++;
        }

        if (isOverlay) {
            variant.setReferenceVariant(referencePathVariant);
        }

        return isOverlay;
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
        logger.debug("TRANSLATED REFERENCE PATH: (" + referencePathVariant.getTranslatedRouteNodes() + ")");
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
