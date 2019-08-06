package algorithm;

import algorithm.common.utility.AlgorithmType;
import algorithm.idastarbase.IDAStarBase;
import algorithm.idastarparallel.IDAStarParallel;
import algorithm.sequential.Sequential;
import graph.Graph;

public class AlgorithmBuilder {

    private Algorithm _algorithm;


    public AlgorithmBuilder(AlgorithmType type, Graph graph, int numProcTask, int numProcParallel) {
        switch (type) {
            case IDASTARBASE:
                _algorithm = new IDAStarBase(graph, numProcTask, numProcParallel);
            case IDASTARPARALLEL:
                _algorithm = new IDAStarParallel(graph, numProcTask, numProcParallel);
            default:
                _algorithm = new Sequential(graph, numProcTask, numProcParallel);
        }
    }

    public Algorithm getAlgorithm() {
        return _algorithm;
    }
}
