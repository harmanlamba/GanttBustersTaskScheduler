package visualisation.controller;

import org.graphstream.graph.Graph;
import org.graphstream.ui.view.Viewer;

public class GraphUpdater extends Viewer {
    public GraphUpdater(Graph graph, ThreadingModel threadingModel) {
        super(graph, threadingModel);
    }
}
