package fileio;

import graph.GraphEdge;
import graph.GraphNode;

import java.util.List;
import java.util.Map;

public class IO {

    private Read _read;
    private Write _write;

    public IO(String inputPath, String outputPath) {
        _read = new Read(inputPath);
        _read.readFile();
        //TODO: Make write object
    }

    public Map<String, GraphNode> getNodeMap() {
        return _read.getNodeMap();
    }

    public List<GraphEdge> getEdgeList() {
        return _read.getEdgeList();
    }

}