package org.megaknytes.decisiontable.ftc.discovery;

import android.content.Context;

import com.qualcomm.ftccommon.FtcEventLoop;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.ftccommon.external.OnCreateEventLoop;
import org.megaknytes.decisiontable.core.utils.device.Device;
import org.megaknytes.decisiontable.core.rule.value.ValueTypeParser;
import org.megaknytes.decisiontable.core.rule.value.ValueParserRegistry;
import org.megaknytes.decisiontable.core.utils.DisabledDTClass;

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

public class ClassDiscovery {
    private static final Logger LOGGER = Logger.getLogger(ClassDiscovery.class.getName());

    private static final Map<String, Device> DRIVER_INSTANCES = new ConcurrentHashMap<>();
    private static final ValueParserRegistry VALUE_PARSER_REGISTRY = new ValueParserRegistry();

    private static final Set<String> IGNORED_PACKAGES = new HashSet<>(Arrays.asList(
            "android",
            "com.android",
            "com.google",
            "com.qualcomm.robotcore.wifi",
            "com.sun",
            "gnu.kawa.swingviews",
            "io.netty",
            "java",
            "kawa",
            "org.apache",
            "org.checkerframework",
            "org.megaknytes.decisiontable.core",
            "org.firstinspires.ftc.robotcore.internal.android",
            "org.java_websocket",
            "org.slf4j",
            "org.threeten",
            "com.journeyapps"
    ));

    private ClassDiscovery() {}

    @OnCreateEventLoop
    public static void onCreateEventLoop(Context context, FtcEventLoop eventLoop) {
        Enumeration<String> dexEntries;

        try {
            dexEntries = new DexFile(context.getPackageCodePath()).entries();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load DexFile for scanning enabled drivers and value parsers.", e);
            throw new RuntimeException("Failed to load DexFile for scanning enabled drivers and value parsers");
        }

        LOGGER.log(Level.INFO, "======== Driver & Value Parser Class Discovery ========");

        DRIVER_INSTANCES.clear();

        while (dexEntries.hasMoreElements()) {
            String className = dexEntries.nextElement();

            if (IGNORED_PACKAGES.stream().anyMatch(className::startsWith)) {
                continue;
            }

            try {
                Class<?> dexClass = Class.forName(className, false, ClassDiscovery.class.getClassLoader());

                if (Device.class.isAssignableFrom(dexClass) && !dexClass.isInterface() && !dexClass.isAnnotationPresent(DisabledDTClass.class)) {
                    Device driverInstance = (Device) dexClass.getDeclaredConstructor().newInstance();
                    DRIVER_INSTANCES.put(driverInstance.getDeviceName(), driverInstance);
                }

                if (ValueTypeParser.class.isAssignableFrom(dexClass) && !dexClass.isInterface() && !dexClass.isAnnotationPresent(DisabledDTClass.class)) {
                    ValueTypeParser<?> parserInstance = (ValueTypeParser<?>) dexClass.getDeclaredConstructor().newInstance();
                    VALUE_PARSER_REGISTRY.registerParser(parserInstance);
                }
            } catch (ClassNotFoundException | NoClassDefFoundError | ExceptionInInitializerError ignored) {
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                LOGGER.log(Level.WARNING, "Class " + className + " does not have a default constructor or has an incorrect access level, skipping.");
                RobotLog.addGlobalWarningMessage("Class " + className + " does not have a default constructor or has an incorrect access level and has not been loaded");
            }
        }

        LOGGER.log(Level.INFO, "Device Drivers: " + DRIVER_INSTANCES.keySet());
        LOGGER.log(Level.INFO, "Value Parsers: " + VALUE_PARSER_REGISTRY.getRegisteredParsers().keySet());
    }

    public static Map<String, Device> getDriverInstances() {
        return DRIVER_INSTANCES;
    }

    public static ValueParserRegistry getValueParserRegistry() {
        return VALUE_PARSER_REGISTRY;
    }
}