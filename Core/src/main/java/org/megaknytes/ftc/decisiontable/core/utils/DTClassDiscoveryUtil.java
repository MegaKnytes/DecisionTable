package org.megaknytes.ftc.decisiontable.core.utils;

import android.content.Context;

import com.qualcomm.ftccommon.FtcEventLoop;

import org.firstinspires.ftc.ftccommon.external.OnCreateEventLoop;
import org.megaknytes.ftc.decisiontable.core.drivers.DTDevice;
import org.megaknytes.ftc.decisiontable.core.drivers.DisabledClass;
import org.megaknytes.ftc.decisiontable.core.xml.values.Value;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import dalvik.system.DexFile;


public class DTClassDiscoveryUtil {
    private static final Logger LOGGER = Logger.getLogger(DTClassDiscoveryUtil.class.getName());
    private static final DTClassDiscoveryUtil INSTANCE = new DTClassDiscoveryUtil();
    private static final Map<String, DTDevice> driverInstances = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Value<?>> valueParserInstances = new ConcurrentHashMap<>();

    private DTClassDiscoveryUtil() {}

    private static final List<String> IGNORED_PACKAGES = Arrays.asList(
            "android", "com.android", "com.google",  "com.qualcomm.robotcore.wifi", "com.sun", "gnu.kawa.swingviews",
            "io.netty", "java", "kawa", "org.apache", "org.checkerframework", "org.firstinspires.ftc.robotcore.internal.android",
            "org.java_websocket", "org.slf4j", "org.threeten", "com.journeyapps"
    );

    @OnCreateEventLoop
    public static void onCreateEventLoop(Context context, FtcEventLoop eventLoop) throws IOException {
        LOGGER.log(Level.INFO, "Event loop created, initializing DTUserRegistry...");
        try {
            INSTANCE.scanForEnabledDriverClasses(context);
            INSTANCE.scanForEnabledValueParsers(context);
        } catch (IOException e) {
            throw new IOException("An error occurred while loading the DexFile to scan for enabled drivers and value parsers.", e);
        }
    }

    /**
     * Gets a map of available value parsers with instances.
     *
     * @param context The application context
     * @return A map of parser names to parser instances
     */
    @SuppressWarnings("unchecked")
    public void scanForEnabledValueParsers(Context context) throws IOException {
        valueParserInstances.clear();

        DexFile dexFile = new DexFile(context.getPackageCodePath());
        Enumeration<String> entries = dexFile.entries();

        while (entries.hasMoreElements()) {
            String className = entries.nextElement();

            if (IGNORED_PACKAGES.stream().anyMatch(className::startsWith)) {
                continue;
            }

            try {
                Class<?> valueParserClass = Class.forName(className, false, DTClassDiscoveryUtil.class.getClassLoader());

                if (Value.class.isAssignableFrom(valueParserClass) && !valueParserClass.isInterface() && !valueParserClass.isAnnotationPresent(DisabledClass.class)) {
                    Value<?> valueParserInstance = (Value<?>) valueParserClass.getDeclaredConstructor().newInstance();
                    valueParserInstances.put(valueParserInstance.getType(), valueParserInstance);
                }
            } catch (ClassNotFoundException | NoClassDefFoundError | ExceptionInInitializerError ignored) {
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                LOGGER.log(Level.WARNING, "Driver class \" + className + \" does not have a default constructor or has an incorrect access level, skipping.");
            }
        }
    }

    public void scanForEnabledDriverClasses(Context context) throws IOException {
        driverInstances.clear();

        DexFile dexFile = new DexFile(context.getPackageCodePath());
        Enumeration<String> entries = dexFile.entries();

        while (entries.hasMoreElements()) {
            String className = entries.nextElement();

            if (IGNORED_PACKAGES.stream().anyMatch(className::startsWith)) {
                continue;
            }

            try {
                Class<?> configClass = Class.forName(className, false, DTClassDiscoveryUtil.class.getClassLoader());
                if (DTDevice.class.isAssignableFrom(configClass) && !configClass.isInterface() && !configClass.isAnnotationPresent(DisabledClass.class)) {
                    try {
                        @SuppressWarnings("unchecked")
                        DTDevice driverInstance = (DTDevice) configClass.newInstance();
                        driverInstances.put(configClass.getSimpleName(), driverInstance);
                    } catch (InstantiationException | IllegalAccessException e) {
                        LOGGER.log(Level.WARNING, "Failed to instantiate driver class: " + className + ", reason: " + e.getMessage());
                    }
                }
            } catch (ClassNotFoundException | NoClassDefFoundError | ExceptionInInitializerError ignored) {
            }
        }
    }

    public static DTClassDiscoveryUtil getInstance() {
        return INSTANCE;
    }

    public static Map<String, DTDevice> getDriverInstances() {
        return driverInstances;
    }

    public Map<Class<?>, Value<?>> getValueParserClasses() {
        return valueParserInstances;
    }
}