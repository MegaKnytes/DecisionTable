package org.megaknytes.decisiontable.core.utils.exception;

/**
 * Thrown when a resolved values type does not match its expected type
 */
public class TypeMismatchException extends RuntimeException {

    public TypeMismatchException(String message) {
        super(message);
    }
}