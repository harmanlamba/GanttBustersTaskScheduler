package graph;

import graph.Graph;
import graph.GraphEdge;
import graph.GraphNode;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphTest {
    private Graph _graph;

    @Before
    public void setup() {
        Map<String, GraphNode> vertexMap = new HashMap<>();
        List<GraphEdge> edgeList = new ArrayList<>();
        GraphNode n0 = new GraphNode("0", 5);
        GraphNode n1 = new GraphNode("1",6);
        GraphNode n2 = new GraphNode("2",5);
        GraphNode n3 = new GraphNode("3",6);
        GraphNode n4 = new GraphNode("4",4);
        GraphNode n5 = new GraphNode("5",7);
        GraphNode n6 = new GraphNode("6",7);
        vertexMap.put("0",n0);
        vertexMap.put("1",n1);
        vertexMap.put("2",n2);
        vertexMap.put("3",n3);
        vertexMap.put("4",n4);
        vertexMap.put("5",n5);
        vertexMap.put("6",n6);
        edgeList.add(new GraphEdge(n0,n1,15));
        edgeList.add(new GraphEdge(n0,n2,11));
        edgeList.add(new GraphEdge(n0,n3,11));
        edgeList.add(new GraphEdge(n1,n4,19));
        edgeList.add(new GraphEdge(n1,n5,4));
        edgeList.add(new GraphEdge(n1,n6,21));
        Graph graph = new Graph(vertexMap, edgeList);
        _graph = graph;
    }

    /**
     * The JGraphT object should be consistent with the Graph created
     */
    @Test
    public void testJGraphTCreated() {

    }


}
