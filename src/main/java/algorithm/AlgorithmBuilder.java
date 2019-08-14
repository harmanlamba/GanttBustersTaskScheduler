package algorithm;

import algorithm.idastarbase.IDAStarBase;
import algorithm.idastarparallel.IDAStarParallel;
import algorithm.sequential.Sequential;
import graph.Graph;

/**
 * Utilises the builder design pattern to create an instantiation of an abstract algorithm, depending on the enum type
 * inputted
 */
public class AlgorithmBuilder {

    /**
     Creates and returns the new instantiation of the algorithm, depending on the number of processors to schedule tasks onto
     * @param graph is a graph of the network
     * @param numProcTask is the number of processors that the tasks needed to be scheduled onto
     * @param numProcParallel is the number of processors the algorithm should be working on
     */
    public static Algorithm getAlgorithm(Graph graph, int numProcTask, int numProcParallel) {
        switch (numProcTask) {
            case 1:
                return new Sequential(graph, numProcTask, numProcParallel);
            default:
                if(numProcParallel > 1) {
                    return new IDAStarParallel(graph, numProcTask, numProcParallel);
                }else {
                    return new IDAStarBase(graph, numProcTask, numProcParallel);
                }
        }
    }
}
