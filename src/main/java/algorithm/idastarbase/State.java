package algorithm.idastarbase;

import graph.Graph;
import graph.GraphEdge;
import graph.GraphNode;
import org.apache.commons.lang3.ArrayUtils;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import java.util.*;

public class State {
    private Map<String, GraphNode> _assignedTasks;
    private Graph _graph;
    private Graph _remainingGraph;
    private Map<String, GraphNode> _freeTasks;     // Tasks that can be scheduled (meet dependencies)
    private int[] _processorMaxTime;        // Index i has the finishing time of the last task scheduled on processor i+1

    public State(Graph graph, int numProcTask) {
        _graph = graph;
        _remainingGraph = graph;
        _assignedTasks = new HashMap<>();
        _freeTasks = new HashMap<>();
        _processorMaxTime = new int[numProcTask];
        updateFreeTasks();
    }

    public void sanitise(int depth, int numProc) {
        for (int i = _assignedTasks.size() - 1; i > depth; i--) {
            // Add nodes back to the remaining graph
            Set<GraphEdge> edgeSet = _graph.getGraph().incomingEdgesOf(_assignedTasks.get(i));
            _remainingGraph.getGraph().addVertex(_assignedTasks.get(i));
            for (GraphEdge currentEdge : edgeSet) {
                DefaultWeightedEdge edge = (DefaultWeightedEdge) _remainingGraph.getGraph().addEdge(currentEdge.getEdgeFrom(), currentEdge.getEdgeTo());
                _remainingGraph.getGraph().setEdgeWeight(edge, currentEdge.getEdgeWeight());
            }

            // Removes the task from assignedTasks
            _assignedTasks.remove(_assignedTasks.get(i).getId());
        }

        _processorMaxTime = new int[numProc];
        for (GraphNode task : _assignedTasks.values()) {
            if (task.getStartTime() + task.getWeight() > _processorMaxTime[task.getProcessor()]) {
                _processorMaxTime[task.getProcessor()] = task.getStartTime() + task.getWeight();
            }
        }
    }


    public void ScheduleTask(GraphNode task) {
        _freeTasks.remove(task.getId());
        _remainingGraph.getGraph().removeVertex(task);
        _assignedTasks.put(task.getId(), task);
        if(_processorMaxTime[task.getProcessor()] < task.getStartTime() + task.getWeight()) {
            _processorMaxTime[task.getProcessor()] = task.getStartTime() + task.getWeight();
        }
        updateFreeTasks();
    }

    //TODO: check the cast

    /**
     * Update the list of free tasks by adding tasks not yet scheduled and their immediate predecessors
     * have already been scheduled, ie all dependencies are met.
     */
    private void updateFreeTasks() {
        for(GraphNode task : ((DirectedWeightedMultigraph<GraphNode, DefaultWeightedEdge>) _remainingGraph.getGraph()).vertexSet()) {
            // If a not-yet scheduled node has all of its dependencies met, it's a free node
            if(_remainingGraph.getGraph().inDegreeOf(task) == 0) {
                _freeTasks.put(task.getId(), task);
            }
        }
    }

    public int getNumberOfFreeTasks() {
        return _freeTasks.size();
    }


    public int getCost() {
//        int max = 0;
//        for(GraphNode node : _assignedTasks) {
//            if(node.getStartTime() + node.getWeight() > max) {
//                max = node.getStartTime() + node.getWeight();
//            }
//        }
//        return max;
        return Collections.max(Arrays.asList(ArrayUtils.toObject(_processorMaxTime)));
    }

    public Map<String, GraphNode> getAssignedTasks() {
        return _assignedTasks;
    }

    public int getProcessorMaxTime(int processor) {
        return _processorMaxTime[processor];
    }

    public GraphNode getGraphNodeFromFreeTasks(int index) {
        return new ArrayList<GraphNode>(_freeTasks.values()).get(index);
    }
}
