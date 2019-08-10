package algorithm.idastarbase;

import graph.GraphNode;

import java.util.List;
import java.util.ArrayList;

public class State {
    private List<GraphNode> _list = new ArrayList<>();
    private final int _freePointer;

    public State(List<GraphNode> list, int freePointer) {
        _list = list;
        _freePointer = freePointer;
    }

    public List<GraphNode> getState() {
        return _list;
    }

    public int get_freePointer() {
        return _freePointer;
    }
}
