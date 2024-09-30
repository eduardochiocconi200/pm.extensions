package com.servicenow.processmining.extensions.pm.model.detectors;

import java.util.ArrayList;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.alg.cycle.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import com.servicenow.processmining.extensions.pm.model.ProcessMiningModel;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelTransition;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelVariant;

public class AllDistinticRoutesDetector
{
    private ProcessMiningModel model = null;
    private ArrayList<Graph<String, DefaultEdge>> graphs = null;

    public AllDistinticRoutesDetector(final ProcessMiningModel model)
    {
        this.model = model;
    }

    public ProcessMiningModel getModel()
    {
        return this.model;
    }

    public ArrayList<Graph<String, DefaultEdge>> getGraphs()
    {
        if (graphs == null) {
            this.graphs = new ArrayList<Graph<String, DefaultEdge>>();

        }

        return graphs;
    }

    public boolean run()
    {
        for (ProcessMiningModelVariant variant : getModel().getVariantsSortedByFrequency()) {
            DefaultDirectedGraph<String, DefaultEdge> g = getGraphForVariant(variant);
            getGraphs().add(g);
        }

        for (Graph<String, DefaultEdge> g : getGraphs()) {
            CycleDetector<String, DefaultEdge> cycleDetector = new CycleDetector<String, DefaultEdge>(g);
            if (cycleDetector.detectCycles()) {
                Set<String> cycleVertices = cycleDetector.findCycles();
            }
        }

        return true;
    }

    private DefaultDirectedGraph<String, DefaultEdge> getGraphForVariant(final ProcessMiningModelVariant variant)
    {
        DefaultDirectedGraph<String, DefaultEdge> g = new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
        for (String node : variant.getDistinctNodes()) {
            g.addVertex(getModel().getNodes().get(node).getName());
        }

        for (String fromNode : variant.getDistinctNodes()) {
            for (ProcessMiningModelTransition transition : variant.getOutgoingTransition(fromNode)) {
                g.addEdge(getModel().getNodes().get(transition.getFrom()).getName(), getModel().getNodes().get(transition.getTo()).getName());
            }
        }

        return g;
    }
}
