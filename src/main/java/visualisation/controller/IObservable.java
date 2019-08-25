package visualisation.controller;

public interface IObservable {

    /**
     * Used to add observer to the implementing class
     * @param e is the instance of observer to be added
     */
    void add(IObserver e);

    /**
     * Used to remove observer to the implementing class
     * @param e is the instance of observer to be removed
     */
    void remove(IObserver e);

    /**
     * Used to notify subscribed observers of any scheduling
     * updates made
     */
    void notifyObserversOfSchedulingUpdate();

    /**
     * Used to notify subscribed observers that the algorithm
     * has completed
     */
    void notifyObserversOfAlgorithmEnding();

    /**
     * Used to notify subscribed observers of any changes made
     * after an iteration of the algorithm
     */
    void notifyObserversOfIterationChange();

    /**
     * Getter method for the maximum possible cost a.k.a.
     * the upper bound
     * @return an int representing the max possible cost
     */
    int getMaximumPossibleCost();
}
