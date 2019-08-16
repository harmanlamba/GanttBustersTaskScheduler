package algorithm.idastarbase;

import algorithm.Algorithm;
import graph.Graph;
import graph.GraphNode;
import org.apache.commons.lang3.ArrayUtils;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import java.util.*;

/**
 * IDAStarBase is a child class of Algorithm which solves the task scheduling problem optimally
 * on one processor.
 */
public class IDAStarBase extends Algorithm {

    private DirectedWeightedMultigraph<GraphNode, DefaultWeightedEdge> _jGraph;
    private Map<String, GraphNode> _taskInfo;
    private List<GraphNode> _freeTaskList;
    private List<GraphNode> _initialTaskList;
    private int _numberOfTasks;
    private int _lowerBound;
    private int _nextLowerBound;
    private boolean _solved;
    private Stack<GraphNode>[] _processorAllocations;

    /**
     * Constructor for IDAStarBase to instantiate the object
     * @param graph is a graph of the network
     * @param numProcTask is the number of processors that the tasks needed to be scheduled onto
     * @param numProcParallel is the number of processors the algorithm should be working on
     */
    public IDAStarBase(Graph graph, int numProcTask, int numProcParallel) {
        super(graph, numProcTask, numProcParallel);
        _bestFState = null;
        _taskInfo = new HashMap<>();
        _freeTaskList = new ArrayList<>();
        _initialTaskList = new ArrayList<>();
        _jGraph = _graph.getGraph();
        _numberOfTasks = _jGraph.vertexSet().size();
        _solved = false;
        _processorAllocations = new Stack[numProcTask];
        for (int i=0; i < numProcTask; i++) {
            _processorAllocations[i] = new Stack();
        }
        initialiseFreeTasks();
        initaliseBottomLevel();
    }

    @Override
    public Map<String, GraphNode> solve() {
        for (GraphNode task : _initialTaskList) {
            _lowerBound = Math.max(maxComputationalTime(), task.getComputationalBottomLevel());
            while (!_solved) {
                _solved = idaRecursive(task, 0);
                _lowerBound = _nextLowerBound; //TODO: make this better please
            }
        }
        return null;
    }

    private boolean idaRecursive(GraphNode task, int processorNumber) {
//        if (potentialLowerBound > _lowerBound) {
//            if (potentialLowerBound > _nextLowerBound) {
//                    _nextLowerBound = potentialLowerBound;
//            }
//            return false;
//        } else {
            _freeTaskList.remove(task);
            task.setStartTime(getStartTime(task, processorNumber));
            task.setProcessor(processorNumber);
            task.setFree(false);
            _taskInfo.put(task.getId(), task);
            _processorAllocations[processorNumber].push(task);
            updateFreeTasks(task);

            if (task.getWeight() + task.getStartTime() > _lowerBound) {
                return false;
            }

            if (_jGraph.outDegreeOf(task) == 0 && _freeTaskList.size() == 0) {
                return true;
            } else {
                for (GraphNode freeTask : _freeTaskList) {
                    for (int i = 0; i < _numProcTask; i++) {
                        _solved = idaRecursive(freeTask, i);
                        if (_solved) {
                            return true;
                        }
                    }
                }
                GraphNode node = _processorAllocations[processorNumber].pop();
                node.setFree(true);
                node.setStartTime(-1);
                node.setProcessor(-1);
                _freeTaskList.add(node);
                _taskInfo.put(node.getId(), node);

                for (GraphNode child : getChildren(node)) {
                    _freeTaskList.remove(child);
                    child.setFree(false);
                    _taskInfo.put(child.getId(), child);

                }
            }
            _nextLowerBound = _lowerBound + 1;
            return _solved;
//        }
    }

    private void updateFreeTasks(GraphNode task) {
        for (GraphNode child : getChildren(task)) {
            boolean childFree = true;
            for (GraphNode parent : getParents(child)) {
                if (parent.isFree()) {
                    childFree = false;
                    break; //TODO: test this
                }
            }
            if (childFree) {
                child.setFree(childFree);
                _taskInfo.put(child.getId(), child);
                _freeTaskList.add(child);
            }
        }
    }

    private Set<GraphNode> getParents(GraphNode task) {
        Set<GraphNode> parents = new HashSet<>();
        for (DefaultWeightedEdge edge : _jGraph.incomingEdgesOf(task)) {
            parents.add((GraphNode) _jGraph.getEdgeSource(edge));
        }
        return parents;
    }

    private Set<GraphNode> getChildren(GraphNode task) {
        Set<GraphNode> children = new HashSet<>();
        for (DefaultWeightedEdge edge : _jGraph.outgoingEdgesOf(task)) {
            children.add((GraphNode) _jGraph.getEdgeTarget(edge));
        }
        return children;
    }


    private int getStartTime(GraphNode task, int processorNumber) {
        int[] maxTimes = new int[_numProcTask];
        for (DefaultWeightedEdge edge : _jGraph.incomingEdgesOf(task)) {
            GraphNode parent = (GraphNode) _jGraph.getEdgeSource(edge);
            if (parent.getProcessor() != processorNumber) {
                int cost = parent.getStartTime() + parent.getWeight(); //parent finish time
                cost += (int) _jGraph.getEdgeWeight(edge); //communication cost
                if (cost > maxTimes[parent.getProcessor()]) {
                    maxTimes[parent.getProcessor()] = cost;
                }
            }
        }
        try {
            GraphNode topOfStack = _processorAllocations[processorNumber].peek();
            maxTimes[processorNumber] = topOfStack.getStartTime() + topOfStack.getWeight();
        } catch (EmptyStackException e) {
            maxTimes[processorNumber] = 0;
        }
        return Collections.max(Arrays.asList(ArrayUtils.toObject(maxTimes)));
    }

    private void initialiseFreeTasks() {
        for (GraphNode task : _jGraph.vertexSet()) {
            if (_graph.getGraph().inDegreeOf(task) == 0) {
                task.setFree(true);
                _initialTaskList.add(task);
                _freeTaskList.add(task);
            } else {
                task.setFree(false);
            }
            _taskInfo.put(task.getId(), task);
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
