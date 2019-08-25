package visualisation.controller;

import graph.GraphNode;

import java.util.Map;

public interface IObserver {
    void updateScheduleInformation(int threadNumber, Map<String, GraphNode> map);
    void algorithmStopped(int bestScheduleCost);
    void updateIterationInformation(int threadNumber, int prunedBranches, int iterations, int lowerBound);
}