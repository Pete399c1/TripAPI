package app.exceptions;

/**
 * Purpose: To handle validation exceptions in the API
 * Author: Peter Marcher
 */
public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}
