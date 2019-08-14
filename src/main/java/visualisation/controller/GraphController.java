package visualisation.controller;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.view.Viewer;

public class GraphController extends Viewer {
    private Graph _graph;
    private SpriteManager _spriteManager;

    public GraphController(Graph graph, ThreadingModel threadingModel) {
        super(graph, threadingModel);
        _graph = graph;
        _spriteManager = new SpriteManager(_graph);

        intializeGraphSprite();
    }

    private void intializeGraphSprite() {
        //Graph attributes
        _graph.addAttribute("ui.antialias");
        _graph.addAttribute("ui.quality");

        //Style list of nodes
        for (Node node : _graph) {
            node.setAttribute("ui.label", node.getId() + "");
            node.addAttribute("ui.style", "text-alignment: center;\n"
                    + "\tstroke-mode: plain; stroke-color:grey; stroke-width: 5px;\n"
                    + "\tfill-mode: plain; fill-color: rgb(0,0,0);\n"
                    + "\tsize: 20px, 20px;\n"
                    + "\ttext-size: 15px; text-color: white;\n");
        }

        //Style list of edges
        int edgeCount = _graph.getEdgeCount();
        for (int i = 0; i < edgeCount; i++) {
            Edge edge = _graph.getEdge(i);
            edge.addAttribute("ui.style", "fill-mode: plain; fill-color: grey;\n"
                    + "\ttext-size: 15px; text-color: white;\n"
                    + "\ttext-alignment: along;\n");
            edge.addAttribute("ui.label",edge.getAttribute("weight") + "");
        }
    }
}
