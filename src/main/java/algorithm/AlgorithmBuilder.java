package algorithm;

import algorithm.common.utility.AlgorithmType;
import algorithm.idastarbase.IDAStarBase;
import algorithm.idastarparallel.IDAStarParallel;
import algorithm.sequential.Sequential;
import graph.Graph;

/**
 * Utilises the builder design pattern to create an instantiation of an abstract algorithm, depending on the enum type
 * inputted
 */
public class AlgorithmBuilder {

    private Algorithm _algorithm;

    /**
     * Creates and stores the new instantiation of the algorithm, with the supplied fields, in the _algorithm field
     * @param type the enum type which is to be instantiated
     * @param graph is a graph of the network
     * @param numProcTask is the number of processors that the tasks needed to be scheduled onto
     * @param numProcParallel is the number of processors the algorithm should be working on
     */
    public AlgorithmBuilder(AlgorithmType type, Graph graph, int numProcTask, int numProcParallel) {
        switch (type) {
            case IDASTARBASE:
                _algorithm = new IDAStarBase(graph, numProcTask, numProcParallel);
                break;
            case IDASTARPARALLEL:
                _algorithm = new IDAStarParallel(graph, numProcTask, numProcParallel);
                break;
            default:
                _algorithm = new Sequential(graph, numProcTask, numProcParallel);
        }
    }

    /**
     * Returns the instantiated algorithm
     * @return the instantiated algorithm
     */
    public Algorithm getAlgorithm() {
        return _algorithm;
    }
}
