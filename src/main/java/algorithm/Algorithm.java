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
    private int _bestScheduleCost;

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

    public Map<String,GraphNode> solveAlgorithm(){
        Map<String, GraphNode> outputMap = solve();
        notifyObserversOfAlgorithmEnding();
        return outputMap;
    }

    public abstract Map<String,GraphNode> getCurrentBestSolution();

    @Override
    public void add(IObserver e) {
        _observerList.add(e);
    }

    @Override
    public void notifyObserversOfSchedulingUpdate() {
        for (IObserver observer : _observerList) {
            observer.updateScheduleInformation(getCurrentBestSolution());
        }
    }

    @Override
    public void notifyObserversOfAlgorithmEnding() {
        for (IObserver observer : _observerList) {
            observer.updateIterationInformation(_branchesPruned, _numberOfIterations, getCurrentLowerBound());
            observer.updateScheduleInformation(getCurrentBestSolution());
            observer.algorithmStopped(getBestScheduleCost());
        }
    }

    @Override
    public void notifyObserversOfIterationChange() {
        for (IObserver observer : _observerList) {
            observer.updateIterationInformation(_branchesPruned, _numberOfIterations, getCurrentLowerBound());
        }
    }

    protected abstract int getBestScheduleCost();

    protected abstract int getCurrentLowerBound();
}
