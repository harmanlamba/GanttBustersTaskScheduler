package visualisation.controller;

/**
 * IObservable - notifies the observer of changes that are relevant
 */
public interface IObservable {

    /**
     * Used to add observer to the implementing class to listen
     * @param e is the instance of observer to be added
     */
    void add(IObserver e);

    /**
     * Used to remove observer listening to the implementing class
     * @param e is the instance of observer to be removed
     */
    void remove(IObserver e);

    /**
     * notifyObserversOfSchedulingUpdate - notifies observers of new schedule produced on thread view
     * @param threadNumber - current thread view
     */
    void notifyObserversOfSchedulingUpdate(int threadNumber);

    /**
     * notifyObserversOfAlgorithmEnding - notifies observers of when algorithm is finished
     * @param threadNumber - current thread view
     */
    void notifyObserversOfAlgorithmEnding(int threadNumber);

    /**
     * notifyObserversOfIterationChange - notifies observers of iteration change in algorithm
     * @param threadNumber - current thread view
     */
    void notifyObserversOfIterationChange(int threadNumber);

    /**
     * getMaximumPossibleCost - notify observers of getting current cost of path from algorithm
     */
    int getMaximumPossibleCost();
}
