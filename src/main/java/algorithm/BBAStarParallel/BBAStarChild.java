package algorithm.BBAStarParallel;

import graph.Graph;
import graph.GraphNode;
import graph.Temp;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

public class BBAStarChild implements IBBAObservable, Runnable {

    private final static int NUMBER_OF_GRAPH_UPDATES = 1000000000;
    private final static int DEPTH_OF_STATES_TO_STORE = 10;
    private List<IBBAObserver> _BBAObserverList;
    private Graph _graph;
    private Map<String, GraphNode> _taskInfo; // Map of String to GraphNode, the string being the ID of the node
    private List<GraphNode> _freeTaskList; // List of tasks that are ready to be scheduled
    private int _upperBound;
    private List<Stack<GraphNode>> _processorAllocation;
    private int _depth;
    private int _numTasks;
    private int _numProcTask;
    private Set<Set<Stack<Temp>>> _previousStates;
    private int _thread;
    private int _graphUpdates;

    /**
     * Constructor for BBASTarChild to instantiate the object
     * @param graph of the network
     * @param numProcTask number of processors for the tasks to be scheduled on in the problem
     * @param thread the thread on which this instance of the algorithm is running on
     * @param bound the bound of the thread
     */
    public BBAStarChild(Graph graph, int numProcTask, int thread, int bound) {
        _BBAObserverList = new ArrayList<>();
        _graph = graph;
        _numProcTask = numProcTask;
        _numTasks = _graph.get_vertexMap().size();
        _thread = thread;
        _upperBound = bound;
        _taskInfo = new HashMap<>();
        _freeTaskList = new ArrayList<>();
        _depth = 0;
        _previousStates = new HashSet<>();
        _processorAllocation = new ArrayList<>();
        _graphUpdates = 0;
        for (int i = 0; i < numProcTask; i++) {
            _processorAllocation.add(new Stack<GraphNode>());
        }
    }

    /**
     * A recursive method that completes a BBA* search on branches by scheduling free tasks until tasks have been scheduled.
     * Branch pruning techniques and cost functions have also been put in place to optimise performance.
     * @param task is the task to be scheduled in this iteration
     * @param processor is the processor number the task should be allocated on
     */
    public void recursive(GraphNode task, int processor) {
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
                    // Update upper bound value
                    int cost = getCostOfCurrentAllocation();
                    if (cost <= _upperBound) {
                        _upperBound = cost;
                        assignCurrentBestSolution();

                        // Notify GUI of scheduling update
                        if (_graphUpdates % NUMBER_OF_GRAPH_UPDATES == 0) {
                            notifyObserversOfSchedulingUpdateBBA();
                        }
                        _graphUpdates += 1;
                    }
                } else {
                    for (GraphNode freeTask : new ArrayList<>(_taskInfo.values())) {
                        // Recursively call method on all tasks yet to be scheduled
                        if (freeTask.isFree()) {
                            for (int i = 0; i < _numProcTask; i++) {
                                recursive(freeTask, i);
                            }
                        }
                    }
                }
            }
            // Backtrack the schedulings on the processor if not yet solver
            sanitise(processor);
            _depth -= 1;
        }
    }

    /**
     * Set each task to be free or not free and create a map of the task ID to the correspondiong
     * GraphNode object
     */
    private void initializeFreeTasks() {
        for (GraphNode task : new ArrayList<>(_graph.get_vertexMap().values())) {
            // Set a task to free if it has no withstanding unscheduled dependencies
            if (task.getParents().size() == 0) {
                _freeTaskList.add(task);
                task.setFree(true);
            } else {
                task.setFree(false);
            }
            _taskInfo.put(task.getId(), task);
        }
    }

    /**
     * Runs the recursive algorithm on all free tasks
     */
    @Override
    public void run() {
        initializeFreeTasks();
        for (GraphNode initTask : new ArrayList<>(_taskInfo.values())) {
            if (initTask.isFree()) {
                recursive(initTask, 0);
            }
        }
        // Set the best cost path of the parent to the one found in this child
        if (BBAStarParent._currentBestSolutions.get(_thread) != null) {
            BBAStarParent._currentBestCosts.put(_thread, _upperBound);
            notifyObserversOfAlgorithmEndingBBA();
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
        for (GraphNode parent : task.getParents()) {
            if (parent.getProcessor() != processor) {
                int cost = parent.getStartTime() + parent.getWeight(); //parent finish time
                cost += (int) _graph.getEdgeWeight(parent, task); //communication cost
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
     * Update the list of free tasks and update the mapping with the child ready to be scheduled
     * @param task the task who's children will be updated in the list of free tasks if they are free
     */
    private void updateFreeTasks(GraphNode task) {
        for (GraphNode child : task.getChildren()) { // For every child node
            boolean childFree = true;
            for (GraphNode parent : child.getParents()) { // If the child node has a parent that hasn't been scheduled
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
     * @return the complete schedule where each stack populated by graph nodes represents
     * the tasks scheduled on a particular processor
     */
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

    /**
     * @return the total cost of the current partial or complete solution, which is the
     * earliest possible starting time (without taking in to account dependencies of a task
     * to be scheduled)
     */
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

    /**
     * Assigns the current best cost/finish time of a complete schedule, not yet guaranteed to be optimal
     */
    private void assignCurrentBestSolution() {
        Deque<GraphNode>[] copyOfStacks  = new ArrayDeque[_numProcTask];
        for (int i=0; i < _numProcTask; i++) {
            copyOfStacks[i] = new ArrayDeque<GraphNode>(_processorAllocation.get(i));
        }
        // Create the solution map
        Map<String, GraphNode> solution = new HashMap<>();
        for (int i = 0; i < _numProcTask; i++) {
            while (!copyOfStacks[i].isEmpty()) {
                GraphNode task = copyOfStacks[i].pop();
                GraphNode copy = new GraphNode(task.getId(), task.getWeight(), task.getProcessor(), task.getStartTime(), task.isFree(), task.getParents(), task.getChildren());
                solution.put(copy.getId(), copy);
            }
        }
        // Pass best solution to the parent
        BBAStarParent._currentBestSolutions.put(_thread, solution);
    }

    /**
     * Backtrack the scheduling done on a particular processor by unscheduling all nodes on that processor
     * @param processor the processor to be sanitised
     */
    private void sanitise(int processor) {
        // Unschedules node, adds it to free task list and set it to free
        GraphNode node = _processorAllocation.get(processor).pop();
        node.setFree(true);
        node.setStartTime(-1);
        node.setProcessor(-1);
        _freeTaskList.add(node);
        _taskInfo.put(node.getId(), node);

        // Sets children of removed node to not free
        for (GraphNode child : node.getChildren()) {
            _freeTaskList.remove(child);
            child.setFree(false);
            _taskInfo.put(child.getId(), child);
        }
    }

    /**
     * Add an observer of the child thread
     * @param observer the observer of the child
     */
    @Override
    public void addBBA(IBBAObserver observer) {
        _BBAObserverList.add(observer);
    }

    /**
     * Remove an observer of the child thread
     * @param observer the observer of the child
     */
    @Override
    public void removeBBA(IBBAObserver observer) {
        _BBAObserverList.remove(observer);
    }

    /**
     * Notify this child thread's observers of statitic updates on the thread
     */
    @Override
    public void notifyObserversOfSchedulingUpdateBBA() {
        for (IBBAObserver observer : _BBAObserverList) {
            observer.updateScheduleInformationBBA(_thread);
        }
    }

    /**
     * Notify the GUI that a specific child thread has stopped running.
     */
    @Override
    public void notifyObserversOfAlgorithmEndingBBA() {
        for (IBBAObserver observer : _BBAObserverList) {
            observer.algorithmStoppedBBA(_thread, _upperBound);
        }
    }
}
