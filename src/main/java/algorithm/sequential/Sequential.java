package algorithm.sequential;

import algorithm.Algorithm;
import graph.Graph;
import graph.GraphNode;
import graph.OutputGraphNode;

import java.util.HashMap;
import java.util.Map;

public class Sequential extends Algorithm {

    int PROCESSOR = 1;

    public Sequential(Graph g) {
        super(g);
    }

    @Override
    public Map<GraphNode, OutputGraphNode> solve() {
        getTopologicalOrdering();
        Map<GraphNode, OutputGraphNode> output = new HashMap<>();

        int currentTime = 0;
        for (int i = 0; i < _order.size(); i++) {
            GraphNode tempNode = _order.get(i);
            OutputGraphNode tempOutputNode = new OutputGraphNode(tempNode, currentTime, PROCESSOR);
            output.put(tempNode, tempOutputNode);
            currentTime += tempNode.getWeight();
            //System.out.println(tempNode.getId() + " " + tempOutputNode.getStartTime());
        }
        return output;
    }
}
