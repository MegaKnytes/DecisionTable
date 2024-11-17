package com.MegaKnytes.DecisionTable.drivers;

import android.content.Context;

import com.MegaKnytes.DecisionTable.DTProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import dalvik.system.DexFile;


public class DTDriverRegistry {

    private static final Logger LOGGER = Logger.getLogger(DTDriverRegistry.class.getName());
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

    public static HashMap<String, Class<? extends DTPDriver>> getClassesWithInstanceOf(Context context) {
        HashMap<String, Class<? extends DTPDriver>> driverClasses = new HashMap<>();

        try {
            DexFile dexFile = new DexFile(context.getPackageCodePath());
            for (String className : Collections.list(dexFile.entries())) {
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
                    Class<?> configClass = Class.forName(className, false, DTProcessor.class.getClassLoader());
                    if (Arrays.asList(configClass.getInterfaces()).contains(DTPDriver.class)) {
                        //TODO: Fix Unchecked Cast - Working for now
                        Class<? extends DTPDriver> driverClass = configClass.asSubclass(DTPDriver.class);
                        driverClasses.put(configClass.getSimpleName(), driverClass);
                    }
                } catch (ClassNotFoundException | NoClassDefFoundError ignored) {
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error while reading classes", e);
        }
        return driverClasses;
    }
}