package org.megaknytes.ftc.decisiontable.core.utils.discovery;

import android.content.Context;

import com.qualcomm.ftccommon.FtcEventLoop;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.ftccommon.external.OnCreateEventLoop;
import org.megaknytes.ftc.decisiontable.core.drivers.DTDevice;
import org.megaknytes.ftc.decisiontable.core.drivers.DTDeviceEx;
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
    private static final Map<String, DTDevice> DRIVER_INSTANCES = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Value<?>> VALUE_PARSER_INSTANCES = new ConcurrentHashMap<>();
    private static final Set<String> IGNORED_PACKAGES = new HashSet<>(Arrays.asList(
            "android", "com.android", "com.google", "com.qualcomm.robotcore.wifi", "com.sun", "gnu.kawa.swingviews",
            "io.netty", "java", "kawa", "org.apache", "org.checkerframework", "org.firstinspires.ftc.robotcore.internal.android",
            "org.java_websocket", "org.slf4j", "org.threeten", "com.journeyapps"
    ));

    private DTClassDiscovery() {}

    @OnCreateEventLoop
    public static void onCreateEventLoop(Context context, FtcEventLoop eventLoop) {
        LOGGER.log(Level.INFO, "Event loop created, initializing DTUserRegistry...");
        Enumeration<String> dexEntries;
        try {
            dexEntries = getDexFileEntries(context);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load DexFile for scanning enabled drivers and value parsers.", e);
            throw new RuntimeException("Failed to load DexFile for scanning enabled drivers and value parsers");
        }
        scanForEnabledClasses(dexEntries);
        LOGGER.log(Level.INFO, "Available device drivers: " + DRIVER_INSTANCES.keySet());
        LOGGER.log(Level.INFO, "Available value parsers: " + VALUE_PARSER_INSTANCES.keySet());
        LOGGER.log(Level.INFO, "Finished scanning for enabled drivers and value parsers.");
    }

    public static void scanForEnabledClasses(Enumeration<String> dexEntries) {
        LOGGER.log(Level.INFO, "Scanning for enabled driver and value parser classes...");
        DRIVER_INSTANCES.clear();
        VALUE_PARSER_INSTANCES.clear();

        while (dexEntries.hasMoreElements()) {
            String className = dexEntries.nextElement();

            if (IGNORED_PACKAGES.stream().anyMatch(className::startsWith)) {
                continue;
            }

            try {
                Class<?> dexClass = Class.forName(className, false, DTClassDiscovery.class.getClassLoader());

                if ((DTDeviceEx.class.isAssignableFrom(dexClass) || DTDevice.class.isAssignableFrom(dexClass))
                        && !dexClass.isInterface() && !dexClass.isAnnotationPresent(DisabledDTClass.class)) {
                    DTDevice driverInstance = (DTDevice) dexClass.getDeclaredConstructor().newInstance();
                    DRIVER_INSTANCES.put(driverInstance.getDeviceName(), driverInstance);
                }

                if (Value.class.isAssignableFrom(dexClass) && !dexClass.isInterface() && !dexClass.isAnnotationPresent(DisabledDTClass.class)) {
                    Value<?> valueParserInstance = (Value<?>) dexClass.getDeclaredConstructor().newInstance();
                    VALUE_PARSER_INSTANCES.put(valueParserInstance.getType(), valueParserInstance);
                }
            } catch (ClassNotFoundException | NoClassDefFoundError | ExceptionInInitializerError ignored) {
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                LOGGER.log(Level.WARNING, "Class " + className + " does not have a default constructor or has an incorrect access level, skipping.");
                RobotLog.addGlobalWarningMessage("Class " + className + " does not have a default constructor or has an incorrect access level");
            }
        }
    }

    private static Enumeration<String> getDexFileEntries(Context context) throws IOException {
        DexFile dexFile = new DexFile(context.getPackageCodePath());
        return dexFile.entries();
    }

    public static Map<String, DTDevice> getDriverInstances() {
        return DRIVER_INSTANCES;
    }

    public static Map<Class<?>, Value<?>> getValueParserClasses() {
        return VALUE_PARSER_INSTANCES;
    }
}