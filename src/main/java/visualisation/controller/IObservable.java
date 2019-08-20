package visualisation.controller;

import graph.Graph;
import graph.GraphNode;

import java.util.Map;

public interface IObservable {

    void add(IObserver e);
    void remove(IObserver e);
    void notifyObserversOfGraph();
    void notifyObserversOfTime();
    void notifyObserversOfStatistics();
    Map<String, GraphNode> getCurrentBestSolution();
    int getBestScheduleCost();
    int getBranchesPruned();
    int getNumberOfIterations();
    int getCurrentLowerBound();
}
