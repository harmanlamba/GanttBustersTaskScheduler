package app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import algorithm.Algorithm;
import algorithm.AlgorithmBuilder;
import exception.InputFileException;
import fileio.IO;
import graph.Graph;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

/**
 * AppTest - testing the input and output of dot files. If exceptions have been caught from some exception (usually found
 * in the dot file), the tests will fail.
 */
public class AppTest 
{
    //File arrays for input file locations
    private String[] _file1;
    private String[] _file2;
    private String[] _file3;
    private String[] _file4;
    private String[] _file5;
    private String[] _invalidFormat;
    private String[] _missingNode;

    @Before
    public void setup() {
        _file1 = new String[]{"src/main/resources/Nodes_7.dot", "2", "-o", "me1", "-p", "4"};
        _file2 = new String[]{"src/main/resources/Nodes_8.dot", "2", "-o", "me2", "-p", "4"};
        _file3 = new String[]{"src/main/resources/Nodes_9.dot", "2", "-o", "me3", "-p", "4"};
        _file4 = new String[]{"src/main/resources/Nodes_11.dot", "2", "-o", "me4", "-p", "4"};
        _file5 = new String[]{"src/main/resources/monster.dot", "2", "-o", "me5", "-p", "4"};
        _invalidFormat = new String[]{"src/main/resources/e6.dot", "1", "-o", "me", "-p", "2"};
        _missingNode = new String[]{"src/main/resources/e7.dot", "1", "-o", "me", "-p", "2"};
    }

    /**
     * scheduleTestHelper - runs required class instantiations to test sequential algorithm type and writing to file
     * @param file - dot file input file location
     */
    private void testHelper(String[] file) throws InputFileException {
        IO io = new IO(file);
        Graph graph = new Graph(io.getNodeMap(), io.getEdgeList());
        Algorithm algorithm =  AlgorithmBuilder.getAlgorithmBuilder().createAlgorithm(graph,
                io.getNumberOfProcessorsForTask(), io.getNumberOfProcessorsForParallelAlgorithm()).getAlgorithm();
        io.write(algorithm.solve());

    }

    @Test
    public void testE1File() {
        try {
            testHelper(_file1);
            assertTrue(true);
        } catch (InputFileException e) {
            assert(false);
        }

    }

    @Test
    public void testE2File() {
        try {
            testHelper(_file2);
            assertTrue(true);
        } catch (InputFileException e) {
            assert(false);
        }
    }

    @Test
    public void testE3File() {
        try {
            testHelper(_file3);
            assertTrue(true);
        } catch (InputFileException e) {
            assert(false);
        }
    }

    @Test
    public void testE4File() {
        try {
            testHelper(_file4);
            assertTrue(true);
        } catch (InputFileException e) {
            assert(false);
        }
    }

    @Test
    public void testE5File() {
        try {
            testHelper(_file5);
            assertTrue(true);
        } catch (InputFileException e) {
            assert(false);
        }
    }

    @Test
    public void testInvalidFormat() {
        try {
            testHelper(_invalidFormat);
            assert(false);
        } catch(InputFileException e) {
            assertEquals(e.getMessage(),"Invalid Format");
        }
    }

    @Test
    public void testMissingNodes() {
        try {
            testHelper(_missingNode);
            assert(false);
        } catch(InputFileException e) {
            assertEquals(e.getMessage(),"Node has not been instantiated");
        }
    }
}
