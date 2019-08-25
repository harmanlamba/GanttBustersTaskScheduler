package visualisation.controller;

import graph.GraphNode;

import java.util.Map;

public interface IObserver {
    void updateScheduleInformation(int threadNumber, Map<String, GraphNode> map);
    void algorithmStopped(int thread, int bestScheduleCost);
    void updateIterationInformation(int threadNumber, int upperBound, int numIterations);
}