package org.megaknytes.ftc.decisiontable.core;


/**
 * Class cannot be instantiated. All methods are static for logging purposes!
 */
public class DTPLogger {

    // A flag to enable or disable logging
    private static boolean isDebugEnabled = false;
    private static boolean isInfoEnabled = true;
    private static boolean isErrorEnabled = true;

    // Constructor is private on purpose! This class scopes static logging methods but is not instantiable.
    private DTPLogger() {}

    // Method to enable logging
    public static void enableDebugLevel(boolean enable) {
        isDebugEnabled = enable;
    }

    public void enableInfoLevel(boolean enable) {
        isInfoEnabled = enable;
    }

    private void enableErrorLevel(boolean enable) {
        isErrorEnabled = enable;
    }

    // Method to log the message
    public static void debug(String message) {
        if (isDebugEnabled) {
            System.out.print(message);
        }
    }

    public static void error(String message) {
        if (isErrorEnabled) {
            System.out.print(message);
        }
    }

    public static void info(String message) {
        if (isInfoEnabled) {
            System.out.print(message);
        }
    }

}