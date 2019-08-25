package algorithm.BBAStarParallel;

import algorithm.Algorithm;
import graph.Graph;
import graph.GraphNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A child class of Algorithm which uses the BBA* algorithm to solve the task scheduling problem with parallelisation
 * */
public class BBAStarParent extends Algorithm implements IBBAObserver {

    private List<Thread> _threadList;
    private List<IBBAObservable> _observableList;
    private int _upperBound;
    private int _lowerBound;
    public static volatile Map<Integer, Map<String, GraphNode>> _currentBestSolutions;
    public static volatile Map<Integer, Integer> _currentBestCosts;
    private int _bestSolutionIndex;
    private boolean _solved;

    /**
     * Constructor for BBASTarParallel to instantiate the object
     * @param g is a graph of the network
     * @param numProcTask is the number of processors that the tasks needed to be scheduled onto
     * @param numProcParallel is the number of processors the algorithm should be working on
     */
    public BBAStarParent(Graph g, int numProcTask, int numProcParallel) {
        super(g, numProcTask, numProcParallel);
        _threadList = new ArrayList<>();
        _observableList = new ArrayList<>();
        _upperBound = initializeUpperBound();
        _lowerBound = _upperBound / numProcTask;
        _currentBestSolutions = new HashMap<>();
        _currentBestCosts = new HashMap<>();
        _solved = false;
    }

    /**
     * The BBA* algorithm is paralellised by running a BBAStarChild algorithm on each processor in a new thread
     * When the best solution is found, the threads will terminate
     * @return the complete schedule solution
     */
    @Override
    public Map<String, GraphNode> solve() {
        int[] bounds = createBoundForThreads();

        // For each t
        for (int i=0; i < _numProcParallel; i++) {
            IBBAObservable child = new BBAStarChild(_graph.deepCopyGraph(), _numProcTask, i, bounds[i]);

            // Add this parent object to the child and make it an observer of parent
            child.addBBA(this);
            _observableList.add(child);

            // Create the child threa and start it
            Thread thread = new Thread(child);
            _threadList.add(thread);
            thread.start();
        }

        try {
            while(!_solved){
                Thread.sleep(100);
            }
            // When the best solution is found, end the thread
            _threadList.get(_bestSolutionIndex).join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return _currentBestSolutions.get(_bestSolutionIndex);
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

    /**
     * Get the lower bound of each thread
     * @return an array where each value entry represents the bound for a specific thread
     */
    private int[] createBoundForThreads() {
        int[] bounds = new int[_numProcParallel];
        for (int i=0; i < _numProcParallel; i++) {
            bounds[i] = _upperBound - (i * (_upperBound - _lowerBound) / _numProcParallel);
        }
        return bounds;
    }

    /**
     * @return the current best cost of a schedule
     */
    @Override
    protected int getBestScheduleCost() {
        return _currentBestCosts.get(_bestSolutionIndex);
    }

    @Override
    protected int getCurrentLowerBound() {
        // Do not implement
        return 0;
    }

    /**
     * Signals that the best schedule cost has been found on a thread, when called,
     * this thread will terminate
     * @param thread the thread with the best schedule cost
     * @param bestScheduleCost
     */
    @Override
    public void algorithmStoppedBBA(int thread, int bestScheduleCost) {
        _bestSolutionIndex = thread;
        _solved = true;
    }

    /**
     * @return the curent best complete schedule solution
     */
    @Override
    public Map<String, GraphNode> getCurrentBestSolution() {
        return _currentBestSolutions.get(_bestSolutionIndex);
    }

    /**
     * Update the GUI with current iteration statistics
     * @param thread the thread which is being updated with statistics
     * @param prunedBranches the number of branches pruned
     * @param iterations the number of iterations (depth)
     * @param lowerBound the current lower bound
     */
    @Override
    public void updateIterationInformationBBA(int thread, int prunedBranches, int iterations, int lowerBound) {
        _branchesPruned = _branchesPruned;
        _numberOfIterations = iterations;
        notifyObserversOfIterationChange(thread);
    }

    /**
     * Update the GUI with the scheduling update of a thread
     * @param thread the thread which is being updated woth statistics
     */
    @Override
    public void updateScheduleInformationBBA(int thread) {
        notifyObserversOfSchedulingUpdate(thread);
    }

}
