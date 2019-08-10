package app;

import static org.junit.Assert.assertTrue;

import algorithm.Algorithm;
import algorithm.AlgorithmBuilder;
import algorithm.common.utility.AlgorithmType;
import fileio.IO;
import graph.Graph;
import org.junit.Before;
import org.junit.Test;

/**
 * TODO: Comment tests
 */
public class AppTest 
{
    private String[] _file1;
    private String[] _file2;
    private String[] _file3;
    private String[] _file4;
    private String[] _file5;

    @Before
    public void setup() {
        _file1 = new String[]{"src/resources/e1.dot", "1", "-o", "me", "-p", "2"};
        _file2 = new String[]{"src/resources/e2.dot", "1", "-o", "me", "-p", "2"};
        _file3 = new String[]{"src/resources/e3.dot", "1", "-o", "me", "-p", "2"};
        _file4 = new String[]{"src/resources/e4.dot", "1", "-o", "me", "-p", "2"};
        _file5 = new String[]{"src/resources/e5.dot", "1", "-o", "me", "-p", "2"};
    }

    private void graphTestHelper(String[] file) {
        IO io = new IO(_file1);
        Graph graph = new Graph(io.getNodeMap(), io.getEdgeList());
        AlgorithmBuilder algorithmBuilder = new AlgorithmBuilder(AlgorithmType.SEQUENTIAL, graph, io.getNumberOfProcessorsForTask(), io.getNumberOfProcessorsForParallelAlgorithm());
        Algorithm algorithm = algorithmBuilder.getAlgorithm();
        io.write(algorithm.solve());
    }

    @Test
    public void testE1File() {
        graphTestHelper(_file1);
        assertTrue(true);
    }

    @Test
    public void testE2File() {
        graphTestHelper(_file2);
        assertTrue(true);
    }

    @Test
    public void testE3File() {
        graphTestHelper(_file3);
        assertTrue(true);
    }

    @Test
    public void testE4File() {
        graphTestHelper(_file4);
        assertTrue(true);
    }

    @Test
    public void testE5File() {
        graphTestHelper(_file5);
        assertTrue(true);
    }

}
