package fileio;


import graph.GraphEdge;
import graph.GraphNode;
import graph.OutputGraphNode;

import java.util.List;
import java.util.Map;

public interface IIO {

    void readFile();

    Map<String, GraphNode> getNodeMap();

    List<GraphEdge> getEdgeList();

    //String lineRegex(String line, String regex);

    void writeFile();

}
