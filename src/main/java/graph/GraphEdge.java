package graph;

import java.util.Objects;

/**
 * GraphEdge - contains fields of directed edge properties (from node, to node) and the edge's weight
 */
public class GraphEdge {

    private GraphNode _edgeFrom; //Edge from node A
    private GraphNode _edgeTo; //Edge from node B
    private int _edgeWeight; //Weight of the edge

    public GraphEdge(GraphNode edgeFrom, GraphNode edgeTo, int edgeWeight){
        _edgeFrom=edgeFrom;
        _edgeTo=edgeTo;
        _edgeWeight=edgeWeight;
    }

    /**
     * getEdgeFrom - getter for from edge node for JGraphT implementation
     * @return - returns node A from edge
     */
    public GraphNode getEdgeFrom(){
        return _edgeFrom;
    }

    /**
     * getEdgeTo - getter for to edge node for JGraphT implementation
     * @return - returns node B to edge
     */
    public GraphNode getEdgeTo(){
        return _edgeTo;
    }

    /**
     * getEdgeWeight - getter for edge weight for JGraphT implementation
     * @return - returns edge's weight
     */
    public int getEdgeWeight(){
        return _edgeWeight;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraphEdge graphEdge = (GraphEdge) o;
        return _edgeWeight == graphEdge._edgeWeight &&
                _edgeFrom.equals(graphEdge._edgeFrom) &&
                _edgeTo.equals(graphEdge._edgeTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_edgeFrom, _edgeTo, _edgeWeight);
    }
}
