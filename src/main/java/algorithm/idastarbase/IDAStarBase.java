package algorithm.idastarbase;

import algorithm.Algorithm;
import graph.Graph;
import graph.GraphNode;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import java.util.*;

/**
 * IDAStarBase is a child class of Algorithm which solves the task scheduling problem optimally
 * on one processor.
 */
public class IDAStarBase extends Algorithm {

    private int _numTasks;
    private int _lowerBound;
    private GraphNode _cTask;
    private GraphNode _pTask;
    private int _cProc;
    private int _pProc;
    private int _depth;
    private State _state;
    private int _orderIndex;
    private List<State> _stateList = new ArrayList<>();
    private List<GraphNode> _stateGraphNodes = new ArrayList<>();

    /**
     * Constructor for IDAStarBase to instantiate the object
     * @param g is a graph of the network
     * @param numProcTask is the number of processors that the tasks needed to be scheduled onto
     * @param numProcParallel is the number of processors the algorithm should be working on
     */
    public IDAStarBase(Graph g, int numProcTask, int numProcParallel) {
        super(g, numProcTask, numProcParallel);
        _numTasks = _graph.getGraph().vertexSet().size();
        getTopologicalOrdering();
        _lowerBound = Math.max(calcWeightProcRatio(), calcCompBottomLevel());
        _cTask = null;
        _pTask = null;
        _cProc = -1;
        _pProc = -1;
        _depth = 0;
        _orderIndex = 0;
        _state = new State(_graph);
    }

    /**
     * Method that solves the problem optimally on one processor
     * @return A map of the nodes with their corresponding start times (string is the name of the
     * node and GraphNode contains all of the node information)
     */
    @Override
    public Map<String,GraphNode> solve() {
        while (true) {
            _lowerBound = IDARecursion();
        }
    }

    /**
     *
     * @return lower bound
     */
    private int IDARecursion() {
        boolean done = false;
//        if(_state.getFreePointer() != _order.size() - 1) {
//            GraphNode nextTask = _order.get(_state.getFreePointer());
//            _state.incrementFreePointer();

         List<GraphNode> nodeList = new ArrayList<>();

        return 1;
    }


    private int calcWeightProcRatio() {
        Set<GraphNode> allNodes = _graph.getGraph().vertexSet();
        int total = 0;
        for (GraphNode node: allNodes) {
            total += node.getWeight();
        }
        return total/_numProcTask;
    }

    private int calcCompBottomLevel() {
        DirectedWeightedMultigraph<GraphNode, DefaultWeightedEdge> graphCopy = _graph.getGraph();
        Set<DefaultWeightedEdge> edges = graphCopy.edgeSet();

        // Finding greatest weighted edge to invert edge weights in next for loop
        double maxEdgeLength = 0;
        for (DefaultWeightedEdge edge: edges) {
            if (graphCopy.getEdgeWeight(edge) > maxEdgeLength) {
                maxEdgeLength = graphCopy.getEdgeWeight(edge);
            }

        }

        // Inverting edge weights
        for (DefaultWeightedEdge edge: edges) {
            // double origWeight = graphCopy.getEdgeWeight(edge);
            graphCopy.setEdgeWeight(edge, maxEdgeLength - graphCopy.getEdgeWeight(edge));
            // System.out.println("Src:" + graphCopy.getEdgeSource(edge) + ", Dest:" + graphCopy.getEdgeTarget(edge) + ", OW: " + origWeight + ", IW: " + graphCopy.getEdgeWeight(edge));
        }

        Set<GraphNode> startNodes = new HashSet<>();
        Set<GraphNode> endNodes = new HashSet<>();

        for (GraphNode node: graphCopy.vertexSet()) {
            if (graphCopy.inDegreeOf(node) == 0) {
                startNodes.add(node);
            }
            if (graphCopy.outDegreeOf(node) == 0) {
                endNodes.add(node);
            }
        }

        DijkstraShortestPath<GraphNode, DefaultWeightedEdge> dijkstra = new DijkstraShortestPath(graphCopy);
        GraphPath<GraphNode, DefaultWeightedEdge> minPath = dijkstra.getPath(startNodes.iterator().next(), endNodes.iterator().next()); // But its actually the longest (critical) path
        double minWeight = -1;
        for (GraphNode start: startNodes) {
            for (GraphNode end: endNodes) {
                if (minWeight == -1 || dijkstra.getPath(start, end).getWeight() < minWeight) {
                    minWeight = dijkstra.getPath(start, end).getWeight();
                    minPath = dijkstra.getPath(start, end);
                }
            }
        }
        // System.out.println(minPath.getEdgeList().size());
        return minPath.getEdgeList().size();
    }
}
