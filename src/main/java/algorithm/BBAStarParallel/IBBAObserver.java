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
}
