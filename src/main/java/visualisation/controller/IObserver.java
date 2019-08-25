package visualisation.controller;

import graph.GraphNode;

import java.util.Map;

public interface IObserver {

    /**
     * Used to let the observer know that updates have been made
     * to schedule information
     * @param map
     */
    void updateScheduleInformation(Map<String, GraphNode> map);

    /**
     * Used to let the observer know that the algorithm has stopped
     * as well as the cost of the optimal schedule
     * @param bestScheduleCost is the cost of the optimal schedule
     */
    void algorithmStopped(int bestScheduleCost);

    /**
     * Used to let the observer know statistics after an iteration
     * @param prunedBranches is the number of branches pruned thus far
     * @param iterations is the number of interations made thus far
     * @param lowerBound is the current lower bound being searched
     */
    void updateIterationInformation(int prunedBranches, int iterations, int lowerBound);
}

