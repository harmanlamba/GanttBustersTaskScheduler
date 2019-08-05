package graph;

public class OutputGraphNode extends GraphNode {
    private int _processor;
    private int _startTime;

    public OutputGraphNode(GraphNode node, int startTime, int processor) {
        super(node.getId(), node.getWeight());
        _startTime = startTime;
        _processor = processor;
    }

    public int getStartTime() {
        return _startTime;
    }

    public int getProcessor() {
        return _processor;
    }
}
