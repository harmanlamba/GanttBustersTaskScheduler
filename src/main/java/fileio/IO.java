package fileio;

import graph.Graph;
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
        _write = new Write(inputPath, outputPath);
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



}