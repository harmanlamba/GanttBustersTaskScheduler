package algorithm.idastarbase;

import algorithm.idastarparallel.IDAStarParallel;
import exception.InputFileException;
import fileio.IO;
import graph.Graph;
import graph.GraphNode;
import org.junit.Test;
import org.junit.Assert.*;

import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class IDAStarParallelTest {
    private final static String NUM_PROC_PARALLEL = "1";
    //File arrays for input file locations
    private final static String[] FILE1_PROC2 = new String[]{"src/main/resources/e1.dot", "2", "-p", NUM_PROC_PARALLEL};
    private final static String[] FILE2_PROC2 = new String[]{"src/main/resources/e2.dot", "2", "-p", NUM_PROC_PARALLEL};
    private final static String[] FILE3_PROC2  = new String[]{"src/main/resources/e3.dot", "2", "-p", NUM_PROC_PARALLEL};
    private final static String[] FILE4_PROC2  = new String[]{"src/main/resources/e4.dot", "2", "-p", NUM_PROC_PARALLEL};
    private final static String[] FILE5_PROC2  = new String[]{"src/main/resources/e5.dot", "2", "-p", NUM_PROC_PARALLEL};
    private final static String[] FILE1_PROC4 = new String[]{"src/main/resources/e1.dot", "4", "-p", NUM_PROC_PARALLEL};
    private final static String[] FILE2_PROC4 = new String[]{"src/main/resources/e2.dot", "4", "-p", NUM_PROC_PARALLEL};
    private final static String[] FILE3_PROC4 = new String[]{"src/main/resources/e3.dot", "4", "-p", NUM_PROC_PARALLEL};
    private final static String[] FILE4_PROC4 = new String[]{"src/main/resources/e4.dot", "4", "-p", NUM_PROC_PARALLEL};
    private final static String[] FILE5_PROC4 = new String[]{"src/main/resources/e5.dot", "4", "-p", NUM_PROC_PARALLEL};

    private Map<String, GraphNode> getIDAStarSolution(String[] file) throws InputFileException {
        IO io = new IO(file);
        Graph graph = new Graph(io.getNodeMap(), io.getEdgeList());
        IDAStarParallel algorithm = new IDAStarParallel(graph, io.getNumberOfProcessorsForTask(), io.getNumberOfProcessorsForParallelAlgorithm());
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
            assertTrue(solutionIsOptimal(getIDAStarSolution(FILE3_PROC2), 48, 7));
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
            assertTrue(solutionIsOptimal(getIDAStarSolution(FILE5_PROC2), 270, 80));
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
