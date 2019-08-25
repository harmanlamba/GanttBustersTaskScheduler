package algorithm.bbastar;

import algorithm.bbastarbase.BBAStarBase;
import exception.InputFileException;
import fileio.IO;
import graph.Graph;
import graph.GraphEdge;
import graph.GraphNode;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the BBAStarBase class
 */
public class BBAStarBaseTest {
    // File arrays for input file locations
    private final static String[] FILE1_PROC2 = new String[]{"src/main/resources/e1.dot", "2", "-p", "1"};
    private final static String[] FILE2_PROC2 = new String[]{"src/main/resources/e2.dot", "2", "-p", "1"};
    private final static String[] FILE3_PROC2  = new String[]{"src/main/resources/e3.dot", "2", "-p", "1"};
    private final static String[] FILE4_PROC2  = new String[]{"src/main/resources/e4.dot", "2", "-p", "1"};
    private final static String[] FILE5_PROC2  = new String[]{"src/main/resources/e5.dot", "2", "-p", "1"};
    private final static String[] FILE1_PROC4 = new String[]{"src/main/resources/e1.dot", "4", "-p", "1"};
    private final static String[] FILE2_PROC4 = new String[]{"src/main/resources/e2.dot", "4", "-p", "1"};
    private final static String[] FILE3_PROC4 = new String[]{"src/main/resources/e3.dot", "4", "-p", "1"};
    private final static String[] FILE4_PROC4 = new String[]{"src/main/resources/e4.dot", "4", "-p", "1"};
    private final static String[] FILE5_PROC4 = new String[]{"src/main/resources/e5.dot", "4", "-p", "1"};

    /**
     * Creates a graph and makes an instance of the algorithm to be used to test optimality without reliance
     * on the IO classes to open an input DOT file.
     * @return an instance of BBAStarBase which is independent of IO classes
     */
    private BBAStarBase BBAStarBaseInstance() {
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
        return new BBAStarBase(graph, 2, 1);
    }

    private Map<String, GraphNode> getBBAStarSolution(String[] file) throws InputFileException {
        IO io = new IO(file);
        Graph graph = new Graph(io.getNodeMap(), io.getEdgeList());
        BBAStarBase algorithm = new BBAStarBase(graph, io.getNumberOfProcessorsForTask(), io.getNumberOfProcessorsForParallelAlgorithm());
        return algorithm.solve();
    }

    /**
     * Checks if the BBAStarBase algorithm's produced schedule is optimal given the best cost last start
     * time and the associated node's weight
     * @return true if the algorithm solution schedule is optimal, false if not.
     */
    private boolean solutionIsOptimal(Map<String, GraphNode> solution, int bestCost) {
        boolean foundBestCost = false;
        for (GraphNode task : solution.values()) {
            if (task.getStartTime()+ task.getWeight() > bestCost) {
                return false;
            }
            if (task.getStartTime()+ task.getWeight() == bestCost) {
                foundBestCost = true;
            }
        }
        return foundBestCost;
    }

    @Test
    public void testE1Proc2WithoutIODependency() {
        BBAStarBase BBAStar = BBAStarBaseInstance();
        assertTrue(solutionIsOptimal(BBAStar.solve(), 28));
    }

    @Test
    public void testE1Proc2() {
        try {
            assertTrue(solutionIsOptimal(getBBAStarSolution(FILE1_PROC2), 28));
        } catch (InputFileException e) {
            assert(false);
        }
    }

    @Test
    public void testE2Proc2() {
        try {
            assertTrue(solutionIsOptimal(getBBAStarSolution(FILE2_PROC2), 581));
        } catch (InputFileException e) {
            assert(false);
        }
    }

    @Test
    public void testE3Proc() {
        try {
            assertEquals(true, solutionIsOptimal(getBBAStarSolution(FILE3_PROC2), 55));
        } catch (InputFileException e) {
            assert(false);
        }
    }

    @Test
    public void testE4Proc2() {
        try {
            assertTrue(solutionIsOptimal(getBBAStarSolution(FILE4_PROC2), 50));
        } catch (InputFileException e) {
            assert(false);
        }
    }

    @Test
    public void testE5Proc2() {
        try {
            assertEquals(true, solutionIsOptimal(getBBAStarSolution(FILE5_PROC2), 350));
        } catch (InputFileException e) {
            assert(false);
        }
    }

    @Test
    public void testE1Proc4() {
        try {
            assertTrue(solutionIsOptimal(getBBAStarSolution(FILE1_PROC4), 22));
        } catch (InputFileException e) {
            assert(false);
        }
    }

    @Test
    public void testE2Proc4() {
        try {
            assertTrue(solutionIsOptimal(getBBAStarSolution(FILE2_PROC4), 581));
        } catch (InputFileException e) {
            assert(false);
        }
    }
    @Test
    public void testE3Proc4() {
        try {
            assertTrue(solutionIsOptimal(getBBAStarSolution(FILE3_PROC4), 55));
        } catch (InputFileException e) {
            assert(false);
        }
    }


    @Test
    public void testE4Proc4() {
        try {
            assertTrue(solutionIsOptimal(getBBAStarSolution(FILE4_PROC4), 50));
        } catch (InputFileException e) {
            assert(false);
        }
    }

    @Test
    public void testE5Proc4() {
        try {
            assertTrue(solutionIsOptimal(getBBAStarSolution(FILE5_PROC4), 227));
        } catch (InputFileException e) {
            assert(false);
        }
    }

}

