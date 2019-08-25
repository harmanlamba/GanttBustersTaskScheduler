package algorithm.BBAStarParallel;

public interface IBBAObserver {

    /**
     * Used to notify GUI that the algorithm has stopped
     * @param thread is the number of the thread making the update
     * @param bestScheduleCost is the cost of the best schedule
     */
    void algorithmStoppedBBA(int thread, int bestScheduleCost);

    /**
     * Used to notify GUI of statistic updates on the thread
     * @param thread is the number of the thread making the update
     */
    void updateScheduleInformationBBA(int thread);

    /**
     * Updates GUI of any updates made in the thread after every iteration
     * @param thread is the number of the thread making the update
     * @param prunedBranches is the number of branches pruned by the thread
     * @param iterations is the number of iterations the thread has completed
     * @param lowerBound is the current lower bound of the thread
     */
    void updateIterationInformationBBA(int thread, int prunedBranches, int iterations, int lowerBound);
}
