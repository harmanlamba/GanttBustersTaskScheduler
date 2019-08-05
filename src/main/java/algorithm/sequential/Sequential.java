package algorithm.sequential;

import algorithm.Algorithm;
import graph.Graph;
import graph.GraphNode;
import graph.OutputGraphNode;

import java.util.ArrayList;
import java.util.List;

public class Sequential extends Algorithm {

    int PROCESSOR = 1;

    public Sequential(Graph g) {
        super(g);
    }

    @Override
    public List<OutputGraphNode> solve() {
        getTopologicalOrdering();
        List<OutputGraphNode> output = new ArrayList<>();

        int currentTime = 0;
        for (int i = 0; i < _order.size(); i++) {
            GraphNode temp = _order.get(i);
            OutputGraphNode tempOutputNode = new OutputGraphNode(temp, currentTime, PROCESSOR);
            output.add(tempOutputNode);
            currentTime += temp.getWeight();
        }
        return output;
    }
}
