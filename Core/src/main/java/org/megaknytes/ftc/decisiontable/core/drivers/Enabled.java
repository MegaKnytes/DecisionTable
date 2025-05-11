package org.megaknytes.ftc.decisiontable.core.drivers;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to indicate that a decision table driver is enabled and should be processed.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Enabled {
}