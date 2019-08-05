package fileio;


import graph.GraphEdge;
import graph.GraphNode;

import java.util.List;

public interface IIO {

    void readFile();
    String lineRegex(String line, String regex);
    void writeFile(List<GraphNode> graphNodes, List<GraphEdge> graphEdge);

}
