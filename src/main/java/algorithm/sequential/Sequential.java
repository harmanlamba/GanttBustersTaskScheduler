package algorithm.sequential;

import algorithm.Algorithm;
import graph.Graph;
import graph.GraphNode;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A child class of algorithm which sequentially solves the task scheduling problem on one processor
 */
public class Sequential extends Algorithm {

    private Map<String, GraphNode> _output;
    private List<GraphNode> _topologicalOrder;

    /**
     * Instantiates a Sequential instance
     * @param g is a graph of the network
     * @param numProcTask is the number of processors that the tasks needed to be scheduled onto
     * @param numProcParallel is the number of processors the algorithm should be working on
     */
    public Sequential(Graph g, int numProcTask, int numProcParallel) {
        super(g, numProcTask, numProcParallel);
        _output = new HashMap<>();
        _topologicalOrder = new ArrayList<>();
    }

    /**
     * Returns the topological order of the graph
     * @return the topological order
     */
    public List<GraphNode> getTopologicalOrdering() {
        TopologicalOrderIterator iterator = new TopologicalOrderIterator(_graph.getGraph());

        while(iterator.hasNext()) {
            GraphNode tempNode = (GraphNode) iterator.next();
            _topologicalOrder.add(tempNode);
        }
        return _topologicalOrder;
    }

    /**
     * Method that solves the problem sequentially on one processor
     * @return A map of the nodes with their corresponding start times (string is the name of the
     * node and GraphNode contains all of the node information)
     */
    @Override
    public Map<String, GraphNode> solve() {
        // Creates output format of ordering and scheduling (start times for sequential)
        int currentTime = 0;
        for (GraphNode task : getTopologicalOrdering()) {
            task.setProcessor(0);
            task.setStartTime(currentTime);
            _output.put(task.getId(), task);
            currentTime += task.getWeight();
        }
        return _output;
    }

    @Override
    public Map<String, GraphNode> getCurrentBestSolution() {
        // Method never called because sequential algorithm is too fast
        return _output;
    }

    @Override
    public int getBestScheduleCost() {
        GraphNode task = _topologicalOrder.get(_topologicalOrder.size()-1);
        return task.getStartTime() + task.getWeight();
    }
}
