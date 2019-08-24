package algorithm.BBAStarParallel;

import graph.GraphNode;

import java.util.Map;

public interface IBBAObserver {
    void algorithmStopped(int thread, int bestScheduleCost);
    void updateScheduleInformation(int thread, Map<String, GraphNode> map);
    void updateIterationInformation(int thread, int prunedBranches, int iterations, int lowerBound);

}
