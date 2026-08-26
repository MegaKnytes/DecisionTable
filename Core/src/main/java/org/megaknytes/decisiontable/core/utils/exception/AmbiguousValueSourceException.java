package org.megaknytes.decisiontable.core.utils.exception;

/**
 * Thrown when an XML element specifies both, or neither, of a value and a reference
 */
public class AmbiguousValueSourceException extends ConfigurationException {

    public AmbiguousValueSourceException(String message) {
        super(message);
    }
}