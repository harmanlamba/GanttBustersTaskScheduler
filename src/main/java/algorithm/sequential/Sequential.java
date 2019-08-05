package algorithm.sequential;

import algorithm.Algorithm;
import graph.Graph;

public class Sequential extends Algorithm {

    public Sequential(Graph g) {
        super(g);
    }

    @Override
    public Graph solve() {
        return _graph;
    }
}
