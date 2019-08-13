package ModelController;

import algorithm.idastarbase.State;

public interface IObservable {

    void add(IObserver e);
    void remove(IObserver e);
    void notifyObservers();
    State getBestFState();


}
