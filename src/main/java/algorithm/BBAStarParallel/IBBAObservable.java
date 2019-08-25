package algorithm.BBAStarParallel;

import graph.GraphNode;
import visualisation.controller.IObserver;

import java.util.Map;

public interface IBBAObservable extends Runnable {
    void addBBA(IBBAObserver e);
    void removeBBA(IBBAObserver e);
    void notifyObserversOfSchedulingUpdateBBA();
    void notifyObserversOfAlgorithmEndingBBA();
}
