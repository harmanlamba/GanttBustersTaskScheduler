package algorithm;

import algorithm.common.utility.AlgorithmType;
import algorithm.idastarbase.IDAStarBase;
import algorithm.idastarparallel.IDAStarParallel;
import algorithm.sequential.Sequential;
import graph.Graph;

public class AlgorithmBuilder {

    private Algorithm _algorithm;


    public AlgorithmBuilder(AlgorithmType type, Graph graph) {
        switch (type) {
            case Idstarbase:
                _algorithm = new IDAStarBase(graph);
            case Idstarparallel:
                _algorithm = new IDAStarParallel(graph);
            default:
                _algorithm = new Sequential(graph);
        }
    }

    public Algorithm getAlgorithm() {
        return _algorithm;
    }
}
