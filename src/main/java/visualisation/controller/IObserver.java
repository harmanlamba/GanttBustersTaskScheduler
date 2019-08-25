package visualisation.controller;

import graph.GraphNode;

import java.util.Map;

/**
 * IOObserver - observes algorithm and gets notified when there are any changes/better solution
 */
public interface IObserver {
    /**
     * updateScheduleInformation - updates the main controller method given observable
     * @param threadNumber - current thread on view
     * @param map - map of data
     */
    void updateScheduleInformation(int threadNumber, Map<String, GraphNode> map);

    /**
     * algorithmStopped - calls observers when algorithm is completed
     * @param thread - current thread on view
     * @param bestScheduleCost - current schedule cost value
     */
    void algorithmStopped(int thread, int bestScheduleCost);

    /**
     * updateIterationInformation - updates the amount of iterations algorithm made
     * @param threadNumber - current thread on view
     * @param upperBound - current upperbound of algorithm
     */
    void updateIterationInformation(int threadNumber, int upperBound);
}