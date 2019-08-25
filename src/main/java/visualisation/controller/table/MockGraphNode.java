package visualisation.controller.table;

/**
 * A duplicate GraphNode class used for the GUI
 */
public class MockGraphNode {
    private String _id; // Unique node id
    private int _weight; // Node weight
    private int _processor; // Processor assigned to node
    private int _startTime; // Node's start time on processor
    private int _endTime; // Node's end time on processor

    public MockGraphNode(String id, int weight, int processor, int startTime){
        _id = id;
        _weight = weight;
        _processor = processor;
        _startTime = startTime;
        _endTime = _startTime + _weight;
    }

    /**
     * Getter method for the MockGraphNode's
     * @return
     */
    public String getId() {
        return _id;
    }

    /**
     * Getter method for the MockGraphNode's weight
     * @return an integer of the node's weight
     */
    public int getWeight() {
        return _weight;
    }

    /**
     * Getter method for the MockGraphNode's processor number
     * @return an int representing the node's processor number
     */
    public int getProcessor() {
        return _processor;
    }

    /**
     * Getter method for the MockGraphNode's start time
     * @return an int representing the node's start time
     */
    public int getStartTime() {
        return _startTime;
    }

    /**
     * Getter method for the MockGraphNode's end time
     * @return an int representing the node's start time
     */
    public int getEndTime() {
        return _endTime;
    }
}
