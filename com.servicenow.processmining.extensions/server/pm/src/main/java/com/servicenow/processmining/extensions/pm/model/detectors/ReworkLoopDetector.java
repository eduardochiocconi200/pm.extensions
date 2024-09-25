package com.servicenow.processmining.extensions.pm.model.detectors;

import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelVariant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReworkLoopDetector
{
    private int V = -1;
    private List<List<Integer>> adj = null;
    private HashMap<String, Integer> nodesMap = new HashMap<String, Integer>();
    private HashMap<Integer, String> reverseNodesMap = new HashMap<Integer, String>();
    private ArrayList<ArrayList<String>> loops = new ArrayList<ArrayList<String>>();

    public ReworkLoopDetector(final ProcessMiningModelVariant variant)
    {
        this.V = variant.getDistinctNodes().size();
        adj = new ArrayList<>(V);

        for (int i = 0; i < V; i++) {
            adj.add(new LinkedList<>());
        }

        initMapping(variant);
    }

    private void initMapping(final ProcessMiningModelVariant variant)
    {
        int i = 0;
        for (String node : variant.getDistinctNodes()) {
            nodesMap.put(node, i);
            reverseNodesMap.put(i, node);
            i++;
        }

        int source = -1;
        int target = -1;
        for (String node : variant.getPath()) {
            if (source == -1) {
                source = nodesMap.get(node);
                continue;
            }
            target = nodesMap.get(node);
            addEdge(source, target);
            source = nodesMap.get(node);
        }
    }

    // Function to check if cycle exists
    private boolean isCyclicUtil(Stack<Integer> path, int i, boolean[] visited, boolean[] recStack)
    {
        logger.debug("Looking for loop in path: (" + path + ") with current node: (" + i + ")");
        // Mark the current node as visited and part of recursion stack
        if (recStack[i]) {
            logger.debug("Found loop with node: (" + i + ") in path: (" + path + ")");
            recordLoop(path, i);
            return true;
        }

        if (visited[i]) {
            return false;
        }

        visited[i] = true;
        recStack[i] = true;
        path.push(i);
    
        List<Integer> children = adj.get(i);

        for (Integer c : children) {
            isCyclicUtil(path, c, visited, recStack);
        }

        path.pop();

        return false;
    }

    private void recordLoop(Stack<Integer> path, int lastNode)
    {
        ArrayList<String> newLoop = new ArrayList<String>();
        newLoop.add(reverseNodesMap.get(lastNode));
        // We traverse the path backwards until we find a repeated node. That is where the cycle starts.
        // After that, we can eliminate all the other nodes.
        for (int i=path.size()-1; i >= 0; i--) {
            Integer node = path.elementAt(i);
            boolean nodeExistsInPath = newLoop.contains(reverseNodesMap.get(node));
            newLoop.add(reverseNodesMap.get(node));
            if (nodeExistsInPath) {
                break;
            }
        }

        // We make sure we are not adding the same loop again.
        boolean foundLoop = false;
        for (ArrayList<String> loop : loops) {
            if (loop.equals(newLoop)) {
                foundLoop = true;
                break;
            }
        }

        // Only add if it is a new loop.
        if (!foundLoop) {
            loops.add(newLoop);
        }
    }

    private void addEdge(int source, int dest)
    {
        if (!adj.get(source).contains(dest)) {
            adj.get(source).add(dest);
            logger.debug("Adding Edge: (" + source + ", " + dest + ")");
        }
    }

    // Returns true if the graph contains a cycle, else false.
    public boolean hasReworkCycles()
    {
        // Mark all the vertices as not visited and not part of recursion stack
        boolean[] visited = new boolean[V];
        boolean[] recStack = new boolean[V];
        Stack<Integer> path = new Stack<Integer>();

        // Call the recursive helper function to detect cycle in different DFS trees
        boolean reworkCycles = false;
        for (int i = 0; i < V; i++) {
            logger.debug("Starting node (" + i + ") traversal");
            isCyclicUtil(path, i, visited, recStack);
            // Clean up!
            for (int j=0; j < V; j++) {
                visited[j]= false;
                recStack[j] = false;
            }
        }

        if (loops.size() > 0) {
            reworkCycles = true;
            logger.debug("Found Loops");
            for (ArrayList<String> loop : loops) {
                logger.debug("Loop: " + loop + ")");
            }
        }
        else {
            logger.debug("No loops in this variant/route path");
        }

        return reworkCycles;
    }

    public ArrayList<ArrayList<String>> getLoops()
    {
        return loops;
    }

    private static final Logger logger = LoggerFactory.getLogger(ReworkLoopDetector.class);
}