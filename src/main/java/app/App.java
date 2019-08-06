package app;

import algorithm.Algorithm;
import algorithm.AlgorithmBuilder;
import algorithm.common.utility.AlgorithmType;
import fileio.IO;
import graph.Graph;

import java.nio.file.Paths;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        for(int i=0;i< args.length;i++) {
            System.out.println(args[i]);
        }
        IO io = new IO("example.dot", "hello.dot");
        Graph graph = new Graph(io.getNodeMap(), io.getEdgeList());
        AlgorithmBuilder algorithmBuilder = new AlgorithmBuilder(AlgorithmType.SEQUENTIAL, graph, 1, 0);
        Algorithm algorithm = algorithmBuilder.getAlgorithm();
        algorithm.solve();
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
