package algorithm.BBAStarParallel;

import algorithm.Algorithm;
import graph.Graph;
import graph.GraphNode;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BBAStarParent extends Algorithm implements IBBAObserver {

    private List<Thread> _threadList;
    private List<IBBAObservable> _observableList;
    private int _upperBound;
    private int _lowerBound;
    private Map<Integer, Map<String, GraphNode>> _currentBestSolutions;
    private Map<Integer, Integer> _currentBestCosts;
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

        for (int i=0; i < _numProcParallel; i++) {
            //TODO: give deep copy of _graph
            IBBAObservable child = new BBAStarChild(_graph, _numProcTask, i, bounds[i]);
            child.add(this);
            _observableList.add(child);
            Thread thread = new Thread(child);
            _threadList.add(thread);
            thread.start();
        }

        try {
            synchronized(this){
                while(!_solved){
                    wait();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return _currentBestSolutions.get(_bestSolutionIndex);
    }


    //TODO: Implement these mofos
    @Override public void algorithmStopped(int thread, int bestScheduleCost) {
        _bestSolutionIndex = thread;
        _currentBestCosts.put(thread, bestScheduleCost);
        synchronized(this){
            notify();
        }
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
        for (int i=0; i < _numProcParallel; i++) {
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



    //TODO: fix these mofos
    @Override public Map<String, GraphNode> getCurrentBestSolution() {
        return _currentBestSolutions.get(0);
    }
    @Override public void updateIterationInformation(int thread, int prunedBranches, int iterations, int lowerBound) {
        _branchesPruned = _branchesPruned;
        _numberOfIterations = iterations;
        notifyObserversOfIterationChange();
    }
    @Override public void updateScheduleInformation(int thread, Map<String, GraphNode> map) {
        notifyObserversOfSchedulingUpdate();
    }

}
