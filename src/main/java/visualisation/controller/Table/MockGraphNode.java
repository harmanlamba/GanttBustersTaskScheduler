package visualisation.controller.Table;

public class MockGraphNode {
    private String _id; // Unique node id
    private int _weight; // Node weight
    private int _processor; // Processor assigned to node
    private int _startTime; // Node's start time on processor
    private int _endTime;

    public MockGraphNode(String id, int weight, int processor, int startTime){
        _id=id;
        _weight=weight;
        _processor=processor;
        _startTime = startTime;
        _endTime= _startTime + _weight;
    }


    public String getId() {
        return _id;
    }

    public int getWeight() {
        return _weight;
    }

    public int getProcessor() {
        return _processor;
    }

    public int getStartTime() {
        return _startTime;
    }

    public int getEndTime() {
        return _endTime;
    }
}
