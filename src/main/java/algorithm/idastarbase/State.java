package algorithm.idastarbase;

import graph.Graph;
import graph.GraphNode;

import java.util.List;
import java.util.ArrayList;

public class State {
    private List<GraphNode> _assignedNodes;
    private Graph _graph;
    private List<GraphNode> _freeNodes;

    public State(Graph graph) {
        _graph = graph;
        _assignedNodes = new ArrayList<>();
        _freeNodes = new ArrayList<>();
        updateFreeNodes();
    }

    public void addNode(GraphNode node) {
        _graph.getGraph().removeVertex(node);
        _assignedNodes.add(node);
        updateFreeNodes();
    }

    //TODO: check the cast
    private void updateFreeNodes() {
        for(Object objectNode : _graph.getGraph().vertexSet()) {
            GraphNode node = (GraphNode) objectNode;
            if(_graph.getGraph().inDegreeOf(node) == 0) {
                _freeNodes.add(node);
            }
        }
    }

    public List<GraphNode> getFreeNodes() {
        return _freeNodes;
    }



}
