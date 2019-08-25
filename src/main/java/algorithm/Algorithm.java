package algorithm;

import visualisation.controller.IObservable;
import visualisation.controller.IObserver;
import graph.GraphNode;
import graph.Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The Algorithm abstract class defines the properties of the different types of algorithms
 * (e.g. Sequential, IDAStarBase). All algorithm classes extending this class may access the
 * input graph, nodes and node scheduling details.
 */
public abstract class Algorithm implements IObservable {

    protected Graph _graph;
    protected final int _numProcTask;
    protected final int _numProcParallel;
    protected List<IObserver> _observerList;
    protected int _branchesPruned;
    protected int _numberOfIterations;
    protected int _bestScheduleCost;
    protected int _threadNumber;

    /**
     * An instance of Algorithm requires the input graph to run the algorithm on,
     * the number of processors specified for the tasks and for parallelisation
     * @param g is a graph of the network
     * @param numProcTask is the number of processors that the tasks needed to be scheduled onto
     * @param numProcParallel is the number of processors the algorithm should be working on
     */
    public Algorithm(Graph g, int numProcTask, int numProcParallel) {
        _graph = g;
        _numProcTask = numProcTask;
        _numProcParallel = numProcParallel;
        _observerList = new ArrayList<>();
        _branchesPruned = 0;
        _numberOfIterations = 1;
        _bestScheduleCost = 0;
    }

    /**
     * Method that solves the problem optimally on one processor
     * @return A map of the nodes with their corresponding start time (string is the name of the
     * node and GraphNode contains all of the node information)
     */
    public abstract Map<String, GraphNode> solve();

    /**
     * Method that begins the algorithm to start solving the scheduling problem
     * @return
     */
    public Map<String,GraphNode> solveAlgorithm(){
        Map<String, GraphNode> outputMap = solve();
        System.out.println("\nDone");
        System.out.println("Best time: " + getBestScheduleCost());

        notifyObserversOfAlgorithmEnding(getSolutionThread());
        return outputMap;
    }

    /**
     * Getter method which retrieves the current best solution for the scheduling based on the algorithm applied
     * but may not necessarily be the most optimal solution (used for visualisation purposes)
     * @return a map of Strings mapping to GraphNodes containing the nodes' scheduling information
     */
    public abstract Map<String,GraphNode> getCurrentBestSolution();

    public abstract int getSolutionThread();

    @Override
    public void add(IObserver e) {
        _observerList.add(e);
    }

    @Override
    public void remove(IObserver e) { _observerList.remove(e); }

    @Override
    public void notifyObserversOfSchedulingUpdate(int threadNumber) {
        for (IObserver observer : _observerList) {
            observer.updateScheduleInformation(threadNumber, getCurrentBestSolution());
        }
    }

    @Override
    public void notifyObserversOfAlgorithmEnding(int threadNumber) {
        for (IObserver observer : _observerList) {
            observer.updateIterationInformation(threadNumber, getCurrentUpperBound(threadNumber), getNumberOfIterations(threadNumber));
            observer.updateScheduleInformation(threadNumber, getCurrentBestSolution());
            observer.algorithmStopped(threadNumber, getBestScheduleCost());
        }
    }

    @Override
    public void notifyObserversOfIterationChange(int threadNumber) {
        for (IObserver observer : _observerList) {
            observer.updateIterationInformation(threadNumber, getCurrentUpperBound(threadNumber), getNumberOfIterations(threadNumber));
        }
    }

    /**
     * Getter method for the cost of the optimal solution
     * @return returns the cost of the optimal solution
     */
    protected abstract int getBestScheduleCost();

    /**
     * Getter method for the current lower bound
     * @return returns the current lower bound to compare during algorithm iterations
     */
    protected abstract int getCurrentUpperBound(int threadNumber);

    protected abstract int getNumberOfIterations(int threadNumber);

    public int getMaximumPossibleCost() {
        int max = 0;
        for (GraphNode node : _graph.get_vertexMap().values()) {
            max += node.getWeight();
        }
        return max;
    }
}
