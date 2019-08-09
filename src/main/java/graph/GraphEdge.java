package graph;

//TODO: Class comments + Method comments
public class GraphEdge {

    private GraphNode _edgeFrom;
    private GraphNode _edgeTo;
    private int _edgeWeight;

    public GraphEdge(GraphNode edgeFrom, GraphNode edgeTo, int edgeWeight){
        _edgeFrom=edgeFrom;
        _edgeTo=edgeTo;
        _edgeWeight=edgeWeight;
    }



    public GraphNode getEdgeFrom(){
        return _edgeFrom;
    }

    public GraphNode getedgeTo(){
        return _edgeTo;
    }

    public int getEdgeWeight(){
        return _edgeWeight;
    }

}
