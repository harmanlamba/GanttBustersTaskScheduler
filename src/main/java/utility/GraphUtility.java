package utility;

import graph.GraphNode;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import java.util.*;

public class GraphUtility {
    public static Map<String, List<String>> getParentAdjacencyMap(DirectedWeightedMultigraph g) {
        Map<String, List<String>> result = new HashMap<>();
        Set<GraphNode> vertexSet = g.vertexSet();
        for (GraphNode vertex: vertexSet) {
            List<String> parents = new ArrayList<>();
            Set<DefaultWeightedEdge> edges = g.incomingEdgesOf(vertex);
            for (DefaultWeightedEdge edge: edges) {
                GraphNode parent = (GraphNode) g.getEdgeSource(edge);
                String pID = parent.getId();
                parents.add(pID);
            }
            String vID = vertex.getId();
            result.put(vID, parents);
        }
        return result;
    }

    public static Map<String, List<String>> getChildrenAdjacencyMap(DirectedWeightedMultigraph g) {
        Map<String, List<String>> result = new HashMap<>();
        Set<GraphNode> vertexSet = g.vertexSet();
        for (GraphNode vertex: vertexSet) {
            List<String> children = new ArrayList<>();
            Set<DefaultWeightedEdge> edges = g.outgoingEdgesOf(vertex);
            for (DefaultWeightedEdge edge: edges) {
                GraphNode child = (GraphNode) g.getEdgeTarget(edge);
                String cID = child.getId();
                children.add(cID);
            }
            String vID = vertex.getId();
            result.put(vID, children);
        }
        return result;
    }

}
