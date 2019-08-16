package graph;

import utility.Utility;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Graph - instantiates and holds JGraphT using IO's graph node map and graph edge lists.
 */
public class Graph {

    private DirectedWeightedMultigraph<GraphNode, DefaultWeightedEdge> _jGraph = (DirectedWeightedMultigraph<GraphNode, DefaultWeightedEdge>)
            Utility.GuardNull(new DirectedWeightedMultigraph<>(DefaultWeightedEdge.class)); //Hold retrievable jGraphT

    private Map<String, GraphNode> _vertexMap;
    private List<GraphEdge> _edgeList;


    public Graph(Map<String, GraphNode> vertexMap, List<GraphEdge> edgeList) {
        _vertexMap = vertexMap;
        _edgeList = edgeList;

        //Assign graph node map to JGraphT
        for (GraphNode graphNode : new ArrayList<>(vertexMap.values())) {
            _jGraph.addVertex(graphNode);
        }

        //Assign graph edge list to JGraphT
        for (GraphEdge graphEdge : edgeList) {
            DefaultWeightedEdge edge = _jGraph.addEdge(graphEdge.getEdgeFrom(), graphEdge.getEdgeTo());
            _jGraph.setEdgeWeight(edge, graphEdge.getEdgeWeight());
        }
    }

    /**
     * getGraph - getter for JGraphT DAG for algorithm's use
     * @return DirectedWeightedMultigraph from JGraphT
     */
    public DirectedWeightedMultigraph getGraph() {
        return _jGraph;
    }

    public Map<String, GraphNode> get_vertexMap() {
        return _vertexMap;
    }

    public List<GraphEdge> get_edgeList() {
        return _edgeList;
    }
}
