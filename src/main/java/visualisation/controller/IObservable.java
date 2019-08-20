package visualisation.controller;

public interface IObservable {

    void add(IObserver e);
    void notifyObserversOfSchedulingUpdate();
    void notifyObserversOfAlgorithmEnding();
    void notifyObserversOfIterationChange();
}
