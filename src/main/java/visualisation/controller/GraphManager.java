package visualisation.controller;

import graph.GraphEdge;
import graph.GraphNode;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.spriteManager.SpriteManager;

import java.util.List;
import java.util.Map;

/**
 * GraphManager - manages the object of the GraphStream graph. Initially created given the Read classes inputs and
 * outputs.
 */
public class GraphManager {
    private SingleGraph _graphStream = new SingleGraph("graph");

    public GraphManager(Map<String, GraphNode> graphNodesMap, List<GraphEdge> graphEdgesList){
        createGraphStream(graphNodesMap,graphEdgesList);
    }

    /**
     * createGraphStream - create nodes and edges given properties according to list of nodes from Read class
     * @param graphNodesMap - map of nodes from Read
     * @param graphEdgesList - list of edges from Read
     */
    private void  createGraphStream(Map<String, GraphNode> graphNodesMap, List<GraphEdge> graphEdgesList){
        for(GraphNode node : graphNodesMap.values()){
             Node nodeGraphStream = _graphStream.addNode(node.getId());
             nodeGraphStream.addAttribute("weight",node.getWeight());
             nodeGraphStream.addAttribute("processor",node.getProcessor());
             nodeGraphStream.addAttribute("startTime",node.getStartTime());
        }

        int edgeID = 0;
        for(GraphEdge edge : graphEdgesList){
            edgeID++;
            Node tempParentNode = _graphStream.getNode(edge.getEdgeFrom().getId());
            Node tempChildNode = _graphStream.getNode(edge.getEdgeTo().getId());
            Edge edgeGraphStream = _graphStream.addEdge(Integer.toString(edgeID),tempParentNode, tempChildNode, true); //True = directed edge
            edgeGraphStream.addAttribute("weight", edge.getEdgeWeight());
        }
    }

    /**
     * Updates the graphstream graph nodes with processor and start time attributes
     * @param graphNodesMap - map of nodes from Read class
     */
    public void updateGraphStream(List<GraphNode> graphNodesMap) {
        for (GraphNode node : graphNodesMap) {
            _graphStream.getNode(node.getId()).setAttribute("processor", node.getProcessor());
            _graphStream.getNode(node.getId()).setAttribute("startTime", node.getStartTime());
        }
    }

    /**
     * getGraph - returns single graph for graphstream
     * @return - single graph Graph with nodes and edges already instantiated
     */
    public SingleGraph getGraph(){
        return _graphStream;
    }
}
