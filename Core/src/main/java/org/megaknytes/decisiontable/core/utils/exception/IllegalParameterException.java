package org.megaknytes.decisiontable.core.utils.exception;

/**
 * Thrown when a parameter or internal variable is referenced, registered, or resolved incorrectly
 */
public class IllegalParameterException extends ConfigurationException {

    public IllegalParameterException(String message) {
        super(message);
    }
}