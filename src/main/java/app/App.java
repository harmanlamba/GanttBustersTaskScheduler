package app;

import algorithm.Algorithm;
import algorithm.AlgorithmBuilder;
import algorithm.common.utility.AlgorithmType;
import exception.InputFileException;
import fileio.IIO;
import fileio.IO;
import graph.Graph;
import utility.Utility;

/**
 * App - takes command user input, reads file and creates a JGraphT from the dot file. Execute algorithm onto graph, and write onto output path.
 */
public class App
{
    public static void main( String[] args ) {
        
        try {
            IIO io = new IO(args);
            Graph graph = new Graph(io.getNodeMap(), io.getEdgeList()); //create graph from nodes and edges
            AlgorithmBuilder algorithmBuilder = new AlgorithmBuilder(AlgorithmType.SEQUENTIAL, graph,
                    io.getNumberOfProcessorsForTask(), io.getNumberOfProcessorsForParallelAlgorithm());

            Algorithm algorithm = algorithmBuilder.getAlgorithm(); //call algorithm graph
            io.write(algorithm.solve()); //write onto output dot file
        } catch (InputFileException e) {
            Utility.printUsage();
        }

    }

// DON'T REMOVE THIS
//    public static void visualisationSample() {
//        //launch(args);
//
//        //#omp parallel for
//        for(int i = 0; i < 10; i++){
//            System.out.println("Hello world from parallel code, executed by " + Pyjama.omp_get_thread_num());
//        }
//
//        System.out.println("Finished Execution");
//    }
}
