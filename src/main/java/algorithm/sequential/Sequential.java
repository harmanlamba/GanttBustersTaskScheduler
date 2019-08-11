package algorithm.sequential;

import algorithm.Algorithm;
import graph.Graph;
import graph.GraphNode;

import java.util.HashMap;
import java.util.Map;

/**
 * A child class of algorithm which sequentially solves the task scheduling problem on one processor
 */
public class Sequential extends Algorithm {

    /**
     * Instantiates a Sequential instance
     * @param g is a graph of the network
     * @param numProcTask is the number of processors that the tasks needed to be scheduled onto
     * @param numProcParallel is the number of processors the algorithm should be working on
     */
    public Sequential(Graph g, int numProcTask, int numProcParallel) {
        super(g, numProcTask, numProcParallel);
    }

    /**
     * Method that solves the problem sequentially on one processor
     * @return A map of the nodes with their corresponding start times (string is the name of the
     * node and GraphNode contains all of the node information)
     */
    @Override
    public Map<String, GraphNode> solve() {

        // Gets topological order of the network graph and puts order into field
        getTopologicalOrdering();
        Map<String, GraphNode> output = new HashMap<>();

        // Creates output format of ordering and scheduling (start times for sequential)
        int currentTime = 0;
        for (int i = 0; i < _order.size(); i++) {
            GraphNode tempNode = _order.get(i);
            GraphNode tempOutputNode = new GraphNode(tempNode, _numProcTask, currentTime);
            output.put(tempNode.getId(), tempOutputNode);
            currentTime += tempNode.getWeight();
            //System.out.println(tempNode.getId() + " " + tempOutputNode.getStartTime());
        }
        return output;
    }
}
