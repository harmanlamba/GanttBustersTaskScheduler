package exception;
/**
 * HandledException - exception created for junit testing for System.exit(1). Read exceptions propagate upwards to app
 * to prevent System.exit exiting JVM, thus making Junit tests viable.
 */
public class HandledException extends Exception {
    public HandledException() {
        super("Exception Handled \n");
    }
    public HandledException(String exceptionMessage) {
        super(exceptionMessage);
    }

}
