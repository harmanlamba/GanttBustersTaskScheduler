package graph;

/**
 * GraphNode - keeps instance of graph node (weight, processor, start time, node id). Such nodes will initially have
 * no processor/startTime assignments - but will be set during computation of algorithm.
 */
public class GraphNode {

    private String _id; //Unique node id
    private int _weight; //Node weight
    private int _processor; //Processor assigned to node
    private int _startTime; //Node's start time on processor
    private int _endTime;
    private boolean _free;
    private int _computationalBottomLevel;

    public GraphNode(String id, int weight) {
        _id = id;
        _weight = weight;
        _processor = -1; //Default processor value
        _startTime = -1; //Default startTime value
    }

    public GraphNode(String id, int weight, int processor, int startTime ){
        _id = id;
        _weight = weight;
        _processor = processor;
        _startTime =  startTime;
    }

    /**
     * equals - overriding equals method
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        return _id.equals(((GraphNode) o)._id);
    }

        /**
         * getId - getter for node id
         * @return returns node id
         */
    public String getId() {
        return _id;
    }

    /**
     * getWeight - getter for node's weight
     * @return returns the node id's weight
     */
    public int getWeight() {
        return _weight;
    }

    /**
     * getProcessor - getter for node id's assigned processor
     * @return returns node id's processor assignment
     */
    public int getProcessor(){
        return _processor;
    }

    /**
     * getStartTime - getter for node id's start time on the processor assignment
     * @return returns start time value for the node
     */
    public int getStartTime(){
        return _startTime;
    }

    public int getEndTime() {
        return _endTime;
    }

    public void setStartTime(int startTime) {
        _startTime = startTime;
    }

    public void setEndTime(int endTime){
        _endTime = endTime;
    }

    public void setProcessor(int processor) {
        _processor = processor;
    }

    public boolean isFree() { return _free; }

    public void setFree(boolean free) {
        this._free = free;
    }

    public int getComputationalBottomLevel() {
        return _computationalBottomLevel;
    }

    public void setComputationalBottomLevel(int computationalBottomLevel) {
        this._computationalBottomLevel = computationalBottomLevel;
    }


}
