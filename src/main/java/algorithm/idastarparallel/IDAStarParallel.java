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
        System.out.println("YAY");


        // Retrieve best schedule from the threads that have run
        IDAStarParallelRecursive bestSchedule = null;
        int bestCost = solutionsList.get(0).getOverallBestFinishTime();
        System.out.println("Best cost = " + bestCost);
//        int tempCost = -1;
        for (int i = 0; i < _numProcParallel; i++) {
            IDAStarParallelRecursive currentPotentialSchedule = solutionsList.get(i);
            if (currentPotentialSchedule.getBestScheduleCost() == bestCost) {
                bestSchedule = currentPotentialSchedule;
//                tempCost = currentPotentialSchedule.getBestScheduleCost();
            }
        }
//        if (tempCost != bestCost) {
//            System.out.println("temp: " + tempCost + ", best: " + bestCost);
//        }
        if (bestSchedule != null) {
            return bestSchedule.getCurrentBestSolution();
        } else {
            System.out.println("returning null");
            return getCurrentBestSolution();
        }

    }

    @Override
    public Map<String, GraphNode> getCurrentBestSolution() {
        return null;
    }

    @Override
    protected int getBestScheduleCost() {
        return 0;
    }

    @Override
    protected int getCurrentLowerBound() {
        return 0;
    }
}
