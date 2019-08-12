package algorithm.idastarbase;

import graph.Graph;
import graph.GraphNode;

import java.util.List;
import java.util.ArrayList;

public class State {
    private List<GraphNode> _assignedTasks;
    private Graph _graph;
    private List<GraphNode> _freeTasks;
    private int[] _processorMaxTime;

    public State(Graph graph, int numProcTask) {
        _graph = graph;
        _assignedTasks = new ArrayList<>();
        _freeTasks = new ArrayList<>();
        _processorMaxTime = new int[numProcTask];
        updateFreeTasks();
    }

    public void addTask(GraphNode node) {
        _graph.getGraph().removeVertex(node);
        _assignedTasks.add(node);
        if(_processorMaxTime[node.getProcessor()] < node.getStartTime() + node.getWeight()) {
            _processorMaxTime[node.getProcessor()] = node.getStartTime() + node.getWeight();
        }
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

    public int getCost() {
        int max = 0;
        for(GraphNode node : _assignedTasks) {
            if(node.getStartTime() + node.getWeight() > max) {
                max = node.getStartTime() + node.getWeight();
            }
        }
        return max;
    }

    public int getProcessorMaxTime(int processor) {
        return _processorMaxTime[processor];
    }
}
