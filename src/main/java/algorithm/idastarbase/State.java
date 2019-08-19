package algorithm.idastarbase;

import graph.Graph;
import graph.GraphEdge;
import graph.GraphNode;
import org.apache.commons.lang3.ArrayUtils;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import java.util.*;

public class State {

    private Graph _graph;
    private Map<String, Boolean> _freeTaskMapping;
    private List<GraphNode> _freeTaskList;

    public State(Graph graph) {
        _graph = graph;
    }
}
