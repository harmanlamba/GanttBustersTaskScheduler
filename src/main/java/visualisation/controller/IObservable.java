package visualisation.controller;

public interface IObservable {

    void add(IObserver e);
    void remove(IObserver e);
    void notifyObserversOfSchedulingUpdate();
    void notifyObserversOfAlgorithmEnding();
    void notifyObserversOfIterationChange();
    int getMaximumPossibleCost();
}
