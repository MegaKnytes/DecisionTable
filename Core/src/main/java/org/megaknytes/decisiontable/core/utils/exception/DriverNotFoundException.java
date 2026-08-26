package org.megaknytes.decisiontable.core.utils.exception;

/**
 * Thrown when a referenced device has not been made available to the configuration
 */
public class DriverNotFoundException extends ConfigurationException {

    public DriverNotFoundException(String message) {
        super(message);
    }
}