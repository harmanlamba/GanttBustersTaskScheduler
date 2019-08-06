package graph;

import fileio.IIO;
import fileio.IO;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Graph {
    private DirectedWeightedMultigraph<GraphNode, DefaultWeightedEdge> _jGraph = new DirectedWeightedMultigraph<>(DefaultWeightedEdge.class);

    public Graph(Map<String, GraphNode> vertexMap, List<GraphEdge> edgeList) {

        for (GraphNode graphNode : new ArrayList<>(vertexMap.values())) {
            _jGraph.addVertex(graphNode);
        }

        for (GraphEdge graphEdge : edgeList) {
            DefaultWeightedEdge edge = _jGraph.addEdge(graphEdge.getEdgeFrom(), graphEdge.getedgeTo());
            _jGraph.setEdgeWeight(edge, graphEdge.getEdgeWeight());
        }
    }

    public DirectedWeightedMultigraph getGraph() {
        return _jGraph;
    }
}
