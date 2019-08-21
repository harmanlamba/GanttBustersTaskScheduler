package algorithm;

/**
 * Enum representing the type of algorithm to be used for the input problem
 */
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