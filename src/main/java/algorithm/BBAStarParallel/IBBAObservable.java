package algorithm.BBAStarParallel;

import graph.GraphNode;
import visualisation.controller.IObserver;

import java.util.Map;

public interface IBBAObservable extends Runnable {
    void add(IBBAObserver e);
    void remove(IBBAObserver e);
    void notifyObserversOfSchedulingUpdate();
    void notifyObserversOfAlgorithmEnding();
    Map<String, GraphNode> getBestPath();
}
