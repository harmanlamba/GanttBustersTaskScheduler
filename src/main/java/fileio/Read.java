package fileio;

import algorithm.common.utility.Utility;
import exception.NodeNotExistException;
import graph.GraphEdge;
import graph.GraphNode;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

//TODO: Class comments
public class Read {

    private BufferedReader _bufferedReader;
    private Map<String, GraphNode> _vertexMap;
    private List<GraphEdge> _edgeList;
    private String _filePath;

    //Regex constants to compare against file lines
    private static final String RGX_NODE = "\t([a-zA-Z0-9]+)\t \\[Weight=([0-9]+)];";
    private static final String RGX_EDGE = "\t([a-zA-Z0-9]+) -> ([a-zA-Z0-9]+)\t \\[Weight=([0-9]+)];";
    private static final String RGX_FIRST_LINE = "digraph \".*\" \\{";

    public Read(String filePath) {
        _vertexMap = (Map<String, GraphNode>) Utility.GuardNull(new HashMap<>()); //hold list of vertices from file
        _edgeList = (List<GraphEdge>) Utility.GuardNull(new ArrayList<>()); //hold list of edges from file
        _filePath = filePath; //retrieve user input file path

        try {
            _bufferedReader = new BufferedReader(new FileReader(_filePath)); //create buffer reader to read input filepath
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + _filePath);
            Utility.printUsage();
            System.exit(404);
        }
    }

    /**
     * readFile - loads and reads the .dot file from specified path, and runs makeNodeEdge on each relevant line
     */
    public void readFile() throws PatternSyntaxException {
        try {
            //Need to take command line arguments so we take in FileReader(args.toString())
            String currentLine = _bufferedReader.readLine();
            String nextLine = _bufferedReader.readLine();
            boolean hasStarted = false; //First line flag

            while (currentLine!= null) {
                if (!hasStarted) {
                    //Get first line and check if valid
                    hasStarted = true;
                    if (!checkLine(RGX_FIRST_LINE, currentLine)) {
                        throw new PatternSyntaxException("Invalid file format - first line :", RGX_FIRST_LINE, 0);
                    }
                } else if (nextLine == null) {
                    // Get last line and check if valid
                    if (!currentLine.equals("}")) {
                        throw new PatternSyntaxException("Invalid file format - last line :", "{", 1);
                    }
                } else {
                    //In between lines
                    makeNodeEdge(currentLine);
                }

                //Increment lines (i, i+1)
                currentLine = nextLine;
                nextLine = _bufferedReader.readLine();
            }
        } catch (IOException e) {
            System.err.println("File not found: " + _filePath);
            Utility.printUsage();
            System.exit(404);
        } catch (NodeNotExistException e) {
            System.err.println("Node does not exist in file");
            Utility.printUsage();
            System.exit(420);
        } catch (PatternSyntaxException e) {
            System.err.println("File format is incorrect");
            Utility.printUsage();
            System.exit(620);
        }
    }

    /**
     * checkLine - check each middle input line in the file depending on node/edge
     * @param regex - regex for either node or edge format line
     * @param line - individual line to compare regex with
     * @return Boolean for checking if line is matching appropriate regex pattern
     */
    private boolean checkLine(String regex, String line) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);
        return matcher.matches();
    }

    /**
     * makeNodeEdge - takes line from .dot file, and extracts the relevant graph info (Vertex+Weight) or (Vertex edges+Weight).
     * Uses regex constants (vertex and edge regex).
     * @param line - line read from readFile
     */
    private void makeNodeEdge(String line) throws NodeNotExistException, PatternSyntaxException {
        Pattern patternNode = Pattern.compile(RGX_NODE);
        Pattern patternEdge = Pattern.compile(RGX_EDGE);

        Matcher matcherNode = patternNode.matcher(line);
        Matcher matcherEdge = patternEdge.matcher(line);

        if (matcherNode.matches()) {
            //If regex compared with node line is correct, add to vertex map
            _vertexMap.put(matcherNode.group(1), new GraphNode(matcherNode.group(1), Integer.parseInt(matcherNode.group(2))));
        } else if (matcherEdge.matches()) {
            //Else if regex compare with edge line is correct, add to edge list
            GraphNode node1 = _vertexMap.get(matcherEdge.group(1));
            GraphNode node2 = _vertexMap.get(matcherEdge.group(2));

            //Throw nodenotexist exception for if any node is currently not shown in edge
            if (node1 == null || node2 == null) {
                throw new NodeNotExistException("Node(s) have not been instantiated before an edge to the node has been created");
            }
            _edgeList.add(new GraphEdge(node1, node2, Integer.parseInt(matcherEdge.group(3))));
        } else {
            //Otherwise, line + file format is incorrect
            throw new PatternSyntaxException("Invalid file format - middle lines:", RGX_EDGE, 2);
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
