package algorithm.bbastarbase;

import algorithm.Algorithm;
import graph.Graph;
import graph.GraphNode;
import graph.Temp;
import org.apache.commons.lang3.ArrayUtils;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import java.util.*;

public class BBAStarBase extends Algorithm {

    private final static int NUMBER_OF_GRAPH_UPDATES = 100000;
    private final static int DEPTH_OF_STATES_TO_STORE = 10;
    private DirectedWeightedMultigraph<GraphNode, DefaultWeightedEdge> _jGraph; // Contains task dependency graph
    private Map<String, GraphNode> _taskInfo; // Map of String to GraphNode, the string being the ID of the node
    private List<GraphNode> _freeTaskList; // List of tasks that are ready to be scheduled
    private int _upperBound;
    private Map<String, GraphNode> _currentBestSolution;
    private List<Stack<GraphNode>> _processorAllocation;
    private int _depth;
    private int _numTasks;
    private Set<Set<Stack<Temp>>> _previousStates;
    private int _graphUpdates;

    //Constructor
    public BBAStarBase(Graph graph, int numProcTask, int numProcParallel) {
        super(graph, numProcTask, numProcParallel);
        _jGraph = graph.getGraph();
        _taskInfo = new HashMap<>();
        _freeTaskList = new ArrayList<>();
        _upperBound = initializeUpperBound();
        _depth = 0;
        _numTasks = _graph.get_vertexMap().size();
        _previousStates = new HashSet<>();
        _processorAllocation = new ArrayList<>();
        for (int i = 0; i < numProcTask; i++) {
            _processorAllocation.add(new Stack<GraphNode>());
        }
        initializeFreeTasks();
    }

    private void recursive(GraphNode task, int processor) {
        int startTime = getStartTime(task, processor);
        if (startTime + task.getWeight() <= _upperBound) {
            _depth += 1;

            // Assigns task to processor
            _freeTaskList.remove(task);
            task.setStartTime(startTime);
            task.setProcessor(processor);
            task.setFree(false);
            _taskInfo.put(task.getId(), task);
            _processorAllocation.get(processor).push(task);
            updateFreeTasks(task);

            Set<Stack<Temp>> temp = new HashSet<>(convertProessorAllocationsToTemp());
            if (!_previousStates.contains(temp)) {
                if (_depth < DEPTH_OF_STATES_TO_STORE) {
                    _previousStates.add(temp);
                }

                if (_freeTaskList.isEmpty() && _depth == _numTasks) {
                    int cost = getCostOfCurrentAllocation();
                    if (cost <= _upperBound) {
                        _upperBound = cost;
                        assignCurrentBestSolution();
                        if (_graphUpdates % NUMBER_OF_GRAPH_UPDATES == 0) {
                            notifyObserversOfSchedulingUpdate(1);
                        }
                        _graphUpdates += 1;
                    }
                } else {
                    for (GraphNode freeTask : new ArrayList<>(_taskInfo.values())) {
                        if (freeTask.isFree()) {
                            for (int i = 0; i < _numProcTask; i++) {
//                                if (_numProcTask > 2) { // If the total number of processors is greater than 2, then there may be homogeneous processors
//                                    int freeProc = getFreeProc();
//                                    if (freeProc > 1 && (i > (_numProcTask - freeProc))) {
//                                        // Do nothing
//                                        _branchesPruned += 1;
//                                    } else {
                                        recursive(freeTask, i);
//                                    }
//                                } else {
//                                    recursive(freeTask, i);
//                                }
                            }
                        }
                    }
                }
            }
            sanitise(processor);
            _depth -= 1;


        }
    }

    // TODO: change temp to an array of strings with two indices for speedup
    private Set<Stack<Temp>> convertProessorAllocationsToTemp() {
        Set<Stack<Temp>> output = new HashSet<>();
        for (int i=0; i < _numProcTask; i++) {
            List<GraphNode> temp = new ArrayList<>(_processorAllocation.get(i));
            Stack<Temp> stack = new Stack<>();
            for (GraphNode task : temp) {
                stack.push(new Temp(task.getId(), task.getStartTime()));
            }
            output.add(stack);
        }
        return output;
    }


    //To Do

    //Implemented
    @Override public Map<String, GraphNode> solve() {
        for (GraphNode initTask : new ArrayList<>(_taskInfo.values())) {
            if (initTask.isFree()) {
                notifyObserversOfIterationChange(1);
                recursive(initTask, 0);
            }
        }
        return _currentBestSolution;
    }
    private int initializeUpperBound() {
        int max = 0;
        for (GraphNode task : new ArrayList<>(_graph.get_vertexMap().values())) {
            max += task.getWeight();
        }
        return max;
    }
    @Override protected int getCurrentLowerBound() {
        //Do Not Implement
        return 0;
    }
    private void initializeFreeTasks() {
        for (GraphNode task : new ArrayList<>(_graph.get_vertexMap().values())) {
            if (_jGraph.inDegreeOf(task) == 0) {
                _freeTaskList.add(task);
                task.setFree(true);
            } else {
                task.setFree(false);
            }
            _taskInfo.put(task.getId(), task);
        }
    }
    private int getStartTime(GraphNode task, int processor) {
        int[] maxTimes = new int[_numProcTask];
        for (DefaultWeightedEdge edge : _jGraph.incomingEdgesOf(task)) {
            GraphNode parent = (GraphNode) _jGraph.getEdgeSource(edge);
            if (parent.getProcessor() != processor) {
                int cost = parent.getStartTime() + parent.getWeight(); //parent finish time
                cost += (int) _jGraph.getEdgeWeight(edge); //communication cost
                if (cost > maxTimes[parent.getProcessor()]) {
                    maxTimes[parent.getProcessor()] = cost;
                }
            }
        }
        // Checks the max finish time of the processor the task wants to be scheduled on
        // E.g. if a non-parent task was scheduled on the same processor after the parent task
        try {
            GraphNode topOfStack = _processorAllocation.get(processor).peek();
            maxTimes[processor] = topOfStack.getStartTime() + topOfStack.getWeight();
        } catch (EmptyStackException e) {
            maxTimes[processor] = 0;
        }
        // Returns the earliest time the task can be scheduled on the specified processor
        return Collections.max(Arrays.asList(ArrayUtils.toObject(maxTimes)));
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
    private void updateFreeTasks(GraphNode task) {
        for (GraphNode child : getChildren(task)) { // For every child node
            boolean childFree = true;
            for (GraphNode parent : getParents(child)) { // If the child node has a parent that hasn't been scheduled
                if (parent.getProcessor() == -1) {
                    childFree = false;
                    break; // Do not update child
                }
            }
            if (childFree) { // Only reaches this point if all parents of child have been scheduled
                // Updates free task list and task mapping with the child is ready to be scheduled
                child.setFree(childFree);
                _taskInfo.put(child.getId(), child);
                _freeTaskList.add(child);
            }
        }
    }
    @Override public Map<String, GraphNode> getCurrentBestSolution() {
        return _currentBestSolution;
    }
    private int getCostOfCurrentAllocation() {
        int max = 0;
        for (Stack<GraphNode> stack : _processorAllocation) {
            try {
                GraphNode task = stack.peek();
                if (task.getStartTime() + task.getWeight() > max) {
                    max = task.getStartTime() + task.getWeight();
                }
            } catch (EmptyStackException e) {
                // Do nothing because this has cost of 0
            }
        }
        return max;
    }
    private void sanitise(int processor) {
        // Unschedules node, adds it to free task list and set it to free
        GraphNode node = _processorAllocation.get(processor).pop();
        node.setFree(true);
        node.setStartTime(-1);
        node.setProcessor(-1);
        _freeTaskList.add(node);
        _taskInfo.put(node.getId(), node);

        // Sets children of removed node to not free
        for (GraphNode child : getChildren(node)) {
            _freeTaskList.remove(child);
            child.setFree(false);
            _taskInfo.put(child.getId(), child);
        }
    }
    private void assignCurrentBestSolution() {
        Deque<GraphNode>[] copyOfStacks  = new ArrayDeque[_numProcTask];
        for (int i=0; i < _numProcTask; i++) {
            copyOfStacks[i] = new ArrayDeque<GraphNode>(_processorAllocation.get(i));
        }

        _currentBestSolution = new HashMap<>();
        for (int i = 0; i < _numProcTask; i++) {
            while (!copyOfStacks[i].isEmpty()) {
                GraphNode task = copyOfStacks[i].pop();
                GraphNode copy = new GraphNode(task.getId(), task.getWeight(), task.getProcessor(), task.getStartTime(), task.getParents(), task.getChildren());
                _currentBestSolution.put(copy.getId(), copy);
            }
        }
    }
    @Override protected int getBestScheduleCost() {
        return _upperBound;
    }
    private int getFreeProc() {
        int free = 0;
        for (int i = 0; i < _numProcTask; i++) {
            if (_processorAllocation.get(i).empty()) {
                free++;
            }
        }
        return free;
    }


}
