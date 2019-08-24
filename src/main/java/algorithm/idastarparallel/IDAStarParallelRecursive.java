package algorithm.idastarparallel;

import algorithm.Algorithm;
import graph.Graph;
import graph.GraphNode;
import org.apache.commons.lang3.ArrayUtils;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

import static utility.GraphUtility.getChildrenAdjacencyMap;
import static utility.GraphUtility.getParentAdjacencyMap;

/**
 * IDAStarBase is a child class of Algorithm which solves the task scheduling problem optimally
 * on one processor.
 */
public class IDAStarParallelRecursive extends Algorithm implements  Runnable {

    private static final int UPDATE_GRAPH_ITERATION_ROLLOVER = 1000000;
    private DirectedWeightedMultigraph<GraphNode, DefaultWeightedEdge> _jGraph; // Contains task dependency graph
    private Map<String, GraphNode> _taskInfo; // Map of String to GraphNode, the string being the ID of the node
    private List<GraphNode> _freeTaskList; // List of tasks that are ready to be scheduled
    private int _numberOfTasks; // Number of nodes/tasks in the graph
    private int _lowerBound; // Current lower bound to find solution against

    private static volatile PriorityBlockingQueue<Integer> _lowerBoundQueue = new PriorityBlockingQueue<>();
    private static volatile TreeSet<Integer> _checkedLowerBoundList = new TreeSet<>();
    private static volatile int _overallBestFinishTime = -1;
    private static volatile boolean _solved; // Represents whether the optimal solution has been found on any thread
    private static volatile Map<String, GraphNode> _overallBestSchedule = new HashMap<>();

    private int _maxCompTime; // Sum of node weights divided by number of processors to schedule tasks to
    private int _idle = 0; // Holds the idle time (unused processor time/wastage) in a particular scheduling iteration/partial state, used for cost function
    private int _threadBestFinishTime = -1; // Stores the cost of the optimal schedule
    private Stack<GraphNode>[] _processorAllocations; // Stack array holding tasks scheduled to the processors
    private int _updateGraphIteration;
    private Map<String, List<String>> _adjChildrenMap;
    private Map<String, List<String>> _adjParentMap;
    private Map<String, GraphNode> _jGraphNodeMap;
    private int _threadNum;


    /**
     * Constructor for IDAStarBase to instantiate the object
     * @param graph is a graph of the network
     * @param numProcTask is the number of processors that the tasks needed to be scheduled onto
     * @param numProcParallel is the number of processors the algorithm should be working on
     */
    public IDAStarParallelRecursive(Graph graph, int numProcTask, int numProcParallel, int threadNum) {
        super(graph, numProcTask, numProcParallel);
        _threadNum = threadNum;
        _taskInfo = new HashMap<>();
        _freeTaskList = new ArrayList<>();
        _jGraph = _graph.getGraph();
        _jGraphNodeMap = new HashMap<>();
        _numberOfTasks = _jGraph.vertexSet().size();
        _solved = false;
        _processorAllocations = new Stack[numProcTask];
        for (int i=0; i < numProcTask; i++) {
            _processorAllocations[i] = new Stack();
        }
        initialiseFreeTasks();
        _adjParentMap = getParentAdjacencyMap(_jGraph);
        _adjChildrenMap = getChildrenAdjacencyMap(_jGraph);
        initaliseBottomLevel();
        _maxCompTime = maxComputationalTime();
        _updateGraphIteration = 0;
    }

    /**
     * Called on the algorithm object to find the most optimal solution to the scheduling problem
     * @return returns a mapping of the Node ID to the GraphNode object containing its scheduling information
     */
    @Override
    public Map<String, GraphNode> solve() {
        for (GraphNode task : _taskInfo.values()) { // For every task that is ready to be scheduled
            if (task.isFree()) {
                _lowerBound = Math.max(maxComputationalTime(), task.getComputationalBottomLevel()) + _threadNum;
                _checkedLowerBoundList.add(_lowerBound);
                while (!_solved) { // If the optimal solution has not already been found
                    _numberOfIterations += 1;
                    notifyObserversOfIterationChange();
                    idaRecursive(task, 0); // Schedules the task

                    // a thread should only try to get a new _lowerBound if it isn't solved
                    while (!_solved) {
                        try {
                            _lowerBound = _lowerBoundQueue.take();
                            if (!_checkedLowerBoundList.contains(_lowerBound))
                                break;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    _checkedLowerBoundList.add(_lowerBound);

                    notifyObserversOfSchedulingUpdate(); //TODO: This line of code perhaps needs to be put in a better place. This is the periodic update to the GUI. Someone please figure out a good place to put this
                }
            }
        }
        return convertProcessorAllocationsToMap();
    }

    /**
     * Getter method which retrieves the current best solution for the scheduling based on the algorithm applied
     * but may not necessarily be the most optimal solution (used for visualisation purposes)
     * @return a map of Strings mapping to GraphNodes containing the nodes' scheduling information
     */
    @Override
    public Map<String, GraphNode> getCurrentBestSolution() {
        return convertProcessorAllocationsToMap();
    }

    /**
     * Getter method for the cost/finish time of the optimal scheduling
     * @return returns an integer representing the optimal finish time
     */
    @Override
    public int getBestScheduleCost() {
        return _threadBestFinishTime;
    }

    /**
     * Getter method for the current lower bound in the IDA Star algorithm
     * @return returns an integer of the representing the current lower bound
     */
    @Override
    public int getCurrentLowerBound() {
        return _lowerBound;
    }

    /**
     * Method that converts the stack array containing tasks scheduled onto processors into a node id, GraphNode mapping
     * that is required for output (visualisation)
     * @return
     */
    private Map<String, GraphNode> convertProcessorAllocationsToMap() {
        //TODO: deep copy the _processorAllocations
        Deque<GraphNode>[] copyOfStacks  = new ArrayDeque[_numProcTask];
        for (int i=0; i < _numProcTask; i++) {
            copyOfStacks[i] = new ArrayDeque<GraphNode>(_processorAllocations[i]);
        }

        _threadBestFinishTime = 0;

        Map<String, GraphNode> optimal = new HashMap<>();
        for (int i = 0; i < _numProcTask; i++) {
            while (!copyOfStacks[i].isEmpty()) {
                GraphNode task = copyOfStacks[i].pop();
                optimal.put(task.getId(), task);
                if (task.getStartTime() + task.getWeight() > _threadBestFinishTime) {
                    _threadBestFinishTime = task.getStartTime() + task.getWeight();
                }
            }

        }
        return optimal;
    }

    /**
     * A recursive method that completes a DFS search on branches by scheduling free tasks until tasks have been scheduled.
     * Branch pruning techniques and cost functions have also been put in place to optimise performance.
     * @param task is the task to be scheduled in this iteration
     * @param processorNumber is the processor number the task should be allocated on
     * @return returns a boolean of whether the solution is optimal or not
     */
    private boolean idaRecursive(GraphNode task, int processorNumber) {


        if (_solved) {
            System.out.println("thread " + Thread.currentThread().getId() + " has stopped");
            return true;
        }

        // Cost function calculation
        int startTime =  getStartTime(task, processorNumber);

        // 1st cost function - Computational bottom level
        int h1 =  task.getComputationalBottomLevel() + startTime;

        int idleNow = 0;
        if (_processorAllocations[processorNumber].isEmpty()) {
            idleNow = startTime;
        } else {
            GraphNode lastNode =  _processorAllocations[processorNumber].peek();
            idleNow = startTime - (lastNode.getStartTime() + lastNode.getWeight());
        }

        // Holds the idle time of processors (time during which processor are unused) during the particular
        // scheduling/partial state
        _idle += idleNow;

        // 2nd cost function - Load balance
        int h2 = _maxCompTime + (_idle/_numProcTask);

        // Grab the maximum of cost functions
        int maxH = Math.max(h1, h2);

        // Sets the max cost function as the next lower bound if the next lower bound is unassigned or too high
        // but the max cost function must be greater than the current lower bound (as the lower bound only increases).
        if (maxH > _lowerBound) {
            if(!_lowerBoundQueue.contains(maxH)) {
                // add maximum cost to the list of lower bounds to check
                _lowerBoundQueue.add(maxH);
            }
            _idle -= idleNow; // Backtracking as iteration will now exit

            return false;

        } else {
            // As task is about to be assigned, it is removed from the free task list
            _freeTaskList.remove(task);

            // Sets all values of GraphNode as per the processor it will be scheduled to
            task.setStartTime(startTime);
            task.setProcessor(processorNumber);
            task.setFree(false);

            // Updates task mapping and pushes task onto the processor stack
            _taskInfo.put(task.getId(), task);
            _processorAllocations[processorNumber].push(task);

            // Children nodes have to be updated to determine if any have become ready to schedule after the current
            // had been scheduled
            updateFreeTasks(task);

            if (_adjChildrenMap.get(task.getId()).size() == 0 && _freeTaskList.size() == 0) {

                int currentFinishedTime = getStackMax();

                if (_overallBestFinishTime > currentFinishedTime || _overallBestFinishTime == -1) {
                    _overallBestFinishTime = currentFinishedTime;
                    setOverallBestSchedule();
                }

                if (_threadBestFinishTime > currentFinishedTime || _threadBestFinishTime == -1) {
                    _threadBestFinishTime = currentFinishedTime;
                    System.out.println("thread " + Thread.currentThread().getId() + " has finished with best time of " + _threadBestFinishTime);
                }
                _solved = true;
                return _solved; // If the solution is most optimal, end search
            } else {
                for (GraphNode freeTask : _taskInfo.values()) {
                    if (freeTask.isFree()) {
                        for (int i = 0; i < _numProcTask; i++) {
                            // Grabs the next free task that is ready to schedule and tries scheduling it on a processor
                            // The next few if statements remove redundant scheduling onto homogeneous processors
                            // that are all empty
                            if (_numProcTask > 2) { // If the total number of processors is greater than 2, then there may be homogeneous processors
                                int freeProc = getFreeProc();
                                if (freeProc > 1 && (i > (_numProcTask - freeProc))) {
                                    // Do nothing
                                    _branchesPruned += 1;
                                } else {
                                    idaRecursive(freeTask, i);
                                }
                            } else {
                                idaRecursive(freeTask, i);
                            }
                            if (_updateGraphIteration % UPDATE_GRAPH_ITERATION_ROLLOVER == 0) {
                                notifyObserversOfSchedulingUpdate();
                            }
                            _updateGraphIteration += 1;
                            if (_solved) {
                                break;
                            }
                        }
                        if (_solved) {
                            break;
                        }
                    }
                }
                if (!_solved) {
                    // If optimal solution is not found after all the schedulings, then backtrack
                    sanitise(processorNumber);
                }
            }
            _idle -= idleNow; // Backtracking
            return _solved;
        }
    }

    /**
     * Takes the recently scheduled task, and updates its children's availability status for scheduling
     * @param task - the task whose children must be checked as to whether they are free
     */
    private void updateFreeTasks(GraphNode task) {
        List<String> cIDs = _adjChildrenMap.get(task.getId());
        for (String cID : cIDs) { // For every child node
            GraphNode child = _taskInfo.get(cID);
            boolean childFree = true;
            List<String> cPIDs = _adjParentMap.get(cID);
            for (String cPID :cPIDs) { // If the child node has a parent that hasn't been scheduled
                GraphNode parent = _taskInfo.get(cPID);
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
     * Calculates the start time for the task to be assigned onto a specified processor
     * based on when its parent nodes have been scheduled, communication costs and the
     * finish time of the processor it wants to be scheduled on
     * @param task - the task to be scheduled
     * @param processorNumber - the processor the task is to be scheduled on
     * @return returns an integer representing its start time on the processor
     */
    private int getStartTime(GraphNode task, int processorNumber) {
        // Calculates the max finish time from all its parents (plus communication costs if the parent was
        // scheduled on a different processor).
        int[] maxTimes = new int[_numProcTask];
        GraphNode jGraphNode = _jGraphNodeMap.get(task.getId());
        for (DefaultWeightedEdge edge : _jGraph.incomingEdgesOf(jGraphNode)) {
            GraphNode parent = _taskInfo.get(((GraphNode) _jGraph.getEdgeSource(edge)).getId());
            if (parent.getProcessor() != processorNumber) {
//                if (parent.getProcessor() == -1) {
//                    System.out.println("STOP");
//                }
                int cost = parent.getStartTime() + parent.getWeight(); //parent finish time
                cost += (int) _jGraph.getEdgeWeight(edge); //communication cost
//                System.out.printf("maxTimes Length: " + maxTimes.length);
//                System.out.println("parent proc: " + parent.getProcessor());
                if (cost > maxTimes[parent.getProcessor()]) {
                    maxTimes[parent.getProcessor()] = cost;
                }
            }
        }
        // Checks the max finish time of the processor the task wants to be scheduled on
        // E.g. if a non-parent task was scheduled on the same processor after the parent task
        try {
            GraphNode topOfStack = _processorAllocations[processorNumber].peek();
            maxTimes[processorNumber] = topOfStack.getStartTime() + topOfStack.getWeight();
        } catch (EmptyStackException e) {
            maxTimes[processorNumber] = 0;
        }
        // Returns the earliest time the task can be scheduled on the specified processor
        return Collections.max(Arrays.asList(ArrayUtils.toObject(maxTimes)));
    }

    /**
     * Initialiser method that adds all tasks to free tasks list and the task mapping
     */
    private void initialiseFreeTasks() {
        for (GraphNode task : _jGraph.vertexSet()) {
            GraphNode temp = new GraphNode(task.getId(), task.getWeight());
            if (_graph.getGraph().inDegreeOf(task) == 0) {
                temp.setFree(true);
                _freeTaskList.add(temp);
            } else {
                temp.setFree(false);
            }
            _taskInfo.put(temp.getId(), temp);
            _jGraphNodeMap.put(temp.getId(), task);
        }
    }

    /**
     * Calculates a part of one of the load balance cost function (ratio of node weight to processor)
     */
    private int maxComputationalTime() {
        int sum = 0;
        for(GraphNode task : _taskInfo.values()) {
            sum += task.getWeight();
        }
        return sum / _numProcTask;
    }

    /**
     * Calculates bottom level (cost function) of all nodes, starting from the leaves of DAG
     * and recursively calculating up the DAG
     */
    private void initaliseBottomLevel() {
        for (GraphNode task : _taskInfo.values()) {
            if (_adjChildrenMap.get(task.getId()).size() == 0) { // Calculate bottom level for a leaf
                calculateBottomLevel(task,0);
            }
        }
    }

    /**
     * Recursive function used to calculate computational bottom level
     * @param task - task to calculate bottom level for
     * @param currentBottomLevel - the current bottom level cost of the task
     */
    private void calculateBottomLevel(GraphNode task, int currentBottomLevel) {
        int potential = task.getWeight() + currentBottomLevel;

        if (potential >= task.getComputationalBottomLevel()) {
            task.setComputationalBottomLevel(potential);

            // For each parent, recursively calculates the computational bottom level
            for (String pID : _adjParentMap.get(task.getId())) {
                GraphNode parent = _taskInfo.get(pID);
                calculateBottomLevel(parent, potential);
            }
        }
    }

    /**
     * Retrieves the maximum finish time for already scheduled tasks out of all the processors
     * @return returns an int with the latest finish time
     */
    private int getStackMax() {
        int max = 0;
        for (int i = 0; i < _numProcTask; i++) {
            try {
                GraphNode node = _processorAllocations[i].peek();
                if (node.getWeight() + node.getStartTime() > max) {
                    max = node.getWeight() + node.getStartTime();
                }
            } catch (EmptyStackException e) {
                // Does not need to be handled as it means no tasks have been scheduled on the processor
            }
        }
        return max;
    }

    /**
     * Backtracking method which unschedules a task and updates its children so that they can no longer be scheduled
     * @param processorNumber - the processor number to remove the latest scheduled task from
     */
    private void sanitise(int processorNumber) {
        // Unschedules node, adds it to free task list and set it to free
        GraphNode node = _processorAllocations[processorNumber].pop();
        node.setFree(true);
        node.setStartTime(-1);
        node.setProcessor(-1);
        _freeTaskList.add(node);
        _taskInfo.put(node.getId(), node);
        // Sets children of removed node to not free
        List<String> cIDs = _adjChildrenMap.get(node.getId());
        for (String ID : cIDs) { // For every child node
            GraphNode child = _taskInfo.get(ID);
            _freeTaskList.remove(child);
            child.setFree(false);
            _taskInfo.put(child.getId(), child);
        }
    }

    private void setOverallBestSchedule() {
        _overallBestSchedule = new HashMap<>();
        for (GraphNode node: _taskInfo.values()) {
            GraphNode newNode = new GraphNode(node.getId(), node.getWeight(), node.getProcessor(), node.getStartTime());
            _overallBestSchedule.put(newNode.getId(), newNode);
        }
    }

    /**
     * Checks the stack array and returns the number of processors that have no tasks scheduled onto them
     * @return returns an integer of the number of empty processors
     */
    private int getFreeProc() {
        int free = 0;
        for (int i = 0; i < _numProcTask; i++) {
            if (_processorAllocations[i].empty()) {
                free++;
            }
        }
        return free;
    }

    public int getOverallBestFinishTime() {
        return _overallBestFinishTime;
    }

    public Map<String, GraphNode> getOverallBestSchedule() {
        return _overallBestSchedule;
    }

    public void resetStaticVolatileFields() {
        _solved = false;
        _lowerBoundQueue = new PriorityBlockingQueue<>();
        _checkedLowerBoundList = new TreeSet<>();
        _overallBestFinishTime = -1;
        _overallBestSchedule = new HashMap<>();
    }

    @Override
    public void run() {
        solve();
    }
}