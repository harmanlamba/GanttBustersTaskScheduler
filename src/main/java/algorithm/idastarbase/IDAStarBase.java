package algorithm.idastarbase;

import algorithm.Algorithm;
import graph.Graph;
import graph.GraphNode;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import java.util.*;

/**
 * IDAStarBase is a child class of Algorithm which solves the task scheduling problem optimally
 * on one processor.
 */
public class IDAStarBase extends Algorithm {

    private DirectedWeightedMultigraph<GraphNode, DefaultWeightedEdge> _jGraph;
    private Map<String, Boolean> _freeTaskMapping;
    private List<GraphNode> _freeTaskList;
    private int _numberOfTasks;

    /**
     * Constructor for IDAStarBase to instantiate the object
     * @param g is a graph of the network
     * @param numProcTask is the number of processors that the tasks needed to be scheduled onto
     * @param numProcParallel is the number of processors the algorithm should be working on
     */
    public IDAStarBase(Graph g, int numProcTask, int numProcParallel) {
        super(g, numProcTask, numProcParallel);
        _bestFState = null;
        _freeTaskMapping = new HashMap<>();
        _freeTaskList = new ArrayList<>();
        _jGraph = _graph.getGraph();
        _numberOfTasks = _jGraph.vertexSet().size();
        initialiseFreeTasks();
        System.out.println(maxComputationalTime());
    }

    @Override
    public Map<String, GraphNode> solve() {
        for (GraphNode task : _freeTaskList) {

        }
        return null;
    }





    private void initialiseFreeTasks() {
        for (GraphNode task : _jGraph.vertexSet()) {
            if (_graph.getGraph().inDegreeOf(task) == 0) {
                _freeTaskList.add(task);
                _freeTaskMapping.put(task.getId(), true);
            } else {
                _freeTaskMapping.put(task.getId(), false);
            }
        }
    }

    private int maxComputationalTime() {
        int sum = 0;
        for(GraphNode task : _jGraph.vertexSet()) {
            sum += task.getWeight();
        }
        return sum / _numProcTask;
    }

}
