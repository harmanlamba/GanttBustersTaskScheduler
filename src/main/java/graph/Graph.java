package graph;

import fileio.IIO;
import fileio.IO;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Graph {
    private Map<String, GraphNode> _vertexMap;
    private DirectedWeightedMultigraph<GraphNode, DefaultWeightedEdge> _jGraph = new DirectedWeightedMultigraph<>(DefaultWeightedEdge.class);

    public Graph(String path) {
        IIO io = new IO(path);
        io.readFile();

        _vertexMap = io.getNodeMap();
        for (GraphNode graphNode : new ArrayList<>(_vertexMap.values())) {
            _jGraph.addVertex(graphNode);
        }

        for (GraphEdge graphEdge : io.getEdgeList()) {
            DefaultWeightedEdge edge = _jGraph.addEdge(graphEdge.getEdgeFrom(), graphEdge.getedgeTo());
            _jGraph.setEdgeWeight(edge, graphEdge.getEdgeWeight());
        }
    }

    public void setup() {

        _vertexMap = new HashMap<>();
        // Adding nodes
        GraphNode node1 = new GraphNode("a", 2);
        _vertexMap.put(node1.getId(), node1);
        GraphNode node2 = new GraphNode("b", 3);
        _vertexMap.put(node2.getId(), node2);
        GraphNode node3 = new GraphNode("c", 3);
        _vertexMap.put(node3.getId(), node3);
        GraphNode node4 = new GraphNode("d", 2);
        _vertexMap.put(node4.getId(), node4);
        GraphNode node5 = new GraphNode("e", 4);
        _vertexMap.put(node4.getId(), node4);
        GraphNode node6 = new GraphNode("f", 1);
        _vertexMap.put(node4.getId(), node4);

        // Adding edges
        _jGraph = new DirectedWeightedMultigraph<>(DefaultWeightedEdge.class);
        _jGraph.addVertex(node1);
        _jGraph.addVertex(node2);
        _jGraph.addVertex(node3);
        _jGraph.addVertex(node4);
        _jGraph.addVertex(node5);
        _jGraph.addVertex(node6);

        DefaultWeightedEdge edge1 = _jGraph.addEdge(node1, node2);
        DefaultWeightedEdge edge2 = _jGraph.addEdge(node1, node3);
        DefaultWeightedEdge edge3 = _jGraph.addEdge(node2, node4);
        DefaultWeightedEdge edge4 = _jGraph.addEdge(node3, node4);
        DefaultWeightedEdge edge5 = _jGraph.addEdge(node1, node5);
        DefaultWeightedEdge edge6 = _jGraph.addEdge(node5, node4);
        DefaultWeightedEdge edge7 = _jGraph.addEdge(node4, node6);
        _jGraph.setEdgeWeight(edge1, 1);
        _jGraph.setEdgeWeight(edge2, 2);
        _jGraph.setEdgeWeight(edge3, 2);
        _jGraph.setEdgeWeight(edge4, 1);
        _jGraph.setEdgeWeight(edge5, 2);
        _jGraph.setEdgeWeight(edge6, 2);
        _jGraph.setEdgeWeight(edge7, 1);
    }

    public DirectedWeightedMultigraph getGraph() {
        return _jGraph;
    }

}
