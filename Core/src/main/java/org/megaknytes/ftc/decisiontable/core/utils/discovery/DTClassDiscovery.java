package org.megaknytes.ftc.decisiontable.core.utils.discovery;

import android.content.Context;

import com.qualcomm.ftccommon.FtcEventLoop;

import org.firstinspires.ftc.ftccommon.external.OnCreateEventLoop;
import org.megaknytes.ftc.decisiontable.core.drivers.DTDevice;
import org.megaknytes.ftc.decisiontable.core.drivers.DTDeviceExtended;
import org.megaknytes.ftc.decisiontable.core.utils.DisabledDTClass;
import org.megaknytes.ftc.decisiontable.core.xml.values.Value;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import dalvik.system.DexFile;

public class DTClassDiscovery {
    private static final Logger LOGGER = Logger.getLogger(DTClassDiscovery.class.getName());
    private static final DTClassDiscovery INSTANCE = new DTClassDiscovery();
    private static final Map<String, DTDevice> driverInstances = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Value<?>> valueParserInstances = new ConcurrentHashMap<>();
    private static final Set<String> IGNORED_PACKAGES = new HashSet<>(Arrays.asList(
            "android", "com.android", "com.google", "com.qualcomm.robotcore.wifi", "com.sun", "gnu.kawa.swingviews",
            "io.netty", "java", "kawa", "org.apache", "org.checkerframework", "org.firstinspires.ftc.robotcore.internal.android",
            "org.java_websocket", "org.slf4j", "org.threeten", "com.journeyapps"
    ));

    private DTClassDiscovery() {
    }

    @OnCreateEventLoop
    public static void onCreateEventLoop(Context context, FtcEventLoop eventLoop) {
        LOGGER.log(Level.INFO, "Event loop created, initializing DTUserRegistry...");
        try {
            INSTANCE.scanForEnabledDriverClasses(context);
            INSTANCE.scanForEnabledValueParsers(context);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load DexFile for scanning enabled drivers and value parsers.", e);
        }
        LOGGER.log(Level.INFO, "Available device drivers: " + driverInstances.keySet());
        LOGGER.log(Level.INFO, "Available value parsers: " + valueParserInstances.keySet());
        LOGGER.log(Level.INFO, "Finished scanning for enabled drivers and value parsers.");
    }

    public void scanForEnabledDriverClasses(Context context) throws IOException {
        LOGGER.log(Level.INFO, "Scanning for enabled driver classes...");
        driverInstances.clear();

        DexFile dexFile = new DexFile(context.getPackageCodePath());
        Enumeration<String> entries = dexFile.entries();

        while (entries.hasMoreElements()) {
            String className = entries.nextElement();

            if (IGNORED_PACKAGES.stream().anyMatch(className::startsWith)) {
                continue;
            }

            try {
                Class<?> configClass = Class.forName(className, false, DTClassDiscovery.class.getClassLoader());
                if (DTDeviceExtended.class.isAssignableFrom(configClass) && !configClass.isInterface() && !configClass.isAnnotationPresent(DisabledDTClass.class)) {
                    instantiateDriver(configClass);
                    continue;
                }
                if (DTDevice.class.isAssignableFrom(configClass) && !configClass.isInterface() && !configClass.isAnnotationPresent(DisabledDTClass.class)) {
                    instantiateDriver(configClass);
                }
            } catch (ClassNotFoundException | NoClassDefFoundError |
                     ExceptionInInitializerError ignored) {
            }
        }
    }

    public void scanForEnabledValueParsers(Context context) throws IOException {
        LOGGER.log(Level.INFO, "Scanning for enabled value parser classes...");
        valueParserInstances.clear();

        DexFile dexFile = new DexFile(context.getPackageCodePath());
        Enumeration<String> entries = dexFile.entries();

        while (entries.hasMoreElements()) {
            String className = entries.nextElement();

            if (IGNORED_PACKAGES.stream().anyMatch(className::startsWith)) {
                continue;
            }

            try {
                Class<?> valueParserClass = Class.forName(className, false, DTClassDiscovery.class.getClassLoader());

                if (Value.class.isAssignableFrom(valueParserClass) && !valueParserClass.isInterface() && !valueParserClass.isAnnotationPresent(DisabledDTClass.class)) {
                    Value<?> valueParserInstance = (Value<?>) valueParserClass.getDeclaredConstructor().newInstance();
                    valueParserInstances.put(valueParserInstance.getType(), valueParserInstance);
                }
            } catch (ClassNotFoundException | NoClassDefFoundError |
                     ExceptionInInitializerError ignored) {
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException |
                     InvocationTargetException e) {
                LOGGER.log(Level.WARNING, "Driver class " + className + " does not have a default constructor or has an incorrect access level, skipping.");
            }
        }
    }

    private void instantiateDriver(Class<?> configClass) {
        try {
            DTDevice driverInstance = (DTDevice) configClass.getDeclaredConstructor().newInstance();
            DTClassDiscovery.driverInstances.put(configClass.getSimpleName(), driverInstance);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            LOGGER.log(Level.WARNING, "Failed to instantiate driver class: " + configClass.getName() + ", reason: " + e.getMessage());
        }
    }

    public static DTClassDiscovery getInstance() {
        return INSTANCE;
    }

    public static Map<String, DTDevice> getDriverInstances() {
        return driverInstances;
    }

    public static Map<Class<?>, Value<?>> getValueParserClasses() {
        return valueParserInstances;
    }
}