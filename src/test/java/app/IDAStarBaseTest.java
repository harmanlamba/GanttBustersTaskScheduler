package app;

import algorithm.Algorithm;
import algorithm.AlgorithmBuilder;
import exception.InputFileException;
import fileio.IO;
import graph.Graph;
import graph.GraphNode;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests that the IDAStarBase Algorithm produces the correct, optimal solution.
 */
// TODO: Remove all IO dependencies - will have to make Graphs from scratch
public class IDAStarBaseTest {
    //File arrays for input file locations
    private String[] _file1;
    private String[] _file2;
    private String[] _file3;
    private String[] _file4;
    private String[] _file5;
    // TODO: Add tests for the algorithm with 4 processors
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

    /**
     * scheduleTestHelper - runs required class instantiations to test sequential algorithm type and writing to file
     * @param file - dot file input file location
     */
    private List<GraphNode> getIDAStarResult(String[] file) throws InputFileException {
        IO io = new IO(file);
        Graph graph = new Graph(io.getNodeMap(), io.getEdgeList());
        Algorithm algorithm =  AlgorithmBuilder.getAlgorithmBuilder().createAlgorithm(graph,
                io.getNumberOfProcessorsForTask(), io.getNumberOfProcessorsForParallelAlgorithm()).getAlgorithm();
        Map<String, GraphNode> scheduleInfo = algorithm.solve();
        return new ArrayList<>(scheduleInfo.values());
    }

    private boolean scheduleIsOptimal(List<GraphNode> scheduleResults, int[] startTimesExpected, int[] processorsExpected) {
        int i = 0;
        for (GraphNode task : scheduleResults) {
                if ((task.getStartTime() != startTimesExpected[i]) || (task.getProcessor() != processorsExpected[i])) {
                    return false;
                }
            i++;
        }
        return true;
    }

    @Test
    public void testE1Schedule() {
        int[] startTimes = new int[] {0, 5, 11, 22, 16, 15, 20};
        int[] processors = new int[] {0, 0, 0, 1, 0, 1, 0};
        try {
            if (scheduleIsOptimal(getIDAStarResult(_file1), startTimes, processors)) {
                assertTrue(true);
            } else {
                assert(false);
            }
        } catch (InputFileException e) {
            assert(false);
        }
    }
    @Test
    public void testE2Schedule() {
        int[] startTimes = new int[] {0, 5, 11, 22, 16, 15, 20};
        int[] processors = new int[] {0, 0, 0, 1, 0, 1, 0};
        try {
            if (scheduleIsOptimal(getIDAStarResult(_file1), startTimes, processors)) {
                assertTrue(true);
            } else {
                assert(false);
            }
        } catch (InputFileException e) {
            assert(false);
        }
    }

    @Test
    public void testE3Schedule() {
        int[] startTimes = new int[] {0, 48, 10, 16, 23, 39, 28, 30, 32};
        int[] processors = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0};
        try {
            if (scheduleIsOptimal(getIDAStarResult(_file3), startTimes, processors)) {
                assertTrue(true);
            } else {
                assert(false);
            }
        } catch (InputFileException e) {
            assert(false);
        }
    }

    @Test
    public void testE4Schedule() {
        int[] startTimes = new int[] {0, 6, 11, 40, 16, 30, 19, 27, 30, 38};
        int[] processors = new int[] {0, 0, 0, 1, 0, 1, 0, 0, 0, 0};
        try {
            if (scheduleIsOptimal(getIDAStarResult(_file4), startTimes, processors)) {
                assertTrue(true);
            } else {
                assert(false);
            }
        } catch (InputFileException e) {
            assert(false);
        }
    }

    @Test
    public void testE5Schedule() {
        int[] startTimes = new int[] {0, 50, 120, 54, 210, 250, 154, 270, 254, 304, 324};
        int[] processors = new int[] {0, 0, 0, 1, 0, 0, 1, 0, 1, 1, 1};
        try {
            if (scheduleIsOptimal(getIDAStarResult(_file5), startTimes, processors)) {
                assertTrue(true);
            } else {
                assert(false);
            }
        } catch (InputFileException e) {
            assert(false);
        }
    }

}
