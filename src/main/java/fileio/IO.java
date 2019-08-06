package fileio;

import graph.GraphEdge;
import graph.GraphNode;
import graph.OutputGraphNode;

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

public class IO implements IIO {
    private BufferedReader _bufferedReader;
    private Map<String, GraphNode> _vertexMap;
    private List<GraphEdge> _edgeList;
    private static final String RGX_NODE = "\t([a-zA-Z0-9]+)\t \\[Weight=([0-9]+)];";
    private static final String RGX_EDGE = "\t([a-zA-Z0-9]+) -> ([a-zA-Z0-9]+)\t \\[Weight=([0-9]+)];";

    public IO(String filePath) {
        _vertexMap = new HashMap<>();
        _edgeList = new ArrayList<>();

        try {
            _bufferedReader = new BufferedReader(new FileReader(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * readFile - loads and reads the .dot file from specified path, and runs makeNodeEdge on each relevant line
     */
    @Override
    public void readFile() {
        try {
            //Need to take command line arguments so we take in FileReader(args.toString())
            String currentLine = _bufferedReader.readLine();
            String nextLine = _bufferedReader.readLine();
            boolean hasStarted = false;

            while (currentLine!= null) {
                if (!hasStarted) {
                    //TODO: DEAL WITH START LINE
                    hasStarted = true;
                } else if (nextLine == null) {
                    //TODO: DEAL WITH END LINE
                } else {
                    //In between lines
                    makeNodeEdge(currentLine);
                }

                currentLine = nextLine;
                nextLine = _bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
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

        if (matcherNode.find()) {
            _vertexMap.put(matcherNode.group(1), new GraphNode(matcherNode.group(1), Integer.parseInt(matcherNode.group(2))));
        } else if (matcherEdge.find()) {
            //Retrieve source dest node and add weighting
            GraphNode node1 = _vertexMap.get(matcherEdge.group(1));
            GraphNode node2 = _vertexMap.get(matcherEdge.group(2));
            if (node1 == null || node2 == null) {
                System.out.println("Vertex does not exist!");
                System.exit(1);
            }
            _edgeList.add(new GraphEdge(node1, node2, Integer.parseInt(matcherEdge.group(3))));
        }
    }

    /**
     * getNodeList - returns the list of nodes from input file
     * @return List of graph nodes
     */
    @Override
    public Map<String, GraphNode> getNodeMap() {
        return _vertexMap;
    }

    /**
     * getEdgeList - returns the list of edges from input file
     * @return List of graph edges
     */
    @Override
    public List<GraphEdge> getEdgeList() {
        return _edgeList;
    }

    void writeFile() {
        Write write = Write(Map<GraphNode, OutputGraphNode> algoResult, String outputPath);
    }

}
