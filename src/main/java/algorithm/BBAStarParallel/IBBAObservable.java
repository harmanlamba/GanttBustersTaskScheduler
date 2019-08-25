package algorithm.BBAStarParallel;

import graph.GraphNode;
import visualisation.controller.IObserver;

import java.util.Map;

public interface IBBAObservable extends Runnable {
    /**
     * Used to add BBAStarParent as an observer to the child thread
     * @param e is the BBAStarParent instance
     */
    void addBBA(IBBAObserver e);

    /**
     * Used to remove BBAStarParent as an observer to the child thread
     * @param e is the BBAStarParent instance
     */
    void removeBBA(IBBAObserver e);

    /**
     * Method used by child thread to notify the parent BBAStarParent
     * instance of any update to the child thread
     */
    void notifyObserversOfSchedulingUpdateBBA();

    /**
     * Method used by child thread to notify the parent BBAStarParent
     * instance that a solution has been found and the algorithm has
     * ended
     */
    void notifyObserversOfAlgorithmEndingBBA();
}
