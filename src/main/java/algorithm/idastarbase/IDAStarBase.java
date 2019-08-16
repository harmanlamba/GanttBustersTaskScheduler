package algorithm.idastarbase;

import algorithm.Algorithm;
import graph.Graph;
import graph.GraphNode;
import javafx.concurrent.Task;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import java.util.*;

/**
 * IDAStarBase is a child class of Algorithm which solves the task scheduling problem optimally
 * on one processor.
 */
public class IDAStarBase extends Algorithm {

    private DirectedWeightedMultigraph<GraphNode, DefaultWeightedEdge> _jGraph;
    private Map<String, GraphNode> _freeTaskMapping;
    private List<GraphNode> _freeTaskList;
    private int _numberOfTasks;
    private int _lowerBound;
    private int _nextLowerBound;
    private boolean _solved;

    /**
     * Constructor for IDAStarBase to instantiate the object
     * @param graph is a graph of the network
     * @param numProcTask is the number of processors that the tasks needed to be scheduled onto
     * @param numProcParallel is the number of processors the algorithm should be working on
     */
    public IDAStarBase(Graph graph, int numProcTask, int numProcParallel) {
        super(graph, numProcTask, numProcParallel);
        _bestFState = null;
        _freeTaskMapping = new HashMap<>();
        _freeTaskList = new ArrayList<>();
        _jGraph = _graph.getGraph();
        _numberOfTasks = _jGraph.vertexSet().size();
        _solved = false;
        initialiseFreeTasks();
        initaliseBottomLevel();

        System.out.println(maxComputationalTime());
    }

    @Override
    public Map<String, GraphNode> solve() {
        for (GraphNode task : _freeTaskList) {
            _lowerBound = Math.max(maxComputationalTime(), task.getComputationalBottomLevel());
            while (!_solved) {
                _solved = idaRecursive(task, 1);
                _lowerBound = _nextLowerBound; //TODO: make this better please
            }
        }
        return null;
    }

    private boolean idaRecursive(GraphNode task, int processorNumber) {


        int potentialLowerBound = Math.max(maxComputationalTime(), task.getComputationalBottomLevel());

        if (potentialLowerBound > _lowerBound) {
            if (potentialLowerBound > _nextLowerBound) {
                    _nextLowerBound = potentialLowerBound;
            }
            return false;
        } else {

            return true; //TODO: fix this return
        }
    }

    private void initialiseFreeTasks() {
        for (GraphNode task : _jGraph.vertexSet()) {
            if (_graph.getGraph().inDegreeOf(task) == 0) {
                task.setFree(true);
                _freeTaskList.add(task);
            } else {
                task.setFree(false);
            }
            _freeTaskMapping.put(task.getId(), task);
        }
    }

    private int maxComputationalTime() {
        int sum = 0;
        for(GraphNode task : _jGraph.vertexSet()) {
            sum += task.getWeight();
        }
        return sum / _numProcTask;
    }

    private void initaliseBottomLevel() {
        for (GraphNode task : _jGraph.vertexSet()) {
            if (_jGraph.outDegreeOf(task) == 0) {
                calculateBottomLevel(task,0);
            }
        }
    }

    private void calculateBottomLevel(GraphNode task, int currentBottomLevel) {
        int potential = task.getWeight() + currentBottomLevel;

        if (potential >= task.getComputationalBottomLevel()) {
            task.setComputationalBottomLevel(potential);

            // For each parent, recursively calculates the computational bottom level
            for (DefaultWeightedEdge edge : _jGraph.incomingEdgesOf(task)) {
                GraphNode parent = _jGraph.getEdgeSource(edge);
                calculateBottomLevel(parent, potential);
            }
        }
    }

}
