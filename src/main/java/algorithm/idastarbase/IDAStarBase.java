package algorithm.idastarbase;

import algorithm.Algorithm;
import graph.Graph;
import graph.GraphNode;

import java.util.Map;

public class IDAStarBase extends Algorithm {

    public IDAStarBase(Graph g, int numProcTask, int numProcParallel) {
        super(g, numProcTask, numProcParallel);
    }

    @Override
    public Map<String,GraphNode> solve() {
        getTopologicalOrdering();
        return null;
    }
}
