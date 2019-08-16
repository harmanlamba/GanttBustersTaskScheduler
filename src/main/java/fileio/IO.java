package fileio;

import exception.InputFileException;
import exception.InvalidInputArgumentException;
import javafx.application.Application;
import utility.Utility;
import graph.GraphEdge;
import graph.GraphNode;
import visualisation.FXApplication;

import static utility.Utility.printUsage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

/**
 * The class is responsible for all the IO that has to be done. This includes but is not limited to reading of the file,
 * input checking of the file to ensure that the file is correct, and writing the output.
 */
public class IO implements IIO {

    //Fields Declaration
    private Read _read;
    private Write _write;
    private int _numberOfProcessorsForParallelAlgorithm;
    private int _numberOfProcessorsForTask;
    private DisplayMode _visualisationState = DisplayMode.COMMAND_LINE;

    public IO(String[] input) throws InputFileException {
        try {
            //Run the reader in order to validate the input and parse it to the Algorithm class
            validateAndParseInput(input);
        } catch (FileNotFoundException | InvalidInputArgumentException e) {
            System.err.println(e.getMessage());
            printUsage(); //Printing the usage of the command correctly in the case of an exception being thrown
        }
    }

    /**
     * validateAndParseInput - Get console inputs and validate if valid user input
     * @param args - user input from console
     * @throws  FileNotFoundException - Can throw the exception in the case that the specified file is not found or
     * @throws  InvalidInputArgumentException - Can throw the exception if user's command inputs are invalid
     */
    private void validateAndParseInput(String[] args) throws FileNotFoundException, InvalidInputArgumentException, InputFileException {
        // Checks for minimum number of required parameters
        if (args.length < 2) {
            printUsage();
        }

        //Instantiate initial fields for parsing
        _numberOfProcessorsForParallelAlgorithm = 0;
        _numberOfProcessorsForTask = 1;
        String nameOfInputFile = args[0];
        String nameOfOutputFile = "";
        String parentPath = ".";

        // Created an automated out - check for exists and parse appropriate filename
        File file = new File(args[0]);
        if (file.exists()) {
            File parentFolder = file.getParentFile();
            if(parentFolder != null){
                parentPath = parentFolder.getAbsolutePath();
            }
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
            if (_numberOfProcessorsForTask < 1) {
                throw new InvalidInputArgumentException();
            }
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
                        if (_numberOfProcessorsForParallelAlgorithm < 1 || _numberOfProcessorsForParallelAlgorithm > Runtime.getRuntime().availableProcessors()) {
                            throw new InvalidInputArgumentException("Invalid input arguments - number of cores for parallel execution must be between 1-" + Runtime.getRuntime().availableProcessors() + "\n");
                        }
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                        System.err.println("Invalid number of processors for parallelism");
                        printUsage();
                    }
                    i++;
                    break;
                //Use visualization GUI with given file
                case "-v":
                    //TODO Add visualisation stuff
                    _visualisationState=DisplayMode.VISUALISE;
                    break;
                //Set output file name (if needed)
                case "-o":
                    try {
                        nameOfOutputFile = args[i + 1].contains(".") ? args[i + 1] : args[i + 1] + ".dot";

                        if (!args[i+1].contains("/")) {
                            nameOfOutputFile = parentPath + "/" + nameOfOutputFile;
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.err.println("Invalid output file name");
                        printUsage();
                    }
                    i++;
                    break;
                default:
                    throw new InvalidInputArgumentException(); //else invalid argument
            }
        }

        //Applying guardnull on read and write input - checking for exist of nulls
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

    public Map<String, GraphNode> getAlgorithmResultMap() {
        return _write.getAlgorithmResultMap();
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

    public DisplayMode getStateOfVisualisation(){
        return _visualisationState;
    }
}