package org.megaknytes.decisiontable.core.utils.exception;

/**
 * Thrown when a target address string cannot be parsed.
 */
public class InvalidAddressException extends ConfigurationException {

    public InvalidAddressException(String message) {
        super(message);
    }
}