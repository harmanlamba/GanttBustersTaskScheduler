package app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import algorithm.Algorithm;
import algorithm.AlgorithmBuilder;
import algorithm.common.utility.AlgorithmType;
import exception.InputFileException;
import fileio.IO;
import graph.Graph;
import org.junit.Before;
import org.junit.Test;

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
        _file1 = new String[]{"src/resources/pepman.dot", "2", "-o", "me", "-p", "2"};
        _file2 = new String[]{"src/resources/e2.dot", "1", "-o", "me", "-p", "2"};
        _file3 = new String[]{"src/resources/e3.dot", "1", "-o", "me", "-p", "2"};
        _file4 = new String[]{"src/resources/e4.dot", "1", "-o", "me", "-p", "2"};
        _file5 = new String[]{"src/resources/e5.dot", "1", "-o", "me", "-p", "2"};
        _invalidFormat = new String[]{"src/resources/e6.dot", "1", "-o", "me", "-p", "2"};
        _missingNode = new String[]{"src/resources/e7.dot", "1", "-o", "me", "-p", "2"};
    }

    /**
     * scheduleTestHelper - runs required class instantiations to test sequential algorithm type and writing to file
     * @param file - dot file input file location
     */
    private void sequentialTestHelper(String[] file) throws InputFileException {
        IO io = new IO(file);
        Graph graph = new Graph(io.getNodeMap(), io.getEdgeList());
        AlgorithmBuilder algorithmBuilder = new AlgorithmBuilder(AlgorithmType.IDASTARBASE, graph,
                io.getNumberOfProcessorsForTask(), io.getNumberOfProcessorsForParallelAlgorithm());
        Algorithm algorithm = algorithmBuilder.getAlgorithm();
        io.write(algorithm.solve());

    }

    @Test
    public void testE1File() {
        try {
            sequentialTestHelper(_file1);
            assertTrue(true);
        } catch (InputFileException e) {
            assert(false);
        }

    }
    /*
    @Test
    public void testE2File() {
        try {
            sequentialTestHelper(_file2);
            assertTrue(true);
        } catch (InputFileException e) {
            assert(false);
        }
    }

    @Test
    public void testE3File() {
        try {
            sequentialTestHelper(_file3);
            assertTrue(true);
        } catch (InputFileException e) {
            assert(false);
        }
    }

    @Test
    public void testE4File() {
        try {
            sequentialTestHelper(_file4);
            assertTrue(true);
        } catch (InputFileException e) {
            assert(false);
        }
    }

    @Test
    public void testE5File() {
        try {
            sequentialTestHelper(_file5);
            assertTrue(true);
        } catch (InputFileException e) {
            assert(false);
        }
    }

    @Test
    public void testInvalidFormat() {
        try {
            sequentialTestHelper(_invalidFormat);
            assert(false);
        } catch(InputFileException e) {
            assertEquals(e.getMessage(),"Invalid Format");
        }
    }

    @Test
    public void testMissingNodes() {
        try {
            sequentialTestHelper(_missingNode);
            assert(false);
        } catch(InputFileException e) {
            assertEquals(e.getMessage(),"Node has not been instantiated");
        }
    }

     */
}
