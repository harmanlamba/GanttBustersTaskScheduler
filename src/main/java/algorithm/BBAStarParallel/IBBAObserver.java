package algorithm.BBAStarParallel;

import graph.GraphNode;

import java.util.Map;

public interface IBBAObserver {
    void algorithmStoppedBBA(int thread, int bestScheduleCost);
    void updateScheduleInformationBBA(int thread);
    void updateIterationInformationBBA(int thread, int prunedBranches, int iterations, int lowerBound);
}
