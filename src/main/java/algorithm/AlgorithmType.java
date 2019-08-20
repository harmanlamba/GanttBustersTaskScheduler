package algorithm;

public enum AlgorithmType {
    SEQUENTIAL("Sequential"),
    IDASTARPARRALLEL("Parallelised IDA*"),
    IDASTARBASE("IDA*");

    private String _name;

    private AlgorithmType(final String name) {
        _name = name;
    }

    public String getName() {
        return _name;
    }
}