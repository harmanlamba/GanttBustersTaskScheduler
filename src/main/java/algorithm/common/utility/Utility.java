package algorithm.common.utility;

public class Utility {

    public static Object GuardNull(Object object){
        if(object != null) {
            return object;
        }else{
            throw new IllegalArgumentException("Argument is null");
        }
    }
}
