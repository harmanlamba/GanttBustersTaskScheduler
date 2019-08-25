package visualisation.controller;

public interface IObservable {

    void add(IObserver e);
    void remove(IObserver e);
    void notifyObserversOfSchedulingUpdate(int threadNumber);
    void notifyObserversOfAlgorithmEnding(int threadNumber);
    void notifyObserversOfIterationChange(int threadNumber);
    int getMaximumPossibleCost();
}
