package fileio;

import utility.Utility;
import graph.GraphEdge;
import graph.GraphNode;
import static utility.Utility.printUsage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

//TODO: class comment
public class IO implements IIO {

    private Read _read;
    private Write _write;
    private int _numberOfProcessorsForParallelAlgorithm;
    private int _numberOfProcessorsForTask;

    public IO(String[] input) {
        try {
            validateAndParseInput(input);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            printUsage();
        }
    }

    /**
     * validateAndParseInput - Get console inputs and validate if valid user input
     * @param args - user input from console
     */
    private void validateAndParseInput(String[] args) throws FileNotFoundException {
        // Checks for minimum number of required parameters
        if (args.length < 2) {
            printUsage();
        }

        //Instantiate initial fields for parsing
        _numberOfProcessorsForParallelAlgorithm = 0;
        _numberOfProcessorsForTask = 1;
        String nameOfInputFile = args[0];
        String nameOfOutputFile = "";
        String parentPath = "";

        // Created an automated out - check for exists and parse appropriate filename
        File file = new File(args[0]);
        if (file.exists()) {
            File parentFolder = file.getParentFile();
            parentPath = parentFolder.getAbsolutePath();
            String[] fileNameSplit = file.getName().split("\\.");
            nameOfInputFile = parentPath + "/" + file.getName();
            nameOfOutputFile = fileNameSplit[0] + "-output.dot";
            nameOfOutputFile = parentPath + "/" + nameOfOutputFile;
        } else {
            throw new FileNotFoundException("File not found - please verify name");
        }

        //Set user input with number of processors
        try {
            _numberOfProcessorsForTask = Integer.parseInt(args[1]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.err.println("Invalid number of processors to allocate tasks on");
            printUsage();
        }

        //Assign number of processors to algorithm parallel
        for (int i = 2; i < args.length; i++) {
            switch (args[i]) {
                //Use multiple processors
                case "-p":
                    try {
                        _numberOfProcessorsForParallelAlgorithm = Integer.parseInt(args[i + 1]);
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                        System.err.println("Invalid number of processors for parallelism");
                        printUsage();
                    }
                    i++;
                    break;
                //Use visualization GUI with given file
                case "-v":
                    //TODO Add visualisation stuff
                    break;
                //Set output file name (if needed)
                case "-o":
                    try {
                        nameOfOutputFile = args[i + 1].contains(".") ? args[i + 1] : args[i + 1] + ".dot";
                        nameOfOutputFile = parentPath + "/" + nameOfOutputFile;
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.err.println("Invalid output file name");
                        printUsage();
                    }
                    i++;
                    break;
                default:
                    System.err.println("Invalid input arguments");
                    printUsage();
            }
        }

        //Apply guardnull on read and write input - checking for exist of nulls
        _read = (Read) Utility.GuardNull(new Read(nameOfInputFile));
        _read.readFile();
        _write = (Write) Utility.GuardNull(new Write(nameOfInputFile, nameOfOutputFile));
    }

    /**
     * getNodeMap - returns node map for graph nodes
     * @return map of nodes to assign to JGraphT instantiation
     */
    public Map<String, GraphNode> getNodeMap() {
        return _read.getNodeMap();
    }

    /**
     * getEdgeList - returns edge list for graph edges
     * @return list of edges to assign to JGraphT instantiation
     */
    public List<GraphEdge> getEdgeList() {
        return _read.getEdgeList();
    }

    /**
     * write - write resulting map of nodes to output file
     * @param algorithmResultMap - result map from algorithm result
     */
    public void write(Map<String, GraphNode> algorithmResultMap) {
        _write.writeToPath(algorithmResultMap);
    }

    /**
     * getNumberOfProcessorsForParallelAlgorithm - returns number of assigned processors of Parallel Algorithm
     * @return number of processors used
     */
    public int getNumberOfProcessorsForParallelAlgorithm() {
        return _numberOfProcessorsForParallelAlgorithm;
    }

    /**
     * getNumberOfProcessorsForTask - returns number of assigned tasks of Parallel Algorithm
     * @return number of tasks used
     */
    public int getNumberOfProcessorsForTask() {
        return _numberOfProcessorsForTask;
    }
}