package exception;

public class EdgeAlreadyExistsException extends Exception {
    public EdgeAlreadyExistsException() {
        super("Duplicate edge detected. Please check the input file \n");
    }
}
