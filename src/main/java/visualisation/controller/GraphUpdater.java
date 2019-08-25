package visualisation.controller;

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

/**
 * GraphUpdater class - provides methods for the controller to update the graph colour and properties according to the
 * algorithm's results.
 */
public class GraphUpdater extends Viewer {
    private final static String DEFAULT_NODE_STYLE =
            "text-alignment: center;\n"
                    + "\tstroke-mode: plain;\n"
                    + "\tstroke-color:white;\n"
                    + "\tfill-mode: plain;\n"
                    + "\tfill-color: black;\n"
                    + "\ttext-style: bold;\n"
                    + "\tsize: 40px, 40px;\n"
                    + "\ttext-size: 20px;\n"
                    + "\ttext-color: white;\n";

    //Graph helpers
    private Graph _graph;
    private SpriteManager _spriteManager;
    private ProcessorColourHelper _processorColourHelper;

    //Toggle properties
    private boolean _isShowSprite = false;
    private boolean _isShowFloppy = false;

    public GraphUpdater(Graph graph, ThreadingModel threadingModel, ProcessorColourHelper processorColourHelper) {
        super(graph, threadingModel);
        _graph=graph;
        _spriteManager= new SpriteManager(_graph);
        _processorColourHelper = processorColourHelper;
        initializeGraphProperties();
    }

    /**
     * Create nodes and edges using initial graph nodes and edges from list -> apply attributes and properties for style
     * and rendering.
     */
    private void initializeGraphProperties() {
        //Graph attributes
        _graph.addAttribute("ui.antialias");
        _graph.addAttribute("ui.quality");
        _graph.addAttribute("ui.stylesheet", "graph {\n" +
                "fill-mode: plain;\n" +
                "fill-color: #414141;\n" +
                "padding: 40px;\n" +
                "}");

        //Style list of nodes
        for (Node node : _graph) {
            node.setAttribute("ui.label", node.getId() + "");
            node.addAttribute("ui.style", DEFAULT_NODE_STYLE);
            _spriteManager.addSprite(node.getId());
        }

        //Style list of edges
        int edgeCount = _graph.getEdgeCount();
        for (int i = 0; i < edgeCount; i++) {
            Edge edge = _graph.getEdge(i);
            edge.addAttribute("ui.label",edge.getAttribute("weight") + "");
            edge.addAttribute("ui.style",
                    "fill-mode: plain; fill-color: rgba(255,255,255,100);\n"
                            + "\ttext-size: 17px; text-color: rgba(255,255,255,255);\n"
                            + "\ttext-alignment: along;\n");
        }

    }

    /**
     * Call updateGraph whenever the observer list is updated -> update all nodes processor assignment colour and sprite details.
     * If the node has no processor assignment, then give it an unassigned style (black with no sprite details).
     * @param graph
     */
    public void updateGraph(Graph graph) {
        //Create nodeslist from graphstream graph
        List<Node> nodesList = new ArrayList<>(graph.getNodeSet());
        for (Node node : nodesList) {
            if ((int) node.getAttribute("processor") != -1) {
                //Update nodes colours (for processor allocation)
                String processColour = _processorColourHelper.getProcessorColour(node.getAttribute("processor"));
                node.removeAttribute("ui.style"); //reset style
                node.addAttribute("ui.style",
                        "text-alignment: center;\n"
                                + "\tstroke-mode: plain;\n"
                                + "\tstroke-color:white;\n"
                                + "\tfill-mode: plain;\n"
                                + "\tfill-color:" + processColour + ";\n"
                                + "\tsize: 40px, 40px;\n"
                                + "\ttext-style: bold;\n"
                                + "\ttext-size: 20px;\n"
                                + "\ttext-color: white;\n");

                //Update nodes information using Sprites
                Sprite sprite = _spriteManager.getSprite(node.getId());
                sprite.addAttribute("ui.label",
                        "Start [P" + node.getAttribute("processor") + "]: " + node.getAttribute("startTime") + "s");
                sprite.addAttribute("ui.style",
                        "\ttext-alignment: under;\n"
                                + "\tfill-mode: plain; fill-color: rgba(0,0,0,0);\n"
                                + "\ttext-background-color: rgba(255,255,255,180);\n"
                                + "\ttext-background-mode: rounded-box;\n"
                                + "\tpadding: 3px;\n"
                                + "\ttext-size: 16px;\n");
                sprite.attachToNode(node.getId());

                //Handle sprite display
                if (!_isShowSprite) {
                    _spriteManager.getSprite(node.getId()).addAttribute("ui.hide");
                } else {
                    _spriteManager.getSprite(node.getId()).removeAttribute("ui.hide");
                }

            } else { //Reset style -> no processor assigned
                node.removeAttribute("ui.style");
                _spriteManager.getSprite(node.getId()).addAttribute("ui.hide");
                node.addAttribute("ui.style", DEFAULT_NODE_STYLE);
            }
        }

    }

    /**
     * Event to toggle sprite renderer from controller button in graph
     * @param graph
     */
    public void toggleSprites(Graph graph) {
        _isShowSprite = !_isShowSprite; //enable sprites in updateGraph
        List<Node> nodesList = new ArrayList<>(graph.getNodeSet());

        if (!_isShowSprite) {
            for (Node node : nodesList) {
                _spriteManager.getSprite(node.getId()).addAttribute("ui.hide");
            }
        } else {
            for (Node node : nodesList) {
                _spriteManager.getSprite(node.getId()).removeAttribute("ui.hide");
            }
        }
    }

    /**
     * toggleMouseManager - toggle between mouse manager property (allow to move graph or not)
     * @param viewPanel
     */
    public void toggleMouseManager(ViewPanel viewPanel) {
        _isShowFloppy = !_isShowFloppy;

        if (_isShowFloppy) {
            viewPanel.setMouseManager(null);
        } else {
            setMouseManager(viewPanel);
        }
    }

    /**
     * Disable mouse on click physics with graph {dangly mode}
     * @param viewPanel - takes panel which views graph
     */
    public void setMouseManager(ViewPanel viewPanel) {
        if (!_isShowFloppy) {
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
                    curElement = view.findNodeOrSpriteAt(event.getX(), event.getY());
                }
            };
            viewPanel.setMouseManager(manager);
        } else { //set view panel to default
            viewPanel.setMouseManager(null);
        }
    }

    public Boolean getSpriteFlag() {
        return _isShowSprite;
    }

    public Boolean getFloppyFlag() {
        return _isShowFloppy;
    }
}
