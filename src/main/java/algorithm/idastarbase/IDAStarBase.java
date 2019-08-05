package algorithm.idastarbase;

import algorithm.Algorithm;
import graph.Graph;
import graph.GraphNode;
import graph.OutputGraphNode;

import java.util.Map;
import java.util.Set;

public class IDAStarBase extends Algorithm {

    public IDAStarBase(Graph g, int numProcTask, int numProcParallel) {
        super(g, numProcTask, numProcParallel);
    }

    @Override
    public Map<GraphNode, OutputGraphNode> solve() {
        getTopologicalOrdering();
        return null;
    }

    private int calcWeightProcRatio() {
        Set<GraphNode> allNodes = _graph.getGraph().vertexSet();
        int total = 0;
        for (GraphNode node: allNodes) {
            total += node.getWeight();
        }
        return total/_numProcTask;
    }
}
