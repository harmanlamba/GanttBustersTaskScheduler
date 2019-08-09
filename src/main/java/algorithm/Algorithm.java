package algorithm;

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
public abstract class Algorithm {

    protected Graph _graph;
    protected List<GraphNode> _order = new ArrayList<>();
    protected final int _numProcTask;
    protected final int _numProcParallel;

    /**
     * An instance of Algorithm requires the input graph to run the algorithm on,
     * the number of processors specified for the tasks and for parallelisation
     * @param g
     * @param numProcTask
     * @param numProcParallel
     */
    public Algorithm(Graph g, int numProcTask, int numProcParallel) {
        _graph = g;
        _numProcTask = numProcTask;
        _numProcParallel = numProcParallel;
    }

    /**
     * @return Mapping of each node ID (in string) to the corresponding GraphNode
     */
    public abstract Map<String, GraphNode> solve();

    
    public void getTopologicalOrdering() {
        TopologicalOrderIterator iterator = new TopologicalOrderIterator(_graph.getGraph());

        while(iterator.hasNext()) {
            GraphNode tempNode = (GraphNode) iterator.next();
            _order.add(tempNode);
        }
    }
}
