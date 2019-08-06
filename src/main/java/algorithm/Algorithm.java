package algorithm;

import graph.GraphNode;
import graph.Graph;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Algorithm {

    protected Graph _graph;
    protected List<GraphNode> _order = new ArrayList<>();
    protected final int _numProcTask;
    protected final int _numProcParallel;

    public Algorithm(Graph g, int numProcTask, int numProcParallel) {
        _graph = g;
        _numProcTask = numProcTask;
        _numProcParallel = numProcParallel;
    }

    public abstract Map<String, GraphNode> solve();

    public void getTopologicalOrdering() {
        TopologicalOrderIterator iterator = new TopologicalOrderIterator(_graph.getGraph());

        while(iterator.hasNext()) {
            GraphNode tempNode = (GraphNode) iterator.next();
            _order.add(tempNode);
        }
    }
}
