package algorithm.idastarbase;

import graph.GraphNode;

public class TaskInfo {

    private String _id;
    private GraphNode _task;
    private boolean _free;
    private int _computationalBottomLevel;

    public TaskInfo(GraphNode task, boolean free, int computationalBottomLevel) {
        _id = task.getId();
        _task = task;
        _free = free;
        _computationalBottomLevel = computationalBottomLevel;

    }

    public String getId() {
        return _id;
    }

    public GraphNode getTask() {
        return _task;
    }

    public boolean isFree() {
        return _free;
    }

    public int getComputationalBottomLevel() {
        return _computationalBottomLevel;
    }

    public void setFree(boolean _free) {
        this._free = _free;
    }

}
