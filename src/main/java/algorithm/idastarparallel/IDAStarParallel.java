package algorithm.idastarparallel;

import algorithm.Algorithm;
import graph.Graph;
import graph.GraphNode;

import java.util.Map;

public class IDAStarParallel extends Algorithm {
    public IDAStarParallel(Graph g, int numProcTask, int numProcParallel) {
        super(g, numProcTask, numProcParallel);
    }

    @Override
    public Map<String, GraphNode> solve() {
        return null;
    }

    @Override
    public Map<String, GraphNode> getCurrentBestSolution() {
        return null;
    }

    @Override
    public int getBestScheduleCost() {
        return 0;
    }

    @Override
    public int getCurrentLowerBound() {
        return 0;
    }
}
