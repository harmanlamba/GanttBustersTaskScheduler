package algorithm.sequential;

import algorithm.Algorithm;
import graph.Graph;
import graph.GraphNode;

import java.util.HashMap;
import java.util.Map;

public class Sequential extends Algorithm {

    public Sequential(Graph g, int numProcTask, int numProcParallel) {
        super(g, numProcTask, numProcParallel);
    }

    @Override
    public Map<String, GraphNode> solve() {
        getTopologicalOrdering();
        Map<String, GraphNode> output = new HashMap<>();

        int currentTime = 0;
        for (int i = 0; i < _order.size(); i++) {
            GraphNode tempNode = _order.get(i);
            GraphNode tempOutputNode = new GraphNode(tempNode, _numProcTask, currentTime);
            output.put(tempNode.getId(), tempOutputNode);
            currentTime += tempNode.getWeight();
            System.out.println(tempNode.getId() + " " + tempOutputNode.getStartTime());
        }
        return output;
    }
}
