package visualisation.controller.timer;

public interface ITimerObservable {

    /**
     * Used to add observer to the Timer Instance
     * @param e is the observer Instance to be added
     */
    void add(ITimerObserver e);

    /**
     * Used to notify subscribed observers of updates
     * made on the timer
     */
    void notifyObserversOfTimerUpdate();

    /**
     * Method used to stop the timer
     */
    void stop();
}
