package algorithm;

import graph.GraphNode;
import graph.Graph;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.util.ArrayList;
import java.util.List;

public abstract class Algorithm {

    protected Graph _graph;
    protected List<GraphNode> _order = new ArrayList<>();

    public Algorithm(Graph g) {
        _graph = g;
    }

    public abstract Graph solve();

    public void getTopologicalOrdering() {
        TopologicalOrderIterator iterator = new TopologicalOrderIterator(_graph.getGraph());

        while(iterator.hasNext()) {
            GraphNode tempNode = (GraphNode) iterator.next();
            System.out.println(tempNode.getId());
            _order.add(tempNode);
        }
    }
}
