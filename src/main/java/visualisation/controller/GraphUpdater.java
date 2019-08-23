package visualisation.controller;

import graph.GraphNode;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.util.DefaultMouseManager;
import org.graphstream.ui.view.util.MouseManager;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class GraphUpdater extends Viewer {
    private final static String DEFAULT_NODE_STYLE =
            "text-alignment: center;\n"
                    + "\tstroke-mode: plain;\n"
                    + "\tstroke-color:white;\n"
                    + "\tstroke-width: 4px;\n"
                    + "\tfill-mode: plain;\n"
                    + "\tfill-color: black;\n"
                    + "\ttext-style: bold;\n"
                    + "\tsize: 40px, 40px;\n"
                    + "\ttext-size: 20px;\n"
                    + "\ttext-color: white;\n";

    private Graph _graph;
    private SpriteManager _spriteManager;
    private ProcessorColourHelper _processorColourHelper;

    public GraphUpdater(Graph graph, ThreadingModel threadingModel, ProcessorColourHelper processorColourHelper) {
        super(graph, threadingModel);
        _graph=graph;
        _spriteManager= new SpriteManager(_graph);
        _processorColourHelper = processorColourHelper;
        initializeGraphProperties();
    }

    private void initializeGraphProperties() {
        //Graph attributes
        _graph.addAttribute("ui.antialias");
        _graph.addAttribute("ui.quality");

        //Style list of nodes
        for (Node node : _graph) {
            node.setAttribute("ui.label", node.getId() + "");
            node.addAttribute("ui.style", DEFAULT_NODE_STYLE);
        }

        //Style list of edges
        int edgeCount = _graph.getEdgeCount();
        for (int i = 0; i < edgeCount; i++) {
            Edge edge = _graph.getEdge(i);
            edge.addAttribute("ui.label",edge.getAttribute("weight") + "");
            edge.addAttribute("ui.style",
                    "fill-mode: plain; fill-color: rgba(0,0,0,100);\n"
                            + "\ttext-size: 17px; text-color: rgba(0,0,0,255);\n"
                            + "\ttext-alignment: along;\n");
        }
    }

    public void updateGraph(Graph graph) {
        //Create nodeslist from graphstream graph
        List<Node> nodesList = new ArrayList<>(graph.getNodeSet());

        //Update nodes processor assignment
        for (Node node : nodesList) {
            if ((int) node.getAttribute("processor") != -1) {
                //Update nodes colours (for processor allocation)
                String processColour = _processorColourHelper.getProcessorColour(node.getAttribute("processor"));
                node.removeAttribute("ui.style"); //reset style
                node.addAttribute("ui.style",
                        "text-alignment: center;\n"
                                + "\tstroke-mode: plain;\n"
                                + "\tstroke-color:white;\n"
                                + "\tstroke-width: 4px;\n"
                                + "\tfill-mode: plain;\n"
                                + "\tfill-color:" + processColour + ";\n"
                                + "\tsize: 40px, 40px;\n"
                                + "\ttext-style: bold;\n"
                                + "\ttext-size: 20px;\n"
                                + "\ttext-color: white;\n");

                //Update nodes information using Sprites
                Sprite sprite = _spriteManager.addSprite(node.getId());
                sprite.addAttribute("ui.label",
                        "Start time: " + node.getAttribute("startTime") + "s");
                sprite.addAttribute("ui.style",
                         "\ttext-alignment: under;\n"
                                + "\tfill-mode: plain; fill-color: rgba(0,0,0,0);\n"
                                + "\ttext-background-color: rgba(222,222,222,180);\n"
                                + "\ttext-background-mode: rounded-box;\n"
                                + "\tpadding: 3px;\n"
                                + "\ttext-font: monospace;\n"
                                + "\ttext-size: 15px;\n");
                sprite.attachToNode(node.getId());

            } else { //Reset style -> no processor assigned
                node.removeAttribute("ui.style");
                _spriteManager.removeSprite(node.getId());
                node.addAttribute("ui.style", DEFAULT_NODE_STYLE);
            }
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

    public void unsetMouseManager(ViewPanel viewPanel) {
        viewPanel.setMouseManager(null);
    }
}
