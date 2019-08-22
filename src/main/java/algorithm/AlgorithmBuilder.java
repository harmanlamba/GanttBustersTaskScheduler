package algorithm;

import algorithm.bbastarbase.BBAStarBase;
import algorithm.idastarbase.IDAStarBase;
import algorithm.idastarparallel.IDAStarParallel;
import algorithm.sequential.Sequential;
import graph.Graph;

/**
 * Utilises the builder design pattern to create an instantiation of an abstract algorithm, depending on the enum type
 * inputted
 */
public class AlgorithmBuilder {

    private static AlgorithmBuilder _algorithmBuilder;
    public AlgorithmType _algorithmType;
    private Algorithm _algorithm;

    private AlgorithmBuilder() { }

    public static AlgorithmBuilder getAlgorithmBuilder() {
        if (_algorithmBuilder == null) {
            _algorithmBuilder = new AlgorithmBuilder();
        }
        return _algorithmBuilder;
    }

    /**
     Creates and returns the new instantiation of the algorithm, depending on the number of processors to schedule tasks onto
     * @param graph is a graph of the network
     * @param numProcTask is the number of processors that the tasks needed to be scheduled onto
     * @param numProcParallel is the number of processors the algorithm should be working on
     */
    public AlgorithmBuilder createAlgorithm(Graph graph, int numProcTask, int numProcParallel) {
        switch (numProcTask) {
            case 1:
                _algorithmType = AlgorithmType.SEQUENTIAL;
                _algorithm = new Sequential(graph, numProcTask, numProcParallel);
                break;
            default:
                if (numProcParallel > 1) {
                    _algorithmType = AlgorithmType.IDASTARPARRALLEL;
                    _algorithm = new IDAStarParallel(graph, numProcTask, numProcParallel);
                } else {
                    _algorithmType = AlgorithmType.IDASTARBASE;
                    _algorithm = new BBAStarBase(graph, numProcTask, numProcParallel);
                }
        }
        return _algorithmBuilder;
    }

    /**
     * getAlgorithm - getter method for the Algorithm instance
     * @return returns the Algorithm object itself
     */
    public Algorithm getAlgorithm() {
        return _algorithm;
    }

    /**
     * getAlgorithmType - getter method for the type of the Algorithm instance
     * @return returns an AlgorithmType enum
     */
    public AlgorithmType getAlgorithmType() {
        return _algorithmType;
    }
}
