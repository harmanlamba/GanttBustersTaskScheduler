package algorithm.idastarbase;

import algorithm.Algorithm;
import algorithm.AlgorithmBuilder;
import algorithm.idastarbase.IDAStarBase;
import exception.InputFileException;
import fileio.IO;
import graph.Graph;
import graph.GraphEdge;
import graph.GraphNode;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the IDAStarBase class.
 */
public class IDAStarBaseTest {
    //File arrays for input file locations
    private String[] _file1;
    private String[] _file2;
    private String[] _file3;
    private String[] _file4;
    private String[] _file5;
    private final int NUM_TASK_PROC = 2;
    private final int NUM_PARALLEL_PROC = 1;
    private Graph _graph;
    // TODO: Add correctness tests for the algorithm with 4 processors
//    private String[] _file1Proc4;
//    private String[] _file2Proc4;
//    private String[] _file3Proc4;
//    private String[] _file4Proc4;
//    private String[] _file5Proc4;

    @Before
    public void setup() {
        _file1 = new String[]{"src/main/resources/e1.dot", "2", "-p", "1"};
        _file2 = new String[]{"src/main/resources/e2.dot", "2", "-p", "1"};
        _file3 = new String[]{"src/main/resources/e3.dot", "2", "-p", "1"};
        _file4 = new String[]{"src/main/resources/e4.dot", "2", "-p", "1"};
        _file5 = new String[]{"src/main/resources/e5.dot", "2", "-p", "1"};
//        _file1Proc4 = new String[]{"src/main/resources/e1.dot", "4", "-p", "1"};
//        _file2Proc4 = new String[]{"src/main/resources/e2.dot", "4", "-p", "1"};
//        _file3Proc4 = new String[]{"src/main/resources/e3.dot", "4", "-p", "1"};
//        _file4Proc4 = new String[]{"src/main/resources/e4.dot", "4", "-p", "1"};
//        _file5Proc4 = new String[]{"src/main/resources/e5.dot", "4", "-p", "1"};
    }

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
        _graph = graph;
        return new IDAStarBase(graph, NUM_TASK_PROC, NUM_PARALLEL_PROC);
    }

    private Map<String, GraphNode> getIDAStarSolution(String[] file) throws InputFileException {
        IO io = new IO(file);
        Graph graph = new Graph(io.getNodeMap(), io.getEdgeList());
        IDAStarBase algorithm = new IDAStarBase(graph, io.getNumberOfProcessorsForTask(), io.getNumberOfProcessorsForParallelAlgorithm());
        return algorithm.solve();
    }

    /**
     * Checks if the IDAStarBase algorithm's produced schedule is optimal
     * @param solution
     * @param optimalStartTimes
     * @param optimalProcessors
     * @return true if the algorithm solution schedule is optimal, false if not.
     */
    private boolean solutionIsOptimal(Map<String, GraphNode> solution, int[] optimalStartTimes, int[] optimalProcessors) {
        int i = 0;
        for (GraphNode task : solution.values()) {

            if ((task.getStartTime() != optimalStartTimes[i]) || (task.getProcessor() != optimalProcessors[i])) {
                    return false;
                }
            i++;
        }
        return true;
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
     * Tests for the correct instantiation of a IDASTar object
     */
    @Test
    public void testInstantiation() {
        IDAStarBase IDAStar = IDAStarBaseInstance();
        assertEquals(IDAStar.getNumProcTask(), NUM_TASK_PROC);
        assertEquals(IDAStar.getNumProcParallel(), NUM_PARALLEL_PROC);
    }

    @Test
    public void testE1SolutionWithoutIODependency() {
        int[] startTimes = new int[] {0, 5, 11, 22, 16, 15, 20};
        int[] processors = new int[] {0, 0, 0, 1, 0, 1, 0};
        IDAStarBase IDAStar = IDAStarBaseInstance();

        if (solutionIsOptimal(IDAStar.solve(), startTimes, processors)) {
            assertTrue(true);
        } else {
            assert(false);
        }
    }

    @Test
    public void testE2Solution() {
        int[] startTimes = new int[] {0, 38, 35, 126, 211, 285, 387, 528};
        int[] processors = new int[] {0, 1, 0, 1, 0, 1, 0, 0};

        try {
            if (solutionIsOptimal(getIDAStarSolution(_file2), startTimes, processors)) {
                assertTrue(true);
            } else {
                assert(false);
            }
        } catch (InputFileException e) {
            assert(false);
        }
    }

    @Test
    public void testE3Solution() {
        int[] startTimes = new int[] {0, 48, 10, 16, 23, 39, 28, 30, 32};
        int[] processors = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0};
        try {
            if (solutionIsOptimal(getIDAStarSolution(_file3), startTimes, processors)) {
                assertTrue(true);
            } else {
                assert(false);
            }
        } catch (InputFileException e) {
            assert(false);
        }
    }

    @Test
    public void testE4Solution() {
        int[] startTimes = new int[] {0, 6, 11, 40, 16, 30, 19, 27, 30, 38};
        int[] processors = new int[] {0, 0, 0, 1, 0, 1, 0, 0, 0, 0};
        try {
            if (solutionIsOptimal(getIDAStarSolution(_file4), startTimes, processors)) {
                assertTrue(true);
            } else {
                assert(false);
            }
        } catch (InputFileException e) {
            assert(false);
        }
    }

    @Test
    public void testE5Solution() {
        int[] startTimes = new int[] {0, 50, 120, 54, 210, 250, 154, 270, 254, 304, 324};
        int[] processors = new int[] {0, 0, 0, 1, 0, 0, 1, 0, 1, 1, 1};
        try {
            if (solutionIsOptimal(getIDAStarSolution(_file5), startTimes, processors)) {
                assertTrue(true);
            } else {
                assert(false);
            }
        } catch (InputFileException e) {
            assert(false);
        }
    }
}
