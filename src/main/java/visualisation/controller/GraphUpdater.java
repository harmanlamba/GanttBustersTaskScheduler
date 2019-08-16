package visualisation.controller;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.util.DefaultMouseManager;
import org.graphstream.ui.view.util.MouseManager;

import java.awt.event.MouseEvent;

public class GraphUpdater extends Viewer {
    private Graph _graph;
    private SpriteManager _spriteManager;

    public GraphUpdater(Graph graph, ThreadingModel threadingModel) {
        super(graph, threadingModel);
        _graph=graph;
        _spriteManager= new SpriteManager(_graph);
        intializeGraphProperties();
    }

    private void intializeGraphProperties() {
        //Graph attributes
        _graph.addAttribute("ui.antialias");
        _graph.addAttribute("ui.quality");

        //Style list of nodes
        for (Node node : _graph) {
            node.setAttribute("ui.label", node.getId() + "");
            node.addAttribute("ui.style", "text-alignment: center;\n"
                    + "\tstroke-mode: plain; stroke-color:grey; stroke-width: 5px;\n"
                    + "\tfill-mode: plain; fill-color: rgb(0,0,0);\n"
                    + "\tsize: 30px, 30px;\n"
                    + "\ttext-size: 15px; text-color: white;\n");
        }

        //Style list of edges
        int edgeCount = _graph.getEdgeCount();
        for (int i = 0; i < edgeCount; i++) {
            Edge edge = _graph.getEdge(i);
            edge.addAttribute("ui.style", "fill-mode: plain; fill-color: grey;\n"
                    + "\ttext-size: 15px; text-color: black;\n"
                    + "\ttext-alignment: along;\n");
            edge.addAttribute("ui.label",edge.getAttribute("weight") + "");
        }
    }

    public void setMouseManager(ViewPanel viewPanel) {
        MouseManager manager = new DefaultMouseManager() {

            @Override
            public void mouseDragged(MouseEvent event) {
            }

            @Override
            protected void mouseButtonPress(MouseEvent event) {
                super.mouseButtonPress(event);
            }

            @Override
            public void mouseClicked(MouseEvent event) {
                super.mouseClicked(event);
            }

            @Override
            public void mousePressed(MouseEvent event) {
                super.mousePressed(event);
                // if you need object of Node pressed, following code will help you, curElement is already defined at DefaultMouseManager.
                curElement = view.findNodeOrSpriteAt(event.getX(), event.getY());
//                if (curElement != null) {
//                    Node node = graph.getNode(curElement.getId());
//                    if(node != null) {
//                        System.out.println("Mouse pressed at node: " + node.getId());
//                    }
//                }
            }

        };

        viewPanel.setMouseManager(manager);
    }
}
