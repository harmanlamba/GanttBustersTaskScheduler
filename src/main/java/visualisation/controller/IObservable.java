package visualisation.controller;

import graph.Graph;
import graph.GraphNode;

import java.util.Map;

public interface IObservable {

    void add(IObserver e);
    void remove(IObserver e);
    void notifyObserversOfGraph();
    void notifyObserversOfTime();
    Graph getAlgorithmGraph();
    Map<String, GraphNode> getCurrentBestSolution();
}
