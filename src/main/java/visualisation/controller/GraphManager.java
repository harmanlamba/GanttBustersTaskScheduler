package visualisation.controller;

import graph.GraphEdge;
import graph.GraphNode;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.spriteManager.SpriteManager;

import java.util.List;
import java.util.Map;

public class GraphManager {
    private SingleGraph _graphStream = new SingleGraph("graph");
    private SpriteManager _spriteManager;

    public GraphManager(Map<String, GraphNode> graphNodesMap, List<GraphEdge> graphEdgesList){
        createGraphStream(graphNodesMap,graphEdgesList);
    }

    private void  createGraphStream(Map<String, GraphNode> graphNodesMap, List<GraphEdge> graphEdgesList){

        for(GraphNode node : graphNodesMap.values()){
             Node nodeGraphStream = _graphStream.addNode(node.getId());
             nodeGraphStream.addAttribute("weight",node.getWeight());
             nodeGraphStream.addAttribute("processor","null");
             nodeGraphStream.addAttribute("startTime","null");
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

    public SingleGraph getGraph(){
        return _graphStream;
    }
}
