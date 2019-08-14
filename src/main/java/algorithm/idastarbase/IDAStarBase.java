package algorithm.idastarbase;

import algorithm.Algorithm;
import graph.Graph;
import graph.GraphNode;

import java.util.*;

/**
 * IDAStarBase is a child class of Algorithm which solves the task scheduling problem optimally
 * on one processor.
 */
public class IDAStarBase extends Algorithm {

    private State _bestFState;
    private int _numTasks;
    private int _upperBound;

    /**
     * Constructor for IDAStarBase to instantiate the object
     * @param g is a graph of the network
     * @param numProcTask is the number of processors that the tasks needed to be scheduled onto
     * @param numProcParallel is the number of processors the algorithm should be working on
     */
    public IDAStarBase(Graph g, int numProcTask, int numProcParallel) {
        super(g, numProcTask, numProcParallel);
        _bestFState = null;
        _numTasks = _graph.getGraph().vertexSet().size();
    }

    /**
     * Method that solves the problem optimally on one processor
     * @return A map of the nodes with their corresponding start times (string is the name of the
     * node and GraphNode contains all of the node information)
     */
    @Override
    public Map<String,GraphNode> solve() {
        State state = new State(_graph, _numProcTask);
        _upperBound = calcUpperBound();
        IDARecursion(null, -1, null, -1, state.getNumberOfFreeTasks(), 0, state);
        return _bestFState.getAssignedTasks();
    }

    /**
     * @return lower bound
     */
    private void IDARecursion(GraphNode cTask, int cProc, GraphNode pTask, int pProc, int numFreeTasks, int depth, State state) {
         if (numFreeTasks != 0) {
             for (int currentFreeTaskIndex = 0; currentFreeTaskIndex < numFreeTasks; currentFreeTaskIndex++) {
                 for (int j = 0; j < _numProcTask; j++) {
                     depth += 1;
                     //TODO: sanitise the schedule
                     state.sanitise(depth, _numProcTask);

                     numFreeTasks = state.getNumberOfFreeTasks();

                     //Schedule a picked task t from free(s) onto proc j. Add it to state s
                     GraphNode t = state.getGraphNodeFromFreeTasks(currentFreeTaskIndex);
                     int startTimeOfT = getTaskTime(state, t, j);
                     t.setStartTime(startTimeOfT);
                     t.setProcessor(j);
                     state.ScheduleTask(t);

                     pTask = cTask;
                     pProc = cProc;
                     cTask = t;
                     cProc = j;

                     int currentStateCost = state.getCost();
                     if (currentStateCost <= _upperBound && depth == _numTasks) {
                        _bestFState = state;
                        _upperBound = currentStateCost;
                     }
                     if (currentStateCost <= _upperBound && depth <= _numTasks) {
                         IDARecursion(cTask, cProc, pTask, pProc, numFreeTasks, depth, state);
                     }
                     depth -= 1;
                 }
             }
         }
        //return true; //TODO: fix what the return here actually should be. idk what it is meant to do.
    }

    private int getTaskTime(State state, GraphNode node, int processor) {
        Set<GraphNode> dependentNodeSet = _graph.getGraph().incomingEdgesOf(node);
        int maxValue = 0;

        List<Integer> maxTimeList = new ArrayList<>();
        for (GraphNode dependentNode : dependentNodeSet) {
            if (dependentNode.getProcessor() != node.getProcessor()) { //If nodes on different processors
                int communicationCost = (int) _graph.getGraph().getEdgeWeight(_graph.getGraph().getEdge(dependentNode, node));
                int processorCost = dependentNode.getStartTime() + dependentNode.getWeight();
                maxTimeList.add(processorCost + communicationCost);
            }
        }
        maxTimeList.add(state.getProcessorMaxTime(processor));
        return Collections.max(maxTimeList);
    }

    private int calcUpperBound() {
        Set<GraphNode> allNodes = _graph.getGraph().vertexSet();
        int total = 0;
        for (GraphNode node: allNodes) {
            total += node.getWeight();
        }
        return total;
    }
}
