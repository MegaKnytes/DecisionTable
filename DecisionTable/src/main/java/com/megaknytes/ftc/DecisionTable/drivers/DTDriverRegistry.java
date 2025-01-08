package com.megaknytes.ftc.DecisionTable.drivers;

import android.content.Context;

import com.megaknytes.ftc.DecisionTable.DTProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import dalvik.system.DexFile;

/**
 * DTDriverRegistry is responsible for discovering and registering driver classes that implement the DTPDriver interface
 */
public class DTDriverRegistry {

    private static final Logger LOGGER = Logger.getLogger(DTDriverRegistry.class.getName());
    /**
     * A list of packages to ignore when scanning for driver classes
     */
    private static final List<String> IGNORED_PACKAGES = new ArrayList<>(Arrays.asList(
            "java",
            "android",
            "com.sun",
            "com.vuforia",
            "com.google",
            "kotlin",
            "com.qualcomm",
            "com.journeyapps"
    ));

    /**
     * Scans the application's dex file for classes that implement the DTPDriver interface and returns them in a map
     *
     * @param context The application context used to access the dex file
     * @return A map of driver class names to their corresponding Class objects
     */
    public static HashMap<String, Class<? extends DTPDriver>> getClassesWithInstanceOf(Context context) {
        // Create a map to store the found driver classes
        HashMap<String, Class<? extends DTPDriver>> driverClasses = new HashMap<>();

        try {
            // Attempt to load the Application Dex File
            DexFile dexFile = new DexFile(context.getPackageCodePath());
            // Iterate through all classes in the dex file
            for (String className : Collections.list(dexFile.entries())) {
                // Skip classes in ignored packages
                boolean skip = false;
                for (String ignoredPackage : IGNORED_PACKAGES){
                    if (className.startsWith(ignoredPackage)){
                        skip = true;
                        break;
                    }
                }
                if (skip){
                    continue;
                }

                try {
                    // Attempt to load the class
                    Class<?> configClass = Class.forName(className, false, DTProcessor.class.getClassLoader());
                    // Check if the class implements the DTPDriver interface
                    if (Arrays.asList(configClass.getInterfaces()).contains(DTPDriver.class)) {
                        // If the class implements the DTPDriver interface, add it to the map
                        Class<? extends DTPDriver> driverClass = configClass.asSubclass(DTPDriver.class);
                        driverClasses.put(configClass.getSimpleName(), driverClass);
                    }
                    // Catch any errors that may occur while loading the classes
                    // I'm not sure exactly why this is necessary, but it seems to work
                } catch (ClassNotFoundException | NoClassDefFoundError ignored) {}
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error while reading classes", e);
        }
        return driverClasses;
    }
}