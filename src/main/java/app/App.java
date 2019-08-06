package app;

import algorithm.Algorithm;
import algorithm.AlgorithmBuilder;
import algorithm.common.utility.AlgorithmType;
import fileio.IO;
import graph.Graph;

import java.io.File;
import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException {
        // Checks for minimum number of required parameters
        if (args.length < 2) {
            printUsage();
            System.exit(401);
        }

        int numberOfProcessorsForParallelAlgorithm = 0;
        int numberOfProcessorsForTask = 1;
        String nameOfInputFile = args[0];
        String nameOfOutputFile = "";
        String parentPath = "";

        // Created an automated out
        File file = new File(args[0]);
        if (file.exists()) {
            File parentFolder = file.getParentFile();
            parentPath = parentFolder.getAbsolutePath();
            String[] fileNameSplit = file.getName().split("\\.");
            nameOfInputFile = parentPath + "/" + file.getName();
            nameOfOutputFile = fileNameSplit[0] + "-output.dot";
            nameOfOutputFile = parentPath + "/" + nameOfOutputFile;
        } else {
            System.out.println("Invalid file format. Accepted: .dot files");
            printUsage();
            System.exit(401);
        }


        try {
            numberOfProcessorsForTask = Integer.parseInt(args[1]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.err.println("Invalid number of processors to allocate tasks on");
            printUsage();
            System.exit(401);
        }

        for (int i = 2; i < args.length; i++) {
            if (args[i].equals("-p")) {
                try {
                    numberOfProcessorsForParallelAlgorithm = Integer.parseInt(args[i + 1]);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.err.println("Invalid number of processors for parallelism");
                    printUsage();
                    System.exit(401);
                }
                i = i +1;
            }

             else if (args[i].equals("-v")) {
                //TODO Add visualisation stuff
            }

             else if (args[i].equals("-o")) {
                try {
                    nameOfOutputFile = args[i + 1].contains(".") ? args[i + 1] : args[i + 1] + ".dot";
                    nameOfOutputFile = parentPath + "/" + nameOfOutputFile;
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.err.println("Invalid output file name");
                    printUsage();
                    System.exit(401);
                }
                i = i + 1;
            }

             else {
                 System.err.println("Invalid input arguments");
                 printUsage();
                 System.exit(401);
            }
        }

        IO io = new IO(nameOfInputFile, nameOfOutputFile);
        Graph graph = new Graph(io.getNodeMap(), io.getEdgeList());
        AlgorithmBuilder algorithmBuilder = new AlgorithmBuilder(AlgorithmType.SEQUENTIAL, graph, numberOfProcessorsForTask, numberOfProcessorsForParallelAlgorithm);
        Algorithm algorithm = algorithmBuilder.getAlgorithm();
        io.write(algorithm.solve());

    }

    private static void printUsage() {
        System.err.println("Usage: ");
        System.err.println("java -jar scheduler.jar INPUT.dot P [OPTION]");
        System.err.println("INPUT.dot    a task graph with integer weights in dot format");
        System.err.println("P            number of processors to schedule the INPUT graph on\n\n");
        System.err.println("Optional:");
        System.err.println("-p N        use N cores for execution in parallel (default is sequential)");
        System.err.println("-v          visualise the search");
        System.err.println("-o OUTPUT   output file is named OUTPUT (default is INPUT-output.dot)");

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
