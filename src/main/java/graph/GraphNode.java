package graph;

/**
 * GraphNode - keeps instance of graph node (weight, processor, start time, node id). Such nodes will initially have
 * no processor/startTime assignments - but will be set during computation of algorithm.
 */
public class GraphNode {

    private String _id; // Unique node id
    private int _weight; // Node weight
    private int _processor; // Processor assigned to node
    private int _startTime; // Node's start time on processor
    private int _endTime; // Node's finish time on processor
    private boolean _free; // Represents whether the task is ready to be scheduled when using the IDA Star algorithm
    private int _computationalBottomLevel;  // A cost function representing the critical path cost from the current node

    // Constructors to create GraphNode objects

    /**
     * GraphNode constructor
     * @param id - string ID of the node
     * @param weight - weight of the node
     */
    public GraphNode(String id, int weight) {
        _id = id;
        _weight = weight;
        _processor = -1; //Default processor value
        _startTime = -1; //Default startTime value
    }

    /**
     * GraphNode constructor
     * @param id - string ID of the node
     * @param weight - weight of the node
     * @param processor - processor number the node is assigned to
     * @param startTime - start time of the scheduled node
     */
    public GraphNode(String id, int weight, int processor, int startTime){
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

    /**
     * setStartTime - setter for node id's start time on the processor assigned
     */
    public void setStartTime(int startTime) {
        _startTime = startTime;
    }

    /**
     * setEndTime - setter for node id's end time on the processor assigned
     */
    public void setEndTime(int endTime){
        _endTime = endTime;
    }

    /**
     * setProcessor - setter for node id's assigned processor
     */
    public void setProcessor(int processor) {
        _processor = processor;
    }

    /**
     * isFree - getter for whether the node is ready to be scheduled
     * @return returns a boolean; true for ready and false if not
     */
    public boolean isFree() { return _free; }

    /**
     * setFree - setter for whether the node is ready to be scheduled
      * @param free
     */
    public void setFree(boolean free) {
        this._free = free;
    }

    /**
     * getComputationalBottomLevel - getter for the node's cost function value
     * @return
     */
    public int getComputationalBottomLevel() {
        return _computationalBottomLevel;
    }

    /**
     * setComputationalBottomLevel - setter method for the node's cost function value
     * @param computationalBottomLevel
     */
    public void setComputationalBottomLevel(int computationalBottomLevel) {
        this._computationalBottomLevel = computationalBottomLevel;
    }

    //Framework Specific Auto Generated Getters for the Table Population
    public String get_id() {
        return _id;
    }

    public int get_weight() {
        return _weight;
    }

    public int get_processor() {
        return _processor;
    }

    public int get_startTime() {
        return _startTime;
    }

    public int get_endTime() {
        return _endTime;
    }
}
