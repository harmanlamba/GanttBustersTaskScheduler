package algorithm.idastarparallel;

import algorithm.Algorithm;
import graph.Graph;
import graph.GraphNode;

import java.util.ArrayList;
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
    public  Map<String, GraphNode> solve() {

        ArrayList<Thread> threadList = new ArrayList<Thread>(_numProcParallel);
        ArrayList<IDAStarParallelRecursive> solutionsList = new ArrayList<IDAStarParallelRecursive>(_numProcParallel);

        for (int i = 0; i < _numProcParallel; i++) {
            IDAStarParallelRecursive potentialSolution = new IDAStarParallelRecursive(_graph, _numProcTask, _numProcParallel, i);
            solutionsList.add(potentialSolution);
            threadList.add(new Thread(potentialSolution));
        }

        solutionsList.get(0).resetStaticVolatileFields();

        for (Thread thread: threadList) {
            thread.start();
        }

        // Joining all threads to wait on their completion
        for (Thread thread: threadList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Retrieve best schedule from the threads that have run
        _bestScheduleCost = solutionsList.get(0).getOverallBestFinishTime();
        return solutionsList.get(0).getOverallBestSchedule();
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
        return _bestScheduleCost;
    }

    @Override
    public int getCurrentUpperBound(int threadNumber) {
        return 0;
    }

    @Override
    protected int getNumberOfIterations(int threadNumber) {
        return 0;
    }
}
