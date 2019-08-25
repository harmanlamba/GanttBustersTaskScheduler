package graph;

import graph.Graph;
import graph.GraphEdge;
import graph.GraphNode;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphTest {
    private Graph _graph;
    private GraphNode _node;

    @Before
    public void setup() {
        // Set up arbitrary Graph
        Map<String, GraphNode> vertexMap = new HashMap<>();
        List<GraphEdge> edgeList = new ArrayList<>();
        GraphNode n0 = new GraphNode("0", 5);
        GraphNode n1 = new GraphNode("1",2);
        GraphNode n2 = new GraphNode("2",1);
        GraphNode n3 = new GraphNode("3", 1);
        vertexMap.put("0",n0);
        vertexMap.put("1",n1);
        vertexMap.put("2",n2);
        vertexMap.put("3",n3);
        edgeList.add(new GraphEdge(n0,n1,5));
        edgeList.add(new GraphEdge(n0,n2,1));
        edgeList.add(new GraphEdge(n1,n3,2));
        edgeList.add(new GraphEdge(n2,n3,9));
        Graph graph = new Graph(vertexMap, edgeList);
        _graph = graph;

        // Set up arbitrary GraphNode
        _node = new GraphNode("0", 5);
    }

    /**
     * Tests that graph nodes initially have no start time
     */
    @Test
    public void testInitTaskProcessor() {
        assertEquals(-1, _node.getProcessor());
    }

    /**
     * Tests that graph nodes initially have no processor allocation
     */
    @Test
    public void testInitTaskStartTime() {
        assertEquals(-1, _node.getStartTime());
    }

    /**
     * Tests that graph nodes initially has a computational bottom level of zero
     */
    @Test
    public void testInitTaskComputationalBottomLevel() {
        assertEquals(0, _node.getComputationalBottomLevel());
    }

    /**
     * Tests the equality method for GraphNode objects.
     * Any 2 nodes with the same id should be treated as equal.
     */
    @Test
    public void testGraphNodeEquality() {
        GraphNode _node1 = new GraphNode("0", 9);
        GraphNode _node2 = new GraphNode("0", 9);
        assertEquals(_node1, _node2);
    }

    /**
     * Tests the equality method for GraphEdge objects.
     * Any 2 edges with the same source node, target node and edge weight should be treated as equal
     */
    @Test
    public void testGraphEdgeEquality() {
        GraphNode node1 = new GraphNode("0", 9);
        GraphNode node2 = new GraphNode("1", 9);
        GraphEdge edge1 = new GraphEdge(node1, node2, 4);
        GraphEdge edge2 = new GraphEdge(node1, node2, 4);
        GraphEdge edge3 = new GraphEdge(node1, node2, 3);
        if ((edge1.equals(edge2)) && !(edge1.equals(edge3))) {
            assert(true);
        } else {
            assert(false);
        }
    }






}
