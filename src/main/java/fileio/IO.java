package fileio;

import algorithm.common.utility.Utility;
import graph.GraphEdge;
import graph.GraphNode;

import java.io.File;
import java.util.List;
import java.util.Map;

import static algorithm.common.utility.Utility.printUsage;

public class IO implements IIO {

    private Read _read;
    private Write _write;
    private int _numberOfProcessorsForParallelAlgorithm;
    private int _numberOfProcessorsForTask;

    public IO(String[] input) {
        validateAndParseInput(input);
    }

    private void validateAndParseInput(String[] args) {
        // Checks for minimum number of required parameters
        if (args.length < 2) {
            printUsage();
            System.exit(401);
        }

        _numberOfProcessorsForParallelAlgorithm = 0;
        _numberOfProcessorsForTask = 1;
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
            System.err.println("Invalid file format. Accepted: .dot files");
            printUsage();
            System.exit(401);
        }


        try {
            _numberOfProcessorsForTask = Integer.parseInt(args[1]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.err.println("Invalid number of processors to allocate tasks on");
            printUsage();
            System.exit(401);
        }

        for (int i = 2; i < args.length; i++) {
            if (args[i].equals("-p")) {
                try {
                    _numberOfProcessorsForParallelAlgorithm = Integer.parseInt(args[i + 1]);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.err.println("Invalid number of processors for parallelism");
                    printUsage();
                    System.exit(401);
                }
                i++;
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
                i++;
            }

            else {
                System.err.println("Invalid input arguments");
                printUsage();
                System.exit(401);
            }
        }

        _read = (Read) Utility.GuardNull(new Read(nameOfInputFile));
        _read.readFile();
        _write = (Write) Utility.GuardNull(new Write(nameOfInputFile, nameOfOutputFile));
    }

    public Map<String, GraphNode> getNodeMap() {
        return _read.getNodeMap();
    }

    public List<GraphEdge> getEdgeList() {
        return _read.getEdgeList();
    }

    public void write(Map<String, GraphNode> algorithmResultMap) {
        _write.writeToPath(algorithmResultMap);
    }

    public int getNumberOfProcessorsForParallelAlgorithm() {
        return _numberOfProcessorsForParallelAlgorithm;
    }

    public int getNumberOfProcessorsForTask() {
        return _numberOfProcessorsForTask;
    }
}