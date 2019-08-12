package algorithm.idastarbase;

import graph.Graph;
import graph.GraphNode;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import java.util.*;

public class State {
    private Map<String, GraphNode> _assignedTasks;
    private Graph _graph;
    private Graph _remainingGraph;
    private List<GraphNode> _freeTasks;
    private int[] _processorMaxTime;

    public State(Graph graph, int numProcTask) {
        _graph = graph;
        _remainingGraph = graph;
        _assignedTasks = new HashMap<>();
        _freeTasks = new ArrayList<>();
        _processorMaxTime = new int[numProcTask];
        updateFreeTasks();
    }

    public void ScheduleTask(GraphNode task) {
        _freeTasks.remove(task);
        _remainingGraph.getGraph().removeVertex(task);
        _assignedTasks.put(task.getId(), task);
        if(_processorMaxTime[task.getProcessor()] < task.getStartTime() + task.getWeight()) {
            _processorMaxTime[task.getProcessor()] = task.getStartTime() + task.getWeight();
        }
        updateFreeTasks();
    }

    //TODO: check the cast
    private void updateFreeTasks() {
        for(GraphNode task : ((DirectedWeightedMultigraph<GraphNode, DefaultWeightedEdge>) _remainingGraph.getGraph()).vertexSet()) {
            if(_remainingGraph.getGraph().inDegreeOf(task) == 0) {
                _freeTasks.add(task);
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
        return Arrays.stream(_processorMaxTime).max().getAsInt();
    }

    public Map<String, GraphNode> getAssignedTasks() {
        return _assignedTasks;
    }

    public int getProcessorMaxTime(int processor) {
        return _processorMaxTime[processor];
    }

    public GraphNode getGraphNodeFromFreeTasks(int index) {
        return _freeTasks.get(index);
    }
}
