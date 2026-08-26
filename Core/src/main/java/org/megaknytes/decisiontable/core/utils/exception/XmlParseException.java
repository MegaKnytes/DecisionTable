package org.megaknytes.decisiontable.core.utils.exception;

/**
 * Thrown when a Decision Table or System Configuration XML document cannot be parsed
 */
public class XmlParseException extends ConfigurationException {

    public XmlParseException(String message) {
        super(message);
    }
}