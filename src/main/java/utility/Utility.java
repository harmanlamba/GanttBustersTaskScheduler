package utility;

/**
 * Provides the utility methods which will be used throughtout the application
 */
public class Utility {

    /**
     * Utility method used to check for nulls before and object gets assigned. Mainly to be used for fields.
     * @param object the object which will be null checked
     * @return returns the object if it is not null
     */
    public static Object GuardNull(Object object){
        if(object != null) {
            return object;
        }else{
            throw new IllegalArgumentException("Argument is null");
        }
    }

    /**
     * Prints the correct usage of the command line arguments
     */
    public static void printUsage() {
        System.err.println("Usage: ");
        System.err.println("java -jar scheduler.jar INPUT.dot P [OPTION]");
        System.err.println("INPUT.dot    a task graph with integer weights in dot format");
        System.err.println("P            number of processors to schedule the INPUT graph on\n\n");
        System.err.println("Optional:");
        System.err.println("-p N        use N cores for execution in parallel (default is sequential)");
        System.err.println("-v          visualise the search");
        System.err.println("-o OUTPUT   output file is named OUTPUT (default is INPUT-output.dot)");
    }

}
