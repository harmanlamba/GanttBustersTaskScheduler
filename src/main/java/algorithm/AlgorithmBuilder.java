package algorithm;

import algorithm.common.utility.AlgorithmType;
import algorithm.idastarbase.IDAStarBase;
import algorithm.idastarparallel.IDAStarParallel;
import algorithm.sequential.Sequential;
import graph.Graph;

public class AlgorithmBuilder {

    public Algorithm AlgorithmBuilder(AlgorithmType type, Graph graph) {
        switch (type) {
            case Idstarbase:
                return new IDAStarBase(graph);
            case Idstarparallel:
                return new IDAStarParallel(graph);
            default:
                return new Sequential(graph);
        }
    }
}
