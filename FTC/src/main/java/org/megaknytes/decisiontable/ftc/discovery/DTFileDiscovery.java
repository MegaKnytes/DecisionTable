package org.megaknytes.decisiontable.ftc.discovery;

import android.content.Context;
import android.os.Environment;

import com.qualcomm.robotcore.util.RobotLog;

import org.megaknytes.decisiontable.core.utils.DecisionTableOpModeFlavor;
import org.megaknytes.decisiontable.core.utils.exceptions.ConfigurationException;
import org.megaknytes.decisiontable.ftc.xml.Ruleset;
import org.megaknytes.decisiontable.ftc.xml.SystemConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class DTFileDiscovery {
    private static final Logger LOGGER = Logger.getLogger(DTFileDiscovery.class.getName());

    public static File[] getDeviceXMLFiles(Context context) {
        LOGGER.log(Level.INFO, "Beginning to scan for device XML files...");

        File userDataDir = new File(Environment.getExternalStorageDirectory(), "DecisionTables");
        File appContextDir = context.getExternalFilesDir(null);

        if (appContextDir == null) {
            LOGGER.log(Level.SEVERE, "Error: Unable to access app context directory while scanning for device XML files");
            throw new RuntimeException("Unable to access app context directory while scanning for device XML files");
        }

        if (!userDataDir.exists() && !userDataDir.mkdirs()) {
            LOGGER.log(Level.SEVERE, "Error: Failed to create user data directory while scanning for device XML files");
            throw new RuntimeException("Failed to create user data directory while scanning for device XML files");
        }

        return Stream.of(userDataDir, appContextDir)
                .filter(File::exists)
                .flatMap(dir -> Arrays.stream(Objects.requireNonNull(dir.listFiles((d, name) -> name.toLowerCase().endsWith(".xml")))))
                .toArray(File[]::new);
    }

    public static Map<String, SystemConfiguration> getEnabledSystemConfigurations(File[] files) throws ParserConfigurationException {
        LOGGER.log(Level.INFO, "Beginning to scan for enabled system configurations...");

        Map<String, SystemConfiguration> enabledSystemConfigurations = new HashMap<>();
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        for (File xmlFile : files) {
            Element root = parseRootElement(builder, xmlFile);

            if (!root.getNodeName().equals("SystemConfiguration") || !isEnabled(root, xmlFile)) {
                continue;
            }

            LOGGER.log(Level.INFO, "Processing system configuration file: " + xmlFile.getAbsolutePath());
            String name = nameOrFallback(root, xmlFile);

            if (enabledSystemConfigurations.containsKey(name)) {
                LOGGER.log(Level.SEVERE, "Duplicate system configuration name found: " + name);
                throw new ConfigurationException("Duplicate system configuration name: " + name);
            }

            enabledSystemConfigurations.put(name, new SystemConfiguration(xmlFile));
        }

        return enabledSystemConfigurations;
    }

    public static Map<String, Ruleset> getEnabledRulesets(File[] files, Map<String, SystemConfiguration> enabledSystemConfigurations) throws ParserConfigurationException {
        LOGGER.log(Level.INFO, "Beginning to scan for enabled rulesets...");

        Map<String, Ruleset> enabledRulesets = new HashMap<>();
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        for (File xmlFile : files) {
            Element root = parseRootElement(builder, xmlFile);

            if (!root.getNodeName().equals("DecisionTable") || !isEnabled(root, xmlFile)) {
                continue;
            }

            LOGGER.log(Level.INFO, "Processing decision table file: " + xmlFile.getAbsolutePath());
            String tableName = nameOrFallback(root, xmlFile);

            if (enabledRulesets.containsKey(tableName)) {
                String newTableName = tableName + "_" + System.currentTimeMillis();
                LOGGER.log(Level.SEVERE, "Duplicate decision table name found: " + tableName + ". Renaming to: " + newTableName);
                RobotLog.addGlobalWarningMessage("Duplicate decision table name found: " + tableName + ". Renaming to: " + newTableName);
                tableName = newTableName;
            }

            String systemConfigurationName = root.getAttribute("systemConfiguration");
            SystemConfiguration systemConfiguration = enabledSystemConfigurations.get(systemConfigurationName);

            if (systemConfiguration == null) {
                LOGGER.log(Level.SEVERE, "System configuration not found: " + systemConfigurationName);
                throw new ConfigurationException("System configuration not found: " + systemConfigurationName);
            }

            DecisionTableOpModeFlavor flavor;

            try {
                flavor = DecisionTableOpModeFlavor.valueOf(root.getAttribute("type"));
            } catch (IllegalArgumentException e) {
                LOGGER.log(Level.SEVERE, "Invalid decision table type in file: " + xmlFile.getAbsolutePath(), e);
                throw new ConfigurationException("Invalid decision table type: " + e.getMessage());
            }

            String transitionTarget = root.hasAttribute("transitionTarget") ? root.getAttribute("transitionTarget") : null;
            enabledRulesets.put(tableName, new Ruleset(xmlFile, systemConfiguration, flavor, transitionTarget));
        }

        return enabledRulesets;
    }

    private static Element parseRootElement(DocumentBuilder builder, File xmlFile) {
        try {
            Document doc = builder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            return doc.getDocumentElement();
        } catch (IOException | SAXException e) {
            LOGGER.log(Level.SEVERE, "Error parsing decision table file: " + xmlFile.getAbsolutePath(), e);
            throw new RuntimeException("Error parsing decision table file: " + xmlFile.getAbsolutePath(), e);
        }
    }

    private static boolean isEnabled(Element root, File xmlFile) {
        String enabled = root.getAttribute("enabled");

        if ("true".equals(enabled) || "1".equals(enabled)) {
            return true;
        }

        if (!"false".equals(enabled) && !"0".equals(enabled)) {
            String message = "Invalid or missing 'enabled' attribute (" + (enabled.isEmpty() ? "<missing>" : "\"" + enabled + "\"")
                    + ") in " + xmlFile.getName() + " -- treating as disabled. Expected true/false/1/0.";
            LOGGER.log(Level.WARNING, message);
            RobotLog.addGlobalWarningMessage(message);
        }

        return false;
    }

    private static String nameOrFallback(Element root, File xmlFile) {
        String name = root.getAttribute("name");

        if (name == null || name.isEmpty()) {
            LOGGER.log(Level.WARNING, "Name is empty, using file name instead: " + xmlFile.getName());
            return xmlFile.getName().replace(".xml", "");
        }

        return name;
    }
}