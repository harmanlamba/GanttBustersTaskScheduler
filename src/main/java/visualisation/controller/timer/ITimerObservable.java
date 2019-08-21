package visualisation.controller.timer;


public interface ITimerObservable {
    void add(ITimerObserver e);
    void notifyObserversOfTimerUpdate();
    void stop();
}
