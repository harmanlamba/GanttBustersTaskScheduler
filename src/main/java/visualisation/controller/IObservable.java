package visualisation.controller;

import algorithm.idastarbase.State;
import graph.Graph;
import graph.GraphNode;

import java.util.Map;

public interface IObservable {

    void add(IObserver e);
    void remove(IObserver e);
    void notifyObservers();
    State getBestFState();
    Graph getAlgorithmGraph();



}
