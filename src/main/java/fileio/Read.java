package fileio;

import algorithm.common.utility.Utility;
import graph.GraphEdge;
import graph.GraphNode;
import app.App;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Read {
    private BufferedReader _bufferedReader;
    private Map<String, GraphNode> _vertexMap;
    private List<GraphEdge> _edgeList;
    private String _filePath;
    private static final String RGX_NODE = "\t([a-zA-Z0-9]+)\t \\[Weight=([0-9]+)];";
    private static final String RGX_EDGE = "\t([a-zA-Z0-9]+) -> ([a-zA-Z0-9]+)\t \\[Weight=([0-9]+)];";
    private static final String RGX_FIRST_LINE = "digraph \".*\" \\{";
    private static final String RGX_LAST_LINE =  "}";

    public Read(String filePath) {
        _vertexMap = (Map<String, GraphNode>) Utility.GuardNull(new HashMap<>());
        _edgeList = (List<GraphEdge>) Utility.GuardNull(new ArrayList<>());
        _filePath = filePath;

        try {
            _bufferedReader = new BufferedReader(new FileReader(_filePath));
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + _filePath);
            Utility.printUsage();
            System.exit(404);
        }
    }

    /**
     * readFile - loads and reads the .dot file from specified path, and runs makeNodeEdge on each relevant line
     */
    public void readFile() {
        try {
            //Need to take command line arguments so we take in FileReader(args.toString())
            String currentLine = _bufferedReader.readLine();
            String nextLine = _bufferedReader.readLine();
            boolean hasStarted = false;

            while (currentLine!= null) {
                if (!hasStarted) {
                    hasStarted = true;
                    if (!checkLine(RGX_FIRST_LINE, currentLine)) {
                        //TODO: make file invalid format exception, with system.exit
                    }
                } else if (nextLine == null) {
                    // Last line
                    if (!currentLine.equals("}")) {
                        //TODO: make file invalid format exception, with system.exit
                    }
                } else {
                    //In between lines
                    makeNodeEdge(currentLine);
                }

                currentLine = nextLine;
                nextLine = _bufferedReader.readLine();
            }
        } catch (IOException e) {
            System.err.println("File could not be read: " + _filePath);
            Utility.printUsage();
            System.exit(406);
        }
    }

    private boolean checkLine(String regex, String line) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * makeNodeEdge - takes line from .dot file, and extracts the relevant graph info (Vertex+Weight) or (Vertex edges+Weight).
     * Uses regex constants (vertex and edge regex).
     * @param line - line read from readFile
     */
    private void makeNodeEdge(String line) {
        Pattern patternNode = Pattern.compile(RGX_NODE);
        Pattern patternEdge = Pattern.compile(RGX_EDGE);

        Matcher matcherNode = patternNode.matcher(line);
        Matcher matcherEdge = patternEdge.matcher(line);

        if (matcherNode.matches()) {
            _vertexMap.put(matcherNode.group(1), new GraphNode(matcherNode.group(1), Integer.parseInt(matcherNode.group(2))));
        } else if (matcherEdge.matches()) {
            //Retrieve source dest node and add weighting
            GraphNode node1 = _vertexMap.get(matcherEdge.group(1));
            GraphNode node2 = _vertexMap.get(matcherEdge.group(2));
            if (node1 == null || node2 == null) {
                //TODO: exception 
                System.err.println("Vertex does not exist.");
                System.exit(407);
            }
            _edgeList.add(new GraphEdge(node1, node2, Integer.parseInt(matcherEdge.group(3))));
        } else {
            //TODO: make file invalid format exception, with system.exit

        }
    }

    /**
     * getNodeList - returns the list of nodes from input file
     * @return List of graph nodes
     */
    public Map<String, GraphNode> getNodeMap() {
        return _vertexMap;
    }

    /**
     * getEdgeList - returns the list of edges from input file
     * @return List of graph edges
     */
    public List<GraphEdge> getEdgeList() {
        return _edgeList;
    }
}
