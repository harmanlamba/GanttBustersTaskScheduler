package algorithm.idastarbase;

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
 * Tests for the IDAStarBase class
 */
public class IDAStarBaseTest {
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
     * @return an instance of IDAStarBase which is independent of IO classes
     */
    private IDAStarBase IDAStarBaseInstance() {
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
        return new IDAStarBase(graph, 2, 1);
    }

    private Map<String, GraphNode> getIDAStarSolution(String[] file) throws InputFileException {
        IO io = new IO(file);
        Graph graph = new Graph(io.getNodeMap(), io.getEdgeList());
        IDAStarBase algorithm = new IDAStarBase(graph, io.getNumberOfProcessorsForTask(), io.getNumberOfProcessorsForParallelAlgorithm());
        return algorithm.solve();
    }

    /**
     * Checks if the IDAStarBase algorithm's produced schedule is optimal given the best cost last start
     * time and the associated node's weight
     * @return true if the algorithm solution schedule is optimal, false if not.
     */
    private boolean solutionIsOptimal(Map<String, GraphNode> solution, int bestCostLastStartTime, int BestCostLastWeight) {
        for (GraphNode task : solution.values()) {
            if ((task.getStartTime() == bestCostLastStartTime) && (task.getWeight() == BestCostLastWeight)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Following instantiation of IDAStar, the current optimal solution should be an empty map
     * (not yet found).
     */
    @Test
    public void testInitNoOptimalSolution() throws InputFileException {
        IDAStarBase IDAStar = IDAStarBaseInstance();
        assertEquals(IDAStar.getCurrentBestSolution(), new HashMap<>());
    }

    /**
     * Tests for the algorithm's calculation of the computational bottom level
     */
    @Test
    public void testComputationalBottomLevel() {
        int[] expectedCompBottomLevels = new int[]{18,13,5,6,4,7,7};
        Map<String, GraphNode> solution = IDAStarBaseInstance().solve();
        int i = 0;
        for (GraphNode task : solution.values()) {
            if (task.getComputationalBottomLevel() != expectedCompBottomLevels[i]) {
                assert(false);
            }
            i++;
        }
        assert(true);
    }

    @Test
    public void testE1Proc2WithoutIODependency() {
        IDAStarBase IDAStar = IDAStarBaseInstance();
        assertTrue(solutionIsOptimal(IDAStar.solve(), 22, 6));
    }

    @Test
    public void testE1Proc2() {
        try {
            assertTrue(solutionIsOptimal(getIDAStarSolution(FILE1_PROC2), 22, 6));
        } catch (InputFileException e) {
            assert(false);
        }
    }

    @Test
    public void testE2Proc2() {
        try {
            assertTrue(solutionIsOptimal(getIDAStarSolution(FILE2_PROC2), 528, 53));
        } catch (InputFileException e) {
            assert(false);
        }
    }

    @Test
    public void testE3Proc() {
        try {
            assertEquals(true, solutionIsOptimal(getIDAStarSolution(FILE3_PROC2), 48, 7));
        } catch (InputFileException e) {
            assert(false);
        }
    }

    @Test
    public void testE4Proc2() {
        try {
            assertTrue(solutionIsOptimal(getIDAStarSolution(FILE4_PROC2), 40, 10));
        } catch (InputFileException e) {
            assert(false);
        }
    }

    @Test
    public void testE5Proc2() {
        try {
            assertEquals(true, solutionIsOptimal(getIDAStarSolution(FILE5_PROC2), 270, 80));
        } catch (InputFileException e) {
            assert(false);
        }
    }

    @Test
    public void testE1Proc4() {
        try {
            assertTrue(solutionIsOptimal(getIDAStarSolution(FILE1_PROC4), 15, 7));
        } catch (InputFileException e) {
            assert(false);
        }
    }

    @Test
    public void testE2Proc4() {
        try {
            assertTrue(solutionIsOptimal(getIDAStarSolution(FILE2_PROC4), 528, 53));
        } catch (InputFileException e) {
            assert(false);
        }
    }
    @Test
    public void testE3Proc4() {
        try {
            assertTrue(solutionIsOptimal(getIDAStarSolution(FILE3_PROC4), 48, 7));
        } catch (InputFileException e) {
            assert(false);
        }
    }


    @Test
    public void testE4Proc4() {
        try {
            assertTrue(solutionIsOptimal(getIDAStarSolution(FILE4_PROC4), 40, 10));
        } catch (InputFileException e) {
            assert(false);
        }
    }

    @Test
    public void testE5Proc4() {
        try {
            assertTrue(solutionIsOptimal(getIDAStarSolution(FILE5_PROC4), 147, 80));
        } catch (InputFileException e) {
            assert(false);
        }
    }

}

