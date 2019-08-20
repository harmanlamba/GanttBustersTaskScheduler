package algorithm;

import visualisation.controller.IObservable;
import visualisation.controller.IObserver;
import graph.GraphNode;
import graph.Graph;
import org.jgrapht.traverse.TopologicalOrderIterator;

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
    protected List<GraphNode> _order = new ArrayList<>();   // The topological order of the graph
    protected final int _numProcTask;
    protected final int _numProcParallel;
    protected List<IObserver> _observerList = new ArrayList<>();

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
    }

    /**
     * Method that solves the problem optimally on one processor
     * @return A map of the nodes with their corresponding start time (string is the name of the
     * node and GraphNode contains all of the node information)
     */
    public abstract Map<String, GraphNode> solve();

    public Map<String,GraphNode> solveAlgorithm(){
        Map<String, GraphNode> outputMap = solve();
        notifyObserversOfTime();
        return outputMap;
    }

    /**
     * Sets the topological order of the graph
     */
    public void getTopologicalOrdering() {
        TopologicalOrderIterator iterator = new TopologicalOrderIterator(_graph.getGraph());

        while(iterator.hasNext()) {
            GraphNode tempNode = (GraphNode) iterator.next();
            _order.add(tempNode);
        }
    }

    /**
     * Getter method for the topologically ordered graph nodes
     * @return topologically ordered graph nodes in a List of GraphNode
     */
    public List<GraphNode> getOrder() {
        return _order;
    }

    /**
     * Getter method for the number of processors to schedule tasks to as specified by the input
     * @return the number of processors to schedule tasks to as specified by the input
     */
    public int getNumProcTask() {
        return _numProcTask;
    }

    /**
     * Getter method for the number of processors to run the algorithm on as specified by the input
     * @return the number of processors to run the algorithm on as specified by the input
     */
    public int getNumProcParallel() {
        return _numProcParallel;
    }

    public abstract Map<String,GraphNode> getCurrentBestSolution();


    public Graph getAlgorithmGraph(){
        return _graph;
    }



    //IObservable Overrides
    @Override
    public void add(IObserver e) {
        _observerList.add(e);
    }

    @Override
    public void remove(IObserver e) {
        _observerList.remove(e);
    }

    @Override
    public void notifyObserversOfSchedulingUpdate() { //TODO: should this contain some sort of input
        for (IObserver observer : _observerList) {
            observer.updateGraph();
            observer.updateTable();
        }
    }

    @Override
    public void notifyObserversOfTime() {
        for (IObserver observer : _observerList) {
            observer.stopTimer();
        }
    }
}
