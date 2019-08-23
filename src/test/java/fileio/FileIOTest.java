package fileio;

import algorithm.Algorithm;
import algorithm.AlgorithmBuilder;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import exception.InputFileException;
import fileio.IO;
import graph.Graph;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests the reading and writing of DOT files.
 * Invalid DOT file inputs will be read and will produce specific error messages.
 * Valid DOT file inputs will be read and will run an arbitrary algorithm (as specified in setup) in order
 * to test the writing of files, which are subject to manual confirmation of file creation.
 */
public class FileIOTest {
    //File arrays for input file locations
    private String[] _file1;
    private String[] _file2;
    private String[] _file3;
    private String[] _file4;
    private String[] _file5;
    private String[] _invalidFormat;
    private String[] _missingNode;
    private String[] _negativeEdgeWeight;
    private String[] _negativeNodeWeight;
    private String[] _ownExample1;
    private ArrayList<Graph> _graphStore;

    @Before
    public void setup() {
        _file1 = new String[]{"src/main/resources/e1.dot", "2", "-o", "me1", "-p", "1"};
        _file2 = new String[]{"src/main/resources/e2.dot", "2", "-o", "me2", "-p", "1"};
        _file3 = new String[]{"src/main/resources/e3.dot", "2", "-o", "me3", "-p", "1"};
        _file4 = new String[]{"src/main/resources/e4.dot", "2", "-o", "me4", "-p", "1"};
        _file5 = new String[]{"src/main/resources/e5.dot", "2", "-o", "me5", "-p", "1"};
        _invalidFormat = new String[]{"src/main/resources/e6.dot", "1", "-o", "me", "-p", "2"};
        _missingNode = new String[]{"src/main/resources/e7.dot", "1", "-o", "me", "-p", "2"};
        _negativeEdgeWeight = new String[]{"src/main/resources/e8.dot", "2", "-o", "me", "-p", "1"};
        _negativeNodeWeight = new String[]{"src/main/resources/e9.dot", "2", "-o", "me", "-p", "1"};
        _ownExample1 = new String[]{"src/main/resources/e10.dot", "2", "-o", "me", "-p", "1"};
    }

    /**
     * Delete files after all tests execute
     */
    @AfterClass
    public static void deleteOutputFile()  {
        File file;
        for (int i = 1; i < 6; i++) {
            file = new File("src/main/resources/me"+i+".dot");
            file.delete();
        }
    }

    /**
     * scheduleTestHelper - runs required class instantiations to test sequential algorithm type and writing to file
     * @param file - dot file input file location
     */
    private void idaStarTestHelper(String[] file) throws InputFileException {
        IO io = new IO(file);

        _graphStore = new ArrayList<>();
        for (int i = 0; i< io.getNumberOfProcessorsForParallelAlgorithm(); i++) {
            IO tempIO = new IO(file);
            Graph graph = new Graph(tempIO.getNodeMap(), tempIO.getEdgeList()); //create graph from nodes and edges
            _graphStore.add(graph);
        }

        Graph graph = new Graph(io.getNodeMap(), io.getEdgeList());
        Algorithm algorithm =  AlgorithmBuilder.getAlgorithmBuilder().createAlgorithm(_graphStore,
                io.getNumberOfProcessorsForTask(), io.getNumberOfProcessorsForParallelAlgorithm()).getAlgorithm();
        io.write(algorithm.solve());
    }

    @Test
    public void testE1File() {
        try {
            idaStarTestHelper(_file1);
            assertTrue(true);
        } catch (InputFileException e) {
            assert(false);
        }
    }

    @Test
    public void testE2File() {
        try {
            idaStarTestHelper(_file2);
            assertTrue(true);
        } catch (InputFileException e) {
            assert(false);
        }
    }

    @Test
    public void testE3File() {
        try {
            idaStarTestHelper(_file3);
            assertTrue(true);
        } catch (InputFileException e) {
            assert(false);
        }
    }

    @Test
    public void testE4File() {
        try {
            idaStarTestHelper(_file4);
            assertTrue(true);
        } catch (InputFileException e) {
            assert(false);
        }
    }

    @Test
    public void testE5File() {
        try {
            idaStarTestHelper(_file5);
            assertTrue(true);
        } catch (InputFileException e) {
            assert(false);
        }
    }

    @Test
    public void testInvalidFormat() {
        try {
            idaStarTestHelper(_invalidFormat);
            assert(false);
        } catch(InputFileException e) {
            assertEquals(e.getMessage(),"Invalid Format");
        }
    }

    @Test
    public void testMissingNodes() {
        try {
            idaStarTestHelper(_missingNode);
            assert(false);
        } catch(InputFileException e) {
            assertEquals(e.getMessage(),"Node has not been instantiated");
        }
    }

    /**
     * BVA testing of a negative edge weight in the input file.
     */
    @Test
    public void testNegativeEdgeWeight() {
        try {
            idaStarTestHelper(_negativeEdgeWeight);
            assert(false);
        } catch(InputFileException e) {
            assertEquals(e.getMessage(),"Invalid Format");
        }
    }

    /**
     * BVA testing of a negative node weight in the input file.
     */
    @Test
    public void testNegativeNodeWeight() {
        try {
            idaStarTestHelper(_negativeNodeWeight);
            assert(false);
        } catch(InputFileException e) {
            assertEquals(e.getMessage(),"Invalid Format");
        }
    }


    // TODO: Apply regex to output DOT files to check for the correct format
    @Test
    public void testOutputFileFormat() {

    }

}
