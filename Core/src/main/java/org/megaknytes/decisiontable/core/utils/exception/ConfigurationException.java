package org.megaknytes.decisiontable.core.utils.exception;

/**
 * Thrown when a Decision Table or System Configuration is invalid or cannot be loaded
 */
public class ConfigurationException extends RuntimeException {

    public ConfigurationException(String message) {
        super(message);
    }
}