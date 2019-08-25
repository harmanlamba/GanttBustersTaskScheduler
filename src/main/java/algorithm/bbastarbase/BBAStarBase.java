package algorithm.bbastarbase;

import algorithm.Algorithm;
import graph.Graph;
import graph.GraphNode;
import graph.Temp;
import org.apache.commons.lang3.ArrayUtils;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import java.util.*;

/**
 * A child class of Algorithm which uses the BBA* algorithm to solve the task scheduling problem.
 * */
public class BBAStarBase extends Algorithm {

    private final static int NUMBER_OF_GRAPH_UPDATES = 100000;
    private final static int DEPTH_OF_STATES_TO_STORE = 10;
    private DirectedWeightedMultigraph<GraphNode, DefaultWeightedEdge> _jGraph; // Contains task dependency graph
    private Map<String, GraphNode> _taskInfo; // Map of String to GraphNode, the string being the ID of the node
    private List<GraphNode> _freeTaskList; // List of tasks that are ready to be scheduled
    private int _upperBound;
    private Map<String, GraphNode> _currentBestSolution;
    private List<Stack<GraphNode>> _processorAllocation;
    private int _depth; // The depth of the search tree
    private int _numTasks;
    private Set<Set<Stack<Temp>>> _previousStates;
    private int _graphUpdates;

    /**
     * Constructor for BBASTarBase to instantiate the object
     * @param graph is a graph of the network
     * @param numProcTask is the number of processors that the tasks needed to be scheduled onto
     * @param numProcParallel is the number of processors the algorithm should be working on
     */
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

    /**
     * A recursive method that completes a BBA* search on branches by scheduling free tasks until tasks have been scheduled.
     * Branch pruning techniques and cost functions have also been put in place to optimise performance.
     * @param task is the task to be scheduled in this iteration
     * @param processor is the processor number the task should be allocated on
     */
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

                // If all tasks have been scheduled
                if (_freeTaskList.isEmpty() && _depth == _numTasks) {
                    int cost = getCostOfCurrentAllocation();

                    // Update upper bound value
                    if (cost <= _upperBound) {
                        _upperBound = cost;
                        System.out.println(_upperBound);
                        assignCurrentBestSolution();

                        // Notify GUI of scheduling update
                        if (_graphUpdates % NUMBER_OF_GRAPH_UPDATES == 0) {
                            notifyObserversOfSchedulingUpdate(1);
                        }
                        _graphUpdates += 1;
                    }
                } else {
                    // Recursively call method on all tasks yet to be scheduled
                    for (GraphNode freeTask : new ArrayList<>(_taskInfo.values())) {
                        if (freeTask.isFree()) {
                            for (int i = 0; i < _numProcTask; i++) {
                                        recursive(freeTask, i);
                            }
                        }
                    }
                }
            }
            // Unschedule the tasks that got scheduled on a processor
            sanitise(processor);
            _depth -= 1;


        }
    }

    // TODO: change temp to an array of strings with two indices for speedup

    /**
     * @return the complete schedule where each stack populated by graph nodes represents
     * the tasks scheduled on a particular processor
     */
    private Set<Stack<Temp>> convertProessorAllocationsToTemp() {
        Set<Stack<Temp>> output = new HashSet<>();

        for (int i=0; i < _numProcTask; i++) {
            // For each processor, create a stack
            List<GraphNode> temp = new ArrayList<>(_processorAllocation.get(i));
            Stack<Temp> stack = new Stack<>();

            // Populate stack with GraphNodes scheduled on that processor
            for (GraphNode task : temp) {
                stack.push(new Temp(task.getId(), task.getStartTime()));
            }
            output.add(stack);
        }
        return output;
    }

    /**
     * @return the complete schedule where each task's ID is mapped to the
     * corresponding graph node object with the schedule information
     */
    @Override public Map<String, GraphNode> solve() {
        for (GraphNode initTask : new ArrayList<>(_taskInfo.values())) {
            if (initTask.isFree()) {
                notifyObserversOfIterationChange(1);
                recursive(initTask, 0);
            }
        }
        return _currentBestSolution;
    }

    /**
     * @return the sum of the weights of all tasks (nodes on graph), which is the
     * initial upper bound
     */
    private int initializeUpperBound() {
        int max = 0;
        for (GraphNode task : new ArrayList<>(_graph.get_vertexMap().values())) {
            max += task.getWeight();
        }
        return max;
    }

    @Override
    protected int getCurrentLowerBound() {
        //Do Not Implement
        return 0;
    }

    /**
     * Set each task to be free or not free and create a map of the task ID to the corresponding
     * GraphNode object
     */
    private void initializeFreeTasks() {
        for (GraphNode task : new ArrayList<>(_graph.get_vertexMap().values())) {
            // Set a task to free if it has no withstanding unscheduled dependencies
            if (_jGraph.inDegreeOf(task) == 0) {
                _freeTaskList.add(task);
                task.setFree(true);
            } else {
                task.setFree(false);
            }
            // Map each task to its ID
            _taskInfo.put(task.getId(), task);
        }
    }

    /**
     * Finds the earliest possible time a task can be scheduled on a specific processor
     * @param task
     * @param processor
     * @return the start time a task will be scheduled
     */
    private int getStartTime(GraphNode task, int processor) {
        int[] maxTimes = new int[_numProcTask];

        // For each immediate predecessor of the task
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

    /**
     * Get the parents of a particular task
     * @param task
     * @return set of parent nodes
     */
    private Set<GraphNode> getParents(GraphNode task) {
        Set<GraphNode> parents = new HashSet<>();
        for (DefaultWeightedEdge edge : _jGraph.incomingEdgesOf(task)) {
            parents.add((GraphNode) _jGraph.getEdgeSource(edge));
        }
        return parents;
    }

    /**
     * Get the children of a particular task
     * @param task
     * @return set of child nodes
     */
    private Set<GraphNode> getChildren(GraphNode task) {
        Set<GraphNode> children = new HashSet<>();
        for (DefaultWeightedEdge edge : _jGraph.outgoingEdgesOf(task)) {
            children.add((GraphNode) _jGraph.getEdgeTarget(edge));
        }
        return children;
    }

    /**
     * Update the list of free tasks and update the mapping with the child ready to be scheduled
     * @param task the task who's children will be updated in the list of free tasks if they are free
     */
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

    /**
     * Assigns the current best cost/finish time of a complete schedule, not yet guaranteed to be optimal
     */
    @Override public Map<String, GraphNode> getCurrentBestSolution() {
        return _currentBestSolution;
    }

    /**
     * @return the total cost of the current partial or complete solution, which is the
     * earliest possible starting time (without taking in to account dependencies of a task
     * to be scheduled)
     */
    private int getCostOfCurrentAllocation() {
        int max = 0;

        // For each processor
        for (Stack<GraphNode> stack : _processorAllocation) {
            try {
                GraphNode task = stack.peek();

                // Set the maximum possible starting time on that processor
                if (task.getStartTime() + task.getWeight() > max) {
                    max = task.getStartTime() + task.getWeight();
                }
            } catch (EmptyStackException e) {
                // Do nothing because this has cost of 0
            }
        }
        return max;
    }

    /**
     * Backtrack the scheduling done on a particular processor by unscheduling all nodes on that processor
     * @param processor the processor to be sanitised
     */
    private void sanitise(int processor) {
        // Unschedules node, adds it to free task list and set it to free
        GraphNode node = _processorAllocation.get(processor).pop();

        // Set task properties to default, unscheduled values
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

    /**
     * Set the current best complete schedule solution
     */
    private void assignCurrentBestSolution() {
        Deque<GraphNode>[] copyOfStacks  = new ArrayDeque[_numProcTask];
        for (int i=0; i < _numProcTask; i++) {
            copyOfStacks[i] = new ArrayDeque<GraphNode>(_processorAllocation.get(i));
        }
        // Create a map to build the solution
        _currentBestSolution = new HashMap<>();
        for (int i = 0; i < _numProcTask; i++) {
            while (!copyOfStacks[i].isEmpty()) {
                GraphNode task = copyOfStacks[i].pop();
                // Create a deep copy of the task
                GraphNode copy = new GraphNode(task.getId(), task.getWeight(), task.getProcessor(), task.getStartTime(), task.getParents(), task.getChildren());
                _currentBestSolution.put(copy.getId(), copy);
            }
        }
    }

    /**
     * @return the current best cost of a schedule
     */
    @Override
    protected int getBestScheduleCost() {
        return _upperBound;
    }

    /**
     * @return the number of processors which have no tasks scheduled on them yet
     */
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
