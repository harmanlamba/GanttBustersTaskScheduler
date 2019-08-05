package algorithm.sequential;

import algorithm.Algorithm;
import graph.Graph;
import graph.GraphNode;
import graph.OutputGraphNode;

import java.util.HashMap;
import java.util.Map;

public class Sequential extends Algorithm {

    public Sequential(Graph g, int numProcTask, int numProcParallel) {
        super(g, numProcTask, numProcParallel);
    }

    @Override
    public Map<GraphNode, OutputGraphNode> solve() {
        getTopologicalOrdering();
        Map<GraphNode, OutputGraphNode> output = new HashMap<>();

        int currentTime = 0;
        for (int i = 0; i < _order.size(); i++) {
            GraphNode tempNode = _order.get(i);
            OutputGraphNode tempOutputNode = new OutputGraphNode(tempNode, currentTime, _numProcTask);
            output.put(tempNode, tempOutputNode);
            currentTime += tempNode.getWeight();
            System.out.println(tempNode.getId() + " " + tempOutputNode.getStartTime());
        }
        return output;
    }
}
