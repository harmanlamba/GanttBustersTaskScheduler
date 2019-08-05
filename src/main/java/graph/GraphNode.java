package graph;

public class GraphNode {

    private String _id;
    private int _weight;

    public GraphNode(String id, int weight) {
        _id = id;
        _weight = weight;
    }

    public String getId() {
        return _id;
    }

    public int getWeight() {
        return _weight;
    }

}
