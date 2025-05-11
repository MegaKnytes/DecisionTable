package org.megaknytes.ftc.decisiontable.core.drivers;

import android.content.Context;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import dalvik.system.DexFile;

public class DTDriverRegistry {
    private static final Logger LOGGER = Logger.getLogger(DTDriverRegistry.class.getName());
    private static final List<String> IGNORED_PACKAGES = Arrays.asList(
            "java", "android", "com.sun", "com.vuforia", "com.google", "kotlin", "com.qualcomm", "com.journeyapps"
    );

    public static HashMap<String, Class<? extends DTDevice>> getClassesWithInstanceOf(Context context) {
        HashMap<String, Class<? extends DTDevice>> driverClasses = new HashMap<>();
        try {
            DexFile dexFile = new DexFile(context.getPackageCodePath());
            for (String className : Collections.list(dexFile.entries())) {
                if (IGNORED_PACKAGES.stream().anyMatch(className::startsWith)) continue;

                try {
                    Class<?> configClass = Class.forName(className, false, DTDriverRegistry.class.getClassLoader());
                    if (DTDevice.class.isAssignableFrom(configClass)
                            && !configClass.isInterface()
                            && configClass.isAnnotationPresent(Enabled.class)) {
                        @SuppressWarnings("unchecked")
                        Class<? extends DTDevice> driverClass = (Class<? extends DTDevice>) configClass;
                        driverClasses.put(configClass.getSimpleName(), driverClass);
                    }
                } catch (ClassNotFoundException | NoClassDefFoundError ignored) {}
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error while reading classes", e);
        }
        return driverClasses;
    }
}
