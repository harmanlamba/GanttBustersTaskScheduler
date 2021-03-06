package fileio;


import graph.GraphEdge;
import graph.GraphNode;

import java.util.List;
import java.util.Map;

/**
 * This interface is used by the IO class and defines the contract that must be used. This interface is used to essentially
 * allows us to change different implementations of IO in the future to increase efficiency if needed.
 */

public interface IIO {

    /*
    Gets the Map that is later used to write the IO file. Important aspect would be that it the key binds to the
    GraphNode entity
     */
    Map<String, GraphNode> getNodeMap();

    /*
    Gets the Edge List in order for the writer to use it and write it to the output file
     */
    List<GraphEdge> getEdgeList();

    /*
    This method explicitly calls on the writer to write to the output file. Note: The reader does not have
    a method in this interface and that is because it gets called in the constructor during the creation of the IO
    object.
     */
    void write(Map<String, GraphNode> algorithmResultMap);

    Map<String, GraphNode> getAlgorithmResultMap();

    /*
    Gets the number of processors on which to schedule tasks as specified by the input
     */
    int getNumberOfProcessorsForTask();

    /*
    Gets the number of processors on which to run the algorithm as specified by the input
     */
    int getNumberOfProcessorsForParallelAlgorithm();

    /*
    Gets file name for GUI text
     */
    String getFileName();

    /*
    Gets the display mode type as specified by the input
     */
    DisplayMode getStateOfVisualisation();

}
