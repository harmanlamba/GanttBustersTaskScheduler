package app;

import algorithm.Algorithm;
import algorithm.AlgorithmBuilder;
import exception.InputFileException;
import fileio.DisplayMode;
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
            switch(_mainIO.getStateOfVisualisation()) {
                case COMMAND_LINE:
                    Graph graph = new Graph(_mainIO.getNodeMap(), _mainIO.getEdgeList()); //create graph from nodes and edges
                    Algorithm algorithm = AlgorithmBuilder.getAlgorithmBuilder().createAlgorithm(graph,
                            _mainIO.getNumberOfProcessorsForTask(), _mainIO.getNumberOfProcessorsForParallelAlgorithm()).getAlgorithm(); //call algorithm graph
                    _mainIO.write(algorithm.solve()); //write onto output dot file
                    break;
                default:
                    //FXApplication will take over
                    Application.launch(FXApplication.class);
            }


        } catch (InputFileException e) {
            Utility.printUsage();
        }

    }
}
