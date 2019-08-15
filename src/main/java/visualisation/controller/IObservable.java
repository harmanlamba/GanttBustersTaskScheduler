package visualisation.controller;

import algorithm.idastarbase.State;
import graph.Graph;

public interface IObservable {

    void add(IObserver e);
    void remove(IObserver e);
    void notifyObservers();
    State getBestFState();
    Graph getAlgorithmGraph();


}
