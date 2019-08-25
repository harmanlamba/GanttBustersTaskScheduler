package algorithm.BBAStarParallel;

import graph.GraphNode;

import java.util.Map;

public interface IBBAObserver {
    void algorithmStoppedBBA(int thread, int bestScheduleCost);
    void updateScheduleInformationBBA(int thread);
}
