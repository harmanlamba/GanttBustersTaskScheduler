package exception;

/**
 * InvalidInputArgumentException - throws the exception if user's command inputs are incorrect according to IO class
 */
public class InvalidInputArgumentException extends Exception {
    public InvalidInputArgumentException(String message) {
        super(message);
    }

    public InvalidInputArgumentException() {
        super("Invalid input arguments\n");
    }
}
