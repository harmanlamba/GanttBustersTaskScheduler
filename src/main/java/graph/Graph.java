package graph;

import utility.Utility;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import java.util.ArrayList;
import java.util.HashMap;
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
            graphEdge.getEdgeFrom().addChild(graphEdge.getEdgeTo());
            graphEdge.getEdgeTo().addParent(graphEdge.getEdgeFrom());
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

    public Graph deepCopyGraph() {
        Map<String, GraphNode> vertexMapCopy = new HashMap<>();
        List<GraphEdge> edgeListCopy = new ArrayList<>();
        for (GraphNode node : _vertexMap.values()) {
            GraphNode nodeCopy = new GraphNode(node.getId(), node.getWeight());
            vertexMapCopy.put(nodeCopy.getId(), nodeCopy);
        }
        for (GraphEdge edge : _edgeList) {
            edgeListCopy.add(new GraphEdge(vertexMapCopy.get(edge.getEdgeFrom().getId()), vertexMapCopy.get(edge.getEdgeTo().getId()), edge.getEdgeWeight()));
        }
        return new Graph(vertexMapCopy, edgeListCopy);
    }

    public int getEdgeWeight(GraphNode edgeFrom, GraphNode edgeTo) {
        for (GraphEdge edge : _edgeList) {
            if (edge.getEdgeFrom() == edgeFrom && edge.getEdgeTo() == edgeTo) {
                return edge.getEdgeWeight();
            }
        }
        return -1;
    }

}
