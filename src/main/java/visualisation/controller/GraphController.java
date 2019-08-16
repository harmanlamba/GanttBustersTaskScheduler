package visualisation.controller;

import fileio.IIO;
import graph.GraphEdge;
import graph.GraphNode;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.view.Viewer;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GraphController{
    private SingleGraph _graphStream;
    private SpriteManager _spriteManager;

    public GraphController(Map<String, GraphNode> graphNodesMap, List<GraphEdge> graphEdgesList){


        createGraphStream(graphNodesMap,graphEdgesList);
        intializeGraphSprite();

    }

    private void intializeGraphSprite() {
        //Graph attributes
        _graphStream.addAttribute("ui.antialias");
        _graphStream.addAttribute("ui.quality");

        //Style list of nodes
        for (Node node : _graphStream) {
            node.setAttribute("ui.label", node.getId() + "");
            node.addAttribute("ui.style", "text-alignment: center;\n"
                    + "\tstroke-mode: plain; stroke-color:grey; stroke-width: 5px;\n"
                    + "\tfill-mode: plain; fill-color: rgb(0,0,0);\n"
                    + "\tsize: 20px, 20px;\n"
                    + "\ttext-size: 15px; text-color: white;\n");
        }

        //Style list of edges
        int edgeCount = _graphStream.getEdgeCount();
        for (int i = 0; i < edgeCount; i++) {
            Edge edge = _graphStream.getEdge(i);
            edge.addAttribute("ui.style", "fill-mode: plain; fill-color: grey;\n"
                    + "\ttext-size: 15px; text-color: white;\n"
                    + "\ttext-alignment: along;\n");
            edge.addAttribute("ui.label",edge.getAttribute("weight") + "");
        }
    }

    private void  createGraphStream(Map<String, GraphNode> graphNodesMap, List<GraphEdge> graphEdgesList){
        _graphStream = new SingleGraph("AlgorithmGraph");;

        for(GraphNode node : graphNodesMap.values()){
         Node nodeGraphStream = _graphStream.addNode(node.getId());
         nodeGraphStream.addAttribute("weight",node.getWeight());
         nodeGraphStream.addAttribute("processor",null);
         nodeGraphStream.addAttribute("startTime",null);
        }

        int edgeID = 0;
        for(GraphEdge edge : graphEdgesList){
            edgeID++;
            Node tempParentNode = _graphStream.getNode(edge.getEdgeFrom().getId());
            Node tempChildNode = _graphStream.getNode(edge.getEdgeTo().getId());
            Edge edgeGraphStream = _graphStream.addEdge(Integer.toString(edgeID),tempParentNode, tempChildNode);
            edgeGraphStream.addAttribute("weight", edge.getEdgeWeight());
        }

    }

    public SingleGraph getGraph(){
        return _graphStream;
    }

}
