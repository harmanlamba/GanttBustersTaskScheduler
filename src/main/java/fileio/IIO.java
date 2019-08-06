package fileio;


import graph.GraphEdge;
import graph.GraphNode;

import java.util.List;
import java.util.Map;

public interface IIO {

    public Map<String, GraphNode> getNodeMap();
    public List<GraphEdge> getEdgeList();
    public void write(Map<String, GraphNode> algorithmResultMap);

}
