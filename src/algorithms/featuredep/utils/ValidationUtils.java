/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.featuredep.utils;


/**
 *
 * @author bennyl
 */
public class ValidationUtils {
    
    public static void validateNumberBetween(double n, double left, double right, String message){
        if (n < left || n > right) throw new ValidationException(message);
    }
    
    public static void validateNumberLessThen(double n, double lessThen, String message){
        if (n >= lessThen) throw new ValidationException(message);
    }
    
    public static class ValidationException extends RuntimeException{

        public ValidationException() {
        }

        public ValidationException(String message) {
            super(message);
        }

        public ValidationException(String message, Throwable cause) {
            super(message, cause);
        }

        public ValidationException(Throwable cause) {
            super(cause);
        }
        
    }
}
