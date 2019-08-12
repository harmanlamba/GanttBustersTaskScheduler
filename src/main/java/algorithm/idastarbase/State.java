package algorithm.idastarbase;

import graph.Graph;
import graph.GraphNode;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import java.util.List;
import java.util.ArrayList;

public class State {
    private List<GraphNode> _assignedTasks;
    private Graph _graph;
    private Graph _remainingGraph;
    private List<GraphNode> _freeTasks;
    private int[] _processorMaxTime;

    public State(Graph graph, int numProcTask) {
        _graph = graph;
        _remainingGraph = graph;
        _assignedTasks = new ArrayList<>();
        _freeTasks = new ArrayList<>();
        _processorMaxTime = new int[numProcTask];
        updateFreeTasks();
    }

    public void addTask(GraphNode node) {
        _freeTasks.remove(node);
        _remainingGraph.getGraph().removeVertex(node);
        _assignedTasks.add(node);
        if(_processorMaxTime[node.getProcessor()] < node.getStartTime() + node.getWeight()) {
            _processorMaxTime[node.getProcessor()] = node.getStartTime() + node.getWeight();
        }
        updateFreeTasks();
    }

    //TODO: check the cast
    private void updateFreeTasks() {
        for(GraphNode node : ((DirectedWeightedMultigraph<GraphNode, DefaultWeightedEdge>) _remainingGraph.getGraph()).vertexSet()) {
            if(_remainingGraph.getGraph().inDegreeOf(node) == 0) {
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
