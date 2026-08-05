package org.megaknytes.decisiontable.core.utils.exceptions;

public class NoRegisteredParserException extends RuntimeException {
    public NoRegisteredParserException(String message) {
        super(message);
    }
}