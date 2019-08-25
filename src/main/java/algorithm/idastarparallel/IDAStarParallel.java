package algorithm.idastarparallel;

import algorithm.Algorithm;
import graph.Graph;
import graph.GraphNode;

import java.util.Map;

public class IDAStarParallel extends Algorithm {

    /**
     * An instance of Algorithm requires the input graph to run the algorithm on,
     * the number of processors specified for the tasks and for parallelisation
     *
     * @param g               is a graph of the network
     * @param numProcTask     is the number of processors that the tasks needed to be scheduled onto
     * @param numProcParallel is the number of processors the algorithm should be working on
     */
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
    public int getSolutionThread() {
        return 0;
    }

    @Override
    public int getBestScheduleCost() {
        return 0;
    }

    @Override
    public int getCurrentUpperBound(int threadNumber) {
        return 0;
    }

    @Override
    protected int getNumberOfIterations(int threadNumber) {
        return 0;
    }

    @Override
    protected int getCurrentLowerBound() {
        return 0;
    }
}
