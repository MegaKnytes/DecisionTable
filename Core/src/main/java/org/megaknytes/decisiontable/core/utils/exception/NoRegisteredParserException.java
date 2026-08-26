package org.megaknytes.decisiontable.core.utils.exception;

/**
 * Thrown when no ValueTypeParser is registered for a requested type
 */
public class NoRegisteredParserException extends RuntimeException {

    public NoRegisteredParserException(String message) {
        super(message);
    }
}