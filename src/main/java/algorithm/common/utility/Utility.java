package algorithm.common.utility;

public class Utility {

    /*
    Utility method used to check for nulls before and object gets assigned. Mainly to be used for fields.
     */
    public static Object GuardNull(Object object){
        if(object != null) {
            return object;
        }else{
            throw new IllegalArgumentException("Argument is null");
        }
    }
}
