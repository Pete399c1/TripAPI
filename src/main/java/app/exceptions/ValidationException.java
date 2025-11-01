package app.exceptions;

/**
 * Is: Cached in the controller and extends runtime so unchecked
 * no need: For throw in the methods
 * Purpose: To handle validation exceptions in the API
 * Author: Peter Marcher
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
