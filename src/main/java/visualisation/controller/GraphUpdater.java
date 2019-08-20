package visualisation.controller;

import graph.GraphNode;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.util.DefaultMouseManager;
import org.graphstream.ui.view.util.MouseManager;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class GraphUpdater extends Viewer {
    private Graph _graph;
    private SpriteManager _spriteManager;
    private List<String> _processorColours = new ArrayList<>();
    private List<String> _colours = new ArrayList<String>() {{
        //TODO: As there are only 6 colours here, it throws an error if I try to schedule the tasks onto 7 processors as there is not enough colours. A better solution needs to be thought of for this.
        add("#90EE90");
        add("#708090");
        add("#8A2BE2");
        add("#FFF8DC");
        add("#FF6347");
        add("#00CED1");
    }};

    public GraphUpdater(Graph graph, ThreadingModel threadingModel) {
        super(graph, threadingModel);
        _graph=graph;
        _spriteManager= new SpriteManager(_graph);
        initializeGraphProperties();
    }

    private void initializeGraphProperties() {
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

    private void updateNode(List<Node> nodesList) {
        //Update nodes processor assignment
        for (Node node : nodesList) {
            String processColour = getProcessorColour(node.getAttribute("processor"));
            node.removeAttribute("ui.style"); //reset style
            node.addAttribute("ui.style", "text-alignment: center;\n"
                    + "\tstroke-mode: plain; stroke-color:grey; stroke-width: 5px;\n"
                    + "\tfill-mode: plain; fill-color:" + processColour + ";\n"
                    + "\tsize: 30px, 30px;\n"
                    + "\ttext-size: 15px; text-color: white;\n");
        }
        //Update nodes additional properties
    }

    private String getProcessorColour(int processorIndex) {
        return _processorColours.get(processorIndex);
    }

    public void setProcessorColours(int processorCount) {
        //If only 1 processor count, then set to main node colour
        if (processorCount > 1) {
            for (int i = 0; i < processorCount; i++) {
                _processorColours.add(_colours.get(i));
            }
        } else {
            _processorColours.add(_colours.get(1));
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
