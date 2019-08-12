package algorithm.idastarbase;

import graph.Graph;
import graph.GraphNode;

import java.util.List;
import java.util.ArrayList;

public class State {
    private List<GraphNode> _assignedTasks;
    private Graph _graph;
    private List<GraphNode> _freeTasks;
    private int[] processorMaxTime;

    public State(Graph graph, int numProcTask) {
        _graph = graph;
        _assignedTasks = new ArrayList<>();
        _freeTasks = new ArrayList<>();
        processorMaxTime = new int[numProcTask];
        updateFreeTasks();
    }

    public void addTask(GraphNode node) {
        _graph.getGraph().removeVertex(node);
        _assignedTasks.add(node);
        updateFreeTasks();
    }

    //TODO: check the cast
    private void updateFreeTasks() {
        for(Object objectNode : _graph.getGraph().vertexSet()) {
            GraphNode node = (GraphNode) objectNode;
            if(_graph.getGraph().inDegreeOf(node) == 0) {
                _freeTasks.add(node);
            }
        }
    }

    public List<GraphNode> getFreeTasks() {
        return _freeTasks;
    }

    public int getNumberOfFreeTasks() {
        return _freeTasks.size();
    }

    public int[] getProcessorMaxTime() {
        return processorMaxTime;
    }

    public void setProcessorMaxTime(int processor, int startTime) {
        processorMaxTime[processor] = startTime;
    }
}
