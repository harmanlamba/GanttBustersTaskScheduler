package exception;
/**
 * InputFileException - exception created for junit testing for System.exit(1). Read exceptions propagate upwards to app
 * to prevent System.exit exiting JVM, thus making Junit tests viable.
 */
public class InputFileException extends Exception {
    public InputFileException() {
        super("Exception Handled \n");
    }
    public InputFileException(String exceptionMessage) {
        super(exceptionMessage);
    }

}
