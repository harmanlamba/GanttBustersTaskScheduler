package algorithm.BBAStarParallel;

import algorithm.Algorithm;
import graph.Graph;
import graph.GraphNode;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BBAStarParent extends Algorithm implements IBBAObserver {

    private List<Thread> _threadList;
    private List<IBBAObservable> _observableList;
    private int _upperBound;
    private int _lowerBound;
    public static volatile Map<Integer, Map<String, GraphNode>> _currentBestSolutions;
    public static volatile Map<Integer, Integer> _currentBestCosts;
    private int _bestSolutionIndex;
    private boolean _solved;

    public BBAStarParent(Graph g, int numProcTask, int numProcParallel) {
        super(g, numProcTask, numProcParallel);
        _threadList = new ArrayList<>();
        _observableList = new ArrayList<>();
        _upperBound = initializeUpperBound();
        _lowerBound = _upperBound / numProcTask;
        _currentBestSolutions = new HashMap<>();
        _currentBestCosts = new HashMap<>();
        _solved = false;
    }

    @Override
    public Map<String, GraphNode> solve() {
        int[] bounds = createBoundForThreads();

        for (int i=0; i < _numProcParallel - 1 ; i++) {
            IBBAObservable child = new BBAStarChild(_graph.deepCopyGraph(), _numProcTask, i, bounds[i]);
            child.addBBA(this);
            _observableList.add(child);
            Thread thread = new Thread(child);
            _threadList.add(thread);
            thread.start();
        }

        try {
            while(!_solved){
                Thread.sleep(100);
            }
            _threadList.get(_bestSolutionIndex).join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return _currentBestSolutions.get(_bestSolutionIndex);
    }


    //Complete
    private int initializeUpperBound() {
        int max = 0;
        for (GraphNode task : new ArrayList<>(_graph.get_vertexMap().values())) {
            max += task.getWeight();
        }
        return max;
    }
    private int[] createBoundForThreads() {
        int[] bounds = new int[_numProcParallel];
        for (int i=0; i < _numProcParallel - 1; i++) {
            bounds[i] = _upperBound - (i * (_upperBound - _lowerBound) / _numProcParallel);
        }
        return bounds;
    }

    @Override protected int getBestScheduleCost() {
        return _currentBestCosts.get(_bestSolutionIndex);
    }

    @Override protected int getCurrentLowerBound() {
        // Do not implement
        return 0;
    }

    @Override public void algorithmStoppedBBA(int thread, int bestScheduleCost) {
        _bestSolutionIndex = thread;
        _solved = true;
    }

    @Override public Map<String, GraphNode> getCurrentBestSolution() {
        return _currentBestSolutions.get(_bestSolutionIndex);
    }

    @Override public void updateIterationInformationBBA(int thread, int prunedBranches, int iterations, int lowerBound) {
        _branchesPruned = _branchesPruned;
        _numberOfIterations = iterations;
        Platform.runLater(() -> {
            notifyObserversOfIterationChange(thread);
        });

    }

    @Override public void updateScheduleInformationBBA(int thread) {
        Platform.runLater(() ->{
            notifyObserversOfSchedulingUpdate(thread);
        });

    }

}
