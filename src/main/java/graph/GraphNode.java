package graph;

import algorithm.common.utility.Utility;

public class GraphNode {

    private String _id;
    private int _weight;
    private int _processor;
    private int _startTime;

    public GraphNode(String id, int weight) {
        _id = id;
        _weight = weight;
        _processor = -1;
        _startTime = -1;
    }

    public GraphNode(String id, int weight, int processor, int startTime ){
        _id = id;
        _weight = weight;
        _processor = processor;
        _startTime =  startTime;
    }

    public GraphNode(GraphNode node, int processor, int startTime ){
        this(node._id,node._weight,processor,startTime);
    }

    public String getId() {
        return _id;
    }

    public int getWeight() {
        return _weight;
    }

    public int getProcessor(){
        return _processor;
    }

    public int getStartTime(){
        return _startTime;
    }


}
