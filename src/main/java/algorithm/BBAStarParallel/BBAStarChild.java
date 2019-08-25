package algorithm.BBAStarParallel;

import graph.Graph;
import graph.GraphNode;
import graph.Temp;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

public class BBAStarChild implements IBBAObservable, Runnable {

    private final static int NUMBER_OF_GRAPH_UPDATES = 9000;
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
                    int cost = getCostOfCurrentAllocation();
                    if (cost <= _upperBound) {
                        _upperBound = cost;
                        assignCurrentBestSolution();
                        if (_graphUpdates % NUMBER_OF_GRAPH_UPDATES == 0) {
                            notifyObserversOfSchedulingUpdateBBA();
                        }
                        _graphUpdates += 1;
                    }
                } else {
                    for (GraphNode freeTask : new ArrayList<>(_taskInfo.values())) {
                        if (freeTask.isFree()) {
                            for (int i = 0; i < _numProcTask; i++) {
                                recursive(freeTask, i);
                            }
                        }
                    }
                }
            }
            sanitise(processor);
            _depth -= 1;
        }
    }



    //Implemented
    private void initializeFreeTasks() {
        for (GraphNode task : new ArrayList<>(_graph.get_vertexMap().values())) {
            if (task.getParents().size() == 0) {
                _freeTaskList.add(task);
                task.setFree(true);
            } else {
                task.setFree(false);
            }
            _taskInfo.put(task.getId(), task);
        }
    }
    @Override public void run() {
        initializeFreeTasks();
        for (GraphNode initTask : new ArrayList<>(_taskInfo.values())) {
            if (initTask.isFree()) {
                recursive(initTask, 0);
            }
        }
        if (BBAStarParent._currentBestSolutions.get(_thread) != null) {
            BBAStarParent._currentBestCosts.put(_thread, _upperBound);
            System.out.println("Thread " + _thread + ": " + _upperBound);
            notifyObserversOfAlgorithmEndingBBA();
        }
    }
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
        } // TODO: change temp to an array of strings with two indices for speedup
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
    private void assignCurrentBestSolution() {
        Deque<GraphNode>[] copyOfStacks  = new ArrayDeque[_numProcTask];
        for (int i=0; i < _numProcTask; i++) {
            copyOfStacks[i] = new ArrayDeque<GraphNode>(_processorAllocation.get(i));
        }

        Map<String, GraphNode> solution = new HashMap<>();
        for (int i = 0; i < _numProcTask; i++) {
            while (!copyOfStacks[i].isEmpty()) {
                GraphNode task = copyOfStacks[i].pop();
                GraphNode copy = new GraphNode(task.getId(), task.getWeight(), task.getProcessor(), task.getStartTime(), task.isFree(), task.getParents(), task.getChildren());
                solution.put(copy.getId(), copy);
            }
        }
        BBAStarParent._currentBestSolutions.put(_thread, solution);
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
        for (GraphNode child : node.getChildren()) {
            _freeTaskList.remove(child);
            child.setFree(false);
            _taskInfo.put(child.getId(), child);
        }
    }

    @Override public void addBBA(IBBAObserver e) {
        _BBAObserverList.add(e);
    }
    @Override public void removeBBA(IBBAObserver e) {
        _BBAObserverList.remove(e);
    }
    @Override public void notifyObserversOfSchedulingUpdateBBA() {
        for (IBBAObserver observer : _BBAObserverList) {
            observer.updateScheduleInformationBBA(_thread);
        }
    }
    @Override public void notifyObserversOfAlgorithmEndingBBA() {
        for (IBBAObserver observer : _BBAObserverList) {
            observer.algorithmStoppedBBA(_thread, _upperBound);
        }
    }
}
