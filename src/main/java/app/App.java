package app;

import algorithm.Algorithm;
import algorithm.AlgorithmBuilder;
import algorithm.common.utility.AlgorithmType;
import exception.InputFileException;
import fileio.IIO;
import fileio.IO;
import graph.Graph;
import javafx.application.Application;
import utility.Utility;
import visualisation.FXApplication;

/**
 * App - takes command user input, reads file and creates a JGraphT from the dot file. Execute algorithm onto graph, and write onto output path.
 */
public class App
{
    public static IIO _mainIO;

    public static void main( String[] args ) {
        
        try {
            _mainIO = new IO(args);
            if(!_mainIO.getStateOfVisualisation()){ // if false run natively
                Graph graph = new Graph(_mainIO.getNodeMap(), _mainIO.getEdgeList()); //create graph from nodes and edges
                AlgorithmBuilder algorithmBuilder = new AlgorithmBuilder(AlgorithmType.SEQUENTIAL, graph,
                        _mainIO.getNumberOfProcessorsForTask(), _mainIO.getNumberOfProcessorsForParallelAlgorithm());

                Algorithm algorithm = algorithmBuilder.getAlgorithm(); //call algorithm graph
                _mainIO.write(algorithm.solve()); //write onto output dot file
            }else{
                //FXApplication will take over
                Application.launch(FXApplication.class);
            }


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
